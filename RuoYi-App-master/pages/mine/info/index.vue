<template>
  <view class="page-container">
    <!-- 用户头像和基本信息卡片 -->
    <view class="user-header-card">
      <view class="avatar-section">
        <image 
          class="user-avatar" 
          :src="getAvatarUrl(user.avatar)" 
          mode="aspectFill"
        ></image>
        <view class="user-basic-info">
          <text class="user-name">{{ user.userName || '未设置' }}</text>
          <text class="user-nickname">{{ user.nickName || '暂无昵称' }}</text>
        </view>
      </view>
    </view>

    <!-- 信息分组展示 -->
    <view class="info-sections">
      <!-- 基本信息 -->
      <view class="info-section">
        <view class="section-header">
          <uni-icons type="person-filled" size="18" color="#007AFF"></uni-icons>
          <text class="section-title">基本信息</text>
        </view>
        <view class="info-card">
          <view class="info-item">
            <view class="info-label">
              <uni-icons type="contact-filled" size="16" color="#666"></uni-icons>
              <text>昵称</text>
            </view>
            <text class="info-value">{{ user.nickName || '暂无' }}</text>
          </view>
          <view class="info-item">
            <view class="info-label">
              <uni-icons type="phone-filled" size="16" color="#666"></uni-icons>
              <text>手机号码</text>
            </view>
            <text class="info-value">{{ user.phonenumber || '暂无' }}</text>
          </view>
          <view class="info-item">
            <view class="info-label">
              <uni-icons type="email-filled" size="16" color="#666"></uni-icons>
              <text>邮箱</text>
            </view>
            <text class="info-value">{{ user.email || '暂无' }}</text>
          </view>
          <view class="info-item">
            <view class="info-label">
              <uni-icons type="person" size="16" color="#666"></uni-icons>
              <text>性别</text>
            </view>
            <text class="info-value">{{ getSexText(user.sex) || '暂无' }}</text>
          </view>
          <view class="info-item">
            <view class="info-label">
              <uni-icons type="more" size="16" color="#666"></uni-icons>
              <text>教师编号/学号</text>
            </view>
            <text class="info-value">{{ user.partyMemberId || '暂无' }}</text>
          </view>
        </view>
      </view>

      <!-- 党员信息 -->
      <view class="info-section">
        <view class="section-header">
          <uni-icons type="star-filled" size="18" color="#FF3B30"></uni-icons>
          <text class="section-title">党员信息</text>
        </view>
        <view class="info-card">
          <view class="info-item">
            <view class="info-label">
              <uni-icons type="star-filled" size="16" color="#666"></uni-icons>
              <text>党员身份</text>
            </view>
            <view class="info-value">
              <DictTag 
                v-if="dict && dict.type && dict.type.party_membership_type"
                :options="dict.type.party_membership_type"
                :value="user.userType"
              />
            </view>
          </view>
          <view class="info-item">
            <view class="info-label">
              <uni-icons type="flag-filled" size="16" color="#666"></uni-icons>
              <text>所属民族</text>
            </view>
            <view class="info-value">
              <DictTag 
                v-if="dict && dict.type && dict.type.party_member_ethnicity"
                :options="dict.type.party_member_ethnicity"
                :value="user.ethnicity"
              />
            </view>
          </view>
          <view class="info-item">
            <view class="info-label">
              <uni-icons type="bars" size="16" color="#666"></uni-icons>
              <text>学历</text>
            </view>
            <view class="info-value">
              <DictTag 
                v-if="dict && dict.type && dict.type.party_member_education"
                :options="dict.type.party_member_education"
                :value="user.education"
              />
            </view>
          </view>
          <view class="info-item">
            <view class="info-label">
              <uni-icons type="list" size="16" color="#666"></uni-icons>
              <text>党员类别</text>
            </view>
            <view class="info-value">
              <DictTag 
                v-if="dict && dict.type && dict.type.category_informations"
                :options="dict.type.category_informations"
                :value="user.category"
              />
            </view>
          </view>
        </view>
      </view>

      <!-- 组织信息 -->
      <view class="info-section">
        <view class="section-header">
          <uni-icons type="home-filled" size="18" color="#34C759"></uni-icons>
          <text class="section-title">组织信息</text>
        </view>
        <view class="info-card">
          <view class="info-item">
            <view class="info-label">
              <uni-icons type="home-filled" size="16" color="#666"></uni-icons>
              <text>所属党组织</text>
            </view>
            <text class="info-value">{{ user.dept ? user.dept.deptName : '暂无' }}</text>
          </view>
          <view class="info-item">
            <view class="info-label">
              <uni-icons type="person-filled" size="16" color="#666"></uni-icons>
              <text>党员职务</text>
            </view>
            <text class="info-value">{{ user.partyPost || '暂无' }}</text>
          </view>
          <view class="info-item">
            <view class="info-label">
              <uni-icons type="person-filled" size="16" color="#666"></uni-icons>
              <text>党员角色</text>
            </view>
            <text class="info-value">{{ user.partyRole || '暂无' }}</text>
          </view>
        </view>
      </view>

      <!-- 重要日期 -->
      <view class="info-section">
        <view class="section-header">
          <uni-icons type="calendar-filled" size="18" color="#FF9500"></uni-icons>
          <text class="section-title">重要日期</text>
        </view>
        <view class="info-card">
          <view class="info-item">
            <view class="info-label">
              <uni-icons type="calendar-filled" size="16" color="#666"></uni-icons>
              <text>出生日期</text>
            </view>
            <text class="info-value">{{ formatDate(user.birthDate) || '暂无' }}</text>
          </view>
          <view class="info-item">
            <view class="info-label">
              <uni-icons type="calendar" size="16" color="#666"></uni-icons>
              <text>申请入党日期</text>
            </view>
            <text class="info-value">{{ formatDate(user.applicationDate) || '暂无' }}</text>
          </view>
          <view class="info-item">
            <view class="info-label">
              <uni-icons type="calendar" size="16" color="#666"></uni-icons>
              <text>成为积极分子日期</text>
            </view>
            <text class="info-value">{{ formatDate(user.activistDate) || '暂无' }}</text>
          </view>
          <view class="info-item">
            <view class="info-label">
              <uni-icons type="calendar" size="16" color="#666"></uni-icons>
              <text>确定发展对象日期</text>
            </view>
            <text class="info-value">{{ formatDate(user.developmentObjectDate) || '暂无' }}</text>
          </view>
          <view class="info-item">
            <view class="info-label">
              <uni-icons type="calendar" size="16" color="#666"></uni-icons>
              <text>成为预备党员日期</text>
            </view>
            <text class="info-value">{{ formatDate(user.probationaryMemberDate) || '暂无' }}</text>
          </view>
          <view class="info-item">
            <view class="info-label">
              <uni-icons type="calendar" size="16" color="#666"></uni-icons>
              <text>转为正式党员日期</text>
            </view>
            <text class="info-value">{{ formatDate(user.fullMemberDate) || '暂无' }}</text>
          </view>
        </view>
      </view>

      <!-- 状态信息 -->
      <view class="info-section">
        <view class="section-header">
          <uni-icons type="gear-filled" size="18" color="#8E8E93"></uni-icons>
          <text class="section-title">状态信息</text>
        </view>
        <view class="info-card">
          <view class="info-item">
            <view class="info-label">
              <uni-icons type="help-filled" size="16" color="#666"></uni-icons>
              <text>是否失联党员</text>
            </view>
            <view class="info-value">
              <DictTag 
                v-if="dict && dict.type && dict.type.party_yes_no"
                :options="dict.type.party_yes_no"
                :value="user.isMissing"
              />
            </view>
          </view>
          <view class="info-item">
            <view class="info-label">
              <uni-icons type="location-filled" size="16" color="#666"></uni-icons>
              <text>是否流动党员</text>
            </view>
            <view class="info-value">
              <DictTag 
                v-if="dict && dict.type && dict.type.party_yes_no"
                :options="dict.type.party_yes_no"
                :value="user.isMobile"
              />
            </view>
          </view>
          <view class="info-item">
            <view class="info-label">
              <uni-icons type="gear-filled" size="16" color="#666"></uni-icons>
              <text>账号状态</text>
            </view>
            <view class="info-value">
              <DictTag 
                v-if="dict && dict.type && dict.type.sys_normal_disable"
                :options="dict.type.sys_normal_disable"
                :value="user.status"
              />
            </view>
          </view>
          <view class="info-item">
            <view class="info-label">
              <uni-icons type="calendar-filled" size="16" color="#666"></uni-icons>
              <text>创建时间</text>
            </view>
            <text class="info-value">{{ formatDate(user.createTime) || '暂无' }}</text>
          </view>
          <view class="info-item" v-if="user.remark">
            <view class="info-label">
              <uni-icons type="compose" size="16" color="#666"></uni-icons>
              <text>备注</text>
            </view>
            <text class="info-value remark-text">{{ user.remark }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 编辑按钮 -->
    <view class="edit-button-container">
      <button class="edit-button" @click="goToEdit">
        <uni-icons type="compose" size="18" color="#fff"></uni-icons>
        <text>编辑信息</text>
      </button>
    </view>
  </view>
</template>

<script>
import { getUserProfile, getRoleListByUserId, getPostListByUserId } from "@/api/system/user"
import { listRole } from "@/api/system/role"
import { listPost } from "@/api/system/post"
import DictTag from '@/components/DictTag/index.vue'
import config from '@/config'
export default {
  dicts: ['party_membership_type', 'party_member_ethnicity', 'party_member_education', 'category_informations', 'party_yes_no', 'sys_normal_disable', 'sys_user_sex'],
  components: {
    DictTag
  },
  data() {
    return {
      user: {},
      roleGroup: "",
      postGroup: "",
      roleOptions: [],
      postOptions: [],
      baseUrl: config.baseUrl
    }
  },
  onLoad() {
    this.getUser()
    this.getRoleList()
    this.getPostList()
  },
  methods: {
    async getUser() {
      try {
        const response = await getUserProfile()
        this.user = response.data
        
        // 直接从响应中获取角色组和职务组数据
        this.user.partyRole = response.roleGroup || ''
        this.user.partyPost = response.postGroup || ''
        
        console.log('用户数据:', this.user)
        console.log('党员角色:', this.user.partyRole)
        console.log('党员职务:', this.user.partyPost)
      } catch (error) {
        console.error('获取用户信息失败:', error)
      }
    },
    
    // 新增方法：获取用户的角色和职务
    async getUserRolesAndPosts(userId) {
      try {
        // 获取用户角色
        const roleRes = await getRoleListByUserId(userId)
        const userRoles = (roleRes.data || []).filter(role => role.flag === true)
        this.user.partyRole = userRoles.map(role => role.roleName).join(',')
        
        // 获取用户职务
        const postRes = await getPostListByUserId(userId)
        const userPosts = postRes.data || []
        this.user.partyPost = userPosts.map(post => post.postName).join(',')
        
        console.log('角色信息:', userRoles)
        console.log('职务信息:', userPosts)
      } catch (error) {
        console.error('获取角色职务信息失败:', error)
      }
    },
    // 获取角色列表
    getRoleList() {
      listRole().then(response => {
        this.roleOptions = response.rows || []
      })
    },
    // 获取职务列表
    getPostList() {
      listPost().then(response => {
        this.postOptions = response.rows || []
      })
    },
    // 根据角色ID获取角色名称
    getRoleName(roleIds) {
      if (!roleIds || !this.roleOptions.length) return ''
      const ids = Array.isArray(roleIds) ? roleIds : roleIds.split(',')
      const names = ids.map(id => {
        const role = this.roleOptions.find(item => item.roleId == id)
        return role ? role.roleName : ''
      }).filter(name => name)
      return names.join(', ')
    },
    // 根据职务ID获取职务名称
    getPostName(postIds) {
      if (!postIds || !this.postOptions.length) return ''
      const ids = Array.isArray(postIds) ? postIds : postIds.split(',')
      const names = ids.map(id => {
        const post = this.postOptions.find(item => item.postId == id)
        return post ? post.postName : ''
      }).filter(name => name)
      return names.join(', ')
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    getSexText(sex) {
      if (!this.dict.type || !this.dict.type.sys_user_sex) return ''
      const sexDict = this.dict.type.sys_user_sex.find(item => item.value === sex)
      return sexDict ? sexDict.label : ''
    },
    goToEdit() {
      uni.navigateTo({
        url: '/pages/mine/info/edit'
      })
    },
    
    // 获取头像URL
    getAvatarUrl(avatar) {
      if (!avatar) return ''
      if (avatar.indexOf('http') === 0) {
        return avatar
      }
      return this.baseUrl + avatar
    }
  }
}
</script>

<style scoped>
/* 全局页面容器 */
.page-container {
  background: linear-gradient(180deg, #F5F7FA 0%, #E8ECEF 100%);
  min-height: 100vh;
  padding: 20rpx 0;
  font-family: -apple-system, PingFang SC, sans-serif;
}

/* 用户头部卡片 */
.user-header-card {
  margin: 30rpx 32rpx;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 32rpx;
  padding: 40rpx 32rpx;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.08);
  backdrop-filter: blur(10rpx);
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

/* 头像和基本信息区域 */
.avatar-section {
  display: flex;
  align-items: center;
  gap: 24rpx;
}

.user-avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  border: 4rpx solid #FFFFFF;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.user-basic-info {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.user-name {
  font-size: 36rpx;
  font-weight: 600;
  color: #1A1A1A;
}

.user-nickname {
  font-size: 28rpx;
  color: #666666;
}

/* 信息分组区域 */
.info-sections {
  padding: 0 32rpx;
}

.info-section {
  margin-bottom: 32rpx;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 24rpx;
  padding-left: 16rpx;
  border-left: 6rpx solid #007AFF;
}

.section-title {
  font-size: 32rpx;
  font-weight: 500;
  color: #1A1A1A;
}

/* 信息卡片 */
.info-card {
  background: #FFFFFF;
  border-radius: 24rpx;
  padding: 24rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #F0F0F0;
}

.info-item:last-child {
  border-bottom: none;
}

.info-label {
  display: flex;
  align-items: center;
  gap: 12rpx;
  font-size: 28rpx;
  color: #666666;
}

.info-value {
  font-size: 28rpx;
  color: #1A1A1A;
  max-width: 50%;
  text-align: right;
}

.remark-text {
  color: #666666;
  line-height: 1.5;
  max-width: 100%;
}

/* 编辑按钮 */
.edit-button-container {
  padding: 32rpx;
  display: flex;
  justify-content: center;
}

.edit-button {
  display: flex;
  align-items: center;
  gap: 12rpx;
  width: 100%;
  height: 88rpx;
  background: linear-gradient(90deg, #007AFF 0%, #4DA8FF 100%);
  border-radius: 44rpx;
  font-size: 32rpx;
  color: #FFFFFF;
  border: none;
  transition: all 0.3s;
}

.edit-button:hover {
  background: linear-gradient(90deg, #005BB5 0%, #3B8CDB 100%);
  transform: scale(1.02);
}
</style>