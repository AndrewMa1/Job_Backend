package buaa.cxtj.job_backend.Service;

import buaa.cxtj.job_backend.POJO.Entity.Firm;
import buaa.cxtj.job_backend.Util.ReturnProtocol;
import com.baomidou.mybatisplus.extension.service.IService;

public interface FirmService extends IService<Firm> {
    ReturnProtocol createFirm(String id, String name, String intro, String picture);
}
