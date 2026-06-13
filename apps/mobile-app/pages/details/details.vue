<template>
	<view class="details-page">
		<TaskTopBar :title="categoryName || '知识详情'" :show-right="false" @back="goBack" />

		<view v-if="loading" class="details-page__loading">
			<text class="details-page__loading-text">加载中...</text>
		</view>

		<template v-else>
			<view class="details-page__hero">
				<image v-if="heroImage" class="details-page__hero-image" :src="heroImage" mode="aspectFill"></image>
				<view class="details-page__hero-content">
					<text class="details-page__eyebrow">{{ categoryName }}</text>
					<text class="details-page__title">{{ heroTitle }}</text>
					<text class="details-page__subtitle">{{ heroSubtitle }}</text>
				</view>
			</view>

			<view v-for="block in contentBlocks" :key="block.title" class="details-page__card">
				<text class="details-page__card-title">{{ block.title }}</text>
				<text class="details-page__paragraph">{{ block.content }}</text>
			</view>

			<view v-if="showVideo" class="details-page__card">
				<text class="details-page__card-title">视频内容</text>
				<video class="details-page__video" :src="assetUrl(selectedItem.path)" controls></video>
			</view>
		</template>
	</view>
</template>

<script>
import { buildAssetUrl, getKnowledgeDetail } from '../../common/api.js';
import TaskTopBar from '../../components/premium/TaskTopBar.vue';

const mockData = {
	中医好文: {
		articleTitle: '中医养生的四季节气',
		articleContent: '顺应四时变化调整饮食、作息与活动强度，是中医养生的重要原则。'
	},
	健康食谱: {
		name: '山药百合粥',
		imageurl: '../../static/pt/2.jpg',
		gongxiao: '适合日常调理脾胃与润肺。',
		shicai: '山药、百合、红枣',
		yaocai: '百合',
		zuofa: '将食材洗净后加水慢炖至软糯即可。'
	},
	常见病防护: {
		name: '失眠',
		introduction: '常见表现为入睡困难、易醒或早醒。',
		preventive: '保持规律作息，晚间减少刺激性饮品。',
		method: '可结合放松训练、睡前泡脚与医生建议进行调理。'
	},
	药材库: {
		name: '黄芪',
		imageurl: '../../static/pt/2.jpg',
		xingweiguijing: '甘，微温。归脾、肺经。',
		yaoliyanjiu: '补气升阳，益卫固表。',
		linchuangzuoyong: '常用于气虚乏力、自汗等调理方向。'
	},
	视频讲座: {
		name: '中医基础理论讲座',
		introduce: '适合作为中医基础入门课程回看学习',
		path: ''
	}
};

const typeMap = {
	中医好文: 'article',
	健康食谱: 'zhonogyaoshipu',
	常见病防护: 'commondiseases',
	药材库: 'chinesemedicine',
	视频讲座: 'video'
};

