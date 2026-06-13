<template>
  <div class="experiment-container">
    <!-- 顶部导航
    <div class="top-nav">
      
      
      
    </div>
     -->
    <!-- 装饰元素 -->
    <div class="decorative-elements">
      <div class="cloud cloud-1"></div>
      <div class="cloud cloud-2"></div>
    </div>
    
    <!-- 步骤条 -->
    <div class="steps-container">
      <el-button type="primary" @click="$router.go(-1)" class="back-btn">
        <i class="el-icon-arrow-left"></i> 返回
      </el-button>
      <div class="progress-info">
        <span>步骤 {{ activeStep + 1 }}/4</span>
      </div>
      <el-steps :active="activeStep" finish-status="success" class="tcm-steps">
        <el-step title="望" class="step-item" @click="changeStep(0)">
          <template #icon>
            <div class="step-icon">👁</div>
          </template>
        </el-step>
        <el-step title="闻" class="step-item" @click="changeStep(1)">
          <template #icon>
            <div class="step-icon">👂</div>
          </template>
        </el-step>
        <el-step title="问" class="step-item" @click="changeStep(2)">
          <template #icon>
            <div class="step-icon">💬</div>
          </template>
        </el-step>
        <el-step title="切" class="step-item" @click="changeStep(3)">
          <template #icon>
            <div class="step-icon">✋</div>
          </template>
        </el-step>
        
      </el-steps>
    </div>
    
    <!-- 主要内容区域 -->
    <div class="main-content">
      <div class="content-wrapper">
        <!-- 步骤内容 -->
        <div v-if="activeStep === 0" class="step-content">
          <h3 class="step-title">望诊 - 观察患者外在表现</h3>
          <div v-for="(question, index) in inspectionQuestions.filter(x=>{return x.module=='01'})" :key="index" class="question-card">
            <div class="question-image-section">
              <el-button v-if="question.urlType=='01' || question.urlType=='02' ||question.urlType=='03'" 
                class="ai-analysis-btn" @click="callAIAnalysis(question.imageUrl,question.urlType)">
                🤖 AI智能分析
              </el-button>
              <img v-if="question.imageUrl" :src="question.imageUrl" alt="问题图片" class="question-image" />
            </div>
            <div class="question-content">
              <p class="question-text">
                <span class="question-number">{{ index + 1 }}.</span> 
                {{ question.questionsName }}
              </p>
              <el-radio-group v-model="question.answer" class="radio-group">
                <el-radio :label="'A'" class="radio-option">{{ question.optionA }}</el-radio>
                <el-radio :label="'B'" class="radio-option">{{ question.optionB }}</el-radio>
                <el-radio :label="'C'" class="radio-option">{{ question.optionC }}</el-radio>
                <el-radio :label="'D'" class="radio-option">{{ question.optionD }}</el-radio>
              </el-radio-group>
            </div>
          </div>
        </div>
        
        <!-- 其他步骤内容类似结构... -->
        <!-- 闻诊内容 -->
        <div v-if="activeStep === 1" class="step-content">
          <h3 class="step-title">闻诊 - 听声音察气味</h3>
          <div v-for="(question, index) in inspectionQuestions.filter(x=>{return x.module=='02'})" :key="index" class="question-card">
            <div class="question-image-section">
              <el-button v-if="question.urlType=='01' || question.urlType=='02' ||question.urlType=='03'" 
                class="ai-analysis-btn" @click="callAIAnalysis(question.imageUrl,question.urlType)">
                🤖 AI智能分析
              </el-button>
              <img v-if="question.imageUrl" :src="question.imageUrl" alt="问题图片" class="question-image" />
            </div>
            <div class="question-content">
              <p class="question-text">
                <span class="question-number">{{ index + 1 }}.</span> 
                {{ question.questionsName }}
              </p>
              <el-radio-group v-model="question.answer" class="radio-group">
                <el-radio :label="'A'" class="radio-option">{{ question.optionA }}</el-radio>
                <el-radio :label="'B'" class="radio-option">{{ question.optionB }}</el-radio>
                <el-radio :label="'C'" class="radio-option">{{ question.optionC }}</el-radio>
                <el-radio :label="'D'" class="radio-option">{{ question.optionD }}</el-radio>
              </el-radio-group>
            </div>
          </div>
        </div>
        
        <!-- 问诊内容 - 恢复原来的Chat组件 -->
        <div v-if="activeStep === 2" class="step-content">
          <h3 class="step-title">问诊 - 询问病史症状</h3>
          <div class="chat-section">
            <Chat/>
          </div>
        </div>
        
        <!-- 切诊内容 -->
        <div v-if="activeStep === 3" class="step-content">
          <h3 class="step-title">切诊 - 触摸脉象诊断</h3>
          <div v-for="(question, index) in inspectionQuestions.filter(x=>{return x.module=='04'})" :key="index" class="question-card">
            <div class="question-image-section">
              <el-button v-if="question.urlType=='01' || question.urlType=='02' ||question.urlType=='03'" 
                class="ai-analysis-btn" @click="callAIAnalysis(question.imageUrl,question.urlType)">
                🤖 AI智能分析
              </el-button>
              <img v-if="question.imageUrl" :src="question.imageUrl" alt="问题图片" class="question-image" />
            </div>
            <div class="question-content">
              <p class="question-text">
                <span class="question-number">{{ index + 1 }}.</span> 
                {{ question.questionsName }}
              </p>
              <el-radio-group v-model="question.answer" class="radio-group">
                <el-radio :label="'A'" class="radio-option">{{ question.optionA }}</el-radio>
                <el-radio :label="'B'" class="radio-option">{{ question.optionB }}</el-radio>
                <el-radio :label="'C'" class="radio-option">{{ question.optionC }}</el-radio>
                <el-radio :label="'D'" class="radio-option">{{ question.optionD }}</el-radio>
              </el-radio-group>
            </div>
          </div>
        </div>
        
        <!-- 操作按钮 -->
        <div class="action-buttons">
          <el-button @click="saveRecord" type="primary" class="save-btn">
            💾 保存记录
          </el-button>
          <el-button v-if="activeStep === 3" @click="dialogFormVisible = true" type="success" class="submit-btn">
            ✅ 提交诊断
          </el-button>
        </div>
      </div>
    </div>
    
    <!-- 其他对话框和组件... -->
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { ElSteps, ElStep, ElRadioGroup, ElRadio, ElInput, ElButton,ElMessage  } from 'element-plus';
import { useRoute, useRouter } from 'vue-router';
import Chat from '@/components/chat.vue';
import { get,post } from '@/utils/request';
const dialogFormVisible = ref(false)
const dialogTableVisible = ref(false)
let gridData=ref([])
const formLabelWidth = '140px'
const form = ref({
  conclusion: '',//诊断结果
  treatmentMethods: '',// 治疗手段
})
let router = useRouter();
let route = useRoute()
// 步骤状态
const activeStep = ref(0);
// 记录内容
const record = ref('');
// 学生信息，包含实验记录
const studentInfo = ref({
  exerciseRecords: []
});

