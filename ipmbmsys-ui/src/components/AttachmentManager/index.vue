<template>
  <div class="attachment-manager">
    <!-- 附件列表 -->
    <div class="attachments-list" v-if="attachmentList.length > 0">
      <div 
        class="attachment-item" 
        v-for="(attachment, index) in attachmentList" 
        :key="index"
      >
        <div class="attachment-info">
          <i class="el-icon-document"></i>
          <div class="file-details">
            <div class="file-name" :title="attachment.name">{{ attachment.name }}</div>
            <div class="file-size">{{ attachment.size }}</div>
          </div>
        </div>
        <div class="attachment-actions">
          <el-button 
            type="text" 
            size="mini" 
            @click="downloadAttachment(attachment)"
            icon="el-icon-download"
          >
            下载
          </el-button>
          <el-button 
            v-if="!readonly"
            type="text" 
            size="mini" 
            @click="deleteAttachment(index)"
            icon="el-icon-delete"
            style="color: #f56c6c;"
          >
            删除
          </el-button>
        </div>
      </div>
    </div>
    
    <!-- 空状态 -->
    <div class="empty-state" v-else>
      <i class="el-icon-document"></i>
      <p>暂无附件</p>
    </div>
    
    <!-- 上传按钮 -->
    <div class="upload-section" v-if="!readonly">
      <el-button 
        type="primary" 
        size="mini" 
        @click="openUploadDialog"
        icon="el-icon-upload"
      >
        上传附件
      </el-button>
    </div>
    
    <!-- 上传对话框 -->
    <el-dialog 
      title="上传附件" 
      :visible.sync="uploadDialogVisible" 
      width="500px"
      :close-on-click-modal="false"
    >
      <el-upload
        ref="upload"
        :action="uploadUrl"
        :headers="uploadHeaders"
        :before-upload="beforeUpload"
        :on-success="handleUploadSuccess"
        :on-error="handleUploadError"
        :on-remove="handleRemoveFile"
        :file-list="fileList"
        :auto-upload="false"
        multiple
        drag
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">
          支持 {{ allowedTypes.join('、') }} 格式，单个文件不超过 {{ maxSize }}MB
        </div>
      </el-upload>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="uploadDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitUpload">确定上传</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { AttachmentManager } from '@/utils/attachment';
import { getToken } from '@/utils/auth';

export default {
  name: 'AttachmentManager',
  
  props: {
    // 附件数据
    value: {
      type: String,
      default: ''
    },
    
    // 是否只读
    readonly: {
      type: Boolean,
      default: false
    },
    
    // 最大文件大小（MB）
    maxSize: {
      type: Number,
      default: 10
    },
    
    // 允许的文件类型
    allowedTypes: {
      type: Array,
      default: () => ['pdf', 'doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx', 'jpg', 'jpeg', 'png', 'txt']
    }
  },
  
  data() {
    return {
      attachmentManager: new AttachmentManager({
        maxFileSize: this.maxSize,
        allowedTypes: this.allowedTypes
      }),
      uploadDialogVisible: false,
      fileList: []
    };
  },
  
  computed: {
    attachmentList() {
      return this.attachmentManager.parseAttachments(this.value);
    },
    
    uploadUrl() {
      return process.env.VUE_APP_BASE_API + "/common/upload";
    },
    
    uploadHeaders() {
      return {
        Authorization: "Bearer " + getToken()
      };
    }
  },
  
  methods: {
    openUploadDialog() {
      this.fileList = [];
      this.uploadDialogVisible = true;
      
      this.$nextTick(() => {
        if (this.$refs.upload) {
          this.$refs.upload.clearFiles();
        }
      });
    },
    
    beforeUpload(file) {
      const validation = this.attachmentManager.validateFile(file);
      if (!validation.valid) {
        this.$modal.msgError(validation.message);
        return false;
      }
      return true;
    },
    
    handleUploadSuccess(response, file, fileList) {
      if (response.code === 200) {
        const newAttachment = this.attachmentManager.createAttachmentObject(response, file);
        const currentAttachments = this.attachmentManager.parseAttachments(this.value);
        currentAttachments.push(newAttachment);
        const updatedAttachments = this.attachmentManager.serializeAttachments(currentAttachments);
        
        this.$emit('input', updatedAttachments);
        this.$emit('change', updatedAttachments);
        this.$modal.msgSuccess("上传成功");
      } else {
        this.$modal.msgError(response.msg || "上传失败");
      }
    },
    
    handleUploadError(err, file, fileList) {
      this.$modal.msgError("上传失败");
    },
    
    handleRemoveFile(file, fileList) {
      this.fileList = fileList;
    },
    
    submitUpload() {
      const uploadFiles = this.$refs.upload.uploadFiles;
      
      if (!uploadFiles || uploadFiles.length === 0) {
        this.$modal.msgWarning("请选择要上传的文件");
        return;
      }
      
      this.$refs.upload.submit();
    },
    
    downloadAttachment(attachment) {
      this.attachmentManager.downloadAttachment(attachment);
    },
    
    deleteAttachment(index) {
      this.$modal.confirm('确认删除该附件吗？').then(() => {
        const attachmentList = [...this.attachmentList];
        attachmentList.splice(index, 1);
        const newAttachments = this.attachmentManager.serializeAttachments(attachmentList);
        
        this.$emit('input', newAttachments);
        this.$emit('change', newAttachments);
        this.$modal.msgSuccess("删除成功");
      });
    }
  }
};
</script>

<style scoped>
.attachment-manager {
  width: 100%;
}

.attachments-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 16px;
}

.attachment-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background-color: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.attachment-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.attachment-info {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
  min-width: 0;
}

.attachment-info i {
  font-size: 20px;
  color: #409eff;
  flex-shrink: 0;
}

.file-details {
  flex: 1;
  min-width: 0;
}

.file-name {
  font-weight: 500;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 2px;
}

.file-size {
  font-size: 12px;
  color: #909399;
}

.attachment-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #909399;
}

.empty-state i {
  font-size: 48px;
  margin-bottom: 16px;
  display: block;
}

.upload-section {
  text-align: center;
  padding: 16px 0;
}

.dialog-footer {
  text-align: right;
}

.el-upload__tip {
  color: #909399;
  font-size: 12px;
  margin-top: 8px;
}
</style>