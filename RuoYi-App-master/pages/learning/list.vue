<template>
  <view class="learning-container">
    <!-- 搜索和筛选区域 -->
    <view class="search-section">
      <view class="search-container">
        <uni-search-bar 
          v-model="searchKeyword" 
          placeholder="搜索半月学主题" 
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
            v-model="queryParams.form"
            :localdata="learningTypeSelectOptions"
            placeholder="活动形式"
            @change="handleFilter"
          ></uni-data-select>
        </view>
        
        <view class="filter-item">
          <uni-data-select
            v-model="queryParams.status"
            :localdata="statusSelectOptions"
            placeholder="活动状态"
            @change="handleFilter"
            :clear="false"
          ></uni-data-select>
        </view>
      </view>
    </view>

    <!-- 半月学列表 -->
    <view class="learning-list">
      <uni-load-more 
        v-if="loading" 
        status="loading"
      ></uni-load-more>
      
      <view 
        v-for="(item, index) in learningList" 
        :key="item.id" 
        class="learning-card"
        @click="goToDetail(item.id)"
      >
        <view class="card-container">
          <!-- 卡片头部 -->
          <view class="card-header">
            <view class="header-left">
              <!-- 活动形式标签 -->
              <DictTag 
                v-if="dict && dict.type && dict.type.learning_type && Array.isArray(dict.type.learning_type)"
                :options="dict.type.learning_type"
                :value="item.form"
              />
              <!-- 活动状态标签 -->
              <DictTag 
                v-if="dict && dict.type && dict.type.meetings_status && Array.isArray(dict.type.meetings_status)"
                :options="dict.type.meetings_status"
                :value="item.status"
              />
            </view>
            <view class="header-right">
              <text class="learning-time">{{ formatTime(item.startTime) }}</text>
            </view>
          </view>
          
          <!-- 卡片内容 -->
          <view class="card-content">
            <view class="learning-title">{{ item.title }}</view>
            
            <view class="learning-info">
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
                <view class="info-item">
                  <uni-icons type="circle" size="14" color="#909399"></uni-icons>
                  <text class="info-text">{{ item.duration || '未设置时长' }}</text>
                </view>
                <view class="info-item">
                  <uni-icons type="home" size="14" color="#909399"></uni-icons>
                  <text class="info-text">{{ getDeptName(item.branchId) }}</text>
                </view>
              </view>
              
              <view class="info-row">
                <view class="info-item full-width">
                  <uni-icons type="calendar" size="14" color="#909399"></uni-icons>
                  <text class="info-text">{{ formatDateTime(item.startTime) }} - {{ formatTime(item.endTime) }}</text>
                </view>
              </view>
            </view>
            
            <view class="learning-description" v-if="item.description">
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
      <view v-if="!loading && learningList.length === 0" class="empty-state">
        <view class="empty-icon">
          <uni-icons type="calendar" size="80" color="#d3d4d6"></uni-icons>
        </view>
        <text class="empty-text">暂无半月学数据</text>
        <text class="empty-subtitle">点击右上角添加第一个半月学活动</text>
      </view>
      
      <!-- 加载更多 -->
      <uni-load-more 
        v-if="!loading && hasMore" 
        status="more" 
        @clickLoadMore="loadMore"
      ></uni-load-more>
      
      <uni-load-more 
        v-if="!loading && !hasMore && learningList.length > 0" 
        status="noMore"
      ></uni-load-more>
    </view>

    <!-- 悬浮新增按钮 -->
    <view class="floating-add-btn" @click="handleAdd">
      <uni-icons type="plus" size="24" color="#fff"></uni-icons>
    </view>
  </view>
</template>

<script>
import { listLearning, delLearning } from '@/api/learning/learning'
import { listAllUser } from '@/api/system/user'
import { listDept } from '@/api/system/dept'
import DictTag from '@/components/DictTag/index.vue'

