package buaa.cxtj.job_backend.Controller;


import buaa.cxtj.job_backend.POJO.DTO.MailDTO;
import buaa.cxtj.job_backend.POJO.Entity.Mail;
import buaa.cxtj.job_backend.Service.MailService;
import buaa.cxtj.job_backend.Util.ReturnProtocol;
import cn.hutool.json.JSONUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mail")
public class MailController {

    @Autowired
    MailService mailService;

    @GetMapping("/getAllMails")
    public ReturnProtocol getAllMails(){
        return mailService.getAllMails();
    }

    @GetMapping("/delMail")
    public ReturnProtocol delMail(@RequestParam("mail_id") String mail_id){
        mailService.removeById(mail_id);
        return new ReturnProtocol(true,"删除成功");
    }

    @GetMapping("/readMail")
    public ReturnProtocol readMail(@RequestParam("mail_id") String mail_id){
        return mailService.readMail(mail_id);
    }



    // 当监听到消息时，自动往mail表里添加数据

    @KafkaListener(topics = {"Mail"},groupId = "Mail_cons")
    public void onMessage1(String mailJsonStr){
        Mail mail = JSONUtil.toBean(mailJsonStr, Mail.class);
        mail.setIsAnswer(false);
        mailService.save(mail);
    }



}
