<template>
  <view class="detail-container">
    <!-- 加载状态 -->
    <view v-if="loading" class="loading-container">
      <uni-load-more status="loading"></uni-load-more>
    </view>
    
    <!-- 详情内容 -->
    <view v-else class="detail-content">
      <!-- 会议基本信息卡片 -->
      <uni-card class="meeting-header" :is-shadow="false" spacing="0">
        <view class="meeting-title">{{ meetingDetail.title }}</view>
        <!-- 会议基本信息卡片中的标签部分 -->
        <view class="meeting-tags">
          <!-- 使用字典标签组件 -->
          <DictTag 
            v-if="dict && dict.type && dict.type.meeting_type && Array.isArray(dict.type.meeting_type)"
            :options="dict.type.meeting_type"
            :value="meetingDetail.meetingType"
          />
          
          <DictTag 
            v-if="dict && dict.type && dict.type.meetings_status && Array.isArray(dict.type.meetings_status)"
            :options="dict.type.meetings_status"
            :value="meetingDetail.status"
          />
        </view>
      </uni-card>
      
      <!-- 会议详细信息卡片 -->
      <uni-card class="meeting-info" title="会议信息" :is-shadow="false">
        <view class="info-row">
          <view class="info-label">
            <uni-icons type="calendar" size="16" color="#909399"></uni-icons>
            <text>开始时间</text>
          </view>
          <view class="info-value">{{ formatDateTime(meetingDetail.startTime) }}</view>
        </view>
        
        <view class="info-row">
          <view class="info-label">
            <uni-icons type="calendar" size="16" color="#909399"></uni-icons>
            <text>结束时间</text>
          </view>
          <view class="info-value">{{ formatDateTime(meetingDetail.endTime) }}</view>
        </view>
        
        <view class="info-row">
          <view class="info-label">
            <uni-icons type="location" size="16" color="#909399"></uni-icons>
            <text>会议地点</text>
          </view>
          <view class="info-value">{{ meetingDetail.location || '未设置' }}</view>
        </view>
        
        <view class="info-row">
          <view class="info-label">
            <uni-icons type="person" size="16" color="#909399"></uni-icons>
            <text>会议负责人</text>
          </view>
          <view class="info-value">{{ getOrganizerName(meetingDetail.organizerId) }}</view>
        </view>
        
        <!-- 会议详细信息卡片中的部门显示 -->
        <view class="info-row">
          <view class="info-label">
            <uni-icons type="home" size="16" color="#909399"></uni-icons>
            <text>所属部门</text>
          </view>
          <view class="info-value">{{ getMeetingDeptName(meetingDetail.branchId) }}</view>
        </view>
      </uni-card>
      
      <!-- 会议内容卡片 -->
      <uni-card v-if="meetingDetail.description" title="会议内容" :is-shadow="false">
        <view class="meeting-content">
          <rich-text :nodes="meetingDetail.description"></rich-text>
        </view>
      </uni-card>
      
      <!-- 附件信息卡片 -->
      <uni-card v-if="attachmentList.length > 0" title="会议附件" :is-shadow="false">
        <view class="attachment-list">
          <view 
            v-for="(attachment, index) in attachmentList" 
            :key="index"
            class="attachment-item"
            @click="downloadAttachment(attachment)"
          >
            <view class="attachment-info">
              <view class="attachment-icon">
                <uni-icons type="paperclip" size="20" color="#409eff"></uni-icons>
              </view>
              <view class="attachment-details">
                <text class="attachment-name">{{ attachment.name }}</text>
                <text class="attachment-size">{{ attachment.size }}</text>
              </view>
            </view>
            <view class="download-icon">
              <uni-icons type="download" size="16" color="#909399"></uni-icons>
            </view>
          </view>
        </view>
      </uni-card>
      
      <!-- 参会人员卡片 -->
      <uni-card title="参会人员" :is-shadow="false">
        <!-- 将操作按钮移到卡片内容顶部，作为工具栏 -->
        <view class="participant-toolbar">
          <view class="toolbar-left">
            <text class="participant-count">共 {{ participants.length }} 人</text>
          </view>
          <view class="toolbar-right">
            <view class="tool-btn" @click="handleSendNotification">
              <uni-icons type="bell" size="14" color="#409eff"></uni-icons>
              <text class="tool-btn-text">通知</text>
            </view>
            <view class="tool-btn" @click="handleSendSignIn">
              <uni-icons type="compose" size="14" color="#67c23a"></uni-icons>
              <text class="tool-btn-text">签到</text>
            </view>
          </view>
        </view>
        
        <view v-if="participants.length > 0" class="participants-list">
          <view 
            v-for="(participant, index) in participants" 
            :key="index"
            class="participant-item"
          >
            <view class="participant-info">
              <view class="participant-details">
                <text class="participant-name">{{ participant.userName }}</text>
                <text class="participant-dept">{{ participant.deptName }}</text>
                <!-- 添加备注显示 -->
                <text v-if="participant.remark" class="participant-remark">备注：{{ participant.remark }}</text>
              </view>
            </view>
            <view class="participant-status">
              <!-- 签到状态 -->
              <DictTag 
                v-if="dict && dict.type && dict.type.check_status && Array.isArray(dict.type.check_status)"
                :options="dict.type.check_status"
                :value="participant.signInStatus"
              />
              
              <!-- 出勤状态 -->
              <DictTag 
                v-if="dict && dict.type && dict.type.attendance_status && Array.isArray(dict.type.attendance_status)"
                :options="dict.type.attendance_status"
                :value="participant.attendanceStatus"
                style="margin-left: 8px;"
              />
            </view>
          </view>
        </view>
        
        <view v-else class="empty-participants">
          <uni-icons type="person" size="40" color="#d3d4d6"></uni-icons>
          <text class="empty-text">暂无参会人员</text>
        </view>
      </uni-card>
      
      <!-- 签到统计卡片 -->
      <uni-card title="签到统计" :is-shadow="false">
        <view class="stats-container">
          <view class="stat-item">
            <text class="stat-number">{{ participants.length }}</text>
            <text class="stat-label">总人数</text>
          </view>
          <view class="stat-item">
            <text class="stat-number">{{ getSignInCount('1') }}</text>
            <text class="stat-label">已签到</text>
          </view>
          <view class="stat-item">
            <text class="stat-number">{{ getSignInCount('0') }}</text>
            <text class="stat-label">未签到</text>
          </view>
        </view>
      </uni-card>
    </view>
    
    <!-- 悬浮编辑按钮 -->
    <view class="floating-edit-btn" @click="handleEdit">
      <uni-icons type="compose" size="24" color="#fff"></uni-icons>
    </view>
    
    <!-- 底部签到按钮 -->
    <view v-if="canSignIn" class="bottom-actions">
      <button 
        class="sign-in-btn" 
        :loading="signInLoading" 
        :disabled="isSignedIn"
        @click="handleSignIn"
      >
        {{ isSignedIn ? '已签到' : '立即签到' }}
      </button>
    </view>
    
    <!-- 发布通知弹窗 -->
    <uni-popup ref="notificationPopup" type="bottom">
      <view class="popup-container-new">
        <view class="popup-header-new">
          <view class="header-left">
            <uni-icons type="bell" size="20" color="#409eff"></uni-icons>
            <text class="popup-title-new">发布通知</text>
          </view>
          <view class="close-btn" @click="closeNotificationPopup">
            <uni-icons type="closeempty" size="18" color="#909399"></uni-icons>
          </view>
        </view>
        
        <view class="popup-content">
          <uni-forms ref="notificationForm" :model="notificationForm" :rules="notificationRules" label-width="80px">
            <view class="form-section">
              <uni-forms-item label="标题" name="title" required>
                <uni-easyinput 
                  v-model="notificationForm.title" 
                  placeholder="请输入通知标题"
                  :maxlength="50"
                  :styles="inputStyles"
                ></uni-easyinput>
              </uni-forms-item>
              
              <uni-forms-item label="内容" name="content" required>
                <uni-easyinput 
                  v-model="notificationForm.content" 
                  type="textarea" 
                  placeholder="请输入通知内容"
                  :maxlength="500"
                  :styles="textareaStyles"
                ></uni-easyinput>
              </uni-forms-item>
            </view>
            
            <view class="info-section">
              <view class="info-item">
                <view class="info-label">
                  <uni-icons type="person" size="14" color="#909399"></uni-icons>
                  <text>发布对象</text>
                </view>
                <view class="participants-tags-new">
                  <uni-tag 
                    v-for="(participant, index) in participants" 
                    :key="index"
                    size="small"
                    type="primary"
                    :text="participant.userName"
                  ></uni-tag>
                </view>
              </view>
              
              <view class="info-item">
                <view class="info-label">
                  <uni-icons type="calendar" size="14" color="#909399"></uni-icons>
                  <text>会议时间</text>
                </view>
                <text class="time-display-new">{{ activityTimeDisplay }}</text>
              </view>
            </view>
          </uni-forms>
        </view>
        
        <view class="popup-actions-new">
          <view class="action-btn-new cancel" @click="closeNotificationPopup">
            <text class="btn-text-new">取消</text>
          </view>
          <view class="action-btn-new primary" @click="submitNotification">
            <text class="btn-text-new">发布通知</text>
          </view>
        </view>
      </view>
    </uni-popup>
    
    <!-- 发布签到弹窗 -->
    <uni-popup ref="signInPopup" type="bottom">
      <view class="popup-container-new">
        <view class="popup-header-new">
          <view class="header-left">
            <uni-icons type="compose" size="20" color="#67c23a"></uni-icons>
            <text class="popup-title-new">发布签到</text>
          </view>
          <view class="close-btn" @click="closeSignInPopup">
            <uni-icons type="closeempty" size="18" color="#909399"></uni-icons>
          </view>
        </view>
        
        <view class="popup-content">
          <uni-forms ref="signInForm" :model="signInForm" :rules="signInRules" label-width="80px">
            <view class="form-section">
              <uni-forms-item label="截止时间" name="deadline" required>
                <uni-datetime-picker 
                  v-model="signInForm.deadline" 
                  type="datetime"
                  placeholder="请选择签到截止时间"
                  :styles="pickerStyles"
                ></uni-datetime-picker>
              </uni-forms-item>
            </view>
            
            <view class="info-section">
              <view class="info-item">
                <view class="info-label">
                  <uni-icons type="person" size="14" color="#909399"></uni-icons>
                  <text>发布对象</text>
                </view>
                <view class="participants-tags-new">
                  <uni-tag 
                    v-for="(participant, index) in participants" 
                    :key="index"
                    size="small"
                    type="primary"
                    :text="participant.userName"
                  ></uni-tag>
                </view>
              </view>
              
              <view class="info-item">
                <view class="info-label">
                  <uni-icons type="calendar" size="14" color="#909399"></uni-icons>
                  <text>会议时间</text>
                </view>
                <text class="time-display-new">{{ activityTimeDisplay }}</text>
              </view>
            </view>
          </uni-forms>
        </view>
        
        <view class="popup-actions-new">
          <view class="action-btn-new cancel" @click="closeSignInPopup">
            <text class="btn-text-new">取消</text>
          </view>
          <view class="action-btn-new primary" @click="submitSignIn">
            <text class="btn-text-new">发布签到</text>
          </view>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script>
