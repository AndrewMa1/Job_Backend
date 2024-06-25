package buaa.cxtj.job_backend.Controller.test;


import buaa.cxtj.job_backend.Util.ReturnProtocol;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/kafka")
@Slf4j
public class testController {

    @Value("${file.upload-dir}")
    private String basePath;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    @GetMapping("/send")
    public ReturnProtocol send(@RequestParam("topic") String topic, @RequestParam("value") String msg){
        kafkaTemplate.send(topic,msg);
        log.info("send success");
        return new ReturnProtocol(true,"send to kafka success",msg);
    }

    @GetMapping("/fileUpload")
    public ReturnProtocol fileUpload(@RequestParam("file")MultipartFile file){
        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(basePath + file.getOriginalFilename());
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return new ReturnProtocol(false,"上传失败");
        }
        return new ReturnProtocol(true,"上传成功");
    }

    @GetMapping("/fileDownload")
    public ReturnProtocol fileDownload(@RequestParam("file")String url, HttpServletResponse response){
        File file = new File(basePath + url);
        if(!file.exists()) return new ReturnProtocol(false,"文件不存在");
        response.reset();
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "attachment;filename=" + url );
        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));) {
            byte[] buff = new byte[1024];
            OutputStream os  = response.getOutputStream();
            int i = 0;
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
                os.flush();
            }
        } catch (IOException e) {
            log.error("{}",e);
            return new ReturnProtocol(false,"上传失败");
        }
        return new ReturnProtocol(true,"上传成功");
    }

}
