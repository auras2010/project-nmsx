
-- Chamber of Delusion mobs should not drop
DELETE FROM `droplist` WHERE `mobId` > 22707 AND `mobId` < 22741;

-- Gracia Epilogue : Chamber of Delusion
REPLACE INTO `droplist` VALUES
-- Invader Warrior of Nightmare (81)
(22708,57,11663,23327,0,700000), -- Adena
(22708,1879,5,10,1,100000), -- Cokes
(22708,1885,1,3,1,90000), -- High Grade Suede
(22708,9628,1,1,1,20000), -- Leonard
(22708,9629,1,1,1,20000), -- Adamantine
(22708,9630,1,1,1,20000), -- Orichalcum
(22708,9533,1,3,2,100), -- Sealed Dynasty Gauntlet Piece
(22708,9543,1,3,2,100), -- Sealed Dynasty Glove Piece
(22708,9538,1,3,2,100), -- Sealed Dynasty Leather Glove Piece
(22708,9539,1,3,2,100), -- Sealed Dynasty Leather Boots Piece
(22708,9534,1,3,2,100), -- Sealed Dynasty Boot Piece
(22708,9544,1,3,2,100), -- Sealed Dynasty Shoes Piece
(22708,9547,1,1,200,1000), -- Water Stone
(22708,9517,1,1,2,10), -- Sealed Dynasty Gauntlets
(22708,9522,1,1,2,10), -- Sealed Dynasty Leather Gloves
(22708,9527,1,1,2,10), -- Sealed Dynasty Gloves
(22708,9528,1,1,2,10), -- Sealed Dynasty Shoes
(22708,9523,1,1,2,10), -- Sealed Dynasty Leather Boots
(22708,9518,1,1,2,10), -- Sealed Dynasty Boots
(22708,9544,1,1,-1,60000), -- Sealed Dynasty Shoes Piece
(22708,10114,1,1,-1,60000), -- Sealed Dynasty Sigil Piece
(22708,9542,1,1,-1,60000), -- Sealed Dynasty Circlet Piece
-- Invader Healer of Nightmare (81)
(22709,57,11687,23373,0,700000), -- Adena
(22709,4043,1,1,1,100000), -- Asofe
(22709,4042,1,1,1,50000), -- Enria
(22709,4040,1,1,1,20000), -- Mold Lubricant
(22709,1895,1,5,1,250000), -- Metallic Fiber
(22709,9545,1,1,2,100), -- Sealed Dynasty Shield Piece
(22709,10114,1,1,2,100), -- Sealed Dynasty Sigil Piece
(22709,9542,1,1,2,100), -- Sealed Dynasty Circlet Piece
(22709,9532,1,1,2,100), -- Sealed Dynasty Helmet Piece
(22709,9537,1,1,2,100), -- Sealed Dynasty Leather Helmet Piece
(22709,9516,1,1,2,10), -- Sealed Dynasty Helmet
(22709,9521,1,1,2,10), -- Sealed Dynasty Leather Helmet
(22709,9526,1,1,2,10), -- Sealed Dynasty Circlet
(22709,12812,1,1,2,10), -- Dynasty Sigil
(22709,9441,1,1,2,10), -- Dynasty Shield
(22709,9548,1,1,200,1000), -- Earth Stone
(22709,1882,1,3,-1,125000), -- Leather
(22709,9530,1,1,-1,31250), -- Sealed Dynasty Breast Plate Piece
(22709,5162,1,1,-1,62500), -- Recipe: Spiritshot (S) Compressed Package (100%)
-- Invader Guide of Nightmare (81)
(22710,57,7448,14896,0,700000), -- Adena
(22710,1879,5,10,1,100000), -- Cokes
(22710,1885,1,3,1,90000), -- High Grade Suede
(22710,9628,1,1,1,20000), -- Leonard
(22710,9629,1,1,1,20000), -- Adamantine
(22710,9630,1,1,1,20000), -- Orichalcum
(22710,9616,1,1,2,50), -- Dynasty Sword Piece
(22710,9617,1,1,2,50), -- Dynasty Blade Piece
(22710,9618,1,1,2,50), -- Dynasty Phantom Piece
(22710,9442,1,1,2,5), -- Dynasty Sword
(22710,9443,1,1,2,5), -- Dynasty Blade
(22710,9444,1,1,2,5), -- Dynasty Phantom
(22710,9549,1,1,200,1000), -- Wind Stone
(22710,1882,1,3,-1,125000), -- Leather
(22710,9628,1,1,-1,30000), -- Leonard
(22710,9530,1,1,-1,31250), -- Sealed Dynasty Breast Plate Piece
-- Invader Destroyer of Nightmare (81)
(22711,57,10259,20518,0,700000), -- Adena
(22711,1895,1,5,1,200000), -- Metallic Fiber
(22711,4040,1,1,1,20000), -- Mold Lubricant
(22711,9628,1,1,1,20000), -- Leonard
(22711,9630,1,1,1,20000), -- Orichalcum
(22711,9550,1,1,200,1000), -- Dark Stone
(22711,9623,1,1,2,50), -- Dynasty Mace Piece
(22711,10546,1,1,2,50), -- Dynasty Staff Fragment
(22711,9622,1,1,2,50), -- Halberd Cudgel Piece
(22711,10547,1,1,2,50), -- Dynasty Crusher Fragment
(22711,9449,1,1,2,5), -- Dynasty Mace
(22711,9448,1,1,2,5), -- Dynasty Cudgel
(22711,10252,1,1,2,5), -- Dynasty Staff
(22711,10253,1,1,2,5), -- Dynasty Crusher
(22711,1808,1,1,-1,60000), -- Recipe: Soulshot: S Grade
(22711,9535,1,1,-1,20000), -- Sealed Dynasty Leather Armor Piece
(22711,9993,1,1,-1,30000), -- Sealed Dynasty Ring Gemstone
-- Invader Assassin of Nightmare (81)
(22712,57,7646,15293,0,700000), -- Adena
(22712,1895,1,5,1,200000), -- Metallic Fiber
(22712,4040,1,1,1,20000), -- Mold Lubricant
(22712,9628,1,1,1,20000), -- Leonard
(22712,9630,1,1,1,20000), -- Orichalcum
(22712,9551,1,1,200,1000), -- Divine Stone
(22712,9624,1,1,2,50), -- Dynasty Bagh-Nakh Piece
(22712,9450,1,1,2,5), -- Dynasty Bagh-Nakh
(22712,1808,1,1,-1,60000), -- Recipe: Soulshot: S Grade
(22712,9628,1,1,-1,30000), -- Leonard
(22712,9531,1,1,-1,30000), -- Sealed Dynasty Gaiter Piece
-- Invader Shaman of Nightmare (81)
(22713,57,9606,19212,0,700000), -- Adena
(22713,1895,1,5,1,200000), -- Metallic Fiber
(22713,4040,1,1,1,20000), -- Mold Lubricant
(22713,9628,1,1,1,20000), -- Leonard
(22713,9630,1,1,1,20000), -- Orichalcum
(22713,1345,100,300,0,150000), -- Shining Arrow
(22713,9619,1,1,2,50), -- Dynasty Bow Piece
(22713,9445,1,1,2,5), -- Dynasty Bow
(22713,9617,1,1,-1,20000), -- Dynasty Blade Piece
(22713,6901,1,1,-1,62500), -- Recipe: Shining Arrow (100%)
(22713,9527,1,1,-1,30000), -- Sealed Dynasty Gloves
-- Invader Archer of Nightmare (81)
(22714,57,7270,14539,0,700000), -- Adena
(22714,1895,1,5,1,200000), -- Metallic Fiber
(22714,4040,1,1,1,20000), -- Mold Lubricant
(22714,9628,1,1,1,20000), -- Leonard
(22714,9630,1,1,1,20000), -- Orichalcum
(22714,960,1,1,2,100), -- Scroll: Enchant Armor (S)
(22714,9620,1,1,2,50), -- Dynasty Knife Piece
(22714,9446,1,1,2,5), -- Dynasty Knife
(22714,9620,1,1,-1,30000), -- Dynasty Knife Piece
(22714,1808,1,1,-1,60000), -- Recipe: Soulshot: S Grade
(22714,9535,1,1,-1,20000), -- Sealed Dynasty Leather Armor Piece
-- Invader Soldier of Nightmare (81)
(22715,57,1817,3635,0,700000), -- Adena
(22715,1879,5,10,1,100000), -- Cokes
(22715,1885,1,3,1,90000), -- High Grade Suede
(22715,9628,1,1,1,20000), -- Leonard
(22715,9629,1,1,1,20000), -- Adamantine
(22715,9630,1,1,1,20000), -- Orichalcum
(22715,9621,1,1,2,50), -- Dynasty Halberd Piece
(22715,9530,1,1,2,100), -- Sealed Dynasty Breast Plate Piece
(22715,9531,1,1,2,100), -- Sealed Dynasty Gaiter Piece
(22715,9447,1,1,2,5), -- Dynasty Halberd
(22715,9514,1,1,2,10), -- Sealed Dynasty Breast Plate
(22715,9515,1,1,2,10), -- Sealed Dynasty Gaiter
(22715,1882,1,3,-1,125000), -- Leather
(22715,9534,1,1,-1,30000), -- Sealed Dynasty Boot Piece
(22715,9541,1,1,-1,20000), -- Sealed Dynasty Stocking Piece
-- Invader Soldier of Nightmare (81)
(22716,57,11663,23327,0,700000), -- Adena
(22716,1879,5,10,1,50000), -- Cokes
(22716,1885,1,3,1,45000), -- High Grade Suede
(22716,9628,1,1,1,10000), -- Leonard
(22716,9629,1,1,1,10000), -- Adamantine
(22716,9630,1,1,1,10000), -- Orichalcum
(22716,9621,1,1,2,25), -- Dynasty Halberd Piece
(22716,9530,1,1,2,50), -- Sealed Dynasty Breast Plate Piece
(22716,9531,1,1,2,50), -- Sealed Dynasty Gaiter Piece
(22716,9447,1,1,2,2), -- Dynasty Halberd
(22716,9514,1,1,2,4), -- Sealed Dynasty Breast Plate
(22716,9515,1,1,2,4), -- Sealed Dynasty Gaiter
-- Invader Disciple of Nightmare (81)
(22717,57,12729,25459,0,700000), -- Adena
(22717,1895,1,5,1,300000), -- Metallic Fiber
(22717,4040,1,1,1,25000), -- Mold Lubricant
(22717,9628,1,1,1,20000), -- Leonard
(22717,9630,1,1,1,20000), -- Orichalcum
(22717,9535,1,1,2,100), -- Sealed Dynasty Leather Armor Piece
(22717,9536,1,1,2,100), -- Sealed Dynasty Leather Leggings Piece
(22717,9519,1,1,2,10), -- Sealed Dynasty Leather Armor
(22717,9520,1,1,2,10), -- Sealed Dynasty Leather Leggings
(22717,9619,1,1,-1,20000), -- Dynasty Bow Piece
(22717,6901,1,1,-1,62500), -- Recipe: Shining Arrow (100%)
(22717,959,1,1,-1,30000), -- Scroll: Enchant Weapon (S)
-- Invader Elite Soldier of Nightmare (81)
(22718,57,13728,27455,0,700000), -- Adena
(22718,1895,1,5,1,300000), -- Metallic Fiber
(22718,4040,1,1,1,25000), -- Mold Lubricant
(22718,9628,1,1,1,20000), -- Leonard
(22718,9630,1,1,1,20000), -- Orichalcum
(22718,6901,1,1,2,10000), -- Recipe: Shining Arrow (100%)
(22718,9540,1,1,2,100), -- Sealed Dynasty Tunic Piece
(22718,9541,1,1,2,100), -- Sealed Dynasty Stocking Piece
(22718,9524,1,1,2,5), -- Sealed Dynasty Tunic
(22718,9525,1,1,2,5), -- Sealed Dynasty Stockings
(22718,9628,1,1,-1,30000), -- Leonard
(22718,9543,1,1,-1,20000), -- Sealed Dynasty Glove Piece
(22718,9535,1,1,-1,20000), -- Sealed Dynasty Leather Armor Piece
-- Nihil Invader Warrior (82)
(22719,57,23296,46592,0,700000),
(22719,9993,1,1,1,13341),
(22719,9991,1,1,1,9493),
(22719,9992,1,1,1,7285),
(22719,9454,1,1,1,201),
(22719,9452,1,1,1,134),
(22719,9453,1,1,1,100),
(22719,1879,2,4,2,164943),
(22719,1885,1,3,2,123707),
(22719,9628,1,1,2,23286),
(22719,9630,1,1,2,15626),
(22719,9629,1,1,2,12909),
(22719,5157,1,1,2,1320),
(22719,9624,1,1,-1,46703),
(22719,1808,1,1,-1,25707),
(22719,3036,1,1,-1,25707),
-- Nihil Invader Healer (82)
(22720,57,22285,44569,0,700000),
(22720,9533,1,1,1,9264),
(22720,9534,1,1,1,9264),
(22720,9538,1,1,1,9264),
(22720,9539,1,1,1,9264),
(22720,9543,1,1,1,9264),
(22720,9544,1,1,1,9264),
(22720,9517,1,1,1,42),
(22720,9518,1,1,1,42),
(22720,9522,1,1,1,42),
(22720,9523,1,1,1,42),
(22720,9527,1,1,1,42),
(22720,9528,1,1,1,42),
(22720,1879,2,4,2,157781),
(22720,1885,1,3,2,118335),
(22720,9628,1,1,2,22275),
(22720,9630,1,1,2,14948),
(22720,9629,1,1,2,12348),
(22720,5162,1,1,2,1262),
(22720,9628,1,1,-1,433961),
(22720,9534,1,1,-1,397058),
(22720,9630,1,1,-1,291211),
-- Nihil Invader Guide (82)
(22721,57,12291,24583,0,700000),
(22721,1985,1,1,1,26785),
(22721,9545,1,1,1,4994),
(22721,9532,1,1,1,4190),
(22721,9537,1,1,1,4190),
(22721,9542,1,1,1,4190),
(22721,10114,1,1,1,2889),
(22721,9529,1,1,1,24),
(22721,13886,1,1,1,24),
(22721,9516,1,1,1,17),
(22721,9521,1,1,1,17),
(22721,9526,1,1,1,17),
(22721,4040,1,1,2,46994),
(22721,9628,1,1,2,18429),
(22721,9630,1,1,2,12367),
(22721,5167,1,1,2,1044),
(22721,9534,1,1,-1,219003),
(22721,9538,1,1,-1,219003),
(22721,3957,1,1,-1,13564),
-- Nihil Invader Destroyer (82)
(22722,57,22221,44443,0,700000),
(22722,9616,1,1,1,2147),
(22722,9617,1,1,1,2147),
(22722,9618,1,1,1,2147),
(22722,9442,1,1,1,7),
(22722,9443,1,1,1,7),
(22722,9444,1,1,1,7),
(22722,1895,3,7,2,194195),
(22722,4043,1,1,2,113280),
(22722,4040,1,1,2,67968),
(22722,4042,1,1,2,56640),
(22722,9625,1,1,2,680),
(22722,1882,8,17,-1,980859),
(22722,9538,1,1,-1,395934),
(22722,5157,1,1,-1,24521),
-- Nihil Invader Assassin (82)
(22723,57,12517,25035,0,700000),
(22723,10546,1,1,1,910),
(22723,10547,1,1,1,910),
(22723,9622,1,1,1,878),
(22723,9623,1,1,1,878),
(22723,9448,1,1,1,3),
(22723,9449,1,1,1,3),
(22723,10252,1,1,1,3),
(22723,10253,1,1,1,3),
(22723,1879,1,3,2,132939),
(22723,1885,1,1,2,132939),
(22723,9628,1,1,2,12512),
(22723,9630,1,1,2,8396),
(22723,9629,1,1,2,6936),
(22723,9626,1,1,2,213),
(22723,1882,4,10,-1,986635),
(22723,9530,1,1,-1,67894),
(22723,5162,1,1,-1,13813),
-- Nihil Invader Shaman (82)
(22724,57,22348,44697,0,700000),
(22724,9624,1,1,1,6272),
(22724,9450,1,1,1,21),
(22724,1895,3,7,2,195304),
(22724,4043,1,1,2,113927),
(22724,4040,1,1,2,68356),
(22724,4042,1,1,2,56964),
(22724,9625,1,1,2,684),
(22724,9535,1,1,-1,148904),
(22724,10547,1,1,-1,46434),
(22724,5167,1,1,-1,24661),
-- Nihil Invader Archer (82)
(22725,57,21864,43728,0,700000),
(22725,9619,1,1,1,6381),
(22725,9445,1,1,1,20),
(22725,1895,3,7,2,191072),
(22725,4040,1,1,2,66875),
(22725,9628,1,1,2,26226),
(22725,9630,1,1,2,17599),
(22725,9626,1,1,2,446),
(22725,1882,8,17,-1,965084),
(22725,9533,1,1,-1,389566),
(22725,9539,1,1,-1,389566),
-- Nihil Invader Soldier (82)
(22726,57,22221,44443,0,700000),
(22726,9620,1,1,1,6436),
(22726,9446,1,1,1,21),
(22726,1879,2,4,2,157334),
(22726,1885,1,3,2,118000),
(22726,9546,1,1,2,22656),
(22726,9628,1,1,2,22212),
(22726,9630,1,1,2,14905),
(22726,9629,1,1,2,12313),
(22726,9623,1,1,-1,44549),
(22726,5272,1,1,-1,24521),
(22726,5277,1,1,-1,24521),
-- Nihil Invader Soldier (82)
(22727,57,5555,11111,0,700000),
(22727,9621,1,1,1,1618),
(22727,9447,1,1,1,5),
(22727,1879,1,1,2,118000),
(22727,1885,1,1,2,59000),
(22727,9547,1,1,2,5664),
(22727,9628,1,1,2,5553),
(22727,9630,1,1,2,3726),
(22727,9629,1,1,2,3078),
(22727,9533,1,1,-1,98983),
(22727,10547,1,1,-1,11543),
(22727,960,1,1,-1,5517),
-- Nihil Invader Disciple (82)
(22728,57,22221,44443,0,700000),
(22728,9531,1,1,1,24954),
(22728,9530,1,1,1,8436),
(22728,9515,1,1,1,51), 
(22728,9514,1,1,1,32),
(22728,1895,3,7,2,194195),
(22728,4040,1,1,2,67968),
(22728,9548,1,1,2,27187),
(22728,9628,1,1,2,26654),
(22728,9630,1,1,2,17886),
(22728,1882,8,17,-1,980859),
(22728,9530,1,1,-1,120530),
(22728,5277,1,1,-1,24521),
-- Nihil Invader Elite Soldier (82)
(22729,57,41652,83304,0,700000),
(22729,9536,1,1,1,28554),
(22729,9535,1,1,1,19425),
(22729,9520,1,1,1,127), 
(22729,9519,1,1,1,79), 
(22729,1895,7,13,2,182000),
(22729,4040,1,1,2,127400),
(22729,4042,1,1,2,106167),
(22729,4043,1,3,2,106167),
(22729,9549,1,1,2,50960),
(22729,9993,1,1,-1,511157),
(22729,10546,1,1,-1,86542),
(22729,9623,1,1,-1,83502),
-- Mutant Warrior (84)
(22730,57,47930,95859,0,700000),
(22730,9541,1,1,1,32521), 
(22730,9540,1,1,1,22124), 
(22730,9525,1,1,1,144), 
(22730,9524,1,1,1,90), 
(22730,1879,5,9,2,143949),
(22730,1885,3,5,2,125955),
(22730,9550,1,1,2,48367),
(22730,9628,1,1,2,47418),
(22730,9630,1,1,2,31820),
(22730,9629,1,1,2,26286),
(22730,9630,1,1,-1,627395),
(22730,10405,1,1,-1,76413),
(22730,959,1,1,-1,4768),
-- Mutant Healer (84)
(22731,57,45236,90472,0,700000),
(22731,9993,1,1,1,25639),
(22731,9991,1,1,1,18244),
(22731,9992,1,1,1,14002),
(22731,9454,1,1,1,386),
(22731,9452,1,1,1,257),
(22731,9453,1,1,1,193),
(22731,1895,7,13,2,195637),
(22731,4040,1,1,2,136946),
(22731,4042,1,1,2,114122),
(22731,4043,1,3,2,114122),
(22731,9551,1,1,2,54778),
(22731,9532,1,1,-1,606812),
(22731,9630,1,1,-1,592137),
(22731,10402,1,1,-1,72119),
-- Mutant Guide (84)
(22732,57,45364,90727,0,700000),
(22732,9533,1,1,1,18665),
(22732,9534,1,1,1,18665),
(22732,9538,1,1,1,18665),
(22732,9539,1,1,1,18665),
(22732,9543,1,1,1,18665),
(22732,9544,1,1,1,18665),
(22732,9517,1,1,1,85),
(22732,9518,1,1,1,85),
(22732,9522,1,1,1,85),
(22732,9523,1,1,1,85),
(22732,9527,1,1,1,85),
(22732,9528,1,1,1,85),
(22732,1895,7,13,2,196189),
(22732,4040,1,1,2,137332),
(22732,9628,1,1,2,53856),
(22732,9630,1,1,2,36140),
(22732,959,1,1,2,275),
(22732,10397,1,1,-1,72555), 
(22732,10400,1,1,-1,72323),
(22732,10403,1,1,-1,72323),
-- Mutant Destroyer (84)
(22733,57,47802,95604,0,700000),
(22733,9545,1,1,1,21145),
(22733,9532,1,1,1,17739),
(22733,9537,1,1,1,17739),
(22733,9542,1,1,1,17739),
(22733,10114,1,1,1,12232),
(22733,9529,1,1,1,103),
(22733,13886,1,1,1,103),
(22733,9516,1,1,1,72),
(22733,9521,1,1,1,72),
(22733,9526,1,1,1,72),
(22733,1879,5,9,2,143566),
(22733,1885,3,5,2,125620),
(22733,9628,1,1,2,47292),
(22733,9630,1,1,2,31736),
(22733,9629,1,1,2,26216),
(22733,960,1,1,2,2412),
(22733,9542,1,1,-1,641232),
(22733,9630,1,1,-1,625725),
(22733,9991,1,1,-1,418133),
-- Mutant Assassin (84)
(22734,57,22356,44712,0,700000),
(22734,10397,1,1,1,1649),
(22734,10399,1,1,1,1649),
(22734,10400,1,1,1,1643),
(22734,10215,1,1,1,5),
(22734,10217,1,1,1,5),
(22734,10218,1,1,1,5),
(22734,1879,2,4,2,156667),
(22734,1885,1,3,2,117500),
(22734,9628,1,1,2,22118),
(22734,9630,1,1,2,14842),
(22734,9629,1,1,2,12261),
(22734,5272,1,1,2,1253),
(22734,9542,1,1,-1,299893),
(22734,9530,1,1,-1,121466),
(22734,1808,1,1,-1,24712),
-- Mutant Shaman (84)
(22735,57,45364,90727,0,700000),
(22735,10402,1,1,1,5002),
(22735,10404,1,1,1,5002),
(22735,10220,1,1,1,15),
(22735,10222,1,1,1,15),
(22735,1895,7,13,2,196189),
(22735,4040,1,1,2,137332),
(22735,9628,1,1,2,53856),
(22735,9630,1,1,2,36140),
(22735,5272,1,1,2,3052), 
(22735,10114,1,1,-1,419597),
(22735,6901,1,1,-1,112823),
(22735,960,1,1,-1,45129),
-- Mutant Overlord (84)
(22736,57,21358,42717,0,700000),
(22736,10403,1,1,1,4710),
(22736,10221,1,1,1,14),
(22736,1895,3,7,2,184742),
(22736,4040,1,1,2,64660),
(22736,9628,1,1,2,25357),
(22736,9630,1,1,2,17016),
(22736,5282,1,1,2,1437),
(22736,9532,1,1,-1,286510),
(22736,9630,1,1,-1,279581),
(22736,10399,1,1,-1,34161),
-- Mutant Soldier (84)
(22737,57,45107,90214,0,700000),
(22737,10405,1,1,1,9947), 
(22737,10223,1,1,1,30),
(22737,1895,7,13,2,108377),
(22737,1879,4,8,2,105366),
(22737,1885,2,4,2,105366),
(22737,4040,1,1,2,75864),
(22737,9628,1,1,2,29751),
(22737,9630,1,1,2,19964),
(22737,9629,1,1,2,16492),
(22737,6901,1,1,2,3793),
(22737,5157,1,1,2,1686),
(22737,10398,1,1,-1,72144),
(22737,10399,1,1,-1,72144),
(22737,10404,1,1,-1,71913),
-- Mutant Soldier (84)
(22738,57,11277,22554,0,700000),
(22738,10398,1,1,1,2495), 
(22738,10216,1,1,1,8), 
(22738,1895,2,4,2,90315),
(22738,1879,1,3,2,79025),
(22738,1885,1,1,2,79025),
(22738,4040,1,1,2,18966),
(22738,9628,1,1,2,7438),
(22738,9630,1,1,2,4991),
(22738,9629,1,1,2,4123),
(22738,6901,1,1,2,948),
(22738,5157,1,1,2,421),
(22738,9545,1,1,-1,180310),
(22738,9622,1,1,-1,22645),
(22738,5272,1,1,-1,12465),
-- Mutant Disciple (84)
(22739,57,82905,165810,0,700000),
(22739,10401,1,1,1,18283),
(22739,10219,1,1,1,55),
(22739,1879,7,15,2,190140),
(22739,1885,4,8,2,174295),
(22739,9628,1,1,2,98425),
(22739,9630,1,1,2,66048),
(22739,9629,1,1,2,54562),
(22739,9619,1,1,-1,173125),
(22739,10398,1,1,-1,132600),
(22739,5277,1,1,-1,91641),
-- Mutant Elite Soldier (84)
(22740,57,87241,174482,0,700000),
(22740,9531,1,1,1,96968),
(22740,9530,1,1,1,32782),
(22740,9515,1,1,1,197),
(22740,9514,1,1,1,123),
(22740,1879,8,16,2,131007),
(22740,1885,4,8,2,131007),
(22740,9628,1,1,2,73981),
(22740,9630,1,1,2,49645),
(22740,9629,1,1,2,41011),
(22740,5167,1,1,2,4192),
(22740,1345,2516,5032,3,49987),
(22740,9620,1,1,-1,180814),
(22740,10401,1,1,-1,139087),
(22740,5282,1,1,-1,96434),
-- Disciple Solina (82)
(22792,57,1033,2086,0,700000), -- Adena
(22792,1895,1,1,2,74287), -- Metallic Fiber
(22792,4043,1,1,2,8667), -- Asofe
(22792,4040,1,1,2,5200), -- Mold Lubricant
(22792,4042,1,1,2,4333), -- Enria
(22792,14166,1,1,200,1526), -- Life Stone 84 lvl
(22792,14167,1,1,200,3140), -- Mid Life Stone 84 lvl
(22792,14168,1,1,200,31), -- High Life Stone 84 lvl
(22792,15540,1,1,3,1000), -- Unlit Torchlight
(22792,15644,1,1,4,621), -- Vesper Thrower Piece
(22792,959,1,1,5,10), -- Scroll: Enchant Weapon (S)
(22792,13467,1,1,6,1), -- Vesper Thrower
(22792,9628,1,1,-1,33117), -- Leonard
(22792,15644,1,1,-1,2519), -- Vesper Thrower Piece
(22792,15825,1,1,-1,295), -- Vesper Thrower Recipe (60%)
-- Solina Knight Captain (83)
(18910,57,1730,3492,0,700000), -- Adena
(18910,1895,1,1,2,142119), -- Metallic Fiber
(18910,4043,1,1,2,16580), -- Asofe
(18910,4040,1,1,2,9948), -- Mold Lubricant
(18910,4042,1,1,2,8290), -- Enria
(18910,10483,1,1,200,3454), -- Life Stone 82 lvl
(18910,10484,1,1,200,864), -- Mid Life Stone 82 lvl
(18910,10485,1,1,200,87), -- High Life Stone 82 lvl
(18910,15672,1,1,100,2224), -- S Vesper Leather Boots Piece
(18910,15674,1,1,100,2224), -- S Vesper gloves piece
(18910,15671,1,1,100,2224), -- S Vesper Leather Gloves Piece
(18910,15667,1,1,100,2224), -- S Vesper Gauntler Piece
(18910,15675,1,1,100,2224), -- S Vesper Shoes Piece
(18910,14117,1,1,300,5), -- S Vesper Shoes
(18910,14114,1,1,300,5), -- S Vesper Leather Boots
(18910,14116,1,1,300,5), -- S Vesper Gloves
(18910,14113,1,1,300,5), -- S Vesper Leather Gloves
(18910,14109,1,1,300,5), -- S Vesper Gauntlet
(18910,14110,1,1,300,5), -- S Vesper Boots
(18910,15667,1,1,-1,13451), -- S Vesper Gauntlet Piece
(18910,15675,1,1,-1,13541), -- S Vesper Shoes Piece
(18910,15672,1,1,-1,13541), -- S Vesper Leather Boots Piece
(18910,15671,1,1,-1,13541), -- S Vesper Gloves Piece
(18910,15801,1,1,-1,1515), -- S Vesper Gauntlet Rec (60%)
(18910,15802,1,1,-1,1515), -- S Vesper Leather Gloves Rec (60%)
(18910,15803,1,1,-1,1515), -- S Vesper Gloves Rec (60%)
(18910,15806,1,1,-1,1515), -- S Vesper Shoes Rec (60%)
(18910,15805,1,1,-1,1515), -- S Vesper Leather Boots Rec (60%)
(18910,15804,1,1,-1,1515), -- S Vesper Boots Rec (60%)
-- Solina Knight
(18909,57,1300,2268,0,700000), -- Adena
(18909,15667,1,1,1,2000), -- Sealed Vesper Gauntlet Piece
(18909,15671,1,1,1,2000), -- Sealed Vesper Leather Gloves Piece
(18909,15674,1,1,1,2000), -- Sealed Vesper Gloves Piece
(18909,15668,1,1,1,2000), -- Sealed Vesper Boots Piece
(18909,15672,1,1,1,2000), -- Sealed Vesper Leather Boots Piece
(18909,15675,1,1,1,2000), -- Sealed Vesper Shoes Piece
(18909,14109,1,1,1,6), -- Sealed Vesper Gauntlet
(18909,14113,1,1,1,6), -- Sealed Vesper Leather Gloves
(18909,14116,1,1,1,6), -- Sealed Vesper Gloves
(18909,14110,1,1,1,6), -- Sealed Vesper Boots
(18909,14114,1,1,1,6), -- Sealed Vesper Leather Boots
(18909,14117,1,1,1,6), -- Sealed Vesper Shoes
(18909,15801,1,1,2,60), -- Recipe - Sealed Vesper Gauntlet (60%)
(18909,15802,1,1,2,60), -- Recipe - Sealed Vesper Leather Gloves (60%)
(18909,15803,1,1,2,60), -- Recipe - Sealed Vesper Gloves (60%)
(18909,15804,1,1,2,60), -- Recipe - Sealed Vesper Boots (60%)
(18909,15805,1,1,2,60), -- Recipe - Sealed Vesper Leather Boots (60%)
(18909,15806,1,1,2,60), -- Recipe - Sealed Vesper Shoes (60%)
(18909,1879,1,1,2,58200), -- Cokes
(18909,1885,1,1,2,26200), -- High Grade Suede
(18909,9628,1,1,2,3200), -- Leonard
(18909,9629,1,1,2,1400), -- Adamantine
(18909,9630,1,1,2,1600), -- Orichalcum
(18909,9573,1,1,200,4500), -- Life Stone: level 80
(18909,9574,1,1,200,1100), -- Mid-Grade Life Stone: level 80
(18909,9575,1,1,200,110), -- High-Grade Life Stone: level 80
(18909,15801,1,1,-1,600), -- Recipe - Sealed Vesper Gauntlet (60%)
(18909,15802,1,1,-1,600), -- Recipe - Sealed Vesper Leather Gloves (60%)
(18909,15803,1,1,-1,600), -- Recipe - Sealed Vesper Gloves (60%)
(18909,15804,1,1,-1,600), -- Recipe - Sealed Vesper Boots (60%)
(18909,15805,1,1,-1,600), -- Recipe - Sealed Vesper Leather Boots (60%)
(18909,15806,1,1,-1,600), -- Recipe - Sealed Vesper Shoes (60%)
(18909,15667,1,1,-1,6000), -- Sealed Vesper Gauntlet Piece
(18909,15671,1,1,-1,6000), -- Sealed Vesper Leather Gloves Piece
(18909,15674,1,1,-1,6000), -- Sealed Vesper Gloves Piece
(18909,15668,1,1,-1,6000), -- Sealed Vesper Boots Piece
(18909,15672,1,1,-1,6000), -- Sealed Vesper Leather Boots Piece
(18909,15675,1,1,-1,6000), -- Sealed Vesper Shoes Piece
(18909,9630,1,1,-1,40000); -- Orichalcum
-- 
REPLACE INTO `droplist` VALUES
(25634,960,1,1,2,489799), -- Scroll: Enchant Armor (S)
(25634,6700,14,42,0,269254), -- Sealed Tateossian Necklace Chain
(25634,6725,1,1,0,57775), -- Sealed Tateossian Ring
(25634,959,1,1,2,48980), -- Scroll: Enchant Weapon (S)
(25634,6724,1,1,0,38516), -- Sealed Tateossian Earring
(25634,6726,1,1,0,28881), -- Sealed Tateossian Necklace
(25634,6577,1,1,2,8164), -- Blessed Scroll: Enchant Weapon (S)
(25634,6699,40,120,0,171573), -- Sealed Tateossian Ring Gem
(25634,6698,81,243,0,76356), -- Sealed Tateossian Earring Part
(25634,6364,1,1,1,6285), -- Forgotten Blade
(25634,6372,1,1,1,6285), -- Heaven's Divider
(25634,6688,8,22,1,193620), -- Forgotten Blade Edge
(25634,6696,4,12,1,368805), -- Heavens Divider Edge
-- 
(25690,960,1,1,1,12000), -- Scroll: Enchant Armor (S)
(25690,9617,1,1,1,339852), -- Dynasty Blade Piece
(25690,9616,1,1,1,339852), -- Dynasty Sword Piece
(25690,9618,1,1,1,339852), -- Dynasty Phantom Piece
(25690,959,1,1,1,17000), -- Scroll: Enchant Weapon (S)
(25690,9992,2,6,1,41090), -- Sealed Dynasty Necklace Gemstone
(25690,9993,5,15,1,10416), -- Sealed Dynasty Ring Gemstone
(25690,9991,1,3,1,178007), -- Sealed Dynasty Earring Gemstone
(25690,6577,1,1,1,17648), -- Blessed Scroll: Enchant Weapon (S)
(25690,9443,1,1,2,4856), -- Dynasty Blade
(25690,9442,1,1,2,4856), -- Dynasty Sword
(25690,9444,1,1,2,4856), -- Dynasty Phantom
(25690,9452,1,1,2,6461), -- Sealed Dynasty Earring
(25690,9453,1,1,2,2843), -- Sealed Dynasty Necklace
(25690,9454,1,1,2,13691), -- Sealed Dynasty Ring
-- 
(25691,960,1,1,1,14000), -- Scroll: Enchant Armor (S)
(25691,9617,1,2,1,359852), -- Dynasty Blade Piece
(25691,9616,1,2,1,359852), -- Dynasty Sword Piece
(25691,9618,1,2,1,359852), -- Dynasty Phantom Piece
(25691,959,1,1,1,19000), -- Scroll: Enchant Weapon (S)
(25691,9992,2,6,1,51090), -- Sealed Dynasty Necklace Gemstone
(25691,9993,5,15,1,14416), -- Sealed Dynasty Ring Gemstone
(25691,9991,1,3,1,198007), -- Sealed Dynasty Earring Gemstone
(25691,6577,1,1,1,19648), -- Blessed Scroll: Enchant Weapon (S)
(25691,9443,1,1,2,5856), -- Dynasty Blade
(25691,9442,1,1,2,5856), -- Dynasty Sword
(25691,9444,1,1,2,5856), -- Dynasty Phantom
(25691,9452,1,1,2,7461), -- Sealed Dynasty Earring
(25691,9453,1,1,2,3843), -- Sealed Dynasty Necklace
(25691,9454,1,1,2,14691), -- Sealed Dynasty Ring
-- 
(25692,960,1,1,1,16000), -- Scroll: Enchant Armor (S)
(25692,9617,1,3,1,379852), -- Dynasty Blade Piece
(25692,9616,1,3,1,379852), -- Dynasty Sword Piece
(25692,9618,1,3,1,379852), -- Dynasty Phantom Piece
(25692,959,1,1,1,21000), -- Scroll: Enchant Weapon (S)
(25692,9992,2,6,1,61090), -- Sealed Dynasty Necklace Gemstone
(25692,9993,5,15,1,24416), -- Sealed Dynasty Ring Gemstone
(25692,9991,1,3,1,218007), -- Sealed Dynasty Earring Gemstone
(25692,6577,1,1,1,21648), -- Blessed Scroll: Enchant Weapon (S)
(25692,9443,1,1,2,6856), -- Dynasty Blade
(25692,9442,1,1,2,6856), -- Dynasty Sword
(25692,9444,1,1,2,6856), -- Dynasty Phantom
(25692,9452,1,1,2,8461), -- Sealed Dynasty Earring
(25692,9453,1,1,2,4843), -- Sealed Dynasty Necklace
(25692,9454,1,1,2,15691), -- Sealed Dynasty Ring
-- 
(25693,960,1,1,1,18000), -- Scroll: Enchant Armor (S)
(25693,9617,2,6,1,399852), -- Dynasty Blade Piece
(25693,9616,2,6,1,399852), -- Dynasty Sword Piece
(25693,9618,2,6,1,399852), -- Dynasty Phantom Piece
(25693,959,1,1,1,23000), -- Scroll: Enchant Weapon (S)
(25693,9992,4,12,1,81090), -- Sealed Dynasty Necklace Gemstone
(25693,9993,10,30,1,44416), -- Sealed Dynasty Ring Gemstone
(25693,9991,2,6,1,238007), -- Sealed Dynasty Earring Gemstone
(25693,6577,1,1,1,23648), -- Blessed Scroll: Enchant Weapon (S)
(25693,9443,1,1,2,7856), -- Dynasty Blade
(25693,9442,1,1,2,7856), -- Dynasty Sword
(25693,9444,1,1,2,7856), -- Dynasty Phantom
(25693,9452,1,1,2,9461), -- Sealed Dynasty Earring
(25693,9453,1,1,2,5843), -- Sealed Dynasty Necklace
(25693,9454,1,1,2,16691), -- Sealed Dynasty Ring
-- 
(25694,15341,1,3,0,1000000), -- Guiding Tea Leaves
(25694,960,1,1,1,20000), -- Scroll: Enchant Armor (S)
(25694,9617,3,9,1,419852), -- Dynasty Blade Piece
(25694,9616,3,9,1,419852), -- Dynasty Sword Piece
(25694,9618,3,9,1,419852), -- Dynasty Phantom Piece
(25694,959,1,1,1,25000), -- Scroll: Enchant Weapon (S)
(25694,9992,7,24,1,101090), -- Sealed Dynasty Necklace Gemstone
(25694,9993,20,60,1,64416), -- Sealed Dynasty Ring Gemstone
(25694,9991,4,13,1,258007), -- Sealed Dynasty Earring Gemstone
(25694,6577,1,1,1,25648), -- Blessed Scroll: Enchant Weapon (S)
(25694,9443,1,1,2,8856), -- Dynasty Blade
(25694,9442,1,1,2,8856), -- Dynasty Sword
(25694,9444,1,1,2,8856), -- Dynasty Phantom
(25694,9452,1,1,2,10461), -- Sealed Dynasty Earring
(25694,9453,1,1,2,6843), -- Sealed Dynasty Necklace
(25694,9454,1,1,2,17691), -- Sealed Dynasty Ring
-- 
(25695,15341,2,3,0,1000000), -- Guiding Tea Leaves
(25695,960,1,3,1,350000), -- Scroll: Enchant Armor (S)
(25695,959,1,3,1,300000), -- Scroll: Enchant Weapon (S)
(25695,9992,14,42,1,101090), -- Sealed Dynasty Necklace Gemstone
(25695,9993,40,120,1,64416), -- Sealed Dynasty Ring Gemstone
(25695,9991,9,27,1,258007), -- Sealed Dynasty Earring Gemstone
(25695,10397,3,9,1,390226), -- Icarus Sawsword Piece
(25695,10399,3,9,1,390226), -- Icarus Spirit Piece
(25695,10400,3,9,1,390226), -- Icarus Heavy Arms Piece
(25695,6577,1,1,1,30648), -- Blessed Scroll: Enchant Weapon (S)
(25695,9452,1,1,2,14461), -- Sealed Dynasty Earring
(25695,9453,1,1,2,10843), -- Sealed Dynasty Necklace
(25695,9454,1,1,2,21691), -- Sealed Dynasty Ring
(25695,10215,1,1,2,4976), -- Icarus Sawsword
(25695,10217,1,1,2,4976), -- Icarus Spirit
(25695,10218,1,1,2,4976); -- Icarus Heavy Arms

