package app.factory.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import app.factory.model.WorkingDay;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RedisService {
    @Autowired
    RedisStringService redisStringService;
    private final ObjectMapper objectMapper;

    @Autowired
    public RedisService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.objectMapper.registerModule(new JavaTimeModule());
    }


    public void saveWorkingDay(WorkingDay workingDay) {
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            String key = "workingDay:" + workingDay.toString();

            jedis.set(key, objectMapper.writeValueAsString(workingDay));
            String report = workingDay.getFullName() + ", " + workingDay.getWorkingTime();
            redisStringService.addDayReport(report);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public List<WorkingDay> getAllObjects() {
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            Set<String> keys = jedis.keys("workingDay:*");
            return keys.stream()
                    .map(key -> {
                        try {
                            String json = jedis.get(key);
                            return objectMapper.readValue(json, WorkingDay.class);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    })
                    .collect(Collectors.toList());
        }
    }

    public void deleteAllObjects() {
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            Set<String> keys = jedis.keys("workingDay:*");
            keys.forEach(jedis::del);
        }
    }

    public boolean checkExistReport(WorkingDay workingDay, boolean isSuperUSer) {

        if (LocalDateTime.now().minusDays(7).isBefore(workingDay.getLocalDateTime()) || isSuperUSer) {

            String report = workingDay.getFullName() + ", " + workingDay.getLocalDateTime().getDayOfMonth() + ", "
                    + workingDay.getWorkingTime() + ", " + workingDay.getItem() + ", " + workingDay.getBatch() + ", "
                    + workingDay.getLevel() + ", " + workingDay.getCoefficient();

            redisStringService.addMonthReport(report);
            redisStringService.addLog(workingDay.toString());
            return false;
        }

        return true;
    }
}