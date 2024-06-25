package buaa.cxtj.job_backend.Service.Impl;

import buaa.cxtj.job_backend.Mapper.UserMapper;
import buaa.cxtj.job_backend.POJO.DTO.LoginFormDTO;
import buaa.cxtj.job_backend.POJO.DTO.RegisterDTO;
import buaa.cxtj.job_backend.POJO.DTO.UserDTO;
import buaa.cxtj.job_backend.POJO.Entity.User;
import buaa.cxtj.job_backend.POJO.Enum.EducationEnum;
import buaa.cxtj.job_backend.POJO.Enum.JobEnum;
import buaa.cxtj.job_backend.POJO.UserHolder;
import buaa.cxtj.job_backend.Service.UserService;
import buaa.cxtj.job_backend.Util.ReturnProtocol;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.jwt.JWTUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public ReturnProtocol login(LoginFormDTO loginForm) {
        String nickname = loginForm.getNickname();
        String password = loginForm.getPassword();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .eq(User::getNickname, nickname)
                .eq(User::getPassword, password);
        User user = baseMapper.selectOne(wrapper);

        if (user == null) {
            return new ReturnProtocol(false, "user does NOT exist");
        } else {
            log.info("user log in:" + nickname);
            UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
            String token = JWTUtil.createToken(BeanUtil.beanToMap(userDTO, false, true), "mty030806".getBytes());
            userDTO.setToken(token);
            //TODO:将登录信息存入Redis


            return new ReturnProtocol(true, userDTO);

        }
    }

    @Override
    public ReturnProtocol register(RegisterDTO registerDTO) {
        String nickname = registerDTO.getNickname();
        String name = registerDTO.getName();
        String password = registerDTO.getPassword();
        EducationEnum education = EducationEnum.getEnum(registerDTO.getEducation());
        JobEnum interestJob = JobEnum.getEnum(registerDTO.getInterestJob());
        User user = new User(nickname, name, password, education, interestJob);
        baseMapper.insert(user);
        return new ReturnProtocol(true, "注册成功");
    }

    @Override
    public ReturnProtocol updateLink(String link) {
        String id = UserHolder.getUser().getId();
        User user = baseMapper.selectById(id);
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<User>()
                .set(User::getLink, link)
                .eq(User::getId, id);
        baseMapper.update(user, updateWrapper);
        log.info("update success " + id + " " + link);
        return new ReturnProtocol(true, "更新仓库/博客链接成功");
    }

    //TODO:上传简历文件
    @Override
    public ReturnProtocol updateResume() {
        return new ReturnProtocol(true, "上传简历成功");
    }

    @Override
    public ReturnProtocol updateAge(Integer age) {
        String id = UserHolder.getUser().getId();
        User user = baseMapper.selectById(id);
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<User>()
                .set(User::getAge, age)
                .eq(User::getId, id);
        baseMapper.update(user, wrapper);
        log.info("update age success " + id + " " + age);
        return new ReturnProtocol(true, "更新年龄成功");
    }

    @Override
    public ReturnProtocol updateIntro(String intro) {
        String id = UserHolder.getUser().getId();
        User user = baseMapper.selectById(id);
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<User>()
                .set(User::getIntro, intro)
                .eq(User::getId, id);
        baseMapper.update(user, wrapper);
        log.info("update intro success " + id + " " + intro);
        return new ReturnProtocol(true, "更新简介成功");
    }
}
