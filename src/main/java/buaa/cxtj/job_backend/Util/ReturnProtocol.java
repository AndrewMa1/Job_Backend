package buaa.cxtj.job_backend.Util;

import lombok.Data;

@Data
public class ReturnProtocol {
    private boolean flag;
    private Object data;
    private String msg;

    public ReturnProtocol(){}
    public ReturnProtocol(boolean flag){
        this.flag = flag;
    }
    public ReturnProtocol(boolean flag, Object data){
        this.flag = flag;
        this.data = data;
    }

    public ReturnProtocol(boolean flag, String msg){
        this.flag = flag;
        this.msg = msg;
    }

    public ReturnProtocol(boolean flag,String msg ,Object data){
        this.flag = flag;
        this.data = data;
        this.msg = msg;
    }
}