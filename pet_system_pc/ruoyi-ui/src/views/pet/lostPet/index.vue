<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="主人姓名" prop="ownerName">
        <el-input
          v-model="queryParams.ownerName"
          placeholder="请输入主人姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="丢失主人电话" prop="ownerPhone">
        <el-input
          v-model="queryParams.ownerPhone"
          placeholder="请输入丢失主人电话"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="宠物姓名" prop="petName">
        <el-input
          v-model="queryParams.petName"
          placeholder="请输入宠物姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="物种" prop="species">
        <el-select v-model="queryParams.species" placeholder="请选择物种" clearable>
          <el-option
            v-for="dict in dict.type.pet_pet_species"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="品种" prop="breed">
        <el-input
          v-model="queryParams.breed"
          placeholder="请输入品种"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="丢失地点" prop="lostLocation">
        <el-input
          v-model="queryParams.lostLocation"
          placeholder="请输入丢失地点"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="丢失时间">
        <el-date-picker
          v-model="daterangeLostTime"
          style="width: 240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>
      <el-form-item label="宠物状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option
            v-for="dict in dict.type.pet_lost_pet_status"
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
          v-hasPermi="['pet:lostPet:add']"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['pet:lostPet:edit']"
        >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['pet:lostPet:remove']"
        >删除
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="lostPetList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="序号" align="center" type="index"/>
      <el-table-column label="主人姓名" align="center" prop="ownerName"/>
      <el-table-column label="丢失主人电话" align="center" prop="ownerPhone" width="130"/>
      <el-table-column label="宠物图片" align="center" prop="images" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.images" :width="50" :height="50"/>
        </template>
      </el-table-column>
      <el-table-column label="宠物姓名" align="center" prop="petName"/>
      <el-table-column label="物种" align="center" prop="species">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.pet_pet_species" :value="scope.row.species"/>
        </template>
      </el-table-column>
      <el-table-column label="品种" align="center" prop="breed"/>
      <el-table-column label="显著特征" align="center" prop="features"/>
      <el-table-column label="丢失地点" align="center" prop="lostLocation" show-overflow-tooltip/>
      <el-table-column label="丢失时间" align="center" prop="lostTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.lostTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="酬谢金额" align="center" prop="reward">
        <template slot-scope="scope">
          {{ scope.row.reward + "￥" }}
        </template>
      </el-table-column>
      <el-table-column label="宠物状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.pet_lost_pet_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['pet:lostPet:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['pet:lostPet:remove']"
          >删除
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

    <!-- 添加或修改宠物丢失对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="900px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <!-- 左侧表单列 -->
            <el-form-item label="用户id" prop="userId">
              <el-input v-model="form.userId" placeholder="请输入用户id"/>
            </el-form-item>
            <el-form-item label="主人姓名" prop="ownerName">
              <el-input v-model="form.ownerName" placeholder="请输入主人姓名"/>
            </el-form-item>
            <el-form-item label="丢失主人电话" prop="ownerPhone">
              <el-input v-model="form.ownerPhone" placeholder="请输入丢失主人电话"/>
            </el-form-item>
            <el-form-item label="宠物姓名" prop="petName">
              <el-input v-model="form.petName" placeholder="请输入宠物姓名"/>
            </el-form-item>
            <el-form-item label="物种" prop="species">
              <el-select v-model="form.species" placeholder="请选择物种">
                <el-option
                  v-for="dict in dict.type.pet_pet_species"
                  :key="dict.value"
                  :label="dict.label"
                  :value="parseInt(dict.value)">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="品种" prop="breed">
              <el-input v-model="form.breed" placeholder="请输入品种"/>
            </el-form-item>
            <el-form-item label="显著特征" prop="features">
              <el-input v-model="form.features" placeholder="请输入显著特征"/>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <!-- 右侧表单列 -->
            <el-form-item label="想说的话" prop="message">
              <el-input v-model="form.message" placeholder="请输入想说的话"/>
            </el-form-item>
            <el-form-item label="丢失地点" prop="lostLocation">
              <el-input v-model="form.lostLocation" placeholder="请输入丢失地点"/>
            </el-form-item>
            <el-form-item label="丢失时间" prop="lostTime">
              <el-date-picker clearable
                              v-model="form.lostTime"
                              type="date"
                              value-format="yyyy-MM-dd"
                              placeholder="请选择丢失时间">
              </el-date-picker>
            </el-form-item>
            <el-form-item label="酬谢金额" prop="reward">
              <el-input v-model="form.reward" placeholder="请输入酬谢金额"/>
            </el-form-item>
            <el-form-item label="宠物图片" prop="images">
              <image-upload v-model="form.images"/>
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态">
                <el-option
                  v-for="dict in dict.type.pet_lost_pet_status"
                  :key="dict.value"
                  :label="dict.label"
                  :value="parseInt(dict.value)">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
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
import {listLostPet, getLostPet, delLostPet, addLostPet, updateLostPet} from "@/api/pet/lostPet"
import {addUserNotice} from "../../../api/pet/userNotice";
import {listTemplate, sendNoticeByTemplate} from "../../../api/pet/template";