REPLACE INTO `droplist` VALUES
-- Cohemenes
(25634,960,1,1,2,489799), -- Scroll: Enchant Armor (S)
(25634,6700,14,42,0,269254), -- Sealed Tateossian Necklace Chain
(25634,6725,1,1,0,57775), -- Sealed Tateossian Ring
(25634,959,1,1,2,48980), -- Scroll: Enchant Weapon (S)
(25634,6724,1,1,0,38516), -- Sealed Tateossian Earring
(25634,6726,1,1,0,28881), -- Sealed Tateossian Necklace
(25634,6577,1,1,2,8164), -- Blessed Scroll: Enchant Weapon (S)
(25634,6699,40,120,0,171573), -- Sealed Tateossian Ring Gem
(25634,6698,81,243,0,76356), -- Sealed Tateossian Earring Part
(25634,6364,1,1,1,6285), -- Forgotten Blade
(25634,6372,1,1,1,6285), -- Heaven's Divider
(25634,6688,8,22,1,193620), -- Forgotten Blade Edge
(25634,6696,4,12,1,368805), -- Heavens Divider Edge
-- Ekimus
(29150,14117,1,1,0,296000), --  Sealed Vesper Shoes
(29150,9528,1,1,1,296000), --  Sealed Dynasty Shoes
(29150,9516,1,1,3,294000), --  Sealed Dynasty Helmet
(29150,9521,1,1,2,294000), --  Sealed Dynasty Leather Helmet
(29150,9526,1,1,1,294000), --  Sealed Dynasty Circlet
(29150,13886,1,1,1,294000), --  Sealed Dynasty Sigil
(29150,9514,1,1,3,294000), --  Sealed Dynasty Breast Plate
(29150,9519,1,1,2,294000), --  Sealed Dynasty Leather Armor
(29150,9524,1,1,1,294000), --  Sealed Dynasty Tunic
(29150,9515,1,1,3,294000), --  Sealed Dynasty Gaiter
(29150,9517,1,1,3,294000), --  Sealed Dynasty Gauntlets
(29150,9518,1,1,3,294000), --  Sealed Dynasty Boots
(29150,9529,1,1,3,294000), --  Sealed Dynasty Shield
(29150,9520,1,1,2,294000), --  Sealed Dynasty Leather Leggings
(29150,9522,1,1,2,294000), --  Sealed Dynasty Leather Gloves
(29150,9523,1,1,2,294000), --  Sealed Dynasty Leather Boots
(29150,9525,1,1,1,294000), --  Sealed Dynasty Stockings
(29150,9527,1,1,1,294000), --  Sealed Dynasty Gloves
(29150,13143,1,1,4,294000), --  Sealed Vesper Helmet
(29150,13144,1,1,5,294000), --  Sealed Vesper Leather Helmet
(29150,13145,1,1,0,294000), --  Sealed Vesper Circlet
(29150,13887,1,1,0,294000), --  Sealed Vesper Sickle
(29150,14105,1,1,4,294000), --  Sealed Vesper Breastplate
(29150,14106,1,1,5,294000), --  Sealed Vesper Leather Breastplate
(29150,14107,1,1,0,294000), --  Sealed Vesper Tunic
(29150,14108,1,1,4,294000), --  Sealed Vesper Gaiters
(29150,14109,1,1,4,294000), --  Sealed Vesper Gauntlet
(29150,14110,1,1,4,294000), --  Sealed Vesper Boots
(29150,14111,1,1,4,294000), --  Sealed Vesper Shield
(29150,14112,1,1,5,294000), --  Sealed Vesper Leather Leggings
(29150,14113,1,1,5,294000), --  Sealed Vesper Leather Gloves
(29150,14114,1,1,5,294000), -- Sealed Vesper Leather Boots
(29150,14115,1,1,0,294000), --  Sealed Vesper Stockings
(29150,14116,1,1,0,294000), --  Sealed Vesper Gloves
(29150,14160,1,1,6,350000), --  Sealed Vesper Earring
(29150,14162,1,1,6,350000), --  Sealed Vesper Ring
(29150,14161,1,1,6,300000), --  Sealed Vesper Necklace
(29150,10552,1,1,7,300000), --  Forgotten Scroll - Fighter's Will
(29150,10553,1,1,7,300000), --  Forgotten Scroll - Archer's Will
(29150,14219,1,1,7,300000), --  Forgotten Scroll - Magician's Will
(29150,13893,1,1,8,100000), --  Sealed Holy Spirit's Cloak
-- Failan\'s Guard
(22422,57,7033,14107,0,700000),
(22422,1876,1,1,2,148014),
(22422,1345,40,120,2,123346),
(22422,1873,2,6,2,59206),
(22422,1868,18,54,2,41115),
(22422,1895,3,9,2,30207),
(22422,4042,1,1,2,12334),
(22422,5480,1,1,1,6643),
(22422,5481,1,1,1,6643),
(22422,9599,1,1,100,616700),
(22422,9551,1,1,200,5921),
(22422,960,1,1,2,296),
(22422,5323,1,1,1,45),
(22422,5320,1,1,1,45),
(22422,4042,1,1,-1,200313),
(22422,6689,1,1,-1,25489),
(22422,959,1,1,-1,481),
-- Zaken DayDream
(29176,135,1,1,0,48000),-- Samurai Longsword
(29176,204,1,1,0,12000),-- Deadman\'s Staff
(29176,205,1,1,0,12000),-- Ghoul\'s Staff
(29176,206,1,1,0,15000),-- Demon\'s Staff
(29176,228,1,1,0,48000),-- Crystal Dagger
(29176,266,1,1,0,30000),-- Great Pata
(29176,286,1,1,0,39000),-- Eminence Bow
(29176,299,1,1,0,30000),-- Orcish Poleaxe
(29176,2503,1,1,0,36000),-- Yaksa Mace
(29176,5286,1,1,0,30000),-- Berserker Blade
(29176,78,1,1,1,10000),-- Great Sword
(29176,91,1,1,1,12000),-- Heavy War Axe
(29176,92,1,1,1,13000),-- Sprite\'s Staff
(29176,142,1,1,1,8000),-- Keshanberk
(29176,148,1,1,1,8000),-- Sword of Valhalla
(29176,229,1,1,1,8000),-- Kris
(29176,243,1,1,1,8000),-- Hell Knife
(29176,267,1,1,1,10000),-- Arthro Nail
(29176,284,1,1,1,13000),-- Dark Elven Long Bow
(29176,300,1,1,1,10000),-- Great Axe
(29176,357,1,1,2,50000),-- Zubei\'s Breastplate
(29176,2376,1,1,2,50000),-- Avadon Breastplate
(29176,2384,1,1,2,50000),-- Zubei\'s Leather Shirt
(29176,2397,1,1,2,50000),-- Tunic of Zubei
(29176,383,1,1,3,50000),-- Zubei\'s Gaiters
(29176,2379,1,1,3,50000),-- Avadon Gaiters
(29176,2388,1,1,3,50000),-- Zubei\'s Leather Gaiters
(29176,2402,1,1,3,50000),-- Stockings of Zubei
(29176,2390,1,1,4,100000),-- Avadon Leather Armor
(29176,2406,1,1,4,100000),-- Avadon Robe
(29176,503,1,1,5,30000),-- Zubei\'s Helmet
(29176,554,1,1,5,30000),-- Zubei\'s Boots
(29176,600,1,1,5,30000),-- Avadon Boots
(29176,612,1,1,5,30000),-- Zubei\'s Gauntlets
(29176,633,1,1,5,30000),-- Zubei\'s Shield
(29176,673,1,1,5,30000),-- Avadon Shield
(29176,947,1,1,5,60000),-- Scroll: Enchant Weapon (Grade B)
(29176,2415,1,1,5,30000),-- Avadon Circlet
(29176,2464,1,1,5,30000),-- Avadon Gloves
(29176,57,180000,220000,6,1000000),-- Adena
(29176,57,180000,220000,7,1000000),-- Adena
(29176,57,180000,220000,8,1000000),-- Adena
(29176,57,180000,220000,9,1000000),-- Adena
(29176,57,180000,220000,10,1000000),-- Adena
(29176,57,180000,220000,11,1000000),-- Adena
(29176,57,180000,220000,12,1000000),-- Adena
(29176,6659,1,1,13,1000000),-- Zaken\'s Earring
(29176,8922,1,1,14,100000),-- Pirate Hat
(29176,10295,1,1,15,50000),-- Transform Sealbook - Zaken
(29176,8747,2,4,200,450000),-- High-Grade Life Stone: level 58
(29176,8757,1,2,200,100000),-- Top-Grade Life Stone: level 58
(29176,8748,2,4,200,400000),-- High-Grade Life Stone: level 61
(29176,8758,1,2,200,50000);-- Top-Grade Life Stone: level 61

