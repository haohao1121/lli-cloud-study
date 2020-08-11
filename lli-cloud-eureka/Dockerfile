
FROM java:8
MAINTAINER LLI
ENV TZ=Asia/Shanghai LANG=C.UTF-8
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
WORKDIR /
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-Duser.timezone=GMT+08", "-jar", "app.jar"]