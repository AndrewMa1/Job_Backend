package buaa.cxtj.job_backend.Service;

import buaa.cxtj.job_backend.POJO.Entity.Dynamic;
import buaa.cxtj.job_backend.Util.ReturnProtocol;

import java.util.List;

public interface RecommendService {

    public List<Dynamic> recTrends();

    public ReturnProtocol recJob();

    public void recUserAndFirm();

    ReturnProtocol searchFirm(String firm);
}
