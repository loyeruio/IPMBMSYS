package com.ipmbmsys.system.service.impl;

import java.util.List;
import com.ipmbmsys.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ipmbmsys.system.mapper.SysCarouselMapper;
import com.ipmbmsys.system.domain.SysCarousel;
import com.ipmbmsys.system.service.ISysCarouselService;

/**
 * 轮播图管理Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-06-17
 */
@Service
public class SysCarouselServiceImpl implements ISysCarouselService 
{
    @Autowired
    private SysCarouselMapper sysCarouselMapper;

    /**
     * 查询轮播图管理
     * 
     * @param id 轮播图管理主键
     * @return 轮播图管理
     */
    @Override
    public SysCarousel selectSysCarouselById(Long id)
    {
        return sysCarouselMapper.selectSysCarouselById(id);
    }

    /**
     * 查询轮播图管理列表
     * 
     * @param sysCarousel 轮播图管理
     * @return 轮播图管理
     */
    @Override
    public List<SysCarousel> selectSysCarouselList(SysCarousel sysCarousel)
    {
        return sysCarouselMapper.selectSysCarouselList(sysCarousel);
    }

    /**
     * 新增轮播图管理
     * 
     * @param sysCarousel 轮播图管理
     * @return 结果
     */
    @Override
    public int insertSysCarousel(SysCarousel sysCarousel)
    {
        sysCarousel.setCreateTime(DateUtils.getNowDate());
        return sysCarouselMapper.insertSysCarousel(sysCarousel);
    }

    /**
     * 修改轮播图管理
     * 
     * @param sysCarousel 轮播图管理
     * @return 结果
     */
    @Override
    public int updateSysCarousel(SysCarousel sysCarousel)
    {
        sysCarousel.setUpdateTime(DateUtils.getNowDate());
        return sysCarouselMapper.updateSysCarousel(sysCarousel);
    }

    /**
     * 批量删除轮播图管理
     * 
     * @param ids 需要删除的轮播图管理主键
     * @return 结果
     */
    @Override
    public int deleteSysCarouselByIds(Long[] ids)
    {
        return sysCarouselMapper.deleteSysCarouselByIds(ids);
    }

    /**
     * 删除轮播图管理信息
     * 
     * @param id 轮播图管理主键
     * @return 结果
     */
    @Override
    public int deleteSysCarouselById(Long id)
    {
        return sysCarouselMapper.deleteSysCarouselById(id);
    }
}
