import { onScopeDispose } from 'vue'

export function useLatestRequest() {
  let controller

  const cancel = () => controller?.abort()
  onScopeDispose(cancel)

  const start = () => {
    cancel()
    const current = new AbortController()
    controller = current
    return {
      signal: current.signal,
      isCurrent: () => controller === current && !current.signal.aborted
    }
  }

  return { start, cancel }
}