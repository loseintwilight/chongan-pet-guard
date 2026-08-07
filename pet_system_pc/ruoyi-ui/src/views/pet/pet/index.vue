<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="宠物名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入宠物名称"
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
      <el-form-item label="所属店铺或站点" prop="deptId">
        <el-select v-model="queryParams.deptId" placeholder="请选择所属店铺或站点" clearable>
          <el-option
            v-for="item in petShopList"
            :key="item.shopId"
            :label="item.shopName"
            :value="item.shopId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="性别" prop="gender">
        <el-select v-model="queryParams.gender" placeholder="请选择性别" clearable>
          <el-option
            v-for="dict in dict.type.pet_pet_gender"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option
            v-for="dict in dict.type.pet_pet_status"
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
          v-hasPermi="['pet:pet:add']"
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
          v-hasPermi="['pet:pet:edit']"
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
          v-hasPermi="['pet:pet:remove']"
        >删除
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="petList"  @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="序号" align="center" prop="petId" type="index"/>
      <el-table-column label="宠物名称" align="center" prop="name"/>
      <el-table-column label="宠物图片" align="center" prop="images" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.images" :width="50" :height="50"/>
        </template>
      </el-table-column>
      <el-table-column label="物种" align="center" prop="species">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.pet_pet_species" :value="scope.row.species"/>
        </template>
      </el-table-column>
      <el-table-column label="品种" align="center" prop="breed"/>
      <el-table-column label="性别" align="center" prop="gender">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.pet_pet_gender" :value="scope.row.gender"/>
        </template>
      </el-table-column>
      <el-table-column label="年龄" align="center">
        <template slot-scope="scope">
          <span v-if="scope.row.age !== null && scope.row.age !== undefined && scope.row.age !== ''">
            {{ scope.row.age }}个月
          </span>
          <span v-else>--</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.pet_pet_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="所属店铺或者站点" align="center" >
        <template slot-scope="scope">
          <span v-for="item in petShopList" v-if="item.shopId === scope.row.deptId">{{ item.shopName }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
            v-hasPermi="['pet:pet:query']"
          >查看详情
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['pet:pet:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['pet:pet:remove']"
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

    <!-- 添加或修改宠物信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form"
               :model="form"
               :rules="rules"
               label-width="100px"
      >
        <el-form-item label="宠物名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入宠物名称"/>
        </el-form-item>
        <el-form-item label="宠物图片" prop="images">
          <image-upload v-model="form.images"/>
        </el-form-item>
        <el-form-item label="物种" prop="species">
          <el-select v-model="form.species" placeholder="请选择物种">
            <el-option
              v-for="dict in dict.type.pet_pet_species"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="品种" prop="breed">
          <el-input v-model="form.breed" placeholder="请输入品种"/>
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-select v-model="form.gender" placeholder="请选择性别">
            <el-option
              v-for="dict in dict.type.pet_pet_gender"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="年龄/月" prop="age">
          <el-input-number
            v-model="form.age"
            :min="0"
            :max="200"
            :step="1"
            :precision="0"
            controls-position="right"
            placeholder="请输入年龄（月）"
            style="width: 40%"
          />
        </el-form-item>
        <el-form-item label="健康状态描述" prop="healthInfo">
          <el-input v-model="form.healthInfo" placeholder="请输入健康状态描述"/>
        </el-form-item>
        <el-form-item label="是否绝育" prop="neuter">
          <el-select v-model="form.neuter" placeholder="请选择是否绝育">
            <el-option
              v-for="dict in dict.type.pet_pet_neuter"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="是否免疫" prop="immunity">
          <el-select v-model="form.immunity" placeholder="请选择是否免疫">
            <el-option
              v-for="dict in dict.type.pet_pet_immunity"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="是否驱虫" prop="insectRepellent">
          <el-select v-model="form.insectRepellent" placeholder="请选择是否驱虫">
            <el-option
              v-for="dict in dict.type.pet_pet_insect_repellent"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="驱虫周期/天" prop="dewormingInterval">
          <el-input-number
            v-model="form.dewormingInterval"
            :min="0"
            :max="1200"
            :step="1"
            :precision="0"
            controls-position="right"
            placeholder="请输入驱虫周期（天）"
            style="width: 40%"
          />
        </el-form-item>
        <el-form-item label="上回驱虫期 " prop="insectRepellentLastTime">
          <el-date-picker clearable
                          v-model="form.insectRepellentLastTime"
                          type="date"
                          value-format="yyyy-MM-dd HH:mm:ss"
                          placeholder="请选择上回驱虫期 ">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="宠物描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入宠物描述"/>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option
              v-for="dict in dict.type.pet_pet_status"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="发现、所在地" prop="location">
          <el-input v-model="form.location" placeholder="请输入发现、所在地"/>
        </el-form-item>
        <el-form-item label="所属店铺或站点" prop="deptId">
          <el-select
            v-model="form.deptId"
            placeholder="请选择所属店铺或站点"
            clearable
            style="width: 100%"
          >
            <el-option
              v-for="item in petShopList"
              :key="item.shopId"
              :label="item.shopName"
              :value="item.shopId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog
      :title="'宠物详情'"
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
        <el-descriptions-item label="宠物名称">
          {{ form.name || '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="宠物图片">
          <image-preview
            v-if="form.images"
            :src="form.images"
            :width="80"
            :height="80"
            style="border-radius: 4px;"
          />
          <span v-else>--</span>
        </el-descriptions-item>
        <el-descriptions-item label="物种">
          <dict-tag :options="dict.type.pet_pet_species" :value="form.species"/>
        </el-descriptions-item>
        <el-descriptions-item label="品种">
          {{ form.breed || '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="性别">
          <dict-tag :options="dict.type.pet_pet_gender" :value="form.gender"/>
        </el-descriptions-item>
        <el-descriptions-item label="年龄">
          {{ form.age ? form.age + '个月' : '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="健康状态描述">
          {{ form.healthInfo || '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="是否绝育">
          <dict-tag :options="dict.type.pet_pet_neuter" :value="form.neuter"/>
        </el-descriptions-item>
        <el-descriptions-item label="是否免疫">
          <dict-tag :options="dict.type.pet_pet_immunity" :value="form.immunity"/>
        </el-descriptions-item>
        <el-descriptions-item label="是否驱虫">
          <dict-tag :options="dict.type.pet_pet_insect_repellent" :value="form.insectRepellent"/>
        </el-descriptions-item>
        <el-descriptions-item label="驱虫周期">
          {{ form.dewormingInterval ? form.dewormingInterval + '天' : '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="上回驱虫期">
          {{ form.insectRepellentLastTime || '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="宠物描述">
          {{ form.description || '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <dict-tag :options="dict.type.pet_pet_status" :value="form.status"/>
        </el-descriptions-item>
        <el-descriptions-item label="发现、所在地">
          {{ form.location || '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="所属店铺">
          {{ getShopName(form.deptId) || '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ form.createTime || '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="备注">
          {{ form.remark || '--' }}
        </el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="viewOpen = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {listPet, getPet, delPet, addPet, updatePet} from "@/api/pet/pet"
import {listPetShop} from "../../../api/pet/pet";

export default {
  name: "Pet",
  dicts: ['pet_pet_neuter', 'pet_pet_status', 'pet_pet_gender', 'pet_pet_insect_repellent', 'pet_pet_species', 'pet_pet_immunity'],
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
      // 宠物信息表格数据
      petList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查看详情对话框
      viewOpen: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 5,
        name: null,
        species: null,
        breed: null,
        gender: null,
        status: null,
        deptId: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        name: [
          {required: true, message: "宠物名称不能为空", trigger: "blur"}
        ],
        images: [
          {required: true, message: "宠物图片不能为空", trigger: "blur"}
        ],
        species: [
          {required: true, message: "物种不能为空", trigger: "change"}
        ],
        breed: [
          {required: true, message: "品种不能为空", trigger: "blur"}
        ],
        gender: [
          {required: true, message: "性别不能为空", trigger: "change"}
        ],
        age: [
          {required: true, message: "年龄不能为空", trigger: "blur"}
        ],
        healthInfo: [
          {required: true, message: "健康状态描述不能为空", trigger: "blur"}
        ],
        neuter: [
          {required: true, message: "是否绝育不能为空", trigger: "change"}
        ],
        immunity: [
          {required: true, message: "是否免疫( 0 -已免疫 , 1-未免疫 , 2 - 未知) 默认是2不能为空", trigger: "change"}
        ],
        insectRepellent: [
          {required: true, message: "是否驱虫 (0 - 未驱虫  1- 已驱虫 2- 未知) 默认是2不能为空", trigger: "change"}
        ],
        status:[
          {required: true, message: "宠物状态不能为空", trigger: "change"}
        ],
        deptId: [
          {required: true, message: "所属店铺或者站点不能为空", trigger: "blur"}
        ],
      },
      petShopList: []
    }
  },
  created() {
    this.getPetShopList()
    this.getList()
  },
  methods: {
    /** 查询宠物信息列表 */
    getList() {
      this.loading = true
      listPet(this.queryParams).then(response => {
        this.petList = response.rows
        this.total = response.total
      })
      this.loading = false
    },
    /** 根据deptId查询店铺名称*/
    getShopName(deptId) {
      if (!deptId && deptId !== 0) return '';

      const shop = this.petShopList.find(item =>
        String(item.shopId) === String(deptId) ||
        String(item.deptId) === String(deptId) ||
        String(item.id) === String(deptId)
      );

      return shop ? (shop.shopName || shop.name || '') : '';
    },

    /** 根据pet 里面的dept_id 查询店铺表的shop_name */
    getPetShopList(){
      this.loading = true
      listPetShop().then(response => {
        this.petShopList = response.rows
      })
      this.loading = false
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.viewOpen = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
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
        deptId: null,
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
      this.ids = selection.map(item => item.petId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加宠物信息"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const petId = row.petId || this.ids
      getPet(petId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改宠物信息"
      })
    },
    /** 查看按钮操作 */
    handleView(row) {
      this.reset()
      const petId = row.petId || this.ids
      getPet(petId).then(response => {
        this.form = response.data
        this.viewOpen = true
        this.title = "查看宠物信息"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.petId != null) {
            updatePet(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addPet(this.form).then(response => {
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
      const petIds = row.petId || this.ids
      this.$modal.confirm('是否确认删除宠物信息编号为"' + petIds + '"的数据项？').then(function () {
        return delPet(petIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {
      })
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('pet/pet/export', {
        ...this.queryParams
      }, `pet_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
