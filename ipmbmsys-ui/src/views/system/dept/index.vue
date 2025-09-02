<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="medium" :inline="true" v-show="showSearch">
      <el-form-item label="党组织名称" prop="deptName">
        <el-input
          v-model="queryParams.deptName"
          placeholder="请输入党组织名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="党组织状态" clearable>
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
          v-hasPermi="['system:dept:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-sort"
          size="mini"
          @click="toggleExpandAll"
        >展开/折叠</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table
      v-if="refreshTable"
      v-loading="loading"
      :data="deptList"
      row-key="deptId"
      :default-expand-all="isExpandAll"
      :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
    >
      <el-table-column prop="deptName" label="党组织名称" width="260">
        <template slot-scope="{ row }">
          <BaseImage :src="row.deptImg ? row.deptImg : ''" :width="20" :height="20"/>
          <span class="text" style="vertical-align: middle;">{{ row.deptName }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="orderNum" label="排序" align="center" width="50"/>
      <el-table-column prop="leader" label="负责人" width="100" align="center" :show-overflow-tooltip="true"></el-table-column>
      <el-table-column prop="phone" label="联系电话" width="120" align="center"></el-table-column>
      <el-table-column prop="organizationAbbr" label="党组织简称" width="100" align="center"></el-table-column>
      <el-table-column prop="organizationType" label="党组织类别" width="100" align="center">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.party_org_type" :value="scope.row.organizationType"/>
        </template>
      </el-table-column>
<!--      <el-table-column prop="organizationCode" label="党组织代码" width="150" align="center"></el-table-column>-->
      <el-table-column prop="status" label="状态" width="100" align="center">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column prop="parentName" label="直接上级党组织" width="180" align="center" :show-overflow-tooltip="true" >
        <template slot-scope="{ row }">
          <BaseImage :src="row.parentImg ? row.parentImg : ''" :width="20" :height="20"/>
          <span class="text" style="vertical-align: middle;">{{ row.parentName }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="remarks" label="备注" width="50" align="center" :show-overflow-tooltip="true"></el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:dept:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-plus"
            @click="handleAdd(scope.row)"
            v-hasPermi="['system:dept:add']"
          >新增</el-button>
          <el-button
            v-if="scope.row.parentId != 0"
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:dept:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加或修改党组织对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24" v-if="form.parentId !== 0">
            <el-form-item label="上级党组织" prop="parentId">
              <treeselect v-model="form.parentId" :options="deptOptions" :normalizer="normalizer" placeholder="选择上级党组织" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="党组织名称" prop="deptName">
              <el-input v-model="form.deptName" placeholder="请输入党组织名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="显示排序" prop="orderNum">
              <el-input-number v-model="form.orderNum" controls-position="right" :min="0" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="党组织简称" prop="organizationAbbr">
              <el-input v-model="form.organizationAbbr" placeholder="请输入党组织简称" maxlength="20" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="党组织类别" prop="organizationType">
              <el-select v-model="form.organizationType" placeholder="请选择党组织类别">
                <el-option
                  v-for="dict in dict.type.party_org_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="负责人" prop="leader">
              <el-select
                v-model="form.leader"
                placeholder="请选择负责人"
                clearable
                @change="handleLeaderChange"
              >
                <el-option
                  v-for="user in userList"
                  :key="user.userId"
                  :label="user.userName"
                  :value="user.userName"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" maxlength="11" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="负责人邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入负责人邮箱" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="备注" prop="remarks">
              <el-input v-model="form.remarks" placeholder="请输入备注" maxlength="255" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="党组织图片" prop="deptImg">
              <image-upload v-model="form.deptImg" :limit="1" :isShowTip="false"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="党组织状态">
              <el-radio-group v-model="form.status">
                <el-radio
                  v-for="dict in dict.type.sys_normal_disable"
                  :key="dict.value"
                  :label="dict.value"
                >{{dict.label}}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listDept, getDept, delDept, addDept, updateDept, listDeptExcludeChild } from "@/api/system/dept";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import {listUser} from "@/api/system/user";
import BaseImage from "@/components/Image/index.vue";

export default {
  name: "Dept",
  dicts: ['sys_normal_disable','party_org_type'],
  components: {BaseImage, Treeselect },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 表格树数据
      deptList: [],
      // 用户数据
      userList: [],
      // 党组织树选项
      deptOptions: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否展开，默认全部展开
      isExpandAll: true,
      // 重新渲染表格状态
      refreshTable: true,
      // 查询参数
      queryParams: {
        deptName: undefined,
        status: undefined,
        userName: undefined,
        phonenumber: undefined,
        deptId: undefined
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        parentId: [
          { required: true, message: "上级党组织不能为空", trigger: "blur" }
        ],
        deptName: [
          { required: true, message: "党组织名称不能为空", trigger: "blur" }
        ],
        orderNum: [
          { required: true, message: "显示排序不能为空", trigger: "blur" }
        ],
        email: [
          {
            type: "email",
            message: "请输入正确的邮箱地址",
            trigger: ["blur", "change"]
          }
        ],
        phone: [
          {
            pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
            message: "请输入正确的手机号码",
            trigger: "blur"
          }
        ],
        organizationAbbr: [
          { required: true, message: "党组织简称不能为空", trigger: "blur" }
        ],
        organizationType: [
          { required: true, message: "党组织类别不能为空", trigger: "blur" }
        ],
        organizationCode: [
          { required: true, message: "党组织代码不能为空", trigger: "blur" }
        ],
        remarks: [
          { required: false, message: "备注不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
    this.getUserList();
  },
  methods: {
    /** 查询党组织列表 */
    getList() {
      this.loading = true;
      listDept(this.queryParams).then(response => {
        // 处理树形数据，添加 parentName 字段
        this.deptList = this.handleTree(response.data, "deptId");
        // 递归查找并为每个党组织项添加 parentName
        this.deptList.forEach(dept => {
          this.addParentName(dept);
        });
        this.loading = false;
      });
    },
    /** 递归查找并为每个党组织项添加 parentName */
    addParentName(dept) {
      // 如果有子节点，则递归遍历子节点
      if (dept.children && dept.children.length) {
        dept.children.forEach(childDept => {
          this.addParentName(childDept);  // 递归添加
        });
      }
      // 为当前党组织添加 parentName
      const parentDept = this.findParentDept(dept.parentId);
      dept.parentName = parentDept ? parentDept.deptName : '';  // 获取父党组织名称
      dept.parentImg = parentDept.deptImg;
    },
    /** 查找对应的父党组织 */
    findParentDept(parentId) {
      // 递归遍历整个树形结构查找父党组织
      function searchParent(depts) {
        for (let i = 0; i < depts.length; i++) {
          const dept = depts[i];
          if (dept.deptId === parentId) {
            return dept;
          }
          if (dept.children && dept.children.length) {
            const found = searchParent(dept.children);  // 递归查找子节点
            if (found) return found;
          }
        }
        return null;
      }
      return searchParent(this.deptList);
    },
    /** 转换党组织数据结构 */
    normalizer(node) {
        if (node.children && !node.children.length) {
        delete node.children;
      }
      return {
        id: node.deptId,
        label: node.deptName,
        children: node.children
      };
    },
    /** 处理负责人选择 */
    handleLeaderChange(userName) {
      const selectedUser = this.userList.find(user => user.userName === userName);
      if (selectedUser) {
        this.form.email = selectedUser.email;
        this.form.phone = selectedUser.phonenumber;
      }
    },
    // 确保获取用户列表的方法正确
    getUserList() {
      listUser().then(response => {
        this.userList = response.rows;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        deptId: undefined,
        parentId: undefined,
        deptName: undefined,
        orderNum: undefined,
        leader: undefined,
        phone: undefined,
        email: undefined,
        status: "0"
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 新增按钮操作 */
    handleAdd(row) {
      this.reset();
      if (row != undefined) {
        this.form.parentId = row.deptId;
      }
      this.open = true;
      this.title = "添加党组织";
      listDept().then(response => {
        this.deptOptions = this.handleTree(response.data, "deptId");
      });
    },
    /** 展开/折叠操作 */
    toggleExpandAll() {
      this.refreshTable = false;
      this.isExpandAll = !this.isExpandAll;
      this.$nextTick(() => {
        this.refreshTable = true;
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      getDept(row.deptId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改党组织";
        listDeptExcludeChild(row.deptId).then(response => {
          this.deptOptions = this.handleTree(response.data, "deptId");
          if (this.deptOptions.length == 0) {
            const noResultsOptions = { deptId: this.form.parentId, deptName: this.form.parentName, children: [] };
            this.deptOptions.push(noResultsOptions);
          }
        });
      });
    },
    /** 提交按钮 */
    submitForm: function() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.deptId != undefined) {
            updateDept(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addDept(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除名称为"' + row.deptName + '"的数据项？').then(function() {
        return delDept(row.deptId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    }
  }
};
</script>

