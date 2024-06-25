package buaa.cxtj.job_backend.Service.Impl;

import buaa.cxtj.job_backend.Mapper.FirmMapper;
import buaa.cxtj.job_backend.Mapper.UserMapper;
import buaa.cxtj.job_backend.POJO.DTO.LoginFormDTO;
import buaa.cxtj.job_backend.POJO.DTO.RegisterDTO;
import buaa.cxtj.job_backend.POJO.DTO.UserDTO;
import buaa.cxtj.job_backend.POJO.Entity.Firm;
import buaa.cxtj.job_backend.POJO.Entity.User;
import buaa.cxtj.job_backend.POJO.Enum.EducationEnum;
import buaa.cxtj.job_backend.POJO.Enum.JobEnum;
import buaa.cxtj.job_backend.POJO.UserHolder;
import buaa.cxtj.job_backend.Service.UserService;
import buaa.cxtj.job_backend.Util.RedisUtil;
import buaa.cxtj.job_backend.Util.ReturnProtocol;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.jwt.JWTUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private final FirmMapper firmMapper;
    private final RedisUtil redisUtil;
    @Autowired
    private UserMapper userMapper;

    @Override
    // 根据用户 ID 集合查询用户信息
    public List<User> getUsersInSet(Set<String> idSet) {
        List<User> users = new ArrayList<>();
        for (String id : idSet) {
            users.add(userMapper.selectById(id));
        }
        return users;
    }

    public List<User> getUsers(Set<String> idSet) {
        return baseMapper.selectBatchIds(idSet);
    }

    @Override
    public ReturnProtocol login(LoginFormDTO loginForm) {
        //获取登录昵称和密码
        String nickname = loginForm.getNickname();
        String password = loginForm.getPassword();
        //从数据库中获取user
        System.out.println(nickname);
        System.out.println(password);

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .eq(User::getNickname, nickname)
                .eq(User::getPassword, password);

        User user = baseMapper.selectOne(wrapper);


        if (user == null) {
            //失败返回错误
            return new ReturnProtocol(false, "user does NOT exist");
        } else {
            //查找成功
            log.info("user log in:" + nickname);
            //将查找的user注入到UserDTO中
            UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
            //将登录的token放入DTO中
            String token = JWTUtil.createToken(BeanUtil.beanToMap(userDTO, false, true), "mty030806".getBytes());
            userDTO.setToken(token);
            //将登录信息存入Redis
            redisUtil.set(RedisUtil.USER_TOKEN + token, userDTO.getId(), RedisUtil.LOGIN_USER_TTL, TimeUnit.MINUTES);
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
        try {
            baseMapper.insert(user);
            return new ReturnProtocol(true, "注册成功", registerDTO);
        } catch (MybatisPlusException e) {
            return new ReturnProtocol(false, "注册失败");
        }
    }

    @Override
    public ReturnProtocol updateLink(String link) {
        String id = UserHolder.getUser().getId();
        User user = baseMapper.selectById(id);
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<User>()
                .set(User::getLink, link)
                .eq(User::getId, id);
        System.out.println(user);
        try {
            baseMapper.update(user, updateWrapper);
            log.info("update success " + id + " " + link);
            return new ReturnProtocol(true, "更新仓库/博客链接成功");
        } catch (MybatisPlusException e) {
            return new ReturnProtocol(false, "更新仓库/博客链接失败");
        }

    }


    @Override
    public ReturnProtocol updateAge(Integer age) {
        String id = UserHolder.getUser().getId();
        User user = baseMapper.selectById(id);
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<User>()
                .set(User::getAge, age)
                .eq(User::getId, id);
        try {
            baseMapper.update(user, wrapper);
            log.info("update age success " + id + " " + age);
            return new ReturnProtocol(true, "更新年龄成功");
        } catch (MybatisPlusException e) {
            return new ReturnProtocol(false, "更新年龄失败");
        }

    }

    @Override
    public ReturnProtocol updateIntro(String intro) {
        String id = UserHolder.getUser().getId();
        User user = baseMapper.selectById(id);
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<User>()
                .set(User::getIntro, intro)
                .eq(User::getId, id);
        try {
            baseMapper.update(user, wrapper);
            log.info("update intro success " + id + " " + intro);
            return new ReturnProtocol(true, "更新简介成功");
        } catch (MybatisPlusException e) {
            return new ReturnProtocol(false, "更新简介失败");
        }

    }

    @Override
    public ReturnProtocol addStaff(String firmId) {
        //获取当前登录的员工信息
        String staffId = UserHolder.getUser().getId();
        //TODO:使用Redis在企业的员工列表中新增员工
        try {
            redisUtil.lSet(RedisUtil.STAFF + firmId, staffId);
            return new ReturnProtocol(true, "新增员工成功");
        } catch (Exception e) {
            return new ReturnProtocol(false, "添加失败");
        }

    }

    @Override
    public ReturnProtocol deleteStaff(String staffId) {
        //获取当前登录的管理员所对应的公司
        String managerId = UserHolder.getUser().getId();
        LambdaQueryWrapper<Firm> wrapper = new LambdaQueryWrapper<Firm>()
                .eq(Firm::getManagerId, managerId);
        String firmId = firmMapper.selectOne(wrapper).getId();
        try {
            //TODO:使用Redis在企业的员工列表中删除员工
            redisUtil.lRemove(RedisUtil.STAFF + firmId, 1, staffId);
            return new ReturnProtocol(true, "删除员工成功");
        } catch (Exception e) {
            return new ReturnProtocol(false, "删除员工失败");
        }
    }

    /**
     *
     * @param follower 被关注者,关系是user关注了follower
     */
    @Override
    public ReturnProtocol follow(String follower) {
        String userId = UserHolder.getUser().getId();
        try {
            //在被关注者的粉丝列表里面新增当前用户
            redisUtil.lSet(RedisUtil.FOLLOWER + follower, userId);
            //在当前用户的关注列表里面新增被关注者
            redisUtil.lSet(RedisUtil.FOLLOW + userId,follower);
            return new ReturnProtocol(true, "关注成功");
        } catch (Exception e) {
            return new ReturnProtocol(false, "关注失败");
        }
    }

}
