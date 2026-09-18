/**
 * 展览内容分类
 *
 * 机器展览区 / 建筑展览区 / 其他内容 三块共用一张 exhibition 表，
 * 靠 category 区分，取值与后端 Exhibition.CATEGORY_* 保持一致。
 */

export const EXHIBITION_CATEGORY = {
  REDSTONE: 'redstone',
  BUILDING: 'building',
  OTHER: 'other'
}

/** 分类下拉选项，section 是该分类对应的前台展示位置 */
export const EXHIBITION_CATEGORY_OPTIONS = [
  { value: EXHIBITION_CATEGORY.REDSTONE, label: '红石', section: '机器展览区', tagType: 'danger' },
  { value: EXHIBITION_CATEGORY.BUILDING, label: '建筑', section: '建筑展览区', tagType: 'primary' },
  { value: EXHIBITION_CATEGORY.OTHER, label: '其他', section: '其他内容', tagType: 'info' }
]

const CATEGORY_MAP = EXHIBITION_CATEGORY_OPTIONS.reduce((acc, item) => {
  acc[item.value] = item
  return acc
}, {})

/** 分类英文值 -> 中文名 */
export function categoryLabel(value) {
  return CATEGORY_MAP[value]?.label || '其他'
}

/** 分类英文值 -> 对应的前台展示位置 */
export function categorySection(value) {
  return CATEGORY_MAP[value]?.section || '其他内容'
}

/** 分类英文值 -> el-tag 的 type */
export function categoryTagType(value) {
  return CATEGORY_MAP[value]?.tagType || 'info'
}
