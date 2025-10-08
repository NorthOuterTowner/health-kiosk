import { createRouter, createWebHistory } from "vue-router";
import type { RouteRecordRaw } from "vue-router";
import Home from "../views/Home.vue";
import User from "../views/User.vue";
import Device from "../views/Device.vue";
import ExamItem from "../views/ExamItem.vue";
import ExamData from "../views/ExamData.vue";
import SelfInfo from "../views/SelfInfo.vue";
import AdminIntruction from "../views/adminIntruction.vue";
import SelfExam from "../views/SelfExam.vue";
import UserIntruction from "../views/userIntruction.vue";

const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "Home",
    component: Home,
  },
  {
    path: "/user",//用户管理
    name: "User",
    component: User
  },
  {
    path: "/device",
    name: "Device",
    component: Device
  },
  {
    path: "/examdata",
    name: "ExamData",
    component: ExamData
  },
  {
    path: "/examitem",
    name: "ExamItem",
    component: ExamItem
  },
  {
    path: "/selfinfo",
    name: "SelfInfo",
    component: SelfInfo
  },
  {
    path: "/userintruction",
    name: "UserIntruction",
    component: UserIntruction
  },
  {
    path: "/adminintruction",
    name: "AdminIntruction",
    component: AdminIntruction
  },
  {
    path: "/selfexam",
    name: "SelfExam",
    component: SelfExam
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
