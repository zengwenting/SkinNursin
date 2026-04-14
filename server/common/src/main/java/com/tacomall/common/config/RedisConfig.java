package com.tacomall.common.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfig extends CachingConfigurerSupport {

    protected static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    @Value("${spring.data.redis.host:127.0.0.1}")
    private String host;

    @Value("${spring.data.redis.port:10010}")
    private Integer port;

    @Value("${spring.data.redis.jedis.pool.max-active:500}")
    private Integer maxTotal;

    @Value("${spring.data.redis.jedis.pool.max-idle:500}")
    private Integer maxIdle;

    @Value("${spring.data.redis.jedis.pool.min-idle:500}")
    private Integer minIdle;

    @Value("${spring.data.redis.password:}")
    private String password;

    @Value("${spring.data.redis.username:}")
    private String username;

    @Value("${spring.data.redis.timeout:500}")
    private Integer timeout;

    @PostConstruct
    public void logRedisConnectionMode() {
        String normalizedPassword = normalize(password);
        String normalizedUsername = normalize(username);
        String authMode;

        if (normalizedPassword == null) {
            authMode = "no-auth";
        } else if (normalizedUsername == null) {
            authMode = "password-only";
        } else {
            authMode = "acl-username-password";
        }

        logger.info("Redis connection settings prepared. address={}:{}, authMode={}", host, port, authMode);
    }

    public JedisPool redisPoolFactory() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(maxTotal);
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMinIdle(minIdle);

        String normalizedPassword = normalize(password);
        String normalizedUsername = normalize(username);
        JedisPool jedisPool;

        if (normalizedPassword == null) {
            jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout);
            logger.info("Redis is configured without authentication. JedisPool will use the no-auth constructor.");
        } else if (normalizedUsername == null) {
            jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, normalizedPassword);
            logger.info("Redis is configured with password-only authentication.");
        } else {
            jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, timeout, normalizedUsername,
                    normalizedPassword, 0, null);
            logger.info("Redis is configured with ACL username/password authentication.");
        }

        logger.info("JedisPool injected successfully");
        logger.info("redis address: {}:{}", host, port);
        return jedisPool;
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

}
