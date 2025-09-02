package com.ipmbmsys.meeting.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ipmbmsys.common.annotation.Excel;
import com.ipmbmsys.common.core.domain.BaseEntity;

/**
 * learning对象 sys_half_month_learning
 *
 * @author ruoyi
 * @date 2025-06-06
 */
public class SysHalfMonthLearning extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 半月学主题 */
    @Excel(name = "半月学主题")
    private String title;

    /** 负责人 */
    @Excel(name = "负责人")
    private Long organizerId;

    /** 所属部门 */
    @Excel(name = "所属部门")
    private Long branchId;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /** 半月学地点 */
    @Excel(name = "半月学地点")
    private String location;

    /** 活动形式 */
    @Excel(name = "活动形式")
    private String form;

    /** 学习时长 */
    @Excel(name = "学习时长")
    private Long duration;

    /** 活动内容介绍 */
    @Excel(name = "活动内容介绍")
    private String description;

    /** 相关附件 */
    @Excel(name = "相关附件")
    private String attachments;

    /** 出勤情况记录 */
    @Excel(name = "出勤情况记录")
    private String attendanceStatus;

    /** 课程状态 */
    @Excel(name = "课程状态")
    private String status;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getTitle()
    {
        return title;
    }
    public void setOrganizerId(Long organizerId)
    {
        this.organizerId = organizerId;
    }

    public Long getOrganizerId()
    {
        return organizerId;
    }
    public void setBranchId(Long branchId)
    {
        this.branchId = branchId;
    }

    public Long getBranchId()
    {
        return branchId;
    }
    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getStartTime()
    {
        return startTime;
    }
    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }
    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getLocation()
    {
        return location;
    }
    public void setForm(String form)
    {
        this.form = form;
    }

    public String getForm()
    {
        return form;
    }
    public void setDuration(Long duration)
    {
        this.duration = duration;
    }

    public Long getDuration()
    {
        return duration;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }
    public void setAttachments(String attachments)
    {
        this.attachments = attachments;
    }

    public String getAttachments()
    {
        return attachments;
    }
    public void setAttendanceStatus(String attendanceStatus)
    {
        this.attendanceStatus = attendanceStatus;
    }

    public String getAttendanceStatus()
    {
        return attendanceStatus;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("title", getTitle())
                .append("organizerId", getOrganizerId())
                .append("branchId", getBranchId())
                .append("startTime", getStartTime())
                .append("endTime", getEndTime())
                .append("location", getLocation())
                .append("form", getForm())
                .append("duration", getDuration())
                .append("description", getDescription())
                .append("attachments", getAttachments())
                .append("attendanceStatus", getAttendanceStatus())
                .append("createTime", getCreateTime())
                .append("status", getStatus())
                .toString();
    }
}
