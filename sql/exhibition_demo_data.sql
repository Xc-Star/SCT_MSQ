-- ============================================================
-- 展览内容（exhibition）测试数据
-- ------------------------------------------------------------
-- 用途：本地/开发环境铺一批演示数据，方便查看机器展览区、
--       建筑展览区、其他内容三个区块以及弹窗的两种图片模式。
--
-- 特点：
--   1. 所有演示行的 remark 都是 'demo'，可整批清除：
--        DELETE FROM `exhibition` WHERE `remark` = 'demo';
--   2. 脚本开头会先删掉旧的 demo 行，所以可以重复执行。
--   3. 图片用的是 picsum.photos 占位图（随机真实照片），
--      需要联网才能显示；换成真实截图直接在管理端重新上传即可。
--
-- 覆盖到的场景：
--   - 分类：redstone(机器) / building(建筑) / other(其他)
--   - 图片数量：1 / 2 / 3 / 4 / 5 / 6 / 7 / 8 / 9 张（拼接模式排布）
--   - 封面：部分显式指定、部分留空（验证「留空自动取第一张」兜底）
--   - 置顶：3 条 top=1，验证置顶优先排序
--   - 隐藏：1 条 status=0，验证前台过滤、后台可见
--   - 横图与竖图混排，验证大图模式的 contain 表现
-- ============================================================

DELETE FROM `exhibition` WHERE `remark` = 'demo';

INSERT INTO `exhibition`
  (`title`, `category`, `cover`, `images`, `content`, `top`, `status`, `create_time`, `remark`)
VALUES

