package com.cuzz.rookiepaybukkit.leaderboard;

import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.Jedis;


//单例模式
public class SingleRedisClient {

    private static volatile Jedis redisClient;

    private SingleRedisClient(){

    }

    public static Jedis getRedisClient() {
        //未初始化或者连接已经断开
        if (redisClient==null||!redisClient.isConnected()){
            //锁住+volatile放置未初始化完成的redisClient提前暴露
            synchronized (SingleRedisClient.class){
               redisClient= SingleRedisClient.CreateJedisClient();
            }
        }
        return redisClient;
    }


    public static Jedis CreateJedisClient(){

        // 创建 Jedis 客户端配置，设置用户名和密码
        DefaultJedisClientConfig config = DefaultJedisClientConfig.builder()
                .user("default") // 如果未启用 ACL，可省略或使用 "default"
                .password("yourpassword") // 替换为您的 Redis 密码
                .build();
        // 创建 Jedis 实例，连接本地 Redis 服务
        Jedis jedis = new Jedis("107.172.217.25", 6379,config);
        return jedis;
    }
}
