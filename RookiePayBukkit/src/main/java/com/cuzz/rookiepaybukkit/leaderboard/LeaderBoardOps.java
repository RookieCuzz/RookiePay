package com.cuzz.rookiepaybukkit.leaderboard;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public abstract class LeaderBoardOps {

    public static final String KEY = "leaderboard:paidMoney";
    //更新记录玩家累充金额
    public abstract double incrementPlayerPaidMoney(Player player,Long amount);

    //获取充值排行榜前N名玩家
    public abstract List<String> getLeaderBoardXn(int topN);

    //获取指定玩家在排行榜中的排名
    public abstract Long getPlayerPaidMoneyRank(Player player);

    //获取玩家累计充值的名额
    public abstract Double getPlayerPaidMoneyAmount(Player player);


}
