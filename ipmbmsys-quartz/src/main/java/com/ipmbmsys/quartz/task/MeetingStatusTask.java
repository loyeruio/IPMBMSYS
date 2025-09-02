package com.ipmbmsys.quartz.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ipmbmsys.meeting.service.ISysMeetingsService;
import com.ipmbmsys.meeting.domain.SysMeetings;
import java.util.List;
import java.util.Date;

/**
 * 会议状态自动更新定时任务
 *
 * @author 系统管理员
 */
@Component("meetingStatusTask")
public class MeetingStatusTask
{
    @Autowired
    private ISysMeetingsService sysMeetingsService;

    /**
     * 更新会议状态
     * 进行中：0、已结束：1、未开始：2
     */
    public void updateMeetingStatus()
    {
        try {
            // 查询所有需要更新状态的会议
            SysMeetings queryParam = new SysMeetings();
            List<SysMeetings> meetings = sysMeetingsService.selectSysMeetingsList(queryParam);
            
            Date currentTime = new Date();
            int updateCount = 0;
            
            for (SysMeetings meeting : meetings) {
                String newStatus = determineStatus(meeting, currentTime);
                
                // 只有状态发生变化时才更新
                if (!newStatus.equals(meeting.getStatus())) {
                    meeting.setStatus(newStatus);
                    sysMeetingsService.updateSysMeetings(meeting);
                    updateCount++;
                }
            }
            
            System.out.println("会议状态更新完成，共更新 " + updateCount + " 条记录");
        } catch (Exception e) {
            System.err.println("会议状态更新失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 根据会议时间判断状态
     * @param meeting 会议对象
     * @param currentTime 当前时间
     * @return 状态值（0：进行中，1：已结束，2：未开始）
     */
    private String determineStatus(SysMeetings meeting, Date currentTime) {
        if (meeting.getStartTime() == null || meeting.getEndTime() == null) {
            return "2"; // 时间不完整，默认未开始
        }
        
        if (currentTime.before(meeting.getStartTime())) {
            return "2"; // 未开始
        } else if (currentTime.after(meeting.getEndTime())) {
            return "1"; // 已结束
        } else {
            return "0"; // 进行中
        }
    }
}