-- ============================ 机器展览区（红石） ============================
(
  '四连鱼塔 · 全自动钓鱼机', 'redstone',
  'https://picsum.photos/seed/sct-r1c/1600/1000',
  '["https://picsum.photos/seed/sct-r1a/1600/1000","https://picsum.photos/seed/sct-r1b/1600/1000","https://picsum.photos/seed/sct-r1c/1600/1000","https://picsum.photos/seed/sct-r1d/1600/1000","https://picsum.photos/seed/sct-r1e/1600/1000","https://picsum.photos/seed/sct-r1f/1200/1600","https://picsum.photos/seed/sct-r1g/1600/1000","https://picsum.photos/seed/sct-r1h/1600/1000"]',
  '四连鱼塔是本服第一台完全无人值守的自动钓鱼机，四座塔并排运行，共用一条中央收集水道，挂机一晚上也不用管。\n\n核心思路是用气泡柱把钓上来的东西推到中间的漏斗矿车收集点，再经过分类装置把附魔书、命名牌和鱼分开存放。整套结构只有 3 个红石元件，其余全靠水流和漏斗，稳定性非常高。\n\n实测效率：单人挂机 1 小时约产出 1200 条以上的鱼，附魔书稳定在每小时 6 到 8 本，偶尔还能出命名牌。\n\n建造材料主要是石砖和玻璃，成本不高。分步搭建教程和材料清单见下方图片，新手照着复刻基本不会翻车。',
  1, 1, '2026-09-10 21:14:00', 'demo'
),
(
  '1.21 全物品仓库', 'redstone',
  NULL,
  '["https://picsum.photos/seed/sct-r2a/1600/1000","https://picsum.photos/seed/sct-r2b/1600/1000","https://picsum.photos/seed/sct-r2c/1600/1000","https://picsum.photos/seed/sct-r2d/1600/1000","https://picsum.photos/seed/sct-r2e/1600/1000","https://picsum.photos/seed/sct-r2f/1200/1600"]',
  '按 1.21 版本配方重新设计过的全物品分类仓库，支持目前版本全部可堆叠物品的自动归类。\n\n整体是一套「漏斗 + 分类器」的经典结构，但针对 1.21 新增的物品做了补充。每个分类单元只占 2x2 的空间，一共铺了 14 排，走一圈大概 40 秒。\n\n取货口做了双向设计：正面是普通取货，背面可以直接连到合成台阵列，做完东西不用再跑回仓库。\n\n注意：这套仓库依赖物品的堆叠上限来判断分类，所以像药水、附魔书这类不可堆叠物品需要单独开一列，别混着装。',
  0, 1, '2026-09-08 19:02:00', 'demo'
),
(
  '高速熔炉阵列（64 炉并联）', 'redstone',
  NULL,
  '["https://picsum.photos/seed/sct-r3a/1600/1000","https://picsum.photos/seed/sct-r3b/1600/1000","https://picsum.photos/seed/sct-r3c/1600/1000"]',
  '64 台熔炉并联的大型烧炼阵列，用来一次性处理大批量的原矿和沙子。\n\n燃料统一从顶部投入，用漏斗线均匀分配到每一台熔炉；产物从底部汇入一条水道，直接推进箱子。整套阵列装满一次燃料可以连续烧 20 分钟左右。\n\n如果只是日常用量，建议只开一半（32 炉），满载运行时对区块加载压力比较明显，低配机器可能会掉帧。',
  0, 1, '2026-08-30 22:41:00', 'demo'
),
(
  '潜影贝农场 · 全自动', 'redstone',
  'https://picsum.photos/seed/sct-r4a/1600/1000',
  '["https://picsum.photos/seed/sct-r4a/1600/1000","https://picsum.photos/seed/sct-r4b/1600/1000","https://picsum.photos/seed/sct-r4c/1600/1000","https://picsum.photos/seed/sct-r4d/1200/1600","https://picsum.photos/seed/sct-r4e/1600/1000"]',
  '利用末地城的潜影贝刷新机制做的自动农场，主要产出潜影壳，用来做潜影盒。\n\n结构上把刷怪平台做成了十字形，配合活塞把潜影贝推到中央的击杀区，掉落物由漏斗矿车统一收集。\n\n效率：每小时大约 120 到 180 个潜影壳，够全服玩家做潜影盒了。\n\n注意一定要先清空周围的末地城，不然刷怪上限会被别的怪占掉。',
  0, 1, '2026-08-24 20:17:00', 'demo'
),
(
  '竹子与甘蔗全自动农场', 'redstone',
  'https://picsum.photos/seed/sct-r5a/1600/1000',
  '["https://picsum.photos/seed/sct-r5a/1600/1000","https://picsum.photos/seed/sct-r5b/1600/1000","https://picsum.photos/seed/sct-r5c/1600/1000","https://picsum.photos/seed/sct-r5d/1600/1000"]',
  '竹子 + 甘蔗的双作物自动农场，产出主要用于合成脚手架和纸。\n\n两种作物共用同一套观察者 + 活塞的收割结构，区别只是底部的种植基质。竹子长得快，单独接了一条线去喂熔炉当燃料，基本实现燃料自由。\n\n整机占地很小，8x12 就能放下，非常适合放在基地旁边当个副业。',
  0, 1, '2026-08-18 21:55:00', 'demo'
),
(
  '简易刷铁机（新手向）', 'redstone',
  NULL,
  '["https://picsum.photos/seed/sct-r6a/1600/1000","https://picsum.photos/seed/sct-r6b/1600/1000"]',
  '专门给新手准备的刷铁机方案，材料只要 3 组石头、2 组玻璃和 1 个漏斗。\n\n原理很简单：用村民和僵尸制造恐慌，触发铁傀儡刷新，再用水流把铁傀儡冲到中央的岩浆处击杀，掉落物由漏斗收集。\n\n效率不高，每小时大概 30 到 40 个铁锭，但胜在好造、好懂、好维护，建议当作第一个红石作品的练手项目。',
  0, 1, '2026-08-11 18:30:00', 'demo'
),

