package buaa.cxtj.job_backend.Controller;


import buaa.cxtj.job_backend.POJO.Entity.Message;
import buaa.cxtj.job_backend.Service.RecommendService;
import buaa.cxtj.job_backend.Util.ReturnProtocol;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/rec")
public class RecommendController {

    @Autowired
    RecommendService recommendService;


    @PostMapping("/recTrends")
    public ReturnProtocol recTrends() {
        recommendService.recTrends();
        return new ReturnProtocol(true,"推送成功");
    }

    @PostMapping("/recJob")
    public ReturnProtocol recJob() {
        return recommendService.recJob();
    }

//    @KafkaListener(topics = {""},groupId = "")
//    public void onMessage1(ConsumerRecord<?, ?> record){
//        // 消费的哪个topic、partition的消息,打印出消息内容
//        System.out.println("简单消费Topic："+record.topic()+"**分区"+record.partition()+"**值内容"+record.value());
//
//    }


}