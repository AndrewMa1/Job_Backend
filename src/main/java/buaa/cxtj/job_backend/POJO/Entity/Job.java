package buaa.cxtj.job_backend.POJO.Entity;
import buaa.cxtj.job_backend.POJO.Enum.JobEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.ArrayList;

@Data
@TableName("t_job")
public class Job {
    @TableId(value = "job_id",type = IdType.ASSIGN_UUID)
    private String jobId;

    private String firmId;

    private String jobName;

    private String jobRequirements;

    private String jobCounts;

    @TableField("job_desc")
    private JobEnum jobDesc;

    private String wage;

    @TableField("work_place")
    private String workPlace;
    @TableField("intern_time")
    private String internTime;
    private String bonus;



    @TableField(exist = false)
    private ArrayList<String> preEmployeeList;


}