package buaa.cxtj.job_backend.Controller;

import buaa.cxtj.job_backend.Service.DynamicService;
import buaa.cxtj.job_backend.Service.FirmService;
import buaa.cxtj.job_backend.Util.ReturnProtocol;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dynamic")
@RequiredArgsConstructor
public class DynamicController {
    @Autowired
    private final DynamicService dynamicService;

    @PostMapping("/postDynamic")
    public ReturnProtocol postDynamic(@RequestParam String content){
        return dynamicService.postDynamic(content);
    }

    @GetMapping("/deleteDynamic")
    public ReturnProtocol deleteDynamic(@RequestParam String id){
        return dynamicService.deleteDynamic(id);
    }

    @GetMapping("/agreeDynamic")
    public ReturnProtocol agreeDynamic(@RequestParam String id){
        return dynamicService.agreeDynamic(id);
    }
}
