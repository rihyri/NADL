import axios from "axios"

const api = axios.create({
    baseURL: 'http://localhost:8080',
    headers: {
        'Content-Type': 'application/json',
    },
})

export const login = (loginId, password) => 
    api.post('/api/auth/login', { loginId, password })