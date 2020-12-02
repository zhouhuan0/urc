package com.yks.urc.service.api;

import com.yks.urc.vo.ResultVO;

public interface IPositionGroupService {
    /**
     * 获取用户的权限组
     *
     * @param
     * @param
     * @return
     */
    ResultVO getPermissionGroupByUser(String jsonStr);

    /**
     * 通过groupId删除权限组
     *
     * @param jsonStr
     * @return
     */
    ResultVO deletePermissionGroup(String jsonStr);
}
