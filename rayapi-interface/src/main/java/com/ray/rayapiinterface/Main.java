package com.ray.rayapiinterface;

import com.ray.rayapiinterface.client.ApiClient;
import com.ray.rayapiinterface.model.User;

public class Main {
    public static void main(String[] args) {
        ApiClient yuApiClient = new ApiClient();
        String result1 = yuApiClient.getNameByGet("鱼皮");
        String result2 = yuApiClient.getNameByPost("鱼皮");
        User user = new User();
        user.setName("鲤鱼旗");
        String result3 = yuApiClient.getUsernameByPost(user);
        System.out.println(result1);
        System.out.println(result2);
        System.out.println(result3);
    }
}