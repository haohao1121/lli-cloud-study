## Consul 安装启动 docker方式启动
### 创建容器映射目录
> docker volume create --name consul-data   对应: /consul/data
> docker volume create --name consul-config  对应: /consul/config
### 单节点部署
> docker run -itd --name consul-dev -p 8500:8500 consul agent -server -bind=0.0.0.0 -client=0.0.0.0 -node=consul-dev -bootstrap-expect=1 -ui
#### 浏览器访问
> http://localhost<docker服务器地址>:8500 
### 集群部署
#### 创建自定义网络
> docker network create lli-consul  
> 如果是swarm模式部署,则需要在管理节点创建,命令如下:  
> docker network create -d overlay lli-consul  
#### 配置文件
> 详见docker文件夹下 docker-compose.yaml 文件
#### 查看节点信息
> docker exec -t node1 consul members
> 具体consul命令见官网或者自行百度,此处仅供参考