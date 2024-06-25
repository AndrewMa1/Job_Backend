package buaa.cxtj.job_backend.Controller;

import buaa.cxtj.job_backend.Service.FirmService;
import buaa.cxtj.job_backend.Util.ReturnProtocol;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/firm")
@RequiredArgsConstructor
public class FirmController {
    private final FirmService firmService;

    @PostMapping("/createFirm")
    public ReturnProtocol createFirm(@RequestParam String name,@RequestParam String intro,@RequestParam String picture){
        return firmService.createFirm(name,intro,picture);
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
    @PostMapping("/hire")
    public ReturnProtocol hirePerson(@RequestParam String user_id,@RequestParam String corporation_id,@RequestParam String post_id){
        firmService.hireClerk(user_id,corporation_id,post_id);
        return new ReturnProtocol(true,"录用成功");
    }
}
