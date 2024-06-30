package buaa.cxtj.job_backend.POJO.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResumeStatusDTO {
    private String corporation_id;
    private String post_id;
    //status为0:等待录取 为1:已录取 -1:被拒绝
    private int status;

    public ResumeStatusDTO(String corporation_id, String post_id) {
        this.corporation_id = corporation_id;
        this.post_id = post_id;
    }

    public ResumeStatusDTO(String corporation_id, String post_id, int status) {
        this.corporation_id = corporation_id;
        this.post_id = post_id;
        this.status = status;
    }
}
