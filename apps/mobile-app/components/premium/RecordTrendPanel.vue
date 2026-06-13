<template>
	<view class="trend-panel">
		<view class="trend-panel__head">
			<view>
				<text class="trend-panel__title">我的记录</text>
				<text class="trend-panel__subtitle">PULSE HISTORY</text>
			</view>
			<view v-if="latestValue" class="trend-panel__tag">{{ latestValue }}</view>
		</view>

		<view class="trend-panel__chart">
			<svg viewBox="0 0 300 150" class="trend-panel__svg">
				<defs>
					<linearGradient id="chart-area-grad" x1="0" y1="0" x2="0" y2="1">
						<stop offset="0" stop-color="#1f5fca" stop-opacity="0.22" />
						<stop offset="1" stop-color="#1f5fca" stop-opacity="0.01" />
					</linearGradient>
				</defs>

				<line
					v-for="(gy, gi) in [30, 60, 90, 120]"
					:key="gi"
					x1="24" :y1="gy" x2="276" :y2="gy"
					stroke="rgba(112,121,117,0.1)" stroke-width="1"
				/>

				<path :d="areaPath" fill="url(#chart-area-grad)" />

				<path
					:d="linePath"
					stroke="#1f5fca" stroke-width="2.5" fill="none"
					stroke-linecap="round" stroke-linejoin="round"
				/>

				<circle
					v-for="(pt, i) in points" :key="i"
					:cx="pt.x" :cy="pt.y" r="4.5"
					fill="#ffffff" stroke="#1f5fca" stroke-width="2"
				/>

				<circle
					v-if="points.length"
					:cx="points[points.length - 1].x"
					:cy="points[points.length - 1].y"
					r="2.5" fill="#1f5fca" stroke="none"
				/>
			</svg>

			<view class="trend-panel__y-label">次/分</view>

			<view class="trend-panel__labels">
				<text
					v-for="(item, i) in displayData" :key="i"
					:class="['trend-panel__label', { 'trend-panel__label--active': i === displayData.length - 1 }]"
				>
					{{ item.label }}
				</text>
			</view>
		</view>
	</view>
</template>

<script>
export default {
	name: 'RecordTrendPanel',
	props: {
		records: {
			type: Array,
			default: () => []
		}
	},
	computed: {
		displayData() {
			if (this.records && this.records.length >= 2) return this.records;
			return this.getDefaultRecords();
		},
		latestValue() {
			const data = this.displayData;
			if (!data.length) return '';
			const v = Number(data[data.length - 1].value) || 0;
			return v ? `${v} 次/分` : '';
		},
		points() {
			const data = this.displayData;
			if (!data.length) return [];

			const values = data.map(d => Number(d.value) || 0);
			const min = Math.min(...values) - 3;
			const max = Math.max(...values) + 3;
			const range = max - min || 1;
			const n = data.length;

			const chartLeft = 24;
			const chartWidth = 252;
			const chartTop = 10;
			const chartHeight = 125;

			return data.map((d, i) => ({
				x: Math.round(chartLeft + (i / (n - 1)) * chartWidth),
				y: Math.round(chartTop + (1 - (Number(d.value) - min) / range) * chartHeight)
			}));
		},
		linePath() {
			return this.points.map((p, i) =>
				`${i === 0 ? 'M' : 'L'}${p.x},${p.y}`
			).join(' ');
		},
		areaPath() {
			const pts = this.points;
			if (pts.length < 2) return '';
			const bottom = 138;
			const line = pts.map((p, i) =>
				`${i === 0 ? 'M' : 'L'}${p.x},${p.y}`
			).join(' ');
			return `${line} L${pts[pts.length - 1].x},${bottom} L${pts[0].x},${bottom} Z`;
		}
	},
	methods: {
		getDefaultRecords() {
			return [
				{ value: 76, label: '07:30' },
				{ value: 74, label: '09:00' },
				{ value: 72, label: '10:30' },
				{ value: 77, label: '12:15' },
				{ value: 73, label: '14:00' },
				{ value: 71, label: '15:30' },
				{ value: 70, label: '18:30' }
			];
		}
	}
};
</script>

<style>
.trend-panel {
	padding: 22px 20px;
	border: 1px solid rgba(255, 255, 255, 0.84);
	border-radius: 28px;
	background: rgba(255, 255, 255, 0.74);
	box-shadow: 0 16px 34px rgba(31, 95, 202, 0.08);
	backdrop-filter: blur(18px);
}

.trend-panel__head {
	display: flex;
	align-items: center;
	justify-content: space-between;
	margin-bottom: 18px;
}

.trend-panel__title {
	color: #1f5fca;
	font-size: 26px;
	font-weight: 800;
	line-height: 1.2;
}

.trend-panel__subtitle {
	display: block;
	margin-top: 6px;
	color: #4d6265;
	font-size: 11px;
	font-weight: 800;
	letter-spacing: 0.16em;
}

.trend-panel__tag {
	padding: 7px 14px;
	border-radius: 999px;
	background: rgba(208, 231, 234, 0.58);
	color: #1f5fca;
	font-size: 12px;
	font-weight: 800;
	white-space: nowrap;
}

.trend-panel__chart {
	position: relative;
}

.trend-panel__svg {
	display: block;
	width: 100%;
	height: auto;
}

.trend-panel__y-label {
	position: absolute;
	right: 4px;
	top: 16px;
	color: #a3c4e8;
	font-size: 10px;
	font-weight: 700;
	letter-spacing: 0.04em;
	writing-mode: vertical-lr;
}

.trend-panel__labels {
	display: flex;
	justify-content: space-between;
	padding: 6px 18px 0;
}

.trend-panel__label {
	color: #5a7a99;
	font-size: 10px;
	font-weight: 700;
	text-align: center;
	flex: 1;
}

.trend-panel__label--active {
	color: #1f5fca;
	font-weight: 800;
}
</style>
