package com.juma.tgm.gateway.sop.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.giants.common.exception.BusinessException;
import com.juma.tgm.gateway.sop.controller.vo.ElementVo;
import com.juma.tgm.gateway.sop.controller.vo.StepVo;
import com.juma.tgm.sop.domain.Sop;
import com.juma.tgm.sop.service.SopService;

@Controller
@RequestMapping(value = "sop")
public class SopController {

    @Resource
    private SopService sopService;
    
    @ResponseBody
    @RequestMapping(value = "{sopId}", method = RequestMethod.GET)
    public List<ElementVo> getSop(@PathVariable Integer sopId) {
        Sop sop = sopService.getSop(sopId);
        if (sop == null) throw new BusinessException("errors.notFound", "errors.notFound", "Sop " + sopId);
        String jsonStr = sop.getSopJson();
        if(StringUtils.isBlank(jsonStr)) throw new BusinessException("errors.paramError", "errors.paramError");
        List<StepVo> sopObjectArr = JSON.parseArray(jsonStr, StepVo.class);
        List<ElementVo> elementVoArr = new ArrayList<ElementVo>();
        for(StepVo stepVo : sopObjectArr) {
            elementVoArr.addAll(stepVo.getElements());
        }
        return elementVoArr;
    }
    
}
