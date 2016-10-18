ALTER TABLE article ADD COLUMN score INT DEFAULT 999999;
UPDATE article SET score=rank;
