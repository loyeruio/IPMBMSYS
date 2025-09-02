package com.ipmbmsys.meeting.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ipmbmsys.common.annotation.Excel;
import com.ipmbmsys.common.core.domain.BaseEntity;

/**
 * 会议参与人员对象 sys_meetings_participant
 *
 * @author ruoyi
 * @date 2025-06-12
 */
public class SysMeetingsParticipant extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 会议 */
    @Excel(name = "会议")
    private Long meetingId;

    /** 与会人员 */
    @Excel(name = "与会人员")
    private Long userId;

    /** 姓名 */
    @Excel(name = "姓名")
    private String userName;

    /** 所属部门 */
    @Excel(name = "所属部门")
    private String deptName;

    /** 签到状态 */
    @Excel(name = "签到状态")
    private String signInStatus;

    /** 出勤状态 */
    @Excel(name = "出勤状态")
    private String attendanceStatus;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setMeetingId(Long meetingId)
    {
        this.meetingId = meetingId;
    }

    public Long getMeetingId()
    {
        return meetingId;
    }
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }
    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getUserName()
    {
        return userName;
    }
    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    public String getDeptName()
    {
        return deptName;
    }
    public void setSignInStatus(String signInStatus)
    {
        this.signInStatus = signInStatus;
    }

    public String getSignInStatus()
    {
        return signInStatus;
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
                .append("meetingId", getMeetingId())
                .append("userId", getUserId())
                .append("userName", getUserName())
                .append("deptName", getDeptName())
                .append("signInStatus", getSignInStatus())
                .append("attendanceStatus", getAttendanceStatus())
                .append("remark", getRemark())
                .toString();
    }
}
