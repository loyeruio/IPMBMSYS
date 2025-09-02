package com.ipmbmsys.meeting.service;

import java.util.List;
import com.ipmbmsys.meeting.domain.SysHalfMonthLearning;

/**
 * learningService接口
 *
 * @author ruoyi
 * @date 2025-06-06
 */
public interface ISysHalfMonthLearningService
{
    /**
     * 查询learning
     *
     * @param id learning主键
     * @return learning
     */
    public SysHalfMonthLearning selectSysHalfMonthLearningById(Long id);

    /**
     * 查询learning列表
     *
     * @param sysHalfMonthLearning learning
     * @return learning集合
     */
    public List<SysHalfMonthLearning> selectSysHalfMonthLearningList(SysHalfMonthLearning sysHalfMonthLearning);

    /**
     * 新增learning
     *
     * @param sysHalfMonthLearning learning
     * @return 结果
     */
    public int insertSysHalfMonthLearning(SysHalfMonthLearning sysHalfMonthLearning);

    /**
     * 修改learning
     *
     * @param sysHalfMonthLearning learning
     * @return 结果
     */
    public int updateSysHalfMonthLearning(SysHalfMonthLearning sysHalfMonthLearning);

    /**
     * 批量删除learning
     *
     * @param ids 需要删除的learning主键集合
     * @return 结果
     */
    public int deleteSysHalfMonthLearningByIds(Long[] ids);

    /**
     * 删除learning信息
     *
     * @param id learning主键
     * @return 结果
     */
    public int deleteSysHalfMonthLearningById(Long id);
}
