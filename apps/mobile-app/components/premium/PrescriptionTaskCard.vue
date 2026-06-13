<template>
	<view class="prescription-task-card" @tap="$emit('toggle')">
		<view class="prescription-task-card__icon">{{ iconText }}</view>
		<view class="prescription-task-card__main">
			<view class="prescription-task-card__meta">
				<text class="prescription-task-card__title">{{ title }}</text>
				<text :class="['prescription-task-card__badge', { 'prescription-task-card__badge--soft': badge !== '必做' }]">
					{{ badge }}
				</text>
			</view>
			<text class="prescription-task-card__content">{{ content }}</text>
		</view>
		<view :class="['prescription-task-card__status', { 'prescription-task-card__status--done': completed }]">
			<view v-if="completed" class="prescription-task-card__status-dot"></view>
		</view>
	</view>
</template>

<script>
export default {
	name: 'PrescriptionTaskCard',
	props: {
		title: {
			type: String,
			required: true
		},
		content: {
			type: String,
			required: true
		},
		iconText: {
			type: String,
			default: '💊'
		},
		badge: {
			type: String,
			default: '建议'
		},
		completed: {
			type: Boolean,
			default: false
		}
	}
};
</script>

<style>
.prescription-task-card {
	display: flex;
	align-items: center;
	gap: 14px;
	padding: 16px;
	border: 1px solid rgba(163, 196, 232, 0.32);
	border-radius: 22px;
	background: rgba(255, 255, 255, 0.9);
	box-shadow: 0 10px 26px rgba(31, 95, 202, 0.04);
}

.prescription-task-card__icon {
	display: flex;
	align-items: center;
	justify-content: center;
	width: 50px;
	height: 50px;
	flex-shrink: 0;
	border-radius: 16px;
	background: rgba(163, 196, 232, 0.52);
	color: #1f5fca;
	font-size: 18px;
	font-weight: 800;
}

.prescription-task-card__main {
	flex: 1;
	min-width: 0;
}

.prescription-task-card__meta {
	display: flex;
	align-items: center;
	gap: 8px;
	margin-bottom: 6px;
}

.prescription-task-card__title {
	color: #103268;
	font-size: 16px;
	font-weight: 800;
}

.prescription-task-card__badge {
	padding: 4px 8px;
	border-radius: 6px;
	background: rgba(255, 218, 214, 0.6);
	color: #93000a;
	font-size: 10px;
	font-weight: 800;
}

.prescription-task-card__badge--soft {
	background: rgba(31, 95, 202, 0.08);
	color: #065043;
}

.prescription-task-card__content {
	color: #3f4945;
	font-size: 13px;
	line-height: 1.55;
}

.prescription-task-card__status {
	display: flex;
	align-items: center;
	justify-content: center;
	width: 28px;
	height: 28px;
	flex-shrink: 0;
	border: 1.5px solid #a3c4e8;
	border-radius: 999px;
	background: rgba(255, 255, 255, 0.9);
}

.prescription-task-card__status--done {
	border-color: #1f5fca;
	background: #1f5fca;
	box-shadow: 0 6px 14px rgba(31, 95, 202, 0.18);
}

.prescription-task-card__status-dot {
	width: 10px;
	height: 10px;
	border-radius: 999px;
	background: #ffffff;
}
</style>
