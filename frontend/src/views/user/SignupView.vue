<template>
    <div class="signup-wrapper">
        <h1>회원가입</h1>

        <form @submit.prevent="handleSignup">
            
            <div class="field">
                <label>아이디</label>
                <div class="input-row">
                    <input 
                        v-model="form.loginId"
                        type="text"
                        placeholder="4~20자, 영문/숫자/언더스코어"
                    />
                    <button type="button" @click="handleCheckLoginId">중복확인</button>
                </div>
                <p v-if="check.loginId.message" :class="check.loginId.ok ? 'success' : 'error'">
                    {{ check.loginId.message }}
                </p>
            </div>

            <div class="field">
                <label>비밀번호</label>
                <input
                    v-model="form.password"
                    type="password"
                    placeholder="8자 이상"
                />
            </div>

            <div class="field">
                <label>비밀번호 확인</label>
                <input
                    v-model="form.passwordConfirm"
                    type="password"
                    placeholder="비밀번호를 다시 입력하세요."
                />
                <p v-if="form.passwordConfirm && !isPasswordMatch" class="error">
                    비밀번호가 일치하지 않습니다.
                </p>
            </div>

            <div class="field">
                <label>이메일</label>
                <div class="input-row">
                    <input
                        v-model="form.email"
                        type="email"
                        placeholder="이메일을 입력하세요" 
                    />
                    <button type="button" @click="handleCheckEmail">중복확인</button>
                </div>
                <p v-if="check.email.message" :class="check.email.ok ? 'success' : 'error'">
                    {{ check.email.message }}
                </p>
            </div>

            <div class="field">
                <label>닉네임</label>
                <div class="input-row">
                    <input
                        v-model="form.nickname"
                        type="text"
                        placeholder="2~10자"
                    />
                    <button type="button" @click="handleCheckNickname">중복확인</button>
                </div>
                <p v-if="check.nickname.message" :class="check.nickname.ok ? 'success' : 'error'">
                    {{  check.nickname.message }}
                </p>
            </div>

            <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
        
            <button type="submit" :disabled="!isFormValid || isLoading">
                {{ isLoading ? '가입 중...' : '회원가입' }}
            </button>
        </form>

        <p class="login-link">
            이미 계정이 있으신가요?
            <router-link to="/login">로그인</router-link>
        </p>
    </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import {
    checkLoginId,
    checkEmail,
    checkNickname,
    signup
} from '@/api/auth'

const router = useRouter()

// 입력값
const form = ref({
    loginId: '',
    password: '',
    passwordConfirm: '',
    email: '',
    nickname: '',
})

// 중복 확인 결과
const check = ref({
    loginId : { ok:false, message:'' },
    email: { ok:false, message:'' },
    nickname: { ok:false, message:'' }
})

const errorMessage = ref('')
const isLoading = ref(false)

// 비밀번호 일치 여부
const isPasswordMatch = computed(
    () => form.value.password === form.value.passwordConfirm
)

// 회원가입 버튼 활성화 조건
const isFormValid = computed(() => 
    check.value.loginId.ok &&
    check.value.email.ok &&
    check.value.nickname.ok &&
    isPasswordMatch.value &&
    form.value.password.length >= 8
)

// 입력값이 바뀌면 중복 확인 결과 초기화
watch(() => form.value.loginId, () => { check.value.loginId = { ok:false, message: '' }})
watch(() => form.value.email, () => { check.value.email = { ok:false, message: '' }})
watch(() => form.value.nickname, () => { check.value.nickname = { ok:false, message: '' }})

// 아이디 중복 확인
async function handleCheckLoginId() {
    
    if (!form.value.loginId) {
        check.value.loginId = { ok:false, message: '아이디를 입력해주세요.' }
        return;
    }

    try {
        await checkLoginId(form.value.loginId)
        check.value.loginId = { ok: true, message: '사용 가능한 아이디입니다.' }
    } catch (e) {
        check.value.loginId = {
            ok: false,
            message: e.response?.data.message || '사용할 수 없는 아이디입니다.'
        }
    }
}

// 이메일 중복 확인
async function handleCheckEmail() {
    if (!form.value.email) {
        check.value.email = { ok:false, message: '이메일을 입력해주세요.' }
        return
    }

    try {
        await checkEmail(form.value.email)
        check.value.email = { ok:true, message: '사용 가능한 이메일입니다.' }
    } catch (e) {
        check.value.email = {
            ok: false,
            message: e.response?.data.message || '사용할 수 없는 이메일입니다.',
        }
    }
}

// 닉네임 중복 확인
async function handleCheckNickname() {
    
    if (!form.value.nickname) {
        check.value.nickname = { ok:false, message:'닉네임을 입력해주세요.' }
        return;

    }

    try {
        await checkNickname(form.value.nickname)
        check.value.nickname = { ok:true, message:'사용 가능한 닉네임입니다.' }
    } catch (e) {
        check.value.nickname = {
            ok: false,
            message: e.response?.data?.message || '사용할 수 없는 닉네임입니다.',
        }
    }
}

// 회원가입 제출
async function handleSignup() {
    isLoading.value = true
    errorMessage.value = ''

    try {
        await signup ({
            loginId: form.value.loginId,
            password: form.value.password,
            email: form.value.email,
            nickname: form.value.nickname
        })

        alert('회원가입이 완료되었습니다!');

        router.push('/login')
    } catch (e) {
        errorMessage.value = e.response?.data?.message || '회원가입 중 오류가 발생했습니다.'
    } finally {
        isLoading.value = false
    }
}

</script>