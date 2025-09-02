<template>
  <view class="participants-container">
    <!-- 顶部操作栏 -->
    <view class="header-actions">
      <view class="meeting-info">
        <text class="meeting-title">{{ meetingDetail.title }}</text>
        <text class="participant-count">参会人员 ({{ participants.length }})</text>
      </view>
      <button class="add-btn" @click="showAddParticipant">
        <uni-icons type="plus" size="16" color="#fff"></uni-icons>
        <text>添加人员</text>
      </button>
    </view>

    <!-- 参与人员列表 -->
    <view class="participants-list">
        <view 
            v-for="(participant, index) in participants" 
            :key="index"
            wx:key="index"
            class="participant-item"
            v-if="participant && participant.userId && participant.userName"
        >
        <view class="participant-info">
          <view class="participant-main">
            <text class="participant-name">{{ participant.userName }}</text>
            <text class="participant-dept">{{ participant.deptName }}</text>
            <!-- 添加备注显示 -->
            <text v-if="participant.remark" class="participant-remark">备注：{{ participant.remark }}</text>
          </view>
          <view class="participant-status">
            <!-- 签到状态 -->
            <DictTag 
              v-if="dict && dict.type && dict.type.check_status"
              :options="dict.type.check_status"
              :value="participant.signInStatus"
              size="small"
            />
            <!-- 出勤状态 -->
            <DictTag 
              v-if="dict && dict.type && dict.type.attendance_status"
              :options="dict.type.attendance_status"
              :value="participant.attendanceStatus"
              size="small"
              style="margin-left: 8px;"
            />
          </view>
        </view>
        
        <view class="participant-actions">
          <button class="edit-btn" @click="editParticipant(participant, index)">
            <uni-icons type="compose" size="16" color="#409eff"></uni-icons>
          </button>
          <button class="delete-btn" @click="deleteParticipant(participant, index)">
            <uni-icons type="trash" size="16" color="#f56c6c"></uni-icons>
          </button>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-if="participants.length === 0" class="empty-state">
      <uni-icons type="person" size="60" color="#d3d4d6"></uni-icons>
      <text class="empty-text">暂无参会人员</text>
      <button class="add-first-btn" @click="showAddParticipant">添加第一个参会人员</button>
    </view>

    <!-- 添加/编辑参与人员弹窗 -->
    <uni-popup ref="participantPopup" type="bottom">
      <view class="popup-content">
        <view class="popup-header">
          <text class="popup-title">{{ isEdit ? '编辑参会人员' : '添加参会人员' }}</text>
        </view>
        
        <view class="form-content">
          <!-- 选择人员 -->
          <view class="form-item" v-if="!isEdit">
            <text class="form-label">选择人员</text>
            <picker 
              :value="selectedUserIndex" 
              :range="availableUsers" 
              range-key="userName"
              @change="onUserChange"
            >
              <view class="picker-input">
                <text>{{ selectedUser ? selectedUser.userName : '请选择人员' }}</text>
                <uni-icons type="arrowdown" size="14" color="#c0c4cc"></uni-icons>
              </view>
            </picker>
          </view>
          
          <!-- 显示选中用户信息 -->
          <view v-if="selectedUser" class="user-info">
            <text class="user-name">{{ selectedUser.userName }}</text>
            <text class="user-dept">{{ getUserDeptName(selectedUser.deptId) }}</text>
          </view>
          
          <!-- 签到状态 -->
          <view class="form-item">
            <text class="form-label">签到状态</text>
            <picker 
              :value="getSignInStatusIndex(currentParticipant.signInStatus)" 
              :range="signInStatusOptions" 
              range-key="label"
              @change="onSignInStatusChange"
            >
              <view class="picker-input">
                <text>{{ getSignInStatusLabel(currentParticipant.signInStatus) }}</text>
                <uni-icons type="arrowdown" size="14" color="#c0c4cc"></uni-icons>
              </view>
            </picker>
          </view>
          
          <!-- 出勤状态 -->
          <view class="form-item">
            <text class="form-label">出勤状态</text>
            <picker 
              :value="getAttendanceStatusIndex(currentParticipant.attendanceStatus)" 
              :range="attendanceStatusOptions" 
              range-key="label"
              @change="onAttendanceStatusChange"
            >
              <view class="picker-input">
                <text>{{ getAttendanceStatusLabel(currentParticipant.attendanceStatus) }}</text>
                <uni-icons type="arrowdown" size="14" color="#c0c4cc"></uni-icons>
              </view>
            </picker>
          </view>
          
          <!-- 备注 -->
          <view class="form-item">
            <text class="form-label">备注</text>
            <textarea 
              v-model="currentParticipant.remark" 
              placeholder="请输入备注信息"
              class="remark-input"
              maxlength="200"
            ></textarea>
          </view>
        </view>
        
        <view class="popup-actions">
          <button class="cancel-btn" @click="closePopup">取消</button>
          <button class="confirm-btn" @click="saveParticipant" :loading="saving">保存</button>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script>
