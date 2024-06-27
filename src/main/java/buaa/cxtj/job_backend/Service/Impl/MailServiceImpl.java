package buaa.cxtj.job_backend.Service.Impl;


import buaa.cxtj.job_backend.Mapper.MailMapper;
import buaa.cxtj.job_backend.Mapper.UserMapper;
import buaa.cxtj.job_backend.POJO.DTO.MailDTO;
import buaa.cxtj.job_backend.POJO.DTO.UserDTO;
import buaa.cxtj.job_backend.POJO.Entity.Mail;
import buaa.cxtj.job_backend.POJO.UserHolder;
import buaa.cxtj.job_backend.Service.MailService;
import buaa.cxtj.job_backend.Util.ReturnProtocol;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MailServiceImpl extends ServiceImpl<MailMapper, Mail> implements MailService {

    @Autowired
    MailMapper mailMapper;

    @Autowired
    UserMapper userMapper;


    @Override
    public ReturnProtocol getAllMails() {
        UserDTO userDTO = UserHolder.getUser();
        String to_id = userDTO.getId();
        QueryWrapper<Mail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("to_id",to_id);
        List<Mail> mailList = mailMapper.selectList(queryWrapper);
        List<MailDTO> result = new ArrayList<>();
        for(Mail mail: mailList){
            MailDTO mailDTO = new MailDTO(mail);
            mailDTO.setSenderName(userMapper.selectById(mailDTO.getSenderId()).getNickname());
            mailDTO.setReceiveName(userMapper.selectById(mailDTO.getReceiveId()).getNickname());
            result.add(mailDTO);
        }
        return new ReturnProtocol(true,result);
    }

    @Override
    public ReturnProtocol readMail(String mail_id) {
        Mail mail = mailMapper.selectById(mail_id);
        mail.setIsRead(true);
        mailMapper.updateById(mail);
        return new ReturnProtocol(true,"已读");
    }
}
