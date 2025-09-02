package com.ipmbmsys.quartz.task;

import com.ipmbmsys.meeting.domain.SysClass;
import com.ipmbmsys.meeting.service.ISysClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 党课状态定时任务
 *
 * @author ruoyi
 */
@Component("classStatusTask")
public class ClassStatusTask
{
    @Autowired
    private ISysClassService classService;

    /**
     * 更新党课状态
     * 进行中：0、已结束：1、未开始：2
     */
    public void updateClassStatus()
    {
        try {
            // 查询所有需要更新状态的党课
            SysClass queryParam = new SysClass();
            List<SysClass> classList = classService.selectSysClassList(queryParam);
            
            Date currentTime = new Date();
            int updateCount = 0;
            
            for (SysClass sysClass : classList) {
                String newStatus = determineStatus(sysClass, currentTime);
                
                // 只有状态发生变化时才更新
                if (!newStatus.equals(sysClass.getStatus())) {
                    sysClass.setStatus(newStatus);
                    classService.updateSysClass(sysClass);
                    updateCount++;
                }
            }
            
            System.out.println("党课状态更新完成，共更新 " + updateCount + " 条记录");
        } catch (Exception e) {
            System.err.println("党课状态更新失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 根据时间判断党课状态
     * @param sysClass 党课对象
     * @param currentTime 当前时间
     * @return 状态值（0：进行中，1：已结束，2：未开始）
     */
    private String determineStatus(SysClass sysClass, Date currentTime)
    {
        Date startTime = sysClass.getStartTime();
        Date endTime = sysClass.getEndTime();
        
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