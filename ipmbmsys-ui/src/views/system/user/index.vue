<template>
  <div class="app-container">
    <el-row :gutter="20" class="full-height-row">
      <!--党组织数据-->
      <el-col :span="5" :xs="24" class="dept-col">
        <el-scrollbar height="calc(100vh - 120px)">
          <div class="dept-container">
            <div class="head-container">
              <el-input
                v-model="deptName"
                placeholder="请输入党组织名称"
                clearable
                size="small"
                prefix-icon="el-icon-search"
                style="margin-bottom: 20px"
              />
            </div>
            <div class="head-container">
              <el-tree
                :data="deptOptions"
                :props="defaultProps"
                :expand-on-click-node="false"
                :filter-node-method="filterNode"
                ref="tree"
                node-key="id"
                default-expand-all
                highlight-current
                @node-click="handleNodeClick"
              >
                <!-- 使用 scoped slot 自定义节点内容 -->
                <template #default="{ node, data }">
                  <span class="custom-tree-node">
                    <BaseImage
                      :src="data.deptImg ? data.deptImg : ''"
                      :width="20"
                      :height="20"
                    />
                    <span>{{ node.label }}</span>
                  </span>
                </template>
              </el-tree>
            </div>
          </div>
        </el-scrollbar>
      </el-col>
      <!--用户数据-->
      <el-col :span="19" :xs="24" class="user-col">
        <div class="user-container" style="height: calc(100vh - 120px); overflow-y: auto;  overflow-x: hidden;">
        <div style="margin-bottom: 10px;">
          <button
            @click="showSearch = !showSearch"
            style="background: none; border: none; color: #409EFF; cursor: pointer; padding: 0; font-size: 14px;"
          >
            {{ showSearch ? '折叠筛选' : '展开筛选' }}
          </button>
        </div>
        <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="130px">
          <el-form-item label="党员姓名" prop="userNameQuery">
            <el-input
              v-model="queryParams.userName"
              placeholder="请输入党员姓名"
              clearable
              style="width: 240px"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="联系电话" prop="phonenumber">
            <el-input
              v-model="queryParams.phonenumber"
              placeholder="请输入联系电话"
              clearable
              style="width: 240px"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="学号/编号" prop="partyMemberId">
            <el-input
              v-model="queryParams.partyMemberId"
              placeholder="请输入学号/编号"
              clearable
              style="width: 240px"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="党员身份" prop="userType">
            <el-select
              v-model="queryParams.userType"
              placeholder="请选择党员身份"
              clearable
              style="width: 240px"
            >
              <el-option
                v-for="dict in dict.type.party_membership_type"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="性别" prop="sex">
            <el-select
              v-model="queryParams.sex"
              placeholder="请选择性别"
              clearable
              style="width: 240px"
            >
              <el-option
                v-for="dict in dict.type.sys_user_sex"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="民族" prop="ethnicity">
            <el-select
              v-model="queryParams.ethnicity"
              placeholder="请选择民族"
              clearable
              style="width: 240px"
            >
              <el-option
                v-for="dict in dict.type.party_member_ethnicity"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="学历" prop="education">
            <el-select
              v-model="queryParams.education"
              placeholder="请选择学历"
              clearable
              style="width: 240px"
            >
              <el-option
                v-for="dict in dict.type.party_member_education"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="党员类别" prop="category">
            <el-select
              v-model="queryParams.category"
              placeholder="请选择党员类别"
              clearable
              style="width: 240px"
            >
              <el-option
                v-for="dict in dict.type.category_informations"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="是否失联" prop="isMissing">
            <el-select
              v-model="queryParams.isMissing"
              placeholder="是否失联党员"
              clearable
              style="width: 240px"
            >
              <el-option
                v-for="dict in dict.type.party_yes_no"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="是否流动" prop="isMobile">
            <el-select
              v-model="queryParams.isMobile"
              placeholder="是否流动党员"
              clearable
              style="width: 240px"
            >
              <el-option
                v-for="dict in dict.type.party_yes_no"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="党员账号状态" prop="status">
            <el-select
              v-model="queryParams.status"
              placeholder="党员账号状态"
              clearable
              style="width: 240px"
            >
              <el-option
                v-for="dict in dict.type.sys_normal_disable"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="出生日期" prop="birthDateRange">
            <el-date-picker
              v-model="daterangeBirthDate"
              style="width: 240px"
              value-format="yyyy-MM-dd"
              type="daterange"
              range-separator="-"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
            ></el-date-picker>
          </el-form-item>
          <el-form-item label="申请入党日期" prop="applicationDateRange">
            <el-date-picker
              v-model="daterangeApplicationDate"
              style="width: 240px"
              value-format="yyyy-MM-dd"
              type="daterange"
              range-separator="-"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
            ></el-date-picker>
          </el-form-item>
          <el-form-item label="成为积极分子日期" prop="activistDateRange">
            <el-date-picker
              v-model="daterangeActivistDate"
              style="width: 240px"
              value-format="yyyy-MM-dd"
              type="daterange"
              range-separator="-"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
            ></el-date-picker>
          </el-form-item>
          <el-form-item label="确定发展对象日期" prop="developmentObjectDateRange">
            <el-date-picker
              v-model="daterangeDevelopmentObjectDate"
              style="width: 240px"
              value-format="yyyy-MM-dd"
              type="daterange"
              range-separator="-"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
            ></el-date-picker>
          </el-form-item>
          <el-form-item label="成为预备党员日期" prop="probationaryMemberDateRange">
            <el-date-picker
              v-model="daterangeProbationaryMemberDate"
              style="width: 240px"
              value-format="yyyy-MM-dd"
              type="daterange"
              range-separator="-"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
            ></el-date-picker>
          </el-form-item>
          <el-form-item label="转为正式党员日期" prop="fullMemberDateRange">
            <el-date-picker
              v-model="daterangeFullMemberDate"
              style="width: 240px"
              value-format="yyyy-MM-dd"
              type="daterange"
              range-separator="-"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
            ></el-date-picker>
          </el-form-item>
          <el-form-item label="创建时间">
            <el-date-picker
              v-model="dateRange"
              style="width: 240px"
              value-format="yyyy-MM-dd"
              type="daterange"
              range-separator="-"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
            ></el-date-picker>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button
              type="primary"
              plain
              icon="el-icon-plus"
              size="mini"
              @click="handleAdd"
              v-hasPermi="['system:user:add']"
            >新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="success"
              plain
              icon="el-icon-edit"
              size="mini"
              :disabled="single"
              @click="handleUpdate"
              v-hasPermi="['system:user:edit']"
            >修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="danger"
              plain
              icon="el-icon-delete"
              size="mini"
              :disabled="multiple"
              @click="handleDelete"
              v-hasPermi="['system:user:remove']"
            >删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="info"
              plain
              icon="el-icon-upload2"
              size="mini"
              @click="handleImport"
              v-hasPermi="['system:user:import']"
            >导入</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="warning"
              plain
              icon="el-icon-download"
              size="mini"
              @click="handleExport"
              v-hasPermi="['system:user:export']"
            >导出</el-button>
          </el-col>
          <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="userList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="党员身份" align="center" key="userType" prop="userType" v-if="columns[0].visible" width="120" >
            <template slot-scope="scope">
              <dict-tag :options="dict.type.party_membership_type" :value="scope.row.userType"/>
            </template>
          </el-table-column>
          <el-table-column label="教师编号/学号" align="center" key="partyMemberId" prop="partyMemberId" v-if="columns[1].visible" width="120" />
          <el-table-column label="教研室/班级" align="center" key="nickName" prop="nickName" v-if="columns[2].visible" width="120" />
          <el-table-column label="党员姓名" align="center" key="userName" prop="userName" v-if="columns[3].visible" width="120"  />
          <el-table-column label="党员图片" align="center" key="imagePath" prop="imagePath" v-if="columns[8].visible" :show-overflow-tooltip="true" >
            <template slot-scope="scope">
              <image-preview :src="scope.row.imagePath" :width="50" :height="50"/>
            </template>
          </el-table-column>
          <el-table-column label="所属党组织" align="center" key="deptName" prop="dept.deptName" v-if="columns[4].visible" width="200" >
            <template slot-scope="{ row }">
              <BaseImage :src="row.dept.deptImg" :width="20" :height="20"/>
              <span class="text" style="vertical-align: middle;">{{ row.dept.deptName }}</span>
            </template>
          </el-table-column>
          <el-table-column label="党员职务" align="center" key="partyPost" prop="partyPost" v-if="columns[5].visible" width="120">
            <template slot-scope="scope">
              <div>
                <el-tag
                  v-for="(post, idx) in (scope.row.partyPost ? scope.row.partyPost.split(',') : [])"
                  :key="idx"
                  :type="['success', 'info', 'warning', 'danger', 'primary'][idx % 5]"
                  size="mini"
                  style="margin: 2px 2px 2px 0;"
                >
                  {{ post }}
                </el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="党员角色" align="center" key="partyRole" prop="partyRole" v-if="columns[6].visible" width="120"/>
          <el-table-column label="联系电话" align="center" key="phonenumber" prop="phonenumber" v-if="columns[7].visible" width="120" />
         
          <el-table-column label="所属民族" align="center" key="ethnicity" prop="ethnicity" v-if="columns[9].visible" width="120" >
          <template slot-scope="scope">
            <dict-tag :options="dict.type.party_member_ethnicity" :value="scope.row.ethnicity"/>
          </template>
          </el-table-column>
          <el-table-column label="党员学历" align="center" key="education" prop="education" v-if="columns[10].visible" width="120" >
          <template slot-scope="scope">
            <dict-tag :options="dict.type.party_member_education" :value="scope.row.education"/>
          </template>
          </el-table-column>
          <el-table-column label="党员类别" align="center" key="category" prop="category" v-if="columns[11].visible" width="120" >
          <template slot-scope="scope">
            <dict-tag :options="dict.type.category_informations" :value="scope.row.category"/>
          </template>
          </el-table-column>
          <el-table-column label="出生日期" align="center" key="birthDate" prop="birthDate" v-if="columns[12].visible" width="120">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.birthDate, '{y}-{m}-{d}') }}</span>
            </template>
          </el-table-column>
          <el-table-column label="申请入党日期" align="center" key="applicationDate" prop="applicationDate" v-if="columns[13].visible" width="130">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.applicationDate, '{y}-{m}-{d}') }}</span>
            </template>
          </el-table-column>
          <el-table-column label="成为积极分子日期" align="center" key="activistDate" prop="activistDate" v-if="columns[14].visible" width="130">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.activistDate, '{y}-{m}-{d}') }}</span>
            </template>
          </el-table-column>
          <el-table-column label="确定发展对象日期" align="center" key="developmentObjectDate" prop="developmentObjectDate" v-if="columns[15].visible" width="130">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.developmentObjectDate, '{y}-{m}-{d}') }}</span>
            </template>
          </el-table-column>
          <el-table-column label="成为预备党员日期" align="center" key="probationaryMemberDate" prop="probationaryMemberDate" v-if="columns[16].visible" width="130">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.probationaryMemberDate, '{y}-{m}-{d}') }}</span>
            </template>
          </el-table-column>
          <el-table-column label="转为正式党员日期" align="center" key="fullMemberDate" prop="fullMemberDate" v-if="columns[17].visible" width="130">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.fullMemberDate, '{y}-{m}-{d}') }}</span>
            </template>
          </el-table-column>
          <el-table-column label="是否为失联党员" align="center" key="isMissing" prop="isMissing" v-if="columns[18].visible" width="130">
            <template slot-scope="scope">
              <dict-tag :options="dict.type.party_yes_no" :value="scope.row.isMissing"/>
            </template>
          </el-table-column>
          <el-table-column label="是否为流动党员" align="center" key="isMobile" prop="isMobile" v-if="columns[19].visible" width="130">
            <template slot-scope="scope">
              <dict-tag :options="dict.type.party_yes_no" :value="scope.row.isMobile"/>
            </template>
          </el-table-column>
          <el-table-column label="党员账号状态" align="center" key="status" v-if="columns[20].visible">
            <template slot-scope="scope">
              <el-switch
                v-model="scope.row.status"
                active-value="0"
                inactive-value="1"
                @change="handleStatusChange(scope.row)"
              ></el-switch>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" align="center" prop="createTime" v-if="columns[21].visible" width="160">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.createTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="备注" align="center" key="remark" prop="remark" v-if="columns[22].visible" width="120" :show-overflow-tooltip="true"/>
          <el-table-column
            label="操作"
            align="center"
            width="160"
            class-name="small-padding fixed-width"
          >
            <template slot-scope="scope" v-if="scope.row.userId !== 1">
              <el-button
                size="mini"
                type="text"
                icon="el-icon-edit"
                @click="handleUpdate(scope.row)"
                v-hasPermi="['system:user:edit']"
              >修改</el-button>
              <el-button
                size="mini"
                type="text"
                icon="el-icon-delete"
                @click="handleDelete(scope.row)"
                v-hasPermi="['system:user:remove']"
              >删除</el-button>
              <el-button
                size="mini"
                type="text"
                icon="el-icon-key"
                @click="handleResetPwd(scope.row)"
                v-hasPermi="['system:user:resetPwd']"
              >重置密码</el-button>
              <el-button
                size="mini"
                type="text"
                icon="el-icon-circle-check"
                @click="handleAuthRole(scope.row)"
                v-hasPermi="['system:user:edit']"
              >分配角色</el-button>
            </template>
          </el-table-column>
        </el-table>

        <pagination
          v-show="total>0"
          :total="total"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="getList"
        />
      </div>
      </el-col>
    </el-row>

    <!-- 添加或修改用户配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="1000px" append-to-body custom-class="el-dialog-container">
      <el-form ref="form" :model="form" :rules="rules" label-width="130px">
        <el-row>
          <el-col :span="12">
            <el-form-item v-if="form.userId == undefined" prop="userName">
              <span slot="label">
                  <el-tooltip content="党员姓名即用户名" placement="top">
                    <i class="el-icon-question"></i>
                  </el-tooltip>
                党员姓名
              </span>
              <el-input v-model="form.userName" placeholder="请输入党员姓名" maxlength="30" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item v-if="form.userId == undefined" label="用户密码" prop="password">
              <el-input v-model="form.password" placeholder="请输入用户密码" type="password" maxlength="20" show-password/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phonenumber">
              <el-input v-model="form.phonenumber" placeholder="请输入联系电话" maxlength="11" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入联系邮箱" maxlength="50" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="教师编号/学号" prop="partyMemberId">
              <el-input v-model="form.partyMemberId" placeholder="请输入教师编号/学号" maxlength="20" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="教研室/班级" prop="nickName">
              <el-input v-model="form.nickName" placeholder="请输入所属教研室/班级" maxlength="30" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="党员性别">
              <el-select v-model="form.sex" placeholder="请选择党员性别">
                <el-option
                  v-for="dict in dict.type.sys_user_sex"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="归属党组织" prop="deptId">
              <treeselect v-model="form.deptId" :options="deptOptions" :show-count="true" placeholder="请选择归属党组织" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="党员职务">
              <el-select v-model="form.postIds" multiple placeholder="请先选择党组织" :disabled="!form.deptId">
                <el-option
                  v-for="item in postOptions"
                  :key="item.postId"
                  :label="item.postName"
                  :value="item.postId"
                  :disabled="item.status == 1"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="党员角色">
              <el-select v-model="form.roleIds" multiple placeholder="请先选择党组织" :disabled="!form.deptId">
                <el-option
                  v-for="item in roleOptions"
                  :key="item.roleId"
                  :label="item.roleName"
                  :value="item.roleId"
                  :disabled="item.status == 1"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="党员身份">
              <el-select v-model="form.userType" placeholder="请选择党员身份">
                <el-option
                  v-for="dict in dict.type.party_membership_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属民族">
              <el-select v-model="form.ethnicity" placeholder="请选择所属民族">
                <el-option
                  v-for="dict in dict.type.party_member_ethnicity"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="党员类别" prop="category">
              <el-select v-model="form.category" placeholder="请输入党员类别">
                <el-option
                  v-for="dict in dict.type.category_informations"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="党员学历">
              <el-select v-model="form.education" placeholder="请选择党员学历">
                <el-option
                  v-for="dict in dict.type.party_member_education"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="出生日期">
              <el-date-picker
                v-model="form.birthDate"
                :picker-options="birthDatePickerOptions"
                value-format="yyyy-MM-dd"
                type="date"
                placeholder="请选择出生日期"
              ></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="申请入党日期">
              <el-date-picker
                v-model="form.applicationDate"
                :picker-options="applicationDatePickerOptions"
                value-format="yyyy-MM-dd"
                type="date"
                placeholder="请选择申请入党日期"
              ></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="成为积极分子日期">
              <el-date-picker
                v-model="form.activistDate"
                :picker-options="activistDatePickerOptions"
                value-format="yyyy-MM-dd"
                type="date"
                placeholder="请选择成为积极分子日期"
              ></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="确定发展对象日期">
              <el-date-picker
                v-model="form.developmentObjectDate"
                :picker-options="developmentObjectDatePickerOptions"
                value-format="yyyy-MM-dd"
                type="date"
                placeholder="请选择确定发展对象日期"
              ></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="成为预备党员日期">
              <el-date-picker
                v-model="form.probationaryMemberDate"
                :picker-options="probationaryMemberDatePickerOptions"
                value-format="yyyy-MM-dd"
                type="date"
                placeholder="请选择成为预备党员日期"
              ></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="转为正式党员日期">
              <el-date-picker
                v-model="form.fullMemberDate"
                :picker-options="fullMemberDatePickerOptions"
                value-format="yyyy-MM-dd"
                type="date"
                placeholder="请选择转为正式党员日期"
              ></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="是否为失联党员">
              <el-radio-group v-model="form.isMissing">
                <el-radio
                  v-for="dict in dict.type.party_yes_no"
                  :key="dict.value"
                  :label="dict.value"
                >{{dict.label}}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否为流动党员">
              <el-radio-group v-model="form.isMobile">
                <el-radio
                  v-for="dict in dict.type.party_yes_no"
                  :key="dict.value"
                  :label="dict.value"
                >{{dict.label}}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="党员账号状态">
              <el-radio-group v-model="form.status">
                <el-radio
                  v-for="dict in dict.type.sys_normal_disable"
                  :key="dict.value"
                  :label="dict.value"
                >{{dict.label}}</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="党员图片" prop="imagePath">
              <image-upload v-model="form.imagePath"/>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 用户导入对话框 -->
    <el-dialog :title="upload.title" :visible.sync="upload.open" width="400px" append-to-body>
      <el-upload
        ref="upload"
        :limit="1"
        accept=".xlsx, .xls"
        :headers="upload.headers"
        :action="upload.url + '?updateSupport=' + upload.updateSupport"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :auto-upload="false"
        drag
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip text-center" slot="tip">
          <div class="el-upload__tip" slot="tip">
            <el-checkbox v-model="upload.updateSupport" /> 是否更新已经存在的用户数据
          </div>
          <span>仅允许导入xls、xlsx格式文件。</span>
          <el-link type="primary" :underline="false" style="font-size:12px;vertical-align: baseline;" @click="importTemplate">下载模板</el-link>
        </div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFileForm">确 定</el-button>
        <el-button @click="upload.open = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listUser, getUser, delUser, addUser, updateUser, resetUserPwd, changeUserStatus, deptTreeSelect, getRoleListByUserId, getPostListByUserId, listUserOptimized } from "@/api/system/user";
