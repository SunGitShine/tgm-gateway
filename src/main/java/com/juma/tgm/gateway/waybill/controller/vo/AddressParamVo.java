package com.juma.tgm.gateway.waybill.controller.vo;

import java.io.Serializable;

/**
 * Created by shawn_lin on 2017/6/29.
 */
public class AddressParamVo implements Serializable{

    private static final long serialVersionUID = 850541210300116293L;

    private Integer addressId;

    private Integer addressType;

    //选填字段

    private String deviceNo;

    private Integer deviceType;

    private String coordinates;

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public Integer getAddressType() {
        return addressType;
    }

    public void setAddressType(Integer addressType) {
        this.addressType = addressType;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public Integer getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Integer deviceType) {
        this.deviceType = deviceType;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

}
