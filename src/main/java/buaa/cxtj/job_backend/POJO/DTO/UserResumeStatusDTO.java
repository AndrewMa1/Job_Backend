package buaa.cxtj.job_backend.POJO.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserResumeStatusDTO {
    private String companyName;
    private String jobName;
    private String companyId;
    private int status;

    public UserResumeStatusDTO(String companyName, String jobName, String companyId, int status) {
        this.companyName = companyName;
        this.jobName = jobName;
        this.companyId = companyId;
        this.status = status;
    }
}
