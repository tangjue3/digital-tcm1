<template>
	<view class="last-record-card">
		<view class="last-record-card__copy">
			<text class="last-record-card__eyebrow">{{ eyebrow }}</text>
			<view class="last-record-card__value-wrap">
				<text class="last-record-card__value">{{ value }}</text>
				<text class="last-record-card__unit">{{ unit }}</text>
			</view>
		</view>
		<view class="last-record-card__chip">{{ chip }}</view>
	</view>
</template>

<script>
export default {
	name: 'LastRecordCard',
	props: {
		eyebrow: {
			type: String,
			default: '上次记录'
		},
		value: {
			type: [Number, String],
			required: true
		},
		unit: {
			type: String,
			default: '次/ 分钟'
		},
		chip: {
			type: String,
			default: '平稳'
		}
	}
};
</script>

<style>
.last-record-card {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 18px 16px;
	border: 1px solid rgba(163, 196, 232, 0.22);
	border-radius: 24px;
	background: rgba(255, 255, 255, 0.82);
	box-shadow: 0 10px 24px rgba(31, 95, 202, 0.05);
}

.last-record-card__copy {
	display: flex;
	flex-direction: column;
}

.last-record-card__eyebrow {
	color: #5a7a99;
	font-size: 11px;
	font-weight: 800;
	letter-spacing: 0.12em;
}

.last-record-card__value-wrap {
	display: flex;
	align-items: baseline;
	gap: 8px;
	margin-top: 8px;
}

.last-record-card__value {
	color: #103268;
	font-size: 40px;
	font-weight: 800;
	line-height: 1;
}

.last-record-card__unit {
	color: #5a7a99;
	font-size: 13px;
}

.last-record-card__chip {
	padding: 6px 12px;
	border-radius: 999px;
	background: rgba(163, 196, 232, 0.46);
	color: #065043;
	font-size: 11px;
	font-weight: 800;
}
</style>
