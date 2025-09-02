<template>
  <div class="app-container">
    <el-row :gutter="20" class="full-height-row">
      <!--党组织数据-->
      <el-col :span="5" :xs="24" class="dept-col">
 
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
              node-key="branchId"
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
      </el-col>
      <!--学习活动数据-->
      <el-col :span="19" :xs="24" class="content-col">
        <div class="content-container" style="height: calc(100vh - 120px); overflow-y: auto; overflow-x: hidden;">
          <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
            <el-form-item label="半月学主题" prop="title">
              <el-input
                v-model="queryParams.title"
                placeholder="请输入半月学主题"
                clearable
                @keyup.enter.native="handleQuery"
              />
            </el-form-item>
            <el-form-item label="负责人" prop="organizerId">
              <el-input
                v-model="queryParams.organizerId"
                placeholder="请输入负责人"
                clearable
                @keyup.enter.native="handleQuery"
              />
            </el-form-item>
            <el-form-item label="所属部门" prop="branchId">
              <el-input
                v-model="queryParams.branchId"
                placeholder="请输入所属部门"
                clearable
                @keyup.enter.native="handleQuery"
              />
            </el-form-item>
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker clearable
                v-model="queryParams.startTime"
                type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="请选择开始时间">
              </el-date-picker>
            </el-form-item>
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker clearable
                v-model="queryParams.endTime"
                type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="请选择结束时间">
              </el-date-picker>
            </el-form-item>
            <el-form-item label="半月学地点" prop="location">
              <el-input
                v-model="queryParams.location"
                placeholder="请输入半月学地点"
                clearable
                @keyup.enter.native="handleQuery"
              />
            </el-form-item>
            <el-form-item label="活动形式" prop="form">
              <el-select v-model="queryParams.form" placeholder="请选择活动形式" clearable>
                <el-option
                  v-for="dict in dict.type.learning_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
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
                v-hasPermi="['learning:learning:add']"
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
                v-hasPermi="['learning:learning:edit']"
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
                v-hasPermi="['learning:learning:remove']"
              >删除</el-button>
            </el-col>
            <el-col :span="1.5">
              <el-button
                type="warning"
                plain
                icon="el-icon-download"
                size="mini"
                @click="handleExport"
                v-hasPermi="['learning:learning:export']"
              >导出</el-button>
            </el-col>
            <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
          </el-row>

          <el-table v-loading="loading" :data="learningList" @selection-change="handleSelectionChange" :expand-row-keys="expandedRows" row-key="id">
            <el-table-column type="expand">
              <template slot-scope="props">
                <ExpandCard
                  :row-data="props.row"
                  participants-title="参加人员详情"
                  :participants="props.row.participantDetails"
                  edit-button-text="管理人员"
                  :show-batch-button="true"
                  batch-button-text="批量考勤"
                  attachments-title="相关附件"
                  :attachments="getAttachmentList(props.row.attachments)"
                  attendance-title="出勤情况记录"
                  :total-count="props.row.participantDetails ? props.row.participantDetails.length : 0"
                  :attendance-count="getAttendanceCount(props.row.participantDetails, '0')"
                  :absent-count="getAttendanceCount(props.row.participantDetails, '1')"
                  :leave-count="getAttendanceCount(props.row.participantDetails, '2')"
                  :late-count="getAttendanceCount(props.row.participantDetails, '3')"
                  :attendance-description="props.row.attendanceStatus"
                  @edit-participants="handleEditParticipants"
                  @batch-action="handleBatchAttendance"
                  @upload-attachment="handleUploadAttachment"
                  @download-attachment="downloadAttachment"
                  @delete-attachment="deleteAttachment"
                >
                  <!-- 自定义参加人员详情表格 -->
                  <template #participants="{ data }">
                    <div v-if="data.participantDetails && data.participantDetails.length > 0">
                      <el-table 
                        :data="data.participantDetails" 
                        size="small"
                        stripe
                        style="width: 100%;"
                      >
                        <el-table-column prop="studentName" label="姓名" align="center" />
                        <el-table-column prop="signInStatus" label="签到状态" align="center">
                          <template slot-scope="scope">
                            <dict-tag :options="dict.type.check_status" :value="scope.row.signInStatus"/>
                          </template>
                        </el-table-column>
                        <el-table-column prop="attendanceStatus" label="出勤状态" align="center">
                          <template slot-scope="scope">
                            <dict-tag :options="dict.type.attendance_status" :value="scope.row.attendanceStatus"/>
                          </template>
                        </el-table-column>

                        <el-table-column prop="totalScore" label="总积分" align="center">
                          <template slot-scope="scope">
                            <span>
                              {{ scope.row.totalScore || 0 }}
                            </span>
                          </template>
                        </el-table-column>

                        <el-table-column prop="performanceScore" label="本次表现积分" align="center">
                          <template slot-scope="scope">
                            <span>
                              {{ scope.row.performanceScore > 0 ? '+' : '' }}{{ scope.row.performanceScore }}
                            </span>
                          </template>
                        </el-table-column>
                        
                        <el-table-column label="学习强国积分" align="center">
                          <template slot-scope="scope">
                            <span>{{ scope.row.xuexiqiangguoPrev }}</span>
                            <i class="el-icon-right"></i>
                            <span>{{ scope.row.xuexiqiangguoCurr }}</span>
                            <span>
                              ({{ scope.row.xuexiqiangguoDiff > 0 ? '+' : '' }}{{ scope.row.xuexiqiangguoDiff }})
                            </span>
                          </template>
                        </el-table-column>
                        
                        <el-table-column label="学习截图" align="center">
                          <template slot-scope="scope">
                            <div v-if="scope.row.xuexiqiangguoImage">
                              <el-image
                                :src="getFullImageUrl(scope.row.xuexiqiangguoImage)"
                                :preview-src-list="[getFullImageUrl(scope.row.xuexiqiangguoImage)]"
                                fit="cover"
                                style="width: 40px; height: 40px;"
                              >
                                <div slot="error">
                                  <i class="el-icon-picture-outline"></i>
                                </div>
                              </el-image>
                            </div>
                            <div v-else>
                              <i class="el-icon-picture"></i>
                              <div>暂无截图</div>
                            </div>
                          </template>
                        </el-table-column>
                        
                        <el-table-column prop="remark" label="备注" align="center">
                          <template slot-scope="scope">
                            {{ scope.row.remark || '无' }}
                          </template>
                        </el-table-column>

                        <el-table-column label="操作" align="center">
                          <template slot-scope="scope">
                            <el-button 
                              type="text" 
                              size="mini" 
                              icon="el-icon-edit"
                              @click="handleEditParticipant(props.row, scope.row)"
                            >
                              编辑
                            </el-button>
                            <el-button 
                              type="text" 
                              size="mini" 
                              icon="el-icon-delete"
                              @click="handleRemoveParticipant(props.row, scope.row)"
                            >
                              移除
                            </el-button>
                          </template>
                        </el-table-column>
                      </el-table>
                    </div>
                    <div v-else>
                      <i class="el-icon-user"></i>
                      <span>暂无参加人员</span>
                      <el-button 
                        type="text" 
                        size="mini" 
                        @click="handleEditParticipants(data)"
                      >
                        添加人员
                      </el-button>
                    </div>
                  </template>
                </ExpandCard>
              </template>
            </el-table-column>
            <el-table-column type="selection" width="55" align="center" />
            <el-table-column label="半月学主题" align="center" prop="title" :show-overflow-tooltip="true"/>
            <el-table-column label="所属部门" align="center" prop="branchId" width="120">
              <template slot-scope="{ row }">
                  <BaseImage :src="row.dept && row.dept.deptImg ? row.dept.deptImg : ''" :width="20" :height="20"/>
                  <span class="text" style="vertical-align: middle;">{{ row.dept ? row.dept.deptName : row.branchId }}</span>
              </template>
            </el-table-column>
            <el-table-column label="负责人" align="center" prop="organizerName"/>
            <el-table-column label="半月学地点" align="center" prop="location" />
            <el-table-column label="活动形式" align="center" prop="form">
              <template slot-scope="scope">
                <dict-tag :options="dict.type.learning_type" :value="scope.row.form"/>
              </template>
            </el-table-column>
            <el-table-column label="学习时长" align="center" prop="duration" />
            <el-table-column label="内容介绍" align="center" prop="description" :show-overflow-tooltip="true">
              <template slot-scope="{ row }">
                <div v-html="row.description"></div>
              </template>
            </el-table-column>
            <el-table-column label="半月学状态" align="center" prop="status">
              <template slot-scope="scope">
                <dict-tag :options="dict.type.meetings_status" :value="scope.row.status"/>
              </template>
            </el-table-column>
            <el-table-column label="开始时间" align="center" prop="startTime" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <span>{{ parseTime(scope.row.startTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
              </template>
            </el-table-column>

            <el-table-column label="结束时间" align="center" prop="endTime" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <span>{{ parseTime(scope.row.endTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" align="center">
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  type="text"
                  icon="el-icon-edit"
                  @click="handleUpdate(scope.row)"
                  v-hasPermi="['learning:learning:edit']"
                >修改</el-button>
                <el-button
                  size="mini"
                  type="text"
                  icon="el-icon-delete"
                  @click="handleDelete(scope.row)"
                  v-hasPermi="['learning:learning:remove']"
                >删除</el-button>
                <el-button 
                  size="mini" 
                  type="text" 
                  icon="el-icon-bell"
                  @click="handleSendNotification(scope.row)"
                >发布通知</el-button>
                <el-button 
                  size="mini" 
                  type="text" 
                  icon="el-icon-edit-outline"
                  @click="handleSignIn(scope.row)"
                >发布签到</el-button>
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

    <!-- 添加或修改半月学对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="半月学主题" prop="title">
          <el-input v-model="form.title" placeholder="请输入半月学主题" />
        </el-form-item>
        <el-form-item label="负责人" prop="organizerId">
          <el-select
              v-model="form.organizerId"
              placeholder="请选择负责人"
              clearable
              @change="handleLeaderChange"
            >
              <el-option
                v-for="user in userList"
                :key="user.userId"
                :label="user.userName"
                :value="user.userId"
              />
            </el-select>
        </el-form-item>
        <el-form-item label="所属部门" prop="branchId">
          <treeselect v-model="form.branchId" :options="deptOptions" :show-count="true" placeholder="请选择所属部门" />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker clearable
            v-model="form.startTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择开始时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker clearable
            v-model="form.endTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择结束时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="半月学地点" prop="location">
          <el-input v-model="form.location" placeholder="请输入半月学地点" />
        </el-form-item>
        <el-form-item label="活动形式" prop="form">
          <el-select v-model="form.form" placeholder="请选择活动形式">
            <el-option
              v-for="dict in dict.type.learning_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="学习时长" prop="duration">
          <el-input v-model="form.duration" placeholder="请输入学习时长" />
        </el-form-item>
        <el-form-item label="内容介绍" prop="description">
          <editor v-model="form.description" :min-height="192"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 管理参加人员对话框 -->
    <el-dialog title="管理参加人员" :visible.sync="participantsDialogVisible" width="1200px" append-to-body>
        <!-- 添加人员 -->
        <el-form :inline="true">
          <el-form-item label="选择人员">
            <el-select
              v-model="selectedUsers"
              multiple
              filterable
              placeholder="请选择要添加的人员"
              style="width: 350px;"
              clearable
            >
              <el-option
                v-for="user in availableUsers"
                :key="user.userId"
                :label="user.userName"
                :value="user.userId"
              >
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-plus" @click="addParticipants">添加人员</el-button>
          </el-form-item>
        </el-form>
        
        <!-- 当前参加人员列表 -->
        <div style="margin-top: 20px;">
          <el-table :data="currentParticipants" border size="small" :row-style="{height: '80px'}" :cell-style="{padding: '8px 0'}">
            <el-table-column type="index" label="序号" width="50" align="center"/>
            <el-table-column prop="studentName" label="姓名" width="80" align="center" />
            <el-table-column label="签到状态" width="110"  align="center">
              <template slot-scope="scope">
                <el-select v-model="scope.row.signInStatus" placeholder="请选择签到状态" size="small">
                  <el-option
                    v-for="dict in dict.type.check_status"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                  ></el-option>
                </el-select>
              </template>
            </el-table-column>
            <el-table-column prop="attendanceStatus" label="出勤状态" width="100" align="center">
              <template slot-scope="scope">
                <el-select v-model="scope.row.attendanceStatus" size="mini">
                  <el-option 
                    v-for="dict in dict.type.attendance_status" 
                    :key="dict.value" 
                    :label="dict.label" 
                    :value="dict.value"
                  ></el-option>
                </el-select>
              </template>
            </el-table-column>
            <el-table-column prop="totalScore" label="总积分" align="center">
              <template slot-scope="scope">
                <el-input-number 
                  v-model="scope.row.totalScore" 
                  size="mini" 
                ></el-input-number>
              </template>
            </el-table-column>

            <el-table-column prop="performanceScore" label="本次表现积分"  align="center">
              <template slot-scope="scope">
                <el-input-number 
                  v-model="scope.row.performanceScore" 
                  size="mini" 
                  :min="-100" 
                  :max="100"
                ></el-input-number>
              </template>
            </el-table-column>
            
            <el-table-column label="学习强国积分" width="240" align="center">
              <template slot-scope="scope">
                <el-row :gutter="10" type="flex" align="middle">
                  <el-col :span="18">
                    <div style="margin-bottom: 10px;">
                      <span style="display: inline-block; width: 50px;">上次</span>
                      <el-input-number
                        v-model="scope.row.xuexiqiangguoPrev"
                        size="mini"
                        :min="0"
                        style="width: calc(100% - 50px);"
                      ></el-input-number>
                    </div>
                    <div>
                      <span style="display: inline-block; width: 50px;">当前</span>
                      <el-input-number
                        v-model="scope.row.xuexiqiangguoCurr"
                        size="mini"
                        :min="0"
                        @change="calculateDiff(scope.row)"
                        style="width: calc(100% - 50px);"
                      ></el-input-number>
                    </div>
                  </el-col>
                  <el-col :span="6">
                    <span>{{ scope.row.xuexiqiangguoDiff > 0 ? '+' : '' }}{{ scope.row.xuexiqiangguoDiff }}</span>
                  </el-col>
                </el-row>
              </template>
            </el-table-column>
            
            <el-table-column label="截图" width="60" align="center">
              <template slot-scope="scope">
                <div>
                  <el-upload
                    :action="uploadUrl"
                    :headers="uploadHeaders"
                    :show-file-list="false"
                    :on-success="(response) => handleImageUploadSuccess(response, scope.row)"
                    :before-upload="beforeImageUpload"
                    accept="image/*"
                  >
                    <el-button size="mini" type="text" icon="el-icon-upload">上传</el-button>
                  </el-upload>
                  <el-button 
                    v-if="scope.row.xuexiqiangguoImage"
                    size="mini" 
                    type="text"
                    icon="el-icon-view"
                    @click="previewImage(scope.row.xuexiqiangguoImage)"
                    style="margin-top: 2px;"
                  >
                    查看
                  </el-button>
                </div>
              </template>
            </el-table-column>

            <el-table-column prop="remark" label="备注" align="center">
              <template slot-scope="scope">
                <el-input 
                  v-model="scope.row.remark" 
                  size="mini" 
                  placeholder="请输入备注"
                />
              </template>
            </el-table-column>
            
            <el-table-column label="操作" width="60" align="center">
              <template slot-scope="scope">
                <el-button 
                  size="mini" 
                  type="text" 
                  icon="el-icon-delete"
                  @click="removeParticipant(scope.$index)"
                >
                  移除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      
        <div slot="footer">
          <el-button @click="participantsDialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="saveParticipants">保 存</el-button>
        </div>
      </el-dialog>

    <!-- 图片预览对话框 -->
    <el-dialog title="学习强国截图" :visible.sync="imagePreviewVisible" width="600px" append-to-body>
      <div class="image-preview-container">
        <img :src="previewImageUrl" style="width: 100%; max-height: 400px; object-fit: contain;" />
      </div>
    </el-dialog>

    <!-- 批量考勤设置对话框 -->
    <el-dialog title="批量考勤" :visible.sync="batchAttendanceVisible" width="600px" append-to-body>
      <el-card shadow="never" style="margin-bottom: 20px;">
        <template #header>
          <span>出勤状态设置</span>
        </template>

        <el-row :gutter="10" type="flex" justify="space-between">
          <el-col :span="5">
            <el-select
              v-model="batchAttendanceForm.status"
              placeholder="请选择出勤状态"
              style="width: 100%;"
            >
              <el-option
                v-for="dict in dict.type.attendance_status"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-col>
          <el-col :span="5">
            <el-button
              type="primary"
              size="middle"
              style="width: 100%;"
              @click="applyBatchAttendance"
            >
              应用设置
            </el-button>
          </el-col>
        </el-row>
      </el-card>

      <el-card shadow="never" style="margin-bottom: 20px;">
        <template #header>
          <span>签到状态设置</span>
        </template>

        <el-row :gutter="10" type="flex" justify="space-between">
          <el-col :span="6">
            <el-select
              v-model="batchAttendanceForm.signInStatus"
              placeholder="请选择签到状态"
              style="width: 100%;"
            >
              <el-option
                v-for="dict in dict.type.check_status"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-col>
          <el-col :span="5">
            <el-button
              type="success"
              size="middle"
              style="width: 100%;"
              @click="applyBatchSignIn"
            >
              一键设置
            </el-button>
          </el-col>
        </el-row>
      </el-card>

      <el-card shadow="never">
        <template #header>
          <span>表现积分设置</span>
        </template>
        
        <el-row :gutter="10" type="flex" justify="space-between">
          <el-col :span="6">
            <el-input-number 
              v-model="batchAttendanceForm.score" 
              :min="-100" 
              :max="100"
              placeholder="统一设置本次表现积分"
              style="width: 100%;"
            ></el-input-number>
          </el-col>
          <el-col :span="5">
            <el-button
              type="warning"
              size="middle"
              style="width: 100%;"
              @click="applyBatchScore"
            >
              应用积分
            </el-button>
          </el-col>
        </el-row>
      </el-card>
      
      <template #footer>
        <el-button @click="batchAttendanceVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitBatchAttendance">保 存</el-button>
      </template>
    </el-dialog>

    <!-- 上传附件对话框 -->
    <el-dialog title="上传附件" :visible.sync="uploadDialogVisible" width="600px" append-to-body>
      <div class="upload-container">
        <el-upload
          ref="upload"
          :action="uploadUrl"
          :headers="uploadHeaders"
          :file-list="fileList"
          :on-success="handleUploadSuccess"
          :on-error="handleUploadError"
          :on-remove="handleRemoveFile"
          :before-upload="beforeUpload"
          :auto-upload="false"
          multiple
          drag
        >
          <i class="el-icon-upload"></i>
          <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
          <div class="el-upload__tip" slot="tip">
            支持多文件上传，单个文件大小不超过10MB
          </div>
        </el-upload>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="uploadDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitUpload">确 定</el-button>
      </div>
    </el-dialog>
    <!-- 单个参与者编辑对话框 -->
    <el-dialog title="编辑参与者信息" :visible.sync="participantEditVisible" width="600px" append-to-body>
      <el-form ref="participantEditForm" :model="currentParticipant" label-width="130px">
        <el-form-item label="姓名">
          <el-input v-model="currentParticipant.studentName" disabled></el-input>
        </el-form-item>
        <el-form-item label="签到状态">
          <el-select v-model="currentParticipant.signInStatus" placeholder="请选择签到状态">
            <el-option
              v-for="dict in dict.type.check_status"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="出勤状态">
          <el-select v-model="currentParticipant.attendanceStatus" placeholder="请选择出勤状态">
            <el-option 
              v-for="dict in dict.type.attendance_status" 
              :key="dict.value" 
              :label="dict.label" 
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="总积分">
            <el-input-number 
              v-model="currentParticipant.totalScore" 
              :min="0"
            ></el-input-number>
        </el-form-item>
        <el-form-item label="本次表现积分">
          <el-input-number 
            v-model="currentParticipant.performanceScore" 
            :min="-100" 
            :max="100"
          ></el-input-number>
        </el-form-item>
        
        <el-form-item label="学习强国上次积分" >
          <el-input-number 
            v-model="currentParticipant.xuexiqiangguoPrev" 
            :min="0"
          ></el-input-number>
        </el-form-item>
        
        <el-form-item label="学习强国当前积分">
          <el-input-number 
            v-model="currentParticipant.xuexiqiangguoCurr" 
            :min="0"
          ></el-input-number>
        </el-form-item>
        
        <el-form-item label="学习强国截图">
          <el-upload
            class="upload-demo"
            :action="upload.url"
            :headers="upload.headers"
            :file-list="[]"
            :on-success="(response) => handleImageUploadSuccess(response, currentParticipant)"
            :before-upload="beforeImageUpload"
            :limit="1"
            accept="image/*">
            <el-button size="small" type="primary">点击上传</el-button>
            <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过2MB</div>
          </el-upload>
          <div v-if="currentParticipant.xuexiqiangguoImage" class="uploaded-image">
            <img :src="getFullImageUrl(currentParticipant.xuexiqiangguoImage)" style="width: 100px; height: 100px; object-fit: cover;" />
          </div>
        </el-form-item>

        <el-form-item label="备注">
          <el-input 
            v-model="currentParticipant.remark" 
            type="textarea" 
            :rows="3"
            placeholder="请输入备注信息"
          ></el-input>
        </el-form-item>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="participantEditVisible = false">取 消</el-button>
        <el-button type="primary" @click="saveParticipantEdit">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 引入通知组件 -->
    <ActivityNotification
      ref="activityNotification"
      activityType="2"
      :activityId="currentNotificationId"
      :activityTitle="currentNotificationTitle"
      :participants="currentNotificationParticipants"
      :activityStartTime="currentNotificationStartTime"
      :activityEndTime="currentNotificationEndTime"
      :activityAttachments="currentNotificationAttachments"
    />
  </div>
</template>

<script>
import { listLearning, getLearning, delLearning, addLearning, updateLearning } from "@/api/learning/learning";
import { listParticipant, addParticipant, updateParticipant, delParticipant } from "@/api/participant/learningparticipant";
import { deptTreeSelect } from "@/api/system/user";
import { listUser } from "@/api/system/user";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import BaseImage from "@/components/Image/index.vue";
import Editor from '@/components/Editor';
import attachmentMixin from '@/mixins/attachmentMixin';
import ExpandCard from '@/components/ExpandCard/index.vue';
import ActivityNotification from "@/components/ActivityNotification";

export default {
  name: "Learning",
  dicts: ['learning_type','attendance_status','check_status','meetings_status'],
  components: {
    Treeselect,
    BaseImage,
    Editor,
    ExpandCard,
    ActivityNotification
  },
  mixins: [attachmentMixin],
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
      // learning表格数据
      learningList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 部门名称
      deptName: undefined,
      // 部门树选项
      deptOptions: undefined,
      // 用户列表
      userList: [],
      // 展开的行
      expandedRows: [],
      // 参加人员对话框
      participantsDialogVisible: false,
      participantEditVisible: false,
      currentParticipant: {},
      currentLearningRow: null,
      // 通知相关
      currentNotificationId: null,
      currentNotificationTitle: "",
      currentNotificationParticipants: [],
      currentNotificationStartTime: null,
      currentNotificationEndTime: null,
      // 附件信息
      currentNotificationAttachments: null,
      participantsForm: {
        id: null,
        participants: []
      },
      // 当前参加人员列表
      currentParticipants: [],
      // 选中的用户
      selectedUsers: [],
      // 可用用户列表
      availableUsers: [],
      // 图片预览
      imagePreviewVisible: false,
      previewImageUrl: '',
      baseUrl: process.env.VUE_APP_BASE_API,
      // 批量考勤
      batchAttendanceVisible: false,
      batchAttendanceForm: {
        status: '0',
        signInStatus: '0',
        score: 0
      },
      // 当前编辑的学习活动ID
      currentLearningId: null,
      attendanceForm: {
        id: null,
        attendanceStatus: ''
      },
      uploadDialogVisible: false,
      fileList: [],
      // 上传配置
      upload: {
        url: process.env.VUE_APP_BASE_API + "/common/upload",
        headers: {
          Authorization: "Bearer " + this.$store.getters.token
        }
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: null,
        organizerId: null,
        branchId: null,
        startTime: null,
        endTime: null,
        location: null,
        form: null,
        duration: null,
        description: null,
        attachments: null,
        attendanceStatus: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        title: [
          { required: true, message: "半月学主题不能为空", trigger: "blur" }
        ],
        organizerId: [
          { required: true, message: "负责人不能为空", trigger: "blur" }
        ],
        branchId: [
          { required: true, message: "所属部门不能为空", trigger: "blur" }
        ],
        startTime: [
          { required: true, message: "开始时间不能为空", trigger: "blur" }
        ],
        endTime: [
          { required: true, message: "结束时间不能为空", trigger: "blur" }
        ],
      },
      // 树形结构默认属性
      defaultProps: {
        children: "children",
        label: "label"
      }
    };
  },
  watch: {
    // 根据名称筛选部门树
    deptName(val) {
      this.$refs.tree.filter(val);
    }
  },
  created() {
    // 先加载部门树和用户列表，再加载数据列表
    Promise.all([
      this.getDeptTree(),
      this.getUserList()
    ]).then(() => {
      this.getList();
    });
  },
  methods: {
    /** 查询learning列表 */
    // 修改：获取列表时同时获取参加人员详情
    async getList() {
      this.loading = true;
      try {
        const response = await listLearning(this.queryParams);
        let rows = response.rows;
        
        // 处理负责人信息
        rows.forEach(item => {
          const user = this.userList.find(u => u.userId === item.organizerId);
          item.organizerName = user ? user.userName : item.organizerId;
        });
        
        // 获取每个学习活动的参加人员详情
        for (const item of rows) {
          try {
            const participantResponse = await listParticipant({ learningId: item.id });
            item.participantDetails = participantResponse.rows.map(p => ({
              ...p,
              studentName: this.getUserName(p.studentId),
            }));
          } catch (error) {
            console.error(`获取学习活动${item.id}的参加人员失败:`, error);
            item.participantDetails = [];
          }
        }
        
        this.learningList = rows;
        this.total = response.total;
        this.fillDeptInfo(this.learningList);
      } catch (error) {
        console.error('获取列表失败:', error);
        this.$modal.msgError('获取列表失败');
      } finally {
        this.loading = false;
      }
    },   
    // fillDeptInfo方法
    fillDeptInfo(list) {
      function findDeptById(options, id) {
        if (!options || !Array.isArray(options)) {
          return null;
        }
        for (let item of options) {
          if (item.id === id) return item;
          if (item.children) {
            const found = findDeptById(item.children, id);
            if (found) return found;
          }
        }
        return null;
      }
      list.forEach(item => {
        const dept = findDeptById(this.deptOptions, item.branchId);
        item.dept = dept
          ? { deptImg: dept.deptImg || '', deptName: dept.label || dept.deptName || '' }
          : { deptImg: '', deptName: '' };
      });
    },
    getDeptTree() {
      return deptTreeSelect().then(response => {
        this.deptOptions = response.data;
      });
    },
    getUserList() {
      return listUser().then(response => {
        this.userList = response.rows;
      });
    },
    // 筛选节点
    filterNode(value, data) {
      if (!value) return true;
      return data.label.indexOf(value) !== -1;
    },
    // 节点单击事件
    handleNodeClick(data) {
      this.queryParams.branchId = data.id;
      this.handleQuery();
    },
    // 负责人变更
    handleLeaderChange(value) {
      const user = this.userList.find(u => u.userId === value);
      if (user) {
        this.form.organizerName = user.userName;
      }
    },
    // 获取出勤统计
    getAttendanceCount(participants, status) {
      if (!participants || !Array.isArray(participants)) return 0;
      return participants.filter(p => p.attendanceStatus === status).length;
    },
    // 编辑单个参加人员
    handleEditParticipant(row, participant) {
      // 设置当前编辑的参与者数据
      this.currentParticipant = { ...participant };
      this.currentLearningId = row.id;
      
      // 打开编辑对话框（需要添加相应的对话框组件）
      this.participantEditVisible = true;
    },

    // 保存单个参与者编辑
    async saveParticipantEdit() {
      try {
        const updateData = {
          id: this.currentParticipant.id,
          learningId: this.currentLearningId,
          studentId: this.currentParticipant.studentId,
          signInStatus: this.currentParticipant.signInStatus,
          attendanceStatus: this.currentParticipant.attendanceStatus,
          totalScore: this.currentParticipant.totalScore,
          performanceScore: this.currentParticipant.performanceScore,
          xuexiqiangguoPrev: this.currentParticipant.xuexiqiangguoPrev,
          xuexiqiangguoCurr: this.currentParticipant.xuexiqiangguoCurr,
          xuexiqiangguoDiff: this.currentParticipant.xuexiqiangguoDiff,
          xuexiqiangguoImage: this.currentParticipant.xuexiqiangguoImage,
          remark: this.currentParticipant.remark,
        };
        
        await updateParticipant(updateData);
        this.$modal.msgSuccess("参与者信息更新成功");
        this.participantEditVisible = false;
        this.getList();
      } catch (error) {
        console.error('更新参与者信息失败:', error);
        this.$modal.msgError("更新失败");
      }
    },
    // 移除单个参加人员
    handleRemoveParticipant(row, participant) {
      this.$modal.confirm('确认移除该参加人员？').then(async () => {
        try {
          // 调用删除API
          await delParticipant(participant.id);
          this.$modal.msgSuccess("移除成功");
          this.getList();
        } catch (error) {
          console.error('移除参加人员失败:', error);
          this.$modal.msgError("移除失败");
        }
      }).catch(() => {});
    },
    // 添加参加人员
    addParticipants() {
      if (this.selectedUsers.length === 0) {
        this.$modal.msgWarning('请选择要添加的人员');
        return;
      }
      
      this.selectedUsers.forEach(userId => {
        const user = this.userList.find(u => u.userId === userId);
        if (user) {
          this.currentParticipants.push({
            id: null, // 新增的记录
            studentId: userId,
            studentName: user.userName,
            signInStatus: '0',
            attendanceStatus: '0',
            totalScore: 0,
            performanceScore: 0,
            xuexiqiangguoPrev: 0,
            xuexiqiangguoCurr: 0,
            xuexiqiangguoDiff: 0,
            xuexiqiangguoImage: null
          });
        }
      });
      
      // 更新可选用户列表
      const participantIds = this.currentParticipants.map(p => p.studentId);
      this.availableUsers = this.userList.filter(user => !participantIds.includes(user.userId));
      this.selectedUsers = [];
    },
    // 移除参加人员
    removeParticipant(index) {
      this.currentParticipants.splice(index, 1);
      
      // 更新可选用户列表
      const participantIds = this.currentParticipants.map(p => p.studentId);
      this.availableUsers = this.userList.filter(user => !participantIds.includes(user.userId));
    },
    // 计算学习强国积分差值
    calculateDiff(participant) {
      participant.xuexiqiangguoDiff = (participant.xuexiqiangguoCurr || 0) - (participant.xuexiqiangguoPrev || 0);
    },
    // 保存参加人员
    async saveParticipants() {
      try {
        // 准备保存数据
        const saveData = this.currentParticipants.map(participant => ({
          id: participant.id,
          learningId: this.currentLearningId,
          studentId: participant.studentId,
          signInStatus: participant.signInStatus,
          attendanceStatus: participant.attendanceStatus,
          totalScore: participant.totalScore,
          performanceScore: participant.performanceScore,
          xuexiqiangguoPrev: participant.xuexiqiangguoPrev,
          xuexiqiangguoCurr: participant.xuexiqiangguoCurr,
          xuexiqiangguoDiff: participant.xuexiqiangguoDiff,
          xuexiqiangguoImage: participant.xuexiqiangguoImage,
          remark: participant.remark,
        }));
        
        // 调用API保存
        for (const data of saveData) {
          if (data.id) {
            await updateParticipant(data);
          } else {
            await addParticipant(data);
          }
        }
        
        this.participantsDialogVisible = false;
        this.$modal.msgSuccess('参加人员保存成功');
        this.getList(); // 刷新列表
      } catch (error) {
        console.error('保存参加人员失败:', error);
        this.$modal.msgError('保存参加人员失败');
      }
    },
    // 预览图片
    previewImage(imageUrl) {
      this.previewImageUrl = this.baseUrl + imageUrl;
      this.imagePreviewVisible = true;
    },
    handleImageUploadSuccess(response, participant) {
      if (response.code === 200) {
        // 移除baseUrl前缀，只保存相对路径
        let imageUrl = response.url;
        if (imageUrl && imageUrl.indexOf(this.baseUrl) === 0) {
          imageUrl = imageUrl.replace(this.baseUrl, "");
        }
        participant.xuexiqiangguoImage = imageUrl;
        this.$modal.msgSuccess('截图上传成功');
      } else {
        this.$modal.msgError('截图上传失败');
      }
    },
    // 添加获取完整图片URL的计算属性或方法
    getFullImageUrl(imagePath) {
      if (!imagePath) return '';
      if (imagePath.indexOf('http') === 0) {
        return imagePath; // 已经是完整URL
      }
      return this.baseUrl + imagePath; // 拼接baseUrl
    },
    // 图片上传前验证
    beforeImageUpload(file) {
      const isImage = file.type.startsWith('image/');
      const isLt2M = file.size / 1024 / 1024 < 2;
      
      if (!isImage) {
        this.$modal.msgError('只能上传图片文件!');
        return false;
      }
      if (!isLt2M) {
        this.$modal.msgError('图片大小不能超过 2MB!');
        return false;
      }
      return true;
    },
    // 修改：管理参加人员
    async handleEditParticipants(row) {
      this.currentLearningId = row.id;
      
      // 获取当前参加人员详情
      try {
        const response = await listParticipant({ learningId: row.id });
        this.currentParticipants = response.rows.map(item => ({
          id: item.id,
          studentId: item.studentId,
          studentName: this.getUserName(item.studentId),
          signInStatus: item.signInStatus || '0',
          attendanceStatus: item.attendanceStatus || '出勤',
          totalScore: item.totalScore || 0,
          performanceScore: item.performanceScore || 0,
          xuexiqiangguoPrev: item.xuexiqiangguoPrev || 0,
          xuexiqiangguoCurr: item.xuexiqiangguoCurr || 0,
          xuexiqiangguoDiff: item.xuexiqiangguoDiff || 0,
          xuexiqiangguoImage: item.xuexiqiangguoImage,
          remark: item.remark || ''
        }));
        
        // 获取可添加的用户列表（排除已参加的）
        const participantIds = this.currentParticipants.map(p => p.studentId);
        this.availableUsers = this.userList.filter(user => !participantIds.includes(user.userId));
        
        this.participantsDialogVisible = true;
      } catch (error) {
        console.error('获取参加人员失败:', error);
        this.$modal.msgError('获取参加人员失败');
      }
    },
    async submitBatchAttendance() {
      if (!this.currentParticipants || this.currentParticipants.length === 0) {
        this.$modal.msgError("没有参与人员数据");
        return;
      }
      try {
        this.$modal.loading("正在处理考勤数据...");
        
        // 只更新已存在的参与人员记录
        const updateData = this.currentParticipants
          .filter(participant => participant.id)
          .map(participant => ({
            id: participant.id,
            learningId: this.currentLearningId,
            userId: participant.userId,
            userName: participant.userName,
            deptName: participant.deptName,
            signInStatus: participant.signInStatus,
            attendanceStatus: participant.attendanceStatus,
            performanceScore: participant.performanceScore,
            totalScore: participant.totalScore,
            xuexiqiangguoPrev: participant.xuexiqiangguoPrev,
            xuexiqiangguoCurr: participant.xuexiqiangguoCurr,
            xuexiqiangguoDiff: participant.xuexiqiangguoDiff,
            xuexiqiangguoImage: participant.xuexiqiangguoImage,
            remark: participant.remark
          }));
        
        // 使用Promise.all并发请求，提高效率
        if (updateData.length > 0) {
          await Promise.all(updateData.map(item => updateParticipant(item)));
        }
        
        this.$modal.closeLoading();
        this.$modal.msgSuccess("批量考勤记录成功");
        this.batchAttendanceVisible = false;
        
        // 刷新列表
        this.getList();
      } catch (error) {
        this.$modal.closeLoading();
        this.$modal.msgError("批量考勤记录失败");
        console.error('批量考勤失败:', error);
      }
    },
    // 批量管理
    handleBatchAttendance(row) {
      this.currentLearningId = row.id;
      this.currentParticipants = row.participantDetails || [];
      this.batchAttendanceForm = {
        status: '0',
        signInStatus: '0',
        score: 0
      };
      this.batchAttendanceVisible = true;
    },
    // 应用批量出勤状态
    applyBatchAttendance() {
      if (!this.batchAttendanceForm.status) {
        this.$modal.msgWarning("请先选择出勤状态");
        return;
      }
      
      this.currentParticipants.forEach(participant => {
        participant.attendanceStatus = this.batchAttendanceForm.status;
      });
      
      this.$modal.msgSuccess("批量设置出勤状态成功，请点击保存按钮提交");
    },
    // 应用批量签到状态
    applyBatchSignIn() {
      if (!this.batchAttendanceForm.signInStatus) {
        this.$modal.msgWarning("请先选择签到状态");
        return;
      }
      this.currentParticipants.forEach(participant => {
        participant.signInStatus = this.batchAttendanceForm.signInStatus;
      });
      this.$modal.msgSuccess("一键签到成功，请点击保存按钮提交");
    },
    // 应用批量积分
    applyBatchScore() {
      this.currentParticipants.forEach(participant => {
        participant.performanceScore = this.batchAttendanceForm.score;
      });
      this.$modal.msgSuccess("批量设置积分成功，请点击保存按钮提交");
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        title: null,
        organizerId: null,
        organizerName: null,
        branchId: null,
        startTime: null,
        endTime: null,
        location: null,
        form: null,
        duration: null,
        description: null,
        attachments: null,
        signInStatus: null,
        attendanceStatus: null,
        createTime: null
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
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加半月学活动";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getLearning(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改半月学活动";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateLearning(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addLearning(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除半月学活动编号为"' + ids + '"的数据项？').then(function() {
        return delLearning(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('learning/learning/export', {
        ...this.queryParams
      }, `learning_${new Date().getTime()}.xlsx`)
    },
    // 获取附件列表
    getAttachmentList(attachments) {
      if (!attachments) return [];
      try {
        return JSON.parse(attachments);
      } catch (e) {
        return [];
      }
    },
    /** 获取单个学习活动的参与人员数据 */
    async getParticipantsForLearning(learningId) {
      try {
        const response = await listParticipant({ learningId: learningId });
        // 返回格式化的参与人员数据，包含用户名
        return (response.rows || []).map(participant => ({
          userId: participant.studentId,
          userName: this.getUserName(participant.studentId),
          ...participant
        }));
      } catch (error) {
        console.error(`获取学习活动${learningId}的参与人员失败:`, error);
        return [];
      }
    },
     // 处理发布通知
     handleSendNotification(row) {
      // 获取当前会议的参与人员
      this.getParticipantsForLearning(row.id).then(participants => {
        this.currentNotificationId = row.id;
        this.currentNotificationTitle = row.title;
        this.currentNotificationParticipants = participants;
        this.currentNotificationStartTime = this.parseTime(row.startTime, '{y}-{m}-{d} {h}:{i}:{s}');
        this.currentNotificationEndTime = this.parseTime(row.endTime, '{y}-{m}-{d} {h}:{i}:{s}');
        this.currentNotificationAttachments = row.attachments; // 传递附件信息
        
        // 使用$nextTick确保props更新后再打开对话框
        this.$nextTick(() => {
          this.$refs.activityNotification.openNotificationDialog();
        });
      }).catch(error => {
        this.$modal.msgError("获取参与人员失败");
        console.error("获取参与人员失败:", error);
      });
    },

    // 处理发布签到
    handleSignIn(row) {
      // 获取当前会议的参与人员
      this.getParticipantsForLearning(row.id).then(participants => {
        this.currentNotificationId = row.id;
        this.currentNotificationTitle = row.title;
        this.currentNotificationParticipants = participants;
        this.currentNotificationStartTime = this.parseTime(row.startTime, '{y}-{m}-{d} {h}:{i}:{s}');
        this.currentNotificationEndTime = this.parseTime(row.endTime, '{y}-{m}-{d} {h}:{i}:{s}');
        this.currentNotificationAttachments = row.attachments; // 传递附件信息
        
        // 使用$nextTick确保props更新后再打开对话框
        this.$nextTick(() => {
          this.$refs.activityNotification.openSignInDialog();
        });
      }).catch(error => {
        this.$modal.msgError("获取参与人员失败");
        console.error("获取参与人员失败:", error);
      });
    },
    // 更新行附件数据
    updateRowAttachments(id, attachments) {
      const updateData = {
        id: id,
        attachments: attachments
      };
      
      updateLearning(updateData).then(response => {
        this.$modal.msgSuccess("附件更新成功");
        // 刷新列表数据
        this.getList();
      }).catch(error => {
        this.$modal.msgError("附件更新失败");
        console.error('更新附件失败:', error);
      });
    },
    // 获取用户名称
    getUserName(userId) {
      const user = this.userList.find(u => u.userId === userId);
      return user ? user.userName : `用户${userId}`;
    }
  }
};
</script>
