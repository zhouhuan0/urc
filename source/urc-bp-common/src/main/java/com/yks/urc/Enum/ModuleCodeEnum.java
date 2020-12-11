package com.yks.urc.Enum;


public enum ModuleCodeEnum {
    USER_MANAGERMENT(1,"用户管理"),
    ROLE_MANAGERMENT(2,"岗位角色管理"),
    SYSTEM_MANAGERMENT(3,"系统管理");
  
	/**
	 * Module_Code
	 */
    private Integer status;
    
    private String statusName;


    ModuleCodeEnum(Integer status,String statusName){
        this.status = status;
        this.statusName = statusName;
    }
    public Integer getStatus() {
        return status;
    }

    public String getStatusName() {
        return statusName;
    }

    public static String getOrderState(Integer status){
        for(ModuleCodeEnum orderStatusEnum:ModuleCodeEnum.values()){
            if(orderStatusEnum.getStatus().equals(status)){
                return orderStatusEnum.getStatusName();
            }
        }
        return null;
    }

}
