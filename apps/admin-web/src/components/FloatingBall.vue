<template>
  <div>
    <!-- 悬浮球 -->
    <div
      class="floating-ball"
      @mousedown="startDrag"
      @mouseup="endDrag"
      @mousemove="drag"
      :style="{ left: ballX + 'px', top: ballY + 'px' }"
      @click="toggleDialog"
    >
      <!-- 可以添加悬浮球图标，例如 -->
     
      <img class="bottom-right-logo" src="@/assets/newlogo.png" alt="Custom Icon" width="100" height="100">
    </div>
    <!-- 集成的对话框 -->
    <Dialog
      v-show="isDialogOpen"
      storage-key="floating-ball"
      @closeDialog="toggleDialog"
      class="dialog-wrapper"
      :style="{ left: dialogX + 'px', top: dialogY + 'px' }"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue';
import Dialog from './chat1.vue';

// 悬浮球位置
const ballX = ref(window.innerWidth - 100);
const ballY = ref(window.innerHeight - 100);
// 对话框是否打开
const isDialogOpen = ref(false);
// 拖动相关
const isDragging = ref(false);
let initialX = 0;
let initialY = 0;
// 对话框位置
const dialogX = ref(0);
const dialogY = ref(0);

const startDrag = (e) => {
  isDragging.value = true;
  initialX = e.clientX - ballX.value;
  initialY = e.clientY - ballY.value;
};

const endDrag = () => {
  isDragging.value = false;
};

const drag = (e) => {
  if (isDragging.value) {
    ballX.value = e.clientX - initialX;
    ballY.value = e.clientY - initialY;
  }
};

const toggleDialog = () => {
  isDialogOpen.value =!isDialogOpen.value;
  if (isDialogOpen.value) {
    // 假设对话框宽度为 300px，可根据实际情况调整
    const dialogWidth = 800; 
    // 假设顶部间距为 20px，可根据实际情况调整
    const topSpacing = 20; 
    dialogX.value = window.innerWidth - dialogWidth;
    dialogY.value = topSpacing;
  }
};
</script>

<style scoped>

.bottom-right-logo {
  position: fixed;
  right: 30px;
  bottom: 30px;
  width: 100px;
  height: 100px;
  z-index: 1000;
  border-radius: 15px;
  box-shadow: 0 5px 20px rgba(139, 69, 19, 0.4);
  background: rgba(255, 255, 255, 0.9);
  padding: 8px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.bottom-right-logo:hover {
  transform: scale(1.1) rotate(5deg);
  box-shadow: 0 8px 30px rgba(139, 69, 19, 0.6);
}
.floating-ball {
  position: fixed;
  width: 40px;
  height: 40px;
  background-color: #007bff;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  color: white;
  font-size: 20px;
  cursor: pointer;
  z-index: 9999;
}

.dialog-wrapper {
  position: fixed;
  z-index: 9998;
  width: 800px;
}
</style>
