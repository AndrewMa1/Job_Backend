package buaa.cxtj.job_backend.Service;

import buaa.cxtj.job_backend.POJO.Entity.Dynamic;
import buaa.cxtj.job_backend.POJO.Entity.Firm;
import buaa.cxtj.job_backend.Util.ReturnProtocol;
import com.baomidou.mybatisplus.extension.service.IService;

public interface DynamicService extends IService<Dynamic> {
    ReturnProtocol postDynamic(String content);

    ReturnProtocol deleteDynamic(String id);

    ReturnProtocol agreeDynamic(String id);
}