export default {
  name: "LostPet",
  dicts: ['pet_lost_pet_status', 'pet_pet_species'],
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
      // 宠物丢失表格数据
      lostPetList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 通知对话框
      rescueNoticeOpen: false,
      // 通知发送加载状态
      noticeLoading: false,
      // 模板列表
      templateListOptions: [],
      // 状态(0-寻找中，1-已找到)时间范围
      daterangeLostTime: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 5,
        userId: null,
        ownerName: null,
        ownerPhone: null,
        petName: null,
        species: null,
        breed: null,
        lostLocation: null,
        lostTime: null,
        status: null,
      },
      // 通知复合表单
      noticeForm: {
        lostId: null,
        userId: null,
        status: null,
        templateId: null,
        ownerName: null,   // 用户姓名
        lostLocation: null,
        lostTime: null,
        petName: null,
        title: '',           // 通知标题
        content: ''          // 通知内容
      },
      // 通知
      notices:{

      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        ownerName: [
          {required: true, message: "主人姓名不能为空", trigger: "blur"}
        ],
        ownerPhone: [
          {required: true, message: "丢失主人电话不能为空", trigger: "blur"}
        ],
        petName: [
          {required: true, message: "宠物姓名不能为空", trigger: "blur"}
        ],
        species: [
          {required: true, message: "物种(0-猫,1-狗,2-其他)不能为空", trigger: "change"}
        ],
        breed: [
          {required: true, message: "品种不能为空", trigger: "blur"}
        ],
        features: [
          {required: true, message: "显著特征不能为空", trigger: "blur"}
        ],
        message: [
          {required: true, message: "想说的话不能为空", trigger: "blur"}
        ],
        lostLocation: [
          {required: true, message: "丢失地点不能为空", trigger: "blur"}
        ],
        lostTime: [
          {required: true, message: "丢失时间不能为空", trigger: "blur"}
        ],
        images: [
          {required: true, message: "宠物图片不能为空", trigger: "blur"}
        ],
        status: [
          {required: true, message: "状态(0-寻找中，1-已找到)不能为空", trigger: "change"}
        ],
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
    /** 查询宠物丢失列表 */
    getList() {
      this.loading = true
      this.queryParams.params = {}
      if (null != this.daterangeLostTime && '' != this.daterangeLostTime) {
        this.queryParams.params["beginLostTime"] = this.daterangeLostTime[0]
        this.queryParams.params["endLostTime"] = this.daterangeLostTime[1]
      }
      listLostPet(this.queryParams).then(response => {
        this.lostPetList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.rescueNoticeOpen = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        lostId: null,
        userId: null,
        ownerName: null,
        ownerPhone: null,
        petName: null,
        species: null,
        breed: null,
        features: null,
        message: null,
        lostLocation: null,
        lostTime: null,
        reward: null,
        images: null,
        status: null,
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
      this.daterangeLostTime = []
      this.resetForm("queryForm")
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.lostId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加宠物丢失"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const lostId = row.lostId || this.ids
      getLostPet(lostId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改宠物丢失"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.lostId != null) {
            updateLostPet(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addLostPet(this.form).then(response => {
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
      const lostIds = row.lostId || this.ids
      this.$modal.confirm('是否确认删除宠物丢失编号为"' + lostIds + '"的数据项？').then(function () {
        return delLostPet(lostIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {
      })
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
          return template.templateName.includes('寻宠') ||
            (template.content && template.content.includes('寻宠'))
        })
        if (this.templateListOptions.length === 0) {
          console.warn('没有找到包含"寻宠"的模板')
          this.$message.warning('没有找到寻宠相关的模板')
        }
      }).catch(error => {
        console.error('获取模板列表失败:', error)
        this.$message.error("获取模板列表失败")
      })
    },
    /** 打开通知对话框 */
    handleNotice(row) {
      this.noticeForm.lostId = row.lostId
      this.noticeForm.userId = row.userId
      this.noticeForm.status = row.status
      this.noticeForm.ownerName = row.ownerName
      this.noticeForm.petName = row.petName
      this.noticeForm.lostTime = row.lostTime
      this.noticeForm.lostLocation = row.lostLocation
      if (row.status == 0) {
        const selectedTemplate = this.templateListOptions.find(template => template.templateId === 19)
        this.noticeForm.templateId = 19
        if (selectedTemplate) {
          this.noticeForm.title = selectedTemplate.title || ''

          // 替换模板中的占位符
          let content = selectedTemplate.content || ''
          content = this.replaceTemplateVariables(content)
          this.noticeForm.content = content
        }
      }
      if (row.status == 1){
        const selectedTemplate = this.templateListOptions.find(template => template.templateId === 20)
        this.noticeForm.templateId = 20
        if (selectedTemplate) {
          this.noticeForm.title = selectedTemplate.title || ''

          // 替换模板中的占位符
          let content = selectedTemplate.content || ''
          content = this.replaceTemplateVariables(content)
          this.noticeForm.content = content
        }
      }
      if (row.status == 2){
        const selectedTemplate = this.templateListOptions.find(template => template.templateId === 21)
        this.noticeForm.templateId = 21
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
        '{owner_name}': this.noticeForm.ownerName || '未知用户',
        '{pet_name}': this.noticeForm.petName || '未知宠物',
        '{lost_time}': this.noticeForm.lostTime || '未知时间',
        '{lost_location}': this.noticeForm.lostLocation || '未知地点',
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
            lostId: this.noticeForm.lostId,
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
    }
  }
}
</script>
