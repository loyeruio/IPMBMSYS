package com.ipmbmsys.system.service.impl;

import java.util.ArrayList;
import java.util.List;
import com.ipmbmsys.common.core.domain.entity.SysPost;
import com.ipmbmsys.system.domain.SysPostDept;
import com.ipmbmsys.system.mapper.SysPostDeptMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ipmbmsys.common.constant.UserConstants;
import com.ipmbmsys.common.exception.ServiceException;
import com.ipmbmsys.common.utils.StringUtils;
import com.ipmbmsys.system.mapper.SysPostMapper;
import com.ipmbmsys.system.mapper.SysUserPostMapper;
import com.ipmbmsys.system.service.ISysPostService;

/**
 * 职务信息 服务层处理
 *
 * @author 贺祎阳
 */
@Service
public class SysPostServiceImpl implements ISysPostService
{
    @Autowired
    private SysPostMapper postMapper;

    @Autowired
    private SysUserPostMapper userPostMapper;

    @Autowired
    private SysPostDeptMapper postDeptMapper;
    /**
     * 查询职务信息集合
     *
     * @param post 职务信息
     * @return 职务信息集合
     */
    @Override
    public List<SysPost> selectPostList(SysPost post)
    {
        return postMapper.selectPostList(post);
    }

    /**
     * 查询所有职务
     *
     * @return 职务列表
     */
    @Override
    public List<SysPost> selectPostAll()
    {
        return postMapper.selectPostAll();
    }

    /**
     * 通过职务ID查询职务信息
     *
     * @param postId 职务ID
     * @return 角色对象信息
     */
    @Override
    public SysPost selectPostById(Long postId)
    {
        return postMapper.selectPostById(postId);
    }

    /**
     * 根据用户ID获取职务选择框列表
     *
     * @param userId 用户ID
     * @return 选中职务ID列表
     */
    @Override
    public List<Long> selectPostListByUserId(Long userId)
    {
        return postMapper.selectPostListByUserId(userId);
    }

    /**
     * 校验职务名称是否唯一
     *
     * @param post 职务信息
     * @return 结果
     */
    @Override
    public boolean checkPostNameUnique(SysPost post)
    {
        Long postId = StringUtils.isNull(post.getPostId()) ? -1L : post.getPostId();
        SysPost info = postMapper.checkPostNameUnique(post.getPostName());
        if (StringUtils.isNotNull(info) && info.getPostId().longValue() != postId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验职务描述是否唯一
     *
     * @param post 职务信息
     * @return 结果
     */
    @Override
    public boolean checkPostDescribesUnique(SysPost post)
    {
        Long postId = StringUtils.isNull(post.getPostId()) ? -1L : post.getPostId();
        SysPost info = postMapper.checkPostDescribesUnique(post.getPostDescribes());
        if (StringUtils.isNotNull(info) && info.getPostId().longValue() != postId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 通过职务ID查询职务使用数量
     *
     * @param postId 职务ID
     * @return 结果
     */
    @Override
    public int countUserPostById(Long postId)
    {
        return userPostMapper.countUserPostById(postId);
    }

    /**
     * 删除职务信息
     *
     * @param postId 职务ID
     * @return 结果
     */
    @Override
    public int deletePostById(Long postId)
    {
        postDeptMapper.deletePostDeptByPostId(postId);
        return postMapper.deletePostById(postId);
    }

    /**
     * 批量删除职务信息
     *
     * @param postIds 需要删除的职务ID
     * @return 结果
     */
    @Override
    public int deletePostByIds(Long[] postIds)
    {
        for (Long postId : postIds)
        {
            SysPost post = selectPostById(postId);
            if (countUserPostById(postId) > 0)
            {
                throw new ServiceException(String.format("%1$s已分配,不能删除", post.getPostName()));
            }
        }
        postDeptMapper.deletePostDept(postIds);
        return postMapper.deletePostByIds(postIds);
    }

    /**
     * 新增保存职务信息
     *
     * @param post 职务信息
     * @return 结果
     */
    @Override
    public int insertPost(SysPost post)
    {
        int rows = postMapper.insertPost(post);
        // 新增职务党组织关联
        insertPostDept(post);
        return rows;
    }

    /**
     * 修改保存职务信息
     *
     * @param post 职务信息
     * @return 结果
     */
    @Override
    public int updatePost(SysPost post)
    {
        Long postId = post.getPostId();
        postDeptMapper.deletePostDeptByPostId(postId);
        // 新增职务与党组织管理
        insertPostDept(post);
        return postMapper.updatePost(post);
    }

    /**
     * 新增党组织职务信息
     *
     * @param post 用户对象
     */
    public void insertPostDept(SysPost post)
    {
        Long[] depts = post.getDeptIds();
        if (StringUtils.isNotEmpty(depts))
        {
            // 新增职务与党组织管理
            List<SysPostDept> list = new ArrayList<SysPostDept>(depts.length);
            for (Long deptId : depts)
            {
                SysPostDept up = new SysPostDept();
                up.setPostId(post.getPostId());
                up.setDeptId(deptId);
                list.add(up);
            }
            postDeptMapper.batchPostDept(list);
        }
    }


    /**
     * 新增党组织职务信息
     *
     * @param postIds,deptIds
     */
    @Override
    public int bindPostsToDepts(Long[] postIds, Long[] deptIds) {
        if (postIds.length == 0 || deptIds.length == 0) {
            throw new ServiceException("请选择职务和党组织");
        }
        List<SysPostDept> list = new ArrayList<>();
        for (Long postId : postIds) {
            for (Long deptId : deptIds) {
                // 检查是否已存在关联
                SysPostDept exist = postDeptMapper.checkExist(postId, deptId);
                if (exist == null) {
                    SysPostDept pd = new SysPostDept();
                    pd.setPostId(postId);
                    pd.setDeptId(deptId);
                    list.add(pd);
                }
            }
        }
        if (!list.isEmpty()) {
            return postDeptMapper.batchPostDept(list);
        }
        return 0;
    }


    /**
     * 根据职务ID列表获取职务信息
     *
     * @param postIds 职务ID数组
     * @return 职务列表
     */
    @Override
    public List<SysPost> selectPostByIds(Long[] postIds)
    {
        if (postIds == null || postIds.length == 0) {
            return new ArrayList<>();
        }
        return postMapper.selectPostByIds(postIds);
    }
}
