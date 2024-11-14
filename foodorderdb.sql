-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1
-- Thời gian đã tạo: Th10 14, 2024 lúc 11:24 PM
-- Phiên bản máy phục vụ: 10.4.32-MariaDB
-- Phiên bản PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `foodorderdb`
--

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `comment`
--

CREATE TABLE `comment` (
  `id` int(11) NOT NULL,
  `author` varchar(255) NOT NULL,
  `content` text NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `meal_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `comment`
--

INSERT INTO `comment` (`id`, `author`, `content`, `created_at`, `meal_id`) VALUES
(1, 'John Doe', 'Delicious meal, highly recommended!', '2024-11-14 09:49:40', 1),
(2, 'Jane Smith', 'Could use a bit more seasoning.', '2024-11-13 17:00:00', 1),
(3, 'Alice Johnson', 'Perfectly cooked and tasty!', '2024-11-14 09:49:40', 2),
(4, 'Bob Brown', 'Not my favorite, but it was okay.', '2024-11-14 09:49:40', 2),
(5, 'Chris Lee', 'Loved every bite of this meal!', '2024-11-13 17:00:00', 3),
(10, 'Current User', '123', '2024-11-14 14:24:07', 1);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `final_order`
--

CREATE TABLE `final_order` (
  `id` bigint(20) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  `final_price` int(11) NOT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `final_order`
--

INSERT INTO `final_order` (`id`, `address`, `date`, `final_price`, `phone_number`, `status`, `user_id`) VALUES
(1, 'Hajduk Veljkova 12', '2024-11-12 08:50:03', 1080, '060123456', 'IN DELIVERY', 1),
(2, 'Hajduk Veljkova 12', '2024-11-15 00:05:19', 540, '060123456', 'IN DELIVERY', 1),
(3, 'Hajduk Veljkova 12', '2024-11-15 02:11:34', 540, '060123456', 'ORDERED', 1);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `meal`
--

CREATE TABLE `meal` (
  `id` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `meal_type_id` int(11) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `image` longtext DEFAULT NULL,
  `image_name` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `meal`
--

INSERT INTO `meal` (`id`, `name`, `price`, `meal_type_id`, `description`, `image`, `image_name`, `is_deleted`) VALUES
(1, 'CAPRICCIOSA', 600.00, 1, 'mesiiiiiiiiiii', '/uploads/meal/capricciosa.jpg', NULL, b'0'),
(2, 'SLATKA PALACINKA', 600.00, 4, NULL, '/uploads/meal/slatka_palacinka.jpg', NULL, b'0'),
(3, 'RUSKA SALATA', 600.00, 3, NULL, '/uploads/meal/ruska_salata.jpg', NULL, b'0'),
(4, 'NESTOOO', 6002.00, 3, NULL, '/uploads/meal/nestooo.jpg', NULL, b'0'),
(15, 'Pham Van Phuc', 12345.00, 1, '123234234', '/uploads/meal/Pham_Van_Phuc.jpg', 'Pham_Van_Phuc.png', b'0');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `meal_type`
--

CREATE TABLE `meal_type` (
  `id` int(11) NOT NULL,
  `type_name` varchar(100) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `image` longtext DEFAULT NULL,
  `image_name` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `meal_type`
--

INSERT INTO `meal_type` (`id`, `type_name`, `description`, `image`, `image_name`, `is_deleted`) VALUES
(1, 'PIZZA', NULL, '/uploads/typeMeal/pizza.jpg', NULL, b'0'),
(2, 'MAIN COURSE', NULL, '/uploads/typeMeal/main_course.jpg', NULL, b'0'),
(3, 'SALAD', NULL, '/uploads/typeMeal/salad.jpg', NULL, b'0'),
(4, 'PANCAKE', NULL, '/uploads/typeMeal/pancake.jpg', NULL, b'0'),
(5, 'BURGER', NULL, '/uploads/typeMeal/burger.jpg', NULL, b'0'),
(6, 'PASTA', NULL, '/uploads/typeMeal/pasta.jpg', NULL, b'0'),
(19, 'Phuc', '234', '/uploads/typeMeal/Phuc.png', 'Phuc.png', b'0');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `order_item`
--

CREATE TABLE `order_item` (
  `id` bigint(20) NOT NULL,
  `meal_description` varchar(255) DEFAULT NULL,
  `meal_image` longtext DEFAULT NULL,
  `meal_image_name` varchar(255) DEFAULT NULL,
  `meal_name` varchar(255) DEFAULT NULL,
  `meal_price` int(11) NOT NULL,
  `meal_type_name` varchar(255) DEFAULT NULL,
  `quantity` int(11) NOT NULL,
  `final_order_id` bigint(20) DEFAULT NULL,
  `meal_id` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `order_item`
--

INSERT INTO `order_item` (`id`, `meal_description`, `meal_image`, `meal_image_name`, `meal_name`, `meal_price`, `meal_type_name`, `quantity`, `final_order_id`, `meal_id`) VALUES
(1, NULL, '/uploads/meal/capricciosa.jpg', NULL, 'CAPRICCIOSA', 600, 'PIZZA', 2, 1, 1),
(2, 'mesiiiiiiiiiii', '/uploads/meal/capricciosa.jpg', NULL, 'CAPRICCIOSA', 600, 'PIZZA', 1, 2, 1),
(3, 'mesiiiiiiiiiii', NULL, NULL, 'CAPRICCIOSA', 600, 'PIZZA', 1, 3, 1);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `user`
--

CREATE TABLE `user` (
  `id` int(11) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `deleted` tinyint(1) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `first_name` varchar(50) DEFAULT NULL,
  `last_name` varchar(50) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phone_number` varchar(20) DEFAULT NULL,
  `role` int(11) DEFAULT NULL,
  `username` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `user`
--

INSERT INTO `user` (`id`, `address`, `deleted`, `email`, `first_name`, `last_name`, `password`, `phone_number`, `role`, `username`) VALUES
(1, 'Hajduk Veljkova 12', 0, 'cileb411@gmail.com', 'Kristijan', 'Bujak', '123', '060123456', 1, 'a'),
(2, 'Zeleznicka 12', 0, 'lavezzimario46@gmail.com', 'Marko', 'Markovic', '123', '0611234321', 0, 'b'),
(3, 'Zeleznicka 12', 0, 'lasadasdario46@gmail.com', 'Marko', 'Markovic', '123', '0611234123123', 2, 'c');

--
-- Chỉ mục cho các bảng đã đổ
--

--
-- Chỉ mục cho bảng `comment`
--
ALTER TABLE `comment`
  ADD PRIMARY KEY (`id`),
  ADD KEY `meal_id` (`meal_id`);

--
-- Chỉ mục cho bảng `final_order`
--
ALTER TABLE `final_order`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKjy6q1vfw5ddfbjwjwb6ix7kmd` (`user_id`);

--
-- Chỉ mục cho bảng `meal`
--
ALTER TABLE `meal`
  ADD PRIMARY KEY (`id`),
  ADD KEY `meal_type_id` (`meal_type_id`);

--
-- Chỉ mục cho bảng `meal_type`
--
ALTER TABLE `meal_type`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `order_item`
--
ALTER TABLE `order_item`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKfv8x677e8u1tjupgikg63bodv` (`final_order_id`),
  ADD KEY `FKfkjjrsukukcxgsivytiyuccap` (`meal_id`);

--
-- Chỉ mục cho bảng `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT cho các bảng đã đổ
--

--
-- AUTO_INCREMENT cho bảng `comment`
--
ALTER TABLE `comment`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT cho bảng `final_order`
--
ALTER TABLE `final_order`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT cho bảng `meal`
--
ALTER TABLE `meal`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT cho bảng `meal_type`
--
ALTER TABLE `meal_type`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT cho bảng `order_item`
--
ALTER TABLE `order_item`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Các ràng buộc cho các bảng đã đổ
--

--
-- Các ràng buộc cho bảng `comment`
--
ALTER TABLE `comment`
  ADD CONSTRAINT `comment_ibfk_1` FOREIGN KEY (`meal_id`) REFERENCES `meal` (`id`) ON DELETE CASCADE;

--
-- Các ràng buộc cho bảng `meal`
--
ALTER TABLE `meal`
  ADD CONSTRAINT `meal_ibfk_1` FOREIGN KEY (`meal_type_id`) REFERENCES `meal_type` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
