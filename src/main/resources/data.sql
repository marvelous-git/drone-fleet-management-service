
INSERT INTO "PUBLIC"."DRONE" VALUES
(1, 100, TIMESTAMP '2023-04-18 06:42:06.090651', TIMESTAMP '2023-04-18 06:44:38.398226', 'ACTIVE', 'LIGHTWEIGHT', '08QDDE4S0001A6', 'LOADED', 100),
(2, 100, TIMESTAMP '2023-04-18 06:42:26.360695', TIMESTAMP '2023-04-18 06:46:42.766328', 'ACTIVE', 'MIDDLEWEIGHT', '0A2BDG1AAG0A01', 'LOADED', 200),
(3, 100, TIMESTAMP '2023-04-18 06:42:49.496838', NULL, 'ACTIVE', 'CRUISERWEIGHT', '1B7VDE71K0D3HM', 'IDLE', 300),
(4, 100, TIMESTAMP '2023-04-18 06:43:09.222197', NULL, 'ACTIVE', 'CRUISERWEIGHT', '0A2BDF1BAC0A01', 'IDLE', 400),
(5, 100, TIMESTAMP '2023-04-18 06:43:26.173401', TIMESTAMP '2023-04-18 06:48:16.797126', 'ACTIVE', 'HEAVYWEIGHT', '08QDEB4S000JYK', 'LOADED', 500);     

INSERT INTO "PUBLIC"."MEDICATION" VALUES
(1, '44919_0117_01', TIMESTAMP '2023-04-18 06:44:38.394208', NULL, 'ACTIVE', 'https://www.example.com/images/example-image01.jpg', 'Acetaminophen', 20, 1),
(2, '49349_623_03', TIMESTAMP '2023-04-18 06:44:38.3963', NULL, 'ACTIVE', 'https://www.example.com/images/example-image02.jpg', 'Aspirin', 50, 1),
(3, '41163_108_50', TIMESTAMP '2023-04-18 06:46:42.762326', NULL, 'ACTIVE', 'https://www.example.com/images/example-image03.jpg', 'Ibuprofen', 100, 2),
(4, '0781_2617_01', TIMESTAMP '2023-04-18 06:46:42.765318', NULL, 'ACTIVE', 'https://www.example.com/images/example-image04.jpg', 'Amoxicillin', 50, 2),
(5, '65862_150_90', TIMESTAMP '2023-04-18 06:48:16.796163', NULL, 'ACTIVE', 'https://www.example.com/images/example-image05.jpg', 'Metformin', 495, 5);