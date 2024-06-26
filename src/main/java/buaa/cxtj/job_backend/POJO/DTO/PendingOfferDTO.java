package buaa.cxtj.job_backend.POJO.DTO;

import lombok.Data;

/**
 * 用于保存存在Redis里的某岗位的待录用人员的信息
 * status为true时说明该人还处于待录取阶段,为false说明已被录取
 */
@Data
public class PendingOfferDTO {
    private String user_id;
    private String resume;

    private boolean status;
    public PendingOfferDTO() {
    }

    public PendingOfferDTO(String user_id, String resume, boolean status) {
        this.user_id = user_id;
        this.resume = resume;
        this.status = status;
    }
}
