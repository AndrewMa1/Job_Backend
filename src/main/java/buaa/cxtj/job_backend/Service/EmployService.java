package buaa.cxtj.job_backend.Service;

import buaa.cxtj.job_backend.POJO.Entity.Job;
import com.baomidou.mybatisplus.extension.service.IService;

public interface EmployService extends IService<Job> {
    public void deliveryPostService(String corporation_id,String user_id,String post_name);
}
