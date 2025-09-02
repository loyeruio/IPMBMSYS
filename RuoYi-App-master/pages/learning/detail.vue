<template>
  <view class="detail-container">
    <!-- 加载状态 -->
    <view v-if="loading" class="loading-container">
      <uni-load-more status="loading"></uni-load-more>
    </view>
    
    <!-- 详情内容 -->
    <view v-else class="detail-content">
      <!-- 半月学基本信息卡片 -->
      <uni-card class="learning-header" :is-shadow="false" spacing="0">
        <view class="learning-title">{{ learningDetail.title }}</view>
        <!-- 半月学基本信息卡片中的标签部分 -->
        <view class="learning-tags">
          <!-- 使用字典标签组件 -->
          <DictTag 
            v-if="dict && dict.type && dict.type.learning_type && Array.isArray(dict.type.learning_type)"
            :options="dict.type.learning_type"
            :value="learningDetail.form"
          />
          
          <DictTag 
            v-if="dict && dict.type && dict.type.meetings_status && Array.isArray(dict.type.meetings_status)"
            :options="dict.type.meetings_status"
            :value="learningDetail.status"
          />
        </view>
      </uni-card>
      
      <!-- 半月学详细信息卡片 -->
      <uni-card class="learning-info" title="活动信息" :is-shadow="false">
        <view class="info-row">
          <view class="info-label">
            <uni-icons type="calendar" size="16" color="#909399"></uni-icons>
            <text>开始时间</text>
          </view>
          <view class="info-value">{{ formatDateTime(learningDetail.startTime) }}</view>
        </view>
        
        <view class="info-row">
          <view class="info-label">
            <uni-icons type="calendar" size="16" color="#909399"></uni-icons>
            <text>结束时间</text>
          </view>
          <view class="info-value">{{ formatDateTime(learningDetail.endTime) }}</view>
        </view>
        
        <view class="info-row">
          <view class="info-label">
            <uni-icons type="location" size="16" color="#909399"></uni-icons>
            <text>活动地点</text>
          </view>
          <view class="info-value">{{ learningDetail.location || '未设置' }}</view>
        </view>
        
        <view class="info-row">
          <view class="info-label">
            <uni-icons type="person" size="16" color="#909399"></uni-icons>
            <text>活动负责人</text>
          </view>
          <view class="info-value">{{ getOrganizerName(learningDetail.organizerId) }}</view>
        </view>
        
        <view class="info-row">
            <view class="info-label">
                <uni-icons type="circle" size="16" color="#909399"></uni-icons>
                <text>学习时长</text>
            </view>
            <view class="info-value">{{ learningDetail.duration || '未设置' }}</view>
        </view>
        
        <!-- 半月学详细信息卡片中的部门显示 -->
        <view class="info-row">
          <view class="info-label">
            <uni-icons type="home" size="16" color="#909399"></uni-icons>
            <text>所属部门</text>
          </view>
          <view class="info-value">{{ getLearningDeptName(learningDetail.branchId) }}</view>
        </view>
      </uni-card>
      
      <!-- 活动内容卡片 -->
      <uni-card v-if="learningDetail.description" title="活动内容" :is-shadow="false">
        <view class="learning-content">
          <rich-text :nodes="learningDetail.description"></rich-text>
        </view>
      </uni-card>
      
      <!-- 附件信息卡片 -->
      <uni-card v-if="attachmentList && attachmentList.length > 0" title="相关附件" :is-shadow="false">
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
      
      <!-- 参加人员卡片 -->
      <uni-card title="参加人员" :is-shadow="false">
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
            <view class="participant-content">
              <!-- 基本信息区域 -->
              <view class="participant-header">
                <view class="participant-basic">
                  <text class="participant-name">{{ participant.studentName }}</text>
                  <text class="participant-dept">{{ participant.deptName }}</text>
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
              
              <!-- 积分信息区域 -->
              <view class="participant-scores">
                <view class="scores-grid">
                  <view class="score-item">
                    <text class="score-label">总积分</text>
                    <text class="score-value primary">{{ participant.totalScore || 0 }}</text>
                  </view>
                  <view class="score-item">
                    <text class="score-label">表现积分</text>
                    <text class="score-value" :class="participant.performanceScore > 0 ? 'positive' : participant.performanceScore < 0 ? 'negative' : ''">
                      {{ participant.performanceScore > 0 ? '+' : '' }}{{ participant.performanceScore || 0 }}
                    </text>
                  </view>
                </view>
                <view class="xuexiqiangguo-score">
                  <text class="score-label">学习强国</text>
                  <view class="score-progress">
                    <text class="score-prev">{{ participant.xuexiqiangguoPrev || 0 }}</text>
                    <uni-icons type="arrowright" size="12" color="#c0c4cc"></uni-icons>
                    <text class="score-curr">{{ participant.xuexiqiangguoCurr || 0 }}</text>
                    <text class="score-diff" :class="participant.xuexiqiangguoDiff > 0 ? 'positive' : participant.xuexiqiangguoDiff < 0 ? 'negative' : ''">
                      ({{ participant.xuexiqiangguoDiff > 0 ? '+' : '' }}{{ participant.xuexiqiangguoDiff || 0 }})
                    </text>
                  </view>
                </view>
              </view>
              
              <!-- 学习截图和备注区域 -->
              <view class="participant-extra" v-if="participant.xuexiqiangguoImage || participant.remark">
                <view v-if="participant.xuexiqiangguoImage" class="learning-screenshot">
                  <text class="screenshot-label">学习截图</text>
                  <view class="screenshot-container" @click="previewImage(participant.xuexiqiangguoImage)">
                    <image 
                      :src="getFullImageUrl(participant.xuexiqiangguoImage)" 
                      mode="aspectFill"
                      class="screenshot-image"
                    ></image>
                    <view class="screenshot-mask">
                      <uni-icons type="eye" size="16" color="#fff"></uni-icons>
                    </view>
                  </view>
                </view>
                <view v-if="participant.remark" class="participant-remark">
                  <text class="remark-label">备注</text>
                  <text class="remark-content">{{ participant.remark }}</text>
                </view>
              </view>
            </view>
          </view>
        </view>
        
        <view v-else class="empty-participants">
          <uni-icons type="person" size="40" color="#d3d4d6"></uni-icons>
          <text class="empty-text">暂无参加人员</text>
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
        <uni-forms ref="notificationForm" :model="notificationForm" label-width="80px">
            <view class="form-section">
            <uni-forms-item label="标题" name="title" required>
                <uni-easyinput 
                v-model="notificationForm.title" 
                placeholder="请输入通知标题"
                :maxlength="50"
                ></uni-easyinput>
            </uni-forms-item>
            
            <uni-forms-item label="内容" name="content" required>
                <uni-easyinput 
                v-model="notificationForm.content" 
                type="textarea" 
                placeholder="请输入通知内容"
                :maxlength="500"
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
                    :text="participant.studentName"
                ></uni-tag>
                </view>
            </view>
            
            <view class="info-item">
                <view class="info-label">
                <uni-icons type="calendar" size="14" color="#909399"></uni-icons>
                <text>学习时间</text>
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
        <uni-forms ref="signInForm" :model="signInForm" label-width="80px">
            <view class="form-section">
            <uni-forms-item label="截止时间" name="deadline" required>
                <uni-datetime-picker 
                v-model="signInForm.deadline" 
                type="datetime"
                placeholder="请选择签到截止时间"
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
                    :text="participant.studentName"
                ></uni-tag>
                </view>
            </view>
            
            <view class="info-item">
                <view class="info-label">
                <uni-icons type="calendar" size="14" color="#909399"></uni-icons>
                <text>学习时间</text>
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
import { getLearning } from '@/api/learning/learning'
import { listParticipant, updateParticipant } from '@/api/participant/learningparticipant'
import { listAllUser } from '@/api/system/user'
import { listDept } from '@/api/system/dept'
import { addNotice } from '@/api/system/notice'
import { AttachmentManager } from '@/utils/attachment'
import DictTag from '@/components/DictTag/index.vue'
import config from '@/config'
export default {
  dicts: ['learning_type', 'meetings_status', 'attendance_status', 'check_status'],
  components: {
    DictTag
  },
  data() {
    return {
      learningId: null,
      learningDetail: {},
      participants: [],
      userList: [],
      currentUser: null,
      loading: false,
      signInLoading: false,
      deptList: [],
      attachmentList: [],
      attachmentManager: new AttachmentManager(),
      // 通知表单
      notificationForm: {
        title: '',
        content: ''
      },
      // 签到表单
      signInForm: {
        deadline: ''
      },
      baseUrl: config.baseUrl
    }
  },
  computed: {
    // 是否可以签到
    canSignIn() {
      // 检查当前用户是否在参与人员列表中
      if (!this.currentUser || !this.participants.length) return false
      return this.participants.some(p => p.userId === this.currentUser.userId)
    },
    
    // 是否已签到
    isSignedIn() {
      if (!this.currentUser || !this.participants.length) return false
      const participant = this.participants.find(p => p.userId === this.currentUser.userId)
      return participant && participant.signInStatus === '1'
    },
    
    // 活动时间显示
    activityTimeDisplay() {
      if (!this.learningDetail.startTime || !this.learningDetail.endTime) return ''
      return `${this.formatDateTime(this.learningDetail.startTime)} - ${this.formatDateTime(this.learningDetail.endTime)}`
    }
  },
  onLoad(options) {
    if (options.id) {
      this.learningId = options.id
      this.loadData()
    }
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        await Promise.all([
          this.getUserList(),
          this.getDeptList(),
          this.getLearningDetail(),
          this.getParticipants()
        ])
      } catch (error) {
        console.error('加载数据失败:', error)
        this.$modal.msgError('加载数据失败')
      } finally {
        this.loading = false
      }
    },
    
    // 获取用户列表
    async getUserList() {
      try {
        const response = await listAllUser({})
        this.userList = response.data || []
        // 获取当前用户信息
        this.currentUser = uni.getStorageSync('userInfo')
      } catch (error) {
        console.error('获取用户列表失败:', error)
      }
    },
    
    // 获取部门列表
    async getDeptList() {
      try {
        const response = await listDept({})
        this.deptList = response.data || []
      } catch (error) {
        console.error('获取部门列表失败:', error)
      }
    },
    
    // 获取半月学详情
    async getLearningDetail() {
      try {
        const response = await getLearning(this.learningId)
        this.learningDetail = response.data || {}
        
        // 处理附件数据
        if (this.learningDetail.attachments) {
          try {
            this.attachmentList = this.attachmentManager.parseAttachments(this.learningDetail.attachments)
          } catch (error) {
            console.error('解析附件失败:', error)
            this.attachmentList = []
          }
        } else {
          this.attachmentList = []
        }
      } catch (error) {
        console.error('获取半月学详情失败:', error)
        // 确保在错误情况下也初始化 attachmentList
        this.attachmentList = []
        throw error
      }
    },
    
    // 获取参与人员列表
    async getParticipants() {
      try {
        const response = await listParticipant({ learningId: this.learningId })
        this.participants = response.rows || []
        
        // 映射用户名和部门名
        this.participants.forEach(participant => {
          // 修复：使用 studentId 而不是 userId
          const user = this.userList.find(u => u.userId === participant.studentId)
          if (user) {
            participant.studentName = user.userName
            const dept = this.deptList.find(d => d.deptId === user.deptId)
            participant.deptName = dept ? dept.deptName : '未知部门'
          } else {
            participant.studentName = '未知用户'
            participant.deptName = '未知部门'
          }
          // 计算学习强国积分差值
          participant.xuexiqiangguoDiff = (participant.xuexiqiangguoCurr || 0) - (participant.xuexiqiangguoPrev || 0);
        })
      } catch (error) {
        console.error('获取参与人员失败:', error)
      }
    },
    
    // 获取负责人姓名
    getOrganizerName(organizerId) {
      if (!organizerId) return '未设置负责人'
      const user = this.userList.find(u => u.userId === organizerId)
      return user ? user.userName : organizerId
    },
    
    // 获取部门名称
    getLearningDeptName(branchId) {
      if (!branchId) return '未设置部门'
      const dept = this.deptList.find(d => d.deptId === branchId)
      return dept ? dept.deptName : branchId
    },
    
    // 获取签到统计
    getSignInCount(status) {
      return this.participants.filter(p => p.signInStatus === status).length
    },
    
    // 编辑半月学
    handleEdit() {
      uni.navigateTo({
        url: `/pages/learning/participants?id=${this.learningId}`
      })
    },
    
    // 发送通知
    handleSendNotification() {
      if (this.participants.length === 0) {
        this.$modal.msgError('暂无参加人员，无法发布通知')
        return
      }
      
      // 重置表单
      this.notificationForm = {
        title: `关于${this.learningDetail.title}的通知`,
        content: ''
      }
      
      this.$refs.notificationPopup.open()
    },
    
    // 发布签到
    handleSendSignIn() {
      if (this.participants.length === 0) {
        this.$modal.msgError('暂无参加人员，无法发布签到')
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
        
        // 修复：使用 studentId 而不是 userId
        const userIds = this.participants.map(p => p.studentId)
        
        const noticeData = {
          noticeTitle: this.notificationForm.title,
          noticeType: '1', // 1-通知
          noticeContent: this.notificationForm.content,
          status: '0', // 0-正常
          noticeUserIds: userIds.join(','),
          recordTime: this.learningDetail.startTime || null,
          attachMent: this.learningDetail.attachments || null,
          activityType: '1', // 1-半月学
          activityId: this.learningId ? this.learningId.toString() : null
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
        
        // 修复：使用 studentId 而不是 userId
        const userIds = this.participants.map(p => p.studentId)
        
        const noticeData = {
          noticeTitle: `${this.learningDetail.title}签到`,
          noticeType: '3', // 3-签到
          noticeContent: `请在${this.formatDateTime(this.signInForm.deadline)}前完成签到`,
          status: '0', // 0-正常
          noticeUserIds: userIds.join(','),
          recordTime: this.signInForm.deadline,
          activityType: '1', // 1-半月学
          activityId: this.learningId ? this.learningId.toString() : null
        }
        
        await addNotice(noticeData)
        this.$modal.msgSuccess('签到发布成功')
        this.closeSignInPopup()
      } catch (error) {
        console.error('发布签到失败:', error)
        this.$modal.msgError('发布签到失败')
      }
    },
    
    // 用户签到
    async handleSignIn() {
      if (!this.currentUser) {
        this.$modal.msgError('请先登录')
        return
      }
      
      this.signInLoading = true
      try {
        const participant = this.participants.find(p => p.userId === this.currentUser.userId)
        if (participant) {
          await updateParticipant({
            ...participant,
            signInStatus: '1'
          })
          
          // 更新本地数据
          participant.signInStatus = '1'
          this.$modal.msgSuccess('签到成功')
        }
      } catch (error) {
        console.error('签到失败:', error)
        this.$modal.msgError('签到失败，请重试')
      } finally {
        this.signInLoading = false
      }
    },
    
    // 下载附件
    downloadAttachment(attachment) {
      this.attachmentManager.downloadAttachment(attachment)
    },
    
    // 格式化日期时间
    formatDateTime(timeStr) {
      if (!timeStr) return ''
      const date = new Date(timeStr)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}`
    },
    // 获取完整图片URL
    getFullImageUrl(imagePath) {
      if (!imagePath) return ''
      if (imagePath.indexOf('http') === 0) {
        return imagePath
      }
      return this.baseUrl + imagePath
    },
    
    // 预览图片
    previewImage(imagePath) {
      if (!imagePath) return
      uni.previewImage({
        urls: [this.getFullImageUrl(imagePath)],
        current: 0
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.detail-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 120rpx;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400rpx;
}

.detail-content {
  padding: 20rpx;
}

.learning-header {
  margin-bottom: 20rpx;
  
  .learning-title {
    font-size: 36rpx;
    font-weight: 600;
    color: #303133;
    margin-bottom: 20rpx;
    line-height: 1.4;
  }
  
  .learning-tags {
    display: flex;
    gap: 12rpx;
    flex-wrap: wrap;
  }
}

.learning-info {
  margin-bottom: 20rpx;
  
  .info-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20rpx 0;
    border-bottom: 1rpx solid #f0f0f0;
    
    &:last-child {
      border-bottom: none;
    }
    
    .info-label {
      display: flex;
      align-items: center;
      gap: 6px;
      font-size: 28rpx;
      color: #606266;
      
      text {
        margin-left: 8rpx;
      }
    }
    
    .info-value {
      font-size: 28rpx;
      color: #303133;
      text-align: right;
      flex: 1;
      margin-left: 40rpx;
    }
  }
}

.learning-content {
  line-height: 1.6;
  color: #606266;
  margin-bottom: 30rpx;
}

// 附件列表样式
.attachment-list {
  .attachment-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 24rpx 0;
    border-bottom: 1rpx solid #f0f0f0;
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
        margin-right: 24rpx;
      }
      
      .attachment-details {
        flex: 1;
        
        .attachment-name {
          display: block;
          font-size: 26rpx;
          color: #303133;
          margin-bottom: 8rpx;
          word-break: break-all;
        }
        
        .attachment-size {
          font-size: 24rpx;
          color: #909399;
        }
      }
    }
    
    .download-icon {
      margin-left: 20rpx;
    }
  }
}

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

// 参与人员列表样式
.participants-list {
  .participant-item {
    background: #fff;
    border-radius: 12px;
    padding: 16px;
    margin-bottom: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    border: 1px solid #f0f0f0;
    
    .participant-content {
      .participant-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 12px;
        
        .participant-basic {
          flex: 1;
          
          .participant-name {
            display: block;
            font-size: 16px;
            font-weight: 600;
            color: #303133;
            margin-bottom: 4px;
            line-height: 1.4;
          }
          
          .participant-dept {
            font-size: 13px;
            color: #909399;
            line-height: 1.3;
          }
        }
        
        .participant-status {
          display: flex;
          align-items: center;
          flex-shrink: 0;
        }
      }
      
      .participant-scores {
        background: #f8fafe;
        border-radius: 8px;
        padding: 12px;
        margin-bottom: 12px;
        border: 1px solid #e8f4ff;
        
        .scores-grid {
          display: flex;
          gap: 16px;
          margin-bottom: 8px;
          
          .score-item {
            flex: 1;
            text-align: center;
            
            .score-label {
              display: block;
              font-size: 11px;
              color: #909399;
              margin-bottom: 4px;
              font-weight: 500;
            }
            
            .score-value {
              display: block;
              font-size: 16px;
              font-weight: 600;
              line-height: 1.2;
              
              &.primary {
                color: #409eff;
              }
              
              &.positive {
                color: #67c23a;
              }
              
              &.negative {
                color: #f56c6c;
              }
            }
          }
        }
        
        .xuexiqiangguo-score {
          border-top: 1px solid #e8f4ff;
          padding-top: 8px;
          
          .score-label {
            font-size: 11px;
            color: #909399;
            margin-bottom: 6px;
            display: block;
            font-weight: 500;
          }
          
          .score-progress {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 6px;
            
            .score-prev, .score-curr {
              font-size: 14px;
              font-weight: 600;
              color: #606266;
            }
            
            .score-curr {
              color: #409eff;
            }
            
            .score-diff {
              font-size: 12px;
              font-weight: 500;
              
              &.positive {
                color: #67c23a;
              }
              
              &.negative {
                color: #f56c6c;
              }
            }
          }
        }
      }
      
      .participant-extra {
        display: flex;
        gap: 12px;
        align-items: flex-start;
        
        .learning-screenshot {
          flex-shrink: 0;
          
          .screenshot-label {
            font-size: 11px;
            color: #909399;
            margin-bottom: 6px;
            display: block;
            font-weight: 500;
          }
        }
        
        .participant-remark {
          flex: 1;
          
          .remark-label {
            font-size: 11px;
            color: #909399;
            margin-bottom: 4px;
            display: block;
            font-weight: 500;
          }
          
          .remark-content {
            font-size: 12px;
            color: #606266;
            line-height: 1.5;
            word-break: break-all;
            background: #f5f7fa;
            padding: 8px;
            border-radius: 4px;
            display: block;
          }
        }
      }
    }
  }
}

.empty-participants {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80rpx 40rpx;
  
  .empty-text {
    font-size: 28rpx;
    color: #909399;
    margin-top: 20rpx;
  }
}

// 统计卡片样式
.stats-container {
  display: flex;
  justify-content: space-around;
  padding: 40rpx 0;
  
  .stat-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    
    .stat-number {
      font-size: 48rpx;
      font-weight: 600;
      color: #409eff;
      margin-bottom: 12rpx;
    }
    
    .stat-label {
      font-size: 24rpx;
      color: #909399;
    }
  }
}

.floating-edit-btn {
  position: fixed;
  right: 40rpx;
  bottom: 200rpx;
  width: 112rpx;
  height: 112rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 24rpx rgba(102, 126, 234, 0.4);
  z-index: 999;
  transition: all 0.3s ease;
  
  &:active {
    transform: scale(0.9);
    box-shadow: 0 4rpx 12rpx rgba(102, 126, 234, 0.6);
  }
}

.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20rpx 40rpx;
  background: #fff;
  border-top: 1rpx solid #f0f0f0;
  z-index: 998;
  
  .sign-in-btn {
    width: 100%;
    height: 88rpx;
    background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
    color: #fff;
    border: none;
    border-radius: 12rpx;
    font-size: 32rpx;
    font-weight: 500;
    
    &[disabled] {
      background: #c0c4cc;
      color: #fff;
    }
  }
}
// 弹窗样式
.popup-container-new {
  background: white;
  border-radius: 20rpx 20rpx 0 0;
  padding: 0;
  max-height: 80vh;
  overflow: hidden;
}

.popup-header-new {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx 40rpx 20rpx;
  border-bottom: 1px solid #f0f0f0;
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 12rpx;
  }
  
  .popup-title-new {
    font-size: 32rpx;
    font-weight: 600;
    color: #303133;
  }
  
  .close-btn {
    padding: 10rpx;
    cursor: pointer;
  }
}

.popup-content {
  padding: 30rpx 40rpx;
  max-height: 60vh;
  overflow-y: auto;
}

.form-section {
  margin-bottom: 30rpx;
}

.info-section {
  .info-item {
    margin-bottom: 20rpx;
    
    .info-label {
      display: flex;
      align-items: center;
      gap: 8rpx;
      font-size: 28rpx;
      color: #606266;
      margin-bottom: 12rpx;
    }
    
    .participants-tags-new {
      display: flex;
      flex-wrap: wrap;
      gap: 12rpx;
    }
    
    .time-display-new {
      font-size: 28rpx;
      color: #303133;
    }
  }
}

.popup-actions-new {
  display: flex;
  padding: 30rpx 40rpx;
  gap: 20rpx;
  border-top: 1px solid #f0f0f0;
  
  .action-btn-new {
    flex: 1;
    height: 80rpx;
    border-radius: 40rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    
    &.cancel {
      background: #f5f7fa;
      
      .btn-text-new {
        color: #606266;
      }
    }
    
    &.primary {
      background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
      
      .btn-text-new {
        color: white;
      }
    }
    
    .btn-text-new {
      font-size: 30rpx;
      font-weight: 500;
    }
  }
}

/* 学习截图样式 */
.screenshot-container {
  position: relative;
  width: 60px;
  height: 60px;
  border-radius: 6px;
  overflow: hidden;
  border: 1px solid #e4e7ed;
  
  .screenshot-image {
    width: 100%;
    height: 100%;
  }
  
  .screenshot-mask {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0;
    transition: opacity 0.3s;
  }
  
  &:active .screenshot-mask {
    opacity: 1;
  }
}
</style>

