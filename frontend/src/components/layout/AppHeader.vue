<template>
    <header class="header">
        <router-link to="/" class="logo">나들</router-link>
    
        <nav class="nav">
            <template v-if="authStore.isLoggedIn">
                <span class="nickname">{{ authStore.nickname }}님</span>
                <button class="nav-btn" @click="handleLogout">로그아웃</button>
            </template>

            <template v-else>
                <router-link to="/login">로그인</router-link>
                <router-link to="/signup">회원가입</router-link>
            </template>
        </nav>
    </header>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

const router = useRouter()
const authStore = useAuthStore()

async function handleLogout() {
    await authStore.logout()
    router.push('/login')
}

</script>