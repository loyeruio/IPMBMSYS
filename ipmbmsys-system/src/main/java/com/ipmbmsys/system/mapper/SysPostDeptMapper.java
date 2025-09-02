package com.ipmbmsys.system.mapper;

import java.util.List;
import com.ipmbmsys.system.domain.SysPostDept;
import org.apache.ibatis.annotations.Param;

/**
 * 用户与职务关联表 数据层
 *
 * @author 贺祎阳
 */
public interface SysPostDeptMapper
{
    /**
     * 通过职务ID删除职务和党组织关联
     *
     * @param postId 职务ID
     * @return 结果
     */
    public int deletePostDeptByPostId(Long postId);

    /**
     * 批量删除用户和职务关联
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deletePostDept(Long[] ids);

    /**
     * 批量新增用户职务信息
     *
     * @param postDeptList 用户职务列表
     * @return 结果
     */
    public int batchPostDept(List<SysPostDept> postDeptList);

    // SysPostDeptMapper.java 新增方法
    SysPostDept checkExist(@Param("postId") Long postId, @Param("deptId") Long deptId);
}
