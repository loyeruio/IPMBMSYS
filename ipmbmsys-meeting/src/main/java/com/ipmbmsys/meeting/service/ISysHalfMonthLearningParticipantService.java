package com.ipmbmsys.meeting.service;

import java.util.List;
import com.ipmbmsys.meeting.domain.SysHalfMonthLearningParticipant;

/**
 * participantService接口
 * 
 * @author ruoyi
 * @date 2025-06-07
 */
public interface ISysHalfMonthLearningParticipantService 
{
    /**
     * 查询participant
     * 
     * @param id participant主键
     * @return participant
     */
    public SysHalfMonthLearningParticipant selectSysHalfMonthLearningParticipantById(Long id);

    /**
     * 查询participant列表
     * 
     * @param sysHalfMonthLearningParticipant participant
     * @return participant集合
     */
    public List<SysHalfMonthLearningParticipant> selectSysHalfMonthLearningParticipantList(SysHalfMonthLearningParticipant sysHalfMonthLearningParticipant);

    /**
     * 新增participant
     * 
     * @param sysHalfMonthLearningParticipant participant
     * @return 结果
     */
    public int insertSysHalfMonthLearningParticipant(SysHalfMonthLearningParticipant sysHalfMonthLearningParticipant);

    /**
     * 修改participant
     * 
     * @param sysHalfMonthLearningParticipant participant
     * @return 结果
     */
    public int updateSysHalfMonthLearningParticipant(SysHalfMonthLearningParticipant sysHalfMonthLearningParticipant);

    /**
     * 批量删除participant
     * 
     * @param ids 需要删除的participant主键集合
     * @return 结果
     */
    public int deleteSysHalfMonthLearningParticipantByIds(Long[] ids);

    /**
     * 删除participant信息
     * 
     * @param id participant主键
     * @return 结果
     */
    public int deleteSysHalfMonthLearningParticipantById(Long id);
}
