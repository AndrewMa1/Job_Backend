package buaa.cxtj.job_backend.POJO.DTO;

import lombok.Data;

@Data
public class JobDTO {
    private String jobName;

    private String jobRequirements;

    private String jobCounts;

    public JobDTO(String jobName, String jobRequirements, String jobCounts) {
        this.jobName = jobName;
        this.jobRequirements = jobRequirements;
        this.jobCounts = jobCounts;
    }
}
