-- --------------------------------------------------------
-- 主机:                           127.0.0.1
-- 服务器版本:                        5.7.9 - MySQL Community Server (GPL)
-- 服务器操作系统:                      Win64
-- HeidiSQL 版本:                  12.10.0.7000
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- 正在导出表  healthkiosk.corpus 的数据：~17 rows (大约)
DELETE FROM `corpus`;
INSERT INTO `corpus` (`id`, `request`, `response`) VALUES
	(1, '你好', '你好');
INSERT INTO `corpus` (`id`, `request`, `response`) VALUES
	(2, '体检流程是什么？', '体检流程包括登录、逐项检测、提交结果，完成后可在后台查看。');
INSERT INTO `corpus` (`id`, `request`, `response`) VALUES
	(3, '设备怎么使用？', '根据屏幕提示操作，将手指或手臂放置在指定位置即可。');
INSERT INTO `corpus` (`id`, `request`, `response`) VALUES
	(4, '需要多久？', '完整体检通常需要 3 至 5 分钟。');
INSERT INTO `corpus` (`id`, `request`, `response`) VALUES
	(5, '数据会保存吗？', '所有体检数据都会自动上传并保存到后台系统。');
INSERT INTO `corpus` (`id`, `request`, `response`) VALUES
	(6, '公司能看到我的体检吗？', '是的，后台管理员可查看你的体检记录用于健康管理。');
INSERT INTO `corpus` (`id`, `request`, `response`) VALUES
	(7, '网络出问题怎么办？', '若网络异常，设备会提示稍后再试，可联系维护人员。');
INSERT INTO `corpus` (`id`, `request`, `response`) VALUES
	(8, '为什么要登录？', '登录用于确认司机身份，确保体检数据准确归属。');
INSERT INTO `corpus` (`id`, `request`, `response`) VALUES
	(9, '忘记登录信息怎么办？', '请联系管理员重置或确认你的登录方式。');
INSERT INTO `corpus` (`id`, `request`, `response`) VALUES
	(10, '体检后能看到报告吗？', '你可以在后台或管理端查看已生成的体检报告。');
INSERT INTO `corpus` (`id`, `request`, `response`) VALUES
	(11, '设备噪音正常吗？', '轻微运行声属于正常现象，如有异常震动请联系维护人员。');
INSERT INTO `corpus` (`id`, `request`, `response`) VALUES
	(12, '体检结束后结果去哪看', '体检数据会同步到后台系统，本人和管理人员都能看到');
INSERT INTO `corpus` (`id`, `request`, `response`) VALUES
	(13, '如果传感器坏了怎么办', '可以联系维护人员，会进行设备检修或更换传感器');
INSERT INTO `corpus` (`id`, `request`, `response`) VALUES
	(14, '我能跳过某一项体检吗', '为了安全规定，体检项目需要全部完成才能提交');
INSERT INTO `corpus` (`id`, `request`, `response`) VALUES
	(15, '指脉氧一直测不出来怎么办', '可以把手指擦干，换另一只手再试试');
INSERT INTO `corpus` (`id`, `request`, `response`) VALUES
	(16, '后台多久能看到我今天的数据', '上传成功后是实时更新的');
INSERT INTO `corpus` (`id`, `request`, `response`) VALUES
	(17, '体检机会不会上传我的隐私信息', '系统只上传必要的体检数据，个人信息会严格加密保护');

-- 正在导出表  healthkiosk.data 的数据：~6 rows (大约)
DELETE FROM `data`;
INSERT INTO `data` (`id`, `user_id`, `tempor`, `alcohol`, `ecg`, `spo2`, `ppg`, `blood_sys`, `blood_dia`, `blood_hr`, `date`, `time`) VALUES
	(24, 'admin', 36.5, 0, '/ecg/002.ecg', 88, 75, 92, 58, 55, '2024-11-24', 2);
