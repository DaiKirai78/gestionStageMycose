import axios from 'axios';

// Create an Axios instance
const apiClient = axios.create({
    baseURL: 'http://localhost:8080',
    headers: {
        'Content-Type': 'application/json'
    }
});

// Add a request interceptor to include the JWT token only for API requests
apiClient.interceptors.request.use(config => {
    const token = localStorage.getItem('token');
    if (token && config.url.startsWith('/api/')) {
        config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
}, error => {
    return Promise.reject(error);
});

export default apiClient;