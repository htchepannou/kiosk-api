UPDATE website SET title_sanitize_regex = NULL;
UPDATE website SET article_url_prefix = '/article.php', title_sanitize_regex = '.[^:]+:+(.[^:]+)' WHERE id = 1010;
UPDATE website SET title_sanitize_regex = '.[^:]+:+(.[^:]+)' WHERE id = 1005;
UPDATE website SET title_sanitize_regex = '.[^:]+:+(.[^:]+)' WHERE id = 1013;

