package test;

import httptool.HttpException;
import httptool.HttpResponse;
import httptool.HttpTools;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by navia on 2015/2/8.
 */
public class uploadTest {

    @Test
    public void uploadTest() throws IOException, HttpException {
        HttpResponse res = HttpTools.sendPost("http://localhost:8080/upload", "a=999", new String[]{"file->D:\\temp\\xx.gif"});
        System.out.println(res.getBody());
    }
}
