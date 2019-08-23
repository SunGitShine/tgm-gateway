package com.juma.tgm.gateway.customer.vo;

/**
 * 描述取货地，配送地信息
 * @ClassName: DeliveryAddressVo
 * @Description:
 * @author: liang
 * @date: 2017-06-23 10:12
 * @Copyright: 2017 www.jumapeisong.com Inc. All rights reserved.
 */
public class DeliveryAddressVo {

    private String address;

    private String contact;

    private String cellPhone;


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }
}
