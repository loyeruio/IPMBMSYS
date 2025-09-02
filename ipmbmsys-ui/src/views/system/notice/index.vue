<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="公告标题" prop="noticeTitle">
        <el-input
          v-model="queryParams.noticeTitle"
          placeholder="请输入公告标题"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="操作人员" prop="createBy">
        <el-input
          v-model="queryParams.createBy"
          placeholder="请输入操作人员"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="类型" prop="noticeType">
        <el-select v-model="queryParams.noticeType" placeholder="公告类型" clearable>
          <el-option
            v-for="dict in dict.type.sys_notice_type"
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
          v-hasPermi="['system:notice:add']"
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
          v-hasPermi="['system:notice:edit']"
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
          v-hasPermi="['system:notice:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="noticeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" align="center" prop="noticeId" width="100" />
      <el-table-column
        label="公告标题"
        align="center"
        prop="noticeTitle"
        :show-overflow-tooltip="true"
      />
      <!-- 在表格中添加新的列 -->
      <el-table-column label="通知类型" align="center" prop="noticeType" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_notice_type" :value="scope.row.noticeType"/>
        </template>
      </el-table-column>
      <el-table-column label="活动类别" align="center" prop="activityType" width="120">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_activity_type" :value="scope.row.activityType"/>
        </template>
      </el-table-column>
      <el-table-column label="通知对象" align="center" prop="noticeUserIds" width="220">
        <template slot-scope="scope">
          <el-tooltip effect="dark" :content="formatUserNames(scope.row.noticeUserIds)" placement="top">
            <span>{{ formatUserDisplay(scope.row.noticeUserIds) }}</span>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column label="通知/签到时间" align="center" prop="recordTime" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <span v-if="scope.row.recordTime">{{ parseTime(scope.row.recordTime) }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <!-- 添加附件列 -->
      <el-table-column label="附件" align="center" prop="attachMent" width="120">
        <template slot-scope="scope">
          <span v-if="scope.row.attachMent && getAttachmentList(scope.row.attachMent).length > 0">
            <el-tooltip effect="dark" :content="formatAttachmentNames(scope.row.attachMent)" placement="top">
              <el-tag size="small" type="info">{{ getAttachmentList(scope.row.attachMent).length }}个附件</el-tag>
            </el-tooltip>
          </span>
          <span v-else>无</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_notice_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="100">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建者" align="center" prop="createBy" width="100" />
      <!-- <el-table-column label="创建者" align="center" prop="remark" width="100" /> -->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:notice:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:notice:remove']"
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

    <!-- 添加或修改公告对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="780px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="公告标题" prop="noticeTitle">
              <el-input v-model="form.noticeTitle" placeholder="请输入公告标题" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <!-- 在表单中添加新的字段 -->
            <el-form-item label="通知类型" prop="noticeType">
              <el-select v-model="form.noticeType" placeholder="请选择通知类型">
                <el-option
                  v-for="dict in dict.type.sys_notice_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.activityType">
            <el-form-item label="活动类别">
              <el-select v-model="form.activityType" placeholder="请选择活动类别">
                <el-option
                  v-for="dict in dict.type.sys_activity_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="通知对象" prop="noticeUserIds">
              <el-input 
                :value="formatUserNames(form.noticeUserIds)" 
                placeholder="修改通知对象请在相应页面添加人员" 
                :rows="2"
                readonly
                disabled
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="记录时间" prop="recordTime" v-if="form.noticeType === '3' || form.noticeType === '1'">
              <el-date-picker
                v-model="form.recordTime"
                type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="请选择记录时间"
                style="width: 100%;"
              ></el-date-picker>
            </el-form-item>
          </el-col>
          <!-- 添加附件显示 -->
          <el-col :span="24" v-if="form.attachMent">
            <el-form-item label="相关附件">
              <div class="attachment-list">
                <el-tag 
                  v-for="(attachment, index) in getAttachmentList(form.attachMent)" 
                  :key="index" 
                  size="small" 
                  style="margin-right: 5px; margin-bottom: 5px;"
                >
                  {{ attachment.name || attachment.fileName || '未知文件' }}
                </el-tag>
              </div>
              <div v-if="getAttachmentList(form.attachMent).length === 0" class="text-muted">
                无附件
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio
                  v-for="dict in dict.type.sys_notice_status"
                  :key="dict.value"
                  :label="dict.value"
                >{{dict.label}}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="内容">
              <editor v-model="form.noticeContent" :min-height="192"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-col :span="12" v-if="form.remark">
          <el-form-item label="备注">
            <el-input 
              v-model="form.remark" 
              type="textarea"
              :rows="2"
              placeholder="请输入备注"
            />
          </el-form-item>
        </el-col>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listNotice, getNotice, delNotice, addNotice, updateNotice } from "@/api/system/notice";
