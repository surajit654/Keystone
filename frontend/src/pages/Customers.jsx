import { useState, useEffect } from 'react';
import Sidebar from '../components/Sidebar';
import { customerApi } from '../api/apiClient';

function Customers() {
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);

  // Selected customer for sites drawer/view
  const [selectedCustomer, setSelectedCustomer] = useState(null);
  const [sites, setSites] = useState([]);
  const [loadingSites, setLoadingSites] = useState(false);

  // Modals
  const [showAddCustomerModal, setShowAddCustomerModal] = useState(false);
  const [showAddSiteModal, setShowAddSiteModal] = useState(false);

  // Form states
  const [newCustomer, setNewCustomer] = useState({ name: '', email: '', phone: '' });
  const [newSite, setNewSite] = useState({ name: '', address: '', city: '' });
  const [formError, setFormError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    fetchCustomers();
  }, [page, search]);

  const fetchCustomers = async () => {
    setLoading(true);
    try {
      const data = await customerApi.getAll(page, 10, search);
      setCustomers(data.content || []);
      setTotalPages(data.totalPages || 1);
      setTotalElements(data.totalElements || 0);

      // If a customer was selected, update its sites
      if (selectedCustomer) {
        fetchSites(selectedCustomer.id);
      }
    } catch (err) {
      console.error('Failed to load customers:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleSelectCustomer = async (cust) => {
    setSelectedCustomer(cust);
    fetchSites(cust.id);
  };

  const fetchSites = async (customerId) => {
    setLoadingSites(true);
    try {
      const data = await customerApi.getSites(customerId);
      setSites(data || []);
    } catch (err) {
      console.error('Failed to load customer sites:', err);
    } finally {
      setLoadingSites(false);
    }
  };

  const handleCreateCustomer = async (e) => {
    e.preventDefault();
    setFormError('');
    setSubmitting(true);
    try {
      await customerApi.create(newCustomer);
      setShowAddCustomerModal(false);
      setNewCustomer({ name: '', email: '', phone: '' });
      fetchCustomers();
    } catch (err) {
      setFormError(err.message || 'Failed to create customer');
    } finally {
      setSubmitting(false);
    }
  };

  const handleCreateSite = async (e) => {
    e.preventDefault();
    setFormError('');
    setSubmitting(true);
    try {
      await customerApi.createSite(selectedCustomer.id, newSite);
      setShowAddSiteModal(false);
      setNewSite({ name: '', address: '', city: '' });
      fetchSites(selectedCustomer.id);
    } catch (err) {
      setFormError(err.message || 'Failed to add site');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="app-container">
      <Sidebar />

      <main className="main-content">
        <div className="page-header">
          <div>
            <h1 className="page-title">Customer & Site Directory</h1>
            <p className="page-subtitle">Manage client organizations, facilities, and service locations</p>
          </div>
          <div className="action-btn-group">
            <button
              onClick={() => setShowAddCustomerModal(true)}
              className="btn btn-primary"
            >
              <span>➕</span>
              <span>New Customer</span>
            </button>
          </div>
        </div>

        {/* Search & Filter Toolbar */}
        <div className="toolbar">
          <div className="search-input-box">
            <span className="search-icon">🔍</span>
            <input
              type="text"
              className="search-input"
              placeholder="Search customers by name or email..."
              value={search}
              onChange={(e) => {
                setSearch(e.target.value);
                setPage(0);
              }}
            />
          </div>
          <div className="filter-group">
            <span style={{ fontSize: '13px', color: 'var(--text-muted)' }}>
              Showing {customers.length} of {totalElements} organizations
            </span>
          </div>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: selectedCustomer ? '1fr 1fr' : '1fr', gap: '24px' }}>
          {/* Customer Table */}
          <div className="table-card">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Customer Name</th>
                  <th>Contact Email</th>
                  <th>Phone</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {loading ? (
                  <tr>
                    <td colSpan="4" style={{ textAlign: 'center', padding: '40px' }}>
                      Loading customers...
                    </td>
                  </tr>
                ) : customers.length === 0 ? (
                  <tr>
                    <td colSpan="4" style={{ textAlign: 'center', padding: '40px', color: 'var(--text-muted)' }}>
                      No customers found matching your search.
                    </td>
                  </tr>
                ) : (
                  customers.map((cust) => (
                    <tr
                      key={cust.id}
                      style={{
                        backgroundColor: selectedCustomer?.id === cust.id ? 'rgba(99, 102, 241, 0.1)' : 'transparent',
                        cursor: 'pointer',
                      }}
                      onClick={() => handleSelectCustomer(cust)}
                    >
                      <td style={{ fontWeight: 600 }}>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                          <span style={{ width: '8px', height: '8px', borderRadius: '50%', backgroundColor: 'var(--accent-cyan)' }}></span>
                          <span>{cust.name}</span>
                        </div>
                      </td>
                      <td style={{ color: 'var(--text-muted)' }}>{cust.email || '—'}</td>
                      <td>{cust.phone || '—'}</td>
                      <td>
                        <button
                          className="btn btn-outline"
                          style={{ padding: '4px 10px', fontSize: '12px' }}
                          onClick={(e) => {
                            e.stopPropagation();
                            handleSelectCustomer(cust);
                          }}
                        >
                          View Sites 🏢
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>

            {/* Pagination */}
            <div className="pagination-container">
              <span className="page-info">
                Page {page + 1} of {totalPages}
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

          {/* Customer Sites Side Panel */}
          {selectedCustomer && (
            <div className="table-card" style={{ padding: '20px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
                <div>
                  <h2 style={{ fontSize: '18px', fontWeight: 700 }}>
                    {selectedCustomer.name} — Sites
                  </h2>
                  <p style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
                    Registered building locations & facilities
                  </p>
                </div>
                <div style={{ display: 'flex', gap: '8px' }}>
                  <button
                    onClick={() => setShowAddSiteModal(true)}
                    className="btn btn-primary"
                    style={{ padding: '6px 12px', fontSize: '12px' }}
                  >
                    ➕ Add Site
                  </button>
                  <button
                    onClick={() => setSelectedCustomer(null)}
                    className="close-btn"
                    title="Close"
                  >
                    ✕
                  </button>
                </div>
              </div>

              {loadingSites ? (
                <div style={{ padding: '30px', textAlign: 'center' }}>Loading sites...</div>
              ) : sites.length === 0 ? (
                <div style={{
                  padding: '30px',
                  textAlign: 'center',
                  backgroundColor: 'var(--bg-primary)',
                  borderRadius: '8px',
                  color: 'var(--text-muted)',
                  fontSize: '13px'
                }}>
                  No sites registered yet for {selectedCustomer.name}. Click <strong>Add Site</strong> to add one.
                </div>
              ) : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                  {sites.map((site) => (
                    <div
                      key={site.id}
                      style={{
                        padding: '14px',
                        backgroundColor: 'var(--bg-primary)',
                        border: '1px solid var(--border-subtle)',
                        borderRadius: '8px',
                        display: 'flex',
                        justifyContent: 'space-between',
                        alignItems: 'center'
                      }}
                    >
                      <div>
                        <div style={{ fontWeight: 600, fontSize: '14px', color: 'var(--text-main)' }}>
                          📍 {site.name}
                        </div>
                        <div style={{ fontSize: '12px', color: 'var(--text-muted)', marginTop: '2px' }}>
                          {site.address ? `${site.address}, ` : ''}{site.city || 'No city specified'}
                        </div>
                      </div>
                      <span className="badge badge-low" style={{ fontSize: '10px' }}>Active Site</span>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}
        </div>

        {/* Modal: Create Customer */}
        {showAddCustomerModal && (
          <div className="modal-backdrop" onClick={() => setShowAddCustomerModal(false)}>
            <div className="modal-container" onClick={(e) => e.stopPropagation()}>
              <div className="modal-header">
                <h3 className="modal-title">Register New Customer</h3>
                <button className="close-btn" onClick={() => setShowAddCustomerModal(false)}>✕</button>
              </div>

              {formError && (
                <div style={{ padding: '12px 24px', backgroundColor: 'var(--priority-high-bg)', color: 'var(--priority-high)', fontSize: '13px' }}>
                  ⚠️ {formError}
                </div>
              )}

              <form onSubmit={handleCreateCustomer}>
                <div className="modal-body">
                  <div className="form-group">
                    <label className="form-label">Company / Client Name *</label>
                    <input
                      className="form-input"
                      type="text"
                      placeholder="e.g. Acme Commercial Tower"
                      value={newCustomer.name}
                      onChange={(e) => setNewCustomer({ ...newCustomer, name: e.target.value })}
                      required
                    />
                  </div>

                  <div className="form-group">
                    <label className="form-label">Contact Email</label>
                    <input
                      className="form-input"
                      type="email"
                      placeholder="facilities@acme.com"
                      value={newCustomer.email}
                      onChange={(e) => setNewCustomer({ ...newCustomer, email: e.target.value })}
                    />
                  </div>

                  <div className="form-group">
                    <label className="form-label">Phone Number</label>
                    <input
                      className="form-input"
                      type="text"
                      placeholder="+1 (555) 019-2834"
                      value={newCustomer.phone}
                      onChange={(e) => setNewCustomer({ ...newCustomer, phone: e.target.value })}
                    />
                  </div>
                </div>

                <div className="modal-footer">
                  <button type="button" className="btn btn-secondary" onClick={() => setShowAddCustomerModal(false)}>
                    Cancel
                  </button>
                  <button type="submit" className="btn btn-primary" disabled={submitting}>
                    {submitting ? 'Saving...' : 'Save Customer'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}

        {/* Modal: Add Site */}
        {showAddSiteModal && selectedCustomer && (
          <div className="modal-backdrop" onClick={() => setShowAddSiteModal(false)}>
            <div className="modal-container" onClick={(e) => e.stopPropagation()}>
              <div className="modal-header">
                <h3 className="modal-title">Add Site for {selectedCustomer.name}</h3>
                <button className="close-btn" onClick={() => setShowAddSiteModal(false)}>✕</button>
              </div>

              {formError && (
                <div style={{ padding: '12px 24px', backgroundColor: 'var(--priority-high-bg)', color: 'var(--priority-high)', fontSize: '13px' }}>
                  ⚠️ {formError}
                </div>
              )}

              <form onSubmit={handleCreateSite}>
                <div className="modal-body">
                  <div className="form-group">
                    <label className="form-label">Facility / Site Name *</label>
                    <input
                      className="form-input"
                      type="text"
                      placeholder="e.g. North Facility - Building 4"
                      value={newSite.name}
                      onChange={(e) => setNewSite({ ...newSite, name: e.target.value })}
                      required
                    />
                  </div>

                  <div className="form-group">
                    <label className="form-label">Street Address</label>
                    <input
                      className="form-input"
                      type="text"
                      placeholder="e.g. 500 Enterprise Way"
                      value={newSite.address}
                      onChange={(e) => setNewSite({ ...newSite, address: e.target.value })}
                    />
                  </div>

                  <div className="form-group">
                    <label className="form-label">City</label>
                    <input
                      className="form-input"
                      type="text"
                      placeholder="e.g. San Francisco"
                      value={newSite.city}
                      onChange={(e) => setNewSite({ ...newSite, city: e.target.value })}
                    />
                  </div>
                </div>

                <div className="modal-footer">
                  <button type="button" className="btn btn-secondary" onClick={() => setShowAddSiteModal(false)}>
                    Cancel
                  </button>
                  <button type="submit" className="btn btn-primary" disabled={submitting}>
                    {submitting ? 'Adding...' : 'Add Site'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}

export default Customers;