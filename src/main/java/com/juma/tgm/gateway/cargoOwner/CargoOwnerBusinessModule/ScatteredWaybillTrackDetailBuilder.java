package com.juma.tgm.gateway.cargoOwner.CargoOwnerBusinessModule;

import com.juma.tgm.driver.domain.Driver;
import com.juma.tgm.driver.service.DriverService;
import com.juma.tgm.gateway.cargoOwner.bo.ScatteredWaybillTrackDetailBo;
import com.juma.tgm.truck.domain.Truck;
import com.juma.tgm.truck.service.TruckService;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.domain.WaybillOperateTrack;
import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum.OperateType;
import com.juma.tgm.waybill.service.WaybillOperateTrackService;
import com.juma.tgm.waybill.vo.WaybillOperateTrackQuery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Resource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * @ClassName: WaybillTrackDetailBuilder
 * @Description:
 * @author: liang
 * @date: 2017-06-20 10:17
 * @Copyright: 2017 www.jumapeisong.com Inc. All rights reserved.
 */
@Component
public class ScatteredWaybillTrackDetailBuilder {

    @Resource
    private WaybillOperateTrackService waybillOperateTrackService;
    @Resource
    private DriverService driverService;
    @Resource
    private TruckService truckService;


    public static final List<Integer> allOperationList = Collections.unmodifiableList(Arrays.asList(
        new Integer[]{
            OperateType.CREATE_WAYBILL.getCode(),
            OperateType.ASSIGNED_SYS.getCode(),
            OperateType.LEAVE_DEPOT.getCode(),
            OperateType.ARRIVE_DEPOT.getCode(),
            OperateType.DELIVERYING.getCode(),
            OperateType.RECEIVED.getCode(),
            OperateType.AI_MATCH_SUCCESS.getCode(),
            OperateType.AI_DRIVER_ANSWER.getCode()
        }
    ));

    /**
     * 按数据id排序
     */
    public static final Comparator<WaybillOperateTrack> operateTrackComparator = new Comparator<WaybillOperateTrack>() {
        @Override
        public int compare(WaybillOperateTrack o1, WaybillOperateTrack o2) {
            return NumberUtils.compare(o2.getTrackId(), o1.getTrackId());
        }
    };


    public ScatteredWaybillTrackDetailBo buildTrackDetailBo(Waybill waybill, List<Integer> typePackage, long timeLength) {
        if (waybill == null) return null;

        List<Integer> notIncludeapplicationList = Arrays.asList();
        List<WaybillOperateTrackQuery> list = waybillOperateTrackService.listByWaybillIdAndTypes(waybill.getWaybillId(),
            typePackage, notIncludeapplicationList);

        if (CollectionUtils.isEmpty(list)) {
            list = new ArrayList<>();
        } else {
            //过滤数据
            this.filterLastManualAssignAndAutoAssign(list);
            this.sortDataById(list);
        }
        //运单司机
        Truck truck = null;

        Driver driver = null ;
        if( waybill.getDriverId() != null ) {
            driver= driverService.getDriver( waybill.getDriverId());
        }
        return new ScatteredWaybillTrackDetailBo(waybill, list, driver, truck, timeLength);

    }

    /**
     * 找到相应的数据
     *
     * @param list
     * @return targetData
     */
    private List<WaybillOperateTrackQuery> filterTargetList(List<WaybillOperateTrackQuery> list, OperateType targetType) {
        if (CollectionUtils.isEmpty(list)) return null;
        if (targetType == null) return null;

        //找到目标类型并删除不是最新的数据
        List<WaybillOperateTrackQuery> targetList = new ArrayList<>();
        for (WaybillOperateTrackQuery wot : list) {
            if (NumberUtils.compare(wot.getOperateType(), targetType.getCode()) != 0) {
                continue;
            }
            WaybillOperateTrackQuery query = new WaybillOperateTrackQuery();
            BeanUtils.copyProperties(wot, query);

            targetList.add(query);
        }

        if (CollectionUtils.isEmpty(targetList)) return null;

        return targetList;
    }

    /**
     * 找到最后一条数据
     *
     * @param list
     * @return
     */
    private WaybillOperateTrackQuery findLastData(List<WaybillOperateTrackQuery> list) {
        if (CollectionUtils.isEmpty(list)) return null;

        Collections.sort(list, ScatteredWaybillTrackDetailBuilder.operateTrackComparator);

        //找到最近的一条记录
        return list.iterator().next();
    }


    /**
     * 最经一次派车成功逻辑
     * 规则：只展示最近一次派车成功轨迹,其中可能是自动派车和人工派车,有人工指派则认为是自动派车失败
     *
     * @param list
     */
    private void filterLastManualAssignAndAutoAssign(List<WaybillOperateTrackQuery> list) {
        if (CollectionUtils.isEmpty(list)) return;

        //如果有人工指派则优先取最后一条人工指派
        List<WaybillOperateTrackQuery> assignList = this.filterTargetList(list, OperateType.ASSIGNED_SYS);

        List<WaybillOperateTrackQuery> autoList = this.filterTargetList(list, OperateType.AI_MATCH_SUCCESS);
        if (CollectionUtils.isNotEmpty(assignList)) {
            //移除自动派车记录
            if (CollectionUtils.isNotEmpty(autoList)) {
                list.removeAll(autoList);
            }
            if (CollectionUtils.isNotEmpty(assignList)) {
                list.removeAll(assignList);
            }
            WaybillOperateTrackQuery target = this.findLastData(assignList);
            list.add(target);
            return;
        }

        //没有人工指派取最后一条自动指派
        if (CollectionUtils.isNotEmpty(autoList)) {
            list.removeAll(autoList);
            WaybillOperateTrackQuery target = this.findLastData(autoList);
            list.add(target);
            return;
        }

    }


    /**
     * 将数据按id排序
     *
     * @param list
     */
    private void sortDataById(List<WaybillOperateTrackQuery> list) {
        Collections.sort(list, ScatteredWaybillTrackDetailBuilder.operateTrackComparator);
    }

    public static void main(String[] args) {
        ScatteredWaybillTrackDetailBuilder st = new ScatteredWaybillTrackDetailBuilder();
        List<WaybillOperateTrackQuery> list = new ArrayList<>();
        WaybillOperateTrackQuery o1 = new WaybillOperateTrackQuery();
        o1.setOperateType(OperateType.CREATE_WAYBILL.getCode());
        o1.setTrackId(2);
        WaybillOperateTrackQuery o2 = new WaybillOperateTrackQuery();
        o2.setOperateType(OperateType.ADD_CARRY_COST.getCode());
        o2.setTrackId(1);

        list.add(o1);
        list.add(o2);
        st.filterLastManualAssignAndAutoAssign(list);
    }

}
