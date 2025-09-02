<template>
  <view class="meeting-container">
    <!-- 搜索和筛选区域 -->
    <view class="search-section">
      <view class="search-container">
        <uni-search-bar 
          v-model="searchKeyword" 
          placeholder="搜索会议主题" 
          @confirm="handleSearch"
          @clear="handleClear"
          cancelButton="none"
          radius="20"
          bgColor="#f8f9fa"
        ></uni-search-bar>
      </view>
      
      <!-- 筛选器 -->
      <view class="filter-section">
        <view class="filter-item">
          <uni-data-select
            v-model="queryParams.meetingType"
            :localdata="meetingTypeSelectOptions"
            placeholder="会议类型"
            @change="handleFilter"
          ></uni-data-select>
        </view>
        
        <view class="filter-item">
          <uni-data-select
            v-model="queryParams.status"
            :localdata="statusSelectOptions"
            placeholder="会议状态"
            @change="handleFilter"
            :clear="false"
          ></uni-data-select>
        </view>
      </view>
    </view>

    <!-- 会议列表 -->
    <view class="meeting-list">
      <uni-load-more 
        v-if="loading" 
        status="loading"
      ></uni-load-more>
      
      <view 
        v-for="(item, index) in meetingList" 
        :key="item.id" 
        class="meeting-card"
        @click="goToDetail(item.id)"
      >
        <view class="card-container">
          <!-- 卡片头部 -->
          <view class="card-header">
            <view class="header-left">
              <!-- 会议类型标签 -->
              <DictTag 
                v-if="dict && dict.type && dict.type.meeting_type && Array.isArray(dict.type.meeting_type)"
                :options="dict.type.meeting_type"
                :value="item.meetingType"
              />
              <!-- 会议状态标签 -->
              <DictTag 
                v-if="dict && dict.type && dict.type.meetings_status && Array.isArray(dict.type.meetings_status)"
                :options="dict.type.meetings_status"
                :value="item.status"
              />
            </view>
            <view class="header-right">
              <text class="meeting-time">{{ formatTime(item.startTime) }}</text>
            </view>
          </view>
          
          <!-- 卡片内容 -->
          <view class="card-content">
            <view class="meeting-title">{{ item.title }}</view>
            
            <view class="meeting-info">
              <view class="info-row">
                <view class="info-item">
                  <uni-icons type="location" size="14" color="#909399"></uni-icons>
                  <text class="info-text">{{ item.location || '未设置地点' }}</text>
                </view>
                <view class="info-item">
                  <uni-icons type="person" size="14" color="#909399"></uni-icons>
                  <text class="info-text">{{ getOrganizerName(item.organizerId) }}</text>
                </view>
              </view>
              
              <view class="info-row">
                <view class="info-item full-width">
                  <uni-icons type="calendar" size="14" color="#909399"></uni-icons>
                  <text class="info-text">{{ formatDateTime(item.startTime) }} - {{ formatTime(item.endTime) }}</text>
                </view>
              </view>
            </view>
            
            <view class="meeting-description" v-if="item.description">
              <text class="description-text">{{ stripHtml(item.description) }}</text>
            </view>
          </view>
          
          <!-- 操作按钮 -->
          <view class="card-actions">
            <view class="action-btn edit-btn" @click.stop="handleEdit(item)">
              <uni-icons type="compose" size="14"></uni-icons>
              <text class="btn-text">修改</text>
            </view>
            
            <view class="action-btn delete-btn" @click.stop="handleDelete(item)">
              <uni-icons type="trash" size="14"></uni-icons>
              <text class="btn-text">删除</text>
            </view>
            
            <view class="action-btn detail-btn" @click.stop="goToDetail(item.id)">
              <uni-icons type="eye" size="14"></uni-icons>
              <text class="btn-text">详情</text>
            </view>
          </view>
        </view>
      </view>
      
      <!-- 空状态 -->
      <view v-if="!loading && meetingList.length === 0" class="empty-state">
        <view class="empty-icon">
          <uni-icons type="calendar" size="80" color="#d3d4d6"></uni-icons>
        </view>
        <text class="empty-text">暂无会议数据</text>
        <text class="empty-subtitle">点击右上角添加第一个会议</text>
      </view>
      
      <!-- 加载更多 -->
      <uni-load-more 
        v-if="!loading && hasMore" 
        status="more" 
        @clickLoadMore="loadMore"
      ></uni-load-more>
      
      <uni-load-more 
        v-if="!loading && !hasMore && meetingList.length > 0" 
        status="noMore"
      ></uni-load-more>
    </view>

    <!-- 悬浮新增按钮 -->
    <view class="floating-add-btn" @click="handleAdd">
      <uni-icons type="plus" size="24" color="#fff"></uni-icons>
    </view>

    <!-- 新增/编辑弹窗 -->
    <uni-popup ref="editPopup" type="bottom" :safe-area="false">
      <view class="edit-form">
        <view class="form-header">
          <text class="form-title">{{ isEdit ? '编辑会议' : '新增会议' }}</text>
          <uni-icons type="closeempty" size="20" @click="closeEditForm"></uni-icons>
        </view>
        
        <uni-forms ref="form" :model="formData" :rules="rules">
          <uni-forms-item label="会议主题" name="title" required>
            <uni-easyinput v-model="formData.title" placeholder="请输入会议主题"></uni-easyinput>
          </uni-forms-item>
          
          <uni-forms-item label="会议类型" name="meetingType" required>
            <uni-data-select
              v-model="formData.meetingType"
              :localdata="meetingTypeSelectOptions"
              placeholder="请选择会议类型"
            ></uni-data-select>
          </uni-forms-item>
          
          <uni-forms-item label="会议地点" name="location">
            <uni-easyinput v-model="formData.location" placeholder="请输入会议地点"></uni-easyinput>
          </uni-forms-item>
          
          <uni-forms-item label="开始时间" name="startTime" required>
            <uni-datetime-picker 
              v-model="formData.startTime" 
              type="datetime"
              placeholder="请选择开始时间"
            ></uni-datetime-picker>
          </uni-forms-item>
          
          <uni-forms-item label="结束时间" name="endTime" required>
            <uni-datetime-picker 
              v-model="formData.endTime" 
              type="datetime"
              placeholder="请选择结束时间"
            ></uni-datetime-picker>
          </uni-forms-item>
          
          <uni-forms-item label="会议内容" name="description">
            <uni-easyinput 
              v-model="formData.description" 
              type="textarea" 
              placeholder="请输入会议内容"
              :maxlength="500"
            ></uni-easyinput>
          </uni-forms-item>
          
          <uni-forms-item label="负责人" name="organizerId" required>
            <uni-data-select
              v-model="formData.organizerId"
              :localdata="userSelectOptions"
              placeholder="请选择负责人"
            ></uni-data-select>
          </uni-forms-item>
        </uni-forms>
        
        <view class="form-actions">
          <uni-button class="cancel-btn" @click="closeEditForm">取消</uni-button>
          <uni-button class="submit-btn" type="primary" @click="submitForm">{{ isEdit ? '更新' : '创建' }}</uni-button>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script>
