package buaa.cxtj.job_backend.POJO.DTO;

import buaa.cxtj.job_backend.POJO.Entity.Dynamic;
import lombok.Data;

@Data
public class FirmDynamicDTO {
    private String userName;
    private Dynamic dynamic;
    private boolean isAgree;

    public FirmDynamicDTO(String nickname, Dynamic dynamic,boolean isAgree) {
        this.userName = nickname;
        this.dynamic = dynamic;
        this.isAgree = isAgree;
    }
}
