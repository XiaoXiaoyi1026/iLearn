package com.ilearn.base.utils;


import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 日期处理
 */
public class DateUtil {

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static final String YYYYMMDD = "yyyyMMdd";

    public static final String HHmmss = "HHmmss";

    public static final String YYYYMM = "yyyyMM";

    private DateUtil() {
    }

    public static @NotNull String toDateTime(LocalDateTime date) {
        return toDateTime(date, YYYY_MM_DD_HH_MM_SS);
    }

    public static @NotNull String toDateTime(@NotNull LocalDateTime dateTime, String pattern) {
        return dateTime.format(DateTimeFormatter.ofPattern(pattern, Locale.SIMPLIFIED_CHINESE));
    }


    public static String toDateText(LocalDate date, String pattern) {
        if (date == null || pattern == null) {
            return null;
        }
        return date.format(DateTimeFormatter.ofPattern(pattern, Locale.SIMPLIFIED_CHINESE));
    }

    /**
     * 从给定的date，加上hour小时 求指定date时间后hour小时的时间
     *
     * @param date 指定的时间
     * @param hour 多少小时后
     * @return 时间
     */
    public static @NotNull Date addExtraHour(Date date, int hour) {
        Calendar cal = Calendar.getInstance();
        if (date != null) {
            cal.setTime(date);
        }
        cal.add(Calendar.HOUR_OF_DAY, hour);
        return cal.getTime();
    }

    /**
     * 从给定的date，加上increase天
     *
     * @param date     日期
     * @param increase 增加的天数
     * @return 增加完后的日期
     */
    public static @NotNull Date increaseDay2Date(Date date, int increase) {
        Calendar cal = Calendar.getInstance();
        if (date != null) {
            cal.setTime(date);
        }
        cal.add(Calendar.DAY_OF_MONTH, increase);
        return cal.getTime();
    }

    /**
     * 从给定的LocalDateTime，加上increase月
     *
     * @param date     日期
     * @param increase 增加的月数
     * @return 增加后的日期
     */
    public static @NotNull LocalDateTime localDateTimeAddMonth(@NotNull LocalDateTime date, int increase) {
        // LocalDateTime --> Date
        Date temp = Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
        // 日期加
        Calendar cal = Calendar.getInstance();
        cal.setTime(temp);
        cal.add(Calendar.MONTH, increase);
        // LocalDateTime <-- Date
        return LocalDateTime.ofInstant(cal.getTime().toInstant(), ZoneId.systemDefault());
    }


    /**
     * 把字符串日期默认转换为yyyy-mm-dd格式的Data对象
     *
     * @param strDate 日期字符串
     * @return 转换后的日期对象
     */
    public static Date format(String strDate, String format) {
        Date d;
        if (null == strDate || "".equals(strDate))
            return null;
        else
            try {
                d = getFormatter(format).parse(strDate);
            } catch (ParseException pex) {
                return null;
            }
        return d;
    }

    /**
     * 获取一个简单的日期格式化对象
     *
     * @return 一个简单的日期格式化对象
     */
    @Contract("_ -> new")
    private static @NotNull SimpleDateFormat getFormatter(String pattern) {
        return new SimpleDateFormat(pattern);
    }

    /**
     * 获取month所在月的所有天
     *
     * @param month      要查询的日期（如果为null 则默认为当前月）
     * @param dateFormat 返回日期的格式（如果为null 则返回yyyy-MM-dd 格式结果）
     * @return 所有天
     */
    public static @NotNull List<String> getAllDaysOfMonthInString(Date month, DateFormat dateFormat) {
        List<String> rs = new ArrayList<>();
        DateFormat df = null;
        if (null == dateFormat) {
            df = new SimpleDateFormat("yyyy-MM-dd");
        }
        Calendar cad = Calendar.getInstance();
        if (null != month) {
            cad.setTime(month);
        }
        int day_month = cad.getActualMaximum(Calendar.DAY_OF_MONTH); // 获取当月天数
        for (int i = 0; i < day_month; i++) {
            cad.set(Calendar.DAY_OF_MONTH, i + 1);
            assert df != null;
            rs.add(df.format(cad.getTime()));

        }
        return rs;
    }

