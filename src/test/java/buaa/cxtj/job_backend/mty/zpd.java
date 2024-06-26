package buaa.cxtj.job_backend.mty;

import buaa.cxtj.job_backend.Mapper.FirmMapper;
import buaa.cxtj.job_backend.Mapper.UserMapper;
import buaa.cxtj.job_backend.Util.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SpringBootTest
public class zpd {
    @Autowired
    private FirmMapper firmMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Test
    public void ss(){
        ;
        System.out.println(firmMapper.selectById("123"));
    }


}
