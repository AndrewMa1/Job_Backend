package buaa.cxtj.job_backend.POJO.DTO;

import lombok.Data;

/**
 * 用于保存存在Redis里的某岗位的待录用人员的信息
 */
@Data
public class PendingOfferDTO {
    private String user_id;
    private String resume;

    public PendingOfferDTO() {
    }

    public PendingOfferDTO(String user_id, String resume) {
        this.user_id = user_id;
        this.resume = resume;
    }
}
