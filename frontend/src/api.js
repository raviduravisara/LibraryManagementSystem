// Production-ready API configuration
const getBaseURL = () => {
  // Use environment variable if available, otherwise fallback to proxy
  return import.meta.env.VITE_API_BASE_URL || '/api';
};

const BASE = getBaseURL();

async function request(path, options = {}) {
  const res = await fetch(`${BASE}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
    ...options,
  })
  if (!res.ok) {
    const text = await res.text()
    throw new Error(text || res.statusText)
  }
  if (res.status === 204) return null
  return res.json()
}

export const api = {
  // Borrowings
  listBorrowings: (params) => request(`/borrowings${params?.memberId ? `?memberId=${encodeURIComponent(params.memberId)}` : ''}`),
  createBorrowing: (data) => request('/borrowings', { method: 'POST', body: JSON.stringify(data) }),
  updateBorrowing: (id, data) => request(`/borrowings/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  returnBorrowing: (id) => request(`/borrowings/${id}/return`, { method: 'POST' }),
  deleteBorrowing: (id) => request(`/borrowings/${id}`, { method: 'DELETE' }),

  // Reservations
  listReservations: (params) => request(`/reservations${params?.memberId ? `?memberId=${encodeURIComponent(params.memberId)}` : ''}`),
  createReservation: (data) => request('/reservations', { method: 'POST', body: JSON.stringify(data) }),
  updateReservation: (id, data) => request(`/reservations/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  receiveReservation: (id) => request(`/reservations/${id}/receive`, { method: 'POST' }),
  deleteReservation: (id) => request(`/reservations/${id}`, { method: 'DELETE' }),
}