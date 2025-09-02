<template>
  <div class="app-container">
    <el-row :gutter="20" class="full-height-row">
      <!--党组织数据-->
      <el-col :span="5" :xs="24" class="dept-col">
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
              ref="tree"
              node-key="branchId"
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
      </el-col>
      <!--党课数据-->
      <el-col :span="19" :xs="24" class="content-col">
        <div class="content-container" style="height: calc(100vh - 120px); overflow-y: auto; overflow-x: hidden;">
          <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
            <el-form-item label="党课主题" prop="title">
              <el-input
                v-model="queryParams.title"
                placeholder="请输入党课主题"
                clearable
                @keyup.enter.native="handleQuery"
              />
            </el-form-item>
            <el-form-item label="上课地点" prop="location">
              <el-input
                v-model="queryParams.location"
                placeholder="请输入上课地点"
                clearable
                @keyup.enter.native="handleQuery"
              />
            </el-form-item>
            <el-form-item label="授课讲师" prop="lecturer">
              <el-input
                v-model="queryParams.lecturer"
                placeholder="请输入授课讲师"
                clearable
                @keyup.enter.native="handleQuery"
              />
            </el-form-item>
            <el-form-item label="组织负责人" prop="organizerId">
              <el-input
                v-model="queryParams.organizerId"
                placeholder="请输入组织负责人"
                clearable
                @keyup.enter.native="handleQuery"
              />
            </el-form-item>
            <el-form-item label="开课时间" prop="startTime">
              <el-date-picker clearable
                v-model="queryParams.startTime"
                type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="请选择开课时间">
              </el-date-picker>
            </el-form-item>
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker clearable
                v-model="queryParams.endTime"
                type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="请选择结束时间">
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
                v-hasPermi="['system:class:add']"
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
                v-hasPermi="['system:class:edit']"
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
                v-hasPermi="['system:class:remove']"
              >删除</el-button>
            </el-col>
            <el-col :span="1.5">
              <el-button
                type="warning"
                plain
                icon="el-icon-download"
                size="mini"
                @click="handleExport"
                v-hasPermi="['system:class:export']"
              >导出</el-button>
            </el-col>
            <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
          </el-row>

          <el-table v-loading="loading" :data="classList" @selection-change="handleSelectionChange" :expand-row-keys="expandedRows" row-key="id">
            <el-table-column type="expand">
              <template slot-scope="props">
                <ExpandCard
                  :row-data="props.row"
                  participants-title="参加人员"
                  edit-button-text="管理人员"
                  :show-batch-button="true"
                  batch-button-text="批量考勤"
                  attachments-title="附件资料"
                  :attachments="getAttachmentList(props.row.attachments)"
                  attendance-title="出勤情况"
                  attendance-button-text="记录出勤"
                  :show-attendance-stats="true"
                  :total-count="getAttendanceStats(props.row.id).total"
                  :attendance-count="getAttendanceStats(props.row.id).attendance"
                  :absent-count="getAttendanceStats(props.row.id).absent"
                  :leave-count="getAttendanceStats(props.row.id).leave"
                  :late-count="getAttendanceStats(props.row.id).late"
                  @edit-participants="handleEditParticipants"
                  @batch-action="handleBatchAttendance"
                  @upload-attachment="handleUploadAttachment"
                  @download-attachment="downloadAttachment"
                  @delete-attachment="deleteAttachment"
                  @edit-attendance="handleEditAttendance"
                >
                  <template #participants="{ data }">
                    <ParticipantsTable
                      ref="batchAttendanceTable"
                      :participants="classParticipants[data.id] || []"
                      :user-list="userList"
                      :editable="false"
                      :attendance-status-options="dict.type.attendance_status"
                      @edit-participant="handleEditSingleParticipant"
                      @update-participant="updateParticipant"
                      @remove-participant="handleRemoveSingleParticipant"
                    />
                  </template>
                </ExpandCard>
              </template>
            </el-table-column>
            <el-table-column type="selection" width="55" align="center" />
            <el-table-column label="党课主题" align="center" prop="title" :show-overflow-tooltip="true"/>
            <el-table-column label="所属部门" align="center" prop="branchId" width="120">
              <template slot-scope="{ row }">
                <BaseImage :src="row.dept.deptImg" :width="20" :height="20"/>
                <span class="text" style="vertical-align: middle;">{{ row.dept.deptName }}</span>
              </template>
            </el-table-column>
            <el-table-column label="上课地点" align="center" prop="location" />
            <el-table-column label="授课讲师" align="center" prop="lecturer" />
            <el-table-column label="组织负责人" align="center" prop="organizerName"/>
            <el-table-column label="课程简介" align="center" prop="description" :show-overflow-tooltip="true">
              <template slot-scope="{ row }">
                <div v-html="row.description"></div>
              </template>
            </el-table-column>
            <el-table-column label="课程状态" align="center" prop="status" >
              <template slot-scope="scope">
                <dict-tag :options="dict.type.meetings_status" :value="scope.row.status"/>
              </template>
            </el-table-column>
            <el-table-column label="开课时间" align="center" prop="startTime" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <span>{{ parseTime(scope.row.startTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
              </template>
            </el-table-column>
            <el-table-column label="结束时间" align="center" prop="endTime" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <span>{{ parseTime(scope.row.endTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
              </template>
            </el-table-column>
            <el-table-column label="课时" align="center" prop="duration" />
            <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  type="text"
                  icon="el-icon-edit"
                  @click="handleUpdate(scope.row)"
                  v-hasPermi="['class:class:edit']"
                >修改</el-button>
                <el-button
                  size="mini"
                  type="text"
                  icon="el-icon-delete"
                  @click="handleDelete(scope.row)"
                  v-hasPermi="['class:class:remove']"
                >删除</el-button>
                <el-button 
                  size="mini" 
                  type="text" 
                  icon="el-icon-bell"
                  @click="handleSendNotification(scope.row)"
                >发布通知</el-button>
                <el-button 
                  size="mini" 
                  type="text" 
                  icon="el-icon-edit-outline"
                  @click="handleSignIn(scope.row)"
                >发布签到</el-button>
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

    <!-- 添加或修改党课对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="党课主题" prop="title">
          <el-input v-model="form.title" placeholder="请输入党课主题" />
        </el-form-item>
        <el-form-item label="上课地点" prop="location">
          <el-input v-model="form.location" placeholder="请输入上课地点" />
        </el-form-item>
        <el-form-item label="授课讲师" prop="lecturer">
          <el-input v-model="form.lecturer" placeholder="请输入授课讲师" />
        </el-form-item>
        <el-form-item label="组织负责人" prop="organizerId">
          <el-select
              v-model="form.organizerId"
              placeholder="请选择负责人"
              clearable
              @change="handleLeaderChange"
            >
              <el-option
                v-for="user in userList"
                :key="user.userId"
                :label="user.userName"
                :value="user.userId"
              />
            </el-select>
        </el-form-item>
        <el-form-item label="所属部门" prop="branchId">
          <treeselect v-model="form.branchId" :options="deptOptions" :show-count="true" placeholder="请选择所属部门" />
        </el-form-item>
        <el-form-item label="课程简介" prop="description">
          <editor v-model="form.description" :min-height="192"/>
        </el-form-item>
        <el-form-item label="开课时间" prop="startTime">
          <el-date-picker clearable
            v-model="form.startTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择开课时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker clearable
            v-model="form.endTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择结束时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="课时" prop="duration">
          <el-input v-model="form.duration" placeholder="请输入课时" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 修改参加人员对话框 -->
    <el-dialog title="修改参加人员" :visible.sync="participantsDialogVisible" width="800px" append-to-body>
      <el-form ref="participantsForm" :model="participantsForm" label-width="100px">
        <el-form-item label="参加人员" prop="participants">
          <el-select
            v-model="participantsForm.participants"
            multiple
            filterable
            placeholder="请选择参加人员"
            style="width: 100%;"
          >
            <el-option
              v-for="user in userList"
              :key="user.userId"
              :label="user.userName"
              :value="user.userId"
            >
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      
      <!-- 参与人员详细信息表格 -->
      <div v-if="participantsForm.participants && participantsForm.participants.length > 0" style="margin-top: 20px;">
        <el-divider content-position="left">参与人员详细信息</el-divider>
        <el-table :data="participantsDetailList" border size="small">
          <el-table-column prop="userName" label="姓名" width="120" />
          <el-table-column prop="deptName" label="部门" width="150" />
          <el-table-column label="签到状态" width="150">
            <template slot-scope="scope">
              <el-select v-model="scope.row.signInStatus" placeholder="请选择签到状态" size="small">
                <el-option
                  v-for="dict in dict.type.check_status"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="出勤状态" width="150">
            <template slot-scope="scope">
              <el-select v-model="scope.row.attendanceStatus" placeholder="请选择出勤状态" size="small">
                <el-option
                  v-for="dict in dict.type.attendance_status"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="备注">
            <template slot-scope="scope">
              <el-input v-model="scope.row.remark" placeholder="请输入备注" size="small" />
            </template>
          </el-table-column>
        </el-table>
      </div>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="participantsDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitParticipants">确 定</el-button>
      </div>
    </el-dialog>
    
    <!-- 上传附件对话框 -->
    <el-dialog title="上传附件" :visible.sync="uploadDialogVisible" width="600px" append-to-body>
      <div class="upload-container">
        <el-upload
          ref="upload"
          :action="uploadUrl"
          :headers="uploadHeaders"
          :file-list="fileList"
          :on-success="handleUploadSuccess"
          :on-error="handleUploadError"
          :on-remove="handleRemoveFile"
          :before-upload="beforeUpload"
          :auto-upload="false"
          multiple
          drag
        >
          <i class="el-icon-upload"></i>
          <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
          <div class="el-upload__tip" slot="tip">
            支持多文件上传，单个文件大小不超过10MB
          </div>
        </el-upload>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="uploadDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitUpload">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="批量考勤" :visible.sync="batchAttendanceDialogVisible" width="600px" append-to-body>
      <el-card shadow="never" style="margin-bottom: 20px;">
        <template #header>
          <span>出勤状态设置</span>
        </template>

        <el-row :gutter="10" type="flex" justify="space-between">
          <el-col :span="5">
            <el-select
              v-model="batchAttendanceStatus"
              placeholder="请选择出勤状态"
              style="width: 100%;"
            >
              <el-option
                v-for="dict in dict.type.attendance_status"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-col>
          <el-col :span="5">
            <el-button
              type="primary"
              size="middle"
              style="width: 100%;"
              @click="applyBatchAttendance"
            >
              应用设置
            </el-button>
          </el-col>
        </el-row>
      </el-card>

      <el-card shadow="never">
        <template #header>
          <span>签到状态设置</span>
        </template>

        <el-row :gutter="10" type="flex" justify="space-between">
          <el-col :span="6">
            <el-select
              v-model="batchSignInStatus"
              placeholder="请选择签到状态"
              style="width: 100%;"
            >
              <el-option
                v-for="dict in dict.type.check_status"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-col>
          <el-col :span="5">
            <el-button
              type="success"
              size="middle"
              style="width: 100%;"
              @click="applyBatchSignIn"
            >
              一键设置
            </el-button>
          </el-col>
        </el-row>
      </el-card>

      <template #footer>
        <el-button @click="batchAttendanceDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitBatchAttendance">保 存</el-button>
      </template>
    </el-dialog>

    <!-- 编辑单个参与人员对话框 -->
    <el-dialog title="编辑参与人员" :visible.sync="editSingleParticipantDialogVisible" width="500px" append-to-body>
      <el-form ref="editSingleParticipantForm" :model="currentParticipant" label-width="100px">
        <el-form-item label="姓名">
          <span>{{ currentParticipant.userName }}</span>
        </el-form-item>
        <el-form-item label="部门">
          <span>{{ currentParticipant.deptName }}</span>
        </el-form-item>
        <el-form-item label="签到状态">
          <el-select v-model="currentParticipant.signInStatus" placeholder="请选择签到状态">
            <el-option
              v-for="dict in dict.type.check_status"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="出勤状态">
          <el-select v-model="currentParticipant.attendanceStatus" placeholder="请选择出勤状态">
            <el-option
              v-for="dict in dict.type.attendance_status"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="currentParticipant.remark" type="textarea" rows="3"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editSingleParticipantDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitSingleParticipantEdit">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 引入通知组件 -->
    <ActivityNotification
      ref="activityNotification"
      activityType="1"
      :activityId="currentNotificationId"
      :activityTitle="currentNotificationTitle"
      :participants="currentNotificationParticipants"
      :activityStartTime="currentNotificationStartTime"
      :activityEndTime="currentNotificationEndTime"
      :activityAttachments="currentNotificationAttachments"
    />
  </div>
</template>

<script>
import { listClass, getClass, delClass, addClass, updateClass } from "@/api/class/class";
import { deptTreeSelect } from "@/api/system/user";
import { listUser } from "@/api/system/user";
import { listClassparticipant, getClassparticipant, addClassparticipant, updateClassparticipant, delClassparticipant } from "@/api/participant/classparticipant";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import BaseImage from "@/components/Image/index.vue";
import Editor from '@/components/Editor';
import attachmentMixin from '@/mixins/attachmentMixin';
import ExpandCard from '@/components/ExpandCard/index.vue';
import ParticipantsTable from '@/components/ParticipantsTable/index.vue';
import ParticipantsDialog from '@/components/ParticipantsDialog/index.vue';
import ActivityNotification from "@/components/ActivityNotification";

export default {
  name: "Class",
  dicts: ['attendance_status', 'check_status', 'meetings_status'],
  components: {
    Treeselect,
    BaseImage,
    Editor,
    ExpandCard,
    ParticipantsTable,
    ParticipantsDialog,
    ActivityNotification
  },
  mixins: [attachmentMixin],
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
      editSingleParticipantDialogVisible: false,
      currentParticipant: {},
      // 总条数
      total: 0,
      // class表格数据
      classList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 党组织数据
      deptOptions: [],
      // 党组织名称
      deptName: "",
      // 批量考勤对话框
      batchAttendanceDialogVisible: false,
      // 单个出勤记录对话框
      attendanceDialogVisible: false,
      // 用户列表
      userList: [],
      // 展开的行
      expandedRows: [],
      currentParticipants: [],
      currentEditingId: null,
      currentNotificationId: null,
      currentNotificationTitle: "",
      currentNotificationParticipants: [],
      currentNotificationStartTime: null,
      currentNotificationEndTime: null,
      // 附件信息
      currentNotificationAttachments: null, 
      // 批量操作状态
      batchAttendanceStatus: '0',
      batchSignInStatus: '0',
      // 修改参加人员对话框
      participantsDialogVisible: false,
      participantsForm: {
        id: null,
        participants: []
      },
      participantsDetailList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: null,
        location: null,
        lecturer: null,
        organizerId: null,
        branchId: null,
        description: null,
        status: null,
        startTime: null,
        endTime: null,
        duration: null,
        attachments: null,
        attendanceStatus: null
      },
      classParticipants: {},
      // 表单参数
      form: {},
      // 树属性
      defaultProps: {
        children: "children",
        label: "label"
      },
      uploadDialogVisible: false,
      fileList: [],
      currentUploadRow: null,
      // 表单校验
      rules: {
        title: [
          { required: true, message: "党课主题不能为空", trigger: "blur" }
        ],
        organizerId: [
          { required: true, message: "组织负责人不能为空", trigger: "blur" }
        ],
        branchId: [
          { required: true, message: "所属部门不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "课程状态不能为空", trigger: "change" }
        ],
        startTime: [
          { required: true, message: "开课时间不能为空", trigger: "blur" }
        ],
        endTime: [
          { required: true, message: "结束时间不能为空", trigger: "blur" }
        ],
      }
    };
  },
  watch: {
    // 根据名称筛选党组织树
    deptName(val) {
      this.$refs.tree.filter(val);
    },
    // 监听参与人员选择变化，更新详细信息列表
    'participantsForm.participants': {
      handler(newVal) {
        this.updateParticipantsDetailList(newVal);
      },
      deep: true
    }
  },
  created() {
    this.getDeptTree();
    this.getUserList().then(() => {
      this.getList();
    });
  },
  methods: {
    /** 查询class列表 */
    async getList() {
      this.loading = true;
      try {
        const response = await listClass(this.queryParams);
        let rows = response.rows;
        rows.forEach(item => {
          const user = this.userList.find(u => u.userId === item.organizerId);
          item.organizerName = user ? user.userName : item.organizerId;
        });
        this.classList = rows;
        this.total = response.total;
        this.fillDeptInfo(this.classList);
        
        // 加载每个课程的参与人员数据
        await this.loadParticipantsForClasses(this.classList);
        
        this.loading = false;
      } catch (error) {
        console.error('获取课程列表失败:', error);
        this.loading = false;
      }
    },
    
    async loadParticipantsForClasses(classList) {
      try {
        for (const classItem of classList) {
          const participants = await this.getParticipantsForClass(classItem.id);
          this.$set(this.classParticipants, classItem.id, participants);
        }
      } catch (error) {
        console.error('加载参与人员数据失败:', error);
      }
    },
    // 获取出勤统计数据
    getAttendanceStats(classId) {
      const participants = this.classParticipants[classId] || [];
      const stats = {
        total: participants.length,
        attendance: 0, // 出勤
        absent: 0,     // 缺席
        leave: 0,      // 请假
        late: 0        // 迟到
      };
      
      // 统计各状态人数
      participants.forEach(participant => {
        switch(participant.attendanceStatus) {
          case '0':
            stats.attendance++;
            break;
          case '1':
            stats.absent++;
            break;
          case '2':
            stats.leave++;
            break;
          case '3':
            stats.late++;
            break;
        }
      });
      
      return stats;
    },
    /** 获取单个课程的参与人员数据 */
    async getParticipantsForClass(classId) {
      try {
        const response = await listClassparticipant({ classId: classId });
        return response.rows || [];
      } catch (error) {
        console.error(`获取课程${classId}的参与人员失败:`, error);
        return [];
      }
    },
    // 更新参与人员数据（不打开对话框）
    async updateParticipant(participant) {
      try {
        this.$modal.loading("正在保存参与人员数据...");
        
        // 更新参与人员信息
        await updateClassparticipant(participant);
        
        this.$modal.closeLoading();
        this.$modal.msgSuccess("修改成功");
        
        // 刷新列表
        this.getList();
      } catch (error) {
        this.$modal.closeLoading();
        this.$modal.msgError("修改失败");
        console.error('更新参与人员失败:', error);
      }
    },
    /** 获取单个课程的参与人员数据 */
    async getParticipantsForClass(classId) {
      try {
        const response = await listClassparticipant({ classId: classId });
        return response.rows || [];
      } catch (error) {
        console.error(`获取课程${classId}的参与人员失败:`, error);
        return [];
      }
    },
    
    // 根据 branchId 给每条数据补充 dept 字段
    fillDeptInfo(list) {
      // deptOptions 是树结构，需递归查找
      function findDeptById(options, id) {
        for (let item of options) {
          if (item.id === id) return item;
          if (item.children) {
            const found = findDeptById(item.children, id);
            if (found) return found;
          }
        }
        return null;
      }
      list.forEach(item => {
        const dept = findDeptById(this.deptOptions, item.branchId);
        item.dept = dept
          ? { deptImg: dept.deptImg || '', deptName: dept.label || dept.deptName || '' }
          : { deptImg: '', deptName: '' };
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
        id: null,
        title: null,
        location: null,
        lecturer: null,
        organizerId: null,
        branchId: null,
        description: null,
        status: "0",
        startTime: null,
        endTime: null,
        duration: null,
        createTime: null,
        attachments: null,
        attendanceStatus: null
      };
      this.resetForm("form");
    },
    // 编辑单个参与人员
    handleEditSingleParticipant(participant) {
      this.currentEditingId = participant.classId;
      this.currentParticipant = JSON.parse(JSON.stringify(participant));
      this.editSingleParticipantDialogVisible = true;
    },
    // 删除单个参与人员
    async handleRemoveSingleParticipant(participant, index) {
      try {
        await this.$modal.confirm(`确认删除参与人员 "${participant.userName}" 吗？`);
        if (participant.id) {
          await delClassparticipant(participant.id);
        }
        this.currentParticipants.splice(index, 1);
        this.$modal.msgSuccess("删除成功");
        // 刷新列表
        this.getList();
      } catch (error) {
        if (error !== 'cancel') {
          this.$modal.msgError("删除失败");
          console.error('删除参与人员失败:', error);
        }
      }
    },
    // 提交单个参与人员编辑
    async submitSingleParticipantEdit() {
      try {
        this.$modal.loading("正在保存参与人员数据...");
        
        // 更新参与人员信息
        await updateClassparticipant(this.currentParticipant);
        
        this.$modal.closeLoading();
        this.$modal.msgSuccess("修改成功");
        this.editSingleParticipantDialogVisible = false;
        
        // 刷新列表
        this.getList();
      } catch (error) {
        this.$modal.closeLoading();
        this.$modal.msgError("修改失败");
        console.error('更新参与人员失败:', error);
      }
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.queryParams.branchId = null;
      this.handleQuery();
    },
    
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加党课";
    },
    
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getClass(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改党课";
      });
    },
    
    // 处理批量考勤
    async handleBatchAttendance(row) {
      this.currentEditingId = row.id;
      this.currentParticipants = await this.getParticipantsForClass(row.id);
      this.batchAttendanceDialogVisible = true;
    },
    
    // 处理出勤记录
    async handleEditAttendance(row) {
      this.currentEditingId = row.id;
      this.currentParticipants = await this.getParticipantsForClass(row.id);
      this.attendanceDialogVisible = true;
    },
    
    // 更新参与人员详细信息列表
    updateParticipantsDetailList(selectedUserIds) {
      if (!selectedUserIds || selectedUserIds.length === 0) {
        this.participantsDetailList = [];
        return;
      }
      
      this.participantsDetailList = selectedUserIds.map(userId => {
        const user = this.userList.find(u => u.userId === userId);
        const existingParticipant = this.currentParticipants.find(p => p.userId === userId);
        
        return {
          userId: userId,
          userName: user ? user.userName : '',
          deptName: user && user.dept ? user.dept.deptName : '',
          signInStatus: existingParticipant ? existingParticipant.signInStatus : '0',
          attendanceStatus: existingParticipant ? existingParticipant.attendanceStatus : '0',
          remark: existingParticipant ? existingParticipant.remark : '',
          id: existingParticipant ? existingParticipant.id : null
        };
      });
    },
    
    // 应用批量出勤状态
    applyBatchAttendance() {
      if (!this.batchAttendanceStatus) {
        this.$modal.msgWarning("请先选择出勤状态");
        return;
      }
      
      this.currentParticipants.forEach(participant => {
        participant.attendanceStatus = this.batchAttendanceStatus;
      });
      
      this.$modal.msgSuccess("批量设置出勤状态成功，请点击保存按钮提交");
    },
    
    // 应用批量签到状态
    applyBatchSignIn() {
      if (!this.batchSignInStatus) {
        this.$modal.msgWarning("请先选择签到状态");
        return;
      }
      this.currentParticipants.forEach(participant => {
        participant.signInStatus = this.batchSignInStatus;
      });
      this.$modal.msgSuccess("一键签到成功，请点击保存按钮提交");
    },
    
    
   // 提交批量考勤
   async submitBatchAttendance() {
      if (!this.currentParticipants || this.currentParticipants.length === 0) {
        this.$modal.msgError("没有参与人员数据");
        return;
      }
      try {
        this.$modal.loading("正在处理考勤数据...");
        
        // 只更新已存在的参与人员记录
        const updateData = this.currentParticipants
          .filter(participant => participant.id)
          .map(participant => ({
            id: participant.id,
            classId: this.currentEditingId,
            userId: participant.userId,
            userName: participant.userName,
            deptName: participant.deptName,
            signInStatus: participant.signInStatus,
            attendanceStatus: participant.attendanceStatus,
            remark: participant.remark
          }));
        
        // 使用Promise.all并发请求，提高效率
        if (updateData.length > 0) {
          await Promise.all(updateData.map(item => updateClassparticipant(item)));
        }
        
        this.$modal.closeLoading();
        this.$modal.msgSuccess("批量考勤记录成功");
        this.batchAttendanceDialogVisible = false;
        
        // 刷新列表
        this.getList();
      } catch (error) {
        this.$modal.closeLoading();
        this.$modal.msgError("批量考勤记录失败");
        console.error('批量考勤失败:', error);
      }
    },
    // 提交单个出勤记录
    async submitAttendance() {
      if (!this.currentParticipants || this.currentParticipants.length === 0) {
        this.$modal.msgError("没有参与人员数据");
        return;
      }
      
      try {
        for (const participant of this.currentParticipants) {
          // 只更新已存在的参与人员记录
          if (participant.id) {
            const updateData = {
              id: participant.id,
              classId: this.currentEditingId,
              userId: participant.userId,
              userName: participant.userName,
              deptName: participant.deptName,
              signInStatus: participant.signInStatus,
              attendanceStatus: participant.attendanceStatus,
              remark: participant.remark
            };
            await updateClassparticipant(updateData);
          }
        }
        
        this.$modal.msgSuccess("出勤记录成功");
        this.attendanceDialogVisible = false;
        this.getList();
      } catch (error) {
        this.$modal.msgError("出勤记录失败");
        console.error('出勤记录失败:', error);
      }
    },
    
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateClass(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addClass(this.form).then(response => {
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
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除党课编号为"' + ids + '"的数据项？').then(function() {
        return delClass(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/class/export', {
        ...this.queryParams
      }, `class_${new Date().getTime()}.xlsx`)
    },
    
    /** 查询党组织下拉树结构 */
    getDeptTree() {
      deptTreeSelect().then(response => {
        this.deptOptions = response.data;
      });
    },
    
    /** 处理负责人选择 */
    handleLeaderChange(userName) {
      const selectedUser = this.userList.find(user => user.userName === userName);
      if (selectedUser) {
        this.form.email = selectedUser.email;
        this.form.phone = selectedUser.phonenumber;
      }
    },
    
    /** 查询用户列表 */
    getUserList() {
      return listUser().then(response => {
        this.userList = response.rows;
      });
    },
    
    // 筛选节点
    filterNode(value, data) {
      if (!value) return true;
      return data.label.indexOf(value) !== -1;
    },
    
    // 节点单击事件
    handleNodeClick(data) {
      this.queryParams.branchId = data.id;
      this.handleQuery();
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
    // 更新行附件数据
    updateRowAttachments(id, attachments) {
      const updateData = {
        id: id,
        attachments: attachments
      };
      
      updateClass(updateData).then(response => {
        this.$modal.msgSuccess("附件更新成功");
        // 刷新列表数据
        this.getList();
      }).catch(error => {
        this.$modal.msgError("附件更新失败");
        console.error('更新附件失败:', error);
      });
    },
    // 提交参与人员修改
    async submitParticipants() {
    const selectedUserIds = this.participantsForm.participants;
    const classId = this.participantsForm.id;
      
      try {
        this.$modal.loading("正在处理参与人员数据...");
        
        // 获取当前参与人员
        const currentParticipants = await this.getParticipantsForClass(classId);
        const currentUserIds = currentParticipants.map(p => p.userId);
        
        // 找出需要删除的参与人员
        const toDelete = currentParticipants.filter(p => !selectedUserIds.includes(p.userId));
        
        // 找出需要添加的参与人员
        const toAdd = selectedUserIds.filter(userId => !currentUserIds.includes(userId));
        
        // 批量删除不再参与的人员
        if (toDelete.length > 0) {
          const deleteIds = toDelete.map(p => p.id).filter(id => id);
          if (deleteIds.length > 0) {
            // 修改API，支持批量删除
            await delClassparticipant(deleteIds.join(','));
          }
        }
        
        // 批量添加新的参与人员
        if (toAdd.length > 0) {
          const addData = toAdd.map(userId => {
            const detailInfo = this.participantsDetailList.find(p => p.userId === userId);
            const user = this.userList.find(u => u.userId === userId);
            if (user) {
              return {
                classId: classId,
                userId: userId,
                userName: user.userName,
                deptName: user.dept?.deptName || '',
                signInStatus: detailInfo ? detailInfo.signInStatus : '0',
                attendanceStatus: detailInfo ? detailInfo.attendanceStatus : '0',
                remark: detailInfo ? detailInfo.remark : ''
              };
            }
            return null;
          }).filter(item => item !== null);
          
          // 这里需要修改后端API，支持批量添加
          if (addData.length > 0) {
            // 如果后端不支持批量添加，可以使用Promise.all并发请求
            await Promise.all(addData.map(item => addClassparticipant(item)));
          }
        }
        
        // 更新已存在的参与人员信息
        const updateData = this.participantsDetailList
          .filter(detailInfo => {
            const existingParticipant = currentParticipants.find(p => p.userId === detailInfo.userId);
            return existingParticipant && selectedUserIds.includes(detailInfo.userId);
          })
          .map(detailInfo => {
            const existingParticipant = currentParticipants.find(p => p.userId === detailInfo.userId);
            return {
              id: existingParticipant.id,
              classId: classId,
              userId: detailInfo.userId,
              userName: detailInfo.userName,
              deptName: detailInfo.deptName,
              signInStatus: detailInfo.signInStatus,
              attendanceStatus: detailInfo.attendanceStatus,
              remark: detailInfo.remark
            };
          });
        
        // 如果后端不支持批量更新，可以使用Promise.all并发请求
        if (updateData.length > 0) {
          await Promise.all(updateData.map(item => updateClassparticipant(item)));
        }
        
        this.$modal.closeLoading();
        this.$modal.msgSuccess("修改成功");
        this.participantsDialogVisible = false;
        
        this.getList();
      } catch (error) {
        this.$modal.closeLoading();
        this.$modal.msgError("修改失败");
        console.error('更新参与人员失败:', error);
      }
    },
     // 处理发布通知
     handleSendNotification(row) {
      // 获取当前会议的参与人员
      this.getParticipantsForClass(row.id).then(participants => {
        this.currentNotificationId = row.id;
        this.currentNotificationTitle = row.title;
        this.currentNotificationParticipants = participants;
        this.currentNotificationStartTime = this.parseTime(row.startTime, '{y}-{m}-{d} {h}:{i}:{s}');
        this.currentNotificationEndTime = this.parseTime(row.endTime, '{y}-{m}-{d} {h}:{i}:{s}');
        this.currentNotificationAttachments = row.attachments; // 传递附件信息
        
        // 使用$nextTick确保props更新后再打开对话框
        this.$nextTick(() => {
          this.$refs.activityNotification.openNotificationDialog();
        });
      }).catch(error => {
        this.$modal.msgError("获取参与人员失败");
        console.error("获取参与人员失败:", error);
      });
    },

    // 处理发布签到
    handleSignIn(row) {
      // 获取当前会议的参与人员
      this.getParticipantsForClass(row.id).then(participants => {
        this.currentNotificationId = row.id;
        this.currentNotificationTitle = row.title;
        this.currentNotificationParticipants = participants;
        this.currentNotificationStartTime = this.parseTime(row.startTime, '{y}-{m}-{d} {h}:{i}:{s}');
        this.currentNotificationEndTime = this.parseTime(row.endTime, '{y}-{m}-{d} {h}:{i}:{s}');
        this.currentNotificationAttachments = row.attachments; // 传递附件信息
        
        // 使用$nextTick确保props更新后再打开对话框
        this.$nextTick(() => {
          this.$refs.activityNotification.openSignInDialog();
        });
      }).catch(error => {
        this.$modal.msgError("获取参与人员失败");
        console.error("获取参与人员失败:", error);
      });
    },
    // 修改参加人员
    async handleEditParticipants(row) {
      this.currentEditingId = row.id;
      this.currentParticipants = await this.getParticipantsForClass(row.id);
      
      // 设置参与人员表单数据
      this.participantsForm = {
        id: row.id,
        participants: this.currentParticipants.map(p => p.userId)
      };
      
      // 初始化详细信息列表
      this.updateParticipantsDetailList(this.participantsForm.participants);
      
      this.participantsDialogVisible = true;
    }
  }
};
</script>