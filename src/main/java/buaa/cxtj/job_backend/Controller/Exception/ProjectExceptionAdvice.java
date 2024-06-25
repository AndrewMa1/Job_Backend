package buaa.cxtj.job_backend.Controller.Exception;

import buaa.cxtj.job_backend.Util.ReturnProtocol;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
public class ProjectExceptionAdvice {

    @ExceptionHandler(Exception.class)
    public ReturnProtocol doException(Exception ex){
        return new ReturnProtocol(false,ex.getMessage());
    }

//    @ExceptionHandler(BusinessException.class)
//    public ReturnProtocol doBusinessException(Exception ex){
//        ex.printStackTrace();
//        return new ReturnProtocol(false,"业务层异常");
//    }
//
//    @ExceptionHandler(SystemException.class)
//    public ReturnProtocol doSystemException(Exception ex){
//        ex.printStackTrace();
//        return new ReturnProtocol(false,"系统层异常");
//    }

}
