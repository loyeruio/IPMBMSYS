package com.ipmbmsys.meeting.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ipmbmsys.common.annotation.Excel;
import com.ipmbmsys.common.core.domain.BaseEntity;

/**
 * 党课参与人员对象 sys_class_participant
 *
 * @author ruoyi
 * @date 2025-06-12
 */
public class SysClassParticipant extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 党课 */
    @Excel(name = "党课")
    private Long classId;

    /** 参与人员 */
    @Excel(name = "参与人员")
    private Long userId;

    /** 姓名 */
    @Excel(name = "姓名")
    private String userName;

    /** 所属部门名称 */
    @Excel(name = "所属部门名称")
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
    public void setClassId(Long classId)
    {
        this.classId = classId;
    }

    public Long getClassId()
    {
        return classId;
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
                .append("classId", getClassId())
                .append("userId", getUserId())
                .append("userName", getUserName())
                .append("deptName", getDeptName())
                .append("signInStatus", getSignInStatus())
                .append("attendanceStatus", getAttendanceStatus())
                .append("remark", getRemark())
                .toString();
    }
}
