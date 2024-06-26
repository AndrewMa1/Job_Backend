package buaa.cxtj.job_backend.Service.Impl;

import buaa.cxtj.job_backend.Mapper.EmployMapper;
import buaa.cxtj.job_backend.POJO.DTO.PendingOfferDTO;
import buaa.cxtj.job_backend.POJO.Entity.Job;
import buaa.cxtj.job_backend.POJO.Entity.User;
import buaa.cxtj.job_backend.Service.EmployService;
import buaa.cxtj.job_backend.Service.UserService;
import buaa.cxtj.job_backend.Util.RedisUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public void deliveryPostService(String corporation_id, String user_id, String post_id,String resume) {
        PendingOfferDTO pendingOfferDTO = new PendingOfferDTO(user_id,resume);
        String json = JSONUtil.toJsonStr(pendingOfferDTO);
        redisUtil.sSet(RedisUtil.KEY_FIRM+corporation_id+":"+RedisUtil.KEY_FIRMPENDING+post_id,json);
    }

    @Override
    public List<User> queryEmployee(String corporation_id, String post_name) {
        String front = RedisUtil.KEY_FIRM + corporation_id + ":" +RedisUtil.KEY_FIRMPENDING+ post_name;
        log.info("查询到公司岗位为" + front);
        log.info("存在吗" + redisUtil.hasKey(front));
        log.info("岗位投递数量为" + redisUtil.sGetSetSize(front));
        Set<Object> userList = redisUtil.sGet(front);
        // 创建一个新的字符串类型的集合
        Set<String> stringSet = new HashSet<>();
        // 遍历对象集合，将每个对象转换为字符串类型，并添加到新集合中
        for (Object obj : userList) {
            String str = String.valueOf(obj); // 将对象转换为字符串
            stringSet.add(str); // 添加到新集合中
        }
        log.info("人员为"+stringSet);
        userService.getUsersInSet(stringSet);
        return userService.getUsersInSet(stringSet); // 返回字符串类型的集合
    }


}
