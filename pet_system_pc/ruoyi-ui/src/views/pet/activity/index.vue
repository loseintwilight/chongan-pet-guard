<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="活动标题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入活动标题"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="创办组织" prop="createOrganize">
        <el-input
          v-model="queryParams.createOrganize"
          placeholder="请输入创办组织"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="活动分类" prop="activityCategory">
        <el-select v-model="queryParams.activityCategory" placeholder="请选择活动分类" clearable>
          <el-option
            v-for="dict in dict.type.pet_activity_category"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="活动地点" prop="address">
        <el-input
          v-model="queryParams.address"
          placeholder="请输入活动地点"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="活动状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择活动状态" clearable>
          <el-option
            v-for="dict in dict.type.pet_activity_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="是否顶置" prop="isTop">
        <el-select v-model="queryParams.isTop" placeholder="请选择是否顶置" clearable>
          <el-option
            v-for="dict in dict.type.pet_activity_is_top"
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
          v-hasPermi="['pet:activity:add']"
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
          v-hasPermi="['pet:activity:edit']"
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
          v-hasPermi="['pet:activity:remove']"
        >删除
        </el-button>
      </el-col>

      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="activityList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="序号" align="center" prop="activityId" type="index" width="50"/>
      <el-table-column label="活动标题" align="center" prop="title" show-overflow-tooltip/>
      <!--      <el-table-column label="活动详情富文本" align="center" prop="content" />-->
      <el-table-column label="图片" align="center" prop="images" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.images" :width="50" :height="50"/>
        </template>
      </el-table-column>
      <el-table-column label="时间" align="center" prop="startSignTime" width="100">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startSignTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创办组织" align="center" prop="createOrganize"/>
      <el-table-column label="活动分类" align="center" prop="activityCategory">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.pet_activity_category" :value="scope.row.activityCategory"/>
        </template>
      </el-table-column>
      <el-table-column label="活动地点" align="center" prop="address"/>
      <el-table-column label="活动状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.pet_activity_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="报名情况" align="center" prop="count">
        <template slot-scope="scope">
          <!-- 显示当前报名人数和总人数限制，并在超限时标红 -->
          <span
            :style="{
                color: scope.row.count >= scope.row.personCount ? 'red' : 'black'
        }">
                已报{{ scope.row.count }}人 / 限{{ scope.row.personCount }}人
           </span>
        </template>
      </el-table-column>
      <el-table-column label="是否顶置" align="center" prop="isTop">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.pet_activity_is_top" :value="scope.row.isTop"/>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['pet:activity:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['pet:activity:remove']"
          >删除
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleConnect(scope.row)"
            v-hasPermi="['pet:activity:content']"
          >查看详细
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-user"
            @click="handleUser(scope.row)"
            v-hasPermi="['pet:activity:content']"
          >用户报名信息
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

    <!-- 添加或修改活动对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="活动标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入活动标题"/>
        </el-form-item>
        <el-form-item label="活动详情">
          <editor v-model="form.content" :min-height="100"/>
        </el-form-item>
        <el-form-item label="图片" prop="images">
          <image-upload v-model="form.images"/>
        </el-form-item>
        <el-form-item label="开始报名时间" prop="startSignTime">
          <el-date-picker clearable
                          v-model="form.startSignTime"
                          type="date"
                          value-format="yyyy-MM-dd"
                          placeholder="请选择开始报名时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="结束报名时间" prop="endSignTime">
          <el-date-picker clearable
                          v-model="form.endSignTime"
                          type="date"
                          value-format="yyyy-MM-dd"
                          placeholder="请选择结束报名时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="活动时间" prop="startTime">
          <el-date-picker clearable
                          v-model="form.startTime"
                          type="date"
                          value-format="yyyy-MM-dd"
                          placeholder="请选择活动时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker clearable
                          v-model="form.endTime"
                          type="date"
                          value-format="yyyy-MM-dd"
                          placeholder="请选择结束时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="创办组织" prop="createOrganize">
          <el-input v-model="form.createOrganize" placeholder="请输入创办组织"/>
        </el-form-item>
        <el-form-item label="活动分类" prop="activityCategory">
          <el-select v-model="form.activityCategory" placeholder="请选择活动分类">
            <el-option
              v-for="dict in dict.type.pet_activity_category"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="活动地点" prop="address">
          <el-input v-model="form.address" placeholder="请输入活动地点"/>
        </el-form-item>
        <el-form-item label="人数限制" prop="personCount">
          <el-input v-model="form.personCount" placeholder="请输入人数限制"/>
        </el-form-item>
        <el-form-item label="活动状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择活动状态">
            <el-option
              v-for="dict in dict.type.pet_activity_status"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="是否顶置" prop="isTop">
          <el-select v-model="form.isTop" placeholder="请选择是否顶置">
            <el-option
              v-for="dict in dict.type.pet_activity_is_top"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>


    <!-- 查看用户报名对话框 -->
    <el-dialog
      title="用户报名信息"
      :visible.sync="UserOpen"
      width="700px"
      append-to-body
      :close-on-click-modal="true"
    >


      <!-- 报名信息列表 -->
      <el-table v-loading="userLoading" :data="activity_registrationList" border>
        <el-table-column type="index" label="序号" align="center" width="80"/>
        <el-table-column label="用户ID" align="center" prop="userId" width="80"/>
        <el-table-column label="联系人姓名" align="center" prop="contactName"/>
        <el-table-column label="联系人电话" align="center" prop="contactPhone"/>
        <el-table-column label="报名时间" align="center" prop="signTime" width="180">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.signTime, '{y}-{m}-{d} {h}:{i}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="报名状态" align="center" prop="status">
          <template slot-scope="scope">
            <dict-tag
              :options="dict.type.pet_user_activity_status"
              :value="String(scope.row.status)"
              fallback="未知状态"
            />
          </template>
        </el-table-column>
      </el-table>

      <!-- 空数据提示 -->
      <div v-if="!userLoading && activity_registrationList.length === 0" align="center" style="padding: 20px;">
        暂无报名数据
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="UserOpen = false">关 闭</el-button>
      </div>
    </el-dialog>


    <!-- 活动详情对话框 -->
    <el-dialog
      title="活动详情"
      :visible.sync="detailOpen"
      width="800px"
      append-to-body
      :close-on-click-modal="true"
    >
      <el-descriptions
        :column="2"
        border
        title="活动详情"
        label-width="100px"
        style="margin-bottom: 20px;"
      >
        <el-descriptions-item label="活动ID">{{ detailForm.activityId || '--' }}</el-descriptions-item>
        <el-descriptions-item label="活动标题">{{ detailForm.title || '--' }}</el-descriptions-item>
        <el-descriptions-item label="活动图片">
          <image-preview
            v-if="detailForm.images"
            :src="detailForm.images"
            :width="80"
            :height="80"
            style="border-radius: 4px;"
          />
          <span v-else>--</span>
        </el-descriptions-item>
        <el-descriptions-item label="开始报名时间">{{ detailForm.startSignTimeFormat || '--' }}</el-descriptions-item>
        <el-descriptions-item label="结束报名时间">{{ detailForm.endSignTimeFormat || '--' }}</el-descriptions-item>
        <el-descriptions-item label="活动开始时间">{{ detailForm.startTimeFormat || '--' }}</el-descriptions-item>
        <el-descriptions-item label="活动结束时间">{{ detailForm.endTimeFormat || '--' }}</el-descriptions-item>
        <el-descriptions-item label="创办组织">{{ detailForm.createOrganize || '--' }}</el-descriptions-item>
        <el-descriptions-item label="活动分类">
          <dict-tag
            :options="dict.type.pet_activity_category"
            :value="detailForm.activityCategory"
          />
        </el-descriptions-item>
        <el-descriptions-item label="活动地点">{{ detailForm.address || '--' }}</el-descriptions-item>
        <el-descriptions-item label="人数限制">{{ detailForm.personCount || '--' }}</el-descriptions-item>
        <el-descriptions-item label="当前报名人数">
          <span
            :style="{color: detailForm.count >= detailForm.personCount && detailForm.personCount ? 'red' : 'inherit'}">
            {{ detailForm.count || 0 }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="活动状态">
          <dict-tag
            :options="dict.type.pet_activity_status"
            :value="detailForm.status"
          />
        </el-descriptions-item>
        <el-descriptions-item label="是否顶置">
          <dict-tag
            :options="dict.type.pet_activity_is_top"
            :value="detailForm.isTop"
          />
        </el-descriptions-item>
        <el-descriptions-item label="活动详情" :span="2">{{ detailForm.content || '--' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailForm.remark || '--' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间" v-if="detailForm.createTimeFormat">{{
            detailForm.createTimeFormat || '--'
          }}
        </el-descriptions-item>
        <el-descriptions-item label="更新时间" v-if="detailForm.updateTimeFormat">{{
            detailForm.updateTimeFormat || '--'
          }}
        </el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {listActivity, getActivity, delActivity, addActivity, updateActivity} from "@/api/pet/activity"
import {listActivity_registration, getActivity_registration} from "../../../api/pet/activity_registration";

export default {
  name: "Activity",
  dicts: ['pet_activity_is_top', 'pet_activity_status', 'pet_activity_category', 'pet_activity_registration', 'pet_user_activity_status'],
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
      // 活动表格数据
      activityList: [],
      //用户报名表数据
      activity_registrationList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: null,
        createOrganize: null,
        activityCategory: null,
        address: null,
        status: null,
        isTop: null,
        activityId: null,
        userId: null,
        contactName: null,
        contactPhone: null,
        cancelTime: null,
        signTime: null,

      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        title: [
          {required: true, message: "活动标题不能为空", trigger: "blur"}
        ],
        content: [
          {required: true, message: "活动详情富文本不能为空", trigger: "blur"}
        ],
        images: [
          {required: true, message: "图片不能为空", trigger: "blur"}
        ],
        startSignTime: [
          {required: true, message: "开始报名时间不能为空", trigger: "blur"}
        ],
        endSignTime: [
          {required: true, message: "结束报名时间不能为空", trigger: "blur"}
        ],
        startTime: [
          {required: true, message: "活动时间不能为空", trigger: "blur"}
        ],
        endTime: [
          {required: true, message: "结束时间不能为空", trigger: "blur"}
        ],
        createOrganize: [
          {required: true, message: "创办组织不能为空", trigger: "blur"}
        ],
        activityCategory: [
          {required: true, message: "活动分类不能为空", trigger: "change"}
        ],
        address: [
          {required: true, message: "活动地点不能为空", trigger: "blur"}
        ],
        personCount: [
          {required: true, message: "人数限制不能为空", trigger: "blur"}
        ],
        status: [
          {required: true, message: "活动状态不能为空", trigger: "change"}
        ],
        isTop: [
          {required: true, message: "是否顶置不能为空", trigger: "change"}
        ],
        userId: [
          {required: true, message: "报名用户不能为空", trigger: "blur"}
        ],
        contactName: [
          {required: true, message: "联系人姓名不能为空", trigger: "blur"}
        ],
        contactPhone: [
          {required: true, message: "联系人电话不能为空", trigger: "blur"}
        ],
      },
      // 详情相关数据
      detailOpen: false,    // 详情弹窗显示状态
      UserOpen: false,
      detailForm: {},
      UserForm: {},
      // 用户报名信息弹窗相关
      userLoading: false,     // 用户报名列表加载状态
      userTotal: 0,           // 用户报名列表总条数
      userQueryParams: {      // 用户报名查询参数
        pageNum: 1,
        pageSize: 10,
        activityId: null,
        userId: null,
        contactName: null,
        contactPhone: null,
        status: null,
        cancelTime: null,
        signTime: null
      },


    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询活动列表 */
    getList() {
      this.loading = true
      listActivity(this.queryParams).then(response => {
        this.activityList = response.rows
        this.total = response.total

        // 重新计算每个活动的有效报名人数（状态值为1的不计入）
        this.activityList.forEach(activity => {
          this.calculateValidRegistrationCount(activity)
        })

        this.loading = false
      })
    },

    /** 计算活动的有效报名人数（状态值为1的不计入） */
    calculateValidRegistrationCount(activity) {
      // 查询该活动的所有报名用户
      listActivity_registration({
        activityId: activity.activityId,
        pageSize: 1000 // 假设每个活动的报名人数不会超过1000
      }).then(response => {
        // 过滤出状态不为1的用户
        const validRegistrations = response.rows.filter(item => item.status !== 1)
        // 更新活动的count字段为有效报名人数
        activity.count = validRegistrations.length

        // 调用API更新后端的count字段
        updateActivity({
          activityId: activity.activityId,
          count: activity.count
        }).then(() => {
          console.log('活动报名人数已更新到后端')
        }).catch(error => {
          console.error('更新后端报名人数失败:', error)
          this.$modal.msgError('更新活动报名人数失败')
        })
      }).catch(error => {
        console.error('计算有效报名人数失败:', error)
      })
    },

    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        activityId: null,
        title: null,
        content: null,
        images: null,
        startSignTime: null,
        endSignTime: null,
        startTime: null,
        endTime: null,
        createOrganize: null,
        activityCategory: null,
        address: null,
        count: null,
        personCount: null,
        status: null,
        isTop: null,
        createTime: null,
        createBy: null,
        updateTime: null,
        updateBy: null,
        remark: null,
        isDeleted: null,
        userId: null,
        contactName: null,
        contactPhone: null,
        cancelTime: null,
        signTime: null,
        uaId: null,

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
      this.ids = selection.map(item => item.activityId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加活动"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const activityId = row.activityId || this.ids
      getActivity(activityId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改活动"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.activityId != null) {
            updateActivity(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addActivity(this.form).then(response => {
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
      const activityIds = row.activityId || this.ids
      this.$modal.confirm('是否确认删除活动编号为"' + activityIds + '"的数据项？').then(function () {
        return delActivity(activityIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {
      })
    },


    /** 查看详情按钮*/
    handleConnect(row) {
      const activityId = row.activityId || this.ids
      getActivity(activityId).then(response => {
        // 格式化时间字段
        const detailData = {
          ...response.data,
          startSignTimeFormat: response.data.startSignTime ? this.parseTime(response.data.startSignTime, '{y}-{m}-{d}') : '',
          endSignTimeFormat: response.data.endSignTime ? this.parseTime(response.data.endSignTime, '{y}-{m}-{d}') : '',
          startTimeFormat: response.data.startTime ? this.parseTime(response.data.startTime, '{y}-{m}-{d}') : '',
          endTimeFormat: response.data.endTime ? this.parseTime(response.data.endTime, '{y}-{m}-{d}') : '',
          createTimeFormat: response.data.createTime ? this.parseTime(response.data.createTime, '{y}-{m}-{d} {h}:{i}:{s}') : '',
          updateTimeFormat: response.data.updateTime ? this.parseTime(response.data.updateTime, '{y}-{m}-{d} {h}:{i}:{s}') : ''
        }
        this.detailForm = detailData
        this.detailOpen = true
      })
    },
    //查看用户信息按钮
    handleUser(row) {
      // 1. 重置用户报名查询参数，设置当前活动ID过滤条件
      this.userQueryParams = {
        pageNum: 1,
        pageSize: 100, // 设为较大值，一次性加载该活动所有报名数据
        activityId: row.activityId || this.ids, // 核心：根据当前活动ID查询报名信息
        userId: null,
        contactName: null,
        contactPhone: null,
        status: null,
        cancelTime: null,
        signTime: null
      };

      // 2. 打开用户报名弹窗
      this.UserOpen = true;
      // 3. 加载用户报名数据
      this.userLoading = true;
      listActivity_registration(this.userQueryParams).then(response => {
        console.log('报名数据:', response.rows); // 添加调试信息
        console.log('字典数据:', this.dict.type.pet_activity_registration); // 检查字典数据
        if (response.rows.length > 0) {
          console.log('第一个报名项的status值:', response.rows[0].status);
          console.log('第一个报名项的status类型:', typeof response.rows[0].status);
        }
        this.activity_registrationList = response.rows;
        this.userLoading = false;
      }).catch(() => {
        this.userLoading = false;
        this.$modal.msgError("获取报名信息失败");
      });
    },


    /** 导出按钮操作 */
    handleExport() {
      this.download('pet/activity/export', {
        ...this.queryParams
      }, `activity_${new Date().getTime()}.xlsx`)
    }
  },
}
</script>
