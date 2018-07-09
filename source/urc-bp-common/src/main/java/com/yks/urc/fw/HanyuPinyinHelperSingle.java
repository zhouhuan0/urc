/**
 * 〈一句话功能简述〉<br>
 * 〈汉字转拼音〉
 *
 * @author linwanxian@youkeshu.com
 * @create 2018/7/9
 * @since 1.0.0
 */
package com.yks.urc.fw;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.Date;

public class HanyuPinyinHelperSingle {

    private static  volatile  HanyuPinyinHelperSingle helperSingle =null;

    public HanyuPinyinHelperSingle() {
    }

    public static synchronized  HanyuPinyinHelperSingle getHelperSingle(){
        if (helperSingle ==null){
            synchronized (HanyuPinyinHelperSingle.class){
                if (helperSingle ==null){
                    helperSingle =new HanyuPinyinHelperSingle();
                }
            }
        }
        return helperSingle;
    }
    /**
     * 将文字转为汉语拼音
     * @param ChineseLanguage 要转成拼音的中文
     */
    public String toHanyuPinyin(String ChineseLanguage){
        char[] cl_chars = ChineseLanguage.trim().toCharArray();
        String hanyupinyin = "";
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);// 输出拼音全部小写
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);// 不带声调
        defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_V) ;
        try {
            for (int i=0; i<cl_chars.length; i++){
                if (String.valueOf(cl_chars[i]).matches("[\u4e00-\u9fa5]+")){// 如果字符是中文,则将中文转为汉语拼音
                    hanyupinyin += PinyinHelper.toHanyuPinyinStringArray(cl_chars[i], defaultFormat)[0];
                } else {// 如果字符不是中文,则不转换
                    hanyupinyin += cl_chars[i];
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            System.out.println("字符不能转成汉语拼音");
        }
        return hanyupinyin;
    }

    public static String getFirstLettersUp(String ChineseLanguage){
        return getFirstLetters(ChineseLanguage ,HanyuPinyinCaseType.UPPERCASE);
    }

    public static String getFirstLettersLo(String ChineseLanguage){
        return getFirstLetters(ChineseLanguage ,HanyuPinyinCaseType.LOWERCASE);
    }

    public static String getFirstLetters(String ChineseLanguage,HanyuPinyinCaseType caseType) {
        char[] cl_chars = ChineseLanguage.trim().toCharArray();
        String hanyupinyin = "";
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(caseType);// 输出拼音全部大写
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);// 不带声调
        try {
            for (int i = 0; i < cl_chars.length; i++) {
                String str = String.valueOf(cl_chars[i]);
                if (str.matches("[\u4e00-\u9fa5]+")) {// 如果字符是中文,则将中文转为汉语拼音,并取第一个字母
                    hanyupinyin += PinyinHelper.toHanyuPinyinStringArray(cl_chars[i], defaultFormat)[0].substring(0, 1);
                } else if (str.matches("[0-9]+")) {// 如果字符是数字,取数字
                    hanyupinyin += cl_chars[i];
                } else if (str.matches("[a-zA-Z]+")) {// 如果字符是字母,取字母
                    hanyupinyin += cl_chars[i];
                } else {// 否则不转换
                    hanyupinyin += cl_chars[i];//如果是标点符号的话，带着
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            System.out.println("字符不能转成汉语拼音");
        }
        return hanyupinyin;
    }

    public static String getPinyinString(String ChineseLanguage){
        char[] cl_chars = ChineseLanguage.trim().toCharArray();
        String hanyupinyin = "";
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);// 输出拼音全部大写
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);// 不带声调
        try {
            for (int i = 0; i < cl_chars.length; i++) {
                String str = String.valueOf(cl_chars[i]);
                if (str.matches("[\u4e00-\u9fa5]+")) {// 如果字符是中文,则将中文转为汉语拼音,并取第一个字母
                    hanyupinyin += PinyinHelper.toHanyuPinyinStringArray(
                            cl_chars[i], defaultFormat)[0];
                } else if (str.matches("[0-9]+")) {// 如果字符是数字,取数字
                    hanyupinyin += cl_chars[i];
                } else if (str.matches("[a-zA-Z]+")) {// 如果字符是字母,取字母

                    hanyupinyin += cl_chars[i];
                } else {// 否则不转换
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            System.out.println("字符不能转成汉语拼音");
        }
        return hanyupinyin;
    }
    /**
     * 取第一个汉字的第一个字符
     * @Title: getFirstLetter
     * @Description: TODO
     * @return String
     * @throws
     */
    public static String getFirstLetter(String ChineseLanguage){
        char[] cl_chars = ChineseLanguage.trim().toCharArray();
        String hanyupinyin = "";
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);// 输出拼音全部大写
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);// 不带声调
        try {
            String str = String.valueOf(cl_chars[0]);
            if (str.matches("[\u4e00-\u9fa5]+")) {// 如果字符是中文,则将中文转为汉语拼音,并取第一个字母
                hanyupinyin = PinyinHelper.toHanyuPinyinStringArray(
                        cl_chars[0], defaultFormat)[0].substring(0, 1);;
            } else if (str.matches("[0-9]+")) {// 如果字符是数字,取数字
                hanyupinyin += cl_chars[0];
            } else if (str.matches("[a-zA-Z]+")) {// 如果字符是字母,取字母

                hanyupinyin += cl_chars[0];
            } else {// 否则不转换

            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            System.out.println("字符不能转成汉语拼音");
        }
        return hanyupinyin;
    }

    public static void main(String[] args) {
        String s ="百度重新捡起了“轻应用”，正式推出智能小程序了。\n" +
                "\n" +
                "其实在此前，百度智能小程序有过一些铺垫。既低调上线了“优信二手车”小程序测试、百度智能小程序在 5 月百度联盟峰会上第一次提及——百度 app 业务部总经理平晓黎表示，百度将于今年 7 月正式推出百度智能小程序后。7 月 4 日，在北京国家会议中心的“百度 AI 开发者大会”上，百度智能小程序才正式推出。\n" +
                "\n" +
                "如今智能小程序业务处于百度 Feed、信息流的主航道业务之下，是比如今百度 AI 体系之下的智能驾驶阿波罗计划和语音技术 DuerOS 更贴近商业化的业务之一。2013 年，百度就曾推出类似的“轻应用”，作为公司的主要战略之一，后来因为开发者的冷落以及轻应用流量入口的逐渐缩窄，这个战略不了了之。\n" +
                "\n" +
                "百度副总裁、百度 APP & 信息流业务体系负责人沈抖是如今百度集团业务的“当红炸子鸡”。沈抖，2012 年加入百度，2017 年之前，曾担任百度公司联盟研发部总监、网页搜索部高级总监、金融服务事业群组（FSG）执行总监。《财经》杂志曾在《李彦宏领兵百度信息流内幕》中披露，2017 年，沈抖晋升为百度公司副总裁，全面负责百度 APP、信息流、好看视频、百家号、百度新闻、百度浏览器、hao123 等移动相关业务。沈抖还可跳过搜索公司总裁向海龙直接向陆奇汇报，而具体的业务，李彦宏也会亲自过问。\n" +
                "\n" +
                "在陆奇时期的百度，百度主航道业务百度信息流就已经由沈抖全面负责。\n" +
                "\n" +
                "今年 1 月，应用“手机百度”也直接更名为“百度”，PingWest品玩（）打开“百度”应用，底部主要功能栏则是：信息流、好看视频、语音搜索、好听、以及个人功能，小程序位于个人功能下。百度开始围绕移动端应用进行全盘的业务布局。\n" +
                "\n" +
                "\n" +
                "7 月 4 日，在百度 CEO 李彦宏宣布百度无人车量产和发布国内首款人工智能芯片，百度高级副总裁、AIG 负责人王海峰博士讲解重要业务百度大脑 3.0 之后，百度副总裁、百度 APP & 信息流业务体系负责人沈抖上台，发布百度智能小程序，沈抖表示， 百度智能小程序不仅可以全面接入百度大脑的AI能力，更将在今年 12 月全面开源，为用户和开发者打造一个体验更佳的开放、智能化移动生态。\n" +
                "\n" +
                "包括携程、苏宁易购、唯品会、同程、春雨医生、爱奇艺、优信二手车、查违章等在内的近百家企业成为首批加入智能小程序生态的合作伙伴，并将陆续推出各自的智能小程序。将为开发者提供更多封装好的 AI 应用。\n" +
                "\n" +
                "百度智能小程序是基于百度 app 流量的延续\n" +
                "\n" +
                "沈抖在今天的发布会上表示，百度智能小程序全面接入百度大脑 3.0，开发者几行代码就可以调用，同时，智能小程序将于今年 12 月全面开源，未来智能小程序不仅可以运行于百度系 App 上，还可以在第三方应用上运行。\n" +
                "\n" +
                "百度智能小程序，其实就是基于百度 app 流量的延续。\n" +
                "\n" +
                "谈到百度智能小程序的入口时，沈抖的观点和微信张小龙颇为类似。沈抖认为“把小程序想象成对用户需求的满足，小程序不一定非要有一个明确的入口，而是希望在用户使用百度想完成一个服务时，能够非常自然的进入到小程序中。”\n" +
                "\n" +
                "比如用户在百度搜索的过程中，植入一个百度智能程序，作为一种信息需求的延续。用户搜索电影，可能就会弹出一个购买电影票的小程序。\n" +
                "\n" +
                "沈抖认为，原来的 PC 搜索很少有讨论社交的需求，现在的信息流也已经有一些基于兴趣、信息点的讨论，是一种基于流量功能的延续。基于信息流，也可以建立其一些弱社交的需求。\n" +
                "\n" +
                "“PC 时代你很难把一项服务无缝的接入到互联网体系中，现在在智能小程序上可以很好的实现。”\n" +
                "\n" +
                "沈抖同时提到，百度搜索集结了大量的搜索需求，是信息获取的入口，不缺流量。而据互联网报告，截至 2018 年 3 月，百度 App 日活达到 1.37 亿。\n" +
                "\n" +
                "是基于 AI 平台技术的延续\n" +
                "\n" +
                "百度智能小程序的特点是智能和开放性。通过连接百度大脑 3.0，开发者可以使用百度 AI 技术平台，用几行代码就能调用所需要的AI能力，例如使用百度的视觉技术、AI 识图、AR 相机技术等，从语音、视觉、智能交互、知识图谱等底层技术，以此来打造自己的小程序，让百度小程序具备百度的 AI 技术基础。\n" +
                "\n" +
                "百度智能小程序的能力，同样是其 AI 技术底层平台的延续。\n" +
                "\n" +
                "百度介绍，得益于百度的 AI 技术，百度智能小程序还可以基于大数据、意图识别、兴趣识别等技术精准的找到各类智能小程序用户，有效缩短用户转化途径，为开发者和合作伙伴带来实实在在的商业效益。\n" +
                "\n" +
                "沈抖在发布会上提到，接入百度智能小程序后，“查违章智能小程序”全网的 DAU 在 50 天内增长了 370%，火车票智能小程序订单转化率在 20 天内提升了 44%。同时百度贴吧小程序上线以来，用户人均使用时长也增加了 30% 以上。\n" +
                "\n" +
                "沈抖强调：“如今，AI 已不再是‘锦上添花’，而是必备的技能。百度将开放 AI 能力，让开发者重回业务理解与创意的赛道，专注于自己的业务逻辑。”\n" +
                "\n" +
                "关于技术体系化的建设，百度智能生活事业群组景鲲提到了类似的观点，“百度更关注平台是因为它是一个可延伸的体验。原来的移动的屏幕体验是一样，我们相信在对话上体验应该是一样的。音箱，只是百度某个赛道的上举措，我们希望这种技术能力在平台不止可以输出到音箱上、汽车上。”\n" +
                "\n" +
                "不同于微信，可嫁接在自有生态体系之外\n" +
                "\n" +
                "更早时间，沈抖在发布手机百度 10.0 版本时提到——互联网信息量太大、信息服务还不够好。而百度有能力理解用户、有能力为用户提供更好的信息体验。将帮助百度有能力为用户随时随地提供信息服务。\n" +
                "\n" +
                "沈抖今天提出智能小程序生态的一大缘由是因为互联网的服务是割裂的。“一个个割裂的应用，让互联网流量无法互通有无。”同时，流量只是集中在几个渠道上是不够的，百度智能小程序希望开发者可以使用百度技术开放的代码，让百度智能小程序跑在互联网生态中。\n" +
                "\n" +
                "沈抖在今天百度 AI 开发者大会上提到，用户在百度 APP 上可以通过搜索和信息流，借助小程序的能力与第三方能力无缝连接。\n" +
                "\n" +
                "而不同于微信小程序，百度智能小程序可嫁接在自有生态体系之外，其区别最大体现在开放上。沈抖提到，“如果微信愿意开源，我就不做百度智能小程序了。”\n" +
                "\n" +
                "\n" +
                "百度提到，智能小程序是业界首个开放的小程序生态，其开放性体现在两个层面。\n" +
                "\n" +
                "首先，开发者只要简单修改几行代码，就可以将自己在其他平台开发的小程序接入百度智能小程序，进而让这个智能小程序无缝运行在百度系App（百度App、百度贴吧、百度网盘等）以及外部App（哔哩哔哩、58同城等）上，实现一端开发，多端可运行。比如开发者开发的百度智能机票小程序可能会在58同城中调用。其次，百度还将开放全域千亿流量扶持开发者，帮助他们快速沉淀精准用户。值得注意的是，百度流量与其他平台不同的是，百度流量中天然存在大量与资讯、服务、工具等相关的需求，尤其是在百度信息流中，用户看到的内容都是根据兴趣匹配推荐的，这些内容可以激发用户各类需求，非常适合开发者去挖掘。\n" +
                "\n" +
                "在移动互联网时代，开发者更关心流量。微信小程序、支付宝小程序以及百度智能小程序，无疑都在提及流量赋能的概念。\n" +
                "\n" +
                "一定程度上，百度通过智能小程序提升了用户搜索的体验，也是一种基于用户搜索信息拓展功能。同时，基于小程序的流量转化，百度可以将商业需求嫁接在第三方植入的小程序上，打开了新的流量入口和出口。\n" +
                "\n" +
                "沈抖认为，PC 互联网时代，大家都在一个容器里。移动互联网时代变了，用户需要进入一个 app，然后离开这个 app，去往另一个 app，体验割裂。\n" +
                "\n" +
                "现在百度希望做一个大的容器，在移动互联网的小程序时代，继续停在百度技术的生态里。";
       HanyuPinyinHelperSingle hanyuPinyinHelperSingle =HanyuPinyinHelperSingle.getHelperSingle();
        System.out.println("startTime:" + StringUtility.dt2Str(new Date(),"yyy-MM-dd HH:mm:sss"));
        System.out.println(hanyuPinyinHelperSingle.toHanyuPinyin(s));
        System.out.println("endTime:" + StringUtility.dt2Str(new Date(),"yyy-MM-dd HH:mm:sss"));
    }
}