import { getCommon } from '@/api/meeting/meeting'
import { 
  listMeetingparticipant, 
  addMeetingparticipant, 
  updateMeetingparticipant, 
  delMeetingparticipant 
} from '@/api/participant/meetingparticipant'
import { listAllUser } from '@/api/system/user'
import { listDept } from '@/api/system/dept'
import DictTag from '@/components/DictTag/index.vue'

export default {
  dicts: ['check_status', 'attendance_status'],
  components: {
    DictTag
  },
  data() {
    return {
      meetingId: null,
      meetingDetail: {},
      participants: [],
      userList: [],
      deptList: [],
      
      // 弹窗相关
      isEdit: false,
      currentParticipant: {
        id: null,
        meetingId: null,
        userId: null,
        userName: '',
        deptName: '',
        signInStatus: '0',
        attendanceStatus: '0',
        remark: ''
      },
      selectedUser: null,
      selectedUserIndex: 0,
      currentEditIndex: -1,
      saving: false
    }
  },
  
  computed: {
    // 签到状态选项
    signInStatusOptions() {
      return this.dict.type.check_status || []
    },
    
    // 出勤状态选项
    attendanceStatusOptions() {
      return this.dict.type.attendance_status || []
    },
    
    // 可选择的用户列表（排除已添加的用户）
    availableUsers() {
      // 增加安全检查
      const participantUserIds = this.participants
        .filter(p => p && p.userId)
        .map(p => p.userId)
      return this.userList.filter(user => user && user.userId && !participantUserIds.includes(user.userId))
    }
  },
  
  onLoad(options) {
    if (options.id) {
      this.meetingId = options.id
      this.init()
    }
  },
  
  methods: {
    // 初始化
    async init() {
      try {
        // 并行加载基础数据
        await Promise.all([
          this.loadMeetingDetail(),
          this.loadUserList(),
          this.loadDeptList()
        ])
        
        // 基础数据加载完成后再加载参与人员
        await this.loadParticipants()
        
        console.log('所有数据加载完成')
      } catch (error) {
        console.error('初始化失败:', error)
        uni.showToast({
          title: '页面初始化失败',
          icon: 'none'
        })
      }
    },
    
    // 加载会议详情
    async loadMeetingDetail() {
      try {
        const response = await getCommon(this.meetingId)
        this.meetingDetail = response.data
      } catch (error) {
        console.error('加载会议详情失败:', error)
        uni.showToast({
          title: '加载会议详情失败',
          icon: 'none'
        })
      }
    },
    
    // 加载用户列表
    async loadUserList() {
      try {
        const response = await listAllUser()
        this.userList = response.data || []
      } catch (error) {
        console.error('加载用户列表失败:', error)
      }
    },
    
    // 加载部门列表
    async loadDeptList() {
      try {
        const response = await listDept()
        this.deptList = response.data || []
      } catch (error) {
        console.error('加载部门列表失败:', error)
      }
    },
    
    // 加载参与人员
    // 加载参与人员
    async loadParticipants() {
      try {
        const response = await listMeetingparticipant({ meetingId: this.meetingId })
        // 增强数据过滤，确保数据完整性
        this.participants = (response.rows || [])
          .filter(participant => {
            if (!participant) {
              console.warn('发现空的participant对象')
              return false
            }
            if (!participant.userId) {
              console.warn('participant缺少userId:', participant)
              return false
            }
            if (!participant.userName) {
              console.warn('participant缺少userName:', participant)
              return false
            }
            return true
          })
          .map(participant => {
            // 确保每个participant都有id属性
            return {
              ...participant,
              id: participant.id || `temp-${Date.now()}-${Math.random()}`
            }
          })
        
        console.log('加载参与人员完成，有效数据:', this.participants.length)
        
        // 验证数据完整性
        this.validateParticipantsData()
      } catch (error) {
        console.error('加载参与人员失败:', error)
        uni.showToast({
          title: '加载参与人员失败',
          icon: 'none'
        })
      }
    },
    
    // 添加数据验证方法
    validateParticipantsData() {
      const invalidItems = this.participants.filter(p => !p || !p.userId || !p.userName)
      if (invalidItems.length > 0) {
        console.error('发现无效的参与人员数据:', invalidItems)
        // 清理无效数据
        this.participants = this.participants.filter(p => p && p.userId && p.userName)
      }
    },
    
    // 显示添加参与人员弹窗
    showAddParticipant() {
      if (this.availableUsers.length === 0) {
        uni.showToast({
          title: '没有可添加的用户',
          icon: 'none'
        })
        return
      }
      
      this.isEdit = false
      this.resetCurrentParticipant()
      this.$refs.participantPopup.open()
    },
    
    editParticipant(participant, index) {
      console.log('editParticipant 调用:', { participant, index, participantsLength: this.participants.length })
      
      // 增强参数检查
      if (!participant) {
        console.error('参与人员对象为空:', participant)
        console.error('当前participants数组:', this.participants)
        console.error('传入的index:', index)
        // 尝试从数组中重新获取
        if (index >= 0 && index < this.participants.length) {
          participant = this.participants[index]
          if (!participant) {
            uni.showToast({
              title: '参与人员信息缺失，请刷新页面重试',
              icon: 'none'
            })
            return
          }
        } else {
          uni.showToast({
            title: '参与人员信息缺失，请刷新页面重试',
            icon: 'none'
          })
          return
        }
      }
      
      if (!participant.userId) {
        console.error('参与人员缺少userId:', participant)
        uni.showToast({
          title: '参与人员数据不完整，请刷新页面重试',
          icon: 'none'
        })
        return
      }
      
      // 检查userList是否已加载
      if (!this.userList || this.userList.length === 0) {
        console.warn('用户列表未加载完成，重新加载...')
        this.loadUserList().then(() => {
          // 重新尝试编辑
          this.editParticipant(participant, index)
        })
        return
      }
      
      this.isEdit = true
      this.currentEditIndex = index
      this.currentParticipant = { ...participant }
      
      // 安全地查找用户
      this.selectedUser = this.userList.find(u => u && u.userId === participant.userId) || null
      
      if (!this.selectedUser) {
        console.warn('未找到对应的用户信息:', participant.userId)
        uni.showToast({
          title: '未找到用户信息，请刷新页面重试',
          icon: 'none'
        })
        return
      }
      
      this.$refs.participantPopup.open()
    },
    
    // 删除参与人员
    async deleteParticipant(participant, index) {
      try {
        await uni.showModal({
          title: '确认删除',
          content: `确定要删除参会人员"${participant.userName}"吗？`,
          confirmText: '删除',
          confirmColor: '#f56c6c'
        })
        
        if (participant.id) {
          await delMeetingparticipant(participant.id)
        }
        
        this.participants.splice(index, 1)
        
        uni.showToast({
          title: '删除成功',
          icon: 'success'
        })
      } catch (error) {
        if (error.errMsg && error.errMsg.includes('cancel')) {
          return
        }
        console.error('删除参与人员失败:', error)
        uni.showToast({
          title: '删除失败',
          icon: 'none'
        })
      }
    },
    
    // 保存参与人员
    async saveParticipant() {
      if (!this.validateParticipant()) {
        return
      }
      
      this.saving = true
      
      try {
        if (this.isEdit) {
          // 更新参与人员
          await updateMeetingparticipant(this.currentParticipant)
          this.participants[this.currentEditIndex] = { ...this.currentParticipant }
          uni.showToast({
            title: '修改成功',
            icon: 'success'
          })
        } else {
          // 添加参与人员
          const participantData = {
            meetingId: this.meetingId,
            userId: this.selectedUser.userId,
            userName: this.selectedUser.userName,
            deptName: this.getUserDeptName(this.selectedUser.deptId),
            signInStatus: this.currentParticipant.signInStatus,
            attendanceStatus: this.currentParticipant.attendanceStatus,
            remark: this.currentParticipant.remark
          }
          
          const response = await addMeetingparticipant(participantData)
          participantData.id = response.data?.id
          this.participants.push(participantData)
          
          uni.showToast({
            title: '添加成功',
            icon: 'success'
          })
        }
        
        this.closePopup()
      } catch (error) {
        console.error('保存参与人员失败:', error)
        uni.showToast({
          title: '保存失败',
          icon: 'none'
        })
      } finally {
        this.saving = false
      }
    },
    
    // 验证参与人员数据
    validateParticipant() {
      if (!this.isEdit && !this.selectedUser) {
        uni.showToast({
          title: '请选择参会人员',
          icon: 'none'
        })
        return false
      }
      return true
    },
    
    // 检查数据完整性
    validateParticipantsData() {
      console.log('验证参与人员数据完整性...')
      const invalidItems = []
      
      this.participants.forEach((participant, index) => {
        if (!participant) {
          invalidItems.push({ index, reason: '对象为空' })
        } else if (!participant.userId) {
          invalidItems.push({ index, reason: '缺少userId', data: participant })
        } else if (!participant.userName) {
          invalidItems.push({ index, reason: '缺少userName', data: participant })
        }
      })
      
      if (invalidItems.length > 0) {
        console.error('发现无效的参与人员数据:', invalidItems)
        // 清理无效数据
        this.participants = this.participants.filter(p => p && p.userId && p.userName)
        uni.showToast({
          title: `已清理${invalidItems.length}条无效数据`,
          icon: 'none'
        })
      }
      
      return invalidItems.length === 0
    },
    
    // 关闭弹窗
    closePopup() {
      this.$refs.participantPopup.close()
      this.resetCurrentParticipant()
    },
    
    // 重置当前参与人员数据
    resetCurrentParticipant() {
      this.currentParticipant = {
        id: null,
        meetingId: this.meetingId,
        userId: null,
        userName: '',
        deptName: '',
        signInStatus: '0',
        attendanceStatus: '0',
        remark: ''
      }
      this.selectedUser = null
      this.selectedUserIndex = 0
      this.currentEditIndex = -1
    },
    
    // 用户选择改变
    onUserChange(e) {
      const index = e.detail.value
      this.selectedUserIndex = index
      this.selectedUser = this.availableUsers[index]
    },
    
    // 签到状态改变
    onSignInStatusChange(e) {
      const index = e.detail.value
      this.currentParticipant.signInStatus = this.signInStatusOptions[index].value
    },
    
    // 出勤状态改变
    onAttendanceStatusChange(e) {
      const index = e.detail.value
      this.currentParticipant.attendanceStatus = this.attendanceStatusOptions[index].value
    },
    
    // 获取用户部门名称
    getUserDeptName(deptId) {
      if (!deptId) return '未设置'
      const dept = this.deptList.find(d => d.deptId === deptId)
      return dept ? dept.deptName : '未设置'
    },
    
    // 获取签到状态标签
    getSignInStatusLabel(value) {
      const option = this.signInStatusOptions.find(o => o.value === value)
      return option ? option.label : '未知'
    },
    
    // 获取出勤状态标签
    getAttendanceStatusLabel(value) {
      const option = this.attendanceStatusOptions.find(o => o.value === value)
      return option ? option.label : '未知'
    },
    
    // 获取签到状态索引
    getSignInStatusIndex(value) {
      return this.signInStatusOptions.findIndex(o => o.value === value)
    },
    
    // 获取出勤状态索引
    getAttendanceStatusIndex(value) {
      return this.attendanceStatusOptions.findIndex(o => o.value === value)
    }
  }
}
</script>

