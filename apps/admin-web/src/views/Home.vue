<template>
  <div class="homepage-wrapper">
    <div class="homepage-background" :class="{ 'loaded': backgroundLoaded }"></div>
    <div class="homepage-overlay"></div>
    <div class="homepage-content">
  <!-- <el-button type="primary" @click="$router.push('/')" class="back-btn">退出登录</el-button> -->
  <div class="homepage-container">

      <!-- 左侧学员个人信息部分 -->
      <div class="left-info">
          <el-card class="info-card">
              <template #header>
                  <h3>学员个人信息</h3>
              </template>
              <div class="info-item">姓名：{{ studentInfo.name }}</div>
              <div class="info-item">学号：{{ studentInfo.studentId }}</div>
              <div class="info-item">年级：{{ studentInfo.grade }}</div>
              <div class="info-item">专业：{{ studentInfo.major }}</div>
              <div class="info-item">历史记录：{{ studentInfo.studyRecord }}</div>
              <!-- 新增做题记录列表 -->
              <el-divider />
              <h4>最近实验记录</h4>

              <el-table
                  :data="studentInfo.exerciseRecords"
                  style="width: 100%; height: 100%;"
                  border
                  stripe
                  size="medium"
                  class="experiment-table"
                  :header-cell-style="{
                    background: 'linear-gradient(135deg, #8B4513 0%, #A0522D 50%, #D2691E 100%)',
                    color: 'white',
                    fontWeight: '600',
                    textShadow: '0 1px 3px rgba(0, 0, 0, 0.3)',
                    border: 'none'
                  }"
              >
                  <el-table-column
                      prop="remark"
                      label="案例类型"
                      width="100"
                      align="center"
                  />
                  <el-table-column
                      prop="userId"
                      label="姓名"
                      width="70"
                      align="center"
                  />
                  <el-table-column
                      prop="score"
                      label="分数"
                      width="70"
                      align="center"
                  />
                  <el-table-column
                      prop="time"
                      label="实验时间"
                      min-width="130"
                  >
                      <template #default="{ row }">
                          {{ formatTime(row.time) }}
                      </template>
                  </el-table-column>
                  <el-table-column label="实验结果">
                      <template #default="{ row }">
                          <el-button type="primary" @click="$router.push('/result?questionNo='+row.questionNo)">实验结果</el-button>
                      </template>
                  </el-table-column>
              </el-table>
              <!-- 显示详情的区域 -->
              <div v-if="currentRecord" class="detail-display">
                  <h4>实验结果</h4>
                  <p>诊断结论：{{ currentRecord.conclusion }}</p>
                  <p>治疗手段：{{ currentRecord.treatmentMethods }}</p>
              </div>
          </el-card>
      </div>
      <!-- 右侧模块部分 -->
      <div class="right-modules">
          <el-row :gutter="20">
              <el-col :span="(module.title === '实验案例教学' || module.title === '模拟实验教学')? 24 : 8" v-for="(module, index) in modules" :key="index">
                  <el-card :class="['module-card', (module.title === '实验案例教学' || module.title === '模拟实验教学')? 'special-module' : '']" @click="goToDetail(module)">
                      <img :src="module.image" alt="模块示意图" class="module-image" :style="{height: (module.title === '实验案例教学' || module.title === '学生模拟实验')? '150px' : 'auto'}" />
                      <div class="module-title">{{ module.title }}</div>
                  </el-card>
              </el-col>
          </el-row>
      </div>
      
  </div>
  </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElCard, ElRow, ElCol, ElTable, ElTableColumn, ElButton } from 'element-plus';
import { useRouter } from 'vue-router';
import { get } from '@/utils/request';

const router = useRouter();
const backgroundLoaded = ref(false);

const loadBackgroundImage = () => {
  const img = new Image();
  img.onload = () => {
    backgroundLoaded.value = true;
  };
  img.onerror = () => {
    backgroundLoaded.value = true;
  };
  img.src = '/background-1.png';
};

