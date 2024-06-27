package buaa.cxtj.job_backend.Service;

import buaa.cxtj.job_backend.POJO.DTO.ExhibitPendingDTO;
import buaa.cxtj.job_backend.POJO.DTO.FirmDTO;
import buaa.cxtj.job_backend.POJO.DTO.JobDTO;
import buaa.cxtj.job_backend.POJO.DTO.PendingOfferDTO;
import buaa.cxtj.job_backend.POJO.Entity.Job;
import buaa.cxtj.job_backend.POJO.Entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface EmployService extends IService<Job> {
    public void deliveryPostService(String corporation_id,String user_id,String post_id,String resume);
    public List<ExhibitPendingDTO> queryEmployee(String corporation_id, String post_id);

    public List<JobDTO> queryJob(String job_id);

    public FirmDTO queryFrim(String id);
}