-- Gracia Epilog : Forge of Gods
REPLACE INTO `droplist` VALUES
-- Scarlet Stakato Worker
('21376', '57', '1883', '3706', '0', '700000'), -- itemname: Adena
('21376', '6371', '1', '1', '1', '9'), -- itemname: Demon Splinter
('21376', '6695', '1', '1', '1', '3952'), -- itemname: Demon Splinter Blade
('21376', '1895', '1', '1', '2', '213014'), -- itemname: Metallic Fiber
('21376', '4040', '1', '1', '2', '14911'), -- itemname: Mold Lubricant
('21376', '9628', '1', '1', '2', '5847'), -- itemname: Leonard
('21376', '9630', '1', '1', '2', '3924'), -- itemname: Orichalcum
('21376', '5167', '1', '1', '2', '331'), -- itemname: Recipe: Blessed Spiritshot (S) Compressed Package (100%)
('21376', '1894', '1', '1', '-1', '708490'), -- itemname: Crafted Leather
('21376', '9992', '1', '1', '-1', '18168'), -- itemname: Sealed Dynasty Necklace Gemstone

-- Scarlet Stakato Soldier
('21377', '57', '1839', '3620', '0', '700000'), -- itemname: Adena
('21377', '6364', '1', '1', '1', '4'), -- itemname: Forgotten Blade
('21377', '6372', '1', '1', '1', '4'), -- itemname: Heaven's Divider
('21377', '6688', '1', '1', '1', '1968'), -- itemname: Forgotten Blade Edge
('21377', '6696', '1', '1', '1', '1999'), -- itemname: Heavens Divider Edge
('21377', '1895', '1', '1', '2', '208027'), -- itemname: Metallic Fiber
('21377', '4040', '1', '1', '2', '14562'), -- itemname: Mold Lubricant
('21377', '9628', '1', '1', '2', '5711'), -- itemname: Leonard
('21377', '9630', '1', '1', '2', '3832'), -- itemname: Orichalcum
('21377', '9625', '1', '1', '2', '146'), -- itemname: Giant's Codex - Oblivion
('21377', '5162', '1', '1', '-1', '4382'), -- itemname: Recipe: Spiritshot (S) Compressed Package (100%)
('21377', '6701', '1', '1', '-1', '52494'), -- itemname: Sealed Imperial Crusader Breastplate Part
('21377', '6702', '1', '1', '-1', '67573'), -- itemname: Sealed Imperial Crusader Gaiters Pattern

