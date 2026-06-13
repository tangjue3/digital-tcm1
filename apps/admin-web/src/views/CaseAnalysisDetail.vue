<template>
  <div class="case-detail-page">
    <div class="page-header">
      <el-button @click="$router.go(-1)" icon="Back" circle></el-button>
      <h2>诊疗案例详情</h2>
    </div>

    <el-row :gutter="20">
      <!-- 左侧：案例详细信息 -->
      <el-col :span="16">
        <el-card class="box-card case-info-card">
          <template #header>
            <div class="card-header">
              <span class="case-title">案例ID: {{ caseId }} - {{ caseData.diseaseName }}</span>
              <div class="tags">
                <el-tag v-for="tag in caseData.tags" :key="tag" size="small" style="margin-left: 5px;">{{ tag }}</el-tag>
              </div>
            </div>
          </template>
          
          <el-descriptions title="患者基本信息" :column="2" border>
            <el-descriptions-item label="姓名">{{ caseData.patientName }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ caseData.gender }}</el-descriptions-item>
            <el-descriptions-item label="年龄">{{ caseData.age }}岁</el-descriptions-item>
            <el-descriptions-item label="籍贯">{{ caseData.origin }}</el-descriptions-item>
            <el-descriptions-item label="确诊时间">{{ caseData.diagnosisDate }}</el-descriptions-item>
            <el-descriptions-item label="就诊医院">{{ caseData.hospital }}</el-descriptions-item>
            <el-descriptions-item label="主治医生">{{ caseData.doctor }}</el-descriptions-item>
          </el-descriptions>

          <div class="section-block">
            <h3><el-icon><FirstAidKit /></el-icon> 症状描述</h3>
            <p>{{ caseData.symptoms }}</p>
          </div>

          <div class="section-block">
            <h3><el-icon><Document /></el-icon> 检查报告</h3>
            <p>{{ caseData.checkReport }}</p>
          </div>

          <div class="section-block">
            <h3><el-icon><Aim /></el-icon> 诊断结论</h3>
            <p>{{ caseData.diagnosis }}</p>
          </div>

          <div class="section-block">
            <h3><el-icon><MagicStick /></el-icon> 治疗方案</h3>
            <p>{{ caseData.treatment }}</p>
          </div>

          <div class="section-block">
            <h3><el-icon><TrendCharts /></el-icon> 预后情况</h3>
            <p>{{ caseData.prognosis }}</p>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：可视化数据分析 -->
      <el-col :span="8">
        <el-card class="box-card chart-card">
          <template #header>
            <div class="card-header">
              <span>案例数据可视化</span>
            </div>
          </template>
          
          <div class="chart-container">
            <h4>治疗效果趋势</h4>
            <div ref="lineChartRef" class="chart"></div>
          </div>

          <div class="chart-container">
            <h4>症状缓解程度对比</h4>
            <div ref="barChartRef" class="chart"></div>
          </div>

          <div class="chart-container">
            <h4>同类病例预后分布</h4>
            <div ref="pieChartRef" class="chart"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { FirstAidKit, Document, Aim, MagicStick, TrendCharts, Back } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const route = useRoute()
const caseId = route.query.id

const caseData = ref({
  patientName: '王**',
  gender: '男',
  age: 35,
  origin: '浙江杭州',
  diseaseName: '腰椎间盘突出症',
  diagnosisDate: '2024-11-15',
  hospital: '杭州市中医院',
  doctor: '张医生',
  tags: ['骨科', '青壮年', '中医理疗'],
  symptoms: '腰部疼痛伴左下肢放射痛3个月，弯腰受限，久坐加重。',
  checkReport: 'MRI显示L4-L5椎间盘向后突出，压迫硬膜囊及左侧神经根。',
  diagnosis: '腰椎间盘突出症（气滞血瘀证）',
  treatment: '1. 针灸推拿：每日一次，疏通经络；2. 中药热敷：活血化瘀；3. 内服身痛逐瘀汤加减。',
  prognosis: '治疗2个疗程后，疼痛评分由8分降至2分，活动度明显改善。'
})

const lineChartRef = ref(null)
const barChartRef = ref(null)
const pieChartRef = ref(null)

onMounted(() => {
  // 模拟根据ID获取不同数据
  if (caseId === '2') {
    caseData.value = {
      patientName: '李**',
      gender: '女',
      age: 28,
      origin: '江苏南京',
      diseaseName: '偏头痛',
      diagnosisDate: '2025-01-10',
      hospital: '省中医院',
      doctor: '刘医生',
      tags: ['神经内科', '青年', '针灸'],
      symptoms: '反复发作性偏侧头痛5年，经期加重，伴恶心。',
      checkReport: '头颅CT未见明显异常，脑血流图示血管痉挛。',
      diagnosis: '偏头痛（肝阳上亢证）',
      treatment: '天麻钩藤饮加减，配合太阳、风池穴位注射。',
      prognosis: '发作频率由每周2次减少至每月1次，程度减轻。'
    }
  }

  nextTick(() => {
    initCharts()
  })
})

const initCharts = () => {
  // 折线图：疼痛评分/症状程度趋势
  if (lineChartRef.value) {
    const lineChart = echarts.init(lineChartRef.value)
    lineChart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: ['初诊', '1周', '2周', '4周', '8周'] },
      yAxis: { type: 'value', name: '症状评分' },
      series: [{
        data: [8, 6, 4, 3, 2],
        type: 'line',
        smooth: true,
        itemStyle: { color: '#D2691E' }
      }]
    })
  }

  // 柱状图：治疗前后对比
  if (barChartRef.value) {
    const barChart = echarts.init(barChartRef.value)
    barChart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: ['疼痛', '活动度', '睡眠'] },
      yAxis: { type: 'value' },
      series: [
        {
          name: '治疗前',
          type: 'bar',
          data: [8, 4, 3],
          itemStyle: { color: '#999' }
        },
        {
          name: '治疗后',
          type: 'bar',
          data: [2, 9, 8],
          itemStyle: { color: '#D2691E' }
        }
      ]
    })
  }

  // 饼图：同类病例预后
  if (pieChartRef.value) {
    const pieChart = echarts.init(pieChartRef.value)
    pieChart.setOption({
      tooltip: { trigger: 'item' },
      series: [{
        name: '预后情况',
        type: 'pie',
        radius: '50%',
        data: [
          { value: 65, name: '痊愈' },
          { value: 25, name: '好转' },
          { value: 10, name: '无效' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }]
    })
  }
}
</script>

<style scoped>
.case-detail-page {
  padding: 20px;
  background-color: #f0f2f5;
  min-height: 100vh;
}

.page-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin-left: 15px;
  color: #333;
}

.box-card {
  margin-bottom: 20px;
  border-radius: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.case-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.section-block {
  margin-top: 25px;
}

.section-block h3 {
  font-size: 16px;
  color: #8B4513;
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  border-bottom: 1px solid #eee;
  padding-bottom: 5px;
}

.section-block h3 .el-icon {
  margin-right: 8px;
}

.section-block p {
  color: #666;
  line-height: 1.6;
  text-align: justify;
}

.chart-container {
  margin-bottom: 30px;
}

.chart-container h4 {
  text-align: center;
  margin-bottom: 10px;
  color: #666;
}

.chart {
  width: 100%;
  height: 250px;
}
</style>