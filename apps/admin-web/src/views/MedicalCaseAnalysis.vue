﻿﻿﻿<template>
  <div class="case-analysis-home">
    <div class="header-section">
      <div class="title-wrap">
        <div class="title">医生诊疗案例分析</div>
        <p class="subtitle">病例检索、分类管理与可视化洞察</p>
      </div>
      <div class="search-box" v-if="activeCategory !== 'visualization'">
        <el-input
          v-model="searchQuery"
          placeholder="搜索疾病、症状、检查项目..."
          class="input-with-select"
          @keyup.enter="handleSearch"
        >
          <template #prepend>
            <el-select v-model="searchType" placeholder="全部" style="width: 90px">
              <el-option label="全部" value="all"></el-option>
              <el-option label="疾病" value="disease"></el-option>
              <el-option label="症状" value="symptom"></el-option>
            </el-select>
          </template>
          <template #append>
            <el-button :icon="Search" @click="handleSearch" />
          </template>
        </el-input>
      </div>
    </div>

    <div class="category-nav">
      <el-tabs v-model="activeCategory" @tab-click="handleCategoryClick">
        <el-tab-pane label="可视化大屏" name="visualization"></el-tab-pane>
        <el-tab-pane label="全部案例" name="all"></el-tab-pane>
        <el-tab-pane label="中医内科" name="internal"></el-tab-pane>
        <el-tab-pane label="针灸推拿科" name="acupuncture"></el-tab-pane>
        <el-tab-pane label="骨伤科" name="orthopedics"></el-tab-pane>
        <el-tab-pane label="妇科" name="gynecology"></el-tab-pane>
        <el-tab-pane label="儿科" name="pediatrics"></el-tab-pane>
      </el-tabs>
    </div>

    <div v-if="activeCategory === 'visualization'" class="visual-board">
      <div class="board-header">
        <h2>医生诊疗案例分析 - 数据可视化大屏</h2>
        <div class="board-time">数据时间：{{ dashboardDate }}</div>
      </div>

      <div class="board-grid">
        <section class="panel left-top">
          <h3>病例关键词 Top10</h3>
          <div ref="topKeywordChartRef" class="chart-box"></div>
        </section>

        <section class="panel left-bottom">
          <h3>本周病例统计</h3>
          <div class="simple-table">
            <div class="table-head">
              <span>日期</span>
              <span>病例数</span>
              <span>高风险</span>
            </div>
            <div class="table-row" v-for="item in weeklyTable" :key="item.day">
              <span>{{ item.day }}</span>
              <span>{{ item.total }}</span>
              <span>{{ item.risk }}</span>
            </div>
          </div>
        </section>

        <section class="panel center-top">
          <h3>本周诊疗总次数</h3>
          <div class="center-metrics">
            <div class="total-count">{{ animatedTotal }}</div>
            <div class="gauge-list">
              <div ref="gauge1Ref" class="gauge-item"></div>
              <div ref="gauge2Ref" class="gauge-item"></div>
              <div ref="gauge3Ref" class="gauge-item"></div>
            </div>
          </div>
        </section>

        <section class="panel center-bottom">
          <h3>病例趋势统计</h3>
          <div ref="trendChartRef" class="chart-box large"></div>
        </section>

        <section class="panel right-top">
          <h3>高频症状排行</h3>
          <div class="ranking-list">
            <div class="ranking-track">
              <div class="rank-item" v-for="(item, idx) in rankingLoopData" :key="`${item.name}-${idx}`">
                <span class="rank-index" :class="{ top: idx % symptomRanking.length < 3 }">{{ (idx % symptomRanking.length) + 1 }}</span>
                <span class="rank-name">{{ item.name }}</span>
                <span class="rank-value">{{ item.value }}</span>
              </div>
            </div>
          </div>
        </section>

        <section class="panel right-bottom">
          <h3>中医病种关键词云</h3>
          <div class="word-cloud">
            <span
              v-for="word in cloudWords"
              :key="word.text"
              class="word"
              :style="{
                left: word.left,
                top: word.top,
                fontSize: word.size,
                opacity: word.opacity
              }"
            >
              {{ word.text }}
            </span>
          </div>
        </section>
      </div>
    </div>

    <div v-else class="case-list">
      <el-empty v-if="filteredCases.length === 0" description="暂无相关案例"></el-empty>

      <el-card
        v-for="item in filteredCases"
        :key="item.id"
        class="case-item"
        shadow="hover"
        @click="goToDetail(item.id)"
      >
        <div class="case-content">
          <div class="case-main">
            <h3 class="case-title">
              <span class="disease-name">{{ item.diseaseName }}</span>
              <el-tag size="small" type="info" class="department-tag">{{ item.department }}</el-tag>
            </h3>
            <p class="case-summary">{{ item.summary }}</p>
            <div class="case-keywords">
              <el-tag
                v-for="tag in item.keywords"
                :key="tag"
                size="small"
                effect="plain"
                class="keyword-tag"
              >
                {{ tag }}
              </el-tag>
            </div>
          </div>
          <div class="case-meta">
            <span class="doctor">主治：{{ item.doctor }}</span>
            <span class="date">{{ item.date }}</span>
            <el-button type="primary" link class="view-btn">查看详情 <el-icon><ArrowRight /></el-icon></el-button>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { Search, ArrowRight } from '@element-plus/icons-vue'

