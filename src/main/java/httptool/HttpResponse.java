package httptool;

import java.util.List;
import java.util.Map;

/**
 * Created by navia on 2015/2/7.
 */
public class HttpResponse {
    private Map<String, List<String>> headers;
    private String body;

    public HttpResponse(Map<String, List<String>> headers, String body) {
        this.headers = headers;
        this.body = body;
    }

    public HttpResponse() {

    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
