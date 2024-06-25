package buaa.cxtj.job_backend.POJO.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Firm {
    @TableId(value = "id",type = IdType.ASSIGN_UUID)
    private String id;              //企业Id
    private String name;            //企业名称
    private String intro;           //企业简介
    private String picture;         //企业图片
    private String managerId;       //管理员Id
}