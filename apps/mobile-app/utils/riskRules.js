import riskRules from '../data/tcmRiskRules.json'

const RISK_ORDER = {
	normal: 0,
	medium: 1,
	high: 2,
	critical: 3
}

function getRiskWeight(level = 'normal') {
	return RISK_ORDER[level] || 0
}

function joinSearchText(item = {}) {
	return [
		item.name,
		item.displayName,
		...(item.aliases || []),
		...(item.keywords || []),
		item.pinyin,
		item.abbr
	].filter(Boolean).join(' ')
}

function applyRiskRules(item = {}) {
	let matched = {
		riskLevel: item.riskLevel || 'normal',
		riskReason: item.riskReason || ''
	}
	const searchText = joinSearchText(item)

	riskRules.forEach((rule) => {
		const hit = (rule.keywords || []).some((keyword) => searchText.includes(keyword))
		if (hit && getRiskWeight(rule.riskLevel) > getRiskWeight(matched.riskLevel)) {
			matched = {
				riskLevel: rule.riskLevel,
				riskReason: rule.riskReason
			}
		}
	})

	return {
		...item,
		riskLevel: matched.riskLevel,
		riskReason: matched.riskReason
	}
}

function getHighestRiskLevel(items = []) {
	return items.reduce((highest, item) => {
		return getRiskWeight(item.riskLevel) > getRiskWeight(highest) ? item.riskLevel : highest
	}, 'normal')
}

function getRiskLabel(level = 'normal') {
	const map = {
		normal: '普通',
		medium: '建议关注',
		high: '高风险',
		critical: '极高风险'
	}
	return map[level] || map.normal
}

export {
	applyRiskRules,
	getHighestRiskLevel,
	getRiskLabel,
	getRiskWeight
}
