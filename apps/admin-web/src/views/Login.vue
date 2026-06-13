<template>
  <div class="login-container">
    <div class="decorative-elements">
      <div class="cloud cloud-1"></div>
      <div class="cloud cloud-2"></div>
      <div class="cloud cloud-3"></div>
    </div>

    <div class="main-title">
      <h1>晨夕四诊</h1>
      <p class="subtitle">传承中医智慧 · AI 赋能现代诊疗</p>
    </div>

    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <div class="logo-section">
            <div class="tcm-symbol">☯</div>
            <h3>平台登录</h3>
          </div>
        </div>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="0px" class="login-form">
        <el-form-item prop="username">
          <div class="input-wrapper">
            <span class="input-icon">账号</span>
            <el-input
              v-model="form.username"
              placeholder="请输入账号"
              class="custom-input"
              size="large"
            />
          </div>
        </el-form-item>

        <el-form-item prop="password">
          <div class="input-wrapper">
            <span class="input-icon">密码</span>
            <el-input
              v-model="form.password"
              type="password"
              show-password
              placeholder="请输入密码"
              class="custom-input"
              size="large"
              @keyup.enter="handleLogin"
            />
          </div>
        </el-form-item>

        <el-form-item v-if="captchaEnabled" prop="code">
          <div class="captcha-row">
            <el-input
              v-model="form.code"
              placeholder="请输入验证码"
              class="custom-input captcha-input"
              size="large"
              @keyup.enter="handleLogin"
            />
            <img v-if="codeUrl" :src="codeUrl" class="captcha-img" alt="验证码" @click="getCode" />
          </div>
        </el-form-item>

        <el-form-item class="button-group">
          <el-button type="primary" :loading="loading" @click="handleLogin" class="login-btn" size="large">
            登录
          </el-button>
          <el-button @click="resetForm" class="reset-btn" size="large">
            重置
          </el-button>
        </el-form-item>
      </el-form>

      <div class="card-footer">
        <div class="tcm-pattern"></div>
      </div>
    </el-card>

    <div class="footer-text">
      <p>中医与智能融合，服务教学与临床</p>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElForm, ElFormItem, ElInput, ElButton, ElCard, ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { getCodeImg, login } from '@/api/auth'
import { setToken } from '@/utils/auth'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)
const loading = ref(false)
const captchaEnabled = ref(false)
const codeUrl = ref('')

const form = ref({
  username: 'admin',
  password: 'admin123',
  code: '',
  uuid: ''
})

const rules = ref({
  username: [
    { required: true, message: '请输入账号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 5, message: '密码长度不能少于 5 位', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ]
})

async function getCode() {
  try {
    const res = await getCodeImg()
    captchaEnabled.value = res.captchaEnabled === undefined ? true : Boolean(res.captchaEnabled)
    if (captchaEnabled.value) {
      codeUrl.value = `data:image/gif;base64,${res.img}`
      form.value.uuid = res.uuid || ''
    } else {
      codeUrl.value = ''
      form.value.uuid = ''
    }
  } catch (error) {
    console.error('[auth] captcha load failed', error)
    captchaEnabled.value = false
  }
}

async function handleLogin() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) {
    return
  }

  loading.value = true
  try {
    const res = await login(form.value.username, form.value.password, form.value.code, form.value.uuid)
    if (res?.code !== 200 || !res?.token) {
      throw new Error(res?.msg || '登录失败')
    }

    setToken(res.token)
    ElMessage.success('登录成功')
    const redirect = Array.isArray(route.query.redirect) ? route.query.redirect[0] : route.query.redirect
    router.replace(redirect || '/home')
  } catch (error) {
    console.error('[auth] login failed', error)
    ElMessage.error(error?.message || '登录失败')
    form.value.code = ''
    if (captchaEnabled.value) {
      await getCode()
    }
  } finally {
    loading.value = false
  }
}

function resetForm() {
  form.value.username = ''
  form.value.password = ''
  form.value.code = ''
  formRef.value?.resetFields()
}

onMounted(() => {
  getCode()
})
</script>

<style scoped>
.login-container {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 20px;
  background:
    radial-gradient(circle at 18% 20%, rgba(92, 161, 255, 0.35), transparent 35%),
    radial-gradient(circle at 82% 15%, rgba(31, 95, 202, 0.25), transparent 30%),
    linear-gradient(135deg, #f4f9ff 0%, #dbe9ff 45%, #c9ddff 100%);
  overflow: hidden;
}

.decorative-elements {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
}

.cloud {
  position: absolute;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.38);
  backdrop-filter: blur(4px);
  animation: float 7s ease-in-out infinite;
}

