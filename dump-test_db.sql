--
-- Table structure for table `client_order`
--
DROP TABLE IF EXISTS client_order;

CREATE TABLE client_order (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `total_shipping` decimal(38,2) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

--
-- Table structure for table `order_`
--
DROP TABLE IF EXISTS order_;

CREATE TABLE order_ (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` int DEFAULT NULL,
  `code_customer` int DEFAULT NULL,
  `date_registration` date DEFAULT NULL,
  `error` varchar(255) DEFAULT NULL,
  `number_control` varchar(255) DEFAULT NULL,
  `product_name` varchar(255) DEFAULT NULL,
  `total_value` decimal(38,2) DEFAULT NULL,
  `unit_value` decimal(38,2) DEFAULT NULL,
  PRIMARY KEY (`id`)
);