INSERT INTO `data` (`id`, `user_id`, `tempor`, `alcohol`, `ecg`, `spo2`, `ppg`, `blood_sys`, `blood_dia`, `blood_hr`, `date`, `time`) VALUES
	(26, 'admin', 37.1, 0, '/ecg/004.ecg', 96, 69, 130, 101, 42, '2024-11-26', 2);
INSERT INTO `data` (`id`, `user_id`, `tempor`, `alcohol`, `ecg`, `spo2`, `ppg`, `blood_sys`, `blood_dia`, `blood_hr`, `date`, `time`) VALUES
	(27, 'admin', 36.6, 0, '/ecg/005.ecg', 98, 20, 118, 77, 75, '2024-11-27', 1);
INSERT INTO `data` (`id`, `user_id`, `tempor`, `alcohol`, `ecg`, `spo2`, `ppg`, `blood_sys`, `blood_dia`, `blood_hr`, `date`, `time`) VALUES
	(29, 'admin', 36.9, 0, '/ecg/007.ecg', 105, 76, 120, 80, 128, '2024-11-29', 1);
INSERT INTO `data` (`id`, `user_id`, `tempor`, `alcohol`, `ecg`, `spo2`, `ppg`, `blood_sys`, `blood_dia`, `blood_hr`, `date`, `time`) VALUES
	(30, 'admin', 36.4, 0, '/ecg/008.ecg', 89, 71, 80, 50, 40, '2024-11-30', 2);
INSERT INTO `data` (`id`, `user_id`, `tempor`, `alcohol`, `ecg`, `spo2`, `ppg`, `blood_sys`, `blood_dia`, `blood_hr`, `date`, `time`) VALUES
	(31, 'admin', 36.8, 5, '/ecg/009.ecg', 100, 78, 125, 82, 170, '2024-12-01', 1);

-- 正在导出表  healthkiosk.device 的数据：~2 rows (大约)
DELETE FROM `device`;
INSERT INTO `device` (`version`, `description`, `type`, `time`, `num`) VALUES
	('v0.1.0-alpha', '开发版', '1', '2025-10-08 11:51:59', 2);
INSERT INTO `device` (`version`, `description`, `type`, `time`, `num`) VALUES
	('v0.1.1-beta', '可用的版本', '1', '2025-10-16 15:22:02', 0);

-- 正在导出表  healthkiosk.function 的数据：~18 rows (大约)
DELETE FROM `function`;
INSERT INTO `function` (`function_id`, `function_key`, `name`, `remark`, `parent`, `class`) VALUES
	(1, 'users', '用户管理', '用户管理模块', NULL, 'menu');
INSERT INTO `function` (`function_id`, `function_key`, `name`, `remark`, `parent`, `class`) VALUES
	(2, 'users:list', '用户列表', '用户列表', 1, 'sub');
INSERT INTO `function` (`function_id`, `function_key`, `name`, `remark`, `parent`, `class`) VALUES
	(3, 'app', '软件管理', '软件管理模块', NULL, 'menu');
INSERT INTO `function` (`function_id`, `function_key`, `name`, `remark`, `parent`, `class`) VALUES
	(4, 'app:list', '软件列表', '软件列表', 3, 'sub');
INSERT INTO `function` (`function_id`, `function_key`, `name`, `remark`, `parent`, `class`) VALUES
	(5, 'examdata', '体检数据', '体检数据模块', NULL, 'menu');
INSERT INTO `function` (`function_id`, `function_key`, `name`, `remark`, `parent`, `class`) VALUES
	(6, 'examdata:list', '体检数据趋势', '体检数据趋势', 5, 'sub');
INSERT INTO `function` (`function_id`, `function_key`, `name`, `remark`, `parent`, `class`) VALUES
	(7, 'info', '个人信息', '个人信息模块', NULL, 'menu');
INSERT INTO `function` (`function_id`, `function_key`, `name`, `remark`, `parent`, `class`) VALUES
	(8, 'info:selfinfo', '个人信息设置', '展示和修改个人信息', 7, 'sub');
