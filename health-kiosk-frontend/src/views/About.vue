<template>
  <div class="layout">
    <Sidebar />
    <div class="user-intruction-page">
      <h2 class="page-title">关于本项目</h2>

      <div class="scroll-container">
        <div class="content">
          <div>

            <div>
              <h3>基本说明</h3>
              <p>
                本项目是铁路司机用健康检测系统的后台管理系统。
                项目基于体检设备进行设计，采用 B/S 架构便于进行访问和使用。项目为用户提供了Android应用（即体检软件）的不同版本，可供用户进行下载和使用。
              </p>
            </div>

            <div>
              <h3>系统架构</h3>
              <p>
                本项目采用前后端分离架构：
                前端使用 <strong>Vue 3 + TypeScript + Naive-UI + Lucide</strong>，
                后端基于 <strong>Node.js + Express + MySQL + Redis</strong> ，
                人脸识别部分使用<strong>Python + dlib</strong>
                前后端通过<strong> RESTful API </strong>进行数据交互，并使用<strong> JWT </strong>进行用户认证。
              </p>
            </div>

            <div>
              <h3>项目特色</h3>
              <ul>
                <li>支持多用户角色登录与权限控制。</li>
                <li>采用响应式布局设计，兼容桌面端与移动端访问。</li>
                <li>体检数据支持可视化分析，图表动态展示健康趋势。（待完成）</li>
                <li>支持远程更新安卓端体检软件。</li>
                <li>对DDOS攻击和SQL注入攻击进行了防护。</li>
                <li>支持中英双语进行访问。</li>
              </ul>
            </div>

            <div>
              <h3>功能分布</h3>
              <div style="display: flex; ">
                <div style="margin-right: 300px;">
                  <h4>管理员端</h4>              
                  <ol>
                    <li>用户管理</li>
                    <li>发布、下载、更新安卓端软件</li>
                    <li>管理体检数据</li>
                    <li>查看体检数据分析</li>
                    <li>修改、查看个人信息</li>
                    <li>管理体检项目</li>
                    <li>提供使用说明</li>
                  </ol>
                </div>

                <div>
                  <h4>基本用户端</h4>           
                  <ol>
                    <li>下载安卓端软件</li>
                    <li>查询个人体检数据</li>
                    <li>修改、查看个人信息</li>
                    <li>查看使用说明</li>
                  </ol>
                </div>
              </div>
            </div>

            <div>
              <h4>联系我们</h4>
              <span>项目地址：</span><a href="https://github.com/NorthOuterTowner/health-kiosk">health-kiosk</a>
              <br />
              <span>开发者：</span><a href="https://github.com/NorthOuterTowner">NorthOuterTowner</a>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import Sidebar from '../components/Sidebar.vue'
</script>

<style scoped>
.layout {
  display: flex;
  gap: 20px;
  min-height: 100vh;
}

.user-intruction-page {
  margin-left: 16rem;
  flex: 1;
  padding: 2rem;
  display: flex;
  flex-direction: column;
}

.page-title {
  font-size: 1.8rem;
  font-weight: 600;
  color: #003366;
  text-align: center;
  margin-bottom: 1rem;
}

.scroll-container {
  flex: 1; 
  background-color: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
  padding: 1.5rem;
  max-height: calc(100vh - 150px); 
}

.content {
  line-height: 1.8;
  color: #333;
}
</style>
