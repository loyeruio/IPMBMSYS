<template>
  <div>
    <!-- 发布通知对话框 -->
    <el-dialog title="发布通知" :visible.sync="notificationDialogVisible" width="500px" append-to-body>
      <el-form ref="notificationForm" :model="notificationForm" :rules="notificationRules" label-width="100px">
        <el-form-item label="通知标题" prop="title">
          <el-input v-model="notificationForm.title" placeholder="请输入通知标题"></el-input>
        </el-form-item>
        <el-form-item label="通知内容" prop="content">
          <el-input
            v-model="notificationForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入通知内容"
          >
          </el-input>
        </el-form-item>
        <el-form-item label="发布对象">
          <div class="participants-list">
            <el-tag v-for="(p, index) in participants" :key="index" size="small" style="margin-right: 5px; margin-bottom: 5px;">
              {{ p.userName }}
            </el-tag>
          </div>
        </el-form-item>
        <el-form-item label="活动时间">
          <span>{{ activityTimeDisplay }}</span>
        </el-form-item>
        <!-- 添加附件展示 -->
        <el-form-item label="相关附件" v-if="activityAttachments && getAttachmentList(activityAttachments).length > 0">
          <div class="attachment-list">
            <el-tag 
              v-for="(attachment, index) in getAttachmentList(activityAttachments)" 
              :key="index" 
              size="small" 
              type="info"
              style="margin-right: 5px; margin-bottom: 5px;"
            >
              <i class="el-icon-paperclip" style="margin-right: 3px;"></i>
              {{ attachment.name || attachment.fileName || '未知文件' }}
            </el-tag>
          </div>
        </el-form-item>
        <el-form-item v-else-if="!activityAttachments || getAttachmentList(activityAttachments).length === 0" label="相关附件">
          <span class="text-muted">无附件</span>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="notificationDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitNotification">发 布</el-button>
      </div>
    </el-dialog>
    
    <!-- 发布签到对话框 -->
    <el-dialog title="发布签到" :visible.sync="signInDialogVisible" width="500px" append-to-body>
      <el-form ref="signInForm" :model="signInForm" :rules="signInRules" label-width="120px">
        <el-form-item label="签到截止时间" prop="deadline">
          <el-date-picker
            v-model="signInForm.deadline"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="选择签到截止时间"
            style="width: 100%;"
          >
          </el-date-picker>
        </el-form-item>
        <el-form-item label="发布对象">
          <div class="participants-list">
            <el-tag v-for="(p, index) in participants" :key="index" size="small" style="margin-right: 5px; margin-bottom: 5px;">
              {{ p.userName }}
            </el-tag>
          </div>
        </el-form-item>
        <el-form-item label="活动时间">
          <span>{{ activityTimeDisplay }}</span>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="signInDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitSignIn">发 布</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { addNotice } from "@/api/system/notice";

