package buaa.cxtj.job_backend.POJO.Entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private Integer id;
    private String from;
    private String to;
    private String msg;
    private String timestamp;
//    private Integer type;//消息发送的类型，0系统群发，1用户私聊
//    private Integer is_read;//消息是否已读，0未读，1已读
}
