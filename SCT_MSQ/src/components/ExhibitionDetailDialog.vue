<template>
  <el-dialog
    v-model="visible"
    class="exhibition-dialog"
    :width="dialogWidth"
    top="5vh"
    append-to-body
    destroy-on-close
    @closed="handleClosed"
  >
    <template #header>
      <div class="dialog-header">
        <el-tag v-if="detail" :type="categoryTagType(detail.category)" size="small" effect="light">
          {{ categorySection(detail.category) }}
        </el-tag>
      </div>
    </template>

    <div class="exh-body" :aria-busy="loading">
      <div v-if="loading" class="exh-inner">
        <div class="exh-media"><ContentSkeleton /></div>
        <div class="exh-text"><ContentSkeleton variant="form" /></div>
      </div>
      <div v-else-if="detail" class="exh-inner">
        <!-- 左：图 -->
        <div class="exh-media">
          <!-- 右上角切换显示模式 -->
          <div class="media-toolbar">
            <div class="mode-switch">
              <button
                type="button"
                class="mode-btn"
                :class="{ active: mode === 'single' }"
                title="只显示一张大图，下方缩略图切换"
                @click="mode = 'single'"
              >
                <el-icon><FullScreen /></el-icon><span>大图</span>
              </button>
              <button
                type="button"
                class="mode-btn"
                :class="{ active: mode === 'grid' }"
                title="拼接显示全部图片"
                @click="mode = 'grid'"
              >
                <el-icon><Grid /></el-icon><span>拼接</span>
              </button>
            </div>
          </div>

          <!-- 模式一：大图 + 缩略图 + 左右翻页 -->
          <div v-if="mode === 'single'" class="single-mode">
            <div class="stage">
              <button
                v-if="images.length > 1"
                type="button"
                class="nav-btn prev"
                title="上一张"
                @click="prev"
              >‹</button>

              <LoadingImage
                :key="images[current]"
                class="stage-img"
                :src="images[current]"
                :alt="detail.title"
                fit="contain"
                :preview-src-list="images"
                :initial-index="current"
                preview-teleported
                hide-on-click-modal
              />

              <button
                v-if="images.length > 1"
                type="button"
                class="nav-btn next"
                title="下一张"
                @click="next"
              >›</button>
            </div>

            <div v-if="images.length > 1" class="thumbs">
              <button
                v-for="(img, i) in images"
                :key="`${i}-${img}`"
                type="button"
                class="thumb"
                :class="{ active: i === current }"
                @click="current = i"
              >
                <LoadingImage :src="img" :alt="`第${i + 1}张缩略图`" class="thumb-image" loading="lazy" />
              </button>
            </div>
          </div>

          <!-- 模式二：拼接，自适应排列 -->
          <div v-else class="grid-mode">
            <LoadingImage
              v-for="(img, i) in images"
              :key="`${i}-${img}`"
              class="grid-img"
              :src="img"
              :alt="`${detail.title} · ${i + 1}`"
              fit="cover"
              :preview-src-list="images"
              :initial-index="i"
              preview-teleported
              hide-on-click-modal
            />
          </div>
        </div>

        <!-- 右：文 -->
        <div class="exh-text">
          <h2 class="exh-title">{{ detail.title }}</h2>
          <div class="exh-content">{{ detail.content }}</div>
          <div class="exh-footer">
            <span class="exh-time">{{ detail.createTime }}</span>
          </div>
        </div>
      </div>

      <div v-else-if="!loading" class="exh-empty">内容不存在或已下架</div>
    </div>
  </el-dialog>
</template>

<script setup>
import { computed, onBeforeUnmount, ref, watch } from 'vue'
import { FullScreen, Grid } from '@element-plus/icons-vue'
import ContentSkeleton from '@/components/ContentSkeleton.vue'
import LoadingImage from '@/components/LoadingImage.vue'
import { getExhibitionDetail } from '@/api/Exhibition'
import { useLatestRequest } from '@/composables/useLatestRequest'
import { categorySection, categoryTagType } from '@/constants/exhibition'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  exhibitionId: { type: [Number, String], default: null }
})

const emit = defineEmits(['update:modelValue'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

/** 窗口宽度，用来决定弹窗尺寸与左右/上下布局 */
const windowWidth = ref(window.innerWidth)
const isNarrow = computed(() => windowWidth.value <= 820)
const dialogWidth = computed(() => (isNarrow.value ? '94%' : 'min(1180px, 92vw)'))

const handleResize = () => {
  windowWidth.value = window.innerWidth
}

const loading = ref(false)
const detail = ref(null)
const detailRequest = useLatestRequest()

/** 显示模式，默认大图 */
const mode = ref('single')
const current = ref(0)

const images = computed(() => (Array.isArray(detail.value?.images) ? detail.value.images : []))

function prev() {
  if (images.value.length < 2) return
  current.value = (current.value - 1 + images.value.length) % images.value.length
}

function next() {
  if (images.value.length < 2) return
  current.value = (current.value + 1) % images.value.length
}

async function fetchDetail(id) {
  const request = detailRequest.start()
  if (id === null || id === undefined || id === '') {
    detail.value = null
    loading.value = false
    return
  }
  loading.value = true
  detail.value = null
  try {
    const res = await getExhibitionDetail(id, { signal: request.signal })
    if (!request.isCurrent()) return
    detail.value = res?.data || null
  } catch (e) {
    if (request.isCurrent()) detail.value = null
  } finally {
    if (request.isCurrent()) loading.value = false
  }
}

/** 打开时重置为默认的大图模式并拉详情 */
watch(
  () => [props.modelValue, props.exhibitionId],
  ([open, id]) => {
    if (!open) {
      detailRequest.cancel()
      loading.value = false
      return
    }
    mode.value = 'single'
    current.value = 0
    fetchDetail(id)
  },
  { immediate: true }
)

/** 大图模式下支持左右方向键翻页 */
function handleKeydown(e) {
  if (!props.modelValue || mode.value !== 'single') return
  if (e.key === 'ArrowLeft') prev()
  if (e.key === 'ArrowRight') next()
}

function handleClosed() {
  detail.value = null
  current.value = 0
  mode.value = 'single'
}

watch(
  () => props.modelValue,
  (open) => {
    if (open) {
      handleResize()
      window.addEventListener('resize', handleResize)
      window.addEventListener('keydown', handleKeydown)
    } else {
      window.removeEventListener('resize', handleResize)
      window.removeEventListener('keydown', handleKeydown)
    }
  }
)

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  window.removeEventListener('keydown', handleKeydown)
})
</script>

