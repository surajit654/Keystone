CREATE TABLE assets (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    asset_code VARCHAR(100),
    type VARCHAR(100),
    site_id BIGINT NOT NULL,

    CONSTRAINT fk_asset_site
        FOREIGN KEY (site_id)
        REFERENCES sites(id)
);