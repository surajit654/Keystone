package com.KEYSTONE.dto;

import com.KEYSTONE.model.PartUsage;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class LogPartUsageRequest {

    @NotNull(message = "Part ID is required")
    private Long partId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    public LogPartUsageRequest() {
    }

    public LogPartUsageRequest(Long partId, Integer quantity) {
        this.partId = partId;
        this.quantity = quantity;
    }

    public Long getPartId() {
        return partId;
    }

    public void setPartId(Long partId) {
        this.partId = partId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
