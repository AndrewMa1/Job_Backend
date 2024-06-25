package buaa.cxtj.job_backend.Service;


import buaa.cxtj.job_backend.POJO.DTO.LoginFormDTO;
import buaa.cxtj.job_backend.POJO.DTO.RegisterDTO;
import buaa.cxtj.job_backend.POJO.Entity.User;
import buaa.cxtj.job_backend.Util.ReturnProtocol;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Set;


public interface UserService extends IService<User> {


    // 根据用户 ID 集合查询用户信息
    public List<User> getUsersInSet(Set<String> idSet);
    ReturnProtocol login(LoginFormDTO loginForm);


    ReturnProtocol register(RegisterDTO registerDTO);

    ReturnProtocol updateLink(String link);


    ReturnProtocol updateAge(Integer age);

    ReturnProtocol updateIntro(String intro);

    ReturnProtocol addStaff(String firmId);

    ReturnProtocol deleteStaff(String staffId);

    ReturnProtocol follow(String id);
}
