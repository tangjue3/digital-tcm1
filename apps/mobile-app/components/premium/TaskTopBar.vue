<template>
	<view class="task-topbar">
		<button class="task-topbar__action" hover-class="task-topbar__action--hover" @tap="$emit('back')">
			<text class="task-topbar__back">&lt;</text>
		</button>
		<text class="task-topbar__title">{{ title }}</text>
		<button v-if="showRight" class="task-topbar__action" hover-class="task-topbar__action--hover" @tap="$emit('rightTap')">
			<view class="task-topbar__signal">
				<view class="task-topbar__signal-ring"></view>
				<view class="task-topbar__signal-dot"></view>
			</view>
		</button>
		<view v-else class="task-topbar__placeholder"></view>
	</view>
</template>

<script>
export default {
	name: 'TaskTopBar',
	props: {
		title: {
			type: String,
			required: true
		},
		showRight: {
			type: Boolean,
			default: true
		}
	}
};
</script>

<style>
.task-topbar {
	display: flex;
	align-items: center;
	justify-content: space-between;
	margin-bottom: 18px;
	padding: 6px 2px;
}

.task-topbar__action,
.task-topbar__placeholder {
	display: flex;
	align-items: center;
	justify-content: center;
	width: 40px;
	height: 40px;
	flex-shrink: 0;
	border: 0;
	border-radius: 999px;
	background: rgba(255, 255, 255, 0.58);
	box-shadow: 0 8px 24px rgba(31, 95, 202, 0.08);
}

.task-topbar__action {
	padding: 0;
}

.task-topbar__action--hover {
	opacity: 0.82;
}

.task-topbar__back {
	color: #1f5fca;
	font-size: 20px;
	font-weight: 800;
	line-height: 1;
}

.task-topbar__title {
	flex: 1;
	padding: 0 14px;
	color: #1f5fca;
	font-size: 20px;
	font-weight: 800;
	text-align: center;
	letter-spacing: 0.01em;
}

.task-topbar__signal {
	position: relative;
	width: 18px;
	height: 18px;
}

.task-topbar__signal-ring {
	position: absolute;
	top: 1px;
	right: 1px;
	bottom: 1px;
	left: 1px;
	border: 2px solid #1f5fca;
	border-radius: 999px;
}

.task-topbar__signal-dot {
	position: absolute;
	top: 0;
	right: 0;
	width: 6px;
	height: 6px;
	border-radius: 999px;
	background: #94d3c1;
}
</style>
