package buaa.cxtj.job_backend.POJO.DTO;

import lombok.Data;

import java.io.File;

/**
 * 返回给前端的信息
 * 用于给企业管理员看该岗位待录取的员工的信息
 */

@Data
public class ExhibitPendingDTO {
    private String id;
    private String nickname;
    private String resume;
    private String intro;
}
