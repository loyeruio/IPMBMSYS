package com.ipmbmsys.system.controller;

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
import com.ipmbmsys.common.annotation.Anonymous;
import com.ipmbmsys.common.annotation.Log;
import com.ipmbmsys.common.core.controller.BaseController;
import com.ipmbmsys.common.core.domain.AjaxResult;
import com.ipmbmsys.common.enums.BusinessType;
import com.ipmbmsys.system.domain.SysCarousel;
import com.ipmbmsys.system.service.ISysCarouselService;
import com.ipmbmsys.common.utils.poi.ExcelUtil;
import com.ipmbmsys.common.core.page.TableDataInfo;

/**
 * 轮播图管理Controller
 * 
 * @author ruoyi
 * @date 2025-06-17
 */
@RestController
@RequestMapping("/system/carousel")
public class SysCarouselController extends BaseController
{
    @Autowired
    private ISysCarouselService sysCarouselService;

    /**
     * 查询轮播图管理列表
     */
    @PreAuthorize("@ss.hasPermi('system:carousel:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysCarousel sysCarousel)
    {
        startPage();
        List<SysCarousel> list = sysCarouselService.selectSysCarouselList(sysCarousel);
        return getDataTable(list);
    }

    /**
     * 公开接口：查询轮播图列表（无需权限，用于登录页和注册页）
     */
    @Anonymous
    @GetMapping("/public/list")
    public AjaxResult publicList(SysCarousel sysCarousel)
    {
        // 只返回状态为启用(0)的轮播图
        sysCarousel.setStatus("0");
        List<SysCarousel> list = sysCarouselService.selectSysCarouselList(sysCarousel);
        return AjaxResult.success(list);
    }

    /**
     * 导出轮播图管理列表
     */
    @PreAuthorize("@ss.hasPermi('system:carousel:export')")
    @Log(title = "轮播图管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysCarousel sysCarousel)
    {
        List<SysCarousel> list = sysCarouselService.selectSysCarouselList(sysCarousel);
        ExcelUtil<SysCarousel> util = new ExcelUtil<SysCarousel>(SysCarousel.class);
        util.exportExcel(response, list, "轮播图管理数据");
    }

    /**
     * 获取轮播图管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:carousel:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(sysCarouselService.selectSysCarouselById(id));
    }

    /**
     * 新增轮播图管理
     */
    @PreAuthorize("@ss.hasPermi('system:carousel:add')")
    @Log(title = "轮播图管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysCarousel sysCarousel)
    {
        return toAjax(sysCarouselService.insertSysCarousel(sysCarousel));
    }

    /**
     * 修改轮播图管理
     */
    @PreAuthorize("@ss.hasPermi('system:carousel:edit')")
    @Log(title = "轮播图管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysCarousel sysCarousel)
    {
        return toAjax(sysCarouselService.updateSysCarousel(sysCarousel));
    }

    /**
     * 删除轮播图管理
     */
    @PreAuthorize("@ss.hasPermi('system:carousel:remove')")
    @Log(title = "轮播图管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(sysCarouselService.deleteSysCarouselByIds(ids));
    }
}
