<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="标题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入标题"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="创作者" prop="author">
        <el-input
          v-model="queryParams.author"
          placeholder="请输入创作者"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="发布状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择发布状态" clearable>
          <el-option
            v-for="dict in dict.type.pet_article_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="发布时间" prop="publishTime">
        <el-date-picker clearable
                        v-model="queryParams.publishTime"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="请选择发布时间">
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
          v-hasPermi="['pet:article:add']"
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
          v-hasPermi="['pet:article:edit']"
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
          v-hasPermi="['pet:article:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="articleList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" align="center" type="index" />
      <el-table-column label="标题" align="center" prop="title" />
      <el-table-column label="封面图" align="center" prop="coverImage" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.coverImage" :width="50" :height="50"/>
        </template>
      </el-table-column>
<!--      <el-table-column label="文章内容" align="center" prop="content" width="200">-->
<!--        <template slot-scope="scope">-->
<!--          <div class="content-preview">-->
<!--            {{ getContentPreview(scope.row.content) }}-->
<!--          </div>-->
<!--        </template>-->
<!--      </el-table-column>-->
      <el-table-column label="创作者" align="center" prop="author" />
      <el-table-column label="浏览量" align="center" prop="viewCount" />
      <el-table-column label="发布状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.pet_article_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="发布时间" align="center" prop="publishTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.publishTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
            v-hasPermi="['pet:article:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['pet:article:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['pet:article:remove']"
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

    <!-- 添加或修改科普文章对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="封面图" prop="coverImage">
          <image-upload v-model="form.coverImage"/>
        </el-form-item>
        <el-form-item label="文章内容">
          <editor v-model="form.content" :min-height="192"/>
        </el-form-item>
        <el-form-item label="创作者" prop="author">
          <el-input v-model="form.author" placeholder="请输入创作者" />
        </el-form-item>
        <el-form-item label="发布状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择发布状态">
            <el-option
              v-for="dict in dict.type.pet_article_status"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="是否顶置" prop="isTop">
          <el-select v-model="form.isTop" placeholder="请选择是否顶置">
            <el-option
              v-for="dict in dict.type.pet_article_top"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="发布时间" prop="publishTime">
          <el-date-picker clearable
                          v-model="form.publishTime"
                          type="date"
                          value-format="yyyy-MM-dd"
                          placeholder="请选择发布时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 科普文章详情对话框 -->
    <el-dialog title="科普文章详情" :visible.sync="detailOpen" width="800px" append-to-body>
      <el-form ref="detailForm" :model="detailForm" label-width="80px" :disabled="true">
        <el-form-item label="标题" prop="title">
          <el-input v-model="detailForm.title" />
        </el-form-item>
        <el-form-item label="封面图" prop="coverImage">
          <image-upload v-model="detailForm.coverImage" :disabled="true"/>
        </el-form-item>
        <el-form-item label="文章内容">
          <div class="detail-content">
            <div v-html="detailForm.content" class="content-html"></div>
          </div>
        </el-form-item>
        <el-form-item label="创作者" prop="author">
          <el-input v-model="detailForm.author" />
        </el-form-item>
        <el-form-item label="发布状态" prop="status">
          <el-select v-model="detailForm.status" placeholder="请选择发布状态">
            <el-option
              v-for="dict in dict.type.pet_article_status"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="是否顶置" prop="isTop">
          <el-select v-model="detailForm.isTop" placeholder="请选择是否顶置">
            <el-option
              v-for="dict in dict.type.pet_article_top"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="发布时间" prop="publishTime">
          <el-date-picker clearable
                          v-model="detailForm.publishTime"
                          type="date"
                          value-format="yyyy-MM-dd"
                          placeholder="请选择发布时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="detailForm.remark" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listArticle, getArticle, delArticle, addArticle, updateArticle } from "@/api/pet/article"

export default {
  name: "Article",
  dicts: ['pet_article_status', 'pet_article_top'],
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
      // 科普文章表格数据
      articleList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否显示详情弹出层
      detailOpen: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: null,
        author: null,
        status: null,
        publishTime: null,
      },
      // 表单参数
      form: {},
      // 详情表单参数
      detailForm: {},
      // 表单校验
      rules: {
        title: [
          { required: true, message: "标题不能为空", trigger: "blur" }
        ],
        coverImage: [
          { required: true, message: "封面图不能为空", trigger: "blur" }
        ],
        author: [
          { required: true, message: "创作者不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "发布状态不能为空", trigger: "change" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询科普文章列表 */
    getList() {
      this.loading = true
      listArticle(this.queryParams).then(response => {
        this.articleList = response.rows
        this.total = response.total
        this.loading = false
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
        articleId: null,
        title: null,
        coverImage: null,
        content: null,
        author: null,
        viewCount: null,
        status: null,
        isTop: null,
        publishTime: null,
        createTime: null,
        createBy: null,
        updateTime: null,
        updateBy: null,
        remark: null,
        isDeleted: null
      }
      this.resetForm("form")
    },
    /** 获取内容预览 */
    getContentPreview(content) {
      if (!content) return ''
      // 移除HTML标签
      const plainText = content.replace(/<[^>]+>/g, '')
      // 截取前100个字符
      if (plainText.length > 100) {
        return plainText.substring(0, 100) + '...'
      }
      return plainText
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
      this.ids = selection.map(item => item.articleId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加科普文章"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const articleId = row.articleId || this.ids
      getArticle(articleId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改科普文章"
      })
    },
    /** 详情按钮操作 */
    handleDetail(row) {
      const articleId = row.articleId
      getArticle(articleId).then(response => {
        this.detailForm = response.data
        this.detailOpen = true
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.articleId != null) {
            updateArticle(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addArticle(this.form).then(response => {
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
      const articleIds = row.articleId || this.ids
      this.$modal.confirm('是否确认删除科普文章编号为"' + articleIds + '"的数据项？').then(function() {
        return delArticle(articleIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('pet/article/export', {
        ...this.queryParams
      }, `article_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>

<style scoped>
.content-preview {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  line-height: 1.4;
  max-height: 4.2em;
}

.detail-content {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 10px;
  max-height: 300px;
  overflow-y: auto;
}

.content-html {
  word-break: break-word;
}

.content-html >>> img {
  max-width: 100%;
  height: auto;
}
</style>
