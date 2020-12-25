
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
