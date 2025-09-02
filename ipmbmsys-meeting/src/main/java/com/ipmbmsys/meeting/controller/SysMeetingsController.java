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
import com.ipmbmsys.meeting.domain.SysMeetings;
import com.ipmbmsys.meeting.service.ISysMeetingsService;
import com.ipmbmsys.common.utils.poi.ExcelUtil;
import com.ipmbmsys.common.core.page.TableDataInfo;

/**
 * 会议管理Controller
 *
 * @author ruoyi
 * @date 2025-03-06
 */
@RestController
@RequestMapping("/meeting/meeting")
public class SysMeetingsController extends BaseController
{
    @Autowired
    private ISysMeetingsService SysMeetingsService;

    /**
     * 查询会议管理列表
     */
    @PreAuthorize("@ss.hasPermi('meeting:meeting:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysMeetings SysMeetings)
    {
        startPage();
        List<SysMeetings> list = SysMeetingsService.selectSysMeetingsList(SysMeetings);
        return getDataTable(list);
    }

    /**
     * 导出会议管理列表
     */
    @PreAuthorize("@ss.hasPermi('meeting:meeting:export')")
    @Log(title = "会议管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysMeetings SysMeetings)
    {
        List<SysMeetings> list = SysMeetingsService.selectSysMeetingsList(SysMeetings);
        ExcelUtil<SysMeetings> util = new ExcelUtil<SysMeetings>(SysMeetings.class);
        util.exportExcel(response, list, "会议管理数据");
    }

    /**
     * 获取会议管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('meeting:meeting:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(SysMeetingsService.selectSysMeetingsById(id));
    }

    /**
     * 新增会议管理
     */
    @PreAuthorize("@ss.hasPermi('meeting:meeting:add')")
    @Log(title = "会议管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysMeetings SysMeetings)
    {
        return toAjax(SysMeetingsService.insertSysMeetings(SysMeetings));
    }

    /**
     * 修改会议管理
     */
    @PreAuthorize("@ss.hasPermi('meeting:meeting:edit')")
    @Log(title = "会议管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysMeetings SysMeetings)
    {
        return toAjax(SysMeetingsService.updateSysMeetings(SysMeetings));
    }

    /**
     * 删除会议管理
     */
    @PreAuthorize("@ss.hasPermi('meeting:meeting:remove')")
    @Log(title = "会议管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(SysMeetingsService.deleteSysMeetingsByIds(ids));
    }
}
