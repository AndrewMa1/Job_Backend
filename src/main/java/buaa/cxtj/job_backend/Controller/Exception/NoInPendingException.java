package buaa.cxtj.job_backend.Controller.Exception;

/**
 * 被录用的人未在待录取名单中
 */
public class NoInPendingException extends RuntimeException{
    public NoInPendingException() {
        super();
    }

    public NoInPendingException(String message) {
        super(message);
    }

    public NoInPendingException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoInPendingException(Throwable cause) {
        super(cause);
    }

    protected NoInPendingException(String message, Throwable cause, boolean enableSuppression,
                                boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
