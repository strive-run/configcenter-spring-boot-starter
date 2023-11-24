CREATE TABLE IF NOT EXISTS `application_config_center_info`
(
    `id`                bigint auto_increment NOT NULL COMMENT '主键',
    `data_id`           varchar(64)           NOT NULL COMMENT '文件ID',
    `profile`           varchar(64)           NOT NULL COMMENT '环境',
    `extension`         varchar(16)           NOT NULL COMMENT '后缀（properties、yaml、json等)',
    `content`           text                  NOT NULL COMMENT '配置内容',
    `last_updated_time` datetime(6)           NOT NULL COMMENT '最后更新时间',
    `last_updated_user` varchar(32) DEFAULT 'system' COMMENT '最后更新操作人',
    `version`           int                   NOT NULL COMMENT '版本',
    PRIMARY KEY (`id`),
    KEY `idx` (`data_id`, `profile`, `version`)
) COMMENT ='应用配置中心信息表';
