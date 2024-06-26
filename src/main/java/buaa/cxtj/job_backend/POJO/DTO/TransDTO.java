package buaa.cxtj.job_backend.POJO.DTO;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class TransDTO {
    private String transName;
    private String userId;
    private String content;
    private String transId;
    private String createTime;

    public TransDTO(String transName, String userId, String content, String transId) {
        this.transName = transName;
        this.userId = userId;
        this.content = content;
        this.transId = transId;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.createTime = sdf.format(new Date());;
    }

}
