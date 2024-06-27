package buaa.cxtj.job_backend.POJO.Entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("t_mail")
@NoArgsConstructor
@AllArgsConstructor
public class Mail {

    @TableId(value = "id",type = IdType.ASSIGN_UUID)
    private String id;
    private String senderId;
    private String receiveId;

    private String content;
    private String createTime;

    private Boolean isRead;


}
