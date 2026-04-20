-- notice
create table ml_sms.notice
(
    `id`      bigint auto_increment            comment '主键' primary key,
    `content` varchar(512) not null default '' comment '内容',
    `idx`     bigint       not null default 0  comment '序号',
    `version` bigint       not null default 0  comment '数据版本',
    `deleted` tinyint      not null default 0  comment '0未删除，1已删除',
    `created` datetime     not null default current_timestamp comment '创建时间',
    `updated` datetime     not null default current_timestamp comment '修改时间'
) comment '通知表';

-- notice
insert into ml_sms.notice (id, content, idx)
values
    ( 1, '【重要通知】：系统将于今晚22:00进行维护，预计时长2小时，届时将无法访问，请提前做好准备！', 1),
    ( 2, '【课程更新】：您已购买的《Python 零基础入门到精通》课程新增 3 节实战课，包含项目部署实操，登录后可直接观看 ', 2),
    ( 3, '【温馨提示】：您的账号在异地（IP：112.156.40.77）尝试登录，若非本人操作，请立即前往「个人中心 - 安全设置」修改密码 ', 3),
    ( 4, '【学习提醒】：您报名的《Java 开发进阶班》本周课程将于今晚 19:30 直播，点击链接提前进入教室：https://joezhou/room/892', 4),
    ( 5, '【订单通知】：您于今日 14:25 购买的《UI 设计全能课程》已支付成功，课程已添加至「我的课程」列表，可随时开始学习 ', 5),
    ( 6, '【重要通知】：因平台服务器升级，明日 01:00-03:00 期间课程观看、作业提交功能暂不可用，已缓存课程可正常学习 ', 6),
    ( 7, '【考试提醒】：《计算机二级 Office》课程期末模拟考将于本周日 9:00 开启，需在「学习中心 - 考试模块」提前预约，逾期无法参与 ', 7),
    ( 8, '【活动提醒】：本月 15 日 - 20 日期间，购买任意「进阶类课程」享 7 折优惠，同时赠送价值 199 元的配套学习资料包，限前 500 名 ', 8),
    ( 9, '【系统通知】：您的学习账号已连续 15 天未登录，之前学习的《PS 图像处理》课程有更新内容，登录后可继续学习进度 ', 9),
    (10, '【会员权益】：您已升级为「年度 VIP 会员」，可享受所有课程免费学、专属学习社群、一对一导师答疑等权益，有效期至 2026 年 5 月 ', 10),
    (11, '【风险提示】：近期有用户收到虚假「课程退费」短信，官方退费仅通过平台「客服中心 - 售后申请」通道办理，切勿点击陌生链接 ', 11),
    (12, '【功能上线】：平台新增「学习计划制定」功能，可根据您的目标自动生成每日学习任务，登录后在「个人中心 - 学习工具」中使用 ', 12),
    (13, '【作业提醒】：您上周提交的《Web 前端开发》课程作业已批改完成，老师给出 3 条优化建议，登录后可在「作业中心」查看详情 ', 13),
    (14, '【系统公告】：为提升观看体验，课程播放器已升级，支持倍速播放（0.5-2.0 倍）、画质切换（标清 / 高清 / 超清），所有课程均适用 ', 14),
    (15, '【续费提醒】：您的「月度会员」将于 3 天后到期，到期后将无法观看付费课程，现在续费可享 8 折优惠，点击链接立即办理：https://vip.joezhou.com/renew', 15),
    (16, '【直播通知】：下周四 19:00 将举办「求职面试技巧」免费直播课，由资深 HR 主讲，可在「首页 - 直播预告」模块提前预约，开播前会提醒 ', 16),
    (17, '【重要通知】：因国家法定节假日，10 月 1 日 - 7 日期间客服工作时间调整为 10:00-18:00，课程学习、作业提交等功能正常可用 ', 17),
    (18, '【功能更新】：平台新增「课程笔记导出」功能，您在学习时记录的笔记可导出为 PDF 格式，方便离线复习，在课程播放页点击「笔记」即可操作 ', 18),
    (19, '【活动通知】：参与「邀请好友注册」活动，每成功邀请 1 位好友完成实名认证并购买课程，您可获得 200 元课程优惠券，无使用门槛 ', 19),
    (20, '【学习提醒】：您制定的《数据分析实战》课程学习计划已逾期 3 天，未完成的 2 节课程建议尽快学习，避免影响整体进度 ', 20),
    (21, '【证书通知】：您已完成《新媒体运营》课程所有学习任务且考试合格，电子证书已生成，登录后在「个人中心 - 我的证书」中可下载 ', 21);
