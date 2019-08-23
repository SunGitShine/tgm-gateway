package com.juma.tgm.gateway.waybill.controller.vo;

import com.juma.crm.customer.domain.ConsignorContactsInfo;
import com.juma.crm.customer.domain.ConsignorCustomerInfo;
import com.juma.tgm.crm.domain.ConsignorCustomerInfoVo;

import java.util.List;

/**
 * 用于保存crm企业列表和联系人列表
 * @ClassName: EnterpriseAndContactListVo
 * @Description:
 * @author: liang
 * @date: 2017-04-17 20:48
 * @Copyright: 2017 www.jumapeisong.com Inc. All rights reserved.
 */
public class EnterpriseAndContactListVo {

    /**
     * 企业客户列表
     */
    List<ConsignorCustomerInfoVo> customerInfos;

    /**
     * 联系人列表
     */
    List<ConsignorContactsInfo> ContactsInfos;


    public List<ConsignorCustomerInfoVo> getCustomerInfos() {
        return customerInfos;
    }

    public void setCustomerInfos(List<ConsignorCustomerInfoVo> customerInfos) {
        this.customerInfos = customerInfos;
    }

    public List<ConsignorContactsInfo> getContactsInfos() {
        return ContactsInfos;
    }

    public void setContactsInfos(List<ConsignorContactsInfo> contactsInfos) {
        ContactsInfos = contactsInfos;
    }
}
