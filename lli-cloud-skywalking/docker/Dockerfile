# Dockerfile两种使用方式
# 方式一,使用原生java:8 基础镜像,把skywalking agent以及filebeat文件拷贝进去
FROM java:8
ENV TZ=Asia/Shanghai LANG=C.UTF-8
WORKDIR /data
COPY *.jar app.jar
COPY agent.tar /data/
COPY filebeat.tar /data/
ADD start.sh start.sh
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN cd /data \
    && tar -xf agent.tar && rm -rf agent.tar \
    && tar -xf filebeat.tar && rm -rf filebeat.tar \
EXPOSE 8801
ENTRYPOINT ["sh","/data/start.sh"]

# 方式二,自定义java基础镜像,包含skywalking agent以及filebeat
FROM lli-java8
ENV TZ=Asia/Shanghai LANG=C.UTF-8
WORKDIR /data
COPY *.jar app.jar
# start.sh 包含 app,filebeat以及skywalking相关启动命令
ADD start.sh start.sh
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
EXPOSE 8801
ENTRYPOINT ["sh","/data/start.sh"]