-- banner
create table ml_sms.banner
(
    `id`      bigint auto_increment            comment '主键' primary key,
    `url`     varchar(256) not null default '' comment '图片地址',
    `info`    varchar(512) not null default '' comment '描述',
    `idx`     bigint       not null default 0  comment '序号',
    `version` bigint       not null default 0  comment '数据版本',
    `deleted` tinyint      not null default 0  comment '0未删除，1已删除',
    `created` datetime     not null default current_timestamp comment '创建时间',
    `updated` datetime     not null default current_timestamp comment '修改时间'
) comment '横幅表';

-- banner
insert into ml_sms.banner (`id`, `url`, `info`, `idx`)
values
    ( 1, 'default-banner.jpg', '想入行 Python 开发却没方向？《Python 零基础入门到精通》课程来帮你！从语法基础到项目实战，300 + 课时搭配 1 对 1 答疑，现在报名送价值 299 元开发工具包，再享首月学习社群专属指导，3 个月从小白到能独立做项目，早学早抢占高薪岗位，扫码立即解锁优惠！', 1),
    ( 2, 'default-banner.jpg', '考计算机二级 Office 总挂科？别愁！《计算机二级 Office 通关课》上线啦！5 年命题老师授课，拆解 100 + 高频考点，提供真题刷题系统 + 错题解析，还送考前押题卷。现在报名立减 150 元，三人团报再享 8 折，考不过免费重学，助你一次拿证，快和同学一起组队学习！', 2),
    ( 3, 'default-banner.jpg', '想提升 UI 设计能力转岗加薪？《UI 设计全能进阶课》专为你打造！涵盖 Figma 实操、交互设计、作品集指导，每周 2 节直播 + 课后作业批改，结课推荐实习机会。现在成为月度 VIP，立享课程免费学，还能解锁 100 + 设计素材库，名额有限，先到先得！', 3),
    ( 4, 'default-banner.jpg', '新媒体运营没思路？《新媒体运营从 0 到 1 实战课》帮你突破瓶颈！教你公众号排版、短视频剪辑、流量变现技巧，搭配真实案例拆解，课后还有项目实操任务。现在报名送价值 199 元运营工具包，老学员推荐再减 50 元，学完能独立运营账号，开启副业赚钱路！', 4),
    ( 5, 'default-banner.jpg', '年度 VIP 会员福利来袭！开通即享所有课程免费学，包括 Python、UI 设计、新媒体运营等 50 + 热门课，还能获得专属导师 1 对 1 规划学习路径、优先参与直播答疑，每月额外赠送 200 元课程优惠券。现在开通立享 7 折，再送价值 399 元学习大礼包，一年仅一次，错过等明年！', 5),
    ( 6, 'default-banner.jpg', '想学 PS 却不知从何下手？《PS 图像处理入门课》超适合新手！从基础工具使用到海报设计、照片修图，40 节视频课 + 配套练习素材，手机电脑都能学。现在报名仅需 99 元，转发朋友圈集 20 赞再减 20 元，还能加入学习群和同学交流，轻松掌握实用技能！', 6),
    ( 7, 'default-banner.jpg', '「求职面试技巧」免费直播课来啦！10 年资深 HR 主讲，教你简历优化、面试应答话术、薪资谈判技巧，还会现场解析热门行业招聘需求。直播时间下周四 19:00，提前预约可领取面试大礼包，直播中还有抽奖，赢取价值 299 元的求职辅导课，扫码立即预约！', 7),
    ( 8, 'default-banner.jpg', '学习 Java 开发想进阶？《Java 开发高级实战课》不容错过！深入讲解 SpringBoot、微服务架构、分布式系统，搭配企业级项目实战，助你提升技术深度。现在报名立享三期免息，还送价值 499 元的 Java 开发手册 + 源码解析，成为年度 VIP 更能免费学，帮你轻松应对大厂面试！', 8),
    ( 9, 'default-banner.jpg', '「邀请好友注册」活动火热进行中！每成功邀请 1 位好友完成实名认证并购买课程，你就能获得 200 元无门槛课程优惠券，邀请 3 人还能免费开通月度 VIP，邀请 10 人直接升级年度 VIP。优惠券可叠加使用，多邀多赚，既能帮朋友学习，又能给自己省学费，赶紧行动！', 9),
    (10, 'default-banner.jpg', '担心碎片化时间浪费？《碎片化学习训练营》开启招募！每天 15 分钟学习 UI 设计、Python 基础等小课程，搭配打卡奖励机制，连续打卡 21 天送价值 99 元课程优惠券。现在加入训练营仅需 19 元，完成所有打卡全额返现，让碎片时间变黄金，轻松提升技能！', 10);
