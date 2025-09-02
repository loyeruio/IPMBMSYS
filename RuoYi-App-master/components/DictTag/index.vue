<template>
  <view class="dict-tag-container">
    <block v-for="(item, index) in options" :key="index">
      <text 
        v-if="isValueMatch(item.value) && (item.raw.listClass == 'default' || item.raw.listClass == '') && (item.raw.cssClass == '' || item.raw.cssClass == null)"
        :class="['dict-tag-text', item.raw.cssClass]"
      >
        {{ item.label }}
      </text>
      <uni-tag
        v-else-if="isValueMatch(item.value)"
        :text="item.label"
        :type="getTagType(item.raw.listClass)"
        :class="item.raw.cssClass"
        size="small"
      ></uni-tag>
    </block>
    <text v-if="unmatch && showValue" class="dict-tag-unmatch">{{ unmatchArray.join(' ') }}</text>
  </view>
</template>

<script>
export default {
  name: "DictTag",
  props: {
    options: {
      type: Array,
      default: () => []
    },
    value: [Number, String, Array],
    // 当未找到匹配的数据时，显示value
    showValue: {
      type: Boolean,
      default: true
    },
    separator: {
      type: String,
      default: ","
    }
  },
  data() {
    return {
      unmatchArray: [] // 记录未匹配的项
    }
  },
  computed: {
    values() {
      if (this.value === null || typeof this.value === 'undefined' || this.value === '') return []
      // 不强制转换为字符串，保持原始类型
      return Array.isArray(this.value) ? this.value : [this.value]
    },
    unmatch() {
      this.unmatchArray = []
      if (this.value === null || typeof this.value === 'undefined' || this.value === '' || this.options.length === 0) return false
      
      let unmatch = false
      this.values.forEach(item => {
        // 使用宽松比较，支持字符串和数字的匹配
        if (!this.options.some(v => v.value == item)) {
          this.unmatchArray.push(item)
          unmatch = true
        }
      })
      return unmatch
    }
  },
  methods: {
    // 新增方法：检查值是否匹配
    isValueMatch(optionValue) {
      return this.values.some(value => value == optionValue) // 使用宽松比较
    },
    getTagType(listClass) {
      const typeMap = {
        'primary': 'primary',
        'success': 'success', 
        'info': 'default',
        'warning': 'warning',
        'danger': 'error'
      }
      return typeMap[listClass] || 'default'
    }
  }
}
</script>

<style lang="scss" scoped>
.dict-tag-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8rpx;
  align-items: center;
}

.dict-tag-text {
  font-size: 24rpx;
  color: #606266;
  padding: 4rpx 8rpx;
}

.dict-tag-unmatch {
  font-size: 24rpx;
  color: #909399;
  padding: 4rpx 8rpx;
}

:deep(.uni-tag) {
  margin-right: 8rpx;
}
</style>
