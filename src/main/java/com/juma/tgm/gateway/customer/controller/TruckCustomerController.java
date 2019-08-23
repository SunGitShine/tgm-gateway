/**
 *
 */
package com.juma.tgm.gateway.customer.controller;

import com.giants.cache.redis.RedisClient;
import com.juma.auth.user.domain.LoginEcoUser;
import com.juma.tgm.common.BaseUtil;
import com.juma.tgm.common.Constants;
import com.juma.tgm.customer.domain.CargoOwnerLoginUser;
import com.juma.tgm.customer.domain.TruckCustomer;
import com.juma.tgm.gateway.common.AbstractController;
import com.juma.tgm.version.service.VersionService;
import java.io.Serializable;
import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author vencent.lu
 */
@Controller
@RequestMapping(value = "truckCustomer")
public class TruckCustomerController extends AbstractController {

    @Resource
    private VersionService versionService;
    @Resource
    private RedisClient redisClient;

    @RequestMapping(value = "profile/update", method = RequestMethod.POST)
    @ResponseBody
    public void updateProfile(@RequestBody TruckCustomer truckCustomer, CargoOwnerLoginUser cargoOwnerLoginUser,
            LoginEcoUser loginEcoUser) {
    }

    /**
     * 获取当前登录人信息
     * 
     * @param cargoOwnerLoginUser
     * @param cargoOwnerLoginEcoUser
     * @return
     */
    @RequestMapping(value = "cargoOwner/loginUser/info", method = RequestMethod.GET)
    @ResponseBody
    public CargoOwnerLoginUser getCustomerLoginUser(CargoOwnerLoginUser cargoOwnerLoginUser,
            LoginEcoUser cargoOwnerLoginEcoUser) {
        // 未评价运单ID TODO WEI 调查微信运单是否需要强制评价
        Integer userId = cargoOwnerLoginEcoUser.getUserId();
        cargoOwnerLoginUser.setLoginEcoUser(cargoOwnerLoginEcoUser);
        Integer waybillId = getWaybillId(
                Constants.APP_USER_PRVEFIEX + Constants.STAR_CUSTOMER + userId + cargoOwnerLoginEcoUser.getTenantId());
        cargoOwnerLoginUser.setWaybillIdNeedToEvaluate(waybillId);
        cargoOwnerLoginUser.setVersionCheck(versionService.checkVersion());
        return cargoOwnerLoginUser;
    }

    // 获取waybillId
    private Integer getWaybillId(String key) {
        Serializable serializable = redisClient.get(key);
        if (null != serializable) {
            String waybillIdStr = String.valueOf(serializable);
            return BaseUtil.getInt(waybillIdStr);
        }
        return 0;
    }

}
