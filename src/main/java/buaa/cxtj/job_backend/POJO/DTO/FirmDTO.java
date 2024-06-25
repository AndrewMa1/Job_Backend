package buaa.cxtj.job_backend.POJO.DTO;

import lombok.Data;

@Data
public class FirmDTO{
    private String id;              //企业Id
    private String name;            //企业名称
    private String intro;           //企业简介
    private String picture;         //企业图片
    private String managerId;       //管理员Id

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public FirmDTO(String id, String name, String intro, String picture, String managerId) {
        this.id = id;
        this.name = name;
        this.intro = intro;
        this.picture = picture;
        this.managerId = managerId;
    }
}
