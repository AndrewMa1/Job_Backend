package buaa.cxtj.job_backend.Service.Impl;

import buaa.cxtj.job_backend.Controller.Exception.HavePostException;
import buaa.cxtj.job_backend.Controller.Exception.NoInPendingException;
import buaa.cxtj.job_backend.Mapper.DynamicMapper;
import buaa.cxtj.job_backend.Mapper.EmployMapper;
import buaa.cxtj.job_backend.Mapper.FirmMapper;
import buaa.cxtj.job_backend.Mapper.UserMapper;
import buaa.cxtj.job_backend.POJO.DTO.*;
import buaa.cxtj.job_backend.POJO.Entity.*;
import buaa.cxtj.job_backend.POJO.Enum.JobEnum;
import buaa.cxtj.job_backend.POJO.UserHolder;
import buaa.cxtj.job_backend.Service.FirmService;

import buaa.cxtj.job_backend.Service.MailService;
import buaa.cxtj.job_backend.Util.RedisUtil;
import buaa.cxtj.job_backend.Util.ReturnProtocol;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    private MailService mailService;
    @Autowired
    KafkaTopicServiceImpl kafkaTopicService;
    @Autowired
    private RedisTemplate redisTemplate;




    @Override
    public ReturnProtocol createFirm(String name,  String intro,  MultipartFile picture) {
        User user = userMapper.selectById(UserHolder.getUser().getId());
        if(user.getCorporation()!=null && !user.getCorporation().isBlank()){
            throw new HavePostException("该用户已经有公司！");
        }
        QueryWrapper<Firm> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name",name);
        Firm existingFirm = firmMapper.selectOne(queryWrapper);
        if (existingFirm != null) {
            // 如果已存在同名公司，可以根据实际需求进行处理，比如抛出异常或者返回相应信息
            return new ReturnProtocol(false,"已存在同名公司!");
        }
        try {
            byte[]bytes = picture.getBytes();
            String userId = UserHolder.getUser().getId();
            String fileName = picture.getOriginalFilename();
            Firm firm = new Firm(name,intro,fileName,userId);
            firmMapper.insert(firm);
            String firm_id = firm.getId();

            if (fileName != null) {
                String extensionName = fileName.substring(fileName.lastIndexOf("."));

                String baseImagePath = "/root/Job_Backend/static/image/firm/";
//                Path path = Paths.get(baseImagePath + firm_id + extensionName);

//                URL resource = getClass().getClassLoader().getResource("");

                Path path = Paths.get(baseImagePath+firm_id+extensionName);

                log.info(String.valueOf(path.toAbsolutePath()));
                Files.write(path, bytes);
                redisUtil.lSet(RedisUtil.STAFF + firm_id, userId);
                user.setCorporation(firm.getId());
                user.setJob("Manager");
                user.setJobName("管理员");
                userMapper.updateById(user);
                firm.setPicture(firm.getId()+extensionName);
                firmMapper.updateById(firm);
                return new ReturnProtocol(true, "创建公司成功", firm_id + extensionName);
            }else {
                return  new ReturnProtocol(false,"上传失败,文件名为null");
            }
        }catch (IOException e){
            e.printStackTrace();
            return new ReturnProtocol(false,"上传失败,IO异常");
        }
    }

    @Override
    public ReturnProtocol showContent(String id) {
        Firm firm = firmMapper.selectById(id);
        User user = userMapper.selectById(firm.getManagerId());
        String managerName = user.getNickname();
        FirmDTO firmDTO = new FirmDTO(id,firm.getName(),firm.getIntro(),firm.getManagerId(),managerName,firm.getPicture());
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
        List<Dynamic> sortedDynamics = dynamics.stream()
                .sorted((d1, d2) -> d2.getCreateTime().compareTo(d1.getCreateTime()))
                .collect(Collectors.toList());
        // 截取前10条
        List<Dynamic> top10Dynamics = sortedDynamics.subList(0, Math.min(sortedDynamics.size(), 10));
        List<FirmDynamicDTO> firmDynamicDTOS = new ArrayList<>();
        String meId = UserHolder.getUser().getId();
        Set<String> agreeSet = redisUtil.sGet(RedisUtil.AGREE + meId).stream().map(Object::toString).collect(Collectors.toSet());

        for(Dynamic dynamic:top10Dynamics){
            User user = userMapper.selectById(dynamic.getUserId());
            if(agreeSet.contains(dynamic.getId())){
                firmDynamicDTOS.add(new FirmDynamicDTO(user.getNickname(),dynamic,true));
            }else{
                firmDynamicDTOS.add(new FirmDynamicDTO(user.getNickname(),dynamic,false));
            }
        }
        return new ReturnProtocol(true, "",firmDynamicDTOS);
    }

    @Override
    public ReturnProtocol showRecruit(String id) {
        // 查询符合条件的招聘岗位信息
        QueryWrapper<Job> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("firm_id", id); // 根据公司id查询
        List<Job> jobList = employMapper.selectList(queryWrapper);
        // 将查询结果转换为JobDTO对象列表
        List<JobDTO> jobDTOList = jobList.stream().map(job -> new JobDTO(job.getJobId(),job.getJobName(), job.getJobRequirements(), job.getJobCounts(),job.getWage(),job.getWorkPlace(),job.getInternTime(),job.getBonus())).toList();
        return new ReturnProtocol(true,"", jobDTOList);
    }

    @Override
    @Transactional
    public void hireClerk(String user_id, String corporation_id, String post_id) {
        User user = userMapper.selectById(user_id);
        String pending = RedisUtil.KEY_FIRM + corporation_id + ":" + RedisUtil.KEY_FIRMPENDING + post_id;
        if(user==null){
            throw new RuntimeException("用户表中不存在该用户");
        }
        if(user.getCorporation()!=null && !user.getCorporation().isBlank()){
            log.info("1"+user.getCorporation().isBlank());
            throw new HavePostException("该用户已经有岗位");
        }
        String front = RedisUtil.KEY_FIRM + corporation_id + ":" + RedisUtil.KEY_FIRMPENDING + post_id;
        Set<Object> objects2 = redisUtil.sGet(front);
        int flag = 0;
        List<DeliveryPostDTO> deliveryPostDTOS = new ArrayList<>();
        for(Object ob : objects2){
            String str = String.valueOf(ob);
            DeliveryPostDTO deliveryPostDTO = JSONUtil.toBean(str, DeliveryPostDTO.class);
            if(deliveryPostDTO.getUserId().equals(user_id)){
                flag = 1;
            }else{
                deliveryPostDTOS.add(deliveryPostDTO);
            }
        }
        Set<Object> objects1 = redisUtil.sGet(front);
        log.info("查询到的所有的该公司该岗位的投递人员 "+objects1);
        log.info("查询的key为 "+front);
        log.info("是否查到此人 "+flag +" "+user_id);
        if(flag == 0){
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
        job.setHireCounts(job.getHireCounts()+1);
        employMapper.updateById(job);

        user.setJobName(job.getJobName());
        userMapper.updateById(user);
        log.info("字符串为 "+pending);
        log.info("user_id "+user_id);

        log.info("全部删除候选人");
        redisUtil.del(front);
        //直接将被录取的人从待录取中直接删除
        if(deliveryPostDTOS.size()!=0){
            log.info("正在重新添加被删除后的候选人");
            for(DeliveryPostDTO p:deliveryPostDTOS){
                String s1 = JSONUtil.toJsonStr(p);
                redisUtil.sSet(front,s1);
            }
        }
        redisUtil.sSet(RedisUtil.KEY_FIRM+corporation_id+":"+RedisUtil.KEY_FIRMCLERK+post_id,user_id);
        redisUtil.lSet(RedisUtil.STAFF+corporation_id,user_id);
        Mail mail = new Mail();
        mail.setSenderId(UserHolder.getUser().getId());
        mail.setReceiveId(userMapper.selectById(user_id).getId());
        mail.setCreateTime(LocalDateTime.now().toString());
        mail.setIsRead(false);
        mail.setType(0);

        String firm_name = firmMapper.selectById(corporation_id).getName();
        String job_name = employMapper.selectById(post_id).getJobName();
        mail.setContent("恭喜您，您已经被录取至"+ firm_name + "公司的" + job_name + "岗位！");

        redisUtil.lRemove(RedisUtil.USERRESUME + user_id,0,JSONUtil.toJsonStr(resumeStatusDTO));
        resumeStatusDTO.setStatus(1);
        redisUtil.lSet(RedisUtil.USERRESUME + user_id,JSONUtil.toJsonStr(resumeStatusDTO));


        kafkaTopicService.sendMessage("Mail",JSONUtil.toJsonStr(mail));
    }


    @Override
    public void publishHireInfo(Job job) {
        job.setHireCounts(0);
        employMapper.insert(job);
        JobEnum interestJob = job.getJobDesc();
        if(!kafkaTopicService.topicExists(interestJob.toString())){
            kafkaTopicService.createTopic(interestJob.toString());
        }
        kafkaTopicService.sendMessage(interestJob.toString(),JSONUtil.toJsonStr(job));
    }

    @Override
    public ReturnProtocol showMembers(String id) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("corporation",id).orderByDesc("follower_num");
        List<User> users = userMapper.selectList(queryWrapper);
        return new ReturnProtocol(true,"",users);
    }

    @Override
    public ReturnProtocol editContent(String firm_id,String name,String intro,MultipartFile picture) {
        Firm firm = firmMapper.selectById(firm_id);
        if(!Objects.equals(firm.getManagerId(), UserHolder.getUser().getId())){
            return new ReturnProtocol(false,"您不是公司管理员，不能编辑！");
        }
        try {
            byte[]bytes = picture.getBytes();
            String fileName = picture.getOriginalFilename();
            if (fileName != null) {
                String extensionName = fileName.substring(fileName.lastIndexOf("."));
                String baseImagePath = "/root/Job_Backend/static/image/firm/";
//                Path path = Paths.get(baseImagePath + firm_id + extensionName);

//                URL resource = getClass().getClassLoader().getResource("");

                Path path = Paths.get(baseImagePath+firm_id+extensionName);
                Files.write(path, bytes);
                firm.setName(name);
                firm.setIntro(intro);
                firm.setPicture(firm_id+extensionName);
                firmMapper.updateById(firm);
                return new ReturnProtocol(true, "修改信息成功", firm_id + extensionName);
            }else {
                return  new ReturnProtocol(false,"上传失败,文件名为null");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new HavePostException("上传失败,IO异常");
        }
    }

    @Override
    public ReturnProtocol ensureExit(String id, String mail_id) {
        User user = userMapper.selectById(id);
        String firmId = user.getCorporation();
        redisUtil.setRemove(RedisUtil.KEY_FIRM+firmId+":"+RedisUtil.KEY_FIRMCLERK+user.getJob(),user.getId());
        redisUtil.lRemove(RedisUtil.STAFF+firmId,0,user.getId());
        user.setJob(null);
        user.setCorporation(null);
        user.setJobName(null);
        userMapper.updateById(user);
        Mail mail = mailService.getById(mail_id);
        mail.setIsAnswer(true);
        mailService.updateById(mail);
        return new ReturnProtocol(true,"成功批准" + user.getNickname() + "退出企业");
    }

    @Override
    public ReturnProtocol rejectExit(String userId, String mailId) {
        User user = userMapper.selectById(userId);
        Mail mail = mailService.getById(mailId);
        mail.setIsAnswer(true);
        mailService.updateById(mail);
        return new ReturnProtocol(true,"成功拒绝" + user.getNickname() + "退出企业");
    }

    @Override
    public ReturnProtocol sendExit(){
        User user = userMapper.selectById(UserHolder.getUser().getId());
        if(!(user.getCorporation()!=null && !user.getCorporation().isBlank())){
            return new ReturnProtocol(false,"该用户没有公司！");
        }
        String firmId = user.getCorporation();
        Firm firm = firmMapper.selectById(firmId);
        if(Objects.equals(firm.getManagerId(), user.getId())){
            return new ReturnProtocol(false,"您是公司管理员，不能退出公司！");
        }
        String managerId = firm.getManagerId();
        User manager = userMapper.selectById(managerId);

        //还需要发送给管理员消息
        Mail mail = new Mail();
        mail.setSenderId(user.getId());
        mail.setReceiveId(manager.getId());
        mail.setCreateTime(LocalDateTime.now().toString());
        mail.setIsRead(false);
        mail.setType(1);

        String job_name = employMapper.selectById(user.getJob()).getJobName();
        mail.setContent("id:" +user.getId() + "员工"+ user.getNickname() + "工作于"+ job_name +"岗位，已经提交辞职申请，请您审批！");

        kafkaTopicService.sendMessage("Mail",JSONUtil.toJsonStr(mail));

        return new ReturnProtocol(true,"已发送至管理员审核");
    }




}