package com.cuzz.rookiepaybukkit.leaderboard;

import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;
import java.util.Set;

public class PoolRedisClient extends LeaderBoardOps {

    private  volatile JedisPool jedisPool;

    private PoolRedisClient() {}

    public Jedis getRedisClient() {
        if (jedisPool == null) {
            synchronized (PoolRedisClient.class) {
                if (jedisPool == null) {
                    jedisPool =createJedisPool();
                }
            }
        }
        return jedisPool.getResource();
    }

    public  JedisPoolConfig createJedisPoolConfig(){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128); // 最大连接数
        poolConfig.setMaxIdle(64);   // 最大空闲连接数
        poolConfig.setMinIdle(16);   // 最小空闲连接数
        poolConfig.setTestOnBorrow(true); // 获取连接时测试连接是否可用
        poolConfig.setTestOnReturn(true); // 归还连接时测试连接是否可用
        poolConfig.setTestWhileIdle(true); // 空闲时测试连接是否可用
        return poolConfig;
    }

    public  JedisPool createJedisPool(){
        return new JedisPool(createJedisPoolConfig(), "107.172.217.25", 6379, 2000, "yourpassword");

    }


    @Override
    public double incrementPlayerPaidMoney(Player player, Long amount) {
        double zincrby = getRedisClient().zincrby(KEY, amount, player.getName());
        return zincrby;
    }

    @Override
    public List<String> getLeaderBoardXn(int topN) {

        List<String> zrevrange = getRedisClient().zrevrange(KEY, 0, topN - 1);
        return zrevrange;
    }

    @Override
    public Long getPlayerPaidMoneyRank(Player player) {
        return getRedisClient().zrevrank(KEY,player.getName());
    }

    @Override
    public Double getPlayerPaidMoneyAmount(Player player) {
        return getRedisClient().zscore(KEY,player.getName());
    }
}
