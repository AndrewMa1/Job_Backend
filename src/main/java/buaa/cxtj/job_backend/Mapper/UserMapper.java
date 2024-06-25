package buaa.cxtj.job_backend.Mapper;


import buaa.cxtj.job_backend.POJO.Entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

@Mapper
public interface UserMapper extends BaseMapper<User> {


}
