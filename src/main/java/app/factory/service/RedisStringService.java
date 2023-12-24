package app.factory.service;

import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.Set;
@Service
public class RedisStringService {
    private static final String REDIS_KEY = "my_strings";

    public void addString(String value) {
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            if (!jedis.sismember(REDIS_KEY, value)) {
                jedis.sadd(REDIS_KEY, value);
            }
        }
    }

    public Set<String> getAllStrings() {
        try (Jedis jedis = new Jedis("localhost",6379)) {
           return jedis.smembers(REDIS_KEY);
        }
    }

    public void deleteAllStrings() {
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            jedis.del(REDIS_KEY);
        }
    }
}
