package buaa.cxtj.job_backend.Controller;


import buaa.cxtj.job_backend.Util.ReturnProtocol;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kafka")
@Slf4j
public class KafkaController {

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    @GetMapping("/send")
    public ReturnProtocol send(@RequestParam("topic") String topic, @RequestParam("value") String msg){
        kafkaTemplate.send(topic,msg);
        log.info("send success");
        return new ReturnProtocol(true,"send to kafka success",msg);
    }

}
