package com.yks.urc.mapper;

import com.yks.urc.entity.DataRuleDO;
import com.yks.urc.entity.UserDO;
import com.yks.urc.vo.UserVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IUserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserDO record);

    int insertSelective(UserDO record);

    UserDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserDO record);

    int updateByPrimaryKey(UserDO record);

    List<UserDO> listUsersByRoleId(Integer roleId);

    /**
     *
     * @param  userName
     * @return UserVO
     * @Author linwanxian@youkeshu.com
     * @Date 2018/6/11 10:44
     */
    UserVO getUserByName(String userName);

    /**
     * 搜索用户
     *
     * @param userVO
     * @param pageNumber
     * @param pageData
     * @return  List<UserDO>
     * @Author linwanxian@youkeshu.com
     * @Date 2018/6/11 10:34
     */
    List<UserDO> getUsersByUserInfo(@Param("userVO") UserVO userVO, @Param("pageNumber") int pageNumber,@Param("pageData") int pageData);
    /**
     * 获取搜索用户分页的总数
     * @param  userVO
     * @return int
     * @Author linwanxian@youkeshu.com
     * @Date 2018/6/11 17:05
     */
   int getUsersByUserInfoCount(@Param("userVO") UserVO userVO);
    /**
     * 批量同步到数据库
     *
     * @param userDoList
     * @return
     */
    int insertBatchUser(@Param("userDoList") List<UserDO> userDoList);

    /**
     *
     * @return
     * @Author linwanxian@youkeshu.com
     * @Date 2018/6/11 10:43
     */
    int deleteUrcUser();
    
    /**
     * 通过钉钉userId获取用户
     * @param userId
     * @return
     */
	UserDO getUserInfoByDingUserId(String userId);

	/**
	 * 根据ruleId查看哪些用户已经拥有数据权限
	 * @param ruleDO
	 * @return
	 */
	List<UserDO> queryUserDataByRuleId(DataRuleDO ruleDO);
	
	/**
	 * 根据ruleId查看哪些用户没有数据权限
	 * @param ruleDO
	 * @return
	 */
	List<UserDO> queryUserNoDataByRuleId(DataRuleDO ruleDO);

}