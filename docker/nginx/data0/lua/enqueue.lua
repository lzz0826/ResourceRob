local queue = ngx.shared.request_queue

-- 接收請求數據
ngx.req.read_body()
local data = ngx.req.get_body_data()
if not data then
    ngx.status = 400
    ngx.say("No data received")
    return
end

-- 增加 write_point 並存入數據
local write_point, err = queue:incr("write_point", 1, 0) -- 初始值為 0
if not write_point then
    ngx.status = 500
    ngx.say("Failed to increment write_point: ", err)
    return
end

local success, err = queue:set(write_point, data)
if not success then
    ngx.status = 500
    ngx.say("Failed to enqueue data: ", err)
    return
end

ngx.say("Data enqueued with key: ", write_point)
