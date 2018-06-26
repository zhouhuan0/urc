/*
 * 文件名：StringFilter.java
 * 版权：Copyright by www.youkeshu.com
 * 描述：
 * 创建人：OuJie
 * 创建时间：2018年06月15日
 * 修改理由：
 * 修改内容：
 */
package com.yks.urc.motan;

import com.alibaba.fastjson.JSONObject;
import com.weibo.api.motan.common.MotanConstants;
import com.weibo.api.motan.core.extension.Activation;
import com.weibo.api.motan.core.extension.SpiMeta;
import com.weibo.api.motan.filter.Filter;
import com.weibo.api.motan.rpc.*;
import com.weibo.api.motan.serialize.DeserializableObject;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.vo.ResultVO;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author OuJie
 * @version 1.2
 * @date 2018年06月15日
 * @see StringFilter
 * @since JDK1.8
 */
@SpiMeta(name = "yks-string-filter")
@Activation(key = {MotanConstants.NODE_TYPE_REFERER,MotanConstants.NODE_TYPE_SERVICE})
public class StringFilter implements Filter {
    private final static Logger log = LoggerFactory.getLogger(StringFilter.class);
    private void decodeSession(Request request){
        try {
            Object[] arrArg = request.getArguments();
            if (arrArg != null && arrArg.length > 0) {
                if (arrArg[0] instanceof Map) {
                    Map<String, String> mapArg = (Map<String, String>) arrArg[0];
                    MotanRequest req = new MotanRequest();
                    req.setMapArg(mapArg);
                    MotanSession.setRequest(req);
                } else if (arrArg[0] instanceof String) {
                    JSONObject jo = StringUtility.parseString((String) arrArg[0]);
                    MotanRequest req = new MotanRequest();
                    req.setJSONObjectArg(jo);
                    MotanSession.setRequest(req);
                }
            }
        }
        catch(Exception ex){
            log.error(String.format("decodeSession:%s",StringUtility.toJSONString_NoException(request.getArguments())),ex);
        }
    }

    @Override
    public Response filter(Caller<?> caller, Request request) {
        decodeSession(request);

        Response response = caller.call(request);
        long startTime = System.currentTimeMillis();
        //返回值类型不是字符串对象
        boolean isStringResult = isStringResult(request);
        log.error("String序列化Filter方法isStringResult耗时："+(System.currentTimeMillis()-startTime)+" ms");
        if (!isStringResult){
            doDecorateResponse(caller,response);
        }
        log.error("String序列化Filter耗时："+(System.currentTimeMillis()-startTime)+" ms");
        return response;
    }

    private boolean isStringResult(Request request) {
        Method method;
        try {
            Class<?> clz = Class.forName(request.getInterfaceName());
            String[] paramDescs = StringUtils.split(request.getParamtersDesc(), ",");
            boolean isParamEmpty = ArrayUtils.isEmpty(paramDescs)||"void".equals(paramDescs[0]);
            Class<?>[] argTypes = new Class<?>[isParamEmpty ? 0 : paramDescs.length];
            if (!isParamEmpty){
                for (int i = 0; i < paramDescs.length; i++) {
                    argTypes[i] = Class.forName(String.valueOf(paramDescs[i]));
                }
            }
            method = BeanUtils.findMethod(clz, request.getMethodName(), argTypes);
        } catch (ClassNotFoundException e) {
            log.error("判断服务返回值类型失败",e);
            return false;
        }
        return method.getReturnType() == String.class;
    }

    private void doDecorateResponse(Caller<?> caller, Response response){
        if (!(response instanceof DefaultResponse)){
            return;
        }
        DefaultResponse resp = (DefaultResponse) response;
        if(caller instanceof Provider){
            doDecorateProvider(resp);
        }else if (caller instanceof Referer){
            doDecorateReferer(resp);
        }
    }

    /**
     * 对motan客户端获取的结果集进行反序列化
     * @param response
     */
    private void doDecorateReferer(DefaultResponse response) {
        Object value = response.getValue();
        if (value instanceof DeserializableObject){
            try {
                value = ((DeserializableObject) value).deserialize(Object.class);
            } catch (IOException e) {
                log.error("对象反序列化失败", e);
            }
        }
        response.setValue(StringUtility.parseObject(value.toString(), ResultVO.class));
    }

    /**
     * 对motan服务端返回的结果集进行序列化
     * @param response
     */
    private void doDecorateProvider(DefaultResponse response) {
        Object value = response.getValue();
        response.setValue(StringUtility.toJSONString_NoException(value));
    }
}
