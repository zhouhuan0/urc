package com.yks.urc.constant;

/**
 * @author zhouhuan
 * @version 1.0
 * @date 2020/11/27
 * @see UrcConstant
 * @since JDK1.8
 */
public interface UrcConstant {
    interface RoleType{
        //角色
        int role = 1;
        //岗位
        int position = 2;
    }

    interface AdministratorType{
        //功能管理员
        byte dataAdministrator = 1;
        //数据管理员
        byte functionAdministrator = 2;
    }
}
