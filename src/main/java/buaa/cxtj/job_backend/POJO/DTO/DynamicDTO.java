package buaa.cxtj.job_backend.POJO.DTO;

import buaa.cxtj.job_backend.POJO.Entity.Dynamic;
import lombok.Data;

import java.util.List;

@Data
public class DynamicDTO {
    private String name;
    private List<Dynamic> dynamics;

    public DynamicDTO(String name, List<Dynamic> dynamics) {
        this.name = name;
        this.dynamics = dynamics;
    }
}
