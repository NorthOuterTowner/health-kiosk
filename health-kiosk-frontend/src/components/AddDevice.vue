<template>
  <div class="overlay-child">
    <div class="user-info">
      <h3>添加新版本软件</h3>
      <label>版本:     <input v-model="localApp.version" :disabled="!props.editable" /> </label>
      <label>选择软件类型: </label>
      <n-select
        v-model:value="localApp.type"
        :options="typeOptions"
        placeholder="选择软件类型"
        style="margin-bottom: 16px; width: 200px;"
      />
      <label>描述:     <input v-model="localApp.description" :disabled="!props.editable"></input></label>
      <label>上传文件: <input type="file" @change="handleFileChange" /></label>
      <div class="buttons">
        <button @click="save">添加</button>
        <button @click="$emit('close')">取消</button>
      </div>
    </div>
  </div>
</template>

<script setup lang ="ts">
import { reactive, toRefs, watch, ref, version } from "vue";
import { addDeviceApi, updateDeviceApi } from "../api/device";
import { pProps, useMessage } from "naive-ui";

const message = useMessage();

const typeOptions = [
    {label: "release", value: '1'},
    {label: "debug", value: '2'},
]

const props = defineProps({
  device: Object,
  editable: Boolean
});

console.log(props.device)

const emit = defineEmits(["close", "update"]);

const handleFileChange = (event: Event) => {
    const target = event.target as HTMLInputElement;
    if (target.files && target.files.length > 0){
        localApp.apk = target.files[0];
    }
};

const localApp = reactive({
    version: props.device?.version ?? "" as string,
    type: props.device?.type ?? "1" as string,
    description: props.device?.description ?? "" as string | null,
    apk: null as File | null
});

const save = async () => {
  try {
    if(localApp.apk == null){
        message.error("请上传APK");
        return;
    }
    if(props.editable == true){
        const res = await addDeviceApi(localApp.version,localApp.type,localApp.description,localApp.apk); // 调用后端接口
        if(res.data.code == 200){
            message.info("添加成功");
        }else{
            console.log(res)
        }
        emit("update"); // 通知父组件更新表格
    }else{
        const res = await updateDeviceApi(localApp.version,localApp.type,localApp.apk); // 调用后端接口
        if(res.data.code == 200){
            message.info("更新成功");
        }else{
            console.log(res)
        }
        emit("update"); // 通知父组件更新表格
    }

  } catch (err) {
    console.error("更新失败", err);
  }
};

</script>

<style scoped>
.overlay-child {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0,0,0,0.3);
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding-top: 40px;
  z-index: 1000;
  color: black;
}

.user-info {
  background-color: #fff;
  padding: 24px;
  border-radius: 8px;
  min-width: 400px;
}

label {
  display: block;
  margin-bottom: 12px;
}

input {
  width: 100%;
  padding: 6px 8px;
  margin-top: 4px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.buttons {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 16px;
}

.buttons button {
  padding: 6px 12px;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  color: #fff;
}

.buttons button:first-child { background-color: #4caf50; }
.buttons button:last-child { background-color: #f44336; }
</style>
