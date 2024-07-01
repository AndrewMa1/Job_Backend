package buaa.cxtj.job_backend.POJO.DTO;

import buaa.cxtj.job_backend.POJO.Enum.EducationEnum;
import buaa.cxtj.job_backend.POJO.Enum.JobEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
public class DeliveryPostDTO {
    private String userId;

    private String name;

    private String sex;

    private String education;

    private String resumeName;

    private String companyId;

    private String jobId;

    private String intro;

    private String age;

    public DeliveryPostDTO(String userId, String name, String sex, String education, String resumeName, String companyId, String jobId, String intro, String age) {
        this.userId = userId;
        this.name = name;
        this.sex = sex;
        this.education = education;
        this.resumeName = resumeName;
        this.companyId = companyId;
        this.jobId = jobId;
        this.intro = intro;
        this.age = age;
    }
}
