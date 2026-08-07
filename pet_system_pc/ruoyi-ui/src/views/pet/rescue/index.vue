<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="上报人姓名" prop="reporterName">
        <el-input
          v-model="queryParams.reporterName"
          placeholder="请输入上报人姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="上报人电话" prop="reporterPhone">
        <el-input
          v-model="queryParams.reporterPhone"
          placeholder="请输入上报人电话"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="发现地点" prop="location">
        <el-input
          v-model="queryParams.location"
          placeholder="请输入发现地点"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="处理状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择处理状态" clearable>
          <el-option
            v-for="dict in dict.type.pet_rescue_status"
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
          v-hasPermi="['pet:rescue:add']"
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
          v-hasPermi="['pet:rescue:edit']"
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
          v-hasPermi="['pet:rescue:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="rescueList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" align="center" prop="rescueId" type="index" width="60"/>
      <el-table-column label="上报人姓名" align="center" prop="reporterName" width="140"/>
      <el-table-column label="上报人电话" align="center" prop="reporterPhone" />
      <el-table-column label="发现地点" align="center" prop="location" />
      <el-table-column label="现场图片" align="center" prop="images" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.images" :width="50" :height="50"/>
        </template>
      </el-table-column>
      <el-table-column label="处理状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.pet_rescue_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
            v-hasPermi="['pet:rescue:query']"
          >查看详情
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['pet:rescue:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['pet:rescue:remove']"
          >删除</el-button>
          <el-button
            v-if="scope.row.status == 0"
            size="mini"
            type="text"
            icon="el-icon-receiving"
            @click="handleAudit(scope.row)"
            v-hasPermi="['pet:rescue:audit']"
            style="color: #dc3041;"
          >审核
          </el-button>
          <el-button
            v-if="scope.row.status === 1"
            size="mini"
            type="text"
            icon="el-icon-check"
            @click="handleComplete(scope.row)"
            v-hasPermi="['pet:rescue:complete']"
            style="color: #67C23A;"
          >已处理
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-phone-outline"
            @click="handleNotice(scope.row)"
            v-hasPermi="['pet:notice:add']"
          >通知
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

    <!-- 添加或修改救助登记对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="上报人姓名" prop="reporterName">
          <el-input v-model="form.reporterName" placeholder="请输入上报人姓名" />
        </el-form-item>
        <el-form-item label="上报人电话" prop="reporterPhone">
          <el-input v-model="form.reporterPhone" placeholder="请输入上报人电话" />
        </el-form-item>
        <el-form-item label="现场图片" prop="images">
          <image-upload v-model="form.images"/>
        </el-form-item>
        <el-form-item label="发现地点" prop="location">
          <el-input v-model="form.location" placeholder="请输入发现地点" />
        </el-form-item>
        <el-form-item label="宠物描述" prop="petDesc">
          <el-input v-model="form.petDesc" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="处理状态" prop="status" >
          <el-select v-model="form.status" placeholder="请选择处理状态" style="width: 150px">
            <el-option
              v-for="dict in dict.type.pet_rescue_status"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
              :disabled="false"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

<!--    查看详情-->
    <el-dialog
      :title="'救助详情'"
      :visible.sync="viewOpen"
      width="600px"
      append-to-body
    >
      <el-descriptions
        :column="1"
        border
        title="基础信息"
        style="margin-bottom: 20px;"
      >
        <el-descriptions-item label="上报人">
          {{ form.reporterName || '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="上报人电话">
          {{ form.reporterPhone || '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="发现地点">
          {{ form.location || '--'}}
        </el-descriptions-item>
        <el-descriptions-item label="现场图片">
          <image-preview
            v-if="form.images"
            :src="form.images"
            :width="80"
            :height="80"
            style="border-radius: 4px;"
          />
          <span v-else>--</span>
        </el-descriptions-item>
        <el-descriptions-item label="宠物描述">
          {{ form.petDesc || '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="处理状态">
          <!-- 未处理 -->
          <el-tag v-if="form.status == 0" type="danger">待处理</el-tag>

          <!-- 处理中 -->
          <el-tag v-else-if="form.status == 1" type="primary">已审核(处理中)</el-tag>

          <!-- 已处理 -->
          <el-tag v-else-if="form.status == 2" type="success">已处理</el-tag>

          <!-- 已审核(未通过) -->
          <el-tag v-else-if="form.status == 3" type="warning">已审核(未通过)</el-tag>

          <!-- 未知状态 -->
          <el-tag v-else type="info">未知</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="处理人ID">
          {{ form.handleId || '暂未处理' }}
        </el-descriptions-item>
        <el-descriptions-item label="处理时间">
          {{ form.handleTime || '暂未处理' }}
        </el-descriptions-item>
        <el-descriptions-item label="处理备注">
          {{ form.handleRemark || '暂无备注' }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ form.createTime || '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="更新时间">
          {{ form.updateTime || '--' }}
        </el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="viewOpen = false">关闭</el-button>
      </div>
    </el-dialog>

<!--    审核-->
    <el-dialog
      :title="'救助详情'"
      :visible.sync="viewOpenAudit"
      width="600px"
      append-to-body
    >
      <el-descriptions
        :column="1"
        border
        title="基础信息"
        style="margin-bottom: 20px;"
      >
        <el-descriptions-item label="上报人">
          {{ form.reporterName || '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="上报人电话">
          {{ form.reporterPhone || '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="发现地点">
          {{ form.location || '--'}}
        </el-descriptions-item>
        <el-descriptions-item label="现场图片">
          <image-preview
            v-if="form.images"
            :src="form.images"
            :width="80"
            :height="80"
            style="border-radius: 4px;"
          />
          <span v-else>--</span>
        </el-descriptions-item>
        <el-descriptions-item label="宠物描述">
          {{ form.petDesc || '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ form.createTime || '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="更新时间">
          {{ form.updateTime || '--' }}
        </el-descriptions-item>
      </el-descriptions>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
          <el-form-item label="处理状态" prop="status">
            <el-select v-model="form.status" placeholder="请选择处理状态">
              <el-option label="已审核(处理中)" :value="1"></el-option>
              <el-option label="已审核(未通过)" :value="3"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="处理时间" prop="handleTime">
            <el-date-picker clearable
                            v-model="form.handleTime"
                            type="date"
                            value-format="yyyy-MM-dd HH:mm:ss"
                            placeholder="请选择处理时间">
            </el-date-picker>
          </el-form-item>
          <el-form-item label="处理备注" prop="handleRemark">
            <el-input v-model="form.handleRemark" type="textarea" placeholder="请输入处理备注" />
          </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="viewOpenAudit = false">关闭</el-button>
      </div>
    </el-dialog>

<!--   通知模板 -->
    <el-dialog :title="title" :visible.sync="rescueNoticeOpen" width="600px" append-to-body>
      <el-select
        v-model="noticeForm.templateId"
        clearable
        @change="handleTemplateChange"
        placeholder="请选择使用的模板">
        <el-option
          v-for="item in templateListOptions"
          :key="item.templateId"
          :label="item.templateName"
          :value="item.templateId">
        </el-option>
      </el-select>
      <el-divider></el-divider>
      <el-form ref="noticeForm" :model="noticeForm" :rules="noticeRules" label-width="100px">
        <el-form-item label="通知标题">
          <el-input v-model="noticeForm.title"></el-input>
        </el-form-item>
        <el-form-item label="通知内容">
          <editor v-model="noticeForm.content" :min-height="180"/>
        </el-form-item>
      </el-form>
      <!-- 添加按钮区域 -->
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="sendNotice" :loading="noticeLoading" >发送通知</el-button>
        <el-button @click="cancelNotice">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listRescue, getRescue, delRescue, addRescue, updateRescue } from "@/api/pet/rescue"
import { listTemplate, sendNoticeByTemplate } from "@/api/pet/template"
import {addUserNotice} from "../../../api/pet/userNotice";

export default {
  name: "Rescue",
  dicts: ['pet_rescue_status'],
  data() {
    return {
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
      // 救助登记表格数据
      rescueList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查看详情对话框
      viewOpen: false,
      // 审核对话框
      viewOpenAudit: false,
      // 通知对话框显示控制
      rescueNoticeOpen: false,
      // 通知发送加载状态
      noticeLoading: false,
      // 模板列表
      templateListOptions: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 5,
        reporterName: null,
        reporterPhone: null,
        location: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 通知复合表单
      noticeForm: {
        rescueId: null,      // 救助记录ID
        userId: null,        // 用户ID
        status: null,        // 救助状态
        templateId: null,     // 模板ID
        reporterName: null,   // 用户姓名
        title: '',           // 通知标题
        content: ''          // 通知内容
      },
      // 通知
      notices:{

      },
      // 表单校验
      rules: {
        reporterName: [
          { required: true, message: "上报人姓名不能为空", trigger: "blur" }
        ],
        reporterPhone: [
          { required: true, message: "上报人电话不能为空", trigger: "blur" }
        ],
        petDesc: [
          { required: true, message: "宠物描述不能为空", trigger: "blur" }
        ],
        location: [
          { required: true, message: "发现地点不能为空", trigger: "blur" }
        ],
        images: [
          { required: true, message: "现场图片不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "处理状态不能为空", trigger: "change" }
        ],
        handleTime:  [
          { required: true, message: "处理状态不能为空", trigger: "date" }
        ],
        handleRemark:  [
          { required: true, message: "处理状态不能为空", trigger: "blur" }
        ]
      },
      // 通知表单验证规则
      noticeRules: {
        title: [
          { required: true, message: "请输入通知标题", trigger: "blur" },
          { min: 2, max: 100, message: "标题长度在2到100个字符之间", trigger: "blur" }
        ],
        content: [
          { required: true, message: "请输入通知内容", trigger: "blur" },
          { min: 5, message: "内容至少5个字符", trigger: "blur" }
        ]
      }


    }
  },
  created() {
    this.getList()
    this.getTemplateList()
  },
  methods: {
    /** 查询救助登记列表 */
    getList() {
      this.loading = true
      listRescue(this.queryParams).then(response => {
        this.rescueList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.viewOpen = false
      this.viewOpenAudit = false
      this.rescueNoticeOpen = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        rescueId: null,
        userId: null,
        reporterName: null,
        reporterPhone: null,
        petDesc: null,
        location: null,
        images: null,
        status: 0,
        handleTime: null,
        handleId: null,
        handleRemark: null,
        createTime: null,
        createBy: null,
        updateTime: null,
        updateBy: null,
        isDeleted: null
      }
      this.resetForm("form")
      this.resetNoticeForm()
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
      this.ids = selection.map(item => item.rescueId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加救助登记"
    },
    /** 查看按钮操作 */
    handleView(row) {
      this.reset()
      const rescueId = row.rescueId|| this.ids
      getRescue(rescueId).then(response => {
        this.form = response.data
        this.viewOpen = true
        this.title = "查看宠物信息"
      }).catch(error => {
        console.error('获取救助详情失败:', error)
        this.$message.error("获取数据失败")
        this.viewOpen = false
      })
    },
    /** 审核 */
    handleAudit(row) {
      this.reset()
      const rescueId = row.rescueId|| this.ids
      getRescue(rescueId).then(response => {
        this.form = response.data
        this.form.status = 1;
        this.viewOpenAudit = true
        this.title = "审核信息"
      })
    },
    /** 处理 */
    handleComplete(row) {
      this.$confirm('确定标记为"已处理"吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        beforeClose: (action, instance, done) => {
          if (action === 'confirm') {
            instance.confirmButtonLoading = true
            instance.confirmButtonText = '处理中...'

            // 准备更新数据
            const updateData = {
              rescueId: row.rescueId,
              status: 2, // 直接设置为已处理状态
            }

            updateRescue(updateData).then(response => {
              this.$message.success("标记为已处理成功")
              this.getList() // 刷新列表
              done()
            }).catch(error => {
              console.error('标记失败:', error)
              this.$message.error("操作失败")
              done()
            }).finally(() => {
              instance.confirmButtonLoading = false
              instance.confirmButtonText = '确定'
            })
          } else {
            done()
          }
        }
      }).catch(() => {})
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const rescueId = row.rescueId || this.ids
      getRescue(rescueId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改救助登记"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.rescueId != null) {
            updateRescue(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.viewOpenAudit = false
              this.getList()
            })
          } else {
            addRescue(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.viewOpenAudit = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const rescueIds = row.rescueId || this.ids
      this.$modal.confirm('是否确认删除救助登记编号为"' + rescueIds + '"的数据项？').then(function() {
        return delRescue(rescueIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 查询模板列表 */
    getTemplateList() {
      const query = {
        pageNum: 1,
        pageSize: 1000,
        status: 0
      }
      listTemplate(query).then(response => {
        this.templateListOptions = response.rows.filter(template => {
          // 检查模板名称或内容是否包含"救助"
          return template.templateName.includes('救助') ||
            (template.content && template.content.includes('救助'))
        })
        if (this.templateListOptions.length === 0) {
          console.warn('没有找到包含"救助"的模板')
          this.$message.warning('没有找到救助相关的模板')
        }
      }).catch(error => {
        console.error('获取模板列表失败:', error)
        this.$message.error("获取模板列表失败")
      })
    },

    /** 打开通知对话框 */
    handleNotice(row) {
      this.noticeForm.rescueId = row.rescueId
      this.noticeForm.userId = row.userId
      this.noticeForm.status = row.status
      this.noticeForm.reporterName = row.reporterName
      if (row.status == 0) {
        const selectedTemplate = this.templateListOptions.find(template => template.templateId === 11)
        this.noticeForm.templateId = 11
        if (selectedTemplate) {
          this.noticeForm.title = selectedTemplate.title || ''

          // 替换模板中的占位符
          let content = selectedTemplate.content || ''
          content = this.replaceTemplateVariables(content)
          this.noticeForm.content = content
        }
      }
      if (row.status == 1){
        const selectedTemplate = this.templateListOptions.find(template => template.templateId === 13)
        this.noticeForm.templateId = 13
        if (selectedTemplate) {
          this.noticeForm.title = selectedTemplate.title || ''

          // 替换模板中的占位符
          let content = selectedTemplate.content || ''
          content = this.replaceTemplateVariables(content)
          this.noticeForm.content = content
        }
      }
      if (row.status == 2){
        const selectedTemplate = this.templateListOptions.find(template => template.templateId === 18)
        this.noticeForm.templateId = 18
        if (selectedTemplate) {
          this.noticeForm.title = selectedTemplate.title || ''

          // 替换模板中的占位符
          let content = selectedTemplate.content || ''
          content = this.replaceTemplateVariables(content)
          this.noticeForm.content = content
        }
      }
      if (row.status == 3){
        const selectedTemplate = this.templateListOptions.find(template => template.templateId === 12)
        this.noticeForm.templateId = 12
        if (selectedTemplate) {
          this.noticeForm.title = selectedTemplate.title || ''

          // 替换模板中的占位符
          let content = selectedTemplate.content || ''
          content = this.replaceTemplateVariables(content)
          this.noticeForm.content = content
        }
      }
      this.rescueNoticeOpen = true
    },

    /** 处理模板选择变化 */
    handleTemplateChange(templateId) {
      if (templateId) {
        const selectedTemplate = this.templateListOptions.find(template => template.templateId === templateId)
        if (selectedTemplate) {
          this.noticeForm.title = selectedTemplate.title || ''

          // 替换模板中的占位符
          let content = selectedTemplate.content || ''
          content = this.replaceTemplateVariables(content)
          this.noticeForm.content = content
        }
      } else {
        this.noticeForm.title = ''
        this.noticeForm.content = ''
      }
    },
    /** 替换模板变量 */
    replaceTemplateVariables(content) {
      if (!content) return ''

      const replacements = {
        '{user_name}': this.noticeForm.reporterName || '未知用户',
        // '{address}': this.form.location || '未知地点',
      }

      let replacedContent = content
      for (const [key, value] of Object.entries(replacements)) {
        const regex = new RegExp(key.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'), 'g')
        replacedContent = replacedContent.replace(regex, value)
      }

      return replacedContent
    },

    /** 发送通知 */
    sendNotice() {
      this.$refs['noticeForm'].validate(valid => {
        if (valid) {
          this.noticeLoading = true

          const noticeData = {
            rescueId: this.noticeForm.rescueId,
            templateId: this.noticeForm.templateId,
            title: this.noticeForm.title,
            content: this.noticeForm.content,
            userId: this.noticeForm.userId
          }

          const UserNoticeVo = {
            noticeId: 0,
            userId: this.noticeForm.userId
          }

          sendNoticeByTemplate(noticeData).then(response => {
            UserNoticeVo.noticeId = response
            addUserNotice(UserNoticeVo).then(response => {
              this.$message.success("通知发送成功")
              this.rescueNoticeOpen = false
              this.resetNoticeForm()
            }).catch(error => {
              console.error('发送通知失败:', error)
              this.$message.error("通知发送失败")
            })
          }).catch(error => {
            console.error('发送通知失败:', error)
            this.$message.error("通知发送失败")
          }).finally(() => {
            this.noticeLoading = false
          })
        } else {
          this.$message.warning('请填写完整的通知信息')
        }
      })
    },
    /** 取消通知 */
    cancelNotice() {
      this.rescueNoticeOpen = false
      this.resetNoticeForm()
    },
    /** 重置通知表单 */
    resetNoticeForm() {
      this.noticeForm = {
        rescueId: null,
        userId: null,
        templateId: null,
        status: null,
        title: '',
        content: ''
      }
      if (this.$refs['noticeForm']) {
        this.$refs['noticeForm'].resetFields()
      }
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('pet/rescue/export', {
        ...this.queryParams
      }, `rescue_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
