package com.yks.urc.mapper;

import com.yks.urc.entity.PermissionDO;

import java.util.List;
import java.util.Map;

import com.yks.urc.vo.FunctionVO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

public interface PermissionMapper {
    /**
     * 获取systemKey
     *
     * @param sysKey
     * @return Permission
     * @Author linwanxian@youkeshu.com
     * @Date 2018/6/12 9:28
     */
    PermissionDO getSystemKey(@Param("sysKey") String sysKey);

    PermissionDO getPermissionBySysKey(@Param("sysKey") String sysKey);

    void deleteSyspermitDefine(@Param("lstPermit") List<PermissionDO> lstPermit);

    /**
     * 先按sysKey删除，再insert
     *
     * @param lstPermit
     * @author panyun@youkeshu.com
     * @date 2018年6月14日 下午7:59:31
     */
    void insertSysPermitDefine(@Param("lstPermit") List<PermissionDO> lstPermit);
    /**

     /**
     * 获取 systemname
     * @param
     * @return
     * @Author linwanxian@youkeshu.com
     * @Date 2018/6/14 15:56
     */
    //List<Permission> getSysKeyAndName( List<String> sysKey);

    /**
     * 通过sysKey 获取permission
     *
     * @param
     * @return
     * @Author linwanxian@youkeshu.com
     * @Date 2018/6/15 9:44
     */
    PermissionDO getPermission(@Param("sysKey") String sysKey);

    List<PermissionDO> getSysApiUrlPrefix();

    /**
     * Description: 根据roleId获取对应的系统功能权限数据
     *
     * @param :
     * @throws Exception 对可能产生的异常进行说明
     * @return:
     * @see
     */
    @MapKey("sysKey")
    Map<String, PermissionDO> getSysContextByRoleId(@Param("roleId") Long roleId);

    /**
     * 得到所有的sys_key(不包含禁用状态的系统)
     *
     * @return
     */
    List<PermissionDO> getAllSysKey();

    /**
     * 得到所有的sys_key(不包含禁用状态的系统)
     *
     * @return
     */
    List<PermissionDO> getAllSysKeyBySysType(@Param("sysType") Integer sysType);

    /**
     * 根据sys_key更新sys_context
     *
     * @param p
     */
    int updateSysContextBySysKey(PermissionDO p);

    /**
     *  根据sys_key更新sys_context 可选择更新内容
     * @param
     * @return
     * @Author lwx
     * @Date 2018/11/12 16:26
     */
    int updateSysContextBySysKeyCondition(PermissionDO p);

    /**
     * (不包含禁用状态的系统)
     * @return
     */
    List<PermissionDO> getAllSysPermit();

    /**
     * Description: 获取系统功能权限Map集合(包含禁用状态的系统)
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/7/2 11:44
     * @see
     */
    @MapKey("sysKey")
    Map<String, PermissionDO> perMissionMap();


    /**
     *  根据sysKey 获取SysName
     * @param
     * @return
     * @Author lwx
     * @Date 2018/8/13 15:07
     */
    String getSysNameByKey(String sysKey);
    /**
     *  根据 sysKey 删除权限
     * @param
     * @return
     * @Author lwx
     * @Date 2018/11/12 10:06
     */
    int deleteSysPermissionBySysKey(String sysKey);


    /**
     *  获取系统类型
     * @param sysKey
     * @return
     */
    Integer getSysType(String sysKey);

    /**
     * 获取所有的系统key和名称(包含禁用状态的系统)
     * @return
     */
    List<PermissionDO> getAllSystem(@Param("sysType") String sysType);

    List<PermissionDO> selectSelective(PermissionDO permissionDO);

    List<PermissionDO> selectSystemInfoPage(@Param("sysKey") String sysKey,@Param("status")Integer status,@Param("sysType") String sysType,@Param("pageData")Integer pageData,@Param("startIndex")Integer startIndex);

    /**
     * 通过系统类型得到的sys_key(不包含禁用状态的系统)
     *
     * @return
     */
    List<PermissionDO> getSysKey(@Param("sysType")String sysType);
}