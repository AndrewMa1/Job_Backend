package buaa.cxtj.job_backend.Controller;


import buaa.cxtj.job_backend.POJO.DTO.UserDTO;
import buaa.cxtj.job_backend.POJO.Entity.Message;
import buaa.cxtj.job_backend.POJO.UserHolder;
import buaa.cxtj.job_backend.Util.ReturnProtocol;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/chat")
@Slf4j
public class ChatController {

    @Autowired
    KafkaTemplate<String,Message> kafkaTemplate;

    private final String topic = "chat";

    @GetMapping("/getChatRooms")
    public ReturnProtocol getChatRooms(){
        UserDTO userDTO = UserHolder.getUser();

        return null;
    }

    @PostMapping("/sendMsg")
    public ReturnProtocol sendMessage(@RequestBody Message message) {
        message.setTimestamp(LocalDateTime.now().toString());
            //发送消息到Kafka主题队列
        kafkaTemplate.send(topic, message);
        return new ReturnProtocol(true,"发送消息成功",message);
    }


    @KafkaListener(topics = {"chat"},groupId = "chat_cons")
    public void onMessage(ConsumerRecord<?, ?> record){
        // 消费的哪个topic、partition的消息,打印出消息内容
        System.out.println("简单消费Topic："+record.topic()+"**分区"+record.partition()+"**值内容"+record.value());

    }






}
