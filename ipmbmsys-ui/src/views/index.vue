<template>
  <div class="dashboard-editor-container">
    <!-- 轮播图 -->
    <el-card class="banner-container" shadow="hover">
      <div v-if="loading" class="loading-placeholder" style="height: 350px;">
        <el-empty description="加载中..." :image-size="100"></el-empty>
      </div>
      <el-carousel
        v-else-if="bannerImages.length > 0"
        :interval="4000"
        type="card"
        height="350px"
        indicator-position="outside"
        arrow="always"
        :autoplay="true"
        @change="handleCarouselChange"
      >
        <el-carousel-item v-for="item in bannerImages" :key="item.id">
          <div class="banner-item" @click="handleBannerClick(item)">
            <image-preview
              :src="item.imageUrl"
              width="100%"
              height="350px"
              class="banner-image"
              @load="handleImageLoad"
            />
            <div class="banner-overlay">
              <h3 class="banner-title">{{ item.title }}</h3>
              <p class="banner-subtitle" v-if="item.remark">{{ item.remark }}</p>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
      <div v-else class="no-banner" style="height: 350px;">
        <el-empty description="暂无轮播图数据" :image-size="100">
          <el-button type="primary" size="small" @click="goToCarouselManage">去管理轮播图</el-button>
        </el-empty>
      </div>
    </el-card>

    <!-- 其余内容保持不变 -->
    <el-row style="margin-top: 30px; margin-bottom: 40px;">
      <el-col :span="24">
        <el-card class="box-card" shadow="hover">
          <div slot="header" class="card-header">
            <span class="header-title">
              <i class="el-icon-office-building" style="margin-right: 8px; color: #409EFF;"></i>
              党组织结构概览
            </span>
            <el-button
              style="float: right;"
              type="primary"
              size="small"
              icon="el-icon-view"
              @click="goToDepts"
              plain
            >
              查看详情
            </el-button>
          </div>
          <div class="org-overview">
            <el-row :gutter="20">
              <el-col :span="6" v-for="(item, index) in orgStats" :key="index">
                <el-card class="org-stat-card" shadow="hover" :body-style="{ padding: '20px' }">
                  <div class="org-stat-item">
                    <div class="org-stat-icon" :class="item.iconClass">
                      <i :class="item.icon"></i>
                    </div>
                    <div class="org-stat-info">
                      <div class="org-stat-title">{{ item.title }}</div>
                      <div class="org-stat-value">{{ item.value }}</div>
                    </div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 统计图表区域 -->
    <el-row :gutter="32" style="margin-bottom: 30px;">
      <el-col :xs="24" :sm="24" :lg="8">
        <el-card class="chart-card" shadow="hover">
          <div class="chart-header">
            <span class="chart-title">
              <i class="el-icon-pie-chart" style="margin-right: 8px; color: #67C23A;"></i>
              党员类别分布
            </span>
          </div>
          <div class="chart-wrapper">
            <pie-chart ref="pieChart" />
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="8">
        <el-card class="chart-card" shadow="hover">
          <div class="chart-header">
            <span class="chart-title">
              <i class="el-icon-s-data" style="margin-right: 8px; color: #E6A23C;"></i>
              党员学历分布
            </span>
          </div>
          <div class="chart-wrapper">
            <bar-chart ref="barChart" />
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="8">
        <el-card class="chart-card" shadow="hover">
          <div class="chart-header">
            <span class="chart-title">
              <i class="el-icon-data-analysis" style="margin-right: 8px; color: #F56C6C;"></i>
              党龄分布
            </span>
          </div>
          <div class="chart-wrapper">
            <raddar-chart ref="raddarChart" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近会议和通知公告区域 -->
    <el-row :gutter="32">
      <el-col :xs="24" :sm="24" :lg="12">
        <el-card class="box-card" shadow="hover">
          <div slot="header" class="card-header">
            <span class="header-title">
              <i class="el-icon-date" style="margin-right: 8px; color: #409EFF;"></i>
              最近党建会议
            </span>
            <el-button
              size="small"
              type="primary"
              icon="el-icon-more"
              @click="goToMeetings"
              plain
            >
              查看更多
            </el-button>
          </div>
          <div class="recent-list">
            <el-timeline v-if="recentMeetings.length > 0">
              <el-timeline-item
                v-for="(item, index) in recentMeetings"
                :key="index"
                :timestamp="item.date"
                placement="top"
                :color="index === 0 ? '#409EFF' : '#909399'"
                :size="index === 0 ? 'large' : 'normal'"
              >
                <el-card class="timeline-card" shadow="hover" :body-style="{ padding: '16px' }">
                  <div class="meeting-item">
                    <div class="meeting-title">
                      <el-tag size="small" :type="getMeetingTypeTag(item.type)" effect="light" style="margin-right: 8px;">
                        <i class="el-icon-video-camera" style="margin-right: 4px;"></i>
                        {{ getMeetingTypeLabel(item.type) }}
                      </el-tag>
                      <span class="title-text">{{ item.title }}</span>
                    </div>
                    <div class="meeting-location">
                      <i class="el-icon-location-outline" style="margin-right: 4px; color: #909399;"></i>
                      <span style="color: #606266;">{{ item.location }}</span>
                    </div>
                  </div>
                </el-card>
              </el-timeline-item>
            </el-timeline>
            <el-empty
              v-else
              description="暂无会议数据"
              :image-size="100"
            >
              <el-button type="primary" size="small" @click="goToMeetings">去添加会议</el-button>
            </el-empty>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="12">
        <el-card class="box-card" shadow="hover">
          <div slot="header" class="card-header">
            <span class="header-title">
              <i class="el-icon-bell" style="margin-right: 8px; color: #E6A23C;"></i>
              通知公告
            </span>
            <el-button
              size="small"
              type="warning"
              icon="el-icon-more"
              @click="goToNotices"
              plain
            >
              查看更多
            </el-button>
          </div>
          <div class="recent-list">
            <el-timeline v-if="recentNotices.length > 0">
              <el-timeline-item
                v-for="(item, index) in recentNotices"
                :key="index"
                :timestamp="item.date"
                placement="top"
                :color="index === 0 ? '#E6A23C' : '#909399'"
                :size="index === 0 ? 'large' : 'normal'"
              >
                <el-card class="timeline-card" shadow="hover" :body-style="{ padding: '16px' }">
                  <div class="notice-item">
                    <div class="notice-title">
                      <el-tag size="small" :type="getNoticeTypeTag(item.type)" effect="light" style="margin-right: 8px;">
                        <i class="el-icon-document" style="margin-right: 4px;"></i>
                        {{ getNoticeTypeLabel(item.type) }}
                      </el-tag>
                      <span class="title-text">{{ item.title }}</span>
                    </div>
                    <div class="notice-publisher">
                      <i class="el-icon-user" style="margin-right: 4px; color: #909399;"></i>
                      <span style="color: #606266;">{{ item.publisher }}</span>
                    </div>
                  </div>
                </el-card>
              </el-timeline-item>
            </el-timeline>
            <el-empty
              v-else
              description="暂无公告数据"
              :image-size="100"
            >
              <el-button type="warning" size="small" @click="goToNotices">去发布公告</el-button>
            </el-empty>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import RaddarChart from './dashboard/RaddarChart'
