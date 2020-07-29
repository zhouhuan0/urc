package com.yks.urc.sellerid.bp.api;

import com.yks.urc.vo.PlatformAccount4Third;

public interface ISysDataruleContext {
    String getSysKey();

    String getEntityCode();

    String getFieldCode();

    String getQueryEntityCode();

    String getPlatformId(PlatformAccount4Third t);
}
