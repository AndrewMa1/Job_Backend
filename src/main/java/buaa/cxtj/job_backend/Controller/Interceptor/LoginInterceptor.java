package buaa.cxtj.job_backend.Controller.Interceptor;

import buaa.cxtj.job_backend.POJO.DTO.UserDTO;
import buaa.cxtj.job_backend.POJO.UserHolder;
import buaa.cxtj.job_backend.Util.RedisUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;


@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    private RedisUtil redisUtil;

    public LoginInterceptor(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("authorization");
        if(StrUtil.isBlank(token)){
            response.setStatus(401);
            return false;
        }
        JWT jwt = JWTUtil.parseToken(token);
        if(jwt.verify()){
            response.setStatus(401);
            return false;
        }
        JSONObject payloads = jwt.getPayloads();
        UserDTO userDTO = JSONUtil.toBean(payloads, UserDTO.class);
        UserHolder.saveUser(userDTO);
        redisUtil.expire(RedisUtil.USER_TOKEN + token,RedisUtil.LOGIN_USER_TTL, TimeUnit.MINUTES);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }
}
