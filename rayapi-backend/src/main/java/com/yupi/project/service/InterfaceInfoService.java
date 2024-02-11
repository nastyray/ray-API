package com.yupi.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ray.rayapicommon.model.entity.InterfaceInfo;

/**
* @author Lenovo
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2024-01-24 16:01:06
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {
    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);
}
