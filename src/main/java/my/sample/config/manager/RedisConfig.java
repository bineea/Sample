package my.sample.config.manager;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.support.ResourceScriptSource;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Bean(name = "redisProperties")
    public PropertiesFactoryBean loadDatabaseProperties()
    {
        PropertiesFactoryBean propertiesFactory = new PropertiesFactoryBean();
        propertiesFactory.setLocation(new ClassPathResource("config/redis.properties"));
        propertiesFactory.setFileEncoding("utf-8");
        return propertiesFactory;
    }

    @Value("#{redisProperties.hostName}")
    private String hostName;
    @Value("#{redisProperties.port}")
    private int port;

    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(1500);  //最大连接数
        jedisPoolConfig.setMaxIdle(1500); //最大空闲连接数
        jedisPoolConfig.setMinIdle(500);   //最小空闲连接数
        jedisPoolConfig.setMaxWaitMillis(20000); //获取连接时最大等待时间
//        jedisPoolConfig.setTestOnBorrow(true); //获取连接时检查是否可用
//        jedisPoolConfig.setTestOnReturn(true); //返回连接时检查是否可用
//        jedisPoolConfig.setTestWhileIdle(true);  //是否开启空闲资源监测
        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(300000); //-1不检测   单位为毫秒  空闲资源监测周期
        jedisPoolConfig.setMinEvictableIdleTimeMillis(30*60*1000);//资源池中资源最小空闲时间 单位为毫秒  达到此值后空闲资源将被移除
        jedisPoolConfig.setNumTestsPerEvictionRun(300); //做空闲监测时，每次采集的样本数  -1代表对所有连接做监测
        return jedisPoolConfig;
    }

    @Bean
    public JedisPool jedisPool(JedisPoolConfig jedisPoolConfig) {
        return new JedisPool(jedisPoolConfig, hostName, port);
    }

    @Bean
    public RedisStandaloneConfiguration redisStandaloneConfiguration() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(hostName, port);
        return configuration;
    }

    @Bean
    public JedisConnectionFactory redisConnectionFactoryByJedis(RedisStandaloneConfiguration redisStandaloneConfiguration, JedisPoolConfig jedisPoolConfig) {
        JedisClientConfiguration jedisClientConfiguration = JedisClientConfiguration.builder().usePooling().poolConfig(jedisPoolConfig).and().readTimeout(Duration.ofMillis(2000)).build();
        return new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration);
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactoryByLettuce(RedisStandaloneConfiguration redisStandaloneConfiguration) {
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    /***********************************Redisson 配置*************************************/
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        //单节点
        //Redis最小空闲连接数量
        config.useSingleServer()
                .setConnectionMinimumIdleSize(10);
        config.useSingleServer()
                .setAddress("redis://"+hostName+":"+port);
        //cluster集群
        //config.useClusterServers().addNodeAddress("redis://"+hostName+":"+port);
        return Redisson.create(config);
    }


    /***********************************RedisTemplate 配置*******************************/
    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);

        //使用StringRedisSerializer来序列化和反序列化redis的key值
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值（默认将使用JDK的序列化方式）
        Jackson2JsonRedisSerializer jacksonSeial = new Jackson2JsonRedisSerializer(Object.class);

        ObjectMapper objectMapper = new ObjectMapper();
        // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        jacksonSeial.setObjectMapper(objectMapper);

        //使用StringRedisSerializer来序列化和反序列化redis的key值
        redisTemplate.setKeySerializer(stringRedisSerializer);
        //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值
        redisTemplate.setValueSerializer(jacksonSeial);

        //使用StringRedisSerializer来序列化和反序列化redis的 hash key值
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的 hash value值
        redisTemplate.setHashValueSerializer(jacksonSeial);

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public ValueOperations<String, Object> valueOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForValue();
    }

    @Bean
    public ListOperations<String, Object> listOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForList();
    }

    @Bean
    public HashOperations<String, String, Object> hashOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForHash();
    }

    @Bean
    public RedisScript<Long> redisScript() {
        return RedisScript.of(new ClassPathResource("config/redisunlock.lua"), Long.class);
    }
}
