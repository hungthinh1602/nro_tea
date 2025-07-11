/*
 Navicat Premium Data Transfer

 Source Server         : cauberong
 Source Server Type    : MySQL
 Source Server Version : 100432
 Source Host           : localhost:3306
 Source Schema         : 2024

 Target Server Type    : MySQL
 Target Server Version : 100432
 File Encoding         : 65001

 Date: 25/12/2024 17:18:07
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for account
-- ----------------------------
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `email` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `create_time` timestamp(0) NULL DEFAULT current_timestamp(),
  `update_time` timestamp(0) NULL DEFAULT current_timestamp(),
  `ban` tinyint(1) NOT NULL DEFAULT 0,
  `is_admin` tinyint(1) NOT NULL DEFAULT 0,
  `last_time_login` timestamp(0) NOT NULL DEFAULT '2002-07-31 00:00:00',
  `last_time_logout` timestamp(0) NOT NULL DEFAULT '2002-07-31 00:00:00',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `active` int(11) NOT NULL DEFAULT 1,
  `thoi_vang` int(11) NOT NULL DEFAULT 0,
  `server_login` int(11) NOT NULL DEFAULT -1,
  `bd_player` double NULL DEFAULT 1,
  `is_gift_box` tinyint(1) NULL DEFAULT 0,
  `gift_time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0',
  `reward` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `cash` int(11) NOT NULL DEFAULT 0,
  `danap` int(11) NOT NULL DEFAULT 0,
  `token` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `xsrf_token` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `newpass` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `luotquay` int(11) NOT NULL DEFAULT 0,
  `vang` bigint(20) NOT NULL DEFAULT 0,
  `event_point` int(11) NOT NULL DEFAULT 0,
  `vip` int(11) NOT NULL DEFAULT 0,
  `sotien` int(11) NOT NULL DEFAULT 0,
  `diem_da_nhan` int(11) NOT NULL DEFAULT 0,
  `hasReceivedVIP` int(11) NOT NULL DEFAULT 0,
  `lastTimeReceivedVIP` bigint(20) NOT NULL DEFAULT 0,
  `coin` int(11) NOT NULL DEFAULT 0,
  `gioithieu` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1013218 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for clan
-- ----------------------------
DROP TABLE IF EXISTS `clan`;
CREATE TABLE `clan`  (
  `id` int(11) NOT NULL,
  `NAME` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `NAME_2` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `slogan` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
  `img_id` int(11) NOT NULL DEFAULT 0,
  `power_point` bigint(20) NOT NULL DEFAULT 0,
  `max_member` smallint(6) NOT NULL DEFAULT 10,
  `clan_point` int(11) NOT NULL DEFAULT 0,
  `LEVEL` int(11) NOT NULL DEFAULT 1,
  `members` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `tops` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `create_time` timestamp(0) NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for event
-- ----------------------------
DROP TABLE IF EXISTS `event`;
CREATE TABLE `event`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of event
-- ----------------------------
INSERT INTO `event` VALUES (1, 'LUNNAR_NEW_YEAR', '{\"damePrecent\":0,\"hpPrecent\":0,\"mpPrecent\":0,\"papPrecent\":0}');

-- ----------------------------
-- Table structure for giftcode
-- ----------------------------
DROP TABLE IF EXISTS `giftcode`;
CREATE TABLE `giftcode`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `count_left` int(11) NOT NULL,
  `detail` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `allGender` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `datecreate` timestamp(0) NOT NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0),
  `expired` timestamp(0) NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of giftcode
-- ----------------------------
INSERT INTO `giftcode` VALUES (1, 'caubevuive', 98776, '[{\"id\":343,\"quantity\":3,\"options\":[{\"param\":0,\"id\":83}]},{\"id\":821,\"quantity\":7,\"options\":[{\"param\":0,\"id\":30}]},{\"id\":380,\"quantity\":20,\"options\":[{\"param\":0,\"id\":30}]}]', 'all', '2024-12-25 05:55:46', '2025-12-20 06:39:08');

-- ----------------------------
-- Table structure for history_transaction
-- ----------------------------
DROP TABLE IF EXISTS `history_transaction`;
CREATE TABLE `history_transaction`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `player_1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `player_2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `item_player_1` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `item_player_2` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `bag_1_before_tran` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `bag_2_before_tran` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `bag_1_after_tran` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `bag_2_after_tran` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `time_tran` timestamp(0) NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29485 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player
-- ----------------------------
DROP TABLE IF EXISTS `player`;
CREATE TABLE `player`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account_id` int(11) NULL DEFAULT NULL,
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `head` int(11) NOT NULL DEFAULT 102,
  `gender` int(11) NOT NULL,
  `have_tennis_space_ship` tinyint(1) NULL DEFAULT 0,
  `clan_id` int(11) NOT NULL DEFAULT -1,
  `data_inventory` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `data_location` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `data_point` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `data_magic_tree` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `items_body` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `items_bag` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `items_box` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `items_box_lucky_round` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `items_daban` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `friends` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `enemies` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `data_intrinsic` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `data_item_time` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `data_task` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `data_mabu_egg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `data_charm` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `skills` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `skills_shortcut` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `pet` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `data_black_ball` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `data_side_task` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `create_time` timestamp(0) NOT NULL DEFAULT current_timestamp(),
  `notify` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL,
  `baovetaikhoan` varchar(1000) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT '[]',
  `captcha` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '[]',
  `data_card` varchar(10000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '[]',
  `lasttimepkcommeson` bigint(20) NOT NULL DEFAULT 0,
  `bandokhobau` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '[]',
  `conduongrandoc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '[]',
  `doanhtrai` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '[]',
  `masterDoesNotAttack` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `nhanthoivang` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '[]',
  `ruonggo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '[]',
  `sieuthanthuy` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '[]',
  `vodaisinhtu` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '[]',
  `rongxuong` bigint(20) NOT NULL DEFAULT 0,
  `data_item_event` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '[]',
  `data_luyentap` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `data_clan_task` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '[]',
  `data_vip` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `rank` int(11) NOT NULL DEFAULT 0,
  `data_achievement` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `giftcode` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `danh_hieu_shop` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '[0,0,0,0,0,0,0,0]',
  `data_clan` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `firstTimeLogin` timestamp(0) NOT NULL DEFAULT current_timestamp(),
  `buarandom` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '[1]',
  `dien_sukien` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '[0]',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `account_id`(`account_id`) USING BTREE,
  CONSTRAINT `player_ibfk_1` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1012775 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for recharge
-- ----------------------------
DROP TABLE IF EXISTS `recharge`;
CREATE TABLE `recharge`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account_id` varchar(999) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `code` varchar(999) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `serial` varchar(999) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `amount` varchar(999) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `type` varchar(999) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `tranid` varchar(999) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `amount_real` varchar(999) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `status` int(11) NOT NULL,
  `time` varchar(999) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sell_item
-- ----------------------------
DROP TABLE IF EXISTS `sell_item`;
CREATE TABLE `sell_item`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `price` int(11) NOT NULL,
  `item` int(11) NOT NULL,
  `slot` int(11) NOT NULL,
  `options` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `users_buy` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `status` int(11) NOT NULL,
  `time` varchar(999) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for shop_ky_gui
-- ----------------------------
DROP TABLE IF EXISTS `shop_ky_gui`;
CREATE TABLE `shop_ky_gui`  (
  `id` int(11) NOT NULL,
  `player_id` int(11) NOT NULL,
  `tab` int(11) NOT NULL,
  `item_id` int(11) NOT NULL,
  `gold` int(11) NOT NULL,
  `gem` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  `itemOption` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `lasttime` bigint(50) NOT NULL,
  `isBuy` int(11) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for super_rank
-- ----------------------------
DROP TABLE IF EXISTS `super_rank`;
CREATE TABLE `super_rank`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `player_id` int(11) NOT NULL,
  `name` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `rank` int(11) NOT NULL,
  `last_pk_time` bigint(20) NOT NULL,
  `last_reward_time` bigint(20) NOT NULL,
  `ticket` int(11) NOT NULL,
  `win` int(11) NOT NULL,
  `lose` int(11) NOT NULL,
  `history` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `received` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2766 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of super_rank
-- ----------------------------
INSERT INTO `super_rank` VALUES (2763, 1011086, 'admin', 1, 1735116245620, 1735116245813, 3, 0, 0, '[]', '{\"head\":1491,\"def\":400,\"hp\":3425358,\"dame\":1598509,\"body\":1492,\"leg\":1493}', 0);
INSERT INTO `super_rank` VALUES (2764, 1011087, 'kakarot', 2, 1735120399771, 1735120399885, 3, 0, 0, '[]', '{\"head\":126,\"def\":3,\"hp\":132,\"dame\":154,\"body\":16,\"leg\":17}', 0);
INSERT INTO `super_rank` VALUES (2765, 1011090, 'picolo', 3, 1735120588713, 1735120588817, 3, 0, 0, '[]', '{\"head\":810,\"def\":2,\"hp\":14191,\"dame\":5457,\"body\":811,\"leg\":812}', 0);

-- ----------------------------
-- Table structure for task_main_template
-- ----------------------------
DROP TABLE IF EXISTS `task_main_template`;
CREATE TABLE `task_main_template`  (
  `id` int(11) NOT NULL,
  `NAME` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `detail` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of task_main_template
-- ----------------------------
INSERT INTO `task_main_template` VALUES (0, 'Nhiệm vụ đầu tiên', 'Chi tiết nhiệm vụ');
INSERT INTO `task_main_template` VALUES (1, 'Nhiệm vụ tập luyện', 'Mộc nhân được đặt nhiều tại %1, ngay trước nhà %2\nHãy đánh ngã 5 mộc nhân, \nsau đó quay về nhà báo cáo với ông %2\nĐể đánh, hãy chạm nhanh 2 lần vào đối tượng\nThưởng 1000 sức mạnh\nThưởng 1000 tiềm năng\nThưởng 200tr vàng');
INSERT INTO `task_main_template` VALUES (2, 'Nhiệm vụ tìm thức ăn', 'Tìm đến %3, tiêu diệt bọn quái %4 và nhặt về 10 đùi gà\nThưởng 1500 sức mạnh\nThưởng 1500 tiềm năng\nThưởng 300tr vàng\nHọc được kỹ năng bay');
INSERT INTO `task_main_template` VALUES (3, 'Nhiệm vụ sao băng', 'Đi khám phá xem vật thể lạ vừa rơi xuống hành tinh\nThưởng 2000 sức mạnh\nThưởng 2000 tiềm năng\nThưởng 400tr vàng');
INSERT INTO `task_main_template` VALUES (4, 'Nhiệm vụ thử thách', 'Khủng long mẹ sống tại Trái Đất\nLợn lòi mẹ sống tại Namếc\nQuỷ đất mẹ sống tại Xayda\nDùng tàu vũ trụ để di chuyển sang hành\nkhác\n-Thưởng 4.000 sức mạnh\n-Thưởng 4.000 tiềm năng');
INSERT INTO `task_main_template` VALUES (5, 'Nhiệm vụ thử thách', 'Lợn lòi mẹ sống tại Namếc\nKhủng long mẹ sống tại Trái Đất\nQuỷ đất mẹ sống tại Xayda\nDùng tàu vũ trụ để di chuyển sang hành\nkhác\n-Thưởng 4.000 sức mạnh\n-Thưởng 4.000 tiềm năng');
INSERT INTO `task_main_template` VALUES (6, 'Nhiệm vụ thử thách', 'Quỷ đất mẹ sống tại Xayda\nKhủng long mẹ sống tại Trái Đất\nLợn lòi mẹ sống tại Namếc\nDùng tàu vũ trụ để di chuyển sang hành\nkhác\n-Thưởng 4.000 sức mạnh\n-Thưởng 4.000 tiềm năng');
INSERT INTO `task_main_template` VALUES (7, 'Nhiệm vụ giải cứu', 'Đến khu vực %13,\nHạ 20 con %9\n- Thưởng 8.000 sức mạnh\n- Thưởng 8.000 tiềm năng');
INSERT INTO `task_main_template` VALUES (8, 'Nhiệm vụ tìm ngọc', 'Ngọc rồng 7 sao đang bị bọn\n%14 cướp đi.\nĐánh bại chúng để tìm lại.\n- Thưởng 15.000 sức mạnh\n- Thưởng 15.000 tiềm năng');
INSERT INTO `task_main_template` VALUES (9, 'Nhiệm vụ bái sư', 'Tìm đường tới %11, trò chuyện với %10 và xin làm đệ tử');
INSERT INTO `task_main_template` VALUES (10, 'Nhiệm vụ thử sức', 'Tiêu diệt %12 thể hiện sức mạnh cho %10 thấy');
INSERT INTO `task_main_template` VALUES (11, 'Nhiệm vụ làm quen các Npc', 'Hãy gặp và làm quen với các Npc để biết chức năng của họ');
INSERT INTO `task_main_template` VALUES (12, 'Nhiệm vụ xin phép', 'Quay về nhà xin %2 cho phép lên đường trở thành chiến binh');
INSERT INTO `task_main_template` VALUES (13, 'Nhiệm vụ nhận quà', 'Gặp Npc Quy Lão nhận ngay đuôi khỉ đi úp cho phê');
INSERT INTO `task_main_template` VALUES (14, 'Nhiệm vụ bang hội đầu tiên', 'Tiến trình sẽ nhanh gấp đôi nếu cùng phối hợp với 1 người đồng đội lên đường làm nhiệm vụ\r\nGợi ý:\r\nHeo rừng xuất hiện tại rừng Bamboo\r\nHeo da xanh xuất \r\nhiện tại núi hoa vàng\r\nHeo xayda xuất hiện tại rừng cọ\r\nHãy tới trạm tàu vũ trụ để có thể di chuyển qua các map');
INSERT INTO `task_main_template` VALUES (15, 'Nhiệm vụ bang hội thứ 2', 'Tiến trình sẽ nhanh gấp đôi nếu cùng phối hợp với 1 người đồng đội lên đường làm nhiệm vụ');
INSERT INTO `task_main_template` VALUES (16, 'Nhiệm vụ tiêu diệt quái vật', 'Tới các hành tinh tiêu diệt quái vật, giải cứu thường dân');
INSERT INTO `task_main_template` VALUES (17, 'Nhiệm vụ giúp đỡ Cui', 'Tìm đường tới thành phố Vegeta, gặp và nói chuyện với Cui');
INSERT INTO `task_main_template` VALUES (18, 'Nhiệm vụ bất khả thi', 'Đạt 50 triệu sức mạnh\nTiêu diệt bọn tay sai của Fide tại Xayda\n- Thưởng 50.000.000 sức mạnh\n- Thưởng 50.000.000 tiềm năng');
INSERT INTO `task_main_template` VALUES (19, 'Nhiệm vụ tìm diệt đệ tử', 'Tiêu diệt bọn đệ tử Kuku, Mập Đầu Đinh,\nRambo của Fide đại ca tại Xayda\nCui có thể biết vị trí của chúng, nếu tìm\nkhông thấy hãy đến gặp Cui tại thành\nphố Vegeta\n- Thưởng 20.000.000 sức mạnh\n- Thưởng 20.000.000 tiềm năng');
INSERT INTO `task_main_template` VALUES (20, 'Nhiệm vụ Tiểu đội sát thủ', 'Tiêu diệt Tiểu Đội Sát Thủ do Fide đại\nca gọi đến tại Xayda\n- Thưởng 20.000.000 sức mạnh\n- Thưởng 20.000.000 tiềm năng');
INSERT INTO `task_main_template` VALUES (21, 'Nhiệm vụ chạm trán Fide đại ca', 'Luyện tập đạt 2 tỷ sức mạnh\nLên đường tìm diệt Fide đại ca');
INSERT INTO `task_main_template` VALUES (22, 'Chú bé đến từ tương lai', 'Đến trái đất, rừng bamboo, rừng dương\nxỉ, nam Kamê tìm người lạ\nĐến đảo rùa đưa thuốc cho Quy Lão\nTheo Ca Lích đến tương lai\nGiúp họ diệt bọn bọ hung con\n- Thưởng 1.000.000 sức mạnh\n- Thưởng 1.000.000 tiềm năng');
INSERT INTO `task_main_template` VALUES (23, 'Chạm chán Robot sát thủ lần 1', 'Hãy đến thành phố phía nam\nđảo balê hoặc cao nguyên\nCùng 2 đồng bang diệt 1300 Xên con cấp 3\nBáo với Bunma tương lai\n- Thưởng 1.000.000 sức mạnh\n- Thưởng 1.000.000 tiềm năng');
INSERT INTO `task_main_template` VALUES (24, 'Chạm trán Robot sát thủ lần 2', 'Trở về quá khứ, đến sân sau siêu thị\nTiêu diệt bọn Rôbốt sát thủ\nBáo với Bunma tương lai\n- Thưởng 1.000.000 sức mạnh\n- Thưởng 1.000.000 tiềm năng');
INSERT INTO `task_main_template` VALUES (25, 'Chạm trán Robot sát thủ lần 3', 'Đến thành phố, ngọn núi, thung lũng phía Bắc\nTiêu diệt bọn Rôbốt sát thủ\nCùng 2 đồng bang diệt 1500 Xên con cấp 5\nBáo với Bunma tương lai\n- Thưởng 1.000.000 sức mạnh\n- Thưởng 1.000.000 tiềm năng');
INSERT INTO `task_main_template` VALUES (26, 'Chạm trán Xên bọ hung', 'Đến thị trấn Ginder\nTiêu diệt Xên Bọ Hung cấp 1\nTiêu diệt Xên Bọ Hung cấp 2\nTiêu diệt Xên Bọ Hung hoàn thiện\nCùng 2 đồng bang diệt 1500 Xên con cấp 8\nBáo với Bunma tương lai\n- Thưởng 1.000.000 sức mạnh\n- Thưởng 1.000.000 tiềm năng');
INSERT INTO `task_main_template` VALUES (27, 'Cuộc dạo chơi của xên', 'Cẩn thận !!!\nNhững vị khách không mời mà tới\nthường tỏ ra nguy hiểm');
INSERT INTO `task_main_template` VALUES (28, 'Cuộc đối đầu không cân sức', 'Vào lúc 12h trưa các ngày, bạn đến gặp NPC Ô sin tại map Đại hội võ thuật.');
INSERT INTO `task_main_template` VALUES (29, 'Hành tinh băng giá', 'Đến Hang Băng\nTiêu diệt cooler');
INSERT INTO `task_main_template` VALUES (30, 'Hành tinh ngục tù', 'Hành tinh ngục tù');
INSERT INTO `task_main_template` VALUES (31, 'Truyền Thuyết Về Trùm Server', 'Ai mới là trùm server?');
INSERT INTO `task_main_template` VALUES (32, 'Truyền thuyết về Siêu Xayda Huyền Thoại', 'Ai mới thật sự là siêu xayda huyền thoại mà Fide từng nhắc tới ?\nThưởng 10 Tr sức mạnh\nThưởng 10 Tr tiềm năng');
INSERT INTO `task_main_template` VALUES (33, 'Ta sẽ trở thành TOP nhiệm vụ Server NRO Xưa', 'Cần cù bù thông minh');
INSERT INTO `task_main_template` VALUES (34, 'Làm nhiệm vụ, làm nhiệm vụ nữa, làm nhiệm vụ mãi', 'Đừng lo lắng rồi mọi chuyện cũng sẽ như đb mà thôi');

SET FOREIGN_KEY_CHECKS = 1;
