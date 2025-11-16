CREATE TABLE USERS (
                       user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       user_name VARCHAR(100) NOT NULL,
                       point INT DEFAULT 0,
                       created_dt DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE PRODUCTS (
                          product_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          product_name VARCHAR(255) NOT NULL,
                          stock INT DEFAULT 0,
                          price INT NOT NULL,
                          status VARCHAR(50),
                          popularity_score INT DEFAULT 0,
                          created_dt DATETIME DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_products_status ON PRODUCTS(status);

CREATE TABLE CART (
                      cart_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      user_id BIGINT NOT NULL,
                      product_id BIGINT NOT NULL
);
CREATE INDEX idx_cart_user ON CART(user_id);

CREATE TABLE COUPONS (
                         coupon_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         coupon_name VARCHAR(255) NOT NULL,
                         discount_rate DOUBLE NOT NULL,
                         discount_type VARCHAR(20) NOT NULL,
                         total_quantity INT,
                         issued_quantity INT,
                         start_date DATETIME,
                         end_date DATETIME,
                         valid_days INT,
                         status VARCHAR(20)
);

CREATE TABLE ORDERS (
                        order_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        user_id BIGINT NOT NULL,
                        total_price INT NOT NULL,
                        discount_price INT DEFAULT 0,
                        final_price INT NOT NULL,
                        cancelled_price INT DEFAULT 0,
                        created_dt DATETIME DEFAULT CURRENT_TIMESTAMP,
                        paid_dt DATETIME,
                        user_coupon_id BIGINT
);
CREATE INDEX idx_orders_user ON ORDERS(user_id, created_dt);
CREATE INDEX idx_orders_coupon ON ORDERS(user_coupon_id);


CREATE TABLE ORDER_ITEMS (
                             order_item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             order_id BIGINT NOT NULL,
                             product_id BIGINT NOT NULL,
                             product_name VARCHAR(255),
                             product_price INT,
                             status VARCHAR(20),
                             quantity INT DEFAULT 1
);
CREATE INDEX idx_order_items_order ON ORDER_ITEMS(order_id);
CREATE INDEX idx_order_items_product ON ORDER_ITEMS(product_id);


CREATE TABLE POINT_HIS (
                           point_his_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           user_id BIGINT NOT NULL,
                           type VARCHAR(20) NOT NULL,
                           point INT,
                           created_dt DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE USER_COUPONS (
                              user_coupon_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              coupon_id BIGINT NOT NULL,
                              user_id BIGINT NOT NULL,
                              status VARCHAR(20),
                              issued_dt DATETIME,
                              used_dt DATETIME,
                              expires_dt DATETIME
);
CREATE INDEX idx_user_coupons_user ON USER_COUPONS(user_id, status);
CREATE INDEX idx_user_coupons_coupon ON USER_COUPONS(coupon_id);
