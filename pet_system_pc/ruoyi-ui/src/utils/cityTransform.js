// src/utils/cityTransform.js
import rawData from "@/data/pca.json"

/**
 * 将原始数据转换为 el-cascader 所需的格式
 * @returns {Array} 转换后的数据
 */
export function transformCityData() {
  const result = []

  // 遍历所有省份
  for (const provinceName in rawData) {
    const provinceObj = {
      code: generateCode(provinceName),
      name: provinceName,
      children: []
    }

    const cities = rawData[provinceName]

    // 遍历所有城市
    for (const cityName in cities) {
      const cityObj = {
        code: generateCode(cityName),
        name: cityName,
        children: []
      }

      const districts = cities[cityName]

      // 处理区县数据
      if (Array.isArray(districts)) {
        districts.forEach(districtName => {
          cityObj.children.push({
            code: generateCode(districtName),
            name: districtName
          })
        })
      }

      provinceObj.children.push(cityObj)
    }

    result.push(provinceObj)
  }

  return result
}

/**
 * 简单的代码生成函数（实际项目中可能需要更复杂的编码规则）
 * @param {String} name 名称
 * @returns {String} 生成的代码
 */
function generateCode(name) {
  // 这里可以替换为实际的编码规则
  return btoa(unescape(encodeURIComponent(name))).slice(0, 6)
}
