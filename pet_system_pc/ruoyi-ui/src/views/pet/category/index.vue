<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <!-- 状态下拉框（替换原输入框） -->
      <el-form-item label="状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择状态"
          clearable
          @keyup.enter.native="handleQuery"
        >
          <el-option
            v-for="dict in dict.type.pet_resource_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          ></el-option>
        </el-select>
      </el-form-item>
      <!-- 店铺ID下拉框（替换原输入框） -->
      <el-form-item label="店铺" prop="shopId">
        <el-select
          v-model="queryParams.shopId"
          placeholder="请选择店铺"
          clearable
          @keyup.enter.native="handleQuery"
        >
          <el-option
            v-for="shop in shopList"
            :key="shop.shopId"
            :label="`${shop.shopName || '未知店铺'}`"
            :value="shop.shopId"
          ></el-option>
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
          v-hasPermi="['pet:category:add']"
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
          v-hasPermi="['pet:category:edit']"
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
          v-hasPermi="['pet:category:remove']"
        >删除
        </el-button>
      </el-col>

      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="categoryList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="序号" align="center" type="index" width="50"/>
      <el-table-column label="分类名称" align="center" prop="categoryName"/>
      <el-table-column label="排序" align="center" prop="order"/>
      <el-table-column label="状态" align="center" prop="status">
        <!-- 列表显示pet_resource_status字典内容 -->
        <template slot-scope="scope">
          <dict-tag :options="dict.type.pet_resource_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="店铺名称" align="center" prop="shopId">
        <template slot-scope="scope">
          <!-- 店铺ID显示优化：ID+名称 -->
          {{ getShopLabel(scope.row.shopId) }}
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
            v-hasPermi="['pet:category:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['pet:category:remove']"
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

    <!-- 添加或修改商品种类对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="form.categoryName" type="textarea" placeholder="请输入内容"/>
        </el-form-item>
        <el-form-item label="排序" prop="order">
          <el-input v-model="form.order" placeholder="请输入排序"/>
        </el-form-item>
        <!-- 弹窗状态下拉框 -->
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
        <!-- 弹窗店铺ID下拉框 -->
        <el-form-item label="店铺id" prop="shopId">
          <el-select v-model="form.shopId" placeholder="请选择店铺">
            <el-option
              v-for="shop in shopList"
              :key="shop.shopId"
              :label="`${shop.shopName || '未知店铺'}`"
              :value="shop.shopId"
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
  </div>
</template>

<script>
import {listCategory, getCategory, delCategory, addCategory, updateCategory} from "@/api/pet/category"
// 导入店铺列表接口（请根据实际项目路径调整）
import {listShop} from "@/api/pet/shop"

export default {
  name: "Category",
  dicts: ['pet_resource_status'],
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
      // 商品种类表格数据
      categoryList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        categoryName: null,
        status: null,
        shopId: null,
      },
      // 表单参数
      form: {},
      // 店铺列表（下拉框数据源）
      shopList: [],
      // 表单校验
      rules: {
        categoryName: [
          {required: true, message: "分类名称不能为空", trigger: "blur"}
        ],
        status: [
          {required: true, message: "状态不能为空", trigger: "change"} // 下拉框用change触发校验
        ],
        shopId: [
          {required: true, message: "店铺id不能为空", trigger: "change"} // 下拉框用change触发校验
        ],
      }
    }
  },
  created() {
    this.getList()
    this.loadShopList() // 初始化加载店铺列表
  },
  methods: {
    /** 加载店铺列表（下拉框数据源） */
    async loadShopList() {
      try {
        // 加载店铺列表（分页参数根据实际需求调整，这里加载全部）
        const response = await listShop({pageNum: 1, pageSize: 999})
        this.shopList = response.rows || []
      } catch (error) {
        this.$modal.msgError("加载店铺列表失败")
        console.error("店铺列表加载失败：", error)
      }
    },
    /** 查询商品种类列表 */
    getList() {
      this.loading = true
      listCategory(this.queryParams).then(response => {
        this.categoryList = response.rows
        this.total = response.total
        this.loading = false
        console.log(response.rows)
      })
    },
    /** 根据店铺ID获取店铺显示标签 */
    getShopLabel(shopId) {
      if (!shopId) return "未选择"
      const shop = this.shopList.find(item => item.shopId === shopId)
      return shop ? `${shop.shopName || '未知店铺'}` : `- 未知店铺`
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        categoryId: null,
        categoryName: null,
        order: null,
        status: null,
        createTime: null,
        updateTime: null,
        createBy: null,
        updateBy: null,
        shopId: null,
        remark: null
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
      this.ids = selection.map(item => item.categoryId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加商品种类"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const categoryId = row.categoryId || this.ids
      getCategory(categoryId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改商品种类"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.categoryId != null) {
            updateCategory(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addCategory(this.form).then(response => {
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
      const categoryIds = row.categoryId || this.ids
      this.$modal.confirm('是否确认删除商品种类编号为"' + categoryIds + '"的数据项？').then(function () {
        return delCategory(categoryIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {
      })
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('pet/category/export', {
        ...this.queryParams
      }, `category_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
