package com.sky.lli.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 使用Apache的commons-Compress压缩解压
 *
 * @author klaus
 * @date 2020/8/14
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApacheCompress {

    /**
     * 将多个文件进行压缩
     *
     * @param files    多个文件对象
     * @param distPath 压缩后输出目录
     * @return 如果为null, 则压缩失败, 成功则返回文件路径
     */
    public static String compressFilesToZip(List<File> files, String distPath) {
        String zipFilePath = distPath + ".zip";
        try (FileOutputStream fileOutputStream = new FileOutputStream(zipFilePath)) {
            //Zip压缩
            ZipArchiveOutputStream zos = new ZipArchiveOutputStream(fileOutputStream);
            //设置编码
            zos.setEncoding("UTF-8");
            //文件名集合，防止重复文件
            ArrayList<String> fileNames = new ArrayList<>();

            //压缩文件
            for (File f : files) {
                //文件不存在或者为空文件夹则忽略
                if (!f.exists() || f.isDirectory()) {
                    continue;
                }

                //构建zip中的实体
                zos.putArchiveEntry(new ZipArchiveEntry(getFileName(fileNames, f.getName())));
                //输出文件压缩中
                FileInputStream fis = new FileInputStream(f);
                IOUtils.copy(fis, zos);
                fis.close();
                //关闭本次输出实体
                zos.closeArchiveEntry();
            }
            //关闭输出
            zos.close();
        } catch (IOException e) {
            log.error("压缩文件失败", e);
            return null;
        }
        return zipFilePath;

    }

    /**
     * 方法说明: 文件解压
     *
     * @param distPath 解压目录
     * @param zipName  压缩文件
     * @date 2020/8/14
     * @author klaus
     */
    public static void unZip(String distPath, File zipName) {
        try (ZipFile file = new ZipFile(zipName)) {
            Enumeration<ZipArchiveEntry> en = file.getEntries();
            ZipArchiveEntry ze;
            while (en.hasMoreElements()) {
                ze = en.nextElement();
                File f = new File(distPath, ze.getName());
                if (ze.isDirectory()) {
                    boolean mkdirs = f.mkdirs();
                    if (mkdirs) {
                        log.info("文件夹创建成功");
                    }
                    continue;
                } else {
                    boolean mkdirs = f.getParentFile().mkdirs();
                    if (mkdirs) {
                        log.info("文件夹创建成功");
                    }
                }

                InputStream is = file.getInputStream(ze);
                OutputStream os = new FileOutputStream(f);
                IOUtils.copy(is, os);
                is.close();
                os.close();
            }
        } catch (IOException e) {
            log.info("解压失败", e);
        }

    }

    /**
     * 构建文件名称
     *
     * @param fileNames   已存在的文件名称集合
     * @param orgFileName 文件原始名称
     * @return 最终zip中的文件名称
     */
    private static String getFileName(ArrayList<String> fileNames, String orgFileName) {
        //构建名称时候需要重复性质的尝试，因此使用StringBuilder临时性质的构件名称
        StringBuilder stringBuilder = new StringBuilder().append(orgFileName);
        int i = 1;

        //如果重复则获取新的名称
        while (fileNames.indexOf(stringBuilder.toString()) > -1) {
            stringBuilder.delete(0, stringBuilder.length());
            //判断是否存在后缀，有则在后缀之间加上
            int suffixIndex = orgFileName.indexOf('.');
            if (suffixIndex > -1) {
                stringBuilder.append(orgFileName.substring(0, suffixIndex))
                        .append(i)
                        .append('.')
                        .append(orgFileName.substring(suffixIndex + 1, orgFileName.length()));
            } else {
                stringBuilder.append(orgFileName).append(i);
            }
            ++i;
        }
        fileNames.add(stringBuilder.toString());
        return stringBuilder.toString();
    }
}
