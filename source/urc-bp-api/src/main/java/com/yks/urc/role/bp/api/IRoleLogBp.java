package com.yks.urc.role.bp.api;

import com.yks.urc.enums.RoleLogEnum;

import java.util.List;

public interface IRoleLogBp {
    void addLog(List<Long> roleIds, RoleLogEnum roleLogEnum, String reqJson);
}
