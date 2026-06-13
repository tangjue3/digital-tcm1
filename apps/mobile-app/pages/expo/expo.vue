<template>
	<view class="expo-page">
		<PremiumTopBar title="云枢四诊" :show-left="false" />

		<view class="expo-page__hero">
			<text class="expo-page__eyebrow">DIGITAL LIBRARY</text>
			<text class="expo-page__hero-title">健康博览</text>
			<text class="expo-page__hero-subtitle">检索中医好文、健康食谱、常见病知识、药材资料与视频讲座。</text>

			<view class="expo-page__search">
				<input
					v-model="text"
					class="expo-page__input"
					type="text"
					confirm-type="search"
					placeholder="搜索食谱、文章、视频..."
					@confirm="search"
				/>
				<button class="expo-page__search-btn" @click="search">搜索</button>
			</view>
		</view>

		<KnowledgeCategoryTabs :items="categories" :active-index="TabCur" @change="sel" />

		<view class="expo-page__section-head">
			<view>
				<text class="expo-page__section-title">{{ activeCategory.name }}</text>
				<text class="expo-page__section-caption">{{ activeCategory.recommendations.length }} 条内容</text>
			</view>
			<text class="expo-page__section-chip">实时更新</text>
		</view>

		<view v-if="activeCategory.recommendations.length" class="expo-page__list">
			<KnowledgeFeedCard
				v-for="(item, index) in activeCategory.recommendations"
				:key="`${activeCategory.name}-${item.rawId || item.title}-${index}`"
				:title="item.title"
				:description="item.desc"
				:image="item.image || '../../static/empt.png'"
				:tag="activeCategory.shortTag"
				:index-label="formatIndex(index)"
				@tap="jump(activeCategory.name, item)"
			/>
		</view>

		<view v-else class="expo-page__empty">
			<text class="expo-page__empty-title">当前分类暂无内容</text>
			<text class="expo-page__empty-text">可以换一个关键词再试，或切换到其他知识分类试试</text>
		</view>

		<PremiumTabBar active="expo" />
	</view>
</template>

<script>
import { buildAssetUrl, getKnowledgeLists } from '../../common/api.js';
import KnowledgeCategoryTabs from '../../components/premium/KnowledgeCategoryTabs.vue';
import KnowledgeFeedCard from '../../components/premium/KnowledgeFeedCard.vue';
import PremiumTabBar from '../../components/premium/PremiumTabBar.vue';
import PremiumTopBar from '../../components/premium/PremiumTopBar.vue';

const defaultCategories = [
	{
		name: '中医好文',
		shortTag: '文章',
		recommendations: [
			{
				title: '中医养生的四季节律',
				desc: '从饮食、起居与运动三个维度梳理四季养生思路，帮助你更好地顺时调养实践。',
				image: '../../static/pt/1.jpg',
				rawId: 1
			},
			{
				title: '穴位按揉的日常方法',
				desc: '介绍几个常见穴位的按揉位置、频率与适用场景，适合作为居家调理参考使用。',
				image: '../../static/pt/1.jpg',
				rawId: 2
			}
		]
	},
	{
		name: '健康食谱',
		shortTag: '食谱',
		recommendations: [
			{
				title: '山药百合粥',
				desc: '适合日常调理脾胃与润肺，做法简单，适合作为轻养生食谱推荐。',
				image: '../../static/pt/2.jpg',
				rawId: '山药百合粥'
			}
		]
	},
	{
		name: '常见病防治',
		shortTag: '病症',
		recommendations: [
			{
				title: '失眠的中医调理',
				desc: '围绕作息、饮食和情志调养整理失眠人群的基础防治知识分享。',
				image: '../../static/pt/2.jpg',
				rawId: 3
			}
		]
	},
	{
		name: '药材库',
		shortTag: '药材',
		recommendations: [
			{
				title: '黄芪',
				desc: '常见补气药材，适合了解基础功效、归经与常见应用参考。',
				image: '../../static/pt/2.jpg',
				rawId: 4
			}
		]
	},
	{
		name: '视频讲座',
		shortTag: '视频',
		recommendations: [
			{
				title: '中医基础理论讲座',
				desc: '从阴阳、五行、脏腑到经络，适合作为入门知识回看。',
				image: '../../static/pt/1.jpg',
				rawId: 5
			}
		]
	}
];

function createBaseCategories() {
	return JSON.parse(JSON.stringify(defaultCategories));
}

