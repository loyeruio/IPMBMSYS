<template>
  <view class="edit-container">
    <!-- 加载状态 -->
    <view v-if="loading" class="loading-container">
      <uni-load-more status="loading"></uni-load-more>
      <text class="loading-text">正在加载通知信息...</text>
    </view>
    
    <!-- 表单内容 -->
    <view v-else class="form-container">
      <view class="form-header">
        <text class="form-title">修改通知公告</text>
      </view>
      
      <uni-forms ref="form" :model="formData" :rules="rules" label-width="80px">
        <!-- 公告标题 -->
        <view class="form-section">
          <uni-forms-item label="公告标题" name="noticeTitle" required>
            <uni-easyinput 
              v-model="formData.noticeTitle" 
              placeholder="请输入公告标题"
              :clearable="true"
              :styles="inputStyles"
            ></uni-easyinput>
          </uni-forms-item>
        </view>
        
        <!-- 基本信息 -->
        <view class="form-section">
          <view class="section-title">
            <uni-icons type="gear-filled" size="18" color="#5A67D8"></uni-icons>
            <text>基本信息</text>
          </view>
          
          <uni-forms-item label="通知类型" name="noticeType" required>
            <uni-data-select
              v-model="formData.noticeType"
              :localdata="noticeTypeOptions"
              placeholder="请选择通知类型"
              :styles="disabledSelectStyles"
              :disabled="true"
            ></uni-data-select>
          </uni-forms-item>
          
          <uni-forms-item label="活动类别" name="activityType">
            <uni-data-select
              v-model="formData.activityType"
              :localdata="activityTypeOptions"
              placeholder="请选择活动类别"
              :styles="disabledSelectStyles"
              :disabled="true"
            ></uni-data-select>
          </uni-forms-item>
          
          <uni-forms-item label="通知对象" name="noticeUserIds">
            <view class="notice-users-display">
              <view v-if="selectedUserNames.length > 0" class="user-tags">
                <view 
                  v-for="(userName, index) in selectedUserNames" 
                  :key="index" 
                  class="user-tag"
                >
                  <uni-icons type="person-filled" size="14" color="#5A67D8"></uni-icons>
                  <text>{{ userName }}</text>
                </view>
              </view>
              <view v-else class="no-users">
                <uni-icons type="info" size="16" color="#9CA3AF"></uni-icons>
                <text>暂无通知对象</text>
              </view>
            </view>
          </uni-forms-item>
          
          <uni-forms-item label="状态" name="status" required>
            <uni-data-select
              v-model="formData.status"
              :localdata="statusOptions"
              placeholder="请选择状态"
              :styles="selectStyles"
            ></uni-data-select>
          </uni-forms-item>
        </view>
        
        <!-- 时间设置 -->
        <view class="form-section" v-if="formData.noticeType === '1'">
          <view class="section-title">
            <uni-icons type="calendar-filled" size="18" color="#5A67D8"></uni-icons>
            <text>签到时间</text>
          </view>
          
          <uni-forms-item label="签到时间" name="recordTime">
            <uni-datetime-picker 
              v-model="formData.recordTime" 
              type="datetime"
              placeholder="请选择签到时间"
              :clear-icon="false"
              :disabled="true"
            ></uni-datetime-picker>
          </uni-forms-item>
        </view>
        
        <!-- 通知内容 -->
        <view class="form-section">
          <view class="section-title">
            <uni-icons type="compose" size="18" color="#5A67D8"></uni-icons>
            <text>通知内容</text>
          </view>
          
          <uni-forms-item label="通知内容" name="noticeContent">
            <uni-easyinput
              v-model="formData.noticeContent"
              type="textarea"
              placeholder="请输入通知内容"
              :styles="textareaStyles"
              auto-height
            ></uni-easyinput>
          </uni-forms-item>
          
          <uni-forms-item label="备注" name="remark">
            <uni-easyinput
              v-model="formData.remark"
              type="textarea"
              placeholder="请输入备注信息"
              :styles="textareaStyles"
              auto-height
            ></uni-easyinput>
          </uni-forms-item>
        </view>
      </uni-forms>
      
      <!-- 操作按钮 -->
      <view class="form-actions">
        <button 
          class="action-btn cancel-btn"
          @click="handleCancel"
          hover-class="btn-hover"
        >
          <uni-icons type="undo" size="16" color="#6B7280"></uni-icons>
          <text>取消</text>
        </button>
        <button 
          class="action-btn submit-btn"
          @click="handleSubmit"
          :disabled="submitting"
          hover-class="btn-hover"
        >
          <uni-icons v-if="!submitting" type="checkmarkempty" size="16" color="#FFF"></uni-icons>
          <text>{{ submitting ? '提交中...' : '更新通知' }}</text>
        </button>
      </view>
    </view>
  </view>