import { listCommon, addCommon, updateCommon, delCommon } from '@/api/meeting/meeting'
import { listAllUser } from '@/api/system/user'
import DictTag from '@/components/DictTag/index.vue'

export default {
  dicts: ['meeting_type', 'meetings_status'],
  components: {
    DictTag
  },
  data() {
    return {
      loading: false,
      hasMore: true,
      searchKeyword: '',
      meetingList: [],
      userList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: '',
        meetingType: '',
        status: ''
      },
      total: 0,
      statusBarHeight: 0,
      isEdit: false,
      formData: {
        id: null,
        title: '',
        meetingType: '',
        location: '',
        startTime: '',
        endTime: '',
        description: '',
        organizerId: '' 
      },
      rules: {
        title: {
          rules: [{
            required: true,
            errorMessage: '请输入会议主题'
          }]
        },
        meetingType: {
          rules: [{
            required: true,
            errorMessage: '请选择会议类型'
          }]
        },
        organizerId: {
          rules: [{
            required: true,
            errorMessage: '请选择负责人'
          }]
        },
        startTime: {
          rules: [{
            required: true,
            errorMessage: '请选择开始时间'
          }]
        },
        endTime: {
          rules: [{
            required: true,
            errorMessage: '请选择结束时间'
          }]
        }
      }
    }
  },
  computed: {
    // uni-data-select专用的数据格式
    meetingTypeSelectOptions() {
      if (!this.dict.type || !this.dict.type.meeting_type) {
        return []
      }
      return this.dict.type.meeting_type.map(item => ({
        value: item.value,
        text: item.label
      }))
    },
    statusSelectOptions() {
      if (!this.dict.type || !this.dict.type.meetings_status) {
        return []
      }
      return this.dict.type.meetings_status.map(item => ({
        value: item.value,
        text: item.label
      }))
    },
    userSelectOptions() {
      return this.userList.map(user => ({
        value: user.userId,
        text: user.userName
      }))
    }
  },
  onLoad() {
    // 获取状态栏高度
    this.statusBarHeight = uni.getSystemInfoSync().statusBarHeight
    
    this.getUserList().then(() => {
      this.getList()
    })
  },
  
  onReachBottom() {
    if (this.hasMore && !this.loading) {
      this.loadMore()
    }
  },
  
  onPullDownRefresh() {
    this.queryParams.pageNum = 1
    this.meetingList = []
    this.hasMore = true
    this.getList().then(() => {
      uni.stopPullDownRefresh()
    })
  },
  methods: {
    // 获取用户列表
    async getUserList() {
      try {
        const response = await listAllUser({})
        this.userList = response.data || []
      } catch (error) {
        console.error('获取用户列表失败:', error)
        this.$modal.msgError('获取用户列表失败')
      }
    },
    
    // 获取负责人姓名
    getOrganizerName(organizerId) {
      if (!organizerId) return '未设置负责人'
      const user = this.userList.find(u => u.userId === organizerId)
      return user ? user.userName : organizerId
    },
    
    // 获取会议列表
    async getList() {
      if (this.loading) return
      
      this.loading = true
      try {
        const response = await listCommon(this.queryParams)
        
        let rows = response.rows || []
        // 映射负责人姓名
        rows.forEach(item => {
          const user = this.userList.find(u => u.userId === item.organizerId)
          item.organizerName = user ? user.userName : item.organizerId
        })
        
        if (this.queryParams.pageNum === 1) {
          this.meetingList = rows
        } else {
          this.meetingList = [...this.meetingList, ...rows]
        }
        
        this.total = response.total || 0
        this.hasMore = this.meetingList.length < this.total
        
      } catch (error) {
        console.error('获取会议列表失败:', error)
        this.$modal.msgError('获取会议列表失败，请检查网络连接')
      } finally {
        this.loading = false
      }
    },
    
    // 加载更多
    loadMore() {
      if (!this.hasMore || this.loading) return
      this.queryParams.pageNum++
      this.getList()
    },
    
    // 跳转到详情页
    goToDetail(id) {
      uni.navigateTo({
        url: `/pages/meeting/detail?id=${id}`
      })
    },
    
    // 搜索
    handleSearch() {
      this.queryParams.title = this.searchKeyword
      this.queryParams.pageNum = 1
      this.meetingList = []
      this.hasMore = true
      this.getList()
    },
    
    // 清除搜索
    handleClear() {
      this.searchKeyword = ''
      this.queryParams.title = ''
      this.queryParams.pageNum = 1
      this.meetingList = []
      this.hasMore = true
      this.getList()
    },
    
    // 筛选
    handleFilter() {
      this.queryParams.pageNum = 1
      this.meetingList = []
      this.hasMore = true
      this.getList()
    },
    
    // 新增会议 - 跳转到新增页面
    handleAdd() {
      console.log('点击新增按钮') // 添加调试日志
      uni.navigateTo({
        url: '/pages/meeting/add',
        fail: (err) => {
          console.error('跳转失败:', err)
          this.$modal.msgError('页面跳转失败，请重试')
        }
      })
    },
    
    // 编辑会议 - 跳转到编辑页面
    handleEdit(item) {
      console.log('点击编辑按钮:', item.id) // 添加调试日志
      uni.navigateTo({
        url: `/pages/meeting/edit?id=${item.id}`,
        fail: (err) => {
          console.error('跳转失败:', err)
          this.$modal.msgError('页面跳转失败，请重试')
        }
      })
    },
    
    // 删除会议 - 保持原有逻辑
    handleDelete(item) {
      console.log('点击删除按钮:', item.id) // 添加调试日志
      uni.showModal({
        title: '确认删除',
        content: `确定要删除会议"${item.title}"吗？`,
        success: async (res) => {
          if (res.confirm) {
            try {
              await delCommon(item.id)
              this.$modal.msgSuccess('删除成功')
              // 重新加载列表
              this.queryParams.pageNum = 1
              this.meetingList = []
              this.hasMore = true
              this.getList()
            } catch (error) {
              console.error('删除失败:', error)
              this.$modal.msgError('删除失败，请重试')
            }
          }
        }
      })
    },
    
    // 修复提交表单方法
    async submitForm() {
      try {
        // 表单验证
        const valid = await this.$refs.form.validate().catch(() => false)
        if (!valid) {
          this.$modal.msgError('请完善表单信息')
          return
        }
        
        // 时间验证
        if (new Date(this.formData.endTime) <= new Date(this.formData.startTime)) {
          this.$modal.msgError('结束时间必须晚于开始时间')
          return
        }
        
        // 提交数据
        if (this.isEdit) {
          await updateCommon(this.formData)
          this.$modal.msgSuccess('更新成功')
        } else {
          await addCommon(this.formData)
          this.$modal.msgSuccess('创建成功')
        }
        
        // 关闭表单并刷新列表
        this.closeEditForm()
        this.queryParams.pageNum = 1
        this.meetingList = []
        this.hasMore = true
        this.getList()
        
      } catch (error) {
        console.error('提交失败:', error)
        this.$modal.msgError(this.isEdit ? '更新失败，请重试' : '创建失败，请重试')
      }
    },
    
    // 关闭编辑表单
    closeEditForm() {
      this.$refs.editPopup.close()
      this.resetForm()
    },

    // 格式化日期时间
    formatDateTime(dateTime) {
      if (!dateTime) return ''
      const date = new Date(dateTime)
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      return `${month}-${day} ${hours}:${minutes}`
    },
    
    // 去除HTML标签
    stripHtml(html) {
      if (!html) return ''
      return html.replace(/<[^>]*>/g, '').substring(0, 100) + (html.length > 100 ? '...' : '')
    },

    // 格式化时间（仅显示时分）
    formatTime(dateTime) {
      if (!dateTime) return ''
      const date = new Date(dateTime)
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      return `${hours}:${minutes}`
    }
  }
}
</script>

