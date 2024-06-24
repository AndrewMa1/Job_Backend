package buaa.cxtj.job_backend.mty;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootTest
class JobBackendApplicationTests {
    @Autowired
    KafkaTemplate kafkaTemplate;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Test
    void contextLoads() {
        redisTemplate.opsForValue().set("val","123");
    }

}