-- article
create table ml_sms.article
(
    `id`      bigint auto_increment            comment '主键' primary key,
    `title`   varchar(128) not null default '' comment '标题',
    `content` varchar(512) not null default '' comment '内容',
    `idx`     bigint       not null default 0  comment '序号',
    `version` bigint       not null default 0  comment '数据版本',
    `deleted` tinyint      not null default 0  comment '0未删除，1已删除',
    `created` datetime     not null default current_timestamp comment '创建时间',
    `updated` datetime     not null default current_timestamp comment '修改时间'
) comment '新闻表';

-- article
insert into ml_sms.article (`id`, `title`, `content`, `idx`)
values
    (1, '【公告】热烈庆祝三班Java课程正式开班！', '我们邀请资深讲师，采用前沿教材，注重实践操作，助力学员轻松掌握Java编程技能。无论你是初学者还是进阶者，这里都将是你成长的摇篮。赶快加入我们，一起开启Java编程之旅吧！名额有限，报名从速！', 1),
    (2, '【公告】好消息！Java教材现已正式出版！', '这本教材由资深Java专家精心撰写，内容深入浅出，既适合初学者入门，也适合进阶者提升。书中包含大量实战案例和练习题，助你轻松掌握Java编程的核心知识和技能。无论你是学生还是职场人士，这本教材都将是你学习Java的绝佳伴侣。赶快行动，抢购一本吧！', 2),
    (3, '【科普】Java究竟是一门怎样的语言？', 'Java是一种广泛应用的编程语言，以其简洁、面向对象和跨平台特性而著称。它支持多线程编程，拥有丰富的API和强大的社区支持。Java广泛应用于Web开发、移动应用、游戏开发等领域，是IT行业的重要基石之一。掌握Java，将为你打开编程世界的大门。', 3),
    (4, '【科普】计算机语言发展历史简述。', '计算机语言发展史可追溯到20世纪40年代，最初是机器语言，直接以二进制代码操作计算机。随后诞生了汇编语言，以英文标签简化编程。到了20世纪50年代，高级语言如Fortran和Algol出现，更接近自然语言。之后，C、C++、Java、Python等语言相继涌现，面向对象编程成为主流。计算机语言的发展推动了计算机技术的飞速进步。', 4),
    (5, '【急聘】诚邀有志之士加入我们的教育团队！', '现招聘Java讲师及助教若干名。Java讲师需具备丰富的教学经验和扎实的编程功底，能够引导学生深入学习Java技术；助教则需协助讲师完成教学任务，解答学生疑问。我们提供优厚的待遇和良好的发展空间，期待你的加入，共同培养更多Java人才！', 5);
