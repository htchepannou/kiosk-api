-- Website
INSERT INTO website(id, name, url, active)
  VALUES (100, 'Mboa Football', 'http://www.mboafootball.com', TRUE);

-- Feeds
INSERT INTO feed VALUES (100, 100, 'rss', 'CMR', 'Mboa Football', 'http://mboafootball.com/rss', TRUE);

-- Images
INSERT INTO image (id, url, public_url, image_key, content_type, title, width, height)
  VALUES ('100', 'http://x.com/1.png', 'http://public.x.com/11.png', 'images/11/0.png', 'image/png', 'sample image', 128, 256);

-- Articles
INSERT INTO article(id, feed_id, image_id, STATUS, title, display_title, slug, country_code, language_code, published_date, url)
  VALUES('100', 100, 100, 0, 'Article #100', 'This is article #100', 'Slug #100', 'CMR', 'FR', '2013-11-15 12:30', 'http://feed100/100');

INSERT INTO article(id, feed_id, image_id, STATUS, title, display_title, slug, country_code, language_code, published_date, url)
VALUES('101', 100, null, 1, 'Article #101', 'This is article #101', 'Slug #101', 'CMR', 'FR', '2013-11-15 13:30', 'http://feed100/101');
