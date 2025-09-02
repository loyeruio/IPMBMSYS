export default {
  // 消息提示
  msg(content) {
    uni.showToast({
      title: content,
      icon: 'none'
    })
  },
  // 错误消息
  msgError(content) {
    uni.showToast({
      title: content,
      icon: 'none', // 修改为none，确保兼容性
      duration: 2000
    })
  },
  // 成功消息
  msgSuccess(content) {
    uni.showToast({
      title: content,
      icon: 'success',
      duration: 2000
    })
  },
  // 隐藏消息
  hideMsg(content) {
    uni.hideToast()
  },
  // 弹出提示
  alert(content, title) {
    uni.showModal({
      title: title || '系统提示',
      content: content,
      showCancel: false
    })
  },
  // 确认窗体
  confirm(content, title) {
    return new Promise((resolve, reject) => {
      uni.showModal({
        title: title || '系统提示',
        content: content,
        cancelText: '取消',
        confirmText: '确定',
        success: function(res) {
          if (res.confirm) {
            resolve(res.confirm)
          } else {
            reject(res.cancel) // 添加reject处理
          }
        },
        fail: function(err) {
          reject(err) // 添加fail处理
        }
      })
    })
  },
  // 提示信息
  showToast(option) {
    if (typeof option === "object") {
      uni.showToast(option)
    } else {
      uni.showToast({
        title: option,
        icon: "none",
        duration: 2500
      })
    }
  },
  // 打开遮罩层
  loading(content) {
    uni.showLoading({
      title: content,
      icon: 'none'
    })
  },
  // 关闭遮罩层
  closeLoading() {
    try {
        uni.hideLoading()
    } catch (e) {
        console.log(e)
    }
  }
}
