package buaa.cxtj.job_backend.Controller;

import buaa.cxtj.job_backend.Service.EmployService;
import buaa.cxtj.job_backend.Util.ReturnProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/employ/")
public class EmployController {

    @Autowired
    EmployService deliveryService;

    @GetMapping(value = "postInfo/{id}")
    public ReturnProtocol postJobInfo(@PathVariable String id){
        return null;
    }

    @PostMapping("delivery")
    public ReturnProtocol deliveryPost(@RequestParam String corporation_id,@RequestParam String user_id){
        deliveryService.deliveryPostService(corporation_id,user_id);
        return new ReturnProtocol(true,"投递成功");
    }
}
