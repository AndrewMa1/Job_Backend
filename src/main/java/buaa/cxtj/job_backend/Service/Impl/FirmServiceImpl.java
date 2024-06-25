package buaa.cxtj.job_backend.Service.Impl;

import buaa.cxtj.job_backend.Mapper.EmployMapper;
import buaa.cxtj.job_backend.Mapper.FirmMapper;
import buaa.cxtj.job_backend.Mapper.UserMapper;
import buaa.cxtj.job_backend.POJO.DTO.FirmDTO;
import buaa.cxtj.job_backend.POJO.DTO.JobDTO;
import buaa.cxtj.job_backend.POJO.Entity.Firm;
import buaa.cxtj.job_backend.POJO.Entity.Job;
import buaa.cxtj.job_backend.POJO.Entity.User;
import buaa.cxtj.job_backend.POJO.UserHolder;
import buaa.cxtj.job_backend.Service.FirmService;

import buaa.cxtj.job_backend.Service.UserService;
import buaa.cxtj.job_backend.Util.ReturnProtocol;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FirmServiceImpl extends ServiceImpl<FirmMapper, Firm> implements FirmService {
    @Autowired
    private FirmMapper firmMapper;
    @Autowired
    private EmployMapper employMapper;

    @Override
    public ReturnProtocol createFirm( String name, String intro, String picture) {
        Firm firm = new Firm(name,intro,picture, UserHolder.getUser().getId());
        int insert = firmMapper.insert(firm);
        System.out.println(insert);
        FirmDTO firmDTO = new FirmDTO(firm.getId(), name,intro,picture,UserHolder.getUser().getId());
        return new ReturnProtocol(true,"创建成功",firmDTO);
    }

    @Override
    public ReturnProtocol showContent(String id) {
        Firm firm = firmMapper.selectById(id);
        FirmDTO firmDTO = new FirmDTO(id,firm.getName(),firm.getIntro(),firm.getPicture(),firm.getManagerId(),null);
        return new ReturnProtocol(true,"",firmDTO);
    }

    @Override
    public ReturnProtocol showDynamic(String id) {

        return null;
    }

    @Override
    public ReturnProtocol showRecruit(String id) {
        // 查询符合条件的招聘岗位信息
        QueryWrapper<Job> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("firm_id", id); // 根据公司id查询
        List<Job> jobList = employMapper.selectList(queryWrapper);

        // 将查询结果转换为JobDTO对象列表
        List<JobDTO> jobDTOList = jobList.stream().map(job -> new JobDTO(job.getJobName(), job.getJobRequirements(), job.getJobCounts())).toList();
        return new ReturnProtocol(true,"", jobDTOList);
    }


}