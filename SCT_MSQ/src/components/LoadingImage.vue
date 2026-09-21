<template>
  <el-image class="loading-image" :src="src" :alt="alt" :fit="fit">
    <template #placeholder>
      <div class="image-state" role="status" aria-label="图片加载中">
        <span class="image-spinner" aria-hidden="true"></span>
      </div>
    </template>
    <template #error>
      <div class="image-state image-error" role="img" :aria-label="alt ? `${alt}：图片加载失败` : '图片加载失败'" title="图片加载失败">
        <el-icon aria-hidden="true"><Picture /></el-icon>
      </div>
    </template>
  </el-image>
</template>

<script setup>
import { Picture } from '@element-plus/icons-vue'

defineProps({
  src: { type: String, default: '' },
  alt: { type: String, default: '' },
  fit: { type: String, default: 'cover' }
})
</script>

<style scoped>
.loading-image { display: block; }
.image-state {
  width: 100%;
  height: 100%;
  min-height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--count-bg);
  color: var(--aqua-600);
  font-size: 24px;
}
.image-error { color: var(--ink-500); }
.image-spinner {
  width: 24px;
  height: 24px;
  box-sizing: border-box;
  border: 3px solid color-mix(in srgb, currentColor 20%, transparent);
  border-top-color: currentColor;
  border-radius: 50%;
  animation: image-spin 0.9s linear infinite;
}
@keyframes image-spin { to { transform: rotate(360deg); } }
@media (prefers-reduced-motion: reduce) {
  .image-spinner { animation: none; }
}
</style>