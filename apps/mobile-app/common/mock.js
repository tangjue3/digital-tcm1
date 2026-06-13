/**
 * Mock 数据工具
 * 当后端接口不可用时，使用 mock 数据进行演示
 */

// Mock 数据开关 - 设置为 true 时使用 mock 数据
const USE_MOCK = true;

// 舌诊/面诊分析 Mock 数据
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

// 声纹分析 Mock 数据
const mockVoiceData = {
	code: 20000,
	success: true,
	msg: "成功",
	data: {
		answer_type: 2,
		report: {
			syndrome_name: "气虚证",
			syndrome_explain: "声音低弱，气息不足，说话易疲劳",
			syndrome_point: null,
			contraindication: null,
			diagnose_key_words: [
				"声音低弱",
				"气息不足",
				"容易疲乏"
			],
			diagnose_explain: "肺气不足，声带功能减弱，导致声音低弱。",
			symptom_names: [
				{
					name: "声音低弱",
					desc: "说话声音较小，气息不足，容易疲劳",
					type: "声音"
				}
			],
			commodity: [
				{
					commodity_name: "[同仁堂]补中益气丸 9g*10丸/盒 补中益气 健脾养胃",
					commodity_img_path: "https://img.alicdn.com/imgextra/i4/2200724907121/O1CN01.jpg",
					commodity_shopping_link: "packages/goods/detail/index?alias=xxx"
				},
				{
					commodity_name: "[云南白药]黄芪精口服液 10ml*10支/盒 补气养血",
					commodity_img_path: "https://img.alicdn.com/imgextra/i4/2200724907121/O1CN02.jpg",
					commodity_shopping_link: "packages/goods/detail/index?alias=xxx2"
				}
			],
			combine_prescriptions: [],
			risk_warning: "建议适当休息，避免过度用嗓，可适当食用补气食物。",
			suggest_live: "保持规律作息，适当运动，避免熬夜。",
			suggest_food: "饮食管理：选择清淡易消化食物，多吃山药、黄芪、党参等补气食材。\n运动管理：适合进行太极拳、八段锦等柔和运动。\n防护措施：注意保暖，避免受凉感冒。"
		},
		addition_questions: [],
		session_id: "mock-session-id-12345"
	}
};

/**
 * 模拟网络延迟
 * @param {number} ms 延迟毫秒数
 */
function delay(ms = 1000) {
	return new Promise(resolve => setTimeout(resolve, ms));
}

/**
 * 上传文件 Mock
 * @param {string} url 接口地址
 * @param {string} filePath 文件路径
 * @param {object} formData 表单数据
 * @returns {Promise} 返回 mock 数据
 */
function mockUploadFile(url, filePath, formData) {
	return new Promise((resolve, reject) => {
		console.log('[Mock] 模拟上传文件:', url, filePath, formData);

		// 模拟延迟
		setTimeout(() => {
			// 根据接口类型返回不同的 mock 数据
			if (url.includes('faceAi')) {
				resolve({
					data: JSON.stringify({
						code: 20000,
						success: true,
						msg: "成功",
						data: mockFaceAiData
					})
				});
			} else if (url.includes('iseC')) {
				resolve({
					data: JSON.stringify(mockVoiceData)
				});
			} else {
				resolve({
					data: JSON.stringify({
						code: 20000,
						success: true,
						msg: "成功",
						data: {}
					})
				});
			}
		}, 1500);
	});
}

/**
 * 安全的上传文件方法
 * 优先尝试真实接口，失败后使用 mock 数据
 * @param {object} options 上传选项
 */
function safeUploadFile(options) {
	const { url, filePath, name, formData, success, fail, complete } = options;

	if (USE_MOCK) {
		// 直接使用 mock 数据
		mockUploadFile(url, filePath, formData)
			.then(res => {
				console.log('[Mock] 返回 mock 数据');
				if (success) success(res);
				if (complete) complete(res);
			})
			.catch(err => {
				console.error('[Mock] Mock 数据生成失败', err);
				if (fail) fail(err);
				if (complete) complete(err);
			});
	} else {
		// 使用真实接口
		uni.uploadFile({
			url,
			filePath,
			name,
			formData,
			success: (res) => {
				console.log('[API] 接口调用成功', res);
				if (success) success(res);
			},
			fail: (err) => {
				console.error('[API] 接口调用失败，使用 mock 数据', err);
				// 接口失败时使用 mock 数据
				mockUploadFile(url, filePath, formData)
					.then(mockRes => {
						console.log('[Mock] 使用 mock 数据替代');
						if (success) success(mockRes);
					})
					.catch(mockErr => {
						if (fail) fail(mockErr);
					});
			},
			complete
		});
	}
}

// 导出
export default {
	USE_MOCK,
	mockFaceAiData,
	mockVoiceData,
	delay,
	mockUploadFile,
	safeUploadFile
};

// 挂载到 uni 对象上方便使用
uni.$mock = {
	uploadFile: safeUploadFile,
	data: {
		faceAi: mockFaceAiData,
		voice: mockVoiceData
	}
};
