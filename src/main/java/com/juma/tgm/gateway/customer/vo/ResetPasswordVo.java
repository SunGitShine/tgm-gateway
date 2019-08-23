/**
 * 
 */
package com.juma.tgm.gateway.customer.vo;

/**
 * @author vencent.lu
 *
 */
public class ResetPasswordVo extends LoginUserVo {
	
    private static final long serialVersionUID = 3247740610321986088L;
    private String verificationCode;

	public String getVerificationCode() {
		return verificationCode;
	}

	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}
	
	

}
