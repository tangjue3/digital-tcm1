const STORAGE_PREFIX = 'tcm-ai-assistant:'

function resolveStorage() {
  if (typeof window === 'undefined') {
    return null
  }
  try {
    return window.sessionStorage
  } catch (error) {
    console.warn('[tcm-ai-assistant] sessionStorage unavailable', error)
    return null
  }
}

function buildStorageKey(key) {
  return `${STORAGE_PREFIX}${key || 'default'}`
}

function loadTcmAiSession(key = 'default') {
  const storage = resolveStorage()
  if (!storage) {
    return null
  }
  try {
    const raw = storage.getItem(buildStorageKey(key))
    if (!raw) {
      return null
    }
    return JSON.parse(raw)
  } catch (error) {
    console.warn('[tcm-ai-assistant] load session failed', error)
    return null
  }
}

function saveTcmAiSession(key = 'default', payload = {}) {
  const storage = resolveStorage()
  if (!storage) {
    return
  }
  try {
    storage.setItem(buildStorageKey(key), JSON.stringify(payload))
  } catch (error) {
    console.warn('[tcm-ai-assistant] save session failed', error)
  }
}

function clearTcmAiSession(key = 'default') {
  const storage = resolveStorage()
  if (!storage) {
    return
  }
  try {
    storage.removeItem(buildStorageKey(key))
  } catch (error) {
    console.warn('[tcm-ai-assistant] clear session failed', error)
  }
}

export {
  loadTcmAiSession,
  saveTcmAiSession,
  clearTcmAiSession
}
