package buaa.cxtj.job_backend.Mapper;

import buaa.cxtj.job_backend.POJO.Entity.Job;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployMapper extends BaseMapper<Job> {
}
