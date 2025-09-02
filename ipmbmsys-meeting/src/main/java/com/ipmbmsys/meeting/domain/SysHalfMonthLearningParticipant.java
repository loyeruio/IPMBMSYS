package com.ipmbmsys.meeting.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ipmbmsys.common.annotation.Excel;
import com.ipmbmsys.common.core.domain.BaseEntity;

/**
 * participant对象 sys_half_month_learning_participant
 *
 * @author ruoyi
 * @date 2025-06-07
 */
public class SysHalfMonthLearningParticipant extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 半月学 */
    @Excel(name = "半月学")
    private Long learningId;

    /** 学生名称 */
    @Excel(name = "学生名称")
    private Long studentId;

    /** 出勤状态 */
    @Excel(name = "签到状态")
    private String signInStatus;

    /** 出勤状态 */
    @Excel(name = "出勤状态")
    private String attendanceStatus;

    /** 半月学总积分 */
    @Excel(name = "总积分")
    private Long totalScore;

    /** 表现积分 */
    @Excel(name = "表现积分")
    private Long performanceScore;

    /** 上次强国积分 */
    @Excel(name = "上次强国积分")
    private Long xuexiqiangguoPrev;

    /** 当前强国积分 */
    @Excel(name = "当前强国积分")
    private Long xuexiqiangguoCurr;

    /** 积分差值 */
    @Excel(name = "积分差值")
    private Long xuexiqiangguoDiff;

    /** 本次强国截图 */
    @Excel(name = "本次强国截图")
    private String xuexiqiangguoImage;

    @Excel(name = "备注")
    private String remark;
    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setLearningId(Long learningId)
    {
        this.learningId = learningId;
    }

    public Long getLearningId()
    {
        return learningId;
    }
    public void setStudentId(Long studentId)
    {
        this.studentId = studentId;
    }

    public Long getStudentId()
    {
        return studentId;
    }
    public void setAttendanceStatus(String attendanceStatus)
    {
        this.attendanceStatus = attendanceStatus;
    }

    public String getAttendanceStatus()
    {
        return attendanceStatus;
    }
    public void setPerformanceScore(Long performanceScore)
    {
        this.performanceScore = performanceScore;
    }

    public Long getPerformanceScore()
    {
        return performanceScore;
    }
    public void setXuexiqiangguoPrev(Long xuexiqiangguoPrev)
    {
        this.xuexiqiangguoPrev = xuexiqiangguoPrev;
    }

    public Long getXuexiqiangguoPrev()
    {
        return xuexiqiangguoPrev;
    }
    public void setXuexiqiangguoCurr(Long xuexiqiangguoCurr)
    {
        this.xuexiqiangguoCurr = xuexiqiangguoCurr;
    }

    public Long getXuexiqiangguoCurr()
    {
        return xuexiqiangguoCurr;
    }
    public void setXuexiqiangguoDiff(Long xuexiqiangguoDiff)
    {
        this.xuexiqiangguoDiff = xuexiqiangguoDiff;
    }

    public Long getXuexiqiangguoDiff()
    {
        return xuexiqiangguoDiff;
    }
    public void setXuexiqiangguoImage(String xuexiqiangguoImage)
    {
        this.xuexiqiangguoImage = xuexiqiangguoImage;
    }

    public String getXuexiqiangguoImage()
    {
        return xuexiqiangguoImage;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Long totalScore) {
        this.totalScore = totalScore;
    }

    public String getSignInStatus() {
        return signInStatus;
    }

    public void setSignInStatus(String signInStatus) {
        this.signInStatus = signInStatus;
    }
    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("learningId", getLearningId())
            .append("studentId", getStudentId())
            .append("attendanceStatus", getAttendanceStatus())
            .append("performanceScore", getPerformanceScore())
            .append("xuexiqiangguoPrev", getXuexiqiangguoPrev())
            .append("xuexiqiangguoCurr", getXuexiqiangguoCurr())
            .append("xuexiqiangguoDiff", getXuexiqiangguoDiff())
            .append("xuexiqiangguoImage", getXuexiqiangguoImage())
            .append("createTime", getCreateTime())
            .append("remark", getRemark())
            .append("totalScore", getTotalScore())
            .append("signInStatus", getSignInStatus())
            .toString();
    }


}