import { getToken } from "@/utils/auth";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import BaseImage from "@/components/Image/index.vue";

export default {
  name: "User",
  dicts: ['sys_normal_disable', 'sys_user_sex', 'party_member_ethnicity', 'party_member_education', 'category_informations', 'party_membership_type', 'party_yes_no'],
  components: {BaseImage, Treeselect },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 用户表格数据
      userList: null,
      // 弹出层标题
      title: "",
      // 党组织树选项
      deptOptions: undefined,
      // 是否显示弹出层
      open: false,
      // 党组织名称
      deptName: undefined,
      // 默认密码
      initPassword: undefined,
      // 日期范围
      dateRange: [],
      daterangeBirthDate: [],
      daterangeApplicationDate: [],
      daterangeActivistDate: [],
      daterangeDevelopmentObjectDate: [],
      daterangeProbationaryMemberDate: [],
      daterangeFullMemberDate: [],
      // 职务选项
      postOptions: [],
      // 角色选项
      roleOptionsAll: [],
      roleOptions: [],
      postOptionsAll: [],
      // 表单参数
      form: {
        birthDate: undefined,
        applicationDate: undefined,
        activistDate: undefined,
        developmentObjectDate: undefined,
        probationaryMemberDate: undefined,
        fullMemberDate: undefined
      },
      defaultProps: {
        children: "children",
        label: "label",
        deptImg: "deptImg"
      },
      // 用户导入参数
      upload: {
        // 是否显示弹出层（用户导入）
        open: false,
        // 弹出层标题（用户导入）
        title: "",
        // 是否禁用上传
        isUploading: false,
        // 是否更新已经存在的用户数据
        updateSupport: 0,
        // 设置上传的请求头部
        headers: { Authorization: "Bearer " + getToken() },
        // 上传的地址
        url: process.env.VUE_APP_BASE_API + "/system/user/importData"
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userName: undefined,
        phonenumber: undefined,
        partyMemberId: undefined,
        userType: undefined,
        ethnicity: undefined,
        education: undefined,
        category: undefined,
        isMissing: undefined,
        isMobile: undefined,
        status: undefined,
        deptId: undefined
      },
      // 列信息
      columns: [
        { key: 0, label: `党员身份`, visible: true },
        { key: 1, label: `教师编号/学号`, visible: true },
        { key: 2, label: `教研室/班级`, visible: true },
        { key: 3, label: `党员姓名`, visible: true },
        { key: 4, label: `所属党组织`, visible: true },
        { key: 5, label: `党员职务`, visible: true },
        { key: 6, label: `党员角色`, visible: true },
        { key: 7, label: `联系电话`, visible: true },
        { key: 8, label: `党员图片`, visible: true },
        { key: 9, label: `所属民族`, visible: true },
        { key: 10, label: `党员学历`, visible: true },
        { key: 11, label: `党员类别`, visible: true },
        { key: 12, label: `出生日期`, visible: true },
        { key: 13, label: `申请入党日期`, visible: true },
        { key: 14, label: `成为积极分子日期`, visible: true },
        { key: 15, label: `确定发展对象日期`, visible: true },
        { key: 16, label: `成为预备党员日期`, visible: true },
        { key: 17, label: `转为正式党员日期`, visible: true },
        { key: 18, label: `是否为失联党员`, visible: true },
        { key: 19, label: `是否为流动党员`, visible: true },
        { key: 20, label: `党员账号状态`, visible: true },
        { key: 21, label: `创建时间`, visible: true },
        { key: 22, label: `备注`, visible: true }
      ],
      // 表单校验
      rules: {
        userName: [
          { required: true, message: "党员姓名不能为空", trigger: "blur" },
          { min: 2, max: 20, message: '党员姓名长度必须介于 2 和 20 之间', trigger: 'blur' }
        ],
        nickName: [
          { required: true, message: "用户昵称不能为空", trigger: "blur" }
        ],
        password: [
          { required: true, message: "用户密码不能为空", trigger: "blur" },
          { min: 5, max: 20, message: '用户密码长度必须介于 5 和 20 之间', trigger: 'blur' },
          { pattern: /^[^<>"'|\\]+$/, message: "不能包含非法字符：< > \" ' \\\ |", trigger: "blur" }
        ],
        email: [
          {
            type: "email",
            message: "请输入正确的邮箱地址",
            trigger: ["blur", "change"]
          }
        ],
        phonenumber: [
          {
            pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
            message: "请输入正确的联系电话",
            trigger: "blur"
          }
        ]
      },
      // 出生日期限制：不能晚于当前日期减18年
      birthDatePickerOptions: {
        disabledDate: (time) => {
          return time.getTime() > Date.now() - 18 * 365 * 24 * 60 * 60 * 1000;
        },
      },
      // 申请入党日期限制：出生日期+18年后，且早于activistDate
      applicationDatePickerOptions: {
        disabledDate: (time) => {
          const birthDate = this.form.birthDate ? new Date(this.form.birthDate) : null;
          const activistDate = this.form.activistDate ? new Date(this.form.activistDate) : null;
          const minDate = birthDate ? birthDate.setFullYear(birthDate.getFullYear() + 18) : null;
          return (minDate && time.getTime() < minDate) ||
            (activistDate && time.getTime() >= activistDate.getTime()) ||
            time.getTime() > Date.now();
        },
      },
      // 成为积极分子日期限制：晚于applicationDate，早于developmentObjectDate
      activistDatePickerOptions: {
        disabledDate: (time) => {
          const applicationDate = this.form.applicationDate ? new Date(this.form.applicationDate) : null;
          const developmentObjectDate = this.form.developmentObjectDate ? new Date(this.form.developmentObjectDate) : null;
          return (applicationDate && time.getTime() <= applicationDate.getTime()) ||
            (developmentObjectDate && time.getTime() >= developmentObjectDate.getTime()) ||
            time.getTime() > Date.now();
        },
      },
      // 确定发展对象日期限制：晚于activistDate+1年，早于probationaryMemberDate
      developmentObjectDatePickerOptions: {
        disabledDate: (time) => {
          const activistDate = this.form.activistDate ? new Date(this.form.activistDate) : null;
          const probationaryMemberDate = this.form.probationaryMemberDate ? new Date(this.form.probationaryMemberDate) : null;
          const minDate = activistDate ? new Date(activistDate.setFullYear(activistDate.getFullYear() + 1)) : null;
          return (minDate && time.getTime() < minDate.getTime()) ||
            (probationaryMemberDate && time.getTime() >= probationaryMemberDate.getTime()) ||
            time.getTime() > Date.now();
        },
      },
      // 成为预备党员日期限制：晚于developmentObjectDate，早于fullMemberDate
      probationaryMemberDatePickerOptions: {
        disabledDate: (time) => {
          const developmentObjectDate = this.form.developmentObjectDate ? new Date(this.form.developmentObjectDate) : null;
          const fullMemberDate = this.form.fullMemberDate ? new Date(this.form.fullMemberDate) : null;
          return (developmentObjectDate && time.getTime() <= developmentObjectDate.getTime()) ||
            (fullMemberDate && time.getTime() >= fullMemberDate.getTime()) ||
            time.getTime() > Date.now();
        },
      },
      // 转为正式党员日期限制：晚于probationaryMemberDate+1年
      fullMemberDatePickerOptions: {
        disabledDate: (time) => {
          const probationaryMemberDate = this.form.probationaryMemberDate ? new Date(this.form.probationaryMemberDate) : null;
          const minDate = probationaryMemberDate ? new Date(probationaryMemberDate.setFullYear(probationaryMemberDate.getFullYear() + 1)) : null;
          return (minDate && time.getTime() < minDate.getTime()) ||
            time.getTime() > Date.now();
        },
      }
    };
  },
  watch: {
    // 根据名称筛选党组织树
    deptName(val) {
      this.$refs.tree.filter(val);
    },
    'form.deptId'(newVal) {
      // 当党组织变化时过滤职务列表和角色列表
      if (this.postOptionsAll.length > 0) {
        this.postOptions = this.filterPostsByDept(newVal);
        this.form.postIds = this.form.postIds.filter(postId =>
          this.postOptions.some(post => post.postId === postId)
        );
      }
      // 过滤角色
      if (this.roleOptionsAll.length > 0) {
        this.roleOptions = this.filterRolesByDept(newVal);
        this.form.roleIds = this.form.roleIds.filter(roleId =>
          this.roleOptions.some(role => role.roleId === roleId)
        );
      }
    }
  },
  created() {
    this.getList();
    this.getDeptTree();
    this.getConfigKey("sys.user.initPassword").then(response => {
      this.initPassword = response.msg;
    });
  },
  methods: {
    /** 查询用户列表 */
    async getList() {
      this.loading = true;
      this.addDateRange(this.queryParams, this.dateRange, "createTime");
      // 添加各个日期范围
      this.addDateRange(this.queryParams, this.daterangeBirthDate, "birthDate");
      this.addDateRange(this.queryParams, this.daterangeApplicationDate, "applicationDate");
      this.addDateRange(this.queryParams, this.daterangeActivistDate, "activistDate");
      this.addDateRange(this.queryParams, this.daterangeDevelopmentObjectDate, "developmentObjectDate");
      this.addDateRange(this.queryParams, this.daterangeProbationaryMemberDate, "probationaryMemberDate");
      this.addDateRange(this.queryParams, this.daterangeFullMemberDate, "fullMemberDate");
      
      try {
        // 使用优化后的接口，一次性获取所有数据
        const response = await listUserOptimized(this.queryParams);
        let rows = response.rows;
        
        // 补充部门信息
        this.fillDeptInfoForUsers(rows);
        
        // 处理返回的数据，确保字段名正确
        this.userList = rows.map(user => ({
          ...user,
          // 添加 userId 字段映射
          userId: user.user_id || user.userId,
          // 确保字段名一致
          partyPost: user.party_post || user.partyPost || '',
          partyRole: user.party_role || user.partyRole || '',
          // 确保其他字段正确映射
          userName: user.user_name || user.userName || '',
          nickName: user.nick_name || user.nickName || '',
          partyMemberId: user.party_member_id || user.partyMemberId || '',
          birthDate: user.birth_date || user.birthDate || '',
          applicationDate: user.application_date || user.applicationDate || '',
          activistDate: user.activist_date || user.activistDate || '',
          developmentObjectDate: user.development_object_date || user.developmentObjectDate || '',
          probationaryMemberDate: user.probationary_member_date || user.probationaryMemberDate || '',
          fullMemberDate: user.full_member_date || user.fullMemberDate || '',
          isMissing: user.is_missing || user.isMissing || '',
          isMobile: user.is_mobile || user.isMobile || '',
          imagePath: user.image_path || user.imagePath || '',
          createTime: user.create_time || user.createTime || '',
          loginDate: user.login_date || user.loginDate || '',
          loginIp: user.login_ip || user.loginIp || '',
          userType: user.user_type || user.userType || ''
        }));
        
        this.total = response.total;
        this.loading = false;
      } catch (error) {
        console.error('获取用户列表失败:', error);
        // 降级到原有方案
        await this.getListLegacy();
      }
    },
    /** 查询用户列表 */
    async getListLegacy() {
      this.loading = true;
      this.addDateRange(this.queryParams, this.dateRange, "createTime");
      // 添加各个日期范围
      this.addDateRange(this.queryParams, this.daterangeBirthDate, "birthDate");
      this.addDateRange(this.queryParams, this.daterangeApplicationDate, "applicationDate");
      this.addDateRange(this.queryParams, this.daterangeActivistDate, "activistDate");
      this.addDateRange(this.queryParams, this.daterangeDevelopmentObjectDate, "developmentObjectDate");
      this.addDateRange(this.queryParams, this.daterangeProbationaryMemberDate, "probationaryMemberDate");
      this.addDateRange(this.queryParams, this.daterangeFullMemberDate, "fullMemberDate");
      console.log(this.queryParams);
      // 1. 获取用户基础信息
      const response = await listUser(this.queryParams);
      let rows = response.rows;
      // 2. 填充角色和岗位
      await this.fillUserRolesAndPosts(rows);
      // 3. 在这里补充 dept 信息
      this.fillDeptInfoForUsers(rows);
      this.userList = rows.map(user => ({
        ...user,
        partyPost: user.posts?.map(post => post.postName).join(',') || '',
        partyRole: user.roles?.map(role => role.roleName).join(',') || ''
      }));
      this.total = response.total;
      this.loading = false;
    },

    // 填充用户的角色和岗位
    async fillUserRolesAndPosts(userList) {
      for (const user of userList) {
        // 获取角色
        if (typeof getRoleListByUserId === 'function') {
          const roleRes = await getRoleListByUserId(user.userId);
          // 只保留用户实际拥有的角色（flag为true的角色）
          user.roles = (roleRes.data || []).filter(role => role.flag === true);
        }
        // 获取岗位
        if (typeof getPostListByUserId === 'function') {
          const postRes = await getPostListByUserId(user.userId);
          user.posts = postRes.data || [];
        }
      }
    },
    // 新增方法：根据 deptId 给用户列表补充 dept 字段
    fillDeptInfoForUsers(list) {
      list.forEach(item => {
        // 如果后端已经返回了部门信息，直接使用
        if (item.dept_name || item.deptName) {
          item.dept = {
            deptImg: item.dept_img || item.deptImg || '',
            deptName: item.dept_name || item.deptName || '',
            leader: item.leader || ''
          };
          return;
        }
        
        // 原有的从 deptOptions 查找逻辑保持不变
        function findDeptById(options, id) {
          if (!options || !Array.isArray(options)) return null;
          for (let item of options) {
            if (item.id === id) return item;
            if (item.children) {
              const found = findDeptById(item.children, id);
              if (found) return found;
            }
          }
          return null;
        }
        
        if (this.deptOptions && this.deptOptions.length > 0) {
          const dept = findDeptById(this.deptOptions, item.deptId);
          item.dept = dept
            ? { deptImg: dept.deptImg || '', deptName: dept.label || dept.deptName || '' }
            : { deptImg: '', deptName: '未知部门' };
        } else {
          item.dept = { deptImg: '', deptName: '部门加载中...' };
        }
      });
    },
    /** 查询党组织下拉树结构 */
    getDeptTree() {
      deptTreeSelect().then(response => {
        this.deptOptions = response.data;
        console.log(response.data);
        if (this.userList && this.userList.length > 0) {
          this.fillDeptInfoForUsers(this.userList);
        }
      });
    },
    // 筛选节点
    filterNode(value, data) {
      if (!value) return true;
      return data.label.indexOf(value) !== -1;
    },
    // 节点单击事件
    handleNodeClick(data) {
      this.queryParams.deptId = data.id;
      this.handleQuery();
    },
    // 用户状态修改
    handleStatusChange(row) {
      let text = row.status === "0" ? "启用" : "停用";
      this.$modal.confirm('确认要"' + text + '""' + row.userName + '"用户吗？').then(function() {
        return changeUserStatus(row.userId, row.status);
      }).then(() => {
        this.$modal.msgSuccess(text + "成功");
      }).catch(function() {
        row.status = row.status === "0" ? "1" : "0";
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        userId: undefined,
        deptId: undefined,
        userName: undefined,
        nickName: undefined,
        password: undefined,
        phonenumber: undefined,
        email: undefined,
        sex: undefined,
        status: "0",
        isMissing: "1",
        isMobile: "1",
        remark: "无",
        postIds: [],
        roleIds: []
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [];
      this.daterangeBirthDate = [];
      this.daterangeApplicationDate = [];
      this.daterangeActivistDate = [];
      this.daterangeDevelopmentObjectDate = [];
      this.daterangeProbationaryMemberDate = [];
      this.daterangeFullMemberDate = [];
      this.resetForm("queryForm");
      this.queryParams.deptId = undefined;
      this.$refs.tree.setCurrentKey(null);
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.userId);
      this.single = selection.length != 1;
      this.multiple = !selection.length;
    },
    // 根据党组织过滤职务的方法
    filterPostsByDept(deptId) {
      if (!deptId) return [];
      return this.postOptionsAll.filter(post =>
        post.deptIds && post.deptIds.includes(Number(deptId))
      );
    },
    filterRolesByDept(deptId) {
      if (!deptId) return [];
      return this.roleOptionsAll.filter(role => role.deptId === Number(deptId));
    },
    handleAdd() {
      this.reset();
      getUser().then(response => {
        this.postOptionsAll = response.posts;
        this.roleOptionsAll = response.roles;
        this.open = true;
        this.title = "添加用户";
        this.form.password = this.initPassword;
        this.roleOptions = this.form.deptId ? this.filterRolesByDept(this.form.deptId) : [];
        this.postOptions = this.form.deptId ? this.filterPostsByDept(this.form.deptId) : [];
      });
    },
    handleUpdate(row) {
      this.reset();
      const userId = row.userId || this.ids;
      getUser(userId).then(response => {
        this.form = response.data;
        this.postOptionsAll = response.posts;
        this.roleOptionsAll = response.roles;
        this.$set(this.form, "postIds", response.postIds);
        this.$set(this.form, "roleIds", response.roleIds);
        this.open = true;
        this.title = "修改用户";
        this.form.password = "";
        this.postOptions = this.form.deptId ? this.filterPostsByDept(this.form.deptId) : [];
        this.roleOptions = this.form.deptId ? this.filterRolesByDept(this.form.deptId) : [];
        this.form.roleIds = this.form.roleIds.filter(roleId => this.roleOptions.some(role => role.roleId === roleId));
      });
    },
    /** 重置密码按钮操作 */
    handleResetPwd(row) {
      this.$prompt('请输入"' + row.userName + '"的新密码', "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        closeOnClickModal: false,
        inputPattern: /^.{5,20}$/,
        inputErrorMessage: "用户密码长度必须介于 5 和 20 之间",
        inputValidator: (value) => {
          if (/<|>|"|'|\||\\/.test(value)) {
            return "不能包含非法字符：< > \" ' \\\ |"
          }
        },
      }).then(({ value }) => {
          resetUserPwd(row.userId, value).then(response => {
            this.$modal.msgSuccess("修改成功，新密码是：" + value);
          });
        }).catch(() => {});
    },
    /** 分配角色操作 */
    handleAuthRole: function(row) {
      const userId = row.userId;
      this.$router.push("/system/user-auth/role/" + userId);
    },
    /** 提交按钮 */
    submitForm: function() {
      this.$refs["form"].validate(valid => {
        if (valid && this.validateDates()) {
          if (this.form.userId != undefined) {
            updateUser(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addUser(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
          console.log("提交表单", this.form);
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const userIds = row.userId || this.ids;
      this.$modal.confirm('是否确认删除用户编号为"' + userIds + '"的数据项？').then(function() {
        return delUser(userIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    // 校验日期规则
    validateDates() {
      const dates = [
        this.form.birthDate,
        this.form.applicationDate,
        this.form.activistDate,
        this.form.developmentObjectDate,
        this.form.probationaryMemberDate,
        this.form.fullMemberDate,
      ].map((date) => (date ? new Date(date) : null));
      // 检查顺序（仅对非空日期校验）
      for (let i = 1; i < dates.length; i++) {
        if (dates[i] && dates[i - 1] && dates[i] <= dates[i - 1]) {
          this.$message.error("日期顺序不正确");
          return false;
        }
      }
      // 检查年龄
      if (dates[0] && dates[1]) {
        const ageInYears = (dates[1] - dates[0]) / (365 * 24 * 60 * 60 * 1000);
        if (ageInYears < 18) {
          this.$message.error("申请入党时年龄必须大于等于18岁");
          return false;
        }
      }
      // 检查积极分子到发展对象的间隔
      if (dates[2] && dates[3]) {
        const intervalInYears = (dates[3] - dates[2]) / (365 * 24 * 60 * 60 * 1000);
        if (intervalInYears < 1) {
          this.$message.error("积极分子到发展对象需至少1年");
          return false;
        }
      }
      // 检查预备党员到正式党员的间隔
      if (dates[4] && dates[5]) {
        const intervalInYears = (dates[5] - dates[4]) / (365 * 24 * 60 * 60 * 1000);
        if (intervalInYears < 1) {
          this.$message.error("预备党员到正式党员需至少1年");
          return false;
        }
      }
      return true;
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/user/export', {
        ...this.queryParams
      }, `user_${new Date().getTime()}.xlsx`)
    },
    /** 导入按钮操作 */
    handleImport() {
      this.upload.title = "用户导入";
      this.upload.open = true;
    },
    /** 下载模板操作 */
    importTemplate() {
      this.download('system/user/importTemplate', {
      }, `user_template_${new Date().getTime()}.xlsx`)
    },
    // 文件上传中处理
    handleFileUploadProgress(event, file, fileList) {
      this.upload.isUploading = true;
    },
    // 文件上传成功处理
    handleFileSuccess(response, file, fileList) {
      this.upload.open = false;
      this.upload.isUploading = false;
      this.$refs.upload.clearFiles();
      this.$alert("<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response.msg + "</div>", "导入结果", { dangerouslyUseHTMLString: true });
      this.getList();
    },
    // 提交上传文件
    submitFileForm() {
      this.$refs.upload.submit();
    }
  }
};
</script>
