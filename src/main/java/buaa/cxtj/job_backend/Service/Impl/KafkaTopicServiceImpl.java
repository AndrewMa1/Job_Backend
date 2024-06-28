package buaa.cxtj.job_backend.Service.Impl;


import buaa.cxtj.job_backend.Config.KafkaAdminConfig;
import buaa.cxtj.job_backend.Mapper.ChatMapper;
import buaa.cxtj.job_backend.POJO.Entity.Chat;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewPartitions;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class KafkaTopicServiceImpl {

    @Autowired
    private AdminClient adminClient;

    @Autowired
    private KafkaAdminConfig kafkaAdminConfig;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ChatMapper chatMapper;

    public void addPartitions(String topicName, int numPartitions) throws ExecutionException, InterruptedException {
        NewPartitions newPartitions = NewPartitions.increaseTo(numPartitions);
        adminClient.createPartitions(Collections.singletonMap(topicName, newPartitions)).all().get();
    }

    public Chat createChatTopic(String user1, String user2) {
        QueryWrapper<Chat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user1",user1).eq("user2",user2).or().eq("user1",user2).eq("user2",user1);
        List<Chat> chats = chatMapper.selectList(queryWrapper);
        if(!chats.isEmpty()){
            return chats.get(0);
        }
        Chat chat = new Chat(user1,user2, LocalDateTime.now().toString());
        chatMapper.insert(chat);
        System.out.println("chat id:"+chat.getId());
        String topicName = chat.getId();
        kafkaAdminConfig.createTopic(topicName, 1, (short) 1);
        return chat;
    }

    public void createTopic(String topicName){
        kafkaAdminConfig.createTopic(topicName, 1, (short) 1);
    }

    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }


    public boolean topicExists(String topicName){
        ListTopicsResult topics = adminClient.listTopics();
        try {
            return topics.names().get().contains(topicName);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}

