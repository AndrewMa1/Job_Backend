package buaa.cxtj.job_backend.Service.Impl;


import buaa.cxtj.job_backend.POJO.Entity.Job;
import buaa.cxtj.job_backend.POJO.Enum.JobEnum;
import buaa.cxtj.job_backend.POJO.UserHolder;
import buaa.cxtj.job_backend.Service.RecommendService;
import buaa.cxtj.job_backend.Util.RedisUtil;
import buaa.cxtj.job_backend.Util.ReturnProtocol;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendServiceImpl implements RecommendService {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    KafkaTopicServiceImpl kafkaTopicService;

    @Autowired
    KafkaConsumerService kafkaConsumerService;

    @Override
    public void recTrends() {
        String userId = UserHolder.getUser().getId();
        //1 从redis中拿到该用户的关注列表
//        List<Object> subscribeList = redisUtil.lGet(RedisUtil.FOLLOW_LIST + userId, 0, redisUtil.lGetListSize(RedisUtil.FOLLOW_LIST + userId));
//        List<String> stringList = subscribeList.stream().map(Object::toString).toList();
        //2 从mysql的dynamic表中拿到这些up主或者公司的动态



    }

    //推荐与用户意向岗位一致的岗位招聘信息
    //1 企业新增岗位招聘时，如果岗位对应的topic不存在，则新建岗位对应的topic
    //2 将该新增岗位信息推送到对应topic中
    //3 用户加载推荐招聘信息时，根据用户填写的意向岗位，找到对应的topic，消费topic中的消息 ******
    @Override
    public ReturnProtocol recJob() {
        String userId = UserHolder.getUser().getId();
        JobEnum interestJob = UserHolder.getUser().getInterestJob();
        List<String> recJobListJson = kafkaConsumerService.readMessagesFromPartition(interestJob.name(), 0);
        List<Job> recJobList = JSONUtil.toList(
                JSONUtil.parseArray(recJobListJson)
                , Job.class);
        return new ReturnProtocol(true,recJobList);
    }


    @Override
    public void recUserAndFirm() {
        String userId = UserHolder.getUser().getId();


    }
}
