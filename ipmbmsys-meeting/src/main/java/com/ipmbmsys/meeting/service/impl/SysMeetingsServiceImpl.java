package com.ipmbmsys.meeting.service.impl;

import java.util.List;
import com.ipmbmsys.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ipmbmsys.meeting.mapper.SysMeetingsMapper;
import com.ipmbmsys.meeting.domain.SysMeetings;
import com.ipmbmsys.meeting.service.ISysMeetingsService;

/**
 * 会议管理Service业务层处理
 *
 * @author ruoyi
 * @date 2025-03-06
 */
@Service
public class SysMeetingsServiceImpl implements ISysMeetingsService
{
    @Autowired
    private SysMeetingsMapper sysMeetingsMapper;

    /**
     * 查询会议管理
     *
     * @param id 会议管理主键
     * @return 会议管理
     */
    @Override
    public SysMeetings selectSysMeetingsById(Long id)
    {
        return sysMeetingsMapper.selectSysMeetingsById(id);
    }

    /**
     * 查询会议管理列表
     *
     * @param SysMeetings 会议管理
     * @return 会议管理
     */
    @Override
    public List<SysMeetings> selectSysMeetingsList(SysMeetings SysMeetings)
    {
        return sysMeetingsMapper.selectSysMeetingsList(SysMeetings);
    }

    /**
     * 新增会议管理
     *
     * @param SysMeetings 会议管理
     * @return 结果
     */
    @Override
    public int insertSysMeetings(SysMeetings SysMeetings)
    {
        SysMeetings.setCreateTime(DateUtils.getNowDate());
        return sysMeetingsMapper.insertSysMeetings(SysMeetings);
    }

    /**
     * 修改会议管理
     *
     * @param SysMeetings 会议管理
     * @return 结果
     */
    @Override
    public int updateSysMeetings(SysMeetings SysMeetings)
    {
        return sysMeetingsMapper.updateSysMeetings(SysMeetings);
    }

    /**
     * 批量删除会议管理
     *
     * @param ids 需要删除的会议管理主键
     * @return 结果
     */
    @Override
    public int deleteSysMeetingsByIds(Long[] ids)
    {
        return sysMeetingsMapper.deleteSysMeetingsByIds(ids);
    }

    /**
     * 删除会议管理信息
     *
     * @param id 会议管理主键
     * @return 结果
     */
    @Override
    public int deleteSysMeetingsById(Long id)
    {
        return sysMeetingsMapper.deleteSysMeetingsById(id);
    }

}
