package buaa.cxtj.job_backend.POJO.DTO;

import lombok.Data;

@Data
public class TransDTO {
    private String transName;
    private String userId;
    private String content;

    public TransDTO(String transName, String userId, String content, String transId) {
        this.transName = transName;
        this.userId = userId;
        this.content = content;
        this.transId = transId;
    }

    private String transId;
}
