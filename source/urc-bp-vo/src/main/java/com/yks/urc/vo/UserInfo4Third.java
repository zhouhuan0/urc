package com.yks.urc.vo;

import java.util.List;

/**
 * @ProjectName: actmgr
 * @Package: com.yks.actmgr.third
 * @ClassName: UserInfo4Third
 * @Author: zengzheng
 * @Description:
 * @Date: 2020/7/28 19:22
 * @Version: 1.0
 */
public class UserInfo4Third {
    private String userName;

    private List<PlatformAccount4Third> platformAccount4ThirdList;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<PlatformAccount4Third> getPlatformAccount4ThirdList() {
        return platformAccount4ThirdList;
    }

    public void setPlatformAccount4ThirdList(List<PlatformAccount4Third> platformAccount4ThirdList) {
        this.platformAccount4ThirdList = platformAccount4ThirdList;
    }
}
