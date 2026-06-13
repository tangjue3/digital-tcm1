<template>
  <el-button type="primary" @click="$router.go(-1)">返回</el-button>
    <div class="detail-page-container">
      <el-card>
        <template #header>
          <h3>{{ currentContent.name }}</h3>
        </template>
        <div v-if="category === 'article'">
          
          <p v-html="currentContent.articleContent"></p>
        </div>
        <div v-if="category === 'recipe'">
          <p>食材：{{ currentContent.shicai }}</p>
          <p>做法：{{ currentContent.zuofa }}</p>
          <p>功效：{{ currentContent.gongxiao }}</p>
          <p>食用方法：{{ currentContent.shiyongfangfa }}</p>
          <p>禁忌：{{ currentContent.jinji }}</p>
          
        </div>
        <div v-if="category === 'video'">
          <video controls width="640" height="360" :src="'/api'+currentContent.path">
          
          </video>
        </div>
        <div v-if="category === 'prevention'">
          <p>症状：{{ currentContent.introduction }}</p>
          <p>防治方法：{{ currentContent.method }}</p>
          <p>预防措施：{{ currentContent.preventive }}</p>
          <p>常见发病季节：{{ currentContent.OnsetSeasons}}</p>
        </div>
        <div v-if="category === 'prescription'">
          <p>药材：{{ currentContent.composition }}</p>
          <p>功能：{{ currentContent.Func }}</p>
          <p>主治：{{ currentContent.indicationoftreatment }}</p>
        </div>
      </el-card>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue';
  import { useRoute } from 'vue-router';
  import { ElCard } from 'element-plus';
  import { get } from '@/utils/request';
  // 模拟各分类下的详细内容数据
  const allDetailContents = {
    article: [
      {
        id: 1,
        title: '中医理论新探',
        content: '深入探讨中医的经典理论和现代应用，阐述了中医在新时代的发展方向和研究成果。'
      },
      {
        id: 2,
        title: '中医文化传承',
        content: '介绍中医文化的历史渊源和传承意义，强调了中医文化在现代社会中的重要价值。'
      }
    ],
    recipe: [
      {
        id: 1,
        title: '红枣桂圆汤',
        ingredients: '红枣、桂圆、冰糖',
        method: '将红枣和桂圆洗净，加水炖煮 30 分钟，加入冰糖调味即可。'
      },
      {
        id: 2,
        title: '薏仁芡实粥',
        ingredients: '薏仁、芡实、大米',
        method: '薏仁、芡实提前浸泡 2 小时，与大米一起煮粥，煮至软糯即可。'
      }
    ],
    video: [
      {
        id: 1,
        title: '针灸入门教学',
        videoUrl: 'https://example.com/acupuncture_video.mp4'
      },
      {
        id: 2,
        title: '中药炮制工艺',
        videoUrl: 'https://example.com/herb_processing_video.mp4'
      }
    ],
    prevention: [
      {
        id: 1,
        title: '感冒防治要点',
        symptoms: '发热、咳嗽、流涕',
        methods: '注意保暖，多喝水，可服用银翘解毒片等药物。'
      },
      {
        id: 2,
        title: '失眠调理策略',
        symptoms: '难以入睡、多梦易醒',
        methods: '保持规律作息，睡前避免兴奋，可饮用酸枣仁茶等。'
      }
    ],
    prescription: [
      {
        id: 1,
        title: '银翘散',
        herbs: '金银花、连翘、桔梗等',
        usage: '每日一剂，分两次服用。'
      },
      {
        id: 2,
        title: '六味地黄丸',
        herbs: '熟地黄、山茱萸、山药等',
        usage: '每次 8 丸，一日 3 次。'
      }
    ]
  };
  
  const route = useRoute();
  const currentContent = ref({});
  const category = ref(route.query.type);
  
  onMounted(async () => {
    let type="";
    if(category.value=='prevention'){
      type="commondiseases"
    }
    if(category.value=='article'){
      type="article"
    }
    if(category.value=='recipe'){
      type="zhonogyaoshipu"
    }
    if(category.value=='prescription'){
      type="prescriptionofdrugs"
    }
    if(category.value=='video'){
      type="video"
    }
    const id = parseInt(route.query.id);
    let data= await get(type+"/"+id)
    currentContent.value = data.data;
    if(category.value=='article'){
      currentContent.value.name= data.data.articleTitle
    }
    console.log(data.data);
  });
  </script>
  
  <style scoped>
  .detail-page-container {
    padding: 20px;
  }
  </style>    
