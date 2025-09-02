package com.ipmbmsys.meeting.service.impl;

import java.util.List;
import com.ipmbmsys.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ipmbmsys.meeting.mapper.SysHalfMonthLearningMapper;
import com.ipmbmsys.meeting.domain.SysHalfMonthLearning;
import com.ipmbmsys.meeting.service.ISysHalfMonthLearningService;

/**
 * learningService业务层处理
 *
 * @author ruoyi
 * @date 2025-06-06
 */
@Service
public class SysHalfMonthLearningServiceImpl implements ISysHalfMonthLearningService
{
    @Autowired
    private SysHalfMonthLearningMapper sysHalfMonthLearningMapper;

    /**
     * 查询learning
     *
     * @param id learning主键
     * @return learning
     */
    @Override
    public SysHalfMonthLearning selectSysHalfMonthLearningById(Long id)
    {
        return sysHalfMonthLearningMapper.selectSysHalfMonthLearningById(id);
    }

    /**
     * 查询learning列表
     *
     * @param sysHalfMonthLearning learning
     * @return learning
     */
    @Override
    public List<SysHalfMonthLearning> selectSysHalfMonthLearningList(SysHalfMonthLearning sysHalfMonthLearning)
    {
        return sysHalfMonthLearningMapper.selectSysHalfMonthLearningList(sysHalfMonthLearning);
    }

    /**
     * 新增learning
     *
     * @param sysHalfMonthLearning learning
     * @return 结果
     */
    @Override
    public int insertSysHalfMonthLearning(SysHalfMonthLearning sysHalfMonthLearning)
    {
        sysHalfMonthLearning.setCreateTime(DateUtils.getNowDate());
        return sysHalfMonthLearningMapper.insertSysHalfMonthLearning(sysHalfMonthLearning);
    }

    /**
     * 修改learning
     *
     * @param sysHalfMonthLearning learning
     * @return 结果
     */
    @Override
    public int updateSysHalfMonthLearning(SysHalfMonthLearning sysHalfMonthLearning)
    {
        return sysHalfMonthLearningMapper.updateSysHalfMonthLearning(sysHalfMonthLearning);
    }

    /**
     * 批量删除learning
     *
     * @param ids 需要删除的learning主键
     * @return 结果
     */
    @Override
    public int deleteSysHalfMonthLearningByIds(Long[] ids)
    {
        return sysHalfMonthLearningMapper.deleteSysHalfMonthLearningByIds(ids);
    }

    /**
     * 删除learning信息
     *
     * @param id learning主键
     * @return 结果
     */
    @Override
    public int deleteSysHalfMonthLearningById(Long id)
    {
        return sysHalfMonthLearningMapper.deleteSysHalfMonthLearningById(id);
    }
}
