/*
 * 文件名：PlatFormEnum.java
 * 版权：Copyright by www.youkeshu.com
 * 描述：
 * 创建人：zhouhuan
 * 创建时间：2020/10/14
 * 修改理由：
 * 修改内容：
 */
package com.yks.urc.Enum;

/**
 * @author zhouhuan
 * @version 1.0
 * @date 2020/10/14
 * @see PlatFormEnum
 * @since JDK1.8
 */
public enum  PlatFormEnum {
    SF("SF","shopify"),
    XP("XP","xshoppy"),
    SY("SY","shopyy"),
    EB("EB","eBay"),
    SE("SE","SHOPEE"),
    WH("WH","WISH"),
    MM("MM","Mymall"),
    YA("YA","亚马逊"),
    SM("SM","速卖通"),
    LZ("LZ","LAZADA"),
    LN("LN","LINIO"),
    JM("JM","Jumia"),
    JO("JO","JOOM"),
    WM("WM","Walmart"),
    DA("DA","daraz"),
    MA("MA","ml"),
    RT("RT","乐天"),
    NE("NE","Newegg"),
    CD("CD","CDISCOUNT"),
    RD("RD","real.de"),
    BW("BW","b2w"),
    OS("OS","Overstock"),
    DY("DY","抖音"),
    CP("CP","coupang"),
    FN("FN","fnac"),
    BM("BM","bigcommerce"),
    PM("PM","PriceMinister");


    private String platCode;

    private String plat;

    public String getPlatCode() {
        return platCode;
    }

    public void setPlatCode(String platCode) {
        this.platCode = platCode;
    }

    public String getPlat() {
        return plat;
    }

    public void setPlat(String plat) {
        this.plat = plat;
    }

    PlatFormEnum(String platCode, String plat){
        this.platCode = platCode;
        this.plat = plat;
    }

    public static String getPlat(String platCode){
        for(PlatFormEnum platFormEnum:PlatFormEnum.values()){
            if(platFormEnum.getPlatCode().equals(platCode)){
                return platFormEnum.getPlat();
            }
        }
        return platCode;
    }
}
