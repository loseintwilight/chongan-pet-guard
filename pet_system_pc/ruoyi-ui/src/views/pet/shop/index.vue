<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="120px">
      <el-form-item label="店铺名称" prop="shopName">
        <el-input
          v-model="queryParams.shopName"
          placeholder="请输入店铺名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="负责人手机号" prop="legalPhone">
        <el-input
          v-model="queryParams.legalPhone"
          placeholder="请输入负责人手机号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="负责人姓名" prop="legalName">
        <el-input
          v-model="queryParams.legalName"
          placeholder="请输入负责人姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="省市区" prop="province">
        <city-cascade
          v-model="queryCityValue"
          @change="handleQueryCityChange"
          style="width: 100%"
          placeholder="请选择省市区"
          clearable
        />
        <!-- 显示当前查询选择的省市区 -->
<!--        <div v-if="queryParams.province" style="margin-top: 8px; font-size: 12px; color: #409EFF;">-->
<!--          <el-tag type="info" size="small">-->
<!--            查询条件：{{ queryParams.province }} - {{ queryParams.city }} - {{ queryParams.district }}-->
<!--          </el-tag>-->
<!--        </div>-->
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option
            v-for="dict in dict.type.pet_shop_status"
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
          v-hasPermi="['pet:shop:add']"
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
          v-hasPermi="['pet:shop:edit']"
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
          v-hasPermi="['pet:shop:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['pet:shop:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="shopList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" align="center" type="index" />
      <el-table-column label="店铺名称" align="center" prop="shopName" />
      <el-table-column label="店铺主图" align="center" prop="mainImage" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.mainImage" :width="50" :height="50"/>
        </template>
      </el-table-column>
      <el-table-column label="负责人手机号" align="center" prop="legalPhone" />
      <el-table-column label="负责人姓名" align="center" prop="legalName" />
      <el-table-column label="省市区" align="center" prop="location" width="200">
        <template slot-scope="scope">
          <span>{{ scope.row.province }}-{{ scope.row.city }}-{{ scope.row.district }}</span>
        </template>
      </el-table-column>
      <el-table-column label="营业时间" align="center" prop="bussinessHours" width="150" />
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.pet_shop_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-search"
            @click="handleDetail(scope.row)"
            v-hasPermi="['pet:shop:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['pet:shop:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['pet:shop:remove']"
          >删除</el-button>
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

    <!-- 添加或修改店铺对话框 - 双列布局 -->
    <el-dialog :title="title" :visible.sync="open" width="900px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="40">
          <!-- 左列 -->
          <el-col :span="12">
            <el-form-item label="店铺名称" prop="shopName">
              <el-input v-model="form.shopName" placeholder="请输入店铺名称" />
            </el-form-item>
            <el-form-item label="店铺主图" prop="mainImage">
              <image-upload v-model="form.mainImage"/>
            </el-form-item>
            <el-form-item label="负责人手机号" prop="legalPhone">
              <el-input v-model="form.legalPhone" placeholder="请输入负责人手机号" />
            </el-form-item>
            <el-form-item label="负责人姓名" prop="legalName">
              <el-input v-model="form.legalName" placeholder="请输入负责人姓名" />
            </el-form-item>
            <el-form-item label="负责人身份证号" prop="legalNumber">
              <el-input v-model="form.legalNumber" placeholder="请输入负责人身份证号" />
            </el-form-item>
            <el-form-item label="营业执照号" prop="licenseNumber">
              <el-input v-model="form.licenseNumber" placeholder="请输入营业执照号" />
            </el-form-item>
            <el-form-item label="营业执照图片" prop="licenseImage">
              <image-upload v-model="form.licenseImage"/>
            </el-form-item>
            <el-form-item label="省市区" prop="province">
              <city-cascade
                v-model="formCityValue"
                @change="handleFormCityChange"
                style="width: 100%"
                placeholder="请选择省市区"
              />
            </el-form-item>
            <el-form-item label="详细地址" prop="address">
              <el-input v-model="form.address" placeholder="请输入详细地址" />
            </el-form-item>
            <el-form-item label="联系电话" prop="ownerPhone">
              <el-input v-model="form.ownerPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>

          <!-- 右列 -->
          <el-col :span="12">
            <el-form-item label="营业时间" prop="bussinessHours">
              <div class="time-picker-container">
                <el-time-picker
                  is-range
                  v-model="businessTimeRange"
                  range-separator="至"
                  start-placeholder="开始时间"
                  end-placeholder="结束时间"
                  placeholder="选择营业时间范围"
                  value-format="HH:mm:ss"
                  format="HH:mm:ss"
                  style="width: 100%"
                  @change="handleTimeChange"
                />
                <div v-if="form.bussinessHours" class="time-display">
                  当前设置：<el-tag type="info">{{ form.bussinessHours }}</el-tag>
                </div>
              </div>
            </el-form-item>
            <el-form-item label="店铺描述" prop="description">
              <editor v-model="form.description" :min-height="192"/>
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
                <el-option
                  v-for="dict in dict.type.pet_shop_status"
                  :key="dict.value"
                  :label="dict.label"
                  :value="parseInt(dict.value)"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="审核时间" prop="reviewTime">
              <el-date-picker
                v-model="form.reviewTime"
                type="date"
                placeholder="请选择审核时间"
                style="width: 100%"
                value-format="yyyy-MM-dd"
              />
            </el-form-item>
            <el-form-item label="审核意见" prop="reviewRemark">
              <el-select v-model="form.reviewRemark" placeholder="请选择审核意见" style="width: 100%">
                <el-option
                  v-for="dict in dict.type.pet_shop_review_remark"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
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

    <!-- 查看详情对话框 - 双列布局 -->
    <el-dialog title="店铺详情" :visible.sync="detailOpen" width="1000px" append-to-body>
      <el-form :model="detailForm" label-width="120px" :disabled="true">
        <el-row :gutter="40">
          <!-- 左列 -->
          <el-col :span="12">
            <el-form-item label="店铺ID">
              <span>{{ detailForm.shopId }}</span>
            </el-form-item>
            <el-form-item label="店铺名称">
              <span>{{ detailForm.shopName }}</span>
            </el-form-item>
            <el-form-item label="店铺主图">
              <div v-if="detailForm.mainImage">
                <el-image
                  :src="detailForm.mainImage"
                  style="max-width: 200px; max-height: 200px; border: 1px solid #eee; border-radius: 4px;"
                  :preview-src-list="[detailForm.mainImage]"
                  fit="contain"
                />
              </div>
              <span v-else>暂无图片</span>
            </el-form-item>
            <el-form-item label="店主ID">
              <span>{{ detailForm.userId || '-' }}</span>
            </el-form-item>
            <el-form-item label="负责人手机号">
              <span>{{ detailForm.legalPhone }}</span>
            </el-form-item>
            <el-form-item label="负责人姓名">
              <span>{{ detailForm.legalName }}</span>
            </el-form-item>
            <el-form-item label="负责人身份证号">
              <span>{{ detailForm.legalNumber }}</span>
            </el-form-item>
            <el-form-item label="营业执照号">
              <span>{{ detailForm.licenseNumber }}</span>
            </el-form-item>
            <el-form-item label="营业执照图片">
              <div v-if="detailForm.licenseImage">
                <el-image
                  :src="detailForm.licenseImage"
                  style="max-width: 200px; max-height: 200px; border: 1px solid #eee; border-radius: 4px;"
                  :preview-src-list="[detailForm.licenseImage]"
                  fit="contain"
                />
              </div>
              <span v-else>暂无图片</span>
            </el-form-item>
            <el-form-item label="省市区">
              <span>{{ detailForm.province }}-{{ detailForm.city }}-{{ detailForm.district }}</span>
            </el-form-item>
            <el-form-item label="详细地址">
              <span>{{ detailForm.address }}</span>
            </el-form-item>
            <el-form-item label="联系电话">
              <span>{{ detailForm.ownerPhone }}</span>
            </el-form-item>
          </el-col>

          <!-- 右列 -->
          <el-col :span="12">
            <el-form-item label="营业时间">
              <span>{{ detailForm.bussinessHours || '未设置' }}</span>
            </el-form-item>
            <el-form-item label="店铺描述">
              <div class="description-content" v-if="detailForm.description" v-html="detailForm.description"></div>
              <span v-else>暂无描述</span>
            </el-form-item>
            <el-form-item label="状态">
              <dict-tag :options="dict.type.pet_shop_status" :value="detailForm.status"/>
            </el-form-item>
            <el-form-item label="创建时间">
              <span>{{ detailForm.createTime ? parseTime(detailForm.createTime) : '-' }}</span>
            </el-form-item>
            <el-form-item label="创建人">
              <span>{{ detailForm.createBy || '-' }}</span>
            </el-form-item>
            <el-form-item label="更新时间">
              <span>{{ detailForm.updateTime ? parseTime(detailForm.updateTime) : '-' }}</span>
            </el-form-item>
            <el-form-item label="更新人">
              <span>{{ detailForm.updateBy || '-' }}</span>
            </el-form-item>
            <el-form-item label="审核人ID">
              <span>{{ detailForm.reviewId || '-' }}</span>
            </el-form-item>
            <el-form-item label="审核时间">
              <span>{{ detailForm.reviewTime ? parseTime(detailForm.reviewTime) : '-' }}</span>
            </el-form-item>
            <el-form-item label="审核意见">
              <dict-tag v-if="detailForm.reviewRemark" :options="dict.type.pet_shop_review_remark" :value="detailForm.reviewRemark"/>
              <span v-else>-</span>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listShop, getShop, delShop, addShop, updateShop } from "@/api/pet/shop"
