-- ==========================================
-- 限流核心 Lua 脚本
-- KEYS[1]: 限流的 Redis Key (例如: rate:limit:user_1)
-- ARGV[1]: 允许的最大访问次数 (例如: 10)
-- ARGV[2]: 时间窗口大小，单位秒 (例如: 60)
-- ==========================================

local key = KEYS[1]
local limit = tonumber(ARGV[1])
local expire_time = tonumber(ARGV[2])

-- 1. 获取当前 Key 的已访问次数
local current = redis.call('get', key)



-- 3. 如果没超标，或者是第一次访问，执行累加操作 (+1)
current = redis.call('incr', key)

-- 4. 极其关键的一步：如果是第一次访问（刚 incr 完变成 1）
-- 我们必须给这个 Key 设置一个过期时间，保证这是一个“滑动/固定时间窗口”
-- 如果不加这个判断，每次访问都重置过期时间，这个 Key 永远也过期不了
if tonumber(current) == 1 then
	redis.call('expire', key, expire_time)
end

-- 5. 返回最新的计数值给 Java 端
return tonumber(current)