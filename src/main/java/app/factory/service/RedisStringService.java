package app.factory.service;

import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.Set;

@Service
public class RedisStringService {
    private static final String DAILY_REPORT = "dailyReport";
    private static final String MONTH_DAY_REPORT = "monthReport";
    private static final String REPORT_LOG = "reportLog";

    public void addDayReport(String value) {
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            if (!jedis.sismember(DAILY_REPORT, value)) {
                jedis.sadd(DAILY_REPORT, value);
            }
        }
    }

    public Set<String> getAllDailyReports() {
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            return jedis.smembers(DAILY_REPORT);
        }
    }

    public void deleteAllDailyReports() {
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            jedis.del(DAILY_REPORT);
        }
    }

    public void addMonthReport(String value) {
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            jedis.sadd(MONTH_DAY_REPORT, value);
        }
    }

    public Set<String> getAllMonthReports() {
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            return jedis.smembers(MONTH_DAY_REPORT);
        }
    }

    public void deleteAllMonthReports() {
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            jedis.del(MONTH_DAY_REPORT);
        }
    }

    public void addLog(String value) {
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            if (!jedis.sismember(REPORT_LOG, value)) {
                jedis.sadd(REPORT_LOG, value);
            }
        }
    }

}
