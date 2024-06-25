package buaa.cxtj.job_backend.Service.Impl;

import buaa.cxtj.job_backend.Mapper.DynamicMapper;
import buaa.cxtj.job_backend.Mapper.FirmMapper;
import buaa.cxtj.job_backend.POJO.Entity.Dynamic;
import buaa.cxtj.job_backend.POJO.Entity.Firm;
import buaa.cxtj.job_backend.POJO.UserHolder;
import buaa.cxtj.job_backend.Service.DynamicService;
import buaa.cxtj.job_backend.Service.FirmService;
import buaa.cxtj.job_backend.Util.ReturnProtocol;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DynamicServiceImpl extends ServiceImpl<DynamicMapper, Dynamic> implements DynamicService{

    @Autowired
    private final DynamicMapper dynamicMapper;
    @Override
    public ReturnProtocol postDynamic( String content) {
        String userId = UserHolder.getUser().getId();
        Dynamic dynamic = new Dynamic(userId,content);
        dynamicMapper.insert(dynamic);
        return new ReturnProtocol(true,"",dynamic);
    }

    @Override
    public ReturnProtocol deleteDynamic(String id) {
        dynamicMapper.deleteById(id);
        return new ReturnProtocol(true,"删除成功",null);
    }

    @Override
    public ReturnProtocol agreeDynamic(String id) {
        Dynamic dynamic = dynamicMapper.selectById(id);
        dynamic.setAgree(dynamic.getAgree() + 1);
        return new ReturnProtocol(true,"",dynamic);
    }
}
