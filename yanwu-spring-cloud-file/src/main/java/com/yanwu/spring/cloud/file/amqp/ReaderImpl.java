// package com.yanwu.spring.cloud.file.amqp;
//
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.amqp.rabbit.annotation.Exchange;
// import org.springframework.amqp.rabbit.annotation.Queue;
// import org.springframework.amqp.rabbit.annotation.QueueBinding;
// import org.springframework.amqp.rabbit.annotation.RabbitListener;
// import org.springframework.stereotype.Component;
//
// /**
//  * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
//  * @date 2019-08-03 15:45.
//  * <p>
//  * description:
//  */
// @Slf4j
// @Component
// public class ReaderImpl {
//
//     @RabbitListener(queues = "test_1")
//     public void test1(String message) {
//         log.info("ReaderImpl test1, message: {}", message);
//     }
//
//     @RabbitListener(bindings = @QueueBinding(
//             value = @Queue(value = "test_2", durable = "true"),
//             exchange = @Exchange(value = "test_2", type = "topic"),
//             key = "test_2"
//     ))
//     public void test2(String message) {
//         log.info("ReaderImpl test2, message: {}", message);
//     }
//
// }
