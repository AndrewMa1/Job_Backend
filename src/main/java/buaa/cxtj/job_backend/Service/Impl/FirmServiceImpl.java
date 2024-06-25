package buaa.cxtj.job_backend.Service.Impl;

import buaa.cxtj.job_backend.Mapper.FrimMapper;
import buaa.cxtj.job_backend.Mapper.UserMapper;
import buaa.cxtj.job_backend.POJO.DTO.FirmDTO;
import buaa.cxtj.job_backend.POJO.Entity.Firm;
import buaa.cxtj.job_backend.POJO.Entity.User;
import buaa.cxtj.job_backend.Service.FirmService;
import buaa.cxtj.job_backend.Service.UserService;
import buaa.cxtj.job_backend.Util.ReturnProtocol;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FirmServiceImpl extends ServiceImpl<FrimMapper, Firm> implements FirmService {
    @Autowired
    private FrimMapper frimMapper;

    @Override
    public ReturnProtocol createFirm(String id, String name, String intro, String picture) {
        Firm firm = new Firm(name,intro,picture,id);
        frimMapper.insert(firm);
        FirmDTO firmDTO = new FirmDTO(firm.getId(), name,intro,picture,id);
        return new ReturnProtocol(true,"创建成功",firmDTO);
    }
}
