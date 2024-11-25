package org.example.lockproject.test;

import com.alibaba.fastjson2.JSON;
import lombok.Data;
import lombok.ToString;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class TicketNginxBookingSystem {



    @ToString
    @Data
    class NginxRep{
        private String status;

        //nginx 計算的票數
        private Integer tickets_count;

        //java總共被 打的次數 用來核對nginx有正確的讓搶到票的近來 必須跟nginx初始化的票處一樣
        private Integer java_rep_count;
        private String message;
    }




    private static String port = "80";



    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(100);

        List<Future<NginxRep>> futures = new ArrayList<>();
        for (int i = 0; i < 2500; i++) {
            int taskId = i; // 為了在 lambda 表達式中使用
            Callable<NginxRep> callableTask = () -> {
                try {
                    return testNginx(Thread.currentThread().getName() + " - Task " + taskId);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            };

            // 提交任務並保存 Future 對象
            futures.add(executor.submit(callableTask));
            //目前單台 nginx 可以撐著 sleep 6 毫秒JAVA後端單台才撐的住 會應為啟動的現成數變化
            Thread.sleep(6);
        }

        // 獲取任務的結果
        for (Future<NginxRep> future : futures) {
            try {
                NginxRep rep = future.get();
                if(rep != null){
                    System.out.println("status"+rep.status);
                    System.out.println("JAVA統計:"+rep.java_rep_count);
                    System.out.println("Nginx統計:"+rep.tickets_count);
                    System.out.println(rep.message);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();

        Thread.sleep(100000);
        System.out.println("結束...");
    }
    private static NginxRep testNginx(String threadName) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");

        Request request = new Request.Builder()
                .url("http://localhost:"+port+"/book-ticket")
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        // 獲取 response body 的內容
        String responseBody = response.body() != null ? response.body().string() : "{}"; // 防止空響應
        System.out.println("打打打....");
//        System.out.println(threadName + " port:"+port + " " + responseBody);


        NginxRep rep = JSON.parseObject(responseBody, NginxRep.class);
//        {"status":"success","tickets_count":9,"java_rep_count":68,"message":"搶票成功"}

//        System.out.println(rep.status);
        if (rep.status != null && !rep.status.equals("success")) {
            return null;
        }
        // 確保關閉 response 以釋放資源
        response.close();
        return rep;
    }

}
