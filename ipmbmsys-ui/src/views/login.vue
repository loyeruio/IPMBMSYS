<template>
  <div class="register-page">
    <div class="register-left">
      <el-carousel height="100vh" indicator-position="inside" v-if="bannerImages.length > 0">
        <el-carousel-item v-for="item in bannerImages" :key="item.id">
          <div class="carousel-container" @click="handleBannerClick(item)">
            <image-preview 
              :src="item.imageUrl" 
              width="100%" 
              height="100vh"
              class="carousel-img"
              :disable-preview="true"
            />
            <div class="blur-background" :style="{ backgroundImage: `url(${item.imageUrl})` }"></div>
            <!-- 可选：添加标题覆盖层 -->
            <div class="banner-overlay" v-if="item.title">
              <h3 class="banner-title">{{ item.title }}</h3>
              <p class="banner-subtitle" v-if="item.remark">{{ item.remark }}</p>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
      <!-- 无轮播图时显示默认图片 -->
      <el-carousel height="100vh" indicator-position="inside" v-else>
        <el-carousel-item v-for="item in 9" :key="item">
          <div class="carousel-container">
            <img :src="require('../assets/images/banner' + item + '.jpg')" class="carousel-img" />
            <div class="blur-background" :style="{ backgroundImage: `url(${require('../assets/images/banner' + item + '.jpg')})` }"></div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>
    <div class="register-right">
      <div class="register">
        <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="register-form">
          <h3 class="title">
            <img src="@/assets/images/school_logo.png" alt="logo" class="school-logo" />
            智慧党员后台管理系统
          </h3>
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              type="text"
              auto-complete="off"
              placeholder="账号"
            >
              <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              auto-complete="off"
              placeholder="密码"
              @keyup.enter.native="handleLogin"
            >
              <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>
          <el-form-item prop="code" v-if="captchaEnabled">
            <el-input
              v-model="loginForm.code"
              auto-complete="off"
              placeholder="验证码"
              style="width: 63%"
              @keyup.enter.native="handleLogin"
            >
              <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
            </el-input>
            <div class="register-code">
              <img :src="codeUrl" @click="getCode" class="register-code-img"/>
            </div>
          </el-form-item>
          <el-checkbox v-model="loginForm.rememberMe" style="margin:0px 0px 25px 0px;">记住密码</el-checkbox>
          <el-form-item style="width:100%;">
            <el-button
              :loading="loading"
              size="medium"
              type="primary"
              style="width:100%;"
              @click.native.prevent="handleLogin"
            >
              <span v-if="!loading">登 录</span>
              <span v-else>登 录 中...</span>
            </el-button>
            <div style="float: right;" v-if="register">
              <router-link class="link-type" :to="'/register'">立即注册</router-link>
            </div>
          </el-form-item>
        </el-form>
        <!-- 底部 -->
        <div class="el-register-footer">
          <span>Copyright © 2018-2024 www.tit.edu.cn All Rights Reserved.</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getCodeImg } from "@/api/login";
import { listCarouselPublic } from "@/api/system/carousel"; // 修改为公开接口
import ImagePreview from '@/components/ImagePreview';
import Cookies from "js-cookie";
import { encrypt, decrypt } from '@/utils/jsencrypt'

