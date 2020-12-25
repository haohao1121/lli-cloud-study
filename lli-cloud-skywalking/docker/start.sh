#!/usr/bin/env bash
set -e
set -x

# 验证参数
if [ -z "$KAFKA_IP" ]; then
  echo "KAFKA_IP is not set!"
  exit
fi
if [ -z "$APP_NAME" ]; then
  echo "APP_NAME is not set!"
  exit
fi

# filebeat kafka参数
sed -i "s/kafkahost/${KAFKA_IP}/g" /data/filebeat/filebeat.yml
sed -i "s/unkown-topic/${APP_NAME}/g" /data/filebeat/filebeat.yml
# 处理权限问题
chown root /data/filebeat/filebeat.yml
nohup /data/filebeat/filebeat -e -c /data/filebeat/filebeat.yml >info.log 2>&1 &

# 方式一 使用 APP_NAME 和 SW_COLLECTOR_IP 参数
#java -javaagent:/data/agent/skywalking-agent.jar -Dskywalking.agent.service_name=${APP_NAME} -Dskywalking.collector.backend_service=${SW_COLLECTOR_IP}:11800 -jar /data/app.jar
# 方式二 使用 SW_AGENT_NAME 和  SW_AGENT_COLLECTOR_BACKEND_SERVICES 参数
java -javaagent:/data/agent/skywalking-agent.jar -jar /data/app.jar
