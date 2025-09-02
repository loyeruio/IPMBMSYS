<template>
  <view class="container">
    <!-- 自定义导航栏 -->
    <view class="custom-navbar" :style="{paddingTop: statusBarHeight + 'px'}">
      <view class="navbar-content">
        <view class="navbar-title">
          <image class="logo" src="@/static/logo.png"></image>
          <text class="title-text">智慧党建</text>
        </view>
        <view class="navbar-right">
          <uni-icons type="search" size="20" color="#fff"></uni-icons>
        </view>
      </view>
    </view>

    <!-- 轮播图 -->
    <view class="banner-section">
      <uni-swiper-dot class="uni-swiper-dot-box" :info="bannerList" :current="current" field="content">
        <swiper class="swiper-box" :current="swiperDotIndex" @change="changeSwiper" :autoplay="true" :interval="4000">
          <swiper-item v-for="(item, index) in bannerList" :key="index">
            <view class="swiper-item" @click="clickBannerItem(item)">
              <image :src="getFullImageUrl(item.imageUrl)" mode="aspectFill" :draggable="false" />
              <view class="banner-overlay">
                <text class="banner-title">{{ item.title }}</text>
                <text class="banner-subtitle" v-if="item.remark">{{ item.remark }}</text>
              </view>
            </view>
          </swiper-item>
        </swiper>
      </uni-swiper-dot>
    </view>

    <!-- 党组织概览 -->
    <view class="stats-section">
      <view class="section-title">
        <uni-icons type="office-building" size="16" color="#e60012"></uni-icons>
        <text class="title-text">党组织概览</text>
      </view>
      <view class="stats-grid">
        <view class="stat-item" v-for="(item, index) in orgStats" :key="index">
          <view class="stat-icon" :class="item.iconClass">
            <uni-icons :type="item.icon" size="24" color="#fff"></uni-icons>
          </view>
          <view class="stat-info">
            <text class="stat-value">{{ item.value }}</text>
            <text class="stat-label">{{ item.title }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 快捷功能 -->
    <view class="quick-section">
      <view class="section-title">
        <uni-icons type="grid" size="16" color="#e60012"></uni-icons>
        <text class="title-text">快捷功能</text>
      </view>
      <view class="quick-grid">
        <view class="quick-item" v-for="(item, index) in quickMenus" :key="index" @click="navigateTo(item.path)">
          <view class="quick-icon" :style="{backgroundColor: item.color}">
            <uni-icons :type="item.icon" size="20" color="#fff"></uni-icons>
          </view>
          <text class="quick-text">{{ item.title }}</text>
        </view>
      </view>
    </view>

    <!-- 最近会议 -->
    <view class="recent-section">
      <view class="section-header">
        <view class="section-title">
          <uni-icons type="calendar" size="16" color="#e60012"></uni-icons>
          <text class="title-text">最近会议</text>
        </view>
        <text class="more-btn" @click="navigateTo('/pages/meeting/list')">更多</text>
      </view>
      <view class="meeting-list">
        <view class="meeting-item" v-for="(item, index) in recentMeetings" :key="index" @click="navigateTo('/pages/meeting/detail?id=' + item.id)">
          <view class="meeting-date">
            <text class="date-day">{{ formatDay(item.startTime) }}</text>
            <text class="date-month">{{ formatMonth(item.startTime) }}</text>
          </view>
          <view class="meeting-info">
            <text class="meeting-title">{{ item.title }}</text>
            <text class="meeting-location">{{ item.location }}</text>
            <text class="meeting-time">{{ formatTime(item.startTime) }}</text>
          </view>
          <view class="meeting-status">
            <uni-tag :text="getMeetingStatus(item)" :type="getMeetingStatusType(item)" size="small"></uni-tag>
          </view>
        </view>
      </view>
    </view>

    <!-- 通知公告 -->
    <view class="notice-section">
      <view class="section-header">
        <view class="section-title">
          <uni-icons type="sound" size="16" color="#e60012"></uni-icons>
          <text class="title-text">通知公告</text>
        </view>
        <text class="more-btn" @click="navigateTo('/pages/notice/list')">更多</text>
      </view>
      <view class="notice-list">
        <view class="notice-item" v-for="(item, index) in recentNotices" :key="index" @click="navigateTo('/pages/notice/detail?id=' + item.noticeId)">
          <view class="notice-icon">
            <uni-icons type="notification" size="16" color="#ff9500"></uni-icons>
          </view>
          <view class="notice-content">
            <text class="notice-title">{{ item.noticeTitle }}</text>
            <text class="notice-time">{{ formatDate(item.createTime) }}</text>
          </view>
          <uni-icons type="right" size="14" color="#c0c4cc"></uni-icons>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { listCarouselPublic } from '@/api/system/carousel'
import { listDept } from '@/api/system/dept'
import { listAllUser } from '@/api/system/user'
import { listCommon } from '@/api/meeting/meeting'
import { listNotice } from '@/api/system/notice'
import config from '@/config'

export default {
  data() {
    return {
      statusBarHeight: 0,
      current: 0,
      swiperDotIndex: 0,
      bannerList: [],
      baseUrl: config.baseUrl,
      orgStats: [
        { title: '党组织总数', value: 0, icon: 'auth', iconClass: 'stat-icon-1' },
        { title: '党员总数', value: 0, icon: 'person', iconClass: 'stat-icon-2' },
        { title: '预备党员', value: 0, icon: 'person-filled', iconClass: 'stat-icon-3' },
        { title: '发展对象', value: 0, icon: 'staff', iconClass: 'stat-icon-4' }
      ],
      quickMenus: [
        { title: '党建会议', icon: 'calendar', path: '/pages/meeting/list', color: '#409eff' },
        { title: '党课学习', icon: 'list', path: '/pages/class/list', color: '#67c23a' },
        { title: '学习活动', icon: 'star', path: '/pages/learning/list', color: '#e6a23c' },
        { title: '通知公告', icon: 'sound', path: '/pages/notice/list', color: '#f56c6c' }
      ],
      recentMeetings: [],
      recentNotices: []
    }
  },
  onLoad() {
    this.getSystemInfo()
    this.fetchData()
  },
  methods: {
    getSystemInfo() {
      const systemInfo = uni.getSystemInfoSync()
      this.statusBarHeight = systemInfo.statusBarHeight
    },
    
    async fetchData() {
      await Promise.all([
        this.fetchBannerData(),
        this.fetchOrgStats(),
        this.fetchRecentMeetings(),
        this.fetchRecentNotices()
      ])
    },
    
    // 获取轮播图数据
    async fetchBannerData() {
      try {
        const res = await listCarouselPublic({
          pageNum: 1,
          pageSize: 5,
          type: '0',
          status: '0'
        })
        if (res.code === 200 && res.data) {
          this.bannerList = res.data.filter(item => {
            const now = new Date()
            const startTime = item.startTime ? new Date(item.startTime) : null
            const endTime = item.endTime ? new Date(item.endTime) : null
            return (!startTime || now >= startTime) && (!endTime || now <= endTime)
          })
        }
      } catch (error) {
        console.error('获取轮播图失败:', error)
      }
    },
    
    // 获取组织统计数据
    async fetchOrgStats() {
      try {
        // 获取党组织数据
        const deptRes = await listDept()
        if (deptRes.code === 200 && deptRes.data) {
          this.orgStats[0].value = deptRes.data.length
        }
        
        // 获取党员数据
        const userRes = await listAllUser({})
        if (userRes.code === 200 && userRes.data) {
          const users = userRes.data
          this.orgStats[1].value = users.length
          
          // 统计预备党员 (category = 2)
          const probationaryMembers = users.filter(user => user.category === '2')
          this.orgStats[2].value = probationaryMembers.length
          
          // 统计发展对象 (category = 1)
          const developmentObjects = users.filter(user => user.category === '1')
          this.orgStats[3].value = developmentObjects.length
        }
      } catch (error) {
        console.error('获取组织统计失败:', error)
      }
    },
    
    // 获取最近会议
    async fetchRecentMeetings() {
      try {
        const res = await listCommon({
          pageNum: 1,
          pageSize: 3,
          orderByColumn: 'start_time',
          isAsc: 'desc'
        })
        if (res.code === 200 && res.rows) {
          this.recentMeetings = res.rows
        }
      } catch (error) {
        console.error('获取会议数据失败:', error)
      }
    },
    
    // 获取最近通知
    async fetchRecentNotices() {
      try {
        const res = await listNotice({
          pageNum: 1,
          pageSize: 3,
          status: '0',
          orderByColumn: 'createTime',
          isAsc: 'desc'
        })
        if (res.code === 200 && res.rows) {
          this.recentNotices = res.rows
        }
      } catch (error) {
        console.error('获取通知数据失败:', error)
      }
    },
    
    changeSwiper(e) {
      this.current = e.detail.current
      this.swiperDotIndex = e.detail.current
    },
    
    clickBannerItem(item) {
      if (item.linkUrl) {
        if (item.linkUrl.startsWith('http')) {
          uni.navigateTo({
            url: `/pages/common/webview/index?url=${encodeURIComponent(item.linkUrl)}&title=${encodeURIComponent(item.title)}`
          })
        } else {
          uni.navigateTo({ url: item.linkUrl })
        }
      }
    },
    
    navigateTo(url) {
      uni.navigateTo({ url })
    },
    
    formatDay(dateStr) {
      if (!dateStr) return '--'
      const date = new Date(dateStr)
      return date.getDate().toString().padStart(2, '0')
    },
    
    formatMonth(dateStr) {
      if (!dateStr) return '--'
      const date = new Date(dateStr)
      return `${date.getMonth() + 1}月`
    },
    
    formatTime(dateStr) {
      if (!dateStr) return '--'
      const date = new Date(dateStr)
      return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
    },
    
    formatDate(dateStr) {
      if (!dateStr) return '--'
      const date = new Date(dateStr)
      return `${date.getMonth() + 1}-${date.getDate()}`
    },
    
    getMeetingStatus(meeting) {
      const now = new Date()
      const startTime = new Date(meeting.startTime)
      const endTime = new Date(meeting.endTime)
      
      if (now < startTime) return '未开始'
      if (now > endTime) return '已结束'
      return '进行中'
    },
    
    getMeetingStatusType(meeting) {
      const status = this.getMeetingStatus(meeting)
      switch (status) {
        case '未开始': return 'primary'
        case '进行中': return 'success'
        case '已结束': return 'info'
        default: return 'primary'
      }
    },
    
    // 获取完整图片URL
    getFullImageUrl(imagePath) {
      if (!imagePath) return ''
      if (imagePath.indexOf('http') === 0) {
        return imagePath
      }
      return this.baseUrl + imagePath
    },
  }
}
</script>

<style lang="scss" scoped>
.container {
  background-color: #f8f8f8;
  min-height: 100vh;
}

// 自定义导航栏
.custom-navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 999;
  background: linear-gradient(135deg, #e60012 0%, #ff4757 100%);
  color: white;
  
  .navbar-content {
    height: 44px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 15px;
    
    .navbar-title {
      display: flex;
      align-items: center;
      
      .logo {
        width: 24px;
        height: 24px;
        margin-right: 8px;
      }
      
      .title-text {
        font-size: 18px;
        font-weight: bold;
      }
    }
  }
}

// 为页面内容添加上边距，避免被固定导航栏遮挡
.container {
  background-color: #f8f8f8;
  min-height: 100vh;
  padding-top: 90px; // 导航栏高度
}

// 轮播图
.banner-section {
  margin: 10px;
  border-radius: 8px;
  overflow: hidden;
  
  .swiper-box {
    height: 160px;
  }
  
  .swiper-item {
    position: relative;
    height: 100%;
    
    image {
      width: 100%;
      height: 100%;
    }
    
    .banner-overlay {
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      background: linear-gradient(transparent, rgba(0, 0, 0, 0.6));
      color: white;
      padding: 20px 15px 15px;
      
      .banner-title {
        display: block;
        font-size: 16px;
        font-weight: bold;
        margin-bottom: 4px;
      }
      
      .banner-subtitle {
        display: block;
        font-size: 12px;
        opacity: 0.9;
      }
    }
  }
}

// 统计区域
.stats-section {
  margin: 10px;
  background: white;
  border-radius: 8px;
  padding: 15px;
  
  .stats-grid {
    display: flex;
    margin-top: 15px;
    
    .stat-item {
      flex: 1;
      display: flex;
      flex-direction: column;
      align-items: center;
      
      .stat-icon {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-bottom: 8px;
        
        &.stat-icon-1 { background: #409eff; }
        &.stat-icon-2 { background: #67c23a; }
        &.stat-icon-3 { background: #e6a23c; }
        &.stat-icon-4 { background: #f56c6c; }
      }
      
      .stat-info {
        text-align: center;
        
        .stat-value {
          display: block;
          font-size: 20px;
          font-weight: bold;
          color: #303133;
          margin-bottom: 2px;
        }
        
        .stat-label {
          display: block;
          font-size: 12px;
          color: #909399;
        }
      }
    }
  }
}

// 快捷功能
.quick-section {
  margin: 10px;
  background: white;
  border-radius: 8px;
  padding: 15px;
  
  .quick-grid {
    display: flex;
    margin-top: 15px;
    
    .quick-item {
      flex: 1;
      display: flex;
      flex-direction: column;
      align-items: center;
      
      .quick-icon {
        width: 36px;
        height: 36px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-bottom: 8px;
      }
      
      .quick-text {
        font-size: 12px;
        color: #606266;
      }
    }
  }
}

// 公共样式
.section-title {
  display: flex;
  align-items: center;
  
  .title-text {
    margin-left: 6px;
    font-size: 16px;
    font-weight: bold;
    color: #303133;
  }
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  .more-btn {
    font-size: 12px;
    color: #909399;
  }
}

// 最近会议
.recent-section {
  margin: 10px;
  background: white;
  border-radius: 8px;
  padding: 15px;
  
  .meeting-list {
    margin-top: 15px;
    
    .meeting-item {
      display: flex;
      align-items: center;
      padding: 12px 0;
      border-bottom: 1px solid #f0f0f0;
      
      &:last-child {
        border-bottom: none;
      }
      
      .meeting-date {
        width: 50px;
        text-align: center;
        margin-right: 15px;
        
        .date-day {
          display: block;
          font-size: 20px;
          font-weight: bold;
          color: #e60012;
        }
        
        .date-month {
          display: block;
          font-size: 12px;
          color: #909399;
        }
      }
      
      .meeting-info {
        flex: 1;
        
        .meeting-title {
          display: block;
          font-size: 14px;
          color: #303133;
          margin-bottom: 4px;
        }
        
        .meeting-location {
          display: block;
          font-size: 12px;
          color: #909399;
          margin-bottom: 2px;
        }
        
        .meeting-time {
          display: block;
          font-size: 12px;
          color: #909399;
        }
      }
      
      .meeting-status {
        margin-left: 10px;
      }
    }
  }
}

// 通知公告
.notice-section {
  margin: 10px;
  background: white;
  border-radius: 8px;
  padding: 15px;
  
  .notice-list {
    margin-top: 15px;
    
    .notice-item {
      display: flex;
      align-items: center;
      padding: 12px 0;
      border-bottom: 1px solid #f0f0f0;
      
      &:last-child {
        border-bottom: none;
      }
      
      .notice-icon {
        margin-right: 12px;
      }
      
      .notice-content {
        flex: 1;
        
        .notice-title {
          display: block;
          font-size: 14px;
          color: #303133;
          margin-bottom: 4px;
        }
        
        .notice-time {
          display: block;
          font-size: 12px;
          color: #909399;
        }
      }
    }
  }
}
</style>