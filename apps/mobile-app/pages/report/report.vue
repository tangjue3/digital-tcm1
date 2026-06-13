<template>
    <view class="health-report-container">
        <view class="report-header">
            <text class="report-title">中医健康报告</text>
        </view>
        <view class="report-section">
            <text class="section-title">用户基本信息</text>
            <view class="info-item">
                <text>性别：{{ userInfo.gender }}</text>
            </view>
            <view class="info-item">
                <text>年龄：{{ userInfo.age }} 岁</text>
            </view>
            <view class="info-item">
                <text>体重：{{ userInfo.weight }} kg</text>
            </view>
            <view class="info-item">
                <text>身高：{{ userInfo.height }} cm</text>
            </view>
            <view class="info-item">
                <text>脉搏：{{ userInfo.pulseRate || '-' }} 次/分钟</text>
            </view>
            <view class="info-item">
                <text>过往病史：{{ userInfo.medicalHistory.join('、') }}</text>
            </view>
        </view>

		<!-- 加载中状态 -->
		<view class="loading-container" v-if="loading">
			<view class="loading-text">智能分析中...</view>
		</view>

		<!-- 数据展示 -->
		<template v-else-if="resdata">
			<view class="report-section">
			    <text class="section-title">诊断结果与体质分析</text>
			    <view class="analysis-content">
			        <text>{{ resdata.physique_analysis }}</text>
			    </view>
			</view>
			<view class="report-section" v-if="resdata.advices && resdata.advices.food">
			    <text class="section-title">饮食建议</text>
			    <view class="advice-content">
			        <view v-for="(item, index) in resdata.advices.food" :key="index">{{ item.advice }}</view>
			    </view>
			</view>
			<view class="report-section" v-if="resdata.advices && resdata.advices.treatment">
			    <text class="section-title">保健建议</text>
			    <view class="advice-content">
			        <text>{{ resdata.advices.treatment[0]?.advice || '暂无建议' }}</text>
			    </view>
			</view>
			<view class="report-section" v-if="resdata.advices && resdata.advices.sport">
			    <text class="section-title">运动建议</text>
			    <view class="advice-content">
			        <text>{{ resdata.advices.sport[0]?.advice || '暂无建议' }}</text>
			    </view>
			</view>
			<view class="report-section" v-if="resdata.advices && resdata.advices.sleep">
			    <text class="section-title">作息建议</text>
			    <view class="advice-content">
			        <text>{{ resdata.advices.sleep[0]?.advice || '暂无建议' }}</text>
			    </view>
			</view>
		</template>

		<!-- 无数据状态 -->
		<view class="report-section" v-else>
			<text class="section-title">暂无分析结果</text>
			<view class="advice-content">
				<text>请先完成信息采集</text>
			</view>
		</view>

		<view style="height: 40px;"></view>
		<view class="action-button-container">
		    <button class="action-button" @click="createHealthPlan">创建个人健康计划</button>
		</view>
    </view>
</template>

<script>
// Mock 数据
const mockFaceAiData = {
	physique_name: "气虚质",
	physique_analysis: "气虚质是由于元气不足，以气息低弱、机体、脏腑功能状态低下为主要特征的一种体质状态。表现为面色偏黄或苍白，气短懒言，容易疲乏，易出汗，舌淡红，舌边有齿痕，脉弱。",
	risk_warning: "气虚质人群容易感冒，病后康复缓慢，易患内脏下垂等疾病。建议注意保暖，避免过度劳累。",
	typical_symptom: "气短乏力；容易疲乏；声音低弱；易出汗；面色偏黄",
	advices: {
		food: [
			{
				title: "宜食补气食物",
				advice: "宜食用具有补气作用的食物，如山药、莲子、黄芪、党参、白扁豆、鸡肉、牛肉等。"
			},
			{
				title: "忌食生冷",
				advice: "忌食生冷寒凉食物，如冷饮、生冷瓜果等，以免损伤脾胃之气。"
			}
		],
		treatment: [
			{
				title: "穴位按摩",
				advice: "可按摩足三里、气海、关元等穴位，每次10-15分钟，每日1-2次，有助于补益元气。"
			}
		],
		sport: [
			{
				title: "适度运动",
				advice: "适合进行柔和的运动，如太极拳、八段锦、散步等，避免剧烈运动导致大汗淋漓。"
			}
		],
		sleep: [
			{
				title: "规律作息",
				advice: "保持规律作息，避免熬夜，保证充足睡眠，有助于恢复元气。"
			}
		]
	}
};

