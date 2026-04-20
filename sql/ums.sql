-- user
create table ml_ums.user
(
    `id`       bigint auto_increment            comment '主键' primary key,
    `username` varchar(128) not null default '' comment '账号',
    `password` varchar(128) not null default '' comment '密码',
    `nickname` varchar(128) not null default '' comment '昵称',
    `email`    varchar(128) not null default '' comment '邮箱',
    `province` varchar(128) not null default '' comment '省份',
    `realname` varchar(128) not null default '' comment '姓名',
    `avatar`   varchar(256) not null default '' comment '头像',
    `zodiac`   char(3)      not null default '' comment '星座',
    `phone`    char(11)     not null default '' comment '手机',
    `idcard`   char(18)     not null default '' comment '身份证号',
    `gender`   tinyint      not null default 0  comment '性别，0女，1男，2保密',
    `age`      tinyint      not null default 0  comment '年龄',
    `info`     varchar(512) not null default '' comment '描述',
    `version`  bigint       not null default 0  comment '数据版本',
    `deleted`  tinyint      not null default 0  comment '0未删除，1已删除',
    `created`  datetime     not null default current_timestamp comment '创建时间',
    `updated`  datetime     not null default current_timestamp comment '修改时间'
) comment '用户表';

-- role
create table ml_ums.role
(
    `id`      bigint auto_increment            comment '主键' primary key,
    `title`   varchar(128) not null default '' comment '标题',
    `idx`     bigint       not null default 0  comment '序号',
    `info`    varchar(512) not null default '' comment '描述',
    `version` bigint       not null default 0  comment '数据版本',
    `deleted` tinyint      not null default 0  comment '0未删除，1已删除',
    `created` datetime     not null default current_timestamp comment '创建时间',
    `updated` datetime     not null default current_timestamp comment '修改时间'
) comment '角色表';

-- role
insert into ml_ums.role (`id`, `title`, `idx`, `info`)
values
    (1, '普通用户',   1, '登录并享受本系统'),
    (2, '系统管理员', 2, '拥有最高权限，继承上述所有角色权限，额外负责系统日志监控、异常排查及全局配置管理等核心工作。'),
    (3, '用户管理员', 3, '主导用户体系数据管理，包含权限菜单配置、角色定义与分配、用户信息维护及权限校验等相关工作。'),
    (4, '课程管理员', 4, '负责课程全链路数据管理，包括课程类别划分、课程信息维护、季次与集次编排，以及课程评论审核、举报内容处理等工作。'),
    (5, '营销管理员', 5, '统筹平台营销相关数据管理，涵盖通知推送、横幅广告配置、新闻通告发布，以及秒杀活动策划、优惠券发放与管控等。'),
    (6, '订单管理员', 6, '聚焦交易环节数据管理，负责购物车商品状态维护、订单创建审核、交易信息查询及订单状态跟踪等核心工作。');

-- menu
create table ml_ums.menu
(
    `id`      bigint auto_increment            comment '主键' primary key,
    `title`   varchar(128) not null default '' comment '标题',
    `url`     varchar(256) not null default '' comment '跳转地址',
    `icon`    varchar(256) not null default '' comment '图标名称',
    `pid`     bigint       not null default 0  comment '父菜单主键，0视为根节点',
    `idx`     bigint       not null default 0  comment '序号',
    `info`    varchar(512) not null default '' comment '描述',
    `version` bigint       not null default 0  comment '数据版本',
    `deleted` tinyint      not null default 0  comment '0未删除，1已删除',
    `created` datetime     not null default current_timestamp comment '创建时间',
    `updated` datetime     not null default current_timestamp comment '修改时间'
) comment '菜单表';

