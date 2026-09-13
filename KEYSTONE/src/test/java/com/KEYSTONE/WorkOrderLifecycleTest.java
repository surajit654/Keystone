package com.KEYSTONE;

import com.KEYSTONE.dto.WorkOrderResponse;
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
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkOrderLifecycleTest {

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

    private User manager;
    private User dispatcher;
    private User technician;
    private User customerUser;
    private Customer customer;
    private Site site;
    private WorkOrder workOrder;

    @BeforeEach
    void setUp() {
        manager = new User();
        manager.setId(1L);
        manager.setEmail("manager@keystone.com");
        manager.setRole(Role.MANAGER);

        dispatcher = new User();
        dispatcher.setId(2L);
        dispatcher.setEmail("dispatcher@keystone.com");
        dispatcher.setRole(Role.DISPATCHER);

        technician = new User();
        technician.setId(3L);
        technician.setEmail("technician@keystone.com");
        technician.setRole(Role.TECHNICIAN);

        customerUser = new User();
        customerUser.setId(4L);
        customerUser.setEmail("customer@keystone.com");
        customerUser.setRole(Role.CUSTOMER);

        customer = new Customer();
        customer.setId(10L);
        customer.setName("Acme Corporation");

        site = new Site();
        site.setId(100L);
        site.setName("Acme HQ");
        site.setCustomer(customer);

        workOrder = new WorkOrder();
        workOrder.setId(1001L);
        workOrder.setCode("WO-2026-0001");
        workOrder.setTitle("HVAC Unit Inspection");
        workOrder.setDescription("Fix HVAC cooling unit");
        workOrder.setCustomer(customer);
        workOrder.setSite(site);
        workOrder.setStatus(WorkOrderStatus.NEW);
        workOrder.setPriority("HIGH");
        workOrder.setSlaDueAt(LocalDateTime.now().plusHours(4));
    }

    @Test
    @DisplayName("Should successfully assign technician to a NEW work order and change state to ASSIGNED")
    void testAssignTechnician() {
        when(workOrderRepository.findById(1001L)).thenReturn(Optional.of(workOrder));
        when(userRepository.findById(3L)).thenReturn(Optional.of(technician));
        when(userRepository.findByEmail("dispatcher@keystone.com")).thenReturn(Optional.of(dispatcher));
        when(workOrderRepository.save(any(WorkOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WorkOrderResponse response = workOrderService.assignTechnician(1001L, 3L, "dispatcher@keystone.com");

        assertNotNull(response);
        assertEquals(WorkOrderStatus.ASSIGNED, workOrder.getStatus());
        assertEquals(technician, workOrder.getAssignee());
        verify(historyRepository, times(1)).save(any(WorkOrderStatusHistory.class));
    }

    @Test
    @DisplayName("Should successfully step through full lifecycle: ASSIGNED -> IN_PROGRESS -> COMPLETED -> CLOSED")
    void testFullWorkOrderLifecycle() {
        workOrder.setStatus(WorkOrderStatus.ASSIGNED);
        workOrder.setAssignee(technician);

        when(workOrderRepository.findById(1001L)).thenReturn(Optional.of(workOrder));
        when(userRepository.findByEmail("technician@keystone.com")).thenReturn(Optional.of(technician));
        when(userRepository.findByEmail("manager@keystone.com")).thenReturn(Optional.of(manager));
        when(workOrderRepository.save(any(WorkOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 1. Technician starts work: ASSIGNED -> IN_PROGRESS
        workOrderService.transitionStatus(1001L, "IN_PROGRESS", "Started diagnostic", "technician@keystone.com");
        assertEquals(WorkOrderStatus.IN_PROGRESS, workOrder.getStatus());

        // 2. Technician completes work: IN_PROGRESS -> COMPLETED
        workOrderService.transitionStatus(1001L, "COMPLETED", "Fixed compressor and tested cooling", "technician@keystone.com");
        assertEquals(WorkOrderStatus.COMPLETED, workOrder.getStatus());

        // 3. Manager signs off: COMPLETED -> CLOSED
        workOrderService.transitionStatus(1001L, "CLOSED", "Customer signed off and invoice approved", "manager@keystone.com");
        assertEquals(WorkOrderStatus.CLOSED, workOrder.getStatus());

        verify(historyRepository, times(3)).save(any(WorkOrderStatusHistory.class));
    }

    @Test
    @DisplayName("Should reject invalid state transition jump from NEW directly to COMPLETED")
    void testRejectDirectJumpFromNewToCompleted() {
        when(workOrderRepository.findById(1001L)).thenReturn(Optional.of(workOrder));
        when(userRepository.findByEmail("technician@keystone.com")).thenReturn(Optional.of(technician));

        assertThrows(IllegalStateException.class, () -> {
            workOrderService.transitionStatus(1001L, "COMPLETED", "Bypassing work", "technician@keystone.com");
        });
    }

    @Test
    @DisplayName("Should reject closing a work order by non-manager (Technician / Dispatcher)")
    void testRejectCloseByNonManager() {
        workOrder.setStatus(WorkOrderStatus.COMPLETED);
        workOrder.setAssignee(technician);

        when(workOrderRepository.findById(1001L)).thenReturn(Optional.of(workOrder));
        when(userRepository.findByEmail("technician@keystone.com")).thenReturn(Optional.of(technician));

        assertThrows(AccessDeniedException.class, () -> {
            workOrderService.transitionStatus(1001L, "CLOSED", "Tech trying to close", "technician@keystone.com");
        });
    }

    @Test
    @DisplayName("Should reject transition on a CLOSED terminal work order")
    void testRejectTransitionOnClosedWorkOrder() {
        workOrder.setStatus(WorkOrderStatus.CLOSED);

        when(workOrderRepository.findById(1001L)).thenReturn(Optional.of(workOrder));
        when(userRepository.findByEmail("manager@keystone.com")).thenReturn(Optional.of(manager));

        assertThrows(IllegalStateException.class, () -> {
            workOrderService.transitionStatus(1001L, "IN_PROGRESS", "Reopening attempt", "manager@keystone.com");
        });
    }

    @Test
    @DisplayName("Should allow Dispatcher or Manager to cancel an open work order")
    void testCancelWorkOrder() {
        when(workOrderRepository.findById(1001L)).thenReturn(Optional.of(workOrder));
        when(userRepository.findByEmail("dispatcher@keystone.com")).thenReturn(Optional.of(dispatcher));
        when(workOrderRepository.save(any(WorkOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        workOrderService.transitionStatus(1001L, "CANCELLED", "Customer requested cancellation", "dispatcher@keystone.com");

        assertEquals(WorkOrderStatus.CANCELLED, workOrder.getStatus());
        verify(historyRepository, times(1)).save(any(WorkOrderStatusHistory.class));
    }
}
