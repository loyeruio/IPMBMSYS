<template>
  <div class="expand-content">
    <!-- 参加人员/与会人员卡片 -->
    <el-card class="expand-card" shadow="never">
      <div slot="header" class="card-header">
        <span class="card-title">
          <i class="el-icon-user"></i>
          {{ participantsTitle }}
        </span>
        <div class="header-actions">
          <el-button 
            size="mini" 
            type="primary" 
            icon="el-icon-edit"
            @click="$emit('edit-participants', rowData)"
          >
            {{ editButtonText }}
          </el-button>
          <el-button 
            v-if="showBatchButton"
            size="mini" 
            type="success" 
            icon="el-icon-s-data"
            @click="$emit('batch-action', rowData)"
          >
            {{ batchButtonText }}
          </el-button>
        </div>
      </div>
      <div class="card-content">
        <slot name="participants" :data="rowData">
          <!-- 默认参加人员显示 -->
          <div v-if="participants && participants.length > 0" class="participants-list">
            <el-tag 
              v-for="(participant, index) in participants" 
              :key="index" 
              class="participant-tag"
              size="small"
            >
              {{ participant.name || participant.studentName || participant }}
            </el-tag>
          </div>
          <div v-else class="empty-state">
            <i class="el-icon-user"></i>
            <span>{{ emptyParticipantsText }}</span>
            <el-button 
              type="text" 
              size="mini" 
              @click="$emit('edit-participants', rowData)"
            >
              {{ addButtonText }}
            </el-button>
          </div>
        </slot>
      </div>
    </el-card>
    
    <!-- 附件卡片 -->
    <el-card class="expand-card" shadow="never">
      <div slot="header" class="card-header">
        <span class="card-title">
          <i class="el-icon-paperclip"></i>
          {{ attachmentsTitle }}
        </span>
        <el-button 
          size="mini" 
          type="success" 
          icon="el-icon-upload"
          @click="$emit('upload-attachment', rowData)"
        >
          上传附件
        </el-button>
      </div>
      <div class="card-content">
        <slot name="attachments" :data="rowData">
          <!-- 默认附件显示 -->
          <div v-if="attachments && attachments.length > 0" class="attachments-list">
            <div 
              v-for="(attachment, index) in attachments"
              :key="index"
              class="attachment-item"
            >
              <div class="attachment-info">
                <i class="el-icon-document"></i>
                <span class="attachment-name">{{ attachment.name }}</span>
                <span class="attachment-size">{{ attachment.size }}</span>
              </div>
              <div class="attachment-actions">
                <el-button 
                  type="text" 
                  size="mini" 
                  icon="el-icon-download"
                  @click="$emit('download-attachment', attachment)"
                >
                  下载
                </el-button>
                <el-button 
                  type="text" 
                  size="mini" 
                  icon="el-icon-delete"
                  class="delete-btn"
                  @click="$emit('delete-attachment', rowData, index)"
                >
                  删除
                </el-button>
              </div>
            </div>
          </div>
          <div v-else class="empty-state">
            <i class="el-icon-document"></i>
            <span>暂无附件</span>
          </div>
        </slot>
      </div>
    </el-card>
    
    <!-- 出勤/参会情况卡片 -->
    <el-card class="expand-card" shadow="never">
      <div slot="header" class="card-header">
        <span class="card-title">
          <i class="el-icon-s-data"></i>
          {{ attendanceTitle }}
        </span>
      </div>
      <div class="card-content">
        <slot name="attendance" :data="rowData">
          <!-- 默认出勤情况显示 -->
          <div v-if="showAttendanceStats" class="attendance-info">
            <!-- 统计信息 -->
            <div class="participants-summary">
              <el-row :gutter="20" type="flex" justify="center">
                <el-col :span="4" :xs="12" :sm="6" :md="4">
                  <div class="summary-item total">
                    <div class="summary-number">{{ totalCount }}</div>
                    <div class="summary-label">总人数</div>
                  </div>
                </el-col>
                <el-col 
                  v-for="dict in dict.type.attendance_status" 
                  :key="dict.value" 
                  :span="4" 
                  :xs="12" 
                  :sm="6" 
                  :md="4"
                >
                  <div class="summary-item" :class="getStatusClass(dict.value)">
                    <div class="summary-number">{{ getStatusCount(dict.value) }}</div>
                    <div class="summary-label">{{ dict.label }}</div>
                  </div>
                </el-col>
              </el-row>
            </div>
          </div>
          <div v-else class="empty-state">
            <i class="el-icon-s-data"></i>
            <span>{{ emptyAttendanceText }}</span>
          </div>
        </slot>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'ExpandCard',
  dicts: ['attendance_status'],
  methods: {
    // 根据出勤状态值获取对应的统计数量
    getStatusCount(statusValue) {
      const statusMap = {
        '0': this.attendanceCount,  // 出勤
        '1': this.absentCount,      // 缺席
        '2': this.leaveCount,       // 请假
        '3': this.lateCount         // 迟到
      }
      return statusMap[statusValue] || 0
    },
    // 根据出勤状态值获取对应的CSS类名
    getStatusClass(statusValue) {
      const classMap = {
        '0': 'attendance',
        '1': 'absent', 
        '2': 'leave',
        '3': 'late'
      }
      return classMap[statusValue] || ''
    }
  },
  props: {
    // 行数据
    rowData: {
      type: Object,
      required: true
    },
    // 参加人员相关
    participantsTitle: {
      type: String,
      default: '参加人员详情'
    },
    participants: {
      type: Array,
      default: () => []
    },
    editButtonText: {
      type: String,
      default: '管理人员'
    },
    addButtonText: {
      type: String,
      default: '添加人员'
    },
    emptyParticipantsText: {
      type: String,
      default: '暂无参加人员'
    },
    showBatchButton: {
      type: Boolean,
      default: false
    },
    batchButtonText: {
      type: String,
      default: '批量操作'
    },
    // 附件相关
    attachmentsTitle: {
      type: String,
      default: '相关附件'
    },
    attachments: {
      type: Array,
      default: () => []
    },
    // 出勤相关
    attendanceTitle: {
      type: String,
      default: '出勤情况记录'
    },
    showAttendanceButton: {
      type: Boolean,
      default: true
    },
    attendanceButtonText: {
      type: String,
      default: '记录出勤'
    },
    showAttendanceStats: {
      type: Boolean,
      default: true
    },
    totalCount: {
      type: Number,
      default: 0
    },
    attendanceCount: {
      type: Number,
      default: 0
    },
    absentCount: {
      type: Number,
      default: 0
    },
    leaveCount: {
      type: Number,
      default: 0
    },
    lateCount: {
      type: Number,
      default: 0
    },
    attendanceDescription: {
      type: String,
      default: ''
    },
    emptyAttendanceText: {
      type: String,
      default: '暂无出勤记录'
    }
  }
}
</script>

