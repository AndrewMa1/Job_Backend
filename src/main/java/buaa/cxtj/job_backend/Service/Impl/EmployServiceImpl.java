package buaa.cxtj.job_backend.Service.Impl;

import buaa.cxtj.job_backend.Mapper.EmployMapper;
import buaa.cxtj.job_backend.POJO.Entity.Job;
import buaa.cxtj.job_backend.Service.EmployService;
import buaa.cxtj.job_backend.Util.RedisUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployServiceImpl extends ServiceImpl<EmployMapper, Job> implements EmployService {
    @Autowired
    RedisUtil redisUtil;


    @Override
    public void deliveryPostService(String corporation_id,String user_id,String post_name) {
        redisUtil.sSet(RedisUtil.KEY_FIRM+corporation_id+":"+post_name,user_id);
    }
}
