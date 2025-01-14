package com.example.controller;

import com.example.common.Result;
import com.example.common.enums.ResultCodeEnum;
import com.example.service.FollowService;
import com.example.utils.FollowDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/follow")
public class FollowController {

    @Autowired
    private FollowService followService;

    @PostMapping("/toggle")
    public Result toggleFollow(@RequestBody FollowDto followDto) {
        followService.toggleFollow(followDto.getUserId(), followDto.getBusinessId());
        return Result.success();
    }

    @GetMapping("/check")
    public Result checkFollow(@RequestParam("userId") Integer userId, @RequestParam("businessId") Integer businessId) {
        if (userId == null || businessId == null) {
            return Result.error(ResultCodeEnum.valueOf("缺少必要的参数"));
        }
        boolean isFollowing = followService.isFollowing(userId, businessId);
        System.out.println(isFollowing);
        return Result.success(isFollowing);
    }

    @GetMapping("/getPeople")
    public Result getPeople(@RequestParam("businessId") Integer businessId) {
        Map<String, Long> stats = followService.getPeopleStats(businessId);
        System.out.println(stats);
        return Result.success(stats);
    }
}
