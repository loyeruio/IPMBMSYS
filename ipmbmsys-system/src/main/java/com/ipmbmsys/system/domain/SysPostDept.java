package com.ipmbmsys.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 用户和职务关联 sys_post_dept
 *
 * @author 贺祎阳
 */
public class SysPostDept
{
    /** 职务ID */
    private Long postId;

    /** 党组织ID */
    private Long deptId;

    public Long getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Long userId)
    {
        this.deptId = userId;
    }

    public Long getPostId()
    {
        return postId;
    }

    public void setPostId(Long postId)
    {
        this.postId = postId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("postId", getPostId())
                .append("deptId", getDeptId())
                .toString();
    }
}
