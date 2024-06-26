package buaa.cxtj.job_backend.Service;

import buaa.cxtj.job_backend.POJO.Entity.Firm;
import buaa.cxtj.job_backend.Util.ReturnProtocol;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

public interface FirmService extends IService<Firm> {
    ReturnProtocol createFirm( String name, String intro, MultipartFile picture);

    ReturnProtocol showContent(String id);

    ReturnProtocol showDynamic(String id);

    ReturnProtocol showRecruit(String id);

    public void hireClerk(String user_id,String corporation_id,String post_id);
}
