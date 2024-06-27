package buaa.cxtj.job_backend.POJO.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JobDTO {
    private String postId;
    private String jobName;

    private String jobRequirements;

    private String jobCounts;

    private int wage;
    private String workPlace;
    private String internTime;
    private int bonus;

    public JobDTO( String postId,String jobName, String jobRequirements, String jobCounts) {
        this.postId = postId;
        this.jobName = jobName;
        this.jobRequirements = jobRequirements;
        this.jobCounts = jobCounts;
    }
}
