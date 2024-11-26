# ResourceRob 高併發搶票系統

![image](https://github.com/lzz0826/ResourceRob/blob/main/imgs/004.png)

## 核心概念包括：
### 核心問題 : 排除非必要請求到後段 , 解決同時大量請求 *每個服務之間調用要注意 原子性
- Redis：最基本的方式 (資源放Redis) 。 <br />
- RabbitMq：解決 "同時" 大量請求。 <br />
- Nginx：區分 "搶到" "未搶到" 只讓搶到的人進到後端 (資源放Nginx)。 <br />
- JAVA後端：處理業務邏輯 接收前端消息 儲存 查群。 <br />
- MySQL：持久化。 <br />
<br />
<br />

# 優化過程 & 方案選擇:

## N台後端服務 + RabbitMQ：
![image](https://github.com/lzz0826/ResourceRob/blob/main/imgs/002.jpg)
1. 在開始爭搶資源時,初始化資源(透過JAVA後端API在 MQ隊列中存放 )
2. 用戶請求到任意後端,收到請求的後端到 MQ 對列中看是否還存在該資源
3. 有資源"消費"並持久化,沒資源返回用戶失敗

- 解決: 同時大量進到 JAVA 後端 使用MQ 排隊
- 未解決: "搶到","沒搶到" 同時進到 JAVA 後端

初始化資源API: <br />
http://localhost:8080/ticket/rabbitmq/setQa <br />
http://localhost:8080/ticket/rabbitmq/setQb

消費資源API: <br />
http://localhost:8080/ticket/rabbitmq/bookTicketA <br />
http://localhost:8080/ticket/rabbitmq/bookTicketB

測試: <br />
ResourceRob/src/main/java/org/example/lockproject/test/TicketRabbitmqBookingSystem.java

## 後端服務 + Nginx：
![image](https://github.com/lzz0826/ResourceRob/blob/main/imgs/001.png)
1. 在開始爭搶資源時,初始化資源(Nginx共享資源中存放 *注意原子性)
2. 用戶請求到 Nginx , 由Nginx判斷是否還有資源,有:扣除資源到後端,無:返回用戶失敗
3. 有資源進到後端持久化

解決: "搶到","沒搶到":只讓搶到的進到後端
未解決: 同時大量 "搶到" 進到 JAVA 會爆

初始化票API: <br />
http://localhost/init-ticket

搶票API: <br />
http://localhost/book-ticket

核對 Count API: <br />
http://localhost:8080/ticket/nginx/getCountTicket

測試: <br />
ResourceRob/src/main/java/org/example/lockproject/test/TicketNginxBookingSystem.java

## N台後端服務 + Nginx + RabbitMQ：
![image](https://github.com/lzz0826/ResourceRob/blob/main/imgs/004.png)
1. 在開始爭搶資源時,初始化資源(Nginx共享資源中存放 *注意原子性)
2. 用戶請求到 Nginx , 由Nginx判斷是否還有資源,有:扣除資源到 RabbitMq ,無:返回用戶失敗
3. RabbitMq 生產消息 , JAVA後端監聽消息
4. JAVA 業務邏輯 + 持久化
- 後端JAVA監聽MQ隊列 : 無論後端 java 起幾台 端口號多少都可以 (無狀態)

#### *RabbitMQ STOMP:
在 Nginx 隊 RabbitMQ 發送 STOMP 請求時 , 每個請求都是 無狀態 且 請求獨立,導致在使用STOMP時每次都建立新連線 <br />
解決方式: 緩衝處理區,使用共享變量 + 起一個線程跑定時任務 , 累積一定量在使用STOMP連接,定時檢查緩衝區 <br />
- 接收請求階段: 接收一個請求時，將write_point原子性自增1，將返回的, 已經更新過的write_point值作為存放當前request data的key, 存放到Share Dict中.
- Timer運行階段: Timer啟動后, 讀取write_point和read_point, 如果發現read_point < write_point, 開啟flush階段, 不斷自增read_point, 將新的read_point值作為key, 從Share Dict取出data, 發送到rabbitmq中, 直到read_point = write_point, 此次flush工作結束.


初始化票API: <br />
http://localhost/initNginx-ticket?area=nginxQA&count=500 <br />
http://localhost/initNginx-ticket?area=nginxQB&count=500

消費資源API Post: <br />
curl -X POST http://172.24.10.37/bookNginx-ticket \
-H "Content-Type: application/json" \
-d '{"userId": "user001", "area": "nginxQA"}'

接收請求階段: 接收一個請求時，將write_point原子性自增1，將返回的, 已經更新過的write_point值作為存放當前request data的key, 存放到Share Dict中.

Timer運行階段: Timer啟動后, 讀取write_point和read_point, 如果發現read_point<write_point, 開啟flush階段, 不斷自增read_point, 將新的read_point值作為key, 從Share Dict取出data, 發送到rabbitmq中, 直到read_point=write_point, 此次flush工作結束.

