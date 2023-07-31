package com.baizhi.controller;

import com.baizhi.entity.Sms;
import com.baizhi.service.SMSService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 发送验证码控制层
 *
 * @author makejava
 * @since 2023-07-14 02:20:23
 */
@RestController
@RequestMapping("/captchas")
public class SMSController {
    private static final Logger log = LoggerFactory.getLogger(SMSController.class);
    /**
     * 服务对象
     */
    private final SMSService smsService;

    @Autowired
    public SMSController(SMSService smsService) {
        this.smsService = smsService;
    }

    @PostMapping
    public void SendCode(@RequestBody Sms sms) {
        log.info("手机号为：{}", sms.getPhone());
        smsService.SendCode(sms.getPhone());
    }

}

