<template>
	<view class="knowledge-feed-card" @tap="$emit('tap')">
		<image class="knowledge-feed-card__image" :src="image" mode="aspectFill"></image>
		<view class="knowledge-feed-card__content">
			<view class="knowledge-feed-card__meta">
				<text class="knowledge-feed-card__tag">{{ tag }}</text>
				<text v-if="indexLabel" class="knowledge-feed-card__index">{{ indexLabel }}</text>
			</view>
			<text class="knowledge-feed-card__title">{{ title }}</text>
			<text class="knowledge-feed-card__desc">{{ description }}</text>
		</view>
	</view>
</template>

<script>
export default {
	name: 'KnowledgeFeedCard',
	props: {
		title: {
			type: String,
			required: true
		},
		description: {
			type: String,
			default: ''
		},
		image: {
			type: String,
			default: ''
		},
		tag: {
			type: String,
			default: '推荐'
		},
		indexLabel: {
			type: String,
			default: ''
		}
	}
};
</script>

<style>
.knowledge-feed-card {
	display: flex;
	gap: 14px;
	padding: 16px;
	border: 1px solid rgba(163, 196, 232, 0.22);
	border-radius: 26px;
	background: rgba(255, 255, 255, 0.86);
	box-shadow: 0 14px 32px rgba(31, 95, 202, 0.06);
}

.knowledge-feed-card__image {
	width: 104px;
	height: 104px;
	flex-shrink: 0;
	border-radius: 22px;
	background: #edf3ef;
}

.knowledge-feed-card__content {
	display: flex;
	min-width: 0;
	flex: 1;
	flex-direction: column;
}

.knowledge-feed-card__meta {
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 8px;
}

.knowledge-feed-card__tag {
	padding: 5px 10px;
	border-radius: 999px;
	background: rgba(208, 231, 234, 0.5);
	color: #065043;
	font-size: 11px;
	font-weight: 800;
}

.knowledge-feed-card__index {
	color: #8da1a4;
	font-size: 11px;
	font-weight: 700;
}

.knowledge-feed-card__title {
	margin-top: 10px;
	color: #103268;
	font-size: 17px;
	font-weight: 800;
	line-height: 1.35;
}

.knowledge-feed-card__desc {
	margin-top: 8px;
	color: #5a7a99;
	font-size: 13px;
	line-height: 1.65;
	display: -webkit-box;
	overflow: hidden;
	text-overflow: ellipsis;
	-webkit-line-clamp: 3;
	-webkit-box-orient: vertical;
}
</style>
