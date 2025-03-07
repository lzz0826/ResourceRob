package org.example.lockproject;


import org.example.lockproject.utils.AESUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.example.lockproject.service.TicketBookingRedisService.*;

@SpringBootTest
public class UtilTests {


    @Test
    public void AESTest() throws Exception {
        String key = "your-testKey-key";
        String data = "testAES";
        String encrypt = AESUtil.encrypt(data,key);

        System.out.println("AES加密:");
        System.out.println(encrypt);


        String decrypt = AESUtil.decrypt(encrypt,key);
        System.out.println("AES解密:");
        System.out.println(decrypt);

    }

//    key:5f3d062866f5f2afe38deadf0dfe3b99f588d4eaca46081d89e6c42803599c4c_user001_nginxQA

    @Test
    public void ticketTokenKeyTest(){
        String key = "5f3d062866f5f2afe38deadf0dfe3b99f588d4eaca46081d89e6c42803599c4c_user001_nginxQA";
        String token = GetTicketTokenKeyToken(key);
        String userId = GetTicketTokenKeyUserId(key);
        String area = GetTicketTokenKeyArea(key);
        System.out.println(token);
        System.out.println(userId);
        System.out.println(area);
    }


}
