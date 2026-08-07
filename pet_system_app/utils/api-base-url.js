/**
 * 解析后端 baseUrl（兼容 module.exports / export default、小程序运行时）
 */
export function getApiBaseUrl() {
	try {
		if (typeof getApp === 'function') {
			const app = getApp()
			const g = app && app.globalData && app.globalData.config
			if (g && g.baseUrl) {
				return String(g.baseUrl).replace(/\/$/, '')
			}
		}
	} catch (e) {}
	try {
		const mod = require('@/config')
		const cfg = mod && (mod.default != null ? mod.default : mod)
		if (cfg && cfg.baseUrl) {
			return String(cfg.baseUrl).replace(/\/$/, '')
		}
	} catch (e2) {}
	return 'http://127.0.0.1:8080'
}
