local queue = ngx.shared.request_queue
local rabbitmq = require("rabbitmqstomp") -- 確保你已安裝該模塊

-- 定時任務的函數
local function flush_timer()
    -- 讀取環境變量 宿主機 IP 地址
    local host_ip = os.getenv("HOST_IP")

    local read_point = queue:get("read_point") or 0
    local write_point = queue:get("write_point") or 0

    if read_point < write_point then
        -- 創建 RabbitMQ 連接
        local conn, err = rabbitmq:new({
            username = "twg",
            password = "123456",
        })
        if not conn then
            ngx.log(ngx.ERR, "Failed to create RabbitMQ client: ", err)
            return
        end

        local ok, err = conn:connect(host_ip, 61613)  -- 傳遞主機和端口作為參數

        if not ok then
            ngx.log(ngx.ERR, "Failed to connect to RabbitMQ: ", err)
            return
        end

        -- 構建發送消息的標頭（headers），指定消息的目的地和內容類型
        local headers = {
            destination = "/queue/nginxQ",
            -- ["content-type"] = "text/plain" -- 設置消息的內容類型為純文本
            ["content-type"] = "application/json" --設置消息的內容類型為JSON
        }

        -- Flush 數據到 RabbitMQ
        for i = read_point + 1, write_point do
            local data = queue:get(i)
            if data then
                -- 發送到 RabbitMQ
                local ok, err = conn:send(data, headers)
                if not ok then
                    ngx.log(ngx.ERR, "Failed to send message to RabbitMQ: ", err)
                    break
                end

                -- 更新 read_point
                queue:incr("read_point", 1)
                queue:delete(i) -- 清理已處理的數據
            end
        end
    end

    -- 註冊下一次定時任務
    local ok, err = ngx.timer.at(2, flush_timer)
    if not ok then
        ngx.log(ngx.ERR, "Failed to schedule timer: ", err)
    end
end

-- 初始化定時器 (毫秒 , 任務)
local ok, err = ngx.timer.at(0, flush_timer)
if not ok then
    ngx.log(ngx.ERR, "Failed to start timer: ", err)
end
