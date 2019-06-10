/**
 * 〈一句话功能简述〉<br>
 * 〈日志记录〉
 *
 * @author lwx
 * @create 2018/11/26
 * @since 1.0.0
 */
package com.yks.urc.user.bp.api;

import java.util.List;
import java.util.Map;

import com.yks.urc.entity.UrcLog;
import com.yks.urc.vo.LogListReqVo;

/**
 * @Description: 用户系统操作日志
 * @author: zengzheng 
 * @version: 2019年6月4日 上午9:36:39
 */
public interface IUrcLogBp {

    /**
     * @Description: 插入系统操作日志
     * @author: zengzheng
     * @param logDO
     * @version: 2019年6月4日 上午9:36:53
     */
    void insertUrcLog(UrcLog urcLog);
    
    /**
     * @Description: 通过条件查询日志
     * @author: zengzheng
     * @param conditionsMap
     * @return
     * @version: 2019年6月4日 上午9:38:58
     */
    List<UrcLog> selectUrcLogByConditions(LogListReqVo logListReqVo);
}