-- menu
insert into ml_ums.menu (`id`, `title`, `idx`, `url`, `icon`, `pid`)
values
    ( 1, '用户管理',   1, '',           'User',         0),
    ( 2, '课程管理',   2, '',           'Notebook',     0),
    ( 3, '营销管理',   3, '',           'Goods',        0),
    ( 4, '订单管理',   4, '',           'Files',        0),
    ( 5, '日志管理',   5, '',           'List',         0),
    ( 6, '用户列表',   1, '/User',      'User',         1),
    ( 7, '角色列表',   2, '/Role',      'Avatar',       1),
    ( 8, '菜单列表',   3, '/Menu',      'Flag',         1),
    ( 9, '类别列表',   1, '/Category',  'Management',   2),
    (10, '课程列表',   2, '/Course',    'Notebook',     2),
    (11, '评论列表',   3, '/Comment',   'Comment',      2),
    (12, '举报列表',   4, '/Report',    'Warning',      2),
    (13, '通知列表',   1, '/Notice',    'Opportunity',  3),
    (14, '横幅列表',   2, '/Banner',    'Picture',      3),
    (15, '新闻列表',   3, '/Article',   'WindPower',    3),
    (16, '秒杀列表',   4, '/Seckill',   'Stopwatch',    3),
    (17, '优惠卷列表', 5, '/Coupons',   'Present',      3),
    (18, '购物车列表', 1, '/Cart',      'ShoppingCart', 4),
    (19, '订单列表',   2, '/Order',     'Goods',        4),
    (20, '后台日志',   1, '/AdminLog',  'Memo',         5),
    (21, '前台日志',   2, '/ServerLog', 'Memo',         5);

-- user_role
create table ml_ums.user_role
(
    `id`         bigint auto_increment       comment '主键' primary key,
    `fk_user_id` bigint                      comment '用户ID，用户表外键',
    `fk_role_id` bigint                      comment '角色ID，角色表外键',
    `version`    bigint   not null default 0 comment '数据版本',
    `deleted`    tinyint  not null default 0 comment '0未删除，1已删除',
    `created`    datetime not null default current_timestamp comment '创建时间',
    `updated`    datetime not null default current_timestamp comment '修改时间'
) comment '用户角色关系表';

-- user_role
insert into ml_ums.user_role (`id`, `fk_user_id`, `fk_role_id`)
values
    (1, 1, 2), (2, 2, 3), (3, 2, 4), (4, 3, 5), (5, 3, 6), (7, 4, 1);


-- role_menu
create table ml_ums.role_menu
(
    `id`         bigint auto_increment       comment '主键' primary key,
    `fk_role_id` bigint                      comment '角色ID，角色表外键',
    `fk_menu_id` bigint                      comment '菜单ID，菜单表外键',
    `version`    bigint   not null default 0 comment '数据版本',
    `deleted`    tinyint  not null default 0 comment '0未删除，1已删除',
    `created`    datetime not null default current_timestamp comment '创建时间',
    `updated`    datetime not null default current_timestamp comment '修改时间'
) comment '角色菜单关系表';

-- role_menu
insert into ml_ums.role_menu (`id`, `fk_role_id`, `fk_menu_id`)
values
    (1, 2, 01), (2, 2, 02), (3, 2, 03), (4, 2, 04), (5, 2, 05), (6, 2, 06), (7, 2, 07), (8, 2, 08), (9, 2, 09), (10, 2, 10), (11, 2, 11), (12, 2, 12), (13, 2, 13), (14, 2, 14), (15, 2, 15), (16, 2, 16), (17, 2, 17), (18, 2, 18), (19, 2, 14), (20, 2, 15), (21, 2, 16), (22, 2, 17), (23, 2, 18), (24, 2, 19), (25, 2, 20), (26, 2, 21), (27, 2, 22),
    (28, 3, 01), (29, 3, 06), (30, 3, 07), (31, 3, 08),
    (32, 4, 02), (33, 4, 09), (34, 4, 10), (35, 4, 11), (36, 4, 12), (37, 4, 13), (38, 4, 14),
    (39, 5, 03), (40, 5, 15), (41, 5, 16), (42, 5, 17), (43, 5, 18),
    (44, 6, 04), (45, 6, 19), (46, 6, 20);
