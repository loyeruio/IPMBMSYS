package com.ipmbmsys.meeting.service;

import com.ipmbmsys.meeting.domain.SysClass;
import java.util.List;


/**
 * classService接口
 *
 * @author ruoyi
 * @date 2025-06-03
 */
public interface ISysClassService
{
    /**
     * 查询class
     *
     * @param id class主键
     * @return class
     */
    public SysClass selectSysClassById(Long id);

    /**
     * 查询class列表
     *
     * @param sysClass class
     * @return class集合
     */
    public List<SysClass> selectSysClassList(SysClass sysClass);

    /**
     * 新增class
     *
     * @param sysClass class
     * @return 结果
     */
    public int insertSysClass(SysClass sysClass);

    /**
     * 修改class
     *
     * @param sysClass class
     * @return 结果
     */
    public int updateSysClass(SysClass sysClass);

    /**
     * 批量删除class
     *
     * @param ids 需要删除的class主键集合
     * @return 结果
     */
    public int deleteSysClassByIds(Long[] ids);

    /**
     * 删除class信息
     *
     * @param id class主键
     * @return 结果
     */
    public int deleteSysClassById(Long id);
}