INSERT INTO `function` (`function_id`, `function_key`, `name`, `remark`, `parent`, `class`) VALUES
	(9, 'info:selfexam', '个人体检记录', '查询个人体检记录', 7, 'sub');
INSERT INTO `function` (`function_id`, `function_key`, `name`, `remark`, `parent`, `class`) VALUES
	(10, 'item', '体检项目模块', '体检项目模块', NULL, 'menu');
INSERT INTO `function` (`function_id`, `function_key`, `name`, `remark`, `parent`, `class`) VALUES
	(11, 'item:list', '体检项目列表', '体检项目列表', 10, 'sub');
INSERT INTO `function` (`function_id`, `function_key`, `name`, `remark`, `parent`, `class`) VALUES
	(12, 'use', '使用说明', '使用说明模块', NULL, 'menu');
INSERT INTO `function` (`function_id`, `function_key`, `name`, `remark`, `parent`, `class`) VALUES
	(13, 'use:instruction', '用户使用说明', '为用户提供系统使用说明', 12, 'sub');
INSERT INTO `function` (`function_id`, `function_key`, `name`, `remark`, `parent`, `class`) VALUES
	(14, 'use:about', '关于系统', '系统基本说明', 12, 'sub');
INSERT INTO `function` (`function_id`, `function_key`, `name`, `remark`, `parent`, `class`) VALUES
	(15, 'use:admin', '管理员使用说明', '为管理员提供管理说明', 12, 'sub');
INSERT INTO `function` (`function_id`, `function_key`, `name`, `remark`, `parent`, `class`) VALUES
	(16, 'role', '权限模块', '权限模块', NULL, 'menu');
INSERT INTO `function` (`function_id`, `function_key`, `name`, `remark`, `parent`, `class`) VALUES
	(17, 'users:role', '角色管理', '角色管理', 1, 'sub');
INSERT INTO `function` (`function_id`, `function_key`, `name`, `remark`, `parent`, `class`) VALUES
	(18, 'users:permission', '权限分配', '权限分配', 1, 'sub');
INSERT INTO `function` (`function_id`, `function_key`, `name`, `remark`, `parent`, `class`) VALUES
	(19, 'info:selfAnalyze', '数据分析', '使用LLM分析个人体检数据', 7, 'sub');
INSERT INTO `function` (`function_id`, `function_key`, `name`, `remark`, `parent`, `class`) VALUES
	(20, 'info:selfLLM', 'AI助手', 'AI助手', 7, 'sub');

-- 正在导出表  healthkiosk.ip 的数据：~5 rows (大约)
DELETE FROM `ip`;
INSERT INTO `ip` (`ip`, `name`, `identity`) VALUES
	('192.168.0.100', 'lrz', '开发人员');
INSERT INTO `ip` (`ip`, `name`, `identity`) VALUES
	('192.168.0.101', 'lmf', '开发人员');
INSERT INTO `ip` (`ip`, `name`, `identity`) VALUES
	('192.168.0.102', 'hhj', '开发人员');
INSERT INTO `ip` (`ip`, `name`, `identity`) VALUES
	('192.168.0.103', 'Android', '体检机用户');
INSERT INTO `ip` (`ip`, `name`, `identity`) VALUES
	('192.168.0.0', 'tp-link', '路由器');

-- 正在导出表  healthkiosk.item 的数据：~9 rows (大约)
DELETE FROM `item`;
INSERT INTO `item` (`id`, `name`, `abbreviation`, `status`, `description`, `usage`) VALUES
	(1, '体温', 'tempor', 1, '用于测量体表温度', '<p>靠近安卓屏幕摄像头，将<strong>自动</strong>进行体温测量，并显示体表温度。</p>');
INSERT INTO `item` (`id`, `name`, `abbreviation`, `status`, `description`, `usage`) VALUES
	(2, '酒精', 'alcohol', 1, '测量酒精含量', '<p><br></p>');
