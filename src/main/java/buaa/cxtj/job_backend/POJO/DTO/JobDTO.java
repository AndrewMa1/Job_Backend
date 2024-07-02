package buaa.cxtj.job_backend.POJO.DTO;

import buaa.cxtj.job_backend.POJO.Enum.JobEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JobDTO {
    private String jobId;
    private String jobName;
    private String firmId;

    private JobEnum jobDesc;

    private int hireCounts;

    private String jobRequirements;
    private String picture;
    private String firmName;

    private String jobCounts;

    private String wage;
    private String workPlace;
    private String internTime;
    private String bonus;

    public JobDTO(String postId, String jobName, String jobRequirements, String jobCounts, String wage, String workPlace, String internTime, String bonus,int hireCounts) {
        this.jobId = postId;
        this.jobName = jobName;
        this.jobRequirements = jobRequirements;
        this.jobCounts = jobCounts;
        this.wage = wage;
        this.workPlace = workPlace;
        this.internTime = internTime;
        this.bonus = bonus;
        this.hireCounts = hireCounts;
    }
}
