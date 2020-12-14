# 主要介绍 elk 单机环境搭建及用法 

## 目标架构
 filebeat -> kafka -> elk
 
 ## 实现方式
1.docker文件夹下 docker-compose.yml为zookeeper和kafka部署脚本  
2.elk-stack.yml为elk部署脚本  
3.filebeat主要修改配置文件中input和output  
4.集成springboot问题,目前想到的是在基础java8镜像中添加filebeat自启动服务,重新构建成基础镜像