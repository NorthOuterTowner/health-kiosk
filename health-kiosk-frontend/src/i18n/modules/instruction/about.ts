export const aboutMessages = {
  zh: {
      title: "关于本项目",
      basic: {
        title: "基本说明",
        desc:
          `本项目是铁路司机用健康检测系统的后台管理系统。
          项目基于体检设备进行设计，采用 B/S 架构便于进行访问和使用。
          项目为用户提供了Android应用（即体检软件）的不同版本，可供用户进行下载和使用。`
          ,
      },
      arch: {
        title: "系统架构",
        desc:
          `本项目采用前后端分离架构：
          前端使用 <strong>Vue 3 + TypeScript + Naive-UI + Lucide</strong>，
          后端基于<strong> Node.js + Express + MySQL + Redis</strong> ，
          人脸识别部分使用 <strong> Python + dlib </strong>。
          前后端通过<strong> RESTful API </strong>进行数据交互，
          并使用<strong> JWT </strong>进行用户认证。`
          ,
      },
      features: {
        title: "项目特色",
        item1: "支持多用户角色登录与权限控制。",
        item2: "采用响应式布局设计，兼容桌面端与移动端访问。",
        item3: "体检数据支持可视化分析，图表动态展示健康趋势。（待完成）",
        item4: "支持远程更新安卓端体检软件。",
        item5: "对DDOS攻击和SQL注入攻击进行了防护。",
        item6: "支持中英双语进行访问。",
      },
      function: {
        title: "功能分布",
        admin: {
          title: "管理员端",
          item1: "用户管理",
          item2: "发布、下载、更新安卓端软件",
          item3: "管理体检数据",
          item4: "查看体检数据分析",
          item5: "修改、查看个人信息",
          item6: "管理体检项目",
          item7: "提供使用说明",
        },
        user: {
          title: "基本用户端",
          item1: "下载安卓端软件",
          item2: "查询个人体检数据",
          item3: "修改、查看个人信息",
          item4: "查看使用说明",
        },
      },
      contact: {
        title: "联系我们",
        repo: "项目地址：",
        dev: "开发者：",
      },
  },

  en: {
      title: "About This Project",
      basic: {
        title: "Basic Description",
        desc:
          `This project is a backend management system 
          for the health examination system used by railway drivers. 
          It is designed based on medical examination devices 
          and adopts a B/S architecture for convenient access and usage. 
          The project provides different versions of an Android application (the examination software) 
          for users to download and use.`
          ,
      },
      arch: {
        title: "System Architecture",
        desc:
          `This project adopts a front-end and back-end separation architecture: 
          the front end uses <strong>Vue 3 + TypeScript + Naive-UI + Lucide</strong>, 
          while the back end is built on <strong>Node.js + Express + MySQL + Redis</strong>.
          The facial recognition module is implemented using <strong>Python + dlib</strong>. 
          The front end and back end communicate through <strong>RESTful API</strong> 
          and use <strong>JWT</strong> for user authentication.`
          ,
      },
      features: {
        title: "Project Features",
        item1: "Supports multi-role login and permission control.",
        item2: "Responsive design compatible with desktop and mobile devices.",
        item3: "Supports visualized analysis of examination data with dynamic charts (in progress).",
        item4: "Supports remote updates of the Android examination software.",
        item5: "Provides protection against DDOS and SQL injection attacks.",
        item6: "Supports bilingual access (Chinese and English).",
      },
      function: {
        title: "Feature Distribution",
        admin: {
          title: "Administrator Side",
          item1: "User Management",
          item2: "Publish, download, and update Android software",
          item3: "Manage examination data",
          item4: "View data analysis",
          item5: "Edit and view personal information",
          item6: "Manage examination items",
          item7: "Provide user instructions",
        },
        user: {
          title: "Basic User Side",
          item1: "Download Android software",
          item2: "View personal examination data",
          item3: "Edit and view personal information",
          item4: "Read user instructions",
        },
      },
      contact: {
        title: "Contact Us",
        repo: "Project Repository: ",
        dev: "Developer: ",
      },
  },
}
