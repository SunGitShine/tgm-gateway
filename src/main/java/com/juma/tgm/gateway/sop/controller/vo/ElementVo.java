package com.juma.tgm.gateway.sop.controller.vo;

import com.juma.tgm.sop.domain.Element;

public class ElementVo extends Element {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -8247459399989808257L;

    private Boolean display;
    
    private Boolean editable;

    public Boolean getDisplay() {
        return display;
    }

    public void setDisplay(Boolean display) {
        this.display = display;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }
    
}
