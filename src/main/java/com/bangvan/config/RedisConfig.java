package com.bangvan.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory; // Thêm import
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer; // Thêm import

@Configuration
public class RedisConfig {

    @Value("${app.redis.port}")
    private int port ;

    @Value("${app.redis.host}")
    private String host;

    @Value("${app.redis.password}")
    private String password;

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory(){
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setPort(port);
        configuration.setHostName(host);
        // Chỉ đặt mật khẩu nếu nó không rỗng hoặc null
        if (password != null && !password.isEmpty()) {
            configuration.setPassword(password);
        }
        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){ // Inject factory
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory); // Sử dụng factory đã inject
        // Cấu hình serializer cho key là String
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // Cấu hình serializer cho value (có thể để mặc định hoặc chọn JdkSerializationRedisSerializer, GenericJackson2JsonRedisSerializer)
        // Ví dụ với JdkSerializationRedisSerializer (mặc định):
        // redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        // Ví dụ với Jackson JSON (cần dependency jackson-databind):
        // redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.afterPropertiesSet(); // Thêm dòng này
        return redisTemplate;
    }

    // Bean riêng cho RedisTemplate<String, String> (cho OTP,...)
    @Bean
    public RedisTemplate<String, String> stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) { // Inject factory
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory); // Sử dụng factory đã inject
        // Cấu hình serializer cho key và value đều là String
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet(); // Thêm dòng này
        return redisTemplate;
    }
}