import PieChart from './dashboard/PieChart'
import BarChart from './dashboard/BarChart'
import ImagePreview from '@/components/ImagePreview'
import { listDept } from "@/api/system/dept";
import { listAllUser } from "@/api/system/user";
import { listCommon } from "@/api/meeting/meeting";
import { listNotice } from "@/api/system/notice";
import { listCarouselPublic } from "@/api/system/carousel"; // 修改为公开接口

export default {
  name: 'Index',
  dicts: ['category_informations', 'sys_notice_type', 'meeting_type', 'party_member_education'],
  components: {
    RaddarChart,
    PieChart,
    BarChart,
    ImagePreview
  },
  data() {
    return {
      recentMeetings: [],
      recentNotices: [],
      bannerImages: [],
      orgStats: [
        { title: '党组织总数', value: 0, icon: 'el-icon-office-building', iconClass: 'org-icon-1' },
        { title: '党员总数', value: 0, icon: 'el-icon-user', iconClass: 'org-icon-2' },
        { title: '预备党员', value: 0, icon: 'el-icon-user-solid', iconClass: 'org-icon-3' },
        { title: '发展对象', value: 0, icon: 'el-icon-s-custom', iconClass: 'org-icon-4' }
      ],
      memberCategoryData: [],
      memberEducationData: [],
      memberAgeData: [],
      loading: true
    }
  },
  mounted() {
    this.fetchDashboardData();
  },
  methods: {
    fetchDashboardData() {
      this.loading = true;

      Promise.all([
        listDept(),
        listAllUser({}),
        listCommon({ pageNum: 1, pageSize: 5, orderByColumn: 'start_time', isAsc: 'desc' }),
        listNotice({ pageNum: 1, pageSize: 5, orderByColumn: 'createTime', isAsc: 'desc', status: '0' }),
        listCarouselPublic({ pageNum: 1, pageSize: 10, type: '0', status: '0', orderByColumn: 'sort', isAsc: 'desc' })
      ]).then(([deptResponse, userResponse, meetingResponse, noticeResponse, carouselResponse]) => {
        // 获取党组织数据
        if (deptResponse.code === 200 && deptResponse.data) {
          this.orgStats[0].value = deptResponse.data.length || 0;
        }

        // 获取党员数据
        if (userResponse.code === 200 && userResponse.data) {
          const users = userResponse.data;
          this.orgStats[1].value = users.length || 0;
          const probationaryMembers = users.filter(user => user.category === '2');
          this.orgStats[2].value = probationaryMembers.length || 0;
          const developmentObjects = users.filter(user => user.category === '1');
          this.orgStats[3].value = developmentObjects.length || 0;

          // 党员类别分布
          const categoryCount = {};
          users.forEach(user => {
            const category = user.category || '0';
            categoryCount[category] = (categoryCount[category] || 0) + 1;
          });
          this.memberCategoryData = Object.keys(categoryCount).map(key => ({
            name: this.getCategoryLabel(key),
            value: categoryCount[key]
          }));

          // 党员学历分布
          const educationCount = {};
          users.forEach(user => {
            if (user.education && user.education !== '0') {
              const education = user.education;
              educationCount[education] = (educationCount[education] || 0) + 1;
            }
          });
          this.memberEducationData = Object.keys(educationCount).map(key => ({
            name: this.getEducationLabel(key),
            value: educationCount[key] || 0
          }));

          // 党龄分布
          const partyAgeGroups = { '1年及以下': 0, '1-3年': 0, '3-5年': 0, '5-10年': 0, '10年以上': 0 };
          const now = new Date();
          users.forEach(user => {
            if (user.fullMemberDate) {
              const fullDate = new Date(user.fullMemberDate);
              if (!isNaN(fullDate.getTime()) && fullDate <= now) {
                const diffYear = (now - fullDate) / (365 * 24 * 60 * 60 * 1000);
                if (diffYear <= 1) partyAgeGroups['1年及以下']++;
                else if (diffYear <= 3) partyAgeGroups['1-3年']++;
                else if (diffYear <= 5) partyAgeGroups['3-5年']++;
                else if (diffYear <= 10) partyAgeGroups['5-10年']++;
                else partyAgeGroups['10年以上']++;
              }
            }
          });
          this.memberAgeData = Object.keys(partyAgeGroups).map(key => ({
            name: key,
            value: partyAgeGroups[key]
          }));

          this.$nextTick(() => {
            this.$refs.pieChart && this.$refs.pieChart.setData(this.memberCategoryData);
            this.$refs.barChart && this.$refs.barChart.setData(this.memberEducationData);
            this.$refs.raddarChart && this.$refs.raddarChart.setData(this.memberAgeData);
          });
        }

        // 获取最近会议
        if (meetingResponse.code === 200 && meetingResponse.rows) {
          this.recentMeetings = meetingResponse.rows.map(meeting => ({
            title: meeting.title || '未命名会议',
            date: this.formatDate(meeting.start_time || meeting.createTime),
            location: meeting.location || '待定',
            type: meeting.type || '0'
          }));
        } else {
          this.recentMeetings = [];
          console.warn('无会议数据:', meetingResponse);
        }

        // 获取通知公告
        if (noticeResponse.code === 200 && noticeResponse.rows) {
          this.recentNotices = noticeResponse.rows.map(notice => ({
            title: notice.noticeTitle || notice.title || '未命名通知',
            date: this.formatDate(notice.createTime),
            publisher: notice.createBy || notice.publisher || '系统管理员',
            type: notice.noticeType || '0'
          }));
        } else {
          this.recentNotices = [];
          console.warn('无通知数据', noticeResponse);
        }

        // 获取轮播图
        if (carouselResponse.code === 200 && carouselResponse.data) {
          const validCarousels = carouselResponse.data.filter(item => {
            const now = new Date();
            const startTime = item.startTime ? new Date(item.startTime) : null;
            const endTime = item.endTime ? new Date(item.endTime) : null;
            return (!startTime || now >= startTime) && (!endTime || now <= endTime);
          });
          this.bannerImages = validCarousels.map(item => ({
            id: item.id,
            title: item.title,
            imageUrl: item.imageUrl,
            linkUrl: item.linkUrl,
            remark: item.remark
          }));
        } else {
          this.bannerImages = [];
          console.warn('无轮播图数据', carouselResponse);
        }

        this.loading = false;
        this.$nextTick(() => {
          this.adjustContainerHeight(); // 手动调整高度
        });
      }).catch(error => {
        console.error('数据加载失败:', error);
        this.loading = false;
        this.$message.error('数据加载失败');
      });
    },

    handleImageLoad() {
      console.log('Image loaded, adjusting container height');
      this.$nextTick(() => this.adjustContainerHeight());
    },

    handleCarouselChange() {
      console.log('Carousel changed, adjusting container height');
      this.$nextTick(() => this.adjustContainerHeight());
    },

    adjustContainerHeight() {
      const container = this.$el.querySelector('.banner-container');
      if (container) {
        container.style.height = '350px';
        console.log('Container height adjusted to:', container.style.height);
      }
    },

    formatDate(dateString) {
      if (!dateString) return '未知时间';
      const date = new Date(dateString);
      if (isNaN(date.getTime())) return '未知时间';
      return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      });
    },

    handleBannerClick(item) {
      if (item.linkUrl) {
        if (item.linkUrl.startsWith('http://') || item.linkUrl.startsWith('https://')) {
          window.open(item.linkUrl, '_blank');
        } else {
          this.$router.push(item.linkUrl);
        }
      }
    },

    getCategoryLabel(value) {
      const dict = this.dict.type.category_informations;
      if (dict && dict.length > 0) {
        const item = dict.find(d => d.value === value);
        return item ? item.label : `类别${value}`;
      }
      const defaultMap = { '0': '积极分子', '1': '发展对象', '2': '预备党员', '3': '正式党员' };
      return defaultMap[value] || `类别${value}`;
    },

    getEducationLabel(value) {
      const dict = this.dict.type.party_member_education;
      if (dict && dict.length > 0) {
        const item = dict.find(d => d.value === value);
        return item ? item.label : `学历${value}`;
      }
      const defaultMap = {
        '1': '小学', '2': '初中', '3': '高中', '4': '专科在读', '5': '专科',
        '6': '本科在读', '7': '本科', '8': '硕士研究生在读', '9': '硕士研究生',
        '10': '博士研究生在读', '11': '博士研究生'
      };
      return defaultMap[value] || `学历${value}`;
    },

    getMeetingTypeLabel(value) {
      const dict = this.dict.type.meeting_type;
      if (dict && dict.length > 0) {
        const item = dict.find(d => d.value === value);
        return item ? item.label : '会议';
      }
      return '会议';
    },

    getMeetingTypeTag(value) {
      const typeMap = { '0': 'primary', '1': 'success', '2': 'warning', '3': 'danger' };
      return typeMap[value] || 'primary';
    },

    getNoticeTypeLabel(value) {
      const dict = this.dict.type.sys_notice_type;
      if (dict && dict.length > 0) {
        const item = dict.find(d => d.value === value);
        return item ? item.label : '公告';
      }
      return '公告';
    },

    getNoticeTypeTag(value) {
      const typeMap = { '0': 'warning', '1': 'success', '2': 'danger' };
      return typeMap[value] || 'warning';
    },

    goToMeetings() { this.$router.push('meeting/meeting'); },
    goToNotices() { this.$router.push('/meeting/notice'); },
    goToDepts() { this.$router.push('/PartyMemSys/dept'); },
    goToCarouselManage() { this.$router.push('/carousel/carousel'); }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-editor-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 84px);
}

