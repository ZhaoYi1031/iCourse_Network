package com.ftp.servlet;

//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.*;
//import org.json.simple.JSONException;

public class Init_mysql {
    Connection con;
    Connection conn;
    Statement st;
    String driver = "com.mysql.jdbc.Driver";
    String url = //"jdbc:mysql://127.0.0.1:3306/mysql";
            "jdbc:mysql://10.2.28.124:3306/icourse?useSSL=false";
    //"jdbc:neo4j:http://39.106.60.94:3306/iCourse";
    String user = "root";
    String password = //"";
            "Hotcode@1506";

    public Init_mysql(int choose) throws ClassNotFoundException, SQLException, Exception {
        if(choose == 0) {
            this.build();
        } else {
            this.build_update();
        }

    }

    public void build_update() throws ClassNotFoundException, SQLException, Exception {
        Class.forName(this.driver);
        this.st = this.getConnection().createStatement();
    }

    public void build() throws ClassNotFoundException, SQLException, Exception {
        Class.forName(this.driver);
        this.con = DriverManager.getConnection(this.url, this.user, this.password);
    }

    public void close() throws SQLException {
        this.con.close();
    }

    public void close_update() throws SQLException {
        this.st.close();
        this.getConnection().close();
    }

    public Connection getConnection() throws SQLException {
        this.conn = DriverManager.getConnection(this.url, this.user, this.password);
        return this.conn;
    }

    public void add(String cypher) throws ClassNotFoundException, SQLException {
        this.st.executeUpdate(cypher);
    }

    public JSONArray executeCypher(String cypher, int length) throws ClassNotFoundException, Exception{

        try(Statement statement = this.con.createStatement() ){
            JSONArray array = new JSONArray();

            ResultSet rs = statement.executeQuery(cypher);
            ResultSetMetaData md = rs.getMetaData();
            int columnCount = md.getColumnCount();
            int count = 0;


            while (rs.next()) {
                JSONObject jsonObj = new JSONObject();

                for (int i = 1; i <= columnCount; i++) {
                    String columnName =md.getColumnLabel(i);
                    String value = rs.getString(columnName);
                    jsonObj.put(columnName, value);
                }
                count ++;
                if(count > length) break;
                array.add(jsonObj);
            }
            //不让它自己关闭掉，而是在调用的地方close
            //close();
            return array;
        }
    }
}


