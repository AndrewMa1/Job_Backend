package buaa.cxtj.job_backend.Service.Impl;

import buaa.cxtj.job_backend.Controller.Exception.HavePostException;
import buaa.cxtj.job_backend.Mapper.EmployMapper;
import buaa.cxtj.job_backend.Mapper.FirmMapper;
import buaa.cxtj.job_backend.Mapper.UserMapper;
import buaa.cxtj.job_backend.POJO.DTO.*;
import buaa.cxtj.job_backend.POJO.Entity.Firm;
import buaa.cxtj.job_backend.POJO.Entity.Job;
import buaa.cxtj.job_backend.POJO.Entity.Mail;
import buaa.cxtj.job_backend.POJO.Entity.User;
import buaa.cxtj.job_backend.POJO.UserHolder;
import buaa.cxtj.job_backend.Service.EmployService;
import buaa.cxtj.job_backend.Service.UserService;
import buaa.cxtj.job_backend.Util.RedisUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployServiceImpl extends ServiceImpl<EmployMapper, Job> implements EmployService {
    @Autowired
    RedisUtil redisUtil;

    @Autowired
    UserService userService;

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    UserMapper userMapper;

    @Autowired
    EmployMapper employMapper;
    @Autowired
    KafkaTopicServiceImpl kafkaTopicService;


    @Autowired
    FirmMapper firmMapper;
    @Override
    public void deliveryPostService(DeliveryPostDTO deliveryPostDTO) {
        String user_id = deliveryPostDTO.getUserId();
        String corporation_id = deliveryPostDTO.getCompanyId();
        String post_id = deliveryPostDTO.getJobId();
        String resume = deliveryPostDTO.getResumeName();
        ResumeStatusDTO resumeStatusDTO = new ResumeStatusDTO(corporation_id, post_id,0);
        redisUtil.lSet(RedisUtil.USERRESUME+user_id,JSONUtil.toJsonStr(resumeStatusDTO));
        redisUtil.sSet(RedisUtil.KEY_FIRM+corporation_id+":"+RedisUtil.KEY_FIRMPENDING+post_id,JSONUtil.toJsonStr(deliveryPostDTO));
    }

    @Override
    public List<ExhibitPendingDTO> queryEmployee(String corporation_id, String post_id) {
        String front = RedisUtil.KEY_FIRM + corporation_id + ":" +RedisUtil.KEY_FIRMPENDING+ post_id;
        log.info("查询到公司岗位为" + front);
        log.info("存在吗" + redisUtil.hasKey(front));
        if(!redisUtil.hasKey(front)){
            throw new RuntimeException("该公司该岗位暂时没有人员投递简历");
        }
        log.info("岗位投递数量为" + redisUtil.sGetSetSize(front));
        Set<Object> userList = redisUtil.sGet(front);
        Map<String, String> userMap = new HashMap<>();
        // 创建一个新的字符串类型的集合
        Set<String> stringSet = new HashSet<>();
        //保存user_id的列表
        Set<String>  users = new HashSet<>();
        for (Object obj : userList) {
            String userStr = String.valueOf(obj); // 假设 obj 能够正确转换为 String
            users.add(userStr);
        }
        log.info("目前在查询公司的某岗位投递人员,人员列表 "+userList);
        // 遍历对象集合，将每个对象转换为字符串类型，并添加到新集合中
        for (Object obj : userList) {
            String str = String.valueOf(obj); // 将对象转换为字符串
            log.info("对象字符串为 "+str);
            PendingOfferDTO pendingOfferDTO = JSONUtil.toBean(str, PendingOfferDTO.class);
            users.add(pendingOfferDTO.getUser_id());
            stringSet.add(str); // 添加到新集合中
            userMap.put(pendingOfferDTO.getUser_id(),pendingOfferDTO.getResume());
        }
        log.info("人员为"+users);
        List<User> usersInSet = userService.getUsersInSet(users);
        log.info("人员信息为 "+usersInSet);
        List<ExhibitPendingDTO> userDto = new ArrayList<>();
        for(User user : usersInSet){
            ExhibitPendingDTO exhibitPendingDTO = new ExhibitPendingDTO();
            BeanUtils.copyProperties(user,exhibitPendingDTO);
            exhibitPendingDTO.setResume(userMap.get(user.getId()));
            userDto.add(exhibitPendingDTO);
        }
        return userDto;
    }
    @Override
    public JobDTO queryJob(String job_id) {
        Job job = employMapper.selectById(job_id);
        if(job==null){
            throw new RuntimeException("查询不到指定的岗位");
        }
        log.info("查询到的结果为 "+ job);
        JobDTO jobDTO = new JobDTO();
        BeanUtils.copyProperties(job,jobDTO);
        Firm firm = firmMapper.selectById(job.getFirmId());
        if(firm==null){
            throw new RuntimeException("该岗位所属公司已跑路");
        }
        jobDTO.setFirmName(firm.getName());
        jobDTO.setPicture(firm.getPicture());
        return jobDTO;
    }

    @Override
    public FirmDTO queryFrim(String id) {
        Firm firm = firmMapper.selectById(id);
        User user = userMapper.selectById(firm.getManagerId());
        if(user==null){
            throw new RuntimeException("没找到这位管理员");
        }
        FirmDTO firmDTO = new FirmDTO(firm.getId(), firm.getName(), firm.getIntro(),firm.getManagerId(),user.getName(),firm.getPicture());
        return firmDTO;

    }

    @Override
    public void reject(String corporation_id, String user_id, String post_id) {
        User user = userMapper.selectById(user_id);
        String pending = RedisUtil.KEY_FIRM + corporation_id + ":" + RedisUtil.KEY_FIRMPENDING + post_id;
        if(user==null){
            throw new RuntimeException("用户表中不存在该用户");
        }
        String front = RedisUtil.KEY_FIRM + corporation_id + ":" + RedisUtil.KEY_FIRMPENDING + post_id;
        Boolean member = redisTemplate.opsForSet().isMember(front, user_id);
        Set<Object> objects1 = redisUtil.sGet(front);
        log.info("查询到的所有的该公司该岗位的投递人员 "+objects1);
        log.info("查询的key为 "+front);
        log.info("是否查到此人 "+member +" "+user_id);
        if(!member){
            throw new RuntimeException("该岗位投递无此人");
        }
        ResumeStatusDTO resumeStatusDTO = new ResumeStatusDTO(corporation_id, post_id, 0);
        List<Object> objects = redisUtil.lGet(RedisUtil.USERRESUME + user_id, 0, -1);
        log.info("该人员的所有投递的简历的状态"+objects);
        log.info("resume内容为"+JSONUtil.toJsonStr(resumeStatusDTO));
        if(objects==null || !objects.contains(JSONUtil.toJsonStr(resumeStatusDTO))){
            throw new RuntimeException("您从未投递过此公司此岗位的简历");
        }
        user.setJob(post_id);
        user.setCorporation(corporation_id);
        Job job = employMapper.selectById(post_id);
        user.setJobName(job.getJobName());
        userMapper.updateById(user);
        log.info("字符串为 "+pending);
        log.info("user_id "+user_id);
        redisTemplate.opsForSet().remove(pending, user_id);//直接将拒绝录取的人从待录取中直接删除
        redisUtil.sSet(RedisUtil.KEY_FIRM+corporation_id+":"+RedisUtil.KEY_FIRMCLERK+post_id,user_id);
        redisUtil.lSet(RedisUtil.STAFF+corporation_id,user_id);
        Mail mail = new Mail();
        mail.setSenderId(UserHolder.getUser().getId());
        mail.setReceiveId(userMapper.selectById(user_id).getId());
        mail.setCreateTime(LocalDateTime.now().toString());
        mail.setIsRead(false);

        String firm_name = firmMapper.selectById(corporation_id).getName();
        String job_name = employMapper.selectById(post_id).getJobName();
        mail.setContent("很遗憾，您已经被拒绝录取至"+ firm_name + "公司的" + job_name + "岗位！");

        redisUtil.lRemove(RedisUtil.USERRESUME + user_id,0,JSONUtil.toJsonStr(resumeStatusDTO));
        resumeStatusDTO.setStatus(-1);
        redisUtil.lSet(RedisUtil.USERRESUME + user_id,JSONUtil.toJsonStr(resumeStatusDTO));


        kafkaTopicService.sendMessage("Mail",JSONUtil.toJsonStr(mail));
    }


}
