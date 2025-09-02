<template>
  <view class="mine-container" :style="{height: `${windowHeight}px`}">
    <!-- 顶部个人信息栏 -->
    <view class="header-section">
      <view class="user-info-card">
        <view class="avatar-section">
          <view v-if="!avatar" class="user-avatar-placeholder">
            <uni-icons type="person-filled" size="40" color="#FFFFFF"></uni-icons>
          </view>
          <image v-if="avatar" @click="handleToAvatar" :src="avatar" class="user-avatar" mode="aspectFill"></image>
          
          <view class="user-details">
            <view v-if="!name" @click="handleToLogin" class="login-section">
              <text class="login-tip">点击登录</text>
              <text class="login-subtitle">登录后查看完整信息</text>
            </view>
            <view v-if="name" class="user-info-content">
              <view class="user-name-section">
                <text class="user-name">{{ userInfo.nickName || name }}</text>
                <view class="user-status">
                  <uni-icons type="checkmarkempty" size="14" color="#34C759"></uni-icons>
                  <text class="status-text">已认证</text>
                </view>
              </view>
              <view class="user-meta-info">
                <view class="meta-item" v-if="userInfo.phonenumber">
                  <uni-icons type="phone" size="12" color="rgba(255,255,255,0.8)"></uni-icons>
                  <text class="meta-text">{{ userInfo.phonenumber }}</text>
                </view>
                <view class="meta-item" v-if="userInfo.dept && userInfo.dept.deptName">
                  <uni-icons type="home" size="12" color="rgba(255,255,255,0.8)"></uni-icons>
                  <text class="meta-text">{{ userInfo.dept.deptName }}</text>
                </view>
              </view>
              <view class="party-info" v-if="userInfo.partyRole || userInfo.partyPost">
                <view class="party-tag" v-if="userInfo.partyRole">
                  <uni-icons type="star-filled" size="10" color="#FFD700"></uni-icons>
                  <text>{{ userInfo.partyRole }}</text>
                </view>
                <view class="party-tag" v-if="userInfo.partyPost">
                  <uni-icons type="person-filled" size="10" color="#FFD700"></uni-icons>
                  <text>{{ userInfo.partyPost }}</text>
                </view>
              </view>
            </view>
          </view>
        </view>
        
        <!-- 快捷操作按钮 -->
        <view class="quick-actions" v-if="name">
          <view class="action-btn" @click="handleToInfo">
            <uni-icons type="person-filled" size="16" color="#007AFF"></uni-icons>
            <text>详情</text>
          </view>
          <view class="action-btn" @click="handleToEditInfo">
            <uni-icons type="compose" size="16" color="#007AFF"></uni-icons>
            <text>编辑</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 主要内容区域 -->
    <view class="content-section">
      <!-- 功能菜单 -->
      <view class="menu-section">
        <view class="section-title">
          <uni-icons type="grid" size="18" color="#007AFF"></uni-icons>
          <text>功能菜单</text>
        </view>
        
        <view class="menu-grid">
          <view class="menu-card" @click="handleToInfo">
            <view class="menu-icon-wrapper">
              <uni-icons type="person-filled" size="24" color="#007AFF"></uni-icons>
            </view>
            <text class="menu-title">个人信息</text>
            <text class="menu-subtitle">查看详细资料</text>
          </view>
          
          <view class="menu-card" @click="handleToEditInfo">
            <view class="menu-icon-wrapper">
              <uni-icons type="compose" size="24" color="#34C759"></uni-icons>
            </view>
            <text class="menu-title">编辑资料</text>
            <text class="menu-subtitle">修改个人信息</text>
          </view>
          
          <view class="menu-card" @click="handleToSetting">
            <view class="menu-icon-wrapper">
              <uni-icons type="gear-filled" size="24" color="#FF9500"></uni-icons>
            </view>
            <text class="menu-title">应用设置</text>
            <text class="menu-subtitle">系统偏好设置</text>
          </view>
          
          <view class="menu-card" @click="handleAbout">
            <view class="menu-icon-wrapper">
              <uni-icons type="heart-filled" size="24" color="#FF3B30"></uni-icons>
            </view>
            <text class="menu-title">关于我们</text>
            <text class="menu-subtitle">了解更多信息</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getUserProfile } from "@/api/system/user"

export default {
  data() {
    return {
      name: this.$store.state.user.name,
      userInfo: {}
    }
  },
  computed: {
    avatar() {
      return this.$store.state.user.avatar
    },
    windowHeight() {
      return uni.getSystemInfoSync().windowHeight - 50
    }
  },
  onLoad() {
    if (this.name) {
      this.getUserInfo()
    }
  },
  onShow() {
    // 页面显示时刷新用户信息
    this.name = this.$store.state.user.name
    if (this.name) {
      this.getUserInfo()
    }
  },
  methods: {
    async getUserInfo() {
      try {
        const response = await getUserProfile()
        this.userInfo = response.data
        
        // 获取角色和职务信息
        this.userInfo.partyRole = response.roleGroup || ''
        this.userInfo.partyPost = response.postGroup || ''
        
        console.log('用户信息:', this.userInfo)
      } catch (error) {
        console.error('获取用户信息失败:', error)
      }
    },
    handleToInfo() {
      this.$tab.navigateTo('/pages/mine/info/index')
    },
    handleToEditInfo() {
      this.$tab.navigateTo('/pages/mine/info/edit')
    },
    handleToSetting() {
      this.$tab.navigateTo('/pages/mine/setting/index')
    },
    handleToLogin() {
      this.$tab.reLaunch('/pages/login')
    },
    handleToAvatar() {
      this.$tab.navigateTo('/pages/mine/avatar/index')
    },
    handleHelp() {
      this.$tab.navigateTo('/pages/mine/help/index')
    },
    handleAbout() {
      this.$tab.navigateTo('/pages/mine/about/index')
    }
  }
}
</script>

