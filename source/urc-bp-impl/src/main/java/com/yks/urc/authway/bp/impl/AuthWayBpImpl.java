/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author linwanxian@youkeshu.com
 * @create 2018/6/14
 * @since 1.0.0
 */
package com.yks.urc.authway.bp.impl;

import com.yks.urc.authway.bp.api.AuthWayBp;
import com.yks.urc.mapper.*;
import com.yks.urc.vo.AuthWayVO;
import com.yks.urc.vo.SysAuthWayVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AuthWayBpImpl implements AuthWayBp {

    @Autowired
    private IRoleMapper roleMapper;
    @Autowired
    private IRolePermissionMapper rolePermissionMapper;
    @Autowired
    private AuthWayMapper authWayMapper;

    /**
     * 1. 首先通过用户判断是否是管理员 2. 通过管理员拿到对应的system_key,若是超管, 获取所有的sys_key , 3. sys_key 拿到对应的业务系统实体
     *
     * @param operator
     * @return
     */
    @Override
    public List<SysAuthWayVO> getMyAuthWay(String operator) {
        boolean isAdmin = roleMapper.isSuperAdminAccount(operator);
        List<SysAuthWayVO> lstAuthWayVOS = new ArrayList<>();
        //1.首先通过用户判断是否是管理员
        if (isAdmin == true) {
            //2. 通过管理员拿到sys_key
            List<String> getSysKey = rolePermissionMapper.getSysKetByRoleAndUserName(operator);
            //组装sysAuthWayVO
            SysAuthWayVO sysAuthWayVO = this.AssembleSysAuthWay(getSysKey);
            lstAuthWayVOS.add(sysAuthWayVO);
        } else {
            lstAuthWayVOS = null;
        }
        return lstAuthWayVOS;
    }

    /**
     * 组装 sysAuthWayVO
     *
     * @param
     * @return
     * @Author linwanxian@youkeshu.com
     * @Date 2018/6/14 17:14
     */
    public SysAuthWayVO AssembleSysAuthWay(List<String> getSysKey) {
        SysAuthWayVO sysAuthWayVO = new SysAuthWayVO();
        List<AuthWayVO> authWayVOS = new ArrayList();
        //3. sys_key 拿到对应的业务系统实体
        for (String sysKey : getSysKey) {
            //获取最终结果
            AuthWayVO authWayVO = authWayMapper.getAuthWayVoBySysKey(sysKey);
            //组装sysKey
            sysAuthWayVO.sysKey = authWayVO.sysKey;
            //然后将authWayVO的sysKey置为空
            authWayVO.sysKey =null;
            //组装sortIdx
            sysAuthWayVO.sortIdx = authWayVO.sortIdx;
            authWayVO.sortIdx = null;
            //组装sysName
            sysAuthWayVO.sysName = authWayVO.sysName;
            authWayVO.sysName =null;
            //组装 lstEntity  authWayVO 和 entity 是一对一的关系 entity和sysAuthWayVO 是一对多的关系
            authWayVOS.add(authWayVO);
        }
        sysAuthWayVO.lstEntity = authWayVOS;
        return sysAuthWayVO;
    }

    public static void main(String[] args) {
        AuthWayBp authWayBp = new AuthWayBpImpl();
        authWayBp.getMyAuthWay("panyun");
    }
}
