/*
 Navicat Premium Data Transfer

 Source Server         : 本机
 Source Server Type    : MySQL
 Source Server Version : 80032 (8.0.32)
 Source Host           : localhost:3306
 Source Schema         : newsdemo

 Target Server Type    : MySQL
 Target Server Version : 80032 (8.0.32)
 File Encoding         : 65001

 Date: 10/01/2024 08:55:43
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for leftmenu
-- ----------------------------
DROP TABLE IF EXISTS `leftmenu`;
CREATE TABLE `leftmenu`  (
  `leftmenuID` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '编号',
  `leftmenuName` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
  `leftmenuContent` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '内容',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
  PRIMARY KEY (`leftmenuID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '知识点信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of leftmenu
-- ----------------------------
INSERT INTO `leftmenu` VALUES ('0e345adfc6ec4374ba9ea2f8c772dbde', '社会责任', '2', '2024-01-10 03:45:14');
INSERT INTO `leftmenu` VALUES ('106abce68ed34d9fa8267be64b82633c', '学科介绍', '4', '2024-01-10 03:47:25');
INSERT INTO `leftmenu` VALUES ('4a5ff7ecb5e4442eb33bf3bfa1b4bc58', '组织机构', '2', '2024-01-10 03:44:27');
INSERT INTO `leftmenu` VALUES ('59ff62755d624a5cb28fcb8a085d5a80', '党建', '3', '2024-01-10 03:46:07');
INSERT INTO `leftmenu` VALUES ('6fe06c8512344a798a3294a572646728', '学院寄语', '2', '2024-01-10 03:43:51');
INSERT INTO `leftmenu` VALUES ('81c37bb7d9624368b45c32c3c1cdf598', '学院文化', '2', '2024-01-10 03:44:20');
INSERT INTO `leftmenu` VALUES ('924ee7fe00e942e1b70a98c8a2b297dc', '学院领导', '2', '2024-01-10 03:44:05');
INSERT INTO `leftmenu` VALUES ('967f9e94a4fd477ca68523a52f383726', '学科专业', '4', '2024-01-10 03:47:30');
INSERT INTO `leftmenu` VALUES ('a578f32f339d4089894483d87ac50538', '办学条件', '2', '2024-01-10 03:44:35');
INSERT INTO `leftmenu` VALUES ('d6d604431fbb4adfbb12c47e180800f6', '工会', '3', '2024-01-10 03:46:18');
INSERT INTO `leftmenu` VALUES ('e40cd143ed294d88b7fef9546e5f8883', '学院历史', '2', '2024-01-10 03:44:12');
INSERT INTO `leftmenu` VALUES ('f417c41be0274b8e9b349f0abb200689', '学院简介', '2', '2024-01-10 03:43:58');
INSERT INTO `leftmenu` VALUES ('f688870355154c17867fd434c24871ed', '共青团', '3', '2024-01-10 03:46:43');

-- ----------------------------
-- Table structure for news
-- ----------------------------
DROP TABLE IF EXISTS `news`;
CREATE TABLE `news`  (
  `newsID` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '编号',
  `newsName` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
  `newsContent` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '内容',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
  PRIMARY KEY (`newsID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '知识点信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of news
-- ----------------------------
INSERT INTO `news` VALUES ('1', '我院荣获“2023年度中国商学院MBA项目TOP100”第29名', '内容', '2023-12-25 00:00:00');
INSERT INTO `news` VALUES ('10', '我院两名教师荣获第二届“江苏省高等学校工程管理类专业教学名师、优秀教师”称号', '内容', '2023-11-24 00:00:00');
INSERT INTO `news` VALUES ('11', '校工会、商学院联合举办“劳模工匠进校园”系列活动', '内容', '2023-11-23 00:00:00');
INSERT INTO `news` VALUES ('12', '我院代表团参加AMBA & BGA 2023亚太年会', '内容', '2023-11-20 00:00:00');
INSERT INTO `news` VALUES ('13', '我院成功举办《中国宏观经济学》教材交流会暨人才培养和经济学科建设圆桌论坛', '内容', '2023-11-20 00:00:00');
INSERT INTO `news` VALUES ('14', '中国三峡集团一级咨询王绍锋来访商学院并作学术报告', '内容', '2023-11-19 00:00:00');
INSERT INTO `news` VALUES ('15', '我院举办河海大学第六届“国际青年学者论坛”商学院分论坛', '内容', '2023-11-01 00:00:00');
INSERT INTO `news` VALUES ('16', '台湾东吴大学阮金祥院长一行来访商学院', '内容', '2023-10-25 00:00:00');
INSERT INTO `news` VALUES ('17', '我院两位教师入选2023年版“全球前2%顶尖科学家榜”', '内容', '2023-10-17 00:00:00');
INSERT INTO `news` VALUES ('18', '我院成功举办MEM雇主论坛暨数字转型时代下的高端复合型人才培养研讨会', '内容', '2023-09-27 00:00:00');
INSERT INTO `news` VALUES ('19', '北京银行南京江宁开发区支行党支部与我院会计学系党支部开展共建活动', '内容', '2023-09-06 00:00:00');
INSERT INTO `news` VALUES ('2', '全国经管院校第二期《创业能力》示范课程工作坊在我院举行', '内容', '2023-12-22 00:00:00');
INSERT INTO `news` VALUES ('20', '“商”下同心共搬迁，“河”你携手启新篇丨我院首批学生入住河海大学常州新校区', '内容', '2023-09-05 00:00:00');
INSERT INTO `news` VALUES ('21', 'AACSB认证导师梁洨洁副教授到访我院指导认证工作', '内容', '2023-09-05 00:00:00');
INSERT INTO `news` VALUES ('22', '河北工业大学学术副校长刘炳胜一行来我院调研', '内容', '2023-09-02 00:00:00');
INSERT INTO `news` VALUES ('23', 'AAPEQ认证顾问组专家来我院指导MPAcc相关工作', '内容', '2023-08-31 00:00:00');
INSERT INTO `news` VALUES ('24', '第九届全国管理案例精英赛华东三赛区晋级赛在河海大学成功举办', '内容', '2023-08-31 00:00:00');
INSERT INTO `news` VALUES ('25', '安徽省池州市东至县委洪克峰书记一行来我院交流', '内容', '2023-08-28 00:00:00');
INSERT INTO `news` VALUES ('26', '我院与南京市江宁区建筑业协会签订人才培养战略合作协议', '内容', '2023-08-16 00:00:00');
INSERT INTO `news` VALUES ('27', '我院召开AOL质量保障体系建设研讨会', '内容', '2023-07-08 00:00:00');
INSERT INTO `news` VALUES ('28', '我院召开主题教育调研成果交流会', '内容', '2023-07-05 00:00:00');
INSERT INTO `news` VALUES ('29', '我院开展“牢记嘱托担使命，踔厉奋发启新程”毕业生党员专题党课', '内容', '2023-06-16 00:00:00');
INSERT INTO `news` VALUES ('3', '我院师生在第二届中国研究生工程管理案例大赛中取得佳绩', '内容', '2023-12-12 00:00:00');
INSERT INTO `news` VALUES ('30', '我院召开2023年招生就业工作专题推进会和职业发展与就业服务专题会', '内容', '2023-06-13 00:00:00');
INSERT INTO `news` VALUES ('4', '河海大学成功举办 2023 年江苏省研究生“绿色转型与高质量发展”学术创新论坛暨生态产品价值实现研讨会', '内容', '2023-12-08 00:00:00');
INSERT INTO `news` VALUES ('5', '我院参加2023年AACSB亚太年会', '内容', '2023-12-04 00:00:00');
INSERT INTO `news` VALUES ('6', '我院成功举办澜沧江-湄公河全流域合作研讨会', '内容', '2023-12-04 00:00:00');
INSERT INTO `news` VALUES ('7', '河海大学举办中国式现代化提供坚实人才战略支撑研讨会暨《河海大学人才研究40年》新书座谈会', '内容', '2023-12-03 00:00:00');
INSERT INTO `news` VALUES ('8', '杨桂山校长与联系班级商学院2020级信息管理与信息系统2班同学调研座谈', '内容', '2023-11-28 00:00:00');
INSERT INTO `news` VALUES ('9', '厦门临晟重工有限公司捐赠助力学院发展', '内容', '2023-11-24 00:00:00');

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice`  (
  `noticeID` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '编号',
  `noticeName` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
  `noticeContent` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '内容',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
  PRIMARY KEY (`noticeID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '知识点信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of notice
-- ----------------------------
INSERT INTO `notice` VALUES ('10', '学术讲座通知：地区人口老龄化与企业跨区域并购', '讲座内容...', '2023-12-12 00:00:00');
INSERT INTO `notice` VALUES ('11', '讲座通知：首席独立董事在审计委员会任职是否可以减少管理层机会主义的定性披露？', '讲座内容...', '2023-12-12 00:00:00');
INSERT INTO `notice` VALUES ('12', '学术讲座通知：“双碳”背景下新能源租赁业务应用与风险防控', '讲座内容...', '2023-11-21 00:00:00');
INSERT INTO `notice` VALUES ('13', '关于2023年第四次学位评定工作时间安排的通知', '通知内容...', '2023-11-14 00:00:00');
INSERT INTO `notice` VALUES ('14', '学术讲座通知：跟投制度研究：基于高管及员工参与IPO配售视角的经验证据', '讲座内容...', '2023-11-07 00:00:00');
INSERT INTO `notice` VALUES ('15', '商学院2023年岗位晋级工作情况公示', '公示内容...', '2023-11-04 00:00:00');
INSERT INTO `notice` VALUES ('17', '学术讲座通知：基于数字平台的社会风险沟通与治理模式、网络协同制造环境下的生产运营管理', '讲座内容...', '2023-11-01 00:00:00');
INSERT INTO `notice` VALUES ('18', '学术讲座通知：时间序列预测研究探讨、灰色调度模型与算法', '讲座内容...', '2023-11-01 00:00:00');
INSERT INTO `notice` VALUES ('19', '关于举行河海大学第六届“国际青年学者云论坛”商学院分论坛的通知', '通知内容...', '2023-10-30 00:00:00');
INSERT INTO `notice` VALUES ('2', '2024年上半年研究生拟申请学位有关事项的通知', '通知内容...', '2023-12-27 00:00:00');
INSERT INTO `notice` VALUES ('20', '河海大学博士学位论文答辩公告', '公告内容...', '2023-10-27 00:00:00');
INSERT INTO `notice` VALUES ('22', '关于做好2023年省社科基金后期资助项目申报工作的通知', '通知内容...', '2023-10-26 00:00:00');
INSERT INTO `notice` VALUES ('23', '2023年度国家社会科学基金重大项目招标公告', '公告内容...', '2023-10-26 00:00:00');
INSERT INTO `notice` VALUES ('24', '关于申报2023年度江苏省高等学校科学技术研究成果奖的通知', '通知内容...', '2023-10-23 00:00:00');
INSERT INTO `notice` VALUES ('25', '学术讲座通知: 组织行为中的研究问题发现与理论框架构建', '讲座内容...', '2023-10-23 00:00:00');
INSERT INTO `notice` VALUES ('26', '河海大学商学院、经济与金融学院2024年接收推荐免试研究生建议录取名单公示', '公示内容...', '2023-10-19 00:00:00');
INSERT INTO `notice` VALUES ('27', '关于选派学生参加我校与澳大利亚西澳大学联合举办寒假Uni-Link短期学分课程项目的通知', '通知内容...', '2023-10-17 00:00:00');
INSERT INTO `notice` VALUES ('28', '学术讲座通知：管理科学研究立项方法探讨', '讲座内容...', '2023-10-16 00:00:00');
INSERT INTO `notice` VALUES ('29', '学术讲座通知：汽车MTO再制造案例中的生产计划', '讲座内容...', '2023-10-16 00:00:00');
INSERT INTO `notice` VALUES ('3', '商学院2024年学术学位博士研究生申请-考核制招生工作细则', '细则内容...', '2023-12-30 00:00:00');
INSERT INTO `notice` VALUES ('30', '学术讲座通知：考虑附加服务的平台兼容策略的竞合博弈分析', '讲座内容...', '2023-10-16 00:00:00');
INSERT INTO `notice` VALUES ('4', '讲座通知：地区文化遵从度与企业ESG表现——中国背景下的研究', '讲座内容...', '2024-01-04 00:00:00');
INSERT INTO `notice` VALUES ('5', '商学院2024年硕博连读研究生（第一批）考核细则', '细则内容...', '2024-01-02 00:00:00');
INSERT INTO `notice` VALUES ('6', '学术讲座通知：脆弱性视角下异质应急储备库选址—分配研究', '讲座内容...', '2023-12-26 00:00:00');
INSERT INTO `notice` VALUES ('7', '学术讲座通知：面向外来物种入侵的管理资源配置优化研究', '讲座内容...', '2023-12-26 00:00:00');
INSERT INTO `notice` VALUES ('8', '讲座通知: 冲突分析图模型前沿进展', '讲座内容...', '2023-12-14 00:00:00');
INSERT INTO `notice` VALUES ('9', '讲座通知：金融行业审计实务前沿及审计人才职业规划', '讲座内容...', '2023-12-14 00:00:00');

-- ----------------------------
-- Table structure for topmenu
-- ----------------------------
DROP TABLE IF EXISTS `topmenu`;
CREATE TABLE `topmenu`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `topmenuName` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
  `topmenuContent` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '内容',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '知识点信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of topmenu
-- ----------------------------
INSERT INTO `topmenu` VALUES (1, '首页', '/', '2024-01-10 03:40:06');
INSERT INTO `topmenu` VALUES (2, '关于我们', '/', '2024-01-10 03:40:12');
INSERT INTO `topmenu` VALUES (3, '党建群团', '/', '2024-01-10 03:40:18');
INSERT INTO `topmenu` VALUES (4, '学科发展', '/', '2024-01-10 03:40:57');
INSERT INTO `topmenu` VALUES (5, '教育项目', '/', '2024-01-10 03:41:05');
INSERT INTO `topmenu` VALUES (6, '师资队伍', '/', '2024-01-10 03:41:09');
INSERT INTO `topmenu` VALUES (7, '合作交流', '/', '2024-01-10 03:41:14');
INSERT INTO `topmenu` VALUES (8, '学生发展', '/', '2024-01-10 03:41:34');
INSERT INTO `topmenu` VALUES (9, '校友之家', '/', '2024-01-10 03:41:40');
INSERT INTO `topmenu` VALUES (10, '招聘招生', '/', '2024-01-10 03:41:44');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `uname` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `password` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `role` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `valid` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'ad', 'jiangsu', '123456', '管理员', '有效');
INSERT INTO `user` VALUES (2, 'lisi', '上海', '123456aa', '管理员', '有效');
INSERT INTO `user` VALUES (3, 'admin', '44443', '1', '管理员', '有效');

SET FOREIGN_KEY_CHECKS = 1;