-- Scarlet Stakato Noble
('21378', '57', '1900', '3739', '0', '700000'), -- itemname: Adena
('21378', '6676', '1', '1', '1', '34'), -- itemname: Sealed Imperial Crusader Gauntlet
('21378', '6677', '1', '1', '1', '34'), -- itemname: Sealed Imperial Crusader Boots
('21378', '6681', '1', '1', '1', '34'), -- itemname: Sealed Draconic Leather Glove
('21378', '6682', '1', '1', '1', '34'), -- itemname: Sealed Draconic Leather Boots
('21378', '6685', '1', '1', '1', '34'), -- itemname: Sealed Major Arcana Glove
('21378', '6686', '1', '1', '1', '34'), -- itemname: Sealed Major Arcana Boots
('21378', '6703', '1', '1', '1', '7649'), -- itemname: Sealed Imperial Crusader Gauntlets Design
('21378', '1879', '1', '1', '2', '104468'), -- itemname: Cokes
('21378', '1885', '1', '1', '2', '52234'), -- itemname: High Grade Suede
('21378', '9628', '1', '1', '2', '4916'), -- itemname: Leonard
('21378', '9630', '1', '1', '2', '3299'), -- itemname: Orichalcum
('21378', '9629', '1', '1', '2', '2725'), -- itemname: Adamantine
('21378', '9626', '1', '1', '2', '84'), -- itemname: Giant's Codex - Discipline
('21378', '6688', '1', '1', '-1', '22016'), -- itemname: Forgotten Blade Edge
('21378', '6696', '1', '1', '-1', '22366'), -- itemname: Heavens Divider Edge
('21378', '6711', '1', '1', '-1', '50468'), -- itemname: Sealed Major Arcana Robe Part

