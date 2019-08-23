package com.juma.tgm.gateway.waybill.controller.util;

import com.alibaba.fastjson.JSONArray;
import com.giants.common.exception.BusinessException;
import com.juma.tgm.common.Constants;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * @ClassName: WaybillDateUtil
 * @Description:
 * @author: liang
 * @date: 2017-08-23 14:49
 * @Copyright: 2017 www.jumapeisong.com Inc. All rights reserved.
 */
@Component
public class WaybillQueryUtil {

    private static final Logger logger = LoggerFactory.getLogger(WaybillQueryUtil.class);


    /**
     * 处理yyyy-MM-dd格式的日期
     *
     * @param param
     * @param queryDate
     */
    public void parseYMD(Map<String, Object> param, String queryDate) {
        Date date;
        String startTime;
        String endTime;
        try {
            date = DateUtils.parseDate(queryDate, new String[]{"yyyy-MM-dd"});

            Date tempStart = DateUtils.setSeconds(DateUtils.setMinutes(DateUtils.setHours(date, 0), 0), 0);
            startTime = Constants.YYYYMMDDHHMMSS.format(tempStart);
            param.put("startTime", startTime);

            Date tempEnd = DateUtils.setSeconds(DateUtils.setMinutes(DateUtils.setHours(date, 23), 59), 59);
            endTime = Constants.YYYYMMDDHHMMSS.format(tempEnd);
            param.put("endTime", endTime);
        } catch (Exception e) {
            throw new BusinessException("start or end time ","errors.date","时间范围");
        }
    }

    /**
     * 处理yyyy-MM-dd格式的日期
     *
     * @param queryDate
     *
     * @param isStart 是否为一天的开始 如果不是则认为是一天的结尾
     */
    public String parseYMD(String queryDate , Boolean isStart) {
        Date date  = null;
        try {
            date = DateUtils.parseDate(queryDate, new String[]{"yyyy-MM-dd"});
        } catch (ParseException e) {
            throw new BusinessException("start or end time ","errors.date","时间范围");
        }
        if( isStart ) {
            Date tempStart = DateUtils.setSeconds(DateUtils.setMinutes(DateUtils.setHours(date, 0), 0), 0);
            return Constants.YYYYMMDDHHMMSS.format(tempStart);
        }
        else {
            Date tempEnd = DateUtils.setSeconds(DateUtils.setMinutes(DateUtils.setHours(date, 23), 59), 59);
            return Constants.YYYYMMDDHHMMSS.format(tempEnd);
        }
    }


    /**
     * 处理yyyy-MM格式的日期
     *
     * @param param
     * @param queryDate
     */
    public void parseYM(Map<String, Object> param, String queryDate) {
        Date date;
        String startTime;
        String endTime;
        try {
            date = DateUtils.parseDate(queryDate, new String[]{"yyyy-MM"});

            Date tempStart = DateUtils.setSeconds(DateUtils.setMinutes(DateUtils.setHours(date, 0), 0), 0);
            startTime = Constants.YYYYMMDDHHMMSS.format(tempStart);
            param.put("startTime", startTime);

            //计算这个月的最后一天
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int lastDate = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            Date tempEnd = DateUtils.setSeconds(DateUtils.setMinutes(DateUtils.setHours(DateUtils.setDays(date, lastDate), 23), 59), 59);
            endTime = Constants.YYYYMMDDHHMMSS.format(tempEnd);
            param.put("endTime", endTime);
        } catch (Exception e) {
            throw new BusinessException("start or end time ","errors.date","时间范围");
        }
    }

    /**
     * 处理yyyy格式的日期
     *
     * @param param
     * @param queryDate
     */
    public void parseY(Map<String, Object> param, String queryDate) {
        Date date;
        String startTime;
        String endTime;
        try {
            date = DateUtils.parseDate(queryDate, new String[]{"yyyy"});

            Date tempStart = DateUtils.setSeconds(DateUtils.setMinutes(DateUtils.setHours(date, 0), 0), 0);
            startTime = Constants.YYYYMMDDHHMMSS.format(tempStart);
            param.put("startTime", startTime);

            //计算这个月的最后一天
            Date tempEnd = DateUtils.setSeconds(DateUtils.setMinutes(DateUtils.setHours(DateUtils.setDays(DateUtils.setMonths(date, 11), 31), 23), 59), 59);
            endTime = Constants.YYYYMMDDHHMMSS.format(tempEnd);
            param.put("endTime", endTime);
        } catch (Exception e) {
            throw new BusinessException("start or end time ","errors.date","时间范围");
        }
    }

//    /**
//     * 格式化用车要求参数
//     *
//     * @param additionalFunctionIds
//     * @return
//     */
//    public List<Integer> buildAdditionalFunctionIds(String additionalFunctionIds) {
//        String[] ids = StringUtils.split(additionalFunctionIds, ",");
//
//        if (ArrayUtils.isEmpty(ids)) return null;
//
//        List<Integer> idList = new ArrayList<>();
//        for (String id : ids) {
//            try {
//                idList.add(NumberUtils.createInteger(id));
//            } catch (Exception e) {
//                logger.error("运单管理查询->用车要求参数错误", e);
//                continue;
//            }
//        }
//
//        if(CollectionUtils.isEmpty(idList)) return null;
//
//        return idList;
//    }

    public void buildAdditionalFunctionIds(Map<String, Object> param) {
        if(param.get("funcIds") == null) return;

        Object[] ids = null;
        try {
            JSONArray additionalFunctionIds =  (JSONArray)param.get("funcIds");
            ids = additionalFunctionIds.toArray();
        } catch (Exception e) {
            logger.error("运单管理查询->用车要求参数错误", e);
        }

        if(ArrayUtils.isEmpty(ids)) {
            param.remove("funcIds");
            return ;
        }
        param.remove("funcIds");
        param.put("additionalFunctionIds", ids);




    }
}
