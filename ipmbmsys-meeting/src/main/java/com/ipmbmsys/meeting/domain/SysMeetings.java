package com.ipmbmsys.meeting.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ipmbmsys.common.annotation.Excel;
import com.ipmbmsys.common.core.domain.BaseEntity;

/**
 * 会议管理对象 sys_meetings
 *
 * @author ruoyi
 * @date 2025-03-06
 */
public class SysMeetings extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 会议主题 */
    @Excel(name = "会议主题")
    private String title;

    /** 会议类型 */
    @Excel(name = "会议类型")
    private String meetingType;

    /** 会议开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "会议开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /** 会议结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "会议结束时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /** 会议地点 */
    @Excel(name = "会议地点")
    private String location;

    /** 组织者（党员ID） */
    @Excel(name = "负责人", readConverterExp = "党员ID")
    private Long organizerId;

    /** 支部ID */
    @Excel(name = "支部ID")
    private Long branchId;

    /** 会议描述 */
    @Excel(name = "会议内容")
    private String description;

    /** 会议状态 */
    @Excel(name = "会议状态")
    private String status;

    /** 与会人员（党员ID，逗号分隔） */
    @Excel(name = "与会人员")
    private String participants;

    /** 附件信息（文件路径，逗号分隔） */
    @Excel(name = "附件信息")
    private String attachments;

    /** 参会情况（记录应到人数、实到人数、未到人员情况等） */
    @Excel(name = "参会情况")
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
    public void setMeetingType(String meetingType)
    {
        this.meetingType = meetingType;
    }

    public String getMeetingType()
    {
        return meetingType;
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
            .append("meetingType", getMeetingType())
            .append("startTime", getStartTime())
            .append("endTime", getEndTime())
            .append("location", getLocation())
            .append("organizerId", getOrganizerId())
            .append("branchId", getBranchId())
            .append("description", getDescription())
            .append("status", getStatus())
            .append("participants", getParticipants())
            .append("attachments", getAttachments())
            .append("attendanceStatus", getAttendanceStatus())
            .append("createTime", getCreateTime())
            .toString();
    }
}
