/**
 * 〈一句话功能简述〉<br>
 * 〈行权限〉
 *
 * @author 31076
 * @create 2018/6/5
 * @since 1.0.0
 */
package com.yks.urc.vo;

import java.io.Serializable;
import java.util.List;

public class ExpressionVO implements Serializable{
    private static final long serialVersionUID = 7054457903132702754L;
    /**
     *
     */
    public String expressionId;
    /**
     * 子级where条件
     */
    public List<ExpressionVO> subWhereClause;
    /**
     * 子级条件的关系符
     */
    public String andOr;
    /**
     *
     */
    public String filedCode;
    /**
     *
     */
    public String entity_code;
    /**
     *
     */
    public String whereClauseId;



}
