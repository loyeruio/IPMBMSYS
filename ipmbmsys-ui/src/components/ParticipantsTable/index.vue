<template>
  <div class="participants-table-container">
    <!-- 参与人员表格 -->
    <el-table 
      :data="participantsData" 
      style="width: 100%"
      size="small"
      :row-class-name="tableRowClassName"
    >
      <el-table-column prop="userName" label="姓名" width="120" align="center" />
      <el-table-column prop="deptName" label="部门" width="150" align="center" />
      <el-table-column label="签到状态" width="120" align="center">
        <template slot-scope="scope">
          <el-select 
            v-if="editable" 
            v-model="scope.row.signInStatus" 
            size="mini"
            style="width: 100%"
            @change="handleSignInChange(scope.row, scope.$index)"
          >
            <el-option
              v-for="dict in dict.type.check_status"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
          <dict-tag 
            v-else
            :options="dict.type.check_status" 
            :value="scope.row.signInStatus"
          />
        </template>
      </el-table-column>
      <el-table-column label="参会情况" width="120" align="center">
        <template slot-scope="scope">
          <el-select 
            v-if="editable" 
            v-model="scope.row.attendanceStatus" 
            size="mini"
            style="width: 100%"
            @change="handleAttendanceChange(scope.row, scope.$index)"
          >
            <el-option
              v-for="dict in dict.type.attendance_status"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
          <dict-tag 
            v-else
            :options="dict.type.attendance_status" 
            :value="scope.row.attendanceStatus"
          />
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center">
        <template slot-scope="scope">
          <el-input
            v-if="editable"
            v-model="scope.row.remark"
            placeholder="请输入备注"
            size="mini"
            @change="handleRemarkChange(scope.row, scope.$index)"
          />
          <span v-else>{{ scope.row.remark || '无' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" align="center">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleEditParticipant(scope.row, scope.$index)"
            style="margin-right: 5px;"
          >
            修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleRemoveParticipant(scope.row, scope.$index)"
            style="color: #f56c6c;"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-dialog title="编辑参与人员" :visible.sync="editDialogVisible" width="500px" append-to-body>
      <el-form :model="currentParticipant" label-width="100px">
        <el-form-item label="姓名">
          <el-input v-model="currentParticipant.userName" disabled></el-input>
        </el-form-item>
        <el-form-item label="部门">
          <el-input v-model="currentParticipant.deptName" disabled></el-input>
        </el-form-item>
        <el-form-item label="签到状态">
          <el-select v-model="currentParticipant.signInStatus" style="width: 100%">
            <el-option
              v-for="dict in dict.type.check_status"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="参会情况">
          <el-select v-model="currentParticipant.attendanceStatus" style="width: 100%">
            <el-option
              v-for="dict in dict.type.attendance_status"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="currentParticipant.remark" type="textarea" rows="3"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="saveParticipantEdit">确 定</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script>
export default {
  name: 'ParticipantsTable',
  dicts: ['attendance_status', 'check_status'],
  props: {
    // 参与人员数据
    participants: {
      type: Array,
      default: () => []
    },
    // 用户列表
    userList: {
      type: Array,
      default: () => []
    },
    // 是否可编辑
    editable: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      participantsData: [],
      editDialogVisible: false,
      currentParticipant: {},
      currentIndex: -1
    }
  },
  computed: {
    // 统计信息
    statistics() {
      const total = this.participantsData.length;
      const attendance = this.participantsData.filter(p => p.attendanceStatus === '0').length;
      const absent = this.participantsData.filter(p => p.attendanceStatus === '1').length;
      const leave = this.participantsData.filter(p => p.attendanceStatus === '2').length;
      const late = this.participantsData.filter(p => p.attendanceStatus === '3').length;
      const signedIn = this.participantsData.filter(p => p.signInStatus === '1').length;
      
      return {
        total,
        attendance,
        absent,
        leave,
        late,
        signedIn
      }
    }
  },
  watch: {
    participants: {
      handler(newVal) {
        this.initParticipantsData(newVal);
      },
      immediate: true,
      deep: true
    }
  },
  // 在 created 或 mounted 钩子中初始化数据
  created() {
    this.initParticipantsData(this.participants);
  },
  methods: {
    // 修改 initParticipantsData 方法
    async initParticipantsData(data) {
      if (data instanceof Promise) {
        try {
          const resolvedData = await data;
          this.participantsData = resolvedData || [];
        } catch (error) {
          console.error('Error resolving participants data:', error);
          this.participantsData = [];
        }
      } else {
        this.participantsData = data || [];
      }
    },
        
    // 表格行样式
    tableRowClassName({ row }) {
      if (row.attendanceStatus === '1') {
        return 'el-table--row-danger';
      } else if (row.attendanceStatus === '2') {
        return 'el-table--row-warning';
      } else if (row.attendanceStatus === '3') {
        return 'el-table--row-danger';
      }
      return '';
    },
    
    // 处理签到状态变化
    handleSignInChange(row, index) {
      this.$emit('signin-change', {
        participant: row,
        index: index,
        participants: this.participantsData
      });
    },
    
    // 处理出勤状态变化
    handleAttendanceChange(row, index) {
      this.$emit('attendance-change', {
        participant: row,
        index: index,
        participants: this.participantsData
      });
    },
    
    // 处理备注变化
    handleRemarkChange(row, index) {
      this.$emit('remark-change', {
        participant: row,
        index: index,
        participants: this.participantsData
      });
    },
    
    // 编辑参与人员
    handleEditParticipant(participant, index) {
      this.currentParticipant = JSON.parse(JSON.stringify(participant));
      this.currentIndex = index;
      this.editDialogVisible = true;
    },
    
    // 保存编辑
    saveParticipantEdit() {
      // 更新本地数据
      if (this.currentIndex > -1) {
        this.participantsData[this.currentIndex] = { ...this.currentParticipant };
      }   
      // 触发事件通知父组件，修改事件名称为 update-participant
      this.$emit('update-participant', this.currentParticipant, this.currentIndex);
      // 关闭对话框
      this.editDialogVisible = false;
    },
    
    // 移除参与人员
    handleRemoveParticipant(participant, index) {
      this.$emit('remove-participant', participant, index);
    },
    
    // 获取当前参与人员数据
    getParticipantsData() {
      return this.participantsData;
    },
    
    // 添加参与人员
    addParticipants(newParticipants) {
      const existingIds = this.participantsData.map(p => p.userId);
      const toAdd = newParticipants.filter(p => !existingIds.includes(p.userId));
      this.participantsData.push(...toAdd);
      this.$emit('participants-updated', this.participantsData);
    },
    
    // 批量设置签到状态
    setBatchSignIn(status) {
      this.participantsData.forEach(participant => {
        participant.signInStatus = status;
      });
    },
    // 在methods中添加
    updateParticipantsData(newData) {
      this.participantsData = [...newData];
    },
    // 批量设置出勤状态
    setBatchAttendance(status) {
      this.participantsData.forEach(participant => {
        participant.attendanceStatus = status;
      });
    }
  }
}
</script>

<style scoped>
.participants-table-container {
  padding: 10px;
}

/* 使用Element UI的表格行样式 */
.el-table .el-table--row-danger {
  background-color: #fef0f0;
}

.el-table .el-table--row-warning {
  background-color: #fdf6ec;
}
</style>