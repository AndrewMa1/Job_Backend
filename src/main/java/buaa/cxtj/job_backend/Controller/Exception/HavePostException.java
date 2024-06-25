package buaa.cxtj.job_backend.Controller.Exception;

public class HavePostException extends RuntimeException{

        public HavePostException() {
            super();
        }

        public HavePostException(String message) {
            super(message);
        }

        public HavePostException(String message, Throwable cause) {
            super(message, cause);
        }

        public HavePostException(Throwable cause) {
            super(cause);
        }

        protected HavePostException(String message, Throwable cause, boolean enableSuppression,
                                           boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }

}
