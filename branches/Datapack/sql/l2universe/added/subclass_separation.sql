DROP TABLE IF EXISTS `subclass_separation`;
CREATE TABLE `subclass_separation` (
	`account_from` varchar(100) not null,
	`account_to` varchar(100),
	`char_id` int not null,
	`subclass_index` int not null,
	`last_separation` datetime,
	PRIMARY KEY (`account_from`,`account_to`)
);