export default {
    data() {
        return {
            userInfo: {
                gender: '男',
                age: '55',
                weight: '68',
                height: '180',
                medicalHistory: ['高血压','糖尿病']
            },
            diagnosisResult: '',
            dietAdvice: '',
            medicineAdvice: '',
            exerciseAdvice: '',
            scheduleAdvice: '',
			resdata: null,
			loading: false
        };
    },
    onLoad(options) {
		// 获取用户信息
		try {
			const userGuide = uni.getStorageSync("userGuide");
			if (userGuide) {
				this.userInfo = JSON.parse(userGuide);
			}
		} catch (e) {
			console.error('解析用户信息失败', e);
		}
		this.getdata();
    },
    methods: {
		getdata() {
			let _this = this;
			_this.loading = true;
			uni.showLoading({
				title: "智能分析中..."
			});

			// 尝试调用真实接口
			uni.uploadFile({
				url: 'http://your-server-host:8089/faceAi',
				filePath: _this.userInfo.tonguePhoto || '',
				name: 'file',
				formData: {
					'type': 2
				},
				success: (uploadFileRes) => {
					console.log('接口调用成功', uploadFileRes);
					try {
						const data = JSON.parse(uploadFileRes.data);
						if (data.data) {
							_this.resdata = data.data;
							uni.setStorageSync('faceData', JSON.stringify(data.data));
						} else {
							// 使用 mock 数据
							_this.useMockData();
						}
					} catch (e) {
						console.error('解析响应数据失败', e);
						_this.useMockData();
					}
				},
				fail: (err) => {
					console.log('接口调用失败，使用 mock 数据', err);
					_this.useMockData();
				},
				complete: () => {
					_this.loading = false;
					uni.hideLoading();
				}
			});
		},

		// 使用 mock 数据
		useMockData() {
			console.log('使用 Mock 数据');
			this.resdata = mockFaceAiData;
			uni.setStorageSync('faceData', JSON.stringify(mockFaceAiData));
			uni.showToast({
				title: '使用演示数据',
				icon: 'none'
			});
		},

		createHealthPlan() {
			uni.switchTab({
				url: "/pages/health-plan/health-plan"
			});
		},

        generateReport() {
            // 简单的中医诊断逻辑示例，实际应用中需要专业的中医知识和算法
            let diagnosis = '';
            let diet = '';
            let medicine = '';
            let exercise = '';
            let schedule = '';

            if (this.userInfo.medicalHistory.includes('高血压')) {
                diagnosis = '从中医角度来看，您可能存在肝阳上亢的体质倾向。肝阳上亢多因肝肾阴虚，水不涵木，肝阳亢逆无所制，气火上扰。常见症状可能有头晕、头痛、面红目赤等。';
                diet = '饮食上宜清淡，多吃芹菜、菠菜、苦瓜等具有清热平肝作用的蔬菜；避免食用辛辣、油腻、刺激性食物，减少盐的摄入。';
                medicine = '可在医生指导下，适当服用一些具有平肝潜阳作用的中药，如天麻钩藤饮等。但请务必遵循医嘱，不可自行用药。';
                exercise = '适合进行一些较为温和的运动，如太极拳、八段锦等，有助于调节身心，平衡阴阳。运动强度以微微出汗为宜，避免过度劳累。';
                schedule = '保持规律的作息，早睡早起，避免熬夜。晚上11点前入睡，保证充足的睡眠时间，有利于肝脏的休息和调养。';
            } else if (this.userInfo.medicalHistory.includes('糖尿病')) {
                diagnosis = '您的体质可能偏向阴虚燥热。阴虚则热，燥热内生，导致体内津液亏损。常见表现可能有多饮、多食、多尿、消瘦等。';
                diet = '饮食应控制糖分摄入，多吃粗粮、蔬菜和低糖水果，如燕麦、荞麦、黄瓜、西红柿等。避免食用高糖、高脂肪食物。';
                medicine = '可在医生的指导下，选用一些滋阴清热的中药，如玉泉丸等。但中药的使用需根据个体情况进行调整，务必咨询专业医生。';
                exercise = '适合进行有氧运动，如散步、慢跑、游泳等，有助于增强体质，提高胰岛素敏感性。运动时间可选择在饭后1小时左右，每次运动30分钟以上。';
                schedule = '作息要规律，避免熬夜和过度劳累。保证充足的睡眠，有利于身体的恢复和调节。';
            } else if (this.userInfo.medicalHistory.includes('心脏病')) {
                diagnosis = '中医认为您可能存在心气不足或心血瘀阻的情况。心气不足则推动血液运行无力，心血瘀阻则心脉不畅。常见症状可能有心慌、心悸、胸闷等。';
                diet = '饮食上可多吃一些具有养心安神作用的食物，如桂圆、红枣、莲子等。减少油腻、高盐食物的摄入，避免暴饮暴食。';
                medicine = '可在医生指导下，服用一些具有活血化瘀、养心安神作用的中药，如丹参滴丸等。但用药需谨慎，遵循医生的建议。';
                exercise = '适合进行一些舒缓的运动，如瑜伽、散步等，有助于放松身心，减轻心脏负担。运动时要注意适度，避免剧烈运动。';
                schedule = '保证充足的休息，避免过度劳累和情绪激动。晚上尽量在10点半前入睡，让心脏得到充分的休息。';
            } else {
                diagnosis = '根据您提供的信息，目前未发现明显的疾病倾向，体质较为平和。但仍需保持健康的生活方式，以维持身体的平衡。';
                diet = '饮食应均衡，荤素搭配，多吃蔬菜水果，保证摄入足够的营养。避免过度食用生冷、辛辣、油腻食物。';
                medicine = '若无特殊不适，一般无需用药。但可根据季节和个人体质，适当食用一些养生药膳，如春季可饮用菊花茶清肝明目。';
                exercise = '坚持适量的运动，如跑步、打球等，每周至少进行3 - 5次，每次运动30分钟以上，有助于增强体质。';
                schedule = '保持规律的作息时间，早睡早起，避免熬夜。晚上11点前入睡，早上7点左右起床，让身体的生物钟保持正常。';
            }

            this.diagnosisResult = diagnosis;
            this.dietAdvice = diet;
            this.medicineAdvice = medicine;
            this.exerciseAdvice = exercise;
            this.scheduleAdvice = schedule;
        }
    }
};
</script>

