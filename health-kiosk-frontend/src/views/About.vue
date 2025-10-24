<template>
  <div class="layout">
    <Sidebar />
    <div class="user-intruction-page">
      <h2 class="page-title">{{ $t('about.title') }}</h2>

      <div class="scroll-container">
        <div class="content">
          <div>

            <div>
              <h3>{{ $t('about.basic.title') }}</h3>
              <p>{{ $t('about.basic.desc') }}</p>
            </div>

            <div>
              <h3>{{ $t('about.arch.title') }}</h3>
              <p v-html="t('about.arch.desc')"></p>
            </div>

            <div>
              <h3>{{ $t('about.features.title') }}</h3>
              <ul>
                <li>{{ $t('about.features.item1') }}</li>
                <li>{{ $t('about.features.item2') }}</li>
                <li>{{ $t('about.features.item3') }}</li>
                <li>{{ $t('about.features.item4') }}</li>
                <li>{{ $t('about.features.item5') }}</li>
                <li>{{ $t('about.features.item6') }}</li>
              </ul>
            </div>

            <div>
              <h3>{{ $t('about.function.title') }}</h3>
              <div style="display: flex;">
                <div style="margin-right: 300px;">
                  <h4>{{ $t('about.function.admin.title') }}</h4>
                  <ol>
                    <li>{{ $t('about.function.admin.item1') }}</li>
                    <li>{{ $t('about.function.admin.item2') }}</li>
                    <li>{{ $t('about.function.admin.item3') }}</li>
                    <li>{{ $t('about.function.admin.item4') }}</li>
                    <li>{{ $t('about.function.admin.item5') }}</li>
                    <li>{{ $t('about.function.admin.item6') }}</li>
                    <li>{{ $t('about.function.admin.item7') }}</li>
                  </ol>
                </div>

                <div>
                  <h4>{{ $t('about.function.user.title') }}</h4>
                  <ol>
                    <li>{{ $t('about.function.user.item1') }}</li>
                    <li>{{ $t('about.function.user.item2') }}</li>
                    <li>{{ $t('about.function.user.item3') }}</li>
                    <li>{{ $t('about.function.user.item4') }}</li>
                  </ol>
                </div>
              </div>
            </div>

            <div>
              <h4>{{ $t('about.contact.title') }}</h4>
              <span>{{ $t('about.contact.repo') }}</span>
              <a href="https://github.com/NorthOuterTowner/health-kiosk">health-kiosk</a>
              <br />
              <span>{{ $t('about.contact.dev') }}</span>
              <a href="https://github.com/NorthOuterTowner">NorthOuterTowner</a>
            </div>

          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import Sidebar from '../components/Sidebar.vue'
const { t } = useI18n();
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
