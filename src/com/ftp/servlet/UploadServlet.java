package com.ftp.servlet;

//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;


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
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
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
     * 发送Post请求
     */
    public void sendPostRequest(String code) {

        //Build parameter string
        String data = "code="+code;
        try {
            // Send the request
            URL url = new URL("https://tongpao.qinix.com/auths/get_data");
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

            //write parameters
            writer.write(data);
            writer.flush();

            // Get the response
            StringBuffer answer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                answer.append(line);
            }
            writer.close();
            reader.close();

            //Output the response
            System.out.println(answer.toString());
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }




    /**
     * 下载文件
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tongpao_login, code, time, get_url;

        get_url = request.getRequestURL().toString();
        System.out.println("********"+get_url);

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

        tongpao_login = request.getParameter("code");//getHeader("tongpao");
        System.out.println("********"+tongpao_login);


        if (tongpao_login != null){

            code = request.getParameter("code");
            time = request.getParameter("status");
            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$"+code+"   "+time);

            Post tp = new Post();
            tp.work(code, time);

        }
    }

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
                case "Male": gender = "1"; break;
                case "Female": gender = "2"; break;
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

        String login_tp = request.getParameter("Time");
        if (login_tp!=null) { //Login_tongpao
            System.out.println("YYYYEAAAAHHHHH");
            try {
                System.out.println("BEFORE HIIIIII");
                Post.outTime();
                //Thread.sleep(1000);
                System.out.println("AFTER HIIIIII");
                Post.outTime();

                cypher = "select * from auth_user where last_login = '" +
                        login_tp + "';";
                //由于auth_user表里面name是unique, 我们需要根据name求出这个用户的id信息.
                Init_mysql e2 = new Init_mysql(0);
                ans = e2.executeCypher(cypher, 1);
                e2.close();

                System.out.println(cypher);

                String res = "fail";
                if (ans.size() == 0){
                    JSONObject jo = new JSONObject();
                    jo.put("result" , res);
                    JSONArray ja = new JSONArray();
                    ja.add(jo);//ja = object.put("resourceName","Chinese");

                    System.out.println("*************"+ja.toString());

                    out.println(ja.toString());
                    out.flush();
                    out.close();

                    return ;
                }


//                String code = request.getParameter("code");
//                String res = "true";
//
//                Post p = new Post();
//                username = p.Getusername(code);
//                if (username == null || username.equals("")){
//                    res = "fail";
//                    JSONObject jo2 = new JSONObject();
//                    jo2.put("result" , res);
//                    JSONArray ja2 = new JSONArray();
//                    ja2.add(jo2);//ja = object.put("resourceName","Chinese");
//
//                    System.out.println("%#^&@@@@@@@"+ja2.toString());
//
//                    out.println(ja2.toString());
//                    out.flush();
//                    out.close();
//                    return ;
//                }

                JSONObject jo = new JSONObject();
                res = "true";
                jo.put("result" , res);
                username = ans.getJSONObject(0).getString("username");
                jo.put("username", username);//ans.getJSONObject(0).getString("username"));
                JSONArray ja = new JSONArray();
                ja.add(jo);//ja = object.put("resourceName","Chinese");

                System.out.println("*************"+ja.toString());

                out.println(ja.toString());
                out.flush();
                out.close();
            }
            catch (Exception e){ e.printStackTrace(); }
            return ;
        }


        login_t = request.getParameter("login");
        if (login_t!=null && login_t.equals("1")) { //Login
            try {
                username = request.getParameter("studentNo"); //其实就是用户名, 最一开始是限制只能是数字的学号,后来为了一致改成了用户名
                password_fill = request.getParameter("password_fill");
                Init_mysql e2 = new Init_mysql(0);
                cypher = "select password from auth_user where username = '" +
                        username + "';";
                //由于auth_user表里面name是unique, 我们需要根据name求出这个用户的id信息.
                ans = e2.executeCypher(cypher, 1);

                String res = "User Not Exist";
                if (ans.size() > 0){
                    passwd = ans.getJSONObject(0).getString("password");
                    System.out.println("$$$$$$$$$$"+passwd);
                    if (passwd.length()>5 &&  passwd.substring(0,5).equals("pbkdf")){ //前面的字符代表是加密后的面膜
                        Hasher hasher = new Hasher();
                        if (hasher.checkPassword(password_fill, passwd)) {
                            res = "true";
                        } else {
                            res = "Wrong password";
                        }
                    }
                    else {
                        System.out.println("!!!PASSWD<5"+passwd);
                        res = "Wrong password";
                    }
                }

                e2.close();

                JSONObject jo = new JSONObject();
                jo.put("result" , res);
                JSONArray ja = new JSONArray();
                ja.add(jo);//ja = object.put("resourceName","Chinese");

                System.out.println("*************"+ja.toString());

                out.println(ja.toString());
                out.flush();
                out.close();
            }
            catch (Exception e){ e.printStackTrace(); }
            return ;
        }


        if (request.getParameter("getUserInfo")!=null && request.getParameter("getUserInfo").equals("1")) { //getUserInfo
            System.out.println("*********GETUSER");
            try {
                username = request.getParameter("username"); //用户名

                Init_mysql e2 = new Init_mysql(0);
                cypher = "select * from auth_user where username = '" +
                        username + "';";
                //由于auth_user表里面name是unique, 我们需要根据name求出这个用户的id信息.
                ans = e2.executeCypher(cypher, 1);

                String res = "User Not Exist";

                if (ans.size()==0){
                    JSONObject jo = new JSONObject();
                    jo.put("result" , res);
                    JSONArray ja = new JSONArray();
                    ja.add(jo);//ja = object.put("resourceName","Chinese");
                    out.println(ja.toString());
                    out.flush();
                    out.close();
                    return ;
                }

                String user_email = "", user_date_joined = "", user_intro = "", user_nickname = "", user_last_login = "", user_last_name = "", user_first_name = "";
                int user_gender, user_college_id = 0;

                if (ans.getJSONObject(0).has("email"))
                    user_email = ans.getJSONObject(0).getString("email");
                if (ans.getJSONObject(0).has("date_joined"))
                    user_date_joined = ans.getJSONObject(0).getString("date_joined");
//                user_last_login = ans.getJSONObject(0).getString("last_login");
                id = ans.getJSONObject(0).getInt("id");


                cypher = "select * from backend_userprofile where user_id = "
                        + id + ";";

                //由于auth_user表里面name是unique, 我们需要根据name求出这个用户的id信息.
                ans = e2.executeCypher(cypher, 1);
                if (ans.size()==0){
                    JSONObject jo = new JSONObject();
                    jo.put("result" , res);
                    JSONArray ja = new JSONArray();
                    ja.add(jo);//ja = object.put("resourceName","Chinese");
                    out.println(ja.toString());
                    out.flush();
                    out.close();
                    return ;
                }
                if (ans.getJSONObject(0).has("intro")) {
                    user_intro = ans.getJSONObject(0).getString("intro");
                    System.out.println("***intro转化中文前"+user_intro);
                    user_intro = convertStringToUTF8(user_intro);
                    System.out.println("***intro转化中文后"+user_intro);
                }
                if (ans.getJSONObject(0).has("nickname")) {
                    user_nickname = ans.getJSONObject(0).getString("nickname");
                    user_nickname = convertStringToUTF8(user_nickname);
                }
                user_gender =  ans.getJSONObject(0).getInt("gender");
                if (ans.getJSONObject(0).has("college_id"))
                    user_college_id = ans.getJSONObject(0).getInt("college_id") ;
                if (ans.getJSONObject(0).has("first_name")) {
                    user_first_name = ans.getJSONObject(0).getString("first_name");
                    user_first_name = convertStringToUTF8(user_first_name);
                }
                if (ans.getJSONObject(0).has("last_name")) {
                    user_last_name = ans.getJSONObject(0).getString("last_name");
                    user_last_name = convertStringToUTF8(user_last_name);
                }

                e2.close();

                JSONObject jo = new JSONObject();
                jo.put("result", "success");
                jo.put("user_email", user_email);
                jo.put("user_date_joined" , user_date_joined);
                jo.put("user_intro", user_intro);
                jo.put("user_nickname", user_nickname);
                jo.put("user_gender", user_gender);
                jo.put("user_colege_id", user_college_id);
//                jo.put("user_last_login", user_last_login);
                jo.put("user_last_name", user_last_name);
                jo.put("user_first_name", user_first_name);

                JSONArray ja = new JSONArray();
                ja.add(jo);//ja = object.put("resourceName","Chinese");
                System.out.println("@@@@@@@"+jo.toString());
                out.println(ja.toString());
                out.flush();
                out.close();
            }
            catch (Exception e){ e.printStackTrace(); }
            return ;
        }


        if (request.getParameter("CourseActivity") != null) { //courseCode->resource
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

                    intro = convertStringToUTF8(intro);


                    String[] suffix_resource_name = resource_name.split("\\.");
                    String suffix = "";

                    if (suffix_resource_name.length >= 2) //代表有后缀名
                        suffix = suffix_resource_name[suffix_resource_name.length-1]; //找到最后一个,也就是后缀名


                    //System.out.println("resource_String转化中文前"+intro);
                    resource_name = convertStringToUTF8(resource_name);
                    //System.out.println("url转化中文后"+intro);


                    if (only_url == 0) //如果是用户上传的资源,需要我们手动修改url的位置 link= uploads/2017/11/20171124121620_58.ppt
                    {
                        int upos = link.indexOf('/'); //找到第一个出现的/位置
                        String sonRoute = link.substring(upos+1, link.length());//   2017/11/20171124121620_58.ppt
                        url = "http://10.2.28.124:8080/dir/" + sonRoute;//http://10.2.28.124:8080/dir/2017/11/
                    }

                    cypher = "select username from auth_user where id = " +
                            user_id + ";";

                    ans2 = e3.executeCypher(cypher, 1);
                    username = ans2.getJSONObject(0).getString("username");
                    username = convertStringToUTF8(username);

                    String cypher_eva = "select * from backend_resource_evaluation where resource_id = " +resource_id +";";

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


                    JSONObject jo = new JSONObject();
                    jo.put("username" , username);
                    jo.put("downloadCount", download_count);
                    jo.put("resourceType", suffix);
                    jo.put("intro", intro);
                    jo.put("resourceName", resource_name);
                    jo.put("url", url);
                    jo.put("evaluation", evaluaiton);

                    jsonArray.add(jo);
                }

                e3.close();
                e2.close();

            } catch (Exception e){ e.printStackTrace(); }

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
                        intro = notExist;
                    }else{
                        intro = convertStringToUTF8(intro);
                    }

                    String[] suffix_resource_name = resource_name.split("\\.");
                    String suffix = "";

                    if (suffix_resource_name.length >= 2) //代表有后缀名
                        suffix = suffix_resource_name[suffix_resource_name.length-1]; //找到最后一个,也就是后缀名

                    resource_name = convertStringToUTF8(resource_name);

                    if (only_url == 0) //如果是用户上传的资源,需要我们手动修改url的位置 link= uploads/2017/11/20171124121620_58.ppt
                    {
                        int upos = link.indexOf('/'); //找到第一个出现的/位置
                        String sonRoute = link.substring(upos+1, link.length());//   2017/11/20171124121620_58.ppt
                        url = "http://10.2.28.124:8080/dir/" + sonRoute;//http://10.2.28.124:8080/dir/2017/11/
                    }

                    cypher = "select username from auth_user where id = " +
                            user_id + ";";
                    ans2 = e3.executeCypher(cypher, 1);
                    username = ans2.getJSONObject(0).getString("username");
                    username = convertStringToUTF8(username);
                    String cypher_eva = "select * from backend_resource_evaluation where resource_id = " +resource_id +";";

                    System.out.println("@@"+cypher_eva);

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

                    JSONObject jo = new JSONObject();
                    jo.put("username" , username);
                    jo.put("downloadCount", download_count);
                    jo.put("resourceType", suffix);
                    jo.put("intro", intro);
                    jo.put("resourceName", resource_name);
                    jo.put("url", url);
                    jo.put("evaluation", evaluaiton);

                    jsonArray.add(jo);
                }

                e3.close();
                e2.close();

            } catch (Exception e){ e.printStackTrace(); }

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
