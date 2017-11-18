package com.ftp.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.json.simple.JSONObject;

/**
 * Servlet implementation class UploadServlet
 */
@WebServlet("/UploadServlet")
public class UploadServlet extends HttpServlet {
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ROOT_PATH = "/var/www/html/";

	/**
     * Default constructor.
     */
    public UploadServlet() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置响应内容类型
//        response.setContentType("text/html");
        // 实际的逻辑是在这里
//        PrintWriter out = response.getWriter();
//        out.println("<h1>" + "Hello world" + "</h1>");

//        request.setCharacterEncoding("UTF-8");
//        response.setHeader("content-type","text/html;charset=UTF-8");
//        PrintWriter out = response.getWriter();
//        JSONObject jsonObject = new JSONObject();
//        File root = new File(ROOT_PATH);
//        for (File file : root.listFiles()) {
//            jsonObject.put(file.getName(), file.isDirectory());
//        }
//        String resJSON = jsonObject.toJSONString();
//        out.print(resJSON);
//        out.close();
        request.setCharacterEncoding("GBK");
        String clientFile = request.getHeader("filename");
        File f = new File(ROOT_PATH+new String(clientFile.getBytes("ISO-8859-1")));
        ServletOutputStream out =response.getOutputStream();
        System.out.println(request.getParameter("filename"));

        if(f.exists()){
            FileInputStream fis = new FileInputStream(f);
            String filename= URLEncoder.encode(f.getName(),"utf-8"); //解决中文文件名下载后乱码的问题
            byte[] b = new byte[fis.available()];
            fis.read(b);
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-Disposition","attachment; filename="+filename+"");
            //获取响应报文输出流对象
            //输出
            out.write(b);
        } else {
//            response.sendError(204,"File doesn't exist");
            out.println("bucunzai");
        }
        out.flush();
        out.close();
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        RequestContext req = new ServletRequestContext(request);
        if (FileUpload.isMultipartContent(req)) {
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload fileUpload = new ServletFileUpload(factory);
            fileUpload.setFileSizeMax(1024 * 1024 * 1024);
            fileUpload.setHeaderEncoding("UTF-8");


            List items = new ArrayList();
            try {
                items = fileUpload.parseRequest(request);
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (Object item : items) {
                FileItem fileItem = (FileItem) item;
                if (fileItem.isFormField()) {
                    System.out.println(fileItem.getFieldName() + " " + fileItem.getName() + " " + new String(fileItem.getString().getBytes("ISO-8859-1"), "GBK"));
                } else {
                    System.out.println(fileItem.getFieldName() + " " + fileItem.getName() + " " + fileItem.isInMemory() + " " + fileItem.getContentType() + " " + fileItem.getSize());
                    if (fileItem.getName() != null && fileItem.getSize() != 0) {
                        File fullFile = new File(fileItem.getName());
                        File newFile = new File(ROOT_PATH + fullFile.getName());
                        try {
                            fileItem.write(newFile);
                        } catch (Exception E) {
                            E.printStackTrace();
                        }
                    } else {
                        System.out.println("No file choosen or empty file");
                        response.sendError(500,"No file choosen or empty file");
                    }
                }
            }
        }
    }

}
