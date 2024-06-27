package buaa.cxtj.job_backend.Util;

import buaa.cxtj.job_backend.Mapper.FirmMapper;
import buaa.cxtj.job_backend.Mapper.UserMapper;
import buaa.cxtj.job_backend.POJO.DTO.UserDTO;
import buaa.cxtj.job_backend.POJO.Entity.Firm;
import buaa.cxtj.job_backend.POJO.Entity.User;
import buaa.cxtj.job_backend.POJO.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PermissionUntil {
    @Autowired
    FirmMapper firmMapper;

    /**
     * 判断该用户是否是管理员
     * @return
     */
    public void checkIfManagerOfFirm(){
        UserDTO user = UserHolder.getUser();
        String user_id = user.getId();
        String corporation_id = user.getCorporation();
        if(corporation_id == null || corporation_id.isBlank()){
            throw new RuntimeException("您非企业管理员,无权限进行操作!");
        }
        Firm firm = firmMapper.selectById(corporation_id);
        if(firm.getManagerId().equals(user_id)){
            return;
        }else{
            throw new RuntimeException("您非企业管理员,无权限进行操作!");
        }

    }
}
