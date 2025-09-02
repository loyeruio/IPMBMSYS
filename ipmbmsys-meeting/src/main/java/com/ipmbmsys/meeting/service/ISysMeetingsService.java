package com.ipmbmsys.meeting.service;

import java.util.List;
import com.ipmbmsys.meeting.domain.SysMeetings;

/**
 * 会议管理Service接口
 *
 * @author ruoyi
 * @date 2025-03-06
 */
public interface ISysMeetingsService
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
     * 批量删除会议管理
     *
     * @param ids 需要删除的会议管理主键集合
     * @return 结果
     */
    public int deleteSysMeetingsByIds(Long[] ids);

    /**
     * 删除会议管理信息
     *
     * @param id 会议管理主键
     * @return 结果
     */
    public int deleteSysMeetingsById(Long id);


}