-- Tepra Scorpion
('21379', '57', '1883', '3706', '0', '700000'), -- itemname: Adena
('21379', '7575', '1', '1', '1', '9'), -- itemname: Draconic Bow
('21379', '7579', '1', '1', '1', '4074'), -- itemname: Draconic Bow Shaft
('21379', '1895', '1', '1', '2', '213014'), -- itemname: Metallic Fiber
('21379', '4040', '1', '1', '2', '14911'), -- itemname: Mold Lubricant
('21379', '9628', '1', '1', '2', '5847'), -- itemname: Leonard
('21379', '9630', '1', '1', '2', '3924'), -- itemname: Orichalcum
('21379', '9546', '1', '1', '2', '5964'), -- itemname: Fire Stone
('21379', '6694', '1', '1', '-1', '22218'), -- itemname: Saint Spear Blade
('21379', '5272', '1', '1', '-1', '4487'), -- itemname: Recipe: Greater Soulshot (S) Compressed Package(100%)
('21379', '6691', '1', '1', '-1', '21822'), -- itemname: Angel Slayer Blade

-- Tepra Scarab
('21380', '57', '1883', '3706', '0', '700000'), -- itemname: Adena
('21380', '6367', '1', '1', '1', '9'), -- itemname: Angel Slayer
('21380', '6691', '1', '1', '1', '4029'), -- itemname: Angel Slayer Blade
('21380', '1879', '1', '1', '2', '103548'), -- itemname: Cokes
('21380', '1885', '1', '1', '2', '51774'), -- itemname: High Grade Suede
('21380', '9628', '1', '1', '2', '4873'), -- itemname: Leonard
('21380', '9630', '1', '1', '2', '3270'), -- itemname: Orichalcum
('21380', '9629', '1', '1', '2', '2701'), -- itemname: Adamantine
('21380', '9547', '1', '1', '2', '4971'), -- itemname: Water Stone
('21380', '5277', '1', '1', '-1', '4487'), -- itemname: Recipe: Greater Spiritshot (S) Compressed Package(100%)
('21380', '6689', '1', '1', '-1', '21411'), -- itemname: Basalt Battlehammer Head
('21380', '6690', '1', '1', '-1', '21864'), -- itemname: Imperial Staff Head

