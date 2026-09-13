import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import Sidebar from '../components/Sidebar';
import { reportApi, workOrderApi } from '../api/apiClient';
import { useAuth } from '../context/AuthContext';

function DashboardPage() {
  const { user } = useAuth();
  const [report, setReport] = useState(null);
  const [recentOrders, setRecentOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadDashboard();
  }, []);

  const loadDashboard = async () => {
    setLoading(true);
    try {
      const [summaryData, ordersData] = await Promise.all([
        reportApi.getSummary().catch(() => null),
        workOrderApi.search({}, 0, 5).catch(() => ({ content: [] })),
      ]);

      setReport(summaryData);
      setRecentOrders(ordersData?.content || []);
    } catch (err) {
      console.error('Failed to load dashboard metrics:', err);
    } finally {
      setLoading(false);
    }
  };

  const statusColors = {
    NEW: '#38bdf8',
    OPEN: '#38bdf8',
    ASSIGNED: '#818cf8',
    IN_PROGRESS: '#fbbf24',
    ON_HOLD: '#f97316',
    COMPLETED: '#34d399',
    CLOSED: '#64748b',
    CANCELLED: '#ef4444',
  };

  const totalOrders = report?.totalWorkOrders || 0;
  const statusCounts = report?.statusCounts || {};

  return (
    <div className="app-container">
      <Sidebar />

      <main className="main-content">
        <div className="page-header">
          <div>
            <h1 className="page-title">Operations Command Center</h1>
            <p className="page-subtitle">Welcome back, {user?.email} · Live SLAs, metrics, and operations analytics</p>
          </div>
          <div className="action-btn-group">
            <Link to="/work-orders" className="btn btn-primary">
              <span>📋</span>
              <span>Open Kanban Board</span>
            </Link>
          </div>
        </div>

        {/* Top KPI Metric Cards */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '20px', marginBottom: '28px' }}>
          <div className="table-card" style={{ padding: '20px' }}>
            <div style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-muted)', textTransform: 'uppercase' }}>
              Active Open Work Orders
            </div>
            <div style={{ fontSize: '32px', fontWeight: 800, color: 'var(--accent-primary)', marginTop: '8px' }}>
              {loading ? '—' : (report?.activeWorkOrders ?? 0)}
            </div>
            <div style={{ fontSize: '12px', color: 'var(--text-dim)', marginTop: '4px' }}>
              In queue / active in field
            </div>
          </div>

          <div className="table-card" style={{ padding: '20px' }}>
            <div style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-muted)', textTransform: 'uppercase' }}>
              SLA Breached / Overdue
            </div>
            <div style={{
              fontSize: '32px',
              fontWeight: 800,
              color: (report?.overdueWorkOrders || 0) > 0 ? 'var(--accent-rose)' : 'var(--accent-emerald)',
              marginTop: '8px'
            }}>
              {loading ? '—' : (report?.overdueWorkOrders ?? 0)}
            </div>
            <div style={{ fontSize: '12px', color: 'var(--text-dim)', marginTop: '4px' }}>
              {(report?.overdueWorkOrders || 0) > 0 ? '🚨 Requires immediate triage' : '✓ All SLAs on track'}
            </div>
          </div>

          <div className="table-card" style={{ padding: '20px' }}>
            <div style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-muted)', textTransform: 'uppercase' }}>
              SLA Compliance (30 Days)
            </div>
            <div style={{
              fontSize: '32px',
              fontWeight: 800,
              color: (report?.slaComplianceRate ?? 100) >= 85 ? 'var(--accent-emerald)' : 'var(--accent-amber)',
              marginTop: '8px'
            }}>
              {loading ? '—' : `${report?.slaComplianceRate ?? 100}%`}
            </div>
            <div style={{ fontSize: '12px', color: 'var(--text-dim)', marginTop: '4px' }}>
              Target: ≥ 85.0% compliance
            </div>
          </div>

          <div className="table-card" style={{ padding: '20px' }}>
            <div style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-muted)', textTransform: 'uppercase' }}>
              Total Platform Orders
            </div>
            <div style={{ fontSize: '32px', fontWeight: 800, color: 'var(--accent-cyan)', marginTop: '8px' }}>
              {loading ? '—' : (report?.totalWorkOrders ?? 0)}
            </div>
            <div style={{ fontSize: '12px', color: 'var(--text-dim)', marginTop: '4px' }}>
              Cumulative historical volume
            </div>
          </div>
        </div>

        {/* Status Mix Distribution Bar */}
        <div className="table-card" style={{ padding: '24px', marginBottom: '28px' }}>
          <h2 style={{ fontSize: '16px', fontWeight: 700, marginBottom: '16px' }}>
            📊 Work Orders Status Mix Distribution
          </h2>

          {totalOrders === 0 ? (
            <div style={{ color: 'var(--text-muted)', fontSize: '13px' }}>No work orders in system yet.</div>
          ) : (
            <>
              {/* Stacked Percentage Bar */}
              <div style={{
                height: '18px',
                borderRadius: '9px',
                backgroundColor: 'var(--bg-primary)',
                display: 'flex',
                overflow: 'hidden',
                marginBottom: '16px'
              }}>
                {Object.entries(statusCounts).map(([status, count]) => {
                  if (count === 0) return null;
                  const pct = (count / totalOrders) * 100;
                  return (
                    <div
                      key={status}
                      title={`${status}: ${count} (${pct.toFixed(1)}%)`}
                      style={{
                        width: `${pct}%`,
                        backgroundColor: statusColors[status] || '#6366f1',
                        transition: 'width 0.3s ease',
                      }}
                    />
                  );
                })}
              </div>

              {/* Status Breakdown Legend Chips */}
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '12px' }}>
                {Object.entries(statusCounts).map(([status, count]) => (
                  <div
                    key={status}
                    style={{
                      display: 'flex',
                      alignItems: 'center',
                      gap: '8px',
                      padding: '6px 12px',
                      backgroundColor: 'var(--bg-primary)',
                      borderRadius: '6px',
                      fontSize: '12px',
                      border: '1px solid var(--border-subtle)'
                    }}
                  >
                    <span style={{
                      width: '8px',
                      height: '8px',
                      borderRadius: '50%',
                      backgroundColor: statusColors[status] || '#6366f1'
                    }} />
                    <span style={{ fontWeight: 600 }}>{status}:</span>
                    <span style={{ color: 'var(--text-muted)' }}>{count}</span>
                  </div>
                ))}
              </div>
            </>
          )}
        </div>

        {/* Technician Productivity & Facility Workload Breakdown */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '24px', marginBottom: '28px' }}>
          {/* Technician Breakdown */}
          <div className="table-card" style={{ padding: '24px' }}>
            <h2 style={{ fontSize: '16px', fontWeight: 700, marginBottom: '16px' }}>
              🔧 Technician Workload & Output
            </h2>

            {report?.technicianBreakdown?.length === 0 ? (
              <div style={{ color: 'var(--text-muted)', fontSize: '13px' }}>No technicians registered.</div>
            ) : (
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Technician</th>
                    <th>Active Jobs</th>
                    <th>Completed</th>
                  </tr>
                </thead>
                <tbody>
                  {report?.technicianBreakdown?.map((t) => (
                    <tr key={t.technicianId}>
                      <td style={{ fontWeight: 600 }}>{t.email}</td>
                      <td>
                        <span className="badge" style={{ backgroundColor: 'var(--bg-primary)', color: 'var(--status-in-progress)' }}>
                          {t.activeJobs} Active
                        </span>
                      </td>
                      <td style={{ color: 'var(--accent-emerald)', fontWeight: 600 }}>
                        {t.completedJobs} Closed
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>

          {/* Site Workload Breakdown */}
          <div className="table-card" style={{ padding: '24px' }}>
            <h2 style={{ fontSize: '16px', fontWeight: 700, marginBottom: '16px' }}>
              🏢 Facility & Site Workload
            </h2>

            {report?.siteBreakdown?.length === 0 ? (
              <div style={{ color: 'var(--text-muted)', fontSize: '13px' }}>No active site jobs.</div>
            ) : (
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Site / Facility</th>
                    <th>Customer</th>
                    <th>Active</th>
                  </tr>
                </thead>
                <tbody>
                  {report?.siteBreakdown?.map((s) => (
                    <tr key={s.siteId}>
                      <td style={{ fontWeight: 600 }}>📍 {s.siteName}</td>
                      <td style={{ color: 'var(--text-muted)' }}>{s.customerName}</td>
                      <td>
                        <span className="badge badge-high" style={{ fontSize: '11px' }}>
                          {s.activeJobs} Jobs
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </div>

        {/* Recent Work Orders */}
        <div className="table-card" style={{ padding: '24px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
            <h2 style={{ fontSize: '16px', fontWeight: 700 }}>Recent Work Orders</h2>
            <Link to="/work-orders" style={{ fontSize: '13px', color: 'var(--accent-primary)', textDecoration: 'none', fontWeight: 600 }}>
              View All Work Orders →
            </Link>
          </div>

          <table className="data-table">
            <thead>
              <tr>
                <th>Code</th>
                <th>Title</th>
                <th>Priority</th>
                <th>Status</th>
                <th>Customer</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan="5" style={{ textAlign: 'center', padding: '30px' }}>Loading summary...</td>
                </tr>
              ) : recentOrders.length === 0 ? (
                <tr>
                  <td colSpan="5" style={{ textAlign: 'center', padding: '30px', color: 'var(--text-muted)' }}>
                    No work orders recorded yet.
                  </td>
                </tr>
              ) : (
                recentOrders.map((o) => (
                  <tr key={o.id}>
                    <td style={{ fontFamily: 'var(--font-mono)', fontWeight: 600, color: 'var(--accent-cyan)' }}>
                      {o.code}
                    </td>
                    <td style={{ fontWeight: 600 }}>{o.title}</td>
                    <td>
                      <span className={`badge badge-${o.priority?.toLowerCase() || 'medium'}`}>{o.priority}</span>
                    </td>
                    <td>
                      <span className="badge" style={{ backgroundColor: 'var(--bg-primary)', color: 'var(--text-main)' }}>
                        {o.status}
                      </span>
                    </td>
                    <td>{o.customerName}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </main>
    </div>
  );
}

export default DashboardPage;