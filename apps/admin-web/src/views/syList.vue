<template>
 <div class="homepage">
  <el-button type="primary" @click="$router.go(-1)"class="back-btn">返回</el-button>
    <div class="experiment-container">
      
      <div class="left-list">
        <el-card
          v-for="(experiment, index) in experiments"
          :key="index"
          class="experiment-card"
          @click="selectExperiment(experiment)"
          :style="{ border: selectedExperiment === experiment ? '2px solid #409EFF' : '1px solid #ebeef5' }"
        >
          <template #header>
            <h3>{{ experiment.caseName }}</h3>
          </template>
          <img :src="experiment.image" alt="实验图片" class="experiment-image" />
          <!-- <div>{{ experiment.caseRemark }}</div> -->
        </el-card>
      </div>
      <div class="right-content">
        <div class="centered-content" v-if="selectedExperiment">
          <h2>{{ selectedExperiment.caseName }} 病例单</h2>

          <div v-html="selectedExperiment.caseRemark"></div>
          <!-- <p>{{ selectedExperiment.caseRemark }}</p> -->
          <el-button type="primary" @click="enterExperiment(selectedExperiment)">进入实验</el-button>
        </div>
        <div class="centered-content" v-else>
          <p>请选择一个实验</p>
        </div>
      </div>
    </div>
    </div>
  </template>
  
  <script setup>
  import { ref,onMounted } from 'vue';
  import { ElCard, ElButton } from 'element-plus';
  import { useRouter } from 'vue-router';
import { get } from '@/utils/request';
  
let router=useRouter();
  // 模拟实验数据
  const experiments = ref([
    {
    //   id: 1,
    //   name: '金疮止血仿真实验',
    //   brief: '模拟金疮出血场景，学习止血方法',
    //   design: '设定不同程度的金疮出血场景，提供多种止血药材和工具，让学生尝试不同的止血方案。',
      image: 'https://baike.duguji.cn/baike/images/f/fc/%E7%A0%B4%E4%BC%A4%E9%A3%8E.jpg'
    // },
    // {
    //   id: 2,
    //   name: '针灸穴位定位仿真实验',
    //   brief: '练习针灸穴位的准确查找',
    //   design: '构建人体模型，随机指定穴位，让学生通过虚拟针灸工具进行定位操作。',
    //   image: 'https://dummyimage.com/300x200/000/fff&text=针灸穴位实验'
    // },
    // {
    //   id: 3,
    //   name: '中药炮制仿真实验',
    //   brief: '了解中药炮制的过程和方法',
    //   design: '提供多种中药材和炮制工具，学生根据配方进行炮制操作，观察药材变化。',
    //   image: 'https://dummyimage.com/300x200/000/fff&text=中药炮制实验'
    }
  ]);
  onMounted(async () => {

let articleList= await get("/case/list")
experiments.value=articleList.rows.map(item => ({
  caseRemark: item.caseRemark,
  caseName: item.caseName,        
  caseNo : item.caseNo,
  // picture: item.picture,
  image: item.picture,
  results: item.results,
  treatmentMethods: item.treatmentMethods,

}))


console.log(articleList);
console.log(experiments.value);
});



  const selectedExperiment = ref(null);
  
  const selectExperiment = (experiment) => {
    selectedExperiment.value = experiment;
  };
  
  const enterExperiment = (experiment) => {
    console.log(experiment);
    console.log(`进入 ${experiment.name} 实验`);
    router.push("/fzsy?id="+experiment.caseNo)
    // 这里可以添加实际进入实验的逻辑，如跳转到实验页面
  };
  </script>
  
  <style scoped>
  .homepage {
  background: rgb(255, 255, 255) url('@/assets/背景图.png') no-repeat center top;
  background-size: cover; /* 或 contain / 100% 100% 等 */
  background-attachment: fixed; /* 固定背景不随滚动 */
}

  .experiment-container {
    display: flex;
    height: 100vh;
    
  }
  
  .left-list {
    width: 30%;
    padding: 20px;
    overflow-y: auto;
    border-right: 1px solid #ebeef5;
    
  }
  
  .experiment-card {
    margin-bottom: 20px;
    cursor: pointer;
    background: rgba(255, 255, 255, 0);
  }
  
  .experiment-image {
    width: 100%;
    height: auto;
    margin-bottom: 10px;
    
  }
  
  .right-content {
    width: 70%;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  
  .centered-content {

    padding: 20px;
    font-size: 30px;
    text-align: center;
  }
.back-btn {
  background: linear-gradient(135deg, #2c5530 0%, #4a7c59 100%);
  border: none;
  border-radius: 15px;
  padding: 12px 20px;
  font-weight: 600;
}

  </style>    