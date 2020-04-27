package com.yks.urc.permitStat.bp.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yks.urc.entity.PermissionDO;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.mapper.PermissionMapper;
import com.yks.urc.permitStat.bp.api.IPermitSortBp;
import com.yks.urc.serialize.bp.api.ISerializeBp;
import com.yks.urc.userValidate.bp.api.IUserValidateBp;
import com.yks.urc.vo.MenuVO;
import com.yks.urc.vo.ModuleVO;
import com.yks.urc.vo.SystemRootVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class PermitSortBpImpl implements IPermitSortBp {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PermissionMapper permissionMapper;

    @Autowired
    private ISerializeBp serializeBp;

    @Autowired
    private IUserValidateBp userValidateBp;

    public void sortSystemRootVO(SystemRootVO cur, String sysKey) {
        try {
            // 对 menu及module排序
            // 将 cur 与系统定义比对，填充 sortIdx
            PermissionDO permissionDO = permissionMapper.getSystemKey(sysKey);
            SystemRootVO sys = serializeBp.json2ObjNew(permissionDO.getSysContext(), new TypeReference<SystemRootVO>() {
            });

            for (MenuVO curModule : cur.menu) {
                Optional<MenuVO> op = sys.menu.stream().filter(c -> StringUtility.stringEqualsIgnoreCase(c.key, curModule.key)).findFirst();
                if (op.isPresent()) {
                    curModule.sortIdx = op.get().sortIdx;
                }
            }

            List<ModuleVO> lstModuleSys = userValidateBp.plainModule(sys);
            List<ModuleVO> lstModuleCur = userValidateBp.plainModule(cur);
            // lstModuleSys 肯定有 sortIdx, lstModuleCur 不一定有
            // lstModuleSys 的 sortIdx 赋给 lstModuleCur
            for (ModuleVO curModule : lstModuleCur) {
                Optional<ModuleVO> op = lstModuleSys.stream().filter(c -> StringUtility.stringEqualsIgnoreCase(c.key, curModule.key)).findFirst();
                if (op.isPresent()) {
                    curModule.sortIdx = op.get().sortIdx;
                }
            }
            // menu进行排序
            cur.menu.sort(menuVOComparator);
            List<MenuVO> lstMenu = cur.menu;
            // module进行排序
            for (MenuVO menu : lstMenu) {
                sortModule(menu.module);
            }
        } catch (Exception ex) {
            logger.error(String.format(serializeBp.obj2Json(cur)), ex);
        }
    }

    private void sortModule(List<ModuleVO> lstModule) {
        if (CollectionUtils.isEmpty(lstModule))
            return;

        lstModule.sort(moduleVOComparator);
        for (ModuleVO m : lstModule) {
            sortModule(m);
        }
    }

    private void sortModule(ModuleVO m) {
        if (m == null)
            return;

        sortModule(m.module);
    }

    Comparator<ModuleVO> moduleVOComparator = new Comparator<ModuleVO>() {
        @Override
        public int compare(ModuleVO o1, ModuleVO o2) {
            Integer idx1 = o1.sortIdx == null ? 0 : o1.sortIdx;
            Integer idx2 = o2.sortIdx == null ? 0 : o2.sortIdx;
            return idx1.compareTo(idx2);
        }
    };

    Comparator<MenuVO> menuVOComparator = new Comparator<MenuVO>() {
        @Override
        public int compare(MenuVO o1, MenuVO o2) {
            Integer idx1 = o1.sortIdx == null ? 0 : o1.sortIdx;
            Integer idx2 = o2.sortIdx == null ? 0 : o2.sortIdx;
            return idx1.compareTo(idx2);
        }
    };
}
