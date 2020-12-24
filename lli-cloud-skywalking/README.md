# 下载地址  
 https://archive.apache.org/dist/skywalking/  
## 使用  
下载服务器对应的版本文件,加压 agent整个文件夹,项目启动添加参数,java -jar 方式为例:  
 -javaagent:${agent-path}/skywalking-agent.jar  
 -Dskywalking.agent.service_name=${app_name}  
 -Dskywalking.collector.backend_service=${collect_id}:11800  
## docker方式  
将agent文件夹拷贝到基础镜像里或者自己制作一个带agent的镜像
