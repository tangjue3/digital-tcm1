<template>
  <div class="simple-login">
    <h1>登录页面</h1>
    <p>用户名: <input type="text" v-model="username" /></p>
    <p>密码: <input type="password" v-model="password" /></p>
    <button @click="login">登录</button>
    <p v-if="message">{{ message }}</p>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const username = ref('admin')
const password = ref('admin123')
const message = ref('')

const login = () => {
  if (username.value === 'admin' && password.value === 'admin123') {
    message.value = '登录成功！'
    setTimeout(() => {
      router.push('/home')
    }, 1000)
  } else {
    message.value = '用户名或密码错误'
  }
}
</script>

<style scoped>
.simple-login {
  max-width: 300px;
  margin: 100px auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 8px;
  text-align: center;
  background: white;
}
.simple-login input {
  width: 100%;
  padding: 8px;
  margin: 5px 0;
  box-sizing: border-box;
}
.simple-login button {
  width: 100%;
  padding: 10px;
  margin-top: 10px;
  background-color: #1f5fca;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
</style>
