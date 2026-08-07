<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="90px">
      <el-form-item label="申请人姓名" prop="applicationName">
        <el-input
          v-model="queryParams.applicationName"
          placeholder="请输入申请人姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="申请人电话" prop="applicationPhone">
        <el-input
          v-model="queryParams.applicationPhone"
          placeholder="请输入申请人电话"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="申请状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择申请状态" clearable>
          <el-option
            v-for="dict in dict.type.pet_adoption_status"
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
          v-hasPermi="['pet:adoption:add']"
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
          v-hasPermi="['pet:adoption:edit']"
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
          v-hasPermi="['pet:adoption:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="adoptionList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" align="center" prop="adoptionId" type="index"/>
      <el-table-column label="申请人姓名" align="center" prop="applicationName" />
      <el-table-column label="申请人电话" align="center" prop="applicationPhone" />
      <el-table-column label="养宠经验" align="center" prop="experience" />
      <el-table-column label="领养理由" align="center" prop="reason" />
      <!-- 宠物详情列 -->
      <el-table-column label="宠物详情" align="center" width="120">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-search"
            @click="handlePetView(scope.row)"
          >查看宠物详情</el-button>
        </template>
      </el-table-column>
      <el-table-column label="申请状态" align="center" prop="status">
        <template slot-scope="scope">
          <!-- 待审核 -->
          <el-tag v-if="scope.row.status == 0" type="warning">待审核</el-tag>
          <!-- 已审核（通过） -->
          <el-tag v-else-if="scope.row.status == 1" type="success">已审核（通过）</el-tag>
          <!-- 已审核（未通过） -->
          <el-tag v-else-if="scope.row.status == 2" type="danger">已审核（未通过）</el-tag>
          <!-- 已领养 -->
          <el-tag v-else-if="scope.row.status == 3" type="info">已领养</el-tag>
          <!-- 未知状态 -->
          <el-tag v-else type="info">未知</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="280">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
          >查看详情
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['pet:adoption:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['pet:adoption:remove']"
          >删除</el-button>
          <!-- 通知按钮 -->
          <el-button
            size="mini"
            type="text"
            icon="el-icon-phone-outline"
            @click="handleNotice(scope.row)"
            v-hasPermi="['pet:notice:add']"
            style="color: #409EFF;"
          >通知
          </el-button>
          <!-- 审核按钮：待审核状态显示 -->
          <el-button
            v-if="scope.row.status == 0"
            size="mini"
            type="text"
            icon="el-icon-receiving"
            @click="handleAudit(scope.row)"
            v-hasPermi="['pet:adoption:audit']"
            style="color: #dc3041;"
          >审核
          </el-button>
          <!-- 通过按钮：待审核状态显示 -->
          <el-button
            v-if="scope.row.status == 0"
            size="mini"
            type="text"
            icon="el-icon-check"
            @click="handlePass(scope.row)"
            v-hasPermi="['pet:adoption:pass']"
            style="color: #67C23A;"
          >通过
          </el-button>
          <!-- 驳回按钮：待审核状态显示 -->
          <el-button
            v-if="scope.row.status == 0"
            size="mini"
            type="text"
            icon="el-icon-close"
            @click="handleReject(scope.row)"
            v-hasPermi="['pet:adoption:reject']"
            style="color: #F56C6C;"
          >驳回
          </el-button>
          <!-- 完成领养按钮：已通过状态显示 -->
          <el-button
            v-if="scope.row.status == 1"
            size="mini"
            type="text"
            icon="el-icon-finished"
            @click="handleComplete(scope.row)"
            v-hasPermi="['pet:adoption:complete']"
            style="color: #409EFF;"
          >完成领养
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

    <!-- 添加或修改宠物领养对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="申请宠物id" prop="petId">
          <el-input v-model="form.petId" placeholder="请输入申请宠物id" />
        </el-form-item>
        <el-form-item label="申请人id" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入申请人id" />
        </el-form-item>
        <el-form-item label="申请人姓名" prop="applicationName">
          <el-input v-model="form.applicationName" placeholder="请输入申请人姓名" />
        </el-form-item>
        <el-form-item label="申请人电话" prop="applicationPhone">
          <el-input v-model="form.applicationPhone" placeholder="请输入申请人电话" />
        </el-form-item>
        <el-form-item label="养宠经验" prop="experience">
          <el-input v-model="form.experience" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="领养理由" prop="reason">
          <el-input v-model="form.reason" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="申请状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择申请状态" style="width: 150px">
            <el-option
              v-for="dict in dict.type.pet_adoption_status"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
              :disabled="true"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog :title="'领养申请详情'" :visible.sync="viewOpen" width="600px" append-to-body>
      <el-descriptions
        :column="1"
        border
        title="申请信息"
        style="margin-bottom: 20px;"
      >
        <el-descriptions-item label="申请人">
          {{ viewForm.applicationName || '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="申请人电话">
          {{ viewForm.applicationPhone || '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="养宠经验">
          {{ viewForm.experience || '--'}}
        </el-descriptions-item>
        <el-descriptions-item label="领养理由">
          {{ viewForm.reason || '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="申请状态">
          <!-- 待审核 -->
          <el-tag v-if="viewForm.status == 0" type="warning">待审核</el-tag>
          <!-- 已审核（通过） -->
          <el-tag v-else-if="viewForm.status == 1" type="success">已审核（通过）</el-tag>
          <!-- 已审核（未通过） -->
          <el-tag v-else-if="viewForm.status == 2" type="danger">已审核（未通过）</el-tag>
          <!-- 已领养 -->
          <el-tag v-else-if="viewForm.status == 3" type="info">已领养</el-tag>
          <!-- 未知状态 -->
          <el-tag v-else type="info">未知</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审核人ID">
          {{ viewForm.reviewerId || '暂未审核' }}
        </el-descriptions-item>
        <el-descriptions-item label="审核时间">
          {{ viewForm.reviewTime || '暂未审核' }}
        </el-descriptions-item>
        <el-descriptions-item label="审核意见">
          {{ viewForm.reviewRemark || '暂无意见' }}
        </el-descriptions-item>
        <el-descriptions-item label="实际领养时间">
          {{ viewForm.adoptTime || '暂未领养' }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ viewForm.createTime || '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="更新时间">
          {{ viewForm.updateTime || '--' }}
        </el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="viewOpen = false">关闭</el-button>
      </div>
    </el-dialog>

    <!-- 审核对话框 -->
    <el-dialog
      :title="'审核领养申请'"
      :visible.sync="auditOpen"
      width="500px"
      append-to-body
    >
      <el-descriptions
        :column="1"
        border
        title="申请信息"
        style="margin-bottom: 20px;"
      >
        <el-descriptions-item label="申请人">
          {{ form.applicationName || '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="申请人电话">
          {{ form.applicationPhone || '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="养宠经验">
          {{ form.experience || '--'}}
        </el-descriptions-item>
        <el-descriptions-item label="领养理由">
          {{ form.reason || '--' }}
        </el-descriptions-item>
      </el-descriptions>
      <el-form ref="auditForm" :model="auditForm" :rules="auditRules" label-width="80px">
        <el-form-item label="审核状态" prop="status">
          <el-select v-model="auditForm.status" placeholder="请选择审核状态">
            <el-option label="已审核（通过）" :value="1"></el-option>
            <el-option label="已审核（未通过）" :value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="审核时间" prop="reviewTime">
          <el-date-picker clearable
                          v-model="auditForm.reviewTime"
                          type="datetime"
                          value-format="yyyy-MM-dd HH:mm:ss"
                          placeholder="请选择审核时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="审核意见" prop="reviewRemark">
          <el-input v-model="auditForm.reviewRemark" type="textarea" placeholder="请输入审核意见" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitAudit">确 定</el-button>
        <el-button @click="auditOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 查看宠物详情对话框 -->
    <el-dialog
      :title="'宠物详情'"
      :visible.sync="petViewOpen"
      width="700px"
      append-to-body
    >
      <el-descriptions
        :column="2"
        border
        title="宠物基本信息"
        style="margin-bottom: 20px;"
      >
        <el-descriptions-item label="宠物ID">
          {{ petForm.petId || '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="宠物名称">
          {{ petForm.name || '--' }}
        </el-descriptions-item>

        <!-- 使用字典标签显示物种 -->
        <el-descriptions-item label="物种">
          <dict-tag v-if="petForm.species !== undefined && petForm.species !== null"
                    :options="dict.type.pet_pet_species"
                    :value="petForm.species.toString()" />
          <span v-else>--</span>
        </el-descriptions-item>

        <el-descriptions-item label="品种">
          {{ petForm.breed || '--' }}
        </el-descriptions-item>

        <!-- 使用字典标签显示性别 -->
        <el-descriptions-item label="性别">
          <dict-tag v-if="petForm.gender !== undefined && petForm.gender !== null"
                    :options="dict.type.pet_pet_gender"
                    :value="petForm.gender.toString()" />
          <span v-else>--</span>
        </el-descriptions-item>

        <el-descriptions-item label="年龄(岁)">
          {{ petForm.age || '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="健康状况">
          {{ petForm.healthInfo || '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="是否绝育">
          <el-tag v-if="petForm.neuter == 1" type="success">是</el-tag>
          <el-tag v-else-if="petForm.neuter == 0" type="info">否</el-tag>
          <span v-else>--</span>
        </el-descriptions-item>
        <el-descriptions-item label="是否免疫">
          <el-tag v-if="petForm.immunity == 1" type="success">是</el-tag>
          <el-tag v-else-if="petForm.immunity == 0" type="info">否</el-tag>
          <span v-else>--</span>
        </el-descriptions-item>
        <el-descriptions-item label="是否驱虫">
          <el-tag v-if="petForm.insectRepellent == 1" type="success">是</el-tag>
          <el-tag v-else-if="petForm.insectRepellent == 0" type="info">否</el-tag>
          <span v-else>--</span>
        </el-descriptions-item>
        <el-descriptions-item label="驱虫间隔(天)">
          {{ petForm.dewormingInterval || '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="最近驱虫时间">
          {{ petForm.insectRepellentLastTime || '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="所在位置" :span="2">
          {{ petForm.location || '--' }}
        </el-descriptions-item>

        <!-- 使用字典标签显示宠物状态 -->
        <el-descriptions-item label="宠物状态">
          <dict-tag v-if="petForm.status !== undefined && petForm.status !== null"
                    :options="dict.type.pet_pet_status"
                    :value="petForm.status.toString()" />
          <span v-else>--</span>
        </el-descriptions-item>

        <el-descriptions-item label="所属部门">
          {{ petForm.deptId || '--' }}
        </el-descriptions-item>
      </el-descriptions>

      <el-descriptions
        :column="1"
        border
        title="宠物描述"
        style="margin-bottom: 20px;"
      >
        <el-descriptions-item>
          {{ petForm.description || '暂无描述' }}
        </el-descriptions-item>
      </el-descriptions>

      <el-descriptions
        :column="1"
        border
        title="宠物图片"
        v-if="petForm.images"
      >
        <el-descriptions-item>
          <image-preview
            :src="petForm.images"
            :width="200"
            :height="200"
            style="border-radius: 4px;"
          />
        </el-descriptions-item>
      </el-descriptions>

      <div slot="footer" class="dialog-footer">
        <el-button @click="petViewOpen = false">关闭</el-button>
      </div>
    </el-dialog>

    <!-- 发送通知弹框 -->
    <el-dialog :title="title" :visible.sync="petNoticeOpen" width="600px" append-to-body>
      <el-select
        v-model=" noticeForm.templateId"
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
      <el-form ref="noticeForm" :model="noticeForm" :rules="rules" label-width="100px">
        <el-form-item label="通知标题">
          <el-input v-model="noticeForm.title"></el-input>
        </el-form-item>
        <el-form-item label="通知内容">
          <editor v-model="noticeForm.content" :min-height="180"/>
        </el-form-item>
      </el-form>
      <!-- 添加按钮区域 -->
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="sendNotice" :loading="noticeLoading">发送通知</el-button>
        <el-button @click="cancelNotice">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listAdoption, getAdoption, delAdoption, addAdoption, updateAdoption } from "@/api/pet/adoption"
import { getPet } from "@/api/pet/pet" // 假设有获取宠物详情的API
import { listTemplate } from "@/api/pet/template"
import { sendNoticeByTemplate } from "@/api/pet/template"
import {addUserNotice} from "../../../api/pet/userNotice";
import {listPetShop} from "../../../api/pet/pet";

export default {
  name: "Adoption",
  dicts: ['pet_adoption_status', 'pet_pet_species', 'pet_pet_gender', 'pet_pet_status'], // 添加宠物相关字典
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
      // 宠物领养表格数据
      adoptionList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否显示查看详情弹出层
      viewOpen: false,
      // 是否显示审核弹出层
      auditOpen: false,
      // 是否显示宠物详情弹出层
      petViewOpen: false,
      // 是否显示通知弹出层
      petNoticeOpen: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 5,
        applicationName: null,
        applicationPhone: null,
        experience: null,
        reason: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 查看详情表单参数
      viewForm: {},
      // 宠物详情表单参数
      petForm: {
        petId: null,
        name: null,
        images: null,
        species: null,
        breed: null,
        gender: null,
        age: null,
        healthInfo: null,
        neuter: null,
        immunity: null,
        insectRepellent: null,
        dewormingInterval: null,
        insectRepellentLastTime: null,
        description: null,
        status: null,
        location: null,
        deptId: null
      },
      // 审核表单参数
      auditForm: {
        adoptionId: null,
        status: null,
        reviewTime: null,
        reviewRemark: null,
        reviewerId: null
      },
      // 通知复合
      noticeForm: {
        adoptionId: null,      // 领养记录ID
        userId: null,        // 用户ID
        petId: null,         //宠物ID
        shopName: null,      //店铺名称
        status: null,        // 领养状态状态
        templateId: null,     // 模板ID
        applicationName: null,   // 用户姓名
        title: '',           // 通知标题
        content: ''          // 通知内容
      },
      // 通知发送加载状态
      noticeLoading: false,
      // 通知模板列表
      templateListOptions: [],
      // 表单校验
      rules: {
        applicationName: [
          { required: true, message: "申请人姓名不能为空", trigger: "blur" }
        ],
        applicationPhone: [
          { required: true, message: "申请人电话不能为空", trigger: "blur" }
        ],
        experience: [
          { required: true, message: "养宠经验不能为空", trigger: "blur" }
        ],
        reason: [
          { required: true, message: "领养理由不能为空", trigger: "blur" }
        ],
      },
      // 审核表单校验
      auditRules: {
        status: [
          { required: true, message: "审核状态不能为空", trigger: "change" }
        ],
        reviewRemark: [
          { required: true, message: "审核意见不能为空", trigger: "blur" }
        ]
      },
      petShopList: []
    }
  },
  created() {
    this.getList()
    this.getTemplateList()
    this.getPetShopList()
    // 添加调试代码查看字典是否加载
    this.$nextTick(() => {
      console.log('加载的字典:', {
        pet_pet_species: this.dict.type.pet_pet_species,
        pet_pet_gender: this.dict.type.pet_pet_gender,
        pet_pet_status: this.dict.type.pet_pet_status
      })
    })
  },
  methods: {

    /** 查询宠物领养列表 */
    getList() {
      this.loading = true
      listAdoption(this.queryParams).then(response => {
        this.adoptionList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.viewOpen = false
      this.auditOpen = false
      this.petViewOpen = false
      this.petNoticeOpen = false
      this.reset()
    },
    getPetShopList(){
      this.loading = true
      listPetShop().then(response => {
        this.petShopList = response.rows
      })
      this.loading = false
    },
    // 表单重置
    reset() {
      this.form = {
        adoptionId: null,
        petId: null,
        userId: null,
        applicationName: null,
        applicationPhone: null,
        experience: null,
        reason: null,
        status: 0,
        reviewerId: null,
        reviewTime: null,
        reviewRemark: null,
        adoptTime: null,
        // 通知相关字段
        templateId: null,
        title: null,
        content: null
      }
      this.resetForm("form")
      this.resetNoticeForm()
    },
    // 宠物表单重置
    resetPetForm() {
      this.petForm = {
        petId: null,
        name: null,
        images: null,
        species: null,
        breed: null,
        gender: null,
        age: null,
        healthInfo: null,
        neuter: null,
        immunity: null,
        insectRepellent: null,
        dewormingInterval: null,
        insectRepellentLastTime: null,
        description: null,
        status: null,
        location: null,
        deptId: null
      }
    },
    // 审核表单重置
    resetAuditForm() {
      this.auditForm = {
        adoptionId: null,
        status: null,
        reviewTime: null,
        reviewRemark: null,
        reviewerId: null
      }
      if (this.$refs.auditForm) {
        this.$refs.auditForm.resetFields()
      }
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
      this.ids = selection.map(item => item.adoptionId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加宠物领养"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const adoptionId = row.adoptionId || this.ids
      getAdoption(adoptionId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改宠物领养"
      })
    },
    /** 查看详情按钮操作 */
    handleView(row) {
      const adoptionId = row.adoptionId
      getAdoption(adoptionId).then(response => {
        this.viewForm = response.data
        this.viewOpen = true
      })
    },
    /** 查看宠物详情按钮操作 */
    handlePetView(row) {
      this.resetPetForm()

      // 先获取领养申请详情，从中获取petId
      const adoptionId = row.adoptionId
      getAdoption(adoptionId).then(response => {
        const adoptionData = response.data
        const petId = adoptionData.petId

        if (petId) {
          // 如果有petId，调用宠物详情API
          getPet(petId).then(petResponse => {
            this.petForm = petResponse.data
            this.petViewOpen = true

            // 调试：查看获取到的宠物数据和字典数据
            console.log('宠物数据:', this.petForm)
            console.log('物种字典数据:', this.dict.type.pet_pet_species)
            console.log('性别字典数据:', this.dict.type.pet_pet_gender)
            console.log('状态字典数据:', this.dict.type.pet_pet_status)
          }).catch(error => {
            console.error('获取宠物详情失败:', error)
            this.$message.error("获取宠物详情失败")
          })
        } else {
          this.$message.warning("该申请未关联宠物信息")
        }
      })
    },

    /** 审核按钮操作 */
    handleAudit(row) {
      this.resetAuditForm()
      const adoptionId = row.adoptionId
      getAdoption(adoptionId).then(response => {
        this.form = response.data
        this.auditForm.adoptionId = adoptionId
        this.auditForm.reviewerId = this.$store.state.user.userId
        this.auditOpen = true
      })
    },
    /** 通过按钮操作 */
    handlePass(row) {
      this.$confirm('确定通过该领养申请吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success',
        beforeClose: (action, instance, done) => {
          if (action === 'confirm') {
            instance.confirmButtonLoading = true
            instance.confirmButtonText = '处理中...'

            const updateData = {
              adoptionId: row.adoptionId,
              status: 1,
              reviewerId: this.$store.state.user.userId,
              reviewTime: new Date(),
              reviewRemark: '申请已通过'
            }

            updateAdoption(updateData).then(response => {
              this.$message.success("已通过申请")
              this.getList()
              done()
            }).catch(error => {
              console.error('操作失败:', error)
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
    /** 驳回按钮操作 */
    handleReject(row) {
      this.$confirm('确定驳回该领养申请吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        beforeClose: (action, instance, done) => {
          if (action === 'confirm') {
            instance.confirmButtonLoading = true
            instance.confirmButtonText = '处理中...'

            const updateData = {
              adoptionId: row.adoptionId,
              status: 2,
              reviewerId: this.$store.state.user.userId,
              reviewTime: new Date(),
              reviewRemark: '申请被驳回'
            }

            updateAdoption(updateData).then(response => {
              this.$message.success("已驳回申请")
              this.getList()
              done()
            }).catch(error => {
              console.error('操作失败:', error)
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
    /** 完成领养按钮操作 */
    handleComplete(row) {
      this.$confirm('确定标记为"已领养"吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success',
        beforeClose: (action, instance, done) => {
          if (action === 'confirm') {
            instance.confirmButtonLoading = true
            instance.confirmButtonText = '处理中...'

            const updateData = {
              adoptionId: row.adoptionId,
              status: 3,
              adoptTime: new Date()
            }

            updateAdoption(updateData).then(response => {
              this.$message.success("已标记为已领养")
              this.getList()
              done()
            }).catch(error => {
              console.error('操作失败:', error)
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


    /** 查询通知模板列表 */
    getTemplateList() {
      const query = {
        pageNum: 1,
        pageSize: 1000000,
        status: 0
      }
      listTemplate(query).then(response => {
        // 前端筛选领养相关的模板
        this.templateListOptions = response.rows.filter(template => {
          // 根据模板名称或内容判断是否与领养相关
          const templateName = template.templateName || '';
          const content = template.content || '';

          // 判断是否包含领养相关关键词
          const adoptionKeywords = ['领养', 'adoption', '宠物领养', '申请领养', '领养申请'];
          return adoptionKeywords.some(keyword =>
            templateName.includes(keyword) || content.includes(keyword)
          );
        });

        console.log('领养相关模板列表:', this.templateListOptions);
      })
    },

    /** 打开通知对话框 */
    handleNotice(row) {
      this.noticeForm.adoptionId = row.adoptionId
      this.noticeForm.userId = row.userId
      this.noticeForm.petId = row.petId
      this.noticeForm.status = row.status
      this.noticeForm.applicationName = row.applicationName

      getPet(row.petId).then(petResponse => {
          this.petForm = petResponse.data
      })
      const selectShop = this.petShopList.find(shop => shop.shopId = this.petForm.deptId )
      this.noticeForm.shopName = selectShop.shopName;
      if (row.status == 0) {
        const selectedTemplate = this.templateListOptions.find(template => template.templateId === 14)
        this.noticeForm.templateId = 14
        if (selectedTemplate) {
          this.noticeForm.title = selectedTemplate.title || ''

          // 替换模板中的占位符p
          let content = selectedTemplate.content || ''
          content = this.replaceTemplateVariables(content)
          this.noticeForm.content = content
        }
      }
      if (row.status == 1){
        const selectedTemplate = this.templateListOptions.find(template => template.templateId === 15)
        this.noticeForm.templateId = 15
        if (selectedTemplate) {
          this.noticeForm.title = selectedTemplate.title || ''

          // 替换模板中的占位符
          let content = selectedTemplate.content || ''
          content = this.replaceTemplateVariables(content)
          this.noticeForm.content = content
        }
      }
      if (row.status == 2){
        const selectedTemplate = this.templateListOptions.find(template => template.templateId === 16)
        this.noticeForm.templateId = 16
        if (selectedTemplate) {
          this.noticeForm.title = selectedTemplate.title || ''

          // 替换模板中的占位符
          let content = selectedTemplate.content || ''
          content = this.replaceTemplateVariables(content)
          this.noticeForm.content = content
        }
      }
      if (row.status == 3){
        const selectedTemplate = this.templateListOptions.find(template => template.templateId === 17)
        this.noticeForm.templateId = 17
        if (selectedTemplate) {
          this.noticeForm.title = selectedTemplate.title || ''

          // 替换模板中的占位符
          let content = selectedTemplate.content || ''
          content = this.replaceTemplateVariables(content)
          this.noticeForm.content = content
        }
      }
      this.petNoticeOpen = true
    },


    /** 处理模板选择变化 */
    handleTemplateChange(templateId) {
      if (templateId) {
        // 从模板列表中找到选中的模板
        const selectedTemplate = this.templateListOptions.find(template => template.templateId === templateId);
        if (selectedTemplate) {
          this.noticeForm.title = selectedTemplate.title || ''

          // 替换模板中的占位符
          let content = selectedTemplate.content || ''
          content = this.replaceTemplateVariables(content)
          this.noticeForm.content = content
        }
      } else {
        // 清空模板时重置内容
        this.noticeForm.title = ''
        this.noticeForm.content = ''
      }
    },
    /** 替换模板变量 */
    replaceTemplateVariables(content) {
      if (!content) return ''

      const replacements = {
        '{user_name}': this.noticeForm.applicationName || '未知用户',
        '{address}': this.noticeForm.shopName || '未知店铺',
        '{pet_name}': this.petForm.name || '未知宠物',
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
            adoptionId: this.noticeForm.adoptionId,
            petId: this.noticeForm.petId,
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
      this.petNoticeOpen = false;
      this.reset();
    },

    /** 重置通知表单 */
    resetNoticeForm() {
      this.noticeForm = {
        adoptionId: null,
        userId: null,
        petId: null,
        status: null,
        templateId: null,
        applicationName: null,
        title: '',
        content: ''
      }
      if (this.$refs['noticeForm']) {
        this.$refs['noticeForm'].resetFields()
      }
    },

    /** 提交审核 */
    submitAudit() {
      this.$refs["auditForm"].validate(valid => {
        if (valid) {
          if (!this.auditForm.reviewTime) {
            this.auditForm.reviewTime = new Date()
          }

          const updateData = {
            ...this.auditForm,
            reviewerId: this.$store.state.user.userId || this.auditForm.reviewerId
          }

          updateAdoption(updateData).then(response => {
            this.$modal.msgSuccess("审核成功")
            this.auditOpen = false
            this.getList()
          })
        }
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.adoptionId != null) {
            updateAdoption(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addAdoption(this.form).then(response => {
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
      const adoptionIds = row.adoptionId || this.ids
      this.$modal.confirm('是否确认删除宠物领养编号为"' + adoptionIds + '"的数据项？').then(function() {
        return delAdoption(adoptionIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('pet/adoption/export', {
        ...this.queryParams
      }, `adoption_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
