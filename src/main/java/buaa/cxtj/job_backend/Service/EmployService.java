package buaa.cxtj.job_backend.Service;

import buaa.cxtj.job_backend.POJO.DTO.*;
import buaa.cxtj.job_backend.POJO.Entity.Job;
import buaa.cxtj.job_backend.POJO.Entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface EmployService extends IService<Job> {
    public void deliveryPostService(DeliveryPostDTO deliveryPostDTO);
    public List<DeliveryPostDTO> queryEmployee(String corporation_id, String post_id);

    public JobDTO queryJob(String job_id);

    public FirmDTO queryFrim(String id);

    void reject(String corporation_id, String user_id, String post_id);
}
