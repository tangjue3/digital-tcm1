import diseaseTree from '../data/tcmDiseaseTree.json'
import { applyRiskRules } from './riskRules.js'

function normalizeText(text = '') {
	return String(text).trim().toLowerCase()
}

function ensureArray(value) {
	return Array.isArray(value) ? value : []
}

function normalizeDiseaseNode(node, parent = null) {
	const parentPath = ensureArray(parent?.path)
	const displayName = node.displayName || node.name
	const path = node.path || [...parentPath, displayName]
	const normalized = {
		parentCode: parent?.code || node.parentCode || '',
		level: parent ? (parent.level || 1) + 1 : (node.level || 1),
		displayName,
		aliases: ensureArray(node.aliases),
		keywords: ensureArray(node.keywords),
		pinyin: node.pinyin || '',
		abbr: node.abbr || '',
		selectable: node.selectable !== undefined ? node.selectable : !node.children,
		riskLevel: node.riskLevel || 'normal',
		riskReason: node.riskReason || '',
		enabled: node.enabled !== false,
		path,
		...node
	}
	return applyRiskRules(normalized)
}

function walkTree(nodes = diseaseTree, parent = null, result = []) {
	nodes.forEach((node) => {
		const normalized = normalizeDiseaseNode(node, parent)
		result.push(normalized)
		if (node.children && node.children.length) {
			walkTree(node.children, normalized, result)
		}
	})
	return result
}

function getDiseaseTree() {
	const normalizeChildren = (nodes, parent = null) => {
		return nodes.map((node) => {
			const normalized = normalizeDiseaseNode(node, parent)
			return {
				...normalized,
				children: node.children ? normalizeChildren(node.children, normalized) : []
			}
		})
	}
	return normalizeChildren(diseaseTree)
}

function getDiseaseSearchIndex() {
	return walkTree().filter((item) => item.enabled && item.selectable)
}

function getSearchBlob(item) {
	return normalizeText([
		item.name,
		item.displayName,
		...(item.aliases || []),
		...(item.keywords || []),
		item.pinyin,
		item.abbr,
		...(item.path || [])
	].filter(Boolean).join(' '))
}

function searchDiseases(keyword) {
	const query = normalizeText(keyword)
	if (!query) {
		return []
	}
	return getDiseaseSearchIndex()
		.filter((item) => getSearchBlob(item).includes(query))
		.slice(0, 30)
}

function findDiseaseByCode(code) {
	return getDiseaseSearchIndex().find((item) => item.code === code) || null
}

export {
	findDiseaseByCode,
	getDiseaseSearchIndex,
	getDiseaseTree,
	searchDiseases
}
