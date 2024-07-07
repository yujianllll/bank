package com.example.trade_service.listener;

import com.example.trade_service.dto.MqConstants;
import com.example.trade_service.dto.MultiDelayMessage;
import com.example.trade_service.entity.Order;
import com.example.trade_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderStatusCheckListener {

    private final OrderService orderService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConstants.DELAY_ORDER_QUEUE, durable = "true"),
            exchange = @Exchange(value = MqConstants.DELAY_EXCHANGE, delayed = "true", type = ExchangeTypes.TOPIC),
            key = MqConstants.DELAY_ORDER_ROUTING_KEY
    ))
    public void listenOrderDelayMessage(MultiDelayMessage<String> msg) {
        // 1.查询订单状态
        Order order = orderService.getById(msg.getData());
        // 2.判断是否已经支付
        if (order == null || order.getStatus() == 2) {
            // 订单不存在或者已经被处理
            return;
        }
        // TODO 3.去支付服务查询真正的支付状态
//        boolean isPay = false;
        // 3.1.已支付，标记订单状态为已支付，或者取消支付
        if (order.getStatus()>=2&&order.getStatus()!=5) {
            System.out.println("已支付，标记订单状态为已支付");
            return;
        }

        // 4.判断是否存在延迟时间，自己发送自己检查是否还会有延时时间
        if (msg.hasNextDelay()) {
            // 4.1.存在，重发延迟消息
            Long nextDelay = msg.removeNextDelay();
            rabbitTemplate.convertAndSend(
                    MqConstants.DELAY_EXCHANGE, MqConstants.DELAY_ORDER_ROUTING_KEY,
                    msg, new MessagePostProcessor() {
                        @Override
                        public Message postProcessMessage(Message message) throws AmqpException {
                            message.getMessageProperties().setDelay(Math.toIntExact(nextDelay));
                            return message;
                        }
                    });
            return;
        }
        // 5.不存在，取消订单
        orderService.cancelOrder(order.getId());
    }
}
