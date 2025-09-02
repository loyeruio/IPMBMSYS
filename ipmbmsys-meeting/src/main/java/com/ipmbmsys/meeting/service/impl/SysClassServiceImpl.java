package com.ipmbmsys.meeting.service.impl;

import java.util.List;
import com.ipmbmsys.common.utils.DateUtils;
import com.ipmbmsys.meeting.domain.SysClass;
import com.ipmbmsys.meeting.mapper.SysClassMapper;
import com.ipmbmsys.meeting.service.ISysClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * classService业务层处理
 *
 * @author ruoyi
 * @date 2025-06-03
 */
@Service
public class SysClassServiceImpl implements ISysClassService
{
    @Autowired
    private SysClassMapper sysClassMapper;

    /**
     * 查询class
     *
     * @param id class主键
     * @return class
     */
    @Override
    public SysClass selectSysClassById(Long id)
    {
        return sysClassMapper.selectSysClassById(id);
    }

    /**
     * 查询class列表
     *
     * @param sysClass class
     * @return class
     */
    @Override
    public List<SysClass> selectSysClassList(SysClass sysClass)
    {
        return sysClassMapper.selectSysClassList(sysClass);
    }

    /**
     * 新增class
     *
     * @param sysClass class
     * @return 结果
     */
    @Override
    public int insertSysClass(SysClass sysClass)
    {
        sysClass.setCreateTime(DateUtils.getNowDate());
        return sysClassMapper.insertSysClass(sysClass);
    }

    /**
     * 修改class
     *
     * @param sysClass class
     * @return 结果
     */
    @Override
    public int updateSysClass(SysClass sysClass)
    {
        return sysClassMapper.updateSysClass(sysClass);
    }

    /**
     * 批量删除class
     *
     * @param ids 需要删除的class主键
     * @return 结果
     */
    @Override
    public int deleteSysClassByIds(Long[] ids)
    {
        return sysClassMapper.deleteSysClassByIds(ids);
    }

    /**
     * 删除class信息
     *
     * @param id class主键
     * @return 结果
     */
    @Override
    public int deleteSysClassById(Long id)
    {
        return sysClassMapper.deleteSysClassById(id);
    }
}
