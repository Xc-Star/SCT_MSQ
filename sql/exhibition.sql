/*
 增量脚本：展览内容（exhibition）
 用途：在已存在的 sct 库上单独执行，不需要重新导入 sct.sql（重新导入会清空现有数据）。
 执行：mysql -h <host> -P 3306 -u root -p sct < sql/exhibition.sql

 可重复执行（幂等）：建表用 IF NOT EXISTS。

 机器展览区 / 建筑展览区 / 其他内容 三块内容共用这一张表，靠 category 区分：

   category 值 | 前台展示位置
   ----------- | -------------------
   redstone    | 机器展览区（/overview/machine）
   building    | 建筑展览区（/overview/building）
   other       | 其他内容（/overview/other）

 注意：category 存的是英文枚举值，中文名在前后端各自映射（前端见
 SCT_MSQ/src/constants/exhibition.js，后端见 Exhibition.CATEGORY_*）。
*/

-- ----------------------------
-- 展览内容表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `exhibition`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '标题',
  `category` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'other' COMMENT '分类 redstone-红石 building-建筑 other-其他',
  `cover` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面图地址，为空时取 images 第一张',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '图片列表，JSON 数组字符串，按展示顺序存放',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '正文',
  `top` tinyint NOT NULL DEFAULT 0 COMMENT '是否置顶 0-否 1-是',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态 0-隐藏 1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_status`(`category` ASC, `status` ASC, `top` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '展览内容（机器/建筑/其他）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 可选：示例数据（正式环境可跳过这一段）
-- ----------------------------
-- INSERT INTO `exhibition` (`title`, `category`, `cover`, `images`, `content`, `top`, `status`) VALUES
--   ('四连鱼塔', 'redstone', NULL,
--    '["/profile/upload/2025/07/02/123054495_p2_20250702014641A001.jpg"]',
--    '自动钓鱼机，四联结构，产量稳定。', 1, 1),
--   ('SCT 主世界大厅', 'building', NULL,
--    '["/profile/upload/2025/07/02/123054495_p2_20250702014641A001.jpg"]',
--    '主世界出生点大厅，采用现代风格。', 0, 1),
--   ('服务器活动合影', 'other', NULL,
--    '["/profile/upload/2025/07/02/123054495_p2_20250702014641A001.jpg"]',
--    '2026 年服务器周年活动合影。', 0, 1);
