<template>
  <div class="layout">
    <Sidebar />
    <div class="user-intruction-page">
      <h2 class="page-title">{{ $t('adminInstruction.title') }}</h2>

      <div class="scroll-container">
        <div class="content">
          <div>
            <div>
              <h3>{{ $t('adminInstruction.sections.userManage.title') }}</h3>
              <p>{{ $t('adminInstruction.sections.userManage.content') }}</p>
            </div>

            <div>
              <h3>{{ $t('adminInstruction.sections.softwareManage.title') }}</h3>
              <p>{{ $t('adminInstruction.sections.softwareManage.content') }}</p>
            </div>

            <div>
              <h3>{{ $t('adminInstruction.sections.dataTrend.title') }}</h3>
              <p>{{ $t('adminInstruction.sections.dataTrend.content') }}</p>
            </div>

            <div>
              <h3>{{ $t('adminInstruction.sections.projectManage.title') }}</h3>
              <p>{{ $t('adminInstruction.sections.projectManage.content') }}</p>
            </div>

            <div>
              <h3>FAQs:</h3>

              <p><strong>{{ $t('userInstruction.faq.q1.question') }}</strong></p>
              <table border="1">
                <thead>
                  <tr>
                    <th>{{ $t('userInstruction.faq.q1.table.name') }}</th>
                    <th>{{ $t('userInstruction.faq.q1.table.meaning') }}</th>
                    <th>{{ $t('userInstruction.faq.q1.table.note') }}</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>release</td>
                    <td>{{ $t('userInstruction.faq.q1.table.release.meaning') }}</td>
                    <td>{{ $t('userInstruction.faq.q1.table.release.note') }}</td>
                  </tr>
                  <tr>
                    <td>debug</td>
                    <td>{{ $t('userInstruction.faq.q1.table.debug.meaning') }}</td>
                    <td>{{ $t('userInstruction.faq.q1.table.debug.note') }}</td>
                  </tr>
                  <tr>
                    <td>alpha</td>
                    <td>{{ $t('userInstruction.faq.q1.table.alpha.meaning') }}</td>
                    <td>{{ $t('userInstruction.faq.q1.table.alpha.note') }}</td>
                  </tr>
                  <tr>
                    <td>beta</td>
                    <td>{{ $t('userInstruction.faq.q1.table.beta.meaning') }}</td>
                    <td>{{ $t('userInstruction.faq.q1.table.beta.note') }}</td>
                  </tr>
                </tbody>
              </table>

              <p><strong>{{ $t('userInstruction.faq.q2.question') }}</strong></p>
              <p>{{ $t('userInstruction.faq.q2.answer') }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import Sidebar from "../components/Sidebar.vue";
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

table {
  border-collapse: collapse;
  width: 100%;
}
th,
td {
  border: 1px solid #ccc;
  padding: 8px;
  text-align: left;
}
thead {
  background: #f7f7f7;
}
</style>
