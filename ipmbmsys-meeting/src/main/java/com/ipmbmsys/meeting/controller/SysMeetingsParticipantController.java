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
import com.ipmbmsys.meeting.domain.SysMeetingsParticipant;
import com.ipmbmsys.meeting.service.ISysMeetingsParticipantService;
import com.ipmbmsys.common.utils.poi.ExcelUtil;
import com.ipmbmsys.common.core.page.TableDataInfo;

/**
 * 会议参与人员Controller
 * 
 * @author ruoyi
 * @date 2025-06-12
 */
@RestController
@RequestMapping("/meeting/meetingparticipant")
public class SysMeetingsParticipantController extends BaseController
{
    @Autowired
    private ISysMeetingsParticipantService sysMeetingsParticipantService;

    /**
     * 查询会议参与人员列表
     */
    @PreAuthorize("@ss.hasPermi('meeting:meetingparticipant:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysMeetingsParticipant sysMeetingsParticipant)
    {
        startPage();
        List<SysMeetingsParticipant> list = sysMeetingsParticipantService.selectSysMeetingsParticipantList(sysMeetingsParticipant);
        return getDataTable(list);
    }

    /**
     * 导出会议参与人员列表
     */
    @PreAuthorize("@ss.hasPermi('meeting:meetingparticipant:export')")
    @Log(title = "会议参与人员", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysMeetingsParticipant sysMeetingsParticipant)
    {
        List<SysMeetingsParticipant> list = sysMeetingsParticipantService.selectSysMeetingsParticipantList(sysMeetingsParticipant);
        ExcelUtil<SysMeetingsParticipant> util = new ExcelUtil<SysMeetingsParticipant>(SysMeetingsParticipant.class);
        util.exportExcel(response, list, "会议参与人员数据");
    }

    /**
     * 获取会议参与人员详细信息
     */
    @PreAuthorize("@ss.hasPermi('meeting:meetingparticipant:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(sysMeetingsParticipantService.selectSysMeetingsParticipantById(id));
    }

    /**
     * 新增会议参与人员
     */
    @PreAuthorize("@ss.hasPermi('meeting:meetingparticipant:add')")
    @Log(title = "会议参与人员", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysMeetingsParticipant sysMeetingsParticipant)
    {
        return toAjax(sysMeetingsParticipantService.insertSysMeetingsParticipant(sysMeetingsParticipant));
    }

    /**
     * 修改会议参与人员
     */
    @PreAuthorize("@ss.hasPermi('meeting:meetingparticipant:edit')")
    @Log(title = "会议参与人员", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysMeetingsParticipant sysMeetingsParticipant)
    {
        return toAjax(sysMeetingsParticipantService.updateSysMeetingsParticipant(sysMeetingsParticipant));
    }

    /**
     * 删除会议参与人员
     */
    @PreAuthorize("@ss.hasPermi('meeting:meetingparticipant:remove')")
    @Log(title = "会议参与人员", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(sysMeetingsParticipantService.deleteSysMeetingsParticipantByIds(ids));
    }
}
