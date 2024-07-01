package buaa.cxtj.job_backend.Controller;


import buaa.cxtj.job_backend.Service.AIService;
import buaa.cxtj.job_backend.Util.ReturnProtocol;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    @Autowired
    AIService aiService;

    @PostMapping("/refineResume")
    public void refineResume(@RequestParam("resume") MultipartFile resume, HttpServletResponse response) {
        String resume_content = null;
        try {
            resume_content = Base64.getEncoder().encodeToString(resume.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String refineResume = aiService.refineResume(resume_content);
//        response.reset();
        response.setContentType("application/octet-stream");
//        response.setContentType(String.valueOf(MediaType.APPLICATION_PDF));
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=optimized_resume.md");
        try (ByteArrayInputStream bis = new ByteArrayInputStream(refineResume.getBytes(StandardCharsets.UTF_8));
             OutputStream os = response.getOutputStream()) {
            byte[] buff = new byte[1024];
            int i = 0;
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
                os.flush();
            }
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json;charset=utf-8");
            try {
                response.getWriter().write("{\"success\": false, \"message\": \"文件下载失败\"}");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }



}