export default {
  dicts: ['learning_type', 'meetings_status'],
  components: {
    DictTag
  },
  data() {
    return {
      loading: false,
      hasMore: true,
      searchKeyword: '',
      learningList: [],
      userList: [],
      deptList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: '',
        form: '',
        status: ''
      },
      total: 0,
      statusBarHeight: 0
    }
  },
  computed: {
    // uni-data-select专用的数据格式
    learningTypeSelectOptions() {
      if (!this.dict.type || !this.dict.type.learning_type) {
        return []
      }
      return this.dict.type.learning_type.map(item => ({
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
    }
  },
  onLoad() {
    // 获取状态栏高度
    this.statusBarHeight = uni.getSystemInfoSync().statusBarHeight
    
    Promise.all([
      this.getUserList(),
      this.getDeptList()
    ]).then(() => {
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
    this.learningList = []
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
    
    // 获取部门列表
    async getDeptList() {
      try {
        const response = await listDept({})
        this.deptList = response.data || []
      } catch (error) {
        console.error('获取部门列表失败:', error)
        this.$modal.msgError('获取部门列表失败')
      }
    },
    
    // 获取负责人姓名
    getOrganizerName(organizerId) {
      if (!organizerId) return '未设置负责人'
      const user = this.userList.find(u => u.userId === organizerId)
      return user ? user.userName : organizerId
    },
    
    // 获取部门名称
    getDeptName(branchId) {
      if (!branchId) return '未设置部门'
      const dept = this.deptList.find(d => d.deptId === branchId)
      return dept ? dept.deptName : branchId
    },
    
    // 获取半月学列表
    async getList() {
      if (this.loading) return
      
      this.loading = true
      try {
        const response = await listLearning(this.queryParams)
        
        let rows = response.rows || []
        // 映射负责人姓名和部门名称
        rows.forEach(item => {
          const user = this.userList.find(u => u.userId === item.organizerId)
          item.organizerName = user ? user.userName : item.organizerId
          
          const dept = this.deptList.find(d => d.deptId === item.branchId)
          item.deptName = dept ? dept.deptName : item.branchId
        })
        
        if (this.queryParams.pageNum === 1) {
          this.learningList = rows
        } else {
          this.learningList = [...this.learningList, ...rows]
        }
        
        this.total = response.total || 0
        this.hasMore = this.learningList.length < this.total
        
      } catch (error) {
        console.error('获取半月学列表失败:', error)
        this.$modal.msgError('获取半月学列表失败，请检查网络连接')
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
        url: `/pages/learning/detail?id=${id}`,
        fail: (err) => {
          console.error('跳转失败:', err)
          this.$modal.msgError('页面跳转失败，请重试')
        }
      })
    },
    
    // 搜索
    handleSearch() {
      this.queryParams.title = this.searchKeyword
      this.queryParams.pageNum = 1
      this.learningList = []
      this.hasMore = true
      this.getList()
    },
    
    // 清除搜索
    handleClear() {
      this.searchKeyword = ''
      this.queryParams.title = ''
      this.queryParams.pageNum = 1
      this.learningList = []
      this.hasMore = true
      this.getList()
    },
    
    // 筛选
    handleFilter() {
      this.queryParams.pageNum = 1
      this.learningList = []
      this.hasMore = true
      this.getList()
    },
    
    // 新增半月学 - 跳转到新增页面
    handleAdd() {
      console.log('点击新增按钮')
      uni.navigateTo({
        url: '/pages/learning/add',
        fail: (err) => {
          console.error('跳转失败:', err)
          this.$modal.msgError('页面跳转失败，请重试')
        }
      })
    },
    
    // 编辑半月学 - 跳转到编辑页面
    handleEdit(item) {
      console.log('点击编辑按钮:', item.id)
      uni.navigateTo({
        url: `/pages/learning/edit?id=${item.id}`,
        fail: (err) => {
          console.error('跳转失败:', err)
          this.$modal.msgError('页面跳转失败，请重试')
        }
      })
    },
    
    // 删除半月学
    handleDelete(item) {
      console.log('点击删除按钮:', item.id)
      uni.showModal({
        title: '确认删除',
        content: `确定要删除半月学"${item.title}"吗？`,
        success: async (res) => {
          if (res.confirm) {
            try {
              await delLearning(item.id)
              this.$modal.msgSuccess('删除成功')
              // 重新加载列表
              this.queryParams.pageNum = 1
              this.learningList = []
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
    
    // 格式化时间
    formatTime(timeStr) {
      if (!timeStr) return ''
      const date = new Date(timeStr)
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      return `${month}-${day} ${hours}:${minutes}`
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
    
    // 去除HTML标签
    stripHtml(html) {
      if (!html) return ''
      return html.replace(/<[^>]+>/g, '').substring(0, 100) + (html.length > 100 ? '...' : '')
    }
  }
}
</script>

<style lang="scss" scoped>
.learning-container {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.search-section {
  background: #fff;
  padding: 20rpx;
  margin-bottom: 20rpx;
  
  .search-container {
    margin-bottom: 20rpx;
  }
  
  .filter-section {
    display: flex;
    gap: 20rpx;
    
    .filter-item {
      flex: 1;
    }
  }
}

.learning-list {
  padding: 0 20rpx;
}

.learning-card {
  margin-bottom: 20rpx;
  border-radius: 16rpx;
  overflow: hidden;
  background: #fff;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  
  &:active {
    transform: scale(0.98);
    box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
  }
}

.card-container {
  padding: 24rpx;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20rpx;
  
  .header-left {
    display: flex;
    gap: 12rpx;
    flex-wrap: wrap;
  }
  
  .header-right {
    .learning-time {
      font-size: 24rpx;
      color: #909399;
      white-space: nowrap;
    }
  }
}

.card-content {
  .learning-title {
    font-size: 32rpx;
    font-weight: 600;
    color: #303133;
    margin-bottom: 20rpx;
    line-height: 1.4;
  }
  
  .learning-info {
    margin-bottom: 20rpx;
    
    .info-row {
      display: flex;
      margin-bottom: 12rpx;
      
      &:last-child {
        margin-bottom: 0;
      }
      
      .info-item {
        display: flex;
        align-items: center;
        flex: 1;
        min-width: 0;
        
        &.full-width {
          flex: none;
          width: 100%;
        }
        
        .info-text {
          font-size: 26rpx;
          color: #606266;
          margin-left: 8rpx;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }
    }
  }
  
  .learning-description {
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

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 40rpx;
  
  .empty-icon {
    margin-bottom: 30rpx;
    opacity: 0.5;
  }
  
  .empty-text {
    font-size: 32rpx;
    color: #909399;
    margin-bottom: 12rpx;
  }
  
  .empty-subtitle {
    font-size: 26rpx;
    color: #c0c4cc;
  }
}

.floating-add-btn {
  position: fixed;
  right: 40rpx;
  bottom: 120rpx;
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
</style>