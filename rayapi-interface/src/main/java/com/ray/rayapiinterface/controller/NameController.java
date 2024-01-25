package com.ray.rayapiinterface.controller;

import com.ray.rayapiinterface.model.User;
import org.springframework.web.bind.annotation.*;

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
    public String getUsernameByPost(@RequestBody User user){
        return "POST 用户名字是" + user.getName();
    }


}