export default {
	components: {
		KnowledgeCategoryTabs,
		KnowledgeFeedCard,
		PremiumTabBar,
		PremiumTopBar
	},
	data() {
		return {
			TabCur: 0,
			text: '',
			categories: createBaseCategories()
		};
	},
	computed: {
		activeCategory() {
			return this.categories[this.TabCur] || this.categories[0];
		}
	},
	onLoad() {
		this.getdata();
	},
	methods: {
		formatIndex(index) {
			return `${String(index + 1).padStart(2, '0')}`;
		},
		search() {
			this.getdata(this.text);
		},
		async getdata(name = '') {
			try {
				const result = await getKnowledgeLists(name);
				const mapped = [
					this.mapArticleItems(result.article),
					this.mapRecipeItems(result.recipe),
					this.mapDiseaseItems(result.disease),
					this.mapHerbItems(result.herb),
					this.mapVideoItems(result.video)
				];
				if (mapped.every(arr => !arr.length)) {
					throw new Error('API 返回数据为空');
				}
				const categories = createBaseCategories();
				categories.forEach((cat, i) => {
					if (mapped[i].length) {
						cat.recommendations = mapped[i];
					}
				});
				this.categories = categories;
			} catch (error) {
				console.error('知识库加载失败，回退为默认内容', error);
				this.categories = createBaseCategories();
			}
		},
		mapArticleItems(rows) {
			if (!Array.isArray(rows) || !rows.length) {
				return [];
			}
			return rows.map((item) => ({
				title: item.articleTitle,
				desc: item.articleContent,
				image: '../../static/pt/1.jpg',
				rawId: item.id
			}));
		},
		mapRecipeItems(rows) {
			if (!Array.isArray(rows) || !rows.length) {
				return [];
			}
			return rows.map((item) => ({
				title: item.name,
				desc: item.gongxiao,
				image: buildAssetUrl(item.imageurl) || '../../static/pt/2.jpg',
				rawId: item.name
			}));
		},
		mapDiseaseItems(rows) {
			if (!Array.isArray(rows) || !rows.length) {
				return [];
			}
			return rows.map((item) => ({
				title: item.name,
				desc: item.introduction,
				image: '../../static/pt/2.jpg',
				rawId: item.id
			}));
		},
		mapHerbItems(rows) {
			if (!Array.isArray(rows) || !rows.length) {
				return [];
			}
			return rows.map((item) => ({
				title: item.name,
				desc: item.xingtai || item.yaoliyanjiu || item.linchuangzuoyong || '',
				image: buildAssetUrl(item.imageurl || item.imgurl) || '../../static/pt/2.jpg',
				rawId: item.id
			}));
		},
		mapVideoItems(rows) {
			if (!Array.isArray(rows) || !rows.length) {
				return [];
			}
			return rows.map((item) => ({
				title: item.name,
				desc: item.introduce,
				image: buildAssetUrl(item.coverImage) || '../../static/pt/1.jpg',
				rawId: item.id
			}));
		},
		jump(name, item) {
			uni.navigateTo({
				url: `/pages/details/details?categoryName=${encodeURIComponent(name)}&id=${encodeURIComponent(item.rawId)}`
			});
		},
		sel(index) {
			this.TabCur = index;
		}
	}
};
</script>

<style>
.expo-page {
	min-height: 100vh;
	padding: 18px 18px 116px;
	background-color: #f5f8ff;
	background-image:
		radial-gradient(circle at 0% 0%, rgba(59, 140, 255, 0.25) 0, transparent 40%),
		radial-gradient(circle at 100% 48%, rgba(31, 95, 202, 0.15) 0, transparent 42%);
	box-sizing: border-box;
}

.expo-page__hero {
	padding: 22px 20px;
	border-radius: 30px;
	background: linear-gradient(135deg, #1f5fca 0%, #1f5fca 100%);
	box-shadow: 0 20px 42px rgba(31, 95, 202, 0.18);
	color: #ffffff;
}

.expo-page__eyebrow {
	display: block;
	font-size: 11px;
	font-weight: 800;
	letter-spacing: 0.16em;
	color: rgba(255, 255, 255, 0.8);
}

.expo-page__hero-title {
	display: block;
	margin-top: 12px;
	font-size: 34px;
	font-weight: 800;
	line-height: 1.15;
}

.expo-page__hero-subtitle {
	display: block;
	margin-top: 12px;
	font-size: 14px;
	line-height: 1.75;
	color: rgba(255, 255, 255, 0.88);
}

.expo-page__search {
	display: flex;
	gap: 10px;
	margin-top: 18px;
	padding: 8px;
	border-radius: 999px;
	background: rgba(255, 255, 255, 0.14);
}

.expo-page__input {
	flex: 1;
	height: 42px;
	padding: 0 16px;
	border-radius: 999px;
	background: rgba(255, 255, 255, 0.92);
	color: #103268;
	font-size: 14px;
}

.expo-page__search-btn {
	padding: 0 18px;
	border-radius: 999px;
	background: #ffffff;
	color: #1f5fca;
	font-size: 14px;
	font-weight: 800;
}

.expo-page__section-head {
	display: flex;
	align-items: flex-end;
	justify-content: space-between;
	gap: 12px;
	margin: 20px 2px 14px;
}

.expo-page__section-title {
	display: block;
	color: #1f5fca;
	font-size: 24px;
	font-weight: 800;
	line-height: 1.25;
}

.expo-page__section-caption {
	display: block;
	margin-top: 6px;
	color: #5a7a99;
	font-size: 12px;
	font-weight: 700;
}

.expo-page__section-chip {
	padding: 7px 12px;
	border-radius: 999px;
	background: rgba(163, 196, 232, 0.5);
	color: #1f5fca;
	font-size: 11px;
	font-weight: 800;
}

.expo-page__list {
	display: flex;
	flex-direction: column;
	gap: 12px;
}

.expo-page__empty {
	margin-top: 20px;
	padding: 44px 20px;
	border-radius: 28px;
	background: rgba(255, 255, 255, 0.82);
	box-shadow: 0 14px 32px rgba(31, 95, 202, 0.05);
	text-align: center;
}

.expo-page__empty-title {
	color: #1f5fca;
	font-size: 20px;
	font-weight: 800;
}

.expo-page__empty-text {
	display: block;
	margin-top: 10px;
	color: #5a7a99;
	font-size: 14px;
	line-height: 1.7;
}
</style>
