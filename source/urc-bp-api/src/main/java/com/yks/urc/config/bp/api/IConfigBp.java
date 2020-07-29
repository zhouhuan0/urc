package com.yks.urc.config.bp.api;

import com.yks.urc.vo.ResultVO;

public interface IConfigBp {
    String getString(String key);

    String getString(String key, String defaultValue);

    /**
     * 默认返回false
     * @return
     * @Author panyun@youkeshu.com
     * @Date 2019-08-18 08:39
     */
    Boolean getBoolean(String key);

    String getEnv();

    String get(String key);

    Long getLong(String key);

    /**
     * 发mq通知配置改变
     *
     * @return
     * @Author panyun@youkeshu.com
     * @Date 2019/5/28 8:41
     */
    void publishConfigUpdate();

    String getStringFromDb(String key);
    String getStringFromDb(String key, String defaultValue);

    void update2Db(String key, String value);

    ResultVO updateConfig(String json);
}
