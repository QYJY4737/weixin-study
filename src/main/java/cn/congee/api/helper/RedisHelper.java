package cn.congee.api.helper;

import cn.congee.api.exception.BaseExceptionMsg;
import cn.congee.api.exception.ServiceException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Redis操作工具类
 * Key生成说明
 * 1)公共数据，微服务之前共享数据（比如字典、科室等）
 * key=公共数据标识:数据标识（业务表主键｜自定义Key）
 * 2)微服务业务数据
 * key=微服务标识:数据标识（业务表主键｜自定义Key）
 * <p>
 * 数据标识必须唯一
 *
 * @Author: yang
 * @Date: 2020-12-09 3:30
 */
@Slf4j
@Component
public class RedisHelper {

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    StringRedisTemplate stringRedisTemplate;


    /**
     * 判断缓存是否存在
     *
     * @param cacheName
     * @param key
     * @return
     */
    public Boolean existKey(String cacheName, String key) {
        if (StringUtils.isAnyBlank(cacheName, key)) {
            throw new ServiceException(BaseExceptionMsg.PARAM_INVALID);
        }
        return redisTemplate.hasKey(redisKey(cacheName, key).toLowerCase());
    }

    /**
     * 保存
     *
     * @param cacheName
     * @param key
     * @param t
     * @param <T>
     */
    public <T> void setObj(String cacheName, String key, T t) {
        if (t == null || StringUtils.isAnyBlank(cacheName, key)) {
            throw new ServiceException(BaseExceptionMsg.PARAM_INVALID);
        }
        BoundValueOperations<String, T> valueOperations = redisTemplate.boundValueOps(redisKey(cacheName, key).toLowerCase());
        valueOperations.set(t);
    }

    /**
     * 保存
     *
     * @param cacheName
     * @param key
     * @param t
     * @param <T>
     */
    public <T> void setObj(String cacheName, String key, List<T> t) {
        if (t == null || StringUtils.isAnyBlank(cacheName, key)) {
            throw new ServiceException(BaseExceptionMsg.PARAM_INVALID);
        }
        BoundValueOperations<String, List<T>> valueOperations = redisTemplate.boundValueOps(redisKey(cacheName, key).toLowerCase());
        valueOperations.set(t);
    }

    /**
     * 保存
     *
     * @param cacheName
     * @param key
     * @param t
     * @param timeout
     * @param <T>
     */
    public <T> void setObj(String cacheName, String key, T t, long timeout) {
        if (t == null || StringUtils.isAnyBlank(cacheName, key)) {
            throw new ServiceException(BaseExceptionMsg.PARAM_INVALID);
        }
        BoundValueOperations<String, T> valueOperations = redisTemplate.boundValueOps(redisKey(cacheName, key).toLowerCase());
        valueOperations.set(t, timeout, TimeUnit.SECONDS);
    }

    /**
     * 保存
     *
     * @param cacheName
     * @param key
     * @param t
     * @param timeout
     * @param <T>
     */
    public <T> void setObj(String cacheName, String key, T t, TimeUnit timeUnit, long timeout) {
        if (t == null || StringUtils.isAnyBlank(cacheName, key)) {
            throw new ServiceException(BaseExceptionMsg.PARAM_INVALID);
        }
        BoundValueOperations<String, T> valueOperations = redisTemplate.boundValueOps(redisKey(cacheName, key).toLowerCase());
        valueOperations.set(t, timeout, timeUnit);
    }

    /**
     * 保存
     *
     * @param cacheName
     * @param key
     * @param t
     * @param timeout
     * @param <T>
     */
    public <T> void setObj(String cacheName, String key, List<T> t, long timeout) {
        if (t == null || StringUtils.isAnyBlank(cacheName, key)) {
            throw new ServiceException(BaseExceptionMsg.PARAM_INVALID);
        }
        BoundValueOperations<String, List<T>> valueOperations = redisTemplate.boundValueOps(redisKey(cacheName, key).toLowerCase());
        valueOperations.set(t, timeout, TimeUnit.SECONDS);
    }