INSERT INTO `item` (`id`, `name`, `abbreviation`, `status`, `description`, `usage`) VALUES
	(3, '心电', 'ecg', 1, '', '');
INSERT INTO `item` (`id`, `name`, `abbreviation`, `status`, `description`, `usage`) VALUES
	(4, '血氧', 'spo2', 1, '', '');
INSERT INTO `item` (`id`, `name`, `abbreviation`, `status`, `description`, `usage`) VALUES
	(5, '光电容积脉搏波', 'ppg', 1, '测量脉搏', '<p><br></p>');
INSERT INTO `item` (`id`, `name`, `abbreviation`, `status`, `description`, `usage`) VALUES
	(6, '收缩压', 'blood_sys', 1, '测量血压', '<p><br></p>');
INSERT INTO `item` (`id`, `name`, `abbreviation`, `status`, `description`, `usage`) VALUES
	(7, '舒张压', 'blood_dia', 1, '', '');
INSERT INTO `item` (`id`, `name`, `abbreviation`, `status`, `description`, `usage`) VALUES
	(8, '心率', 'blood_hr', 1, '', '');

-- 正在导出表  healthkiosk.news 的数据：~5 rows (大约)
DELETE FROM `news`;
INSERT INTO `news` (`id`, `publish`, `content`, `publish_time`, `update_time`, `status`) VALUES
	(1, 'admin', '<h2>自助体检机系统重大升级公告</h2>\r\n<p>为了进一步提升铁路司机朋友们的体检体验，提高体检数据的精确度与可读性，我们于近日完成了对自助体检机的全面升级。本次升级主要面向血压监测模块、心率算法引擎与体检数据传输机制。我们在实际线路环境中进行了超过 300 次验证，通过对比数据，我们发现新版系统能够更好地适应室内外温差、司机手部状态差异以及体动干扰带来的随机噪声。</p>\r\n<p>本次升级的核心亮点包括：</p>\r\n<ul>\r\n    <li><strong>血压采集稳定性提升 22%</strong>：通过优化硬件信号滤波参数，新版设备能够减少外部震动导致的脉搏信号波动，使血压数据更接近临床结果。</li>\r\n    <li><strong>心率异常波形自动判定算法上线</strong>：新增早搏、心律不齐风险提示，为司机健康风险预警提供早发现支持。</li>\r\n    <li><strong>数据上传延迟减少约 40%</strong>：针对部分站点网络不稳定问题，我们加入了离线缓冲机制，确保数据不丢失。</li>\r\n</ul>\r\n<p>升级期间可能出现体检等待时间略微增加的情况，我们深表歉意。未来我们将持续倾听司机朋友以及各站段工作人员的反馈，不断优化体检体验。</p>', '2025-12-04 07:51:52', '2025-12-04 07:51:52', 'draft');
INSERT INTO `news` (`id`, `publish`, `content`, `publish_time`, `update_time`, `status`) VALUES
	(2, 'admin', '<h2>冬季驾驶与体检健康提示</h2>\r\n<p>进入冬季后，随着气温持续下降，很多铁路司机在体检时会出现体温偏低、血压偏高、手部末梢循环不足等情况。这些都可能影响自助体检机的检测结果，特别是在清晨或夜间测量时，更容易出现脉搏信号弱、血压初测偏差较大的问题。</p>\r\n<p>为了帮助各位司机在冬季也能顺利完成体检，我们整理了以下几点建议，希望大家在日常驾驶和体检流程中加以参考：</p>\r\n<ul>\r\n    <li><strong>测量前让身体先“热起来”</strong>：可以搓搓手、活动手腕，促进血液循环，使脉搏更容易被设备捕捉。</li>\r\n    <li><strong>夜间驾车后不建议立即测量血压</strong>：应先休息 5～10 分钟，让心率稳定下来，以免血压偏高。</li>\r\n    <li><strong>多喝温水，避免体温过低</strong>：体温过低会影响血氧测量的准确性，也会降低心率信号质量。</li>\r\n    <li><strong>关注肩颈健康</strong>：冬季厚衣服可能导致长期驾驶姿势更僵硬，建议每两小时适当活动。</li>\r\n</ul>\r\n<p>我们提醒各位司机，不要因为体检的小偏差产生压力，系统会根据历史数据自动进行综合分析。保持良好作息、适当运动，才能在冬季“稳稳开好车”。</p>', '2025-12-04 07:52:50', '2025-12-04 07:52:50', 'publish');