onMounted(() => {
  setTimeout(() => {
    loadBackgroundImage();
  }, 100);
});

// 模拟学员个人信息
const studentInfo = ref({
  name: '张三',
  studentId: '20250001',
  grade: '大三',
  major: '中医学',
  studyRecord: '你已完成次仿真模拟实验',
  exerciseRecords: []
});
// 模拟右侧模块数据
// 在文件顶部添加
import experimentImage from '@/assets/望闻问切.jpg';
import experimentsImage from '@/assets/实验板块.jpg';

// 然后在modules数组中使用
const modules = ref([
  {
      id: 6,
      title: '实验案例教学',
      type: "",
      image: experimentImage,
      link: '/simulation'
  },
  {
      id: 6,
      title: '模拟实验教学',
      type: "",
      image: experimentsImage,
      link: '/simulation'
  },
  {
      id: 1,
      title: '中医好文',
      type: "article",
      image: 'https://img95.699pic.com/photo/60042/4246.jpg_wh860.jpg',
      link: '/article'
  },
  {
      id: 2,
      title: '中药食谱',
      type: "recipe",
      image: 'https://bpic.588ku.com/back_origin_min_pic/19/10/22/58dfb249e73cfc96733ad926706f6553.jpg',
      link: '/recipe'
  },
  {
      id: 3,
      title: '中医教学视频',
      type: "video",
      image: 'https://img95.699pic.com/photo/50163/1760.jpg_wh860.jpg',
      link: '/video'
  },
  {
      id: 4,
      title: '常见病防治',
      type: "prevention",
      image: 'https://img95.699pic.com/photo/60015/0062.jpg_wh860.jpg',
      link: '/prevention'
  },
  {
      id: 5,
      title: '常见药方',
      type: "prescription",
      image: 'https://img95.699pic.com/photo/50168/2991.jpg_wh860.jpg',
      link: '/prescription'
  }
]);
// 当前选中的记录
const currentRecord = ref(null);

// 时间格式化方法
const formatTime = (timeString) => {
  return timeString? timeString.replace('T', ' ').substring(0, 16) : '';
};

// 显示详情的方法
const showDetail = (record) => {
  // currentRecord.value = record;
};

// 跳转到详情页的方法
const goToDetail = (module) => {
  console.log(`跳转到 ${module.title} 详情页面：${module.link}`);
  if (module.type) {
      router.push("/cglist?type=" + module.type);
  } else {
      router.push("/syList");
  }
};

onMounted(async () => {
  try {
      const res = await get("/records/list");
      console.log('获取到的响应数据:', res);
      if (res.code === 200) {
          studentInfo.value.exerciseRecords = res.rows.map(item => ({
              remark: item.remark,
              userId: item.userId,
              score: item.score,
              time: item.time,
              conclusion: item.conclusion,
              treatmentMethods: item.treatmentMethods,
              // 保留原有字段兼容
              date: item.time.split(' ')[0],
              title: item.caseNo,
              questionNo: item.questionNo
          }));

          const experimentCount = res.rows.length;
          studentInfo.value.studyRecord = `你已完成${experimentCount}次仿真模拟实验`;
          console.log('学生的实验记录:', studentInfo.value.studyRecord);
      }
      console.log('学生的实验记录:', studentInfo.value.exerciseRecords);
  } catch (error) {
      console.error('获取实验记录失败:', error);
  }
});
</script>

<style scoped>
.homepage-wrapper {
  position: relative;
  min-height: 100vh;
  width: 100%;
  overflow: hidden;
}

.homepage-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -2;
  background: rgba(245, 248, 255, 0.9) no-repeat center center;
  background-size: cover;
  background-attachment: fixed;
  will-change: transform;
  transform: translateZ(0);
  opacity: 0;
  transition: opacity 0.8s ease-in-out;
}

