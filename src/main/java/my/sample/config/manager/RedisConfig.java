package my.sample.config.manager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfig {

    @Bean
    public JedisPoolConfig jedisPoolConfig() {

    }

    @Bean
    public JedisPool jedisPool() {

    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory();

        //序列化

        //反序列化

        return ;
    }

}
