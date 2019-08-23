package com.juma.tgm.gateway.customer.vo;

import java.util.Objects;

/**
 * @ClassName: WaybillOperateTrackDetailVo
 * @Description:
 * @author: liang
 * @date: 2017-06-20 10:26
 * @Copyright: 2017 www.jumapeisong.com Inc. All rights reserved.
 */
public class TrackDetailVo {

    private String title;

    private String content;

    private Integer operationType;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getOperationType() {
        return operationType;
    }

    public void setOperationType(Integer operationType) {
        this.operationType = operationType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackDetailVo that = (TrackDetailVo) o;
        return Objects.equals(title, that.title) &&
            Objects.equals(content, that.content) &&
            Objects.equals(operationType, that.operationType);
    }

    @Override
    public int hashCode() {

        return Objects.hash(title, content, operationType);
    }
}