<style lang="scss" scoped>
.meeting-container {
  background: linear-gradient(180deg, #f8f9fa 0%, #ffffff 100%);
  min-height: 100vh;
  position: relative;
}

.uni-search-bar {
  padding: 0px;
}
// 搜索区域
.search-section {
  background: #fff;
  padding: 15px;
  margin-bottom: 10px;
  border-radius: 5px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  margin: 15px;
  

  
  .filter-section {
    display: flex;
    gap: 12px;
    
    .filter-item {
      flex: 1;
      
      :deep(.uni-data-select) {
        border-radius: 8px;
        border: 1px solid #e4e7ed;
      }
    }
  }
}

// 悬浮新增按钮
.floating-add-btn {
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
  
  // 添加呼吸动画效果
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

// 会议列表
.meeting-list {
  padding: 0 15px 100px; // 增加底部padding为悬浮按钮留出空间
}

.meeting-card {
  margin-bottom: 15px;
  
  .card-container {
    background: #fff;
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    transition: all 0.3s ease;
    
    &:active {
      transform: translateY(-2px);
      box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
    }
  }
}

// 卡片头部
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 15px 0;
  
  .header-left {
    display: flex;
    gap: 8px;
  }
  
  .header-right {
    .meeting-time {
      font-size: 12px;
      color: #909399;
      font-weight: 500;
    }
  }
}

// 徽章样式
.meeting-type-badge, .meeting-status-badge {
  padding: 4px 8px;
  border-radius: 12px;
  
  .type-text, .status-text {
    font-size: 11px;
    font-weight: 500;
  }
}

.type-primary {
  background: linear-gradient(135deg, #409eff, #66b1ff);
  .type-text { color: #fff; }
}

.type-success {
  background: linear-gradient(135deg, #67c23a, #85ce61);
  .type-text { color: #fff; }
}

.type-warning {
  background: linear-gradient(135deg, #e6a23c, #ebb563);
  .type-text { color: #fff; }
}

.type-danger {
  background: linear-gradient(135deg, #f56c6c, #f78989);
  .type-text { color: #fff; }
}

.status-success {
  background: rgba(103, 194, 58, 0.1);
  .status-text { color: #67c23a; }
}

.status-warning {
  background: rgba(230, 162, 60, 0.1);
  .status-text { color: #e6a23c; }
}

.status-default {
  background: rgba(144, 147, 153, 0.1);
  .status-text { color: #909399; }
}

// 卡片内容
.card-content {
  padding: 15px;
  
  .meeting-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 12px;
    line-height: 1.4;
  }
  
  .meeting-info {
    .info-row {
      display: flex;
      gap: 15px;
      margin-bottom: 8px;
      
      &:last-child {
        margin-bottom: 0;
      }
    }
    
    .info-item {
      display: flex;
      align-items: center;
      flex: 1;
      
      &.full-width {
        flex: none;
        width: 100%;
      }
      
      .info-text {
        margin-left: 6px;
        font-size: 13px;
        color: #606266;
        flex: 1;
      }
    }
  }
  
  .meeting-description {
    margin-top: 12px;
    padding: 10px;
    background: #f8f9fa;
    border-radius: 8px;
    border-left: 3px solid #409eff;
    
    .description-text {
      font-size: 12px;
      color: #909399;
      line-height: 1.5;
    }
  }
}

// 操作按钮
.card-actions {
  display: flex;
  border-top: 1px solid #f5f7fa;
  
  .action-btn {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    height: 44px;
    font-size: 13px;
    transition: all 0.2s ease;
    
    .btn-text {
      margin-left: 4px;
      font-weight: 500;
    }
    
    &:not(:last-child) {
      border-right: 1px solid #f5f7fa;
    }
    
    &.edit-btn {
      color: #409eff;
      
      &:active {
        background: rgba(64, 158, 255, 0.1);
      }
    }
    
    &.delete-btn {
      color: #f56c6c;
      
      &:active {
        background: rgba(245, 108, 108, 0.1);
      }
    }
    
    &.detail-btn {
      color: #909399;
      
      &:active {
        background: rgba(144, 147, 153, 0.1);
      }
    }
  }
}

// 空状态
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  
  .empty-icon {
    margin-bottom: 20px;
    opacity: 0.6;
  }
  
  .empty-text {
    font-size: 16px;
    color: #909399;
    margin-bottom: 8px;
    font-weight: 500;
  }
  
  .empty-subtitle {
    font-size: 14px;
    color: #c0c4cc;
  }
}

// 编辑表单
.edit-form {
  background: #fff;
  border-radius: 20px 20px 0 0;
  max-height: 80vh;
  
  .form-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px;
    border-bottom: 1px solid #f5f7fa;
    
    .form-title {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
    }
  }
  
  .uni-forms {
    padding: 20px;
  }
  
  .form-actions {
    display: flex;
    gap: 15px;
    padding: 20px;
    border-top: 1px solid #f5f7fa;
    
    .cancel-btn, .submit-btn {
      flex: 1;
      height: 44px;
      border-radius: 8px;
      font-weight: 500;
    }
    
    .cancel-btn {
      background: #f5f7fa;
      color: #606266;
      border: none;
    }
  }
}

// 响应式适配
@media (max-width: 375px) {
  .filter-section {
    flex-direction: column;
    gap: 8px;
  }
  
  .info-row {
    flex-direction: column;
    gap: 8px !important;
  }
}
</style>
