import { createRouter, createWebHistory } from "vue-router";
import type { RouteRecordRaw } from "vue-router";
import Home from "../views/Home.vue";
import User from "../views/userViews/User.vue";
import Device from "../views/deviceViews/Device.vue";
import ExamItem from "../views/examitemViews/ExamItem.vue";
import ExamData from "../views/examdataViews/ExamData.vue";
import SelfInfo from "../views/selfViews/SelfInfo.vue";
import AdminIntruction from "../views/instructionViews/adminIntruction.vue";
import SelfExam from "../views/selfViews/SelfExam.vue";
import UserIntruction from "../views/instructionViews/userIntruction.vue";
import About from "../views/instructionViews/About.vue";
import Role from "../views/roleViews/roleManage.vue"
import Forbidden403 from "../views/statusViews/Forbidden403.vue";
import NotFound404 from "../views/statusViews/NotFound404.vue";
import Server500 from "../views/statusViews/Server500.vue";
import RoleAssign from "../views/roleViews/RoleAssign.vue";

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
  },
  {
    path: "/about",
    name: "About",
    component: About
  },
  {
    path: "/role",
    name: "Role",
    component: Role
  },
  {
    path: "/roleassign",
    name: "RoleAssign",
    component: RoleAssign
  },
  {
    path: "/403",
    name: "Forbidden403",
    component: Forbidden403
  },
  {
    path: "/404",
    name: "NotFound404",
    component: NotFound404
  },
  {
    path: "/500",
    name: "Server500",
    component: Server500
  },
  {
    path: '/:pathMatch(.*)*',
    name: '404',
    component: NotFound404,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