-- Assassin Beetle
('21381', '57', '1892', '3724', '0', '700000'), -- itemname: Adena
('21381', '6679', '1', '1', '1', '22'), -- itemname: Sealed Imperial Crusader Helmet
('21381', '6683', '1', '1', '1', '22'), -- itemname: Sealed Draconic Leather Helmet
('21381', '6687', '1', '1', '1', '22'), -- itemname: Sealed Major Arcana Circlet
('21381', '6678', '1', '1', '1', '32'), -- itemname: Sealed Imperial Crusader Shield
('21381', '13885', '1', '1', '1', '21'), -- itemname: Sealed Arcana Sigil
('21381', '6706', '1', '1', '1', '8683'), -- itemname: Sealed Imperial Crusader Helmet Pattern
('21381', '6710', '1', '1', '1', '8683'), -- itemname: Sealed Draconic Leather Helmet Pattern
('21381', '1895', '1', '1', '2', '214046'), -- itemname: Metallic Fiber
('21381', '4040', '1', '1', '2', '14983'), -- itemname: Mold Lubricant
('21381', '4042', '1', '1', '2', '12486'), -- itemname: Enria
('21381', '4043', '1', '1', '2', '24972'), -- itemname: Asofe
('21381', '9548', '1', '1', '2', '5993'), -- itemname: Earth Stone
('21381', '6695', '1', '1', '-1', '21515'), -- itemname: Demon Splinter Blade
('21381', '7579', '1', '1', '-1', '22182'), -- itemname: Draconic Bow Shaft
('21381', '5282', '1', '1', '-1', '4509'), -- itemname: Recipe: Greater Blessed Spiritshot (S) Compressed Package(100%)

-- Mercenary of Destruction
('21382', '57', '1843', '3629', '0', '700000'), -- itemname: Adena
('21382', '6371', '1', '1', '1', '9'), -- itemname: Demon Splinter
('21382', '6695', '1', '1', '1', '3869'), -- itemname: Demon Splinter Blade
('21382', '1895', '1', '1', '2', '208544'), -- itemname: Metallic Fiber
('21382', '4040', '1', '1', '2', '14598'), -- itemname: Mold Lubricant
('21382', '9628', '1', '1', '2', '5725'), -- itemname: Leonard
('21382', '9630', '1', '1', '2', '3842'), -- itemname: Orichalcum
('21382', '9549', '1', '1', '2', '5839'), -- itemname: Wind Stone
('21382', '5529', '1', '1', '-1', '27117'), -- itemname: Dragon Slayer Edge
('21382', '1808', '1', '1', '-1', '4393'), -- itemname: Recipe: Soulshot: S Grade
('21382', '5533', '1', '1', '-1', '27112'), -- itemname: Elysian Head

-- Knight of Destruction
('21383', '57', '1890', '3720', '0', '700000'), -- itemname: Adena
('21383', '6364', '1', '1', '1', '4'), -- itemname: Forgotten Blade
('21383', '6372', '1', '1', '1', '4'), -- itemname: Heaven's Divider
('21383', '6688', '1', '1', '1', '2022'), -- itemname: Forgotten Blade Edge
('21383', '6696', '1', '1', '1', '2054'), -- itemname: Heavens Divider Edge
('21383', '1879', '1', '1', '2', '103925'), -- itemname: Cokes
('21383', '1885', '1', '1', '2', '51962'), -- itemname: High Grade Suede
('21383', '9628', '1', '1', '2', '4890'), -- itemname: Leonard
('21383', '9630', '1', '1', '2', '3282'), -- itemname: Orichalcum
('21383', '9629', '1', '1', '2', '2711'), -- itemname: Adamantine
('21383', '9550', '1', '1', '2', '4989'), -- itemname: Dark Stone
('21383', '6703', '1', '1', '-1', '144268'), -- itemname: Sealed Imperial Crusader Gauntlets Design
('21383', '6704', '1', '1', '-1', '144268'), -- itemname: Sealed Imperial Crusader Boots Design
('21383', '5277', '1', '1', '-1', '4503'), -- itemname: Recipe: Greater Spiritshot (S) Compressed Package(100%)

