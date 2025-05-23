package org.example.lockproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("all")
@Service
public class RedisService {

  @Autowired
  private RedisTemplate redisTemplate;

  /**
   * 寫入緩存
   *
   * @param key
   * @param value
   * @return
   */
  public boolean set(final String key, Object value) {
    boolean result = false;
    try {
      ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
      operations.set(key, value);
      result = true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }

  /**
   * 寫入緩存設置時效時間
   *
   * @param key
   * @param value
   * @return
   */
  public boolean set(final String key, Object value, Long expireTime) {
    boolean result = false;
    try {
      ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
      operations.set(key, value);
      redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
      result = true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }

  /**
   * 批量刪除對應的value
   *
   * @param keys
   */
  public void remove(final String... keys) {
    for (String key : keys) {
      remove(key);
    }
  }

  /**
   * 批量删除key
   *
   * @param pattern
   */
  public void removePattern(final String pattern) {
    Set<Serializable> keys = redisTemplate.keys(pattern);
    if (keys.size() > 0)
      redisTemplate.delete(keys);
  }

  /**
   * 删除對應的value
   *
   * @param key
   */
  public void remove(final String key) {
    if (exists(key)) {
      redisTemplate.delete(key);
    }
  }

  /**
   * 判断缓存中是否有對應的value
   *
   * @param key
   * @return
   */
  public boolean exists(final String key) {
    return redisTemplate.hasKey(key);
  }

  /**
   * 讀取缓存
   *
   * @param key
   * @return
   */
  public Object get(final String key) {
    Object result = null;
    ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
    result = operations.get(key);
    return result;
  }

  /**
   * 哈希 添加
   *
   * @param key
   * @param hashKey
   * @param value
   */
  public void hmSet(String key, Object hashKey, Object value) {
    HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
    hash.put(key, hashKey, value);
  }

  /**
   * 哈希獲取数据
   *
   * @param key
   * @param hashKey
   * @return
   */
  public Object hmGet(String key, Object hashKey) {
    HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
    return hash.get(key, hashKey);
  }

  /**
   * 列表添加
   *
   * @param k
   * @param v
   */
  public void lPush(String k, Object v) {
    ListOperations<String, Object> list = redisTemplate.opsForList();
    list.rightPush(k, v);
  }

  /**
   * 列表獲取
   *
   * @param k
   * @param l
   * @param l1
   * @return
   */
  public List<Object> lRange(String k, long l, long l1) {
    ListOperations<String, Object> list = redisTemplate.opsForList();
    return list.range(k, l, l1);
  }

  /**
   * 集合添加
   *
   * @param key
   * @param value
   */
  public void add(String key, Object value) {
    SetOperations<String, Object> set = redisTemplate.opsForSet();
    set.add(key, value);
  }

  /**
   * 集合獲取
   *
   * @param key
   * @return
   */
  public Set<Object> setMembers(String key) {
    SetOperations<String, Object> set = redisTemplate.opsForSet();
    return set.members(key);
  }

  /**
   * 有序集合添加
   *
   * @param key
   * @param value
   * @param scoure
   */
  public void zAdd(String key, Object value, double scoure) {
    ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
    zset.add(key, value, scoure);
  }

  /**
   * 有序集合獲取
   *
   * @param key
   * @param scoure
   * @param scoure1
   * @return
   */
  public Set<Object> rangeByScore(String key, double scoure, double scoure1) {
    ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
    return zset.rangeByScore(key, scoure, scoure1);
  }

}