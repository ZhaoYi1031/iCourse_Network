package com.ftp.servlet;

import okhttp3.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Mr.ZY on 2017/12/29.
 */
public class Post {

    HostnameVerifier hv = new HostnameVerifier() {
        public boolean verify(String urlHostName, SSLSession session) {
            System.out.println("Warning: URL Host: " + urlHostName + " vs. "
                    + session.getPeerHost());
            return true;
        }
    };

    private static void trustAllHttpsCertificates() throws Exception {
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
        javax.net.ssl.TrustManager tm = new miTM();
        trustAllCerts[0] = tm;
        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
                .getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc
                .getSocketFactory());
    }

    static class miTM implements javax.net.ssl.TrustManager,
            javax.net.ssl.X509TrustManager {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public void checkServerTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }

        public void checkClientTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }
    }

    public static void outTime(){
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//可以方便地修改日期格式

        Long now_ms = System.currentTimeMillis() % 1000000;

        String nowTime = dateFormat.format( now );
        nowTime = nowTime+"."+now_ms;//".000000";
        System.out.println("NOWNOW::::"+nowTime);
    }

    public String Getusername(String code){


        outTime();

        System.out.println("~~~~~~~~~~~~~~");
        try {
            if (code==null || code.equals(""))
                return "";
            trustAllHttpsCertificates();
            HttpsURLConnection.setDefaultHostnameVerifier(hv);

            OkHttpClient client = new OkHttpClient();
            System.out.println("???????");
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            JSONObject post_para = new JSONObject();
            System.out.println("????????????");

            post_para.put("code", code);

            System.out.println("^^^^" + post_para.toString());

            RequestBody body = RequestBody.create(JSON, post_para.toString());
            System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&" + body.toString());
            Request request_tp = new Request.Builder()
                    .url("https://tongpao.qinix.com/auths/get_data")//https://tongpao.qinix.com/auths/get_data")
                    .addHeader("Tongpao-Auth-appid", "c643da987bdc3ec74efbb0ef7927f7ea")
                    .addHeader("Tongpao-Auth-secret", "GNcP_Pa0Z3nFjjsQa8sd8VCUmUEiIZBa6Rue682LDsMyUIx7iwPplQ")
                    .post(body).build();

            System.out.println("*********" + request_tp.toString());

            System.out.println("*********" + request_tp.toString());

            Response response_tp = client.newCall(request_tp).execute();

            byte[] bytes = response_tp.body().bytes();
            String responseString = new String(bytes);

            System.out.println("@@@@@@" + responseString);

            JSONObject jsonObject = (JSONObject)(new JSONParser().parse(responseString));//new JSONObject(responseString);
            JSONObject data = (JSONObject)(jsonObject.get("data"));//new JSONObject(responseString);

            System.out.println("#$$#$#"+data);

            String tongpao_username;

            tongpao_username = (String) data.get("tongpao_username");
            System.out.println("USERNAME:::" + tongpao_username);
            return tongpao_username;
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }


    public void work(String code, String time){
        HashMap mp_college = new HashMap(){
            {
                put("\\u6750\\u6599\\u79d1\\u5b66\\u4e0e\\u5de5\\u7a0b\\u5b66\\u9662", 1);
                put("\\u7535\\u5b50\\u4fe1\\u606f\\u5de5\\u7a0b\\u5b66\\u9662", 2);
                put("\\u81ea\\u52a8\\u5316\\u79d1\\u5b66\\u4e0e\\u7535\\u6c14\\u5de5\\u7a0b\\u5b66\\u9662", 3);
                put("\\u80fd\\u6e90\\u4e0e\\u52a8\\u529b\\u5de5\\u7a0b\\u5b66\\u9662", 4);
                put("\\u822a\\u7a7a\\u79d1\\u5b66\\u4e0e\\u5de5\\u7a0b\\u5b66\\u9662", 5);
                put("\\u8ba1\\u7b97\\u673a\\u5b66\\u9662", 6);
                put("\\u673a\\u68b0\\u5de5\\u7a0b\\u53ca\\u81ea\\u52a8\\u5316\\u5b66\\u9662", 7);
                put("\\u7ecf\\u6d4e\\u7ba1\\u7406\\u5b66\\u9662", 8);
                put("\\u6570\\u5b66\\u4e0e\\u7cfb\\u7edf\\u79d1\\u5b66\\u5b66\\u9662", 9);
                put("\\u751f\\u7269\\u4e0e\\u533b\\u5b66\\u5de5\\u7a0b\\u5b66\\u9662", 10);
                put("\\u4eba\\u6587\\u793e\\u4f1a\\u79d1\\u5b66\\u5b66\\u9662", 11);
                put("\\u5916\\u56fd\\u8bed\\u5b66\\u9662", 12);
                put("\\u4ea4\\u901a\\u79d1\\u5b66\\u4e0e\\u5de5\\u7a0b\\u5b66\\u9662", 13);
                put("\\u53ef\\u9760\\u6027\\u4e0e\\u7cfb\\u7edf\\u5de5\\u7a0b\\u5b66\\u9662", 14);
                put("\\u5b87\\u822a\\u5b66\\u9662", 15);
                put("\\u98de\\u884c\\u5b66\\u9662", 16);
                put("\\u4eea\\u5668\\u79d1\\u5b66\\u4e0e\\u5149\\u7535\\u5de5\\u7a0b\\u5b66\\u9662", 17);
                put("\\u5317\\u4eac\\u5b66\\u9662", 18);
                put("\\u7269\\u7406\\u79d1\\u5b66\\u4e0e\\u6838\\u80fd\\u5de5\\u7a0b\\u5b66\\u9662", 19);
                put("\\u6cd5\\u5b66\\u9662", 20);
                put("\\u8f6f\\u4ef6\\u5b66\\u9662", 21);
                put("\\u73b0\\u4ee3\\u8fdc\\u7a0b\\u6559\\u80b2\\u5b66\\u9662", 22);
                put("\\u9ad8\\u7b49\\u5de5\\u7a0b\\u5b66\\u9662", 23);
                put("\\u4e2d\\u6cd5\\u5de5\\u7a0b\\u5e08\\u5b66\\u9662", 24);
                put("\\u56fd\\u9645\\u5b66\\u9662", 25);
                put("\\u65b0\\u5a92\\u4f53\\u827a\\u672f\\u4e0e\\u8bbe\\u8ba1\\u5b66\\u9662", 26);
                put("\\u5316\\u5b66\\u4e0e\\u73af\\u5883\\u5b66\\u9662", 27);
                put("\\u601d\\u60f3\\u653f\\u6cbb\\u7406\\u8bba\\u5b66\\u9662", 28);
                put("\\u4eba\\u6587\\u4e0e\\u793e\\u4f1a\\u79d1\\u5b66\\u9ad8\\u7b49\\u7814\\u7a76\\u9662", 29);
                put("\\u7a7a\\u95f4\\u4e0e\\u73af\\u5883\\u5b66\\u9662", 30);
                put("\\u58eb\\u5609\\u4e66\\u9662", 75);
                put("\\u51af\\u5982\\u4e66\\u9662", 74);
                put("\\u5317\\u822a\\u5b66\\u9662", 37);
                put("\\u58eb\\u8c14\\u4e66\\u9662", 73);
                put("\\u56fd\\u9645\\u901a\\u7528\\u5de5\\u7a0b\\u5b66\\u9662", 35);
                put("\\u77e5\\u884c\\u4e66\\u9662", 79);
                put("\\u81f4\\u771f\\u4e66\\u9662", 77);
                put("\\u5b88\\u9537\\u4e66\\u9662", 76);
            }
        };

        try {
            // sendPostRequest(code);
            //SslUtils.ignoreSsl();

            outTime();
            trustAllHttpsCertificates();
            HttpsURLConnection.setDefaultHostnameVerifier(hv);

            OkHttpClient client = new OkHttpClient();
            System.out.println("???????");
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            JSONObject post_para = new JSONObject();
            System.out.println("????????????");

            //post_para.put("code", code);
            post_para.put("code", code);
            //post_para.put("redirect", "http://www.baidu.com");
            //post_para.put("need_email", "1");

            System.out.println("^^^^" + post_para.toString());

            //SslUtils.ignoreSsl();

            RequestBody body = RequestBody.create(JSON, post_para.toString());
            System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&" + body.toString());
            Request request_tp = new Request.Builder()
                    .url("https://tongpao.qinix.com/auths/get_data")//https://tongpao.qinix.com/auths/get_data")
                    .addHeader("Tongpao-Auth-appid", "c643da987bdc3ec74efbb0ef7927f7ea")
                    .addHeader("Tongpao-Auth-secret", "GNcP_Pa0Z3nFjjsQa8sd8VCUmUEiIZBa6Rue682LDsMyUIx7iwPplQ")
                    .post(body).build();

            System.out.println("*********" + request_tp.toString());
            //.post(new FormBody.Builder()
            //.add("redirect", "http://www.baidu.com/")
//                           .add("redirect", "http://www.baidu.com/")
//                            .add("need_email", "1")
//                            .add("need_email", "1")
//                            .add("need_personal", "1")
//                            .add("need_school_info", "1")
//                            .add("need_identification", "1")
            //.build()
            //).build();
            System.out.println("*********" + request_tp.toString());



            Response response_tp = client.newCall(request_tp).execute();

            System.out.println("(()))))))))))(#@——*R@()#$*@#)$*#)!*$!)");

            byte[] bytes = response_tp.body().bytes();
            String responseString = new String(bytes);
            System.out.println("@@@@@@" + responseString);

            JSONObject jsonObject = (JSONObject)(new JSONParser().parse(responseString));//new JSONObject(responseString);
            JSONObject data = (JSONObject)(jsonObject.get("data"));//new JSONObject(responseString);

            System.out.println("#$$#$#"+data);

            String tongpao_username, college, email, birthday, gender, real_name, identification, class_name, major, phone_num_str;
            phone_num_str = college = email = birthday = gender = real_name = identification = class_name = major =  "";
            Long grade = 0L;
            int college_id = 0;

            int gender_u = 0;

            int phone_number = 0;



            if (data.get("jdfksl") != null)
                System.out.println("233333");
            else
                System.out.println("666666");


            tongpao_username = (String)data.get("tongpao_username");
            System.out.println("USERNAME:::"+tongpao_username);

            if (data.get("email") != null)
                email = (String)data.get("email");

            if (data.get("real_name") != null)
                real_name = (String)data.get("real_name");

            if (data.get("birthday") != null)
                birthday = (String)data.get("birthday");

            if (data.get("gender") != null)
                gender = (String)data.get("gender");


            System.out.println("@!!!!男");
            System.out.println(")))))):"+UploadServlet.convertStringToUTF8("男"));

//            if (gender.equals("男"))
//                gender_u = 1;
//            else if (gender.equals("女"))
//                gender_u = 2;

            if (UploadServlet.convertStringToUTF8(gender).equals("\\u7537"))
                gender_u = 1;
            else if (UploadServlet.convertStringToUTF8(gender).equals("\\u5973"))
                gender_u = 2;

            System.out.println(gender+"^&@#($!("+gender_u);
            System.out.println("TEST:::"+gender+":::"+UploadServlet.convertStringToUTF8(gender));


            if (data.get("grade") != null)
                grade = (Long)data.get("grade");

            if (data.get("college") != null)
                college = (String)data.get("college");
            if (mp_college.containsKey(UploadServlet.convertStringToUTF8(college)))
                college_id = (int)mp_college.get(UploadServlet.convertStringToUTF8(college));
            else
                college_id = 0;
            System.out.println(college+"@@@@@!#"+college_id);



            if (data.get("major") != null)
                major = (String)data.get("major");

            if (data.get("class_name") != null)
                class_name = (String)data.get("class_name");

            if (data.get("identification") != null)
                identification = (String)data.get("identification");


            AddTPUser atp = new AddTPUser();
            atp.work(tongpao_username, "", email, birthday, gender,
                     grade, "", identification, class_name, "",
                     phone_num_str,
                     time,
                     gender_u,
                    college_id);







//!!!!JSONArray jsonArray = JSONArray.fromObject(responseString);
            //System.out.println("############Token:"+ jsonArray.toString());// + jsonArray.getString("data"));

            //int siz = jsonArray.size();
            //for (int i = 0; i < siz; i++) {
            //JSONObject object = jsonArray.getJSONObject(i);

            //String resourceType = unicodeToString(object.getString("resourceType"));
            //String url = object.getString("url");
            // }

//            profile = json_text["data"]
//            # print(profile)
//            # print(type(profile))
//            student_id = str(profile["student_id"])
//            # print(type(student_id))
//
//            students = User.objects.filter(username = student_id)
//            if (students.count() > 0): #之前已经登录过 #len(students)
//            #        test = requests.post("http://127.0.0.1:8000/sign/login/",data={"error":0, "username":student_id,"password":"111111111111111111111111111111"})
//            #        # print(test.text)
//            # print("ENNNDDD")
//            # print("user:", student_id)
//            return HttpResponse(json.dumps({"error":0, "username":student_id}))
//            #        request.session['username'] = student_id # store in session
//            #        return HttpResponseRedirect("/")
//
//            college_dict = {
//                    '材料科学与工程学院':         1,
//                    '电子信息工程学院':         2,
//                    '自动化科学与电气工程学院':         3,
//                    '能源与动力工程学院':         4,
//                    '航空科学与工程学院':         5,
//                    '计算机学院':         6,
//                    '机械工程及自动化学院':         7,
//                    '经济管理学院':         8,
//                    '数学与系统科学学院':         9,
//                    '生物与医学工程学院':        10,
//                    '人文社会科学学院':        11,
//                    '外国语学院':        12,
//                    '交通科学与工程学院':        13,
//                    '可靠性与系统工程学院':        14,
//                    '宇航学院':        15,
//                    '飞行学院':        16,
//                    '仪器科学与光电工程学院':        17,
//                    '北京学院':        18,
//                    '物理科学与核能工程学院':        19,
//                    '法学院':        20,
//                    '软件学院':        21,
//                    '现代远程教育学院':        22,
//                    '高等工程学院':        23,
//                    '中法工程师学院':        24,
//                    '国际学院':        25,
//                    '新媒体艺术与设计学院':        26,
//                    '化学与环境学院':        27,
//                    '思想政治理论学院':        28,
//                    '人文与社会科学高等研究':        29
//            }
//
//            tongpao_username = profile["tongpao_username"]
//            birthday = gender = grade = real_name = identification = class_name = major = grade = ""
//            phone_number = 0
//
//            if ("phone_number" in profile):
//            phone_number = profile["phone_number"]
//            # print(phone_number)
//            phone_number = int(phone_number[0])
//            # print(phone_number)
//            if ("email" in profile):
//            email = profile["email"]
//            if ("real_name" in profile):
//            real_name = profile["real_name"]
//            if ("birthday" in profile):
//            birthday = profile["birthday"]
//            if ("gender" in profile):
//            gender = profile["gender"]
//            # print("%#!@##",gender)
//            gender_dict = {"男":1, "女":2}
//            gender = gender_dict[gender]
//            # print("@@@@",gender)
//            if ("grade" in profile):
//            grade = profile["grade"]
//            if ("college" in profile):
//            college = profile["college"]
//            if ("major" in profile):
//            major = profile["major"]
//            if ("class_name" in profile):
//            class_name = profile["class_name"]
//            if ("identification" in profile):
//            identification = profile["identification"]
//
//            user = User()
//            user.username = student_id
//
//            # print("!!!!username=",student_id)
//            user.set_password("111111111111111111111111111111")
//            user.is_active = True
//            user.is_superuser = True
//            user.email = email
//            user.first_name = "TongPao"
//
//            user.save()
//
//            user_profile = UserProfile()
//
//            user_profile.user_id = user.id
//            user_profile.gender = gender
//            user_profile.college_id = college_dict[college]
//            user_profile.intro = "同袍用户"
//
//            user_profile.nickname = ""
//            user_profile.info = ""
//
//            user_profile.save()
//
//            tp_u = Tongpao_Userprofile()
//            tp_u.student_id = student_id
//            tp_u.tongpao_username = tongpao_username
//            tp_u.phone_number = phone_number
//            tp_u.email = email
//            tp_u.real_name = real_name
//            tp_u.gender = gender
//            tp_u.birthday = birthday
//            tp_u.grade = grade
//            tp_u.college = college
//            tp_u.major = major
//            tp_u.class_name = class_name
//            tp_u.identification = identification
//
//            tp_u.save()
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