// 模拟图片数据
const imageData = {
  0: {
    望诊: 'https://img.shetu66.com/2023/04/17/1681708325771014.jpg',
  },
  1: {
    闻诊: 'https://img.shetu66.com/2023/04/17/1681708325771014.jpg',
  },
  2: {
    问诊: 'https://img.shetu66.com/2023/04/17/1681708325771014.jpg'
  },
  3: {
    切诊: 'https://img.shetu66.com/2023/04/17/1681708325771014.jpg',
  }
};
const QuestionNo = ref('');//实验标识符
function readQuestionNo(response) {
  const data = response?.data;
  if (typeof data === 'string') {
    return data.trim();
  }
  if (data && typeof data === 'object' && typeof data.questionNo === 'string') {
    return data.questionNo.trim();
  }
  const msg = typeof response?.msg === 'string' ? response.msg.trim() : '';
  return msg.includes('-') ? msg : '';
}
// 当前显示的图片
const currentImage = ref('');
// 图片加载失败标志位
const imageLoadFailed = ref({});

// 问题数据
const inspectionQuestions = ref([]);
// 切诊问题数据
// const pulseDiagnosisQuestions = ref([]);

// 自定义图片 URL
const customImageUrl = ref('');

// 切换步骤
const changeStep = (step) => {
  activeStep.value = step;
  record.value = '';
  // 根据当前步骤从 imageData 中获取对应的图片链接
  const stepData = imageData[step];
  currentImage.value = Object.values(stepData)[0];
  imageLoadFailed.value[currentImage.value] = false; // 重置加载失败标志
  console.log('当前步骤:', step, '当前图片:', currentImage.value);
};