// 轮播图样式
.banner-container {
  margin-bottom: 30px;
  border-radius: 12px;
  overflow: hidden;
  min-height: 350px !important;
  max-height: 350px !important; // 强制最大高度

  ::v-deep .el-card__body {
    padding: 0;
  }

  ::v-deep .el-carousel {
    height: 350px !important;
    overflow: hidden !important;
  }

  ::v-deep .el-carousel__container {
    height: 350px !important;
    overflow: hidden !important;
  }
}

.loading-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 350px !important;
  background: #f8f9fa;
  border-radius: 8px;
}

.banner-item {
  position: relative;
  height: 350px !important;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.3s ease;

  &:hover {
    transform: scale(1.02);
  }
}

.banner-image {
  width: 100%;
  height: 100%;
  border-radius: 8px;
  object-fit: cover; // 确保图片填充且不失真
}

.banner-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
  color: white;
  padding: 30px 20px 20px;
  z-index: 10;

  .banner-title {
    font-size: 24px;
    font-weight: bold;
    margin: 0 0 8px 0;
    text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
  }

  .banner-subtitle {
    font-size: 14px;
    margin: 0;
    opacity: 0.9;
    text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
  }
}

.no-banner {
  height: 350px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
  border-radius: 8px;
}

// 卡片头部样式
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .header-title {
    font-size: 16px;
    font-weight: bold;
    color: #303133;
  }
}

