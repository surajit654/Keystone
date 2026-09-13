package com.KEYSTONE.dto;

import com.KEYSTONE.model.Part;

import java.math.BigDecimal;

public class PartResponse {

    private Long id;
    private String name;
    private String partNumber;
    private Integer stockQuantity;
    private BigDecimal unitCost;

    public PartResponse() {
    }

    public static PartResponse fromEntity(Part part) {
        PartResponse res = new PartResponse();
        res.setId(part.getId());
        res.setName(part.getName());
        res.setPartNumber(part.getPartNumber());
        res.setStockQuantity(part.getStockQuantity());
        res.setUnitCost(part.getUnitCost());
        return res;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }
}