<style scoped>
/* 统一的展开卡片样式 */
.expand-content {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.expand-card {
  margin-bottom: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
}

.expand-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.expand-card:last-child {
  margin-bottom: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0;
  min-height: 40px; /* 确保最小高度一致 */
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
}

.card-title i {
  margin-right: 8px;
  color: #409eff;
  font-size: 18px;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.card-content {
  padding-top: 0px;
}

/* 参加人员列表样式 */
.participants-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.participant-tag {
  margin: 0;
  border-radius: 16px;
  padding: 4px 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
}

/* 附件列表样式 */
.attachments-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.attachment-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: white;
  border-radius: 6px;
  border: 1px solid #e4e7ed;
  transition: all 0.3s ease;
}

.attachment-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.attachment-info {
  display: flex;
  align-items: center;
  flex: 1;
}

.attachment-info i {
  margin-right: 8px;
  color: #409eff;
  font-size: 16px;
}

.attachment-name {
  font-weight: 500;
  color: #303133;
  margin-right: 12px;
}

.attachment-size {
  color: #909399;
  font-size: 12px;
}

.attachment-actions {
  display: flex;
  gap: 8px;
}

.delete-btn {
  color: #f56c6c !important;
}

/* 出勤统计样式 */
.attendance-info {
  padding: 16px 0;
}

.participants-summary {
  margin-bottom: 20px;
}

.summary-item {
  text-align: center;
  padding: 16px 12px;
  background: #ffffff;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  transition: all 0.3s ease;
}

.summary-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.summary-number {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
  line-height: 1;
}

.summary-label {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

/* 不同状态的颜色 */
.summary-item.total .summary-number {
  color: #409eff;
}

.summary-item.attendance .summary-number {
  color: #67c23a;
}

.summary-item.absent .summary-number {
  color: #f56c6c;
}

.summary-item.leave .summary-number {
  color: #e6a23c;
}

.summary-item.late .summary-number {
  color: #909399;
}

.attendance-description {
  text-align: center;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #409eff;
}

.attendance-description-content {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #606266;
  font-size: 14px;
}

.attendance-description-content i {
  margin-right: 8px;
  color: #409eff;
  font-size: 16px;
}

/* 空状态样式 */
.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #909399;
  font-size: 14px;
}

.empty-state i {
  font-size: 48px;
  margin-bottom: 16px;
  display: block;
  color: #c0c4cc;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .summary-item {
    margin-bottom: 12px;
    padding: 12px 8px;
  }
  
  .summary-number {
    font-size: 20px;
  }
  
  .summary-label {
    font-size: 12px;
  }
}
</style>