</template>

<script>
import { getNotice, updateNotice } from '@/api/system/notice'
import { getDicts } from '@/api/system/dict/data'
import { listAllUser } from '@/api/system/user'

export default {
  data() {
    return {
      loading: true,
      submitting: false,
      noticeId: null,
      noticeTypeOptions: [],
      activityTypeOptions: [],
      statusOptions: [],
      userOptions: [],
      formData: {
        noticeId: null,
        noticeTitle: '',
        noticeType: '',
        activityType: '',
        noticeContent: '',
        noticeUserIds: [],
        recordTime: '',
        status: '0',
        remark: ''
      },
      rules: {
        noticeTitle: { rules: [{ required: true, errorMessage: '请输入公告标题' }] },
        noticeType: { rules: [{ required: true, errorMessage: '请选择通知类型' }] },
        status: { rules: [{ required: true, errorMessage: '请选择状态' }] }
      },
      inputStyles: {
        borderRadius: '8px',
        backgroundColor: '#F9FAFB',
        padding: '12px'
      },
      selectStyles: {
        borderRadius: '8px',
        backgroundColor: '#F9FAFB',
        padding: '12px'
      },
      disabledSelectStyles: {
        borderRadius: '8px',
        backgroundColor: '#F3F4F6',
        padding: '12px',
        color: '#9CA3AF'
      },
      textareaStyles: {
        borderRadius: '8px',
        backgroundColor: '#F9FAFB',
        padding: '12px',
        minHeight: '150px'
      }
    }
  },
  
  computed: {
    selectedUserNames() {
      if (!Array.isArray(this.formData.noticeUserIds) || this.formData.noticeUserIds.length === 0) {
        return []
      }
      
      return this.formData.noticeUserIds.map(userId => {
        const user = this.userOptions.find(option => option.value === userId)
        return user ? user.text : `用户${userId}`
      }).filter(Boolean)
    }
  },
  
  onLoad(options) {
    if (options.id) {
      this.noticeId = options.id
      this.loadInitData()
    } else {
      this.$modal.msgError('缺少通知ID参数')
      this.goBack()
    }
  },
  
  methods: {
    async loadInitData() {
      try {
        await Promise.all([
          this.loadDictData(),
          this.loadUserData(),
          this.loadNoticeData()
        ])
      } catch (error) {
        console.error('加载初始数据失败:', error)
        this.$modal.msgError('加载数据失败，请重试')
      } finally {
        this.loading = false
      }
    },
    
    async loadDictData() {
      try {
        const [noticeTypeRes, activityTypeRes, statusRes] = await Promise.all([
          getDicts('sys_notice_type'),
          getDicts('sys_activity_type'),
          getDicts('sys_notice_status')
        ])
        
        this.noticeTypeOptions = noticeTypeRes.data.map(item => ({
          value: item.dictValue,
          text: item.dictLabel
        }))
        
        this.activityTypeOptions = activityTypeRes.data.map(item => ({
          value: item.dictValue,
          text: item.dictLabel
        }))
        
        this.statusOptions = statusRes.data.map(item => ({
          value: item.dictValue,
          text: item.dictLabel
        }))
      } catch (error) {
        console.error('加载字典数据失败:', error)
        throw error
      }
    },
    
    async loadUserData() {
      try {
        const response = await listAllUser({})
        this.userOptions = (response.data || []).map(user => ({
          value: user.userId,
          text: user.userName
        }))
      } catch (error) {
        console.error('加载用户列表失败:', error)
        throw error
      }
    },
    
    async loadNoticeData() {
      try {
        const response = await getNotice(this.noticeId)
        const notice = response.data
        this.formData = {
          noticeId: notice.noticeId,
          noticeTitle: notice.noticeTitle || '',
          noticeType: notice.noticeType || '',
          activityType: notice.activityType || '',
          noticeContent: notice.noticeContent || '',
          noticeUserIds: notice.noticeUserIds ? notice.noticeUserIds.split(',').map(id => parseInt(id)) : [],
          recordTime: notice.recordTime || '',
          status: notice.status || '0',
          remark: notice.remark || ''
        }
      } catch (error) {
        console.error('加载通知数据失败:', error)
        this.$modal.msgError('加载通知数据失败')
        throw error
      }
    },
    
    async handleSubmit() {
      if (this.submitting) return
      
      try {
        const valid = await this.$refs.form.validate().catch(() => false)
        if (!valid) {
          this.$modal.msgError('请完善表单信息')
          return
        }
        
        this.submitting = true
        
        // 处理通知对象数据
        const submitData = {
          ...this.formData,
          noticeUserIds: Array.isArray(this.formData.noticeUserIds) 
            ? this.formData.noticeUserIds.join(',') 
            : this.formData.noticeUserIds
        }
        
        await updateNotice(submitData)
        this.$modal.msgSuccess('更新通知成功')
        this.goBack()
      } catch (error) {
        console.error('更新通知失败:', error)
        this.$modal.msgError('更新通知失败，请重试')
      } finally {
        this.submitting = false
      }
    },
    
    handleCancel() {
      this.goBack()
    },
    
    goBack() {
      uni.navigateBack({
        delta: 1
      })
    }
  }
}
</script>