const router = useRouter()
const searchQuery = ref('')
const searchType = ref('all')
const activeCategory = ref('all')

const dashboardDate = new Date().toLocaleDateString('zh-CN', {
  year: 'numeric',
  month: '2-digit',
  day: '2-digit'
})

const allCases = ref([
  {
    id: '1',
    diseaseName: '腰椎间盘突出症',
    department: '骨伤科',
    category: 'orthopedics',
    summary: '中年男性，长期重体力劳动，腰痛伴左下肢放射痛，经正骨与中药调理后明显缓解。',
    keywords: ['腰痛', '坐骨神经痛', '身痛逐瘀汤'],
    doctor: '张医生',
    date: '2026-03-21'
  },
  {
    id: '2',
    diseaseName: '偏头痛',
    department: '针灸推拿科',
    category: 'acupuncture',
    summary: '青年女性，反复偏头痛伴恶心，针刺风池、太阳等穴后，发作频率下降。',
    keywords: ['头痛', '针灸', '肝阳上亢'],
    doctor: '刘医生',
    date: '2026-03-19'
  },
  {
    id: '3',
    diseaseName: '慢性胃炎',
    department: '中医内科',
    category: 'internal',
    summary: '老年患者，胃脘隐痛多年，辨证为脾胃虚寒，予黄芪建中汤加减治疗。',
    keywords: ['胃痛', '脾胃虚寒', '黄芪建中汤'],
    doctor: '王医生',
    date: '2026-03-18'
  },
  {
    id: '4',
    diseaseName: '月经不调',
    department: '妇科',
    category: 'gynecology',
    summary: '患者月经周期不规律、量少色淡，辨证为气血亏虚，八珍汤调理后恢复。',
    keywords: ['月经不调', '气血亏虚', '八珍汤'],
    doctor: '赵医生',
    date: '2026-03-17'
  },
  {
    id: '5',
    diseaseName: '小儿咳嗽',
    department: '儿科',
    category: 'pediatrics',
    summary: '患儿夜间咳嗽加重，喉间痰鸣，辨证为痰热壅肺，清气化痰后改善。',
    keywords: ['咳嗽', '痰热', '小儿推拿'],
    doctor: '孙医生',
    date: '2026-03-16'
  },
  {
    id: '6',
    diseaseName: '颈椎病',
    department: '骨伤科',
    category: 'orthopedics',
    summary: '办公室人群，颈肩僵硬伴上肢麻木，正骨联合理疗后活动度提高。',
    keywords: ['颈肩痛', '上肢麻木', '推拿'],
    doctor: '周医生',
    date: '2026-03-14'
  },
  {
    id: '7',
    diseaseName: '高血压病',
    department: '中医内科',
    category: 'internal',
    summary: '中年男性，高血压病史5年，头晕目眩，面红目赤，辨证为肝阳上亢，予天麻钩藤饮加减治疗。',
    keywords: ['高血压', '肝阳上亢', '天麻钩藤饮'],
    doctor: '李医生',
    date: '2026-03-13'
  },
  {
    id: '8',
    diseaseName: '糖尿病',
    department: '中医内科',
    category: 'internal',
    summary: '老年患者，糖尿病史10年，口渴多饮，多食易饥，辨证为气阴两虚，予玉女煎加减治疗。',
    keywords: ['糖尿病', '气阴两虚', '玉女煎'],
    doctor: '张医生',
    date: '2026-03-12'
  },
  {
    id: '9',
    diseaseName: '慢性支气管炎',
    department: '中医内科',
    category: 'internal',
    summary: '老年患者，慢性咳嗽咳痰10余年，遇寒加重，辨证为寒痰阻肺，予小青龙汤加减治疗。',
    keywords: ['咳嗽', '寒痰阻肺', '小青龙汤'],
    doctor: '王医生',
    date: '2026-03-11'
  },
  {
    id: '10',
    diseaseName: '失眠',
    department: '中医内科',
    category: 'internal',
    summary: '青年女性，失眠多梦，心烦易怒，辨证为心脾两虚，予归脾汤加减治疗。',
    keywords: ['失眠', '心脾两虚', '归脾汤'],
    doctor: '刘医生',
    date: '2026-03-10'
  },
  {
    id: '11',
    diseaseName: '便秘',
    department: '中医内科',
    category: 'internal',
    summary: '中年女性，大便干结难解，腹胀腹痛，辨证为肠道津亏，予麻子仁丸加减治疗。',
    keywords: ['便秘', '肠道津亏', '麻子仁丸'],
    doctor: '赵医生',
    date: '2026-03-09'
  },
  {
    id: '12',
    diseaseName: '过敏性鼻炎',
    department: '中医内科',
    category: 'internal',
    summary: '青年男性，反复鼻塞流涕，遇冷风加重，辨证为肺卫不固，予玉屏风散加减治疗。',
    keywords: ['过敏性鼻炎', '肺卫不固', '玉屏风散'],
    doctor: '孙医生',
    date: '2026-03-08'
  },
  {
    id: '13',
    diseaseName: '肩周炎',
    department: '针灸推拿科',
    category: 'acupuncture',
    summary: '中年女性，右肩关节疼痛，活动受限，针刺肩髃、肩贞等穴后，疼痛明显减轻。',
    keywords: ['肩周炎', '针灸', '肩髃穴'],
    doctor: '周医生',
    date: '2026-03-07'
  },
  {
    id: '14',
    diseaseName: '面瘫',
    department: '针灸推拿科',
    category: 'acupuncture',
    summary: '青年男性，左侧面部肌肉瘫痪，口角歪斜，针刺阳白、地仓等穴后，症状逐渐恢复。',
    keywords: ['面瘫', '针灸', '阳白穴'],
    doctor: '吴医生',
    date: '2026-03-06'
  },
  {
    id: '15',
    diseaseName: '腰肌劳损',
    department: '针灸推拿科',
    category: 'acupuncture',
    summary: '中年男性，腰部肌肉酸痛，劳累后加重，推拿配合艾灸治疗后，症状明显改善。',
    keywords: ['腰肌劳损', '推拿', '艾灸'],
    doctor: '郑医生',
    date: '2026-03-05'
  },
  {
    id: '16',
    diseaseName: '膝关节炎',
    department: '骨伤科',
    category: 'orthopedics',
    summary: '老年女性，膝关节疼痛，行走困难，辨证为寒湿痹阻，予中药外敷配合理疗治疗。',
    keywords: ['膝关节炎', '寒湿痹阻', '中药外敷'],
    doctor: '王医生',
    date: '2026-03-04'
  },
  {
    id: '17',
    diseaseName: '踝关节扭伤',
    department: '骨伤科',
    category: 'orthopedics',
    summary: '青年男性，踝关节扭伤后肿胀疼痛，予正骨手法复位配合中药熏洗治疗。',
    keywords: ['踝关节扭伤', '正骨', '中药熏洗'],
    doctor: '张医生',
    date: '2026-03-03'
  },
  {
    id: '18',
    diseaseName: '网球肘',
    department: '骨伤科',
    category: 'orthopedics',
    summary: '中年男性，肘关节外侧疼痛，握拳时加重，予小针刀治疗后，疼痛明显减轻。',
    keywords: ['网球肘', '小针刀', '疼痛'],
    doctor: '李医生',
    date: '2026-03-02'
  },
  {
    id: '19',
    diseaseName: '痛经',
    department: '妇科',
    category: 'gynecology',
    summary: '青年女性，经期腹痛剧烈，色暗有块，辨证为气滞血瘀，予少腹逐瘀汤加减治疗。',
    keywords: ['痛经', '气滞血瘀', '少腹逐瘀汤'],
    doctor: '赵医生',
    date: '2026-03-01'
  },
  {
    id: '20',
    diseaseName: '带下病',
    department: '妇科',
    category: 'gynecology',
    summary: '中年女性，带下量多，色黄腥臭，辨证为湿热下注，予龙胆泻肝汤加减治疗。',
    keywords: ['带下病', '湿热下注', '龙胆泻肝汤'],
    doctor: '孙医生',
    date: '2026-02-29'
  },
  {
    id: '21',
    diseaseName: '产后恶露不尽',
    department: '妇科',
    category: 'gynecology',
    summary: '产后女性，恶露持续不尽，色淡质稀，辨证为气血亏虚，予补中益气汤加减治疗。',
    keywords: ['产后恶露不尽', '气血亏虚', '补中益气汤'],
    doctor: '刘医生',
    date: '2026-02-28'
  },
  {
    id: '22',
    diseaseName: '小儿厌食',
    department: '儿科',
    category: 'pediatrics',
    summary: '3岁患儿，食欲不振，面色萎黄，辨证为脾胃虚弱，予参苓白术散加减治疗。',
    keywords: ['小儿厌食', '脾胃虚弱', '参苓白术散'],
    doctor: '周医生',
    date: '2026-02-27'
  },
  {
    id: '23',
    diseaseName: '小儿遗尿',
    department: '儿科',
    category: 'pediatrics',
    summary: '5岁患儿，夜间遗尿，神疲乏力，辨证为肾气不足，予缩泉丸加减治疗。',
    keywords: ['小儿遗尿', '肾气不足', '缩泉丸'],
    doctor: '吴医生',
    date: '2026-02-26'
  },
  {
    id: '24',
    diseaseName: '小儿腹泻',
    department: '儿科',
    category: 'pediatrics',
    summary: '1岁患儿，腹泻稀水样便，日行数次，辨证为脾虚湿盛，予七味白术散加减治疗。',
    keywords: ['小儿腹泻', '脾虚湿盛', '七味白术散'],
    doctor: '郑医生',
    date: '2026-02-25'
  }
])

