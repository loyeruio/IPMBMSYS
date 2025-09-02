<template>
  <view class="edit-container">
    <!-- 加载状态 -->
    <view v-if="loading" class="loading-container">
      <uni-load-more status="loading"></uni-load-more>
      <text class="loading-text">正在加载党课信息...</text>
    </view>
    
    <!-- 表单内容 -->
    <view v-else class="form-container">
      <view class="form-header">
        <text class="form-title">修改党课信息</text>
      </view>
      
      <uni-forms ref="form" :model="formData" :rules="rules" label-width="80px">
        <!-- 党课主题 -->
        <view class="form-section">
          <uni-forms-item label="党课主题" name="title" required>
            <uni-easyinput 
              v-model="formData.title" 
              placeholder="请输入党课主题"
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
          
          <uni-forms-item label="上课地点" name="location">
            <uni-easyinput 
              v-model="formData.location" 
              placeholder="请输入上课地点"
              :clearable="true"
              :styles="inputStyles"
            >
              <template v-slot:left>
                <uni-icons type="location-filled" size="18" color="#5A67D8"></uni-icons>
              </template>
            </uni-easyinput>
          </uni-forms-item>
          
          <uni-forms-item label="授课讲师" name="lecturer">
            <uni-easyinput 
              v-model="formData.lecturer" 
              placeholder="请输入授课讲师"
              :clearable="true"
              :styles="inputStyles"
            >
              <template v-slot:left>
                <uni-icons type="person-filled" size="18" color="#5A67D8"></uni-icons>
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
          <uni-forms-item label="开课时间" name="startTime" required>
            <uni-datetime-picker 
              v-model="formData.startTime" 
              type="datetime"
              placeholder="请选择开课时间"
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
          
          <uni-forms-item label="课时" name="duration">
            <uni-easyinput 
              v-model="formData.duration" 
              placeholder="请输入课时"
              type="number"
              :clearable="true"
              :styles="inputStyles"
            >
              <template v-slot:right>
                <text style="color: #909399; font-size: 14px;">课时</text>
              </template>
            </uni-easyinput>
          </uni-forms-item>
        </view>
        
        <!-- 课程内容 -->
        <view class="form-section">
          <view class="section-title">
            <uni-icons type="compose" size="18" color="#5A67D8"></uni-icons>
            <text>课程内容</text>
          </view>
          
          <uni-forms-item label="课程简介" name="description">
            <uni-easyinput
              v-model="formData.description"
              type="textarea"
              placeholder="请输入课程简介"
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
          <text>{{ submitting ? '提交中...' : '更新党课' }}</text>
        </button>
      </view>
    </view>
  </view>
</template>

<script>
import { getClass, updateClass } from '@/api/class/class'
import { listAllUser } from '@/api/system/user'
import { listDept } from '@/api/system/dept'

export default {
  data() {
    return {
      loading: true,
      submitting: false,
      classId: null,
      userOptions: [],
      deptOptions: [],
      isPicking: false,
      formData: {
        id: null,
        title: '',
        location: '',
        lecturer: '',
        organizerId: '',
        branchId: null,
        description: '',
        startTime: '',
        endTime: '',
        duration: '',
        status: '0'
      },
      rules: {
        title: { rules: [{ required: true, errorMessage: '请输入党课主题' }] },
        organizerId: { rules: [{ required: true, errorMessage: '请选择负责人' }] },
        branchId: { rules: [{ required: true, errorMessage: '请选择所属支部' }] },
        startTime: { rules: [{ required: true, errorMessage: '请选择开课时间' }] },
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
      pickerStyles: {
        borderRadius: '8px',
        backgroundColor: '#F9FAFB',
        padding: '12px',
        transition: 'none'
      },
      textareaStyles: {
        borderRadius: '8px',
        backgroundColor: '#F9FAFB',
        padding: '12px',
        minHeight: '150px'
      }
    }
  },
  
  onLoad(options) {
    if (options.id) {
      this.classId = options.id
      this.loadInitData()
    } else {
      this.$modal.msgError('缺少党课ID参数')
      uni.navigateBack()
    }
  },
  
  methods: {
    async loadInitData() {
      try {
        await Promise.all([
          this.loadUserData(),
          this.loadDeptData(),
          this.loadClassDetail()
        ])
      } catch (error) {
        console.error('初始化数据加载失败:', error)
        this.$modal.msgError('数据加载失败，请重试')
      } finally {
        this.loading = false
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
          this.deptOptions = this.buildDeptTree(response.data)
        }
      } catch (error) {
        console.error('加载部门数据失败:', error)
      }
    },
    
    buildDeptTree(depts) {
      return depts.map(dept => ({
        value: dept.deptId,
        text: dept.deptName
      }))
    },
    
    async loadClassDetail() {
      try {
        const response = await getClass(this.classId)
        if (response.code === 200) {
          const classData = response.data
          this.formData = {
            id: classData.id,
            title: classData.title || '',
            location: classData.location || '',
            lecturer: classData.lecturer || '',
            organizerId: classData.organizerId || '',
            branchId: classData.branchId || null,
            description: classData.description || '',
            startTime: classData.startTime || '',
            endTime: classData.endTime || '',
            duration: classData.duration ? String(classData.duration) : '',
            status: classData.status || '0'
          }
        } else {
          this.$modal.msgError('获取党课详情失败')
          uni.navigateBack()
        }
      } catch (error) {
        console.error('加载党课详情失败:', error)
        this.$modal.msgError('加载党课详情失败，请重试')
        uni.navigateBack()
      }
    },
    
    async handleSubmit() {
      try {
        // 表单验证
        await this.$refs.form.validate()
        
        // 时间验证
        if (this.formData.startTime && this.formData.endTime) {
          const startTime = new Date(this.formData.startTime)
          const endTime = new Date(this.formData.endTime)
          if (startTime >= endTime) {
            this.$modal.msgError('结束时间必须晚于开课时间')
            return
          }
        }
        
        this.submitting = true
        
        // 准备提交数据
        const submitData = {
          ...this.formData,
          duration: this.formData.duration ? Number(this.formData.duration) : null
        }
        
        const response = await updateClass(submitData)
        
        if (response.code === 200) {
          this.$modal.msgSuccess('党课更新成功')
          uni.navigateBack()
        } else {
          this.$modal.msgError(response.msg || '更新失败，请重试')
        }
      } catch (error) {
        console.error('提交失败:', error)
        if (error.errorFields) {
          // 表单验证错误
          const firstError = error.errorFields[0]
          this.$modal.msgError(firstError.errors[0])
        } else {
          this.$modal.msgError('更新失败，请重试')
        }
      } finally {
        this.submitting = false
      }
    },
    
    handleCancel() {
      uni.showModal({
        title: '确认取消',
        content: '确定要取消编辑吗？未保存的更改将丢失。',
        success: (res) => {
          if (res.confirm) {
            uni.navigateBack()
          }
        }
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

/* 加载状态动画 */
@keyframes loading {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.submit-btn[disabled] uni-icons {
  animation: loading 1s linear infinite;
}
</style>