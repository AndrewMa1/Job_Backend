package buaa.cxtj.job_backend.Controller;

import buaa.cxtj.job_backend.POJO.Entity.User;
import buaa.cxtj.job_backend.Service.EmployService;
import buaa.cxtj.job_backend.Util.ReturnProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "/api/employ/")
public class EmployController {

    @Autowired
    EmployService employService;

    @GetMapping(value = "postInfo/{id}")
    public ReturnProtocol postJobInfo(@PathVariable String id){
        return null;
    }

    @PostMapping("delivery")
    public ReturnProtocol deliveryPost(@RequestParam String corporation_id,@RequestParam String user_id,@RequestParam String post_name){
        employService.deliveryPostService(corporation_id,user_id,post_name);
        return new ReturnProtocol(true,"投递成功");
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
