package com.ipmbmsys.meeting.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ipmbmsys.common.annotation.Excel;
import com.ipmbmsys.common.core.domain.BaseEntity;

/**
 * class对象 sys_class
 *
 * @author ruoyi
 * @date 2025-06-03
 */
public class SysClass extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 党课主题 */
    @Excel(name = "党课主题")
    private String title;

    /** 上课地点 */
    @Excel(name = "上课地点")
    private String location;

    /** 授课讲师 */
    @Excel(name = "授课讲师")
    private String lecturer;

    /** 组织负责人 */
    @Excel(name = "组织负责人")
    private Long organizerId;

    /** 所属部门 */
    @Excel(name = "所属部门")
    private Long branchId;

    /** 课程简介 */
    @Excel(name = "课程简介")
    private String description;

    /** 课程状态 */
    @Excel(name = "课程状态")
    private String status;

    /** 开课时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "开课时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;


    /** 课时（分钟） */
    @Excel(name = "课时", readConverterExp = "分=钟")
    private Long duration;

    /** 参加人员 */
    @Excel(name = "参加人员")
    private String participants;

    /** 附件资料 */
    @Excel(name = "附件资料")
    private String attachments;

    /** 出勤情况 */
    @Excel(name = "出勤情况")
    private String attendanceStatus;

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
    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getLocation()
    {
        return location;
    }
    public void setLecturer(String lecturer)
    {
        this.lecturer = lecturer;
    }

    public String getLecturer()
    {
        return lecturer;
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
    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
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
    public void setDuration(Long duration)
    {
        this.duration = duration;
    }

    public Long getDuration()
    {
        return duration;
    }
    public void setParticipants(String participants)
    {
        this.participants = participants;
    }

    public String getParticipants()
    {
        return participants;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("title", getTitle())
                .append("location", getLocation())
                .append("lecturer", getLecturer())
                .append("organizerId", getOrganizerId())
                .append("branchId", getBranchId())
                .append("description", getDescription())
                .append("status", getStatus())
                .append("startTime", getStartTime())
                .append("endTime", getEndTime())
                .append("duration", getDuration())
                .append("createTime", getCreateTime())
                .append("participants", getParticipants())
                .append("attachments", getAttachments())
                .append("attendanceStatus", getAttendanceStatus())
                .toString();
    }
}