<style scoped>
/* 整体容器样式 */
.edit-container {
  background-color: #f5f5f5;
  min-height: 100vh;
  padding: 20rpx;
}

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
}

.loading-text {
  margin-top: 20rpx;
  font-size: 28rpx;
  color: #6B7280;
}

/* 表单容器 */
.form-container {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

/* 表单标题 */
.form-header {
  margin-bottom: 40rpx;
}

.form-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #1F2937;
}

/* 表单分组 */
.form-section {
  margin-bottom: 40rpx;
}

.section-title {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
  font-size: 32rpx;
  font-weight: 500;
  color: #1F2937;
}

.section-title text {
  margin-left: 10rpx;
}

/* 输入框和选择框样式 */
.form-section .uni-easyinput__content,
.form-section .uni-data-select__input {
  border: 1px solid #E5E7EB;
  transition: border-color 0.3s ease;
}

.form-section .uni-easyinput__content:hover,
.form-section .uni-data-select__input:hover {
  border-color: #5A67D8;
}

/* 时间选择器样式 */
.form-section .uni-datetime-picker {
  border: 1px solid #E5E7EB;
  border-radius: 8rpx;
  background-color: #F9FAFB;
  padding: 12rpx;
}

/* 文本区域样式 */
.form-section .uni-textarea {
  border: 1px solid #E5E7EB;
  border-radius: 8rpx;
  background-color: #F9FAFB;
  padding: 12rpx;
  min-height: 150px;
}

/* 操作按钮 - 优化样式 */
.form-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 40rpx;
  gap: 20rpx;
  padding: 0 10rpx;
}

.action-btn {
  flex: 1;
  height: 88rpx;
  border-radius: 16rpx;
  font-size: 30rpx;
  font-weight: 500;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.cancel-btn {
  background: linear-gradient(135deg, #F8FAFC 0%, #E2E8F0 100%);
  color: #64748B;
  border: 2rpx solid #E2E8F0;
}

.submit-btn {
  background: linear-gradient(135deg, #667EEA 0%, #764BA2 100%);
  color: #FFFFFF;
  position: relative;
}

.submit-btn:disabled {
  background: linear-gradient(135deg, #CBD5E0 0%, #A0AEC0 100%);
  color: #718096;
}

/* 按钮悬停效果 */
.btn-hover {
  transform: translateY(-2rpx);
  box-shadow: 0 6rpx 20rpx rgba(0, 0, 0, 0.15);
}

.cancel-btn.btn-hover {
  background: linear-gradient(135deg, #E2E8F0 0%, #CBD5E0 100%);
  border-color: #CBD5E0;
}

.submit-btn.btn-hover {
  background: linear-gradient(135deg, #5A67D8 0%, #6B46C1 100%);
}

/* 按钮内图标和文字布局 */
.action-btn uni-icons {
  margin-right: 8rpx;
}

.action-btn text {
  font-weight: 500;
  letter-spacing: 1rpx;
}

/* 响应式调整 */
@media screen and (max-width: 750rpx) {
  .form-container {
    padding: 20rpx;
  }
  
  .form-title {
    font-size: 32rpx;
  }
  
  .form-actions {
    flex-direction: column;
    gap: 24rpx;
  }
  
  .action-btn {
    width: 100%;
    height: 96rpx;
    font-size: 32rpx;
  }
}
/* 通知对象展示样式 */
.notice-users-display {
  min-height: 80rpx;
  padding: 20rpx;
  background-color: #F9FAFB;
  border: 1px solid #E5E7EB;
  border-radius: 8rpx;
}

.user-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}

.user-tag {
  display: flex;
  align-items: center;
  padding: 8rpx 16rpx;
  background-color: #EEF2FF;
  border: 1px solid #C7D2FE;
  border-radius: 20rpx;
  font-size: 26rpx;
  color: #5A67D8;
}

.user-tag uni-icons {
  margin-right: 6rpx;
}

.no-users {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #9CA3AF;
  font-size: 28rpx;
}

.no-users uni-icons {
  margin-right: 8rpx;
}
/* 加载状态动画 */
@keyframes loading {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.submit-btn[disabled] uni-icons {
  animation: loading 1s linear infinite;
}
</style>