-- Lavastone Golem
('21385', '57', '2427', '4777', '0', '700000'), -- itemname: Adena
('21385', '6370', '1', '1', '1', '11'), -- itemname: Saint Spear
('21385', '6694', '1', '1', '1', '5286'), -- itemname: Saint Spear Blade
('21385', '1894', '1', '1', '2', '33714'), -- itemname: Crafted Leather
('21385', '4039', '1', '1', '2', '32029'), -- itemname: Mold Glue
('21385', '4041', '1', '1', '2', '8355'), -- itemname: Mold Hardener
('21385', '4044', '1', '1', '2', '32029'), -- itemname: Thons
('21385', '9551', '1', '1', '2', '7687'), -- itemname: Divine Stone
('21385', '3036', '1', '1', '-1', '5783'), -- itemname: Recipe: Spiritshot S
('21385', '959', '1', '1', '-1', '520'), -- itemname: Scroll: Enchant Weapon (S)
('21385', '3957', '1', '1', '-1', '5783'), -- itemname: Recipe: Blessed Spiritshot S

-- Magma Golem
('21386', '57', '2554', '5027', '0', '700000'), -- itemname: Adena
('21386', '6684', '1', '1', '1', '32'), -- itemname: Sealed Major Arcana Robe
('21386', '6711', '1', '1', '1', '12523'), -- itemname: Sealed Major Arcana Robe Part
('21386', '1895', '1', '1', '2', '240742'), -- itemname: Metallic Fiber
('21386', '4040', '1', '1', '2', '16852'), -- itemname: Mold Lubricant
('21386', '4042', '1', '1', '2', '14043'), -- itemname: Enria
('21386', '4043', '1', '1', '2', '28087'), -- itemname: Asofe
('21386', '959', '1', '1', '2', '34'), -- itemname: Scroll: Enchant Weapon (S)
('21386', '1345', '60', '180', '2', '93622'), -- itemname: Shining Arrow
('21386', '6688', '1', '1', '-1', '29596'), -- itemname: Forgotten Blade Edge
('21386', '960', '1', '1', '-1', '5477'), -- itemname: Scroll: Enchant Armor (S)
('21386', '1894', '1', '1', '-1', '480429'), -- itemname: Crafted Leather

-- Arimanes of Destruction
('21387', '57', '2576', '5071', '0', '700000'), -- itemname: Adena
('21387', '6365', '1', '1', '1', '4'), -- itemname: Basalt Battlehammer
('21387', '6366', '1', '1', '1', '4'), -- itemname: Imperial Staff
('21387', '6369', '1', '1', '1', '4'), -- itemname: Dragon Hunter Axe
('21387', '6689', '1', '1', '1', '1804'), -- itemname: Basalt Battlehammer Head
('21387', '6690', '1', '1', '1', '1842'), -- itemname: Imperial Staff Head
('21387', '6693', '1', '1', '1', '1872'), -- itemname: Dragon Hunter Axe Blade
('21387', '1895', '1', '1', '2', '291471'), -- itemname: Metallic Fiber
('21387', '4040', '1', '1', '2', '20403'), -- itemname: Mold Lubricant
('21387', '9628', '1', '1', '2', '8001'), -- itemname: Leonard
('21387', '9630', '1', '1', '2', '5369'), -- itemname: Orichalcum
('21387', '960', '1', '1', '2', '408'), -- itemname: Scroll: Enchant Armor (S)
('21387', '6708', '1', '1', '-1', '196689'), -- itemname: Sealed Draconic Leather Gloves Fabric
('21387', '6709', '1', '1', '-1', '196689'), -- itemname: Sealed Draconic Leather Boots Design
('21387', '5157', '1', '1', '-1', '6140'), -- itemname: Recipe: Soulshot (S) Compressed Package (100%)

-- Balrog of Destruction
('21389', '57', '2445', '4813', '0', '700000'), -- itemname: Adena
('21389', '6371', '1', '1', '1', '11'), -- itemname: Demon Splinter
('21389', '6695', '1', '1', '1', '5133'), -- itemname: Demon Splinter Blade
('21389', '1894', '1', '1', '2', '33973'), -- itemname: Crafted Leather
('21389', '4039', '1', '1', '2', '32275'), -- itemname: Mold Glue
('21389', '4041', '1', '1', '2', '8419'), -- itemname: Mold Hardener
('21389', '4044', '1', '1', '2', '32275'), -- itemname: Thons
('21389', '5272', '1', '1', '2', '430'), -- itemname: Recipe: Greater Soulshot (S) Compressed Package(100%)
('21389', '6712', '1', '1', '-1', '186681'), -- itemname: Sealed Major Arcana Gloves fabric
('21389', '5162', '1', '1', '-1', '5827'), -- itemname: Recipe: Spiritshot (S) Compressed Package (100%)
('21389', '6713', '1', '1', '-1', '186681'), -- itemname: Sealed Major Arcana Boots Design

-- Ashuras of Destruction
('21390', '57', '2514', '4949', '0', '700000'), -- itemname: Adena
('21390', '6680', '1', '1', '1', '32'), -- itemname: Sealed Draconic Leather Armor
('21390', '6707', '1', '1', '1', '12329'), -- itemname: Sealed Draconic Leather Armor Part
('21390', '1895', '1', '1', '2', '284420'), -- itemname: Metallic Fiber
('21390', '4040', '1', '1', '2', '19909'), -- itemname: Mold Lubricant
('21390', '4042', '1', '1', '2', '16591'), -- itemname: Enria
('21390', '4043', '1', '1', '2', '33182'), -- itemname: Asofe
('21390', '5277', '1', '1', '2', '442'), -- itemname: Recipe: Greater Spiritshot (S) Compressed Package(100%)
('21390', '6706', '1', '1', '-1', '328015'), -- itemname: Sealed Imperial Crusader Helmet Pattern
('21390', '6710', '1', '1', '-1', '328015'), -- itemname: Sealed Draconic Leather Helmet Pattern

-- Lavasilisk
('21391', '57', '2631', '5178', '0', '700000'), -- itemname: Adena
('21391', '6674', '1', '1', '1', '20'), -- itemname: Sealed Imperial Crusader Breastplate
('21391', '6675', '1', '1', '1', '33'), -- itemname: Sealed Imperial Crusader Gaiters
('21391', '6701', '1', '1', '1', '6931'), -- itemname: Sealed Imperial Crusader Breastplate Part
('21391', '6702', '1', '1', '1', '8922'), -- itemname: Sealed Imperial Crusader Gaiters Pattern
('21391', '1879', '1', '1', '2', '144675'), -- itemname: Cokes
('21391', '1885', '1', '1', '2', '72338'), -- itemname: High Grade Suede
('21391', '9628', '1', '1', '2', '6808'), -- itemname: Leonard
('21391', '9630', '1', '1', '2', '4569'), -- itemname: Orichalcum
('21391', '9629', '1', '1', '2', '3774'), -- itemname: Adamantine
('21391', '5282', '1', '1', '2', '386'), -- itemname: Recipe: Greater Blessed Spiritshot (S) Compressed Package(100%)
('21391', '6706', '1', '1', '-1', '343236'), -- itemname: Sealed Imperial Crusader Helmet Pattern
('21391', '6710', '1', '1', '-1', '343236'), -- itemname: Sealed Draconic Leather Helmet Pattern

-- Blazing Ifrit
('21392', '57', '2575', '5068', '0', '700000'), -- itemname: Adena
('21392', '6370', '1', '1', '1', '12'), -- itemname: Saint Spear
('21392', '6694', '1', '1', '1', '5609'), -- itemname: Saint Spear Blade
('21392', '1895', '1', '1', '2', '291298'), -- itemname: Metallic Fiber
('21392', '4040', '1', '1', '2', '20391'), -- itemname: Mold Lubricant
('21392', '9628', '1', '1', '2', '7997'), -- itemname: Leonard
('21392', '9630', '1', '1', '2', '5366'), -- itemname: Orichalcum
('21392', '6901', '1', '1', '2', '1019'), -- itemname: Recipe: Shining Arrow (100%)
('21392', '5282', '1', '1', '-1', '6136'), -- itemname: Recipe: Greater Blessed Spiritshot (S) Compressed Package(100%)
('21392', '5529', '1', '1', '-1', '37877'), -- itemname: Dragon Slayer Edge
('21392', '1808', '1', '1', '-1', '6136'), -- itemname: Recipe: Soulshot: S Grade

-- Magma Drake
('21393', '57', '2658', '5232', '0', '700000'), -- itemname: Adena
('21393', '6724', '1', '1', '1', '54'), -- itemname: Sealed Tateossian Earring
('21393', '6725', '1', '1', '1', '80'), -- itemname: Sealed Tateossian Ring
('21393', '6726', '1', '1', '1', '40'), -- itemname: Sealed Tateossian Necklace
('21393', '6698', '1', '1', '1', '17226'), -- itemname: Sealed Tateossian Earring Part
('21393', '6699', '1', '1', '1', '19115'), -- itemname: Sealed Tateossian Ring Gem
('21393', '6700', '1', '1', '1', '10499'), -- itemname: Sealed Tateossian Necklace Chain
('21393', '1895', '1', '1', '2', '300713'), -- itemname: Metallic Fiber
('21393', '4040', '1', '1', '2', '21050'), -- itemname: Mold Lubricant
('21393', '4042', '1', '1', '2', '17542'), -- itemname: Enria
('21393', '4043', '1', '1', '2', '35083'), -- itemname: Asofe
('21393', '5157', '1', '1', '2', '467'), -- itemname: Recipe: Soulshot (S) Compressed Package (100%)
('21393', '5533', '1', '1', '-1', '39094'), -- itemname: Elysian Head
('21393', '1894', '1', '2', '-1', '666785'), -- itemname: Crafted Leather

