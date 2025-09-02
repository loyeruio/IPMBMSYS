<template>
  <view class="detail-container">
    <!-- 加载状态 -->
    <uni-load-more v-if="loading" status="loading"></uni-load-more>
    
    <!-- 详情内容 -->
    <view v-else-if="noticeInfo" class="detail-content">
      <!-- 基本信息卡片 -->
      <view class="info-card">
        <view class="card-header">
          <view class="title-section">
            <text class="notice-title">{{ noticeInfo.noticeTitle }}</text>
            <view class="tags-section">
              <DictTag 
                v-if="dict && dict.type && dict.type.sys_notice_type"
                :options="dict.type.sys_notice_type"
                :value="noticeInfo.noticeType"
              />
              <DictTag 
                v-if="dict && dict.type && dict.type.sys_notice_status"
                :options="dict.type.sys_notice_status"
                :value="noticeInfo.status"
              />
              <DictTag 
                v-if="noticeInfo.activityType && dict && dict.type && dict.type.sys_activity_type"
                :options="dict.type.sys_activity_type"
                :value="noticeInfo.activityType"
              />
            </view>
          </view>
        </view>
      </view>
      
      <!-- 详细信息卡片 -->
      <view class="info-card">
        <view class="info-section">
          <view class="info-item">
            <text class="info-label">创建时间</text>
            <text class="info-value">{{ formatDateTime(noticeInfo.createTime) }}</text>
          </view>
          
          <view class="info-item">
            <text class="info-label">创建者</text>
            <text class="info-value">{{ noticeInfo.createBy || '系统' }}</text>
          </view>
          
          <view class="info-item" v-if="noticeInfo.recordTime">
            <text class="info-label">记录时间</text>
            <text class="info-value">{{ formatDateTime(noticeInfo.recordTime) }}</text>
          </view>
          
          <view class="info-item" v-if="noticeInfo.noticeUserIds">
            <text class="info-label">通知对象</text>
            <text class="info-value">{{ formatUserNames(noticeInfo.noticeUserIds) }}</text>
          </view>
          
          <view class="info-item" v-if="noticeInfo.remark">
            <text class="info-label">备注</text>
            <text class="info-value">{{ noticeInfo.remark }}</text>
          </view>
        </view>
      </view>
      
      <!-- 附件信息卡片 -->
      <view class="info-card" v-if="attachmentList.length > 0">
        <view class="card-title">
          <uni-icons type="paperclip" size="16" color="#409eff"></uni-icons>
          <text class="title-text">相关附件</text>
        </view>
        <view class="attachment-list">
          <view 
            v-for="(attachment, index) in attachmentList" 
            :key="index" 
            class="attachment-item"
            @click="downloadAttachment(attachment)"
          >
            <view class="attachment-info">
              <view class="attachment-icon">
                <uni-icons type="paperclip" size="20" color="#409eff"></uni-icons>
              </view>
              <view class="attachment-details">
                <text class="attachment-name">{{ attachment.name || attachment.fileName || '未知文件' }}</text>
                <text class="attachment-size">{{ attachment.size || '未知大小' }}</text>
              </view>
            </view>
            <view class="download-icon">
              <uni-icons type="download" size="16" color="#909399"></uni-icons>
            </view>
          </view>
        </view>
      </view>
      
      <!-- 内容卡片 -->
      <view class="info-card" v-if="noticeInfo.noticeContent">
        <view class="card-title">
          <uni-icons type="list" size="16" color="#409eff"></uni-icons>
          <text class="title-text">通知内容</text>
        </view>
        <view class="content-section">
          <rich-text :nodes="noticeInfo.noticeContent"></rich-text>
        </view>
      </view>
    </view>
    
    <!-- 错误状态 -->
    <view v-else class="error-state">
      <view class="error-icon">
        <uni-icons type="info" size="80" color="#f56c6c"></uni-icons>
      </view>
      <text class="error-text">加载失败</text>
      <text class="error-subtitle">请检查网络连接后重试</text>
      <uni-button class="retry-btn" type="primary" size="mini" @click="loadNoticeDetail">重新加载</uni-button>
    </view>
  </view>
</template>

<script>
import { getNotice, delNotice } from '@/api/system/notice'
import { listAllUser } from '@/api/system/user'
import { AttachmentManager } from '@/utils/attachment'
import DictTag from '@/components/DictTag/index.vue'

