/*
 * 文件名：IHrBp.java
 * 版权：Copyright by www.youkeshu.com
 * 描述：
 * 创建人：zhouhuan
 * 创建时间：2020/11/27
 * 修改理由：
 * 修改内容：
 */
package com.yks.urc.hr.bp.api;

/**
 * @author zhouhuan
 * @version 1.0
 * @date 2020/11/27
 * @see IHrBp
 * @since JDK1.8
 */
public interface IHrBp {
    /**
     * 岗位信息同步(从人事系统同步)
     */
   void  positionSync() throws Exception;

    /**
     * 异步执行拉取岗位信息
     */
   void asynPullPosition();
}
