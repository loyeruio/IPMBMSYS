package com.ipmbmsys.system.service;

import java.util.List;
import com.ipmbmsys.system.domain.SysCarousel;

/**
 * 轮播图管理Service接口
 * 
 * @author ruoyi
 * @date 2025-06-17
 */
public interface ISysCarouselService 
{
    /**
     * 查询轮播图管理
     * 
     * @param id 轮播图管理主键
     * @return 轮播图管理
     */
    public SysCarousel selectSysCarouselById(Long id);

    /**
     * 查询轮播图管理列表
     * 
     * @param sysCarousel 轮播图管理
     * @return 轮播图管理集合
     */
    public List<SysCarousel> selectSysCarouselList(SysCarousel sysCarousel);

    /**
     * 新增轮播图管理
     * 
     * @param sysCarousel 轮播图管理
     * @return 结果
     */
    public int insertSysCarousel(SysCarousel sysCarousel);

    /**
     * 修改轮播图管理
     * 
     * @param sysCarousel 轮播图管理
     * @return 结果
     */
    public int updateSysCarousel(SysCarousel sysCarousel);

    /**
     * 批量删除轮播图管理
     * 
     * @param ids 需要删除的轮播图管理主键集合
     * @return 结果
     */
    public int deleteSysCarouselByIds(Long[] ids);

    /**
     * 删除轮播图管理信息
     * 
     * @param id 轮播图管理主键
     * @return 结果
     */
    public int deleteSysCarouselById(Long id);
}
