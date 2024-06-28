package buaa.cxtj.job_backend.POJO.DTO;

import buaa.cxtj.job_backend.POJO.Entity.Dynamic;
import lombok.Data;

@Data
public class FirmDynamicDTO {
    private String userName;
    private Dynamic dynamic;

    public FirmDynamicDTO(String nickname, Dynamic dynamic) {
        this.userName = nickname;
        this.dynamic = dynamic;
    }
}
