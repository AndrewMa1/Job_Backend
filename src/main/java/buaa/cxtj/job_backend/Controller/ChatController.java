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
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/chat")
@Slf4j
public class ChatController {

    @Autowired
    KafkaTemplate<String,Message> kafkaTemplate;
    @Autowired
    SimpMessagingTemplate template;


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

    @PostMapping("/getMsg")
    public ReturnProtocol getMessages(@RequestBody Message message) {
        message.setTimestamp(LocalDateTime.now().toString());
        //发送消息到Kafka主题队列
        kafkaTemplate.send(topic, message);
        return new ReturnProtocol(true,"发送消息成功",message);
    }


//    @KafkaListener(topics = {"chat"},groupId = "chat_cons")
//    public void onMessage(Message message){
//        // 消费的哪个topic、partition的消息,打印出消息内容
//        template.convertAndSend("/topic/group", message);
//
//    }
//
//    @MessageMapping("/sendMessage")
//    @SendTo("/topic/group")
//    public Message broadcastGroupMessage(@Payload Message message) {
////将此消息发送给所有订阅者
//        return message;
//    }



}
