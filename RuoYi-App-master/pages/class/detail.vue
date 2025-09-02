<template>
  <view class="detail-container">
    <!-- 加载状态 -->
    <view v-if="loading" class="loading-container">
      <uni-load-more status="loading"></uni-load-more>
    </view>
    
    <!-- 详情内容 -->
    <view v-else class="detail-content">
      <!-- 党课基本信息卡片 -->
      <uni-card class="class-header" :is-shadow="false" spacing="0">
        <view class="class-title">{{ classDetail.title }}</view>
        <!-- 党课基本信息卡片中的标签部分 -->
        <view class="class-tags">
          <!-- 使用字典标签组件 -->
          <DictTag 
            v-if="dict && dict.type && dict.type.meetings_status && Array.isArray(dict.type.meetings_status)"
            :options="dict.type.meetings_status"
            :value="classDetail.status"
          />
        </view>
      </uni-card>
      
      <!-- 党课详细信息卡片 -->
      <uni-card class="class-info" title="党课信息" :is-shadow="false">
        <view class="info-row">
          <view class="info-label">
            <uni-icons type="calendar" size="16" color="#909399"></uni-icons>
            <text>开课时间</text>
          </view>
          <view class="info-value">{{ formatDateTime(classDetail.startTime) }}</view>
        </view>
        
        <view class="info-row">
          <view class="info-label">
            <uni-icons type="calendar" size="16" color="#909399"></uni-icons>
            <text>结束时间</text>
          </view>
          <view class="info-value">{{ formatDateTime(classDetail.endTime) }}</view>
        </view>
        
        <view class="info-row">
          <view class="info-label">
            <uni-icons type="location" size="16" color="#909399"></uni-icons>
            <text>上课地点</text>
          </view>
          <view class="info-value">{{ classDetail.location || '未设置' }}</view>
        </view>
        
        <view class="info-row">
          <view class="info-label">
            <uni-icons type="person-filled" size="16" color="#909399"></uni-icons>
            <text>授课讲师</text>
          </view>
          <view class="info-value">{{ classDetail.lecturer || '未设置' }}</view>
        </view>
        
        <view class="info-row">
          <view class="info-label">
            <uni-icons type="person" size="16" color="#909399"></uni-icons>
            <text>组织负责人</text>
          </view>
          <view class="info-value">{{ getOrganizerName(classDetail.organizerId) }}</view>
        </view>
        
        <view class="info-row">
          <view class="info-label">
            <uni-icons type="circle" size="16" color="#909399"></uni-icons>
            <text>课时</text>
          </view>
          <view class="info-value">{{ classDetail.duration || '0' }}课时</view>
        </view>
        
        <!-- 党课详细信息卡片中的部门显示 -->
        <view class="info-row">
          <view class="info-label">
            <uni-icons type="home" size="16" color="#909399"></uni-icons>
            <text>所属部门</text>
          </view>
          <view class="info-value">{{ getClassDeptName(classDetail.branchId) }}</view>
        </view>
      </uni-card>
      
      <!-- 党课内容卡片 -->
      <uni-card v-if="classDetail.description" title="课程简介" :is-shadow="false">
        <view class="class-content">
          <rich-text :nodes="classDetail.description"></rich-text>
        </view>
      </uni-card>
      
      <!-- 附件信息卡片 -->
      <uni-card v-if="attachmentList.length > 0" title="课程附件" :is-shadow="false">
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
      
      <!-- 参课人员卡片 -->
      <uni-card title="参课人员" :is-shadow="false">
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
          <text class="empty-text">暂无参课人员</text>
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
                  <text>党课时间</text>
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
                  <text>党课时间</text>
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
import { getClass } from '@/api/class/class'
import { listClassparticipant, updateClassparticipant } from '@/api/participant/classparticipant'
import { listAllUser } from '@/api/system/user'
import { listDept } from '@/api/system/dept'
import { addNotice } from '@/api/system/notice'
import { AttachmentManager } from '@/utils/attachment'
import DictTag from '@/components/DictTag/index.vue'