// 保存记录
const saveRecord = () => {
 let arr=["01","02","03","04"];
 if(activeStep.value==2){
  activeStep.value=activeStep.value+1
 }else{
  let arr1=inspectionQuestions.value.filter(x=>{
    return x.module==arr[activeStep.value]
  })
  let isok=true;
  arr1.map(item=>{
    if(item.answer==""){
      isok=false
    }
  })
  if(!isok){
    alert("有未填写完整项，请检查")
  }else{
    alert("保存成功")
    if(activeStep.value==3){

    }else{
      activeStep.value=activeStep.value+1
    }
   
  }
 }


  
};

// 'searchValue": null,'createBy": null,"createTime":null,"updateBy": null,"updateTime": null,"remark": null.params":fh
const up = async() => {
  if(form.value.conclusion=="" || form.value.treatmentMethods==""){
    ElMessage.error("请填写完整信息")
    return
  }else if(!QuestionNo.value){
    ElMessage.error("questionNo is empty, please reload this case and try again")
    return
  }else if(!inspectionQuestions.value.length){
    ElMessage.error("question list is empty, please reload this case and try again")
    return
  }else{
  // console.log(inspectionQuestions.value);
  inspectionQuestions.value.map(item=>{
    delete item.id;
    delete item.searchValue;
    delete item.createBy;
    delete item.createTime;
    delete item.updateTime;
    delete item.remark;
    delete item.params;
    delete item.updateBy;

  })
  inspectionQuestions.value.push({
    module:"03",
    caseNo:inspectionQuestions.value[0].caseNo,
    questionNo:QuestionNo.value,
    questionsName:"你是怎么受伤的？",
    optionA:"医生，您好！我是在上个星期天的时候不小心受伤的。当时我在花园里修剪树枝，一不小心被树枝上的刺扎到了手指。当时只是觉得有点疼，没太在意，就用纸巾擦了擦伤口，没做其他处理。later过了几天，伤口周围开始红肿、疼痛，还出现了一些脓水。"
  })
  inspectionQuestions.value.push({
    module:"03",
    caseNo:inspectionQuestions.value[0].caseNo,
    questionNo:QuestionNo.value,
    questionsName:"你受伤后有没有及时处理伤口？",
    optionA:"我在受伤后没有及时处理伤口。当时在花园修剪树枝时被刺扎伤手指后，我只用纸巾擦了擦伤口，没有进行进一步的处理，如清洗、消毒或包扎。later伤口出现红肿、疼痛和脓水时，我才意识到可能感染了，这时已经过去了几天."
  })
  inspectionQuestions.value.push({
    module:"03",
    caseNo:inspectionQuestions.value[0].caseNo,
    questionNo:QuestionNo.value,
    questionsName:"你现在感觉如何",
    optionA:"食欲下降，睡眠质量不佳，有时还会感到恶心。我非常担心病情的进一步发展，希望医生能尽快给予治疗，控制感染并缓解疼痛"
  })
  try {
    inspectionQuestions.value.forEach(item => {
      item.questionNo = QuestionNo.value
    })
    let  Questionres= await post("/detail/addList",inspectionQuestions.value)
    if (Questionres?.code !== 200) {
      throw new Error(Questionres?.msg || 'detail/addList failed')
    }
    const scoreResult = await post("/records/tcmCaseRecordsResult",{
      conclusion:form.value.conclusion,
      treatmentMethods:form.value.treatmentMethods,
      questionNo:QuestionNo.value,
    })
    if (scoreResult?.code !== 200) {
      throw new Error(scoreResult?.msg || 'tcmCaseRecordsResult failed')
    }
    router.push("/result?questionNo="+encodeURIComponent(QuestionNo.value));
  } catch (error) {
    console.error('[virtual-diagnosis] submit failed', error)
    ElMessage.error(error?.message || 'submit failed')
  }
}
};

