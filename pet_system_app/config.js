// 应用全局配置（须同时 export default，否则微信小程序里 import 得到 undefined）
const config = {
	// baseUrl: 'https://vue.ruoyi.vip/prod-api',
	baseUrl: 'http://127.0.0.1:8080',
	downloadUrl: 'http://127.0.0.1:8080/common/download?name=',
	getDownloadUrl(fileName) {
		if (!fileName) return '';
		if (fileName.startsWith('http://') || fileName.startsWith('https://')) {
			return fileName;
		}
		return this.downloadUrl + encodeURIComponent(fileName);
	},
	appInfo: {
		name: 'ruoyi-app',
		version: '1.2.0',
		logo: '/static/logo.png',
		site_url: 'http://ruoyi.vip',
		agreements: [
			{
				title: '隐私政策',
				url: 'https://ruoyi.vip/protocol.html'
			},
			{
				title: '用户服务协议',
				url: 'https://ruoyi.vip/protocol.html'
			}
		]
	}
}

module.exports = config
export default config