.homepage-background.loaded {
  background: url('/background-1.png') no-repeat center center;
  background-size: cover;
  background-attachment: fixed;
  opacity: 1;
}

.homepage-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -1;
  background: linear-gradient(
    135deg,
    rgba(255, 255, 255, 0.85) 0%,
    rgba(255, 255, 255, 0.9) 50%,
    rgba(255, 255, 255, 0.85) 100%
  );
  backdrop-filter: blur(4px);
}

.homepage-content {
  position: relative;
  z-index: 1;
  min-height: 100vh;
}

.back-btn {
  background: linear-gradient(135deg, #a36803fb 0%, #825209 100%);
  border: none;
  border-radius: 15px;
  padding: 12px 20px;
  font-weight: 600;
}
.homepage-container {
  font-family: "思源宋体", "PingFang SC", sans-serif;
  max-width: 100%;
  margin: 0 auto;
  padding: 30px 20px;
  display: flex;
  min-height: calc(100vh - 60px);
  gap: 20px;
}

@media (max-width: 1200px) {
  .homepage-container {
    flex-direction: column;
    padding: 20px 15px;
  }

  .left-info {
    width: 100% !important;
    padding-right: 0 !important;
    margin-bottom: 20px;
  }

  .right-modules {
    width: 100% !important;
  }
}

@media (max-width: 768px) {
  .homepage-container {
    padding: 15px 10px;
  }

  .info-card {
    padding: 15px;
  }

  .module-card {
    margin-bottom: 12px;
  }
}

.left-info {
  width: 35%;
  padding-right: 20px;
  position: relative;
}

.info-card {
  height: 100%;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(15px);
  border: 2px solid rgba(139, 69, 19, 0.2);
  border-radius: 25px;
  box-shadow: 0 10px 40px rgba(139, 69, 19, 0.15);
  overflow: hidden;
  position: relative;
  transition: all 0.4s ease;
}

.info-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #8B4513 0%, #D2691E 50%, #8B4513 100%);
  z-index: 1;
}

.info-card:hover {
  transform: translateY(-8px);
  border-color: rgba(139, 69, 19, 0.4);
  box-shadow: 0 15px 50px rgba(139, 69, 19, 0.25);
}

:deep(.info-card .el-card__header) {
  background: linear-gradient(135deg, #8B4513 0%, #A0522D 50%, #D2691E 100%);
  color: white;
  text-align: center;
  padding: 25px 20px;
  border-bottom: none;
  position: relative;
  overflow: hidden;
}

:deep(.info-card .el-card__header::before) {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  animation: shimmer 3s infinite;
}

@keyframes shimmer {
  0% { left: -100%; }
  100% { left: 100%; }
}

:deep(.info-card .el-card__header h3) {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  letter-spacing: 1px;
  position: relative;
  z-index: 2;
}

:deep(.info-card .el-card__header h3::after) {
  /* content: '📚'; */
  margin-left: 10px;
  font-size: 20px;
}

:deep(.info-card .el-card__body) {
  padding: 30px 25px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.95) 0%, rgba(248, 246, 243, 0.95) 100%);
}

.info-item {
  margin-bottom: 18px;
  padding: 16px 20px;
  background: linear-gradient(135deg, rgba(139, 69, 19, 0.08) 0%, rgba(210, 105, 30, 0.05) 100%);
  border: 1px solid rgba(139, 69, 19, 0.15);
  border-left: 5px solid #8B4513;
  border-radius: 12px;
  font-size: 16px;
  color: #2c3e50;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.info-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 0;
  height: 100%;
  background: linear-gradient(90deg, rgba(139, 69, 19, 0.1), rgba(210, 105, 30, 0.1));
  transition: width 0.3s ease;
  z-index: 0;
}

.info-item:hover {
  transform: translateX(8px);
  border-left-color: #D2691E;
  box-shadow: 0 4px 15px rgba(139, 69, 19, 0.2);
  color: #1a252f;
}

