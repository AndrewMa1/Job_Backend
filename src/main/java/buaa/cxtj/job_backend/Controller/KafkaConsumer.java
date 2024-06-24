package buaa.cxtj.job_backend.Controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@EnableKafka
public class KafkaConsumer {


//    @KafkaListener(topics = "test", groupId = "mty")
//    public void test(String msg) {
//        log.info("从test分区收到消息: " + msg);
//        stringRedisTemplate.opsForValue().set("val", "1");
//    }

}
