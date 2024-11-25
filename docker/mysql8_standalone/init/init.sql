CREATE DATABASE ticket;

USE ticket;

/**
  ticket QA
 */
CREATE TABLE IF NOT EXISTS `ticket_nginxQA` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT 'Primary key ID',
    `ticketId` varchar(40) NOT NULL COMMENT 'ticketId',
    `userId` varchar(40) DEFAULT NULL COMMENT 'userId',
    `area` varchar(40) DEFAULT NULL COMMENT 'area',
    `time` timestamp NOT NULL COMMENT 'time',
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/**
  ticket QB
 */
CREATE TABLE IF NOT EXISTS `ticket_nginxQB` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT 'Primary key ID',
    `ticketId` varchar(40) NOT NULL COMMENT 'ticketId',
    `userId` varchar(40) DEFAULT NULL COMMENT 'userId',
    `area` varchar(40) DEFAULT NULL COMMENT 'area',
    `time` timestamp NOT NULL COMMENT 'time',
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;