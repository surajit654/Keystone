import { useState, useEffect } from 'react';
import Sidebar from '../components/Sidebar';
import { dispatcherApi, technicianApi } from '../api/apiClient';
import { useAuth } from '../context/AuthContext';

function Technicians() {
  const { user } = useAuth();
  const isTechUser = user?.role === 'TECHNICIAN';

  const [technicians, setTechnicians] = useState([]);
  const [techRequests, setTechRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(null);
  const [transitionNote, setTransitionNote] = useState('');

  useEffect(() => {
    loadData();
  }, [user]);

  const loadData = async () => {
    setLoading(true);
    try {
      if (isTechUser) {
        const myJobs = await technicianApi.getRequests();
        setTechRequests(myJobs || []);
      } else {
        const list = await dispatcherApi.getTechnicians();
        setTechnicians(list || []);
      }
    } catch (err) {
      console.error('Failed to load technician data:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateStatus = async (requestId, nextStatus) => {
    setActionLoading(requestId);
    try {
      await technicianApi.updateStatus(requestId, nextStatus, transitionNote);
      setTransitionNote('');
      loadData();
    } catch (err) {
      alert(err.message || 'Status transition failed');
    } finally {
      setActionLoading(null);
    }
  };

  return (
    <div className="app-container">
      <Sidebar />

      <main className="main-content">
        <div className="page-header">
          <div>
            <h1 className="page-title">
              {isTechUser ? 'My Field Service Jobs' : 'Field Technician Roster'}
            </h1>
            <p className="page-subtitle">
              {isTechUser
                ? 'Active assignments, on-site status progression, and task close-outs'
                : 'Active maintenance technicians and personnel status'}
            </p>
          </div>
        </div>

        {isTechUser ? (
          /* Technician Individual Field View */
          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            {loading ? (
              <div style={{ textAlign: 'center', padding: '40px' }}>Loading your assignments...</div>
            ) : techRequests.length === 0 ? (
              <div className="table-card" style={{ padding: '40px', textAlign: 'center', color: 'var(--text-muted)' }}>
                🎉 You have no pending work assignments at this time!
              </div>
            ) : (
              techRequests.map((req) => (
                <div key={req.id} className="table-card" style={{ padding: '24px' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '12px' }}>
                    <div>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '8px' }}>
                        <span className="card-code">{req.code}</span>
                        <span className="badge" style={{ backgroundColor: 'var(--bg-primary)', color: 'var(--text-main)' }}>
                          {req.status}
                        </span>
                      </div>
                      <h2 style={{ fontSize: '18px', fontWeight: 700, marginTop: '4px' }}>{req.title}</h2>
                      <p style={{ color: 'var(--text-muted)', fontSize: '14px', marginTop: '4px' }}>{req.description}</p>
                      <div style={{ fontSize: '12px', color: 'var(--text-dim)', marginTop: '8px' }}>
                        🏢 {req.customerName} · 📍 {req.siteName || 'Main Facility'} ({req.siteCity || ''})
                      </div>
                    </div>
                    <div>
                      <span className={`badge badge-${req.priority?.toLowerCase() || 'medium'}`}>
                        {req.priority} PRIORITY
                      </span>
                    </div>
                  </div>

                  <div style={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    marginTop: '20px',
                    paddingTop: '16px',
                    borderTop: '1px solid var(--border-subtle)',
                    flexWrap: 'wrap',
                    gap: '12px'
                  }}>
                    <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
                      <span style={{ fontSize: '12px', color: 'var(--text-dim)' }}>Labor Logged: {req.totalLaborMinutes || 0}m</span>
                      <span>·</span>
                      <span style={{ fontSize: '12px', color: 'var(--accent-emerald)' }}>Parts: ${Number(req.totalPartsCost || 0).toFixed(2)}</span>
                    </div>

                    <div style={{ display: 'flex', gap: '10px' }}>
                      {req.status === 'ASSIGNED' && (
                        <button
                          className="btn btn-primary"
                          disabled={actionLoading === req.id}
                          onClick={() => handleUpdateStatus(req.id, 'IN_PROGRESS')}
                        >
                          ▶ Start Work
                        </button>
                      )}

                      {req.status === 'IN_PROGRESS' && (
                        <>
                          <button
                            className="btn btn-secondary"
                            disabled={actionLoading === req.id}
                            onClick={() => handleUpdateStatus(req.id, 'ON_HOLD')}
                          >
                            ⏸️ Put On Hold
                          </button>
                          <button
                            className="btn btn-primary"
                            style={{ backgroundColor: 'var(--accent-emerald)' }}
                            disabled={actionLoading === req.id}
                            onClick={() => handleUpdateStatus(req.id, 'COMPLETED')}
                          >
                            ✓ Mark Completed
                          </button>
                        </>
                      )}

                      {req.status === 'ON_HOLD' && (
                        <button
                          className="btn btn-primary"
                          disabled={actionLoading === req.id}
                          onClick={() => handleUpdateStatus(req.id, 'IN_PROGRESS')}
                        >
                          ▶ Resume Work
                        </button>
                      )}

                      {req.status === 'COMPLETED' && (
                        <span style={{ fontSize: '13px', color: 'var(--accent-emerald)', fontWeight: 600 }}>
                          ✓ Awaiting Manager Close-out
                        </span>
                      )}
                    </div>
                  </div>
                </div>
              ))
            )}
          </div>
        ) : (
          /* Dispatcher / Manager Roster View */
          <div className="table-card">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Technician ID</th>
                  <th>Email</th>
                  <th>Role</th>
                  <th>Availability</th>
                </tr>
              </thead>
              <tbody>
                {loading ? (
                  <tr>
                    <td colSpan="4" style={{ textAlign: 'center', padding: '40px' }}>Loading technicians...</td>
                  </tr>
                ) : technicians.length === 0 ? (
                  <tr>
                    <td colSpan="4" style={{ textAlign: 'center', padding: '40px', color: 'var(--text-muted)' }}>
                      No technicians found in system.
                    </td>
                  </tr>
                ) : (
                  technicians.map((t) => (
                    <tr key={t.id}>
                      <td style={{ fontFamily: 'var(--font-mono)', fontWeight: 600, color: 'var(--accent-cyan)' }}>
                        TECH-00{t.id}
                      </td>
                      <td style={{ fontWeight: 600 }}>{t.email}</td>
                      <td>
                        <span className="user-role-badge">{t.role}</span>
                      </td>
                      <td>
                        <span className="badge badge-low">🟢 Ready for Dispatch</span>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        )}
      </main>
    </div>
  );
}

export default Technicians;