-- Necromancer of Destruction
('21384', '57', '2420', '4913', '0', '700000'), -- itemname: Adena
('21384', '6371', '1', '1', '1', '11'), -- itemname: Demon Splinter
('21384', '6695', '1', '1', '1', '5185'), -- itemname: Demon Splinter Blade
('21384', '9628', '1', '1', '2', '12785'), -- itemname: Leonard
('21384', '9630', '1', '1', '2', '8579'), -- itemname: Orichalcum
('21384', '9629', '1', '1', '2', '7087'), -- itemname: Adamantine
('21384', '960', '1', '1', '-1', '5298'), -- itemname: Scroll: Enchant Armor (S)
('21384', '1894', '1', '1', '-1', '464713'), -- itemname: Crafted Leather
('21384', '9540', '1', '1', '-1', '23694'), -- itemname: Sealed Dynasty Tunic Piece

-- Iblis of Destruction
('21388', '57', '2420', '4913', '0', '700000'), -- itemname: Adena
('21388', '6680', '1', '1', '1', '31'), -- itemname: Sealed Draconic Leather Armor
('21388', '6707', '1', '1', '1', '12114'), -- itemname: Sealed Draconic Leather Armor Part
('21388', '9628', '1', '1', '2', '12785'), -- itemname: Leonard
('21388', '9630', '1', '1', '2', '8579'), -- itemname: Orichalcum
('21388', '9629', '1', '1', '2', '7087'), -- itemname: Adamantine
('21388', '960', '1', '1', '-1', '5298'), -- itemname: Scroll: Enchant Armor (S)
('21388', '1894', '1', '1', '-1', '464713'), -- itemname: Crafted Leather
('21388', '9540', '1', '1', '-1', '23694'), -- itemname: Sealed Dynasty Tunic Piece

-- Lavasaurus
('21394', '57', '2420', '4913', '0', '700000'), -- itemname: Adena
('21394', '6370', '1', '1', '1', '11'), -- itemname: Saint Spear
('21394', '6694', '1', '1', '1', '5381'), -- itemname: Saint Spear Blade
('21394', '9628', '1', '1', '2', '12785'), -- itemname: Leonard
('21394', '9630', '1', '1', '2', '8579'), -- itemname: Orichalcum
('21394', '9629', '1', '1', '2', '7087'), -- itemname: Adamantine
('21394', '960', '1', '1', '-1', '5298'), -- itemname: Scroll: Enchant Armor (S)
('21394', '1894', '1', '1', '-1', '464713'), -- itemname: Crafted Leather
('21394', '9540', '1', '1', '-1', '23694'), -- itemname: Sealed Dynasty Tunic Piece

-- Elder Lavasaurus
('21395', '57', '2540', '5000', '0', '700000'), -- itemname: Adena
('21395', '6674', '1', '1', '1', '20'), -- itemname: Sealed Imperial Crusader Breastplate
('21395', '6675', '1', '1', '1', '31'), -- itemname: Sealed Imperial Crusader Gaiters
('21395', '6701', '1', '1', '1', '6693'), -- itemname: Sealed Imperial Crusader Breastplate Part
('21395', '6702', '1', '1', '1', '8615'), -- itemname: Sealed Imperial Crusader Gaiters Pattern
('21395', '9628', '1', '1', '2', '13148'), -- itemname: Leonard
('21395', '9630', '1', '1', '2', '8823'), -- itemname: Orichalcum
('21395', '9629', '1', '1', '2', '7289'), -- itemname: Adamantine
('21395', '960', '1', '1', '-1', '5448'), -- itemname: Scroll: Enchant Armor (S)
('21395', '1894', '1', '1', '-1', '477927'), -- itemname: Crafted Leather
('21395', '9540', '1', '1', '-1', '24368'), -- itemname: Sealed Dynasty Tunic Piece

-- Ancient Lavasaurus
('18803', '57', '2540', '5000', '0', '700000'), -- itemname: Adena
('18803', '6674', '1', '1', '1', '20'), -- itemname: Sealed Imperial Crusader Breastplate
('18803', '6675', '1', '1', '1', '31'), -- itemname: Sealed Imperial Crusader Gaiters
('18803', '6701', '1', '1', '1', '6693'), -- itemname: Sealed Imperial Crusader Breastplate Part
('18803', '6702', '1', '1', '1', '8615'), -- itemname: Sealed Imperial Crusader Gaiters Pattern
('18803', '9628', '1', '1', '2', '13148'), -- itemname: Leonard
('18803', '9630', '1', '1', '2', '8823'), -- itemname: Orichalcum
('18803', '9629', '1', '1', '2', '7289'), -- itemname: Adamantine
('18803', '960', '1', '1', '-1', '5448'), -- itemname: Scroll: Enchant Armor (S)
('18803', '1894', '1', '1', '-1', '477927'), -- itemname: Crafted Leather
('18803', '9540', '1', '1', '-1', '24368'); -- itemname: Sealed Dynasty Tunic Piece

-- Anais Drop
DELETE FROM `droplist` WHERE `mobId` = 25701;

-- Anays Drops
REPLACE INTO `droplist` VALUES
(25701,14109,1,1,0,8472), -- Sealed Vesper Gauntlet
(25701,14110,1,1,0,8472), -- Sealed Vesper Boots
(25701,14113,1,1,0,8472), -- Sealed Vesper Leather Gloves
(25701,14114,1,1,0,8472), -- Sealed Vesper Leather Boots
(25701,14116,1,1,0,8472), -- Sealed Vesper Gloves
(25701,14117,1,1,0,8472), -- Sealed Vesper Shoes
(25701,15484,1,1,0,197697), -- High-level Angel Circlet
(25701,13143,1,1,1,11861), -- Sealed Vesper Helmet
(25701,13144,1,1,1,11861), -- Sealed Vesper Leather Helmet
(25701,13145,1,1,1,11861), -- Sealed Vesper Circlet
(25701,13887,1,1,1,16945), -- Sealed Vesper Sigil
(25701,14111,1,1,1,16945), -- Sealed Vesper Shield
(25701,959,1,1,2,44847), -- Scroll: Enchant Weapon (S)
(25701,960,1,1,2,448478), -- Scroll: Enchant Armor (S)
(25701,6577,1,1,2,7474); -- Blessed Scroll: Enchant Weapon (S)

INSERT INTO `droplist` VALUES
-- Giant Marpanak
(25680,9448,1,1,1,904), -- Dynasty Cudgel
(25680,9449,1,1,1,904), -- Dynasty Mace
(25680,9622,1,1,1,273540), -- Halberd Cudgel Piece
(25680,9623,1,1,1,273540), -- Dynasty Mace Piece
(25680,10252,1,1,1,904), -- Dynasty Staff
(25680,10253,1,1,1,904), -- Dynasty Crusher
(25680,10546,1,3,1,141748), -- Dynasty Staff Fragment
(25680,10547,2,4,1,94498), -- Dynasty Crusher Fragment
(25680,9516,1,1,0,14813), -- Sealed Dynasty Helmet
(25680,9529,1,1,0,21163), -- Sealed Dynasty Shield
(25680,9532,9,27,0,203055), -- Sealed Dynasty Helmet Piece
(25680,9545,40,120,0,54457), -- Sealed Dynasty Shield Piece
(25680,10114,14,42,0,90008), -- Sealed Dynasty Sigil Piece
(25680,13885,1,1,0,40396), -- Sealed Arcana Sigil
(25680,959,1,1,2,52706), -- Scroll: Enchant Weapon (S)
(25680,960,1,1,2,527071), -- Scroll: Enchant Armor (S)
(25680,6577,1,1,2,8784), -- Blessed Scroll: Enchant Weapon (S)
-- Gorgolos
(25681,9442,1,1,1,891), -- Dynasty Sword
(25681,9443,1,1,1,891), -- Dynasty Blade
(25681,9444,1,1,1,891), -- Dynasty Phantom
(25681,9616,1,3,1,139316), -- Dynasty Sword Piece
(25681,9617,1,1,1,278632), -- Dynasty Blade Piece
(25681,9618,1,1,1,278632), -- Dynasty Phantom Piece
(25681,9514,1,1,0,6162), -- Sealed Dynasty Breast Plate
(25681,9515,1,1,0,9860), -- Sealed Dynasty Gaiter
(25681,9530,2,6,0,410547), -- Sealed Dynasty Breast Plate Piece
(25681,9531,15,45,0,161915), -- Sealed Dynasty Gaiter Piece
-- Last Titan Utenus
(25684,9519,1,1,0,4005), -- Sealed Dynasty Leather Armor
(25684,9520,1,1,0,6409), -- Sealed Dynasty Leather Leggings
(25684,9535,2,4,0,327825), -- Sealed Dynasty Leather Armor Piece
(25684,9536,4,12,0,180707), -- Sealed Dynasty Leather Leggings Piece
(25684,10220,1,1,1,1062), -- Icarus Hammer
(25684,10222,1,1,1,1062), -- Icarus Hall
(25684,10402,1,1,1,352297), -- Icarus Hammer Piece
(25684,10404,1,1,1,352297), -- Icarus Hall Piece
(25684,959,1,1,2,28504), -- Scroll: Enchant Weapon (S)
(25684,960,1,1,2,285045), -- Scroll: Enchant Armor (S)
(25684,6577,1,1,2,4750), -- Blessed Scroll: Enchant Weapon (S)
-- Hekaton Prime
(25687,9519,1,1,0,7424), -- Sealed Dynasty Leather Armor
(25687,9520,1,1,0,11878), -- Sealed Dynasty Leather Leggings
(25687,9535,8,22,0,121511), -- Sealed Dynasty Leather Armor Piece
(25687,9536,4,12,0,334904), -- Sealed Dynasty Leather Leggings Piece
(25687,10221,1,1,1,3939), -- Icarus Hand
(25687,10403,1,3,1,653144), -- Icarus Hand Piece
(25687,3936,1,3,2,495271), -- Blessed Scroll of Resurrection
(25687,6578,1,1,2,88048); -- Blessed Scroll: Enchant Armor (S)