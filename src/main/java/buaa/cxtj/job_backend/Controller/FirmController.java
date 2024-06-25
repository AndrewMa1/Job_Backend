package buaa.cxtj.job_backend.Controller;

import buaa.cxtj.job_backend.Service.FirmService;
import buaa.cxtj.job_backend.Util.ReturnProtocol;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/firm")
@RequiredArgsConstructor
public class FirmController {
    private final FirmService firmService;

    @GetMapping("/createFirm")
    public ReturnProtocol createFirm(@RequestParam String id,@RequestParam String name,@RequestParam String intro,@RequestParam String picture){
        return firmService.createFirm(id,name,intro,picture);
    }
}
