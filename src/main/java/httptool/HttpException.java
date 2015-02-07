package httptool;

/**
 * Created by navia on 2015/2/7.
 */
public class HttpException extends Throwable {
    public HttpException(String message) {
        super(message);
    }

    public HttpException() {
        super();
    }
}
