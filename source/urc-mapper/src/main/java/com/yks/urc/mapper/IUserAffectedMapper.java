package com.yks.urc.mapper;


import com.yks.urc.entity.UserAffectedDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 版权：Copyright by www.youkeshu.com
 * 描述：代码注释以及格式化示例
 * 创建人：@author Songguanye
 * 创建时间：2019/1/17 15:03
 * 修改理由：
 * 修改内容：
 */
public interface IUserAffectedMapper {

    /**
     * 删除角色时 将角色id和角色下的用户保存到urc_role_user_affected表
     * @param map
     */
    void saveAffectedUser(Map map);

    /**
     * 查询urc_role_user_affected里的用户
     * @return
     */
    List<UserAffectedDO> selectAffectedUserList();

    /**
     * 根据用户名删除受角色变化影响的用户
     * @param idList 用户名
     */
    void deleteAffectedUserByUserNameList(@Param("idList") List<Long> idList);
    /**
     * 根据用户名删除受角色变化影响的用户
     * @param id 主键
     */
    void deleteAffectedUserByUserName(Long id);
}
