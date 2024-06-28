package buaa.cxtj.job_backend.Controller;


import buaa.cxtj.job_backend.POJO.DTO.LoginFormDTO;
import buaa.cxtj.job_backend.POJO.DTO.RegisterDTO;
import buaa.cxtj.job_backend.POJO.DTO.UpdateDTO;
import buaa.cxtj.job_backend.POJO.Entity.User;
import buaa.cxtj.job_backend.POJO.UserHolder;
import buaa.cxtj.job_backend.Service.UserService;
import buaa.cxtj.job_backend.Util.ReturnProtocol;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RestController
@RequestMapping("/api/user/")
public class UserController {

    private final UserService userService;
    private final String baseResumePath = "static/resume/";

    @Autowired
    private ResourceLoader resourceLoader;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/login")
    public ReturnProtocol login(@RequestBody LoginFormDTO loginFormDTO) {

        return userService.login(loginFormDTO);
    }

    @PostMapping("register")
    public ReturnProtocol register(@RequestBody RegisterDTO registerDTO) {

        return userService.register(registerDTO);
    }

    @PostMapping("update")
    public ReturnProtocol update(@RequestBody UpdateDTO updateDTO){
        return userService.update(updateDTO);
    }

    @PostMapping("upload/resume")
    public ReturnProtocol updateResume(@RequestParam("file") MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            String userId = UserHolder.getUser().getId();
            String fileName = file.getOriginalFilename();
            if (fileName != null) {
                String extensionName = fileName.substring(fileName.lastIndexOf("."));

                URL resource = getClass().getClassLoader().getResource("");

                Path path = Paths.get(resource.getPath()+baseResumePath+userId+extensionName);

//                Path path = Paths.get(baseResumePath + userId + extensionName);
                log.info(String.valueOf(path.toAbsolutePath()));
                Files.write(path,bytes);
                LambdaUpdateWrapper<User>wrapper = new LambdaUpdateWrapper<User>()
                        .set(User::getResume,userId+extensionName)
                        .eq(User::getId,userId);

                userService.update(null,wrapper);
                return new ReturnProtocol(true, "上传成功",userId + extensionName);
            }else {
                return new ReturnProtocol(false,"上传失败,文件名为NULL");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ReturnProtocol(false, "上传失败,IO异常");
        }
    }

    @PostMapping("upload/image/avatar")
    public ReturnProtocol uploadImage(@RequestParam("file")MultipartFile file){
        try {
            byte[]bytes = file.getBytes();
            String userId = UserHolder.getUser().getId();
            String fileName = file.getOriginalFilename();
            if (fileName != null) {
                String extensionName = fileName.substring(fileName.lastIndexOf("."));
                String baseImagePath = "static/image/";

                URL resource = getClass().getClassLoader().getResource("");

                Path path = Paths.get(resource.getPath()+baseImagePath+userId+extensionName);

//                Path path = Paths.get(baseImagePath + userId + extensionName);
                log.info(String.valueOf(path.toAbsolutePath()));
                Files.write(path, bytes);
                return new ReturnProtocol(true, "上传成功", userId + extensionName);
            }else {
                return  new ReturnProtocol(false,"上传失败,文件名为null");
            }
        }catch (IOException e){
            e.printStackTrace();
            return new ReturnProtocol(false,"上传失败,IO异常");
        }
    }



    @GetMapping("download/resume")
    public ReturnProtocol downloadResume(@RequestParam("userId") String userId, HttpServletResponse response){
//        String pathName = baseResumePath +userId + ".pdf";
        URL resource = getClass().getClassLoader().getResource("");
        String pathName = resource.getPath()+baseResumePath+userId+ ".pdf";

        File file = new File(pathName);
        if(!file.exists()){
            return new ReturnProtocol(false,"未上传简历");
        }
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "attachment;filename=" + userId + ".pdf" );
        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            byte[] buff = new byte[1024];
            OutputStream os  = response.getOutputStream();
            int i;
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
                os.flush();
            }
            return new ReturnProtocol(true,"下载简历成功");
        } catch (IOException e) {
            return new ReturnProtocol(false,"下载简历失败");
        }
    }

    @GetMapping("add/staff")
    public ReturnProtocol addStaff(@RequestParam String firmId) {
        return userService.addStaff(firmId);
    }

    @PostMapping("delete/staff")
    public ReturnProtocol deleteStaff(@RequestBody String firmId) {
        return userService.deleteStaff(firmId);
    }

    @PostMapping("follow")
    public ReturnProtocol follow(@RequestParam String follower) {
        return userService.follow(follower);
    }

    @GetMapping("get/info")
    public ReturnProtocol getInfo(@RequestParam String id){
        return userService.getUser(id);
    }

    @GetMapping("get/firmId")
    public ReturnProtocol getFirmId(@RequestParam String userId){
        return userService.getFirmId(userId);
    }

    @GetMapping("get/staff")
    public ReturnProtocol getStaffs(@RequestParam String firmId){
        return userService.getStaffs(firmId);
    }

    @GetMapping("get/isFollowed")
    public ReturnProtocol isFollowed(@RequestParam String id){
        return userService.isFollowed(id);
    }

    @GetMapping("delete/follow")
    public ReturnProtocol deleteFollow(@RequestParam String id){
        return userService.deleteFollow(id);
    }
}
