package com.ray.rayapicommon.service;

import com.ray.rayapicommon.model.entity.InterfaceInfo;


/**
* @author Lenovo
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2024-01-24 16:01:06
*/
public interface InnerInterfaceInfoService {

    /**
     *  从数据库中查询模拟接口是否存在(请求路径、请求方法)
     * @param path
     * @param method
     * @return
     */
    InterfaceInfo getInterfaceInfo(String path,String method);
}
