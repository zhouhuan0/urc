/**
 * 〈一句话功能简述〉<br>
 * 〈分页结果〉
 *
 * @author 31076
 * @create 2018/6/5
 * @since 1.0.0
 */
package com.yks.urc.vo;

import java.io.Serializable;
import java.util.List;

public class PageResultVO implements Serializable {
    private static final long serialVersionUID = 199412161535815664L;
    /**
     *
     */
    public List<?> lst;
    /**
     *
     */
    public int totalPage;
    /**
     *
     */
    public int pageSize;
    /**
     * 总条数
     */
    public long total;
}
