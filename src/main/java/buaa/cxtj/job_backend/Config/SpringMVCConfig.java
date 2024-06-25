package buaa.cxtj.job_backend.Config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringMVCConfig implements WebMvcConfigurer {

//    @Autowired
//    private RedisUtil redisUtil;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new LoginInterceptor(redisUtil))
//                .excludePathPatterns("/api/search/**").excludePathPatterns("/api/users/login")
//                .excludePathPatterns("/api/users/register").excludePathPatterns("/api/users/sendCode");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //匹配到resourceHandler,将URL映射至location,也就是本地文件夹
        registry.addResourceHandler("/file/**").addResourceLocations("src/main/resources/static/");
    }

}