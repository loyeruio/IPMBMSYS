package com.ipmbmsys.web.controller.system;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.ipmbmsys.common.core.domain.entity.SysPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ipmbmsys.common.annotation.Log;
import com.ipmbmsys.common.core.controller.BaseController;
import com.ipmbmsys.common.core.domain.AjaxResult;
import com.ipmbmsys.common.core.page.TableDataInfo;
import com.ipmbmsys.common.enums.BusinessType;
import com.ipmbmsys.common.utils.poi.ExcelUtil;
import com.ipmbmsys.system.service.ISysPostService;

/**
 * 职务信息操作处理
 *
 * @author 贺祎阳
 */
@RestController
@RequestMapping("/system/post")
public class SysPostController extends BaseController
{
    @Autowired
    private ISysPostService postService;

    /**
     * 获取职务列表
     */
    @PreAuthorize("@ss.hasPermi('system:post:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysPost post)
    {
        startPage();
        List<SysPost> list = postService.selectPostList(post);
        return getDataTable(list);
    }

    @Log(title = "职务管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:post:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysPost post)
    {
        List<SysPost> list = postService.selectPostList(post);
        ExcelUtil<SysPost> util = new ExcelUtil<SysPost>(SysPost.class);
        util.exportExcel(response, list, "职务数据");
    }

    /**
     * 根据职务编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:post:query')")
    @GetMapping(value = "/{postId}")
    public AjaxResult getInfo(@PathVariable Long postId)
    {
        return success(postService.selectPostById(postId));
    }

    /**
     * 新增职务
     */
    @PreAuthorize("@ss.hasPermi('system:post:add')")
    @Log(title = "职务管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysPost post)
    {
        if (!postService.checkPostNameUnique(post))
        {
            return error("新增职务'" + post.getPostName() + "'失败，职务名称已存在");
        }
        else if (!postService.checkPostDescribesUnique(post))
        {
            return error("新增职务'" + post.getPostName() + "'失败，职务编码已存在");
        }
        post.setCreateBy(getUsername());
        return toAjax(postService.insertPost(post));
    }

    /**
     * 修改职务
     */
    @PreAuthorize("@ss.hasPermi('system:post:edit')")
    @Log(title = "职务管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysPost post)
    {
        if (!postService.checkPostNameUnique(post))
        {
            return error("修改职务'" + post.getPostName() + "'失败，职务名称已存在");
        }
        else if (!postService.checkPostDescribesUnique(post))
        {
            return error("修改职务'" + post.getPostName() + "'失败，职务编码已存在");
        }
        post.setUpdateBy(getUsername());
        return toAjax(postService.updatePost(post));
    }
    /**
     * 批量添加组织职务
     */
    @PreAuthorize("@ss.hasPermi('system:post:edit')")
    @Log(title = "职务管理", businessType = BusinessType.UPDATE)
    @PostMapping("/bindDepts")
    public AjaxResult bindDepts(@RequestBody Map<String, Object> params) {
        // 安全转换postIds为Long数组
        List<?> postIdList = (List<?>) params.get("postIds");
        Long[] postIds = postIdList.stream()
                .map(id -> Long.parseLong(id.toString()))
                .toArray(Long[]::new);
        // 安全转换deptIds为Long数组
        List<?> deptIdList = (List<?>) params.get("deptIds");
        Long[] deptIds = deptIdList.stream()
                .map(id -> Long.parseLong(id.toString()))
                .toArray(Long[]::new);
        return toAjax(postService.bindPostsToDepts(postIds, deptIds));
    }
    /**
     * 删除职务
     */
    @PreAuthorize("@ss.hasPermi('system:post:remove')")
    @Log(title = "职务管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{postIds}")
    public AjaxResult remove(@PathVariable Long[] postIds)
    {
        return toAjax(postService.deletePostByIds(postIds));
    }

    /**
     * 获取职务选择框列表
     */
    @GetMapping("/optionselect")
    public AjaxResult optionselect()
    {
        List<SysPost> posts = postService.selectPostAll();
        return success(posts);
    }
}
