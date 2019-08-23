/**
 * 
 */
package com.juma.tgm.gateway.customer.vo;

/**
 * @author vencent.lu
 *
 */
public class RegisterUserVo extends LoginUserVo {

    private static final long serialVersionUID = 8489367766102752746L;
    private String verificationCode;
    private String regionCode;
    private String inviteCode;
    private String location;// 地址详情

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
