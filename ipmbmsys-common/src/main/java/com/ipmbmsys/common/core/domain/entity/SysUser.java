package com.ipmbmsys.common.core.domain.entity;

import java.util.Date;
import java.util.List;
import javax.validation.constraints.*;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ipmbmsys.common.annotation.Excel;
import com.ipmbmsys.common.annotation.Excel.ColumnType;
import com.ipmbmsys.common.annotation.Excel.Type;
import com.ipmbmsys.common.annotation.Excels;
import com.ipmbmsys.common.core.domain.BaseEntity;
import com.ipmbmsys.common.xss.Xss;

/**
 * 用户对象 sys_user
 *
 * @author 贺祎阳
 */
public class SysUser extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private Long userId;

    /** 党组织ID */
    @Excel(name = "党组织编号", type = Type.IMPORT)
    private Long deptId;

    /** 党组织对象 */
    @Excels({
            @Excel(name = "党组织名称", targetAttr = "deptName", type = Type.EXPORT),
            @Excel(name = "党组织负责人", targetAttr = "leader", type = Type.EXPORT)
    })
    private SysDept dept;

    /** 用户类型（0教师身份 1学生身份） */
    @Excel(name = "党员身份", readConverterExp = "0=教师,1=学生")
    private String userType;

    /** 用户账号 */
    @Excel(name = "党员名称")
    private String userName;

    /** 用户昵称 */
    @Excel(name = "教研室/班级")
    private String nickName;

    /** 用户邮箱 */
    @Excel(name = "用户邮箱")
    private String email;

    /** 手机号码 */
    @Excel(name = "手机号码", cellType = ColumnType.TEXT)
    private String phonenumber;

    /** 用户性别 */
    @Excel(name = "用户性别", readConverterExp = "0=男,1=女")
    private String sex;

    // 新增字段
    @Excel(name = "学号/编号")
    private String partyMemberId;

    @Excel(name = "民族", readConverterExp =
            "0=汉族,1=壮族,2=满族,3=回族,4=苗族,5=维吾尔族,6=彝族,7=土家族,8=藏族,9=蒙古族," +
                    "10=傣族,11=瑶族,12=朝鲜族,13=白族,14=哈尼族,15=哈萨克族,16=黎族,17=傈僳族,18=佤族,19=畲族," +
                    "20=东乡族,21=纳西族,22=藏族,23=柯尔克孜族,24=土族,25=达斡尔族,26=仫佬族,27=羌族,28=布朗族," +
                    "29=撒拉族,30=毛南族,31=仡佬族,32=锡伯族,33=阿昌族,34=普米族,35=塔吉克族,36=怒族,37=乌孜别克族," +
                    "38=俄罗斯族,39=鄂温克族,40=德昂族,41=保安族,42=裕固族,43=京族,44=塔塔尔族,45=独龙族,46=鄂伦春族," +
                    "47=赫哲族,48=门巴族,49=珞巴族,50=基诺族,51=水族,52=高山族,53=侗族,54=拉祜族,55=景颇族")
    private String ethnicity;

    @Excel(name = "学历", readConverterExp = "1=小学,2=初中,3=高中,4=专科在读,5=专科,6=本科在读,7=本科,8=硕士研究生在读,9=硕士研究生,10=博士研究生在读,11=博士研究生")
    private String education;

    @Excel(name = "类别", readConverterExp = "0=积极分子,1=发展对象,2=预备党员,3=正式党员")
    private String category;
    @Excel(name = "出生日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date birthDate;

    @Excel(name = "申请入党日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date applicationDate;

    @Excel(name = "成为积极分子日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date activistDate;

    @Excel(name = "确定发展对象日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date developmentObjectDate;

    @Excel(name = "成为预备党员日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date probationaryMemberDate;

    @Excel(name = "转为正式党员日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date fullMemberDate;

    @Excel(name = "是否失联党员", readConverterExp = "0=是,1=否")
    private String isMissing;

    @Excel(name = "是否流动党员", readConverterExp = "0=是,1=否")
    private String isMobile;

    private String imagePath;

    /** 用户头像 */
    private String avatar;

    /** 密码 */
    private String password;

    /** 帐号状态（0正常 1停用） */
    @Excel(name = "党员账号状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    /** 最后登录IP */
    private String loginIp;

    /** 最后登录时间 */
    private Date loginDate;

    /** 角色对象 */
    private List<SysRole> roles;

    /** 角色组 */
    private Long[] roleIds;

    /** 职务组 */
    private Long[] postIds;

    /** 角色ID */
    private Long roleId;

    /** 职务列表 */
    private List<SysPost> posts;

    public List<SysPost> getPosts() {
        return posts;
    }

    public void setPosts(List<SysPost> posts) {
        this.posts = posts;
    }

    public SysUser() {
    }

    public SysUser(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public boolean isAdmin()
    {
        return isAdmin(this.userId);
    }

    public static boolean isAdmin(Long userId)
    {
        return userId != null && 1L == userId;
    }

    public Long getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    @Xss(message = "用户昵称不能包含脚本字符")
    @Size(min = 0, max = 30, message = "用户昵称长度不能超过30个字符")
    public String getNickName()
    {
        return nickName;
    }

    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }

    @Xss(message = "用户账号不能包含脚本字符")
    @NotBlank(message = "用户账号不能为空")
    @Size(min = 0, max = 30, message = "用户账号长度不能超过30个字符")
    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    @Email(message = "邮箱格式不正确")
    @Size(min = 0, max = 50, message = "邮箱长度不能超过50个字符")
    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    @Size(min = 0, max = 11, message = "手机号码长度不能超过11个字符")
    public String getPhonenumber()
    {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber)
    {
        this.phonenumber = phonenumber;
    }

    public String getSex()
    {
        return sex;
    }

    public void setSex(String sex)
    {
        this.sex = sex;
    }

    public String getAvatar()
    {
        return avatar;
    }

    public void setAvatar(String avatar)
    {
        this.avatar = avatar;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getLoginIp()
    {
        return loginIp;
    }

    public void setLoginIp(String loginIp)
    {
        this.loginIp = loginIp;
    }

    public Date getLoginDate()
    {
        return loginDate;
    }

    public void setLoginDate(Date loginDate)
    {
        this.loginDate = loginDate;
    }

    public SysDept getDept()
    {
        return dept;
    }

    public void setDept(SysDept dept)
    {
        this.dept = dept;
    }

    public List<SysRole> getRoles()
    {
        return roles;
    }

    public void setRoles(List<SysRole> roles)
    {
        this.roles = roles;
    }

    public Long[] getRoleIds()
    {
        return roleIds;
    }

    public void setRoleIds(Long[] roleIds)
    {
        this.roleIds = roleIds;
    }

    public Long[] getPostIds()
    {
        return postIds;
    }

    public void setPostIds(Long[] postIds)
    {
        this.postIds = postIds;
    }

    public Long getRoleId()
    {
        return roleId;
    }

    public void setRoleId(Long roleId)
    {
        this.roleId = roleId;
    }

    public String getPartyMemberId() {
        return partyMemberId;
    }

    public void setPartyMemberId(String partyMemberId) {
        this.partyMemberId = partyMemberId;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(Date applicationDate) {
        this.applicationDate = applicationDate;
    }

    public Date getActivistDate() {
        return activistDate;
    }

    public void setActivistDate(Date activistDate) {
        this.activistDate = activistDate;
    }

    public Date getDevelopmentObjectDate() {
        return developmentObjectDate;
    }

    public void setDevelopmentObjectDate(Date developmentObjectDate) {
        this.developmentObjectDate = developmentObjectDate;
    }

    public Date getProbationaryMemberDate() {
        return probationaryMemberDate;
    }

    public void setProbationaryMemberDate(Date probationaryMemberDate) {
        this.probationaryMemberDate = probationaryMemberDate;
    }

    public Date getFullMemberDate() {
        return fullMemberDate;
    }

    public void setFullMemberDate(Date fullMemberDate) {
        this.fullMemberDate = fullMemberDate;
    }

    public String getIsMissing() {
        return isMissing;
    }

    public void setIsMissing(String isMissing) {
        this.isMissing = isMissing;
    }

    public String getIsMobile() {
        return isMobile;
    }

    public void setIsMobile(String isMobile) {
        this.isMobile = isMobile;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("userId", getUserId())
                .append("deptId", getDeptId())
                .append("posts", getPosts())
                .append("userType", getUserType())
                .append("userName", getUserName())
                .append("nickName", getNickName())
                .append("email", getEmail())
                .append("phonenumber", getPhonenumber())
                .append("sex", getSex())
                .append("avatar", getAvatar())
                .append("password", getPassword())
                .append("status", getStatus())
                .append("delFlag", getDelFlag())
                .append("loginIp", getLoginIp())
                .append("loginDate", getLoginDate())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .append("dept", getDept())
                .append("partyMemberId", getPartyMemberId())
                .append("ethnicity", getEthnicity())
                .append("birthDate", getBirthDate())
                .append("education", getEducation())
                .append("category", getCategory())
                .append("applicationDate", getApplicationDate())
                .append("activistDate", getActivistDate())
                .append("developmentObjectDate", getDevelopmentObjectDate())
                .append("probationaryMemberDate", getProbationaryMemberDate())
                .append("fullMemberDate", getFullMemberDate())
                .append("isMissing", getIsMissing())
                .append("isMobile", getIsMobile())
                .append("imagePath", getImagePath())
                .append("roles", getRoles())
                .append("roleIds", getRoleIds())
                .append("postIds", getPostIds())
                .append("roleId", getRoleId())
                .toString();
    }
}