<style>
.health-report-container {
    padding: 20px;
    background-color: #f5f9ff;
    min-height: 100vh;
}

.report-header {
    text-align: center;
    margin-bottom: 20px;
}

.report-title {
    font-size: 24px;
    font-weight: bold;
    color: #007aff;
}

.report-section {
    background-color: #ffffff;
    border-radius: 10px;
    padding: 20px;
    margin-bottom: 20px;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.section-title {
    font-size: 18px;
    font-weight: bold;
    color: #007aff;
    margin-bottom: 15px;
}

.info-item {
    margin-bottom: 10px;
}

.analysis-content,
.advice-content {
    line-height: 1.5;
}

.loading-container {
	text-align: center;
	padding: 40px 0;
}

.loading-text {
	color: #007aff;
	font-size: 16px;
}

.action-button-container {
     position: fixed;
	 width: 80%;
       bottom: 20px;
       left: 10%;
       right: 0;
       text-align: center;
       z-index: 100;
}

.action-button {
    padding: 8px 16px;
    background: linear-gradient(to right, #007aff, #00bfff);
    color: white;
    border: none;
    border-radius: 5px;
    font-size: 14px;
    cursor: pointer;
    transition: background 0.3s ease;
	animation: breathe 1s infinite alternate;
}

.action-button:hover {
    background: linear-gradient(to right, #0056b3, #0086b3);
}

@keyframes breathe {
    from {
        transform: scale(1);
    }
    to {
        transform: scale(1.1);
    }
}
</style>
