package com.ipmbmsys.system.service;

import java.util.List;
import com.ipmbmsys.common.core.domain.TreeSelect;
import com.ipmbmsys.common.core.domain.entity.SysDept;

/**
 * 党组织管理 服务层
 * 
 * @author 贺祎阳
 */
public interface ISysDeptService
{
    /**
     * 查询党组织管理数据
     * 
     * @param dept 党组织信息
     * @return 党组织信息集合
     */
    public List<SysDept> selectDeptList(SysDept dept);

    /**
     * 查询党组织树结构信息
     * 
     * @param dept 党组织信息
     * @return 党组织树信息集合
     */
    public List<TreeSelect> selectDeptTreeList(SysDept dept);

    /**
     * 构建前端所需要树结构
     * 
     * @param depts 党组织列表
     * @return 树结构列表
     */
    public List<SysDept> buildDeptTree(List<SysDept> depts);

    /**
     * 构建前端所需要下拉树结构
     * 
     * @param depts 党组织列表
     * @return 下拉树结构列表
     */
    public List<TreeSelect> buildDeptTreeSelect(List<SysDept> depts);

    /**
     * 根据角色ID查询党组织树信息
     * 
     * @param roleId 角色ID
     * @return 选中党组织列表
     */
    public List<Long> selectDeptListByRoleId(Long roleId);

    /**
     * 根据党组织ID查询信息
     * 
     * @param deptId 党组织ID
     * @return 党组织信息
     */
    public SysDept selectDeptById(Long deptId);

    /**
     * 根据ID查询所有子党组织（正常状态）
     * 
     * @param deptId 党组织ID
     * @return 子党组织数
     */
    public int selectNormalChildrenDeptById(Long deptId);

    /**
     * 是否存在党组织子节点
     * 
     * @param deptId 党组织ID
     * @return 结果
     */
    public boolean hasChildByDeptId(Long deptId);

    /**
     * 查询党组织是否存在用户
     * 
     * @param deptId 党组织ID
     * @return 结果 true 存在 false 不存在
     */
    public boolean checkDeptExistUser(Long deptId);

    /**
     * 校验党组织名称是否唯一
     * 
     * @param dept 党组织信息
     * @return 结果
     */
    public boolean checkDeptNameUnique(SysDept dept);

    /**
     * 校验党组织是否有数据权限
     * 
     * @param deptId 党组织id
     */
    public void checkDeptDataScope(Long deptId);

    /**
     * 新增保存党组织信息
     * 
     * @param dept 党组织信息
     * @return 结果
     */
    public int insertDept(SysDept dept);

    /**
     * 修改保存党组织信息
     * 
     * @param dept 党组织信息
     * @return 结果
     */
    public int updateDept(SysDept dept);

    /**
     * 删除党组织管理信息
     * 
     * @param deptId 党组织ID
     * @return 结果
     */
    public int deleteDeptById(Long deptId);
}