<style scoped>
.dialog-header {
  display: flex;
  align-items: center;
  gap: 8px;
}

.exh-body {
  min-height: 200px;
}

.exh-inner {
  display: flex;
  gap: 20px;
  height: 72vh;
  max-height: 760px;
}

/* ---------------- 左：图 ---------------- */
.exh-media {
  position: relative;
  flex: 1.6 1 0;
  min-width: 0;
  display: flex;
  flex-direction: column;
  background: var(--count-bg);
  border-radius: var(--radius-md);
  padding: 10px;
  overflow: hidden;
}

.media-toolbar {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 3;
}

.mode-switch {
  display: flex;
  padding: 2px;
  border-radius: var(--radius-pill);
  background: var(--shell-strong);
  box-shadow: var(--shadow-soft), var(--glass-spec);
  outline: 1px solid var(--shell-border);
  backdrop-filter: blur(2px) saturate(1.8);
  -webkit-backdrop-filter: blur(2px) saturate(1.8);
}

.mode-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border: none;
  border-radius: var(--radius-pill);
  background: transparent;
  color: var(--ink-500);
  font-size: 0.78rem;
  line-height: 1.4;
  cursor: pointer;
  transition: all 0.2s;
}

.mode-btn:hover {
  color: var(--aqua-500);
}

.mode-btn.active {
  background: var(--tag-bg);
  color: var(--ink-900);
  box-shadow: inset 0 0 0 1px var(--aqua-400), var(--glass-spec);
}

.mode-btn.active:hover { background: var(--sct-button-hover-bg); }

/* 模式一 */
.single-mode {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
}

.stage {
  position: relative;
  flex: 1;
  min-height: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stage-img {
  width: 100%;
  height: 100%;
}

.stage-img :deep(img) {
  /* border-radius: var(--radius-pill); */
}

.nav-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  z-index: 2;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 50%;
  background: var(--sct-button-bg);
  color: var(--ink-900);
  box-shadow: inset 0 0 0 1px var(--shell-border), var(--shadow-soft), var(--glass-spec);
  backdrop-filter: blur(2px) saturate(1.8);
  -webkit-backdrop-filter: blur(2px) saturate(1.8);
  font-size: 1.4rem;
  line-height: 1;
  cursor: pointer;
  transition: background 0.2s;
}

.nav-btn:hover {
  background: var(--sct-button-hover-bg);
}

.nav-btn.prev {
  left: 8px;
}

.nav-btn.next {
  right: 8px;
}

.thumbs {
  display: flex;
  gap: 8px;
  padding: 10px 2px 2px;
  overflow-x: auto;
  flex-shrink: 0;
}

.thumb {
  flex: 0 0 auto;
  width: 62px;
  height: 46px;
  padding: 0;
  border: 2px solid transparent;
  border-radius: var(--radius-md);
  background: var(--shell-strong);
  overflow: hidden;
  cursor: pointer;
  opacity: 0.65;
  transition: all 0.2s;
}

.thumb-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.thumb:hover {
  opacity: 1;
}

.thumb.active {
  border-color: var(--aqua-500);
  opacity: 1;
}

/* 模式二 */
.grid-mode {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(130px, 1fr));
  grid-auto-rows: min-content;
  gap: 8px;
  /* 给右上角的模式切换留出位置，避免压住第一排图片 */
  padding: 34px 2px 2px 0;
}

.grid-img {
  width: 100%;
  aspect-ratio: 1 / 1;
  border-radius: var(--radius-md);
  overflow: hidden;
  background: var(--hair);
  cursor: zoom-in;
}

/* ---------------- 右：文 ---------------- */
.exh-text {
  flex: 1 1 0;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.exh-title {
  margin: 0 0 12px;
  padding-right: 4px;
  font-size: 1.2rem;
  font-weight: 600;
  line-height: 1.45;
  color: var(--ink-900);
  letter-spacing: 0;
  word-break: break-word;
}

.exh-content {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding-right: 6px;
  color: var(--ink-500);
  font-size: 0.92rem;
  line-height: 1.8;
  white-space: pre-wrap;
  word-break: break-word;
}

.exh-footer {
  flex-shrink: 0;
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid var(--hair);
}

.exh-time {
  font-size: 0.82rem;
  color: var(--ink-500);
}

.exh-empty {
  padding: 60px 0;
  text-align: center;
  color: var(--ink-500);
  font-size: 0.92rem;
}

/* 窄屏改成上下布局 */
@media (max-width: 820px) {
  .exh-inner {
    flex-direction: column;
    height: auto;
    max-height: none;
    gap: 14px;
  }

  .exh-media {
    flex: none;
    height: 46vh;
  }

  .exh-text {
    flex: none;
  }

  .exh-content {
    max-height: 34vh;
    flex: none;
  }

  .grid-mode {
    grid-template-columns: repeat(auto-fill, minmax(96px, 1fr));
  }

  .exh-title {
    font-size: 1.05rem;
  }
}
</style>
