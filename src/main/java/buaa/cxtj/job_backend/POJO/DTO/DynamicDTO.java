package buaa.cxtj.job_backend.POJO.DTO;

import buaa.cxtj.job_backend.POJO.Entity.Dynamic;
import lombok.Data;

import java.util.List;

@Data
public class DynamicDTO {
    private String name;
    private List<Dynamic> dynamics;
    private List<Boolean> isAgree;
    private List<String> poster;
    private List<String> transNames;

    public DynamicDTO(String name,List<String> transNames ,List<Dynamic> dynamics) {
        this.name = name;
        this.transNames = transNames;
        this.dynamics = dynamics;
    }

    public DynamicDTO(String name, List<Dynamic> dynamics, List<Boolean> isAgree, List<String>poster) {
        this.name = name;
        this.dynamics = dynamics;
        this.isAgree = isAgree;
        this.poster = poster;
    }
}
