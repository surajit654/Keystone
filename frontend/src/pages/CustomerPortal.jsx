import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { apiRequest } from '../api/apiClient';

function CustomerPortal() {
  const { user, logout } = useAuth();
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [formError, setFormError] = useState('');

  const [newRequest, setNewRequest] = useState({
    title: '',
    description: '',
    priority: 'MEDIUM',
  });

  useEffect(() => {
    fetchMyRequests();
  }, []);

  const fetchMyRequests = async () => {
    setLoading(true);
    try {
      const data = await apiRequest('/customer/requests');
      setRequests(data || []);
    } catch (err) {
      console.error('Failed to load customer requests:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateRequest = async (e) => {
    e.preventDefault();
    setFormError('');
    setSubmitting(true);
    try {
      await apiRequest('/customer/requests', {
        method: 'POST',
        body: JSON.stringify(newRequest),
      });
      setShowCreateModal(false);
      setNewRequest({ title: '', description: '', priority: 'MEDIUM' });
      fetchMyRequests();
    } catch (err) {
      setFormError(err.message || 'Failed to submit service request');
    } finally {
      setSubmitting(false);
    }
  };

  const getStatusStep = (status) => {
    const s = status?.toUpperCase();
    if (s === 'COMPLETED' || s === 'CLOSED') return 3;
    if (s === 'IN_PROGRESS' || s === 'ON_HOLD') return 2;
    if (s === 'ASSIGNED') return 1;
    return 0; // NEW or PENDING
  };

  return (
    <div style={{ minHeight: '100vh', backgroundColor: 'var(--bg-primary)', color: 'var(--text-main)' }}>
      {/* Top Navbar */}
      <header style={{
        backgroundColor: 'var(--bg-secondary)',
        borderBottom: '1px solid var(--border-subtle)',
        padding: '16px 32px',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between'
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <div className="brand-logo" style={{ width: '36px', height: '36px', fontSize: '18px' }}>K</div>
          <div>
            <div style={{ fontWeight: 700, fontSize: '16px' }}>KEYSTONE Customer Portal</div>
            <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>Meridian Facilities Self-Service</div>
          </div>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
          <div style={{ fontSize: '13px', color: 'var(--text-muted)' }}>
            Logged in as <strong>{user?.email}</strong>
          </div>
          <button onClick={logout} className="logout-btn">
            Sign Out
          </button>
        </div>
      </header>

      {/* Main Container */}
      <main style={{ maxWidth: '1000px', margin: '40px auto', padding: '0 20px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '28px' }}>
          <div>
            <h1 style={{ fontSize: '24px', fontWeight: 700 }}>My Service Requests</h1>
            <p style={{ fontSize: '14px', color: 'var(--text-muted)', marginTop: '4px' }}>
              Track live maintenance progress and submit new work requests
            </p>
          </div>
          <button
            onClick={() => setShowCreateModal(true)}
            className="btn btn-primary"
            style={{ padding: '10px 20px' }}
          >
            <span>➕</span>
            <span>Raise New Request</span>
          </button>
        </div>

        {/* Requests List */}
        {loading ? (
          <div style={{ textAlign: 'center', padding: '60px', color: 'var(--text-muted)' }}>
            Loading your service requests...
          </div>
        ) : requests.length === 0 ? (
          <div className="table-card" style={{ padding: '60px', textAlign: 'center' }}>
            <div style={{ fontSize: '40px', marginBottom: '16px' }}>🏢</div>
            <h2 style={{ fontSize: '18px', fontWeight: 700 }}>No Service Requests Yet</h2>
            <p style={{ color: 'var(--text-muted)', fontSize: '14px', maxWidth: '400px', margin: '8px auto 20px' }}>
              Have an equipment breakdown or maintenance need? Click "Raise New Request" to alert the dispatch team.
            </p>
            <button onClick={() => setShowCreateModal(true)} className="btn btn-primary">
              Submit First Request
            </button>
          </div>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
            {requests.map((req) => {
              const currentStep = getStatusStep(req.status);
              return (
                <div key={req.id} className="table-card" style={{ padding: '24px' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '16px' }}>
                    <div>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '6px' }}>
                        <span className="badge" style={{ backgroundColor: 'var(--bg-primary)', color: 'var(--accent-cyan)' }}>
                          {req.code || `REQUEST #${req.id}`}
                        </span>
                        <span className={`badge badge-${req.priority?.toLowerCase() || 'medium'}`}>
                          {req.priority} PRIORITY
                        </span>
                      </div>
                      <h2 style={{ fontSize: '18px', fontWeight: 700 }}>{req.title}</h2>
                      <p style={{ color: 'var(--text-muted)', fontSize: '14px', marginTop: '6px' }}>
                        {req.description}
                      </p>
                    </div>

                    <div style={{ fontSize: '12px', color: 'var(--text-dim)' }}>
                      Submitted: {req.createdAt ? new Date(req.createdAt).toLocaleDateString() : 'Recent'}
                    </div>
                  </div>

                  {/* Real-time Status Progress Tracker Timeline */}
                  <div style={{
                    marginTop: '24px',
                    padding: '16px 20px',
                    backgroundColor: 'var(--bg-primary)',
                    borderRadius: '10px',
                    border: '1px solid var(--border-subtle)'
                  }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', position: 'relative' }}>
                      {['Submitted', 'Assigned', 'In Progress', 'Completed'].map((stepLabel, idx) => {
                        const isDone = idx <= currentStep;
                        const isCurrent = idx === currentStep;
                        return (
                          <div key={stepLabel} style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', flex: 1 }}>
                            <div style={{
                              width: '28px',
                              height: '28px',
                              borderRadius: '50%',
                              backgroundColor: isDone ? 'var(--accent-primary)' : 'var(--bg-tertiary)',
                              color: 'white',
                              display: 'flex',
                              alignItems: 'center',
                              justifyContent: 'center',
                              fontSize: '12px',
                              fontWeight: 700,
                              boxShadow: isCurrent ? 'var(--shadow-glow)' : 'none',
                              marginBottom: '6px',
                              zIndex: 2,
                            }}>
                              {isDone ? '✓' : idx + 1}
                            </div>
                            <span style={{
                              fontSize: '11px',
                              fontWeight: isCurrent ? 700 : 500,
                              color: isDone ? 'var(--text-main)' : 'var(--text-dim)',
                              textTransform: 'uppercase',
                              letterSpacing: '0.5px'
                            }}>
                              {stepLabel}
                            </span>
                          </div>
                        );
                      })}
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        )}

        {/* Modal: RAISE NEW REQUEST */}
        {showCreateModal && (
          <div className="modal-backdrop" onClick={() => setShowCreateModal(false)}>
            <div className="modal-container" onClick={(e) => e.stopPropagation()}>
              <div className="modal-header">
                <h3 className="modal-title">Submit Maintenance Service Request</h3>
                <button className="close-btn" onClick={() => setShowCreateModal(false)}>✕</button>
              </div>

              {formError && (
                <div style={{ padding: '12px 24px', backgroundColor: 'var(--priority-high-bg)', color: 'var(--priority-high)', fontSize: '13px' }}>
                  ⚠️ {formError}
                </div>
              )}

              <form onSubmit={handleCreateRequest}>
                <div className="modal-body">
                  <div className="form-group">
                    <label className="form-label">Issue Summary / Title *</label>
                    <input
                      className="form-input"
                      type="text"
                      placeholder="e.g. Server Room HVAC Unit blowing warm air"
                      value={newRequest.title}
                      onChange={(e) => setNewRequest({ ...newRequest, title: e.target.value })}
                      required
                    />
                  </div>

                  <div className="form-group">
                    <label className="form-label">Detailed Description *</label>
                    <textarea
                      className="form-textarea"
                      placeholder="Please provide symptoms, affected building areas, or equipment details..."
                      value={newRequest.description}
                      onChange={(e) => setNewRequest({ ...newRequest, description: e.target.value })}
                      required
                    />
                  </div>

                  <div className="form-group">
                    <label className="form-label">Urgency / Priority *</label>
                    <select
                      className="form-select"
                      value={newRequest.priority}
                      onChange={(e) => setNewRequest({ ...newRequest, priority: e.target.value })}
                    >
                      <option value="HIGH">🔴 High (Business Critical / Emergency)</option>
                      <option value="MEDIUM">🟡 Medium (Standard Maintenance)</option>
                      <option value="LOW">🟢 Low (Minor / General Upkeep)</option>
                    </select>
                  </div>
                </div>

                <div className="modal-footer">
                  <button type="button" className="btn btn-secondary" onClick={() => setShowCreateModal(false)}>
                    Cancel
                  </button>
                  <button type="submit" className="btn btn-primary" disabled={submitting}>
                    {submitting ? 'Submitting to Dispatch...' : 'Submit Request'}
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

export default CustomerPortal;
