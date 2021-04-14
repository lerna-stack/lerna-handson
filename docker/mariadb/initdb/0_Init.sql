create table `CONCERTS` (
    `ID` VARCHAR(512) NOT NULL PRIMARY KEY,
    `NUMBER_OF_TICKETS` INTEGER NOT NULL,
    `CANCELLED` BOOLEAN NOT NULL,
    `CREATED_AT` DATETIME NOT NULL,
    `UPDATED_AT` DATETIME NOT NULL
);

# https://github.com/akka/akka-projection/blob/4011670f079a76e33de7b5a4e2faa83dcbc6c991/examples/src/test/resources/create-table-mysql.sql
CREATE TABLE IF NOT EXISTS akka_projection_offset_store (
    projection_name VARCHAR(255) NOT NULL,
    projection_key VARCHAR(255) NOT NULL,
    current_offset VARCHAR(255) NOT NULL,
    manifest VARCHAR(4) NOT NULL,
    mergeable BOOLEAN NOT NULL,
    last_updated BIGINT NOT NULL,
    PRIMARY KEY(projection_name, projection_key)
);
CREATE INDEX projection_name_index ON akka_projection_offset_store (projection_name);