export default {
  dicts: ['sys_notice_type', 'sys_notice_status', 'sys_activity_type'],
  components: {
    DictTag
  },
  data() {
    return {
      loading: true,
      noticeId: null,
      noticeInfo: null,
      userList: [],
      attachmentManager: new AttachmentManager()
    }
  },
  
  computed: {
    // 附件列表
    attachmentList() {
      return this.attachmentManager.parseAttachments(this.noticeInfo?.attachMent)
    }
  },
  
  onLoad(options) {
    if (options.id) {
      this.noticeId = options.id
      this.getUserList().then(() => {
        this.loadNoticeDetail()
      })
    } else {
      this.$modal.msgError('缺少通知ID参数')
      uni.navigateBack()
    }
  },
  methods: {
    // 获取用户列表
    async getUserList() {
      try {
        const response = await listAllUser({})
        this.userList = response.data || []
      } catch (error) {
        console.error('获取用户列表失败:', error)
      }
    },
    
    // 加载通知详情
    async loadNoticeDetail() {
      this.loading = true
      try {
        const response = await getNotice(this.noticeId)
        this.noticeInfo = response.data
      } catch (error) {
        console.error('获取通知详情失败:', error)
        this.$modal.msgError('获取通知详情失败')
        this.noticeInfo = null
      } finally {
        this.loading = false
      }
    },
    
    // 编辑通知
    handleEdit() {
      uni.navigateTo({
        url: `/pages/notice/edit?id=${this.noticeId}`
      })
    },
    
    // 删除通知
    handleDelete() {
      uni.showModal({
        title: '确认删除',
        content: `确定要删除通知"${this.noticeInfo.noticeTitle}"吗？`,
        success: async (res) => {
          if (res.confirm) {
            try {
              await delNotice(this.noticeId)
              this.$modal.msgSuccess('删除成功')
              uni.navigateBack()
            } catch (error) {
              console.error('删除失败:', error)
              this.$modal.msgError('删除失败，请重试')
            }
          }
        }
      })
    },
    
    // 格式化日期时间
    formatDateTime(dateTime) {
      if (!dateTime) return ''
      const date = new Date(dateTime)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}`
    },
    
    // 格式化用户名称
    formatUserNames(userIds) {
      if (!userIds) return '全部用户'
      const ids = userIds.split(',').filter(id => id)
      const userNames = ids.map(id => {
        const user = this.userList.find(u => u.userId == id)
        return user ? user.userName : `用户${id}`
      })
      return userNames.join('、')
    },
    
    // 下载附件
    downloadAttachment(attachment) {
      this.attachmentManager.downloadAttachment(attachment)
    }
  }
}
</script>

<style lang="scss" scoped>
.detail-container {
  background: #f8f9fa;
  min-height: 100vh;
  padding: 15px;
  padding-bottom: 80px;
}

.detail-content {
  .info-card {
    background: #fff;
    border-radius: 12px;
    margin-bottom: 15px;
    overflow: hidden;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
    
    .card-header {
      padding: 20px;
      border-bottom: 1px solid #f0f0f0;
      
      .title-section {
        .notice-title {
          display: block;
          font-size: 18px;
          font-weight: 600;
          color: #303133;
          margin-bottom: 12px;
          line-height: 1.4;
        }
        
        .tags-section {
          display: flex;
          gap: 8px;
          flex-wrap: wrap;
        }
      }
    }
    
    .card-title {
      display: flex;
      align-items: center;
      padding: 15px 20px;
      border-bottom: 1px solid #f0f0f0;
      background: #fafbfc;
      
      .title-text {
        margin-left: 8px;
        font-size: 14px;
        font-weight: 500;
        color: #303133;
      }
    }
    
    .info-section {
      padding: 20px;
      
      .info-item {
        display: flex;
        margin-bottom: 16px;
        
        &:last-child {
          margin-bottom: 0;
        }
        
        .info-label {
          width: 80px;
          font-size: 14px;
          color: #909399;
          flex-shrink: 0;
        }
        
        .info-value {
          flex: 1;
          font-size: 14px;
          color: #303133;
          line-height: 1.5;
        }
      }
    }
    
    .content-section {
      padding: 20px;
      
      :deep(rich-text) {
        font-size: 14px;
        line-height: 1.6;
        color: #303133;
      }
    }
    
    .attachment-list {
      padding: 15px 20px;
      
      .attachment-item {
        display: flex;
        align-items: center;
        padding: 12px 0;
        border-bottom: 1px solid #f0f0f0;
        cursor: pointer;
        transition: background-color 0.3s ease;
        
        &:last-child {
          border-bottom: none;
        }
        
        &:hover {
          background-color: #f8f9fa;
        }
        
        .attachment-info {
          display: flex;
          align-items: center;
          flex: 1;
          
          .attachment-icon {
            margin-right: 12px;
          }
          
          .attachment-details {
            display: flex;
            flex-direction: column;
            
            .attachment-name {
              font-size: 14px;
              color: #303133;
              margin-bottom: 4px;
            }
            
            .attachment-size {
              font-size: 12px;
              color: #909399;
            }
          }
        }
        
        .download-icon {
          margin-left: 8px;
        }
      }
    }
  }
}

.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  
  .error-icon {
    margin-bottom: 16px;
    opacity: 0.5;
  }
  
  .error-text {
    font-size: 16px;
    color: #909399;
    margin-bottom: 8px;
  }
  
  .error-subtitle {
    font-size: 14px;
    color: #c0c4cc;
    margin-bottom: 20px;
  }
  
  .retry-btn {
    width: 120px;
  }
}

.action-buttons {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 15px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  gap: 15px;
  
  .action-btn {
    flex: 1;
    height: 44px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 16px;
    font-weight: 500;
    
    &.edit-btn {
      background: #409eff;
      color: #fff;
      border: none;
    }
    
    &.delete-btn {
      background: #f56c6c;
      color: #fff;
      border: none;
    }
  }
}
</style>