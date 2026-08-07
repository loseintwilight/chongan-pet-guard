<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="70px">
      <el-form-item label="店铺名称" prop="shopId">
        <el-select v-model="queryParams.shopId" placeholder="请选择所属店铺" clearable>
          <el-option
            v-for="item in shopListOptions"
            :key="item.shopId"
            :label="item.shopName"
            :value="item.shopId">
          </el-option>
        </el-select>
      </el-form-item>
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
      <el-form-item label="预计开始托养时间" prop="startDate">
        <el-date-picker clearable
                        v-model="queryParams.startDate"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="请选择预计开始托养时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="预计结束托养时间" prop="endDate">
        <el-date-picker clearable
                        v-model="queryParams.endDate"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="请选择预计结束托养时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="托养状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择托养状态" clearable>
          <el-option
            v-for="dict in dict.type.pet_boarding_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="审核时间" prop="reviewTime">
        <el-date-picker clearable
                        v-model="queryParams.reviewTime"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="请选择审核时间">
        </el-date-picker>
      </el-form-item>
      <!--      <el-form-item label="审核备注" prop="reviewRemark">-->
      <!--        <el-input-->
      <!--          v-model="queryParams.reviewRemark"-->
      <!--          placeholder="请输入审核备注"-->
      <!--          clearable-->
      <!--          @keyup.enter.native="handleQuery"-->
      <!--        />-->
      <!--      </el-form-item>-->
      <el-form-item label="支付状态" prop="paymentStatus">
        <el-select v-model="queryParams.paymentStatus" placeholder="请选择支付状态" clearable>
          <el-option
            v-for="dict in dict.type.pet_boarding_payment_status"
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
          v-hasPermi="['pet:boarding:add']"
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
          v-hasPermi="['pet:boarding:edit']"
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
          v-hasPermi="['pet:boarding:remove']"
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
          v-hasPermi="['pet:boarding:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="boardingList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="序号" align="center" type="index"/>
      <el-table-column label="店铺名称" align="center" prop="shopId" show-overflow-tooltip>
        <template slot-scope="scope">
          <span v-for="item in shopListOptions" v-if="item.shopId === scope.row.shopId">{{ item.shopName }}</span>
        </template>
      </el-table-column>
      <el-table-column label="宠物图片" align="center" prop="images" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.images" :width="50" :height="50"/>
        </template>
      </el-table-column>
      <el-table-column label="宠物名称" align="center" prop="name"/>
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
      <el-table-column label="年龄/月" align="center" prop="age">
        <template slot-scope="scope">
          {{ scope.row.age + "个月" }}
        </template>
      </el-table-column>
      <el-table-column label="健康状态" align="center" prop="healthInfo" show-overflow-tooltip/>
      <el-table-column label="特殊要求" align="center" prop="specialRequirement"/>
      <el-table-column label="托养状态" align="center" prop="status" width="120">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.pet_boarding_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="支付状态" align="center" prop="paymentStatus">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.pet_boarding_payment_status" :value="scope.row.paymentStatus"/>
        </template>
      </el-table-column>

      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="300px">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-s-grid"
            @click="handleDetail(scope.row)"
            v-hasPermi="['pet:boarding:list']"
          >详情
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['pet:boarding:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['pet:boarding:remove']"
          >删除
          </el-button>
          <el-button
            :disabled="scope.row.status !== 0"
            size="mini"
            type="text"
            icon="el-icon-edit-outline"
            @click="handleAudit(scope.row)"
            v-hasPermi="['pet:boarding:edit']"
          >审核
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

    <!-- 添加或修改宠物托养对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="900px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <!-- 左侧表单列 -->
            <!--            <el-form-item label="用户id" prop="userId">-->
            <!--              <el-input v-model="form.userId" placeholder="请输入用户id"/>-->
            <!--            </el-form-item>-->
            <el-form-item label="所属店铺" prop="shopId">
              <el-select v-model="form.shopId" placeholder="请选择所属店铺名称" clearable>
                <el-option
                  v-for="item in shopListOptions"
                  :key="item.shopId"
                  :label="item.shopName"
                  :value="item.shopId">
                </el-option>
              </el-select>
            </el-form-item>

            <el-form-item label="宠物名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入宠物名称"/>
            </el-form-item>
            <el-form-item label="支付状态" prop="paymentStatus">
              <el-select v-model="form.paymentStatus" placeholder="请选择支付状态">
                <el-option
                  v-for="dict in dict.type.pet_boarding_payment_status"
                  :key="dict.value"
                  :label="dict.label"
                  :value="parseInt(dict.value)">
                </el-option>
              </el-select>
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
                  :value="parseInt(dict.value)">
                </el-option>
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
                  :value="parseInt(dict.value)">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="年龄/月" prop="age">
              <el-input-number v-model="form.age" :min="0"></el-input-number>
            </el-form-item>
            <el-form-item label="健康状态" prop="healthInfo">
              <el-input v-model="form.healthInfo" placeholder="请输入健康状态"/>
            </el-form-item>
            <el-form-item label="是否绝育" prop="neuter">
              <el-select v-model="form.neuter" placeholder="请选择是否绝育">
                <el-option
                  v-for="dict in dict.type.pet_pet_neuter"
                  :key="dict.value"
                  :label="dict.label"
                  :value="parseInt(dict.value)">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="是否免疫" prop="immunity">
              <el-select v-model="form.immunity" placeholder="请选择是否免疫">
                <el-option
                  v-for="dict in dict.type.pet_pet_immunity"
                  :key="dict.value"
                  :label="dict.label"
                  :value="parseInt(dict.value)">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="是否驱虫" prop="insectRepellent">
              <el-select v-model="form.insectRepellent" placeholder="请选择是否驱虫">
                <el-option
                  v-for="dict in dict.type.pet_pet_insect_repellent"
                  :key="dict.value"
                  :label="dict.label"
                  :value="parseInt(dict.value)">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="驱虫周期，天" prop="dewormingInterval">
              <el-input-number v-model="form.dewormingInterval" :min="0"></el-input-number>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <!-- 右侧表单列 -->
            <el-form-item label="宠物描述" prop="description">
              <el-input v-model="form.description" placeholder="请输入宠物描述"/>
            </el-form-item>
            <el-form-item label="预计开始托养时间" prop="startDate">
              <el-date-picker clearable
                              v-model="form.startDate"
                              type="date"
                              value-format="yyyy-MM-dd"
                              placeholder="请选择预计开始托养时间">
              </el-date-picker>
            </el-form-item>
            <el-form-item label="预计结束时间" prop="endDate">
              <el-date-picker clearable
                              v-model="form.endDate"
                              type="date"
                              value-format="yyyy-MM-dd"
                              placeholder="请选择预计结束时间">
              </el-date-picker>
            </el-form-item>
            <el-form-item label="总托养天数" prop="totalDays">
              <el-input-number v-model="form.totalDays" :min="0"></el-input-number>
            </el-form-item>
            <el-form-item label="总费用/元" prop="totalAmount">
              <el-input v-model="form.totalAmount" placeholder="请输入总费用"/>
            </el-form-item>
            <el-form-item label="特殊要求" prop="specialRequirement">
              <el-input v-model="form.specialRequirement" placeholder="请输入特殊要求"/>
            </el-form-item>
            <el-form-item label="紧急联系人" prop="emergencyContact">
              <el-input v-model="form.emergencyContact" placeholder="请输入紧急联系人"/>
            </el-form-item>
            <el-form-item label="紧急联系人电话" prop="emergencyPhone">
              <el-input v-model="form.emergencyPhone" placeholder="请输入紧急联系人电话"/>
            </el-form-item>
            <el-form-item label="托养状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择托养状态">
                <el-option
                  v-for="dict in dict.type.pet_boarding_status"
                  :key="dict.value"
                  :label="dict.label"
                  :value="parseInt(dict.value)"
                  :disabled="[0,6, 7].includes(parseInt(dict.value))"
                >
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="审核时间" prop="reviewTime">
              {{ form.status === 0 ? "未审核" : form.reviewTime }}
            </el-form-item>
            <el-form-item label="审核备注" prop="reviewRemark">
              <el-input v-model="form.reviewRemark" disabled placeholder="请输入审核备注"/>
            </el-form-item>
            <el-form-item label="实际送宠时间" prop="checkInTime">
              <el-date-picker clearable
                              v-model="form.checkInTime"
                              type="date"
                              value-format="yyyy-MM-dd"
                              placeholder="请选择实际送宠时间">
              </el-date-picker>
            </el-form-item>
            <el-form-item label="实际接宠时间" prop="checkOutTime">
              <el-date-picker clearable
                              v-model="form.checkOutTime"
                              type="date"
                              value-format="yyyy-MM-dd"
                              placeholder="请选择实际接宠时间">
              </el-date-picker>
            </el-form-item>
            <el-form-item label="上回驱虫日期" prop="insectRepellentLastTime">
              <el-date-picker clearable
                              v-model="form.insectRepellentLastTime"
                              type="date"
                              value-format="yyyy-MM-dd"
                              placeholder="请选择上回驱虫日期">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>


    <!--  审核宠物托养申请对话框  -->
    <el-dialog :title="title" :visible.sync="auditOpen" width="700px" append-to-body>
      <div>
        <el-descriptions direction="vertical" :column="4" border>
          <el-descriptions-item label="店铺名称" v-for="item in shopListOptions" v-if="item.shopId === form.shopId">
            {{ item.shopName }}
          </el-descriptions-item>
          <el-descriptions-item label="宠物名称">{{ form.name }}</el-descriptions-item>
          <el-descriptions-item label="宠物物种">
            <dict-tag :options="dict.type.pet_pet_species" :value="form.species"/>
          </el-descriptions-item>
          <el-descriptions-item label="宠物品种">{{ form.breed }}</el-descriptions-item>
          <el-descriptions-item label="性别">
            <dict-tag :options="dict.type.pet_pet_gender" :value="form.gender"></dict-tag>
          </el-descriptions-item>
          <el-descriptions-item label="健康状态描述">{{ form.healthInfo }}</el-descriptions-item>
          <el-descriptions-item label="是否绝育">
            <dict-tag :options="dict.type.pet_pet_neuter" :value="form.neuter"></dict-tag>
          </el-descriptions-item>
          <el-descriptions-item label="是否免疫">
            <dict-tag :options="dict.type.pet_pet_immunity" :value="form.immunity"></dict-tag>
          </el-descriptions-item>
          <el-descriptions-item label="是否驱虫">
            <dict-tag :options="dict.type.pet_pet_insect_repellent" :value="form.insectRepellent"></dict-tag>
          </el-descriptions-item>
          <el-descriptions-item label="驱虫周期/日">{{ form.dewormingInterval }}</el-descriptions-item>
          <el-descriptions-item label="上回驱虫日期">{{ form.insectRepellentLastTime }}</el-descriptions-item>
          <el-descriptions-item label="宠物描述">{{ form.description }}</el-descriptions-item>
          <el-descriptions-item label="预计开始托养时间">{{ form.startDate }}</el-descriptions-item>
          <el-descriptions-item label="预计结束托养时间">{{ form.endDate }}</el-descriptions-item>
          <el-descriptions-item label="总托养天数">{{ form.totalDays }}</el-descriptions-item>
          <el-descriptions-item label="总费用/元">{{ form.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="特殊要求">{{ form.specialRequirement }}</el-descriptions-item>
          <el-descriptions-item label="紧急联系人">{{ form.emergencyContact }}</el-descriptions-item>
          <el-descriptions-item label="紧急联系人电话">{{ form.emergencyPhone }}</el-descriptions-item>
          <el-descriptions-item label="托养状态">
            <dict-tag :options="dict.type.pet_boarding_status" :value="form.status"></dict-tag>
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <br>
      <el-form ref="auditForm" :model="form" :rules="auditRules" label-width="100px">
        <el-form-item label="审核状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择托养状态">
            <el-option
              v-for="dict in dict.type.pet_boarding_status"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
              :disabled="![0,6, 7].includes(parseInt(dict.value))"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="审核备注" prop="reviewRemark">
          <el-input
            type="textarea"
            v-model="form.reviewRemark"
            placeholder="请输入审核备注"
            :rows="3">
          </el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitAudit">确 定</el-button>
        <el-button @click="cancelAudit">取 消</el-button>
      </div>
    </el-dialog>

    <!--  查看宠物托养详情信息对话框  -->
    <el-dialog :title="title" :visible.sync="petDetailOpen" width="800px" append-to-body>
      <el-descriptions direction="vertical" :column="4" border>
        <el-descriptions-item label="店铺名称" v-for="item in shopListOptions" v-if="item.shopId === form.shopId">
          {{ item.shopName }}
        </el-descriptions-item>
        <el-descriptions-item label="宠物名称">{{ form.name }}</el-descriptions-item>
        <el-descriptions-item label="宠物物种">
          <dict-tag :options="dict.type.pet_pet_species" :value="form.species"/>
        </el-descriptions-item>
        <el-descriptions-item label="宠物品种">{{ form.breed }}</el-descriptions-item>
        <el-descriptions-item label="性别">
          <dict-tag :options="dict.type.pet_pet_gender" :value="form.gender"></dict-tag>
        </el-descriptions-item>
        <el-descriptions-item label="健康状态描述">{{ form.healthInfo }}</el-descriptions-item>
        <el-descriptions-item label="是否绝育">
          <dict-tag :options="dict.type.pet_pet_neuter" :value="form.neuter"></dict-tag>
        </el-descriptions-item>
        <el-descriptions-item label="是否免疫">
          <dict-tag :options="dict.type.pet_pet_immunity" :value="form.immunity"></dict-tag>
        </el-descriptions-item>
        <el-descriptions-item label="是否驱虫">
          <dict-tag :options="dict.type.pet_pet_insect_repellent" :value="form.insectRepellent"></dict-tag>
        </el-descriptions-item>
        <el-descriptions-item label="驱虫周期/日">{{ form.dewormingInterval }}</el-descriptions-item>
        <el-descriptions-item label="上回驱虫日期">{{ form.insectRepellentLastTime }}</el-descriptions-item>
        <el-descriptions-item label="宠物描述">{{ form.description }}</el-descriptions-item>
        <el-descriptions-item label="预计开始托养时间">{{ form.startDate }}</el-descriptions-item>
        <el-descriptions-item label="预计结束托养时间">{{ form.endDate }}</el-descriptions-item>
        <el-descriptions-item label="总托养天数">{{ form.totalDays }}</el-descriptions-item>
        <el-descriptions-item label="总费用/元">{{ form.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="特殊要求">{{ form.specialRequirement }}</el-descriptions-item>
        <el-descriptions-item label="紧急联系人">{{ form.emergencyContact }}</el-descriptions-item>
        <el-descriptions-item label="紧急联系人电话">{{ form.emergencyPhone }}</el-descriptions-item>
        <el-descriptions-item label="托养状态">
          <dict-tag :options="dict.type.pet_boarding_status" :value="form.status"></dict-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>


    <!--  发送通知弹框  -->
    <el-dialog :title="title" :visible.sync="petNoticeOpen" width="600px" append-to-body>
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
import {listBoarding, getBoarding, delBoarding, addBoarding, updateBoarding} from "@/api/pet/boarding"
import {listShop} from "@/api/pet/shop";
import {listTemplate} from "@/api/pet/template";
import {sendNoticeByTemplate} from "@/api/pet/template";
import {addUserNotice} from "../../../api/pet/userNotice";

export default {
  name: "Boarding",
  dicts: ['pet_boarding_status', 'pet_pet_neuter', 'pet_boarding_payment_status', 'pet_pet_gender', 'pet_pet_insect_repellent', 'pet_pet_species', 'pet_pet_immunity'],
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
      // 宠物托养表格数据
      boardingList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      petDetailOpen: false,
      auditOpen: false,
      petNoticeOpen: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 5,
        shopId: null,
        shopName: null,
        name: null,
        species: null,
        breed: null,
        gender: null,
        neuter: null,
        immunity: null,
        insectRepellent: null,
        startDate: null,
        endDate: null,
        status: null,
        reviewTime: null,
        reviewRemark: null,
        paymentStatus: null,
      },
      // 表单参数
      form: {},
      // 通知复合表单
      noticeForm: {
        boardingId: null,      // 托养记录ID
        userId: null,        // 用户ID
        shopId: null,        //店铺名称
        shopName: null,      // 店铺ID
        status: null,        // 托养状态
        petId: null,         //宠物id
        petName: null,       // 宠物名称
        templateId: null,     // 模板ID
        emergencyContact: null,   // 紧急联系人
        title: '',           // 通知标题
        content: ''          // 通知内容
      },
      // 表单校验
      rules: {
        shopId: [
          {required: true, message: "店铺id不能为空", trigger: "blur"}
        ],
        name: [
          {required: true, message: "宠物名称不能为空", trigger: "blur"}
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
          {required: true, message: "健康状态不能为空", trigger: "blur"}
        ],
        neuter: [
          {required: true, message: "是否绝育不能为空", trigger: "change"}
        ],
        immunity: [
          {required: true, message: "是否免疫不能为空", trigger: "change"}
        ],
        insectRepellent: [
          {required: true, message: "是否驱虫不能为空", trigger: "change"}
        ],
        description: [
          {required: true, message: "宠物描述不能为空", trigger: "blur"}
        ],
        startDate: [
          {required: true, message: "预计开始托养时间不能为空", trigger: "blur"}
        ],
        endDate: [
          {required: true, message: "预计结束时间不能为空", trigger: "blur"}
        ],
        images: [
          {required: true, message: "宠物图片不能为空", trigger: "blur"}
        ]
      },
      auditRules: {
        status: [
          {required: true, message: "请选择审核状态", trigger: "change"}
        ],
        reviewRemark: [
          {required: true, message: "审核备注不能为空", trigger: "blur"}
        ]
      },
      shopListOptions: [],
      auditParams: {},
      templateListOptions: []
    }
  },
  created() {
    this.getList()
    this.getShopList()
    this.getTemplateList()
  },
  methods: {
    /** 查询通知模板列表 */
    getTemplateList() {
      const query = {
        pageNum: 1,
        pageSize: 1000,
        status: 0
      }
      listTemplate(query).then(response => {
        this.templateListOptions = response.rows.filter(template => {
          // 检查模板名称或内容是否包含"救助"
          return template.templateName.includes('托养') ||
            (template.content && template.content.includes('托养'))
        })
        if (this.templateListOptions.length === 0) {
          console.warn('没有找到包含"托养"的模板')
          this.$message.warning('没有找到托养相关的模板')
        }
      }).catch(error => {
        console.error('获取模板列表失败:', error)
        this.$message.error("获取模板列表失败")
      })
    },
    /** 查询店铺列表 */
    getShopList() {
      const query = {
        pageNum: 1,
        pageSize: 100000,
      }
      listShop(query).then(response => {
        this.shopListOptions = response.rows
      })
    },
    /** 查询宠物托养列表 */
    getList() {
      this.loading = true
      listBoarding(this.queryParams).then(response => {
        this.boardingList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    /** 提交审核 */
    submitAudit() {
      this.$refs["auditForm"].validate(valid => {
        if (valid) {
          const auditData = {
            boardingId: this.form.boardingId,  // 确保使用正确的ID
            status: this.form.status,
            reviewRemark: this.form.reviewRemark
          };

          updateBoarding(auditData).then(response => {
            this.$modal.msgSuccess("审核状态修改成功")
            this.auditOpen = false
            this.getList()
          })
        }
      })
    },

    /** 取消审核 */
    cancelAudit() {
      this.auditOpen = false
      this.resetForm("auditForm")
    },

    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        boardingId: null,
        userId: null,
        shopId: null,
        shopName: null,
        name: null,
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
        startDate: null,
        endDate: null,
        totalDays: null,
        totalAmount: null,
        specialRequirement: null,
        emergencyContact: null,
        emergencyPhone: null,
        status: null,
        reviewerId: null,
        reviewTime: null,
        reviewRemark: null,
        checkInTime: null,
        checkOutTime: null,
        paymentStatus: null,
        createTime: null,
        updateTime: null,
        isDeleted: null,
        images: null,

      }
      this.resetForm("form")
      this.resetNoticeForm()
    },
    resetNoticeForm() {
      this.noticeForm = {
        boardingId: null,      // 托养记录ID
        userId: null,        // 用户ID
        shopId: null,        //店铺名称
        shopName: null,      // 店铺ID
        startDate: null,     //预计托养时间
        startTime: null,     //实际托养时间
        status: null,        // 托养状态
        petId: null,         //宠物id
        petName: null,       // 宠物名称
        templateId: null,     // 模板ID
        emergencyContact: null,   // 紧急联系人
        title: '',           // 通知标题
        content: ''          // 通知内容
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
      this.ids = selection.map(item => item.boardingId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加宠物托养"
      this.form.status = 0
    },
    /** 查看详情操作 */
    handleDetail(row) {
      this.reset()
      getBoarding(row.boardingId).then(response => {
        this.form = response.data
        this.petDetailOpen = true
        this.title = "宠物托养详情"
      })
    },
    /** 宠物托养信息审核 */
    handleAudit(row) {
      this.reset()
      // 确保创建新的对象而不是引用
      const rowData = {...row};
      getBoarding(rowData.boardingId).then(response => {
        this.form = {...response.data}
        this.auditOpen = true
        this.title = "托养申请审核"
      })
    },


    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      getBoarding(row.boardingId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改宠物托养信息"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.boardingId != null) {
            updateBoarding(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addBoarding(this.form).then(response => {
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
      const boardingIds = row.boardingId || this.ids
      this.$modal.confirm('是否确认删除宠物托养编号为"' + boardingIds + '"的数据项？').then(function () {
        return delBoarding(boardingIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {
      })
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('pet/boarding/export', {
        ...this.queryParams
      }, `boarding_${new Date().getTime()}.xlsx`)
    },

    //点击通知按钮
    handleNotice(row) {
      this.noticeForm.boardingId = row.boardingId
      this.noticeForm.userId = row.userId
      this.noticeForm.petName = row.name
      this.noticeForm.startDate = row.startDate
      this.noticeForm.shopId = row.shopId
      this.noticeForm.status = row.status
      this.noticeForm.emergencyContact = row.emergencyContact

      const selectShop = this.shopListOptions.find(shop => shop.shopId = this.noticeForm.shopId )
      this.noticeForm.shopName = selectShop.shopName;

      if (row.status == 0) {
        const selectedTemplate = this.templateListOptions.find(template => template.templateId === 5)
        this.noticeForm.templateId = 5
        if (selectedTemplate) {
          this.noticeForm.title = selectedTemplate.title || ''

          // 替换模板中的占位符
          let content = selectedTemplate.content || ''
          content = this.replaceTemplateVariables(content)
          this.noticeForm.content = content
        }
      }
      if (row.status == 7){
        const selectedTemplate = this.templateListOptions.find(template => template.templateId === 6)
        this.noticeForm.templateId = 6
        if (selectedTemplate) {
          this.noticeForm.title = selectedTemplate.title || ''

          // 替换模板中的占位符
          let content = selectedTemplate.content || ''
          content = this.replaceTemplateVariables(content)
          this.noticeForm.content = content
        }
      }
      if (row.status == 6){
        const selectedTemplate = this.templateListOptions.find(template => template.templateId === 7)
        this.noticeForm.templateId = 7
        if (selectedTemplate) {
          this.noticeForm.title = selectedTemplate.title || ''

          // 替换模板中的占位符
          let content = selectedTemplate.content || ''
          content = this.replaceTemplateVariables(content)
          this.noticeForm.content = content
        }
      }
      if (row.status == 1){
        const selectedTemplate = this.templateListOptions.find(template => template.templateId === 7)
        this.noticeForm.templateId = 7
        if (selectedTemplate) {
          this.noticeForm.title = selectedTemplate.title || ''

          // 替换模板中的占位符
          let content = selectedTemplate.content || ''
          content = this.replaceTemplateVariables(content)
          this.noticeForm.content = content
        }
      }
      if (row.status == 2){
        const selectedTemplate = this.templateListOptions.find(template => template.templateId === 8)
        this.noticeForm.templateId = 8
        if (selectedTemplate) {
          this.noticeForm.title = selectedTemplate.title || ''

          // 替换模板中的占位符
          let content = selectedTemplate.content || ''
          content = this.replaceTemplateVariables(content)
          this.noticeForm.content = content
        }
      }
      if (row.status == 4){
        const selectedTemplate = this.templateListOptions.find(template => template.templateId === 9)
        this.noticeForm.templateId = 9
        if (selectedTemplate) {
          this.noticeForm.title = selectedTemplate.title || ''

          // 替换模板中的占位符
          let content = selectedTemplate.content || ''
          content = this.replaceTemplateVariables(content)
          this.noticeForm.content = content
        }
      }
      if (row.status == 5){
        const selectedTemplate = this.templateListOptions.find(template => template.templateId === 10)
        this.noticeForm.templateId = 10
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
        '{user_name}': this.noticeForm.emergencyContact || '未知用户',
        '{pet_name}': this.noticeForm.petName || '未知宠物',
        '{start_date}': this.noticeForm.startDate || '未知时间',
        '{address}': this.noticeForm.shopName || '未知店铺',
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
            boardingId: this.noticeForm.boardingId,
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
              this.petNoticeOpen = false
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
      this.resetNoticeForm();
    }

  }
}
</script>
