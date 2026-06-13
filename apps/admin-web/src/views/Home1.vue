<template>
      <el-button type="primary" @click="$router.push('/login')">退出登录</el-button>
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
                      <!-- {{ row }} -->
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
              <el-col :span="8" v-for="(module, index) in modules" :key="index">
                  <el-card class="module-card" @click="goToDetail(module)">
                      <img :src="module.image" alt="模块示意图" class="module-image" />
                      <div class="module-title">{{ module.title }}</div>
                  </el-card>
              </el-col>
          </el-row>
      </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElCard, ElRow, ElCol, ElTable, ElTableColumn, ElButton } from 'element-plus';
import { useRouter } from 'vue-router';
import { get } from '@/utils/request';

const router = useRouter();
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
const modules = ref([

  {
      id: 6,
      title: '模拟教学',
      type: "",
      image: 'https://img95.699pic.com/photo/50163/1786.jpg_wh860.jpg',
      link: '/simulation'
  },{
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
  return timeString ? timeString.replace('T', ' ').substring(0, 16) : '';
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
              // remark: item.remark,
              remark: "破伤风案例",
              userId: item.userId,
              score: item.score,
              time: item.time,
              conclusion: item.conclusion,
              treatmentMethods: item.treatmentMethods,
              // 保留原有字段兼容
              date: item.time.split(' ')[0],
              title: item.caseNo,
              questionNo:item.questionNo
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


.homepage-container {
  display: flex;
  height: 100vh;
  padding: 20px;
}

.left-info {
  width: 32%;
  padding-right: 20px;
}

.info-card {
  height: 100%;
}

.info-item {
  margin-bottom: 10px;
}

.right-modules {
  width: 70%;
}

.module-card {
  cursor: pointer;
  text-align: center;
  margin-bottom: 15px;
}

.module-image {
  width: 100%;
  height: auto;
  margin-bottom: 10px;
}

.module-title {
  font-weight: bold;
}

.experiment-table {
  margin-top: 10px;
}

.detail-display {
  margin-top: 20px;
  border: 1px solid #e4e7ed;
  padding: 15px;
  border-radius: 4px;
}

.detail-display h4 {
  margin-bottom: 10px;
  color: #303133;
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
</style>    