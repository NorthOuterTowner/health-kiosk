<template>
  <div class="layout">
    <Sidebar />
  <div class="self-page">
    <h2>{{$t('selfinfo.header')}}</h2>
    <div style="display: flex;">
    <n-card :title= "$t('selfinfo.form.title')" class="settings-card">
      <n-form
        :model="form"
        :rules="rules"
        ref="formRef"
        label-placement="left"
        label-width="100"
      >
        <!-- 用户名 -->
        <n-form-item :label="$t('selfinfo.form.username.label')" path="username">
          <n-input v-model:value="form.username" :placeholder="$t('selfinfo.form.username.placeholder')" />
        </n-form-item>

        <!-- 密码 -->
        <n-form-item :label="$t('selfinfo.form.password.label')" path="password">
          <n-input
            v-model:value="form.password"
            :placeholder="$t('selfinfo.form.password.placeholder')"
            type="password"
            show-password-on="click"
          />
          <n-button @click="reset_pwd">{{ $t('selfinfo.form.password.buttonText') }}</n-button>
        </n-form-item>

        <!-- 邮箱 -->
        <n-form-item :label="$t('selfinfo.form.email.label')" path="email">
          <n-input v-model:value="form.email" :placeholder="$t('selfinfo.form.email.placeholder')" />
          <n-button @click="reset_email">{{ $t('selfinfo.form.email.buttonText') }}</n-button>
        </n-form-item>

        <!-- 性别 -->
        <n-form-item :label="$t('selfinfo.form.gender.label')" path="gender">
          <n-select
            v-model:value="form.gender"
            :options="genderOptions"
            :placeholder="$t('selfinfo.form.gender.placeholder')"
          />
        </n-form-item>

        <!-- 年龄 -->
        <n-form-item :label="$t('selfinfo.form.age.label')" path="age">
          <n-input-number
            v-model:value="form.age"
            :min="0"
            :max="150"
            :placeholder="$t('selfinfo.form.age.placeholder')"
          />
        </n-form-item>

        <n-form-item>
          <n-button type="warning" @click="reset_form" style="margin: 10px;">{{ $t('selfinfo.form.buttonGroup.reset') }}</n-button>
          <n-button type="primary" @click="change_info" style="margin-right: 10px;">{{ $t('selfinfo.form.buttonGroup.save') }}</n-button>
          <n-button type="error" @click="log_out" style="margin-right: 10px;">{{ $t('selfinfo.form.buttonGroup.logout')}}</n-button> 
        </n-form-item>
      </n-form>
    </n-card>

    <!-- System Settings -->
    <n-card :title="$t('selfinfo.systemForm.title')" class="settings-card">
      <n-form
        :model="form"
        :rules="rules"
        ref="systemFormRef"
        label-placement="left"
        label-width="100"
      >
        <!-- lang -->
        <n-form-item :label="$t('selfinfo.systemForm.language.label')" path="language">
          <n-select
            v-model:value="locale"
            :options="languageOptions"
            :placeholder="$t('selfinfo.systemForm.language.placeholder')"
          />
        </n-form-item>

        <!-- time -->
        <n-form-item :label="$t('selfinfo.systemForm.system_time.label')" path="time">
          <n-time-picker
            v-model:value="form.time"
            format="HH:mm:ss"
            value-format="timestamp"
            type = "datetime"
            :placeholder="$t('selfinfo.systemForm.system_time.placeholder')"
          />
        </n-form-item>

        <!-- picture -->
        <n-form-item :label="$t('selfinfo.systemForm.upload_picture.label')">
          <n-upload
            :custom-request="handleUpload" 
            list-type="image-card"               
            :max="1"
          >
            {{ $t('selfinfo.systemForm.upload_picture.button_text') }}
          </n-upload>
        </n-form-item>

        <!-- 生日 -->
        <n-form-item :label="$t('selfinfo.systemForm.birthday.label')" path="birthday">
          <n-date-picker
            v-model:value="form.birthday"
            type="date"
            :placeholder="$t('selfinfo.systemForm.birthday.placeholder')"
          />
        </n-form-item>
      </n-form>
    </n-card>

    </div>
  </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import {
  NForm,
  NFormItem,
  NInput,
  NInputNumber,
  NSelect,
  NDatePicker,
  NButton,
  NCard,
  useMessage,
  NUpload,
  NTimePicker
} from "naive-ui";

import type { FormInst, FormRules, UploadFileInfo } from "naive-ui";
import Sidebar from "../components/Sidebar.vue";
import { changeInfoApi, resetPasswordApi, setEmailApi, uploadPictureApi, getInfoApi } from "../../api/user/user";
import { useI18n } from 'vue-i18n'

const { t, locale } = useI18n();

const languageOptions = [
  { label: "中文", value: 'zh' },
  { label: "English", value: 'en' }
]

const initialForm = {
  username: "" as string | null,
  password: "" as string,
  email: "" as string,
  gender: "" as string | null,
  age: null as number | null,
  birthday: null as number | null,
  time: Date.now() as number
};

//const res = await getInfoApi();

const form = ref({...initialForm})

onMounted(async () => {
  const res = await getInfoApi();
  const row = res?.data?.rows[0];
  form.value.username = row.name;
  form.value.email = row.email;
  form.value.gender = row.gender;
  form.value.age = row.age;
})

// gender options
const genderOptions = [
  { label: t("utils.gender.male"), value: "男" },
  { label: t("utils.gender.female"), value: "女" }
];

// form verify rules
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
  console.log(form.value.password)
  const res = resetPasswordApi(form.value.password,null);
  console.log(res)
  message.info("验证邮件已发送至您的邮箱");
}

const reset_email = () => {
  setEmailApi(form.value.email);
  message.info("验证邮件已发送至您的邮箱");
}

const change_info = async () => {
  const res: any = await changeInfoApi(form.value);
  if(res.data.code == 200){
    message.info("信息修改成功");
  }else{
    message.error(res.data.msg);
  }
}

const log_out = async () => {
  router.push("/");
  localStorage.setItem("token","");
  localStorage.setItem("role", "0");
}

const reset_form = () => {
  form.value = initialForm;
  message.info("表单已重置")
}

import type { UploadCustomRequestOptions } from "naive-ui";
import router from "../../router";

const handleUpload = async (options: UploadCustomRequestOptions) => {
  if (options.file && options.file.file) {
    const res = await uploadPictureApi(options.file.file);
    if(res?.data.code === 200){
      message.info("上传成功")
    }else{
      message.error("上传失败")
    }
  }
}

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
