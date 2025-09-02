package com.ipmbmsys.meeting.service;

import java.util.List;
import com.ipmbmsys.meeting.domain.SysMeetingsParticipant;

/**
 * 会议参与人员Service接口
 * 
 * @author ruoyi
 * @date 2025-06-12
 */
public interface ISysMeetingsParticipantService 
{
    /**
     * 查询会议参与人员
     * 
     * @param id 会议参与人员主键
     * @return 会议参与人员
     */
    public SysMeetingsParticipant selectSysMeetingsParticipantById(Long id);

    /**
     * 查询会议参与人员列表
     * 
     * @param sysMeetingsParticipant 会议参与人员
     * @return 会议参与人员集合
     */
    public List<SysMeetingsParticipant> selectSysMeetingsParticipantList(SysMeetingsParticipant sysMeetingsParticipant);

    /**
     * 新增会议参与人员
     * 
     * @param sysMeetingsParticipant 会议参与人员
     * @return 结果
     */
    public int insertSysMeetingsParticipant(SysMeetingsParticipant sysMeetingsParticipant);

    /**
     * 修改会议参与人员
     * 
     * @param sysMeetingsParticipant 会议参与人员
     * @return 结果
     */
    public int updateSysMeetingsParticipant(SysMeetingsParticipant sysMeetingsParticipant);

    /**
     * 批量删除会议参与人员
     * 
     * @param ids 需要删除的会议参与人员主键集合
     * @return 结果
     */
    public int deleteSysMeetingsParticipantByIds(Long[] ids);

    /**
     * 删除会议参与人员信息
     * 
     * @param id 会议参与人员主键
     * @return 结果
     */
    public int deleteSysMeetingsParticipantById(Long id);
}
