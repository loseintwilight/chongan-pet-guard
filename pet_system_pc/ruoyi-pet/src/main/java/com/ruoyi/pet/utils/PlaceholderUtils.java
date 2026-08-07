package com.ruoyi.pet.utils;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.pet.mapper.NoticeTemplateMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 模板代替工具类
 */
@Slf4j
@Component
public class PlaceholderUtils {


    // 预编译正则表达式，提高性能
    // 匹配 {key} 格式的占位符，其中key不能为空
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{([^\\{\\}]+)\\}");

    /**
     * 使用正则表达式渲染通知模板
     *
     * @param templateContent 模板内容，如 "您好，{user_name}！"
     * @param params          参数Map
     * @return 渲染后的最终内容
     */
    public static String renderTemplate(String templateContent, Map<String, Object> params) {
        if (StringUtils.isEmpty(templateContent) || params == null || params.isEmpty()) {
            return templateContent;
        }

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(templateContent);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            // 获取占位符的键名，例如 "user_name"
            String key = matcher.group(1);

            // 从Map中获取对应的值
            Object value = params.get(key);

            // 处理值，如果为null则替换为空字符串，避免显示 "null"
            String replacement = (value == null) ? "" : value.toString();

            // 使用 Matcher.appendReplacement() 进行替换，这个方法会处理特殊字符的转义
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }

        // 将剩余的部分添加到 StringBuffer
        matcher.appendTail(sb);

        return sb.toString();
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 根据表名和记录ID获取字段映射，用于模板替换
     *
     * @param tableName 表名
     * @param recordId  记录ID
     * @return 字段映射Map
     */
    public Map<String, Object> getFieldMapping(String tableName, Long recordId) {
        Map<String, Object> fieldMap = new HashMap<>();
        if (recordId == null || StringUtils.isEmpty(recordId.toString())) {
            return fieldMap;
        }
        try {
            // 根据不同的表使用不同的主键字段
            String whereClause = " WHERE ";
            if ("sys_user".equals(tableName)) {
                whereClause += "user_id = ?";
            } else if ("tb_pet_boarding".equals(tableName)) {
                whereClause += "boarding_id = ?";
            } else if ("tb_rescue".equals(tableName)) {
                whereClause += "rescue_id = ?";
            } else if ("tb_pet".equals(tableName)) {
                //whereClause += "pet_id = ?";
                whereClause = " LEFT JOIN tb_shop ON tb_shop.shop_id = tb_pet.dept_id WHERE pet_id = ?";
            } else {
                whereClause += "id = ?"; // 默认使用id
            }
            String sql = "SELECT * FROM " + tableName + whereClause;
            List<Map<String, Object>> records = jdbcTemplate.queryForList(sql, recordId);
            log.info("-------------------------------&&&&&&&&&&&&&&&");
            records.forEach(record->{
                System.out.println(record.toString());
            });
            if (!records.isEmpty()) {
                fieldMap.putAll(records.get(0));
            }
            log.info("从表 {} 查询ID {} 的记录结果: {}", tableName, recordId, fieldMap);
        } catch (Exception e) {
            log.error("查询表 {} 记录 {} 出错: ", tableName, recordId, e);
        }
        return fieldMap;
    }


    /**
     * 结合数据库字段进行模板渲染
     *
     * @param templateContent 模板内容
     * @param tableName       表名
     * @param recordId        记录ID
     * @return 渲染后的内容
     */
    public String renderTemplateWithTableFields(String templateContent, String tableName, Long recordId) {
        Map<String, Object> params = getFieldMapping(tableName, recordId);
        log.error("------------------------------------");
        log.error("{}", params);
        System.out.println("--------------------------------");
        return renderTemplate(templateContent, params);
    }

}