.info-item:hover::before {
  width: 100%;
}

.info-item:nth-child(1)::after { content: '👤'; }
.info-item:nth-child(2)::after { content: '🎓'; }
.info-item:nth-child(3)::after { content: '📅'; }
.info-item:nth-child(4)::after { content: '🏥'; }
.info-item:nth-child(5)::after { content: '📊'; }

.info-item::after {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 18px;
  opacity: 0.6;
  transition: all 0.3s ease;
  z-index: 1;
}

.info-item:hover::after {
  opacity: 1;
  transform: translateY(-50%) scale(1.2);
}

:deep(.el-divider) {
  border-color: rgba(139, 69, 19, 0.3);
  margin: 25px 0;
  position: relative;
}

:deep(.el-divider__text) {
  background: linear-gradient(135deg, #8B4513 0%, #D2691E 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-weight: 700;
  font-size: 16px;
  padding: 0 15px;
  position: relative;
}

:deep(.el-divider__text::before) {
  content: '📋';
  margin-right: 8px;
  -webkit-text-fill-color: #8B4513;
}

/* 实验记录表格样式优化 */
.experiment-table {
  margin-top: 20px;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 6px 25px rgba(139, 69, 19, 0.15);
  border: 1px solid rgba(139, 69, 19, 0.2);
}

/* 替换现有的渐变色样式 */
:deep(.experiment-table .el-table__header-wrapper .el-table__header thead tr) {
  background: #8B4513 !important;
}

:deep(.experiment-table .el-table__header-wrapper .el-table__header thead tr th) {
  background: transparent !important;
  color: white !important;
  font-weight: 600 !important;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.3) !important;
}

:deep(.experiment-table .el-table__body tr) {
  transition: all 0.3s ease;
}

:deep(.experiment-table .el-table__body tr:hover) {
  background: linear-gradient(135deg, rgba(139, 69, 19, 0.08) 0%, rgba(210, 105, 30, 0.05) 100%);
  transform: scale(1.01);
}

:deep(.experiment-table .el-table__body td) {
  border-bottom: 1px solid rgba(139, 69, 19, 0.1);
  padding: 12px 8px;
}

:deep(.experiment-table .el-button--primary) {
  background: linear-gradient(135deg, #8B4513 0%, #D2691E 100%);
  border: none;
  border-radius: 20px;
  padding: 8px 16px;
  font-size: 13px;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(139, 69, 19, 0.3);
}

:deep(.experiment-table .el-button--primary:hover) {
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 4px 15px rgba(139, 69, 19, 0.4);
}
.right-modules {
  width: 70%;
}

.module-card {
  cursor: pointer;
  text-align: center;
  margin-bottom: 15px;
  background: rgba(255, 255, 255, 0.8); /* 只改这一行：从完全透明改为半透明 */
  border: 2px solid #808080;
  border-radius: 20px;
  transition: all 0.3s ease; /* 添加过渡效果 */
}

.module-card:hover {
  background: rgba(255, 255, 255, 1); /* 悬停时完全不透明 */
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.module-image {
  width: 100%;
  margin-bottom: 10px;
}

.module-title {
  font-weight: bold;
}


.detail-display {
  margin-top: 20px;
  border: 1px solid #e4e7ed;
  padding: 15px;
  border-radius: 4px;
}
.detail-display {
  margin-top: 20px;
  border: 1px solid #e4e7ed;
  padding: 15px;
  border-radius: 4px;
}


.detail-display p {
  margin-bottom: 5px;
}

:deep(.el-table__cell) {
  padding: 8px 0;
}

:deep(.el-table td.el-table__cell) {
  padding: 8px 0;
}

:deep(.el-table .el-table__row .el-table__cell:nth-child(4)) {
  padding-right: 10px;
}

.special-module {
  border: 2px solid red;
  font-weight: bold;
  color: red;
}
</style>