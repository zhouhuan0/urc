package com.yks.urc.service.api;

import com.yks.urc.vo.ResultVO;

/**
 * @author zhouhuan
 * @version 1.0
 * @date 2020/12/1
 * @see ISystemService
 * @since JDK1.8
 */
public interface ISystemService {
    //获取系统下拉框列表
    ResultVO getSystemList();

    //获取系统的功能权限
    ResultVO getSystemPermission(String jsonStr);

    //系统信息更新
    ResultVO updateSystemInfo(String jsonStr);

    //获取系统信息列表
    ResultVO getSystemInfoList(String jsonStr);
}
