package com.example.service;

import com.example.mapper.FollowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FollowService {

    @Autowired
    private FollowMapper followMapper;

    public Map<String, Long> getPeopleStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("a1", followMapper.countTodayFollows());  // 今日新增
        stats.put("a2", followMapper.countYesterdayFollows());  // 昨日新增
        stats.put("a3", followMapper.countMonthlyFollows());  // 本月新增
        stats.put("a4", followMapper.count());  // 粉丝总数
        return stats;
    }

    public void toggleFollow(Integer userId, Integer businessId) {
        if (followMapper.isFollowing(userId, businessId) > 0) {
            followMapper.unfollow(userId, businessId);
        } else {
            followMapper.follow(userId, businessId);
        }
    }

    public boolean isFollowing(Integer userId, Integer businessId) {
        return followMapper.isFollowing(userId, businessId) > 0;
    }
}