// 处理图片加载错误
const handleImageError = (event) => {
  const imgUrl = event.target.src;
  if (imageLoadFailed.value[imgUrl]) return; // 避免重复处理
  imageLoadFailed.value[imgUrl] = true;
  console.error('图片加载失败:', imgUrl);
  event.target.src = 'https://ts1.tc.mm.bing.net/th/id/R-C.7107374caf7e055aa0144298a3f9fde4?rik=6AINojN9keya6w&riu=http%3a%2f%2fn.sinaimg.cn%2fsinacn10%2f227%2fw492h535%2f20180525%2f87fc-haysviy5219720.jpg&ehk=DhNLO6yIO01BmsGcjykBYFCfFcY5nnJKHrgZpnYAa5U%3d&risl=&pid=ImgRaw&r=0'; // 替换为默认图片链接
};

// 封装获取数据的函数
const fetchData = async (id) => {
  const firstPageRes = await get("/question/list?pageNum=1&pageSize=100&caseNo="+encodeURIComponent(id));
  console.log(firstPageRes.rows);
 const rows = Array.isArray(firstPageRes.rows) ? firstPageRes.rows : [];
 rows.map(item => {
  item.answer="";
  item.questionNo=QuestionNo.value
 })
 inspectionQuestions.value=rows;
};

// 调用AI分析接口
const callAIAnalysis = async (imageUrl,type) => {
  console.log('AI分析接口调用:', imageUrl,type);
  let res=await get('/ai/faceAiPlus', { imageUrl, type })
  console.log('AI分析接口调用成功:', res);
  gridData.value=res.data.features
  
  dialogTableVisible.value=true;
  // try {
  //   // 这里需要替换为实际的AI分析接口地址和参数，可将图片URL作为参数传递
  //   const response = await get(`/your-ai-analysis-api-url?imageUrl=${imageUrl}`);
  //   console.log('AI分析接口调用成功:', response);
  // } catch (error) {
  //   console.error('AI分析接口调用失败:', error);
  // }
};


// 设置自定义图片
const setCustomImage = () => {
  if (customImageUrl.value) {
    // 这里可以添加更多的图片 URL 合法性检查
    imageLoadFailed.value[customImageUrl.value] = false;
  }
};

// 在组件挂载时调用获取数据的函数，并初始化图片和悬浮组件位置
onMounted(async () => {
  try {
    if (!route.query.id) {
      throw new Error('Missing case id')
    }
    let  Questionres= await post("/records/addPlus",{
      userId:'张三',
      caseNo:route.query.id
    })
    const questionNo = readQuestionNo(Questionres)
    if (!questionNo) {
      throw new Error('records/addPlus did not return a valid questionNo')
    }
    QuestionNo.value=questionNo
    console.info('[virtual-diagnosis] questionNo', QuestionNo.value)
    await fetchData(route.query.id);
  } catch (error) {
    console.error('[virtual-diagnosis] init failed', error)
    ElMessage.error(error?.message || 'init failed')
  }

  const initialStepData = imageData[activeStep.value];
  currentImage.value = Object.values(initialStepData)[0];

});

</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Serif+SC:wght@400;500;600;700&display=swap');

.experiment-container {
  min-height: 100vh;
  /* 移除原有的渐变背景，保持透明 */
  background: transparent;
  background-image: url('@/assets/背景图.png');
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  font-family: 'Noto Serif SC', serif;
}

