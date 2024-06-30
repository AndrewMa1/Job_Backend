package buaa.cxtj.job_backend.Controller;

import buaa.cxtj.job_backend.Service.DynamicService;
import buaa.cxtj.job_backend.Service.FirmService;
import buaa.cxtj.job_backend.Util.ReturnProtocol;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/dynamic")
@RequiredArgsConstructor
public class DynamicController {
    @Autowired
    private final DynamicService dynamicService;

    @GetMapping("/showDynamic")
    public ReturnProtocol showDynamic(){
        return dynamicService.showDynamic();
    }

    @GetMapping("/postDynamic")
    public ReturnProtocol postDynamic(@RequestParam("content") String content){
        return dynamicService.postDynamic(content);
    }

    @PostMapping("/postDynamic")
    public ReturnProtocol postDynamic(@RequestParam("content") String content, @RequestParam(name = "picture", required = false)MultipartFile picture){
        return dynamicService.postDynamic(content,picture);
    }

    @GetMapping("/deleteDynamic")
    public ReturnProtocol deleteDynamic(@RequestParam("id") String id){
        return dynamicService.deleteDynamic(id);
    }

    @GetMapping("/agreeDynamic")
    public ReturnProtocol agreeDynamic(@RequestParam("id") String id){
        return dynamicService.agreeDynamic(id);
    }

    @GetMapping("/commentDynamic")
    public ReturnProtocol commentDynamic(@RequestParam("id") String id,@RequestParam("comment") String comment){
        return dynamicService.commentDynamic(id,comment);
    }

    @GetMapping("/showOther")
    public ReturnProtocol showOther(@RequestParam("id") String id){
        return dynamicService.showOther(id);
    }
    @GetMapping("/transDynamic")
    public ReturnProtocol transDynamic(@RequestParam String id, @RequestParam(name = "content", required = false) String new_content){
        return dynamicService.transDynamic(id,new_content);
    }

    @GetMapping("/showComment")
    public ReturnProtocol showComment(@RequestParam String id){
        return dynamicService.showComment(id);
    }
}
