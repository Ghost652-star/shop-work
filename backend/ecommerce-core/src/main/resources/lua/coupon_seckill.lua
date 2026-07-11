-- 优惠券秒杀 Lua 脚本
-- KEYS[1]: coupon:stock:{couponId}   (库存计数器)
-- KEYS[2]: coupon:claimed:{couponId} (已领取用户 Set)
-- ARGV[1]: userId
-- 返回值: 0=成功, 1=库存不足, 2=已领取过

local stock = tonumber(redis.call('GET', KEYS[1]) or '0')
if stock <= 0 then
    return 1
end

local claimed = redis.call('SISMEMBER', KEYS[2], ARGV[1])
if claimed == 1 then
    return 2
end

redis.call('DECR', KEYS[1])
redis.call('SADD', KEYS[2], ARGV[1])
return 0
