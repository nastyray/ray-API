package com.ray.rayapiinterface.controller;


import com.ray.rayapiclientsdk.model.User;
import com.ray.rayapiclientsdk.utils.SignUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * ClassName: NameController
 * Package: com.ray.rayapiinterface.controller
 * Description: 名称API
 *
 * @Author lilray
 * @Create 2024/1/25 17:03
 * @Version 1.0
 */
@RestController
@RequestMapping("/name")
public class NameController {

    @GetMapping("/")
    public String getNameByGet(String name){
        return "GET 你的名字是" + name;
    }

    @PostMapping("/")
    public String getNameByPost(@RequestParam String name){
        return "POST 你的名字是" + name;
    }

    @PostMapping("/user")
    public String getUsernameByPost(@RequestBody User user, HttpServletRequest request){
        // 1.拿到这五个我们可以一步一步去做校验,比如 accessKey 我们先去数据库中查一下
        // 从请求头中获取参数
        String accessKey = request.getHeader("accessKey");
        String nonce = request.getHeader("nonce");
        String timestamp = request.getHeader("timestamp");
        String sign = request.getHeader("sign");
        String body = request.getHeader("body");
        // 不能直接获取秘钥
        //        String secretKey = request.getHeader("secretKey");


        // 2.校验权限,这里模拟一下,直接判断 accessKey 是否为"yupi",实际应该查询数据库验证权限
        if (!accessKey.equals("lilray")){
            throw new RuntimeException("无权限");
        }

        // 3.校验一下随机数,因为时间有限,就不带大家再到后端去存储了,后端存储用hashmap或redis都可以
        // 校验随机数,模拟一下,直接判断nonce是否大于10000
        if (Long.parseLong(nonce) > 10000) {
            throw new RuntimeException("无权限");
        }

        // 4.校验时间戳与当前时间的差距,交给大家自己实现
        // if (timestamp) {}

        //todo 实际情况中是从数据库中查出 secretKey
        String severSign = SignUtils.genSign(body, "abcdefgh");
        //如果生成的签名不一致，则抛出异常，并提示"无权限"
        if ( !sign.equals(severSign)){
            throw new RuntimeException("无权限");

        }



        // 如果权限校验通过，返回 "POST 用户名字是" + 用户名
        String result = "POST 用户名字是" + user.getUsername();
        //调用成功后，次数+1

        return result;

    }


}

