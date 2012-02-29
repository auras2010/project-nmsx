
-- Krateis Cube Manager
INSERT INTO `spawnlist` (location,count,npc_templateid,locx,locy,locz,randomx,randomy,heading,respawn_delay,loc_id,periodOfDay) VALUES 
('', 1, 32503, -70586, -71062, -1416, 0, 0, 64703, 60, 0, 0);
DELETE FROM spawnlist WHERE npc_templateid = 35411;
DELETE FROM spawnlist WHERE npc_templateid = 35412;
DELETE FROM spawnlist WHERE npc_templateid = 35413;
DELETE FROM spawnlist WHERE npc_templateid = 35414;
DELETE FROM spawnlist WHERE npc_templateid = 35415;
DELETE FROM spawnlist WHERE npc_templateid = 35416;
DELETE FROM spawnlist WHERE npc_templateid = 35410;
DELETE FROM spawnlist WHERE npc_templateid = 35368;
DELETE FROM spawnlist WHERE npc_templateid = 35369;
DELETE FROM spawnlist WHERE npc_templateid = 35370;
DELETE FROM spawnlist WHERE npc_templateid = 35371;

INSERT INTO `spawnlist` VALUES 
('',1,35640,57957,-28134,594,0,0,1000,60,0,0),
('',1,35641,57816,-29603,568,0,0,1000,60,0,0),
('',1,35641,58144,-29087,566,0,0,1000,60,0,0),
('',1,35638,57344,-29783,578,0,0,1000,60,0,0),
('',1,35604,143944,-119196,-2136,0,0,1000,0,0,0),
('',1,35604,143944,-119196,-2136,0,0,1000,60,0,0),
('',1,35603,152924,-126604,-2304,0,0,33000,60,0,0),
('',1,32007,140968,-123600,-1904,0,0,18000,60,0,0),
('',1,35602,140704,-124020,-1904,0,0,34000,60,0,0),
('',1,35602,140732,-123796,-1904,0,0,2000,60,0,0),
('',1,35605,140824,-124844,-1864,0,0,10000,60,0,0),
('',1,35601,141152,-124272,-1864,0,0,10000,60,0,0),
('',1,35640,58024,-25744,595,0,0,49000,60,0,0),
('',1,35639,58128,-32000,301,0,0,0,60,0,0);

INSERT INTO `spawnlist` VALUES 
('Wood NPC',1,32593,147016,23738,-1997,0,0,16384,60,0,0),
('Lawrance NPC',1,32595,152485,-57517,-3427,0,0,16384,60,0,0),
('Sophia NPC',1,32596,37103,-49871,-1134,0,0,16384,60,0,0);

INSERT INTO `spawnlist` VALUES
('monastery_of_silence',1,22792,110201,-81754,-3397,0,0,32034,10,0,0),
('monastery_of_silence',1,22792,109762,-81868,-3397,0,0,36423,10,0,0),
('monastery_of_silence',1,22792,109763,-82835,-3396,0,0,49036,10,0,0),
('monastery_of_silence',1,22792,109760,-83755,-3397,0,0,49104,10,0,0),
('monastery_of_silence',1,22792,110102,-84635,-3397,0,0,55864,10,0,0),
('monastery_of_silence',1,22792,110057,-85552,-3400,0,0,48414,10,0,0),
('monastery_of_silence',1,22792,110792,-85350,-3397,0,0,3217,10,0,0),
('monastery_of_silence',1,22792,111804,-81723,-3397,0,0,1138,10,0,0),
('monastery_of_silence',1,22792,112047,-82739,-3397,0,0,32892,10,0,0),
('monastery_of_silence',1,22792,112596,-82666,-3397,0,0,8284,10,0,0),
('monastery_of_silence',1,22792,112929,-81822,-3397,0,0,58383,10,0,0),
('monastery_of_silence',1,22792,109894,-80402,-3525,0,0,25698,10,0,0),
('monastery_of_silence',1,22792,109167,-80035,-3525,0,0,27396,10,0,0);

UPDATE `spawnlist` SET `locx` = 119120, `locy` = -80576, `locz` = -2694 WHERE `npc_templateid` = 32757;
-- Anays spawn
UPDATE `raidboss_spawnlist` SET `boss_id` = 25701 WHERE `boss_id` = 29096;