package com.yks.urc.permitStat.bp.api;

import com.yks.urc.vo.FunctionVO;
import com.yks.urc.vo.MenuVO;
import com.yks.urc.vo.ModuleVO;
import com.yks.urc.vo.SystemRootVO;

public interface ITreeNodeHandler {
    void handleMenu(MenuVO curMemu);

    void handleModule(ModuleVO moduleVO);

    void handleFunction(FunctionVO f);
}