    /**
     * 获取month所在月的所有天
     *
     * @param month 要查询的日期（如果为null 则默认为当前月）
     * @return 日期List
     */
    public static @NotNull List<Date> getAllDaysOfMonth(Date month) {
        List<Date> rs = new ArrayList<>();
        Calendar cad = Calendar.getInstance();
        if (null != month) {
            cad.setTime(month);
        }
        int day_month = cad.getActualMaximum(Calendar.DAY_OF_MONTH); // 获取当月天数
        for (int i = 0; i < day_month; i++) {
            cad.set(Calendar.DAY_OF_MONTH, i + 1);
            rs.add(cad.getTime());

        }
        return rs;
    }

    /**
     * 获取指定日期区间所有天
     *
     * @param begin      开始
     * @param end        结束
     * @param dateFormat (如果为null 则返回yyyy-MM-dd格式的日期)
     * @return 区间内所有天
     */
    public static @NotNull List<String> getSpecifyDaysOfMonthInString(Date begin, Date end, DateFormat dateFormat) {
        DateFormat df = null;
        if (null == dateFormat) {
            df = new SimpleDateFormat("yyyy-MM-dd");
        }
        List<String> rs = new ArrayList<>();
        List<Date> tmplist = getSpecifyDaysOfMonth(begin, end);
        for (Date date : tmplist) {
            assert df != null;
            rs.add(df.format(date));
        }
        return rs;
    }

    /**
     * 获取指定日期区间所有天
     *
     * @param begin 开始
     * @param end   结束
     * @return 所有天
     */
    public static @NotNull List<Date> getSpecifyDaysOfMonth(Date begin, Date end) {
        List<Date> rs = new ArrayList<>();
        Calendar cad = Calendar.getInstance();
        int day_month;
        // 设置开始日期为指定日期
        if (null == begin) {
            // day_month = cad.getActualMaximum(Calendar.DAY_OF_MONTH); // 获取当月天数
            cad.set(Calendar.DAY_OF_MONTH, 1);// 设置开始日期为当前月的第一天
            begin = cad.getTime();
        }
        cad.setTime(begin);
        if (null == end) {// 如果结束日期为空 ，设置结束日期为下月的第一天
            day_month = cad.getActualMaximum(Calendar.DAY_OF_MONTH); // 获取当月天数
            cad.set(Calendar.DAY_OF_MONTH, day_month + 1);
            end = cad.getTime();
        }
        cad.set(Calendar.DAY_OF_MONTH, 1);// 设置开始日期为当前月的第一天
        Date tmp;
        int i = 1;
        while (true) {
            cad.set(Calendar.DAY_OF_MONTH, i);
            i++;
            tmp = cad.getTime();
            if (tmp.before(end)) {
                rs.add(cad.getTime());
            } else {
                break;
            }
        }
        return rs;
    }

    /**
     * 获取当前日期
     *
     * @return 一个包含年月日的<code>Date</code>型日期
     */
    public static synchronized @NotNull Date getCurrDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    public static @NotNull String format(Date date, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }

    /**
     * 获取当前完整时间,样式: yyyy－MM－dd hh:mm:ss
     *
     * @return 一个包含年月日时分秒的<code>String</code>型日期。yyyy-MM-dd hh:mm:ss
     */
    public static @NotNull String getCurrDateTimeStr() {
        return format(getCurrDate(), YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 获得指定日期的前一天
     *
     * @param specifiedDay YYYY_MM_DD_HH_MM_SS 格式
     * @param formatStr    日期类型
     * @return 前一天
     */
    public static @NotNull String getSpecifiedDayBefore(String specifiedDay, String formatStr) {// 可以用new
        // Date().toLocalString()传递参数
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS).parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert date != null;
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day - 1);

        return new SimpleDateFormat(formatStr).format(c.getTime());
    }

