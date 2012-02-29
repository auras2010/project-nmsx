-- --------------------------------------------
-- Table structure for custom_notspawned
-- --------------------------------------------
DROP TABLE IF EXISTS `custom_notspawned`;
CREATE TABLE IF NOT EXISTS `custom_notspawned` (
  `id` int(11) NOT NULL,
  `isCustom` int(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE = MYISAM DEFAULT CHARSET=utf8;