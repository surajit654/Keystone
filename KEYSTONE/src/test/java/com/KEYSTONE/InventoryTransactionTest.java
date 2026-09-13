package com.KEYSTONE;

import com.KEYSTONE.dto.LogPartUsageRequest;
import com.KEYSTONE.dto.PartUsageResponse;
import com.KEYSTONE.model.*;
import com.KEYSTONE.repository.*;
import com.KEYSTONE.service.WorkOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryTransactionTest {

    @Mock
    private WorkOrderRepository workOrderRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private SiteRepository siteRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private WorkOrderStatusHistoryRepository historyRepository;
    @Mock
    private PartRepository partRepository;
    @Mock
    private PartUsageRepository partUsageRepository;
    @Mock
    private TimeLogRepository timeLogRepository;

    @InjectMocks
    private WorkOrderService workOrderService;

    private WorkOrder activeWorkOrder;
    private Part hvacFilter;

    @BeforeEach
    void setUp() {
        activeWorkOrder = new WorkOrder();
        activeWorkOrder.setId(200L);
        activeWorkOrder.setStatus(WorkOrderStatus.IN_PROGRESS);
        activeWorkOrder.setDescription("Filter replacement maintenance");

        hvacFilter = new Part();
        hvacFilter.setId(50L);
        hvacFilter.setName("High Capacity HVAC Filter");
        hvacFilter.setPartNumber("FLT-HVAC-01");
        hvacFilter.setStockQuantity(10);
        hvacFilter.setUnitCost(new BigDecimal("45.00"));
    }

    @Test
    @DisplayName("Should successfully consume parts and decrement inventory when sufficient stock is present")
    void testSuccessfulPartConsumption() {
        when(workOrderRepository.findById(200L)).thenReturn(Optional.of(activeWorkOrder));
        when(partRepository.findById(50L)).thenReturn(Optional.of(hvacFilter));
        when(partRepository.save(any(Part.class))).thenAnswer(i -> i.getArgument(0));
        when(partUsageRepository.save(any(PartUsage.class))).thenAnswer(i -> {
            PartUsage pu = i.getArgument(0);
            pu.setId(999L);
            return pu;
        });

        LogPartUsageRequest request = new LogPartUsageRequest();
        request.setPartId(50L);
        request.setQuantity(3);

        PartUsageResponse response = workOrderService.logPartUsage(200L, request, "tech@keystone.com");

        assertNotNull(response);
        assertEquals(3, response.getQuantity());
        assertEquals(new BigDecimal("45.00"), response.getUnitCost());
        // Verify inventory was decremented from 10 to 7
        assertEquals(7, hvacFilter.getStockQuantity());
        verify(partRepository, times(1)).save(hvacFilter);
        verify(partUsageRepository, times(1)).save(any(PartUsage.class));
    }

    @Test
    @DisplayName("Should strictly reject parts logging when requested quantity exceeds available stock")
    void testRejectOverStockConsumption() {
        when(workOrderRepository.findById(200L)).thenReturn(Optional.of(activeWorkOrder));
        when(partRepository.findById(50L)).thenReturn(Optional.of(hvacFilter));

        LogPartUsageRequest request = new LogPartUsageRequest();
        request.setPartId(50L);
        request.setQuantity(15); // Requesting 15 when only 10 available

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            workOrderService.logPartUsage(200L, request, "tech@keystone.com");
        });

        assertTrue(exception.getMessage().contains("Insufficient inventory stock"));
        // Ensure stock was NOT decremented
        assertEquals(10, hvacFilter.getStockQuantity());
        verify(partRepository, never()).save(any());
        verify(partUsageRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reject parts logging if work order is in a non-active status like CLOSED")
    void testRejectPartConsumptionOnClosedWorkOrder() {
        activeWorkOrder.setStatus(WorkOrderStatus.CLOSED);
        when(workOrderRepository.findById(200L)).thenReturn(Optional.of(activeWorkOrder));

        LogPartUsageRequest request = new LogPartUsageRequest();
        request.setPartId(50L);
        request.setQuantity(1);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            workOrderService.logPartUsage(200L, request, "tech@keystone.com");
        });

        assertTrue(exception.getMessage().contains("Can only log parts against an active work order"));
        verify(partRepository, never()).findById(any());
    }
}