.back-btn {
  background: linear-gradient(135deg, #2c5530 0%, #4a7c59 100%);
  border: none;
  border-radius: 15px;
  padding: 12px 20px;
  font-weight: 600;
}

.nav-title h2 {
  color: #2c5530;
  margin: 0;
  font-size: 1.6rem;
  font-weight: 700;
  letter-spacing: 3px;
}

.nav-title p {
  color: #5a6c57;
  margin: 5px 0 0 0;
  font-size: 0.9rem;
  letter-spacing: 1px;
}

.progress-info {
  background: rgba(44, 85, 48, 0.1);
  padding: 8px 16px;
  border-radius: 20px;
  color: #2c5530;
  font-weight: 600;
}

/* 装饰元素 */
.decorative-elements {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  overflow: hidden;
}

.cloud {
  position: absolute;
  background: rgba(255, 255, 255, 0.05); /* 降低云朵透明度 */
  border-radius: 50px;
  opacity: 0.3; /* 进一步降低透明度 */
  animation: float 6s ease-in-out infinite;
}

.cloud-1 {
  width: 100px;
  height: 40px;
  top: 20%;
  left: 10%;
}

.cloud-2 {
  width: 80px;
  height: 30px;
  top: 15%;
  right: 15%;
  animation-delay: 3s;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-20px); }
}

/* 步骤条 */
.steps-container {
  padding: 30px 40px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 20px;
  width: 100%;
}

.back-btn {
  background: linear-gradient(135deg, #2c5530 0%, #4a7c59 100%);
  border: none;
  border-radius: 15px;
  padding: 12px 20px;
  font-weight: 600;
  position: absolute;
  left: 40px;
  z-index: 100;
}

.tcm-steps {
  flex: 1;
  max-width: 600px;
  margin-left: 0px; /* 为返回按钮留出空间 */
  display: flex;
  justify-content: center;
}

.progress-info {
  background: rgba(44, 85, 48, 0.1);
  padding: 8px 16px;
  border-radius: 20px;
  color: #2c5530;
  font-weight: 600;
  position: absolute;
  right: 40px;
  z-index: 100;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .steps-container {
    flex-direction: column;
    gap: 15px;
    padding: 20px;
    position: relative;
  }
  
  .back-btn {
    position: static;
    align-self: flex-start;
    margin-bottom: 10px;
  }
  
  .tcm-steps {
    margin-left: 0;
    width: 100%;
  }
  
  .progress-info {
    position: static;
    align-self: flex-end;
  }
}

.nav-title h2 {
  color: #2c5530;
  margin: 0;
  font-size: 1.6rem;
  font-weight: 700;
  letter-spacing: 3px;
}

.nav-title p {
  color: #5a6c57;
  margin: 5px 0 0 0;
  font-size: 0.9rem;
  letter-spacing: 1px;
}

.progress-info {
  background: rgba(44, 85, 48, 0.1);
  padding: 8px 16px;
  border-radius: 20px;
  color: #2c5530;
  font-weight: 600;
}

/* 装饰元素 */
.decorative-elements {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  overflow: hidden;
}

.cloud {
  position: absolute;
  background: rgba(255, 255, 255, 0.05); /* 降低云朵透明度 */
  border-radius: 50px;
  opacity: 0.3; /* 进一步降低透明度 */
  animation: float 6s ease-in-out infinite;
}

.cloud-1 {
  width: 100px;
  height: 40px;
  top: 20%;
  left: 10%;
}

.cloud-2 {
  width: 80px;
  height: 30px;
  top: 15%;
  right: 15%;
  animation-delay: 3s;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-20px); }
}

/* 步骤条 */
.steps-container {
  padding: 30px 0;
  display: flex;
  justify-content: center;
}

.tcm-steps {
  width: 60%;
  max-width: 600px;
  justify-content: center;

}

.tcm-steps :deep(.el-step__title) {
  font-family: 'Noto Serif SC', serif;
  font-size: 1.2rem;
  font-weight: 600;
  color: #2c5530;
  letter-spacing: 2px;
}