    /**
     * 获得指定日期的后一天
     *
     * @param specifiedDay YYYY_MM_DD_HH_MM_SS 格式
     * @param formatStr    日期类型
     * @return 最后一天
     */
    public static @NotNull String getSpecifiedDayAfter(String specifiedDay, String formatStr) {
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS).parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert date != null;
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day + 1);

        return new SimpleDateFormat(formatStr).format(c.getTime());
    }

    /**
     * 获取本周第一天的日期
     *
     * @return 本周第一天
     */
    public static @NotNull String getWeekFirstDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        int day_of_week = cal.get(Calendar.DAY_OF_WEEK) - 2;
        cal.add(Calendar.DATE, -day_of_week);
        return sdf.format(cal.getTime());
    }

    /**
     * 获取当前月的第一天
     *
     * @return 当月第一天
     */
    public static @NotNull String getCurrentMonthFirstDay() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 当前月的第一天
        cal.set(GregorianCalendar.DAY_OF_MONTH, 1);
        Date beginTime = cal.getTime();
        return sdf.format(beginTime);
    }

    /**
     * 获取昨天开始时间
     *
     * @return 昨天开始时间
     */
    public static @NotNull String getYesterdayStart() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(cal.getTime());
    }

    public static @NotNull String getYesterdayEnd() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(cal.getTime()) + " 23:59:59";
    }

    public static @NotNull String getCurrDayStart() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(cal.getTime());
    }

    /**
     * 功能：获取指定月份的第一天<br/>
     */
    public static @NotNull String getStartDayWithMonth(String month) throws ParseException {
        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat mf = new SimpleDateFormat("yyyy-MM");
        Date date = mf.parse(month);
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 0);// 因为格式化时默认了DATE为本月第一天所以此处为0
        return sdf.format(calendar.getTime());
    }

    /**
     * 功能：获取指定月份的最后一天<br/>
     */
    public static @NotNull String getEndDayWithMonth(String month) throws ParseException {
        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat mf = new SimpleDateFormat("yyyy-MM");
        Date date = mf.parse(month);
        calendar.setTime(date);
        calendar.roll(Calendar.DATE, -1);// api解释roll()：向指定日历字段添加指定（有符号的）时间量，不更改更大的字段
        return sdf.format(calendar.getTime());
    }

    public static @NotNull String formatYearMonthDay(String dateStr) throws ParseException {
        if (StringUtils.isNotBlank(dateStr)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(dateStr);
            return sdf.format(date);
        } else {
            return "";
        }
    }

    /**
     * 功能：<br/>
     * 根据时间 yyyy-MM-dd 获取该日期是本月第几周
     */
    public static int getWeekIndexOfMonth(String dateStr) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(dateStr);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.WEEK_OF_MONTH);
    }

    /**
     * 获取当前时间到指定时间距离多少秒 功能：<br/>
     */
    public static int getSecondToDesignationTime(String designationTime) {
        // 24小时制
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date toDate;
        try {
            toDate = dateFormat.parse(designationTime);
            return (int) ((toDate.getTime() - dateFormat.parse(DateUtil.getCurrDateTimeStr()).getTime()) / 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getYear() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR);
    }

    public static int getMonth() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.MONTH) + 1;
    }

    public static int getDay() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.DATE);
    }

    /**
     * 通过时间秒毫秒数判断两个时间的间隔
     *
     * @param start 开始
     * @param end   结束
     * @return 间隔
     */
    public static int differentDaysByMillisecond(@NotNull LocalDateTime start, @NotNull LocalDateTime end) {
        // ZoneOffset.of("+8") 是指定为东8区
        return (int) ((end.toInstant(ZoneOffset.of("+8")).toEpochMilli() - start.toInstant(ZoneOffset.of("+8")).toEpochMilli()) / (1000 * 3600 * 24));
    }
}
