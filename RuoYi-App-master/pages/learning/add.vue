<template>
  <view class="add-container">
    <!-- 表单内容 -->
    <view class="form-container">
      <view class="form-header">
        <text class="form-title">创建半月学活动</text>
      </view>
      
      <uni-forms ref="form" :model="formData" :rules="rules" label-width="80px">
        <!-- 活动主题 -->
        <view class="form-section">
          <uni-forms-item label="活动主题" name="title" required>
            <uni-easyinput 
              v-model="formData.title" 
              placeholder="请输入半月学主题"
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
          
          <uni-forms-item label="活动形式" name="form" required>
            <uni-data-select
              v-model="formData.form"
              :localdata="learningTypeOptions"
              placeholder="请选择活动形式"
              :styles="selectStyles"
            ></uni-data-select>
          </uni-forms-item>
          
          <uni-forms-item label="活动地点" name="location">
            <uni-easyinput 
              v-model="formData.location" 
              placeholder="请输入活动地点"
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
          
          <uni-forms-item label="学习时长" name="duration">
            <uni-easyinput 
              v-model="formData.duration" 
              placeholder="请输入学习时长（如：2小时）"
              :clearable="true"
              :styles="inputStyles"
            >
              <template v-slot:left>
                <uni-icons type="clock-filled" size="18" color="#5A67D8"></uni-icons>
              </template>
            </uni-easyinput>
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
        
        <!-- 活动内容 -->
        <view class="form-section">
          <view class="section-title">
            <uni-icons type="compose" size="18" color="#5A67D8"></uni-icons>
            <text>活动内容</text>
          </view>
          
          <uni-forms-item label="活动内容" name="description">
            <uni-easyinput
              v-model="formData.description"
              type="textarea"
              placeholder="请输入活动内容"
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
          <text>{{ submitting ? '提交中...' : '创建活动' }}</text>
        </button>
      </view>
    </view>
  </view>
</template>

<script>
import { addLearning } from '@/api/learning/learning'
import { getDicts } from '@/api/system/dict/data'
import { listAllUser } from '@/api/system/user'
import { listDept } from '@/api/system/dept'

export default {
  data() {
    return {
      submitting: false,
      learningTypeOptions: [],
      userOptions: [],
      deptOptions: [],
      formData: {
        title: '',
        form: '',
        location: '',
        startTime: '',
        endTime: '',
        description: '',
        organizerId: '',
        branchId: null,
        duration: '',
        status: '0' // 默认为待开始
      },
      rules: {
        title: { rules: [{ required: true, errorMessage: '请输入活动主题' }] },
        form: { rules: [{ required: true, errorMessage: '请选择活动形式' }] },
        organizerId: { rules: [{ required: true, errorMessage: '请选择负责人' }] },
        branchId: { rules: [{ required: true, errorMessage: '请选择所属支部' }] },
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
          this.loadDeptData()
        ])
      } catch (error) {
        console.error('加载初始数据失败:', error)
        uni.showToast({
          title: '数据加载失败',
          icon: 'error'
        })
      }
    },
    
    async loadDictData() {
      try {
        const response = await getDicts('learning_type')
        if (response.code === 200) {
          this.learningTypeOptions = response.data.map(item => ({
            value: item.dictValue,
            text: item.dictLabel
          }))
        }
      } catch (error) {
        console.error('加载字典数据失败:', error)
      }
    },
    
    async loadUserData() {
      try {
        const response = await listAllUser()
        if (response.code === 200) {
          this.userOptions = response.data.map(user => ({
            value: user.userId,
            text: user.userName
          }))
        }
      } catch (error) {
        console.error('加载用户数据失败:', error)
      }
    },
    
    async loadDeptData() {
      try {
        const response = await listDept()
        if (response.code === 200) {
          this.deptOptions = response.data.map(dept => ({
            value: dept.deptId,
            text: dept.deptName
          }))
        }
      } catch (error) {
        console.error('加载部门数据失败:', error)
      }
    },
    
    handleCancel() {
      uni.navigateBack()
    },
    
    async handleSubmit() {
      try {
        // 表单验证
        const valid = await this.$refs.form.validate()
        if (!valid) {
          return
        }
        
        // 时间验证
        if (new Date(this.formData.startTime) >= new Date(this.formData.endTime)) {
          uni.showToast({
            title: '结束时间必须晚于开始时间',
            icon: 'error'
          })
          return
        }
        
        this.submitting = true
        
        const response = await addLearning(this.formData)
        
        if (response.code === 200) {
          uni.showToast({
            title: '创建成功',
            icon: 'success'
          })
          
          setTimeout(() => {
            uni.navigateBack()
          }, 1500)
        } else {
          throw new Error(response.msg || '创建失败')
        }
      } catch (error) {
        console.error('提交失败:', error)
        uni.showToast({
          title: error.message || '创建失败',
          icon: 'error'
        })
      } finally {
        this.submitting = false
      }
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