INSERT INTO `news` (`id`, `publish`, `content`, `publish_time`, `update_time`, `status`) VALUES
	(3, 'admin', '<h2>心电图模块算法优化及使用说明</h2>\r\n<p>为了让铁路司机在体检时能够获得更加准确的心电图分析结果，我们对体检机的 ECG 算法引擎进行了优化。本次更新主要集中在信号去噪、高低频干扰识别以及基础异常波形检测三个方面。在工程师的连续测试下，新版算法很好地应对了司机在操作过程中因轻微移动导致的基线漂移问题，也有效降低了静电干扰造成的虚假波峰。</p>\r\n<p>以下是新版算法的三大提升点：</p>\r\n<ul>\r\n    <li><strong>自适应滤波升级</strong>：新增“驾驶后心率恢复阶段识别”，防止因突发心率波动误判心律异常。</li>\r\n    <li><strong>异常波形采样增强</strong>：对于疑似早搏、心率不齐等情况，新版本会自动进行二次采样以提高准确度。</li>\r\n    <li><strong>数据分段上报机制</strong>：减少一次性数据包过大导致的上传失败问题，提高数据稳定性。</li>\r\n</ul>\r\n<p>我们建议司机在测量心电图时尽量保持手臂放松、身体后靠、避免说话，以降低体动干扰。如果测量过程中出现“请保持静止”的提示，请稍作调整后重新开始检测。</p>', '2025-12-04 07:53:02', '2025-12-04 07:53:02', 'publish');
INSERT INTO `news` (`id`, `publish`, `content`, `publish_time`, `update_time`, `status`) VALUES
	(4, 'admin', '<h2>关于体检机系统数据安全的说明</h2>\r\n<p>为了保障铁路司机体检数据的安全，我们持续加强后台服务、数据存储和传输链路的安全措施。体检数据涉及个人健康隐私，系统从设计之初就严格遵循国家相关数据合规要求，并采用端到端加密方式进行传输，确保体检数据不会被非法访问。</p>\r\n<p>本次安全升级主要包括：</p>\r\n<ul>\r\n    <li><strong>HTTPS 全链路强制加密</strong>：Android 端与后台服务之间的通信全部采用加密隧道，避免中间人攻击。</li>\r\n    <li><strong>数据脱敏展示</strong>：在后台展示层，对于驾驶员信息仅展示必要的部分内容，敏感字段统一隐藏。</li>\r\n    <li><strong>异常访问预警系统</strong>：新增检测程序可实时发现异常调用、暴力尝试、异常高频请求等行为。</li>\r\n</ul>\r\n<p>安全一直是铁路行业的生命线，我们将继续保持高标准，持续守护司机朋友们的健康数据安全。</p>', '2025-12-04 07:53:09', '2025-12-04 07:53:09', 'draft');
INSERT INTO `news` (`id`, `publish`, `content`, `publish_time`, `update_time`, `status`) VALUES
	(5, 'admin', '<h2>AI 体检趋势分析系统正式上线</h2>\r\n<p>随着体检设备数量不断增加，后台需要处理的体检记录呈指数级增长。为提升分析效率，我们正式上线了“AI 体检趋势分析系统”。该系统能够基于时间序列、心率波动模型以及司机个人历史数据，对血压、心率、血氧等指标进行趋势推断，并给出风险提示。</p>\r\n<p>系统具备以下功能：</p>\r\n<ul>\r\n    <li><strong>7 日体检趋势自动生成</strong>：司机可查看自己一周内体检波动情况，轻松了解身体变化。</li>\r\n    <li><strong>高风险司机预警机制</strong>：当连续多天出现异常数据，系统将自动通知相关管理人员。</li>\r\n    <li><strong>智能报告生成</strong>：后台可一键生成详细体检分析报告，包括波形质量、检测环境影响、历史对比等。</li>\r\n</ul>\r\n<p>未来我们将继续扩展 AI 模型的能力，将心电图深度波形分析纳入系统，让健康监测更加智能、更加贴心，也让铁路运行更加安全。</p>', '2025-12-04 07:53:15', '2025-12-04 07:53:15', 'draft');

