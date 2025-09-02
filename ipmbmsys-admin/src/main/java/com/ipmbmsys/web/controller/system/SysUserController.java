package com.ipmbmsys.web.controller.system;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import com.ipmbmsys.common.core.domain.entity.SysPost;
import org.apache.commons.lang3.ArrayUtils;
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
import org.springframework.web.multipart.MultipartFile;
import com.ipmbmsys.common.annotation.Log;
import com.ipmbmsys.common.core.controller.BaseController;
import com.ipmbmsys.common.core.domain.AjaxResult;
import com.ipmbmsys.common.core.domain.entity.SysDept;
import com.ipmbmsys.common.core.domain.entity.SysRole;
import com.ipmbmsys.common.core.domain.entity.SysUser;
import com.ipmbmsys.common.core.page.TableDataInfo;
import com.ipmbmsys.common.enums.BusinessType;
import com.ipmbmsys.common.utils.SecurityUtils;
import com.ipmbmsys.common.utils.StringUtils;
import com.ipmbmsys.common.utils.poi.ExcelUtil;
import com.ipmbmsys.system.service.ISysDeptService;
import com.ipmbmsys.system.service.ISysPostService;
import com.ipmbmsys.system.service.ISysRoleService;
import com.ipmbmsys.system.service.ISysUserService;

/**
 * 用户信息
 *
 * @author 贺祎阳
 */
@RestController
@RequestMapping("/system/user")
public class SysUserController extends BaseController
{
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private ISysPostService postService;

     /**
     * 获取用户列表（优化版本，包含角色职务信息）
     */
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/listOptimized")
    public TableDataInfo listOptimized(SysUser user)
    {
        startPage();
        List<Map<String, Object>> list = userService.selectUserListWithRolesAndPosts(user);
        return getDataTable(list);
    }

