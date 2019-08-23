package com.juma.tgm.gateway.waybill.controller.vo;

import com.juma.tgm.cityManage.domain.CityManage;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.text.Collator;
import java.util.*;

/**
 * @ClassName: CityManageInfoVo
 * @Description:
 * @author: liang
 * @date: 2017-09-05 16:53
 * @Copyright: 2017 www.jumapeisong.com Inc. All rights reserved.
 */
public class CityManageInfoVo implements Serializable {

    private Map<String, List<CityManage>> citySeparations;

    public void addCity(CityManage city) {
        if(city == null) return;

        if(StringUtils.isBlank(city.getProvinceName())) return;

        if (this.citySeparations == null) {
            this.citySeparations = new HashMap<>();
        }

        List<CityManage> list = null;
        if (this.citySeparations.containsKey(city.getProvinceName())) {//在已有省下添加
            list = this.citySeparations.get(city.getProvinceName());
        } else {//新增省添加
            list = new ArrayList<CityManage>();
            this.citySeparations.put(city.getProvinceName(), list);
        }
        list.add(city);
    }

    public LinkedHashMap<String, List<CityManage>> getCitySeparations() {
        if(this.citySeparations == null) return null;

        Comparator<Object> com = Collator.getInstance(java.util.Locale.CHINA);

        LinkedHashMap<String, List<CityManage>> rst = new LinkedHashMap<>();

        List<String> names = new ArrayList<>(this.citySeparations.keySet());

        //按首字母排序
        Collections.sort(names, com);

        for(String name : names){
            List<CityManage> cities = this.citySeparations.get(name);
            rst.put(name, cities);
        }

        return rst;
    }
}
