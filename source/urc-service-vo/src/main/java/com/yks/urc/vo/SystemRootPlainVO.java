/**
 * 〈一句话功能简述〉<br>
 * 〈功能权限定义相关VO〉
 *
 * @author 31076
 * @create 2018/6/5
 * @since 1.0.0
 */
package com.yks.urc.vo;

import java.io.Serializable;
import java.util.List;

/**
 * 树结构打平
 *
 * @return
 * @Author panyun@youkeshu.com
 * @Date 2020-07-22 12:02
 */
public class SystemRootPlainVO implements Serializable {
    public List<MenuVO> lstMenu;

    public List<ModuleVO> lstModule;

    public List<FunctionVO> lstFunction;
}
