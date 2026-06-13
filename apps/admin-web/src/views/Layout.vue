<template>
  <div class="layout-shell">
    <Sidebar />
    <main class="layout-main">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import Sidebar from './Sidebar.vue'
</script>

<style scoped>
.layout-shell {
  min-height: 100vh;
  padding: 12px;
  background: transparent;
}

.layout-main {
  margin-top: 12px;
}
</style>
