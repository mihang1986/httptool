package servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;

@WebServlet(name = "FileUploadServlet", urlPatterns = {"/upload"})
@MultipartConfig(location = "D:\\", fileSizeThreshold = 0, maxFileSize = 99999999, maxRequestSize = 99999999)
public class UploadServlet extends HttpServlet {
	private static final String SAVE_PATH = "D:\\";
	 
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
 
//    	BufferedReader br =request.getReader();
//    	String s;
//    	while((s = br.readLine()) != null){
//    		System.out.println(s);
//    	}
//    	
    	


        request.setCharacterEncoding("utf-8");
 
        Part part = request.getPart("file"); //  request.getParts();
        File f = new File(SAVE_PATH + File.separator);
        if (!f.exists()) {
            f.mkdirs();
        }
 
        String h = part.getHeader("content-disposition");
        String filename = h.substring(h.lastIndexOf("=") + 2, h.length() - 1);
 
        part.write(SAVE_PATH + File.separator + filename);
        
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

}