const filteredCases = computed(() => {
  return allCases.value.filter((item) => {
    if (activeCategory.value !== 'all' && item.category !== activeCategory.value) {
      return false
    }

    if (searchQuery.value) {
      const query = searchQuery.value.trim().toLowerCase()
      const matchDisease = item.diseaseName.toLowerCase().includes(query)
      const matchSummary = item.summary.toLowerCase().includes(query)
      const matchKeywords = item.keywords.some((k) => k.toLowerCase().includes(query))

      if (searchType.value === 'disease') return matchDisease
      if (searchType.value === 'symptom') return matchSummary || matchKeywords

      return matchDisease || matchSummary || matchKeywords
    }

    return true
  })
})

const weeklyTable = ref([
  { day: '周一', total: 78, risk: 9 },
  { day: '周二', total: 88, risk: 12 },
  { day: '周三', total: 95, risk: 10 },
  { day: '周四', total: 89, risk: 11 },
  { day: '周五', total: 92, risk: 13 },
  { day: '周六', total: 74, risk: 8 },
  { day: '周日', total: 66, risk: 7 }
])

const totalCases = computed(() => weeklyTable.value.reduce((sum, item) => sum + item.total, 0))

const symptomRanking = ref([
  { name: '腰痛伴下肢放射痛', value: 1254 },
  { name: '咳嗽痰多夜间加重', value: 986 },
  { name: '胃脘隐痛食后胀满', value: 742 },
  { name: '头痛眩晕情绪波动', value: 620 },
  { name: '月经量少色淡乏力', value: 487 }
])
const rankingLoopData = computed(() => [...symptomRanking.value, ...symptomRanking.value])
const animatedTotal = ref(0)

