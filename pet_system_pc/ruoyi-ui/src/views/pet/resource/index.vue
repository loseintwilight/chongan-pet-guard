<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="商品名称" prop="resourceName">
        <el-input
          v-model="queryParams.resourceName"
          placeholder="请输入商品名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <!-- 搜索栏：分类ID改为下拉框 -->
      <el-form-item label="分类" prop="categoryId">
        <el-select
          v-model="queryParams.categoryId"
          placeholder="请选择分类"
          clearable
        >
          <el-option
            v-for="category in categoryList"
            :key="category.categoryId"
            :label="`${category.categoryName}`"
            :value="category.categoryId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option
            v-for="dict in dict.type.pet_resource_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <!-- 搜索栏：店铺ID改为下拉框 -->
      <el-form-item label="店铺" prop="shopId">
        <el-select
          v-model="queryParams.shopId"
          placeholder="请选择店铺"
          clearable
        >
          <el-option
            v-for="shop in shopList"
            :key="shop.shopId"
            :label="`${shop.shopName || '未知店铺'}`"
            :value="shop.shopId"
          />
        </el-select>
      </el-form-item>
      <!-- 新增：搜索栏价格（元）输入框 -->
      <el-form-item label="商品价格（元）" prop="priceYuan">
        <el-input
          v-model="queryParams.priceYuan"
          placeholder="请输入商品价格（元）"
          clearable
          type="number"
          step="0.01"
          min="0"
          @keyup.enter.native="handleQuery"
        />
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
          v-hasPermi="['pet:resource:add']"
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
          v-hasPermi="['pet:resource:edit']"
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
          v-hasPermi="['pet:resource:remove']"
        >删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['pet:resource:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="resourceList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="序号" align="center" prop="resourceId" type="index" width="50"/>
      <el-table-column label="商品名称" align="center" prop="resourceName"/>
      <el-table-column label="商品图片" align="center" prop="images" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.images" :width="50" :height="50"/>
        </template>
      </el-table-column>
      <el-table-column label="分类名称" align="center" prop="categoryName"/>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.pet_resource_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="规格" align="center" prop="specification"/>
      <el-table-column label="库存" align="center" prop="stock"/>
      <el-table-column label="销量" align="center" prop="sales"/>
      <!-- 核心：表格展示分转元（保留2位小数，空值友好展示） -->
      <el-table-column label="商品价格（元）" align="center">
        <template slot-scope="scope">
          {{ formatPrice(scope.row.price) }}
        </template>
      </el-table-column>
      <el-table-column label="店铺名称" align="center">
        <template slot-scope="scope">
          <span v-for="item in shopList" v-if="item.shopId === scope.row.shopId" :key="item.shopId">{{
              item.shopName
            }}</span>
          <span v-if="!shopList.find(item => item.shopId === scope.row.shopId)">未知店铺</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['pet:resource:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['pet:resource:remove']"
          >删除
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

    <!-- 添加或修改商品对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="商品名称" prop="resourceName">
          <el-input v-model="form.resourceName" placeholder="请输入商品名称"/>
        </el-form-item>
        <el-form-item label="商品图片" prop="images">
          <image-upload v-model="form.images"/>
        </el-form-item>
        <!-- 弹窗：分类ID改为下拉框 -->
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" @change="handleCategoryChange">
            <el-option
              v-for="category in categoryList"
              :key="category.categoryId"
              :label="`${category.categoryName}`"
              :value="category.categoryId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="form.categoryName" type="textarea" placeholder="请输入内容"/>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option
              v-for="dict in dict.type.pet_resource_status"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="规格" prop="specification">
          <el-input v-model="form.specification" placeholder="请输入规格"/>
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number
            v-model="form.stock"
            :min="0"
            :precision="0"
            placeholder="请输入库存"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注"/>
        </el-form-item>
        <!-- 核心：表单输入元，标注明确，实时转分 -->
        <el-form-item label="商品价格（元）" prop="priceYuan">
          <el-input-number
            v-model="form.priceYuan"
            :min="0"
            :precision="2"
            :step="0.01"
            placeholder="请输入商品价格（单位：元）"
            style="width: 100%"
            @change="handlePriceChange"
          />
        </el-form-item>

        <!-- 弹窗：店铺ID改为下拉框 -->
        <el-form-item label="店铺" prop="shopId">
          <el-select v-model="form.shopId" placeholder="请选择店铺">
            <el-option
              v-for="shop in shopList"
              :key="shop.shopId"
              :label="`${shop.shopName || '未知店铺'}`"
              :value="shop.shopId"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {listResource, getResource, delResource, addResource, updateResource} from "@/api/pet/resource"
// 导入分类列表接口（根据实际项目路径调整）
import {listCategory} from "@/api/pet/category"
// 导入店铺列表接口（根据实际项目路径调整）
import {listShop} from "@/api/pet/shop"

export default {
  name: "Resource",
  dicts: ['pet_resource_status'],
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      resourceList: [],
      // 新增：分类列表、店铺列表
      categoryList: [],
      shopList: [],
      title: "",
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        resourceName: null,
        categoryId: null,
        categoryName: null,
        status: null,
        shopId: null,
        priceYuan: null, // 搜索用：元
        price: null      // 搜索用：分（内部转换）
      },
      // 表单参数：只保留price（存分），priceYuan（显元）
      form: {},
      // 校验规则调整：分类/店铺ID改为change触发，适配下拉框
      rules: {
        resourceName: [
          {required: true, message: "商品名称不能为空", trigger: "blur"}
        ],
        images: [
          {required: true, message: "图片URL不能为空", trigger: "blur"}
        ],
        categoryId: [
          {required: true, message: "分类不能为空", trigger: "change"}
        ],
        categoryName: [
          {required: true, message: "分类名称不能为空", trigger: "blur"}
        ],
        status: [
          {required: true, message: "状态不能为空", trigger: "change"}
        ],
        specification: [
          {required: true, message: "规格不能为空", trigger: "blur"}
        ],
        stock: [
          {required: true, message: "库存不能为空", trigger: "blur"}
        ],
        priceYuan: [ // 校验元输入
          {required: true, message: "商品价格不能为空", trigger: "blur"}
        ],
        shopId: [
          {required: true, message: "店铺不能为空", trigger: "change"}
        ]
      }
    }
  },
  created() {
    this.getList()
    // 初始化获取分类列表、店铺列表
    this.getCategoryList()
    this.getShopList()
  },
  methods: {
    /** 查询商品列表 */
    getList() {
      this.loading = true
      // 处理价格参数：如果有priceYuan，转换为分
      const params = {...this.queryParams}
      if (params.priceYuan !== null && params.priceYuan !== '') {
        params.price = Math.round(Number(params.priceYuan) * 100)
      }
      // 删除priceYuan，避免传到后端
      delete params.priceYuan

      listResource(params).then(response => {
        this.resourceList = response.rows
        this.total = response.total
        this.loading = false
      })
    },

    /** 获取分类列表 */
    getCategoryList() {
      listCategory({pageNum: 1, pageSize: 999}) // 查全部分类（不分页）
        .then(response => {
          this.categoryList = response.rows || response.list || response
        })
        .catch(error => {
          console.error('获取分类列表失败:', error)
          this.$modal.msgError('获取分类列表失败，请刷新重试')
        })
    },

    /** 获取店铺列表 */
    getShopList() {
      listShop({pageNum: 1, pageSize: 999}) // 查全部店铺（不分页）
        .then(response => {
          this.shopList = response.rows || response.list || response
        })
        .catch(error => {
          console.error('获取店铺列表失败:', error)
          this.$modal.msgError('获取店铺列表失败，请刷新重试')
        })
    },

    // 格式化价格显示
    formatPrice(priceInCents) {
      if (priceInCents === null || priceInCents === undefined || priceInCents === '') {
        return '——'
      }
      return (Number(priceInCents) / 100).toFixed(2) + ' 元'
    },

    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        resourceId: null,
        resourceName: null,
        images: null,
        categoryId: null,
        categoryName: null,
        status: null,
        specification: null,
        stock: null,
        sales: null,
        createTime: null,
        createBy: null,
        updateTime: null,
        updateBy: null,
        remark: null,
        isDeleted: null,
        price: null,     // 存储分（传给后端）
        priceYuan: null  // 展示元（前端输入）
      }
      this.resetForm("form")
    },

    /** 价格变化处理：元转分 */
    handlePriceChange(value) {
      if (value !== null && value !== undefined) {
        // 元转分，四舍五入避免浮点精度问题
        this.form.price = Math.round(Number(value) * 100)
      } else {
        this.form.price = null
      }
    },

    /** 分类变化处理：自动填充分类名称 */
    handleCategoryChange(categoryId) {
      const category = this.categoryList.find(item => item.categoryId === categoryId)
      if (category) {
        this.form.categoryName = category.categoryName
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
      // 重置价格相关参数
      this.queryParams.priceYuan = null
      this.queryParams.price = null
      this.handleQuery()
    },

    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.resourceId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },

    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加商品"
    },

    /** 修改按钮操作：分转元回显 */
    handleUpdate(row) {
      this.reset()
      const resourceId = row.resourceId || this.ids
      getResource(resourceId).then(response => {
        this.form = response.data
        // 分转元
        if (this.form.price !== null && this.form.price !== undefined) {
          this.form.priceYuan = (Number(this.form.price) / 100).toFixed(2)
        } else {
          this.form.priceYuan = null
        }
        this.open = true
        this.title = "修改商品"
      })
    },

    /** 提交按钮：确保元转分后提交 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          // 确保价格正确转换（作为最后保障）
          if (this.form.priceYuan !== null && this.form.priceYuan !== undefined) {
            this.form.price = Math.round(Number(this.form.priceYuan) * 100)
          } else {
            this.form.price = null
          }

          // 直接传分（form.price）给后端
          const request = this.form.resourceId ? updateResource(this.form) : addResource(this.form)
          request.then(() => {
            this.$modal.msgSuccess(this.form.resourceId ? "修改成功" : "新增成功")
            this.open = false
            this.getList()
          }).catch(err => {
            console.error("提交失败：", err)
            this.$modal.msgError("提交失败，请重试")
          })
        }
      })
    },

    /** 删除按钮操作 */
    handleDelete(row) {
      const resourceIds = row.resourceId || this.ids
      this.$modal.confirm('是否确认删除商品编号为"' + resourceIds + '"的数据项？').then(() => {
        return delResource(resourceIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {
      })
    },

    /** 导出按钮操作：导出价格转元展示 */
    handleExport() {
      const exportParams = {...this.queryParams}
      // 导出时如果有价格筛选，恢复元的格式（可选，根据导出需求调整）
      if (exportParams.priceYuan) {
        exportParams.price = exportParams.priceYuan
        delete exportParams.priceYuan
      }
      this.download('pet/resource/export', exportParams, `resource_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
