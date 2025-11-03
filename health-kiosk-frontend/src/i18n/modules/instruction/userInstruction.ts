export const userInstructionMessages = {
  zh: {
    title: "体检系统用户操作指南",
    sections: {
      download: {
        title: "下载Android端软件",
        content: `
        用户可进入软件版本管理模块，选择最新的稳定软件版本，
        点击进行下载，如有其他特殊需要，则也可根据版本号辨别自身需要进行使用。
        `
      },
      viewInfo: {
        title: "查看个人信息",
        content: `
        用户可进入个人信息模块，
        点击查看个人信息，
        即可查看当前的个人信息。
        `
      },
      editInfo: {
        title: "编辑个人信息",
        content: `
        用户进入个人信息模块后，可对个人信息进行任意修改，
        若需要修改密码，请先绑定邮箱。
        输入邮箱后点击进行验证，之后进入邮箱中点击确认按钮，即可对邮箱进行确认。
        修改密码时，系统会向用户邮箱发送邮件进行验证，用户验证完毕后进行密码重置工作。
        `
      },
      checkData: {
        title: "查看体检数据",
        content: `
        用户进入个人信息模块中的查看体检数据，即可查看本人的体检信息。
        请确保您使用本款体检机软件进行了体检，否则无法看到体检数据。
        `
      }
    },
    faq: {
      q1: {
        question: "Q1: 如何辨别版本号",
        table: {
          name: "名称",
          meaning: "释义",
          note: "说明",
          release: { meaning: "发布版", note: "一般情况下如无特殊需要，可尽量选择该版本。" },
          debug: { meaning: "开发版", note: "如您需要记录日志等中间信息，可选择此版本。" },
          alpha: { meaning: "内部版本", note: "当前还在测试阶段，功能可以正常运行，但是开发者仍然在测试中。" },
          beta: { meaning: "公测版本", note: "该版本软件基本无问题，正在开放用户使用。" }
        }
      },
      q2: {
        question: "Q2: 我的布局显示有异常怎么办？",
        answer: "您可以尝试更换为Edge浏览器进行访问。"
      }
    }
  },

  en: {
    title: "User Operation Guide for Physical Examination System",
    sections: {
      download: {
        title: "Download the Android App",
        content: `
        Users can go to the software version management module,
        select the latest stable version, and click to download. 
        If there are special needs, 
        users can identify suitable versions by version numbers.
        `
      },
      viewInfo: {
        title: "View Personal Information",
        content: "Users can enter the personal information module and click to view their current personal details."
      },
      editInfo: {
        title: "Edit Personal Information",
        content: `
        After entering the personal information module, 
        users can freely modify their information. 
        To change the password, please bind your email first. 
        Enter the email and click verify, then confirm through the link sent to your mailbox. 
        When resetting your password, the system will send an email for verification.
        `
      },
      checkData: {
        title: "View Medical Data",
        content: `
        In the personal information module, users can view their own medical data. 
        Please ensure you have taken the physical examination using this system, 
        otherwise data may not be available.
        `
      }
    },
    faq: {
      q1: {
        question: "Q1: How to distinguish version types?",
        table: {
          name: "Name",
          meaning: "Meaning",
          note: "Description",
          release: { meaning: "Release Version", note: "Recommended for most users without special needs." },
          debug: { meaning: "Debug Version", note: "Select this version if you need logs or debug info." },
          alpha: { meaning: "Alpha Version", note: "Internal testing version, functional but under development." },
          beta: { meaning: "Beta Version", note: "Public testing version, mostly stable and available for use." }
        }
      },
      q2: {
        question: "Q2: What should I do if the layout looks abnormal?",
        answer: "Try accessing the system with the Edge browser."
      }
    }
  }
};
