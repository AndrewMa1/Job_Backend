package buaa.cxtj.job_backend.Service.Impl;

import buaa.cxtj.job_backend.Controller.Exception.HavePostException;
import buaa.cxtj.job_backend.Controller.Exception.NoInPendingException;
import buaa.cxtj.job_backend.Mapper.DynamicMapper;
import buaa.cxtj.job_backend.Mapper.EmployMapper;
import buaa.cxtj.job_backend.Mapper.FirmMapper;
import buaa.cxtj.job_backend.Mapper.UserMapper;
import buaa.cxtj.job_backend.POJO.DTO.FirmDTO;
import buaa.cxtj.job_backend.POJO.DTO.JobDTO;
import buaa.cxtj.job_backend.POJO.DTO.PendingOfferDTO;
import buaa.cxtj.job_backend.POJO.Entity.Dynamic;
import buaa.cxtj.job_backend.POJO.Entity.Firm;
import buaa.cxtj.job_backend.POJO.Entity.Job;
import buaa.cxtj.job_backend.POJO.Entity.User;
import buaa.cxtj.job_backend.POJO.Enum.JobEnum;
import buaa.cxtj.job_backend.POJO.UserHolder;
import buaa.cxtj.job_backend.Service.FirmService;

import buaa.cxtj.job_backend.Util.RedisUtil;
import buaa.cxtj.job_backend.Util.ReturnProtocol;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FirmServiceImpl extends ServiceImpl<FirmMapper, Firm> implements FirmService {
    @Autowired
    private FirmMapper firmMapper;
    @Autowired
    private EmployMapper employMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private DynamicMapper dynamicMapper;
    @Autowired
    KafkaTopicServiceImpl kafkaTopicService;

    @Autowired
    private RedisTemplate redisTemplate;




    @Override
    public ReturnProtocol createFirm(String name, String intro, MultipartFile picture) {
        try {
            // 获取图片的字节数组
            byte[] pictureBytes = picture.getBytes();
            // 将字节数组转换为Base64编码的字符串
            String pictureBase64 = Base64.getEncoder().encodeToString(pictureBytes);
            // 创建Firm对象并存储图片Base64字符串
            Firm firm = new Firm(name, intro, pictureBase64, UserHolder.getUser().getId());
            // 插入到数据库
            int insert = firmMapper.insert(firm);
            // 创建FirmDTO对象
            FirmDTO firmDTO = new FirmDTO(firm.getId(), name, intro, UserHolder.getUser().getId());
            // 返回成功的结果
            return new ReturnProtocol(true, "创建成功", firmDTO);
        } catch (IOException e) {
            e.printStackTrace();
            // 处理异常情况
            return new ReturnProtocol(false, "上传图片失败", null);
        }
    }

    @Override
    public ReturnProtocol showContent(String id) {
        Firm firm = firmMapper.selectById(id);
        User user = userMapper.selectById(firm.getManagerId());
        String managerName = user.getNickname();
        FirmDTO firmDTO = new FirmDTO(id,firm.getName(),firm.getIntro(),firm.getManagerId(),managerName);
        return new ReturnProtocol(true,"",firmDTO);
    }

    @Override
    public ReturnProtocol showDynamic(String id) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("corporation",id).orderByDesc("follower_num");
        List<User> users = userMapper.selectList(queryWrapper);
        List<Dynamic> dynamics = new ArrayList<>();
        for(User user:users){
            QueryWrapper<Dynamic> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("user_id",user.getId());
            dynamics.addAll(dynamicMapper.selectList(queryWrapper1));
        }
        dynamics.subList(0, Math.min(dynamics.size(),10));
        return new ReturnProtocol(true,"",dynamics);
    }

    @Override
    public ReturnProtocol showRecruit(String id) {
        // 查询符合条件的招聘岗位信息
        QueryWrapper<Job> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("firm_id", id); // 根据公司id查询
        List<Job> jobList = employMapper.selectList(queryWrapper);
        // 将查询结果转换为JobDTO对象列表
        List<JobDTO> jobDTOList = jobList.stream().map(job -> new JobDTO(job.getJobName(), job.getJobRequirements(), job.getJobCounts())).toList();
        return new ReturnProtocol(true,"", jobDTOList);
    }

    @Override
    public void hireClerk(String user_id, String corporation_id, String post_id) {
        User user = userMapper.selectById(user_id);
        if(user.getCorporation()!=null && !user.getCorporation().isBlank()){
            log.info("1"+user.getCorporation().isBlank());
            throw new HavePostException("该用户已经有岗位");
        }
        Map<String, Integer> userMap = new HashMap<>();
        PendingOfferDTO p1 = null;
        Set<Object> users = redisUtil.sGet(RedisUtil.KEY_FIRM + corporation_id + ":" + RedisUtil.KEY_FIRMPENDING + post_id);
        for (Object obj : users) {
            String str = String.valueOf(obj); // 将对象转换为字符串
            PendingOfferDTO pendingOfferDTO = JSONUtil.toBean(str, PendingOfferDTO.class);
            userMap.put(pendingOfferDTO.getUser_id(),pendingOfferDTO.getStatus());
            if(pendingOfferDTO.getUser_id().equals(user_id)){
                p1=pendingOfferDTO;
            }
        }
        if(p1==null){
            throw new RuntimeException("该岗位投递无此人");
        }
        if(userMapper.selectById(user_id).getCorporation()==null && !userMapper.selectById(user_id).getCorporation().isBlank()){
            throw new NoInPendingException("该员工已经有岗位了");
        }
        user.setJob(post_id);
        user.setCorporation(corporation_id);
        userMapper.updateById(user);
        redisUtil.sSet(RedisUtil.KEY_FIRM+corporation_id+":"+RedisUtil.KEY_FIRMCLERK+post_id,user_id);
    }

    @Override
    public void publishHireInfo(Job job) {
        employMapper.insert(job);
        JobEnum interestJob = job.getJobDesc();
        if(!kafkaTopicService.topicExists(interestJob.toString())){
            kafkaTopicService.createTopic(interestJob.toString());
        }
        kafkaTopicService.sendMessage(interestJob.toString(),JSONUtil.toJsonStr(job));
    }


}