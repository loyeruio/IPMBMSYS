<template>
  <view class="participants-container">
    <!-- 顶部操作栏 -->
    <view class="header-actions">
      <view class="learning-info">
        <text class="learning-title">{{ learningDetail.title }}</text>
        <text class="participant-count">参加人员 ({{ participants.length }})</text>
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
        v-if="participant && participant.studentId && participant.studentName"
      >
        <view class="participant-content">
          <!-- 基本信息区域 -->
          <view class="participant-header">
            <view class="participant-basic">
              <text class="participant-name">{{ participant.studentName }}</text>
              <text class="participant-dept">{{ participant.deptName }}</text>
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
          
          <!-- 积分信息区域 -->
          <view class="participant-scores">
            <view class="scores-grid">
              <view class="score-item">
                <text class="score-label">总积分</text>
                <text class="score-value primary">{{ participant.totalScore || 0 }}</text>
              </view>
              <view class="score-item">
                <text class="score-label">表现积分</text>
                <text class="score-value" :class="participant.performanceScore > 0 ? 'positive' : participant.performanceScore < 0 ? 'negative' : ''">
                  {{ participant.performanceScore > 0 ? '+' : '' }}{{ participant.performanceScore || 0 }}
                </text>
              </view>
            </view>
            <view class="xuexiqiangguo-score">
              <text class="score-label">学习强国</text>
              <view class="score-progress">
                <text class="score-prev">{{ participant.xuexiqiangguoPrev || 0 }}</text>
                <uni-icons type="arrowright" size="12" color="#c0c4cc"></uni-icons>
                <text class="score-curr">{{ participant.xuexiqiangguoCurr || 0 }}</text>
                <text class="score-diff" :class="participant.xuexiqiangguoDiff > 0 ? 'positive' : participant.xuexiqiangguoDiff < 0 ? 'negative' : ''">
                  ({{ participant.xuexiqiangguoDiff > 0 ? '+' : '' }}{{ participant.xuexiqiangguoDiff || 0 }})
                </text>
              </view>
            </view>
          </view>
          
          <!-- 学习截图和备注区域 -->
          <view class="participant-extra" v-if="participant.xuexiqiangguoImage || participant.remark">
            <view v-if="participant.xuexiqiangguoImage" class="learning-screenshot">
              <text class="screenshot-label">学习截图</text>
              <view class="screenshot-container" @click="previewImage(participant.xuexiqiangguoImage)">
                <image 
                  :src="getFullImageUrl(participant.xuexiqiangguoImage)" 
                  mode="aspectFill"
                  class="screenshot-image"
                ></image>
                <view class="screenshot-mask">
                  <uni-icons type="eye" size="16" color="#fff"></uni-icons>
                </view>
              </view>
            </view>
            <view v-if="participant.remark" class="participant-remark">
              <text class="remark-label">备注</text>
              <text class="remark-content">{{ participant.remark }}</text>
            </view>
          </view>
        </view>
        
        <!-- 操作按钮 -->
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
      <text class="empty-text">暂无参加人员</text>
      <button class="add-first-btn" @click="showAddParticipant">添加第一个参加人员</button>
    </view>

    <!-- 添加/编辑参与人员弹窗 -->
    <uni-popup ref="participantPopup" type="bottom">
      <view class="popup-content">
        <view class="popup-header">
          <text class="popup-title">{{ isEdit ? '编辑参加人员' : '添加参加人员' }}</text>
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
          
          <!-- 学习强国积分 -->
          <view class="form-item">
            <text class="form-label">学习强国积分（前）</text>
            <input 
              v-model.number="currentParticipant.xuexiqiangguoPrev" 
              type="number"
              placeholder="请输入学习前积分"
              class="score-input"
            />
          </view>
          
          <view class="form-item">
            <text class="form-label">学习强国积分（后）</text>
            <input 
              v-model.number="currentParticipant.xuexiqiangguoCurr" 
              type="number"
              placeholder="请输入学习后积分"
              class="score-input"
            />
          </view>
          
          <!-- 表现积分 -->
          <view class="form-item">
            <text class="form-label">本次表现积分</text>
            <input 
              v-model.number="currentParticipant.performanceScore" 
              type="number"
              placeholder="请输入表现积分"
              class="score-input"
            />
          </view>
          
          <!-- 总积分 -->
          <view class="form-item">
            <text class="form-label">总积分</text>
            <input 
              v-model.number="currentParticipant.totalScore" 
              type="number"
              placeholder="请输入总积分"
              class="score-input"
              :min="0"
            />
          </view>
          <!-- 学习截图上传 -->
          <view class="form-item">
            <text class="form-label">学习截图</text>
            <view class="image-upload-container">
              <view v-if="currentParticipant.xuexiqiangguoImage" class="uploaded-image-preview">
                <image 
                  :src="getFullImageUrl(currentParticipant.xuexiqiangguoImage)" 
                  mode="aspectFill"
                  class="preview-image"
                  @click="previewImage(currentParticipant.xuexiqiangguoImage)"
                ></image>
                <view class="image-actions">
                  <view class="action-btn delete-btn" @click="deleteImage">
                    <uni-icons type="trash" size="12" color="#f56c6c"></uni-icons>
                  </view>
                </view>
              </view>
              <view v-else class="upload-placeholder" @click="chooseImage">
                <uni-icons type="camera" size="24" color="#c0c4cc"></uni-icons>
                <text class="upload-text">点击上传学习截图</text>
              </view>
            </view>
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
import { getLearning } from '@/api/learning/learning'
import { listParticipant, addParticipant, updateParticipant, delParticipant } from '@/api/participant/learningparticipant'
import { listAllUser } from '@/api/system/user'
import { listDept } from '@/api/system/dept'
import DictTag from '@/components/DictTag/index.vue'
import upload from '@/utils/upload'
import config from '@/config'
export default {
  dicts: ['check_status', 'attendance_status'],
  components: {
    DictTag
  },
  data() {
    return {
      learningId: null,
      learningDetail: {},
      participants: [],
      userList: [],
      deptList: [],
      
      // 弹窗相关
      isEdit: false,
      currentParticipant: {
        id: null,
        learningId: null,
        studentId: null,
        studentName: '',
        deptName: '',
        signInStatus: '0',
        attendanceStatus: '0',
        xuexiqiangguoPrev: 0,
        xuexiqiangguoCurr: 0,
        performanceScore: 0,
        totalScore: 0,
        remark: ''
      },
      selectedUser: null,
      selectedUserIndex: 0,
      currentEditIndex: -1,
      saving: false,
      baseUrl: config.baseUrl,
      uploadUrl: '/common/upload'
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
      const participantUserIds = this.participants
        .filter(p => p && p.studentId)
        .map(p => p.studentId)
      return this.userList.filter(user => user && user.userId && !participantUserIds.includes(user.userId))
    }
  },
  
  onLoad(options) {
    if (options.id) {
      this.learningId = options.id
      this.init()
    }
  },
  
  methods: {
    // 初始化
    async init() {
      try {
        // 并行加载基础数据
        await Promise.all([
          this.loadLearningDetail(),
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
    
    // 加载半月学详情
    async loadLearningDetail() {
      try {
        const response = await getLearning(this.learningId)
        this.learningDetail = response.data
      } catch (error) {
        console.error('加载半月学详情失败:', error)
        uni.showToast({
          title: '加载半月学详情失败',
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
    async loadParticipants() {
      try {
        const response = await listParticipant({ learningId: this.learningId })
        this.participants = response.rows || []
        
        // 映射用户名和部门名（参考 detail.vue 的实现）
        this.participants.forEach(participant => {
          // 使用 studentId 而不是 userId
          const user = this.userList.find(u => u.userId === participant.studentId)
          if (user) {
            participant.studentName = user.userName
            const dept = this.deptList.find(d => d.deptId === user.deptId)
            participant.deptName = dept ? dept.deptName : '未知部门'
          } else {
            participant.studentName = '未知用户'
            participant.deptName = '未知部门'
          }
          
          // 计算学习强国积分差值
          participant.xuexiqiangguoDiff = (participant.xuexiqiangguoCurr || 0) - (participant.xuexiqiangguoPrev || 0)
        })
        
        // 过滤无效数据
        this.participants = this.participants.filter(participant => {
          if (!participant) {
            console.warn('发现空的participant对象')
            return false
          }
          if (!participant.studentId) {
            console.warn('participant缺少studentId:', participant)
            return false
          }
          if (!participant.studentName) {
            console.warn('participant缺少studentName:', participant)
            return false
          }
          return true
        })
        
        // 确保每个participant都有id属性
        this.participants = this.participants.map(participant => {
          return {
            ...participant,
            id: participant.id || `temp-${Date.now()}-${Math.random()}`,
            // 计算学习强国积分差值
            xuexiqiangguoDiff: (participant.xuexiqiangguoCurr || 0) - (participant.xuexiqiangguoPrev || 0)
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
      const invalidItems = this.participants.filter(p => !p || !p.studentId || !p.studentName)
      if (invalidItems.length > 0) {
        console.error('发现无效的参与人员数据:', invalidItems)
        // 清理无效数据
        this.participants = this.participants.filter(p => p && p.studentId && p.studentName)
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
    
    // 编辑参与人员
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
      
      if (!participant.studentId) {
        console.error('参与人员缺少studentId:', participant)
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
      this.selectedUser = this.userList.find(u => u && u.userId === participant.studentId) || null
      
      if (!this.selectedUser) {
        console.warn('未找到对应的用户信息:', participant.studentId)
        // 创建一个临时用户对象用于显示
        this.selectedUser = {
          userId: participant.studentId,
          userName: participant.studentName,
          deptId: null
        }
      }
      
      this.selectedUserIndex = this.availableUsers.findIndex(u => u && u.userId === participant.studentId)
      if (this.selectedUserIndex === -1) {
        this.selectedUserIndex = 0
      }
      
      this.$refs.participantPopup.open()
    },
    
    // 删除参与人员
    async deleteParticipant(participant, index) {
      try {
        await uni.showModal({
          title: '确认删除',
          content: `确定要删除参加人员 "${participant.studentName}" 吗？`
        })
        
        if (participant.id && !participant.id.toString().startsWith('temp-')) {
          await delParticipant(participant.id)
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
    
    // 重置当前参与人员
    resetCurrentParticipant() {
      this.currentParticipant = {
        id: null,
        learningId: this.learningId,
        studentId: null,
        studentName: '',
        deptName: '',
        signInStatus: '0',
        attendanceStatus: '0',
        xuexiqiangguoPrev: 0,
        xuexiqiangguoCurr: 0,
        performanceScore: 0,
        remark: ''
      }
      this.selectedUser = null
      this.selectedUserIndex = 0
      this.currentEditIndex = -1
    },
    
    // 用户选择变化
    onUserChange(e) {
      const index = e.detail.value
      this.selectedUserIndex = index
      this.selectedUser = this.availableUsers[index]
      
      if (this.selectedUser) {
        this.currentParticipant.studentId = this.selectedUser.userId
        this.currentParticipant.studentName = this.selectedUser.userName
        this.currentParticipant.deptName = this.getUserDeptName(this.selectedUser.deptId)
      }
    },
    
    // 签到状态变化
    onSignInStatusChange(e) {
      const index = e.detail.value
      this.currentParticipant.signInStatus = this.signInStatusOptions[index].value
    },
    
    // 出勤状态变化
    onAttendanceStatusChange(e) {
      const index = e.detail.value
      this.currentParticipant.attendanceStatus = this.attendanceStatusOptions[index].value
    },
    
    // 保存参与人员
    async saveParticipant() {
      if (!this.selectedUser) {
        uni.showToast({
          title: '请选择人员',
          icon: 'none'
        })
        return
      }
      
      this.saving = true
      
      try {
        const participantData = {
          ...this.currentParticipant,
          learningId: this.learningId,
          studentId: this.selectedUser.userId,
          studentName: this.selectedUser.userName,
          deptName: this.getUserDeptName(this.selectedUser.deptId),
          // 计算学习强国积分差值
          xuexiqiangguoDiff: (this.currentParticipant.xuexiqiangguoCurr || 0) - (this.currentParticipant.xuexiqiangguoPrev || 0)
        }
        
        if (this.isEdit) {
          // 更新
          if (participantData.id && !participantData.id.toString().startsWith('temp-')) {
            await updateParticipant(participantData)
          }
          this.participants[this.currentEditIndex] = participantData
        } else {
          // 新增
          const response = await addParticipant(participantData)
          participantData.id = response.data?.id || `temp-${Date.now()}-${Math.random()}`
          this.participants.push(participantData)
        }
        
        uni.showToast({
          title: this.isEdit ? '更新成功' : '添加成功',
          icon: 'success'
        })
        
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
    
    // 关闭弹窗
    closePopup() {
      this.$refs.participantPopup.close()
      this.resetCurrentParticipant()
    },
    
    // 获取用户部门名称
    getUserDeptName(deptId) {
      if (!deptId || !this.deptList) return ''
      const dept = this.deptList.find(d => d.deptId === deptId)
      return dept ? dept.deptName : ''
    },
    
    // 获取签到状态标签
    getSignInStatusLabel(value) {
      const option = this.signInStatusOptions.find(o => o.value === value)
      return option ? option.label : '未签到'
    },
    
    // 获取出勤状态标签
    getAttendanceStatusLabel(value) {
      const option = this.attendanceStatusOptions.find(o => o.value === value)
      return option ? option.label : '正常'
    },
    
    // 获取签到状态索引
    getSignInStatusIndex(value) {
      return this.signInStatusOptions.findIndex(o => o.value === value)
    },
    
    // 获取出勤状态索引
    getAttendanceStatusIndex(value) {
      return this.attendanceStatusOptions.findIndex(o => o.value === value)
    },
    // 获取完整图片URL
    getFullImageUrl(imagePath) {
      if (!imagePath) return ''
      if (imagePath.indexOf('http') === 0) {
        return imagePath
      }
      return this.baseUrl + imagePath
    },
    
    // 预览图片
    previewImage(imagePath) {
      if (!imagePath) return
      uni.previewImage({
        urls: [this.getFullImageUrl(imagePath)],
        current: 0
      })
    },
    
    // 选择图片
    chooseImage() {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['camera', 'album'],
        success: (res) => {
          this.uploadImage(res.tempFilePaths[0])
        },
        fail: (err) => {
          uni.showToast({
            title: '选择图片失败',
            icon: 'none'
          })
        }
      })
    },
    
    // 上传图片
    async uploadImage(filePath) {
      uni.showLoading({
        title: '上传中...'
      })
      
      try {
        const response = await upload({
          url: this.uploadUrl,
          filePath: filePath,
          name: 'file'
        })
        
        if (response.code === 200) {
          this.currentParticipant.xuexiqiangguoImage = response.data || response.fileName
          uni.showToast({
            title: '上传成功',
            icon: 'success'
          })
        } else {
          throw new Error(response.msg || '上传失败')
        }
      } catch (error) {
        uni.showToast({
          title: error.message || '上传失败',
          icon: 'none'
        })
      } finally {
        uni.hideLoading()
      }
    },
    
    // 删除图片
    deleteImage() {
      uni.showModal({
        title: '确认删除',
        content: '确定要删除这张学习截图吗？',
        success: (res) => {
          if (res.confirm) {
            this.currentParticipant.xuexiqiangguoImage = ''
            uni.showToast({
              title: '删除成功',
              icon: 'success'
            })
          }
        }
      })
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
  
  .learning-info {
    flex: 1;
    
    .learning-title {
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
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  display: flex;
  align-items: flex-start;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0f0f0;
  
  .participant-content {
    flex: 1;
    
    .participant-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 12px;
      
      .participant-basic {
        flex: 1;
        
        .participant-name {
          display: block;
          font-size: 16px;
          font-weight: 600;
          color: #303133;
          margin-bottom: 4px;
          line-height: 1.4;
        }
        
        .participant-dept {
          font-size: 13px;
          color: #909399;
          line-height: 1.3;
        }
      }
      
      .participant-status {
        display: flex;
        align-items: center;
        flex-shrink: 0;
      }
    }
    
    .participant-scores {
      background: #f8fafe;
      border-radius: 8px;
      padding: 12px;
      margin-bottom: 12px;
      border: 1px solid #e8f4ff;
      
      .scores-grid {
        display: flex;
        gap: 16px;
        margin-bottom: 8px;
        
        .score-item {
          flex: 1;
          text-align: center;
          
          .score-label {
            display: block;
            font-size: 11px;
            color: #909399;
            margin-bottom: 4px;
            font-weight: 500;
          }
          
          .score-value {
            display: block;
            font-size: 16px;
            font-weight: 600;
            line-height: 1.2;
            
            &.primary {
              color: #409eff;
            }
            
            &.positive {
              color: #67c23a;
            }
            
            &.negative {
              color: #f56c6c;
            }
          }
        }
      }
      
      .xuexiqiangguo-score {
        border-top: 1px solid #e8f4ff;
        padding-top: 8px;
        
        .score-label {
          font-size: 11px;
          color: #909399;
          margin-bottom: 6px;
          display: block;
          font-weight: 500;
        }
        
        .score-progress {
          display: flex;
          align-items: center;
          justify-content: center;
          gap: 6px;
          
          .score-prev, .score-curr {
            font-size: 14px;
            font-weight: 600;
            color: #606266;
          }
          
          .score-curr {
            color: #409eff;
          }
          
          .score-diff {
            font-size: 12px;
            font-weight: 500;
            
            &.positive {
              color: #67c23a;
            }
            
            &.negative {
              color: #f56c6c;
            }
          }
        }
      }
    }
    
    .participant-extra {
      display: flex;
      gap: 12px;
      align-items: flex-start;
      
      .learning-screenshot {
        flex-shrink: 0;
        
        .screenshot-label {
          font-size: 11px;
          color: #909399;
          margin-bottom: 6px;
          display: block;
          font-weight: 500;
        }
        
        .screenshot-container {
          position: relative;
          width: 50px;
          height: 50px;
          border-radius: 6px;
          overflow: hidden;
          border: 1px solid #e4e7ed;
          
          .screenshot-image {
            width: 100%;
            height: 100%;
          }
          
          .screenshot-mask {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(0, 0, 0, 0.5);
            display: flex;
            align-items: center;
            justify-content: center;
            opacity: 0;
            transition: opacity 0.3s;
          }
          
          &:active .screenshot-mask {
            opacity: 1;
          }
        }
      }
      
      .participant-remark {
        flex: 1;
        
        .remark-label {
          font-size: 11px;
          color: #909399;
          margin-bottom: 4px;
          display: block;
          font-weight: 500;
        }
        
        .remark-content {
          font-size: 12px;
          color: #606266;
          line-height: 1.5;
          word-break: break-all;
          background: #f5f7fa;
          padding: 8px;
          border-radius: 4px;
          display: block;
        }
      }
    }
  }
  
  .participant-actions {
    display: flex;
    flex-direction: column;
    gap: 8px;
    margin-left: 12px;
    flex-shrink: 0;
    
    .edit-btn, .delete-btn {
      width: 32px;
      height: 32px;
      border-radius: 6px;
      border: none;
      display: flex;
      align-items: center;
      justify-content: center;
      transition: all 0.2s;
    }
    
    .edit-btn {
      background: #e1f3ff;
      
      &:active {
        background: #d1edff;
        transform: scale(0.95);
      }
    }
    
    .delete-btn {
      background: #fef0f0;
      
      &:active {
        background: #fde2e2;
        transform: scale(0.95);
      }
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
    
    .score-input {
      width: 100%;
      padding-left: 12px;
      border: 1px solid #dcdfe6;
      border-radius: 6px;
      font-size: 14px;
      color: #606266;
      box-sizing: border-box;
      height: 44px;
      line-height: 20px;
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

/* 学习截图样式 */

/* 图片上传样式 */
.image-upload-container {
  margin-top: 8px;
}

.uploaded-image-preview {
  position: relative;
  width: 100px;
  height: 100px;
  border-radius: 4px;
  overflow: hidden;
  border: 1px solid #e4e7ed;
}

.preview-image {
  width: 100%;
  height: 100%;
}

.image-actions {
  position: absolute;
  top: 4px;
  right: 4px;
  display: flex;
  gap: 4px;
}

.action-btn {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0;
}

.preview-btn {
  background: rgba(64, 158, 255, 0.8);
}

.delete-btn {
  background: rgba(245, 108, 108, 0.8);
}

.upload-placeholder {
  width: 100px;
  height: 100px;
  border: 2px dashed #c0c4cc;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #fafafa;
}

.upload-text {
  font-size: 12px;
  color: #c0c4cc;
  margin-top: 4px;
}
</style>
