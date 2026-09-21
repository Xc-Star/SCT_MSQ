import request from '@/common/request.js'

export const getConfigList = () => request.get("/admin/config")

export const updateConfig = (data) => request.put("/admin/config", data)

export const getConfig = () => request.get("/config")

export async function getServerShortName() {
	try {
		const { data } = await getConfig()
		return data.find(item => item.configKey === 'server_short_name')?.configValue || 'SCT'
	} catch {
		return 'SCT'
	}
}