-- ============================ 建筑展览区 ============================
(
  'SCT 主世界大厅', 'building',
  'https://picsum.photos/seed/sct-b1b/1600/1000',
  '["https://picsum.photos/seed/sct-b1a/1600/1000","https://picsum.photos/seed/sct-b1b/1600/1000","https://picsum.photos/seed/sct-b1c/1600/1000","https://picsum.photos/seed/sct-b1d/1600/1000","https://picsum.photos/seed/sct-b1e/1200/1600","https://picsum.photos/seed/sct-b1f/1600/1000","https://picsum.photos/seed/sct-b1g/1600/1000"]',
  '服务器出生点大厅，也是所有新玩家的第一站。\n\n整体用了石砖 + 深板岩的冷色调，穹顶做成了开放式结构，白天采光很好。大厅中央是传送门区，四个方向分别通往机器区、建筑区、资源世界和末地。\n\n两侧墙面留了展示墙，用来轮播服务器的活动公告和玩家作品。\n\n建造历时大约三周，参与玩家 9 人。设计上刻意保持了开阔的视野，避免新手出生后找不到方向。',
  1, 1, '2026-09-12 20:05:00', 'demo'
),
(
  '地狱交通枢纽 · 全线路图', 'building',
  'https://picsum.photos/seed/sct-b2a/1600/1000',
  '["https://picsum.photos/seed/sct-b2a/1600/1000","https://picsum.photos/seed/sct-b2b/1600/1000"]',
  '以地狱（下界）为主的交通网络中心节点，连接主城、各大农场和资源点。\n\n全部线路都建在 y=120 的高度，避免了地形干扰。枢纽本身是十字形结构，每条线路入口都有明显的标识牌和颜色编码，跟着颜色走不会迷路。\n\n目前已经开通 11 条线路，从枢纽出发到最远的刷怪塔大约 40 秒。\n\n新线路的建造规范也贴在枢纽入口，欢迎玩家自己接线路过来。',
  0, 1, '2026-09-05 21:30:00', 'demo'
),
(
  '海晶石宫殿', 'building',
  'https://picsum.photos/seed/sct-b3a/1600/1000',
  '["https://picsum.photos/seed/sct-b3a/1600/1000","https://picsum.photos/seed/sct-b3b/1600/1000","https://picsum.photos/seed/sct-b3c/1600/1000","https://picsum.photos/seed/sct-b3d/1200/1600","https://picsum.photos/seed/sct-b3e/1600/1000"]',
  '建在深海中央的海晶石主题宫殿，从海面上看只露出尖顶，需要潜下去才能看到全貌。\n\n主体材料是海晶石和暗海晶石，配合海晶灯做整体照明，入夜之后整座建筑会泛出青蓝色的光，非常好看。\n\n内部做了三个区域：正厅、藏书室和观景长廊。观景长廊全部用玻璃铺底，站在上面可以直接看到海底的珊瑚。\n\n建造时用了大量海绵排水，前后大概消耗了 8 组。',
  0, 1, '2026-08-28 22:10:00', 'demo'
),
(
  '中世纪小镇「风车谷」', 'building',
  'https://picsum.photos/seed/sct-b4d/1600/1000',
  '["https://picsum.photos/seed/sct-b4a/1600/1000","https://picsum.photos/seed/sct-b4b/1600/1000","https://picsum.photos/seed/sct-b4c/1600/1000","https://picsum.photos/seed/sct-b4d/1600/1000","https://picsum.photos/seed/sct-b4e/1600/1000","https://picsum.photos/seed/sct-b4f/1600/1000"]',
  '依山而建的中世纪风格小镇，共 14 栋建筑，包含住宅、铁匠铺、酒馆和一座可以真正转起来的风车。\n\n屋顶统一用了深色橡木和石砖的组合，街道用粗泥和砂砾铺成，刻意做旧。小镇的照明全部靠灯笼，没有插火把，晚上看起来很有氛围。\n\n风车内部是真的有机械结构，用红石带动叶片旋转，不是装饰。\n\n这个项目是服务器里玩家协作程度最高的一个，前后有 12 个人参与。',
  1, 1, '2026-08-21 20:44:00', 'demo'
),
(
  '末地传送门神殿', 'building',
  NULL,
  '["https://picsum.photos/seed/sct-b5a/1600/1000","https://picsum.photos/seed/sct-b5b/1600/1000","https://picsum.photos/seed/sct-b5c/1200/1600"]',
  '围绕主世界末地传送门修建的神殿，把原本露天生成的传送门整个包了进去。\n\n建筑用了黑曜石 + 紫珀块的配色，和传送门本身的色调呼应。神殿地面做了下沉设计，玩家从入口走下台阶时，传送门会从视野下方慢慢出现，仪式感很强。\n\n四周立了 12 根柱子，每根柱子上刻了一个参与击杀末影龙的玩家名字。',
  0, 1, '2026-08-14 19:26:00', 'demo'
),

