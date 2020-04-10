package com.leyou.page.test;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestRabbitMqSendMsg {
    @Autowired
    //private RabbitTemplate rabbitTemplate; // 早期
    private AmqpTemplate amqpTemplate; // 推荐

    @Test
    public void test(){
        /**
         * 参数一： 交换机名称
         * 参数二： 路由key
         * 参数三：发送的内容： 基本数据类型和String，JavaBean，集合
         */
        amqpTemplate.convertAndSend("leyou.text.exchange","feifei.test.update", "小飞飞真的好纯啊！");
    }

}
