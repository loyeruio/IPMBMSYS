<template>
  <view class="notice-container">
    <!-- 搜索和筛选区域 -->
    <view class="search-section">
      <view class="search-container">
        <uni-search-bar 
          v-model="searchKeyword" 
          placeholder="搜索公告标题" 
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
            v-model="queryParams.noticeType"
            :localdata="noticeTypeSelectOptions"
            placeholder="通知类型"
            @change="handleFilter"
          ></uni-data-select>
        </view>
        
        <view class="filter-item">
          <uni-data-select
            v-model="queryParams.status"
            :localdata="statusSelectOptions"
            placeholder="状态"
            @change="handleFilter"
            :clear="false"
          ></uni-data-select>
        </view>
      </view>
    </view>

    <!-- 通知列表 -->
    <view class="notice-list">
      <uni-load-more 
        v-if="loading" 
        status="loading"
      ></uni-load-more>
      
      <view 
        v-for="(item, index) in noticeList" 
        :key="item.noticeId" 
        class="notice-card"
        @click="goToDetail(item.noticeId)"
      >
        <view class="card-container">
          <!-- 卡片头部 -->
          <view class="card-header">
            <view class="header-left">
              <!-- 通知类型标签 -->
              <DictTag 
                v-if="dict && dict.type && dict.type.sys_notice_type && Array.isArray(dict.type.sys_notice_type)"
                :options="dict.type.sys_notice_type"
                :value="item.noticeType"
              />
              <!-- 状态标签 -->
              <DictTag 
                v-if="dict && dict.type && dict.type.sys_notice_status && Array.isArray(dict.type.sys_notice_status)"
                :options="dict.type.sys_notice_status"
                :value="item.status"
              />
            </view>
            <view class="header-right">
              <text class="notice-time">{{ formatDate(item.createTime) }}</text>
            </view>
          </view>
          
          <!-- 卡片内容 -->
          <view class="card-content">
            <view class="notice-title">{{ item.noticeTitle }}</view>
            
            <view class="notice-info">
              <view class="info-row">
                <view class="info-item">
                  <uni-icons type="person" size="14" color="#909399"></uni-icons>
                  <text class="info-text">{{ item.createBy || '系统' }}</text>
                </view>
                <view class="info-item" v-if="item.recordTime">
                  <uni-icons type="calendar" size="14" color="#909399"></uni-icons>
                  <text class="info-text">{{ formatDateTime(item.recordTime) }}</text>
                </view>
              </view>
              
              <view class="info-row" v-if="item.noticeUserIds">
                <view class="info-item full-width">
                  <uni-icons type="contact" size="14" color="#909399"></uni-icons>
                  <text class="info-text">{{ formatUserDisplay(item.noticeUserIds) }}</text>
                </view>
              </view>
            </view>
            
            <view class="notice-description" v-if="item.noticeContent">
              <text class="description-text">{{ stripHtml(item.noticeContent) }}</text>
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
            
            <view class="action-btn detail-btn" @click.stop="goToDetail(item.noticeId)">
              <uni-icons type="eye" size="14"></uni-icons>
              <text class="btn-text">详情</text>
            </view>
          </view>
        </view>
      </view>
      
      <!-- 空状态 -->
      <view v-if="!loading && noticeList.length === 0" class="empty-state">
        <view class="empty-icon">
          <uni-icons type="sound" size="80" color="#d3d4d6"></uni-icons>
        </view>
        <text class="empty-text">暂无通知公告</text>
        <text class="empty-subtitle">点击右上角添加第一个通知</text>
      </view>
      
      <!-- 加载更多 -->
      <uni-load-more 
        v-if="!loading && hasMore" 
        status="more" 
        @clickLoadMore="loadMore"
      ></uni-load-more>
      
      <uni-load-more 
        v-if="!loading && !hasMore && noticeList.length > 0" 
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
import { listNotice, addNotice, updateNotice, delNotice } from '@/api/system/notice'
import { listAllUser } from '@/api/system/user'
import DictTag from '@/components/DictTag/index.vue'

