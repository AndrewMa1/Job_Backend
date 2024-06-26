package buaa.cxtj.job_backend.Service;

import buaa.cxtj.job_backend.POJO.DTO.DynamicDTO;
import buaa.cxtj.job_backend.POJO.Entity.Dynamic;
import buaa.cxtj.job_backend.Util.ReturnProtocol;

import java.util.List;

public interface RecommendService {

    public DynamicDTO recTrends();

    public ReturnProtocol recJob();

    public void recUserAndFirm();

    ReturnProtocol searchFirm(String firm);
}
