package buaa.cxtj.job_backend.Mapper;

import buaa.cxtj.job_backend.POJO.Entity.Dynamic;
import buaa.cxtj.job_backend.POJO.Entity.Firm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DynamicMapper extends BaseMapper<Dynamic> {
}