-- 正在导出表  healthkiosk.permission 的数据：~76 rows (大约)
DELETE FROM `permission`;
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(2, 3, 1);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(3, 4, 1);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(4, 5, 1);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(5, 3, 2);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(6, 4, 2);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(7, 5, 2);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(8, 0, 3);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(9, 1, 3);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(10, 2, 3);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(11, 3, 3);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(12, 4, 3);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(13, 5, 3);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(14, 0, 4);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(15, 1, 4);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(16, 2, 4);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(17, 3, 4);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(18, 4, 4);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(19, 5, 4);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(20, 2, 5);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(21, 3, 5);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(22, 4, 5);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(23, 5, 5);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(24, 2, 6);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(25, 3, 6);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(26, 4, 6);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(27, 5, 6);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(28, 1, 7);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(29, 2, 7);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(30, 3, 7);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(31, 4, 7);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(32, 5, 7);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(33, 1, 8);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(34, 2, 8);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(35, 3, 8);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(36, 4, 8);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(37, 5, 8);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(38, 2, 9);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(39, 3, 9);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(40, 4, 9);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(41, 5, 9);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(42, 3, 10);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(43, 4, 10);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(44, 5, 10);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(45, 3, 11);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(46, 4, 11);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(47, 5, 11);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(48, 0, 12);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(49, 1, 12);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(50, 2, 12);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(51, 3, 12);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(52, 4, 12);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(53, 5, 12);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(54, 0, 13);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(55, 1, 13);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(56, 2, 13);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(57, 3, 13);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(58, 4, 13);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(59, 5, 13);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(60, 0, 14);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(61, 1, 14);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(62, 2, 14);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(63, 3, 14);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(64, 4, 14);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(65, 5, 14);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(66, 3, 15);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(67, 4, 15);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(68, 5, 15);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(69, 3, 16);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(70, 4, 16);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(71, 5, 16);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(72, 3, 17);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(73, 4, 17);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(74, 5, 17);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(75, 3, 18);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(76, 4, 18);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(77, 5, 18);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(78, 3, 16);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(79, 4, 16);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(80, 5, 16);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(81, 3, 17);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(82, 4, 17);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(83, 5, 17);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(84, 2, 19);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(85, 3, 19);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(86, 4, 19);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(87, 5, 19);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(88, 2, 20);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(89, 3, 20);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(90, 4, 20);
INSERT INTO `permission` (`permission_id`, `role_id`, `function_id`) VALUES
	(91, 5, 20);

-- 正在导出表  healthkiosk.role 的数据：~6 rows (大约)
DELETE FROM `role`;
INSERT INTO `role` (`role_id`, `role_name`, `remark`, `use`) VALUES
	(0, '访客', '未注册的用户', 1);
INSERT INTO `role` (`role_id`, `role_name`, `remark`, `use`) VALUES
	(1, '游客', '前端注册的用户', 1);
INSERT INTO `role` (`role_id`, `role_name`, `remark`, `use`) VALUES
	(2, '用户', 'Android设备端注册的用户', 1);
INSERT INTO `role` (`role_id`, `role_name`, `remark`, `use`) VALUES
	(3, '管理员', '可进行用户授权和相关后台管理内容', 1);
INSERT INTO `role` (`role_id`, `role_name`, `remark`, `use`) VALUES
	(4, '超级管理员', '可进行管理员授权', 1);
