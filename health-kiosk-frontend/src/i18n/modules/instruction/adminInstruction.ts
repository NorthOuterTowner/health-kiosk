export const adminInstructionMessages = {
  zh: {
    title: "体检系统管理员操作指南",
    sections: {
      userManage: {
        title: "用户管理",
        content: `
        您可以进入用户管理模块进行系统用户的全生命周期管理，包括：
        添加新用户、为用户分配角色和权限、修改用户信息、重置密码、禁用或删除用户账号等操作。
        管理员可通过搜索和筛选功能快速定位特定用户。
        系统还提供用户统计分析功能：
        - 查看用户角色占比（如普通用户、管理员等）；
        - 查看最近一周的用户注册趋势；
        管理员应定期检查用户权限配置，确保数据访问安全与最小化授权原则的落实。
        `
      },
      softwareManage: {
        title: "软件设备管理",
        content: `
        在软件设备管理模块，您可以查看、添加、删除和更新系统中的软件版本。
        系统支持为不同终端或设备分配对应的软件包，方便版本控制与兼容性维护。
        若需更新软件而不改变版本号，可使用“更新软件”功能发布补丁包。
        同时，模块提供软件版本的状态标识（如“release”、“debug”、“alpha”、“beta”）。
        管理员还可上传版本说明，以便记录更新日志与变更信息。
        关于版本命名规则和发布策略，请参考 Q1 部分的版本表。
        `
      },
      dataTrend: {
        title: "体检数据趋势查询",
        content: `
        在体检数据趋势模块，您可以基于时间段、性别、年龄段、部门或体检项目等条件
        对历史体检数据进行统计与可视化分析。
        模块支持生成折线图、柱状图等多种图表，帮助识别健康趋势与潜在风险。
        管理员可导出分析结果用于科研或报告撰写。
        若系统部署了数据权限控制机制，请确保分析结果遵守隐私与数据安全规范。
        `
      },
      projectManage: {
        title: "体检项目管理",
        content: `
        在体检项目管理模块中，您可以查看、添加、修改或停用各类体检项目。
        当某项目暂时不可用时，可将其状态设置为“停用”，待修复后重新启用。
        您也可以为每个项目上传相关的操作说明、检查流程或注意事项文档，
        帮助操作人员快速了解该项目的使用方式。
        新增项目时建议遵循统一的命名规范，并确保录入的信息准确完整。
        该模块还提供项目分组与排序功能，便于不同部门按需查看。
        `
      }
    }
  },

  en: {
    title: "Administrator Operation Guide for Physical Examination System",
    sections: {
      userManage: {
        title: "User Management",
        content: `
        In the User Management module, administrators can perform full lifecycle management of system users, 
        including adding new users, assigning roles and permissions, editing user profiles, resetting passwords, 
        disabling or deleting accounts.  
        Advanced filtering and search tools are available to locate users quickly.  
        The module also provides user statistics and analytics:
        - View role distribution (e.g., regular users, administrators);  
        - View weekly user registration trends;   
        It is recommended that administrators regularly review role assignments to ensure compliance 
        with security and minimum-privilege principles.
        `
      },
      softwareManage: {
        title: "Software and Device Management",
        content: `
        In this module, administrators can view, add, remove, and update software versions.  
        The system allows assigning specific software builds to different terminals or devices 
        for version control and compatibility maintenance.  
        If you need to update the software without changing its version number, 
        use the “Internal Update” feature to release a patch package.  
        Each version has a status label (e.g., “Release”, “Debug”, “Alpha”,“Beta”).  
        You can also upload version notes and change logs for better traceability.  
        For version naming conventions and publishing guidelines, refer to the Q1 version table below.
        `
      },
      dataTrend: {
        title: "Medical Data Trend Analysis",
        content: `
        In the Medical Data Trend module, you can analyze historical examination data 
        based on multiple filters such as time range, gender, age group, department, or examination item.  
        The system provides visual reports such as line and bar charts 
        to help identify long-term health trends or potential risks.  
        Administrators can export reports for research or documentation purposes.  
        Make sure to follow data privacy and access control policies when handling sensitive data.
        `
      },
      projectManage: {
        title: "Medical Project Management",
        content: `
        In the Project Management module, administrators can view, add, edit, or deactivate 
        physical examination items.  
        When a project is under maintenance, set its status to “Disabled” until the issue is resolved.  
        You can upload related documentation such as operation guides or safety instructions 
        to assist staff in using the feature properly.  
        When adding new items, follow consistent naming rules and ensure all details 
        (e.g., project code, fee, applicable group) are accurately filled in.  
        The module also supports project grouping and ordering for better organization across departments.
        `
      }
    }
  }
};
