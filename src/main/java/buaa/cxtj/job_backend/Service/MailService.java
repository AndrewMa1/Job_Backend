package buaa.cxtj.job_backend.Service;

import buaa.cxtj.job_backend.POJO.Entity.Mail;
import buaa.cxtj.job_backend.Util.ReturnProtocol;
import com.baomidou.mybatisplus.extension.service.IService;

public interface MailService extends IService<Mail> {
    public ReturnProtocol getAllMails();

    public ReturnProtocol readMail(String mail_id);


}
