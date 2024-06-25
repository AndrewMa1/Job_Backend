package buaa.cxtj.job_backend.mty;

import buaa.cxtj.job_backend.Util.RedisUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

@SpringBootTest
class JobBackendApplicationTests {
    @Autowired
    KafkaTemplate kafkaTemplate;

    @Autowired
    RedisUtil redisUtil;



    @Test
    void contextLoads() {

    }

}
