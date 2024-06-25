package buaa.cxtj.job_backend.POJO.DTO;

import lombok.Data;

@Data
public class RegisterDTO {
    private String nickname;
    private String name;
    private String password;
    private Integer education;
    private Integer interestJob;
}
