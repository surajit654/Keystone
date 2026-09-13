import { useState, useEffect } from 'react';
import Sidebar from '../components/Sidebar';
import { workOrderApi, customerApi, dispatcherApi, partApi } from '../api/apiClient';
import { useAuth } from '../context/AuthContext';

const STATUS_COLUMNS = [
  { id: 'NEW', label: 'New / Open', color: 'var(--status-new)', indicator: '#38bdf8' },
  { id: 'ASSIGNED', label: 'Assigned', color: 'var(--status-assigned)', indicator: '#818cf8' },
  { id: 'IN_PROGRESS', label: 'In Progress', color: 'var(--status-in-progress)', indicator: '#fbbf24' },
  { id: 'ON_HOLD', label: 'On Hold', color: 'var(--status-on-hold)', indicator: '#f97316' },
  { id: 'COMPLETED', label: 'Completed', color: 'var(--status-completed)', indicator: '#34d399' },
  { id: 'CLOSED', label: 'Closed', color: 'var(--status-closed)', indicator: '#64748b' },
  { id: 'CANCELLED', label: 'Cancelled', color: 'var(--status-cancelled)', indicator: '#ef4444' },
];

function WorkOrders() {
  const { user } = useAuth();
  const [workOrders, setWorkOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [viewMode, setViewMode] = useState('kanban'); // 'kanban' | 'table'

  // Search and Filter state
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [priorityFilter, setPriorityFilter] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);

  // Reference data
  const [customersList, setCustomersList] = useState([]);
  const [techniciansList, setTechniciansList] = useState([]);
  const [sitesForSelectedCustomer, setSitesForSelectedCustomer] = useState([]);
  const [availableParts, setAvailableParts] = useState([]);

  // Modals & Details state
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [detailTab, setDetailTab] = useState('overview'); // 'overview' | 'parts' | 'time' | 'history'

  // Action form states
  const [logPartData, setLogPartData] = useState({ partId: '', quantity: 1 });
  const [logTimeData, setLogTimeData] = useState({ minutes: 30, note: '' });
  const [statusNote, setStatusNote] = useState('');
  const [assignTechId, setAssignTechId] = useState('');
  const [actionError, setActionError] = useState('');
  const [actionLoading, setActionLoading] = useState(false);

  // Create form state
  const [newOrder, setNewOrder] = useState({
    title: '',
    description: '',
    priority: 'MEDIUM',
    customerId: '',
    siteId: '',
    assigneeId: '',
  });

  const [formError, setFormError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    fetchWorkOrders();
  }, [search, statusFilter, priorityFilter, page, viewMode]);

  useEffect(() => {
    loadDropdownData();
  }, []);

  const loadDropdownData = async () => {
    try {
      const [custData, techData, partsData] = await Promise.all([
        customerApi.getAll(0, 100).catch(() => ({ content: [] })),
        dispatcherApi.getTechnicians().catch(() => []),
        partApi.getAll().catch(() => []),
      ]);
      setCustomersList(custData.content || []);
      setTechniciansList(techData || []);
      setAvailableParts(partsData || []);
    } catch (err) {
      console.error('Failed to load dropdown reference data:', err);
    }
  };

  const handleCustomerChange = async (customerId) => {
    setNewOrder({ ...newOrder, customerId, siteId: '' });
    if (customerId) {
      try {
        const sites = await customerApi.getSites(customerId);
        setSitesForSelectedCustomer(sites || []);
      } catch (err) {
        console.error('Failed to load sites for customer:', err);
      }
    } else {
      setSitesForSelectedCustomer([]);
    }
  };

  const fetchWorkOrders = async () => {
    setLoading(true);
    try {
      const filters = {
        search,
        status: statusFilter,
        priority: priorityFilter,
      };
      const data = await workOrderApi.search(filters, page, viewMode === 'kanban' ? 100 : 15);
      setWorkOrders(data.content || []);
      setTotalPages(data.totalPages || 1);
      setTotalElements(data.totalElements || 0);

      // Refresh selected order if open
      if (selectedOrder) {
        const refreshed = await workOrderApi.getById(selectedOrder.id);
        setSelectedOrder(refreshed);
      }
    } catch (err) {
      console.error('Failed to load work orders:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleOpenDetail = async (order) => {
    try {
      const full = await workOrderApi.getById(order.id);
      setSelectedOrder(full);
      setDetailTab('overview');
      setActionError('');
      setStatusNote('');
    } catch (err) {
      console.error('Failed to load full work order detail:', err);
      setSelectedOrder(order);
    }
  };

  const handleCreateOrder = async (e) => {
    e.preventDefault();
    setFormError('');
    setSubmitting(true);

    try {
      const payload = {
        ...newOrder,
        customerId: Number(newOrder.customerId),
        siteId: Number(newOrder.siteId),
        assigneeId: newOrder.assigneeId ? Number(newOrder.assigneeId) : null,
      };

      await workOrderApi.create(payload);
      setShowCreateModal(false);
      setNewOrder({
        title: '',
        description: '',
        priority: 'MEDIUM',
        customerId: '',
        siteId: '',
        assigneeId: '',
      });
      fetchWorkOrders();
    } catch (err) {
      setFormError(err.message || 'Failed to create work order');
    } finally {
      setSubmitting(false);
    }
  };

  const handleStatusTransition = async (targetStatus) => {
    if (!selectedOrder) return;
    setActionError('');
    setActionLoading(true);
    try {
      const updated = await workOrderApi.transitionStatus(selectedOrder.id, targetStatus, statusNote);
      setSelectedOrder(updated);
      setStatusNote('');
      fetchWorkOrders();
    } catch (err) {
      setActionError(err.message || 'Failed to transition status');
    } finally {
      setActionLoading(false);
    }
  };

  const handleAssignTechnician = async () => {
    if (!selectedOrder || !assignTechId) return;
    setActionError('');
    setActionLoading(true);
    try {
      const updated = await workOrderApi.assignTechnician(selectedOrder.id, Number(assignTechId));
      setSelectedOrder(updated);
      setAssignTechId('');
      fetchWorkOrders();
    } catch (err) {
      setActionError(err.message || 'Failed to assign technician');
    } finally {
      setActionLoading(false);
    }
  };

  const handleLogPart = async (e) => {
    e.preventDefault();
    if (!selectedOrder || !logPartData.partId) return;
    setActionError('');
    setActionLoading(true);
    try {
      await workOrderApi.logParts(selectedOrder.id, Number(logPartData.partId), Number(logPartData.quantity));
      const refreshed = await workOrderApi.getById(selectedOrder.id);
      setSelectedOrder(refreshed);
      setLogPartData({ partId: '', quantity: 1 });
      fetchWorkOrders();
    } catch (err) {
      setActionError(err.message || 'Failed to log parts');
    } finally {
      setActionLoading(false);
    }
  };

  const handleLogTime = async (e) => {
    e.preventDefault();
    if (!selectedOrder) return;
    setActionError('');
    setActionLoading(true);
    try {
      await workOrderApi.logTime(selectedOrder.id, Number(logTimeData.minutes), logTimeData.note);
      const refreshed = await workOrderApi.getById(selectedOrder.id);
      setSelectedOrder(refreshed);
      setLogTimeData({ minutes: 30, note: '' });
      fetchWorkOrders();
    } catch (err) {
      setActionError(err.message || 'Failed to log time');
    } finally {
      setActionLoading(false);
    }
  };

  const getPriorityBadge = (priority) => {
    switch (priority?.toUpperCase()) {
      case 'HIGH':
        return <span className="badge badge-high">High (4h)</span>;
      case 'LOW':
        return <span className="badge badge-low">Low (48h)</span>;
      default:
        return <span className="badge badge-medium">Medium (24h)</span>;
    }
  };

  const getSlaBadge = (order) => {
    if (!order.slaDueAt) return null;
    if (order.slaBreached) {
      return <span className="sla-badge sla-breached">🚨 SLA Breached</span>;
    }
    const due = new Date(order.slaDueAt);
    const now = new Date();
    const hoursLeft = Math.round((due - now) / (1000 * 60 * 60));

    if (hoursLeft <= 2 && hoursLeft >= 0) {
      return <span className="sla-badge" style={{ backgroundColor: 'rgba(245, 158, 11, 0.2)', color: 'var(--accent-amber)' }}>⏳ Due in {hoursLeft}h</span>;
    }

    return <span className="sla-badge sla-ok">⏱️ Due {due.toLocaleDateString([], { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })}</span>;
  };

  return (
    <div className="app-container">
      <Sidebar />

      <main className="main-content">
        <div className="page-header">
          <div>
            <h1 className="page-title">Work Orders & Field Operations</h1>
            <p className="page-subtitle">Governed state machine, SLA monitoring, and inventory tracking</p>
          </div>
          <div className="action-btn-group">
            <button
              onClick={() => {
                setShowCreateModal(true);
                setFormError('');
              }}
              className="btn btn-primary"
            >
              <span>➕</span>
              <span>Create Work Order</span>
            </button>
          </div>
        </div>

        {/* Search, Filter & View Mode Toolbar */}
        <div className="toolbar">
          <div className="search-input-box">
            <span className="search-icon">🔍</span>
            <input
              type="text"
              className="search-input"
              placeholder="Search by code, title, or description..."
              value={search}
              onChange={(e) => {
                setSearch(e.target.value);
                setPage(0);
              }}
            />
          </div>

          <div className="filter-group">
            <select
              className="select-input"
              value={statusFilter}
              onChange={(e) => {
                setStatusFilter(e.target.value);
                setPage(0);
              }}
            >
              <option value="">All Statuses</option>
              {STATUS_COLUMNS.map((col) => (
                <option key={col.id} value={col.id}>{col.label}</option>
              ))}
            </select>

            <select
              className="select-input"
              value={priorityFilter}
              onChange={(e) => {
                setPriorityFilter(e.target.value);
                setPage(0);
              }}
            >
              <option value="">All Priorities</option>
              <option value="HIGH">High Priority</option>
              <option value="MEDIUM">Medium Priority</option>
              <option value="LOW">Low Priority</option>
            </select>

            <div className="view-toggle">
              <button
                className={`toggle-btn ${viewMode === 'kanban' ? 'active' : ''}`}
                onClick={() => setViewMode('kanban')}
              >
                📊 Board
              </button>
              <button
                className={`toggle-btn ${viewMode === 'table' ? 'active' : ''}`}
                onClick={() => setViewMode('table')}
              >
                📄 List
              </button>
            </div>
          </div>
        </div>

        {/* KANBAN BOARD */}
        {viewMode === 'kanban' && (
          <div className="kanban-board">
            {STATUS_COLUMNS.map((col) => {
              const columnOrders = workOrders.filter((wo) =>
                wo.status === col.id || (col.id === 'NEW' && wo.status === 'OPEN')
              );

              return (
                <div key={col.id} className="kanban-column">
                  <div className="kanban-column-header">
                    <div className="column-title-group">
                      <span className="column-indicator" style={{ backgroundColor: col.indicator }}></span>
                      <span className="column-title">{col.label}</span>
                    </div>
                    <span className="column-count">{columnOrders.length}</span>
                  </div>

                  <div className="kanban-card-list">
                    {columnOrders.map((order) => (
                      <div
                        key={order.id}
                        className="kanban-card"
                        onClick={() => handleOpenDetail(order)}
                      >
                        <div className="card-top">
                          <span className="card-code">{order.code}</span>
                          {getPriorityBadge(order.priority)}
                        </div>

                        <div className="card-title">{order.title}</div>

                        <div className="card-location">
                          <span>🏢</span>
                          <span>{order.customerName} · {order.siteName || 'Site'}</span>
                        </div>

                        <div className="card-bottom">
                          <div style={{ fontSize: '11px', color: 'var(--text-muted)' }}>
                            {order.assigneeEmail ? `👤 ${order.assigneeEmail.split('@')[0]}` : '⚪ Unassigned'}
                          </div>
                          <div>{getSlaBadge(order)}</div>
                        </div>
                      </div>
                    ))}

                    {columnOrders.length === 0 && (
                      <div style={{
                        padding: '30px 10px',
                        textAlign: 'center',
                        color: 'var(--text-dim)',
                        fontSize: '12px',
                        fontStyle: 'italic'
                      }}>
                        No orders
                      </div>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        )}

        {/* TABLE VIEW */}
        {viewMode === 'table' && (
          <div className="table-card">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Code</th>
                  <th>Title</th>
                  <th>Priority</th>
                  <th>Status</th>
                  <th>Customer & Site</th>
                  <th>Assignee</th>
                  <th>Parts Cost</th>
                  <th>Labor</th>
                  <th>SLA Due</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {loading ? (
                  <tr>
                    <td colSpan="10" style={{ textAlign: 'center', padding: '40px' }}>Loading work orders...</td>
                  </tr>
                ) : workOrders.length === 0 ? (
                  <tr>
                    <td colSpan="10" style={{ textAlign: 'center', padding: '40px', color: 'var(--text-muted)' }}>
                      No work orders found.
                    </td>
                  </tr>
                ) : (
                  workOrders.map((order) => (
                    <tr key={order.id} style={{ cursor: 'pointer' }} onClick={() => handleOpenDetail(order)}>
                      <td style={{ fontFamily: 'var(--font-mono)', fontWeight: 600, color: 'var(--accent-cyan)' }}>
                        {order.code}
                      </td>
                      <td style={{ fontWeight: 600 }}>{order.title}</td>
                      <td>{getPriorityBadge(order.priority)}</td>
                      <td>
                        <span className="badge" style={{ backgroundColor: 'var(--bg-primary)', color: 'var(--text-main)' }}>
                          {order.status}
                        </span>
                      </td>
                      <td style={{ fontSize: '13px' }}>
                        <div>{order.customerName}</div>
                        <div style={{ color: 'var(--text-muted)', fontSize: '11px' }}>{order.siteName}</div>
                      </td>
                      <td>{order.assigneeEmail || '—'}</td>
                      <td style={{ fontFamily: 'var(--font-mono)', color: 'var(--accent-emerald)' }}>
                        ${order.totalPartsCost ? Number(order.totalPartsCost).toFixed(2) : '0.00'}
                      </td>
                      <td>{order.totalLaborMinutes || 0} min</td>
                      <td>{getSlaBadge(order)}</td>
                      <td>
                        <button
                          className="btn btn-outline"
                          style={{ padding: '4px 10px', fontSize: '12px' }}
                          onClick={(e) => {
                            e.stopPropagation();
                            handleOpenDetail(order);
                          }}
                        >
                          Details 🔍
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>

            <div className="pagination-container">
              <span className="page-info">
                Page {page + 1} of {totalPages} ({totalElements} total work orders)
              </span>
              <div className="page-nav-group">
                <button
                  className="btn btn-secondary"
                  style={{ padding: '6px 12px', fontSize: '12px' }}
                  disabled={page === 0}
                  onClick={() => setPage((p) => Math.max(0, p - 1))}
                >
                  ◀ Previous
                </button>
                <button
                  className="btn btn-secondary"
                  style={{ padding: '6px 12px', fontSize: '12px' }}
                  disabled={page >= totalPages - 1}
                  onClick={() => setPage((p) => p + 1)}
                >
                  Next ▶
                </button>
              </div>
            </div>
          </div>
        )}

        {/* Modal: CREATE WORK ORDER */}
        {showCreateModal && (
          <div className="modal-backdrop" onClick={() => setShowCreateModal(false)}>
            <div className="modal-container" onClick={(e) => e.stopPropagation()}>
              <div className="modal-header">
                <h3 className="modal-title">Create Work Order</h3>
                <button className="close-btn" onClick={() => setShowCreateModal(false)}>✕</button>
              </div>

              {formError && (
                <div style={{ padding: '12px 24px', backgroundColor: 'var(--priority-high-bg)', color: 'var(--priority-high)', fontSize: '13px' }}>
                  ⚠️ {formError}
                </div>
              )}

              <form onSubmit={handleCreateOrder}>
                <div className="modal-body">
                  <div className="form-group">
                    <label className="form-label">Work Order Title *</label>
                    <input
                      className="form-input"
                      type="text"
                      placeholder="e.g. HVAC Chiller compressor inspection"
                      value={newOrder.title}
                      onChange={(e) => setNewOrder({ ...newOrder, title: e.target.value })}
                      required
                    />
                  </div>

                  <div className="form-group">
                    <label className="form-label">Description</label>
                    <textarea
                      className="form-textarea"
                      placeholder="Detailed issue description, symptoms, and requirements..."
                      value={newOrder.description}
                      onChange={(e) => setNewOrder({ ...newOrder, description: e.target.value })}
                    />
                  </div>

                  <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                    <div className="form-group">
                      <label className="form-label">Priority Level *</label>
                      <select
                        className="form-select"
                        value={newOrder.priority}
                        onChange={(e) => setNewOrder({ ...newOrder, priority: e.target.value })}
                        required
                      >
                        <option value="HIGH">🔴 High (4h SLA)</option>
                        <option value="MEDIUM">🟡 Medium (24h SLA)</option>
                        <option value="LOW">🟢 Low (48h SLA)</option>
                      </select>
                    </div>

                    <div className="form-group">
                      <label className="form-label">Assign Technician</label>
                      <select
                        className="form-select"
                        value={newOrder.assigneeId}
                        onChange={(e) => setNewOrder({ ...newOrder, assigneeId: e.target.value })}
                      >
                        <option value="">Unassigned (Queue as New)</option>
                        {techniciansList.map((t) => (
                          <option key={t.id} value={t.id}>{t.email}</option>
                        ))}
                      </select>
                    </div>
                  </div>

                  <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                    <div className="form-group">
                      <label className="form-label">Customer Organization *</label>
                      <select
                        className="form-select"
                        value={newOrder.customerId}
                        onChange={(e) => handleCustomerChange(e.target.value)}
                        required
                      >
                        <option value="">Select customer...</option>
                        {customersList.map((c) => (
                          <option key={c.id} value={c.id}>{c.name}</option>
                        ))}
                      </select>
                    </div>

                    <div className="form-group">
                      <label className="form-label">Site / Facility *</label>
                      <select
                        className="form-select"
                        value={newOrder.siteId}
                        onChange={(e) => setNewOrder({ ...newOrder, siteId: e.target.value })}
                        required
                        disabled={!newOrder.customerId}
                      >
                        <option value="">
                          {newOrder.customerId ? 'Select site location...' : 'Select customer first'}
                        </option>
                        {sitesForSelectedCustomer.map((s) => (
                          <option key={s.id} value={s.id}>{s.name} ({s.city || 'Main'})</option>
                        ))}
                      </select>
                    </div>
                  </div>
                </div>

                <div className="modal-footer">
                  <button type="button" className="btn btn-secondary" onClick={() => setShowCreateModal(false)}>
                    Cancel
                  </button>
                  <button type="submit" className="btn btn-primary" disabled={submitting}>
                    {submitting ? 'Generating Code & Dispatching...' : 'Dispatch Work Order'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}

        {/* Modal: ENHANCED WORK ORDER DETAILS (Overview, Parts, Time, History) */}
        {selectedOrder && (
          <div className="modal-backdrop" onClick={() => setSelectedOrder(null)}>
            <div className="modal-container" style={{ maxWidth: '720px' }} onClick={(e) => e.stopPropagation()}>
              <div className="modal-header">
                <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                  <span className="card-code" style={{ fontSize: '16px' }}>{selectedOrder.code}</span>
                  <span className="badge" style={{ backgroundColor: 'var(--bg-primary)', color: 'var(--text-main)' }}>
                    {selectedOrder.status}
                  </span>
                </div>
                <button className="close-btn" onClick={() => setSelectedOrder(null)}>✕</button>
              </div>

              {actionError && (
                <div style={{ padding: '12px 24px', backgroundColor: 'var(--priority-high-bg)', color: 'var(--priority-high)', fontSize: '13px' }}>
                  ⚠️ {actionError}
                </div>
              )}

              {/* Navigation Tabs */}
              <div style={{ display: 'flex', gap: '8px', padding: '12px 24px 0', borderBottom: '1px solid var(--border-subtle)' }}>
                {['overview', 'parts', 'time', 'history'].map((tab) => (
                  <button
                    key={tab}
                    className={`toggle-btn ${detailTab === tab ? 'active' : ''}`}
                    style={{ textTransform: 'capitalize', padding: '8px 16px', fontSize: '13px' }}
                    onClick={() => setDetailTab(tab)}
                  >
                    {tab === 'parts' && '📦 '}
                    {tab === 'time' && '⏱️ '}
                    {tab === 'history' && '📜 '}
                    {tab}
                  </button>
                ))}
              </div>

              <div className="modal-body" style={{ maxHeight: '60vh', overflowY: 'auto' }}>
                {/* TAB 1: OVERVIEW & STATUS TRANSITIONS */}
                {detailTab === 'overview' && (
                  <>
                    <div>
                      <h3 style={{ fontSize: '18px', fontWeight: 700, marginBottom: '6px' }}>{selectedOrder.title}</h3>
                      <p style={{ color: 'var(--text-muted)', fontSize: '14px', lineHeight: 1.5 }}>
                        {selectedOrder.description || 'No detailed description provided.'}
                      </p>
                    </div>

                    <div style={{
                      display: 'grid',
                      gridTemplateColumns: '1fr 1fr',
                      gap: '12px',
                      padding: '16px',
                      backgroundColor: 'var(--bg-primary)',
                      borderRadius: '8px',
                      border: '1px solid var(--border-subtle)'
                    }}>
                      <div>
                        <div style={{ fontSize: '11px', color: 'var(--text-dim)', textTransform: 'uppercase', fontWeight: 600 }}>Priority</div>
                        <div style={{ marginTop: '4px' }}>{getPriorityBadge(selectedOrder.priority)}</div>
                      </div>
                      <div>
                        <div style={{ fontSize: '11px', color: 'var(--text-dim)', textTransform: 'uppercase', fontWeight: 600 }}>SLA Status</div>
                        <div style={{ marginTop: '4px' }}>{getSlaBadge(selectedOrder)}</div>
                      </div>
                      <div>
                        <div style={{ fontSize: '11px', color: 'var(--text-dim)', textTransform: 'uppercase', fontWeight: 600 }}>Customer</div>
                        <div style={{ fontWeight: 600, fontSize: '13px', marginTop: '4px' }}>{selectedOrder.customerName}</div>
                      </div>
                      <div>
                        <div style={{ fontSize: '11px', color: 'var(--text-dim)', textTransform: 'uppercase', fontWeight: 600 }}>Site Location</div>
                        <div style={{ fontSize: '13px', marginTop: '4px' }}>{selectedOrder.siteName || 'Main Facility'}</div>
                      </div>
                      <div>
                        <div style={{ fontSize: '11px', color: 'var(--text-dim)', textTransform: 'uppercase', fontWeight: 600 }}>Assigned Technician</div>
                        <div style={{ fontSize: '13px', marginTop: '4px', color: selectedOrder.assigneeEmail ? 'var(--accent-cyan)' : 'var(--text-muted)' }}>
                          {selectedOrder.assigneeEmail || 'Unassigned'}
                        </div>
                      </div>
                      <div>
                        <div style={{ fontSize: '11px', color: 'var(--text-dim)', textTransform: 'uppercase', fontWeight: 600 }}>Target SLA Due</div>
                        <div style={{ fontSize: '12px', marginTop: '4px' }}>
                          {selectedOrder.slaDueAt ? new Date(selectedOrder.slaDueAt).toLocaleString() : 'N/A'}
                        </div>
                      </div>
                    </div>

                    {/* Governed State Machine Action Controls */}
                    <div style={{
                      padding: '16px',
                      backgroundColor: 'rgba(99, 102, 241, 0.08)',
                      borderRadius: '8px',
                      border: '1px solid var(--border-glow)'
                    }}>
                      <h4 style={{ fontSize: '14px', fontWeight: 700, marginBottom: '12px' }}>
                        ⚙️ Governed Status Transitions
                      </h4>

                      <div className="form-group" style={{ marginBottom: '12px' }}>
                        <input
                          className="form-input"
                          type="text"
                          placeholder="Optional transition note or comment..."
                          value={statusNote}
                          onChange={(e) => setStatusNote(e.target.value)}
                        />
                      </div>

                      <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
                        {/* Technician Actions */}
                        {selectedOrder.status === 'ASSIGNED' && (
                          <button
                            className="btn btn-primary"
                            disabled={actionLoading}
                            onClick={() => handleStatusTransition('IN_PROGRESS')}
                          >
                            ▶ Start Work (IN_PROGRESS)
                          </button>
                        )}

                        {selectedOrder.status === 'IN_PROGRESS' && (
                          <>
                            <button
                              className="btn btn-secondary"
                              disabled={actionLoading}
                              onClick={() => handleStatusTransition('ON_HOLD')}
                            >
                              ⏸️ Put On Hold
                            </button>
                            <button
                              className="btn btn-primary"
                              style={{ backgroundColor: 'var(--accent-emerald)' }}
                              disabled={actionLoading}
                              onClick={() => handleStatusTransition('COMPLETED')}
                            >
                              ✓ Mark Completed
                            </button>
                          </>
                        )}

                        {selectedOrder.status === 'ON_HOLD' && (
                          <button
                            className="btn btn-primary"
                            disabled={actionLoading}
                            onClick={() => handleStatusTransition('IN_PROGRESS')}
                          >
                            ▶ Resume Work
                          </button>
                        )}

                        {/* Manager Sign-off Action */}
                        {selectedOrder.status === 'COMPLETED' && (
                          <button
                            className="btn btn-primary"
                            style={{ backgroundColor: 'var(--accent-purple)' }}
                            disabled={actionLoading}
                            onClick={() => handleStatusTransition('CLOSED')}
                          >
                            🔒 Manager Close & Sign Off
                          </button>
                        )}

                        {/* Dispatcher / Manager Cancellation */}
                        {selectedOrder.status !== 'CLOSED' && selectedOrder.status !== 'CANCELLED' && selectedOrder.status !== 'COMPLETED' && (
                          <button
                            className="btn btn-outline"
                            style={{ color: 'var(--accent-rose)', borderColor: 'var(--accent-rose)' }}
                            disabled={actionLoading}
                            onClick={() => handleStatusTransition('CANCELLED')}
                          >
                            ✕ Cancel Work Order
                          </button>
                        )}
                      </div>

                      {/* Dispatcher Reassignment Control */}
                      {selectedOrder.status !== 'CLOSED' && selectedOrder.status !== 'CANCELLED' && (
                        <div style={{ marginTop: '16px', display: 'flex', gap: '8px', alignItems: 'center' }}>
                          <select
                            className="select-input"
                            style={{ flex: 1 }}
                            value={assignTechId}
                            onChange={(e) => setAssignTechId(e.target.value)}
                          >
                            <option value="">Reassign to technician...</option>
                            {techniciansList.map((t) => (
                              <option key={t.id} value={t.id}>{t.email}</option>
                            ))}
                          </select>
                          <button
                            className="btn btn-secondary"
                            disabled={!assignTechId || actionLoading}
                            onClick={handleAssignTechnician}
                          >
                            Reassign
                          </button>
                        </div>
                      )}
                    </div>
                  </>
                )}

                {/* TAB 2: PARTS USAGE & INVENTORY */}
                {detailTab === 'parts' && (
                  <div>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
                      <h4 style={{ fontSize: '15px', fontWeight: 700 }}>Parts Consumed on Job</h4>
                      <span style={{ fontSize: '14px', fontWeight: 700, color: 'var(--accent-emerald)' }}>
                        Total Parts Cost: ${Number(selectedOrder.totalPartsCost || 0).toFixed(2)}
                      </span>
                    </div>

                    {/* Log Part Form */}
                    {selectedOrder.status === 'IN_PROGRESS' && (
                      <form onSubmit={handleLogPart} style={{
                        display: 'flex',
                        gap: '10px',
                        marginBottom: '16px',
                        padding: '12px',
                        backgroundColor: 'var(--bg-primary)',
                        borderRadius: '8px'
                      }}>
                        <select
                          className="form-select"
                          style={{ flex: 2 }}
                          value={logPartData.partId}
                          onChange={(e) => setLogPartData({ ...logPartData, partId: e.target.value })}
                          required
                        >
                          <option value="">Select inventory part...</option>
                          {availableParts.map((p) => (
                            <option key={p.id} value={p.id} disabled={p.stockQuantity <= 0}>
                              {p.name} ({p.partNumber}) - Stock: {p.stockQuantity} - ${Number(p.unitCost).toFixed(2)}
                            </option>
                          ))}
                        </select>
                        <input
                          type="number"
                          min="1"
                          className="form-input"
                          style={{ width: '80px' }}
                          placeholder="Qty"
                          value={logPartData.quantity}
                          onChange={(e) => setLogPartData({ ...logPartData, quantity: e.target.value })}
                          required
                        />
                        <button type="submit" className="btn btn-primary" disabled={actionLoading}>
                          Log Part
                        </button>
                      </form>
                    )}

                    {selectedOrder.partsUsed?.length === 0 ? (
                      <div style={{ padding: '24px', textAlign: 'center', color: 'var(--text-muted)' }}>
                        No parts logged on this job yet.
                      </div>
                    ) : (
                      <table className="data-table">
                        <thead>
                          <tr>
                            <th>Part</th>
                            <th>Number</th>
                            <th>Qty</th>
                            <th>Unit Cost</th>
                            <th>Total</th>
                          </tr>
                        </thead>
                        <tbody>
                          {selectedOrder.partsUsed?.map((pu) => (
                            <tr key={pu.id}>
                              <td>{pu.partName}</td>
                              <td style={{ fontFamily: 'var(--font-mono)' }}>{pu.partNumber || '—'}</td>
                              <td>{pu.quantity}</td>
                              <td>${Number(pu.unitCost).toFixed(2)}</td>
                              <td style={{ fontWeight: 600, color: 'var(--accent-emerald)' }}>
                                ${Number(pu.totalCost).toFixed(2)}
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    )}
                  </div>
                )}

                {/* TAB 3: TIME LOGGING */}
                {detailTab === 'time' && (
                  <div>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
                      <h4 style={{ fontSize: '15px', fontWeight: 700 }}>Technician Labor Logs</h4>
                      <span style={{ fontSize: '14px', fontWeight: 700, color: 'var(--accent-cyan)' }}>
                        Total Labor Time: {selectedOrder.totalLaborMinutes || 0} Minutes
                      </span>
                    </div>

                    {/* Log Time Form */}
                    {selectedOrder.status !== 'CLOSED' && selectedOrder.status !== 'CANCELLED' && (
                      <form onSubmit={handleLogTime} style={{
                        display: 'flex',
                        gap: '10px',
                        marginBottom: '16px',
                        padding: '12px',
                        backgroundColor: 'var(--bg-primary)',
                        borderRadius: '8px'
                      }}>
                        <input
                          type="number"
                          min="1"
                          className="form-input"
                          style={{ width: '110px' }}
                          placeholder="Minutes"
                          value={logTimeData.minutes}
                          onChange={(e) => setLogTimeData({ ...logTimeData, minutes: e.target.value })}
                          required
                        />
                        <input
                          type="text"
                          className="form-input"
                          style={{ flex: 1 }}
                          placeholder="Activity note (e.g. Diagnostic & wiring replacement)..."
                          value={logTimeData.note}
                          onChange={(e) => setLogTimeData({ ...logTimeData, note: e.target.value })}
                        />
                        <button type="submit" className="btn btn-primary" disabled={actionLoading}>
                          Log Labor
                        </button>
                      </form>
                    )}

                    {selectedOrder.timeLogs?.length === 0 ? (
                      <div style={{ padding: '24px', textAlign: 'center', color: 'var(--text-muted)' }}>
                        No time logged on this job yet.
                      </div>
                    ) : (
                      <table className="data-table">
                        <thead>
                          <tr>
                            <th>Technician</th>
                            <th>Time Spent</th>
                            <th>Activity Note</th>
                          </tr>
                        </thead>
                        <tbody>
                          {selectedOrder.timeLogs?.map((tl) => (
                            <tr key={tl.id}>
                              <td>{tl.technicianEmail}</td>
                              <td style={{ fontWeight: 600 }}>{tl.minutes} min</td>
                              <td style={{ color: 'var(--text-muted)' }}>{tl.note || '—'}</td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    )}
                  </div>
                )}

                {/* TAB 4: AUDIT STATUS HISTORY TIMELINE */}
                {detailTab === 'history' && (
                  <div>
                    <h4 style={{ fontSize: '15px', fontWeight: 700, marginBottom: '16px' }}>
                      Audit Status History Timeline
                    </h4>

                    {selectedOrder.statusHistory?.length === 0 ? (
                      <div style={{ padding: '24px', textAlign: 'center', color: 'var(--text-muted)' }}>
                        No status transitions recorded.
                      </div>
                    ) : (
                      <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                        {selectedOrder.statusHistory?.map((h, i) => (
                          <div key={h.id || i} style={{
                            padding: '12px 16px',
                            backgroundColor: 'var(--bg-primary)',
                            borderRadius: '8px',
                            border: '1px solid var(--border-subtle)',
                            display: 'flex',
                            justifyContent: 'space-between',
                            alignItems: 'center'
                          }}>
                            <div>
                              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                                <span className="badge" style={{ backgroundColor: 'var(--bg-secondary)', color: 'var(--accent-cyan)' }}>
                                  {h.status}
                                </span>
                                <span style={{ fontSize: '13px', fontWeight: 600 }}>
                                  by {h.changedByEmail || 'System'}
                                </span>
                              </div>
                              {h.note && (
                                <div style={{ fontSize: '12px', color: 'var(--text-muted)', marginTop: '4px' }}>
                                  💬 {h.note}
                                </div>
                              )}
                            </div>
                            <div style={{ fontSize: '12px', color: 'var(--text-dim)' }}>
                              {h.changedAt ? new Date(h.changedAt).toLocaleString() : ''}
                            </div>
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                )}
              </div>

              <div className="modal-footer">
                <button className="btn btn-secondary" onClick={() => setSelectedOrder(null)}>
                  Close
                </button>
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}

export default WorkOrders;