package org.example.lockproject.test;

import com.alibaba.fastjson2.JSON;
import okhttp3.*;
import org.example.lockproject.common.BaseResp;
import org.example.lockproject.common.StatusCode;
import org.example.lockproject.controller.rep.BookTicket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

public class TicketRabbitmqBookingSystem {

    private static final String port8080 = "8080";
    private static final String port8081 = "8081";


    public static void main(String[] args) throws Exception {

        int a = 0;
        int b = 0;

        ExecutorService ex = Executors.newFixedThreadPool(70);
        List<Future<BookTicket>> queueAs = new ArrayList<>();
        List<Future<BookTicket>> queueBs = new ArrayList<>();

        for (int i = 0 ; i < 11000 ; i++){
            String userId = UUID.randomUUID().toString();

            if (i %2 == 0){
                chooseQueue(userId,"bookTicketA",queueAs,ex);
            }else {
                chooseQueue(userId,"bookTicketB",queueAs,ex);
            }
        }

        for (Future<BookTicket> future : queueBs) {
            try {
                BookTicket bookTicket = future.get();
                if (bookTicket != null){
                    if (bookTicket.isGet()){
                        b++;
                    }
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }

        }


        for (Future<BookTicket> future : queueAs) {
            try {
                BookTicket bookTicket = future.get();
                if (bookTicket != null){
                    if (bookTicket.isGet()){
                        a++;
                    }
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }

        }

        System.out.println("A:"+a);
        System.out.println("B:"+b);

        ex.shutdown();

    }

    private static void chooseQueue(String userId , String queueName ,List<Future<BookTicket>> queue , ExecutorService ex) throws Exception {
        Callable<BookTicket> callable = (()->{
            BookTicket bookTicket = setQueue(userId,queueName);
            if (bookTicket != null){
                System.out.println(bookTicket.getArea()+":"+bookTicket.getTicketToken());
                return bookTicket;
            }
            return null;
        });
        // 預檢查 callable 的結果
        try {
            BookTicket result = callable.call();
            if (result != null) {
                queue.add(ex.submit(() -> result)); // 只在有非空結果時才提交
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    private static BookTicket setQueue(String userId, String queueName) throws IOException {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("text/plain");

            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url("http://localhost:"+randomPont()+"/ticket/rabbitmq/"+queueName+"?userId="+userId)
                    .method("GET", null)
                    .build();
            Response response = client.newCall(request).execute();
            String responseBody = response.body() != null ? response.body().string() : "{}"; // 防止空響應
//        System.out.println(responseBody);
            BaseResp rep = JSON.parseObject(responseBody,BaseResp.class);
//        System.out.println(rep);

            if (rep.getStatusCode() != StatusCode.Success.code){
                return null;
            };
            String jsonString = JSON.toJSONString(rep.getData());
            BookTicket data = JSON.parseObject(jsonString ,BookTicket.class);
            if (data.isGet()){
                return data;
            }
            return null;
        }catch (Exception e){
            System.out.println(e.getMessage());

        }
        return null;
    }


    private static String randomPont(){
        int r = 0;
        r = (int)(Math.random()*10)+1;
        if (r %2 == 0){
            return port8080;
        }else {
            return port8081;
        }
    }
}
