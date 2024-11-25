package org.example.lockproject.test;

import com.alibaba.fastjson2.JSON;
import lombok.Data;
import lombok.ToString;
import okhttp3.*;
import org.example.lockproject.mq.enums.NginxQueueEnums;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


public class TicketNginxAndRabbitmqBookingSystem {

    private static String port = "80";

    private static final AtomicInteger countA = new AtomicInteger(0);
    private static final AtomicInteger countB = new AtomicInteger(0);

    @ToString
    @Data
    class NginxAndMqRep{
        private String userId;

        private String area;

        private String message;

        private String status;

        private String url;
    }



    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(100);

        List<Future<NginxAndMqRep>> futures = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            int finalI = i;
            Callable<NginxAndMqRep> callableTask = () -> {
                try {
                    String userId = UUID.randomUUID().toString();
                    return testNginxAndMq(userId,area(finalI));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            };

            // 提交任務並保存 Future 對象
            futures.add(executor.submit(callableTask));
            //目前單台 nginx 可以撐著 sleep 6 毫秒JAVA後端單台才撐的住 會應為啟動的現成數變化
//            Thread.sleep(6);
        }

        // 獲取任務的結果
        for (Future<NginxAndMqRep> future : futures) {
            try {
                NginxAndMqRep rep = future.get();
                if(rep != null){

                    if(rep.status.equals("success")){
                        NginxQueueEnums parse = NginxQueueEnums.parse(rep.area);
                        switch (parse) {
                            case NginxQA:
                                countA.incrementAndGet();
                                break;
                            case NginxQB:
                                countB.incrementAndGet();
                                break;
                        }
                        System.out.println("-----------------------");
                        System.out.println(rep.userId);
                        System.out.println(rep.area);
                        System.out.println(rep.url);
                        System.out.println("-----------------------");
                    }

                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }


        System.out.println("----------");
        System.out.println("計算搶到的票 countA :"+countA);
        System.out.println("計算搶到的票 countB :"+countB);
        System.out.println("----------");

        Thread.sleep(100000);
        executor.shutdown();

        System.out.println("----------");
        System.out.println("計算搶到的票 countA :"+countA);
        System.out.println("計算搶到的票 countB :"+countB);
        System.out.println("----------");
        System.out.println("結束...");
    }

    private static NginxAndMqRep testNginxAndMq(String userId , String area) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        String jsonBody = String.format("{\"userId\": \"%s\", \"area\": \"%s\"}", userId, area);
        RequestBody body = RequestBody.create(mediaType, jsonBody);
        Request request = new Request.Builder()
                .url("http://localhost:" + port + "/bookNginx-ticket")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();

        String responseBody = response.body() != null ? response.body().string() : "{}"; // 防止空響應
//        System.out.println("服務器響應: " + responseBody);


        NginxAndMqRep rep = JSON.parseObject(responseBody, NginxAndMqRep.class);

        if (rep.status != null && !rep.status.equals("success")) {
            return null;
        }
        // 確保關閉 response 以釋放資源
        response.close();
        return rep;
    }

    private static String area(int i){
        if(i%2 == 0){
            return NginxQueueEnums.NginxQA.area;
        }
        return NginxQueueEnums.NginxQB.area;
    }



}
