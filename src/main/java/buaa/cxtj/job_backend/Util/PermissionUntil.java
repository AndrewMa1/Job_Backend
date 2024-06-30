package buaa.cxtj.job_backend.Util;

import buaa.cxtj.job_backend.Mapper.FirmMapper;
import buaa.cxtj.job_backend.Mapper.UserMapper;
import buaa.cxtj.job_backend.POJO.DTO.UserDTO;
import buaa.cxtj.job_backend.POJO.Entity.Firm;
import buaa.cxtj.job_backend.POJO.Entity.User;
import buaa.cxtj.job_backend.POJO.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PermissionUntil {
    @Autowired
    FirmMapper firmMapper;
    @Autowired
    UserMapper userMapper;
    /**
     * 判断该用户是否是管理员
     * @return
     */
    public void checkIfManagerOfFirm(){
        UserDTO user = UserHolder.getUser();
        log.info("当前登录用户为 "+user);
        String user_id = user.getId();
        User user1 = userMapper.selectById(user_id);
        if(user1==null){
            throw new RuntimeException("当前登录的用户不在用户表中");
        }
        String corporation_id = user1.getCorporation();
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
