import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, logout as logoutApi } from '@/api/auth'

export const useAuthStore = defineStore('auth', () => {
    const accessToken = ref(localStorage.getItem('accessToken') || null)
    const nickname = ref(localStorage.getItem('nickname') || null)

    const isLoggedIn = computed(() => !!accessToken.value)

    async function login(loginId, password) {
        const response = await loginApi(loginId, password)
        const { accessToken: token, nickname: name } = response.data.data;

        accessToken.value =  token
        nickname.value = name 

        // 새로고침해도 로그인 유지 
        localStorage.setItem('accessToken', token)
        localStorage.setItem('nickname', name)
    }

    // 백엔드 호출 후 로컬 상태 정리
    async function logout() {
        try {
            await logoutApi(accessToken.value)
        } catch (e) {
            // 이미 만료된 토큰이어도 로컬 정리는 진행
            console.error('로그아웃 요청 실패: ', e)
        } finally {
            accessToken.value = null
            nickname.value = null
            localStorage.removeItem('accessToken')
            localStorage.removeItem('nickname')
        }
    }

    return { accessToken, nickname, isLoggedIn, login , logout }
})