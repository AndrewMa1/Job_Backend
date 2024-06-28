package buaa.cxtj.job_backend.Service.Impl;


import buaa.cxtj.job_backend.Mapper.DynamicMapper;
import buaa.cxtj.job_backend.Mapper.FirmMapper;
import buaa.cxtj.job_backend.Mapper.UserMapper;
import buaa.cxtj.job_backend.POJO.DTO.DynamicDTO;
import buaa.cxtj.job_backend.POJO.Entity.Dynamic;
import buaa.cxtj.job_backend.POJO.Entity.Firm;
import buaa.cxtj.job_backend.POJO.Entity.Job;
import buaa.cxtj.job_backend.POJO.Entity.User;
import buaa.cxtj.job_backend.POJO.Enum.JobEnum;
import buaa.cxtj.job_backend.POJO.UserHolder;
import buaa.cxtj.job_backend.Service.RecommendService;
import buaa.cxtj.job_backend.Util.RedisUtil;
import buaa.cxtj.job_backend.Util.ReturnProtocol;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RecommendServiceImpl implements RecommendService {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    DynamicMapper dynamicMapper;

    @Autowired
    FirmMapper firmMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    KafkaTopicServiceImpl kafkaTopicService;

    @Autowired
    KafkaConsumerService kafkaConsumerService;

    @Override
    public DynamicDTO recTrends() {
        String userId = UserHolder.getUser().getId();

        //1 从redis中拿到该用户的关注列表
        List<Object> subscribeList = redisUtil.sGet(RedisUtil.FOLLOW + userId).stream().toList();
        List<String> stringList = subscribeList.stream().map(Object::toString).toList();

        //2 从mysql的dynamic表中拿到这些up主或者公司的动态
        ArrayList<Dynamic> result = new ArrayList<>();
        ArrayList<String>poster = new ArrayList<>();
        for(String userid:stringList){
            QueryWrapper<Dynamic> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id",userid);
            List<Dynamic> dynamicList = dynamicMapper.selectList(queryWrapper);
            result.addAll(dynamicMapper.selectList(queryWrapper));
            String userName = userMapper.selectById(userid).getNickname();
            for(int i=0;i<dynamicList.size();++i){
                poster.add(userName);
            }
        }

        //3 从redis取出该用户的点赞动态列表
        Set<String> agreeSet = redisUtil.sGet(RedisUtil.AGREE + userId).stream().map(Object::toString).collect(Collectors.toSet());
        List<String> fullSet = result.stream().map(Dynamic::getId).collect(Collectors.toList());
        ArrayList<Boolean> isAgreeList = new ArrayList<>();
        for(String id: fullSet){
            if(agreeSet.contains(id)){
                isAgreeList.add(true);
            }else {
                isAgreeList.add(false);
            }
        }
        DynamicDTO dynamicDTO = new DynamicDTO(userMapper.selectById(userId).getNickname(),result,isAgreeList,poster);
        return dynamicDTO;
    }

    @Override
    public DynamicDTO recRandomTrends() {
        String userId = UserHolder.getUser().getId();


        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(userQueryWrapper);
        Collections.shuffle(userList);
        userList = userList.subList(0,Math.min(50, userList.size()));
        System.out.println(userList.size());

        //2 从mysql的dynamic表中拿到这些up主或者公司的动态
        ArrayList<Dynamic> result = new ArrayList<>();
        ArrayList<String>poster = new ArrayList<>();
        for(User user :userList){
            QueryWrapper<Dynamic> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", user.getId());
            List<Dynamic> dynamicList = dynamicMapper.selectList(queryWrapper);
            result.addAll(dynamicList);
            for(int i=0;i<dynamicList.size();++i){
                poster.add(user.getNickname());
            }
        }

        //3 从redis取出该用户的点赞动态列表
        Set<String> agreeSet = redisUtil.sGet(RedisUtil.AGREE + userId).stream().map(Object::toString).collect(Collectors.toSet());
        List<String> fullSet = result.stream().map(Dynamic::getId).toList();
        ArrayList<Boolean> isAgreeList = new ArrayList<>();
        for(String id: fullSet){
            if(agreeSet.contains(id)){
                isAgreeList.add(true);
            }else {
                isAgreeList.add(false);
            }
        }
        DynamicDTO dynamicDTO = new DynamicDTO(userMapper.selectById(userId).getNickname(),result.subList(0,Math.min(10, result.size())),
                isAgreeList.subList(0,Math.min(10, isAgreeList.size())),poster.subList(0,Math.min(10, poster.size())));
        return dynamicDTO;
    }

    //推荐与用户意向岗位一致的岗位招聘信息
    //1 企业新增岗位招聘时，如果岗位对应的topic不存在，则新建岗位对应的topic
    //2 将该新增岗位信息推送到对应topic中
    //3 用户加载推荐招聘信息时，根据用户填写的意向岗位，找到对应的topic，消费topic中的消息 ******
    @Override
    public ReturnProtocol recJobForUser() {
        JobEnum interestJob = null;
        List<Job> recJobList = new ArrayList<>();
        interestJob = userMapper.selectById(UserHolder.getUser().getId()).getInterestJob();
        List<String> recJobListJson = kafkaConsumerService.readMessagesFromPartition(interestJob.toString(), 0);
        for (String jsonString : recJobListJson) {
            JSONObject jsonObject = JSONUtil.parseObj(jsonString);
            Job job = jsonObject.toBean(Job.class);
            recJobList.add(job);
        }
        List<Job> result = new ArrayList<>();
        if(!recJobList.isEmpty()) result = recJobList.subList(0, Math.min(9, recJobList.size()));
        return new ReturnProtocol(true,result);
    }

    @Override
    public ReturnProtocol recJobForVisitor() {
        List<Job> recJobList = new ArrayList<>();

        List<JobEnum> jobEnumList = new ArrayList<>(List.of(JobEnum.values()));
        Collections.shuffle(jobEnumList);
        List<JobEnum> jobEnum = jobEnumList.subList(0, 9);

        for(JobEnum job : jobEnum){
            List<String> recJobListJson = kafkaConsumerService.readMessagesFromPartition(job.toString(), 0);
            for (String jsonString : recJobListJson) {
                JSONObject jsonObject = JSONUtil.parseObj(jsonString);
                Job job_i = jsonObject.toBean(Job.class);
                recJobList.add(job_i);
            }
        }

        List<Job> result = new ArrayList<>();
        if(!recJobList.isEmpty()) result = recJobList.subList(0, Math.min(9, recJobList.size()));
        return new ReturnProtocol(true,result);
    }

    @Override
    public void recUserAndFirm() {
        String userId = UserHolder.getUser().getId();


    }


    @Override
    public ReturnProtocol searchFirm(String firm) {
        //根据公司名称，返回公司信息，作模糊搜索
        QueryWrapper<Firm> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name",firm);
        List<Firm> firms = firmMapper.selectList(queryWrapper);
        return new ReturnProtocol(true,firms);
    }
}
