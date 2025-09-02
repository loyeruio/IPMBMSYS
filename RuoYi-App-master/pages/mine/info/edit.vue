<template>
  <view class="page-container">
    <view class="form-container">
      <uni-forms ref="form" :model="user" :rules="rules" labelWidth="140px">
        <!-- 基本信息卡片 -->
        <view class="form-section">
          <view class="section-header">
            <uni-icons type="person-filled" size="18" color="#007AFF"></uni-icons>
            <text class="section-title">基本信息</text>
          </view>
          <view class="form-card">
            <uni-forms-item label="用户昵称" name="nickName" required>
              <uni-easyinput 
                v-model="user.nickName" 
                placeholder="请输入昵称" 
                :styles="inputStyles"
              />
            </uni-forms-item>
            
            <uni-forms-item label="手机号码" name="phonenumber" required>
              <uni-easyinput 
                v-model="user.phonenumber" 
                placeholder="请输入手机号码" 
                :styles="inputStyles"
              />
            </uni-forms-item>
            
            <uni-forms-item label="邮箱" name="email" required>
              <uni-easyinput 
                v-model="user.email" 
                placeholder="请输入邮箱" 
                :styles="inputStyles"
              />
            </uni-forms-item>
            
            <uni-forms-item label="教师编号/学号" name="partyMemberId">
              <uni-easyinput 
                v-model="user.partyMemberId" 
                placeholder="请输入教师编号/学号" 
                :styles="inputStyles"
              />
            </uni-forms-item>
            
            <uni-forms-item label="性别" name="sex" required>
              <uni-data-checkbox 
                v-model="user.sex" 
                :localdata="sexOptions" 
                :multiple="false"
                selectedColor="#007AFF"
              />
            </uni-forms-item>
          </view>
        </view>

        <!-- 党员信息卡片 -->
        <view class="form-section">
          <view class="section-header">
            <uni-icons type="star-filled" size="18" color="#FF3B30"></uni-icons>
            <text class="section-title">党员信息</text>
          </view>
          <view class="form-card">
            <uni-forms-item label="党员身份" name="userType">
              <uni-data-select
                v-model="user.userType"
                :localdata="userTypeOptions"
                placeholder="请选择党员身份"
                :clear="false"
              ></uni-data-select>
            </uni-forms-item>
            
            <uni-forms-item label="所属民族" name="ethnicity">
              <uni-data-select
                v-model="user.ethnicity"
                :localdata="ethnicityOptions"
                placeholder="请选择所属民族"
                :clear="false"
              ></uni-data-select>
            </uni-forms-item>
            
            <uni-forms-item label="学历" name="education">
              <uni-data-select
                v-model="user.education"
                :localdata="educationOptions"
                placeholder="请选择学历"
                :clear="false"
              ></uni-data-select>
            </uni-forms-item>
            
            <uni-forms-item label="党员类别" name="category">
              <uni-data-select
                v-model="user.category"
                :localdata="categoryOptions"
                placeholder="请选择党员类别"
                :clear="false"
              ></uni-data-select>
            </uni-forms-item>
          </view>
        </view>

        <!-- 重要日期卡片 -->
        <view class="form-section">
          <view class="section-header">
            <uni-icons type="calendar-filled" size="18" color="#FF9500"></uni-icons>
            <text class="section-title">重要日期</text>
          </view>
          <view class="form-card">
            <uni-forms-item label="出生日期" name="birthDate">
              <uni-datetime-picker 
                v-model="user.birthDate" 
                type="date" 
                :clear-icon="false"
                placeholder="请选择出生日期"
              />
            </uni-forms-item>
            
            <uni-forms-item label="申请入党日期" name="applicationDate">
              <uni-datetime-picker 
                v-model="user.applicationDate" 
                type="date" 
                :clear-icon="false"
                placeholder="请选择申请入党日期"
              />
            </uni-forms-item>
            
            <uni-forms-item label="成为积极分子日期" name="activistDate">
              <uni-datetime-picker 
                v-model="user.activistDate" 
                type="date" 
                :clear-icon="false"
                placeholder="请选择成为积极分子日期"
              />
            </uni-forms-item>
            
            <uni-forms-item label="确定发展对象日期" name="developmentObjectDate">
              <uni-datetime-picker 
                v-model="user.developmentObjectDate" 
                type="date" 
                :clear-icon="false"
                placeholder="请选择确定发展对象日期"
              />
            </uni-forms-item>
            
            <uni-forms-item label="成为预备党员日期" name="probationaryMemberDate">
              <uni-datetime-picker 
                v-model="user.probationaryMemberDate" 
                type="date" 
                :clear-icon="false"
                placeholder="请选择成为预备党员日期"
              />
            </uni-forms-item>
            
            <uni-forms-item label="转为正式党员日期" name="fullMemberDate">
              <uni-datetime-picker 
                v-model="user.fullMemberDate" 
                type="date" 
                :clear-icon="false"
                placeholder="请选择转为正式党员日期"
              />
            </uni-forms-item>
          </view>
        </view>

        <!-- 状态信息卡片 -->
        <view class="form-section">
          <view class="section-header">
            <uni-icons type="gear-filled" size="18" color="#8E8E93"></uni-icons>
            <text class="section-title">状态信息</text>
          </view>
          <view class="form-card">
            <uni-forms-item label="是否失联党员" name="isMissing" class="checkbox-item">
              <uni-data-checkbox 
                v-model="user.isMissing" 
                :localdata="yesNoOptions" 
                :multiple="false"
                selectedColor="#007AFF"
              />
            </uni-forms-item>
            
            <uni-forms-item label="是否流动党员" name="isMobile" class="checkbox-item">
              <uni-data-checkbox 
                v-model="user.isMobile" 
                :localdata="yesNoOptions" 
                :multiple="false"
                selectedColor="#007AFF"
              />
            </uni-forms-item>
            
            <uni-forms-item label="备注" name="remark">
              <uni-easyinput 
                v-model="user.remark" 
                type="textarea" 
                placeholder="请输入备注" 
                :styles="textareaStyles"
                maxlength="200"
              />
            </uni-forms-item>
          </view>
        </view>
      </uni-forms>
      
      <!-- 操作按钮 -->
      <view class="button-container">
        <button class="cancel-button" @click="goBack">
          <text>取消</text>
        </button>
        <button class="submit-button" @click="submit">
          <uni-icons type="checkmarkempty" size="18" color="#fff"></uni-icons>
          <text>保存</text>
        </button>
      </view>
    </view>
  </view>