    /**
     * 获取用户列表
     */
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysUser user) {
        startPage();
        List<SysUser> list = userService.selectUserList(user);
        return getDataTable(list);
    }

    /**
     * 获取所有用户列表（不分页），用于统计
     */
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/listAll")
    public AjaxResult listAll(SysUser user)
    {
        List<SysUser> list = userService.selectUserList(user);
        return AjaxResult.success(list);
    }

    /**
     * 导出用户列表
     */
    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:user:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysUser user)
    {
        List<SysUser> list = userService.selectUserList(user);
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        util.exportExcel(response, list, "用户数据");
    }

    /**
     * 导入用户数据
     */
    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('system:user:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        List<SysUser> userList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = userService.importUser(userList, updateSupport, operName);
        return success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        util.importTemplateExcel(response, "用户数据");
    }

    /**
     * 根据用户编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping(value = { "/", "/{userId}" })
    public AjaxResult getInfo(@PathVariable(value = "userId", required = false) Long userId)
    {
        userService.checkUserDataScope(userId);
        AjaxResult ajax = AjaxResult.success();
        List<SysRole> roles = roleService.selectRoleAll();
        ajax.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        ajax.put("posts", postService.selectPostAll());
        if (StringUtils.isNotNull(userId))
        {
            SysUser sysUser = userService.selectUserById(userId);
            ajax.put(AjaxResult.DATA_TAG, sysUser);
            ajax.put("postIds", postService.selectPostListByUserId(userId));
            ajax.put("roleIds", sysUser.getRoles().stream().map(SysRole::getRoleId).collect(Collectors.toList()));
        }
        return ajax;
    }

    /**
     * 新增用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysUser user)
    {
        deptService.checkDeptDataScope(user.getDeptId());
        roleService.checkRoleDataScope(user.getRoleIds());
        if (!userService.checkUserNameUnique(user))
        {
            return error("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user))
        {
            return error("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user))
        {
            return error("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        // 添加日期校验
        if (!validateDates(user)) {
            return error("日期校验失败");
        }
        user.setCreateBy(getUsername());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        return toAjax(userService.insertUser(user));
    }

    /**
     * 修改用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysUser user)
    {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        deptService.checkDeptDataScope(user.getDeptId());
        roleService.checkRoleDataScope(user.getRoleIds());
        if (!userService.checkUserNameUnique(user))
        {
            return error("修改用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user))
        {
            return error("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user))
        {
            return error("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        // 添加日期校验
        if (!validateDates(user)) {
            return error("日期校验失败");
        }
        user.setUpdateBy(getUsername());
        return toAjax(userService.updateUser(user));
    }

    /**
     * 删除用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds)
    {
        if (ArrayUtils.contains(userIds, getUserId()))
        {
            return error("当前用户不能删除");
        }
        return toAjax(userService.deleteUserByIds(userIds));
    }

    /**
     * 重置密码
     */
    @PreAuthorize("@ss.hasPermi('system:user:resetPwd')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public AjaxResult resetPwd(@RequestBody SysUser user)
    {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(getUsername());
        return toAjax(userService.resetPwd(user));
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysUser user)
    {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        user.setUpdateBy(getUsername());
        return toAjax(userService.updateUserStatus(user));
    }

    /**
     * 根据用户编号获取授权角色
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping("/authRole/{userId}")
    public AjaxResult authRole(@PathVariable("userId") Long userId)
    {
        AjaxResult ajax = AjaxResult.success();
        SysUser user = userService.selectUserById(userId);
        List<SysRole> roles = roleService.selectRolesByUserId(userId);
        ajax.put("user", user);
        ajax.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        return ajax;
    }

    /**
     * 用户授权角色
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.GRANT)
    @PutMapping("/authRole")
    public AjaxResult insertAuthRole(Long userId, Long[] roleIds)
    {
        userService.checkUserDataScope(userId);
        roleService.checkRoleDataScope(roleIds);
        userService.insertUserAuth(userId, roleIds);
        return success();
    }

    /**
     * 获取党组织树列表
     */
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/deptTree")
    public AjaxResult deptTree(SysDept dept)
    {
        return success(deptService.selectDeptTreeList(dept));
    }

    private boolean validateDates(SysUser user) {
        // 将 Date 转换为 LocalDate
        LocalDate birthDate = toLocalDate(user.getBirthDate());
        LocalDate applicationDate = toLocalDate(user.getApplicationDate());
        LocalDate activistDate = toLocalDate(user.getActivistDate());
        LocalDate developmentObjectDate = toLocalDate(user.getDevelopmentObjectDate());
        LocalDate probationaryMemberDate = toLocalDate(user.getProbationaryMemberDate());
        LocalDate fullMemberDate = toLocalDate(user.getFullMemberDate());

        // 1. 检查日期顺序
        if (!isDateSequenceValid(birthDate, applicationDate, activistDate, developmentObjectDate, probationaryMemberDate, fullMemberDate)) {
            return false;
        }

        // 2. 检查年龄是否满18岁
        if (birthDate != null && applicationDate != null) {
            Period age = Period.between(birthDate, applicationDate);
            if (age.getYears() < 18) {
                return false;
            }
        }

        // 3. 检查积极分子到发展对象的间隔（至少1年）
        if (activistDate != null && developmentObjectDate != null) {
            Period interval = Period.between(activistDate, developmentObjectDate);
            if (interval.getYears() < 1) {
                return false;
            }
        }

        // 4. 检查预备党员到正式党员的间隔（至少1年）
        if (probationaryMemberDate != null && fullMemberDate != null) {
            Period interval = Period.between(probationaryMemberDate, fullMemberDate);
            if (interval.getYears() < 1) {
                return false;
            }
        }

        return true;
    }

    /**
     * 检查日期顺序是否有效
     */
    private boolean isDateSequenceValid(LocalDate... dates) {
        for (int i = 1; i < dates.length; i++) {
            if (dates[i - 1] != null && dates[i] != null && dates[i].isBefore(dates[i - 1])) {
                return false;
            }
        }
        return true;
    }

    /**
     * 将 java.util.Date 转换为 java.time.LocalDate
     */
    private LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * 根据用户ID获取角色列表
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping("/roles/{userId}")
    public AjaxResult getRolesByUserId(@PathVariable("userId") Long userId)
    {
        List<SysRole> roles = roleService.selectRolesByUserId(userId);
        return success(roles);
    }

    /**
     * 根据用户ID获取职务列表
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping("/posts/{userId}")
    public AjaxResult getPostsByUserId(@PathVariable("userId") Long userId)
    {
        List<Long> postIds = postService.selectPostListByUserId(userId);
        // 根据ID获取完整的职务信息
        List<SysPost> posts = postService.selectPostByIds(postIds.toArray(new Long[0]));
        return success(posts);
    }

   
}
