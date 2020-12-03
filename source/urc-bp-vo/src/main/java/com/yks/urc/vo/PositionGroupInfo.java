package com.yks.urc.vo;

import com.yks.urc.entity.RoleDO;

import java.util.List;

public class PositionGroupInfo {
    /**
     * 权限组Id
     */
    private String groupId;
    /**
     * 权限组名称
     */
    private String groupName;
    /**
     * 岗位信息
     */
    private List<UserByPosition> positions;
    /**
     * 权限信息
     */
    private List<PermissionVO> selectedContext;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<UserByPosition> getPositions() {
        return positions;
    }

    public void setPositions(List<UserByPosition> positions) {
        this.positions = positions;
    }

    public List<PermissionVO> getSelectedContext() {
        return selectedContext;
    }

    public void setSelectedContext(List<PermissionVO> selectedContext) {
        this.selectedContext = selectedContext;
    }
}