export default {
  dicts: ['meetings_status', 'attendance_status', 'check_status'],
  components: {
    DictTag
  },
  data() {
    return {
      classId: null,
      classDetail: {},
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
      return this.attachmentManager.parseAttachments(this.classDetail.attachments)
    },
    
    // 活动时间显示
    activityTimeDisplay() {
      if (this.classDetail.startTime && this.classDetail.endTime) {
        return `${this.formatDateTime(this.classDetail.startTime)} 至 ${this.formatDateTime(this.classDetail.endTime)}`
      } else if (this.classDetail.startTime) {
        return this.formatDateTime(this.classDetail.startTime)
      } else {
        return '未设置'
      }
    }
  },
  
  onLoad(options) {
    if (options.id) {
      this.classId = options.id
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
        this.getClassDetail()
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
    
    // 获取党课详情
    async getClassDetail() {
      try {
        this.loading = true
        const response = await getClass(this.classId)
        this.classDetail = response.data
        
        // 获取参课人员
        await this.getParticipants()
      } catch (error) {
        console.error('获取党课详情失败:', error)
        this.$modal.msgError('获取党课详情失败')
      } finally {
        this.loading = false
      }
    },
    
    // 获取参课人员
    async getParticipants() {
      try {
        const response = await listClassparticipant({ classId: this.classId })
        this.participants = response.rows || []
      } catch (error) {
        console.error('获取参课人员失败:', error)
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
          this.$modal.msgError('您不在参课人员名单中')
          return
        }
        
        // 更新签到状态
        await updateClassparticipant({
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
        url: `/pages/class/participants?id=${this.classId}`
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
    
    // 获取党课部门名称
    getClassDeptName(branchId) {
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
    
    // 发送通知
    handleSendNotification() {
      this.notificationForm.title = `党课通知：${this.classDetail.title}`
      this.notificationForm.content = `您好，${this.classDetail.title} 即将开始，请准时参加。\n\n时间：${this.activityTimeDisplay}\n地点：${this.classDetail.location || '待定'}\n讲师：${this.classDetail.lecturer || '待定'}`
      this.$refs.notificationPopup.open()
    },
    
    // 发送签到
    handleSendSignIn() {
      // 设置默认截止时间为党课开始时间
      if (this.classDetail.startTime) {
        this.signInForm.deadline = this.classDetail.startTime
      }
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
        
        // 构建通知数据
        const noticeData = {
          noticeTitle: this.notificationForm.title,
          noticeContent: this.notificationForm.content,
          noticeType: '1', // 通知类型
          status: '0' // 正常状态
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
        
        // 这里可以调用签到相关的API
        // 暂时只显示成功消息
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
  min-height: 100vh;
  background: #f8f9fa;
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

// 党课头部卡片样式
.class-header {
  margin-bottom: 10px;
  
  .class-title {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 12px;
    line-height: 1.4;
  }
  
  .class-tags {
    display: flex;
    align-items: center;
    gap: 8px;
  }
}

// 党课信息卡片样式
.class-info {
  margin-bottom: 10px;
  
  .info-row {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    padding: 12px 0;
    border-bottom: 1px solid #f0f0f0;
    
    &:last-child {
      border-bottom: none;
    }
    
    .info-label {
      display: flex;
      align-items: center;
      gap: 6px;
      min-width: 80px;
      
      text {
        font-size: 13px;
        color: #606266;
      }
    }
    
    .info-value {
      flex: 1;
      text-align: right;
      font-size: 13px;
      color: #303133;
      word-break: break-all;
    }
  }
}

// 党课内容样式
.class-content {
  font-size: 14px;
  line-height: 1.6;
  color: #606266;
}

// 附件列表样式
.attachment-list {
  .attachment-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 12px 0;
    border-bottom: 1px solid #f0f0f0;
    transition: background-color 0.2s ease;
    
    &:last-child {
      border-bottom: none;
    }
    
    &:active {
      background-color: #f5f5f5;
    }
    
    .attachment-info {
      display: flex;
      align-items: center;
      flex: 1;
      
      .attachment-icon {
        margin-right: 10px;
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

// 参课人员工具栏样式
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
  }
  50% {
    transform: scale(1.05);
  }
  100% {
    transform: scale(1);
  }
}

// 底部操作按钮
.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12px 20px;
  background: #ffffff;
  border-top: 1px solid #e4e7ed;
  z-index: 998;
  
  .sign-in-btn {
    width: 100%;
    height: 44px;
    background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
    color: #ffffff;
    border: none;
    border-radius: 22px;
    font-size: 16px;
    font-weight: 500;
    
    &:disabled {
      background: #c0c4cc;
      color: #ffffff;
    }
  }
}

// 弹窗样式
.popup-container-new {
  background: #ffffff;
  border-radius: 12px 12px 0 0;
  max-height: 80vh;
  overflow: hidden;
}

.popup-header-new {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 8px;
    
    .popup-title-new {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
    }
  }
  
  .close-btn {
    padding: 4px;
  }
}

.popup-content {
  padding: 20px;
  max-height: 60vh;
  overflow-y: auto;
}

.form-section {
  margin-bottom: 20px;
}

.info-section {
  .info-item {
    margin-bottom: 16px;
    
    .info-label {
      display: flex;
      align-items: center;
      gap: 6px;
      margin-bottom: 8px;
      
      text {
        font-size: 13px;
        color: #606266;
      }
    }
    
    .participants-tags-new {
      display: flex;
      flex-wrap: wrap;
      gap: 6px;
    }
    
    .time-display-new {
      font-size: 13px;
      color: #303133;
    }
  }
}

.popup-actions-new {
  display: flex;
  padding: 16px 20px;
  border-top: 1px solid #f0f0f0;
  gap: 12px;
  
  .action-btn-new {
    flex: 1;
    height: 40px;
    border-radius: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s ease;
    
    &.cancel {
      background: #f5f5f5;
      
      .btn-text-new {
        color: #606266;
      }
    }
    
    &.primary {
      background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
      
      .btn-text-new {
        color: #ffffff;
      }
    }
    
    &:active {
      transform: scale(0.95);
    }
    
    .btn-text-new {
      font-size: 14px;
      font-weight: 500;
    }
  }
}
</style>