const cloudWords = ref([
  { text: '腰椎间盘突出', left: '35%', top: '42%', size: '38px', opacity: 0.95 },
  { text: '偏头痛', left: '16%', top: '22%', size: '24px', opacity: 0.88 },
  { text: '慢性胃炎', left: '60%', top: '26%', size: '26px', opacity: 0.9 },
  { text: '月经不调', left: '70%', top: '54%', size: '22px', opacity: 0.86 },
  { text: '小儿咳嗽', left: '18%', top: '62%', size: '24px', opacity: 0.82 },
  { text: '颈椎病', left: '56%', top: '68%', size: '20px', opacity: 0.8 },
  { text: '肝阳上亢', left: '8%', top: '46%', size: '18px', opacity: 0.75 },
  { text: '脾胃虚寒', left: '45%', top: '14%', size: '19px', opacity: 0.78 },
  { text: '推拿', left: '78%', top: '34%', size: '17px', opacity: 0.72 },
  { text: '针灸', left: '28%', top: '76%', size: '16px', opacity: 0.7 }
])

const topKeywordChartRef = ref(null)
const trendChartRef = ref(null)
const gauge1Ref = ref(null)
const gauge2Ref = ref(null)
const gauge3Ref = ref(null)

let topKeywordChart = null
let trendChart = null
let gaugeChart1 = null
let gaugeChart2 = null
let gaugeChart3 = null
let dashboardTimer = null
let countTimer = null
const trendAxis = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
const trendSeriesA = ref([180, 160, 172, 148, 206, 194, 188])
const trendSeriesB = ref([120, 132, 128, 150, 162, 154, 169])
const gaugeValues = ref([87, 73, 76])

