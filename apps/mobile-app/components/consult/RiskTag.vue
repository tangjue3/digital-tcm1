<template>
	<text v-if="visible" :class="['risk-tag', `risk-tag--${level}`]">{{ label }}</text>
</template>

<script>
import { getRiskLabel } from '../../utils/riskRules.js'

export default {
	name: 'RiskTag',
	props: {
		level: {
			type: String,
			default: 'normal'
		},
		showNormal: {
			type: Boolean,
			default: false
		}
	},
	computed: {
		visible() {
			return this.showNormal || this.level !== 'normal'
		},
		label() {
			return getRiskLabel(this.level)
		}
	}
}
</script>

<style scoped>
.risk-tag {
	display: inline-flex;
	align-items: center;
	justify-content: center;
	padding: 5px 10px;
	border-radius: 999px;
	font-size: 11px;
	font-weight: 800;
	line-height: 1.2;
	letter-spacing: 0.04em;
}

.risk-tag--normal {
	background: rgba(208, 231, 234, 0.42);
	color: #065043;
}

.risk-tag--medium {
	background: rgba(255, 236, 204, 0.72);
	color: #ad6800;
}

.risk-tag--high,
.risk-tag--critical {
	background: rgba(255, 225, 223, 0.88);
	color: #c24134;
}
</style>
