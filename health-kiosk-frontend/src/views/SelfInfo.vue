<template>
  <div class="layout">
    <Sidebar />
  <div class="self-page">
    <h2>个人信息设置</h2>
    <div style="display: flex;">
    <n-card title="修改个人信息" class="settings-card">
      <n-form
        :model="form"
        :rules="rules"
        ref="formRef"
        label-placement="left"
        label-width="80"
      >
        <!-- 用户名 -->
        <n-form-item label="用户名" path="username">
          <n-input v-model:value="form.username" placeholder="请输入用户名" />
        </n-form-item>

        <!-- 密码 -->
        <n-form-item label="密码" path="password">
          <n-input
            v-model:value="form.password"
            placeholder="请输入密码"
            type="password"
            show-password-on="click"
          />
          <n-button @click="reset_pwd">修改密码</n-button>
        </n-form-item>

        <!-- 邮箱 -->
        <n-form-item label="邮箱" path="email">
          <n-input v-model:value="form.email" placeholder="请输入邮箱" />
          <n-button @click="reset_email">验证邮箱</n-button>
        </n-form-item>

        <!-- 性别 -->
        <n-form-item label="性别" path="gender">
          <n-select
            v-model:value="form.gender"
            :options="genderOptions"
            placeholder="请选择性别"
          />
        </n-form-item>

        <!-- 年龄 -->
        <n-form-item label="年龄" path="age">
          <n-input-number
            v-model:value="form.age"
            :min="0"
            :max="150"
            placeholder="请输入年龄"
          />
        </n-form-item>

        <n-form-item>
          <n-button type="primary" style="margin: 10px;">重置</n-button>
          <n-button type="error" @click="change_info">保存信息</n-button>
        </n-form-item>
      </n-form>
    </n-card>
    <!-- Just n-card's copy-->
     <n-card title="修改系统设置" class="settings-card">
      <n-form
        :model="form"
        :rules="rules"
        ref="formRef"
        label-placement="left"
        label-width="80"
      >
        <!-- lang -->
        <n-form-item label="语言" path="username">
          <n-input v-model:value="form.username" placeholder="请输入用户名" />
        </n-form-item>

        <!-- time -->
        <n-form-item label="系统时间" path="password">
          <n-input
            v-model:value="form.password"
            placeholder="请输入密码"
            type="password"
            show-password-on="click"
          />
        </n-form-item>

        <!-- 邮箱 -->
        <n-form-item label="上传图片" path="avatorList">
          <n-upload
            action="http://localhost:3000/upload"  
            list-type="image-card"               
            :max="1"                           
            v-model:file-list="form.avatarList"
          >
            点击上传
          </n-upload>
        </n-form-item>

        <!-- 生日 -->
        <n-form-item label="生日" path="birthday">
          <n-date-picker
            v-model:value="form.birthday"
            type="date"
            placeholder="请选择生日"
          />
        </n-form-item>

      </n-form>
    </n-card>
    </div>
  </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import {
  NForm,
  NFormItem,
  NInput,
  NInputNumber,
  NSelect,
  NDatePicker,
  NButton,
  NCard,
  NSpace,
  useMessage,
  NUpload
} from "naive-ui";

import type { FormInst, FormRules, UploadFileInfo } from "naive-ui";
import Sidebar from "../components/Sidebar.vue";
import { changeInfoApi, resetPasswordApi, setEmailApi } from "../api/user";

// 表单数据
const form = ref({
  username: "" as string | null,
  password: "" as string,
  email: "" as string,
  gender: null as string | null,
  age: null as number | null,
  birthday: null as number | null,
  avatarList: [] as UploadFileInfo[]
});

// 性别选项
const genderOptions = [
  { label: "男", value: "男" },
  { label: "女", value: "女" }
];

// 表单验证规则
const rules: FormRules = {
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
  email: [
    { required: true, message: "请输入邮箱", trigger: "blur" },
    { type: "email", message: "邮箱格式不正确", trigger: "blur" }
  ],
  gender: [{ required: true, message: "请选择性别", trigger: "change" }],
  age: [{ type: "number", required: true, message: "请输入年龄" }],
  birthday: [{ required: true, message: "请选择生日", trigger: "change" }]
};

const formRef = ref<FormInst | null>(null);
const message = useMessage();

const reset_pwd = () => {
  resetPasswordApi(form.value.password);
}

const reset_email = () => {
  setEmailApi(form.value.email);
}

const change_info = () => {
  const res: any = changeInfoApi(form.value);

    console.log(res)
  
}

// 提交
const submitForm = () => {
  formRef.value?.validate((errors) => {
    if (!errors) {
      // 模拟提交 API
      console.log("提交数据:", form.value);
      message.success("保存成功！");
    } else {
      message.error("请填写完整信息！");
    }
  });
};

// 重置
const resetForm = () => {
  form.value = {
    username: "",
    password: "",
    email: "",
    gender: null,
    age: null,
    birthday: null,
    avatarList: []
  };
};
</script>

<style scoped>
.layout {
  display: flex;
  gap: 20px;
}

.self-page {
  margin-left: 16rem;
  flex: 1;
  padding-left: 1em;
  padding-right: 60px;
}
.settings-container {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}
.settings-card {
  width: 550px;
  border-radius: 16px;
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.1);
  margin: 20px;
}
</style>
