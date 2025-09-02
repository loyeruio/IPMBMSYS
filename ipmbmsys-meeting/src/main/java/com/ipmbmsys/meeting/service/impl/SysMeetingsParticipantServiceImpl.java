package com.ipmbmsys.meeting.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ipmbmsys.meeting.mapper.SysMeetingsParticipantMapper;
import com.ipmbmsys.meeting.domain.SysMeetingsParticipant;
import com.ipmbmsys.meeting.service.ISysMeetingsParticipantService;

/**
 * 会议参与人员Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-06-12
 */
@Service
public class SysMeetingsParticipantServiceImpl implements ISysMeetingsParticipantService 
{
    @Autowired
    private SysMeetingsParticipantMapper sysMeetingsParticipantMapper;

    /**
     * 查询会议参与人员
     * 
     * @param id 会议参与人员主键
     * @return 会议参与人员
     */
    @Override
    public SysMeetingsParticipant selectSysMeetingsParticipantById(Long id)
    {
        return sysMeetingsParticipantMapper.selectSysMeetingsParticipantById(id);
    }

    /**
     * 查询会议参与人员列表
     * 
     * @param sysMeetingsParticipant 会议参与人员
     * @return 会议参与人员
     */
    @Override
    public List<SysMeetingsParticipant> selectSysMeetingsParticipantList(SysMeetingsParticipant sysMeetingsParticipant)
    {
        return sysMeetingsParticipantMapper.selectSysMeetingsParticipantList(sysMeetingsParticipant);
    }

    /**
     * 新增会议参与人员
     * 
     * @param sysMeetingsParticipant 会议参与人员
     * @return 结果
     */
    @Override
    public int insertSysMeetingsParticipant(SysMeetingsParticipant sysMeetingsParticipant)
    {
        return sysMeetingsParticipantMapper.insertSysMeetingsParticipant(sysMeetingsParticipant);
    }

    /**
     * 修改会议参与人员
     * 
     * @param sysMeetingsParticipant 会议参与人员
     * @return 结果
     */
    @Override
    public int updateSysMeetingsParticipant(SysMeetingsParticipant sysMeetingsParticipant)
    {
        return sysMeetingsParticipantMapper.updateSysMeetingsParticipant(sysMeetingsParticipant);
    }

    /**
     * 批量删除会议参与人员
     * 
     * @param ids 需要删除的会议参与人员主键
     * @return 结果
     */
    @Override
    public int deleteSysMeetingsParticipantByIds(Long[] ids)
    {
        return sysMeetingsParticipantMapper.deleteSysMeetingsParticipantByIds(ids);
    }

    /**
     * 删除会议参与人员信息
     * 
     * @param id 会议参与人员主键
     * @return 结果
     */
    @Override
    public int deleteSysMeetingsParticipantById(Long id)
    {
        return sysMeetingsParticipantMapper.deleteSysMeetingsParticipantById(id);
    }
}
