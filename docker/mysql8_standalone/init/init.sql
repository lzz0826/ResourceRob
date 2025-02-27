CREATE DATABASE ticket;

USE ticket;

/**
  ticket QA
 */
CREATE TABLE IF NOT EXISTS `ticket_nginxQA` (
                                                `id` INT NOT NULL AUTO_INCREMENT COMMENT 'Primary key ID',
                                                `ticket_name` varchar(40) NOT NULL DEFAULT '' COMMENT 'ticketId',
    `user_id` varchar(40) NOT NULL DEFAULT '' COMMENT 'userId',
    `area` varchar(40) NOT NULL DEFAULT '' COMMENT 'area',
    `book_time` timestamp NOT NULL COMMENT 'bookTime',
    `ticket_token` varchar(70) NOT NULL DEFAULT '' COMMENT 'ticketToken',
    `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'updateTime',
    `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'createTime',
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/**
  ticket QB
 */
CREATE TABLE IF NOT EXISTS `ticket_nginxQB` (
                                                `id` INT NOT NULL AUTO_INCREMENT COMMENT 'Primary key ID',
                                                `ticket_name` varchar(40) NOT NULL DEFAULT '' COMMENT 'ticketId',
    `user_id` varchar(40) NOT NULL DEFAULT '' COMMENT 'userId',
    `area` varchar(40) NOT NULL DEFAULT '' COMMENT 'area',
    `book_time` timestamp NOT NULL COMMENT 'bookTime',
    `ticket_token` varchar(70) NOT NULL DEFAULT '' COMMENT 'ticketToken',
    `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'updateTime',
    `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'createTime',
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;