import axios from "axios"

const api = axios.create({
    baseURL: 'http://localhost:8080',
    headers: {
        'Content-Type': 'application/json',
    },
})

export const login = (loginId, password) => 
    api.post('/api/auth/login', { loginId, password })

export const checkLoginId = (value) =>
  api.get('/api/auth/check/login-id', { params: { value } })

export const checkEmail = (value) =>
  api.get('/api/auth/check/email', { params: { value } })

export const checkNickname = (value) =>
  api.get('/api/auth/check/nickname', { params: { value } })

export const signup = (data) =>
  api.post('/api/auth/signup', data)