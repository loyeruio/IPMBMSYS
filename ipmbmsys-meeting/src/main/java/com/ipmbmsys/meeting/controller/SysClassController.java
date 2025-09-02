package com.ipmbmsys.meeting.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ipmbmsys.meeting.domain.SysClass;
import com.ipmbmsys.meeting.service.ISysClassService;
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

import com.ipmbmsys.common.utils.poi.ExcelUtil;
import com.ipmbmsys.common.core.page.TableDataInfo;

/**
 * classController
 *
 * @author ruoyi
 * @date 2025-06-03
 */
@RestController
@RequestMapping("/class/class")
public class SysClassController extends BaseController
{
    @Autowired
    private ISysClassService sysClassService;

    /**
     * 查询class列表
     */
    @PreAuthorize("@ss.hasPermi('class:class:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysClass sysClass)
    {
        startPage();
        List<SysClass> list = sysClassService.selectSysClassList(sysClass);
        return getDataTable(list);
    }

    /**
     * 导出class列表
     */
    @PreAuthorize("@ss.hasPermi('class:class:export')")
    @Log(title = "class", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysClass sysClass)
    {
        List<SysClass> list = sysClassService.selectSysClassList(sysClass);
        ExcelUtil<SysClass> util = new ExcelUtil<SysClass>(SysClass.class);
        util.exportExcel(response, list, "class数据");
    }

    /**
     * 获取class详细信息
     */
    @PreAuthorize("@ss.hasPermi('class:class:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(sysClassService.selectSysClassById(id));
    }

    /**
     * 新增class
     */
    @PreAuthorize("@ss.hasPermi('class:class:add')")
    @Log(title = "class", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysClass sysClass)
    {
        return toAjax(sysClassService.insertSysClass(sysClass));
    }

    /**
     * 修改class
     */
    @PreAuthorize("@ss.hasPermi('class:class:edit')")
    @Log(title = "class", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysClass sysClass)
    {
        return toAjax(sysClassService.updateSysClass(sysClass));
    }

    /**
     * 删除class
     */
    @PreAuthorize("@ss.hasPermi('class:class:remove')")
    @Log(title = "class", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(sysClassService.deleteSysClassByIds(ids));
    }
}
