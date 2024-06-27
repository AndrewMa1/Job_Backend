package buaa.cxtj.job_backend.POJO.DTO;


import buaa.cxtj.job_backend.POJO.Entity.Mail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailDTO {
    private String id;
    private String senderId;
    private String senderName;

    private String receiveId;
    private String receiveName;

    private String content;
    private String createTime;
    private Boolean isRead;

    public MailDTO(Mail mail){
        this.id = mail.getId();
        this.senderId = mail.getSenderId();
        this.receiveId = mail.getReceiveId();
        this.content = mail.getContent();
        this.createTime = mail.getCreateTime();
        this.isRead = mail.getIsRead();
    }


}
