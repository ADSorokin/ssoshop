-- MySQL dump 10.13  Distrib 8.0.19, for Win64 (x86_64)
--
-- Host: localhost    Database: ccodb
-- ------------------------------------------------------
-- Server version	8.4.3

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `art_clusters`
--

DROP TABLE IF EXISTS `art_clusters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `art_clusters` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `art_clusters`
--

LOCK TABLES `art_clusters` WRITE;
/*!40000 ALTER TABLE `art_clusters` DISABLE KEYS */;
INSERT INTO `art_clusters` VALUES (1),(2),(3),(4),(5),(6),(7),(8),(9),(10);
/*!40000 ALTER TABLE `art_clusters` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart_items`
--

DROP TABLE IF EXISTS `cart_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart_items` (
  `price` decimal(38,2) DEFAULT NULL,
  `quantity` int NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1re40cjegsfvw58xrkdp6bac6` (`product_id`),
  KEY `FK709eickf3kc0dujx3ub9i7btf` (`user_id`),
  CONSTRAINT `FK1re40cjegsfvw58xrkdp6bac6` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `FK709eickf3kc0dujx3ub9i7btf` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_items`
--

LOCK TABLES `cart_items` WRITE;
/*!40000 ALTER TABLE `cart_items` DISABLE KEYS */;
/*!40000 ALTER TABLE `cart_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,'Category 1'),(2,'Category 2'),(3,'Category 3');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cluster_users`
--

DROP TABLE IF EXISTS `cluster_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cluster_users` (
  `cluster_id` bigint NOT NULL,
  `user_id` bigint DEFAULT NULL,
  KEY `FKr1unbq6htdk3nopa8eo2vpao4` (`cluster_id`),
  CONSTRAINT `FKr1unbq6htdk3nopa8eo2vpao4` FOREIGN KEY (`cluster_id`) REFERENCES `art_clusters` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cluster_users`
--

LOCK TABLES `cluster_users` WRITE;
/*!40000 ALTER TABLE `cluster_users` DISABLE KEYS */;
INSERT INTO `cluster_users` VALUES (1,1),(2,3),(3,4),(4,5),(5,22),(6,21),(7,19),(8,12),(9,31),(10,51);
/*!40000 ALTER TABLE `cluster_users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cluster_weights`
--

DROP TABLE IF EXISTS `cluster_weights`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cluster_weights` (
  `weight` double DEFAULT NULL,
  `cluster_id` bigint NOT NULL,
  KEY `FKbjnpfr3r1e7g7kun0ugqk4j74` (`cluster_id`),
  CONSTRAINT `FKbjnpfr3r1e7g7kun0ugqk4j74` FOREIGN KEY (`cluster_id`) REFERENCES `art_clusters` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cluster_weights`
--

LOCK TABLES `cluster_weights` WRITE;
/*!40000 ALTER TABLE `cluster_weights` DISABLE KEYS */;
INSERT INTO `cluster_weights` VALUES (0,3),(0,3),(0,3),(0,3),(0,3),(0,3),(0,3),(0,3),(0,3),(0,3),(0,3),(0,3),(0,3),(0,3),(0,3),(0,3),(0,3),(0,3),(0,3),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,7),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,8),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,9),(0,2),(0,2),(0,2),(0,2),(0.851962326481,2),(0,2),(0,2),(1,2),(0.851962326481,2),(0,2),(0,2),(0.10499095251900001,2),(0,2),(0.20589113209464902,2),(0.043046721,2),(0,2),(0.20589113209464902,2),(0.248937853094649,2),(0,2),(0,2),(0,2),(0,2),(0,2),(0,2),(0,2),(0,2),(0,2),(0,2),(0,2),(0.387420489,2),(0,2),(0,2),(0,2),(0.387420489,2),(0,2),(0,2),(0,2),(0,2),(0,2),(0,2),(0,2),(0,2),(0,2),(0,2),(0,2),(0,2),(0,2),(0,2),(0,2),(0,2),(0,2),(0,2),(0,2),(0,4),(0,4),(0,4),(0,4),(0,4),(0,4),(0,4),(0,4),(0,4),(0,4),(1,4),(0,4),(0,4),(0,4),(1,4),(0,4),(0,4),(0,4),(0,4),(0,4),(0,4),(0,4),(0,4),(0,4),(0,4),(0,4),(0,4),(0,4),(0,4),(0.21499084800000007,4),(0,4),(0,4),(0,4),(0.21499084800000007,4),(0,4),(0,4),(0,4),(0,4),(0,4),(0,4),(0,4),(0,4),(0,4),(0,4),(0,4),(0,4),(0,4),(0,4),(0,4),(0,4),(0,4),(0,4),(0,4),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(0,6),(1,6),(0,6),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(0,5),(1,5),(0,5),(0,5),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0,10),(0.0021669309404970596,1),(0.0021668535812668904,1),(0,1),(0.000006589373194026555,1),(0.0023263051398720704,1),(0,1),(0,1),(0,1),(0,1),(0.000153305319031548,1),(0,1),(0.00000007735923017007343,1),(0.0000007735923017007343,1),(0.0000007735923017007343,1),(0,1),(0,1),(0.0000007735923017007343,1),(0,1),(0,1),(0.000011973107831842163,1),(0.000011973107831842163,1),(0,1),(0,1),(0.000011973107831842163,1),(0,1),(0,1),(0,1),(0,1),(0,1),(0,1),(0,1),(0,1),(0,1),(0,1),(0,1),(0,1),(0,1),(0,1),(0,1),(0,1),(0,1),(0,1),(0,1),(0,1),(0,1),(0,1),(0,1),(0,1),(0,1),(0,1),(0,1),(0.9993438675225141,1),(0.002164246944790277,1);
/*!40000 ALTER TABLE `cluster_weights` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `price` decimal(38,2) NOT NULL,
  `quantity` int NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbioxgbv59vetrxe0ejfubep1w` (`order_id`),
  KEY `FKocimc7dtr037rh4ls4l95nlfi` (`product_id`),
  CONSTRAINT `FKbioxgbv59vetrxe0ejfubep1w` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `FKocimc7dtr037rh4ls4l95nlfi` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (10.99,1,1,1,1),(20.49,1,2,1,2),(8.99,1,3,1,5),(8.99,1,4,2,5),(8.99,1,5,3,5),(50.00,1,6,3,9),(30.99,1,7,3,8),(22.99,1,8,4,12),(10.99,1,9,4,1),(8.99,1,10,4,5),(30.99,1,11,5,8),(22.99,1,12,5,12),(25.30,1,13,6,4),(8.99,1,14,6,5),(10.99,1,15,7,1),(15.75,1,16,7,3),(48.99,1,17,8,19),(30.99,1,18,8,8),(30.99,1,19,9,8),(29.49,1,20,9,18),(9.99,1,21,9,15),(9.99,1,22,10,15),(11.50,1,23,10,11),(10.99,1,24,11,1),(10.99,1,25,12,1),(10.99,1,26,13,1),(10.99,3,28,15,1),(5.00,1,29,15,53),(5.00,1,30,16,52),(5.00,1,31,17,51),(5.00,1,32,18,52),(5.00,1,33,19,52);
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `total_price` decimal(38,2) DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_date` datetime(6) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `status` enum('CANCELLED','COMPLETED','DELIVERED','NEW','PAID','SHIPPED') DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK32ql8ubntj5uh44ph9659tiih` (`user_id`),
  CONSTRAINT `FK32ql8ubntj5uh44ph9659tiih` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (40.47,1,'2025-01-24 08:50:32.324940',1,'COMPLETED'),(8.99,2,'2025-01-24 08:50:39.538422',2,'COMPLETED'),(89.98,3,'2025-01-24 08:50:47.080935',3,'COMPLETED'),(42.97,4,'2025-01-24 10:24:17.560263',10,'COMPLETED'),(53.98,5,'2025-01-24 10:24:22.809585',11,'COMPLETED'),(34.29,6,'2025-01-24 10:43:34.169580',6,'COMPLETED'),(26.74,7,'2025-01-24 10:43:41.885556',7,'COMPLETED'),(79.98,8,'2025-01-24 10:43:50.618201',10,'COMPLETED'),(70.47,9,'2025-01-24 10:44:09.232293',13,'COMPLETED'),(21.49,10,'2025-01-24 10:45:02.478004',5,'COMPLETED'),(10.99,11,'2025-01-26 11:07:01.806579',1,'NEW'),(10.99,12,'2025-01-26 11:49:44.718764',1,'NEW'),(10.99,13,'2025-01-26 11:54:12.098417',1,'NEW'),(37.97,15,'2025-01-27 14:12:54.808828',1,'NEW'),(5.00,16,'2025-01-27 14:13:06.589378',21,'NEW'),(5.00,17,'2025-01-27 14:13:20.772801',22,'NEW'),(5.00,18,'2025-02-01 16:40:13.319059',1,'PAID'),(5.00,19,'2025-02-01 16:51:38.075475',2,'PAID');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `amount` double NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `message` varchar(255) NOT NULL,
  `status` varchar(255) NOT NULL,
  `currency` enum('RUB','USD') NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
INSERT INTO `payments` VALUES (40.47,'2025-01-24 08:51:56.522144',1,1,'Payment failed due to insufficient funds or other error','FAILED','RUB'),(40.47,'2025-01-24 08:52:00.849935',2,1,'Payment was successful','SUCCESS','RUB'),(8.99,'2025-01-24 08:54:05.058988',3,2,'Payment was successful','SUCCESS','RUB'),(89.98,'2025-01-24 08:54:27.550744',4,3,'Payment was successful','SUCCESS','RUB'),(42.97,'2025-01-24 10:26:02.728523',5,4,'Payment was successful','SUCCESS','RUB'),(53.98,'2025-01-24 10:26:21.749805',6,5,'Payment was successful','SUCCESS','RUB'),(34.29,'2025-01-24 10:48:12.783431',7,6,'Payment was successful','SUCCESS','RUB'),(26.74,'2025-01-24 10:48:31.687861',8,7,'Payment was successful','SUCCESS','RUB'),(79.98,'2025-01-24 10:48:57.124095',9,8,'Payment failed due to insufficient funds or other error','FAILED','RUB'),(79.98,'2025-01-24 10:49:00.515234',10,8,'Payment was successful','SUCCESS','RUB'),(70.47,'2025-01-24 10:49:26.467001',11,9,'Payment was successful','SUCCESS','RUB'),(21.49,'2025-01-24 10:49:50.413040',12,10,'Payment failed due to insufficient funds or other error','FAILED','RUB'),(21.49,'2025-01-24 10:49:53.611222',13,10,'Payment was successful','SUCCESS','RUB'),(5,'2025-02-01 16:44:51.254826',14,18,'Payment was successful','SUCCESS','RUB'),(5,'2025-02-01 16:52:10.206258',15,19,'Payment failed due to insufficient funds or other error','FAILED','RUB'),(5,'2025-02-01 16:52:14.035904',16,19,'Payment failed due to insufficient funds or other error','FAILED','RUB'),(5,'2025-02-01 16:52:22.586744',17,19,'Payment was successful','SUCCESS','RUB');
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_characteristics`
--

DROP TABLE IF EXISTS `product_characteristics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_characteristics` (
  `product_id` bigint NOT NULL,
  `characteristic` varchar(255) DEFAULT NULL,
  KEY `FKlwuvy71q239qxmgor4u6ni4ej` (`product_id`),
  CONSTRAINT `FKlwuvy71q239qxmgor4u6ni4ej` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_characteristics`
--

LOCK TABLES `product_characteristics` WRITE;
/*!40000 ALTER TABLE `product_characteristics` DISABLE KEYS */;
INSERT INTO `product_characteristics` VALUES (51,'char_1'),(51,'char_2'),(52,'char_1'),(52,'char_2'),(53,'char_1'),(11,'char_1'),(11,'char_2'),(15,'char_2'),(1,'char_3'),(2,'char_3'),(5,'char_3'),(5,'char_3'),(12,'char_1'),(12,'char_3');
/*!40000 ALTER TABLE `product_characteristics` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `popularity` double NOT NULL,
  `price` decimal(38,2) NOT NULL,
  `stock_quantity` int NOT NULL,
  `category_id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `updated_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) NOT NULL,
  `image_path` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKog2rp4qthbtt2lfyhfo32lsw9` (`category_id`),
  CONSTRAINT `FKog2rp4qthbtt2lfyhfo32lsw9` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (8.5,10.99,41,1,NULL,1,'2025-01-27 14:12:54.911823','Description for Product 1','string','Product 1'),(9.1,20.49,29,2,NULL,2,'2025-01-24 08:50:32.479940','Description for Product 2','images/product2.jpg','Product 2'),(7.8,15.75,39,3,NULL,3,'2025-01-24 10:43:41.930556','Description for Product 3','images/product3.jpg','Product 3'),(6.5,25.30,14,1,NULL,4,'2025-01-24 10:43:34.219582','Description for Product 4','images/product4.jpg','Product 4'),(7.2,8.99,65,2,NULL,5,'2025-01-24 10:43:34.223581','Description for Product 5','images/product5.jpg','Product 5'),(8.9,18.50,20,3,NULL,6,NULL,'Description for Product 6','images/product6.jpg','Product 6'),(9.4,12.00,60,1,NULL,7,NULL,'Description for Product 7','images/product7.jpg','Product 7'),(5.5,30.99,6,2,NULL,8,'2025-01-24 10:44:09.282293','Description for Product 8','images/product8.jpg','Product 8'),(4.6,50.00,7,3,NULL,9,'2025-01-24 08:50:47.163935','Description for Product 9','images/product9.jpg','Product 9'),(9,19.95,25,1,NULL,10,'2025-01-26 15:18:25.979925','Description for Product 10','images/product10.jpg','Product 10'),(7.3,11.50,44,2,NULL,11,'2025-01-24 10:45:02.526005','Description for Product 11','images/product11.jpg','Product 11'),(8.8,22.99,33,3,NULL,12,'2025-01-24 10:24:22.855589','Description for Product 12','images/product12.jpg','Product 12'),(6.8,14.30,50,1,NULL,13,NULL,'Description for Product 13','images/product13.jpg','Product 13'),(8.5,26.75,20,2,NULL,14,NULL,'Description for Product 14','images/product14.jpg','Product 14'),(7.4,9.99,63,3,NULL,15,'2025-01-24 10:45:02.523005','Description for Product 15','images/product15.jpg','Product 15'),(8.7,18.00,22,1,NULL,16,NULL,'Description for Product 16','images/product16.jpg','Product 16'),(9.6,13.99,55,2,NULL,17,NULL,'Description for Product 17','images/product17.jpg','Product 17'),(5.4,29.49,11,3,NULL,18,'2025-01-24 10:44:09.286296','Description for Product 18','images/product18.jpg','Product 18'),(4.8,48.99,5,1,NULL,19,'2025-01-24 10:43:50.667195','Description for Product 19','images/product19.jpg','Product 19'),(8.2,21.45,18,2,NULL,20,NULL,'Description for Product 20','images/product20.jpg','Product 20'),(7.6,9.89,48,3,NULL,21,NULL,'Description for Product 21','images/product21.jpg','Product 21'),(8.3,24.95,32,1,NULL,22,NULL,'Description for Product 22','images/product22.jpg','Product 22'),(6.9,16.40,42,2,NULL,23,NULL,'Description for Product 23','images/product23.jpg','Product 23'),(8.4,28.80,11,3,NULL,24,NULL,'Description for Product 24','images/product24.jpg','Product 24'),(7.7,10.50,75,1,NULL,25,NULL,'Description for Product 25','images/product25.jpg','Product 25'),(8.6,17.80,23,2,NULL,26,NULL,'Description for Product 26','images/product26.jpg','Product 26'),(9.7,12.25,58,3,NULL,27,NULL,'Description for Product 27','images/product27.jpg','Product 27'),(5.6,31.30,13,1,NULL,28,NULL,'Description for Product 28','images/product28.jpg','Product 28'),(4.9,47.90,9,2,NULL,29,NULL,'Description for Product 29','images/product29.jpg','Product 29'),(8.1,20.50,19,3,NULL,30,NULL,'Description for Product 30','images/product30.jpg','Product 30'),(7.8,10.89,47,1,NULL,31,NULL,'Description for Product 31','images/product31.jpg','Product 31'),(8.7,23.95,28,2,NULL,32,NULL,'Description for Product 32','images/product32.jpg','Product 32'),(6.7,18.50,36,3,NULL,33,NULL,'Description for Product 33','images/product33.jpg','Product 33'),(8,27.05,16,1,NULL,34,NULL,'Description for Product 34','images/product34.jpg','Product 34'),(7.5,9.45,70,2,NULL,35,NULL,'Description for Product 35','images/product35.jpg','Product 35'),(8.2,19.60,21,3,NULL,36,NULL,'Description for Product 36','images/product36.jpg','Product 36'),(9.8,15.99,60,1,NULL,37,NULL,'Description for Product 37','images/product37.jpg','Product 37'),(5.9,33.45,14,2,NULL,38,NULL,'Description for Product 38','images/product38.jpg','Product 38'),(4.7,49.99,7,3,NULL,39,NULL,'Description for Product 39','images/product39.jpg','Product 39'),(8.3,22.49,17,1,NULL,40,NULL,'Description for Product 40','images/product40.jpg','Product 40'),(7.9,11.99,46,2,NULL,41,NULL,'Description for Product 41','images/product41.jpg','Product 41'),(9,25.99,29,3,NULL,42,NULL,'Description for Product 42','images/product42.jpg','Product 42'),(6.6,14.75,39,1,NULL,43,NULL,'Description for Product 43','images/product43.jpg','Product 43'),(8.1,29.00,9,2,NULL,44,NULL,'Description for Product 44','images/product44.jpg','Product 44'),(7.1,10.99,69,3,NULL,45,NULL,'Description for Product 45','images/product45.jpg','Product 45'),(8.4,18.90,24,1,NULL,46,NULL,'Description for Product 46','images/product46.jpg','Product 46'),(9.5,13.50,55,2,NULL,47,NULL,'Description for Product 47','images/product47.jpg','Product 47'),(5.4,30.99,15,3,NULL,48,NULL,'Description for Product 48','images/product48.jpg','Product 48'),(4.6,46.80,10,1,NULL,49,NULL,'Description for Product 49','images/product49.jpg','Product 49'),(8.6,21.95,20,2,NULL,50,NULL,'Description for Product 50','images/product50.jpg','Product 50'),(6,5.00,49,1,'2025-01-27 14:06:28.380439',51,'2025-01-27 14:13:20.818802','Product 55','string','Product 51'),(7,5.00,47,1,'2025-01-27 14:07:20.861311',52,'2025-02-01 16:51:38.096474','Product 56','string','Product 52'),(6,5.00,49,1,'2025-01-27 14:07:47.390840',53,'2025-01-27 14:12:54.927823','Product 57','string','Product 53');
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ratings`
--

DROP TABLE IF EXISTS `ratings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ratings` (
  `rating` double NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `item_id` bigint NOT NULL,
  `timestamp` bigint DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ratings`
--

LOCK TABLES `ratings` WRITE;
/*!40000 ALTER TABLE `ratings` DISABLE KEYS */;
INSERT INTO `ratings` VALUES (8.5,1,1,1737708632436,1),(9.1,2,2,1737708632444,1),(7.2,3,5,1737708632448,1),(7.2,4,5,1737708639582,2),(7.2,5,5,1737708647137,3),(4.6,6,9,1737708647140,3),(5.5,7,8,1737708647144,3),(8.8,8,12,1737714257626,10),(8.5,9,1,1737714257631,10),(7.2,10,5,1737714257636,10),(5.5,11,8,1737714262844,11),(8.8,12,12,1737714262847,11),(6.5,13,4,1737715414208,6),(7.2,14,5,1737715414213,6),(8.5,15,1,1737715421920,7),(7.8,16,3,1737715421922,7),(4.8,17,19,1737715430660,10),(5.5,18,8,1737715430662,10),(5.5,19,8,1737715449271,13),(5.4,20,18,1737715449274,13),(7.4,21,15,1737715449277,13),(7.4,22,15,1737715502514,5),(7.3,23,11,1737715502518,5),(8.5,24,1,1737889621892,1),(8.5,25,1,1737892184780,1),(8.5,26,1,1737892452167,1),(9,27,10,1737904563498,2),(8.5,28,1,1737987174896,1),(6,29,53,1737987174901,1),(7,30,52,1737987186618,21),(6,31,51,1737987200801,22),(7,32,52,1738428013380,1),(7,33,52,1738428698092,2);
/*!40000 ALTER TABLE `ratings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `active` bit(1) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `updated_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('ADMIN','USER') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (_binary '','2024-01-01 00:00:00.000000',1,'2024-01-01 00:00:00.000000','admin@example.com','Admin','System','$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG','ADMIN'),(_binary '','2024-01-02 10:15:00.000000',2,'2024-01-02 10:15:00.000000','john.admin@example.com','John','Admin','$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG','ADMIN'),(_binary '','2024-01-03 12:30:00.000000',3,'2024-01-03 12:30:00.000000','alice.j@example.com','Alice','Johnson','$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG','USER'),(_binary '','2024-01-04 14:45:00.000000',4,'2024-01-04 14:45:00.000000','bob.smith@example.com','Bob','Smith','$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG','USER'),(_binary '','2024-01-05 16:20:00.000000',5,'2024-01-05 16:20:00.000000','carol.w@example.com','Carol','Williams','$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG','USER'),(_binary '','2024-01-06 09:10:00.000000',6,'2024-01-06 09:10:00.000000','david.b@example.com','David','Brown','$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG','USER'),(_binary '','2024-01-07 11:25:00.000000',7,'2024-01-07 11:25:00.000000','emma.d@example.com','Emma','Davis','$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG','USER'),(_binary '\0','2024-01-08 13:40:00.000000',8,'2024-01-08 13:40:00.000000','frank.w@example.com','Frank','Wilson','$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG','USER'),(_binary '\0','2024-01-09 15:55:00.000000',9,'2024-01-09 15:55:00.000000','grace.t@example.com','Grace','Taylor','$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG','USER'),(_binary '','2024-01-10 17:30:00.000000',10,'2024-01-10 17:30:00.000000','henry.a@example.com','Henry','Anderson','$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG','USER'),(_binary '','2024-01-11 08:45:00.000000',11,'2024-01-11 08:45:00.000000','isabel.m@example.com','Isabel','Martinez','$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG','USER'),(_binary '','2024-01-12 10:20:00.000000',12,'2024-01-12 10:20:00.000000','james.t@example.com','James','Thomas','$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG','USER'),(_binary '','2024-01-13 12:35:00.000000',13,'2024-01-13 12:35:00.000000','kelly.w@example.com','Kelly','White','$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG','USER'),(_binary '','2024-01-14 14:50:00.000000',14,'2024-01-14 14:50:00.000000','liam.h@example.com','Liam','Harris','$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG','USER'),(_binary '','2024-01-15 16:15:00.000000',15,'2024-01-15 16:15:00.000000','mia.c@example.com','Mia','Clark','$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG','USER'),(_binary '','2024-01-16 09:30:00.000000',16,'2024-01-16 09:30:00.000000','noah.l@example.com','Noah','Lewis','$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG','USER'),(_binary '','2024-01-17 11:45:00.000000',17,'2024-01-17 11:45:00.000000','olivia.l@example.com','Olivia','Lee','$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG','USER'),(_binary '','2024-01-18 14:00:00.000000',18,'2024-01-18 14:00:00.000000','peter.w@example.com','Peter','Walker','$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG','USER'),(_binary '','2024-01-19 16:15:00.000000',19,'2024-01-19 16:15:00.000000','quinn.h@example.com','Quinn','Hall','$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG','USER'),(_binary '','2024-01-20 08:30:00.000000',20,'2024-01-20 08:30:00.000000','rachel.y@example.com','Rachel','Young','$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG','USER'),(_binary '','2024-01-21 10:45:00.000000',21,'2024-01-21 10:45:00.000000','samuel.k@example.com','Samuel','King','$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG','USER'),(_binary '','2024-01-22 13:00:00.000000',22,'2024-01-22 13:00:00.000000','tara.w@example.com','Tara','Wright','$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG','USER');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'ccodb'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-02-01 22:41:23