export default {
  name: "Login",
  components: {
    ImagePreview
  },
  data() {
    return {
      codeUrl: "",
      bannerImages: [], // 添加轮播图数据
      loginForm: {
        username: "admin",
        password: "admin123",
        rememberMe: false,
        code: "",
        uuid: ""
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的账号" }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" }
        ],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      loading: false,
      // 验证码开关
      captchaEnabled: true,
      // 注册开关
      register: true,
      redirect: undefined
    };
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect;
      },
      immediate: true
    }
  },
  created() {
    this.getCode();
    this.getCookie();
    this.fetchBannerImages(); // 添加获取轮播图数据
  },
  methods: {
    getCode() {
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled;
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img;
          this.loginForm.uuid = res.uuid;
        }
      });
    },
    getCookie() {
      const username = Cookies.get("username");
      const password = Cookies.get("password");
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
      };
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true;
          if (this.loginForm.rememberMe) {
            Cookies.set("username", this.loginForm.username, { expires: 30 });
            Cookies.set("password", encrypt(this.loginForm.password), { expires: 30 });
            Cookies.set('rememberMe', this.loginForm.rememberMe, { expires: 30 });
          } else {
            Cookies.remove("username");
            Cookies.remove("password");
            Cookies.remove('rememberMe');
          }
          this.$store.dispatch("Login", this.loginForm).then(() => {
            this.$router.push({ path: this.redirect || "/" }).catch(()=>{});
          }).catch(() => {
            this.loading = false;
            if (this.captchaEnabled) {
              this.getCode();
            }
          });
        }
      });
    },
    
    // 获取轮播图数据 - 登录页类型（使用公开接口）
    fetchBannerImages() {
      const query = {
        pageNum: 1,
        pageSize: 10,
        type: '1', // 1表示登录页轮播图
        status: '0',
        orderByColumn: 'sort',
        isAsc: 'desc'
      }
      
      listCarouselPublic(query).then(response => {
        if (response.code === 200 && response.data) {
          const validCarousels = response.data.filter(item => {
            const now = new Date()
            const startTime = item.startTime ? new Date(item.startTime) : null
            const endTime = item.endTime ? new Date(item.endTime) : null
            
            if (startTime && now < startTime) {
              return false
            }
            
            if (endTime && now > endTime) {
              return false
            }
            
            return true
          })
          
          this.bannerImages = validCarousels.map(item => ({
            id: item.id,
            title: item.title,
            imageUrl: item.imageUrl,
            linkUrl: item.linkUrl,
            remark: item.remark
          }))
        } else {
          this.bannerImages = []
        }
      }).catch(error => {
        console.error('获取登录页轮播图数据失败:', error)
        this.bannerImages = []
      })
    },
    
    // 轮播图点击事件
    handleBannerClick(item) {
      if (item.linkUrl) {
        if (item.linkUrl.startsWith('http://') || item.linkUrl.startsWith('https://')) {
          window.open(item.linkUrl, '_blank')
        } else {
          this.$router.push(item.linkUrl)
        }
      }
    }
  }
};
</script>

<style rel="stylesheet/scss" lang="scss">
.register-page {
  display: flex;
  height: 100vh;
  background: #f5f5f5;
}
.register-left {
  flex: 7;
  background: #222;
  min-width: 0;
  height: 100vh;
  display: block;
  padding: 0;
  margin: 0;
}
.el-carousel, .el-carousel__container {
  width: 100% !important;
  height: 100vh !important;
}
.carousel-container {
  position: relative;
  width: 100%;
  height: 100vh;
}
.carousel-img {
  width: 100%;
  height: 100vh;
  object-fit: contain;
  display: block;
  border-radius: 0;
  margin: 0;
  padding: 0;
  position: relative;
  z-index: 2;
}
.blur-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  background-size: cover;
  background-position: center;
  filter: blur(20px);
  z-index: 1;
  opacity: 0.8;
}
.register-right {
  flex: 3;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #ffffff 0%, #ffe7e7 100%);
  position: relative;
  height: 100vh;
}
.register {
  width: 400px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: auto;
  background: rgba(255,255,255,0.85);
  margin-right: 0;
  border-radius: 12px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.08);
  padding-bottom: 16px;
}
.title {
  margin: 0px auto 30px auto;
  text-align: center;
  color: #707070;
  display: flex;
  align-items: center;
  justify-content: center;
}
.school-logo {
  height: 36px;
  margin-right: 10px;
  vertical-align: middle;
}
.register-form {
  border-radius: 6px;
  background: #ffffff;
  width: 400px;
  padding: 25px 25px 5px 25px;
  .el-input {
    height: 38px;
    input {
      height: 38px;
    }
  }
  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 2px;
  }
}
.register-code {
  width: 33%;
  height: 38px;
  float: right;
  img {
    cursor: pointer;
    vertical-align: middle;
  }
}
.el-register-footer {
  margin-top: 20px;
  height: 40px;
  line-height: 40px;
  width: 100%;
  text-align: center;
  color: #707070;
  font-family: Arial;
  font-size: 12px;
  letter-spacing: 1px;
}
.register-code-img {
  height: 38px;
}

// 添加轮播图覆盖层样式
.banner-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
  color: white;
  padding: 30px 20px 20px;
  z-index: 3;
}

.banner-title {
  font-size: 24px;
  font-weight: bold;
  margin: 0 0 8px 0;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
}

.banner-subtitle {
  font-size: 16px;
  margin: 0;
  opacity: 0.9;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
}
</style>