<style lang="scss" scoped>
.participants-container {
  background-color: #f5f5f5;
  min-height: 100vh;
}

.header-actions {
  background: #fff;
  padding: 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #ebeef5;
  
  .meeting-info {
    flex: 1;
    
    .meeting-title {
      display: block;
      font-size: 16px;
      font-weight: 500;
      color: #303133;
      margin-bottom: 4px;
    }
    
    .participant-count {
      font-size: 14px;
      color: #909399;
    }
  }
  
  .add-btn {
    background: #409eff;
    color: #fff;
    border: none;
    border-radius: 6px;
    padding: 8px 16px;
    font-size: 14px;
    display: flex;
    align-items: center;
    gap: 4px;
  }
}

.participants-list {
  padding: 10px;
}

.participant-item {
  background: #fff;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  
  .participant-info {
    flex: 1;
    
    .participant-main {
      margin-bottom: 8px;
      
      .participant-name {
        font-size: 16px;
        font-weight: 500;
        color: #303133;
        margin-right: 10px;
      }
      
      .participant-dept {
        font-size: 14px;
        color: #909399;
      }
      
      .participant-remark {
        display: block;
        font-size: 12px;
        color: #666;
        margin-top: 4px;
        line-height: 1.4;
        word-break: break-all;
      }
    }
    
    .participant-status {
      display: flex;
      align-items: center;
    }
  }
  
  .participant-actions {
    display: flex;
    gap: 10px;
    
    .edit-btn, .delete-btn {
      width: 36px;
      height: 36px;
      border-radius: 50%;
      border: none;
      display: flex;
      align-items: center;
      justify-content: center;
    }
    
    .edit-btn {
      background: #e1f3ff;
    }
    
    .delete-btn {
      background: #fef0f0;
    }
  }
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  
  .empty-text {
    display: block;
    font-size: 16px;
    color: #909399;
    margin: 20px 0;
  }
  
  .add-first-btn {
    background: #409eff;
    color: #fff;
    border: none;
    border-radius: 6px;
    padding: 12px 24px;
    font-size: 14px;
  }
}

