package com.ipmbmsys.meeting.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ipmbmsys.meeting.mapper.SysClassParticipantMapper;
import com.ipmbmsys.meeting.domain.SysClassParticipant;
import com.ipmbmsys.meeting.service.ISysClassParticipantService;

/**
 * 党课参与人员Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-06-12
 */
@Service
public class SysClassParticipantServiceImpl implements ISysClassParticipantService 
{
    @Autowired
    private SysClassParticipantMapper sysClassParticipantMapper;

    /**
     * 查询党课参与人员
     * 
     * @param id 党课参与人员主键
     * @return 党课参与人员
     */
    @Override
    public SysClassParticipant selectSysClassParticipantById(Long id)
    {
        return sysClassParticipantMapper.selectSysClassParticipantById(id);
    }

    /**
     * 查询党课参与人员列表
     * 
     * @param sysClassParticipant 党课参与人员
     * @return 党课参与人员
     */
    @Override
    public List<SysClassParticipant> selectSysClassParticipantList(SysClassParticipant sysClassParticipant)
    {
        return sysClassParticipantMapper.selectSysClassParticipantList(sysClassParticipant);
    }

    /**
     * 新增党课参与人员
     * 
     * @param sysClassParticipant 党课参与人员
     * @return 结果
     */
    @Override
    public int insertSysClassParticipant(SysClassParticipant sysClassParticipant)
    {
        return sysClassParticipantMapper.insertSysClassParticipant(sysClassParticipant);
    }

    /**
     * 修改党课参与人员
     * 
     * @param sysClassParticipant 党课参与人员
     * @return 结果
     */
    @Override
    public int updateSysClassParticipant(SysClassParticipant sysClassParticipant)
    {
        return sysClassParticipantMapper.updateSysClassParticipant(sysClassParticipant);
    }

    /**
     * 批量删除党课参与人员
     * 
     * @param ids 需要删除的党课参与人员主键
     * @return 结果
     */
    @Override
    public int deleteSysClassParticipantByIds(Long[] ids)
    {
        return sysClassParticipantMapper.deleteSysClassParticipantByIds(ids);
    }

    /**
     * 删除党课参与人员信息
     * 
     * @param id 党课参与人员主键
     * @return 结果
     */
    @Override
    public int deleteSysClassParticipantById(Long id)
    {
        return sysClassParticipantMapper.deleteSysClassParticipantById(id);
    }
}
