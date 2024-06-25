package buaa.cxtj.job_backend.POJO.DTO;

import buaa.cxtj.job_backend.POJO.Enum.EducationEnum;
import buaa.cxtj.job_backend.POJO.Enum.JobEnum;
import lombok.Data;

@Data
public class UserDTO {
    private String nickname;
    private String id;
    private String name;
    private EducationEnum education;
    private JobEnum interestJob;
    private String link;
    private String resume;
    private String intro;
    private Integer age;
    private String job;

    private String token;
}