-- ============================ 其他内容 ============================
(
  '服务器二周年合影', 'other',
  'https://picsum.photos/seed/sct-o1a/1600/1000',
  '["https://picsum.photos/seed/sct-o1a/1600/1000","https://picsum.photos/seed/sct-o1b/1600/1000","https://picsum.photos/seed/sct-o1c/1600/1000","https://picsum.photos/seed/sct-o1d/1600/1000"]',
  '2026 年 9 月，SCT 服务器两周年纪念活动合影。\n\n当天在线玩家 27 人，大家在大厅集合，按加入服务器的先后顺序排了队形。最早的一批玩家站在最前排，后面是新加入的朋友。\n\n活动流程是：合影 → 建筑巡礼 → 红石比赛 → 烟花。最后一张是活动结束时放烟花的画面。\n\n感谢两年来一起玩的朋友，服务器会一直开下去。',
  0, 1, '2026-09-14 21:00:00', 'demo'
),
(
  '玩家建筑比赛作品集（2026 夏）', 'other',
  'https://picsum.photos/seed/sct-o2b/1600/1000',
  '["https://picsum.photos/seed/sct-o2a/1600/1000","https://picsum.photos/seed/sct-o2b/1600/1000","https://picsum.photos/seed/sct-o2c/1600/1000","https://picsum.photos/seed/sct-o2d/1600/1000","https://picsum.photos/seed/sct-o2e/1600/1000","https://picsum.photos/seed/sct-o2f/1200/1600","https://picsum.photos/seed/sct-o2g/1600/1000","https://picsum.photos/seed/sct-o2h/1600/1000","https://picsum.photos/seed/sct-o2i/1600/1000"]',
  '2026 年夏季建筑比赛的参赛作品合集，一共收到 19 份作品，这里挑出了 9 份做成图集。\n\n比赛主题是「水」，所以能看到大量围绕湖泊、瀑布和港口的设计。评审从创意、完成度和细节三个维度打分，最终由全体玩家投票选出前三名。\n\n第一名是一座下沉式的水下图书馆，第二名是海崖上的灯塔，第三名是渔村码头。\n\n下一届比赛的主题会在公告区提前两周公布，欢迎提前准备。',
  0, 1, '2026-09-02 20:15:00', 'demo'
),
(
  '红石教程：从入门到自动化', 'other',
  NULL,
  '["https://picsum.photos/seed/sct-o3a/1600/1000"]',
  '写给完全没接触过红石的新玩家的一份入门指引。\n\n第一阶段先搞懂三样东西：红石粉的传输距离（15 格）、中继器的作用（延时的同时还能延长信号）、以及比较器的两种模式。这三个搞清楚，大部分常见装置就都能看懂了。\n\n第二阶段推荐从「自动门」和「自动农场」这两个项目入手，它们用到的元件少，出问题也容易排查。\n\n第三阶段可以开始看 BUD、观察者这些进阶内容。\n\n服务器里每周三晚上有红石答疑，有不懂的直接在群里问就行。',
  0, 1, '2026-08-26 19:40:00', 'demo'
),
(
  '幕后：插件开发记录', 'other',
  'https://picsum.photos/seed/sct-o4a/1600/1000',
  '["https://picsum.photos/seed/sct-o4a/1600/1000","https://picsum.photos/seed/sct-o4b/1600/1000"]',
  '记录一下服务器自己写的几个插件，方便后来接手的人了解现状。\n\n目前主要有三个：一个是玩家数据统计（在线时长、方块破坏/放置数），一个是自动备份（每 2 小时一次，保留最近 12 份），还有一个是自定义的欢迎消息和称号系统。\n\n技术栈是 Paper API + MySQL，代码放在私有仓库里。后续计划把数据统计做成网页排行榜，接到官网这边来。',
  0, 0, '2026-08-09 17:20:00', 'demo'
);
