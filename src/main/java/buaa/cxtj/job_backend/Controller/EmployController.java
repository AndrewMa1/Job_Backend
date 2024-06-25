package buaa.cxtj.job_backend.Controller;

import buaa.cxtj.job_backend.Util.ReturnProtocol;
import jakarta.websocket.server.PathParam;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/employ/")
public class EmployController {

    @GetMapping(value = "/postInfo/{id}")
    public ReturnProtocol postJobInfo(@PathVariable String id){
        return null;
    }
}
