<template>
  <el-image
    :src="realSrc"
    fit="cover"
    :style="imageStyle"
    :preview-src-list="realSrcList"
    class="base-image"
  >
    <div slot="error" class="image-slot">
      <img :src="defaultSrc" class="error-image" />
    </div>
  </el-image>
</template>

<script>
import { isExternal } from "@/utils/validate";

export default {
  name: "BaseImage",
  props: {
    src: {
      type: String,
      default: ""
    },
    width: {
      type: [Number, String],
      default: 20
    },
    height: {
      type: [Number, String],
      default: 20
    },
    verticalAlign: {
      type: String,
      default: "middle"
    }
  },
  data() {
    return {
      defaultSrc: require('@/assets/images/school_logo.png')
    }
  },
  computed: {
    realSrc() {
      if (!this.src) return this.defaultSrc
      const real_src = this.src.split(",")[0]
      return isExternal(real_src) ? real_src : process.env.VUE_APP_BASE_API + real_src
    },
    realSrcList() {
      if (!this.src) return [this.defaultSrc]
      return this.src.split(",").map(item =>
        isExternal(item) ? item : process.env.VUE_APP_BASE_API + item
      )
    },
    imageStyle() {
      return {
        width: this.formatSize(this.width),
        height: this.formatSize(this.height),
        verticalAlign: this.verticalAlign
      }
    }
  },
  methods: {
    formatSize(size) {
      return typeof size === "number" ? `${size}px` : size
    }
  }
}
</script>

<style lang="scss" scoped>
.base-image {
  display: inline-block;
  line-height: 0; // 消除图片底部间隙

  ::v-deep .el-image {
    vertical-align: inherit;

    &__inner {
      vertical-align: inherit;
    }
  }

  .image-slot {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;

    .error-image {
      width: 100%;
      height: 100%;
      object-fit: contain;
    }
  }
}
</style>
