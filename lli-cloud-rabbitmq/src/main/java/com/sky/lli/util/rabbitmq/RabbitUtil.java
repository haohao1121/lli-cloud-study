package com.sky.lli.util.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AbstractExchange;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

/**
 * 初版:
 * RabbitMQ 工具类
 * 基本用法封装
 * @author lihao
 */
@Configuration
public class RabbitUtil {

    private static final Logger logger = LoggerFactory.getLogger(RabbitUtil.class);

    private final RabbitAdmin rabbitAdmin;

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitUtil(RabbitAdmin rabbitAdmin, RabbitTemplate rabbitTemplate) {
        this.rabbitAdmin = rabbitAdmin;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 转换Message对象
     *
     * @param messageType 返回消息类型 MessageProperties类中常量
     * @param msg         数据
     * @return Message
     */
    public Message getMessage(String messageType, Object msg) {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType(messageType);
        return new Message(msg.toString().getBytes(), messageProperties);
    }

    /**
     * 有绑定Key的directExchange发送
     *
     * @param directExchange directExchange
     * @param routingKey     routingKey
     * @param msg            数据
     */
    public void sendMessageToDirectExchange(String directExchange, String routingKey, Object msg) {
        addExchange(new DirectExchange(directExchange));
        Message message = getMessage(MessageProperties.CONTENT_TYPE_JSON, msg);
        rabbitTemplate.send(directExchange, routingKey, message);
    }

    /**
     * 有绑定Key的topicExchange发送
     *
     * @param topicExchange topicExchange
     * @param routingKey    routingKey
     * @param msg           数据
     */
    public void sendMessageToTopicExchange(String topicExchange, String routingKey, Object msg) {
        addExchange(new TopicExchange(topicExchange));
        Message message = getMessage(MessageProperties.CONTENT_TYPE_JSON, msg);
        rabbitTemplate.send(topicExchange, routingKey, message);
    }

    /**
     * 给direct交换机指定queue发送消息,
     * routingKey默认与queue名字相同
     *
     * @param directExchange directExchange
     * @param queueName      queueName
     * @param msg            数据
     */
    public void sendToDirectQueue(String directExchange, String queueName, Object msg) {
        sendToDirectQueue(directExchange, queueName, queueName, msg);
    }

    /**
     * 给direct交换机指定queue,routingKey发送消息
     *
     * @param directExchange directExchange
     * @param queueName      queueName
     * @param routingKey     routingKey
     * @param msg            数据
     */
    public void sendToDirectQueue(String directExchange, String queueName, String routingKey, Object msg) {
        sendToDirectQueue(directExchange, queueName, routingKey, msg, null);
    }

    /**
     * 给direct交换机指定queue,routingKey,CorrelationData发送消息
     *
     * @param directExchange  directExchange
     * @param queueName       queueName
     * @param routingKey      routingKey
     * @param msg             数据
     * @param correlationData correlationData
     */
    public void sendToDirectQueue(String directExchange, String queueName, String routingKey, Object msg, CorrelationData correlationData) {
        DirectExchange exchange = new DirectExchange(directExchange);
        addExchange(exchange);
        Queue queue = new Queue(queueName);
        addQueue(queue);
        //添加绑定
        addBinding(queue, exchange, routingKey);
        //发送消息到指定routingKey对应的队列上
        rabbitTemplate.convertAndSend(directExchange, routingKey, msg, correlationData);
    }

    /**
     * 给topic交换机指定 topicExchange ,queue,发送消息
     * routingKey 默认为  queueName
     *
     * @param topicExchange topicExchange
     * @param queueName     queueName
     * @param msg           数据
     */
    public void sendToTopicQueue(String topicExchange, String queueName, Object msg) {
        sendToTopicQueue(topicExchange, queueName, queueName, msg);
    }

    /**
     * 给topic交换机指定queue,routingKey发送消息
     *
     * @param topicExchange topicExchange
     * @param queueName     queueName
     * @param routingKey    routingKey
     * @param msg           数据
     */
    public void sendToTopicQueue(String topicExchange, String queueName, String routingKey, Object msg) {
        sendToTopicQueue(topicExchange, queueName, routingKey, msg, null);
    }

    /**
     * 给topic交换机指定queue,routingKey,CorrelationData发送消息
     *
     * @param topicExchange   topicExchange
     * @param queueName       queueName
     * @param routingKey      routingKey
     * @param msg             数据
     * @param correlationData correlationData
     */
    public void sendToTopicQueue(String topicExchange, String queueName, String routingKey, Object msg, CorrelationData correlationData) {
        TopicExchange exchange = new TopicExchange(topicExchange);
        Queue queue = new Queue(queueName);
        addExchange(exchange);
        addQueue(queue);
        //添加绑定
        addBinding(queue, exchange, routingKey);
        //如果routingKey是模糊匹配，则会发送到多个队列
        rabbitTemplate.convertAndSend(topicExchange, routingKey, msg, correlationData);
    }

    /**
     * 给fanoutExchange交换机指定queue,routingKey,CorrelationData发送消息
     *
     * @param fanoutExchange fanoutExchange
     * @param queueName      queueName集合
     * @param msg            数据
     */
    public void sendToFanoutQueue(String fanoutExchange, String queueName, Object msg) {
        sendToFanoutQueue(fanoutExchange, queueName, msg, null);
    }

    /**
     * 给fanoutExchange交换机指定queue,CorrelationData发送消息
     *
     * @param fanoutExchange  fanoutExchange
     * @param queueName       queueName集合
     * @param msg             数据
     * @param correlationData correlationData
     */
    public void sendToFanoutQueue(String fanoutExchange, String queueName, Object msg, CorrelationData correlationData) {
        sendToFanoutQueue(fanoutExchange, Collections.singletonList(queueName), msg, correlationData);
    }

    /**
     * 给fanoutExchange交换机指定queue集合,CorrelationData发送消息
     *
     * @param fanoutExchange  fanoutExchange
     * @param queueList       queueName集合
     * @param msg             数据
     * @param correlationData correlationData
     */
    public void sendToFanoutQueue(String fanoutExchange, List<String> queueList, Object msg, CorrelationData correlationData) {
        FanoutExchange exchange = new FanoutExchange(fanoutExchange);
        addExchange(exchange);
        addBinding(exchange, queueList);
        //fanoutExchange模式不需要routingKey，所以routingKey为空
        rabbitTemplate.convertAndSend(fanoutExchange, "", msg, correlationData);
    }


    /**
     * 创建Exchange
     *
     * @param exchange exchange
     */
    public void addExchange(AbstractExchange exchange) {
        rabbitAdmin.declareExchange(exchange);
        logger.info("已添加 Exchange:{}", exchange.getName());
    }

    /**
     * 删除一个Exchange
     *
     * @param exchangeName exchangeName
     */
    public boolean deleteExchange(String exchangeName) {
        return rabbitAdmin.deleteExchange(exchangeName);
    }

    /**
     * 创建一个指定的Queue
     *
     * @param queue queue
     */
    public void addQueue(Queue queue) {
        String queueName = rabbitAdmin.declareQueue(queue);
        logger.info("已添加 Queue:{}", queue.getName());
    }

    /**
     * Delete a queue.
     *
     * @param queueName the name of the queue.
     * @param unused    true if the queue should be deleted only if not in use.
     * @param empty     true if the queue should be deleted only if empty.
     */
    public void deleteQueue(String queueName, boolean unused, boolean empty) {
        rabbitAdmin.deleteQueue(queueName, unused, empty);
    }

    /**
     * 删除一个queue
     *
     * @param queueName queueName
     * @return true if the queue existed and was deleted.
     */
    public boolean deleteQueue(String queueName) {
        return rabbitAdmin.deleteQueue(queueName);
    }

    /**
     * 清空一个queue
     *
     * @param queueName queue
     */
    public void purgeQueue(final String queueName) {
        rabbitAdmin.purgeQueue(queueName, Boolean.TRUE);
    }


    /**
     * 绑定一个队列到一个DirectExchange使用一个routingKey
     *
     * @param queue      queue
     * @param exchange   exchange
     * @param routingKey routingKey
     */
    public void addBinding(Queue queue, DirectExchange exchange, String routingKey) {
        Binding binding = BindingBuilder.bind(queue).to(exchange).with(routingKey);
        rabbitAdmin.declareBinding(binding);
        logger.info("已添加 Binding:DirectExchange[{}]->Queue[{}]->routingKey[{}]", exchange.getName(), queue.getName(), routingKey);
    }

    /**
     * 绑定一个队列到一个TopicExchange使用一个routingKey
     *
     * @param queue      queue
     * @param exchange   exchange
     * @param routingKey routingKey
     */
    public void addBinding(Queue queue, TopicExchange exchange, String routingKey) {
        Binding binding = BindingBuilder.bind(queue).to(exchange).with(routingKey);
        rabbitAdmin.declareBinding(binding);
        logger.info("已添加 Binding:TopicExchange[{}]->Queue[{}]->routingKey[{}]", exchange.getName(), queue.getName(), routingKey);
    }

    /**
     * 方法说明: 绑定queue到 FanoutExchange 上
     *
     * @param exchange  FanoutExchange
     * @param queueList queue集合
     */
    public void addBinding(FanoutExchange exchange, List<String> queueList) {
        for (String queueName : queueList) {
            Queue queue = new Queue(queueName);
            addQueue(queue);
            Binding binding = BindingBuilder.bind(queue).to(exchange);
            rabbitAdmin.declareBinding(binding);
            logger.info("已添加 Binding:FanoutExchange[{}]->Queue[{}]", exchange.getName(), queueName);
        }
    }

    /**
     * 绑定一个Exchange到一个匹配型Exchange 使用一个routingKey
     *
     * @param exchange      exchange
     * @param topicExchange topicExchange
     * @param routingKey    routingKey
     */
    public void addBinding(Exchange exchange, TopicExchange topicExchange, String routingKey) {
        Binding binding = BindingBuilder.bind(exchange).to(topicExchange).with(routingKey);
        rabbitAdmin.declareBinding(binding);
    }

    /**
     * 去掉一个binding
     *
     * @param binding binding
     */
    public void removeBinding(Binding binding) {
        rabbitAdmin.removeBinding(binding);
    }
}