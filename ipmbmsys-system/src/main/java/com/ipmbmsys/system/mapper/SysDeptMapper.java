package com.ipmbmsys.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ipmbmsys.common.core.domain.entity.SysDept;

/**
 * 党组织管理 数据层
 * 
 * @author 贺祎阳
 */
public interface SysDeptMapper
{
    /**
     * 查询党组织管理数据
     * 
     * @param dept 党组织信息
     * @return 党组织信息集合
     */
    public List<SysDept> selectDeptList(SysDept dept);

    /**
     * 根据角色ID查询党组织树信息
     * 
     * @param roleId 角色ID
     * @param deptCheckStrictly 党组织树选择项是否关联显示
     * @return 选中党组织列表
     */
    public List<Long> selectDeptListByRoleId(@Param("roleId") Long roleId, @Param("deptCheckStrictly") boolean deptCheckStrictly);

    /**
     * 根据党组织ID查询信息
     * 
     * @param deptId 党组织ID
     * @return 党组织信息
     */
    public SysDept selectDeptById(Long deptId);

    /**
     * 根据ID查询所有子党组织
     * 
     * @param deptId 党组织ID
     * @return 党组织列表
     */
    public List<SysDept> selectChildrenDeptById(Long deptId);

    /**
     * 根据ID查询所有子党组织（正常状态）
     * 
     * @param deptId 党组织ID
     * @return 子党组织数
     */
    public int selectNormalChildrenDeptById(Long deptId);

    /**
     * 是否存在子节点
     * 
     * @param deptId 党组织ID
     * @return 结果
     */
    public int hasChildByDeptId(Long deptId);

    /**
     * 查询党组织是否存在用户
     * 
     * @param deptId 党组织ID
     * @return 结果
     */
    public int checkDeptExistUser(Long deptId);

    /**
     * 校验党组织名称是否唯一
     * 
     * @param deptName 党组织名称
     * @param parentId 父党组织ID
     * @return 结果
     */
    public SysDept checkDeptNameUnique(@Param("deptName") String deptName, @Param("parentId") Long parentId);

    /**
     * 新增党组织信息
     * 
     * @param dept 党组织信息
     * @return 结果
     */
    public int insertDept(SysDept dept);

    /**
     * 修改党组织信息
     * 
     * @param dept 党组织信息
     * @return 结果
     */
    public int updateDept(SysDept dept);

    /**
     * 修改所在党组织正常状态
     * 
     * @param deptIds 党组织ID组
     */
    public void updateDeptStatusNormal(Long[] deptIds);

    /**
     * 修改子元素关系
     * 
     * @param depts 子元素
     * @return 结果
     */
    public int updateDeptChildren(@Param("depts") List<SysDept> depts);

    /**
     * 删除党组织管理信息
     * 
     * @param deptId 党组织ID
     * @return 结果
     */
    public int deleteDeptById(Long deptId);
}
