package buaa.cxtj.job_backend.Controller;

import buaa.cxtj.job_backend.POJO.DTO.ExhibitPendingDTO;
import buaa.cxtj.job_backend.POJO.DTO.JobDTO;
import buaa.cxtj.job_backend.POJO.DTO.PendingOfferDTO;
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
//    @Value("${file.upload-dir}")
//    private String basePath;
    private String basePath="/root/Job_Backend/resume/job/";


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
     * @param post_id
     * @return
     */
    @GetMapping ("queryEmployee")
    public ReturnProtocol queryEmployee(@RequestParam String corporation_id,@RequestParam String post_id){
       List<ExhibitPendingDTO> strings = employService.queryEmployee(corporation_id, post_id);
        return new ReturnProtocol(true,strings);
    }


    /**
     * 根据job_id返回对应信息
     */
    @GetMapping("queryJob/{id}")
    public ReturnProtocol queryJob(@PathVariable String id){
        List<JobDTO> jobDTOS = employService.queryJob(id);
        return new ReturnProtocol(true,jobDTOS);
    }

    /**
     * 根据公司id返还公司信息
     */
    @GetMapping("queryFirm/{id}")
    public ReturnProtocol queryFirm(@PathVariable String id){
        return new ReturnProtocol(true,employService.queryFrim(id));
    }

}
