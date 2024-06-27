package buaa.cxtj.job_backend.Service.Impl;

import buaa.cxtj.job_backend.Mapper.EmployMapper;
import buaa.cxtj.job_backend.POJO.DTO.ExhibitPendingDTO;
import buaa.cxtj.job_backend.POJO.DTO.JobDTO;
import buaa.cxtj.job_backend.POJO.DTO.PendingOfferDTO;
import buaa.cxtj.job_backend.POJO.Entity.Job;
import buaa.cxtj.job_backend.POJO.Entity.User;
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
import org.springframework.stereotype.Service;

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
    EmployMapper employMapper;

    @Override
    public void deliveryPostService(String corporation_id, String user_id, String post_id,String resume) {
        PendingOfferDTO pendingOfferDTO = new PendingOfferDTO(user_id,resume,1);
        String json = JSONUtil.toJsonStr(pendingOfferDTO);
        redisUtil.sSet(RedisUtil.KEY_FIRM+corporation_id+":"+RedisUtil.KEY_FIRMPENDING+post_id,json);
    }

    @Override
    public List<ExhibitPendingDTO> queryEmployee(String corporation_id, String post_id) {
        String front = RedisUtil.KEY_FIRM + corporation_id + ":" +RedisUtil.KEY_FIRMPENDING+ post_id;
        log.info("查询到公司岗位为" + front);
        log.info("存在吗" + redisUtil.hasKey(front));
        log.info("岗位投递数量为" + redisUtil.sGetSetSize(front));
        Set<Object> userList = redisUtil.sGet(front);
        Map<String, String> userMap = new HashMap<>();
        // 创建一个新的字符串类型的集合
        Set<String> stringSet = new HashSet<>();
        //保存user_id的列表
        Set<String>  users = new HashSet<>();
        // 遍历对象集合，将每个对象转换为字符串类型，并添加到新集合中
        for (Object obj : userList) {
            String str = String.valueOf(obj); // 将对象转换为字符串
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
    public List<JobDTO> queryJob(String job_id) {
        // 使用 MyBatis-Plus 的 QueryWrapper 进行条件查询
        QueryWrapper<Job> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("job_id", job_id);  // 在原始的 queryWrapper 上设置查询条件
        List<Job> jobs = employMapper.selectList(queryWrapper);// 使用 queryWrapper 进行查询
        List<JobDTO> jobDTOS = new ArrayList<>();
        log.info("查询到的结果为 "+ jobs);
        for(Job job:jobs){
            JobDTO jobDTO = new JobDTO();
            BeanUtils.copyProperties(job,jobDTO);
            jobDTOS.add(jobDTO);
        }
        return jobDTOS;
    }



}
