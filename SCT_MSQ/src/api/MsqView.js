import request from '@/common/request.js'

const pendingPlayers = new Map()

export function getPlayer(name) {
	const id = name.trim()
	if (pendingPlayers.has(id)) return pendingPlayers.get(id)
	const controller = new AbortController()
	const timeout = setTimeout(() => controller.abort(), 10000)
	const pending = fetch(`https://api.ashcon.app/mojang/v2/user/${encodeURIComponent(id)}`, {
		signal: controller.signal
	}).then(response => {
		if (response.status === 404) return null
		if (!response.ok) throw new Error('玩家信息查询失败')
		return response.json()
	}).finally(() => {
		clearTimeout(timeout)
		pendingPlayers.delete(id)
	})
	pendingPlayers.set(id, pending)
	return pending
}

export const getOneMsqVO = (msqId, config = {}) => request.get("/topic/getOne/" + msqId, config)

export const getMsqVO = (type, config = {}) => request.get("/topic/getType/" + type, config)

export const submitMsq = (msqSubmitDTO) => request.post("/topic/submit", msqSubmitDTO)

export const getMsqResult = (id, config = {}) => request.get("/msq/getMsqResult/" + encodeURIComponent(id), config)