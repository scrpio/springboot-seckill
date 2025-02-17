package com.shop.seckill.rabbitmq;

import com.shop.seckill.utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQSender {

    private static Logger log = LoggerFactory.getLogger(MQSender.class);

    @Autowired
    AmqpTemplate amqpTemplate;

    @Autowired
    private RedisUtil redisUtil;

    public void sendTopic(Object message) {
        String msg = redisUtil.beanToString(message);
        log.info("send topic message:" + msg);
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key1", msg + "1");
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key2", msg + "2");
    }

    public void sendSeckillMessage(SeckillMessage message) {
        String msg = redisUtil.beanToString(message);
        log.info("send message:" + msg);
        amqpTemplate.convertAndSend(MQConfig.QUEUE, msg);

    }
}
