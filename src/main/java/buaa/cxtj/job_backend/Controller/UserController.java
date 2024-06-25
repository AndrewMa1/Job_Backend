package buaa.cxtj.job_backend.Controller;


import buaa.cxtj.job_backend.POJO.DTO.LoginFormDTO;
import buaa.cxtj.job_backend.POJO.DTO.RegisterDTO;
import buaa.cxtj.job_backend.Service.UserService;
import buaa.cxtj.job_backend.Util.ReturnProtocol;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/login")
    public ReturnProtocol login(@RequestBody LoginFormDTO loginFormDTO) {

        return userService.login(loginFormDTO);
    }

    @PostMapping("register")
    public ReturnProtocol login(@RequestBody RegisterDTO registerDTO) {

        return userService.register(registerDTO);
    }

    @PostMapping("update/age")
    public ReturnProtocol updateAge(@RequestParam Integer age) {

        return userService.updateAge(age);
    }

    @PostMapping("update/intro")
    public ReturnProtocol updateIntro(@RequestParam String intro) {
        return userService.updateIntro(intro);
    }

    @PostMapping("update/link")
    public ReturnProtocol updateLink(@RequestParam String link) {
        return userService.updateLink(link);
    }

    @PostMapping("update/resume")
    public ReturnProtocol updateResume() {
        return userService.updateResume();
    }
}
