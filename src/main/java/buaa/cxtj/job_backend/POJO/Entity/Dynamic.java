package buaa.cxtj.job_backend.POJO.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("t_dynamic")
@NoArgsConstructor
public class Dynamic {
    @TableId(value = "id",type = IdType.ASSIGN_UUID)
    private String id;              //动态Id
    private String userId;      //用户Id
    private String content;       //动态内容
    private int agree;             //点赞量
    private int trans;              //转发量
    private String transId;         //被转发用户Id
    private int comments;
    private String createTime;      //创建时间

    public Dynamic( String userId, String content) {
        this.userId = userId;
        this.content = content;
        this.agree = 0;
        this.trans = 0;
        this.transId = null;
        this.createTime = LocalDateTime.now().toString();
    }

    public Dynamic(String userId, String content, String transId) {
        this.userId = userId;
        this.content = content;
        this.agree = 0;
        this.trans = 0;
        this.transId = transId;
        this.createTime = LocalDateTime.now().toString();
    }
}