.popup-content {
  background: #fff;
  border-radius: 12px 12px 0 0;
  max-height: 80vh;
  
  .popup-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px;
    border-bottom: 1px solid #ebeef5;
    
    .popup-title {
      font-size: 18px;
      font-weight: 500;
      color: #303133;
    }
    
    .close-btn {
      background: none;
      border: none;
      padding: 4px;
    }
  }
  
  .form-content {
    padding: 20px;
    max-height: 50vh;
    overflow-y: auto;
  }
  
  .form-item {
    margin-bottom: 20px;
    
    .form-label {
      display: block;
      font-size: 14px;
      color: #606266;
      margin-bottom: 8px;
    }
    
    .picker-input {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 12px;
      border: 1px solid #dcdfe6;
      border-radius: 6px;
      background: #fff;
      font-size: 14px;
      color: #606266;
    }
    
    .remark-input {
      width: 100%;
      min-height: 80px;
      padding: 12px;
      border: 1px solid #dcdfe6;
      border-radius: 6px;
      font-size: 14px;
      color: #606266;
      resize: none;
    }
  }
  
  .user-info {
    background: #f5f7fa;
    padding: 12px;
    border-radius: 6px;
    margin-bottom: 20px;
    
    .user-name {
      display: block;
      font-size: 16px;
      font-weight: 500;
      color: #303133;
      margin-bottom: 4px;
    }
    
    .user-dept {
      font-size: 14px;
      color: #909399;
    }
  }
  
  .popup-actions {
    display: flex;
    gap: 15px;
    padding: 20px;
    border-top: 1px solid #ebeef5;
    
    .cancel-btn, .confirm-btn {
      flex: 1;
      padding: 12px;
      border-radius: 6px;
      font-size: 16px;
      border: none;
    }
    
    .cancel-btn {
      background: #f5f7fa;
      color: #606266;
    }
    
    .confirm-btn {
      background: #409eff;
      color: #fff;
    }
  }
}
</style>