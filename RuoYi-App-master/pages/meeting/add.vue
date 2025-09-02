<template>
  <view class="add-container">
    <!-- 表单内容 -->
    <view class="form-container">
      <view class="form-header">
        <text class="form-title">创建会议信息</text>
      </view>
      
      <uni-forms ref="form" :model="formData" :rules="rules" label-width="80px">
        <!-- 会议主题 -->
        <view class="form-section">
          <uni-forms-item label="会议主题" name="title" required>
            <uni-easyinput 
              v-model="formData.title" 
              placeholder="请输入会议主题"
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
          
          <uni-forms-item label="会议类型" name="meetingType" required>
            <uni-data-select
              v-model="formData.meetingType"
              :localdata="meetingTypeOptions"
              placeholder="请选择会议类型"
              :styles="selectStyles"
            ></uni-data-select>
          </uni-forms-item>
          
          <uni-forms-item label="会议地点" name="location">
            <uni-easyinput 
              v-model="formData.location" 
              placeholder="请输入会议地点"
              :clearable="true"
              :styles="inputStyles"
            >
              <template v-slot:left>
                <uni-icons type="location-filled" size="18" color="#5A67D8"></uni-icons>
              </template>
            </uni-easyinput>
          </uni-forms-item>
          
          <uni-forms-item label="负责人" name="organizerId" required>
            <uni-data-select
              v-model="formData.organizerId"
              :localdata="userOptions"
              placeholder="请选择负责人"
              :styles="selectStyles"
            ></uni-data-select>
          </uni-forms-item>

          <!-- 添加支部选择 -->
          <uni-forms-item label="所属支部" name="branchId" required>
            <uni-data-select
              v-model="formData.branchId"
              :localdata="deptOptions"
              placeholder="请选择所属支部"
              :styles="selectStyles"
            ></uni-data-select>
          </uni-forms-item>
        </view>
        
        <!-- 时间安排 -->
        <view class="form-section">
          <view class="section-title">
            <uni-icons type="calendar-filled" size="18" color="#5A67D8"></uni-icons>
            <text>时间安排</text>
          </view>
          <uni-forms-item label="开始时间" name="startTime" required>
            <uni-datetime-picker 
              v-model="formData.startTime" 
              type="datetime"
              placeholder="请选择开始时间"
              :clear-icon="false"
            ></uni-datetime-picker>
          </uni-forms-item>

          <uni-forms-item label="结束时间" name="endTime" required>
            <uni-datetime-picker 
              v-model="formData.endTime" 
              type="datetime"
              placeholder="请选择结束时间"
              :clear-icon="false"
            ></uni-datetime-picker>
          </uni-forms-item>
        </view>
        
        <!-- 会议内容 -->
        <view class="form-section">
          <view class="section-title">
            <uni-icons type="compose" size="18" color="#5A67D8"></uni-icons>
            <text>会议内容</text>
          </view>
          
          <uni-forms-item label="会议内容" name="description">
            <uni-easyinput
              v-model="formData.description"
              type="textarea"
              placeholder="请输入会议内容"
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
          <text>{{ submitting ? '提交中...' : '创建会议' }}</text>
        </button>
      </view>
    </view>
  </view>
</template>

<script>
import { addCommon } from '@/api/meeting/meeting'
import { getDicts } from '@/api/system/dict/data'
import { listAllUser } from '@/api/system/user'
import { listDept } from '@/api/system/dept' // 添加部门API导入

export default {
  data() {
    return {
      submitting: false,
      meetingTypeOptions: [],
      userOptions: [],
      deptOptions: [], // 添加部门选项
      formData: {
        title: '',
        meetingType: '',
        location: '',
        startTime: '',
        endTime: '',
        description: '',
        organizerId: '',
        branchId: null, // 支部ID字段
        status: '0' // 添加状态字段，默认为待开始
      },
      rules: {
        title: { rules: [{ required: true, errorMessage: '请输入会议主题' }] },
        meetingType: { rules: [{ required: true, errorMessage: '请选择会议类型' }] },
        organizerId: { rules: [{ required: true, errorMessage: '请选择负责人' }] },
        branchId: { rules: [{ required: true, errorMessage: '请选择所属支部' }] }, // 添加支部验证规则
        startTime: { rules: [{ required: true, errorMessage: '请选择开始时间' }] },
        endTime: { rules: [{ required: true, errorMessage: '请选择结束时间' }] }
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
      textareaStyles: {
        borderRadius: '8px',
        backgroundColor: '#F9FAFB',
        padding: '12px',
        minHeight: '150px'
      }
    }
  },
  
  onLoad() {
    this.loadInitData()
  },
  
  methods: {
    async loadInitData() {
      try {
        await Promise.all([
          this.loadDictData(),
          this.loadUserData(),
          this.loadDeptData() // 添加部门数据加载
        ])
      } catch (error) {
        console.error('加载初始数据失败:', error)
        this.$modal.msgError('加载数据失败，请重试')
      }
    },
    
    async loadDictData() {
      try {
        const response = await getDicts('meeting_type')
        this.meetingTypeOptions = response.data.map(item => ({
          value: item.dictValue,
          text: item.dictLabel
        }))
      } catch (error) {
        console.error('加载会议类型失败:', error)
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
    
    // 添加加载部门数据的方法
    async loadDeptData() {
      try {
        const response = await listDept({})
        this.deptOptions = (response.data || []).map(dept => ({
          value: dept.deptId,
          text: dept.deptName
        }))
      } catch (error) {
        console.error('加载部门列表失败:', error)
        throw error
      }
    },
    
    async submitForm() {
      if (this.submitting) return
      try {
        const valid = await this.$refs.form.validate().catch(() => false)
        if (!valid) {
          this.$modal.msgError('请完善表单信息')
          return
        }
        if (new Date(this.formData.endTime) <= new Date(this.formData.startTime)) {
          this.$modal.msgError('结束时间必须晚于开始时间')
          return
        }
        
        this.submitting = true
        
        // 数据预处理
        const submitData = {
          ...this.formData,
          organizerId: parseInt(this.formData.organizerId),
          branchId: parseInt(this.formData.branchId), // 确保branchId为数字类型
          status: '0'
        }
        
        await addCommon(submitData)
        this.$modal.msgSuccess('会议创建成功')
        setTimeout(() => {
          this.goBack()
        }, 1500)
      } catch (error) {
        console.error('创建会议失败:', error)
        if (error.response && error.response.data) {
          this.$modal.msgError(error.response.data.msg || '创建失败，请重试')
        } else {
          this.$modal.msgError('创建失败，请重试')
        }
      } finally {
        this.submitting = false
      }
    },
    
    handleCancel() {
      uni.showModal({
        title: '提示',
        content: '确定要取消创建吗？未保存的内容将丢失',
        success: (res) => {
          if (res.confirm) {
            this.goBack()
          }
        }
      })
    },
    
    handleSubmit() {
      if (this.submitting) {
        this.$modal.showToast('正在提交中，请稍候...')
        return
      }
      this.submitForm()
    },
    
    goBack() {
      uni.navigateBack({ delta: 1 })
    }
  }
}
</script>

<style scoped>
/* 整体容器样式 */
.add-container {
  background-color: #f5f5f5;
  min-height: 100vh;
  padding: 20rpx;
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

/* 加载状态动画 */
@keyframes loading {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.submit-btn[disabled] uni-icons {
  animation: loading 1s linear infinite;
}
</style>