package buaa.cxtj.job_backend.POJO.Entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_chat")
public class Chat{
    @TableId(value = "id",type = IdType.ASSIGN_UUID)
    private String id;

    private String user1;
    private String user2;

    private String timestamp;

    @TableField(exist = false)
    private String nowUserId;

    @TableField(exist = false)
    private String user1Name;
    @TableField(exist = false)
    private String user2Name;

    public Chat(String user1, String user2, String timestamp){
        this.user1 = user1;
        this.user2 = user2;
        this.timestamp = timestamp;
    }
}
