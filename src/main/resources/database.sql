-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema hyperativa
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `hyperativa` ;

-- -----------------------------------------------------
-- Schema hyperativa
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `hyperativa` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `hyperativa` ;

-- -----------------------------------------------------
-- Table `hyperativa`.`client`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `hyperativa`.`client` ;

CREATE TABLE IF NOT EXISTS `hyperativa`.`client` (
                                                     `id` INT NOT NULL AUTO_INCREMENT,
                                                     `name` VARCHAR(45) NOT NULL,
    `date` DATETIME NOT NULL,
    PRIMARY KEY (`id`))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `hyperativa`.`import`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `hyperativa`.`import` ;

CREATE TABLE IF NOT EXISTS `hyperativa`.`import` (
                                                     `id` INT NOT NULL AUTO_INCREMENT,
                                                     `file_name` VARCHAR(200) NOT NULL,
    `chunk` VARCHAR(8) NOT NULL,
    `date` DATETIME NOT NULL,
    `file_date` DATE NOT NULL,
    `client_id` INT NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `fk_import_client1_idx` (`client_id` ASC) VISIBLE,
    CONSTRAINT `fk_import_client1`
    FOREIGN KEY (`client_id`)
    REFERENCES `hyperativa`.`client` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `hyperativa`.`card`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `hyperativa`.`card` ;

CREATE TABLE IF NOT EXISTS `hyperativa`.`card` (
                                                   `id` BIGINT NOT NULL AUTO_INCREMENT,
                                                   `card_number` VARCHAR(255) NOT NULL,
    `client_id` INT NOT NULL,
    `date` DATETIME NOT NULL,
    `import_id` INT NULL,
    PRIMARY KEY (`id`),
    INDEX `fk_cards_client_idx` (`client_id` ASC) VISIBLE,
    INDEX `fk_card_import1_idx` (`import_id` ASC) VISIBLE,
    CONSTRAINT `fk_cards_client`
    FOREIGN KEY (`client_id`)
    REFERENCES `hyperativa`.`client` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT `fk_card_import1`
    FOREIGN KEY (`import_id`)
    REFERENCES `hyperativa`.`import` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
