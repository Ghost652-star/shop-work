package com.ecommerce.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

/**
 * RabbitMQ 配置类
 */
@Configuration
public class RabbitMQConfig {

    // ==================== 优惠券秒杀队列声明 ====================

    public static final String COUPON_SECKILL_QUEUE = "coupon.seckill.queue";
    public static final String COUPON_SECKILL_EXCHANGE = "coupon.seckill.exchange";
    public static final String COUPON_SECKILL_ROUTING_KEY = "coupon.seckill";

    @Bean
    public Queue couponSeckillQueue() {
        return new Queue(COUPON_SECKILL_QUEUE, true); // 持久化
    }

    @Bean
    public DirectExchange couponSeckillExchange() {
        return new DirectExchange(COUPON_SECKILL_EXCHANGE, true, false);
    }

    @Bean
    public Binding couponSeckillBinding() {
        return BindingBuilder
                .bind(couponSeckillQueue())
                .to(couponSeckillExchange())
                .with(COUPON_SECKILL_ROUTING_KEY);
    }

    // ==================== JSON 消息转换器 ====================

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    // ==================== Redis Lua 秒杀脚本 ====================

    /** 优惠券库存 key 前缀 */
    public static final String COUPON_STOCK_KEY = "coupon:stock:";
    /** 已领取用户 set key 前缀 */
    public static final String COUPON_CLAIMED_KEY = "coupon:claimed:";

    @Bean("couponSeckillScript")
    public DefaultRedisScript<Long> couponSeckillScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setResultType(Long.class);
        script.setScriptSource(new ResourceScriptSource(
                new ClassPathResource("lua/coupon_seckill.lua")));
        return script;
    }
}
