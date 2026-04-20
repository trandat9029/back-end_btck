CREATE TABLE IF NOT EXISTS departments (
    department_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    department_name VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO departments (department_name) VALUES
('Phòng DEV1'),
('Phòng DEV2'),
('Phòng DEV3'),
('Phòng DEV4'),
('Phòng DEV5'),
('Phòng DEV6'),
('Phòng DEV7'),
('Phòng DEV8'),
('Phòng DEV9'),
('Phòng DEV10'),
('Phòng DEV11');

CREATE TABLE IF NOT EXISTS employees (
    employee_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    department_id BIGINT NOT NULL,
    employee_name VARCHAR(255) NOT NULL,
    employee_name_kana VARCHAR(255),
    employee_birth_date DATE,
    employee_email VARCHAR(255) NOT NULL,
    employee_telephone VARCHAR(50),
    employee_login_id VARCHAR(50) NOT NULL UNIQUE,
    employee_login_password VARCHAR(100) NOT NULL,
    employee_role TINYINT NOT NULL DEFAULT 1,

    CONSTRAINT fk_employee_department
        FOREIGN KEY (department_id)
        REFERENCES departments(department_id)
);
INSERT INTO `employees` (`department_id`, `employee_name`, `employee_name_kana`, `employee_birth_date`, `employee_email`, `employee_telephone`, `employee_login_id`, `employee_login_password`, `employee_role`) VALUES
-- Nhóm trùng tên 'An' để test Level và End Date
(2, 'An', 'アン', '1990-05-05', 'an1@example.com', '0911111111', 'user01', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy', 1),
(3, 'An', 'アン', '1992-01-01', 'an2@example.com', '0911111112', 'user02', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy', 1),
(4, 'An', 'アン', '1995-10-10', 'an3@example.com', '0911111113', 'user03', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy', 1),

-- Nhóm trùng tên 'Bình'
(2, 'Bình', 'ビン', '1988-03-03', 'binh1@example.com', '0922222221', 'user04', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy', 1),
(5, 'Bình', 'ビン', '1993-07-07', 'binh2@example.com', '0922222222', 'user05', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy', 1),

-- Các tên khác để test Sort Name
(6, 'Cường', 'クオン', '1994-08-25', 'cuong@example.com', '0933333333', 'user06', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy', 1);

CREATE TABLE IF NOT EXISTS certifications (
    certification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    certification_name VARCHAR(50) NOT NULL,
    certification_level INT NOT NULL
);
INSERT INTO certifications (certification_name, certification_level) VALUES
('Trình độ tiếng nhật cấp 1', 1),
('Trình độ tiếng nhật cấp 2', 2),
('Trình độ tiếng nhật cấp 3', 3),
('Trình độ tiếng nhật cấp 4', 4),
('Trình độ tiếng nhật cấp 5', 5);

CREATE TABLE IF NOT EXISTS employees_certifications (
    employee_certification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    certification_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    score DECIMAL(5,2) NOT NULL,

    CONSTRAINT fk_emp_cert_employee
        FOREIGN KEY (employee_id)
        REFERENCES employees(employee_id),

    CONSTRAINT fk_emp_cert_certification
        FOREIGN KEY (certification_id)
        REFERENCES certifications(certification_id)
);
-- User01: Tên 'An', Trình độ N1 (Level 1), Hết hạn 2030
INSERT INTO `employees_certifications` (`employee_id`, `certification_id`, `start_date`, `end_date`, `score`)
SELECT employee_id, 1, '2020-01-01', '2030-01-01', 170 FROM employees WHERE employee_login_id = 'user01';

-- User02: Tên 'An', Trình độ N1 (Level 1), Hết hạn 2025 (Để test ưu tiên 3: End Date ASC)
INSERT INTO `employees_certifications` (`employee_id`, `certification_id`, `start_date`, `end_date`, `score`)
SELECT employee_id, 1, '2020-01-01', '2025-01-01', 150 FROM employees WHERE employee_login_id = 'user02';

-- User03: Tên 'An', Trình độ N3 (Level 3) (Để test ưu tiên 2: Level ASC)
INSERT INTO `employees_certifications` (`employee_id`, `certification_id`, `start_date`, `end_date`, `score`)
SELECT employee_id, 3, '2020-01-01', '2028-01-01', 120 FROM employees WHERE employee_login_id = 'user03';

-- User04: Tên 'Bình', Trình độ N2
INSERT INTO `employees_certifications` (`employee_id`, `certification_id`, `start_date`, `end_date`, `score`)
SELECT employee_id, 2, '2021-01-01', '2029-01-01', 140 FROM employees WHERE employee_login_id = 'user04';


INSERT INTO employees (department_id, employee_name, employee_email, employee_login_id, employee_login_password, employee_role)
VALUES (1, 'Administrator', 'la@luvina.net', 'admin', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy', 0);
