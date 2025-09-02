<template>
  <div class="app-container">
    <!-- 党组织树 -->
    <el-row :gutter="20" class="full-height-row">
      <!--党组织数据-->
      <el-col :span="4" :xs="24" class="dept-col">
        <el-scrollbar height="calc(100vh - 120px)">
          <div class="dept-container">
            <div class="head-container">
              <el-input
                v-model="deptName"
                placeholder="请输入党组织名称"
                clearable
                size="small"
                prefix-icon="el-icon-search"
                style="margin-bottom: 20px"
              />
            </div>
            <div class="head-container">
              <el-tree
                :data="deptOptions"
                :props="defaultProps"
                :expand-on-click-node="false"
                :filter-node-method="filterNode"
                ref="deptTree"
                node-key="id"
                default-expand-all
                highlight-current
                @node-click="handleNodeClick"
              >
                <!-- 使用 scoped slot 自定义节点内容 -->
                <template #default="{ node, data }">
                    <span class="custom-tree-node">
                      <BaseImage
                        :src="data.deptImg ? data.deptImg : ''"
                        :width="20"
                        :height="20"
                      />
                      <span>{{ node.label }}</span>
                    </span>
                </template>
              </el-tree>
            </div>
          </div>
        </el-scrollbar>
      </el-col>
      <!--职务数据-->
      <el-col :span="20" :xs="24" class="content-col">
        <div class="content-container" style="height: calc(100vh - 120px); overflow-y: auto; overflow-x: hidden;">
          <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
            <el-form-item label="职务描述" prop="postDescribes">
              <el-input
                v-model="queryParams.postDescribes"
                placeholder="请输入职务描述"
                clearable
                @keyup.enter.native="handleQuery"
              />
            </el-form-item>
            <el-form-item label="职务名称" prop="postName">
              <el-input
                v-model="queryParams.postName"
                placeholder="请输入职务名称"
                clearable
                @keyup.enter.native="handleQuery"
              />
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-select v-model="queryParams.status" placeholder="职务状态" clearable>
                <el-option
                  v-for="dict in dict.type.sys_normal_disable"
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
                v-hasPermi="['system:post:add']"
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
                v-hasPermi="['system:post:edit']"
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
                v-hasPermi="['system:post:remove']"
              >删除</el-button>
            </el-col>
            <el-col :span="1.5">
              <el-button
                type="warning"
                plain
                icon="el-icon-download"
                size="mini"
                @click="handleExport"
                v-hasPermi="['system:post:export']"
              >导出</el-button>
            </el-col>
            <el-col :span="1.5">
              <el-button
                type="info"
                plain
                icon="el-icon-connection"
                size="mini"
                :disabled="multiple"
                @click="openBindDeptDialog"
                v-hasPermi="['system:post:edit']"
              >关联党组织</el-button>
            </el-col>
            <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
          </el-row>

          <el-table v-loading="loading" :data="postList" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55" align="center" />
            <el-table-column label="职务编号" align="center" prop="postId" width="80" />
            <el-table-column label="职务名称" align="center" prop="postName" />
            <el-table-column label="职务描述" align="center" prop="postDescribes" :show-overflow-tooltip="true" />
            <el-table-column label="状态" align="center" prop="status">
              <template slot-scope="scope">
                <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status"/>
              </template>
            </el-table-column>
            <el-table-column label="创建时间" align="center" prop="createTime" width="180">
              <template slot-scope="scope">
                <span>{{ parseTime(scope.row.createTime) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="备注" align="center" prop="remark" />
            <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  type="text"
                  icon="el-icon-edit"
                  @click="handleUpdate(scope.row)"
                  v-hasPermi="['system:post:edit']"
                >修改</el-button>
                <el-button
                  size="mini"
                  type="text"
                  icon="el-icon-delete"
                  @click="handleDelete(scope.row)"
                  v-hasPermi="['system:post:remove']"
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
        </div>
      </el-col>
    </el-row>

    <!-- 添加或修改职务对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="职务名称" prop="postName">
          <el-input v-model="form.postName" placeholder="请输入职务名称" />
        </el-form-item>
        <el-form-item label="关联党组织" prop="deptIds">
          <treeselect
            v-model="form.deptIds"
            :options="deptOptions"
            :multiple="true"
            :flat="true"
            :show-count="true"
            :allow-invalid="false"
            placeholder="请选择党组织（可多选）"
          />
        </el-form-item>
        <el-form-item label="职务描述" prop="postDescribes">
          <el-input type="textarea" v-model="form.postDescribes" placeholder="请输入职务描述（少于500字）" />
        </el-form-item>
        <el-form-item label="职务状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in dict.type.sys_normal_disable"
              :key="dict.value"
              :label="dict.value"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 在模板中添加弹窗 -->
    <el-dialog title="批量关联党组织" :visible.sync="bindDeptVisible" width="500px" append-to-body>
      <el-form>
        <el-form-item label="党组织">
          <treeselect
            v-model="selectedDeptIds"
            :options="deptOptions"
            :multiple="true"
            :flat="true"
            :show-count="true"
            placeholder="请选择党组织"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="bindDeptVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleBindDepts">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {listPost, getPost, delPost, addPost, updatePost, bindPostsToDepts} from "@/api/system/post";
import { deptTreeSelect } from "@/api/system/user";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import BaseImage from "@/components/Image/index.vue";

export default {
  name: "Post",
  dicts: ['sys_normal_disable'],
  components: {BaseImage, Treeselect },
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
      // 党组织树选项
      deptOptions: undefined,
      // 党组织名称
      deptName: undefined,
      // 总条数
      total: 0,
      // 职务表格数据
      postList: [],
      // 原始职务表格数据
      originalPostList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      bindDeptVisible: false,
      selectedDeptIds: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        postDescribes: undefined,
        postName: undefined,
        status: undefined
      },
      // 表单参数
      form: {
        postId: undefined,
        deptIds: [], // 改为数组类型
      },
      defaultProps: {
        children: "children",
        label: "label"
      },
      // 表单校验
      rules: {
        postName: [
          { required: true, message: "职务名称不能为空", trigger: "blur" }
        ],
        postDescribes: [
          { required: true, message: "职务描述不能为空", trigger: "blur" }
        ],
        deptIds: [
          { required: true, message: "至少选择一个党组织", trigger: "change" }
        ],
        postSort: [
          { required: true, message: "职务顺序不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
    this.getDeptTree();
  },
  methods: {
    ORDER_SELECTED() {
      return ORDER_SELECTED
    },
    /** 查询职务列表 */
    getList() {
      this.loading = true;
      listPost(this.queryParams).then(response => {
        this.postList = response.rows;
        this.originalPostList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 查询党组织下拉树结构 */
    getDeptTree() {
      deptTreeSelect().then(response => {
        this.deptOptions = response.data;
      });
    },
    // 筛选节点
    filterNode(value, data) {
      if (!value) return true;
      return data.label.indexOf(value) !== -1;
    },
    // 节点单击事件
    handleNodeClick(data) {
      const selectedDeptId = data.id;
      if (selectedDeptId) {
        this.postList = this.originalPostList.filter(post => {
          return post.deptIds.includes(selectedDeptId);
        });
      } else {
        this.postList = this.originalPostList;
      }
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        postId: undefined,
        postDescribes: undefined,
        postName: undefined,
        postSort: 0,
        status: "0",
        remark: undefined
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.postId)
      this.single = selection.length != 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      // this.postOptions = response.posts;
      this.open = true;
      this.title = "添加职务";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const postId = row.postId || this.ids;
      getPost(postId).then(response => {
        this.form = response.data;
        // 过滤无效的部门ID
        this.form.deptIds = response.data.deptIds.filter(deptId =>
          this.isDeptExist(this.deptOptions, deptId)
        );
        this.open = true;
        this.title = "修改职务";
      });
    },
    // 递归检查部门ID是否存在
    isDeptExist(nodes, deptId) {
      for (const node of nodes) {
        if (node.id === deptId) return true;
        if (node.children && this.isDeptExist(node.children, deptId)) {
          return true;
        }
      }
      return false;
    },
    /** 提交按钮 */
    submitForm: function () {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.postId != undefined) {
            updatePost(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addPost(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const postIds = row.postId || this.ids;
      this.$modal.confirm('是否确认删除职务编号为"' + postIds + '"的数据项？').then(function () {
        return delPost(postIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/post/export', {
        ...this.queryParams
      }, `post_${new Date().getTime()}.xlsx`);
    },
    // 打开弹窗
    openBindDeptDialog() {
      if (this.ids.length === 0) {
        this.$modal.msgWarning("请先选择职务");
        return;
      }
      this.selectedDeptIds = [];
      this.bindDeptVisible = true;
    },
    // 提交绑定
    handleBindDepts() {
      if (this.selectedDeptIds.length === 0) {
        this.$modal.msgWarning("请选择党组织");
        return;
      }
      const params = {
        postIds: this.ids,
        deptIds: this.selectedDeptIds
      };
      bindPostsToDepts(params).then(() => {
        this.$modal.msgSuccess("关联成功");
        this.bindDeptVisible = false;
        this.getList(); // 刷新列表
      });
    }
  }
};
</script>
