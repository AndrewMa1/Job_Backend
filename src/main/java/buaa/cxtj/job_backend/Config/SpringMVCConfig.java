package buaa.cxtj.job_backend.Config;


import buaa.cxtj.job_backend.Controller.Interceptor.LoginInterceptor;
import buaa.cxtj.job_backend.Util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringMVCConfig implements WebMvcConfigurer {

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor(redisUtil))
                .excludePathPatterns("/api/user/login")
                .excludePathPatterns("/api/user/register")
                .excludePathPatterns("/api/rec/recJobForVisitor")
                .excludePathPatterns("/static/**")
                .excludePathPatterns("/chat/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //匹配到resourceHandler,将URL映射至location,也就是本地文件夹
        registry.addResourceHandler("/static/image/user/**").addResourceLocations("file:/root/Job_Backend/static/image/user/");
        registry.addResourceHandler("/static/image/firm/**").addResourceLocations("file:/root/Job_Backend/static/image/firm/");
        registry.addResourceHandler("/static/resume/**").addResourceLocations("file:/root/Job_Backend/static/resume/");
        registry.addResourceHandler("/static/dynamic/**").addResourceLocations("file:/root/Job_Backend/static/dynamic/");
    }

}