export default {
	components: {
		TaskTopBar
	},
	data() {
		return {
			categoryName: '',
			selectedItem: {},
			loading: true
		};
	},
	computed: {
		heroTitle() {
			return (
				this.selectedItem.name ||
				this.selectedItem.articleTitle ||
				this.selectedItem.title ||
				'知识详情'
			);
		},
		heroSubtitle() {
			if (this.categoryName === '中医好文') {
				return '精选知识文章，帮助你快速了解核心养生理念分享';
			}
			if (this.categoryName === '健康食谱') {
				return this.selectedItem.gongxiao || '查看食材、药材与做法建议参考';
			}
			if (this.categoryName === '常见病防护') {
				return this.selectedItem.introduction || '了解常见问题的调理与防护思路。';
			}
			if (this.categoryName === '药材库') {
				return this.selectedItem.yaoliyanjiu || '查看药材基础属性与临床应用说明参考';
			}
			return this.selectedItem.introduce || '观看讲座内容，补充系统化知识储备';
		},
		heroImage() {
			if (this.categoryName === '中医好文') {
				return '../../static/pt/1.jpg';
			}
			if (this.categoryName === '健康食谱') {
				return this.assetUrl(this.selectedItem.imageurl) || '../../static/pt/2.jpg';
			}
			if (this.categoryName === '药材库') {
				return this.assetUrl(this.selectedItem.imageurl || this.selectedItem.imgurl) || '../../static/pt/2.jpg';
			}
			if (this.categoryName === '视频讲座') {
				return this.assetUrl(this.selectedItem.coverImage) || '../../static/pt/1.jpg';
			}
					return '../../static/pt/2.jpg';
		},
		contentBlocks() {
			if (this.categoryName === '健康食谱') {
				return [
					{ title: '功效简介', content: this.selectedItem.gongxiao || '暂无' },
					{ title: '食材', content: this.selectedItem.shicai || '暂无' },
					{ title: '药材', content: this.selectedItem.yaocai || '暂无' },
					{ title: '做法', content: this.selectedItem.zuofa || '暂无' }
				];
			}
			if (this.categoryName === '中医好文') {
				return [{ title: '正文内容', content: this.selectedItem.articleContent || '暂无内容' }];
			}
			if (this.categoryName === '药材库') {
				return [
					{ title: '性味归经', content: this.selectedItem.xingweiguijing || '暂无' },
					{ title: '功效研究', content: this.selectedItem.yaoliyanjiu || '暂无' },
					{ title: '临床作用', content: this.selectedItem.linchuangzuoyong || '暂无' }
				];
			}
			if (this.categoryName === '视频讲座') {
				return [{ title: '内容简介', content: this.selectedItem.introduce || '暂无介绍' }];
			}
			return [
				{ title: '症状介绍', content: this.selectedItem.introduction || '暂无' },
				{ title: '预防建议', content: this.selectedItem.preventive || '暂无' },
				{ title: '调理方法', content: this.selectedItem.method || '暂无' }
			];
		},
		showVideo() {
			return this.categoryName === '视频讲座' && !!this.selectedItem.path;
		}
	},
	async onLoad(options) {
		this.categoryName = decodeURIComponent(options.categoryName || '中医好文');
		const type = typeMap[this.categoryName] || 'article';
		const id = decodeURIComponent(options.id || '');

		try {
			const data = await getKnowledgeDetail(type, id);
			this.selectedItem = data || mockData[this.categoryName] || {};
		} catch (error) {
			console.error('知识详情加载失败，回退为默认内容', error);
			this.selectedItem = mockData[this.categoryName] || {};
		}

		this.loading = false;
	},
	methods: {
		assetUrl(path) {
			return buildAssetUrl(path);
		},
		goBack() {
			uni.navigateBack({
				delta: 1
			});
		}
	}
};
</script>

<style>
.details-page {
	min-height: 100vh;
	padding: 18px 18px 28px;
	background:
		linear-gradient(180deg, #ffffff 0%, #f8faf7 100%),
		radial-gradient(circle at 100% 0%, rgba(208, 231, 234, 0.24) 0, transparent 42%);
	box-sizing: border-box;
}

.details-page__loading {
	margin-top: 120px;
	padding: 56px 28px;
	border-radius: 30px;
	background: rgba(255, 255, 255, 0.82);
	box-shadow: 0 18px 42px rgba(31, 95, 202, 0.07);
	text-align: center;
}

.details-page__loading-text {
	color: #1f5fca;
	font-size: 20px;
	font-weight: 800;
}

.details-page__hero {
	overflow: hidden;
	border-radius: 30px;
	background: rgba(255, 255, 255, 0.82);
	box-shadow: 0 18px 42px rgba(31, 95, 202, 0.07);
}

.details-page__hero-image {
	width: 100%;
	height: 220px;
	display: block;
	background: #edf3ef;
}

.details-page__hero-content {
	padding: 20px 18px 22px;
}

.details-page__eyebrow {
	display: block;
	color: #5a7a99;
	font-size: 11px;
	font-weight: 800;
	letter-spacing: 0.16em;
}

.details-page__title {
	display: block;
	margin-top: 12px;
	color: #103268;
	font-size: 28px;
	font-weight: 800;
	line-height: 1.25;
}

.details-page__subtitle {
	display: block;
	margin-top: 12px;
	color: #5a7a99;
	font-size: 14px;
	line-height: 1.75;
}

.details-page__card {
	margin-top: 16px;
	padding: 20px 18px;
	border: 1px solid rgba(163, 196, 232, 0.22);
	border-radius: 26px;
	background: rgba(255, 255, 255, 0.82);
	box-shadow: 0 14px 32px rgba(31, 95, 202, 0.05);
}

.details-page__card-title {
	display: block;
	color: #103268;
	font-size: 18px;
	font-weight: 800;
}

.details-page__paragraph {
	display: block;
	margin-top: 12px;
	color: #5a7a99;
	font-size: 14px;
	line-height: 1.85;
	white-space: pre-line;
}

.details-page__video {
	width: 100%;
	margin-top: 14px;
	border-radius: 22px;
}
</style>
