<template>
  <el-dialog 
    :title="dialogTitle" 
    :visible.sync="dialogVisible" 
    width="900px" 
    append-to-body
    @close="handleClose"
  >
  <div class="dialog-content">
      <!-- 添加人员区域 -->
      <div class="add-participants-section">
        <el-form :inline="true" size="small">
          <el-form-item label="添加人员">
            <el-select
              v-model="selectedUsers"
              multiple
              filterable
              placeholder="请选择参与人员"
              style="width: 300px;"
            >
              <el-option
                v-for="user in userList"
                :key="user.userId"
                :label="getUserOptionLabel(user)"
                :value="user.userId"
                :disabled="isUserAlreadyAdded(user.userId)"
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleAddParticipants">添加</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 参与人员表格 -->
      <ParticipantsTable
        ref="participantsTable"
        :participants="currentParticipants"
        :user-list="userList"
        :editable="true"
        :attendance-status-options="attendanceStatusOptions"
        @attendance-change="handleAttendanceChange"
        @signin-change="handleSignInChange"
        @remark-change="handleRemarkChange"
        @participant-removed="handleParticipantRemoved"
      />
    </div>
    
    <div slot="footer" class="dialog-footer">
      <el-button @click="handleClose">取 消</el-button>
      <el-button type="primary" @click="handleConfirm">确 定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import ParticipantsTable from '@/components/ParticipantsTable/index.vue'

export default {
  name: 'ParticipantsDialog',
  components: {
    ParticipantsTable
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    title: {
      type: String,
      default: '管理参与人员'
    },
    participants: {
      type: Array,
      default: () => []
    },
    userList: {
      type: Array,
      default: () => []
    },
    attendanceStatusOptions: {
      type: Array,
      default: () => [
        { label: '出勤', value: '0' },
        { label: '缺席', value: '1' },
        { label: '请假', value: '2' },
        { label: '迟到', value: '3' }
      ]
    }
  },
  data() {
    return {
      selectedUsers: [],
      currentParticipants: []
    }
  },
  computed: {
    dialogVisible: {
      get() {
        return this.visible;
      },
      set(val) {
        this.$emit('update:visible', val);
      }
    },
    dialogTitle() {
      return this.title;
    }
  },
  watch: {
    participants: {
      handler(newVal) {
        this.currentParticipants = [...newVal];
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    // 检查用户是否已添加（优化版本）
    isUserAlreadyAdded(userId) {
      return this.currentParticipants.some(p => {
        // 确保比较的是相同类型的值
        return String(p.userId) === String(userId);
      });
    },
    
    // 获取用户选项标签（标注已添加的用户）
    getUserOptionLabel(user) {
      const deptName = user.dept && user.dept.deptName ? user.dept.deptName : '';
      const isAdded = this.isUserAlreadyAdded(user.userId);
      return `${user.userName} (${deptName})${isAdded ? ' [已添加]' : ''}`;
    },
    
    // 添加参与人员（优化版本）
    handleAddParticipants() {
      if (this.selectedUsers.length === 0) {
        this.$message.warning('请选择要添加的人员');
        return;
      }
      
      // 过滤掉已存在的用户（确保类型一致性）
      const existingIds = this.currentParticipants.map(p => String(p.userId));
      const validUsers = this.selectedUsers.filter(userId => {
        return !existingIds.includes(String(userId));
      });
      
      if (validUsers.length === 0) {
        this.$message.warning('选择的人员已存在，请选择其他人员');
        this.selectedUsers = []; // 清空选择
        return;
      }
      
      const newParticipants = validUsers.map(userId => {
        const user = this.userList.find(u => u.userId === userId);
        return {
          userId: userId,
          userName: user.userName,
          deptName: user.dept?.deptName || '',
          signInStatus: '0',
          attendanceStatus: '',
          remark: ''
        };
      });
      
      this.currentParticipants.push(...newParticipants);
      this.selectedUsers = [];
      
      const skippedCount = this.selectedUsers.length - validUsers.length;
      if (skippedCount > 0) {
        this.$message.success(`成功添加 ${validUsers.length} 人，${skippedCount} 人已存在`);
      } else {
        this.$message.success(`成功添加 ${validUsers.length} 人`);
      }
    },
    
    // 处理签到状态变化
    handleSignInChange(data) {
      // 可以在这里处理签到状态变化的逻辑
    },
    
    // 处理出勤状态变化
    handleAttendanceChange(data) {
      // 可以在这里处理出勤状态变化的逻辑
    },
    
    // 处理备注变化
    handleRemarkChange(data) {
      // 可以在这里处理备注变化的逻辑
    },
    
    // 处理参与人员移除
    handleParticipantRemoved(data) {
      this.currentParticipants = data.participants;
    },
    
    // 关闭对话框
    handleClose() {
      this.dialogVisible = false;
      this.selectedUsers = [];
    },
    
    // 确认保存
    handleConfirm() {
      const participantsData = this.$refs.participantsTable.getParticipantsData();
      this.$emit('confirm', participantsData);
      this.handleClose();
    }
  }
}
</script>

<style scoped>
.dialog-content {
  max-height: 600px;
  overflow-y: auto;
}

.add-participants-section {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 16px;
}

.dialog-footer {
  text-align: right;
}
</style>