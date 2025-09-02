package com.ipmbmsys.meeting.mapper;

import java.util.List;
import com.ipmbmsys.meeting.domain.SysClassParticipant;

/**
 * 党课参与人员Mapper接口
 * 
 * @author ruoyi
 * @date 2025-06-12
 */
public interface SysClassParticipantMapper 
{
    /**
     * 查询党课参与人员
     * 
     * @param id 党课参与人员主键
     * @return 党课参与人员
     */
    public SysClassParticipant selectSysClassParticipantById(Long id);

    /**
     * 查询党课参与人员列表
     * 
     * @param sysClassParticipant 党课参与人员
     * @return 党课参与人员集合
     */
    public List<SysClassParticipant> selectSysClassParticipantList(SysClassParticipant sysClassParticipant);

    /**
     * 新增党课参与人员
     * 
     * @param sysClassParticipant 党课参与人员
     * @return 结果
     */
    public int insertSysClassParticipant(SysClassParticipant sysClassParticipant);

    /**
     * 修改党课参与人员
     * 
     * @param sysClassParticipant 党课参与人员
     * @return 结果
     */
    public int updateSysClassParticipant(SysClassParticipant sysClassParticipant);

    /**
     * 删除党课参与人员
     * 
     * @param id 党课参与人员主键
     * @return 结果
     */
    public int deleteSysClassParticipantById(Long id);

    /**
     * 批量删除党课参与人员
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysClassParticipantByIds(Long[] ids);
}