const handleSearch = () => {
  console.log('Searching for:', searchQuery.value)
}

const handleCategoryClick = async (tab) => {
  if (tab.props.name === 'visualization') {
    animatedTotal.value = 0
    await nextTick()
    initVisualizationCharts()
  }
}

const goToDetail = (id) => {
  router.push(`/home/caseAnalysisDetail?id=${id}`)
}

const initVisualizationCharts = () => {
  initTopKeywordChart()
  initTrendChart()
  initGaugeCharts()
}

const initTopKeywordChart = () => {
  if (!topKeywordChartRef.value) return
  topKeywordChart?.dispose()
  topKeywordChart = echarts.init(topKeywordChartRef.value)

  topKeywordChart.setOption({
    grid: { left: 45, right: 18, top: 20, bottom: 36 },
    xAxis: {
      type: 'category',
      data: ['腰痛', '咳嗽', '胃痛', '头痛', '月经不调', '颈肩痛', '失眠', '乏力', '眩晕', '腹胀'],
      axisLine: { lineStyle: { color: '#4e82d1' } },
      axisLabel: { color: '#b9d4ff', fontSize: 11, interval: 0, rotate: 25 }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      splitLine: { lineStyle: { color: 'rgba(120,170,255,0.2)' } },
      axisLabel: { color: '#b9d4ff' }
    },
    series: [{
      data: [98, 88, 84, 82, 78, 74, 71, 69, 66, 63],
      type: 'bar',
      barWidth: 12,
      itemStyle: {
        borderRadius: [4, 4, 0, 0],
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#49d8ff' },
          { offset: 1, color: '#1f6bf2' }
        ])
      }
    }]
  })
}

const initTrendChart = () => {
  if (!trendChartRef.value) return
  trendChart?.dispose()
  trendChart = echarts.init(trendChartRef.value)

  trendChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: {
      top: 0,
      textStyle: { color: '#b9d4ff' },
      data: ['门诊接诊数', '病例归档数']
    },
    grid: { left: 36, right: 18, top: 40, bottom: 30 },
    xAxis: {
      type: 'category',
      data: trendAxis,
      axisLine: { lineStyle: { color: '#4e82d1' } },
      axisLabel: { color: '#b9d4ff' }
    },
    yAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: 'rgba(120,170,255,0.2)' } },
      axisLabel: { color: '#b9d4ff' }
    },
    series: [
      {
        name: '门诊接诊数',
        type: 'line',
        smooth: true,
        data: trendSeriesA.value,
        symbol: 'circle',
        symbolSize: 6,
        lineStyle: { width: 3, color: '#45d8ff' },
        itemStyle: { color: '#45d8ff' }
      },
      {
        name: '病例归档数',
        type: 'line',
        smooth: true,
        data: trendSeriesB.value,
        symbol: 'circle',
        symbolSize: 6,
        lineStyle: { width: 3, color: '#5ca5ff' },
        itemStyle: { color: '#5ca5ff' }
      }
    ]
  })
}

