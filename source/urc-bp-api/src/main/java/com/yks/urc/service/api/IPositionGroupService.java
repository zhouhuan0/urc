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
    ResultVO getPermissionGroupByUser(String jsonStr,String operator);

    /**
     * 通过groupId删除权限组
     *
     * @param jsonStr
     * @return
     */
    ResultVO deletePermissionGroup(String jsonStr);

    /**
     * 添加或更新权限组
     * @param jsonStr
     * @return
     */
    ResultVO addOrUpdatePermissionGroup(String jsonStr);

    /**
     * 获取权限组详情
     * @param jsonStr
     * @return
     */
    ResultVO getPermissionGroupInfo(String jsonStr);

    /**
     * 获取岗位列表
     * @param jsonStr
     * @return
     */
    ResultVO getPositionList(String jsonStr);

    /**
     * 获取岗位的功能权限
     * @param jsonStr
     * @return
     */
    ResultVO getPositionPermission(String jsonStr);

    /**
     * 根据权限key查询岗位或岗位用户
     * @return
     */
    ResultVO getPositionInfoByPermitKey(String jsonStr);

    /**
     * 导出根据权限key查询岗位或岗位用户数据
     * @param jsonStr
     * @return
     */
    ResultVO exportPositionInfoByPermitKey(String jsonStr);
}