INSERT INTO `role` (`role_id`, `role_name`, `remark`, `use`) VALUES
	(5, '开发者', '可访问测试和开发期的内容', 1);
INSERT INTO `role` (`role_id`, `role_name`, `remark`, `use`) VALUES
	(6, '测试角色', '仅供测试使用的角色。', 0);

-- 正在导出表  healthkiosk.user 的数据：~14 rows (大约)
DELETE FROM `user`;
INSERT INTO `user` (`account`, `name`, `age`, `gender`, `pic`, `pwd`, `height`, `weight`, `role`, `email_enc`, `email_hash`, `key_version`, `register_time`, `birthday`) VALUES
	('10', '10', NULL, NULL, NULL, '4a44dc15364204a80fe80e9039455cc1608281820fe2b24f1e5233ade6af1dd5', NULL, NULL, 1, NULL, NULL, NULL, '2025-11-26 17:03:19', NULL);
INSERT INTO `user` (`account`, `name`, `age`, `gender`, `pic`, `pwd`, `height`, `weight`, `role`, `email_enc`, `email_hash`, `key_version`, `register_time`, `birthday`) VALUES
	('11', 'testUser', NULL, NULL, NULL, '5772527c5398c3b1d9999c5a9388823c454126d8a387a32ce461ad7cfc13f656', NULL, NULL, 1, NULL, NULL, NULL, '2025-11-28 19:12:41', NULL);
INSERT INTO `user` (`account`, `name`, `age`, `gender`, `pic`, `pwd`, `height`, `weight`, `role`, `email_enc`, `email_hash`, `key_version`, `register_time`, `birthday`) VALUES
	('2', '2', NULL, NULL, NULL, 'd4735e3a265e16eee03f59718b9b5d03019c07d8b6c51f90da3a666eec13ab35', NULL, NULL, 2, NULL, NULL, NULL, '2025-11-24 16:34:25', NULL);
INSERT INTO `user` (`account`, `name`, `age`, `gender`, `pic`, `pwd`, `height`, `weight`, `role`, `email_enc`, `email_hash`, `key_version`, `register_time`, `birthday`) VALUES
	('3', '3', NULL, NULL, NULL, '4e07408562bedb8b60ce05c1decfe3ad16b72230967de01f640b7e4729b49fce', NULL, NULL, 1, NULL, NULL, NULL, '2025-11-23 16:34:31', NULL);
INSERT INTO `user` (`account`, `name`, `age`, `gender`, `pic`, `pwd`, `height`, `weight`, `role`, `email_enc`, `email_hash`, `key_version`, `register_time`, `birthday`) VALUES
	('5', '5', NULL, NULL, NULL, 'ef2d127de37b942baad06145e54b0c619a1f22327b2ebbcfbec78f5564afe39d', NULL, NULL, 1, NULL, NULL, NULL, '2025-11-24 16:34:40', NULL);
INSERT INTO `user` (`account`, `name`, `age`, `gender`, `pic`, `pwd`, `height`, `weight`, `role`, `email_enc`, `email_hash`, `key_version`, `register_time`, `birthday`) VALUES
	('6', '6', NULL, NULL, NULL, 'e7f6c011776e8db7cd330b54174fd76f7d0216b612387a5ffcfb81e6f0919683', NULL, NULL, 1, NULL, NULL, NULL, '2025-11-28 17:02:56', NULL);
INSERT INTO `user` (`account`, `name`, `age`, `gender`, `pic`, `pwd`, `height`, `weight`, `role`, `email_enc`, `email_hash`, `key_version`, `register_time`, `birthday`) VALUES
	('7', '7', NULL, NULL, NULL, '7902699be42c8a8e46fbbb4501726517e86b22c56a189f7625a6da49081b2451', NULL, NULL, 1, NULL, NULL, NULL, '2025-11-28 17:03:04', NULL);
