package com.yks.urc.vo;

import java.util.List;

/**
 * roleId 枚举
 * 0    账号管理员
 * 10    销售总监
 * 11    销售经理
 * 12    销售主管
 * 13    销售专员
 * <p>
 * 20    客服总监
 * 21    客服经理
 * 22    客服主管
 * 23    客服专员
 * <p>
 * 100    临时委派人员
 * 101    财务专员
 * 102    法务专员
 * 103    采购开发
 * 104    物流专员
 * 105    仓储专员
 * 106    人资专员
 * 107    资产专员
 * 108    特殊总监
 * 109    其他1
 * 110    其他2
 *
 * @return
 * @Author panyun@youkeshu.com
 * @Date 2020-07-27 11:10
 */
public class RoleUserInfo {
    //角色ID
    private int roleId;
    //角色名称
    private String roleName;
    //用户信息
    private List<UserInfo> userInfoList;

    public static class UserInfo {
        public String userName;
        public String userNameCn;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<UserInfo> getUserInfoList() {
        return userInfoList;
    }

    public void setUserInfoList(List<UserInfo> userInfoList) {
        this.userInfoList = userInfoList;
    }
}
