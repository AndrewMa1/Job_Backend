package buaa.cxtj.job_backend.POJO.DTO;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class TransDTO {
    private String transName;
    private String userId;
    private String content;
    private String transId;
    private String createTime;

    private String picture;

    public TransDTO(String transName, String userId, String content, String transId, String picture) {
        this.transName = transName;
        this.userId = userId;
        this.content = content;
        this.transId = transId;
        this.createTime = LocalDateTime.now().toString();
        this.picture = picture;
    }

}
