-- cart
create table ml_oms.cart
(
    `id`           bigint auto_increment               comment '主键' primary key,
    `fk_user_id`   bigint                              comment '用户ID，用户表外键',
    `username`     varchar(128)  not null default ''   comment '用户账号（冗余）',
    `fk_course_id` bigint                              comment '课程ID，课程表外键',
    `course_title` varchar(128)  not null default ''   comment '课程标题（冗余）',
    `course_cover` varchar(256)  not null default ''   comment '课程封面图（冗余）',
    `course_price` decimal(8, 2) not null default 0.00 comment '课程单价，单位元（冗余）',
    `version`      bigint        not null default 0    comment '数据版本',
    `deleted`      tinyint       not null default 0    comment '0未删除，1已删除',
    `created`      datetime      not null default current_timestamp comment '创建时间',
    `updated`      datetime      not null default current_timestamp comment '修改时间'
) comment '购物车表';
-- order
create table ml_oms.order
(
    `id`            bigint auto_increment               comment '主键' primary key,
    `sn`            varchar(128)  not null default ''   comment '编号',
    `total_amount`  decimal(8, 2) not null default 0.00 comment '总金额',
    `pay_amount`    decimal(8, 2) not null default 0.00 comment '实际支付总金额',
    `pay_type`      tinyint       not null default 0    comment '支付方式，0未支付，1微信，2支付宝，3其他',
    `status`        tinyint       not null default 0    comment '订单状态，0未付款，1已付款，2已取消，3其他',
    `fk_user_id`    bigint                              comment '用户ID，用户表外键',
    `username`      varchar(128) not null default ''    comment '用户账号（冗余）',
    `fk_coupons_id` bigint                              comment '优惠卷ID，优惠卷表外键',
    `info`          varchar(512)  not null default ''   comment '描述',
    `version`       bigint        not null default 0    comment '数据版本',
    `deleted`       tinyint       not null default 0    comment '0未删除，1已删除',
    `created`       datetime      not null default current_timestamp comment '创建时间',
    `updated`       datetime      not null default current_timestamp comment '修改时间'
) comment '订单表';
-- order_detail
create table ml_oms.order_detail
(
    `id`           bigint auto_increment               comment '主键' primary key,
    `fk_order_id`  bigint                              comment '订单ID，订单表外键',
    `fk_course_id` bigint                              comment '课程ID，课程表外键',
    `course_title` varchar(128)  not null default ''   comment '课程标题（冗余）',
    `course_cover` varchar(256)  not null default ''   comment '课程封面图（冗余）',
    `course_price` decimal(8, 2) not null default 0.00 comment '课程单价，单位元（冗余）',
    `version`      bigint   not null default 0         comment '数据版本',
    `deleted`      tinyint  not null default 0         comment '0未删除，1已删除',
    `created`      datetime not null default current_timestamp comment '创建时间',
    `updated`      datetime not null default current_timestamp comment '修改时间'
) comment '订单明细表';
