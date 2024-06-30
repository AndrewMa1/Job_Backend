package buaa.cxtj.job_backend.Service.Impl;

import buaa.cxtj.job_backend.Controller.UserController;
import buaa.cxtj.job_backend.Mapper.EmployMapper;
import buaa.cxtj.job_backend.Mapper.FirmMapper;
import buaa.cxtj.job_backend.Mapper.UserMapper;
import buaa.cxtj.job_backend.POJO.DTO.*;
import buaa.cxtj.job_backend.POJO.Entity.Firm;
import buaa.cxtj.job_backend.POJO.Entity.Job;
import buaa.cxtj.job_backend.POJO.Entity.User;
import buaa.cxtj.job_backend.POJO.Enum.EducationEnum;
import buaa.cxtj.job_backend.POJO.Enum.JobEnum;
import buaa.cxtj.job_backend.POJO.UserHolder;
import buaa.cxtj.job_backend.Service.UserService;
import buaa.cxtj.job_backend.Util.RedisUtil;
import buaa.cxtj.job_backend.Util.ReturnProtocol;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWTUtil;
import com.alibaba.druid.support.json.JSONUtils;
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
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private final FirmMapper firmMapper;
    private final RedisUtil redisUtil;
    private  final EmployMapper employMapper;
    @Autowired
    UserMapper userMapper;

    @Override
    // 根据用户 ID 集合查询用户信息
    public List<User> getUsersInSet(Set<String> idSet) {
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

        User user = userMapper.selectOne(wrapper);


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
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .eq(User::getNickname,nickname);
        User user1 = baseMapper.selectOne(wrapper);
        if(user1!=null){
            return new ReturnProtocol(false,"注册失败,昵称重复");
        }
        try {
            baseMapper.insert(user);
            return new ReturnProtocol(true, "注册成功", registerDTO);
        } catch (MybatisPlusException e) {
            return new ReturnProtocol(false, "注册失败");
        }
    }

    @Override
    public ReturnProtocol update(UpdateDTO updateDTO){
        String id = UserHolder.getUser().getId();
        User user = baseMapper.selectById(id);
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<User>()
                .set(User::getAge,updateDTO.getAge())
                .set(User::getNickname,updateDTO.getNickname())
                .set(User::getBlog,updateDTO.getBlog())
                .set(User::getIntro,updateDTO.getIntro())
                .set(User::getRepo,updateDTO.getRepo())
                .set(User::getEducation,updateDTO.getEducation())
                .set(User::getInterestJob,updateDTO.getInterestJob())
                .set(User::getJobName,updateDTO.getJobName())
                .eq(User::getId,id);
        try{
            baseMapper.update(user,wrapper);
            log.info("update success");
            return new ReturnProtocol(true,"更新成功");
        }catch (MybatisPlusException e){
            return  new ReturnProtocol(false,"更新失败");
        }
    }

    @Override
    public ReturnProtocol addStaff(String firmId) {
        //获取当前登录的员工信息
        String staffId = UserHolder.getUser().getId();

        Firm firm = firmMapper.selectById(firmId);
        if(firm == null){
            return new ReturnProtocol(false,"公司不存在");
        }
        //TODO:使用Redis在企业的员工列表中新增员工
        try {
            redisUtil.sSet(RedisUtil.STAFF + firmId, staffId);
            LambdaUpdateWrapper<User>wrapper = new LambdaUpdateWrapper<User>()
                    .set(User::getCorporation,firmId)
                    .eq(User::getId,staffId);
            baseMapper.update(null,wrapper);
            Set<Object>staffs = redisUtil.sGet(RedisUtil.STAFF + firmId);



            return new ReturnProtocol(true, "新增员工成功",staffs);
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
            LambdaUpdateWrapper<User>wrapper1 = new LambdaUpdateWrapper<User>()
                    .set(User::getCorporation,null)
                    .set(User::getJob,null)
                    .set(User::getJobName,null)
                    .eq(User::getId,staffId);

            baseMapper.update(null,wrapper1);
            //TODO:使用Redis在企业的员工列表中删除员工
            redisUtil.setRemove(RedisUtil.STAFF + firmId, staffId);
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
            //将follower的粉丝数量加1
            LambdaUpdateWrapper<User>wrapper = new LambdaUpdateWrapper<User>()
                    .setSql("follower_num = follower_num + 1")
                    .eq(User::getId,follower);
            baseMapper.update(null,wrapper);

            LambdaUpdateWrapper<User>wrapper1 = new LambdaUpdateWrapper<User>()
                    .setSql("interest_num = interest_num + 1")
                            .eq(User::getId,userId);
            baseMapper.update(null,wrapper1);
            //在被关注者的粉丝列表里面新增当前用户
            redisUtil.sSet(RedisUtil.FOLLOWER + follower, userId);
            //在当前用户的关注列表里面新增被关注者
            redisUtil.sSet(RedisUtil.FOLLOW + userId,follower);
            return new ReturnProtocol(true, "关注成功");
        } catch (Exception e) {
            return new ReturnProtocol(false, "关注失败");
        }
    }

    @Override
    public ReturnProtocol getUser(String id) {
        try{
            User user = userMapper.selectById(id);
            return new ReturnProtocol(true,"获取成功",user);
        }catch (MybatisPlusException e){
            return new ReturnProtocol(false,"获取失败");
        }
    }

    @Override
    public ReturnProtocol getFirmId(String userId) {
        LambdaQueryWrapper<Firm> wrapper = new LambdaQueryWrapper<Firm>()
                .eq(Firm::getManagerId,userId);
        try{
            String firmId = firmMapper.selectOne(wrapper).getId();
            return new ReturnProtocol(true,"查询成功",firmId);
        }catch (MybatisPlusException e){
            return new ReturnProtocol(false,"查询失败");
        }

    }

    @Override
    public ReturnProtocol getStaffs(String firmId){
        try{
            Set<Object>staffs = redisUtil.sGet(RedisUtil.STAFF + firmId);
            return new ReturnProtocol(true,"获取成功",staffs);
        }catch (Exception e){
            return new ReturnProtocol(false,"获取失败");
        }


    }

    @Override
    public ReturnProtocol isFollowed(String id){
        try{
            String userId = UserHolder.getUser().getId();
            boolean isFollowed = redisUtil.sHasKey(RedisUtil.FOLLOW + userId,id);
            return new ReturnProtocol(true,"获取成功",isFollowed);
        }catch (Exception e){
            return new ReturnProtocol(false,"获取失败");
        }
    }

    @Override
    public ReturnProtocol deleteFollow(String id) {
        String userId = UserHolder.getUser().getId();
        try{
            redisUtil.setRemove(RedisUtil.FOLLOW+userId,id);
            redisUtil.setRemove(RedisUtil.FOLLOWER+id,userId);
            LambdaUpdateWrapper<User>wrapper = new LambdaUpdateWrapper<User>()
                    .setSql("follower_num = follower_num - 1")
                    .eq(User::getId,id);
            LambdaUpdateWrapper<User>wrapper1 = new LambdaUpdateWrapper<User>()
                    .setSql("interest_num = interest_num - 1")
                    .eq(User::getId,userId);
            baseMapper.update(null,wrapper1);

            baseMapper.update(null,wrapper);
            return new ReturnProtocol(true,"取消关注成功");
        }catch (Exception e){
            return new ReturnProtocol(false,"取消关注失败");
        }
    }

    @Override
    public ReturnProtocol changeAdmin(String newAdmin) {
        String userId = UserHolder.getUser().getId();
        try{
            LambdaUpdateWrapper<Firm> firmWrapper = new LambdaUpdateWrapper<Firm>()
                    .set(Firm::getManagerId,newAdmin)
                    .eq(Firm::getManagerId,userId);
            firmMapper.update(null,firmWrapper);
            LambdaUpdateWrapper<User>userWrapper = new LambdaUpdateWrapper<User>()
                    .set(User::getJobName,"管理员")
                    .eq(User::getId,newAdmin);
            baseMapper.update(null,userWrapper);
            userWrapper = new LambdaUpdateWrapper<User>()
                    .set(User::getJobName,"普通员工")
                    .eq(User::getId,userId);
            baseMapper.update(null,userWrapper);

            return new ReturnProtocol(true,"更改权限成功");
        }catch (MybatisPlusException e){
            return new ReturnProtocol(false,"更新权限失败");
        }

    }

    @Override
    public List<UserResumeStatusDTO> queryResumeStatus() {
        UserDTO user = UserHolder.getUser();
        List<Object> objects = redisUtil.lGet(RedisUtil.USERRESUME + user.getId(), 0, -1);
        log.info("全部项目 "+objects);
        // 将 JSON 字符串列表转换为 ResumeStatusDTO 对象列表
        List<ResumeStatusDTO> resumeStatusList = objects.stream()
                .map(json -> JSONUtil.toBean((String) json, ResumeStatusDTO.class))
                .collect(Collectors.toList());
        log.info("转化玩的全部项目为 "+resumeStatusList);
        List<UserResumeStatusDTO> userResumeStatusDTOS = new ArrayList<>();
        for(ResumeStatusDTO resumeStatus:resumeStatusList){
            Firm firm = firmMapper.selectById(resumeStatus.getCorporation_id());
            Job job = employMapper.selectById(resumeStatus.getPost_id());
            UserResumeStatusDTO userResumeStatusDTO = new UserResumeStatusDTO(firm.getName(), job.getJobName(), firm.getId(), resumeStatus.getStatus());
            userResumeStatusDTOS.add(userResumeStatusDTO);
        }
        return userResumeStatusDTOS;
    }

}
