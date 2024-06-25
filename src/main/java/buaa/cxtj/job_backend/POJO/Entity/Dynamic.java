package buaa.cxtj.job_backend.POJO.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_dynamic")
public class Dynamic {
    @TableId(value = "id",type = IdType.ASSIGN_UUID)
    private String id;              //动态Id
    private String userId;      //用户Id
    private String content;       //动态内容
    private int agree;             //点赞量
    private int trans;              //转发量
    private String transId;         //被转发用户Id

}
