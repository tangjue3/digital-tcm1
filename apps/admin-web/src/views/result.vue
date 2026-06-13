<template>
    <div class="result-page-container">
      <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
      <h1>实验评分结果</h1>
      <el-card>
        <template #header>
          <h3>总评分：{{ totalScore }} 分</h3>
        </template>
        <div class="chart-container">
          <!-- 雷达图展示各项评分 -->
          <div ref="radarChartRef" class="chart-item"></div>
          <!-- 柱状图展示不同步骤评分对比 -->
          <div ref="barChartRef" class="chart-item"></div>
        </div>
      </el-card>
      <h1>实验排名结果</h1>
      <!-- {{ tableData[0].time }} -->
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="userId" label="用户"/>
        <el-table-column prop="time" label="时间"  />
        <el-table-column prop="score" label="得分" />
      </el-table>
    </div>
  </template>
  
  <script setup>
import { get } from '@/utils/request';
  import { ref, onMounted } from 'vue';
  import { useRoute } from 'vue-router';
  import { ElCard, ElTable, ElTableColumn, ElMessage } from 'element-plus';  
  import * as echarts from 'echarts';
let route = useRoute()
  // 模拟实验评分数据
  const totalScore = ref(null);
  const radarData = ref([0, 0, 0, 0]);
  const barData = ref([0, 0, 0, 0]);
  
  const radarChartRef = ref(null);
  const barChartRef = ref(null);
  let tableData=ref([])

function getRouteQuestionNo() {
  const value = route.query.questionNo
  return Array.isArray(value) ? (value[0] || '') : (value || '')
}

function getCaseNoFromQuestionNo(questionNo) {
  return questionNo.replace(/-\d{14}$/, '')
}

function parseExtend2(extend2) {
  if (!extend2) {
    return [0, 0, 0, 0]
  }
  try {
    const parsed = typeof extend2 === 'string' ? JSON.parse(extend2) : extend2
    const values = Object.values(parsed).map(item => Number(item) || 0)
    while (values.length < 4) {
      values.push(0)
    }
    return values.slice(0, 4)
  } catch (error) {
    console.error('[virtual-diagnosis] parse extend2 failed', error)
    ElMessage.warning('extend2 parse failed')
    return [0, 0, 0, 0]
  }
}
  onMounted( async () => {
    const questionNo = getRouteQuestionNo()
    if (!questionNo || !questionNo.includes('-')) {
      ElMessage.error('Missing valid questionNo')
      return
    }
    const caseNo = getCaseNoFromQuestionNo(questionNo)
    let resdata
    try {
      resdata= await get("/records/recordReult?questionNo="+encodeURIComponent(questionNo))
      let chartdata= await get("/records/getCharts?caseNo="+encodeURIComponent(caseNo))
      tableData.value=Array.isArray(chartdata?.data) ? chartdata.data.slice(0,5) : [];
      if (!resdata || (!resdata.questionNo && !resdata.extend2)) {
        ElMessage.warning('No result data found for questionNo')
      }
      
      totalScore.value = resdata?.score ?? 0
      const scoreData = parseExtend2(resdata?.extend2)
      radarData.value = scoreData
      barData.value = scoreData
      console.info('[virtual-diagnosis] result loaded', { questionNo, caseNo, score: totalScore.value })
    } catch (error) {
      console.error('[virtual-diagnosis] load result failed', error)
      ElMessage.error(error?.message || 'load result failed')
      return
    }
    // 初始化雷达图
    const radarChart = echarts.init(radarChartRef.value);
    const radarOption = {
      tooltip: {
        trigger: 'item',
        formatter: function (params) {
          return `${params.name}<br/>评分: ${params.value}`;
        }
      },
      radar: {
        indicator: [
          { name: '望诊', max: 25 },
          { name: '闻诊', max: 25 },
          { name: '问诊', max: 25 },
          { name: '切诊', max: 25 }
        ],
        axisLine: {
          lineStyle: {
            color: '#ccc'
          }
        },
        splitLine: {
          lineStyle: {
            color: '#ccc'
          }
        },
        splitArea: {
          areaStyle: {
            color: ['rgba(255, 255, 255, 0.1)', 'rgba(0, 0, 0, 0.1)'].reverse()
          }
        },
        name: {
          textStyle: {
            color: '#333'
          }
        },
        radius: '70%',
        splitNumber: 5,
        scale: true
      },
      series: [
        {
          name: '各项评分',
          type: 'radar',
          data: [
            {
              value: radarData.value,
              name: '各项评分'
            }
          ]
        }
      ]
    };
    radarChart.setOption(radarOption);
  
    // 初始化柱状图
    const barChart = echarts.init(barChartRef.value);
    const barOption = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        },
        formatter: function (params) {
          const param = params[0];
          return `${param.name}<br/>评分: ${param.value}`;
        }
      },
      xAxis: {
        type: 'category',
        data: ['望', '闻', '问', '切']
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          data: barData.value,
          type: 'bar'
        }
      ]
    };
    barChart.setOption(barOption);
  });
  </script>
  
  <style scoped>
  .result-page-container {
    font-family: "思源宋体", "PingFang SC", sans-serif;
  max-width: 100%;
  margin: 0 auto;
  padding: 30px 20px;
  background: rgb(255, 255, 255) url('@/assets/back.jpg') no-repeat center top;
  background-size: cover; /* 或 contain / 100% 100% 等 */
  background-attachment: fixed; /* 固定背景不随滚动 */}
  


  
  .chart-container {
    display: flex;
    gap: 20px;
    margin-top: 20px;
  }
  
  .chart-item {
    flex: 1;
    height: 300px;
  }
  </style>    