import { getCommon } from '@/api/meeting/meeting'
import { listMeetingparticipant, updateMeetingparticipant } from '@/api/participant/meetingparticipant'
import { listAllUser } from '@/api/system/user'
import { listDept } from '@/api/system/dept'
import { addNotice } from '@/api/system/notice'
import { AttachmentManager } from '@/utils/attachment'
import DictTag from '@/components/DictTag/index.vue'

export default {
  dicts: ['meeting_type', 'meetings_status', 'attendance_status', 'check_status'],
  components: {
    DictTag
  },
  data() {
    return {
      meetingId: null,
      meetingDetail: {},
      participants: [],
      userList: [],
      currentUser: null,
      loading: false,
      signInLoading: false,
      deptList: [],
      attachmentManager: new AttachmentManager(),
      
      // 通知相关数据
      notificationForm: {
        title: '',
        content: ''
      },
      notificationRules: {
        title: {
          rules: [{
            required: true,
            errorMessage: '请输入通知标题'
          }]
        },
        content: {
          rules: [{
            required: true,
            errorMessage: '请输入通知内容'
          }]
        }
      },
      
      // 签到相关数据
      signInForm: {
        deadline: null
      },
      signInRules: {
        deadline: {
          rules: [{
            required: true,
            errorMessage: '请选择签到截止时间'
          }]
        }
      },
      
      // 表单样式配置
      inputStyles: {
        borderColor: '#e4e7ed',
        borderRadius: '6px',
        backgroundColor: '#ffffff'
      },
      textareaStyles: {
        borderColor: '#e4e7ed',
        borderRadius: '6px',
        backgroundColor: '#ffffff',
        minHeight: '80px'
      },
      pickerStyles: {
        borderColor: '#e4e7ed',
        borderRadius: '6px',
        backgroundColor: '#ffffff'
      }
    }
  },
  
  computed: {
    // 当前用户是否可以签到
    canSignIn() {
      return this.currentUser && this.participants.some(p => p.userId === this.currentUser.userId)
    },
    
    // 当前用户是否已签到
    isSignedIn() {
      if (!this.currentUser) return false
      const participant = this.participants.find(p => p.userId === this.currentUser.userId)
      return participant && participant.signInStatus === '1'
    },
    
    // 附件列表
    attachmentList() {
      return this.attachmentManager.parseAttachments(this.meetingDetail.attachments)
    },
    
    // 活动时间显示
    activityTimeDisplay() {
      if (this.meetingDetail.startTime && this.meetingDetail.endTime) {
        return `${this.formatDateTime(this.meetingDetail.startTime)} 至 ${this.formatDateTime(this.meetingDetail.endTime)}`
      } else if (this.meetingDetail.startTime) {
        return this.formatDateTime(this.meetingDetail.startTime)
      } else {
        return '未设置'
      }
    }
  },
  
  onLoad(options) {
    if (options.id) {
      this.meetingId = options.id
      this.init()
    }
  },
  
  methods: {
    // 初始化
    async init() {
      await Promise.all([
        this.getCurrentUser(),
        this.loadUserList(),
        this.loadDeptList(),
        this.getMeetingDetail()
      ])
    },
    
    // 获取当前用户信息
    getCurrentUser() {
      this.currentUser = this.$store.getters.userInfo
    },
    
    // 加载部门列表
    async loadDeptList() {
      try {
        const response = await listDept({})
        this.deptList = response.data || []
      } catch (error) {
        console.error('加载部门列表失败:', error)
      }
    },
    
    // 加载用户列表
    async loadUserList() {
      try {
        const response = await listAllUser({})
        this.userList = response.data || []
      } catch (error) {
        console.error('加载用户列表失败:', error)
      }
    },
    
    // 获取负责人姓名
    getOrganizerName(organizerId) {
      if (!organizerId) return '未设置负责人'
      const user = this.userList.find(u => u.userId === organizerId)
      return user ? user.userName : '未设置负责人'
    },
    
    // 获取会议详情
    async getMeetingDetail() {
      try {
        this.loading = true
        const response = await getCommon(this.meetingId)
        this.meetingDetail = response.data
        
        // 获取参会人员
        await this.getParticipants()
      } catch (error) {
        console.error('获取会议详情失败:', error)
        this.$modal.msgError('获取会议详情失败')
      } finally {
        this.loading = false
      }
    },
    
    // 获取参会人员
    async getParticipants() {
      try {
        const response = await listMeetingparticipant({ meetingId: this.meetingId })
        this.participants = response.rows || []
      } catch (error) {
        console.error('获取参会人员失败:', error)
      }
    },
    
    // 签到
    async handleSignIn() {
      if (!this.currentUser) {
        this.$modal.msgError('请先登录')
        return
      }
      
      if (this.isSignedIn) {
        this.$modal.msgError('您已签到')
        return
      }
      
      try {
        this.signInLoading = true
        
        const participant = this.participants.find(p => p.userId === this.currentUser.userId)
        if (!participant) {
          this.$modal.msgError('您不在参会人员名单中')
          return
        }
        
        // 更新签到状态
        await updateMeetingparticipant({
          ...participant,
          signInStatus: '1'
        })
        
        this.$modal.msgSuccess('签到成功')
        await this.getParticipants()
      } catch (error) {
        console.error('签到失败:', error)
        this.$modal.msgError('签到失败')
      } finally {
        this.signInLoading = false
      }
    },
    
    // 处理编辑
    handleEdit() {
      uni.navigateTo({
        url: `/pages/meeting/participants?id=${this.meetingId}`
      })
    },
    
    // 下载附件
    downloadAttachment(attachment) {
      this.attachmentManager.downloadAttachment(attachment)
    },
    
    // 获取签到统计
    getSignInCount(status) {
      return this.participants.filter(p => p.signInStatus === status).length
    },
    
    // 获取会议部门名称
    getMeetingDeptName(branchId) {
      if (!branchId) return '未设置'
      const dept = this.deptList.find(d => d.deptId === branchId)
      return dept ? dept.deptName : '未设置'
    },

    // 格式化日期时间
    formatDateTime(dateTime) {
      if (!dateTime) return ''
      const date = new Date(dateTime)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}`
    },
    
    // 处理发布通知
    handleSendNotification() {
      if (this.participants.length === 0) {
        this.$modal.msgError('暂无参会人员，无法发布通知')
        return
      }
      
      // 重置表单
      this.notificationForm = {
        title: `关于${this.meetingDetail.title}的通知`,
        content: ''
      }
      
      this.$refs.notificationPopup.open()
    },
    
    // 处理发布签到
    handleSendSignIn() {
      if (this.participants.length === 0) {
        this.$modal.msgError('暂无参会人员，无法发布签到')
        return
      }
      
      // 设置默认截止时间为当前时间后2小时
      const now = new Date()
      now.setHours(now.getHours() + 2)
      this.signInForm.deadline = now.toISOString().slice(0, 19).replace('T', ' ')
      
      this.$refs.signInPopup.open()
    },
    
    // 关闭通知弹窗
    closeNotificationPopup() {
      this.$refs.notificationPopup.close()
    },
    
    // 关闭签到弹窗
    closeSignInPopup() {
      this.$refs.signInPopup.close()
    },
    
    // 提交通知
    async submitNotification() {
      try {
        await this.$refs.notificationForm.validate()
        
        const userIds = this.participants.map(p => p.userId)
        
        const noticeData = {
          noticeTitle: this.notificationForm.title,
          noticeType: '1', // 1-通知
          noticeContent: this.notificationForm.content,
          status: '0', // 0-正常
          noticeUserIds: userIds.join(','),
          recordTime: this.meetingDetail.startTime || null,
          attachMent: this.meetingDetail.attachments || null,
          activityType: '0', // 0-会议
          activityId: this.meetingId ? this.meetingId.toString() : null
        }
        
        await addNotice(noticeData)
        this.$modal.msgSuccess('通知发布成功')
        this.closeNotificationPopup()
      } catch (error) {
        console.error('发布通知失败:', error)
        this.$modal.msgError('发布通知失败')
      }
    },
    
    // 提交签到
    async submitSignIn() {
      try {
        await this.$refs.signInForm.validate()
        
        const userIds = this.participants.map(p => p.userId)
        
        const noticeData = {
          noticeTitle: `${this.meetingDetail.title}签到`,
          noticeType: '3', // 3-签到
          noticeContent: `请在${this.formatDateTime(this.signInForm.deadline)}前完成签到`,
          status: '0', // 0-正常
          noticeUserIds: userIds.join(','),
          recordTime: this.signInForm.deadline,
          activityType: '0', // 0-会议
          activityId: this.meetingId ? this.meetingId.toString() : null
        }
        
        await addNotice(noticeData)
        this.$modal.msgSuccess('签到发布成功')
        this.closeSignInPopup()
      } catch (error) {
        console.error('发布签到失败:', error)
        this.$modal.msgError('发布签到失败')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.detail-container {
  background-color: #f5f5f5;
  min-height: 100vh;
  padding-bottom: 80px;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}

.detail-content {
  padding: 10px;
}

.meeting-header {
  margin-bottom: 15px;
  
  .meeting-title {
    font-size: 18px;
    font-weight: bold;
    color: #303133;
    margin-bottom: 10px;
    line-height: 1.4;
  }
  
  .meeting-tags {
    display: flex;
    gap: 8px;
  }
}

.meeting-info {
  margin-bottom: 15px;
  
  .info-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 0;
    border-bottom: 1px solid #f0f0f0;
    
    &:last-child {
      border-bottom: none;
    }
    
    .info-label {
      display: flex;
      align-items: center;
      gap: 6px;
      font-size: 14px;
      color: #606266;
    }
    
    .info-value {
      font-size: 14px;
      color: #303133;
      text-align: right;
      flex: 1;
      margin-left: 20px;
    }
  }
}

.meeting-content {
  line-height: 1.6;
  color: #606266;
  margin-bottom: 15px;
}

// 附件列表样式
.attachment-list {
  .attachment-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 0;
    border-bottom: 1px solid #f0f0f0;
    cursor: pointer;
    transition: background-color 0.2s;
    
    &:last-child {
      border-bottom: none;
    }
    
    &:active {
      background-color: #f8f9fa;
    }
    
    .attachment-info {
      display: flex;
      align-items: center;
      flex: 1;
      
      .attachment-icon {
        margin-right: 12px;
      }
      
      .attachment-details {
        flex: 1;
        
        .attachment-name {
          display: block;
          font-size: 13px;
          color: #303133;
          margin-bottom: 4px;
          word-break: break-all;
        }
        
        .attachment-size {
          font-size: 12px;
          color: #909399;
        }
      }
    }
    
    .download-icon {
      margin-left: 10px;
    }
  }
}

// 参会人员工具栏样式
.participant-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  margin-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
  
  .toolbar-left {
    .participant-count {
      font-size: 13px;
      color: #909399;
    }
  }
  
  .toolbar-right {
    display: flex;
    align-items: center;
    
    .tool-btn {
      display: flex;
      align-items: center;
      gap: 4px;
      padding: 6px 12px;
      background: transparent;
      border: none;
      transition: all 0.2s ease;
      
      &:active {
        transform: scale(0.95);
        background: #f5f5f5;
      }
      
      &:not(:last-child) {
        border-right: 1px solid #e4e7ed;
        padding-right: 12px;
        margin-right: 12px;
      }
      
      .tool-btn-text {
        font-size: 12px;
        color: #606266;
      }
    }
  }
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.participants-list {
  margin-bottom: 15px;
  
  .participant-item {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    padding: 12px 0;
    border-bottom: 1px solid #f0f0f0;
    
    &:last-child {
      border-bottom: none;
    }
    
    .participant-info {
      flex: 1;
      
      .participant-details {
        .participant-name {
          display: block;
          font-size: 14px;
          color: #303133;
          margin-bottom: 4px;
        }
        
        .participant-dept {
          display: block;
          font-size: 12px;
          color: #909399;
          margin-bottom: 4px;
        }
        
        .participant-remark {
          display: block;
          font-size: 12px;
          color: #666;
          line-height: 1.4;
          word-break: break-all;
        }
      }
    }
    
    .participant-status {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-left: 10px;
    }
  }
}

.empty-participants {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 20px;
  
  .empty-text {
    margin-top: 10px;
    font-size: 14px;
    color: #909399;
  }
}

.stats-container {
  display: flex;
  justify-content: space-around;
  
  .stat-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    
    .stat-number {
      font-size: 24px;
      font-weight: bold;
      color: #e60012;
      margin-bottom: 4px;
    }
    
    .stat-label {
      font-size: 12px;
      color: #909399;
    }
  }
}

// 悬浮编辑按钮
.floating-edit-btn {
  position: fixed;
  right: 20px;
  bottom: 80px;
  width: 56px;
  height: 56px;
  border-radius: 28px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.4);
  z-index: 999;
  transition: all 0.3s ease;
  
  &:active {
    transform: scale(0.95);
    box-shadow: 0 2px 15px rgba(102, 126, 234, 0.6);
  }
  
  &::before {
    content: '';
    position: absolute;
    top: -2px;
    left: -2px;
    right: -2px;
    bottom: -2px;
    border-radius: 30px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    opacity: 0.3;
    animation: pulse 2s infinite;
    z-index: -1;
  }
}

@keyframes pulse {
  0% {
    transform: scale(1);
    opacity: 0.3;
  }
  50% {
    transform: scale(1.1);
    opacity: 0.1;
  }
  100% {
    transform: scale(1);
    opacity: 0.3;
  }
}

.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 15px;
  background: white;
  border-top: 1px solid #ebeef5;
  z-index: 999;
  
  .sign-in-btn {
    width: 100%;
    height: 44px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border: none;
    border-radius: 8px;
    font-size: 16px;
    font-weight: 500;
    
    &[disabled] {
      background: #c0c4cc;
      color: #ffffff;
    }
  }
}

// 弹窗样式
.popup-container-new {
  width: 100vw;
  max-width: 400px;
  background: white;
  border-radius: 16px 16px 0 0;
  overflow: hidden;
  
  .popup-header-new {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 24px 16px;
    background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
    
    .header-left {
      display: flex;
      align-items: center;
      gap: 8px;
      
      .popup-title-new {
        font-size: 18px;
        font-weight: 600;
        color: #303133;
      }
    }
    
    .close-btn {
      padding: 4px;
      border-radius: 50%;
      transition: background 0.2s ease;
      
      &:active {
        background: #f0f0f0;
      }
    }
  }
  
  .popup-content {
    padding: 0 24px 20px;
    
    .form-section {
      margin-bottom: 20px;
    }
    
    .info-section {
      .info-item {
        margin-bottom: 16px;
        
        &:last-child {
          margin-bottom: 0;
        }
        
        .info-label {
          display: flex;
          align-items: center;
          gap: 6px;
          margin-bottom: 8px;
          
          text {
            font-size: 14px;
            color: #606266;
            font-weight: 500;
          }
        }
      }
    }
  }
  
  .participants-tags-new {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
    padding: 12px;
    background: #f8f9fa;
    border-radius: 8px;
    border: 1px solid #e4e7ed;
    max-height: 100px;
    overflow-y: auto;
  }
  
  .time-display-new {
    display: block;
    font-size: 14px;
    color: #303133;
    padding: 12px;
    background: #f8f9fa;
    border-radius: 8px;
    border: 1px solid #e4e7ed;
  }
  
  .popup-actions-new {
    display: flex;
    gap: 12px;
    padding: 20px 24px;
    background: #fafafa;
    
    .action-btn-new {
      flex: 1;
      height: 44px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 8px;
      transition: all 0.2s ease;
      
      &.cancel {
        background: #ffffff;
        border: 1px solid #dcdfe6;
        
        .btn-text-new {
          color: #606266;
        }
        
        &:active {
          background: #f5f7fa;
          transform: scale(0.98);
        }
      }
      
      &.primary {
        background: linear-gradient(135deg, #409eff 0%, #3a8ee6 100%);
        
        .btn-text-new {
          color: #ffffff;
        }
        
        &:active {
          background: linear-gradient(135deg, #3a8ee6 0%, #337ecc 100%);
          transform: scale(0.98);
        }
      }
      
      .btn-text-new {
        font-size: 16px;
        font-weight: 500;
      }
    }
  }
}

// 响应式适配
@media (max-width: 375px) {
  .info-row {
    flex-direction: column;
    align-items: flex-start !important;
    gap: 8px;
    
    .info-value {
      text-align: left !important;
      margin-left: 0 !important;
    }
  }
  
  .stats-container {
    flex-direction: column;
    gap: 20px;
    
    .stat-item {
      flex-direction: row;
      justify-content: space-between;
    }
  }
  
  .toolbar-right {
      flex-direction: column;
      gap: 6px;
      
      .tool-btn {
        padding: 3px 6px;
        
        .tool-btn-text {
          font-size: 10px;
        }
      }
    }
}
</style>