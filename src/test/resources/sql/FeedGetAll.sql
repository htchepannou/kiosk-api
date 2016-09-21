INSERT INTO website VALUES (1001, 'Mboa Football', 'http://www.mboafootball.com', NULL, NULL, NULL, NULL, FALSE);
INSERT INTO website VALUES (1002, 'Cameroon Post Online', 'http://www.cameroonpostline.com', NULL, NULL, NULL, NULL, TRUE);
INSERT INTO website VALUES (1003, 'Spark Cameroon', 'http://www.sparkcameroun.com/', NULL, NULL, NULL, NULL, TRUE);

INSERT INTO feed VALUES (1001, 1001, 'rss', 'CMR', 'Mboa Football', 'http://mboafootball.com/rss', TRUE);
INSERT INTO feed VALUES (1002, 1002, 'rss', 'CMR', 'Cameroon Post Online', 'http://www.cameroonpostline.com/feed/', TRUE);
INSERT INTO feed VALUES (1003, 1003, 'rss', 'CMR', 'Spark Cameroon', 'http://www.sparkcameroun.com/feed/', FALSE);
