package com.juma.tgm.gateway.sop.controller.vo;

import java.util.ArrayList;
import java.util.List;

import com.juma.tgm.sop.domain.Step;

public class StepVo extends Step {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -6773478526393834546L;
    
    private List<ElementVo> elements = new ArrayList<ElementVo>();

    public List<ElementVo> getElements() {
        return elements;
    }

    public void setElements(List<ElementVo> elements) {
        this.elements = elements;
    }
    
    public void addElement(ElementVo element) {
        this.elements.add(element);
    }
    
    
}
