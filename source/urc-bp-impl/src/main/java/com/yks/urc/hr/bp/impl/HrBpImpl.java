/*
 * 文件名：HrBpImpl.java
 * 版权：Copyright by www.youkeshu.com
 * 描述：
 * 创建人：zhouhuan
 * 创建时间：2020/11/27
 * 修改理由：
 * 修改内容：
 */
package com.yks.urc.hr.bp.impl;

import com.alibaba.fastjson.JSONObject;
import com.yks.urc.config.bp.api.IConfigBp;
import com.yks.urc.enums.CommonMessageCodeEnum;
import com.yks.urc.exception.URCBizException;
import com.yks.urc.fw.HttpUtility;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.hr.bp.api.IHrBp;
import com.yks.urc.mapper.IRoleMapper;
import com.yks.urc.user.bp.impl.UserBpImpl;
import com.yks.urc.vo.PositionVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zhouhuan
 * @version 1.0
 * @date 2020/11/27
 * @see HrBpImpl
 * @since JDK1.8
 */
@Component
public class HrBpImpl implements IHrBp {
    private static Logger logger = LoggerFactory.getLogger(HrBpImpl.class);

    @Autowired
    private IConfigBp configBp;
    @Autowired
    private IRoleMapper roleMapper;
    @Override
    public void positionSync() throws Exception {
        String url = configBp.getString("GET_POSITION_URL", "http://ykshr.kokoerp.com/api/Position/getAllList");
        String sendPost = HttpUtility.sendPost(url, null);
        logger.info("request position response : {}",sendPost);
        if (StringUtility.isNullOrEmpty(sendPost)) {
            logger.error(String.format("同步岗位失败, url为: %s ", url));
            return;
        }

        JSONObject jsonObject = StringUtility.parseString(sendPost);
        if(!StringUtility.stringEqualsIgnoreCase(jsonObject.getString("state"),CommonMessageCodeEnum.SUCCESS.getCode())){
            return;
        }

        List<PositionVO> list = StringUtility.jsonToList(jsonObject.getString("list"), PositionVO.class);
        for (PositionVO positionVO : list) {

        }
        //保存岗位信息
        //roleMapper.insertOrUpdate()
    }


}
