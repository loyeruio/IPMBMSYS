package com.ipmbmsys.quartz.task;

import com.ipmbmsys.meeting.domain.SysHalfMonthLearning;
import com.ipmbmsys.meeting.service.ISysHalfMonthLearningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 半月学状态定时任务
 *
 * @author ruoyi
 */
@Component("halfMonthLearningStatusTask")
public class HalfMonthLearningStatusTask
{
    @Autowired
    private ISysHalfMonthLearningService halfMonthLearningService;

    /**
     * 更新半月学状态
     * 进行中：0、已结束：1、未开始：2
     */
    public void updateHalfMonthLearningStatus()
    {
        try {
            // 查询所有需要更新状态的半月学
            SysHalfMonthLearning queryParam = new SysHalfMonthLearning();
            List<SysHalfMonthLearning> learningList = halfMonthLearningService.selectSysHalfMonthLearningList(queryParam);
            
            Date currentTime = new Date();
            int updateCount = 0;
            
            for (SysHalfMonthLearning learning : learningList) {
                String newStatus = determineStatus(learning, currentTime);
                
                // 只有状态发生变化时才更新
                if (!newStatus.equals(learning.getStatus())) {
                    learning.setStatus(newStatus);
                    halfMonthLearningService.updateSysHalfMonthLearning(learning);
                    updateCount++;
                }
            }
            
            System.out.println("半月学状态更新完成，共更新 " + updateCount + " 条记录");
        } catch (Exception e) {
            System.err.println("半月学状态更新失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 根据时间判断半月学状态
     * @param learning 半月学对象
     * @param currentTime 当前时间
     * @return 状态值（0：进行中，1：已结束，2：未开始）
     */
    private String determineStatus(SysHalfMonthLearning learning, Date currentTime)
    {
        Date startTime = learning.getStartTime();
        Date endTime = learning.getEndTime();
        
        if (startTime == null || endTime == null)
        {
            return "2"; // 时间不完整，默认未开始
        }
        
        if (currentTime.before(startTime))
        {
            return "2"; // 未开始
        }
        else if (currentTime.after(endTime))
        {
            return "1"; // 已结束
        }
        else
        {
            return "0"; // 进行中
        }
    }
}