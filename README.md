<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">智慧党员管理系统</h1>
<h4 align="center">一个基于RuoYi-Vue框架开发的智慧党员管理系统</h4>

## 平台简介

本项目是一套智慧党员管理系统，基于开源的[RuoYi-Vue](https://gitee.com/y_project/RuoYi-Vue)框架进行二次开发，旨在为党组织提供一套完整、高效的党员管理解决方案。

*   **后端**：采用Spring Boot、Spring Security、MyBatis、Redis & Jwt等技术，保证了系统的稳定性和安全性。
*   **前端**：采用Vue和Element UI，提供了美观、易用的用户界面。
*   **移动端**：提供了微信小程序版本，方便党员随时随地进行访问和操作。

## 主要功能

1.  **党员信息管理**：对党员的基本信息、党籍状态、奖惩记录等进行统一管理。
2.  **会议管理**：支持在线发布会议通知、记录会议纪要、管理参会人员等。
3.  **学习管理**：提供在线学习资料、记录学习进度、考核学习成果等功能。
4.  **组织管理**：对党组织架构进行维护，支持多层级组织结构。
5.  **系统监控**：提供服务监控、缓存监控、连接池监控等功能，确保系统稳定运行。
6.  **代码生成**：内置代码生成器，可以一键生成前后端代码，提高开发效率。

## 系统截图

<table>
    <tr>
        <td><img width="2560" height="1528" alt="image" src="https://github.com/user-attachments/assets/59937683-2200-4050-b04e-b5f121475f11" /></td>
        <td><img width="2560" height="1528" alt="image" src="https://github.com/user-attachments/assets/e45bf9ce-2948-4936-a274-8d69a7b20866" /></td>
        <td><img width="2560" height="1528" alt="image" src="https://github.com/user-attachments/assets/29185218-31f2-47f9-8c17-6e0e8900c3cf" /></td>
        <td><img width="2560" height="1528" alt="image" src="https://github.com/user-attachments/assets/c2fcded7-74f2-49d8-8592-809fc7faeded" /></td>
    </tr>
    <tr>
        <td><img width="2560" height="1528" alt="image" src="https://github.com/user-attachments/assets/5583b5eb-c988-4260-8f1b-ba18ebabd6fe" /></td>
        <td><img width="2560" height="1528" alt="image" src="https://github.com/user-attachments/assets/3fe2c6d2-816c-4c35-ada0-c076f934f74f" /></td>
        <td><img width="2560" height="1528" alt="image" src="https://github.com/user-attachments/assets/b37316b5-ad38-4369-a574-5c02f7edb83c" /></td>
        <td><img width="2560" height="1528" alt="image" src="https://github.com/user-attachments/assets/8d4a890c-440a-4928-afc5-758cf0a94bbe" /></td>
    </tr>
</table>

## 快速开始

1.  **后端启动**
    *   导入`ipmbmsys`项目到IDE中。
    *   创建数据库`ipmbmsys`并导入`sql/ry_20231130.sql`和`sql/quartz.sql`。
    *   修改`ipmbmsys-admin/src/main/resources/application-druid.yml`中的数据库连接信息。
    *   启动`ipmbmsys-admin`模块的`IpmbmsysApplication.java`。

2.  **前端启动**
    *   进入`ipmbmsys-ui`目录。
    *   运行`npm install`安装依赖。
    *   运行`npm run dev`启动服务。

3.  **小程序启动**
    *   使用HBuilderX导入`RuoYi-App-master`项目。
    *   修改`manifest.json`中的小程序AppID。
    *   运行到微信开发者工具进行预览。

## 技术交流

如果您在项目使用中遇到问题，或有任何建议，欢迎随时提出。
