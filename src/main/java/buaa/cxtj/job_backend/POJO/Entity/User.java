package buaa.cxtj.job_backend.POJO.Entity;


import buaa.cxtj.job_backend.POJO.Enum.EducationEnum;
import buaa.cxtj.job_backend.POJO.Enum.JobEnum;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("t_user")
public class User {
    @TableId(value = "id",type = IdType.ASSIGN_UUID)
    private String id;
    @TableField("nickname")
    private String nickname;
    @TableField("name")
    private String name;
    @TableField ("password")
    private String password;
    @TableField("education")
    private EducationEnum education;
    @TableField("interest_job")
    private JobEnum interestJob;
    @TableField("blog")
    private String blog;
    @TableField("repo")
    private String repo;
    @TableField("resume")
    private String resume;
    @TableField("intro")
    private String intro;
    @TableField("age")
    private Integer age;
    @TableField("job")
    private String job;
    @TableField("corporation")
    private String corporation;
    @TableField("follower_num")
    private Integer followerNum;
    @TableField("interest_num")
    private Integer interestNum;


    public User(String nickname, String name, String password, EducationEnum education, JobEnum interestJob) {
        this.nickname = nickname;
        this.name = name;
        this.password = password;
        this.education = education;
        this.interestJob = interestJob;
    }

    public User() {
    }
}