INSERT INTO `user` (`account`, `name`, `age`, `gender`, `pic`, `pwd`, `height`, `weight`, `role`, `email_enc`, `email_hash`, `key_version`, `register_time`, `birthday`) VALUES
	('8', '8', NULL, NULL, NULL, '2c624232cdd221771294dfbb310aca000a0df6ac8b66b696d90ef06fdefb64a3', NULL, NULL, 1, NULL, NULL, NULL, '2025-11-28 17:03:07', NULL);
INSERT INTO `user` (`account`, `name`, `age`, `gender`, `pic`, `pwd`, `height`, `weight`, `role`, `email_enc`, `email_hash`, `key_version`, `register_time`, `birthday`) VALUES
	('9', '9', NULL, NULL, NULL, '19581e27de7ced00ff1ce50b2047e7a567c76b1cbaebabe5ef03f7c3017bb5b7', NULL, NULL, 1, NULL, NULL, NULL, '2025-11-27 17:03:13', NULL);
INSERT INTO `user` (`account`, `name`, `age`, `gender`, `pic`, `pwd`, `height`, `weight`, `role`, `email_enc`, `email_hash`, `key_version`, `register_time`, `birthday`) VALUES
	('admin', 'admin', 30, '男', '1768550626716.jpg', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 180, 65, 5, 'ZntTALWJ2M2MpTnOBOtYJSm76KGWzfN3p5OrU+hdO5Q4mpL6W7DFmC73Av+2diMP', '429ca99f1adb243b70017eae55ed8967dd6077f53d030f83300583e269ca0fd5', 'v1', '2025-11-26 14:08:59', NULL);
INSERT INTO `user` (`account`, `name`, `age`, `gender`, `pic`, `pwd`, `height`, `weight`, `role`, `email_enc`, `email_hash`, `key_version`, `register_time`, `birthday`) VALUES
	('lmf', 'lmf', NULL, NULL, NULL, 'd2e14c798217f0cd783930877ca62c681860a0a48bc1baf9fc66ff64da21bfdb', NULL, NULL, 1, 'ayqdcRzwlScYWp7ro6OGaApqNnWn6NnyuJ7hG0esfdNmCOdscP2PARGMbwj323qR', 'cc143d1395476355298d727dfeb33076c18b83df1718986150f9701e8be3b94b', 'v1', '2025-11-26 15:02:28', NULL);
INSERT INTO `user` (`account`, `name`, `age`, `gender`, `pic`, `pwd`, `height`, `weight`, `role`, `email_enc`, `email_hash`, `key_version`, `register_time`, `birthday`) VALUES
	('show1', '测试2', NULL, NULL, NULL, 'show1', NULL, NULL, 0, NULL, NULL, NULL, '2025-11-23 16:08:58', NULL);
INSERT INTO `user` (`account`, `name`, `age`, `gender`, `pic`, `pwd`, `height`, `weight`, `role`, `email_enc`, `email_hash`, `key_version`, `register_time`, `birthday`) VALUES
	('user', 'user', NULL, '男', NULL, '04f8996da763b7a969b1028ee3007569eaf3a635486ddab211d512c85b9df8fb', NULL, NULL, 2, NULL, NULL, NULL, '2025-11-30 16:09:02', NULL);
INSERT INTO `user` (`account`, `name`, `age`, `gender`, `pic`, `pwd`, `height`, `weight`, `role`, `email_enc`, `email_hash`, `key_version`, `register_time`, `birthday`) VALUES
	('user2', 'user2', 5, '男', '1759821101919.png', '94edf28c6d6da38fd35d7ad53e485307f89fbeaf120485c8d17a43f323deee71', 1, 1, 2, '2NtdXvuuQsA3yzptRxuEfYnP2Lxn7sH2j6jnsOHEYcjxV4p82XjtFEYQW3GZEzIk', '429ca99f1adb243b70017eae55ed8967dd6077f53d030f83300583e269ca0fd5', 'v1', '2025-11-27 15:07:58', NULL);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
