# SpringCloud Eureka

微服务中注册中心

### Docker 方式启动说明

集群模式高可用一般至少3个节点,本地测试开发可以使用单节点,两个参数   

###### 1.SPRING_PROFILES_ACTIVE   
多环境动态指定配置文件,比如 test,uat,prod 等.  

###### 2.EUREKA_SERVER_LIST  
service-url,单节点或集群指定,默认 http://localhost:700/eureka/  
如果集群模式,以逗号分割


#### 单节点部署  

```
docker run -itd --name <容器名:eureka-server> -p <端口号:7001>:7001 image:tag
```


#### 集群模式部署  
在根目录 docker 文件夹下有 eureka-stack.yml 文件,默认启动三个节点,相互注册,命令如下:  
docker swarm 方式:  

```
docker stack deploy -c eureka-stack.yml 
```
 
docker-compose 方式:  

```
docker-compose -f eureka-stack.yml up -d
```

### 注意  
集群模式Eureka部署到的网络 lli-cloud 需要手动创建，命令如下：  

```
docker network create lli-cloud
```
如果是swarm模式部署,则需要在管理节点创建,命令如下:  

```
docker network create -d overlay lli-cloud
```

### 服务端注册到Eureka  
从部署模版中可以看出这三个Eureka实例在网络上的别名(alias)都是eureka，对于客户端可以在配置文件中指定这个别名即可，不必指定三个示例的名字。

```
application.yml 配置文件,client端可以如下配置
eureka.client.serviceUrl.defaultZone=http://${EUREKA_SERVER_ADDRESS:localhost}:7001/eureka/
```


单服务启动用法:

```
docker run -itd --name <容器名称>  -e EUREKA_SERVER_ADDRESS=eureka --network lli-cloud -p 8080:8080 image:tag
```


docker-compose或swarm方式:  

```
version: '3'
services:
  web:
    image: binblee/demo-web
    networks:
      - lli-cloud
    environment:
      - EUREKA_SERVER_ADDRESS=eureka

  bookservice:
    image: binblee/demo-bookservice
    networks:
      - lli-cloud
    environment:
      - EUREKA_SERVER_ADDRESS=eureka

networks:
  lli-cloud:
    external:
      name: lli-cloud
```

要注意的是所有依赖于Eureka的应用服务都要挂到lli-cloud网络上(也就是说要和Eureka Server在同一网络上)，否则无法和Eureka Server通信.
