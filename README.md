# ResourceRob 高併發搶票系統

![image](https://github.com/lzz0826/ResourceRob/blob/main/imgs/Ticket.jpg)

## 核心概念包括：
### 核心問題 : 排除非必要請求到後段 , 解決同時大量請求 *每個服務之間調用要注意 原子性
- Redis：Tokne過期通知 (資源放Redis) 。 <br />
- RabbitMq：解決 "同時" 大量請求。 <br />
- Nginx：區分 "搶到" "未搶到" 只讓搶到的人進到後端 (資源放Nginx)。 <br />
- JAVA後端：處理業務邏輯 接收前端消息 儲存 查群。 <br />
- MySQL：持久化。 <br />
<br />
開票 -> 搶到 -> Redis 存(過期相當於付款時間) -> 到期 -> 檢查(DB 狀態) -> 有付 -> 不變  
                                                               -> 沒付 -> 改DB狀態 -> 放回Nginx 
<br />

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

##### Nginx 服務API
初始化票API: <br />
http://localhost/initNginx-ticket?area=nginxQA&count=500 <br />
http://localhost/initNginx-ticket?area=nginxQB&count=500  <br />

補票&添加票API GET: (以初始化好的票再添加)API: <br />
http://localhost/addTicketNginx-ticket?area=nginxQA&addQuantity=100 <br />

消費資源API Post: <br />
curl -X POST http://172.24.10.37/bookNginx-ticket \
-H "Content-Type: application/json" \
-d '{"userId": "user001", "area": "nginxQA"}'

搶票API CURL: <br />
curl -X POST http://172.24.10.199/bookNginx-ticket -H "Content-Type: application/json" -d '{"userId": "user001", "area": "nginxQA", "ticketName": "standard"}'

<br />
接收請求階段: 接收一個請求時，將write_point原子性自增1，將返回的, 已經更新過的write_point值作為存放當前request data的key, 存放到Share Dict中.

Timer運行階段: Timer啟動后, 讀取write_point和read_point, 如果發現read_point<write_point, 開啟flush階段, 不斷自增read_point, 將新的read_point值作為key, 從Share Dict取出data, 發送到rabbitmq中, 直到read_point=write_point, 此次flush工作結束.

##### JAVA 服務API

查詢票 CURL: <br />
curl --location 'http://localhost:8080/ticket/nginx/checkoutTicket/user001'

付款 CURL:  <br />
curl --location 'http://localhost:8080/ticket/nginx/checkoutTicketToken' \
--header 'Content-Type: application/json' \
--data '{
    "ticketName" : "standard",
    "userId" : "user001",
    "area" : "nginxQA",
    "ticketToken" : "ac33acf6b04ef21af1ccfdecf23882926d1afca7d6879e34abfdd2cf944f753d",
    "bookTime" : "1742893981"
}'

檢查TicketToken是否過期 CURL:  <br />
curl --location 'http://localhost:8080/ticket/nginx/ticketToPayed' \
--header 'Content-Type: application/json' \
--data '{
    "ticketName" : "standard",
    "userId" : "user001",
    "area" : "nginxQA",
    "ticketToken" : "1dbd5fb4e98286f155cbce9a6742ea7013d9495f71eac2858a86fca2c3d0eb05",
    "bookTime" : "1743486316"
}'

## 加密規則
1. 簡介
本文件定義了使用 HMAC-SHA256 進行數據簽名的加密規則，確保數據完整性與安全性。  <br />

2. 數據格式
數據格式 中間以_分隔  <br />
data =  ticketName + userId + area + book_time  <br />

- ticket_name：票券名稱
- userId：使用者 ID
- area：座位區域
- book_time：訂票時間，格式為 1742800901

示例：standard_user001_nginxQA_1742800901 <br />

3. 加密方法
使用 HMAC-SHA256 生成簽名，步驟如下：

準備密鑰 secret_key（應妥善保管，不對外公開）。

使用 HMAC-SHA256 對 <data> 進行簽名。

簽名結果轉為十六進制字符串。

4.簽名驗證
