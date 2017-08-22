package com.example.demo.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

@Configuration
@Profile("azureRedis")
public class RedisConfigsAz {
	
	@Value("${azureRedis.host}")
	public String host;
	@Value("${azureRedis.port}")
	public int port;	
	@Value("${azureRedis.pass}")
	public String pass;
	
	@Bean
	@Scope("singleton")
	public JedisPool getJedisPool(){
	
		return new JedisPool(new JedisPoolConfig(),this.host,this.port,Protocol.DEFAULT_TIMEOUT,this.pass,Protocol.DEFAULT_DATABASE,true,null,null,null);
	}
	
}
