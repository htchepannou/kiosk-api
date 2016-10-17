-- Website
INSERT INTO website(id, name, url, active)
VALUES (100, 'Mboa Football', 'http://www.mboafootball.com', TRUE);

-- Feeds
INSERT INTO feed VALUES (100, 100, 'rss', 'CMR', 'Mboa Football', 'http://mboafootball.com/rss', TRUE);
INSERT INTO feed VALUES (200, 100, 'rss', 'CMR', 'Cameroon Post Online', 'http://www.cameroonpostline.com/feed/', TRUE);

-- Images
INSERT INTO image (id, url, public_url, image_key, content_type, title, width, height)
VALUES ('100', 'http://x.com/1.png', 'http://public.x.com/11.png', 'images/11/0.png', 'image/png', 'sample image', 128, 256);


-- Articles
INSERT INTO article (id, feed_id, image_id, status, title, slug, country_code, language_code, published_date, url, rank)
VALUES ('100', 100, '100', 0, 'Article #100', 'Slug #100', 'CMR', 'FR', now(), 'http://feed100/100', 10);

INSERT INTO article (id, feed_id, image_id, status, title, slug, country_code, language_code, published_date, url, rank)
VALUES ('101', 100, '100', 1, 'Article #101', 'Slug #101', 'CMR', 'FR', now(), 'http://feed100/101', 3);

INSERT INTO article (id, feed_id, status, title, slug, country_code, language_code, published_date, url, rank)
VALUES ('102', 100, 1, 'Article #102', 'Slug #102', 'CMR', 'FR', now(), 'http://feed100/102', 7);


INSERT INTO article (id, feed_id, status, title, slug, country_code, language_code, published_date, url, rank)
VALUES ('200', 200, 1, 'Article #200', 'Slug #200', 'CMR', 'FR', now(), 'http://feed200/200', 10);
