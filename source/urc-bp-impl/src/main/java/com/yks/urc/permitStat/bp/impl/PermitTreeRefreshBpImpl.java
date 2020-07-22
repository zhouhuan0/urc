package com.yks.urc.permitStat.bp.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yks.urc.entity.PermissionDO;
import com.yks.urc.mapper.PermissionMapper;
import com.yks.urc.permitStat.bp.api.IPermitTreeRefreshBp;
import com.yks.urc.permitStat.bp.api.ITreeNodeHandler;
import com.yks.urc.serialize.bp.api.ISerializeBp;
import com.yks.urc.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class PermitTreeRefreshBpImpl implements IPermitTreeRefreshBp {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PermissionMapper permissionMapper;

    @Autowired
    private ISerializeBp serializeBp;

    @Override
    public void refreshNewestFields(SystemRootVO rootVO) {
        try {
            refreshNewestFields_Ex(rootVO);
        } catch (Exception ex) {
            logger.error(serializeBp.obj2Json(rootVO), ex);
        }
    }

    private void refreshNewestFields_Ex(SystemRootVO rootVO) {
        PermissionDO perNewest = permissionMapper.getPermissionBySysKey(rootVO.system.key);
        rootVO.system.name = perNewest.getSysName();

        SystemRootVO rootNewest = serializeBp.json2ObjNew(perNewest.getSysContext(), new TypeReference<SystemRootVO>() {
        });
        SystemRootPlainVO plainVO = tree2Plain(rootNewest);
        // 更新所有节点的name/sortIdx/url
        scanMenu(rootVO, new ITreeNodeHandler() {
            @Override
            public void handleMenu(MenuVO curMemu) {
                Optional<MenuVO> op = plainVO.lstMenu.stream().filter(f -> f.key.equalsIgnoreCase(curMemu.key)).findFirst();
                if (op.isPresent()) {
                    MenuVO mNewest = op.get();
                    curMemu.name = mNewest.name;
                    curMemu.sortIdx = mNewest.sortIdx;
                    curMemu.url = mNewest.url;
                }
            }

            @Override
            public void handleModule(ModuleVO curModule) {
                Optional<ModuleVO> op = plainVO.lstModule.stream().filter(f -> f.key.equalsIgnoreCase(curModule.key)).findFirst();
                if (op.isPresent()) {
                    ModuleVO mNewest = op.get();
                    curModule.name = mNewest.name;
                    curModule.sortIdx = mNewest.sortIdx;
                    curModule.url = mNewest.url;
                }
            }

            @Override
            public void handleFunction(FunctionVO curModule) {
                Optional<FunctionVO> op = plainVO.lstFunction.stream().filter(f -> f.key.equalsIgnoreCase(curModule.key)).findFirst();
                if (op.isPresent()) {
                    FunctionVO mNewest = op.get();
                    curModule.name = mNewest.name;
                }
            }
        });
    }

    private SystemRootPlainVO tree2Plain(SystemRootVO rootVO) {
        SystemRootPlainVO plainVO = new SystemRootPlainVO();
        plainVO.lstModule = new ArrayList<>();
        plainVO.lstFunction = new ArrayList<>();
        plainVO.lstMenu = new ArrayList<>();
        scanMenu(rootVO, new ITreeNodeHandler() {
            @Override
            public void handleMenu(MenuVO curMemu) {
                plainVO.lstMenu.add(curMemu);

            }

            @Override
            public void handleModule(ModuleVO moduleVO) {
                plainVO.lstModule.add(moduleVO);
            }

            @Override
            public void handleFunction(FunctionVO f) {
                plainVO.lstFunction.add(f);
            }
        });
        return plainVO;
    }

    private void scanMenu(SystemRootVO rootVO, ITreeNodeHandler treeNodeHandler) {
        List<MenuVO> menu1 = rootVO.menu;
        if (CollectionUtils.isEmpty(menu1)) {
            return;
        }
        for (int j = 0; j < menu1.size(); j++) {
            MenuVO curMemu = menu1.get(j);
            treeNodeHandler.handleMenu(curMemu);
            scanModule(curMemu.module, treeNodeHandler);
        }
    }


    private void scanModule(List<ModuleVO> lstModule, ITreeNodeHandler treeNodeHandler) {
        if (CollectionUtils.isEmpty(lstModule)) {
            return;
        }
        for (ModuleVO moduleVO : lstModule) {
            treeNodeHandler.handleModule(moduleVO);
            scanModule(moduleVO.module, treeNodeHandler);
            scanFunction(moduleVO.function, treeNodeHandler);
        }
    }


    private void scanFunction(List<FunctionVO> lstFunction, ITreeNodeHandler treeNodeHandler) {
        if (CollectionUtils.isEmpty(lstFunction)) {
            return;
        }
        for (FunctionVO f : lstFunction) {
            treeNodeHandler.handleFunction(f);
            scanFunction(f.function, treeNodeHandler);
        }
    }

}
