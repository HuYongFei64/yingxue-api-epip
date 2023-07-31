package com.baizhi.service.impl;

import com.baizhi.constants.RedisPrefix;
import com.baizhi.service.SMSService;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class SMSServiceImpl implements SMSService {

    private static final Logger log = LoggerFactory.getLogger(SMSServiceImpl.class);

    private final RedisTemplate redisTemplate;

    @Autowired
    public SMSServiceImpl(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    /**
     * 发送验证码
     * @param phone
     */
    @Override
    public void SendCode(String phone) {

        //判断当前手机号是否已发送验证码
        if (redisTemplate.hasKey("TimeOut"+phone)) throw new RuntimeException("当前手机号已发送验证码，请稍后再试");

        try {
            //生成四位数的验证码
            String code = RandomStringUtils.randomNumeric(4);
            log.info("验证码为：{}",code);
            //发送验证码
            //SmsUtil.sendSms(phone,code);
            //将验证码存入redis
            redisTemplate.opsForValue().set(RedisPrefix.CODE_KEY+phone,code,1, TimeUnit.MINUTES);
            //生成超时时间
            redisTemplate.opsForValue().set("TimeOut"+phone,"当前验证码已存入！",2,TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new RuntimeException("验证码发送失败");
        }

    }

}
