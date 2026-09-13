package com.KEYSTONE;

import com.KEYSTONE.dto.DashboardSummaryResponse;
import com.KEYSTONE.model.*;
import com.KEYSTONE.repository.SiteRepository;
import com.KEYSTONE.repository.UserRepository;
import com.KEYSTONE.repository.WorkOrderRepository;
import com.KEYSTONE.service.ReportingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportingServiceTest {

    @Mock
    private WorkOrderRepository workOrderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SiteRepository siteRepository;

    @InjectMocks
    private ReportingService reportingService;

    private WorkOrder wo1;
    private WorkOrder wo2;
    private WorkOrder wo3;
    private User tech1;
    private Site site1;

    @BeforeEach
    void setUp() {
        Customer c1 = new Customer();
        c1.setId(10L);
        c1.setName("Acme Inc");

        site1 = new Site();
        site1.setId(100L);
        site1.setName("Downtown Tower");
        site1.setCustomer(c1);

        tech1 = new User();
        tech1.setId(3L);
        tech1.setEmail("technician@keystone.com");
        tech1.setRole(Role.TECHNICIAN);

        wo1 = new WorkOrder();
        wo1.setId(1L);
        wo1.setStatus(WorkOrderStatus.IN_PROGRESS);
        wo1.setPriority("HIGH");
        wo1.setCustomer(c1);
        wo1.setSite(site1);
        wo1.setAssignee(tech1);
        wo1.setSlaDueAt(LocalDateTime.now().plusHours(2));

        wo2 = new WorkOrder();
        wo2.setId(2L);
        wo2.setStatus(WorkOrderStatus.COMPLETED);
        wo2.setPriority("CRITICAL");
        wo2.setCustomer(c1);
        wo2.setSite(site1);
        wo2.setAssignee(tech1);
        wo2.setSlaDueAt(LocalDateTime.now().plusHours(1));

        wo3 = new WorkOrder();
        wo3.setId(3L);
        wo3.setStatus(WorkOrderStatus.NEW);
        wo3.setPriority("MEDIUM");
        wo3.setCustomer(c1);
        wo3.setSite(site1);
        wo3.setSlaDueAt(LocalDateTime.now().minusHours(1)); // Overdue / SLA breached
    }

    @Test
    @DisplayName("Should aggregate work order summary statistics and technician/site workloads accurately")
    void testGetDashboardSummary() {
        when(workOrderRepository.findAll()).thenReturn(List.of(wo1, wo2, wo3));
        when(userRepository.findByRole(Role.TECHNICIAN)).thenReturn(List.of(tech1));
        when(siteRepository.findAll()).thenReturn(List.of(site1));

        DashboardSummaryResponse summary = reportingService.getDashboardSummary();

        assertNotNull(summary);
        assertEquals(3, summary.getTotalWorkOrders());
        assertEquals(2, summary.getActiveWorkOrders()); // wo1 (IN_PROGRESS) + wo3 (NEW)
        assertEquals(1, summary.getOverdueWorkOrders()); // wo3 is overdue
        assertNotNull(summary.getStatusCounts());
        assertEquals(1L, summary.getStatusCounts().get("IN_PROGRESS"));
        assertEquals(1L, summary.getStatusCounts().get("COMPLETED"));
        assertEquals(1L, summary.getStatusCounts().get("NEW"));

        // SLA Compliance
        assertTrue(summary.getSlaComplianceRate() >= 0.0 && summary.getSlaComplianceRate() <= 100.0);

        // Breakdown validations
        assertNotNull(summary.getTechnicianBreakdown());
        assertEquals(1, summary.getTechnicianBreakdown().size());
        assertEquals("technician@keystone.com", summary.getTechnicianBreakdown().get(0).getEmail());
        assertEquals(1, summary.getTechnicianBreakdown().get(0).getActiveJobs());

        assertNotNull(summary.getSiteBreakdown());
        assertEquals(1, summary.getSiteBreakdown().size());
        assertEquals("Downtown Tower", summary.getSiteBreakdown().get(0).getSiteName());
    }
}