const createGaugeOption = (name, value, color) => ({
  series: [
    {
      type: 'gauge',
      startAngle: 220,
      endAngle: -40,
      radius: '92%',
      progress: { show: true, roundCap: true, width: 10, itemStyle: { color } },
      axisLine: { lineStyle: { width: 10, color: [[1, 'rgba(125,177,255,0.2)']] } },
      pointer: { show: false },
      axisTick: { show: false },
      splitLine: { show: false },
      axisLabel: { show: false },
      detail: {
        valueAnimation: true,
        formatter: '{value}%',
        color: '#d8e8ff',
        fontSize: 16,
        offsetCenter: [0, '6%']
      },
      title: {
        offsetCenter: [0, '70%'],
        color: '#9fc6ff',
        fontSize: 12
      },
      data: [{ value, name }]
    }
  ]
})

const initGaugeCharts = () => {
  if (!gauge1Ref.value || !gauge2Ref.value || !gauge3Ref.value) return
  gaugeChart1?.dispose()
  gaugeChart2?.dispose()
  gaugeChart3?.dispose()

  gaugeChart1 = echarts.init(gauge1Ref.value)
  gaugeChart2 = echarts.init(gauge2Ref.value)
  gaugeChart3 = echarts.init(gauge3Ref.value)

  gaugeChart1.setOption(createGaugeOption('归档率', gaugeValues.value[0], '#48d6ff'))
  gaugeChart2.setOption(createGaugeOption('辨证率', gaugeValues.value[1], '#66b0ff'))
  gaugeChart3.setOption(createGaugeOption('回访率', gaugeValues.value[2], '#8bc1ff'))
}

const updateAnimatedTotal = () => {
  const target = totalCases.value
  const diff = target - animatedTotal.value
  if (Math.abs(diff) <= 1) {
    animatedTotal.value = target
    return
  }
  animatedTotal.value += Math.ceil(diff / 6)
}

const tickDashboardData = () => {
  const nextA = Math.max(120, Math.min(240, trendSeriesA.value[trendSeriesA.value.length - 1] + Math.round((Math.random() - 0.5) * 24)))
  const nextB = Math.max(90, Math.min(210, trendSeriesB.value[trendSeriesB.value.length - 1] + Math.round((Math.random() - 0.5) * 20)))
  trendSeriesA.value = [...trendSeriesA.value.slice(1), nextA]
  trendSeriesB.value = [...trendSeriesB.value.slice(1), nextB]
  if (trendChart) {
    trendChart.setOption({
      series: [
        { name: '门诊接诊数', data: trendSeriesA.value },
        { name: '病例归档数', data: trendSeriesB.value }
      ]
    })
  }

  gaugeValues.value = gaugeValues.value.map((value, index) => {
    const delta = Math.round((Math.random() - 0.5) * 4)
    const min = index === 0 ? 80 : 68
    const max = index === 0 ? 95 : 88
    return Math.max(min, Math.min(max, value + delta))
  })
  if (gaugeChart1 && gaugeChart2 && gaugeChart3) {
    gaugeChart1.setOption(createGaugeOption('归档率', gaugeValues.value[0], '#48d6ff'))
    gaugeChart2.setOption(createGaugeOption('辨证率', gaugeValues.value[1], '#66b0ff'))
    gaugeChart3.setOption(createGaugeOption('回访率', gaugeValues.value[2], '#8bc1ff'))
  }
}

const resizeAllCharts = () => {
  topKeywordChart?.resize()
  trendChart?.resize()
  gaugeChart1?.resize()
  gaugeChart2?.resize()
  gaugeChart3?.resize()
}