export default {
  dicts: ['sys_notice_type', 'sys_notice_status', 'sys_activity_type'],
  components: {
    DictTag
  },
  data() {
    return {
      loading: false,
      hasMore: true,
      searchKeyword: '',
      noticeList: [],
      userList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        noticeTitle: '',
        noticeType: '',
        status: ''
      },
      total: 0,
      statusBarHeight: 0
    }
  },
  computed: {
    // uni-data-select专用的数据格式
    noticeTypeSelectOptions() {
      if (!this.dict.type || !this.dict.type.sys_notice_type) {
        return []
      }
      return this.dict.type.sys_notice_type.map(item => ({
        value: item.value,
        text: item.label
      }))
    },
    statusSelectOptions() {
      if (!this.dict.type || !this.dict.type.sys_notice_status) {
        return []
      }
      return this.dict.type.sys_notice_status.map(item => ({
        value: item.value,
        text: item.label
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
    this.noticeList = []
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
    
    // 获取通知列表
    async getList() {
      if (this.loading) return
      
      this.loading = true
      try {
        const response = await listNotice(this.queryParams)
        
        let rows = response.rows || []
        
        if (this.queryParams.pageNum === 1) {
          this.noticeList = rows
        } else {
          this.noticeList = [...this.noticeList, ...rows]
        }
        
        this.total = response.total || 0
        this.hasMore = this.noticeList.length < this.total
        
      } catch (error) {
        console.error('获取通知列表失败:', error)
        this.$modal.msgError('获取通知列表失败，请检查网络连接')
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
        url: `/pages/notice/detail?id=${id}`
      })
    },
    
    // 搜索
    handleSearch() {
      this.queryParams.noticeTitle = this.searchKeyword
      this.queryParams.pageNum = 1
      this.noticeList = []
      this.hasMore = true
      this.getList()
    },
    
    // 清除搜索
    handleClear() {
      this.searchKeyword = ''
      this.queryParams.noticeTitle = ''
      this.queryParams.pageNum = 1
      this.noticeList = []
      this.hasMore = true
      this.getList()
    },
    
    // 筛选
    handleFilter() {
      this.queryParams.pageNum = 1
      this.noticeList = []
      this.hasMore = true
      this.getList()
    },
    
    // 新增通知 - 跳转到新增页面
    handleAdd() {
      console.log('点击新增按钮')
      uni.navigateTo({
        url: '/pages/notice/add',
        fail: (err) => {
          console.error('跳转失败:', err)
          this.$modal.msgError('页面跳转失败，请重试')
        }
      })
    },
    
    // 编辑通知 - 跳转到编辑页面
    handleEdit(item) {
      console.log('点击编辑按钮:', item.noticeId)
      uni.navigateTo({
        url: `/pages/notice/edit?id=${item.noticeId}`,
        fail: (err) => {
          console.error('跳转失败:', err)
          this.$modal.msgError('页面跳转失败，请重试')
        }
      })
    },
    
    // 删除通知
    handleDelete(item) {
      console.log('点击删除按钮:', item.noticeId)
      uni.showModal({
        title: '确认删除',
        content: `确定要删除通知"${item.noticeTitle}"吗？`,
        success: async (res) => {
          if (res.confirm) {
            try {
              await delNotice(item.noticeId)
              this.$modal.msgSuccess('删除成功')
              // 重新加载列表
              this.queryParams.pageNum = 1
              this.noticeList = []
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

    // 格式化日期
    formatDate(dateTime) {
      if (!dateTime) return ''
      const date = new Date(dateTime)
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${month}-${day}`
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
    
    // 格式化用户显示
    formatUserDisplay(userIds) {
      if (!userIds) return '全部用户'
      const ids = userIds.split(',').filter(id => id)
      const userNames = ids.map(id => {
        const user = this.userList.find(u => u.userId == id)
        return user ? user.userName : `用户${id}`
      })
      
      if (userNames.length <= 3) {
        return userNames.join('、')
      } else {
        return `${userNames.slice(0, 3).join('、')}等${userNames.length}人`
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.notice-container {
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

// 通知列表
.notice-list {
  padding: 0 15px 100px;
}

.notice-card {
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
    .notice-time {
      font-size: 12px;
      color: #909399;
      font-weight: 500;
    }
  }
}

// 卡片内容
.card-content {
  padding: 15px;
  
  .notice-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 12px;
    line-height: 1.4;
  }
  
  .notice-info {
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
        font-size: 12px;
        color: #606266;
      }
    }
  }
  
  .notice-description {
    margin-top: 12px;
    
    .description-text {
      font-size: 13px;
      color: #909399;
      line-height: 1.5;
    }
  }
}

// 操作按钮
.card-actions {
  display: flex;
  border-top: 1px solid #f0f0f0;
  
  .action-btn {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 12px;
    font-size: 12px;
    color: #606266;
    transition: all 0.3s ease;
    
    &:not(:last-child) {
      border-right: 1px solid #f0f0f0;
    }
    
    .btn-text {
      margin-left: 4px;
    }
    
    &:active {
      background-color: #f5f7fa;
    }
    
    &.edit-btn {
      color: #409eff;
      
      &:active {
        background-color: rgba(64, 158, 255, 0.1);
      }
    }
    
    &.delete-btn {
      color: #f56c6c;
      
      &:active {
        background-color: rgba(245, 108, 108, 0.1);
      }
    }
    
    &.detail-btn {
      color: #67c23a;
      
      &:active {
        background-color: rgba(103, 194, 58, 0.1);
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
    margin-bottom: 16px;
    opacity: 0.5;
  }
  
  .empty-text {
    font-size: 16px;
    color: #909399;
    margin-bottom: 8px;
  }
  
  .empty-subtitle {
    font-size: 14px;
    color: #c0c4cc;
  }
}
</style>