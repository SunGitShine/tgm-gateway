package com.juma.tgm.gateway.cargoOwner.vo;

import com.juma.auth.user.domain.UserIdentityCardInfo;

import java.io.Serializable;

/**
 * @ClassName: UserVerify
 * @Description:
 * @author: liang
 * @date: 2017-11-21 10:10
 * @Copyright: 2017 www.jumapeisong.com Inc. All rights reserved.
 */
public class UserVerifyVo extends UserIdentityCardInfo implements Serializable {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