.tcm-steps :deep(.el-step__line) {
  background: linear-gradient(90deg, #2c5530 0%, #4a7c59 100%);
  height: 3px;
}

.step-icon {
  font-size: 1.5rem;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #2c5530 0%, #4a7c59 100%);
  border-radius: 50%;
  color: white;
}

/* 主要内容 */
.main-content {
  padding: 0 40px 40px;
  position: relative;
  z-index: 10;
}

.content-wrapper {
  background: rgba(255, 255, 255, 0.85); /* 降低背景透明度 */
  backdrop-filter: blur(10px);
  border-radius: 25px;
  padding: 40px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.step-title {
  text-align: center;
  color: #2c5530;
  font-size: 1.8rem;
  font-weight: 700;
  margin-bottom: 30px;
  letter-spacing: 3px;
  position: relative;
}

.step-title::after {
  content: '';
  position: absolute;
  bottom: -10px;
  left: 50%;
  transform: translateX(-50%);
  width: 80px;
  height: 3px;
  background: linear-gradient(90deg, #2c5530 0%, #4a7c59 100%);
  border-radius: 2px;
}

/* 问题卡片 */
.question-card {
  display: flex;
  margin-bottom: 30px;
  background: rgba(240, 249, 235, 0.3); /* 降低背景透明度 */
  border-radius: 20px;
  padding: 25px;
  border: 2px solid rgba(44, 85, 48, 0.1);
  transition: all 0.3s ease;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.05);
}

.question-card:hover {
  border-color: rgba(44, 85, 48, 0.3);
  box-shadow: 0 12px 35px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
  background: rgba(240, 249, 235, 0.5); /* 悬停时稍微增加透明度 */
}

.question-image-section {
  position: relative;
  width: 15%;
  margin-right: 25px;
}

.question-image {
  width: 100%;
  height: auto;
  border-radius: 15px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.ai-analysis-btn {
  position: absolute;
  top: 10px;
  left: 10px;
  z-index: 10;
  background: linear-gradient(135deg, #ff6b6b 0%, #ff8e8e 100%);
  border: none;
  border-radius: 20px;
  padding: 8px 15px;
  font-size: 12px;
  font-weight: 600;
  color: white;
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
}

.question-content {
  width: 70%;
}

.question-text {
  font-size: 1.1rem;
  color: #2c5530;
  margin-bottom: 20px;
  line-height: 1.6;
  font-weight: 500;
}

.question-number {
  font-weight: 700;
  color: #2c5530;
  margin-right: 8px;
  font-size: 1.2rem;
}

/* 单选按钮组 */
.radio-group {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.radio-option {
  background: rgba(255, 255, 255, 0.6); /* 降低背景透明度 */
  border: 2px solid rgba(44, 85, 48, 0.2);
  border-radius: 12px;
  padding: 15px;
  transition: all 0.3s ease;
  font-weight: 500;
  color: #2c5530;
}

.radio-option:hover {
  border-color: #2c5530;
  background: rgba(240, 249, 235, 0.6); /* 悬停时的透明背景 */
}

.radio-option :deep(.el-radio__input.is-checked .el-radio__inner) {
  background: #2c5530;
  border-color: #2c5530;
}

/* 操作按钮 */
.action-buttons {
  text-align: center;
  margin-top: 40px;
  display: flex;
  gap: 20px;
  justify-content: center;
}

.save-btn,
.submit-btn {
  padding: 15px 30px;
  border-radius: 25px;
  font-size: 1.1rem;
  font-weight: 600;
  letter-spacing: 1px;
  border: none;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.save-btn {
  background: linear-gradient(135deg, #2c5530 0%, #4a7c59 100%);
}

.submit-btn {
  background: linear-gradient(135deg, #28a745 0%, #34ce57 100%);
}

.save-btn:hover,
.submit-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.15);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .question-card {
    flex-direction: column;
  }
  
  .question-image-section,
  .question-content {
    width: 100%;
    margin-right: 0;
  }
  
  .question-image-section {
    margin-bottom: 20px;
  }
}

@media (max-width: 768px) {
  .top-nav {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }
  
  .main-content {
    padding: 0 20px 40px;
  }
  
  .content-wrapper {
    padding: 25px;
  }
  
  .radio-group {
    grid-template-columns: 1fr;
  }
  
  .action-buttons {
    flex-direction: column;
    align-items: center;
  }
}
</style>

/* Chat组件样式 */
.chat-section {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 15px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(139, 69, 19, 0.1);
  border: 1px solid rgba(139, 69, 19, 0.2);
  min-height: 400px;
}
