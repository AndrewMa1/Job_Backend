package buaa.cxtj.job_backend.Controller;


import buaa.cxtj.job_backend.Mapper.ChatMapper;
import buaa.cxtj.job_backend.Mapper.UserMapper;
import buaa.cxtj.job_backend.POJO.DTO.UserDTO;
import buaa.cxtj.job_backend.POJO.Entity.Chat;
import buaa.cxtj.job_backend.POJO.Entity.Message;
import buaa.cxtj.job_backend.POJO.UserHolder;
import buaa.cxtj.job_backend.Service.Impl.KafkaTopicServiceImpl;
import buaa.cxtj.job_backend.Util.ReturnProtocol;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/chat")
@Slf4j
public class ChatController {

    @Autowired
    KafkaTemplate<String,Message> kafkaTemplate;

    @Autowired
    KafkaTopicServiceImpl kafkaTopicService;

    @Autowired
    ChatMapper chatMapper;

    @Autowired
    UserMapper userMapper;

    private final String topic = "chat";

    @GetMapping("/getChatRooms")
    public ReturnProtocol getChatRooms(){
        UserDTO userDTO = UserHolder.getUser();
        String userId = userDTO.getId();
        QueryWrapper<Chat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user1", userId).or().eq("user2",userId);
        List<Chat> chatList = chatMapper.selectList(queryWrapper);
        return new ReturnProtocol(true,chatList);
    }

    @GetMapping("/newChat/{to_id}")
    public ReturnProtocol newChatRoom(@PathVariable("to_id")String to){
        Chat chat = kafkaTopicService.createChatTopic(UserHolder.getUser().getId(),to);
        chat.setNowUserId(UserHolder.getUser().getId());
        chat.setUser1Name(UserHolder.getUser().getNickname()); //当前用户的姓名
        chat.setUser2Name(userMapper.selectById(to).getNickname());
        return new ReturnProtocol(true,"create chat room success",chat);
    }

    @PostMapping("/sendMsg")
    public ReturnProtocol sendMessage(@RequestBody Message message) {
        message.setTimestamp(LocalDateTime.now().toString());
            //发送消息到Kafka主题队列
        kafkaTemplate.send(topic, message);
        return new ReturnProtocol(true,"发送消息成功",message);
    }

//    @PostMapping("/getMsg")
//    public ReturnProtocol getMessages(@RequestBody Message message) {
//        message.setTimestamp(LocalDateTime.now().toString());
//        //发送消息到Kafka主题队列
//        kafkaTemplate.send(topic, message);
//        return new ReturnProtocol(true,"发送消息成功",message);
//    }



}
