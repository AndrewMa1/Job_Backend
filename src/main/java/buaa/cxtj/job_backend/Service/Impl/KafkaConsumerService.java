package buaa.cxtj.job_backend.Service.Impl;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Service
public class KafkaConsumerService {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Autowired
    KafkaTopicServiceImpl kafkaTopicService;

    private final String groupId = "chat-cons";

    @Value("${spring.kafka.consumer.key-deserializer}")
    private String keyDeserializer;

    @Value("${spring.kafka.consumer.value-deserializer}")
    private String valueDeserializer;

    private KafkaConsumer<String, String> createConsumer() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new KafkaConsumer<>(props);
    }

    public List<String> readMessagesFromPartition(String topicName, int partition) {
        if(!kafkaTopicService.topicExists(topicName)){
            return new ArrayList<>();
        }

        KafkaConsumer<String, String> consumer = createConsumer();
        TopicPartition topicPartition = new TopicPartition(topicName, partition);
        consumer.assign(Collections.singletonList(topicPartition));
        consumer.seekToBeginning(Collections.singletonList(topicPartition));


        List<String> messages = new ArrayList<>();
        boolean continuePolling = true;

        while (continuePolling) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
            if (records.isEmpty()) {
                continuePolling = false;
            } else {
                for (ConsumerRecord<String, String> record : records) {
                    messages.add(record.value());
                }
            }
        }
        consumer.close();
        return messages;
    }
}