import { listAllUser } from "@/api/system/user"; 

export default {
  name: "Notice",
  dicts: ['sys_notice_status', 'sys_notice_type', 'sys_activity_type'],
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
      // 公告表格数据
      noticeList: [],
      // 弹出层标题
      title: "",
      // 用户列表数据
      userList: [],
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        noticeTitle: undefined,
        createBy: undefined,
        status: undefined
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        noticeTitle: [
          { required: true, message: "公告标题不能为空", trigger: "blur" }
        ],
        noticeType: [
          { required: true, message: "公告类型不能为空", trigger: "change" }
        ]
      }
    };
  },
  created() {
    this.getUserList().then(() => {
      this.getList();
    });
  },
  methods: {
    /** 查询公告列表 */
    getList() {
      this.loading = true;
      listNotice(this.queryParams).then(response => {
        this.noticeList = response.rows;
        this.total = response.total;
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
        noticeId: undefined,
        noticeTitle: undefined,
        noticeType: undefined,
        noticeContent: undefined,
        noticeUserIds: undefined,
        recordTime: undefined,
        activityType: undefined,
        activityId: undefined,
        status: "0"
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
      this.ids = selection.map(item => item.noticeId)
      this.single = selection.length!=1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加公告";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const noticeId = row.noticeId || this.ids
      getNotice(noticeId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改公告";
      });
    },
    formatActivityType(activityType) {
      const typeMap = {
        'meeting': '党会',
        'learning': '半月学',
        'class': '党课'
      };
      return typeMap[activityType] || activityType;
    },
    /** 提交按钮 */
    submitForm: function() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.noticeId != undefined) {
            updateNotice(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addNotice(this.form).then(response => {
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
      const noticeIds = row.noticeId || this.ids
      this.$modal.confirm('是否确认删除公告编号为"' + noticeIds + '"的数据项？').then(function() {
        return delNotice(noticeIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    // 格式化用户数量
    formatUserCount(userIds) {
      if (!userIds) return "无";
      const ids = userIds.split(",").filter(id => id);
      return `${ids.length}人`;
    },
    // 获取用户列表
    getUserList() {
      return listAllUser().then(response => {
        // 修改：使用 response.data 而不是 response.rows
        this.userList = response.data;
      });
    },
    
    // 格式化用户名称（用于tooltip显示）
    formatUserNames(userIds) {
      if (!userIds) return "无";
      const ids = userIds.split(",").filter(id => id);
      const userNames = ids.map(id => {
        const user = this.userList.find(u => u.userId == id);
        return user ? user.userName : `用户${id}`;
      });
      return userNames.join("、");
    },
    
    // 格式化用户数量和名称显示
    formatUserDisplay(userIds) {
      if (!userIds) return "无";
      const ids = userIds.split(",").filter(id => id);
      const userNames = ids.map(id => {
        const user = this.userList.find(u => u.userId == id);
        return user ? user.userName : `用户${id}`;
      });
      
      if (userNames.length <= 3) {
        return userNames.join("、");
      } else {
        return `${userNames.slice(0, 3).join("、")}等${userNames.length}人`;
      }
    },
    // 获取附件列表
    getAttachmentList(attachments) {
      if (!attachments) return [];
      try {
        return JSON.parse(attachments);
      } catch (e) {
        return [];
      }
    },
    
    // 格式化附件名称（用于tooltip显示）
    formatAttachmentNames(attachments) {
      if (!attachments) return "无附件";
      try {
        const attachmentList = JSON.parse(attachments);
        if (attachmentList.length === 0) return "无附件";
        return attachmentList.map(att => att.name || att.fileName || '未知文件').join('、');
      } catch (e) {
        return "附件格式错误";
      }
    },
  }
};
</script>
