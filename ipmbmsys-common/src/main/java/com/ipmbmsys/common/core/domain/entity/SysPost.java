package com.ipmbmsys.common.core.domain.entity;

import com.ipmbmsys.common.annotation.Excel;
import com.ipmbmsys.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 职务表 sys_post
 *
 * @author 贺祎阳
 */
public class SysPost extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 职务序号 */
    @Excel(name = "职务序号", cellType = Excel.ColumnType.NUMERIC)
    private Long postId;

    /** 职务描述 */
    @Excel(name = "职务描述")
    private String postDescribes;

    /** 职务名称 */
    @Excel(name = "职务名称")
    private String postName;

    /** 职务排序 */
    @Excel(name = "职务排序")
    private Integer postSort;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 党组织组 */
    private Long[] deptIds;

    /** 用户是否存在此职务标识 默认不存在 */
    private boolean flag = false;

    public Long getPostId()
    {
        return postId;
    }

    public void setPostId(Long postId)
    {
        this.postId = postId;
    }

    @NotBlank(message = "职务描述不能为空")
    @Size(min = 0, max = 500, message = "职务描述长度不能超过500个字符")
    public String getPostDescribes()
    {
        return postDescribes;
    }

    public void setPostDescribes(String postDescribes)
    {
        this.postDescribes = postDescribes;
    }

    @NotBlank(message = "职务名称不能为空")
    @Size(min = 0, max = 50, message = "职务名称长度不能超过50个字符")
    public String getPostName()
    {
        return postName;
    }

    public void setPostName(String postName)
    {
        this.postName = postName;
    }

    @NotNull(message = "显示顺序不能为空")
    public Integer getPostSort()
    {
        return postSort;
    }

    public void setPostSort(Integer postSort)
    {
        this.postSort = postSort;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public boolean isFlag()
    {
        return flag;
    }

    public void setFlag(boolean flag)
    {
        this.flag = flag;
    }

    public Long[] getDeptIds() {
        return deptIds;
    }

    public void setDeptIds(Long[] deptIds) {
        this.deptIds = deptIds;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("postId", getPostId())
                .append("postDescribes", getPostDescribes())
                .append("postName", getPostName())
                .append("postSort", getPostSort())
                .append("status", getStatus())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
