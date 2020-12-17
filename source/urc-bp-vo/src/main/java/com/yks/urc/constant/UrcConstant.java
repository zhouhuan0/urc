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
        //数据管理员
        Byte dataAdministrator = 2;
        //功能管理员
        Byte functionAdministrator = 1;
    }

    interface SysType{
        //erp系统(内部系统)
        int ERP = 0;
        //FBA系统
        int FBA = 1;
    }
}
