package com.ipmbmsys.system.mapper;

import java.util.List;
import com.ipmbmsys.system.domain.SysRoleDept;

/**
 * 角色与党组织关联表 数据层
 * 
 * @author 贺祎阳
 */
public interface SysRoleDeptMapper
{
    /**
     * 通过角色ID删除角色和党组织关联
     * 
     * @param roleId 角色ID
     * @return 结果
     */
    public int deleteRoleDeptByRoleId(Long roleId);

    /**
     * 批量删除角色党组织关联信息
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteRoleDept(Long[] ids);

    /**
     * 查询党组织使用数量
     * 
     * @param deptId 党组织ID
     * @return 结果
     */
    public int selectCountRoleDeptByDeptId(Long deptId);

    /**
     * 批量新增角色党组织信息
     * 
     * @param roleDeptList 角色党组织列表
     * @return 结果
     */
    public int batchRoleDept(List<SysRoleDept> roleDeptList);
}
