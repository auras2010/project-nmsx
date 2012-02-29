CREATE TABLE `buffer_buff_list` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `buffType` varchar(10) DEFAULT NULL,
  `buffId` int(5) DEFAULT '0',
  `buffLevel` int(5) DEFAULT NULL,
  `forClass` tinyint(1) DEFAULT NULL,
  `canUse` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=140 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of buffer_buff_list
-- ----------------------------
INSERT INTO `buffer_buff_list` VALUES ('1', 'buff', '1085', '3', '1', '1');
INSERT INTO `buffer_buff_list` VALUES ('2', 'buff', '1304', '3', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('3', 'buff', '1087', '3', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('4', 'buff', '1354', '1', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('5', 'buff', '1062', '2', '2', '1');
INSERT INTO `buffer_buff_list` VALUES ('6', 'buff', '1243', '6', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('7', 'buff', '1045', '6', '2', '1');
INSERT INTO `buffer_buff_list` VALUES ('8', 'buff', '1048', '6', '2', '1');
INSERT INTO `buffer_buff_list` VALUES ('9', 'buff', '1397', '3', '0', '1');
INSERT INTO `buffer_buff_list` VALUES ('10', 'buff', '1078', '6', '1', '1');
INSERT INTO `buffer_buff_list` VALUES ('11', 'buff', '1242', '3', '0', '1');
INSERT INTO `buffer_buff_list` VALUES ('12', 'buff', '1353', '1', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('13', 'buff', '1352', '1', '2', '1');
INSERT INTO `buffer_buff_list` VALUES ('14', 'buff', '1059', '3', '1', '1');
INSERT INTO `buffer_buff_list` VALUES ('15', 'buff', '1077', '3', '0', '1');
INSERT INTO `buffer_buff_list` VALUES ('16', 'buff', '1240', '3', '0', '1');
INSERT INTO `buffer_buff_list` VALUES ('17', 'buff', '1086', '2', '0', '1');
INSERT INTO `buffer_buff_list` VALUES ('18', 'buff', '1392', '3', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('19', 'buff', '1043', '1', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('20', 'buff', '1032', '3', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('21', 'buff', '1036', '2', '2', '1');
INSERT INTO `buffer_buff_list` VALUES ('22', 'buff', '1035', '4', '2', '1');
INSERT INTO `buffer_buff_list` VALUES ('23', 'buff', '1068', '3', '0', '1');
INSERT INTO `buffer_buff_list` VALUES ('24', 'buff', '1044', '3', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('27', 'buff', '1033', '3', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('28', 'buff', '1259', '4', '0', '1');
INSERT INTO `buffer_buff_list` VALUES ('30', 'buff', '1040', '3', '0', '1');
INSERT INTO `buffer_buff_list` VALUES ('31', 'buff', '1393', '3', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('32', 'buff', '1268', '4', '0', '1');
INSERT INTO `buffer_buff_list` VALUES ('33', 'buff', '1303', '2', '1', '1');
INSERT INTO `buffer_buff_list` VALUES ('34', 'buff', '1204', '2', '2', '1');
INSERT INTO `buffer_buff_list` VALUES ('35', 'dance', '271', '1', '0', '1');
INSERT INTO `buffer_buff_list` VALUES ('36', 'dance', '272', '1', '0', '1');
INSERT INTO `buffer_buff_list` VALUES ('37', 'dance', '273', '1', '1', '1');
INSERT INTO `buffer_buff_list` VALUES ('38', 'dance', '274', '1', '0', '1');
INSERT INTO `buffer_buff_list` VALUES ('39', 'dance', '275', '1', '0', '1');
INSERT INTO `buffer_buff_list` VALUES ('40', 'dance', '276', '1', '1', '1');
INSERT INTO `buffer_buff_list` VALUES ('41', 'dance', '277', '1', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('42', 'dance', '307', '1', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('43', 'dance', '309', '1', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('44', 'dance', '310', '1', '0', '1');
INSERT INTO `buffer_buff_list` VALUES ('45', 'dance', '311', '1', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('46', 'dance', '366', '1', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('47', 'dance', '365', '1', '1', '1');
INSERT INTO `buffer_buff_list` VALUES ('48', 'song', '264', '1', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('49', 'song', '265', '1', '2', '1');
INSERT INTO `buffer_buff_list` VALUES ('50', 'song', '266', '1', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('51', 'song', '267', '1', '2', '1');
INSERT INTO `buffer_buff_list` VALUES ('52', 'song', '268', '1', '2', '1');
INSERT INTO `buffer_buff_list` VALUES ('53', 'song', '269', '1', '0', '1');
INSERT INTO `buffer_buff_list` VALUES ('54', 'song', '270', '1', '2', '1');
INSERT INTO `buffer_buff_list` VALUES ('55', 'song', '304', '1', '2', '1');
INSERT INTO `buffer_buff_list` VALUES ('56', 'song', '305', '1', '2', '1');
INSERT INTO `buffer_buff_list` VALUES ('57', 'song', '306', '1', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('58', 'song', '308', '1', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('59', 'song', '363', '1', '2', '1');
INSERT INTO `buffer_buff_list` VALUES ('60', 'song', '364', '1', '2', '1');
INSERT INTO `buffer_buff_list` VALUES ('61', 'song', '349', '1', '2', '1');
INSERT INTO `buffer_buff_list` VALUES ('62', 'chant', '1007', '3', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('63', 'chant', '1009', '3', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('64', 'chant', '1006', '3', '0', '1');
INSERT INTO `buffer_buff_list` VALUES ('65', 'chant', '1002', '3', '0', '1');
INSERT INTO `buffer_buff_list` VALUES ('66', 'chant', '1229', '18', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('67', 'chant', '1251', '2', '0', '1');
INSERT INTO `buffer_buff_list` VALUES ('68', 'chant', '1252', '3', '0', '1');
INSERT INTO `buffer_buff_list` VALUES ('69', 'chant', '1253', '3', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('70', 'chant', '1284', '3', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('71', 'chant', '1310', '4', '0', '1');
INSERT INTO `buffer_buff_list` VALUES ('72', 'chant', '1309', '3', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('73', 'chant', '1308', '3', '0', '1');
INSERT INTO `buffer_buff_list` VALUES ('74', 'chant', '1362', '1', '2', '1');
INSERT INTO `buffer_buff_list` VALUES ('77', 'special', '1413', '1', '1', '1');
INSERT INTO `buffer_buff_list` VALUES ('78', 'special', '1363', '1', '0', '1');
INSERT INTO `buffer_buff_list` VALUES ('81', 'special', '1388', '3', '0', '1');
INSERT INTO `buffer_buff_list` VALUES ('82', 'special', '1389', '3', '2', '1');
INSERT INTO `buffer_buff_list` VALUES ('83', 'special', '1356', '1', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('84', 'special', '1355', '1', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('94', 'special', '1357', '1', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('99', 'song', '529', '1', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('100', 'dance', '530', '1', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('101', 'kamael', '1476', '3', '3', '0');
INSERT INTO `buffer_buff_list` VALUES ('102', 'kamael', '1479', '3', '3', '0');
INSERT INTO `buffer_buff_list` VALUES ('103', 'kamael', '1478', '2', '3', '0');
INSERT INTO `buffer_buff_list` VALUES ('104', 'kamael ', '1477', '3', '3', '0');
INSERT INTO `buffer_buff_list` VALUES ('105', 'buff', '1311', '6', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('106', 'buff', '1307', '3', '2', '1');
INSERT INTO `buffer_buff_list` VALUES ('107', 'kamael', '499', '3', '3', '0');
INSERT INTO `buffer_buff_list` VALUES ('108', 'kamael', '1470', '1', '3', '0');
INSERT INTO `buffer_buff_list` VALUES ('109', 'special', '1457', '1', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('110', 'special', '341', '1', '3', '0');
INSERT INTO `buffer_buff_list` VALUES ('111', 'chant', '1461', '1', '2', '1');
INSERT INTO `buffer_buff_list` VALUES ('113', 'special', '1323', '1', '2', '0');
INSERT INTO `buffer_buff_list` VALUES ('123', 'vip', '76', '1', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('124', 'vip', '83', '1', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('125', 'vip', '109', '1', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('126', 'vip', '282', '1', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('127', 'vip', '292', '1', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('128', 'vip', '298', '1', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('129', 'vip', '425', '1', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('130', 'special', '825', '1', '2', '1');
INSERT INTO `buffer_buff_list` VALUES ('131', 'special', '826', '1', '2', '1');
INSERT INTO `buffer_buff_list` VALUES ('132', 'special', '827', '1', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('133', 'special', '828', '1', '2', '1');
INSERT INTO `buffer_buff_list` VALUES ('134', 'special', '829', '1', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('135', 'special', '830', '1', '3', '1');
INSERT INTO `buffer_buff_list` VALUES ('136', 'special', '4699', '13', '0', '1');
INSERT INTO `buffer_buff_list` VALUES ('137', 'special', '4700', '13', '0', '1');
INSERT INTO `buffer_buff_list` VALUES ('138', 'special', '4702', '13', '0', '1');
INSERT INTO `buffer_buff_list` VALUES ('139', 'special', '4703', '13', '0', '1');
