package com.ipmbmsys.system.mapper;

import java.util.List;
import com.ipmbmsys.system.domain.SysCarousel;

/**
 * 轮播图管理Mapper接口
 * 
 * @author ruoyi
 * @date 2025-06-17
 */
public interface SysCarouselMapper 
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
     * 删除轮播图管理
     * 
     * @param id 轮播图管理主键
     * @return 结果
     */
    public int deleteSysCarouselById(Long id);

    /**
     * 批量删除轮播图管理
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysCarouselByIds(Long[] ids);
}