.cloud-1 { width: 120px; height: 42px; top: 18%; left: 12%; }
.cloud-2 { width: 92px; height: 34px; top: 14%; right: 14%; animation-delay: 2s; }
.cloud-3 { width: 140px; height: 46px; bottom: 16%; left: 20%; animation-delay: 4s; }

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-18px); }
}

.main-title {
  text-align: center;
  margin-bottom: 36px;
  z-index: 1;
}

.main-title h1 {
  font-family: "Microsoft YaHei", "PingFang SC", "Hiragino Sans GB", serif;
  font-size: 3.25rem;
  font-weight: 700;
  color: #104299;
  margin: 0;
  letter-spacing: 6px;
  text-shadow: 0 8px 24px rgba(31, 95, 202, 0.2);
}

.subtitle {
  font-family: "Microsoft YaHei", "PingFang SC", "Hiragino Sans GB", sans-serif;
  font-size: 1.05rem;
  color: #2a5daa;
  margin-top: 10px;
  letter-spacing: 1.4px;
}

.login-card {
  width: 420px;
  max-width: 90vw;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 22px;
  border: 1px solid rgba(120, 170, 255, 0.35);
  box-shadow: 0 24px 60px rgba(18, 74, 160, 0.2);
  overflow: hidden;
}

.card-header {
  text-align: center;
  padding: 8px 0;
}

.logo-section {
  display: inline-flex;
  align-items: center;
  gap: 12px;
}

.tcm-symbol {
  font-size: 1.8rem;
  color: #1f5fca;
}

.card-header h3 {
  margin: 0;
  color: #1a4f9f;
  font-size: 1.35rem;
  font-weight: 700;
  letter-spacing: 1px;
}

.login-form {
  padding: 20px 30px;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  margin-bottom: 18px;
}

.input-icon {
  position: absolute;
  left: 13px;
  z-index: 10;
  color: #1f5fca;
  font-size: 0.85rem;
  font-weight: 700;
}

.custom-input {
  width: 100%;
  min-width: 300px;
}

.custom-input :deep(.el-input__wrapper) {
  padding-left: 52px;
  border-radius: 12px;
  border: 1px solid #c7dcff;
  background: #f8fbff;
  box-shadow: none;
  min-width: 320px;
}

.captcha-row {
  display: flex;
  width: 100%;
  gap: 12px;
  align-items: center;
  margin-bottom: 18px;
}

.captcha-input {
  min-width: 0;
}

.captcha-input :deep(.el-input__wrapper) {
  min-width: 0;
  padding-left: 16px;
}

.captcha-img {
  width: 116px;
  height: 40px;
  border-radius: 8px;
  border: 1px solid #c7dcff;
  cursor: pointer;
}

.button-group {
  margin-top: 28px;
  display: flex;
  gap: 15px;
}

.login-btn {
  flex: 2;
  height: 50px;
  border-radius: 12px;
  border: none;
  background: linear-gradient(120deg, #1f5fca 0%, #3f8cff 100%);
  font-size: 1.05rem;
  font-weight: 700;
}

.reset-btn {
  flex: 1;
  height: 50px;
  border-radius: 12px;
  border: 1px solid #c7dcff;
  background: #ffffff;
  color: #1f5fca;
  font-weight: 600;
}

.card-footer {
  height: 6px;
  background: linear-gradient(90deg, #1f5fca 0%, #3f8cff 50%, #1f5fca 100%);
}

.tcm-pattern {
  display: none;
}

.footer-text {
  margin-top: 22px;
  text-align: center;
}

.footer-text p {
  color: #2a5daa;
  font-size: 0.9rem;
  letter-spacing: 0.5px;
}

@media (max-width: 768px) {
  .main-title h1 { font-size: 2.4rem; letter-spacing: 3px; }
  .subtitle { font-size: 0.95rem; letter-spacing: 0.8px; }
  .login-card { width: 350px; }
  .login-form { padding: 16px 20px; }
}

@media (max-width: 480px) {
  .main-title h1 { font-size: 2rem; letter-spacing: 2px; }
  .login-card { width: 300px; }
  .button-group { flex-direction: column; gap: 10px; }
  .login-btn,
  .reset-btn { flex: none; }
}
</style>
