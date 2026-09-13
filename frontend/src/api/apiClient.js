const API_BASE = '/api';

export async function apiRequest(endpoint, options = {}) {
  const token = localStorage.getItem('keystone_token');

  const headers = {
    'Content-Type': 'application/json',
    ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
    ...options.headers,
  };

  const response = await fetch(`${API_BASE}${endpoint}`, {
    ...options,
    headers,
  });

  if (response.status === 401) {
    localStorage.removeItem('keystone_token');
    localStorage.removeItem('keystone_user');
    if (window.location.pathname !== '/login') {
      window.location.href = '/login';
    }
  }

  const contentType = response.headers.get('content-type');
  let data = null;
  if (contentType && contentType.includes('application/json')) {
    data = await response.json();
  } else {
    data = await response.text();
  }

  if (!response.ok) {
    const errorMsg = data?.message || data?.error || (typeof data === 'string' ? data : 'An unexpected error occurred');
    const error = new Error(errorMsg);
    error.status = response.status;
    error.fieldErrors = data?.fieldErrors;
    throw error;
  }

  return data;
}

export const authApi = {
  login: (email, password) =>
    apiRequest('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password }),
    }),
};

export const customerApi = {
  getAll: (page = 0, size = 20, search = '') => {
    const params = new URLSearchParams({ page, size });
    if (search) params.append('search', search);
    return apiRequest(`/customers?${params.toString()}`);
  },
  getById: (id) => apiRequest(`/customers/${id}`),
  create: (customerData) =>
    apiRequest('/customers', {
      method: 'POST',
      body: JSON.stringify(customerData),
    }),
  update: (id, customerData) =>
    apiRequest(`/customers/${id}`, {
      method: 'PUT',
      body: JSON.stringify(customerData),
    }),
  getSites: (customerId) => apiRequest(`/customers/${customerId}/sites`),
  createSite: (customerId, siteData) =>
    apiRequest(`/customers/${customerId}/sites`, {
      method: 'POST',
      body: JSON.stringify(siteData),
    }),
};

export const workOrderApi = {
  search: (filters = {}, page = 0, size = 20) => {
    const params = new URLSearchParams({ page, size });
    if (filters.status) params.append('status', filters.status);
    if (filters.priority) params.append('priority', filters.priority);
    if (filters.customerId) params.append('customerId', filters.customerId);
    if (filters.siteId) params.append('siteId', filters.siteId);
    if (filters.assigneeId) params.append('assigneeId', filters.assigneeId);
    if (filters.search) params.append('search', filters.search);
    return apiRequest(`/work-orders?${params.toString()}`);
  },
  getById: (id) => apiRequest(`/work-orders/${id}`),
  create: (workOrderData) =>
    apiRequest('/work-orders', {
      method: 'POST',
      body: JSON.stringify(workOrderData),
    }),
  update: (id, workOrderData) =>
    apiRequest(`/work-orders/${id}`, {
      method: 'PUT',
      body: JSON.stringify(workOrderData),
    }),
  assignTechnician: (id, technicianId) =>
    apiRequest(`/work-orders/${id}/assign`, {
      method: 'POST',
      body: JSON.stringify({ technicianId }),
    }),
  transitionStatus: (id, status, note = '') =>
    apiRequest(`/work-orders/${id}/status`, {
      method: 'POST',
      body: JSON.stringify({ status, note }),
    }),
  getStatusHistory: (id) => apiRequest(`/work-orders/${id}/history`),
  logParts: (id, partId, quantity) =>
    apiRequest(`/work-orders/${id}/parts`, {
      method: 'POST',
      body: JSON.stringify({ partId, quantity }),
    }),
  getPartsUsed: (id) => apiRequest(`/work-orders/${id}/parts`),
  logTime: (id, minutes, note = '') =>
    apiRequest(`/work-orders/${id}/time`, {
      method: 'POST',
      body: JSON.stringify({ minutes, note }),
    }),
  getTimeLogs: (id) => apiRequest(`/work-orders/${id}/time`),
  getTechnicianWorkOrders: () => apiRequest('/technician/work-orders'),
};

export const partApi = {
  getAll: () => apiRequest('/parts'),
};

export const reportApi = {
  getSummary: () => apiRequest('/reports/summary'),
};

export const dispatcherApi = {
  getRequests: () => apiRequest('/dispatcher/requests'),
  getUnassignedRequests: () => apiRequest('/dispatcher/requests/unassigned'),
  getTechnicians: () => apiRequest('/dispatcher/technicians'),
  assignTechnician: (requestId, technicianId) =>
    apiRequest(`/dispatcher/requests/${requestId}/assign`, {
      method: 'PUT',
      body: JSON.stringify({ technicianId }),
    }),
};

export const technicianApi = {
  getRequests: () => apiRequest('/technician/work-orders'),
  updateStatus: (requestId, status, note = '') =>
    apiRequest(`/work-orders/${requestId}/status`, {
      method: 'POST',
      body: JSON.stringify({ status, note }),
    }),
};
