/**
 * 快速配置后端地址工具
 * 
 * 使用方法：
 * 1. 在命令行中运行：node 快速配置后端地址.js
 * 2. 或者直接修改下面的 BACKEND_URL 变量
 * 3. 然后运行脚本自动更新 config.js
 */

const fs = require('fs');
const path = require('path');

// ============ 配置区域 ============
// 请修改为你的实际后端地址
const BACKEND_URL = 'http://192.168.1.100:8080';  // 修改这里！

// 开发环境和生产环境配置
const CONFIG = {
  development: {
    baseUrl: BACKEND_URL,
    downloadUrl: `${BACKEND_URL}/common/download?name=`,
  },
  production: {
    baseUrl: 'https://your-domain.com',  // 生产环境域名
    downloadUrl: 'https://your-domain.com/common/download?name=',
  }
};

// 当前使用的环境
const CURRENT_ENV = 'development';  // 可选: 'development' 或 'production'

// ============ 脚本逻辑 ============

const configPath = path.join(__dirname, 'config.js');

// 读取当前配置
let configContent = fs.readFileSync(configPath, 'utf8');

// 替换 baseUrl
const currentConfig = CONFIG[CURRENT_ENV];
configContent = configContent.replace(
  /baseUrl:\s*['"].*?['"]/,
  `baseUrl: '${currentConfig.baseUrl}'`
);

// 替换 downloadUrl
configContent = configContent.replace(
  /downloadUrl:\s*['"].*?['"]/,
  `downloadUrl: '${currentConfig.downloadUrl}'`
);

// 写回文件
fs.writeFileSync(configPath, configContent, 'utf8');

console.log('✅ 配置更新成功！');
console.log('📝 当前环境:', CURRENT_ENV);
console.log('🌐 后端地址:', currentConfig.baseUrl);
console.log('📥 下载地址:', currentConfig.downloadUrl);
console.log('\n💡 提示:');
console.log('1. 请确保后端服务已启动');
console.log('2. 请确保手机和电脑在同一局域网（开发环境）');
console.log('3. 在微信开发者工具中勾选"不校验合法域名"');
console.log('4. 重新编译小程序即可看到验证码');
