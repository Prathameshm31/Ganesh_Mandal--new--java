CREATE DATABASE IF NOT EXISTS ganesh_mandal;
USE ganesh_mandal;

CREATE TABLE IF NOT EXISTS members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    mobile VARCHAR(15) NOT NULL,
    address TEXT,
    last_year_amount DECIMAL(10, 2) DEFAULT 0.00,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_member_mobile (mobile),
    INDEX idx_member_name (name)
);

CREATE TABLE IF NOT EXISTS collections (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_mode VARCHAR(10) NOT NULL COMMENT 'CASH or ONLINE',
    transaction_id VARCHAR(100),
    collection_date DATE NOT NULL,
    remarks TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE,
    INDEX idx_collection_member (member_id),
    INDEX idx_collection_date (collection_date),
    INDEX idx_collection_payment_mode (payment_mode)
);
