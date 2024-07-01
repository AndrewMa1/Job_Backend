package buaa.cxtj.job_backend.Controller;

import buaa.cxtj.job_backend.POJO.DTO.*;
import buaa.cxtj.job_backend.POJO.Entity.User;
import buaa.cxtj.job_backend.POJO.UserHolder;
import buaa.cxtj.job_backend.Service.EmployService;
import buaa.cxtj.job_backend.Util.PermissionUntil;
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
//    @Value("${file.upload-dir}")
//    private String basePath;
    private String basePath="/root/Job_Backend/static/resume/job/";
//    private String basePath="src/main/resources/static/";

    @Autowired
    PermissionUntil permissionUntil;

    @GetMapping(value = "postInfo/{id}")
    public ReturnProtocol postJobInfo(@PathVariable String id){
        return null;
    }

    @PostMapping("delivery")
    public ReturnProtocol deliveryPost(@RequestParam String userId,
                                       @RequestParam String name,
                                       @RequestParam String sex,
                                       @RequestParam String education,
                                       @RequestParam MultipartFile resume,
                                       @RequestParam String companyId,
                                       @RequestParam String jobId,
                                       @RequestParam String intro,
                                       @RequestParam String age){
        if(userId.length()==0){
            throw new RuntimeException("您未登录,请登录");
        }
        String resumeName = resume.getOriginalFilename();
        DeliveryPostDTO deliveryPostDTO = new DeliveryPostDTO(userId,name,sex,education,resumeName,companyId,jobId,intro,age);
        try {
            log.info("基本信息为: "+deliveryPostDTO.getEducation()+'\n'+resumeName);
            byte[] bytes = resume.getBytes();
            log.info("长度是 "+bytes.length);
            Path path = Paths.get(basePath + resume.getOriginalFilename());
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return new ReturnProtocol(false,"上传失败");
        }
        deliveryPostDTO.setName(resumeName);
        employService.deliveryPostService(deliveryPostDTO);
        return new ReturnProtocol(true,resume.getOriginalFilename());
    }

    /**
     * 公司管理员查询该公司某岗位下的应聘成员列表
     * @param corporation_id
     * @param post_id
     * @return
     */
    @GetMapping ("queryEmployee")
    public ReturnProtocol queryEmployee(@RequestParam String corporation_id,@RequestParam String post_id){
        permissionUntil.checkIfManagerOfFirm();
       List<ExhibitPendingDTO> strings = employService.queryEmployee(corporation_id, post_id);
        return new ReturnProtocol(true,strings);
    }


    /**
     * 根据job_id返回对应信息
     */
    @GetMapping("queryJob/{id}")
    public ReturnProtocol queryJob(@PathVariable String id){
        JobDTO jobDTO = employService.queryJob(id);
        return new ReturnProtocol(true,jobDTO);
    }

    /**
     * 根据公司id返还公司信息
     */
    @GetMapping("queryFirm/{id}")
    public ReturnProtocol queryFirm(@PathVariable String id){
        return new ReturnProtocol(true,employService.queryFrim(id));
    }

    /**
     * 公司拒绝某人的申请
     * @return
     */
    @GetMapping("reject")
    public ReturnProtocol reject(@RequestParam String corporation_id, @RequestParam String user_id, @RequestParam String post_id){
        log.info("正在拒绝录用");
        permissionUntil.checkIfManagerOfFirm();
        employService.reject(corporation_id, user_id,post_id);
        return new ReturnProtocol(true,"拒绝成功");
    }

}