export default {
  name: "ActivityNotification",
  props: {
    // 活动类型：meeting, class, learning
    activityType: {
      type: String,
      required: true
    },
    // 活动ID
    activityId: {
      type: Number,
      default: 0
    },
    // 活动标题
    activityTitle: {
      type: String,
      required: true
    },
    // 参与人员列表
    participants: {
      type: Array,
      required: true
    },
    // 活动开始时间
    activityStartTime: {
      type: String,
      default: null
    },
    // 活动结束时间
    activityEndTime: {
      type: String,
      default: null
    },
    // 添加附件信息prop
    activityAttachments: {
      type: String,
      default: null
    }
  },
  data() {
    return {
      // 通知对话框
      notificationDialogVisible: false,
      notificationForm: {
        title: "",
        content: ""
      },
      notificationRules: {
        title: [
          { required: true, message: "请输入通知标题", trigger: "blur" },
          { min: 1, max: 50, message: "长度在 1 到 50 个字符", trigger: "blur" }
        ],
        content: [
          { required: true, message: "请输入通知内容", trigger: "blur" }
        ]
      },
      
      // 签到对话框
      signInDialogVisible: false,
      signInForm: {
        deadline: null
      },
      signInRules: {
        deadline: [
          { required: true, message: "请选择签到截止时间", trigger: "change" }
        ]
      }
    };
  },
  computed: {
    // 格式化活动时间显示
    activityTimeDisplay() {
      if (this.activityStartTime && this.activityEndTime) {
        return `${this.activityStartTime} 至 ${this.activityEndTime}`;
      } else if (this.activityStartTime) {
        return this.activityStartTime;
      } else {
        return "未设置";
      }
    }
  },
  methods: {
    // 打开通知对话框
    openNotificationDialog() {
      // 先关闭对话框确保状态重置
      this.notificationDialogVisible = false;
      
      // 重置表单数据
      this.notificationForm = {
        title: `关于${this.activityTitle}的通知`,
        content: ""
      };
      
      // 使用nextTick确保DOM更新后再打开对话框
      this.$nextTick(() => {
        this.notificationDialogVisible = true;
        // 重置表单验证状态
        this.$nextTick(() => {
          if (this.$refs.notificationForm) {
            this.$refs.notificationForm.clearValidate();
          }
        });
      });
    },
    
    // 打开签到对话框
    openSignInDialog() {
      // 设置截止时间为当前时间后2小时，使用正确的字符串格式
      const now = new Date();
      now.setHours(now.getHours() + 2);
      // 格式化为 yyyy-MM-dd HH:mm:ss 格式
      const year = now.getFullYear();
      const month = String(now.getMonth() + 1).padStart(2, '0');
      const day = String(now.getDate()).padStart(2, '0');
      const hours = String(now.getHours()).padStart(2, '0');
      const minutes = String(now.getMinutes()).padStart(2, '0');
      const seconds = String(now.getSeconds()).padStart(2, '0');
      
      this.signInForm.deadline = `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
      this.signInDialogVisible = true;
    },
    
    // 提交通知
    // 提交通知
    submitNotification() {
      this.$refs.notificationForm.validate(valid => {
        if (valid) {
          const userIds = this.participants.map(p => p.userId);
          if (userIds.length === 0) {
            this.$modal.msgError("没有参与人员，无法发送通知");
            return;
          }
          
          // 构建通知数据
          const noticeData = {
            noticeTitle: this.notificationForm.title,
            noticeType: "1", // 1-通知
            noticeContent: this.notificationForm.content,
            status: "0", // 0-正常
            noticeUserIds: userIds.join(","), // 使用新字段存储用户ID列表
            // 通知类型保存活动时间到record_time字段，确保格式正确
            recordTime: this.activityStartTime || null,
            // 添加附件信息
            attachMent: this.activityAttachments || null,
            // 使用新字段存储活动信息
            activityType: this.activityType,
            activityId: this.activityId ? this.activityId.toString() : null
          };
          
          // 调用添加通知API
          addNotice(noticeData).then(response => {
            this.$modal.msgSuccess("通知发布成功");
            this.notificationDialogVisible = false;
            // 重置表单
            this.notificationForm = {
              title: "",
              content: ""
            };
          }).catch(error => {
            console.error("发布通知失败:", error);
          });
        }
      });
    },
    
    // 提交签到
    submitSignIn() {
      this.$refs.signInForm.validate(valid => {
        if (valid) {
          const userIds = this.participants.map(p => p.userId);
          if (userIds.length === 0) {
            this.$modal.msgError("没有参与人员，无法发布签到");
            return;
          }
          
          // 构建签到通知数据
          const noticeData = {
            noticeTitle: `${this.activityTitle}签到`,
            noticeType: "3", // 3-签到
            noticeContent: `请在${this.parseTime(this.signInForm.deadline, '{y}-{m}-{d} {h}:{i}')}前完成签到`,
            status: "0", // 0-正常
            noticeUserIds: userIds.join(","), // 使用新字段存储用户ID列表
            // 签到类型保存deadline到record_time字段
            recordTime: this.signInForm.deadline,
            // 使用新字段存储活动信息
            activityType: this.activityType,
            activityId: this.activityId ? this.activityId.toString() : null
          };
          
          // 调用添加通知API
          addNotice(noticeData).then(response => {
            this.$modal.msgSuccess("签到发布成功");
            this.signInDialogVisible = false;
            // 重置表单
            this.signInForm = {
              deadline: null
            };
          }).catch(error => {
            console.error("发布签到失败:", error);
          });
        }
      });
    },
    // 添加获取附件列表的方法
    getAttachmentList(attachments) {
      if (!attachments) return [];
      try {
        return JSON.parse(attachments);
      } catch (e) {
        return [];
      }
    }
  }
};
</script>

<style scoped>
.participants-list {
  max-height: 100px;
  overflow-y: auto;
  padding: 5px;
  border: 1px solid #EBEEF5;
  border-radius: 4px;
}

.attachment-list {
  max-height: 80px;
  overflow-y: auto;
  padding: 5px;
  border: 1px solid #EBEEF5;
  border-radius: 4px;
  background-color: #fafafa;
}

.text-muted {
  color: #909399;
  font-style: italic;
}
</style>