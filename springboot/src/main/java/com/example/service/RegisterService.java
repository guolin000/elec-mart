package com.example.service;

import com.example.common.Result;
import com.example.utils.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

@Service
public class RegisterService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private EmailUtil emailUtil;

    public Result sendVerificationCode(String email) {
        if (email == null || email.isEmpty()) {
            return Result.error("500","邮箱不能为空");
        }

        // 生成 6 位随机验证码
        String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));

        // 发送邮件
        boolean sent = emailUtil.sendEmail(email, "注册验证码", "您的验证码是：" + code + "，有效期为5分钟。");
        if (!sent) {
            return Result.error("500","验证码发送失败");
        }

        // 将验证码存入 Redis，设置 5 分钟过期时间
        redisTemplate.opsForValue().set("verify:" + email, code, 5, TimeUnit.MINUTES);

        return Result.success("验证码发送成功");
    }

    public Result registerUser(String email, String inputCode) {
        // 从 Redis 中获取存储的验证码
        String storedCode = redisTemplate.opsForValue().get("verify:" + email);
        if (storedCode == null) {
            return Result.error("500","验证码已过期");
        }

        if (!storedCode.equals(inputCode)) {
            return Result.error("500","验证码不正确");
        }

        // 验证成功，执行后续注册逻辑（省略）

        // 删除 Redis 中的验证码
        redisTemplate.delete("verify:" + email);

        return Result.success("注册成功");
    }
}