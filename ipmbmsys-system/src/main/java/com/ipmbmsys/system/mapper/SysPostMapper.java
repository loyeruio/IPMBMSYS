package com.ipmbmsys.system.mapper;

import java.util.List;
import com.ipmbmsys.common.core.domain.entity.SysPost;

/**
 * 职务信息 数据层
 *
 * @author 贺祎阳
 */
public interface SysPostMapper
{
    /**
     * 查询职务数据集合
     *
     * @param post 职务信息
     * @return 职务数据集合
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
     * 查询用户所属职务组
     *
     * @param userName 用户名
     * @return 结果
     */
    public List<SysPost> selectPostsByUserName(String userName);

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
     * 修改职务信息
     *
     * @param post 职务信息
     * @return 结果
     */
    public int updatePost(SysPost post);

    /**
     * 新增职务信息
     *
     * @param post 职务信息
     * @return 结果
     */
    public int insertPost(SysPost post);

    /**
     * 校验职务名称
     *
     * @param postName 职务名称
     * @return 结果
     */
    public SysPost checkPostNameUnique(String postName);

    /**
     * 校验职务编码
     *
     * @param postDescribes 职务编码
     * @return 结果
     */
    public SysPost checkPostDescribesUnique(String postDescribes);

    /**
     * 根据职务ID列表查询职务信息
     *
     * @param postIds 职务ID数组
     * @return 职务列表
     */
    public List<SysPost> selectPostByIds(Long[] postIds);
}
