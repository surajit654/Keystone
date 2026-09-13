package com.KEYSTONE.dto;

import com.KEYSTONE.model.PartUsage;

import java.math.BigDecimal;

public class PartUsageResponse {

    private Long id;
    private Long workOrderId;
    private Long partId;
    private String partName;
    private String partNumber;
    private Integer quantity;
    private BigDecimal unitCost;
    private BigDecimal totalCost;

    public PartUsageResponse() {
    }

    public static PartUsageResponse fromEntity(PartUsage usage) {
        PartUsageResponse res = new PartUsageResponse();
        res.setId(usage.getId());
        res.setWorkOrderId(usage.getWorkOrder() != null ? usage.getWorkOrder().getId() : null);
        if (usage.getPart() != null) {
            res.setPartId(usage.getPart().getId());
            res.setPartName(usage.getPart().getName());
            res.setPartNumber(usage.getPart().getPartNumber());
        }
        res.setQuantity(usage.getQuantity());
        res.setUnitCost(usage.getUnitCost());
        if (usage.getUnitCost() != null && usage.getQuantity() != null) {
            res.setTotalCost(usage.getUnitCost().multiply(BigDecimal.valueOf(usage.getQuantity())));
        }
        return res;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(Long workOrderId) {
        this.workOrderId = workOrderId;
    }

    public Long getPartId() {
        return partId;
    }

    public void setPartId(Long partId) {
        this.partId = partId;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }
}
