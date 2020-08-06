package com.yks.urc.cache.bp.api;

import java.util.List;

/**
 * 版权：Copyright by www.youkeshu.com
 * 描述：代码注释以及格式化示例
 * 创建人：@author Songguanye
 * 创建时间：2019/1/19 15:16
 * 修改理由：
 * 修改内容：
 */
public interface IUpdateAffectedUserPermitCache {
    /**
     *刷新超级管理员的权限
     * @param roleId 角色id
     */
    void assignAllPermit2SuperAdministrator(Long roleId);
}
