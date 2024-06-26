package buaa.cxtj.job_backend.POJO.DTO;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class CommentDTO {
    private String idCom;
    private String nameCom;
    private String idDy;
    private String comment;
    private String createTime;

    public CommentDTO(String idCom, String idDy, String comment) {
        this.idCom = idCom;
        this.idDy = idDy;
        this.comment = comment;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.createTime = sdf.format(new Date());;
    }
}
