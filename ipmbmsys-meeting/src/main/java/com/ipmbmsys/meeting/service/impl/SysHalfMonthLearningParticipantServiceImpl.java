package com.ipmbmsys.meeting.service.impl;

import java.util.List;
import com.ipmbmsys.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ipmbmsys.meeting.mapper.SysHalfMonthLearningParticipantMapper;
import com.ipmbmsys.meeting.domain.SysHalfMonthLearningParticipant;
import com.ipmbmsys.meeting.service.ISysHalfMonthLearningParticipantService;

/**
 * participantService业务层处理
 * 
 * @author ruoyi
 * @date 2025-06-07
 */
@Service
public class SysHalfMonthLearningParticipantServiceImpl implements ISysHalfMonthLearningParticipantService 
{
    @Autowired
    private SysHalfMonthLearningParticipantMapper sysHalfMonthLearningParticipantMapper;

    /**
     * 查询participant
     * 
     * @param id participant主键
     * @return participant
     */
    @Override
    public SysHalfMonthLearningParticipant selectSysHalfMonthLearningParticipantById(Long id)
    {
        return sysHalfMonthLearningParticipantMapper.selectSysHalfMonthLearningParticipantById(id);
    }

    /**
     * 查询participant列表
     * 
     * @param sysHalfMonthLearningParticipant participant
     * @return participant
     */
    @Override
    public List<SysHalfMonthLearningParticipant> selectSysHalfMonthLearningParticipantList(SysHalfMonthLearningParticipant sysHalfMonthLearningParticipant)
    {
        return sysHalfMonthLearningParticipantMapper.selectSysHalfMonthLearningParticipantList(sysHalfMonthLearningParticipant);
    }

    /**
     * 新增participant
     * 
     * @param sysHalfMonthLearningParticipant participant
     * @return 结果
     */
    @Override
    public int insertSysHalfMonthLearningParticipant(SysHalfMonthLearningParticipant sysHalfMonthLearningParticipant)
    {
        sysHalfMonthLearningParticipant.setCreateTime(DateUtils.getNowDate());
        return sysHalfMonthLearningParticipantMapper.insertSysHalfMonthLearningParticipant(sysHalfMonthLearningParticipant);
    }

    /**
     * 修改participant
     * 
     * @param sysHalfMonthLearningParticipant participant
     * @return 结果
     */
    @Override
    public int updateSysHalfMonthLearningParticipant(SysHalfMonthLearningParticipant sysHalfMonthLearningParticipant)
    {
        return sysHalfMonthLearningParticipantMapper.updateSysHalfMonthLearningParticipant(sysHalfMonthLearningParticipant);
    }

    /**
     * 批量删除participant
     * 
     * @param ids 需要删除的participant主键
     * @return 结果
     */
    @Override
    public int deleteSysHalfMonthLearningParticipantByIds(Long[] ids)
    {
        return sysHalfMonthLearningParticipantMapper.deleteSysHalfMonthLearningParticipantByIds(ids);
    }

    /**
     * 删除participant信息
     * 
     * @param id participant主键
     * @return 结果
     */
    @Override
    public int deleteSysHalfMonthLearningParticipantById(Long id)
    {
        return sysHalfMonthLearningParticipantMapper.deleteSysHalfMonthLearningParticipantById(id);
    }
}
