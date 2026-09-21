import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getConfig } from '@/api/System'

export function useSiteBackground() {
  const route = useRoute()
  const config = ref({})
  const viewport = window.matchMedia('(max-width: 768px)')
  const isMobile = ref(viewport.matches)
  const updateViewport = () => { isMobile.value = viewport.matches }

  watch([config, isMobile, () => route.path], () => {
    const isQuestionnaire = route.path === '/msq' || route.path === '/msq/result'
    const key = isQuestionnaire ? 'msq_background' : 'main_background'
    const image = (isMobile.value && config.value[`phone_${key}`]) || config.value[key] || ''
    document.documentElement.style.setProperty('--sct-bg-image', image ? `url(${JSON.stringify(image)})` : 'none')
  }, { immediate: true })

  onMounted(async () => {
    viewport.addEventListener('change', updateViewport)
    try {
      const { data } = await getConfig()
      if (Array.isArray(data)) config.value = Object.fromEntries(data.map(item => [item.configKey, item.configValue]))
    } catch {
      config.value = {}
    }
  })

  onBeforeUnmount(() => {
    viewport.removeEventListener('change', updateViewport)
    document.documentElement.style.removeProperty('--sct-bg-image')
  })
}