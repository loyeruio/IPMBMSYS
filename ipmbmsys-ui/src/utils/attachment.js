/**
 * 附件管理工具类
 * 提供统一的附件上传、下载、删除等功能
 */
import { getToken } from "@/utils/auth";

export class AttachmentManager {
  constructor(options = {}) {
    this.uploadUrl = options.uploadUrl || process.env.VUE_APP_BASE_API + "/common/upload";
    this.downloadUrl = options.downloadUrl || process.env.VUE_APP_BASE_API + "/common/download/resource";
    this.maxFileSize = options.maxFileSize || 10; // MB
    this.allowedTypes = options.allowedTypes || ['pdf', 'doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx', 'jpg', 'jpeg', 'png', 'txt'];
    this.headers = {
      Authorization: "Bearer " + getToken(),
      ...options.headers
    };
  }

  /**
   * 格式化文件大小
   * @param {number} size 文件大小（字节）
   * @returns {string} 格式化后的大小
   */
  formatFileSize(size) {
    if (typeof size === 'number') {
      if (size < 1024) return size + 'B';
      if (size < 1024 * 1024) return (size / 1024).toFixed(1) + 'KB';
      if (size < 1024 * 1024 * 1024) return (size / (1024 * 1024)).toFixed(1) + 'MB';
      return (size / (1024 * 1024 * 1024)).toFixed(1) + 'GB';
    }
    return size || '未知';
  }

  /**
   * 解析附件列表
   * @param {string} attachments 附件字符串
   * @returns {Array} 附件对象数组
   */
  parseAttachments(attachments) {
    if (!attachments || attachments === '') return [];
    
    try {
      // 尝试解析JSON格式
      const parsed = JSON.parse(attachments);
      return Array.isArray(parsed) ? parsed : [];
    } catch (e) {
      // 如果解析失败，按逗号分割处理（兼容旧格式）
      const attachmentPaths = attachments.split(',').filter(path => path.trim());
      return attachmentPaths.map((path, index) => {
        const fileName = this.getFileNameFromPath(path);
        return {
          name: fileName || `附件${index + 1}`,
          path: path.trim(),
          url: this.getDownloadUrl(path.trim()),
          size: this.estimateFileSize(fileName)
        };
      });
    }
  }

  /**
   * 从路径中提取文件名
   * @param {string} path 文件路径
   * @returns {string} 文件名
   */
  getFileNameFromPath(path) {
    if (!path) return '';
    return path.substring(path.lastIndexOf('/') + 1);
  }

  /**
   * 获取下载URL
   * @param {string} path 文件路径
   * @returns {string} 下载URL
   */
  getDownloadUrl(path) {
    return `${this.downloadUrl}?resource=${encodeURIComponent(path)}`;
  }

  /**
   * 根据文件扩展名估算文件大小
   * @param {string} fileName 文件名
   * @returns {string} 估算大小
   */
  estimateFileSize(fileName) {
    if (!fileName) return '未知';
    
    const extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    const estimatedSizes = {
      'pdf': '1-5MB',
      'doc': '500KB-2MB', 
      'docx': '500KB-2MB',
      'xls': '200KB-1MB',
      'xlsx': '200KB-1MB',
      'ppt': '1-10MB',
      'pptx': '1-10MB',
      'jpg': '100KB-2MB',
      'jpeg': '100KB-2MB',
      'png': '100KB-5MB',
      'txt': '1KB-100KB'
    };
    
    return estimatedSizes[extension] || '未知';
  }

  /**
   * 验证文件
   * @param {File} file 文件对象
   * @returns {Object} 验证结果
   */
  validateFile(file) {
    const result = { valid: true, message: '' };
    
    // 检查文件大小
    const fileSizeMB = file.size / 1024 / 1024;
    if (fileSizeMB > this.maxFileSize) {
      result.valid = false;
      result.message = `文件大小不能超过 ${this.maxFileSize}MB`;
      return result;
    }
    
    // 检查文件类型
    const fileName = file.name;
    const fileExt = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    if (this.allowedTypes.length > 0 && !this.allowedTypes.includes(fileExt)) {
      result.valid = false;
      result.message = `不支持的文件类型，请上传 ${this.allowedTypes.join('、')} 格式的文件`;
      return result;
    }
    
    return result;
  }

  /**
   * 创建附件对象
   * @param {Object} response 上传响应
   * @param {File} file 文件对象
   * @returns {Object} 附件对象
   */
  createAttachmentObject(response, file) {
    let filePath;
    
    if (response.data) {
      filePath = typeof response.data === 'string' ? response.data : 
                response.data.url || response.data.path || response.data.fileName;
    } else if (response.fileName) {
      filePath = response.fileName;
    } else {
      filePath = file.name;
    }
    
    return {
      name: file.name,
      path: filePath,
      url: this.getDownloadUrl(filePath),
      size: this.formatFileSize(file.size),
      uploadTime: new Date().toISOString()
    };
  }

  /**
   * 序列化附件列表
   * @param {Array} attachments 附件数组
   * @returns {string} 序列化字符串
   */
  serializeAttachments(attachments) {
    if (!Array.isArray(attachments) || attachments.length === 0) {
      return '';
    }
    return JSON.stringify(attachments);
  }

  /**
   * 下载附件
   * @param {Object} attachment 附件对象
   */
  downloadAttachment(attachment) {
    const link = document.createElement('a');
    link.href = attachment.url;
    link.download = attachment.name;
    link.target = '_blank';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }
}

// 创建默认实例
export const defaultAttachmentManager = new AttachmentManager();

// 导出工具函数
export const {
  formatFileSize,
  parseAttachments,
  getFileNameFromPath,
  getDownloadUrl,
  validateFile,
  createAttachmentObject,
  serializeAttachments,
  downloadAttachment
} = defaultAttachmentManager;