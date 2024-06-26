package buaa.cxtj.job_backend.POJO.DTO;

import lombok.Data;

@Data
public class FirmDTO{
    private String id;              //企业Id
    private String name;            //企业名称
    private String intro;           //企业简介
    private String managerId;       //管理员Id
    private String managerName;        //管理员名字

    public FirmDTO(String id, String name, String intro,  String managerId, String managerName) {
        this.id = id;
        this.name = name;
        this.intro = intro;
        this.managerId = managerId;
        this.managerName = managerName;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

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



    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public FirmDTO(String id, String name, String intro,  String managerId) {
        this.id = id;
        this.name = name;
        this.intro = intro;
        this.managerId = managerId;
    }
}
