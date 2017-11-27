package com.ftp.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MT {
    public static void main(String args[]) {
//        Request request = new Request.Builder()
//                .url("http://39.106.60.94:8080/Hello/HelloWorld")
//                .post(new FormBody.Builder()
//                        .add("homePage", "init")
//                        .build()
//                )
//                .build();
//        OkHttpClient client = new OkHttpClient();
//        try {
//            System.out.println("OK "+client.newCall(request).execute().body().string());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }
    static void downloadFile(final String urlpath, final String remoteFileName,final String localFileName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlpath);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setReadTimeout(5000);
                    con.setConnectTimeout(5000);
                    con.setRequestProperty("filename",remoteFileName);
                    con.setRequestProperty("Charset", "UTF-8");
                    con.setRequestMethod("GET");
                    if (con.getResponseCode() == 200) {
                        InputStream is = con.getInputStream();//获取输入流
                        FileOutputStream fileOutputStream = null;//文件输出流
                        if (is != null) {
                            fileOutputStream = new FileOutputStream(new File(localFileName));//指定文件保存路径，代码看下一步
                            byte[] buf = new byte[1024];
                            int ch;
                            while ((ch = is.read(buf)) != -1) {
                                fileOutputStream.write(buf, 0, ch);//将获取到的流写入文件中
                            }
                        }
                        if (fileOutputStream != null) {
                            fileOutputStream.flush();
                            fileOutputStream.close();
                        }
                        System.out.println("Success");
                    } else {
                        System.out.println("Failed");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
