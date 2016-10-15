-- CSS selector
ALTER TABLE website ADD COLUMN image_css_selector VARCHAR(255);

UPDATE website SET image_css_selector = '.post .wp-caption img' WHERE id = 1002;
UPDATE website SET image_css_selector = '.panel-body img' WHERE id = 1005;
UPDATE website SET image_css_selector = '.topimage-post img' WHERE id = 1006;
UPDATE website SET image_css_selector = '.image-wrapper img' WHERE id = 1007;
UPDATE website SET image_css_selector = '.image-wrapper img' WHERE id = 1008;
UPDATE website SET image_css_selector = '#content-area .wp-caption img' WHERE id = 1009;
UPDATE website SET image_css_selector = '#photo img' WHERE id = 1010;
UPDATE website SET image_css_selector = '.itemBody .itemImageBlock img' WHERE id = 1011;
UPDATE website SET image_css_selector = '.itemBody .itemImageBlock img' WHERE id = 1012;
UPDATE website SET image_css_selector = '.page-content h2 img' WHERE id = 1013;

-- Remove duplicate feed
DELETE FROM feed WHERE id = 1004;
DELETE FROM website WHERE id = 1004;

-- Change feed
UPDATE feed SET url = 's3://rss/1005.xml' WHERE id = 1005;
UPDATE feed SET url = 's3://rss/1013.xml' WHERE id = 1013;

-- Title
ALTER TABLE website ADD COLUMN title_sanitize_regex VARCHAR(255);

UPDATE website SET title_sanitize_regex = '.[^:]+:+(.[^:]+)' WHERE id = 1005;
UPDATE website SET title_sanitize_regex = '.[^:]+:+(.[^:]+)' WHERE id = 1013;
