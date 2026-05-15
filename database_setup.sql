CREATE DATABASE IF NOT EXISTS `FinalPhase`
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `FinalPhase`;

CREATE TABLE IF NOT EXISTS `Branches` (
  `BranchID` INT NOT NULL AUTO_INCREMENT,
  `BranchName` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`BranchID`),
  UNIQUE KEY `uk_branches_name` (`BranchName`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `Customer` (
  `customerId` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `phoneNumber` VARCHAR(30) NOT NULL,
  `paymentMethod` VARCHAR(50) NOT NULL,
  `isDiningIn` TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`customerId`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `Menu` (
  `menuItemId` INT NOT NULL AUTO_INCREMENT,
  `itemName` VARCHAR(150) NOT NULL,
  `itemPrice` DECIMAL(10,2) NOT NULL,
  `category` VARCHAR(100) NOT NULL,
  `size` VARCHAR(50) DEFAULT NULL,
  `isAvailable` TINYINT(1) NOT NULL DEFAULT 1,
  `imagePath` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`menuItemId`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `Orders` (
  `orderId` INT NOT NULL AUTO_INCREMENT,
  `customerId` INT NOT NULL,
  `menuItemId` INT NOT NULL,
  `branchId` INT NOT NULL,
  `quantity` INT NOT NULL,
  `orderDate` DATETIME NOT NULL,
  PRIMARY KEY (`orderId`),
  KEY `idx_orders_customer` (`customerId`),
  KEY `idx_orders_menu_item` (`menuItemId`),
  KEY `idx_orders_branch` (`branchId`),
  CONSTRAINT `fk_orders_customer`
    FOREIGN KEY (`customerId`) REFERENCES `Customer` (`customerId`)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT `fk_orders_menu`
    FOREIGN KEY (`menuItemId`) REFERENCES `Menu` (`menuItemId`)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT `fk_orders_branch`
    FOREIGN KEY (`branchId`) REFERENCES `Branches` (`BranchID`)
    ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `MonthlySalary` (
  `BranchID` INT NOT NULL,
  `Month` VARCHAR(20) NOT NULL,
  `SalaryAmount` DECIMAL(10,2) NOT NULL,
  `PaymentDate` DATE NOT NULL,
  `Notes` TEXT DEFAULT NULL,
  PRIMARY KEY (`BranchID`, `Month`),
  CONSTRAINT `fk_monthlysalary_branch`
    FOREIGN KEY (`BranchID`) REFERENCES `Branches` (`BranchID`)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `EmployeeSalaries` (
  `EmployeeSalaryID` INT NOT NULL AUTO_INCREMENT,
  `BranchID` INT NOT NULL,
  `MonthlySalary` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`EmployeeSalaryID`),
  KEY `idx_employee_salaries_branch` (`BranchID`),
  CONSTRAINT `fk_employee_salaries_branch`
    FOREIGN KEY (`BranchID`) REFERENCES `Branches` (`BranchID`)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

INSERT INTO `Branches` (`BranchName`)
SELECT 'Main Branch'
WHERE NOT EXISTS (
  SELECT 1 FROM `Branches` WHERE `BranchName` = 'Main Branch'
);

INSERT INTO `Branches` (`BranchName`)
SELECT 'North Branch'
WHERE NOT EXISTS (
  SELECT 1 FROM `Branches` WHERE `BranchName` = 'North Branch'
);

INSERT INTO `Branches` (`BranchName`)
SELECT 'South Branch'
WHERE NOT EXISTS (
  SELECT 1 FROM `Branches` WHERE `BranchName` = 'South Branch'
);

INSERT INTO `Menu` (`itemName`, `itemPrice`, `category`, `size`, `isAvailable`, `imagePath`)
SELECT 'Classic Burger', 35.00, 'Burgers', 'Regular', 1, 'C:/Users/BisanCo/eclipse-workspace/dataBaseProj/menu-burger.png'
WHERE NOT EXISTS (
  SELECT 1 FROM `Menu` WHERE `itemName` = 'Classic Burger'
);

INSERT INTO `Menu` (`itemName`, `itemPrice`, `category`, `size`, `isAvailable`, `imagePath`)
SELECT 'Cheese Burger', 39.00, 'Burgers', 'Regular', 1, 'C:/Users/BisanCo/eclipse-workspace/dataBaseProj/menuLogo.png'
WHERE NOT EXISTS (
  SELECT 1 FROM `Menu` WHERE `itemName` = 'Cheese Burger'
);

INSERT INTO `Menu` (`itemName`, `itemPrice`, `category`, `size`, `isAvailable`, `imagePath`)
SELECT 'Caesar Salad', 28.00, 'Salads', 'Medium', 1, 'C:/Users/BisanCo/eclipse-workspace/dataBaseProj/category.png'
WHERE NOT EXISTS (
  SELECT 1 FROM `Menu` WHERE `itemName` = 'Caesar Salad'
);

INSERT INTO `Menu` (`itemName`, `itemPrice`, `category`, `size`, `isAvailable`, `imagePath`)
SELECT 'Margherita Pizza', 48.00, 'Pizza', 'Large', 1, 'C:/Users/BisanCo/eclipse-workspace/dataBaseProj/RR.png'
WHERE NOT EXISTS (
  SELECT 1 FROM `Menu` WHERE `itemName` = 'Margherita Pizza'
);

INSERT INTO `Menu` (`itemName`, `itemPrice`, `category`, `size`, `isAvailable`, `imagePath`)
SELECT 'Orange Juice', 16.00, 'Drinks', 'Medium', 1, 'C:/Users/BisanCo/eclipse-workspace/dataBaseProj/search.png'
WHERE NOT EXISTS (
  SELECT 1 FROM `Menu` WHERE `itemName` = 'Orange Juice'
);

INSERT INTO `Customer` (`name`, `phoneNumber`, `paymentMethod`, `isDiningIn`)
SELECT 'Ahmad Salem', '0599000001', 'Cash', 1
WHERE NOT EXISTS (
  SELECT 1 FROM `Customer` WHERE `name` = 'Ahmad Salem' AND `phoneNumber` = '0599000001'
);

INSERT INTO `Customer` (`name`, `phoneNumber`, `paymentMethod`, `isDiningIn`)
SELECT 'Lina Nasser', '0599000002', 'Credit Card', 0
WHERE NOT EXISTS (
  SELECT 1 FROM `Customer` WHERE `name` = 'Lina Nasser' AND `phoneNumber` = '0599000002'
);

INSERT INTO `Customer` (`name`, `phoneNumber`, `paymentMethod`, `isDiningIn`)
SELECT 'Omar Khaled', '0599000003', 'Debit Card', 1
WHERE NOT EXISTS (
  SELECT 1 FROM `Customer` WHERE `name` = 'Omar Khaled' AND `phoneNumber` = '0599000003'
);

INSERT INTO `Orders` (`customerId`, `menuItemId`, `branchId`, `quantity`, `orderDate`)
SELECT
  (SELECT `customerId` FROM `Customer` WHERE `name` = 'Ahmad Salem' AND `phoneNumber` = '0599000001' LIMIT 1),
  (SELECT `menuItemId` FROM `Menu` WHERE `itemName` = 'Classic Burger' LIMIT 1),
  (SELECT `BranchID` FROM `Branches` WHERE `BranchName` = 'Main Branch' LIMIT 1),
  2,
  '2026-05-15 12:30:00'
WHERE NOT EXISTS (
  SELECT 1
  FROM `Orders`
  WHERE `customerId` = (SELECT `customerId` FROM `Customer` WHERE `name` = 'Ahmad Salem' AND `phoneNumber` = '0599000001' LIMIT 1)
    AND `menuItemId` = (SELECT `menuItemId` FROM `Menu` WHERE `itemName` = 'Classic Burger' LIMIT 1)
    AND `branchId` = (SELECT `BranchID` FROM `Branches` WHERE `BranchName` = 'Main Branch' LIMIT 1)
    AND `orderDate` = '2026-05-15 12:30:00'
);

INSERT INTO `Orders` (`customerId`, `menuItemId`, `branchId`, `quantity`, `orderDate`)
SELECT
  (SELECT `customerId` FROM `Customer` WHERE `name` = 'Lina Nasser' AND `phoneNumber` = '0599000002' LIMIT 1),
  (SELECT `menuItemId` FROM `Menu` WHERE `itemName` = 'Caesar Salad' LIMIT 1),
  (SELECT `BranchID` FROM `Branches` WHERE `BranchName` = 'North Branch' LIMIT 1),
  1,
  '2026-05-15 13:00:00'
WHERE NOT EXISTS (
  SELECT 1
  FROM `Orders`
  WHERE `customerId` = (SELECT `customerId` FROM `Customer` WHERE `name` = 'Lina Nasser' AND `phoneNumber` = '0599000002' LIMIT 1)
    AND `menuItemId` = (SELECT `menuItemId` FROM `Menu` WHERE `itemName` = 'Caesar Salad' LIMIT 1)
    AND `branchId` = (SELECT `BranchID` FROM `Branches` WHERE `BranchName` = 'North Branch' LIMIT 1)
    AND `orderDate` = '2026-05-15 13:00:00'
);

INSERT INTO `Orders` (`customerId`, `menuItemId`, `branchId`, `quantity`, `orderDate`)
SELECT
  (SELECT `customerId` FROM `Customer` WHERE `name` = 'Omar Khaled' AND `phoneNumber` = '0599000003' LIMIT 1),
  (SELECT `menuItemId` FROM `Menu` WHERE `itemName` = 'Margherita Pizza' LIMIT 1),
  (SELECT `BranchID` FROM `Branches` WHERE `BranchName` = 'South Branch' LIMIT 1),
  3,
  '2026-05-15 14:15:00'
WHERE NOT EXISTS (
  SELECT 1
  FROM `Orders`
  WHERE `customerId` = (SELECT `customerId` FROM `Customer` WHERE `name` = 'Omar Khaled' AND `phoneNumber` = '0599000003' LIMIT 1)
    AND `menuItemId` = (SELECT `menuItemId` FROM `Menu` WHERE `itemName` = 'Margherita Pizza' LIMIT 1)
    AND `branchId` = (SELECT `BranchID` FROM `Branches` WHERE `BranchName` = 'South Branch' LIMIT 1)
    AND `orderDate` = '2026-05-15 14:15:00'
);

INSERT INTO `MonthlySalary` (`BranchID`, `Month`, `SalaryAmount`, `PaymentDate`, `Notes`)
SELECT
  (SELECT `BranchID` FROM `Branches` WHERE `BranchName` = 'Main Branch' LIMIT 1),
  'May 2026',
  12000.00,
  '2026-05-31',
  'Sample monthly payroll for main branch'
WHERE NOT EXISTS (
  SELECT 1
  FROM `MonthlySalary`
  WHERE `BranchID` = (SELECT `BranchID` FROM `Branches` WHERE `BranchName` = 'Main Branch' LIMIT 1)
    AND `Month` = 'May 2026'
);

INSERT INTO `MonthlySalary` (`BranchID`, `Month`, `SalaryAmount`, `PaymentDate`, `Notes`)
SELECT
  (SELECT `BranchID` FROM `Branches` WHERE `BranchName` = 'North Branch' LIMIT 1),
  'May 2026',
  9800.00,
  '2026-05-31',
  'Sample monthly payroll for north branch'
WHERE NOT EXISTS (
  SELECT 1
  FROM `MonthlySalary`
  WHERE `BranchID` = (SELECT `BranchID` FROM `Branches` WHERE `BranchName` = 'North Branch' LIMIT 1)
    AND `Month` = 'May 2026'
);

INSERT INTO `MonthlySalary` (`BranchID`, `Month`, `SalaryAmount`, `PaymentDate`, `Notes`)
SELECT
  (SELECT `BranchID` FROM `Branches` WHERE `BranchName` = 'South Branch' LIMIT 1),
  'May 2026',
  10500.00,
  '2026-05-31',
  'Sample monthly payroll for south branch'
WHERE NOT EXISTS (
  SELECT 1
  FROM `MonthlySalary`
  WHERE `BranchID` = (SELECT `BranchID` FROM `Branches` WHERE `BranchName` = 'South Branch' LIMIT 1)
    AND `Month` = 'May 2026'
);

INSERT INTO `EmployeeSalaries` (`BranchID`, `MonthlySalary`)
SELECT
  (SELECT `BranchID` FROM `Branches` WHERE `BranchName` = 'Main Branch' LIMIT 1),
  12000.00
WHERE NOT EXISTS (
  SELECT 1 FROM `EmployeeSalaries`
  WHERE `BranchID` = (SELECT `BranchID` FROM `Branches` WHERE `BranchName` = 'Main Branch' LIMIT 1)
    AND `MonthlySalary` = 12000.00
);

INSERT INTO `EmployeeSalaries` (`BranchID`, `MonthlySalary`)
SELECT
  (SELECT `BranchID` FROM `Branches` WHERE `BranchName` = 'North Branch' LIMIT 1),
  9800.00
WHERE NOT EXISTS (
  SELECT 1 FROM `EmployeeSalaries`
  WHERE `BranchID` = (SELECT `BranchID` FROM `Branches` WHERE `BranchName` = 'North Branch' LIMIT 1)
    AND `MonthlySalary` = 9800.00
);

INSERT INTO `EmployeeSalaries` (`BranchID`, `MonthlySalary`)
SELECT
  (SELECT `BranchID` FROM `Branches` WHERE `BranchName` = 'South Branch' LIMIT 1),
  10500.00
WHERE NOT EXISTS (
  SELECT 1 FROM `EmployeeSalaries`
  WHERE `BranchID` = (SELECT `BranchID` FROM `Branches` WHERE `BranchName` = 'South Branch' LIMIT 1)
    AND `MonthlySalary` = 10500.00
);
