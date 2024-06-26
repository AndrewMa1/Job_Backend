package buaa.cxtj.job_backend.Controller;

import buaa.cxtj.job_backend.POJO.Entity.User;
import buaa.cxtj.job_backend.Service.EmployService;
import buaa.cxtj.job_backend.Util.ReturnProtocol;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@RequestMapping(value = "/api/employ/")
public class EmployController {

    @Autowired
    EmployService employService;
    @Value("${file.upload-dir}")
    private String basePath;

    @GetMapping(value = "postInfo/{id}")
    public ReturnProtocol postJobInfo(@PathVariable String id){
        return null;
    }

    @PostMapping("delivery")
    public ReturnProtocol deliveryPost(@RequestParam String corporation_id,@RequestParam String user_id,@RequestParam String post_id,@RequestParam("file") MultipartFile file){
        try {
            log.info("基本信息为: "+corporation_id+'\n'+user_id+'\n'+post_id+'\n'+file.getOriginalFilename());
            byte[] bytes = file.getBytes();
            log.info("长度是 "+bytes.length);
            Path path = Paths.get(basePath + file.getOriginalFilename());
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return new ReturnProtocol(false,"上传失败");
        }
        log.info("基本信息为: "+corporation_id+'\n'+user_id+'\n'+post_id+'\n'+file.getOriginalFilename());
        employService.deliveryPostService(corporation_id,user_id,post_id,file.getOriginalFilename());
        return new ReturnProtocol(true,file.getOriginalFilename());
    }

    /**
     * 公司管理员查询该公司某岗位下的应聘成员列表
     * @param corporation_id
     * @param post_name
     * @return
     */
    @PostMapping ("queryEmployee")
    public ReturnProtocol queryEmployee(@RequestParam String corporation_id,@RequestParam String post_name){
       List<User> strings = employService.queryEmployee(corporation_id, post_name);
        return new ReturnProtocol(true,strings);
    }

}