// 组织概览样式
.org-overview {
  .org-stat-card {
    transition: all 0.3s ease;
    border-radius: 8px;

    &:hover {
      transform: translateY(-5px);
    }
  }

  .org-stat-item {
    display: flex;
    align-items: center;

    .org-stat-icon {
      width: 50px;
      height: 50px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 15px;

      i {
        font-size: 24px;
        color: white;
      }

      &.org-icon-1 { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
      &.org-icon-2 { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
      &.org-icon-3 { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
      &.org-icon-4 { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }
    }

    .org-stat-info {
      flex: 1;

      .org-stat-title { font-size: 14px; color: #606266; margin-bottom: 5px; }
      .org-stat-value { font-size: 24px; font-weight: bold; color: #303133; }
    }
  }
}

// 图表卡片样式
.chart-card {
  height: 400px;
  border-radius: 8px;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
  }

  .chart-header {
    padding: 20px 20px 10px;
    border-bottom: 1px solid #f0f0f0;

    .chart-title {
      font-size: 16px;
      font-weight: bold;
      color: #303133;
    }
  }

  .chart-wrapper {
    height: 320px;
    padding: 20px;
  }
}

// 时间线样式
.recent-list {
  max-height: 400px;
  overflow-y: auto;
  overflow-x: hidden;

  .timeline-card {
    border-radius: 6px;
    transition: all 0.3s ease;
    overflow-x: hidden;

    &:hover {
      transform: translateX(5px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }
  }

  .meeting-item, .notice-item {
    .meeting-title, .notice-title {
      display: flex;
      align-items: center;
      margin-bottom: 8px;

      .title-text {
        font-weight: 500;
        color: #303133;
        line-height: 1.4;
      }
    }

    .meeting-location, .notice-publisher {
      display: flex;
      align-items: center;
      font-size: 12px;
      color: #909399;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .dashboard-editor-container { padding: 10px; }
  .banner-container ::v-deep .el-carousel { height: 250px !important; }
  .org-overview .org-stat-item { flex-direction: column; text-align: center; .org-stat-icon { margin-right: 0; margin-bottom: 10px; } }
  .chart-card { height: auto; margin-bottom: 20px; .chart-wrapper { height: 250px; } }
}

// 滚动条样式
.recent-list::-webkit-scrollbar { width: 6px; }
.recent-list::-webkit-scrollbar-track { background: #f1f1f1; border-radius: 3px; }
.recent-list::-webkit-scrollbar-thumb { background: #c1c1c1; border-radius: 3px; &:hover { background: #a8a8a8; } }
</style>