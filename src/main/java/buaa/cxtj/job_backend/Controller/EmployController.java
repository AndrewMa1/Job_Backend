package buaa.cxtj.job_backend.Controller;

import buaa.cxtj.job_backend.POJO.DTO.*;
import buaa.cxtj.job_backend.POJO.DTO.ExhibitPendingDTO;
import buaa.cxtj.job_backend.POJO.DTO.JobDTO;
import buaa.cxtj.job_backend.POJO.DTO.PendingOfferDTO;
import buaa.cxtj.job_backend.POJO.DTO.UserDTO;
import buaa.cxtj.job_backend.POJO.Entity.Job;
import buaa.cxtj.job_backend.POJO.Entity.User;
import buaa.cxtj.job_backend.POJO.Enum.JobEnum;
import buaa.cxtj.job_backend.POJO.UserHolder;
import buaa.cxtj.job_backend.Service.EmployService;
import buaa.cxtj.job_backend.Util.PermissionUntil;
import buaa.cxtj.job_backend.Util.ReturnProtocol;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
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
        String extensionName = resumeName.substring(resumeName.lastIndexOf("."));
        DeliveryPostDTO deliveryPostDTO = new DeliveryPostDTO(userId,name,sex,education,resumeName,companyId,jobId,intro,age);
        try {
            log.info("基本信息为: "+deliveryPostDTO.getEducation()+'\n'+resumeName);
            byte[] bytes = resume.getBytes();
            log.info("长度是 "+bytes.length);
            Path path = Paths.get(basePath + userId+"&"+jobId+extensionName);
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return new ReturnProtocol(false,"上传失败");
        }
        employService.deliveryPostService(deliveryPostDTO);
        return new ReturnProtocol(true,resume.getOriginalFilename());
    }

    @PostMapping("getUserPostResume")
    public ReturnProtocol getUserPostResume(@RequestBody jobResumeDTO jobResumeDTO, HttpServletResponse response){
        String user_id = jobResumeDTO.getUser_id();
        String post_id = jobResumeDTO.getPost_id();
        String pathName = basePath + user_id + "&" + post_id + ".pdf";
        File file = new File(pathName);
        if(!file.exists()){
            return new ReturnProtocol(false,"未上传简历");
        }
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "attachment;filename=" + user_id + "&" + post_id + ".pdf");
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



    /**
     * 公司管理员查询该公司某岗位下的应聘成员列表
     * @param corporation_id
     * @param post_id
     * @return
     */
    @GetMapping ("queryEmployee")
    public ReturnProtocol queryEmployee(@RequestParam String corporation_id,@RequestParam String post_id){
        permissionUntil.checkIfManagerOfFirm();
       List<DeliveryPostDTO> strings = employService.queryEmployee(corporation_id, post_id);
        return new ReturnProtocol(true,strings);
    }

    @PostMapping("editJobInfo")
    public ReturnProtocol editJobInfo(@RequestBody Job job){
        permissionUntil.checkIfManagerOfFirm();
        if(job.getJobDesc()!=null) {
            job.setJobDesc(JobEnum.getEnum(job.getJobDesc().getValue() - 1));
        }
        employService.updateById(job);
        return new ReturnProtocol(true,"修改成功");
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
