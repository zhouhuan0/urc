/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author linwanxian@youkeshu.com
 * @create 2018/6/14
 * @since 1.0.0
 */
package com.yks.urc.dataauthorization.bp.api;

import com.yks.urc.vo.OmsAccountVO;
import com.yks.urc.vo.OmsPlatformVO;

import java.util.List;

public interface DataAuthorization {

    List<OmsPlatformVO> getPlatformList(String operator);

    List<OmsAccountVO> getShopList(String operator, String platform);

}