    /**
     * 获取缓存对象
     *
     * @param cacheName
     * @param key
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> T getObj(String cacheName, String key, Class<T> tClass) {
        if (StringUtils.isAnyBlank(cacheName, key)) {
            throw new ServiceException(BaseExceptionMsg.PARAM_INVALID);
        }
        String redisKey = redisKey(cacheName, key).toLowerCase();

        boolean hasKey = redisTemplate.hasKey(redisKey);
        if (!hasKey) {
            log.error("redis key={} 不存在", key);
            return null;
        }
        BoundValueOperations<String, T> valueOperations = redisTemplate.boundValueOps(redisKey);
        if (valueOperations.get() == null) {
            return null;
        }
        if (tClass == String.class) {
            return valueOperations.get();
        }
        if (valueOperations.get() == null) {
            return null;
        }
        return JSON.parseObject(valueOperations.get().toString(), tClass);
    }

    /**
     * 模糊匹配
     *
     * @param keys
     * @return
     */
    public Object keys(String keys) {
        return scanKeys(keys);
    }

    /**
     * 获取缓存对象
     *
     * @param cacheName
     * @param key
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> List<T> getArrayObj(String cacheName, String key, Class<T> tClass) {
        if (StringUtils.isAnyBlank(cacheName, key)) {
            throw new ServiceException(BaseExceptionMsg.PARAM_INVALID);
        }
        String redisKey = redisKey(cacheName, key).toLowerCase();

        boolean hasKey = redisTemplate.hasKey(redisKey);
        if (!hasKey) {
            log.error("redis key={} 不存在", key);
            return null;
        }
        BoundValueOperations<String, List<T>> valueOperations = redisTemplate.boundValueOps(redisKey);
        if (valueOperations.get() == null) {
            return null;
        }
        return JSONArray.parseArray(valueOperations.get().toString(), tClass);
    }

    /**
     * 清空公共的缓存
     *
     * @param cacheName
     * @param redisKey
     */
    public void delRedis(String cacheName, String redisKey) {
        Set<String> keys = scanKeys(redisKey(cacheName, redisKey).toLowerCase() + "*");
        redisTemplate.delete(keys);
    }

    /**
     * 清空正则匹配到的Keys
     *
     * @param pattern
     */
    public void delRedis(String pattern) {
        Set<String> keys = scanKeys(pattern);
        redisTemplate.delete(keys);
    }

    /**
     * 自增
     *
     * @param key
     * @param liveTime
     * @return
     */
    public Long incr(String cacheName, String key, long liveTime) {
        String redisKey = redisKey(cacheName, key).toLowerCase();
        RedisAtomicLong entityIdCounter = new RedisAtomicLong(redisKey, redisTemplate.getConnectionFactory());
        Long increment = entityIdCounter.getAndIncrement();

        boolean in = increment == null || increment.longValue() == 0;
        if (in && liveTime > 0) {
            entityIdCounter.expire(liveTime, TimeUnit.SECONDS);
        }
        return increment;
    }

    /**
     * 自增
     *
     * @param key
     * @param liveTime
     * @return
     */
    public Long incrByOne(String cacheName, String key, long liveTime) {
        String redisKey = redisKey(cacheName, key).toLowerCase();
        RedisAtomicLong entityIdCounter = new RedisAtomicLong(redisKey, redisTemplate.getConnectionFactory());
        Long increment = entityIdCounter.incrementAndGet();
        entityIdCounter.expire(liveTime, TimeUnit.SECONDS);
        return increment;
    }


    /**
     * 对于热搜问题，每次搜索默认　对应分值加　１
     */
    private static final Double score = Double.valueOf(1);

    /**
     * 　搜索时增加对应的关键字的分值
     *
     * @param key   　改搜索对应的分类
     * @param value 搜索的关键字
     */
    public void add(String cacheName, String key, String value) {
        ZSetOperations zSet = redisTemplate.opsForZSet();
        Double scores = zSet.score(redisKey(cacheName, key).toLowerCase(), value);
        if (null != scores) {  // 已经存在key加一
            zSet.incrementScore(redisKey(cacheName, key).toLowerCase(), value, score);
        } else {
            zSet.add(redisKey(cacheName, key).toLowerCase(), value, score);
        }
    }

    /**
     * 从大到小查找最热门的搜索关键字
     *
     * @param key   搜索对应的分类
     * @param count 关键字的数量
     * @return　对应的关键字
     */
    public List<String> reverseFindTop(String cacheName, String key, Long count) {
        ZSetOperations zSet = redisTemplate.opsForZSet();
        Set set = zSet.reverseRangeByScore(redisKey(cacheName, key).toLowerCase(), 0, Double.MAX_VALUE, 0, count);
        return new ArrayList<>(set);
    }


