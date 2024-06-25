package buaa.cxtj.job_backend.mty;

import buaa.cxtj.job_backend.Mapper.FirmMapper;
import buaa.cxtj.job_backend.Mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class zpd {
    @Autowired
    private UserMapper firmMapper;
    @Test
    public void ss(){
        ;
        System.out.println(firmMapper.selectById("123"));
    }
}
