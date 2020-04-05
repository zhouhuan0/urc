/*
 * 文件名：DateUtil.java
 * 版权：Copyright by www.youkeshu.com
 * 描述：  时间工具
 * 创建人：12256
 * 创建时间：2018年1月30日
 * 修改理由：
 * 修改内容：
 */

package com.yks.urc.fw;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * 时间工具类
 *
 * @author 李春林
 * @version 1.0
 * @date 2018年1月30日
 * @see DateUtil
 * @since JDK1.8
 */

public class DateUtil {

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    /**
     * 获取制定日期的格式化字符串
     *
     * @param date   Date 日期
     * @param format String 格式
     * @return String
     */
    public static String getFormatedDate(LocalDateTime date, String format) {
        return date.format(DateTimeFormatter.ofPattern(format));
    }


    /**
     * 获取指定月份的最后一天，
     *
     * @param date  Date类型
     * @param date1 String类型 yyyy-MM-dd mm:HH:ss 或 yyyy-MM-dd
     * @return Date
     * @throws ParseException
     */
    @SuppressWarnings("static-access")
    public static LocalDateTime lastDayOfMonth(LocalDateTime date, String date1) {
        return LocalDateTime.parse(date.with(TemporalAdjusters.lastDayOfMonth()).format(DateTimeFormatter.ofPattern(date1)));
    }

    /**
     * 是否是闰年
     *
     * @param year 年份
     * @return boolean
     */
    public static boolean isLeapYear(int year) {
        GregorianCalendar calendar = new GregorianCalendar();
        return calendar.isLeapYear(year);
    }

    /**
     * 获得指定日期的前一天
     *
     * @param specifiedDay 指定的日期
     * @return String
     * @throws Exception
     */
    public static String getSpecifiedDayBefore(String specifiedDay) {
        return LocalDate.parse(specifiedDay).plusDays(-1).toString();
    }


    /**
     * 获得指定日期的后一天
     *
     * @param specifiedDay 指定的日期
     * @return String
     */
    public static String getSpecifiedDayAfter(String specifiedDay) {
        return LocalDate.parse(specifiedDay).plusDays(1).toString();
    }

    /**
     * 获取指定的天 (月)
     *
     * @param specifiedDay 指定的日期
     * @return String
     */
    public static int getDay(LocalDateTime specifiedDay) {

        return specifiedDay.getDayOfMonth();
    }

    /**
     * 获取指定的月份
     *
     * @param specifiedDay 指定的日期
     * @return String
     */
    public static int getMonth(LocalDateTime specifiedDay) {
        return specifiedDay.getMonth().getValue();
    }

    /**
     * 获取指定的年份
     *
     * @param specifiedDay 指定的日期
     * @return String
     */
    public static int getYear(LocalDateTime specifiedDay) {
        return specifiedDay.getYear();
    }

    /**
     * 获取指定的小时
     *
     * @param specifiedDay 指定的日期
     * @return String
     */
    public static int getHour(LocalDateTime specifiedDay) {
        return specifiedDay.getHour();
    }


    /**
     * 获取指定的分种
     *
     * @param specifiedDay 指定的日期
     * @return String
     */
    public static int getMinute(LocalDateTime specifiedDay) {
        return specifiedDay.getMinute();
    }

    /**
     * long类型时间戳转DATE类型
     *
     * @param time
     * @return
     * @see
     */
    public static Date getDateByLongTime(long time) {
        if (new Long(time).toString().length() == 13) {
            return new Date(time);
        }

        return null;
    }

    /**
     * Date 类型 转成 LocalDateTime 类型
     *
     * @param date date
     * @return LocalDateTime
     */
    public static LocalDateTime Date2LocalDateTime(Date date) {
        if (null == date) {
            return LocalDateTime.now();
        }

        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zoneId);
    }

    /**
     * 字符串转Date
     *
     * @param dateStr 时间字符串
     * @param patten  格式
     * @return Date
     * @throws ParseException 异常
     */
    public static Date String2Date(String dateStr, String patten) throws ParseException {
        if (StringUtils.isEmpty(dateStr)) {
            return new Date();
        }

        if (StringUtils.isEmpty(patten)) {
            patten = YYYY_MM_DD_HH_MM_SS;
        }
        SimpleDateFormat smf = new SimpleDateFormat(patten);
        return smf.parse(dateStr);
    }

    public static String dateTimeZoneTrans(Date date, String sourceTimeZone, String targetTimeZone, String pattern) {
        SimpleDateFormat sourceTimeZoneSdf = new SimpleDateFormat(pattern);
        sourceTimeZoneSdf.setTimeZone(TimeZone.getTimeZone(sourceTimeZone));
        sourceTimeZoneSdf.format(date);

        SimpleDateFormat targetTimeZoneSdf = new SimpleDateFormat(pattern);
        targetTimeZoneSdf.setTimeZone(TimeZone.getTimeZone(targetTimeZone));

        return targetTimeZoneSdf.format(date);
    }

    /**
     * 获取当天的前一天日期
     *
     * @return 当天的前一天日期
     */
    public static String getYesterdayDay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(YYYY_MM_DD);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        return dateFormat.format(calendar.getTime());
    }

    /**
     * 获取上个月最后一天
     *
     * @return string yyyy-MM-dd
     */
    public static String getAfterMonthLastDay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(YYYY_MM_DD);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        return dateFormat.format(calendar.getTime());
    }

    /**
     * 获取当前时间
     *
     * @return string yyyy-MM-dd
     */
    public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(YYYY_MM_DD);
        Calendar calendar = Calendar.getInstance();
        return dateFormat.format(calendar.getTime());
    }

    /**
     * 获取当前月第一天
     *
     * @return string yyyy-MM-dd
     */
    public static String getCurrentMonthFirstDay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(YYYY_MM_DD);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return dateFormat.format(calendar.getTime());
    }

    /**
     * 获取当前月最后一天
     * return string yyyy-MM-dd
     */
    public static String getCurrentMonthLastDay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(YYYY_MM_DD);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return dateFormat.format(calendar.getTime());
    }

    /**
     * @return
     */
    public static String getCurrentWeekFirstDay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(YYYY_MM_DD);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return dateFormat.format(calendar.getTime());
    }

    /**
     * 获取本周最后一天（按中国习惯，把周日当成每周的最后一天）
     *
     * @return
     */
    public static String getCurrentWeekLastDay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(YYYY_MM_DD);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        return dateFormat.format(calendar.getTime());
    }

    public static int getCurrentMonthDays() {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    public static int getDateDay(String date) {
        return LocalDate.parse(date).getDayOfMonth();
    }

    public static int getDayOfWeek(String date) {
        return LocalDate.parse(date).getDayOfWeek().getValue();
    }

    public static String getBeforeDay(String date) {
        return LocalDate.parse(date).plusDays(-1).toString();
    }

    public static String formatDate(Date date, String formatDate) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(formatDate);
        try {
            String dateStr = sdf.format(date);
            return dateStr;
        } catch (Exception e) {
            return date.toString();
        }
    }

    /**
     * 获取明天
     * @return string yyyy-MM-dd
     */
    public static String getTomorrowDay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(YYYY_MM_DD);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        return dateFormat.format(calendar.getTime());
    }

    /**
     * 获取当前时间往前推几个月的时间
     * @param num 数量
     * @return 如果传入的num大于12 或 小于 12， 则返回当前时间
     */
    public static String getBeforeMonthsDay(int num){
        SimpleDateFormat dateFormat = new SimpleDateFormat(YYYY_MM_DD);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, num);
        return dateFormat.format(calendar.getTime());
    }
}