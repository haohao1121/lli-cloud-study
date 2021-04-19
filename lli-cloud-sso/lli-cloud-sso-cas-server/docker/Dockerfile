FROM java:8
ENV TZ=Asia/Shanghai LANG=C.UTF-8
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
WORKDIR /data
COPY *.war app.war
EXPOSE 8010
ENTRYPOINT ["java", "-Duser.timezone=GMT+08", "-jar", "app.war"]