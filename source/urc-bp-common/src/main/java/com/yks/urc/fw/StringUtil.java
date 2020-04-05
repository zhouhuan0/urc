/*  
 * 文件名：StringUtil.java  
 * 版权：Copyright by www.youkeshu.com  
 * 描述：  操作字符串的工具类
 * 创建人：李春林  
 * 创建时间：2018年1月30日    
 * 修改理由：  
 * 修改内容：  
 */  

package com.yks.urc.fw;

import java.math.BigDecimal;

/**  
 * 操作字符串的工具类  
 * 字符串操作的工具方法集合
 * @author 李春林   
 * @version 1.0  
 * @see StringUtil  
 * @since JDK1.8 
 * @date 2018年1月30日
 */
public class StringUtil
{
    /**
     * 过滤空NULL
     * 
     * @param object Object对象
     * @return 过滤后的字符串或""
     * @see
     */
    public static String FilterNull(Object object)
    {
        return object != null && !"null".equals(object.toString()) ? object.toString().trim() : "";
    }
    
    /**
     * 是否为空
     * 
     * @param object Object对象
     * @return true表示为空，false表示非空
     * @see
     */
    public static boolean isEmpty(Object object)
    {
        if (object == null)
        {
            return true;
        }
        if ("".equals(FilterNull(object.toString())))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * 是否不为空
     * 
     * @param object Object对象
     * @return true表示不为空，false表示为空
     * @see
     */
    public static boolean isNotEmpty(Object object)
    {
        if (object == null)
        {
            return false;
        }
        if ("".equals(FilterNull(object.toString())))
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    
    /**
     * 是否可转化为数字
     * 
     * @param object Object对象
     * @return true可以转为数字，false不可转为数字
     * @see
     */
    public static boolean isNum(Object object)
    {
        try
        {
            new BigDecimal(object.toString());
            return true;
        }
        catch (Exception e)
        {
           
        }
        return false;
    }
    
    

}
