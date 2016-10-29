ALTER TABLE article ADD COLUMN display_title VARCHAR(255);

UPDATE article SET display_title=title;
