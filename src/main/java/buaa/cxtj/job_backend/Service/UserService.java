package buaa.cxtj.job_backend.Service;


import buaa.cxtj.job_backend.POJO.DTO.LoginFormDTO;
import buaa.cxtj.job_backend.POJO.DTO.RegisterDTO;
import buaa.cxtj.job_backend.POJO.Entity.User;
import buaa.cxtj.job_backend.Util.ReturnProtocol;
import com.baomidou.mybatisplus.extension.service.IService;


public interface UserService extends IService<User> {


    ReturnProtocol login(LoginFormDTO loginForm);


    ReturnProtocol register(RegisterDTO registerDTO);

    ReturnProtocol updateLink(String link);

    ReturnProtocol updateResume();

    ReturnProtocol updateAge(Integer age);

    ReturnProtocol updateIntro(String intro);
}
