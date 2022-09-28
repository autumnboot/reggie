package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.reggie.common.R;
import com.reggie.entity.User;
import com.reggie.service.UserService;
import com.reggie.utils.SMSUtils;
import com.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    //发送短信验证码
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();
        if(StringUtils.isNotEmpty(phone)){
            //生成随机验证码4位
            String code = ValidateCodeUtils.generateValidateCode4String(4);
            log.info("code = {}",code);
            //调用短信服务API发送短信
            //SMSUtils.sendMessage("瑞吉外卖","",phone,code);
            //保存验证码
            session.setAttribute(phone,code);
            return R.success("验证码发送成功！");
        }
        return R.error("验证码发送失败！");
    }

    //移动端登录
    //map正好对应phone和code，也可以用userDto
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info("map = {}",map.toString());
        //获取手机号
        String phone = (String) map.get("phone");
        //获取用户输入的验证码
        String code = (String) map.get("code");
        //获取保存在session的验证码
        String codeInSession = (String) session.getAttribute(phone);
        //验证码比对
        if(codeInSession != null && codeInSession.equals(code)){
            //判断当前数据库中是否有该用户信息
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if(user == null){
                //新增用户信息，即自动注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);//也可以不设置，默认为1
                userService.save(user);
            }
            session.setAttribute("user", user.getId());
            return R.success(user);
        }
        return R.error("登录失败！");
    }
}
