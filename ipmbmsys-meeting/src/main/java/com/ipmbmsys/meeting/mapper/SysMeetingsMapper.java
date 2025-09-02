package com.ipmbmsys.meeting.mapper;

import java.util.List;
import com.ipmbmsys.meeting.domain.SysMeetings;

/**
 * 会议管理Mapper接口
 *
 * @author ruoyi
 * @date 2025-03-06
 */
public interface SysMeetingsMapper
{
    /**
     * 查询会议管理
     *
     * @param id 会议管理主键
     * @return 会议管理
     */
    public SysMeetings selectSysMeetingsById(Long id);

    /**
     * 查询会议管理列表
     *
     * @param SysMeetings 会议管理
     * @return 会议管理集合
     */
    public List<SysMeetings> selectSysMeetingsList(SysMeetings SysMeetings);

    /**
     * 新增会议管理
     *
     * @param SysMeetings 会议管理
     * @return 结果
     */
    public int insertSysMeetings(SysMeetings SysMeetings);

    /**
     * 修改会议管理
     *
     * @param SysMeetings 会议管理
     * @return 结果
     */
    public int updateSysMeetings(SysMeetings SysMeetings);

    /**
     * 删除会议管理
     *
     * @param id 会议管理主键
     * @return 结果
     */
    public int deleteSysMeetingsById(Long id);

    /**
     * 批量删除会议管理
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysMeetingsByIds(Long[] ids);

    /**
     * 批量更新会议状态
     *
     * @param meetings 会议列表
     * @return 结果
     */
    public int batchUpdateMeetingStatus(List<SysMeetings> meetings);
    
    /**
     * 查询需要更新状态的会议（状态可能不准确的会议）
     *
     * @return 会议列表
     */
    public List<SysMeetings> selectMeetingsForStatusUpdate();
}
