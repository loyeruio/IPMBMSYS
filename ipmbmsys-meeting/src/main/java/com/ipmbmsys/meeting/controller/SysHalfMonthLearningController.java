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
import com.ipmbmsys.meeting.domain.SysHalfMonthLearning;
import com.ipmbmsys.meeting.service.ISysHalfMonthLearningService;
import com.ipmbmsys.common.utils.poi.ExcelUtil;
import com.ipmbmsys.common.core.page.TableDataInfo;

/**
 * learningController
 *
 * @author ruoyi
 * @date 2025-06-06
 */
@RestController
@RequestMapping("/learning/learning")
public class SysHalfMonthLearningController extends BaseController
{
    @Autowired
    private ISysHalfMonthLearningService sysHalfMonthLearningService;

    /**
     * 查询learning列表
     */
    @PreAuthorize("@ss.hasPermi('learning:learning:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysHalfMonthLearning sysHalfMonthLearning)
    {
        startPage();
        List<SysHalfMonthLearning> list = sysHalfMonthLearningService.selectSysHalfMonthLearningList(sysHalfMonthLearning);
        return getDataTable(list);
    }

    /**
     * 导出learning列表
     */
    @PreAuthorize("@ss.hasPermi('learning:learning:export')")
    @Log(title = "learning", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysHalfMonthLearning sysHalfMonthLearning)
    {
        List<SysHalfMonthLearning> list = sysHalfMonthLearningService.selectSysHalfMonthLearningList(sysHalfMonthLearning);
        ExcelUtil<SysHalfMonthLearning> util = new ExcelUtil<SysHalfMonthLearning>(SysHalfMonthLearning.class);
        util.exportExcel(response, list, "learning数据");
    }

    /**
     * 获取learning详细信息
     */
    @PreAuthorize("@ss.hasPermi('learning:learning:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(sysHalfMonthLearningService.selectSysHalfMonthLearningById(id));
    }

    /**
     * 新增learning
     */
    @PreAuthorize("@ss.hasPermi('learning:learning:add')")
    @Log(title = "learning", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysHalfMonthLearning sysHalfMonthLearning)
    {
        return toAjax(sysHalfMonthLearningService.insertSysHalfMonthLearning(sysHalfMonthLearning));
    }

    /**
     * 修改learning
     */
    @PreAuthorize("@ss.hasPermi('learning:learning:edit')")
    @Log(title = "learning", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysHalfMonthLearning sysHalfMonthLearning)
    {
        return toAjax(sysHalfMonthLearningService.updateSysHalfMonthLearning(sysHalfMonthLearning));
    }

    /**
     * 删除learning
     */
    @PreAuthorize("@ss.hasPermi('learning:learning:remove')")
    @Log(title = "learning", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(sysHalfMonthLearningService.deleteSysHalfMonthLearningByIds(ids));
    }
}
