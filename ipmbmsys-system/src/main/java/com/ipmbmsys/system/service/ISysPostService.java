package com.ipmbmsys.system.service;

import java.util.List;

import com.ipmbmsys.common.core.domain.entity.SysPost;

/**
 * 职务信息 服务层
 *
 * @author 贺祎阳
 */
public interface ISysPostService
{
    /**
     * 查询职务信息集合
     *
     * @param post 职务信息
     * @return 职务列表
     */
    public List<SysPost> selectPostList(SysPost post);

    /**
     * 查询所有职务
     *
     * @return 职务列表
     */
    public List<SysPost> selectPostAll();

    /**
     * 通过职务ID查询职务信息
     *
     * @param postId 职务ID
     * @return 角色对象信息
     */
    public SysPost selectPostById(Long postId);

    /**
     * 根据用户ID获取职务选择框列表
     *
     * @param userId 用户ID
     * @return 选中职务ID列表
     */
    public List<Long> selectPostListByUserId(Long userId);

    /**
     * 校验职务名称
     *
     * @param post 职务信息
     * @return 结果
     */
    public boolean checkPostNameUnique(SysPost post);

    /**
     * 校验职务编码
     *
     * @param post 职务信息
     * @return 结果
     */
    public boolean checkPostDescribesUnique(SysPost post);

    /**
     * 通过职务ID查询职务使用数量
     *
     * @param postId 职务ID
     * @return 结果
     */
    public int countUserPostById(Long postId);

    /**
     * 删除职务信息
     *
     * @param postId 职务ID
     * @return 结果
     */
    public int deletePostById(Long postId);

    /**
     * 批量删除职务信息
     *
     * @param postIds 需要删除的职务ID
     * @return 结果
     */
    public int deletePostByIds(Long[] postIds);

    /**
     * 新增保存职务信息
     *
     * @param post 职务信息
     * @return 结果
     */
    public int insertPost(SysPost post);

    /**
     * 修改保存职务信息
     *
     * @param post 职务信息
     * @return 结果
     */
    public int updatePost(SysPost post);

    /**
     * 修改保存职务信息
     *
     * @param postIds,deptIds职务信息
     * @return 结果
     */
    int bindPostsToDepts(Long[] postIds, Long[] deptIds);

    /**
     * 根据职务ID列表获取职务信息
     *
     * @param postIds 职务ID数组
     * @return 职务列表
     */
    public List<SysPost> selectPostByIds(Long[] postIds);
}
