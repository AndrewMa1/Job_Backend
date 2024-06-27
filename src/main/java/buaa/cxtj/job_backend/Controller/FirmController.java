package buaa.cxtj.job_backend.Controller;

import buaa.cxtj.job_backend.POJO.DTO.FirmDTO;
import buaa.cxtj.job_backend.POJO.Entity.Job;
import buaa.cxtj.job_backend.Service.FirmService;
import buaa.cxtj.job_backend.Util.PermissionUntil;
import buaa.cxtj.job_backend.Util.ReturnProtocol;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping("/api/firm")
@RequiredArgsConstructor
public class FirmController {
    private final FirmService firmService;
    @Autowired
    private PermissionUntil permissionUntil;

    @PostMapping("/createFirm")
    public ReturnProtocol createFirm(@RequestParam("name") String name,@RequestParam("intro") String intro,@RequestParam("picture") MultipartFile picture){
        return firmService.createFirm(name,intro,picture);
    }
    @PostMapping("/editContent")
    public ReturnProtocol editContent(@RequestParam("id") String id,@RequestParam("name") String name,@RequestParam("intro") String intro,@RequestParam("picture") MultipartFile picture){
        return firmService.editContent(id,name,intro,picture);
    }
    @GetMapping("/exitFirm")
    public ReturnProtocol exitFirm(){
        return firmService.exitFirm();
    }
    @GetMapping("/showMember")
    public ReturnProtocol showMembers(@RequestParam String id){
        return firmService.showMembers(id);
    }

    @GetMapping("/showContent")
    public ReturnProtocol showContent(@RequestParam String id){
        return firmService.showContent(id);
    }

    @GetMapping("/showDynamic")
    public ReturnProtocol showDynamic(@RequestParam String id){
        return firmService.showDynamic(id);
    }

    @GetMapping("/showRecruit")
    public ReturnProtocol showRecruit(@RequestParam String id){
        return firmService.showRecruit(id);
    }

    /**
     *
     * @param user_id 录取的人的id
     * @param corporation_id 录用公司id
     * @param post_id 录取的岗位id
     * @return
     */
    @GetMapping("/hire")
    public ReturnProtocol hirePerson(@RequestParam String user_id,@RequestParam String corporation_id,@RequestParam String post_id){
        log.info("正在录取clerk");
        permissionUntil.checkIfManagerOfFirm();
        firmService.hireClerk(user_id,corporation_id,post_id);
        return new ReturnProtocol(true,"录用成功");
    }

    @PostMapping("/publishInfo")
    public ReturnProtocol publishHireInfo(@RequestBody Job job){
        permissionUntil.checkIfManagerOfFirm();
        firmService.publishHireInfo(job);
        return new ReturnProtocol(true,"发布成功");
    }
}
