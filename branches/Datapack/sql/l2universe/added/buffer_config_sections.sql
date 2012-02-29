DROP TABLE IF EXISTS `buffer_config_sections`;
CREATE TABLE `buffer_config_sections` (
  `section_id` int(11) NOT NULL AUTO_INCREMENT,
  `section_name` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`section_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of buffer_config_sections
-- ----------------------------
INSERT INTO `buffer_config_sections` VALUES ('1', 'General');
INSERT INTO `buffer_config_sections` VALUES ('2', 'Buffs');
INSERT INTO `buffer_config_sections` VALUES ('3', 'Prices');
INSERT INTO `buffer_config_sections` VALUES ('4', 'VIP');
