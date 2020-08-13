package com.sky.lli.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

import static org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE;


/**
 * 说明: ftp相关配置
 *
 * @author klaus
 * @date 2020-01-01
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FtpHelper {
    /**
     * FTP端口
     */
    private int port = 21;
    /**
     * FTP主机名或者IP
     */
    private String host = "";
    /**
     * FTP用户名称
     */
    private String userName = "";
    /**
     * FTP用户密码
     */
    private String userPassword;
    /**
     * 本地字符集编码
     */
    private String localEncoding = "utf-8";
    /**
     * 服务器编码字符集
     */
    private String serverEncoding = "iso-8859-1";

    /**
     * 调用前必须配置ftp链接参数
     */
    public static FtpHelper ftpUploaderBuild(String host, int port, String userName, String userPassword) {
        FtpHelper ftpUploader = new FtpHelper();
        ftpUploader.port = port;
        ftpUploader.host = host;
        ftpUploader.userName = userName;
        ftpUploader.userPassword = userPassword;
        return ftpUploader;
    }

    /**
     * 向ftp写文件(数据)
     *
     * @param fileName    文件名称
     * @param inputStream 文件流
     * @param distFile    Ftp路径
     * @throws IOException     IOException
     * @throws SocketException SocketException
     */
    public void upload(String fileName, InputStream inputStream, String distFile, boolean autoCloseInputStream) throws IOException {
        // 获取ftpClient实例
        FTPClient ftpClient = connectFTPServer();

        // 远程目录
        String distPath = distFile.substring(0, distFile.lastIndexOf('/'));
        // 判断文件目录是否存在
        if (!ftpClient.changeWorkingDirectory(distPath)) {
            // 不存在创建
            ftpClient.makeDirectory(distPath);
        }
        // 指定写入的目录
        ftpClient.changeWorkingDirectory(distPath);
        // 写操作/采用二进制方式传输
        ftpClient.setFileType(BINARY_FILE_TYPE);
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
        ftpClient.setBufferSize(1024 * 500);
        // 文件名
        String fn = new String(fileName.getBytes(localEncoding), serverEncoding);
        // 文件全路径
        String ffn = distFile + fn;
        // 执行传输[参数1:新文件名称;参数2:本地文件的输入流][注意]文件路径最好使用全路径否则可能发生空文件情况
        boolean us = ftpClient.storeFile(fn, inputStream);
        if (us) {
            log.info("File [{}] Upload Success!", ffn);
        } else {
            throw new IOException("File [" + ffn + "] Upload Fail!");
        }
        //是否关闭文件输入流
        if (autoCloseInputStream) {
            inputStream.close();
        }

        ///退出登录/断开连接
        boolean b = ftpClient.logout();
        if (b) {
            log.info("Logout Ftp Server Success!");
        } else {
            log.info("Logout Ftp Server Fail!");
        }
        ftpClient.disconnect();
    }


    /**
     * FTP 下载文件
     *
     * @param ftpPath   FTP中的路径
     * @param fileName  文件名称
     * @param localPath 下载到本地路径
     * @throws IOException 异常
     */
    public void download(String ftpPath, String fileName, String localPath) throws IOException {
        // 替换路径中的反斜杠
        String srcPath = new String(ftpPath.replace("\\\\", "/").getBytes(localEncoding), serverEncoding);
        // 处理路径
        if (!srcPath.endsWith("/")) {
            srcPath = srcPath + '/';
        }

        //处理下载后的目录
        String distPath = new String(localPath.replace("\\\\", "/").getBytes(localEncoding), serverEncoding);
        if (!distPath.endsWith("/")) {
            distPath = distPath + '/';
        }

        boolean succ = new File(distPath).mkdirs();
        if (succ) {
            log.info("文件夹 [{}] 创建成功", distPath);
        }

        // 连接ftp
        FTPClient ftp = connectFTPServer();

        ftp.enterLocalPassiveMode();

        // 判断是否登陆成功
        if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
            ftp.disconnect();
            throw new IOException("=======MISS LOGIN,CONNECT DISCONNECT...======");
        }

        // 转移到FTP服务器目录
        ftp.changeWorkingDirectory(srcPath);

        // 得到目录的相应文件列表
        FTPFile[] fs = ftp.listFiles();

        for (FTPFile ftpFile : fs) {
            if (!ftpFile.getName().equals(fileName)) {
                continue;
            }
            try (OutputStream os = new FileOutputStream(distPath + fileName)) {
                ftp.retrieveFile(new String(ftpFile.getName().getBytes(serverEncoding), localEncoding), os);
                os.flush();
            }
        }

        boolean b = ftp.logout();
        if (b) {
            log.info("Logout Ftp Server Success!");
        } else {
            log.info("Logout Ftp Server Fail!");
        }

        ftp.disconnect();
    }


    private FTPClient connectFTPServer() throws IOException {
        // 实例化一个ftp客户端
        FTPClient ftpClient = new FTPClient();
        // 连接服务器
        log.info("HOST:{},Port:{}", host, port);
        ftpClient.connect(host, port);
        boolean isLogin = ftpClient.login(userName, userPassword);
        if (!isLogin) {
            throw new IOException("Login Ftp Server Fail,Plase Check Port,Host,user,password is all Rigth?");
        } else {
            log.info("Login Ftp Server Success!");
        }
        log.info("FTP Server Type:{}", ftpClient.getSystemType());
        return ftpClient;
    }
}