<style lang="scss" scoped>
page {
  background: linear-gradient(180deg, #F5F7FA 0%, #E8ECEF 100%);
}

.mine-container {
  width: 100%;
  height: 100%;
  font-family: -apple-system, PingFang SC, sans-serif;

  .header-section {
    padding: 40rpx 32rpx 60rpx 32rpx;
    
    position: relative;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(255, 255, 255, 0.1);
      backdrop-filter: blur(10rpx);
    }

    .user-info-card {
      position: relative;
      z-index: 2;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 32rpx;
      padding: 40rpx 32rpx;
      backdrop-filter: blur(20rpx);
      border: 1rpx solid rgba(255, 255, 255, 0.2);
      box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.1);

      .avatar-section {
        display: flex;
        align-items: flex-start;
        gap: 24rpx;
        margin-bottom: 32rpx;

        .user-avatar-placeholder {
          width: 120rpx;
          height: 120rpx;
          border-radius: 50%;
          background: rgba(255, 255, 255, 0.2);
          display: flex;
          align-items: center;
          justify-content: center;
          border: 3rpx solid rgba(255, 255, 255, 0.3);
        }

        .user-avatar {
          width: 120rpx;
          height: 120rpx;
          border-radius: 50%;
          border: 3rpx solid rgba(255, 255, 255, 0.3);
          box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.2);
        }

        .user-details {
          flex: 1;
          
          .login-section {
            .login-tip {
              font-size: 36rpx;
              font-weight: 600;
              color: #FFFFFF;
              display: block;
              margin-bottom: 8rpx;
            }
            
            .login-subtitle {
              font-size: 24rpx;
              color: rgba(255, 255, 255, 0.8);
            }
          }

          .user-info-content {
            .user-name-section {
              display: flex;
              align-items: center;
              gap: 16rpx;
              margin-bottom: 16rpx;

              .user-name {
                font-size: 36rpx;
                font-weight: 600;
                color: #FFFFFF;
              }

              .user-status {
                display: flex;
                align-items: center;
                gap: 6rpx;
                background: rgba(52, 199, 89, 0.2);
                padding: 6rpx 12rpx;
                border-radius: 20rpx;
                border: 1rpx solid rgba(52, 199, 89, 0.3);

                .status-text {
                  font-size: 20rpx;
                  color: #34C759;
                }
              }
            }

            .user-meta-info {
              display: flex;
              flex-direction: column;
              gap: 12rpx;
              margin-bottom: 20rpx;

              .meta-item {
                display: flex;
                align-items: center;
                gap: 12rpx;

                .meta-text {
                  font-size: 24rpx;
                  color: rgba(255, 255, 255, 0.9);
                }
              }
            }

            .party-info {
              display: flex;
              gap: 16rpx;
              flex-wrap: wrap;

              .party-tag {
                display: flex;
                align-items: center;
                gap: 8rpx;
                background: rgba(255, 215, 0, 0.2);
                padding: 8rpx 16rpx;
                border-radius: 20rpx;
                border: 1rpx solid rgba(255, 215, 0, 0.3);

                text {
                  font-size: 22rpx;
                  color: #FFD700;
                  font-weight: 500;
                }
              }
            }
          }
        }
      }

      .quick-actions {
        display: flex;
        gap: 24rpx;
        justify-content: center;

        .action-btn {
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 8rpx;
          padding: 20rpx 32rpx;
          background: rgba(255, 255, 255, 0.2);
          border-radius: 20rpx;
          border: 1rpx solid rgba(255, 255, 255, 0.3);
          backdrop-filter: blur(10rpx);
          transition: all 0.3s;

          text {
            font-size: 24rpx;
            color: #FFFFFF;
            font-weight: 500;
          }

          &:active {
            transform: scale(0.95);
            background: rgba(255, 255, 255, 0.3);
          }
        }
      }
    }
  }

  .content-section {
    position: relative;
    top: -40rpx;
    padding: 0 32rpx;

    .menu-section {
      .section-title {
        display: flex;
        align-items: center;
        gap: 12rpx;
        margin-bottom: 32rpx;
        padding-left: 16rpx;
        border-left: 6rpx solid #007AFF;

        text {
          font-size: 32rpx;
          font-weight: 600;
          color: #1A1A1A;
        }
      }

      .menu-grid {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 24rpx;

        .menu-card {
          background: rgba(255, 255, 255, 0.95);
          border-radius: 24rpx;
          padding: 32rpx 24rpx;
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 16rpx;
          box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
          backdrop-filter: blur(10rpx);
          border: 1rpx solid rgba(255, 255, 255, 0.8);
          transition: all 0.3s;

          &:active {
            transform: scale(0.95);
            box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
          }

          .menu-icon-wrapper {
            width: 80rpx;
            height: 80rpx;
            border-radius: 50%;
            background: rgba(0, 122, 255, 0.1);
            display: flex;
            align-items: center;
            justify-content: center;
          }

          .menu-title {
            font-size: 28rpx;
            font-weight: 600;
            color: #1A1A1A;
            text-align: center;
          }

          .menu-subtitle {
            font-size: 22rpx;
            color: #666666;
            text-align: center;
            line-height: 1.4;
          }
        }
      }
    }
  }
}
</style>