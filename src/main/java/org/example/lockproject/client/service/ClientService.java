package org.example.lockproject.client.service;


import com.alibaba.fastjson2.JSON;
import com.google.gson.reflect.TypeToken;
import lombok.extern.log4j.Log4j2;
import org.example.lockproject.client.obj.req.AddQuantityReq;
import org.example.lockproject.utils.OkHttpUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Log4j2
public class ClientService {

//    已初始化好的票再添加API
    public AddQuantityReq addQuantityReq(String area , int addQuantity) {
        String url = " http://localhost/addTicketNginx-ticket?area=" + area + "&addQuantity="+addQuantity;
        Map<String, String> header = new HashMap<>();
        AddQuantityReq results = OkHttpUtil.get(url,header ,new TypeToken<AddQuantityReq>() {
        }.getType());
        log.info(JSON.toJSONString(results));
        return results ;
    }

}
