<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="120px">
      <el-form-item label="使用的模板名称" prop="templateId">
        <el-select v-model="queryParams.templateId" placeholder="请选择使用的模板名称" clearable >
          <el-option
            v-for="item in templateList"
            :key="item.templateId"
            :label="item.templateName"
            :value="parseInt(item.templateId)"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="通知标题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入通知标题"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="发布时间" prop="publishTime">
        <el-date-picker clearable
          v-model="queryParams.publishTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择发布时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="发布人" prop="publishName">
        <el-input
          v-model="queryParams.publishName"
          placeholder="请输入发布人"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option
            v-for="dict in dict.type.pet_notice_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['pet:notice:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['pet:notice:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['pet:notice:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="noticeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" align="center" prop="noticeId" type="index"/>
      <el-table-column label="使用的模板" align="center">
        <template slot-scope="scope">
          <span>{{ getTemplateName(scope.row.templateId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="通知标题" align="center" prop="title" />

      <el-table-column label="发布时间" align="center" prop="publishTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.publishTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="发布人" align="center" prop="publishName" />
      <el-table-column label="收件人" align="center" prop="recipientName"/>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.pet_notice_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200px">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['pet:notice:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['pet:notice:remove']"
          >删除</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-s-grid"
            @click="handleDetail(scope.row)"
            v-hasPermi="['pet:notice:list']"
          >详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改通知对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="550px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="150px">
        <el-form-item label="使用的模板名称" prop="templateId">
          <el-select v-model="form.templateId" placeholder="请选择使用的模板名称" clearable >
            <el-option
              v-for="item in templateList"
              :key="item.templateId"
              :label="item.templateName"
              :value="item.templateId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="通知标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入通知标题" />
        </el-form-item>
        <el-form-item label="通知内容模板">
          <editor v-model="form.content" :min-height="192"/>
        </el-form-item>
        <el-form-item label="发布时间" prop="publishTime">
          <el-date-picker clearable
            v-model="form.publishTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择发布时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="发布人" prop="publishName">
          <el-input v-model="form.publishName" placeholder="请输入发布人" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option
              v-for="dict in dict.type.pet_notice_status"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

<!--    通知信息详情表-->
    <el-dialog :title="title" :visible.sync="openView" width="550px" append-to-body>
      <div style="margin-bottom: 15px; font-weight: bold; text-align: center;"></div>
      <el-table :data="detailTableData" border style="width: 100%">
        <el-table-column label="">
          <template slot-scope="scope">
            <div style="display: flex; align-items: flex-start;">
              <span style="width: 120px; font-weight: bold;">{{ scope.row.label }}:</span>
              <div style="flex: 1;">
                <div v-if="scope.row.label === '通知内容'" v-html="scope.row.value"></div>
                <dict-tag v-else-if="scope.row.label === '状态'" :options="dict.type.pet_notice_status" :value="scope.row.value"></dict-tag>
                <span v-else>{{ scope.row.value }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancel">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listNotice, getNotice, delNotice, addNotice, updateNotice } from "@/api/pet/notice"
import {listTemplate} from "@/api/pet/template";
import {getBoarding} from "@/api/pet/boarding";

export default {
  name: "Notice",
  dicts: ['pet_notice_status'],
  data() {
    return {
      // 查询模板名称
      templateList: [],
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 通知表格数据
      noticeList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      openView: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        templateId: null,
        title: null,
        recipientName: null,
        publishTime: null,
        publishName: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      },
      // 详情表格数据
      detailTableData: []
    }
  },
  created() {
    this.getList()
    this.getTemplateList()
  },
  methods: {
    formatRecipients() {
      console.log('form数据:', this.form)
      console.log('收件人相关字段:', {
        isToAll: this.form.isToAll,
        toAll: this.form.toAll,
        sendToAll: this.form.sendToAll,
        recipientType: this.form.recipientType,
        recipients: this.form.recipients,
        recipientNames: this.form.recipientNames,
        recipientName: this.form.recipientName,
        recipient: this.form.recipient
      })
      
      // 检查多种可能的"所有人"标识字段
      const isToAll = this.form.isToAll || this.form.toAll || this.form.sendToAll || this.form.recipientType === 1;
      if (isToAll === 1 || isToAll === true) {
        return '所有人';
      }

      // 检查多种可能的收件人字段
      if (this.form.recipients && Array.isArray(this.form.recipients)) {
        return this.form.recipients.join(', ');
      }

      if (this.form.recipientNames) {
        return this.form.recipientNames;
      }

      if (this.form.recipientName) {
        return this.form.recipientName;
      }
      
      if (this.form.recipient) {
        return this.form.recipient;
      }
      
      // 检查更多可能的字段名称
      for (const key in this.form) {
        if (key.includes('recip') || key.includes('user')) {
          console.log('可能的收件人字段:', key, this.form[key]);
        }
      }

      return '未知收件人';
    },
    getTemplateName(templateId) {
      if (!templateId) return '';
      // 确保类型一致，避免字符串和数字比较问题
      const template = this.templateList.find(item => parseInt(item.templateId) === parseInt(templateId));
      return template ? template.templateName : '';
    },
    getTemplateList(){
      const params = {
        pageNum: 1,
        pageSize: 1000000,
        status: 0
      }
      listTemplate(params).then(response=>{
        this.templateList = response.rows
      })
    },

    /** 查询通知列表 */
    getList() {
      this.loading = true
      listNotice(this.queryParams).then(response => {
        this.noticeList = response.rows
        if (this.noticeList.length > 0) {
          console.log('列表数据结构:', this.noticeList[0])
          console.log('列表收件人字段:', {
            recipientName: this.noticeList[0].recipientName,
            recipients: this.noticeList[0].recipients,
            recipient: this.noticeList[0].recipient
          })
        }
        this.total = response.total
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.openView = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        noticeId: null,
        templateId: null,
        title: null,
        noticeMain: null,
        publishTime: null,
        publishName: null,
        status: null,
        createTime: null,
        createBy: null,
        updateTime: null,
        updateBy: null,
        remark: null,
        isDeleted: null
      }
      this.resetForm("form")
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.noticeId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加通知"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const noticeId = row.noticeId || this.ids
      getNotice(noticeId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改通知"
      })
    },
    /** 查看详情操作 */
    handleDetail(row) {
      // 先保存row中的收件人信息
      const rowRecipientInfo = {
        recipientName: row.recipientName,
        recipients: row.recipients,
        recipient: row.recipient
      }
      
      this.reset()
      const noticeId = row.noticeId || this.ids
      getNotice(noticeId).then(response => {
        this.form = response.data
        console.log('详情数据:', this.form)
        // 详细记录所有可能的收件人相关字段
        console.log('详情收件人相关字段:', {
          recipientName: this.form.recipientName,
          recipients: this.form.recipients,
          recipientNames: this.form.recipientNames,
          recipient: this.form.recipient,
          isToAll: this.form.isToAll,
          toAll: this.form.toAll,
          sendToAll: this.form.sendToAll,
          recipientType: this.form.recipientType,
          // 添加更多可能的字段名称
          userIds: this.form.userIds,
          userNames: this.form.userNames,
          memberIds: this.form.memberIds,
          memberNames: this.form.memberNames,
          targetIds: this.form.targetIds,
          targetNames: this.form.targetNames
        })
        console.log('列表row中的收件人信息:', rowRecipientInfo)
        if (this.form.status === undefined){
          console.warn('返回数据中不包含 status 字段')
        }
        
        // 直接从form中获取收件人信息
        let recipientValue = '未知收件人'
        
        // 优先使用row中的收件人信息（因为列表可以正常显示）
        if (rowRecipientInfo.recipientName) {
          recipientValue = rowRecipientInfo.recipientName
        } else if (rowRecipientInfo.recipients && Array.isArray(rowRecipientInfo.recipients)) {
          recipientValue = rowRecipientInfo.recipients.join(', ')
        } else if (rowRecipientInfo.recipient) {
          recipientValue = rowRecipientInfo.recipient
        } 
        // 然后检查详情数据中的收件人字段
        else if (this.form.recipientName) {
          recipientValue = this.form.recipientName
        } else if (this.form.recipients && Array.isArray(this.form.recipients)) {
          recipientValue = this.form.recipients.join(', ')
        } else if (this.form.recipientNames) {
          recipientValue = this.form.recipientNames
        } else if (this.form.recipient) {
          recipientValue = this.form.recipient
        } else if (this.form.userNames) {
          recipientValue = this.form.userNames
        } else if (this.form.memberNames) {
          recipientValue = this.form.memberNames
        } else if (this.form.targetNames) {
          recipientValue = this.form.targetNames
        } else if (this.form.userIds && Array.isArray(this.form.userIds)) {
          recipientValue = this.form.userIds.join(', ')
        } else if (this.form.memberIds && Array.isArray(this.form.memberIds)) {
          recipientValue = this.form.memberIds.join(', ')
        } else if (this.form.targetIds && Array.isArray(this.form.targetIds)) {
          recipientValue = this.form.targetIds.join(', ')
        } else if (this.form.isToAll === 1 || this.form.isToAll === true || 
                  this.form.toAll === 1 || this.form.toAll === true || 
                  this.form.sendToAll === 1 || this.form.sendToAll === true ||
                  this.form.recipientType === 1) {
          recipientValue = '所有人'
        } else {
          // 如果所有字段都不存在，列出表单中所有包含recip, user, member, target的字段
          console.log('未找到标准收件人字段，表单中包含的相关字段：')
          for (const key in this.form) {
            if (key.includes('recip') || key.includes('user') || key.includes('member') || key.includes('target')) {
              console.log(`  ${key}:`, this.form[key])
            }
          }
        }
        
        // 填充详情表格数据
        this.detailTableData = [
          { label: '使用的模板名称', value: this.getTemplateName(this.form.templateId) },
          { label: '通知标题', value: this.form.title },
          { label: '收件人', value: recipientValue },
          { label: '通知内容', value: this.form.noticeMain },
          { label: '发布时间', value: this.parseTime(this.form.publishTime, '{y}-{m}-{d}') },
          { label: '发布人', value: this.form.publishName },
          { label: '状态', value: this.form.status },
          { label: '备注', value: this.form.remark }
        ]
        this.openView = true
        this.title = "通知详情"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.noticeId != null) {
            updateNotice(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addNotice(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const noticeIds = row.noticeId || this.ids
      this.$modal.confirm('是否确认删除通知编号为"' + noticeIds + '"的数据项？').then(function() {
        return delNotice(noticeIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('pet/notice/export', {
        ...this.queryParams
      }, `notice_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
