package quick.start.commons.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * redis工具类
 *
 * @author Admin
 */
@Component
public class RedisUtil {
    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 原子递增
     *
     * @param key
     * @return
     */
    public Long increment(String key) {
        Long counter = stringRedisTemplate.opsForValue().increment(key, 1);
        return counter;
    }

    /**
     * 原子递减
     *
     * @param key
     * @return
     */
    public Long decrement(String key) {
        Long counter = stringRedisTemplate.opsForValue().decrement(key, 1);
        return counter;
    }

    /**
     * 批量删除缓存
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    /**
     * 批量删除缓存
     */
    public void remove(List<String> keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    /**
     * 批量删除缓存(通配符)
     */
    public void removePattern(final String pattern) {
        Set<Serializable> keys = redisTemplate.keys(pattern);
        if (keys.size() > 0) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 删除缓存
     */
    public void remove(final String key) {
        if (exists(key)) {
            stringRedisTemplate.delete(key);
        }
    }

    /**
     * 判断缓存中是否有对应的value
     */
    public boolean exists(final String key) {
        return stringRedisTemplate.hasKey(key);
    }

    /**
     * 读取缓存
     */
    public Object get(final String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 写入缓存
     */
    public boolean set(final String key, String value) {
        boolean result = false;
        stringRedisTemplate.opsForValue().set(key, value);
        result = true;
        return result;
    }

    /**
     * 设置过期时间
     *
     * @param key        key
     * @param expireTime 过期时间长度
     * @param timeUnit   时间类型
     * @return 是否设置成功
     */
    public boolean expire(final String key, Long expireTime, TimeUnit timeUnit) {
        return stringRedisTemplate.expire(key, expireTime, timeUnit);
    }

    /**
     * 写入缓存(可以配置过期时间)
     */
    public boolean set(final String key, String value, Long expireTime, TimeUnit timeUnit) {
        boolean result = false;
        stringRedisTemplate.opsForValue().set(key, value);
        stringRedisTemplate.expire(key, expireTime, timeUnit);
        result = true;
        return result;
    }

    /**
     * 根据主键key获取hash所有的值转化成bean
     *
     * @param type bean的类型
     * @param key  key
     * @return
     */
    public <T> T selectByPrimaryKey(Class<T> type, String key) {
        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(key);
        String jsonStr = JSONObject.toJSONString(map);
        return JSON.parseObject(jsonStr, type);
    }

    /**
     * redis hash set 数据
     *
     * @param obj 保存的对象
     * @param key 保存的key前缀
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public <T> void setRedisHash(T obj, String key) throws Exception {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            String name = field.getName();
            if (!"class".equals(name) && !Modifier.isStatic(field.getModifiers())) {
                // 将属性的首字符大写，方便构造get，set方法
                name = name.substring(0, 1).toUpperCase() + name.substring(1);
                Method m;
                Object value = null;
                m = obj.getClass().getMethod("get" + name);
                value = m.invoke(obj);
                stringRedisTemplate.opsForHash().put(key.toString(), field.getName(), coverToString(value));
            }
        }
    }

    /**
     * 将其他数据类型转换为string
     *
     * @param obj 数据字段
     * @return
     */
    public String coverToString(Object obj) {
        if (obj != null) {
            if (obj instanceof Date) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return formatter.format(obj);
            } else {
                return String.valueOf(obj);
            }
        }
        return "";
    }

    /**
     * list数据插入,当key存在时,在原list上增加,不会覆盖
     *
     * @param key
     * @param list
     * @param <T>
     */
    public <T> void setRedisList(String key, List<T> list, Long expireTime, TimeUnit timeUnit) {
        redisTemplate.opsForList().leftPushAll(key, list);
        redisTemplate.expire(key, expireTime, timeUnit);
    }

    /**
     * list数据插入,当key存在时,在原list上增加,不会覆盖
     *
     * @param key  key
     * @param list list
     * @param <T>  Object
     */
    public <T> void setRedisList(String key, List<T> list) {
        redisTemplate.opsForList().leftPushAll(key, list);
    }

    /**
     * list数据获取
     *
     * @param key
     * @return
     */
    public List getRedisList(String key) {
        return stringRedisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * list数据插入,当key存在时,在原list上增加,不会覆盖
     *
     * @param key
     * @param object
     * @param <T>
     */
    public <T> void setRedisList(String key, String object) {
        stringRedisTemplate.opsForList().leftPush(key, object);
    }

    /**
     * 增加zset，默认失效时间为1天
     *
     * @param key
     * @param object
     * @param score
     * @param <T>
     * @param <R>
     */
    public <T, R> void addRedisZSet(String key, String object, double score) {
        stringRedisTemplate.opsForZSet().add(key, object, score);
        stringRedisTemplate.expire(key, 1L, TimeUnit.DAYS);
    }

    public <T, R> void addRedisZSet(
            String key, T object, double score, Long expireTime, TimeUnit timeUnit) {
        redisTemplate.opsForZSet().add(key, object, score);
        redisTemplate.expire(key, expireTime, timeUnit);
    }

    public <K, V> Set<V> getZSetRang(K key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    public Long getZSetSize(Object key) {
        return this.redisTemplate.opsForZSet().size(key);
    }

    /** -------------------hash相关操作------------------------- */
    /**
     * 获取存储在哈希表中指定字段的值
     *
     * @param key
     * @param field
     * @return
     */
    public Object hGet(String key, String field) {
        return stringRedisTemplate.opsForHash().get(key, field);
    }

    /**
     * 获取所有给定字段的值
     *
     * @param key
     * @return
     */
    public Map<Object, Object> hGetAll(String key) {
        return stringRedisTemplate.opsForHash().entries(key);
    }

    /**
     * 获取所有指定字段集合的值
     *
     * @param key
     * @param fields 字段集合
     * @return
     */
    public List<Object> hMultiGet(String key, Collection<Object> fields) {
        return stringRedisTemplate.opsForHash().multiGet(key, fields);
    }

    public void hPut(String key, String hashKey, String value) {
        stringRedisTemplate.opsForHash().put(key, hashKey, value);
    }

    public void hPutAll(String key, Map<String, String> maps) {
        stringRedisTemplate.opsForHash().putAll(key, maps);
    }

    /**
     * 仅当hashKey不存在时才设置
     *
     * @param key
     * @param hashKey
     * @param value
     * @return
     */
    public Boolean hPutIfAbsent(String key, String hashKey, String value) {
        return stringRedisTemplate.opsForHash().putIfAbsent(key, hashKey, value);
    }

    /**
     * 删除一个或多个哈希表字段
     *
     * @param key
     * @param fields
     * @return
     */
    public Long hDelete(String key, Object... fields) {
        return stringRedisTemplate.opsForHash().delete(key, fields);
    }

    /**
     * 查看哈希表 key 中，指定的字段是否存在
     *
     * @param key
     * @param field
     * @return
     */
    public boolean hExists(String key, String field) {
        return stringRedisTemplate.opsForHash().hasKey(key, field);
    }

    /**
     * 为哈希表 key 中的指定字段的整数值加上增量 increment
     *
     * @param key
     * @param field
     * @param increment
     * @return
     */
    public Long hIncrBy(String key, Object field, long increment) {
        return stringRedisTemplate.opsForHash().increment(key, field, increment);
    }

    /**
     * 为哈希表 key 中的指定字段的整数值加上增量 increment
     *
     * @param key
     * @param field
     * @param delta
     * @return
     */
    public Double hIncrByFloat(String key, Object field, double delta) {
        return stringRedisTemplate.opsForHash().increment(key, field, delta);
    }

    /**
     * 获取所有哈希表中的字段
     *
     * @param key
     * @return
     */
    public Set<Object> hKeys(String key) {
        return stringRedisTemplate.opsForHash().keys(key);
    }

    /**
     * 获取哈希表中字段的数量
     *
     * @param key
     * @return
     */
    public Long hSize(String key) {
        return stringRedisTemplate.opsForHash().size(key);
    }

    /**
     * 获取哈希表中所有值
     *
     * @param key
     * @return
     */
    public List<Object> hValues(String key) {
        return stringRedisTemplate.opsForHash().values(key);
    }

    /** --------------------set相关操作-------------------------- */

    /**
     * set添加元素
     *
     * @param key
     * @param values
     * @return
     */
    public Long sAdd(String key, String... values) {
        return stringRedisTemplate.opsForSet().add(key, values);
    }

    /**
     * set移除元素
     *
     * @param key
     * @param values
     * @return
     */
    public Long sRemove(String key, Object... values) {
        return stringRedisTemplate.opsForSet().remove(key, values);
    }

    /**
     * 移除并返回集合的一个随机元素
     *
     * @param key
     * @return
     */
    public String sPop(String key) {
        return stringRedisTemplate.opsForSet().pop(key);
    }

    /**
     * 将元素value从一个集合移到另一个集合
     *
     * @param key
     * @param value
     * @param destKey
     * @return
     */
    public Boolean sMove(String key, String value, String destKey) {
        return stringRedisTemplate.opsForSet().move(key, value, destKey);
    }

    /**
     * 获取set集合的大小
     *
     * @param key
     * @return
     */
    public Long sSize(String key) {
        return stringRedisTemplate.opsForSet().size(key);
    }

    /**
     * 判断集合是否包含value
     *
     * @param key
     * @param value
     * @return
     */
    public Boolean sIsMember(String key, Object value) {
        if (value == null) {
            return false;
        }
        return stringRedisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 获取两个集合的交集
     *
     * @param key
     * @param otherKey
     * @return
     */
    public Set<String> sIntersect(String key, String otherKey) {
        return stringRedisTemplate.opsForSet().intersect(key, otherKey);
    }

    /**
     * 获取key集合与多个集合的交集
     *
     * @param key
     * @param otherKeys
     * @return
     */
    public Set<String> sIntersect(String key, Collection<String> otherKeys) {
        return stringRedisTemplate.opsForSet().intersect(key, otherKeys);
    }

    /**
     * key集合与otherKey集合的交集存储到destKey集合中
     *
     * @param key
     * @param otherKey
     * @param destKey
     * @return
     */
    public Long sIntersectAndStore(String key, String otherKey, String destKey) {
        return stringRedisTemplate.opsForSet().intersectAndStore(key, otherKey,
                destKey);
    }

    /**
     * key集合与多个集合的交集存储到destKey集合中
     *
     * @param key
     * @param otherKeys
     * @param destKey
     * @return
     */
    public Long sIntersectAndStore(String key, Collection<String> otherKeys,
                                   String destKey) {
        return stringRedisTemplate.opsForSet().intersectAndStore(key, otherKeys,
                destKey);
    }

    /**
     * 获取两个集合的并集
     *
     * @param key
     * @param otherKeys
     * @return
     */
    public Set<String> sUnion(String key, String otherKeys) {
        return stringRedisTemplate.opsForSet().union(key, otherKeys);
    }

    /**
     * 获取key集合与多个集合的并集
     *
     * @param key
     * @param otherKeys
     * @return
     */
    public Set<String> sUnion(String key, Collection<String> otherKeys) {
        return stringRedisTemplate.opsForSet().union(key, otherKeys);
    }

    /**
     * key集合与otherKey集合的并集存储到destKey中
     *
     * @param key
     * @param otherKey
     * @param destKey
     * @return
     */
    public Long sUnionAndStore(String key, String otherKey, String destKey) {
        return stringRedisTemplate.opsForSet().unionAndStore(key, otherKey, destKey);
    }

    /**
     * key集合与多个集合的并集存储到destKey中
     *
     * @param key
     * @param otherKeys
     * @param destKey
     * @return
     */
    public Long sUnionAndStore(String key, Collection<String> otherKeys,
                               String destKey) {
        return stringRedisTemplate.opsForSet().unionAndStore(key, otherKeys, destKey);
    }

    /**
     * 获取两个集合的差集
     *
     * @param key
     * @param otherKey
     * @return
     */
    public Set<String> sDifference(String key, String otherKey) {
        return stringRedisTemplate.opsForSet().difference(key, otherKey);
    }

    /**
     * 获取key集合与多个集合的差集
     *
     * @param key
     * @param otherKeys
     * @return
     */
    public Set<String> sDifference(String key, Collection<String> otherKeys) {
        return stringRedisTemplate.opsForSet().difference(key, otherKeys);
    }

    /**
     * key集合与otherKey集合的差集存储到destKey中
     *
     * @param key
     * @param otherKey
     * @param destKey
     * @return
     */
    public Long sDifference(String key, String otherKey, String destKey) {
        return stringRedisTemplate.opsForSet().differenceAndStore(key, otherKey,
                destKey);
    }

    /**
     * key集合与多个集合的差集存储到destKey中
     *
     * @param key
     * @param otherKeys
     * @param destKey
     * @return
     */
    public Long sDifference(String key, Collection<String> otherKeys,
                            String destKey) {
        return stringRedisTemplate.opsForSet().differenceAndStore(key, otherKeys,
                destKey);
    }

    /**
     * 获取集合所有元素
     *
     * @param key
     * @return
     */
    public Set<String> setMembers(String key) {
        return stringRedisTemplate.opsForSet().members(key);
    }

    /**
     * 随机获取集合中的一个元素
     *
     * @param key
     * @return
     */
    public Object sRandomMember(String key) {
        return stringRedisTemplate.opsForSet().randomMember(key);
    }

    /**
     * 随机获取集合中count个元素
     *
     * @param key
     * @param count
     * @return
     */
    public List<String> sRandomMembers(String key, long count) {
        return stringRedisTemplate.opsForSet().randomMembers(key, count);
    }

    /**
     * 随机获取集合中count个元素并且去除重复的
     *
     * @param key
     * @param count
     * @return
     */
    public Set<String> sDistinctRandomMembers(String key, long count) {
        return stringRedisTemplate.opsForSet().distinctRandomMembers(key, count);
    }

    /**
     * @param key
     * @param options
     * @return
     */
    public Cursor<String> sScan(String key, ScanOptions options) {
        return stringRedisTemplate.opsForSet().scan(key, options);
    }
}
