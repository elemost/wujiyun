import { storeToRefs } from 'pinia'
import { useThemeStore } from '@/store/theme'

export function useGlobalTheme() {
  const { themeColor } = storeToRefs(useThemeStore())
  return { themeColor, useThemeStore }
}