package com.ray.rayapiclientsdk.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;

import com.ray.rayapiclientsdk.model.User;
import com.ray.rayapiclientsdk.utils.SignUtils;


import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: ApiClient
 * Package: com.ray.rayapiinterface.client
 * Description: 调用第三方接口的客户端
 *
 * @Author lilray
 * @Create 2024/1/25 17:40
 * @Version 1.0
 */
public class ApiClient {

    private static  final  String GATEWAY_HOST = "http://localhost:8090";

    private String accessKey;

    private String secretKey;

    public ApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String getNameByGet(String name){
        // 可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        String result= HttpUtil.get(GATEWAY_HOST + "/api/name/", paramMap);
        System.out.println(result);
        return result;
    }

    public String getNameByPost(String name){
        // 可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        String result= HttpUtil.post(GATEWAY_HOST + "/api/name", paramMap);
        System.out.println(result);
        return result;
    }

    private Map<String,String> getHeaderMap(String body){
        //创建一个新的HashMap
        Map<String,String> hashMap = new HashMap<>();
        // 将 "accessKey" 和其对应的值放入 map 中
        hashMap.put("accessKey", accessKey);
        //注意，不能直接发送密钥
        //hashMap.put("secretKey", secretKey);
        //生成随机数（生成一个包含4个随机数字的字符串）
        hashMap.put("nonce", RandomUtil.randomNumbers(4));
        //请求体的内容
        hashMap.put("body",body);
        //当前时间戳
        //System.currentTimeMillis()返回当前时间的毫秒数，除以1000转换为秒数
        hashMap.put("timestamp",String.valueOf(System.currentTimeMillis() / 1000));
        hashMap.put("sign", SignUtils.genSign(body,secretKey));
        // 返回构造的请求头 map
        return hashMap;
    }

    public String getUsernameByPost( User user){
        // 将User对象转换为JSON字符串
        String json = JSONUtil.toJsonStr(user);
        // 使用HttpRequest工具发起POST请求，并获取服务器的响应
        HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST + "/api/name/user")
                .addHeaders(getHeaderMap(json))
                .body(json) // 将JSON字符串设置为请求体
                .execute(); // 执行请求
        // 打印服务器返回的状态码
        System.out.println(httpResponse.getStatus());
        // 获取服务器返回的结果
        String result = httpResponse.body();
        // 打印服务器返回的结果
        System.out.println(result);
        // 返回服务器返回的结果
        return result;

    }
}
