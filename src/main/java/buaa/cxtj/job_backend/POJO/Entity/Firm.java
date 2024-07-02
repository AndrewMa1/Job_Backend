package buaa.cxtj.job_backend.POJO.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_firm")
public class Firm {
    @TableId(value = "id",type = IdType.ASSIGN_UUID)
    private String id;              //企业Id
    private String name;            //企业名称
    private String intro;           //企业简介
    private String picture;         //企业图片
    private String managerId;       //管理员Id

    public Firm(String name, String intro, String picture, String managerId) {
        this.name = name;
        this.intro = intro;
        this.picture = picture;
        this.managerId = managerId;
    }
}

