package com.leyou.page.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 消费者：
 *      监听mq
 */
@Slf4j
@Component
public class Listener {

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue( // 配置队列
                            value="leyou.test.queue", // 队列名称
                            durable = "true" // 是否持久化
                    ),
                    exchange = @Exchange(
                            value="leyou.text.exchange",//交换机名称
                            type = ExchangeTypes.TOPIC , //采用主题类型
                            ignoreDeclarationExceptions = "true"// 创建交换机失败的时候，继续创建其他属性
                    ),
                    key = "feifei.#"
            )
    )
    public void adadfadfa(String msg){
        log.info("【消费者】消费消息：{}",msg);
    }
}
