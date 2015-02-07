package httptool;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by navia on 2015/2/7.
 */
public class HttpTools {
    private static final Pattern URL_PATTERN = Pattern.compile("^((?:http|https)://[^?]+)\\??(.*)$");
    private static final String PARAM_MATCHER = "^[^?/\\ ]*$";
    private static final Pattern PARAM_PATTERN = Pattern.compile("([^&]+)=([^&]+)");
    private static final String FILE_SPLITTER = "->";

    /**
     * 组装参数,考虑以下情况,用户传递的URL中带有部分参数,并且用户同时传递了
     * PARAM参数,这种情况下就需要将用户URL以及PARAM中的参数进行组合
     * @param param
     * @return
     * @throws HttpException
     */
    private static final String assembleParam(String... param) throws HttpException {
        StringBuffer result = new StringBuffer();
        for(String p : param){
            if(!Pattern.matches(PARAM_MATCHER, p)){
                throw new HttpException("不合法的URL组合方式");
            }
            if(p != null && !"".equals(p)) {
                result.append(p).append("&");
            }
        }

        if(result.length() != 0) {
            result.deleteCharAt(result.length() - 1);
        }

        return result.toString();
    }

    /**
     * 模拟发送GET请求
     * @param url
     * @param param
     * @return
     * @throws HttpException
     * @throws java.io.IOException
     */
    public static final HttpResponse sendGet(String url, String param) throws HttpException, IOException {
        Matcher m = URL_PATTERN.matcher(url);
        String _url, _param;

        // 组装URL路径以及参数
        if(m.find()){
            _url = m.group(1);
            _param = assembleParam(m.group(2), param);
        }else{
            throw new HttpException("不合法的URL组合方式");
        }

        // 打开和URL之间的连接
        URL realUrl = new URL(_url + "?" + _param);
        URLConnection conn = realUrl.openConnection();

        // 设置通用的请求属性
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("Charset", "UTF-8");
        conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");

        // 建立实际的连接
        conn.connect();

        // 定义BufferedReader输入流来读取URL的响应
        StringBuffer _body = new StringBuffer();
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
            _body.append(line).append("\n");
        }

        in.close();

        return new HttpResponse(conn.getHeaderFields(), _body.toString());
    }

    /**
     * 模拟发送GET请求
     * @param url
     * @return
     * @throws HttpException
     * @throws java.io.IOException
     */
    public static final HttpResponse sendGet(String url) throws HttpException, IOException {
        return sendGet(url, "");
    }


    public static final HttpResponse sendPost(String url, String param, String[] filepaths) throws HttpException, IOException {
        Matcher m = URL_PATTERN.matcher(url);
        String _url, _param;

        // 组装URL路径以及参数
        if(m.find()){
            _url = m.group(1);
            _param = assembleParam(m.group(2), param);
        }else{
            throw new HttpException("不合法的URL组合方式");
        }

        // 打开和URL之间的连接
        URL realUrl = new URL(_url);
        URLConnection conn = realUrl.openConnection();

        // 设置通用的请求属性
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("Charset", "UTF-8");
        conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");

        // 发送POST请求必须设置如下两行
        conn.setDoOutput(true);
        conn.setDoInput(true);

        // post方式不能使用缓存
        conn.setUseCaches(false);

        OutputStream out;

        // 如果存在上传文件
        if(filepaths != null){
            StringBuffer sb;
            DataInputStream in;
            byte[] bufferOut = new byte[1024];
            int bytes = 0;

            // 设置边界
            String BOUNDARY = "----------" + System.currentTimeMillis();
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

            out = new DataOutputStream(conn.getOutputStream());

            // 循环迭代普通字段
            m = PARAM_PATTERN.matcher(_param);
            while(m.find()){
                sb = new StringBuffer();
                sb.append("--").append(BOUNDARY).append("\r\n")
                        .append("Content-Disposition: form-data; name=")
                        .append("\"").append(m.group(1)).append("\"")
                        .append("\r\n\r\n").append(m.group(2)).append("\r\n");
                out.write(sb.toString().getBytes("utf-8"));
            }

            //循环迭代文件
            for(String fn : filepaths){
                String[] fx = fn.split(FILE_SPLITTER);
                File f = new File(fx[1]);
                if(f.exists()){
                    sb = new StringBuffer();
                    sb.append("--").append(BOUNDARY).append("\r\n")
                            .append("Content-Disposition: form-data; name=")
                            .append("\"").append(fx[0]).append("\"; filename=")
                            .append("\"").append(f.getName()).append("\"").append("\r\n")
                            .append("Content-Type:application/octet-stream\r\n\r\n");
                    out.write(sb.toString().getBytes("utf-8"));

                    // 文件正文部分
                    in = new DataInputStream(new FileInputStream(f));
                    while ((bytes = in.read(bufferOut)) != -1) {
                        out.write(bufferOut, 0, bytes);
                    }
                    in.close();
                }
            }

            // 定义最后数据分隔线
            byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");
            out.write(foot);

        }else {
            out = new DataOutputStream(conn.getOutputStream());
            out.write(_param.getBytes("UTF-8"));
        }

        out.flush();
        out.close();

        // 定义BufferedReader输入流来读取URL的响应
        StringBuffer _body = new StringBuffer();
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
            _body.append(line).append("\n");
        }
        in.close();

        return new HttpResponse(conn.getHeaderFields(), _body.toString());
    }

}
