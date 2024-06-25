package buaa.cxtj.job_backend.Service;

import buaa.cxtj.job_backend.Util.ReturnProtocol;

public interface RecommendService {

    public void recTrends();

    public ReturnProtocol recJob();

    public void recUserAndFirm();

}
