package buaa.cxtj.job_backend.POJO.Entity;


import lombok.Data;

@Data
public class Message {
    private String sender;
    private String receiver;
    private String content;
    private String timestamp;

    public Message() {
    }


    public Message(String sender, String content) {
        this.sender = sender;
        this.content = content;
    }


    @Override
    public String toString() {
        return "Message{" +
                "sender='" + sender + '\'' +
                ", content='" + content + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
