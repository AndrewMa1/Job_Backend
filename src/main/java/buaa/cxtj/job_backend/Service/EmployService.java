package buaa.cxtj.job_backend.Service;

import buaa.cxtj.job_backend.POJO.Entity.Job;
import buaa.cxtj.job_backend.POJO.Entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface EmployService extends IService<Job> {
    public void deliveryPostService(String corporation_id,String user_id,String post_id,String resume);
    public List<User> queryEmployee(String corporation_id, String post_name);
}
