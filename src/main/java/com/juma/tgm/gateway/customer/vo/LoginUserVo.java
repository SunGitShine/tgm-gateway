/**
 * 
 */
package com.juma.tgm.gateway.customer.vo;

import java.io.Serializable;

/**
 * @author vencent.lu
 *
 */
public class LoginUserVo implements Serializable{

    private static final long serialVersionUID = 6271544800801667210L;
    private String phone;
    private String password;
    private String location;// 地址详情

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
