package com.yupi.project.service;

import com.yupi.project.model.entity.UserInterfaceInfo;

import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Lenovo
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service
* @createDate 2024-02-04 11:01:18
*/
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {

    //校验
    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);

    /**
     * 调用接口统计
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean invokeCount(long interfaceInfoId,long userId);
}
