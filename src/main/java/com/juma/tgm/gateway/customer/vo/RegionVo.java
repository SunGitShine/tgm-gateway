package com.juma.tgm.gateway.customer.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

/**
 * @ClassName: RegionVo
 * @Description:
 * @author: liang
 * @date: 2017-03-15 19:21
 * @Copyright: 2017 www.jumapeisong.com Inc. All rights reserved.
 */
public class RegionVo implements Serializable {

    private static final long serialVersionUID = 6166448835112266413L;

    private Integer regionId;

    private Integer parentRegionId;

    private String regionCode;

    private String regionName;

    private String province;

    private String city;

    private List<RegionVo> children;

    public Integer getRegionId() {
        return regionId;
    }

    public void setRegionId(Integer regionId) {
        this.regionId = regionId;
    }

    public Integer getParentRegionId() {
        return parentRegionId;
    }

    public void setParentRegionId(Integer parentRegionId) {
        this.parentRegionId = parentRegionId;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<RegionVo> getChildren() {
        return children;
    }

    public void setChildren(List<RegionVo> children) {
        this.children = children;
    }

    public void addChild(RegionVo child) {

        if (CollectionUtils.isEmpty(this.children)) {
            this.children = new ArrayList<>();
        }

        this.children.add(child);
    }
}
