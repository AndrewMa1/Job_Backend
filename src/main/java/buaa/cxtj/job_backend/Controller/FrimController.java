package buaa.cxtj.job_backend.Controller;

import buaa.cxtj.job_backend.Service.FirmService;
import buaa.cxtj.job_backend.Util.ReturnProtocol;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/firm")
@RequiredArgsConstructor
public class FrimController {
    private final FirmService firmService;

    @PostMapping("/createFirm")
    public ReturnProtocol createFirm(@RequestParam String id,@RequestParam String name,@RequestParam String intro,@RequestParam String picture){
        return firmService.createFirm(id,name,intro,picture);
    }

    @GetMapping("/showContent")
    public ReturnProtocol showContent(@RequestParam String id){
        return null;
    }
}