</template>

<script>
import { getUserProfile, updateUserProfile } from "@/api/system/user"

export default {
  dicts: ['party_membership_type', 'party_member_ethnicity', 'party_member_education', 'category_informations', 'party_yes_no', 'sys_user_sex'],
  data() {
    return {
      user: {
        nickName: "",
        phonenumber: "",
        email: "",
        sex: "",
        partyMemberId: "",
        userType: "",
        ethnicity: "",
        education: "",
        category: "",
        partyPost: "",
        partyRole: "",
        birthDate: "",
        applicationDate: "",
        activistDate: "",
        developmentObjectDate: "",
        probationaryMemberDate: "",
        fullMemberDate: "",
        isMissing: "",
        isMobile: "",
        remark: ""
      },
      // 将 inputStyles 和 textareaStyles 移到 data 的根级别
      inputStyles: {
        backgroundColor: '#f8f9fa',
        borderRadius: '12rpx',
        padding: '24rpx 20rpx',
        fontSize: '28rpx'
      },
      textareaStyles: {
        backgroundColor: '#f8f9fa',
        borderRadius: '12rpx',
        padding: '24rpx 20rpx',
        fontSize: '28rpx',
        minHeight: '120rpx'
      },
      rules: {
        nickName: {
          rules: [{
            required: true,
            errorMessage: '用户昵称不能为空'
          }]
        },
        phonenumber: {
          rules: [{
            required: true,
            errorMessage: '手机号码不能为空'
          }, {
            pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
            errorMessage: '请输入正确的手机号码'
          }]
        },
        email: {
          rules: [{
            required: true,
            errorMessage: '邮箱地址不能为空'
          }, {
            format: 'email',
            errorMessage: '请输入正确的邮箱地址'
          }]
        }
      }
    }
  },
  computed: {
    // 性别选项
    sexOptions() {
      if (!this.dict.type || !this.dict.type.sys_user_sex) {
        return []
      }
      return this.dict.type.sys_user_sex.map(item => ({
        value: item.value,
        text: item.label
      }))
    },
    // 党员身份选项
    userTypeOptions() {
      if (!this.dict.type || !this.dict.type.party_membership_type) {
        return []
      }
      return this.dict.type.party_membership_type.map(item => ({
        value: item.value,
        text: item.label
      }))
    },
    // 民族选项
    ethnicityOptions() {
      if (!this.dict.type || !this.dict.type.party_member_ethnicity) {
        return []
      }
      return this.dict.type.party_member_ethnicity.map(item => ({
        value: item.value,
        text: item.label
      }))
    },
    // 学历选项
    educationOptions() {
      if (!this.dict.type || !this.dict.type.party_member_education) {
        return []
      }
      return this.dict.type.party_member_education.map(item => ({
        value: item.value,
        text: item.label
      }))
    },
    // 党员类别选项
    categoryOptions() {
      if (!this.dict.type || !this.dict.type.category_informations) {
        return []
      }
      return this.dict.type.category_informations.map(item => ({
        value: item.value,
        text: item.label
      }))
    },
    // 是否选项
    yesNoOptions() {
      if (!this.dict.type || !this.dict.type.party_yes_no) {
        return []
      }
      return this.dict.type.party_yes_no.map(item => ({
        value: item.value,
        text: item.label
      }))
    }
  },
  onLoad() {
    this.getUser()
  },
  onReady() {
    this.$refs.form.setRules(this.rules)
  },
  methods: {
    getUser() {
      getUserProfile().then(response => {
        console.log('获取到的用户数据:', response.data) // 添加调试日志
        this.user = response.data
        // 格式化日期字段为YYYY-MM-DD格式
        this.formatDateFields()
      }).catch(error => {
        console.error('获取用户信息失败:', error)
        this.$modal.msgError('获取用户信息失败')
      })
    },
    formatDateFields() {
      const dateFields = ['birthDate', 'applicationDate', 'activistDate', 'developmentObjectDate', 'probationaryMemberDate', 'fullMemberDate']
      dateFields.forEach(field => {
        if (this.user[field]) {
          this.user[field] = this.formatDate(this.user[field])
        }
      })
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    submit(ref) {
      this.$refs.form.validate().then(res => {
        updateUserProfile(this.user).then(response => {
          this.$modal.msgSuccess("修改成功")
          // 更新store中的用户信息
          this.$store.dispatch('user/getInfo')
        })
      })
    },
    goBack() {
      uni.navigateBack()
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

/* 表单容器 */
.form-container {
  padding: 0 32rpx;
}

/* 表单分组 */
.form-section {
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

/* 表单卡片 */
.form-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 24rpx;
  padding: 24rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
}

/* 表单项 */
uni-forms-item {
  padding: 20rpx 0;
  border-bottom: 1rpx solid #F0F0F0;
}

uni-forms-item:last-child {
  border-bottom: none;
}

.form-item__box {
  display: flex;
  align-items: center;
}

.form-item__label {
  font-size: 28rpx;
  color: #666666;
  width: 200rpx !important;
}

.form-item__content {
  flex: 1;
}

/* 输入框样式 */
uni-easyinput {
  background: #F8F9FB;
  border-radius: 16rpx;
  padding: 16rpx 20rpx;
  font-size: 28rpx;
  color: #1A1A1A;
}

uni-easyinput input,
uni-easyinput textarea {
  background: transparent !important;
}

uni-easyinput:focus-within {
  background: #E6F0FA;
  box-shadow: 0 0 0 2rpx #007AFF;
}

/* 选择框样式 */
uni-data-select,
uni-data-checkbox {
  font-size: 28rpx;
  color: #1A1A1A;
}

uni-data-select .uni-select,
uni-data-checkbox .uni-checkbox {
  background: #F8F9FB;
  border-radius: 16rpx;
  padding: 16rpx 20rpx;
}

uni-data-select .uni-select:focus,
uni-data-checkbox .uni-checkbox:focus {
  background: #E6F0FA;
  box-shadow: 0 0 0 2rpx #007AFF;
}

/* 操作按钮容器 */
.button-container {
  display: flex;
  gap: 24rpx;
  padding: 32rpx;
  justify-content: center;
}

.cancel-button,
.submit-button {
  flex: 1;
  height: 88rpx;
  border-radius: 44rpx;
  font-size: 32rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  transition: all 0.3s;
}

.cancel-button {
  background: #F0F0F0;
  color: #1A1A1A;
  border: none;
}

.cancel-button:hover {
  background: #E0E0E0;
  transform: scale(1.02);
}

.submit-button {
  background: linear-gradient(90deg, #007AFF 0%, #4DA8FF 100%);
  color: #FFFFFF;
  border: none;
}

.submit-button:hover {
  background: linear-gradient(90deg, #005BB5 0%, #3B8CDB 100%);
  transform: scale(1.02);
}
</style>