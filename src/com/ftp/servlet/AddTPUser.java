package com.ftp.servlet;

import net.sf.json.JSONArray;

/**
 * Created by Mr.ZY on 2017/12/30.
 */
public class AddTPUser {




    int id;
    public void work(String tongpao_username, String college, String email,
                     String birthday, String gender, Long grade, String real_name,
                     String identification, String class_name, String major,
                     String phone_number,
                     String last_login,
                     int gender_u,
                     int college_id){




        try {
            Init_mysql e1 = new Init_mysql(1);
            Init_mysql e2 = new Init_mysql(0);

            String cypher;

            JSONArray ans;

            cypher = "select * from auth_user where username='" + tongpao_username + "';";

            ans = e2.executeCypher(cypher, 1);

            if (ans.size() > 0) {

                id = ans.getJSONObject(0).getInt("id");

                System.out.println("!!!HAS registered!!!" + tongpao_username);
                cypher = "UPDATE `icourse`.`auth_user` SET `last_login`='"
                        +last_login+
                        "' WHERE `id`= "
                        + id + ";";

                System.out.println(cypher);
                e1.add(cypher); //更新最后登录时间到auth_user

                return;
            }
            String passwd = "111111111111111111111111111111";
            Hasher hasher = new Hasher();
            System.out.println("Before hash passwd: "+passwd);
            passwd = hasher.trans(passwd);
            System.out.println("After hash passwd: "+passwd);

            cypher = "insert into auth_user (username,password,is_superuser,first_name,last_name,email,is_staff,is_active,date_joined, last_login)" +
                    " values('" + tongpao_username + "','" + passwd + "',0,'Android_Tongpao','','" + email + "',0,1,'" + last_login + "','" + last_login + "');";
            System.out.println(cypher);

            e1.add(cypher); //插入到auth_user

            cypher = "select * from auth_user where username='" + tongpao_username + "';";

            ans = e2.executeCypher(cypher, 1);

            id = ans.getJSONObject(0).getInt("id");

            cypher = "insert into backend_userprofile (user_id, gender, college_id, intro, nickname)" +
                    " values(" + id + "," + gender_u + "," + college_id + ",'Tongpao User', '');";
            System.out.println(cypher);

            e1.add(cypher); //插入到backend_userprofile

            //real_name = college = major = ""; //!!!如何解决中文编码问题,就是使得不是从class出来后就变成乱码了

            cypher = "insert into backend_tongpao_userprofile (student_id, tongpao_username, phone_number, email, real_name, " +
                    "gender, birthday, grade, college, major, class_name, identification)" +
                    " values('" + tongpao_username + "','" + tongpao_username + "'," + 0 +
                    ",'" + email + "','" + real_name + "'," + gender_u + ",'" + birthday + "','" +
                    grade + "','" + college + "','" + major + "','" + class_name + "','" + identification+
                    "');";
            System.out.println(cypher);

            e1.add(cypher); //插入到tongpao_userprofile

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
