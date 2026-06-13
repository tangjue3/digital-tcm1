<template>
    <div class="knowledge-page">
      <!-- <div style="color: #fff;
      height: 50px;
      line-height: 50px;
      padding-left: 40px;
      background-color: #3d342d; font-weight: bold;">中医药知识库</div> -->
  
      <el-tabs v-model="activeTab" type="border-card" class="knowledge-tabs" @tab-click="handleTabClick">
        <!-- 原有的功能模块 -->
        <el-tab-pane label="方药查询" name="prescription_query">
          <div class="func-content">
            <el-input v-model="searchKey" placeholder="输入方剂/药材名称搜索" style="width: 300px; margin-bottom: 20px; margin-right: 10px;"></el-input>
            <el-button type="primary" @click="searchPrescription">搜索</el-button>
            
            <el-table :data="prescriptionList" border style="margin-top: 20px;">
              <el-table-column prop="name" label="方剂名称"></el-table-column>
              <el-table-column prop="composition" label="组成"></el-table-column>
              <el-table-column prop="effect" label="功效"></el-table-column>
              <el-table-column prop="indication" label="适应症"></el-table-column>
            </el-table>
          </div>
        </el-tab-pane>

        <el-tab-pane label="医案解读" name="medical_record">
          <div class="func-content">
            <el-select v-model="caseType" placeholder="选择病症类型">
              <el-option label="感冒" value="感冒"></el-option>
              <el-option label="胃痛" value="胃痛"></el-option>
              <el-option label="失眠" value="失眠"></el-option>
            </el-select>
            <el-button type="primary" @click="getMedicalRecord" style="margin-left: 10px;">查看解读</el-button>
            
            <div class="record-content" v-if="medicalRecord">
              <h4>经典医案：</h4>
              <p>{{ medicalRecord.case }}</p>
              <h4>AI解读：</h4>
              <p>{{ medicalRecord.analysis }}</p>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="治法推荐" name="therapy">
          <div class="func-content">
            <el-select v-model="symptomType" placeholder="选择核心症状">
              <el-option label="发热恶寒" value="发热恶寒"></el-option>
              <el-option label="胃脘胀痛" value="胃脘胀痛"></el-option>
              <el-option label="心悸失眠" value="心悸失眠"></el-option>
            </el-select>
            <el-button type="primary" @click="getTherapy" style="margin-left: 10px;">获取推荐</el-button>
            
            <div class="therapy-content" v-if="therapyList.length">
              <h4>推荐治法：</h4>
              <ul>
                <li v-for="(item, index) in therapyList" :key="index">{{ item }}</li>
              </ul>
            </div>
          </div>
        </el-tab-pane>

        <!-- 从中医四诊迁移过来的模块 -->
        <el-tab-pane label="中医好文" name="article">
            <div class="search-group">
              <el-input v-model="searchKeywords.article" placeholder="搜索中医好文" style="width: 300px; margin-right: 10px;"></el-input>
              <el-button type="primary" @click="filterContent('article')">搜索</el-button>
            </div>
            <div class="card-list">
                <el-card v-for="(item, index) in getCurrentContent('article')" :key="index" class="content-card" @click="jump(item, 'article')">
                    <template #header>
                        <el-text line-clamp="1">{{ item.title }}</el-text>
                    </template>
                    <img :src="item.imgUrl" alt="模块示意图" class="module-image" />
                    <el-text line-clamp="3">{{ item.description }}</el-text>
                </el-card>
            </div>
        </el-tab-pane>

        <el-tab-pane label="中药食谱" name="recipe">
            <div class="search-group">
              <el-input v-model="searchKeywords.recipe" placeholder="搜索中药食谱" style="width: 300px; margin-right: 10px;"></el-input>
              <el-button type="primary" @click="filterContent('recipe')">搜索</el-button>
            </div>
            <div class="card-list">
                <el-card v-for="(item, index) in getCurrentContent('recipe')" :key="index" class="content-card" @click="jump(item, 'recipe')">
                    <template #header>
                        <el-text line-clamp="1">{{ item.title }}</el-text>
                    </template>
                    <img :src="item.imgUrl" alt="模块示意图" class="module-image" />
                    <el-text line-clamp="3">{{ item.description }}</el-text>
                </el-card>
            </div>
        </el-tab-pane>

        <el-tab-pane label="中医教学视频" name="video">
            <div class="search-group">
              <el-input v-model="searchKeywords.video" placeholder="搜索中医教学视频" style="width: 300px; margin-right: 10px;"></el-input>
              <el-button type="primary" @click="filterContent('video')">搜索</el-button>
            </div>
             <div class="card-list">
                <el-card v-for="(item, index) in getCurrentContent('video')" :key="index" class="content-card" @click="jump(item, 'video')">
                    <template #header>
                        <el-text line-clamp="1">{{ item.title }}</el-text>
                    </template>
                    <img :src="item.imgUrl" alt="模块示意图" class="module-image" />
                    <el-text line-clamp="3">{{ item.description }}</el-text>
                </el-card>
            </div>
        </el-tab-pane>

        <el-tab-pane label="常见病防治" name="prevention">
            <div class="search-group">
              <el-input v-model="searchKeywords.prevention" placeholder="搜索常见病防治" style="width: 300px; margin-right: 10px;"></el-input>
              <el-button type="primary" @click="filterContent('prevention')">搜索</el-button>
            </div>
             <div class="card-list">
                <el-card v-for="(item, index) in getCurrentContent('prevention')" :key="index" class="content-card" @click="jump(item, 'prevention')">
                    <template #header>
                        <el-text line-clamp="1">{{ item.title }}</el-text>
                    </template>
                    <img :src="item.imgUrl" alt="模块示意图" class="module-image" />
                    <el-text line-clamp="3">{{ item.description }}</el-text>
                </el-card>
            </div>
        </el-tab-pane>

        <el-tab-pane label="常见药方" name="prescription">
            <div class="search-group">
              <el-input v-model="searchKeywords.prescription" placeholder="搜索常见药方" style="width: 300px; margin-right: 10px;"></el-input>
              <el-button type="primary" @click="filterContent('prescription')">搜索</el-button>
            </div>
             <div class="card-list">
                <el-card v-for="(item, index) in getCurrentContent('prescription')" :key="index" class="content-card" @click="jump(item, 'prescription')">
                    <template #header>
                        <el-text line-clamp="1">{{ item.title }}</el-text>
                    </template>
                    <img :src="item.imgUrl" alt="模块示意图" class="module-image" />
                    <el-text line-clamp="3">{{ item.description }}</el-text>
                </el-card>
            </div>
        </el-tab-pane>

      </el-tabs>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref, computed, onMounted } from 'vue'
  import { ElCard, ElButton, ElInput, ElTable, ElTableColumn, ElSelect, ElOption, ElTabs, ElTabPane, ElText } from 'element-plus'
  import { useRouter } from 'vue-router'
  import { get } from '@/utils/request'
  
  const router = useRouter()
  const activeTab = ref('prescription_query')
  
  // --- 原有功能逻辑 ---
  
  // 方药查询相关
  const searchKey = ref('')
  const defaultPrescriptionData = [
    {
      name: '逍遥散',
      composition: '柴胡、当归、白芍、白术、茯苓、炙甘草、薄荷、生姜',
      effect: '疏肝解郁，健脾养血',
      indication: '肝郁脾虚证，症见胁肋胀痛、头晕目眩、神疲食少等'
    },
    {
      name: '四君子汤',
      composition: '人参、白术、茯苓、炙甘草',
      effect: '益气健脾',
      indication: '脾胃气虚证，症见面色萎白、语声低微、气短乏力等'
    },
    {
      name: '麻黄汤',
      composition: '麻黄、桂枝、杏仁、炙甘草',
      effect: '发汗解表，宣肺平喘',
      indication: '外感风寒表实证，症见恶寒发热、无汗而喘等'
    },
    {
      name: '四物汤',
      composition: '当归、熟地、白芍、川芎',
      effect: '补血调血',
      indication: '营血虚滞证，症见头晕目眩、心悸失眠、面色无华等'
    },
    {
      name: '六味地黄丸',
      composition: '熟地、山茱萸、山药、泽泻、茯苓、丹皮',
      effect: '滋阴补肾',
      indication: '肾阴不足证，症见腰膝酸软、头晕耳鸣、盗汗遗精等'
    },
    {
      name: '补中益气汤',
      composition: '黄芪、人参、白术、炙甘草、当归、陈皮、升麻、柴胡',
      effect: '补中益气，升阳举陷',
      indication: '脾胃气虚证，症见体倦乏力、少气懒言、内脏下垂等'
    },
    {
      name: '桂枝汤',
      composition: '桂枝、白芍、生姜、大枣、炙甘草',
      effect: '解肌发表，调和营卫',
      indication: '外感风寒表虚证，症见发热、恶风、汗出等'
    },
    {
      name: '小柴胡汤',
      composition: '柴胡、黄芩、人参、半夏、甘草、生姜、大枣',
      effect: '和解少阳',
      indication: '少阳证，症见寒热往来、胸胁苦满、口苦咽干等'
    },
    {
      name: '白虎汤',
      composition: '石膏、知母、粳米、炙甘草',
      effect: '清热生津',
      indication: '阳明气分热盛证，症见壮热面赤、烦渴引饮、脉洪大等'
    },
    {
      name: '理中丸',
      composition: '人参、干姜、白术、炙甘草',
      effect: '温中祛寒，补气健脾',
      indication: '脾胃虚寒证，症见脘腹冷痛、呕吐泄泻、畏寒肢冷等'
    }
  ]

  const prescriptionList = ref([...defaultPrescriptionData])
  
  const searchPrescription = () => {
    if (!searchKey.value) {
      prescriptionList.value = [...defaultPrescriptionData]
      return
    }
    prescriptionList.value = defaultPrescriptionData.filter(item =>
      item.name.includes(searchKey.value) || item.composition.includes(searchKey.value)
    )
  }
  
  // 医案解读相关
  const caseType = ref('')
  const medicalRecord = ref<any>(null)
  const getMedicalRecord = () => {
    if (!caseType.value) {
      alert('请选择病症类型！')
      return
    }
    if (caseType.value === '感冒') {
      medicalRecord.value = {
        case: '患者男，28岁，恶寒发热1天，无汗，鼻塞流清涕，头痛身痛，舌苔薄白，脉浮紧。予麻黄汤加减，1剂后汗出热退，诸症缓解。',
        analysis: '本案为外感风寒表实证，麻黄汤为辛温解表之代表方，麻黄发汗解表、宣肺平喘，桂枝解肌发表、温通经脉，杏仁降利肺气，炙甘草调和诸药。药证相符，故收效甚速。'
      }
    } else if (caseType.value === '胃痛') {
      medicalRecord.value = {
        case: '患者女，45岁，胃脘隐痛1月余，喜温喜按，食少便溏，神疲乏力，舌淡苔白，脉细弱。予黄芪建中汤加减，服药1周后胃痛减轻，食欲改善。',
        analysis: '本案为脾胃虚寒证，黄芪建中汤温中补虚、和里缓急，黄芪益气补中，桂枝温中散寒，白芍缓急止痛，饴糖补中健脾，共奏温中健脾、和胃止痛之功。'
      }
    }
  }
  
  // 治法推荐相关
  const symptomType = ref('')
  const therapyList = ref<string[]>([])
  const getTherapy = () => {
    if (!symptomType.value) {
      alert('请选择核心症状！')
      return
    }
    if (symptomType.value === '发热恶寒') {
      therapyList.value = [
        '辛温解表法：适用于风寒感冒，代表方麻黄汤、桂枝汤；',
        '辛凉解表法：适用于风热感冒，代表方银翘散、桑菊饮；',
        '扶正解表法：适用于气虚感冒，代表方参苏饮、玉屏风散。'
      ]
    } else if (symptomType.value === '胃脘胀痛') {
      therapyList.value = [
        '疏肝和胃法：适用于肝胃不和证，代表方柴胡疏肝散；',
        '温中健脾法：适用于脾胃虚寒证，代表方理中丸、黄芪建中汤；',
        '消食导滞法：适用于食积胃脘证，代表方保和丸、枳实导滞丸。'
      ]
    }
  }
  
  // --- 迁移功能逻辑 ---
  
  const allContents = ref<any>({
    article: [],
    recipe: [],
    video: [],
    prevention: [],
    prescription: []
  })
  
  const searchKeywords = ref<any>({
    article: '',
    recipe: '',
    video: '',
    prevention: '',
    prescription: ''
  })
  
  onMounted(async () => {
    try {
      let articleList = await get('/article/list');
      let zhonogyaoshipu = await get('/zhonogyaoshipu/list');
      let commondiseases = await get('/commondiseases/list');
      let prescriptionofdrugs = await get('/prescriptionofdrugs/list');
      let video = await get('/video/list');
  
      if (articleList && articleList.rows) {
        allContents.value.article = articleList.rows.map((item: any) => ({
          ...item,
          title: item.articleTitle,
          description: item.articleContent,
          imgUrl: "/api" + item.imgurl
        }));
      }
      if (zhonogyaoshipu && zhonogyaoshipu.rows) {
        allContents.value.recipe = zhonogyaoshipu.rows.map((item: any) => ({
          ...item,
          title: item.name,
          description: item.introduce,
          imgUrl: item.imageurl
        }));
      }
      if (commondiseases && commondiseases.rows) {
        allContents.value.prevention = commondiseases.rows.map((item: any) => ({
          ...item,
          title: item.name,
          description: item.introduction,
          imgUrl: "/api/profile/upload/tcm/jibing.jpeg"
        }));
      }
      if (prescriptionofdrugs && prescriptionofdrugs.rows) {
        allContents.value.prescription = prescriptionofdrugs.rows.map((item: any) => ({
          ...item,
          title: item.name,
          description: item.indicationoftreatment,
          imgUrl: "/api" + item.imgurl
        }));
      }
      if (video && video.rows) {
        allContents.value.video = video.rows.map((item: any) => ({
          ...item,
          title: item.name,
          description: item.introduce,
          imgUrl: "/api" + item.coverImage
        }));
      }
    } catch (error) {
      console.error("Failed to fetch data", error);
    }
  });
  
  const getCurrentContent = (type: string) => {
    const keyword = searchKeywords.value[type];
    if (!keyword) {
      return allContents.value[type] || [];
    }
    return allContents.value[type].filter((item: any) => {
      return (
        item.title?.includes(keyword) || item.description?.includes(keyword)
      );
    });
  }
  
  function filterContent(type: string) {
    // 触发更新
    getCurrentContent(type);
  }
  
  function jump(item: any, type: string) {
    router.push('/home/detailPage?type=' + type + '&id=' + item.id);
  }

  const handleTabClick = (tab: any) => {
    console.log(tab.props.name);
  }
  </script>
  
  <style scoped>
  .knowledge-page {
    padding: 20px;
  }
  
  .func-content {
    margin-top: 10px;
  }
  
  .record-content, .therapy-content {
    margin-top: 20px;
    background: #f5f7fa;
    padding: 15px;
    border-radius: 8px;
  }
  
  .search-group {
    margin-bottom: 20px;
    display: flex;
    align-items: center;
  }
  
  .card-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 20px;
  }
  
  .content-card {
    cursor: pointer;
    transition: all 0.3s;
  }
  
  .content-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  }
  
  .module-image {
    width: 100%;
    height: 160px;
    object-fit: cover;
    margin-bottom: 10px;
    border-radius: 4px;
  }
  
  :deep(.el-tabs__item.is-active) {
    color: #D2691E !important;
  }
  
  :deep(.el-button--primary) {
    background-color: #D2691E;
    border-color: #D2691E;
  }
  </style>