onMounted(() => {
  animatedTotal.value = totalCases.value
  if (activeCategory.value === 'visualization') {
    nextTick(() => {
      initVisualizationCharts()
    })
  }
  dashboardTimer = window.setInterval(() => {
    if (activeCategory.value === 'visualization') tickDashboardData()
  }, 5000)
  countTimer = window.setInterval(() => {
    if (activeCategory.value === 'visualization') updateAnimatedTotal()
  }, 120)
  window.addEventListener('resize', resizeAllCharts)
})

onUnmounted(() => {
  window.removeEventListener('resize', resizeAllCharts)
  if (dashboardTimer) window.clearInterval(dashboardTimer)
  if (countTimer) window.clearInterval(countTimer)
  topKeywordChart?.dispose()
  trendChart?.dispose()
  gaugeChart1?.dispose()
  gaugeChart2?.dispose()
  gaugeChart3?.dispose()
})
</script>

<style scoped>
.case-analysis-home {
  min-height: 100vh;
  padding: 18px;
  background: linear-gradient(180deg, #f5f9ff 0%, #eaf3ff 100%);
}

.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 16px 18px;
  border-radius: 12px;
  border: 1px solid #d2e3ff;
  background: #fff;
}

.title-wrap {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.title {
  font-size: 22px;
  font-weight: 700;
  color: #16458d;
}

.subtitle {
  font-size: 13px;
  color: #4b75b1;
}

.search-box {
  width: 420px;
}

:deep(.el-input-group__prepend) {
  background-color: #fff;
}

.category-nav {
  padding: 8px 18px 0;
  border-radius: 12px;
  border: 1px solid #d2e3ff;
  margin-bottom: 16px;
  background: #fff;
}

:deep(.el-tabs__item.is-active) {
  color: #1f6bf2;
  font-weight: 700;
}

:deep(.el-tabs__active-bar) {
  background-color: #1f6bf2;
}

.visual-board {
  border-radius: 14px;
  padding: 18px;
  color: #d8e8ff;
  background:
    radial-gradient(circle at 20% 15%, rgba(50, 111, 255, 0.25), transparent 40%),
    radial-gradient(circle at 85% 10%, rgba(73, 216, 255, 0.18), transparent 35%),
    linear-gradient(160deg, #041a46 0%, #052766 45%, #093583 100%);
  border: 1px solid rgba(120, 170, 255, 0.35);
  box-shadow: 0 18px 45px rgba(4, 26, 70, 0.45);
}

.board-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}

.board-header h2 {
  margin: 0;
  font-size: 26px;
  letter-spacing: 1px;
  color: #ffffff;
  line-height: 1.3;
}

.board-time {
  font-size: 12px;
  color: #9fc6ff;
}

.board-grid {
  display: grid;
  grid-template-columns: 1.05fr 1.6fr 1.05fr;
  grid-template-rows: 290px 290px;
  gap: 14px;
}

.panel {
  border-radius: 12px;
  border: 1px solid rgba(120, 170, 255, 0.35);
  background: linear-gradient(180deg, rgba(10, 48, 112, 0.78) 0%, rgba(9, 42, 96, 0.55) 100%);
  backdrop-filter: blur(2px);
  padding: 12px;
  position: relative;
  overflow: hidden;
  animation: panelFade 0.55s ease both;
}

.panel::after {
  content: '';
  position: absolute;
  inset: 0;
  border: 1px solid rgba(127, 182, 255, 0.22);
  border-radius: 12px;
  pointer-events: none;
}

.panel h3 {
  margin: 0 0 8px;
  font-size: 15px;
  color: #d8e8ff;
  letter-spacing: 0.4px;
}

.chart-box {
  width: 100%;
  height: calc(100% - 28px);
}

.chart-box.large {
  height: calc(100% - 30px);
}

.left-top { grid-column: 1; grid-row: 1; }
.left-bottom { grid-column: 1; grid-row: 2; }
.center-top { grid-column: 2; grid-row: 1; }
.center-bottom { grid-column: 2; grid-row: 2; }
.right-top { grid-column: 3; grid-row: 1; }
.right-bottom { grid-column: 3; grid-row: 2; }

.center-metrics {
  height: calc(100% - 28px);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 14px;
}

.total-count {
  font-size: 76px;
  font-weight: 800;
  line-height: 1;
  color: #ffd86b;
  text-shadow: 0 8px 20px rgba(255, 216, 107, 0.28);
  letter-spacing: 1px;
}

.gauge-list {
  width: 100%;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
}

.gauge-item {
  height: 128px;
}

.simple-table {
  margin-top: 8px;
  border: 1px solid rgba(125, 177, 255, 0.22);
  border-radius: 10px;
  overflow: hidden;
}

.table-head,
.table-row {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  padding: 10px 12px;
  font-size: 13px;
}

.table-head {
  background: rgba(73, 141, 255, 0.2);
  color: #e3efff;
  font-weight: 700;
}

.table-row {
  color: #c3dbff;
  border-top: 1px solid rgba(125, 177, 255, 0.2);
}

.ranking-list {
  margin-top: 10px;
  height: calc(100% - 36px);
  overflow: hidden;
}

.ranking-track {
  display: flex;
  flex-direction: column;
  gap: 10px;
  animation: rankingScroll 12s linear infinite;
}

.rank-item {
  display: grid;
  grid-template-columns: 28px minmax(0, 1fr) 64px;
  align-items: center;
  gap: 8px;
  padding: 10px;
  border-radius: 8px;
  background: rgba(12, 64, 150, 0.45);
}

.rank-index {
  width: 24px;
  height: 24px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  background: rgba(125, 177, 255, 0.2);
}

.rank-index.top {
  background: linear-gradient(120deg, #ffd86b, #f6b847);
  color: #20345f;
  font-weight: 700;
}

.rank-name {
  font-size: 13px;
  color: #d8e8ff;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.rank-value {
  font-size: 13px;
  color: #7dc6ff;
  text-align: right;
}

.word-cloud {
  margin-top: 8px;
  position: relative;
  width: 100%;
  height: calc(100% - 32px);
  border-radius: 10px;
  background: radial-gradient(circle at 50% 50%, rgba(73, 141, 255, 0.25), rgba(8, 36, 87, 0.35));
  overflow: hidden;
}

.word {
  position: absolute;
  color: #bfe0ff;
  font-weight: 700;
  white-space: nowrap;
  transform: translate(-50%, -50%);
  animation: pulse 3.2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { text-shadow: 0 0 0 rgba(73, 216, 255, 0); }
  50% { text-shadow: 0 0 14px rgba(73, 216, 255, 0.42); }
}

@keyframes rankingScroll {
  0% { transform: translateY(0); }
  100% { transform: translateY(calc(-50% - 5px)); }
}

@keyframes panelFade {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.case-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.case-item {
  cursor: pointer;
  transition: all 0.25s;
  border: 1px solid #d2e3ff;
  border-left: 4px solid transparent;
  border-radius: 12px;
}

.case-item:hover {
  transform: translateX(5px);
  border-left-color: #1f6bf2;
  box-shadow: 0 8px 22px rgba(31, 107, 242, 0.14);
}

.case-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.case-main {
  flex: 1;
  margin-right: 20px;
}

.case-title {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  font-size: 18px;
  color: #173f81;
}

.department-tag {
  margin-left: 10px;
}

.case-summary {
  color: #4d6ea3;
  font-size: 14px;
  margin-bottom: 10px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.keyword-tag {
  margin-right: 8px;
  color: #1f6bf2;
  background-color: #edf4ff;
  border-color: #d2e3ff;
}

.case-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  min-width: 140px;
}

.doctor,
.date {
  font-size: 13px;
  color: #6c86ad;
  margin-bottom: 5px;
}

.view-btn {
  margin-top: 5px;
  color: #1f6bf2;
}

@media (max-width: 1400px) {
  .board-grid {
    grid-template-columns: 1fr;
    grid-template-rows: repeat(6, 280px);
  }

  .left-top,
  .left-bottom,
  .center-top,
  .center-bottom,
  .right-top,
  .right-bottom {
    grid-column: auto;
    grid-row: auto;
  }
}

@media (max-width: 900px) {
  .header-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .search-box {
    width: 100%;
  }

  .board-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 6px;
  }

  .board-header h2 {
    font-size: 20px;
  }

  .total-count {
    font-size: 56px;
  }

  .gauge-list {
    grid-template-columns: 1fr;
  }
}
</style>
