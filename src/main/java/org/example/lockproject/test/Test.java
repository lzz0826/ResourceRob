package org.example.lockproject.test;

import okhttp3.*;

import java.io.IOException;

public class Test {


    public static void main(String[] args) throws IOException {
        new Thread(()->{
            String name = Thread.currentThread().getName();
            try {
                test(name);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        },"t1").start();

        new Thread(()->{
            String name = Thread.currentThread().getName();
            try {
                test(name);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        },"t2").start();


    }

    private static void test(String threadName) throws IOException {
        for (int i = 0 ; i <2000; i++){
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = RequestBody.create(mediaType, "");

            String port = randomPort();
            Request request = new Request.Builder()
                    .url("http://localhost:"+port+"/lock/ticket/bookTicket")
                    .method("GET", null)
                    .build();
            Response response = client.newCall(request).execute();
            // 獲取 response body 的內容
            String responseBody = response.body().string();
            System.out.println(threadName + " port:"+port + " " + responseBody);
            // 確保關閉 response 以釋放資源
            response.close();
        }
    }


    private static String randomPort() {
        int r = (int)(Math.random() * 2);
        return r == 0 ? "8080" : "8081";
    }



}