import CityCascade from "@/components/CityCascade"
import rawData from "@/data/pca.json"  // 导入原始省市区数据

export default {
  name: "Shop",
  components: {
    CityCascade
  },
  dicts: ['pet_shop_status', 'pet_shop_review_remark'],
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
      // 店铺，站点表格数据
      shopList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否显示详情弹出层
      detailOpen: false,
      // 营业时间范围选择器值
      businessTimeRange: null,
      // 查询城市选择器值（编码数组） - 专门用于查询
      queryCityValue: [],
      // 表单城市选择器值（编码数组） - 专门用于表单
      formCityValue: [],
      // 原始省市区数据
      cityData: rawData,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        shopName: null,
        legalPhone: null,
        legalName: null,
        province: null,
        city: null,
        district: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 详情表单参数
      detailForm: {},
      // 表单校验
      rules: {
        shopName: [
          { required: true, message: "店铺名称不能为空", trigger: "blur" }
        ],
        mainImage: [
          { required: true, message: "店铺主图不能为空", trigger: "blur" }
        ],
        legalPhone: [
          { required: true, message: "负责人手机不能为空", trigger: "blur" }
        ],
        legalName: [
          { required: true, message: "负责人姓名不能为空", trigger: "blur" }
        ],
        legalNumber: [
          { required: true, message: "负责人身份证号不能为空", trigger: "blur" }
        ],
        licenseNumber: [
          { required: true, message: "营业执照号不能为空", trigger: "blur" }
        ],
        licenseImage: [
          { required: true, message: "营业执照图片不能为空", trigger: "blur" }
        ],
        province: [
          { required: true, message: "省份不能为空", trigger: "blur" }
        ],
        city: [
          { required: true, message: "城市不能为空", trigger: "blur" }
        ],
        district: [
          { required: true, message: "区县不能为空", trigger: "blur" }
        ],
        address: [
          { required: true, message: "详细地址不能为空", trigger: "blur" }
        ],
        ownerPhone: [
          { required: true, message: "联系电话不能为空", trigger: "blur" }
        ],
        bussinessHours: [
          { required: true, message: "营业时间不能为空", trigger: "change" }
        ],
        description: [
          { required: true, message: "店铺描述不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "状态不能为空", trigger: "change" }
        ],
        reviewTime: [
          { required: true, message: "审核时间不能为空", trigger: "change" }
        ],
        reviewRemark: [
          { required: true, message: "审核意见不能为空", trigger: "change" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询店铺，站点列表 */
    getList() {
      this.loading = true
      listShop(this.queryParams).then(response => {
        this.shopList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.businessTimeRange = null
      this.formCityValue = []
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        shopId: null,
        shopName: null,
        mainImage: null,
        userId: null,
        legalPhone: null,
        legalName: null,
        legalNumber: null,
        licenseNumber: null,
        licenseImage: null,
        province: null,
        city: null,
        district: null,
        address: null,
        ownerPhone: null,
        bussinessHours: null,
        description: null,
        status: null,
        createTime: null,
        createBy: null,
        updateTime: null,
        updateBy: null,
        reviewId: null,
        reviewTime: null,
        reviewRemark: null,
        isDeleted: null
      }
      this.businessTimeRange = null
      this.formCityValue = []
      this.resetForm("form")
    },
    // 详情表单重置
    resetDetail() {
      this.detailForm = {
        shopId: null,
        shopName: null,
        mainImage: null,
        userId: null,
        legalPhone: null,
        legalName: null,
        legalNumber: null,
        licenseNumber: null,
        licenseImage: null,
        province: null,
        city: null,
        district: null,
        address: null,
        ownerPhone: null,
        bussinessHours: null,
        description: null,
        status: null,
        createTime: null,
        createBy: null,
        updateTime: null,
        updateBy: null,
        reviewId: null,
        reviewTime: null,
        reviewRemark: null,
        isDeleted: null
      }
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      // 只有当省市区三个字段都有值时才进行查询，否则清空查询条件
      if (!this.queryParams.province || !this.queryParams.city || !this.queryParams.district) {
        // 如果查询条件不完整，清空所有相关参数
        this.queryParams.province = null
        this.queryParams.city = null
        this.queryParams.district = null
        this.queryCityValue = [] // 清空查询选择器值
        console.log('查询条件不完整，已清空省市区查询条件')
      } else {
        // 确保查询条件精确匹配
        console.log('查询省市区条件:', this.queryParams.province, this.queryParams.city, this.queryParams.district)
      }
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.queryCityValue = [] // 重置查询选择器值
      this.queryParams.province = null
      this.queryParams.city = null
      this.queryParams.district = null
      console.log('已重置查询条件')
      this.handleQuery()
    },
    /** 查询表单城市选择变化 */
    handleQueryCityChange(selectedData) {
      console.log('查询城市选择变化:', selectedData)

      // 重置查询参数
      this.queryParams.province = null
      this.queryParams.city = null
      this.queryParams.district = null

      if (selectedData) {
        // 根据节点结构判断选择级别
        if (selectedData.parent && selectedData.parent.parent) {
          // 选择了区县 - 设置完整的省市区
          this.queryParams.province = selectedData.parent.parent.label
          this.queryParams.city = selectedData.parent.label
          this.queryParams.district = selectedData.label

          // 同时更新queryCityValue，确保选择器显示正确
          this.updateQueryCityValueFromSelected(selectedData)

          console.log('查询条件已设置(区县):', {
            province: this.queryParams.province,
            city: this.queryParams.city,
            district: this.queryParams.district,
            queryCityValue: this.queryCityValue
          })
        } else if (selectedData.parent) {
          // 选择了城市，提示需要选择到区县
          this.$message.warning("请选择到具体的区县进行精确查询")
          this.queryCityValue = [] // 清空选择器值
          this.queryParams.province = null
          this.queryParams.city = null
          this.queryParams.district = null
        } else {
          // 选择了省份，提示需要选择到区县
          this.$message.warning("请选择到具体的区县进行精确查询")
          this.queryCityValue = [] // 清空选择器值
          this.queryParams.province = null
          this.queryParams.city = null
          this.queryParams.district = null
        }
      } else {
        // 清空选择
        this.queryParams.province = null
        this.queryParams.city = null
        this.queryParams.district = null
        console.log('已清空查询条件')
      }
    },
    /** 根据选择的节点更新queryCityValue */
    updateQueryCityValueFromSelected(selectedData) {
      if (selectedData && selectedData.pathValues) {
        // 如果selectedData包含完整的路径值，直接使用
        this.queryCityValue = selectedData.pathValues || []
      } else if (selectedData && selectedData.parent && selectedData.parent.parent) {
        // 否则根据节点结构生成编码
        const provinceName = selectedData.parent.parent.label
        const cityName = selectedData.parent.label
        const districtName = selectedData.label

        const codes = this.generateRegionCodes(provinceName, cityName, districtName)
        this.queryCityValue = codes
      }
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.shopId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加店铺"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const shopId = row.shopId || this.ids
      getShop(shopId).then(response => {
        this.form = response.data

        // 设置营业时间范围选择器
        if (this.form.bussinessHours) {
          this.parseBusinessHours(this.form.bussinessHours)
        }

        // 设置城市选择器值
        // 根据已有的省市区名称获取对应的编码
        this.setCityCascadeValue()

        this.open = true
        this.title = "修改店铺"
      })
    },
    /** 根据省市区名称设置城市选择器值 */
    setCityCascadeValue() {
      // 清空现有的城市选择器值
      this.formCityValue = []

      // 只有当三个字段都有值时才尝试设置编码
      if (this.form.province && this.form.city && this.form.district) {
        try {
          // 根据省市区名称生成对应的编码
          const codes = this.generateRegionCodes(this.form.province, this.form.city, this.form.district)
          if (codes && codes.length === 3) {
            this.formCityValue = codes
            console.log('设置的省市区编码:', codes, '对应名称:', this.form.province, this.form.city, this.form.district)
          } else {
            console.warn('未能生成对应的省市区编码')
            this.formCityValue = []
          }
        } catch (error) {
          console.error('设置省市区编码失败:', error)
          this.formCityValue = []
        }
      }
    },
    /** 生成省市区编码 */
    generateRegionCodes(provinceName, cityName, districtName) {
      // 使用和CityCascade组件相同的编码生成逻辑
      // 确保编码一致，这样选择器才能正确显示
      const generateCode = (name) => {
        // 这里使用和transformCityData.js相同的编码逻辑
        try {
          return btoa(unescape(encodeURIComponent(name))).slice(0, 6)
        } catch (e) {
          // 如果编码失败，使用简单的哈希
          let hash = 0
          for (let i = 0; i < name.length; i++) {
            hash = ((hash << 5) - hash) + name.charCodeAt(i)
            hash = hash & hash // Convert to 32bit integer
          }
          return Math.abs(hash).toString().slice(0, 6)
        }
      }

      const provinceCode = generateCode(provinceName)
      const cityCode = generateCode(cityName)
      const districtCode = generateCode(districtName)

      return [provinceCode, cityCode, districtCode]
    },
    /** 解析营业时间字符串为时间范围数组 */
    parseBusinessHours(timeStr) {
      if (!timeStr) {
        this.businessTimeRange = null
        return
      }

      try {
        // 格式如：09:00:00-18:00:00
        const timeArr = timeStr.split('-')
        if (timeArr.length === 2) {
          const startTimeStr = timeArr[0].trim()
          const endTimeStr = timeArr[1].trim()

          // 创建时间对象
          const createTimeObj = (timeString) => {
            const parts = timeString.split(':')
            const time = new Date()
            time.setHours(parseInt(parts[0]) || 0)
            time.setMinutes(parseInt(parts[1]) || 0)
            time.setSeconds(parseInt(parts[2]) || 0)
            return time
          }

          this.businessTimeRange = [
            createTimeObj(startTimeStr),
            createTimeObj(endTimeStr)
          ]
        } else {
          this.businessTimeRange = null
        }
      } catch (error) {
        console.error('解析营业时间失败:', error)
        this.businessTimeRange = null
      }
    },
    /** 时间范围选择器变化处理 */
    handleTimeChange(value) {
      if (value && value.length === 2) {
        // 格式化为 HH:mm:ss-HH:mm:ss 格式
        this.form.bussinessHours = `${value[0]}-${value[1]}`
      } else {
        this.form.bussinessHours = null
      }
    },
    /** 表单城市选择变化 */
    handleFormCityChange(selectedData) {
      console.log('表单城市选择变化:', selectedData)

      if (selectedData) {
        // 获取选择的节点信息
        if (selectedData.parent && selectedData.parent.parent) {
          // 选择了区县
          this.form.province = selectedData.parent.parent.label
          this.form.city = selectedData.parent.label
          this.form.district = selectedData.label
        }
        // } else if (selectedData.parent) {
        //   // 选择了城市
        //   this.form.province = selectedData.parent.label
        //   this.form.city = selectedData.label
        //   this.form.district = ''
        // } else {
        //   // 选择了省份
        //   this.form.province = selectedData.label
        //   this.form.city = ''
        //   this.form.district = ''
        // }

        // 更新formCityValue，确保双向绑定
        this.updateFormCityValueFromSelected(selectedData)
      } else {
        // 清空选择
        this.form.province = ''
        this.form.city = ''
        this.form.district = ''
        this.formCityValue = []
      }
    },
    /** 根据表单的省市区更新formCityValue */
    updateFormCityValueFromSelected(selectedData) {
      if (selectedData && selectedData.pathValues) {
        // 如果selectedData包含完整的路径值，直接使用
        this.formCityValue = selectedData.pathValues || []
      } else if (this.form.province && this.form.city && this.form.district) {
        const codes = this.generateRegionCodes(this.form.province, this.form.city, this.form.district)
        this.formCityValue = codes
      } else {
        this.formCityValue = []
      }
    },
    /** 详情按钮操作 */
    handleDetail(row) {
      this.resetDetail()
      const shopId = row.shopId
      getShop(shopId).then(response => {
        this.detailForm = response.data
        this.detailOpen = true
      })
    },
    // 时间格式化方法
    parseTime(time, pattern) {
      if (!time) {
        return ''
      }
      const format = pattern || '{y}-{m}-{d} {h}:{i}:{s}'
      let date
      if (typeof time === 'object') {
        date = time
      } else {
        if ((typeof time === 'string') && (/^[0-9]+$/.test(time))) {
          time = parseInt(time)
        } else if (typeof time === 'string') {
          time = time.replace(new RegExp(/-/gm), '/').replace('T', ' ').replace(new RegExp(/\.[\d]{3}/gm), '');
        }
        if ((typeof time === 'number') && (time.toString().length === 10)) {
          time = time * 1000
        }
        date = new Date(time)
      }
      const formatObj = {
        y: date.getFullYear(),
        m: date.getMonth() + 1,
        d: date.getDate(),
        h: date.getHours(),
        i: date.getMinutes(),
        s: date.getSeconds(),
        a: date.getDay()
      }
      const time_str = format.replace(/{(y|m|d|h|i|s|a)+}/g, (result, key) => {
        let value = formatObj[key]
        if (key === 'a') { return ['日', '一', '二', '三', '四', '五', '六'][value] }
        if (result.length > 0 && value < 10) {
          value = '0' + value
        }
        return value || 0
      })
      return time_str
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          // 确保省市区三个字段都有值
          if (!this.form.province || !this.form.city || !this.form.district) {
            this.$message.error("请选择完整的省市区信息")
            return
          }

          if (this.form.shopId != null) {
            updateShop(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addShop(this.form).then(response => {
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
      const shopIds = row.shopId || this.ids
      this.$modal.confirm('是否确认删除店铺，站点编号为"' + shopIds + '"的数据项？').then(function() {
        return delShop(shopIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('pet/shop/export', {
        ...this.queryParams
      }, `shop_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
