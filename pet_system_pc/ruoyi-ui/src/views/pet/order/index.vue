<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="订单号" prop="orderSn">
        <el-input
          v-model="queryParams.orderSn"
          placeholder="请输入订单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="下单手机号" prop="userPhone">
        <el-input
          v-model="userPhoneQuery"
          placeholder="请输入下单用户手机号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <!-- 搜索栏：店铺ID下拉框 -->
      <el-form-item label="店铺名称" prop="shopId">
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
          />
        </el-select>
      </el-form-item>
      <el-form-item label="订单状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择订单状态" clearable>
          <el-option
            v-for="dict in dict.type.pet_donation_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker clearable
                        v-model="queryParams.createTime"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="请选择创建时间">
        </el-date-picker>
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
          v-hasPermi="['pet:order:add']"
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
          v-hasPermi="['pet:order:edit']"
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
          v-hasPermi="['pet:order:remove']"
        >删除
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="orderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="序号" align="center" prop="orderId" type="index" width="50"/>
      <el-table-column label="订单号" align="center" prop="orderSn"/>
      <el-table-column label="用户昵称" align="center">
        <template slot-scope="scope">
          <span>{{ getUserName(scope.row.userId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="手机号" align="center">
        <template slot-scope="scope">
          <span>{{ getUserPhone(scope.row.userId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="店铺名称" align="center">
        <template slot-scope="scope">
          <span v-for="item in shopList" v-if="item.shopId === scope.row.shopId">{{ item.shopName }}</span>
        </template>
      </el-table-column>
      <!-- 表格展示：分转元 -->
      <el-table-column label="订单总金额（元）" align="center">
        <template slot-scope="scope">
          {{ scope.row.totalAmount ? (Number(scope.row.totalAmount) / 100).toFixed(2) + ' 元' : '——' }}
        </template>
      </el-table-column>
      <el-table-column label="订单状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.pet_donation_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <!--      <el-table-column label="备注" align="center" prop="remark"/>-->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['pet:order:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['pet:order:remove']"
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

    <!-- 添加或修改订单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <!-- 核心修改：订单号输入框完全禁用（灰色不可输入） -->
        <el-form-item label="订单号" prop="orderSn">
          <el-input
            v-model="form.orderSn"
            placeholder="系统自动生成"
            disabled/><!-- 直接写死禁用，无论新增还是修改都不可编辑 -->

        </el-form-item>
        <el-form-item label="下单用户" prop="userId">
          <el-select
            v-model="form.userId"
            filterable
            remote
            reserve-keyword
            placeholder="请输入手机号搜索用户"
            :remote-method="searchUsers"
            :loading="userLoading"
            @change="handleUserSelect">
            <el-option
              v-for="user in filteredUserList"
              :key="user.userId"
              :label="`${user.phonenumber} (${user.nickName || '未设置昵称'})`"
              :value="user.userId">
            </el-option>
          </el-select>
        </el-form-item>
        <!-- 店铺ID下拉框 -->
        <el-form-item label="店铺名称" prop="shopId">
          <el-select
            v-model="form.shopId"
            placeholder="请选择店铺"
            filterable
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
        <!-- 表单展示：元（自动计算，只读） -->
        <el-form-item label="订单总金额（元）" prop="totalAmountYuan">
          <el-input v-model="form.totalAmountYuan" placeholder="自动计算" disabled/>
        </el-form-item>
        <el-form-item label="订单状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择订单状态">
            <el-option
              v-for="dict in dict.type.pet_donation_status"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>

        <!-- 商品信息表格：单价展示元，内部存储分 -->
        <el-form-item label="商品信息">
          <el-table :data="form.orderItems" border style="width: 100%">
            <el-table-column label="商品">
              <template slot-scope="scope">
                <el-select
                  v-model="scope.row.resourceId"
                  filterable placeholder="请选择商品"
                  clearable
                  @change="handleProductChange(scope.row,$event)"
                >
                  <el-option
                    v-for="product in productList"
                    :key="product.resourceId"
                    :label="product.resourceName"
                    :value="product.resourceId">
                  </el-option>
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="数量">
              <template slot-scope="scope">
                <el-input-number
                  v-model="scope.row.quantity"
                  :min="1"
                  :max="999"
                  size="small"
                  @change="handleQuantityChange">
                </el-input-number>
              </template>
            </el-table-column>
            <!-- 商品单价展示元 -->
            <el-table-column label="单价（元）">
              <template slot-scope="scope">
                <span>{{ scope.row.price ? (Number(scope.row.price) / 100).toFixed(2) + ' 元' : '0.00 元' }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作">
              <template slot-scope="scope">
                <el-button
                  type="danger"
                  icon="el-icon-delete"
                  circle
                  size="mini"
                  @click="removeOrderItem(scope.$index)">
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-button
            type="primary"
            icon="el-icon-plus"
            size="small"
            @click="addOrderItem"
            style="margin-top: 10px;">
            添加商品
          </el-button>
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
import {listOrder, getOrder, delOrder, addOrder, updateOrder} from "@/api/pet/order"
import {listResource} from "@/api/pet/resource"
// 引入店铺列表接口（请根据实际接口路径修改）
import {listShop} from "@/api/pet/shop"
// 引入用户列表接口
import {listUser} from "@/api/system/user"

export default {
  name: "Order",
  dicts: ['pet_donation_status'],
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      orderList: [],
      title: "",
      open: false,
      productList: [],
      // 店铺列表数据
      shopList: [],
      // 用户列表数据
      userList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderSn: null,
        userId: null,
        shopId: null,
        totalAmount: null,
        status: null,
        createTime: null,
      },
      form: {},
      rules: {
        orderSn: [
          {required: true, message: "订单号不能为空", trigger: "blur"}
        ],
        userId: [
          {required: true, message: "下单用户不能为空", trigger: "blur"}
        ],
        shopId: [
          {required: true, message: "店铺id不能为空", trigger: "change"}
        ],
        totalAmountYuan: [
          {required: true, message: "订单总金额不能为空", trigger: "blur"}
        ],
        status: [
          {required: true, message: "订单状态不能为空", trigger: "change"}
        ],
      },
      userLoading: false,
      filteredUserList: [],
      userPhoneQuery: null
    }
  },
  created() {
    this.getList()
    // 初始化时加载店铺列表
    this.getShopList()
    // 初始化时加载用户列表
    this.getUserList()
  },
  methods: {
    searchUsers(phone) {
      if (phone) {
        this.userLoading = true;

        // 如果 userList 还没有加载，则先加载全部用户
        if (this.userList.length === 0) {
          this.getUserList().then(() => {
            this.doFilterUsers(phone);
            this.userLoading = false;
          }).catch(() => {
            this.userLoading = false;
          });
        } else {
          this.doFilterUsers(phone);
          this.userLoading = false;
        }
      } else {
        this.filteredUserList = [];
      }
    },

    doFilterUsers(phone) {
      this.filteredUserList = this.userList.filter(user =>
        user.phonenumber && user.phonenumber.includes(phone)
      );
    },

    handleUserSelect(userId) {
      // 用户选择后的处理逻辑（如有需要）
      //console.log('Selected user ID:', userId);
    },
    /** 查询订单列表 */
    getList() {
      this.loading = true
      listOrder(this.queryParams).then(response => {
        this.orderList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 获取店铺列表
    getShopList() {
      // 店铺列表查询参数（根据实际接口调整）
      const shopParams = {
        pageNum: 1,
        pageSize: 1000000 // 加载所有店铺
      }
      listShop(shopParams).then(response => {
        this.shopList = response.rows || []
      }).catch(err => {
        console.error("获取店铺列表失败：", err)
        this.$modal.msgError("获取店铺列表失败，请重试")
      })
    },
    // 修改 getUserList 方法使其返回 Promise
    getUserList() {
      const userParams = {
        pageNum: 1,
        pageSize: 10000000
      };
      return listUser(userParams).then(response => {
        console.log(response)
        this.userList = response.rows || [];
      }).catch(err => {
        console.error("获取用户列表失败：", err);
        this.$modal.msgError("获取用户列表失败，请重试");
      });
    },
    // 根据userId获取用户昵称
    getUserName(userId) {
      if (!userId) {
        return '--'
      }
      for (let i = 0; i < this.userList.length; i++) {
        if (this.userList[i].userId === userId) {
          return this.userList[i].nickName || '未注册用户'
        }
      }
      return '未知用户'
    },
    // 根据userId获取用户手机号
    getUserPhone(userId) {
      if (!userId) {
        return '--'
      }
      for (let i = 0; i < this.userList.length; i++) {
        if (this.userList[i].userId === userId) {
          return this.userList[i].phonenumber || '未知手机号'
        }
      }
      return '未知手机号'
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        orderId: null,
        orderSn: null, // 新增时自动生成或后端生成
        userId: null,
        shopId: null, // 下拉框绑定值（店铺ID）
        totalAmount: null, // 存储分（传给后端）
        totalAmountYuan: null, // 展示元（前端用）
        status: null,
        createTime: null,
        createBy: null,
        updateTime: null,
        updateBy: null,
        remark: null,
        isDeleted: null,
        orderItems: [] // 商品项数组
      }
      this.resetForm("form")
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      // 搜索时将元转分传给后端
      if (this.queryParams.totalAmount) {
        this.queryParams.totalAmount = Math.round(Number(this.queryParams.totalAmount) * 100);
      }

      // 如果输入了手机号，先转换为用户ID
      if (this.userPhoneQuery) {
        this.getUserIdByPhone(this.userPhoneQuery).then(userId => {
          if (userId) {
            this.queryParams.userId = userId;
            this.getList();
          } else {
            this.$modal.msgWarning("未找到该手机号对应的用户");
          }
        });
      } else {
        // 清空用户ID查询条件
        this.queryParams.userId = null;
        this.getList();
      }
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.userPhoneQuery = null;  // 重置手机号输入
      this.handleQuery();
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.orderId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 - 自动生成订单号（确保禁用状态下有值） */
    handleAdd() {
      this.reset();
      this.addOrderItem();
      this.getProductList();
      // 确保店铺列表已加载
      if (this.shopList.length === 0) {
        this.getShopList()
      }
      // 自动生成订单号（示例规则：ORD + 时间戳 + 3位随机数）
      this.form.orderSn = 'ORD' + Date.now() + Math.floor(Math.random() * 1000);
      this.open = true;
      this.title = "添加订单";
    },
    /** 修改按钮操作（分转元回显） */
    handleUpdate(row) {
      this.reset();
      const orderId = row.orderId || this.ids;
      getOrder(orderId).then(response => {
        this.form = response.data;
        // 后端返回的分转元，用于前端展示
        this.form.totalAmountYuan = this.form.totalAmount ? (Number(this.form.totalAmount) / 100).toFixed(2) : null;
        // 商品项单价分转元（内部仍保留分的price字段）
        if (this.form.orderItems && this.form.orderItems.length > 0) {
          this.form.orderItems.forEach((item, index) => {
            // 确保每个商品项都有响应式属性
            this.$set(this.form.orderItems, index, {
              ...item,
              priceYuan: item.price ? (Number(item.price) / 100).toFixed(2) : '0.00',
              quantity: item.quantity || 1
            });
          })
        }
        if (!this.form.orderItems || this.form.orderItems.length === 0) {
          this.addOrderItem();
        }
        this.getProductList();
        // 确保店铺列表已加载
        if (this.shopList.length === 0) {
          this.getShopList()
        }
        this.open = true;
        this.title = "修改订单";
      });
    },
    /** 提交按钮（元转分传输） */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          // 将前端展示的元转分，赋值给totalAmount传给后端
          this.form.totalAmount = Math.round(Number(this.form.totalAmountYuan) * 100);
          // 确保商品项的price是分（无需转换，已存储分）
          if (this.form.orderItems) {
            this.form.orderItems.forEach(item => {
              item.price = Number(item.price);
              item.quantity = Number(item.quantity);
            })
          }
          // 店铺ID确保为数字类型
          this.form.shopId = Number(this.form.shopId);

          const request = this.form.orderId ? updateOrder(this.form) : addOrder(this.form)
          request.then(() => {
            this.$modal.msgSuccess(this.form.orderId ? "修改成功" : "新增成功")
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
      const orderIds = row.orderId || this.ids
      this.$modal.confirm('是否确认删除订单编号为"' + orderIds + '"的数据项？').then(() => {
        return delOrder(orderIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {
      })
    },
    /** 导出按钮操作 */
    handleExport() {
      // 导出时如果有金额筛选，恢复元的格式（可选）
      const exportParams = {...this.queryParams}
      if (exportParams.totalAmount) {
        exportParams.totalAmount = (Number(exportParams.totalAmount) / 100).toFixed(2)
      }
      this.download('pet/order/export', exportParams, `order_${new Date().getTime()}.xlsx`)
    },
    /** 添加订单商品项 */
    addOrderItem() {
      if (!this.form.orderItems) {
        this.$set(this.form, 'orderItems', [])
      }
      this.form.orderItems.push({
        productId: null,
        productName: '',
        quantity: 1,
        price: 0, // 存储分
        priceYuan: '0.00' // 展示元
      });
      this.calculateTotalAmount();
    },
    /** 删除订单商品项 */
    removeOrderItem(index) {
      this.form.orderItems.splice(index, 1);
      this.calculateTotalAmount()
    },
    /** 获取商品列表 */
    getProductList() {
      const resourceParams = {
        pageNum: 1,
        pageSize: 1000000,
        status: 0
      }
      listResource(resourceParams).then(response => {
        this.productList = response.rows || []
      }).catch(err => {
        console.error("获取商品列表失败：", err)
        this.$modal.msgError("获取商品列表失败，请重试")
      })
    },
    /** 处理商品选择变化，同步价格（分转元展示） */
    handleProductChange(row, productId) {
      if (productId) {
        const selectedProduct = this.productList.find(product => product.resourceId === productId);
        if (selectedProduct) {
          this.$set(row, 'productName', selectedProduct.resourceName);
          this.$set(row, 'price', selectedProduct.price || 0); // 存储分
          this.$set(row, 'priceYuan', selectedProduct.price ? (Number(selectedProduct.price) / 100).toFixed(2) : '0.00'); // 展示元
        }
      } else {
        this.$set(row, 'productName', '');
        this.$set(row, 'price', 0);
        this.$set(row, 'priceYuan', '0.00');
      }
      this.calculateTotalAmount();
    },
    /** 计算订单总金额（分转元展示） */
    calculateTotalAmount() {
      let totalFen = 0;
      if (this.form.orderItems && this.form.orderItems.length > 0) {
        this.form.orderItems.forEach(item => {
          if (item.quantity && item.price) {
            totalFen += item.quantity * Number(item.price);
          }
        });
      }
      // 分转元，保留2位小数用于前端展示
      this.$set(this.form, 'totalAmountYuan', (totalFen / 100).toFixed(2));
      // 同步分的字段（可选，提交时再转也可）
      this.form.totalAmount = totalFen;
    },
    /** 处理数量变化 */
    handleQuantityChange(value, oldValue) {
      this.$nextTick(() => {
        this.calculateTotalAmount();
        // 强制更新组件以确保视图刷新
        this.$forceUpdate();
      });
    },
    // 根据手机号获取用户ID
    getUserIdByPhone(phone) {
      // 先在已加载的用户列表中查找
      const user = this.userList.find(u => u.phonenumber === phone);
      if (user) {
        return Promise.resolve(user.userId);
      }

      // 如果没找到，调用后台接口查询
      return listUser({phonenumber: phone, pageNum: 1, pageSize: 1}).then(response => {
        if (response.rows && response.rows.length > 0) {
          return response.rows[0].userId;
        }
        return null;
      }).catch(() => {
        return null;
      });
    }


  }
}
</script>
