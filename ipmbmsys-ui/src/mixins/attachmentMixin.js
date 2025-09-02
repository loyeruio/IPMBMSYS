/**
 * 附件管理混入
 * 为Vue组件提供统一的附件管理功能
 */
import { AttachmentManager } from '@/utils/attachment'; // 路径保持不变
import { getToken } from '@/utils/auth';

export default {
  data() {
    return {
      // 附件管理器
      attachmentManager: new AttachmentManager(),
      
      // 上传对话框
      uploadDialogVisible: false,
      
      // 当前操作的行数据
      currentUploadRow: null,
      
      // 文件列表
      fileList: [],
      
      // 上传配置
      uploadConfig: {
        url: process.env.VUE_APP_BASE_API + "/common/upload",
        headers: {
          Authorization: "Bearer " + getToken()
        },
        maxSize: 10, // MB
        allowedTypes: ['pdf', 'doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx', 'jpg', 'jpeg', 'png', 'txt']
      },
      // 添加批量上传相关数据
      uploadingFiles: [], // 正在上传的文件列表
      uploadedAttachments: [], // 已上传成功的附件列表
      totalUploadCount: 0, // 总上传文件数
      successUploadCount: 0, // 成功上传文件数
    };
  },
  
  computed: {
    uploadUrl() {
      return this.uploadConfig.url;
    },
    
    uploadHeaders() {
      return this.uploadConfig.headers;
    }
  },
  
  methods: {
    /**
     * 获取附件列表
     * @param {string} attachments 附件字符串
     * @returns {Array} 附件列表
     */
    getAttachmentList(attachments) {
      return this.attachmentManager.parseAttachments(attachments);
    },
    
    /**
     * 格式化文件大小
     * @param {number} size 文件大小
     * @returns {string} 格式化后的大小
     */
    formatFileSize(size) {
      return this.attachmentManager.formatFileSize(size);
    },
    
    /**
     * 打开上传对话框
     * @param {Object} row 行数据
     */
    handleUploadAttachment(row) {
      this.currentUploadRow = row;
      this.fileList = [];
      this.uploadDialogVisible = true;
      
      // 重置批量上传状态
      this.uploadingFiles = [];
      this.uploadedAttachments = [];
      this.totalUploadCount = 0;
      this.successUploadCount = 0;
      
      this.$nextTick(() => {
        if (this.$refs.upload) {
          this.$refs.upload.clearFiles();
        }
      });
    },
    
    /**
     * 上传前验证
     * @param {File} file 文件对象
     * @returns {boolean} 是否通过验证
     */
    beforeUpload(file) {
      const validation = this.attachmentManager.validateFile(file);
      if (!validation.valid) {
        this.$modal.msgError(validation.message);
        return false;
      }
      return true;
    },
    
    /**
     * 上传成功处理 - 修改为批量处理模式
     * @param {Object} response 响应数据
     * @param {File} file 文件对象
     * @param {Array} fileList 文件列表
     */
    handleUploadSuccess(response, file, fileList) {
      if (response.code === 200) {
        // 创建新附件对象
        const newAttachment = this.attachmentManager.createAttachmentObject(response, file);
        this.uploadedAttachments.push(newAttachment);
        this.successUploadCount++;
        
        console.log(`文件上传成功: ${file.name}, 进度: ${this.successUploadCount}/${this.totalUploadCount}`);
        
        // 检查是否所有文件都上传完成
        if (this.successUploadCount === this.totalUploadCount) {
          this.handleAllFilesUploaded();
        }
      } else {
        this.$modal.msgError(response.msg || "上传失败");
        this.successUploadCount++; // 即使失败也要计数，避免卡住
        
        // 检查是否所有文件都处理完成
        if (this.successUploadCount === this.totalUploadCount) {
          this.handleAllFilesUploaded();
        }
      }
    },
    
    /**
     * 所有文件上传完成后的处理
     */
    handleAllFilesUploaded() {
      if (this.uploadedAttachments.length === 0) {
        this.$modal.msgError("没有文件上传成功");
        return;
      }
      
      if (this.currentUploadRow) {
        // 解析现有附件
        const currentAttachments = this.attachmentManager.parseAttachments(
          this.currentUploadRow.attachments
        );
        
        // 批量添加新附件
        currentAttachments.push(...this.uploadedAttachments);
        
        // 序列化附件列表
        const updatedAttachments = this.attachmentManager.serializeAttachments(currentAttachments);
        
        console.log('批量更新附件:', {
          新增数量: this.uploadedAttachments.length,
          总附件数: currentAttachments.length,
          附件数据: updatedAttachments
        });
        
        // 一次性更新数据库
        this.updateRowAttachments(this.currentUploadRow.id, updatedAttachments);
        
        this.$modal.msgSuccess(`成功上传 ${this.uploadedAttachments.length} 个文件`);
      }
      
      // 重置状态
      this.uploadedAttachments = [];
      this.successUploadCount = 0;
      this.totalUploadCount = 0;
    },
    
    /**
     * 上传失败处理
     * @param {Error} err 错误信息
     * @param {File} file 文件对象
     * @param {Array} fileList 文件列表
     */
    handleUploadError(err, file, fileList) {
      this.$modal.msgError(`文件 ${file.name} 上传失败`);
      this.successUploadCount++; // 失败也要计数
      
      // 检查是否所有文件都处理完成
      if (this.successUploadCount === this.totalUploadCount) {
        this.handleAllFilesUploaded();
      }
    },
        /**
     * 移除文件处理
     * @param {File} file 文件对象
     * @param {Array} fileList 文件列表
     */
    handleRemoveFile(file, fileList) {
      // 如果文件还未上传，需要从总数中减去
      if (file.status === 'ready') {
        this.totalUploadCount = Math.max(0, this.totalUploadCount - 1);
        console.log(`移除待上传文件: ${file.name}, 剩余: ${this.totalUploadCount}`);
      }
      
      // 更新文件列表
      this.fileList = fileList;
    },
    /**
     * 提交上传 - 修改为批量模式
     */
    submitUpload() {
      const uploadFiles = this.$refs.upload.uploadFiles;
      
      if (!uploadFiles || uploadFiles.length === 0) {
        this.$modal.msgWarning("请选择要上传的文件");
        return;
      }
      
      // 重新计算待上传文件数量（只计算ready状态的文件）
      const readyFiles = uploadFiles.filter(file => file.status === 'ready');
      this.totalUploadCount = readyFiles.length;
      this.successUploadCount = 0;
      this.uploadedAttachments = [];
      
      if (this.totalUploadCount === 0) {
        this.$modal.msgWarning("没有待上传的文件");
        return;
      }
      
      console.log(`开始批量上传，共 ${this.totalUploadCount} 个文件`);
      
      // 开始上传
      this.$refs.upload.submit();
    },
    /**
     * 下载附件
     * @param {Object} attachment 附件对象
     */
    downloadAttachment(attachment) {
      this.attachmentManager.downloadAttachment(attachment);
    },
    
    /**
     * 删除附件
     * @param {Object} row 行数据
     * @param {number} index 附件索引
     */
    deleteAttachment(row, index) {
      this.$modal.confirm('确认删除该附件吗？').then(() => {
        const attachmentList = this.attachmentManager.parseAttachments(row.attachments);
        attachmentList.splice(index, 1);
        const newAttachments = this.attachmentManager.serializeAttachments(attachmentList);
        
        this.updateRowAttachments(row.id, newAttachments);
      });
    },
    /**
     * 所有文件上传完成后的处理
     */
    handleAllFilesUploaded() {
      if (this.uploadedAttachments.length === 0) {
        this.$modal.msgError("没有文件上传成功");
        return;
      }
      
      if (this.currentUploadRow) {
        // 解析现有附件
        const currentAttachments = this.attachmentManager.parseAttachments(
          this.currentUploadRow.attachments
        );
        
        // 批量添加新附件
        currentAttachments.push(...this.uploadedAttachments);
        
        // 序列化附件列表
        const updatedAttachments = this.attachmentManager.serializeAttachments(currentAttachments);
        
        console.log('批量更新附件:', {
          新增数量: this.uploadedAttachments.length,
          总附件数: currentAttachments.length,
          附件数据: updatedAttachments
        });
        
        // 一次性更新数据库
        this.updateRowAttachments(this.currentUploadRow.id, updatedAttachments);
        
        this.$modal.msgSuccess(`成功上传 ${this.uploadedAttachments.length} 个文件`);
        
        // 关闭上传对话框
        this.uploadDialogVisible = false;
        
        // 刷新列表数据
        if (this.getList && typeof this.getList === 'function') {
          this.getList();
        }
      }
      
      // 重置状态
      this.uploadedAttachments = [];
      this.successUploadCount = 0;
      this.totalUploadCount = 0;
    }
  }
};