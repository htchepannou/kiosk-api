INSERT INTO website(id, name, url, article_url_prefix, article_url_suffix, title_css_selector, slug_css_selector, active, title_sanitize_regex)
  VALUES (1001, 'Mboa Football', 'http://mboafootball.com', NULL, NULL, NULL, NULL, FALSE, NULL);


INSERT INTO website(id, name, url, article_url_prefix, article_url_suffix, title_css_selector, slug_css_selector, active, title_sanitize_regex)
  VALUES (1101, 'cameroon-info.net', 'http://cameroon-info.net', '/article', '.html', '.cp-post-content h3', '.cp-post-content .slug', TRUE, NULL);

INSERT INTO website(id, name, url, article_url_prefix, article_url_suffix, title_css_selector, slug_css_selector, active, title_sanitize_regex)
  VALUES (1102, 'Camfoot', 'http://camfoot.com', '/p', '.html', '.content .title', '.content .slug', TRUE, '..');
