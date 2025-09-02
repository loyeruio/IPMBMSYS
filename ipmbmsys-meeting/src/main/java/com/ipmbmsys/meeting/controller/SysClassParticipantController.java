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
import com.ipmbmsys.meeting.domain.SysClassParticipant;
import com.ipmbmsys.meeting.service.ISysClassParticipantService;
import com.ipmbmsys.common.utils.poi.ExcelUtil;
import com.ipmbmsys.common.core.page.TableDataInfo;

/**
 * 党课参与人员Controller
 * 
 * @author ruoyi
 * @date 2025-06-12
 */
@RestController
@RequestMapping("/meeting/classparticipant")
public class SysClassParticipantController extends BaseController
{
    @Autowired
    private ISysClassParticipantService sysClassParticipantService;

    /**
     * 查询党课参与人员列表
     */
    @PreAuthorize("@ss.hasPermi('meeting:classparticipant:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysClassParticipant sysClassParticipant)
    {
        startPage();
        List<SysClassParticipant> list = sysClassParticipantService.selectSysClassParticipantList(sysClassParticipant);
        return getDataTable(list);
    }

    /**
     * 导出党课参与人员列表
     */
    @PreAuthorize("@ss.hasPermi('meeting:classparticipant:export')")
    @Log(title = "党课参与人员", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysClassParticipant sysClassParticipant)
    {
        List<SysClassParticipant> list = sysClassParticipantService.selectSysClassParticipantList(sysClassParticipant);
        ExcelUtil<SysClassParticipant> util = new ExcelUtil<SysClassParticipant>(SysClassParticipant.class);
        util.exportExcel(response, list, "党课参与人员数据");
    }

    /**
     * 获取党课参与人员详细信息
     */
    @PreAuthorize("@ss.hasPermi('meeting:classparticipant:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(sysClassParticipantService.selectSysClassParticipantById(id));
    }

    /**
     * 新增党课参与人员
     */
    @PreAuthorize("@ss.hasPermi('meeting:classparticipant:add')")
    @Log(title = "党课参与人员", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysClassParticipant sysClassParticipant)
    {
        return toAjax(sysClassParticipantService.insertSysClassParticipant(sysClassParticipant));
    }

    /**
     * 修改党课参与人员
     */
    @PreAuthorize("@ss.hasPermi('meeting:classparticipant:edit')")
    @Log(title = "党课参与人员", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysClassParticipant sysClassParticipant)
    {
        return toAjax(sysClassParticipantService.updateSysClassParticipant(sysClassParticipant));
    }

    /**
     * 删除党课参与人员
     */
    @PreAuthorize("@ss.hasPermi('meeting:classparticipant:remove')")
    @Log(title = "党课参与人员", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(sysClassParticipantService.deleteSysClassParticipantByIds(ids));
    }
}
