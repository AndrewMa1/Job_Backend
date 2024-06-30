package buaa.cxtj.job_backend.POJO.DTO;

import lombok.Data;

@Data
public class UpdateDTO {
    private String nickname;
    private Integer education;
    private Integer interestJob;
    private Integer age;
    private String blog;
    private String repo;
    private String intro;
    private String jobName;
}