    /**
     * 获取key的一个value的score
     *
     * @param key
     * @param value
     * @return
     */
    public Double getScore(String cacheName, String key, String value) {
        ZSetOperations zSet = redisTemplate.opsForZSet();
        return zSet.score(redisKey(cacheName, key).toLowerCase(), value);
    }


    /**
     * 生成缓存Key
     *
     * @param cacheName
     * @param redisKey
     * @return
     */
    private String redisKey(String cacheName, String redisKey) {
        return cacheName + "::" + redisKey;
    }


    /**
     * 存放hash
     *
     * @param cacheName
     * @param key
     * @param field
     * @param value
     * @param <T>
     */
    public <T> void hset(String cacheName, String key, String field, T value) {
        redisTemplate.opsForHash().put(redisKey(cacheName, key), field, value);
    }


    /**
     * 查询hash缓存
     *
     * @param key
     * @return
     */
    public <T> Map<String, T> hgetAll(String cacheName, String key) {
        return redisTemplate.opsForHash().entries(redisKey(cacheName, key));
    }

    /**
     * 判断是否存在
     *
     * @param key
     * @param field
     * @return
     */
    public boolean hexists(String cacheName, String key, String field) {
        return redisTemplate.opsForHash().hasKey(redisKey(cacheName, key), field);
    }

    /**
     * hash
     *
     * @param key
     * @param map
     */
    public <T> void hputAll(String cacheName, String key, Map<String, T> map) {
        redisTemplate.opsForHash().putAll(redisKey(cacheName, key), map);
    }

    /**
     * 查询hash 值
     *
     * @param key
     * @return
     */
    public <T> List<T> hvalues(String cacheName, String key, Class c) {
        return JSON.parseArray(JSON.toJSONString(redisTemplate.opsForHash().values(redisKey(cacheName, key))), c);
    }

    /**
     * hash 删除
     *
     * @param key
     * @param field
     */
    public void hdel(String cacheName, String key, String field) {
        redisTemplate.opsForHash().delete(redisKey(cacheName, key), field);
    }

    public void listAdd(String cacheName, String key, String field) {
        redisTemplate.opsForSet().add(redisKey(cacheName, key), field);
    }


    public void listDelete(String cacheName, String key, String field) {
        redisTemplate.opsForSet().remove(redisKey(cacheName, key), field);
    }

    public Set<String> listGet(String cacheName, String key) {
        return redisTemplate.opsForSet().members(redisKey(cacheName, key));
    }

    /**
     * 获取符合条件的key
     *
     * @param pattern 表达式
     * @return
     */
    public Set<String> scanKeys(String pattern) {
        Set<String> keys = new HashSet<>();
        this.scan(pattern, item -> {
            // 符合条件的key
            keys.add(item);
        });
        return keys;
    }

    /**
     * SCAN是基于游标的迭代器。这意味着在每次调用该命令时，服务器都会返回相应的查询数据和一个新的游标，用户需要将该游标用作下一个调用中的游标参数。
     * 游标设置为0时，迭代将开始，服务器返回的游标为0时，迭代将终止。
     * ursor: 游标位置，整数值；从 0 开始，到 0 结束；查询的结果有可能是0个，但游标不为 0, 只要游标不为 0，就代表遍历还没有结束。
     * match pattern: 正则匹配字段 （可选）
     * count: 限定单次扫描的数量 limit hint（相当于一个小的增量），默认为10。其并不是查询结果返回的最大数量。比如 count 为 10000，意味着每次扫描 1w 条记录，但是有可能只有 10 条符合条件。（可选）
     *
     * @param pattern  表达式
     * @param consumer 消费者
     */
    private void scan(String pattern, Consumer<String> consumer) {

        RedisSerializer<String> keySerializer = (RedisSerializer<String>) stringRedisTemplate.getKeySerializer();

        ScanOptions options = ScanOptions.scanOptions()
                .match(pattern)
                .count(1000)
                .build();
        try (Cursor<String> cursor = stringRedisTemplate.executeWithStickyConnection((RedisCallback<Cursor<String>>) connection ->
                new ConvertingCursor<>(connection.scan(options), keySerializer::deserialize))) {

            if (cursor == null) {
                return;
            }
            while (cursor.hasNext()) {
                String key = cursor.next();
                consumer.accept(key);
            }
        } catch (IOException e) {
            log.error("IO异常，异常信息", e);
        }
    }

}
