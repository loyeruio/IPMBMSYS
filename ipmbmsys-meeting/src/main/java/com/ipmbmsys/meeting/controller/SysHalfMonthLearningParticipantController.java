package com.ipmbmsys.meeting.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ipmbmsys.common.annotation.Log;
import com.ipmbmsys.common.core.controller.BaseController;
import com.ipmbmsys.common.core.domain.AjaxResult;
import com.ipmbmsys.common.enums.BusinessType;
import com.ipmbmsys.meeting.domain.SysHalfMonthLearningParticipant;
import com.ipmbmsys.meeting.service.ISysHalfMonthLearningParticipantService;
import com.ipmbmsys.common.utils.poi.ExcelUtil;
import com.ipmbmsys.common.core.page.TableDataInfo;

/**
 * participantController
 * 
 * @author ruoyi
 * @date 2025-06-07
 */
@RestController
@RequestMapping("/participant/participant")
public class SysHalfMonthLearningParticipantController extends BaseController
{
    @Autowired
    private ISysHalfMonthLearningParticipantService sysHalfMonthLearningParticipantService;

    /**
     * 查询participant列表
     */
    @PreAuthorize("@ss.hasPermi('participant:participant:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysHalfMonthLearningParticipant sysHalfMonthLearningParticipant)
    {
        startPage();
        List<SysHalfMonthLearningParticipant> list = sysHalfMonthLearningParticipantService.selectSysHalfMonthLearningParticipantList(sysHalfMonthLearningParticipant);
        return getDataTable(list);
    }

    /**
     * 导出participant列表
     */
    @PreAuthorize("@ss.hasPermi('participant:participant:export')")
    @Log(title = "participant", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysHalfMonthLearningParticipant sysHalfMonthLearningParticipant)
    {
        List<SysHalfMonthLearningParticipant> list = sysHalfMonthLearningParticipantService.selectSysHalfMonthLearningParticipantList(sysHalfMonthLearningParticipant);
        ExcelUtil<SysHalfMonthLearningParticipant> util = new ExcelUtil<SysHalfMonthLearningParticipant>(SysHalfMonthLearningParticipant.class);
        util.exportExcel(response, list, "participant数据");
    }

    /**
     * 获取participant详细信息
     */
    @PreAuthorize("@ss.hasPermi('participant:participant:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(sysHalfMonthLearningParticipantService.selectSysHalfMonthLearningParticipantById(id));
    }

    /**
     * 新增participant
     */
    @PreAuthorize("@ss.hasPermi('participant:participant:add')")
    @Log(title = "participant", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysHalfMonthLearningParticipant sysHalfMonthLearningParticipant)
    {
        return toAjax(sysHalfMonthLearningParticipantService.insertSysHalfMonthLearningParticipant(sysHalfMonthLearningParticipant));
    }

    /**
     * 修改participant
     */
    @PreAuthorize("@ss.hasPermi('participant:participant:edit')")
    @Log(title = "participant", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysHalfMonthLearningParticipant sysHalfMonthLearningParticipant)
    {
        return toAjax(sysHalfMonthLearningParticipantService.updateSysHalfMonthLearningParticipant(sysHalfMonthLearningParticipant));
    }

    /**
     * 删除participant
     */
    @PreAuthorize("@ss.hasPermi('participant:participant:remove')")
    @Log(title = "participant", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(sysHalfMonthLearningParticipantService.deleteSysHalfMonthLearningParticipantByIds(ids));
    }
}
