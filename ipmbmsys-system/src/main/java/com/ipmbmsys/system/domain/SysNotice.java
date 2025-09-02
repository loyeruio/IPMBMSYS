package com.ipmbmsys.system.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ipmbmsys.common.core.domain.BaseEntity;
import com.ipmbmsys.common.xss.Xss;

import java.util.Date;

/**
 * 通知公告表 sys_notice
 *
 * @author 贺祎阳
 */
public class SysNotice extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 公告ID */
    private Long noticeId;

    /** 公告标题 */
    private String noticeTitle;

    /** 公告类型（1通知 2公告） */
    private String noticeType;

    /** 公告内容 */
    private String noticeContent;

    /** 公告状态（0正常 1关闭） */
    private String status;

    /** 通知人员列表 */
    private String noticeUserIds;

    /** 签到时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date recordTime;

    /** 附件信息 */
    private String attachMent;

    /** 活动类别 */
    private String activityType;

    /** 活动 */
    private String activityId;

    public Long getNoticeId()
    {
        return noticeId;
    }

    public void setNoticeId(Long noticeId)
    {
        this.noticeId = noticeId;
    }

    public void setNoticeTitle(String noticeTitle)
    {
        this.noticeTitle = noticeTitle;
    }

    @Xss(message = "公告标题不能包含脚本字符")
    @NotBlank(message = "公告标题不能为空")
    @Size(min = 0, max = 50, message = "公告标题不能超过50个字符")
    public String getNoticeTitle()
    {
        return noticeTitle;
    }

    public void setNoticeType(String noticeType)
    {
        this.noticeType = noticeType;
    }

    public String getNoticeType()
    {
        return noticeType;
    }

    public void setNoticeContent(String noticeContent)
    {
        this.noticeContent = noticeContent;
    }

    public String getNoticeContent()
    {
        return noticeContent;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    public String getNoticeUserIds() {
        return noticeUserIds;
    }

    public void setNoticeUserIds(String noticeUserIds) {
        this.noticeUserIds = noticeUserIds;
    }

    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }

    public String getAttachMent() {
        return attachMent;
    }

    public void setAttachMent(String attachMent) {
        this.attachMent = attachMent;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("noticeId", getNoticeId())
                .append("noticeTitle", getNoticeTitle())
                .append("noticeType", getNoticeType())
                .append("noticeContent", getNoticeContent())
                .append("status", getStatus())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .append("noticeUserIds", getNoticeUserIds())
                .append("recordTime", getRecordTime())
                .append("attachMent", getAttachMent())
                .append("activityType", getActivityType())
                .append("activityId", getActivityId())
                .toString();
    }
}