-- seckill
create table ml_sms.seckill
(
    `id`         bigint auto_increment            comment '主键' primary key,
    `title`      varchar(128) not null default '' comment '标题',
    `info`       varchar(512) not null default '' comment '描述',
    `start_time` datetime     not null default current_timestamp comment '活动开始时间',
    `end_time`   datetime     not null default current_timestamp comment '活动结束时间',
    `status`     tinyint      not null default 0  comment '0未开始，1已开始，2已结束',
    `version`    bigint       not null default 0  comment '数据版本',
    `deleted`    tinyint      not null default 0  comment '0未删除，1已删除',
    `created`    datetime     not null default current_timestamp comment '创建时间',
    `updated`    datetime     not null default current_timestamp comment '修改时间'
) comment '秒杀表';

insert into ml_sms.seckill (id, title, info, start_time, end_time) values
                                                                       (1, '上午场', '暂无描述。', '2025-12-07 08:00:00', '2025-12-07 09:00:00'),
                                                                       (2, '中午场', '暂无描述。', '2025-12-07 12:00:00', '2025-12-07 13:00:00'),
                                                                       (3, '下午场', '暂无描述。', '2025-12-07 17:00:00', '2025-12-07 18:00:00');
-- seckill_detail
create table ml_sms.seckill_detail
(
    `id`            bigint auto_increment               comment '主键' primary key,
    `fk_seckill_id` bigint                              comment '秒杀ID，秒杀表外键',
    `fk_course_id`  bigint                              comment '课程ID，课程表外键',
    `course_title`  varchar(128)  not null default ''   comment '课程标题（冗余）',
    `course_cover`  varchar(256)  not null default ''   comment '课程封面图（冗余）',
    `course_price`  decimal(8, 2) not null default 0.00 comment '课程单价，单位元（冗余）',
    `sk_price`      decimal(8, 2) not null default 0.00 comment '秒杀价格，单位元',
    `sk_count`      int           not null default 0    comment '秒杀数量',
    `info`          varchar(512)  not null default ''   comment '描述',
    `version`       bigint        not null default 0    comment '数据版本',
    `deleted`       tinyint       not null default 0    comment '0未删除，1已删除',
    `created`       datetime      not null default current_timestamp comment '创建时间',
    `updated`       datetime      not null default current_timestamp comment '修改时间'
) comment '秒杀明细表';

insert into ml_sms.seckill_detail (id, fk_seckill_id, fk_course_id, course_title, course_cover, course_price, sk_price, sk_count) values
                                                                                                                                      (1, 1, 1, 'JB1-1-新手村', 'default-course-cover.jpg', 11100.00, 9.9, 1),
                                                                                                                                      (2, 1, 2, 'JB1-2-基础启航', 'default-course-cover.jpg', 11200.00, 10.9, 2),
                                                                                                                                      (3, 2, 3, 'JB1-3-面向对象', 'default-course-cover.jpg', 11300.00, 11.9, 3),
                                                                                                                                      (4, 3, 4, 'JB1-4-高级进阶', 'default-course-cover.jpg', 11400.00, 12.9, 4);
-- coupons
create table ml_sms.coupons
(
    `id`          bigint auto_increment               comment '主键' primary key,
    `code`        varchar(128)  not null default ''   comment '兑换码',
    `title`       varchar(128)  not null default ''   comment '标题',
    `cp_price`    decimal(8, 2) not null default 0.00 comment '优惠金额，单位分',
    `info`        varchar(512)  not null default ''   comment '描述',
    `start_time`  datetime      not null default current_timestamp comment '优惠卷生效时间',
    `end_time`    datetime      not null default current_timestamp comment '优惠卷失效时间',
    `version`     bigint        not null default 0    comment '数据版本',
    `deleted`     tinyint       not null default 0    comment '0未删除，1已删除',
    `created`     datetime      not null default current_timestamp comment '创建时间',
    `updated`     datetime      not null default current_timestamp comment '修改时间'
) comment '优惠卷表';

-- coupons
insert into ml_sms.coupons (`id`, `code`, `title`, `cp_price`, `info`)
values
    (1, 'ml010', '双11通用A卷', 500, '双11活动专用优惠券'),
    (2, 'ml050', '双11通用B卷', 400, '双11活动专用优惠券'),
    (3, '33333', '双11通用C卷', 200, '双11活动专用优惠券');
