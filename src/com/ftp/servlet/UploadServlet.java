package com.ftp.servlet;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Servlet implementation class UploadServlet
 */
@WebServlet("/UploadServlet")
public class UploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private String ROOT_PATH = "/var/www/html/";//文件默认存放位置

    /**
     * Default constructor.
     */
    public UploadServlet() {
        // TODO Auto-generated constructor stub
    }

    /**
     * 下载文件
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("GBK");
        String clientFile = request.getHeader("filename");
        if (clientFile != null) {
            File f = new File(ROOT_PATH + new String(clientFile.getBytes("ISO-8859-1")));
            ServletOutputStream out = response.getOutputStream();
            System.out.println(request.getParameter("filename"));

            if (f.exists()) {
                FileInputStream fis = new FileInputStream(f);
                String filename = URLEncoder.encode(f.getName(), "utf-8"); //解决中文文件名下载后乱码的问题
                byte[] b = new byte[fis.available()];
                fis.read(b);
                response.setCharacterEncoding("utf-8");
                response.setHeader("Content-Disposition", "attachment; filename=" + filename + "");
                //获取响应报文输出流对象
                //输出
                out.write(b);
            } else {
                out.println("bucunzai");
            }
            out.flush();
            out.close();
        }
    }

//    public static String chineseToUnicode(String str){
//        char[]arChar=str.toCharArray();
//        int iValue=0;
//        StringBuffer uStr = new StringBuffer();
//        for(int i=0;i<arChar.length;i++){
//            iValue=(int)str.charAt(i);
//            uStr.append("&#"+iValue+";");
//        }
//        return uStr.toString();
//    }

    /**
     * 汉字 转换为对应的 UTF-8编码
     * @param s 木
     * @return E69CA8
     */
    public static String convertStringToUTF8(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
//        ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(s);
//        return byteBuffer.toString();


        String res = "";
        StringBuffer output = new StringBuffer();
        try {
//            byte[] string_bytes = s.getBytes();
//            res = new String(string_bytes, "utf-8");
            //String strInput ="湖北武汉";

            System.out.println("\""+s+ "\" 的utf8编码：");
            for (int i = 0; i < s.length(); i++)
            {
                output.append("\\u" +Integer.toString(s.charAt(i), 16));
            }
            System.out.println(output);

        }catch (Exception e){e.printStackTrace();}
        return output.toString();
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ServletOutputStream out = response.getOutputStream();

        Init_mysql con;
        String cypher, columnName, name, passwd, courseCode, gen, gender, mail, username, register_t, login_t,
                password_fill, url, link, resource_name, intro, fileRoute;
        JSONArray ans0, ans, ans2, ans4;
        int user_id =0, cnt, id, contribution, only_url, download_count, resource_id;
        double evaluaiton;

        register_t = request.getParameter("registration");

        if (register_t != null && register_t.equals("1")){ //Register

            passwd = request.getParameter("studentPasswd");
            gen = request.getParameter("studentGender");
            switch (gen)
            {
                case "male": gender = "1"; break;
                case "female": gender = "2"; break;
                default: gender = "0";
            }
            mail = request.getParameter("studentMail");
            username = request.getParameter("studentNo"); //修改了从number到username//其实就是用户名, 最一开始是限制只能是数字的学号,后来为了一致改成了用户名

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            System.out.println("Login Time:::"+df.format(new Date()));// new Date()为获取当前系统时间

            String now_time = df.format(new Date());

            try {
                Hasher hasher = new Hasher();
                System.out.println("Before hash passwd: "+passwd);
                passwd = hasher.trans(passwd);
                System.out.println("After hash passwd: "+passwd);


                Init_mysql e1 = new Init_mysql(1);
                Init_mysql e2 = new Init_mysql(0);

                cypher = "select * from  auth_user where username='" + username + "';";

                ans = e2.executeCypher(cypher, 1);

                if (ans.size() > 0) {
                    System.out.println("!!!SAME!!!"+username);
                    JSONObject jo2 = new JSONObject();
                    jo2.put("status" , "same");
                    out.println(jo2.toString());
                    out.flush();
                    out.close();
                    return ;
                }


                cypher = "insert into auth_user (username,password,is_superuser,first_name,last_name,email,is_staff,is_active,date_joined)" +
                        " values('" + username + "','" + passwd + "',0,'Android','','" + mail + "',0,1,'" + now_time + "');";
                
                e1.add(cypher); //插入到auth_user

                cypher = "select id from auth_user where username = '" + username + "';";

                //由于auth_user表里面name是unique, 我们需要根据name求出这个用户的id信息.
                ans = e2.executeCypher(cypher, 1);

                user_id = ans.getJSONObject(0).getInt("id");
                cypher = "insert into backend_userprofile (gender,nickname,user_id)" + " values('" +
                        gender + "','"+ username + "','" + user_id + "');";

                e1.add(cypher);

                JSONObject jo = new JSONObject();
                jo.put("status" , "success");
                out.println(jo.toString());
                out.flush();
                out.close();

                e2.close();
                e1.close_update();
            }catch (Exception e){
                JSONObject jo = new JSONObject();
                jo.put("status" , "fail");
                out.println(jo.toString());
                out.flush();
                out.close();
                e.printStackTrace();
            }
            System.out.println("Regster Done!!!");
            return ;
        }

        login_t = request.getParameter("login");
        if (login_t!=null && login_t.equals("1")) { //Register
            try {
                username = request.getParameter("studentNo"); //其实就是用户名, 最一开始是限制只能是数字的学号,后来为了一致改成了用户名
                password_fill = request.getParameter("password_fill");
                Init_mysql e2 = new Init_mysql(0);
                cypher = "select password from auth_user where username = '" +
                        username + "';";
                //由于auth_user表里面name是unique, 我们需要根据name求出这个用户的id信息.
                ans = e2.executeCypher(cypher, 1);

                String res = "NotExist";
                if (ans.size() > 0){
                    passwd = ans.getJSONObject(0).getString("password");
                    if (passwd.length()>5 &&  passwd.substring(0,5).equals("pbkdf")){ //前面的字符代表是加密后的面膜
                        Hasher hasher = new Hasher();
                        if (hasher.checkPassword(password_fill, passwd)) {
                            res = "true";
                        } else {
                            res = "false";
                        }
                    }
                    else {
                        System.out.println("password = " + passwd + "passwortd_t" + password_fill);
                        if (passwd.equals(password_fill)) {
                            res = "true";
                        } else {
                            res = "false";
                        }
                    }
                }

                e2.close();

                JSONObject jo = new JSONObject();
                jo.put("result" , res);
                JSONArray ja = new JSONArray();
                ja.add(jo);//ja = object.put("resourceName","Chinese");
                out.println(ja.toString());
                out.flush();
                out.close();
            }
            catch (Exception e){ e.printStackTrace(); }
            return ;
        }

        System.out.println("Before CourseActivity");
        if (request.getParameter("CourseActivity") != null) {
            courseCode = request.getParameter("CourseActivity");

            System.out.println(")))((((CourseA");
            JSONArray jsonArray = new JSONArray();

            try {

                Init_mysql e2 = new Init_mysql(0);

                Init_mysql e3 = new Init_mysql(0);

                Init_mysql e4 = new Init_mysql(0); //evaluitions

                cypher = "select * from backend_resource where course_code='"
                            + courseCode + "';";

                ans = e2.executeCypher(cypher, 10000); //一个资源最多10000个资源

                System.out.println("$$$$$$ans.size() = " + ans.size());

                byte[] notExist_b = "该资源尚无简介".getBytes();
                String notExist = new String(notExist_b);
                notExist = convertStringToUTF8(notExist);

                for (int i = 0; i < (int)ans.size(); ++i) //
                {
                    resource_id = ans.getJSONObject(i).getInt("id");

                    user_id = ans.getJSONObject(i).getInt("upload_user_id");
                    download_count = ans.getJSONObject(i).getInt("download_count");
                    only_url = ans.getJSONObject(i).getInt("only_url"); //only_url为0代表是用户上传的资源,此时的资源的url为空,存放在服务器的相对路径在link字段里
                    url = ans.getJSONObject(i).getString("url");
                    if (only_url==0)
                        link = ans.getJSONObject(i).getString("link");
                    else
                        link = "";
                    resource_name = ans.getJSONObject(i).getString("name");
                    intro = ans.getJSONObject(i).getString("intro");


                    if (intro == null || intro.equals(""))
                        intro = notExist;

                    System.out.println("url转化中文前"+intro);
                    intro = convertStringToUTF8(intro);
                    System.out.println("url转化中文后"+intro);


                    String[] suffix_resource_name = resource_name.split("\\.");
                    String suffix = "";



                    if (suffix_resource_name.length >= 2) //代表有后缀名
                        suffix = suffix_resource_name[suffix_resource_name.length-1]; //找到最后一个,也就是后缀名

                    System.out.println("suffix: "+suffix);

                    //System.out.println("resource_String转化中文前"+intro);
                    resource_name = convertStringToUTF8(resource_name);
                    //System.out.println("url转化中文后"+intro);

                    System.out.println("user_id: "+user_id+"download_count :"+download_count+ "url: "+url);

                    if (only_url == 0) //如果是用户上传的资源,需要我们手动修改url的位置 link= uploads/2017/11/20171124121620_58.ppt
                    {
                        int upos = link.indexOf('/'); //找到第一个出现的/位置
                        String sonRoute = link.substring(upos+1, link.length());//   2017/11/20171124121620_58.ppt
                        System.out.println(sonRoute);
                        url = "http://10.2.28.124:8080/dir/" + sonRoute;//http://10.2.28.124:8080/dir/2017/11/
                        System.out.println("新的url"+url);
                    }


                    cypher = "select username from auth_user where id = " +
                            user_id + ";";

                    System.out.println("****cypher = "+cypher);

                    ans2 = e3.executeCypher(cypher, 1);
                    username = ans2.getJSONObject(0).getString("username");

                    username = convertStringToUTF8(username);

                    System.out.println("Username upload is: "+username);

                    String cypher_eva = "select * from backend_resource_evaluation where resource_id = " +resource_id +";";

                    System.out.println("Evalution: "+cypher_eva);

                    ans4 = e4.executeCypher(cypher_eva, 100000);

                    evaluaiton = -1.0;
                    if (ans4.size() != 0){
                        int siz_eva = ans4.size();
                        int tot_eva = 0;
                        for (int i_eva = 0; i_eva < siz_eva; ++i_eva){
                            tot_eva += ans4.getJSONObject(i_eva).getInt("grade");
                        }
                        evaluaiton = (double)tot_eva / (double)siz_eva;
                    }

                    System.out.println("&&&EVA==="+evaluaiton+" ans4.size()"+ ans4.size());


                    JSONObject jo = new JSONObject();
                    jo.put("username" , username);
                    jo.put("downloadCount", download_count);
                    jo.put("resourceType", suffix);

                    jo.put("intro", intro);
                    jo.put("resourceName", resource_name);
                    jo.put("url", url);

                    jo.put("evaluation", evaluaiton);

                    System.out.println("!!!jsonarray = "+jo.toString());

                    jsonArray.add(jo);
                }

                e3.close();
                e2.close();

            } catch (Exception e){ e.printStackTrace(); }


//            for (int i=0;i<10;i++) {
//                JSONObject object = new JSONObject();
//                object.put("resourceName","Chinese");
//                object.put("resourceType",types[new Random().nextInt(types.length)]);
//                jsonArray.add(object);
//            }

            out.println(jsonArray.toString());
            //out.println(jsonArray.toJSONString());
            out.flush();
            out.close();
            return ;
        }

        System.out.println("Before homepage");
        if (request.getParameter("homePage") != null) {
            System.out.println(")))((((HOmepage");
            JSONArray jsonArray = new JSONArray();
            String [] types = {"doc","pdf","ppt","xls"};

            try {
                Init_mysql e2 = new Init_mysql(0);

                Init_mysql e3 = new Init_mysql(0);

                Init_mysql e4 = new Init_mysql(0);

                cypher = "select id,download_count,upload_user_id,name,only_url,url,link,intro from backend_resource order by download_count desc limit 20;";
                //找到下载量最高的20个资源

                ans = e2.executeCypher(cypher, 20); // 20 most download

                System.out.println("$$$$$$ans.size() = " + ans.size());

//                byte[] notExist_b = "该资源尚无简介".getBytes();
//                String notExist = new String(notExist_b, "utf-8");
//                notExist = convertStringToUTF8(notExist);

                String notExist = "\\u8be5\\u6587\\u4ef6\\u5c1a\\u65e0\\u7b80\\u4ecb";
                //实在不知道上面那个哪边编码有问题了,索性直接自己占据主动权,先转换成utf-8?unicode?格式

                for (int i = 0; i < (int)ans.size(); ++i) //ans.size()应该就是20,即返回20个资源
                {
                    resource_id = ans.getJSONObject(i).getInt("id");

                    user_id = ans.getJSONObject(i).getInt("upload_user_id");
                    download_count = ans.getJSONObject(i).getInt("download_count");
                    url = ans.getJSONObject(i).getString("url");
                    link = ans.getJSONObject(i).getString("link");
                    only_url = ans.getJSONObject(i).getInt("only_url"); //only_url为0代表是用户上传的资源,此时的资源的url为空,存放在服务器的相对路径在link字段里
                    resource_name = ans.getJSONObject(i).getString("name");
                    intro = ans.getJSONObject(i).getString("intro");


                    if (intro == null || intro.equals("")) {
                        System.out.println("!!!!!!!!!!!!!!!!!"+intro);
                        intro = notExist;
                    }else{
                        System.out.println("#####"+intro);
                        intro = convertStringToUTF8(intro);
                        System.out.println("&&&&&"+intro);
                    }

                    String[] suffix_resource_name = resource_name.split("\\.");
                    String suffix = "";

                    if (suffix_resource_name.length >= 2) //代表有后缀名
                        suffix = suffix_resource_name[suffix_resource_name.length-1]; //找到最后一个,也就是后缀名

//                    System.out.println("suffix: "+suffix);

                    //System.out.println("resource_String转化中文前"+intro);
                    resource_name = convertStringToUTF8(resource_name);
                    //System.out.println("url转化中文后"+intro);

//                    System.out.println("user_id: "+user_id+"download_count :"+download_count+ "url: "+url);

                    if (only_url == 0) //如果是用户上传的资源,需要我们手动修改url的位置 link= uploads/2017/11/20171124121620_58.ppt
                    {
                        int upos = link.indexOf('/'); //找到第一个出现的/位置
                        String sonRoute = link.substring(upos+1, link.length());//   2017/11/20171124121620_58.ppt
                        System.out.println(sonRoute);
                        url = "http://10.2.28.124:8080/dir/" + sonRoute;//http://10.2.28.124:8080/dir/2017/11/
                        System.out.println("新的url"+url);
                    }


                    cypher = "select username from auth_user where id = " +
                            user_id + ";";

//                    System.out.println("****cypher = "+cypher);

                    ans2 = e3.executeCypher(cypher, 1);
                    username = ans2.getJSONObject(0).getString("username");

                    username = convertStringToUTF8(username);

//                    System.out.println("Username upload is: "+username);

                    String cypher_eva = "select * from backend_resource_evaluation where resource_id = " +resource_id +";";

                    System.out.println("Evalution: "+cypher_eva);

                    ans4 = e4.executeCypher(cypher_eva, 100000);

                    evaluaiton = -1.0;
                    if (ans4.size() != 0){
                        int siz_eva = ans4.size();
                        int tot_eva = 0;
                        for (int i_eva = 0; i_eva < siz_eva; ++i_eva){
                            tot_eva += ans4.getJSONObject(i_eva).getInt("grade");
                        }
                        evaluaiton = (double)tot_eva / (double)siz_eva;
                    }

                    System.out.println("&&&EVA==="+evaluaiton+" ans4.size()"+ ans4.size());

                    JSONObject jo = new JSONObject();
                    jo.put("username" , username);
                    jo.put("downloadCount", download_count);
                    jo.put("resourceType", suffix);

                    jo.put("intro", intro);
                    jo.put("resourceName", resource_name);
                    jo.put("url", url);

                    jo.put("evaluation", evaluaiton);

                    System.out.println("!!!jsonarray = "+jo.toString());

                    jsonArray.add(jo);
                }

                e3.close();
                e2.close();

            } catch (Exception e){ e.printStackTrace(); }


//            for (int i=0;i<10;i++) {
//                JSONObject object = new JSONObject();
//                object.put("resourceName","Chinese");
//                object.put("resourceType",types[new Random().nextInt(types.length)]);
//                jsonArray.add(object);
//            }

            out.println(jsonArray.toString());
            //out.println(jsonArray.toJSONString());
            out.flush();
            out.close();
            return ;
        } else {
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
                            response.sendError(500, "No file choosen or empty file");
                        }
                    }
                }
            }
        }
    }

}
