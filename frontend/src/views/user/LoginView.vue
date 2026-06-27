<template>
    <div class="login-wrapper">
        <div class="login-box">
            <h1>나들</h1>
        
            <form class="form" @submit.prevent="handleLogin">
                <div>
                    <label>아이디</label>
                    <input
                        v-model="loginId"
                        type="text"
                        placeholder="아이디를 입력하세요"
                        :class="{ error: errorMessage }"
                    />    
                </div>

                <div>
                    <label>비밀번호</label>
                    <input
                        v-model="password"
                        type="password"
                        placeholder="비밀번호를 입력하세요"
                        :class="{ error : errorMessage }"
                    />
                </div>

                <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>

                <button type="submit" :disabled="isLoading">
                    {{ isLoading ? '로그인 중...'  :  '로그인' }}
                </button>
            </form>

            <p>
                아직 계정이 없으신가요?
                <router-link to="/signup">회원가입</router-link>
            </p>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

const router = useRouter();
const authStore = useAuthStore();

const loginId = ref('')
const password = ref('')
const errorMessage = ref('')
const isLoading = ref(false)

async function handleLogin() {

    if (!loginId.value || !password.value) {
        errorMessage.value = '아이디와 비밀번호를 입력해주세요.'
        return
    }

    isLoading.value = true
    errorMessage.value = ''

    try {
        await authStore.login(loginId.value, password.value)
        router.push('/')
    } catch (e) {
        errorMessage.value = e.response?.data?.message || '로그인 중 오류가 발생했습니다.'
    } finally {
        isLoading.value = false
    }
}

</script>