SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';



CREATE SCHEMA IF NOT EXISTS `jobseekerdb` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;

USE `jobseekerdb` ;



-- -----------------------------------------------------

-- Table `jobseekerdb`.`SocialJobs`

-- -----------------------------------------------------

CREATE  TABLE IF NOT EXISTS `jobseekerdb`.`socialjobs` (

  `id` INT NOT NULL AUTO_INCREMENT ,

  `description` TEXT NOT NULL ,

  `url` VARCHAR(1000) NULL ,

  `source` VARCHAR(45) NOT NULL ,

  `timestamp` TIMESTAMP NOT NULL ,

  `city` VARCHAR(45) NULL ,

  PRIMARY KEY (`id`) ,

  INDEX `idx_timestamp` (`timestamp` ASC) ) 

ENGINE = InnoDB;





-- -----------------------------------------------------

-- Table `jobseekerdb`.`Jobs`

-- -----------------------------------------------------

CREATE  TABLE IF NOT EXISTS `jobseekerdb`.`jobs` (

  `id` INT NOT NULL AUTO_INCREMENT ,

  `title` VARCHAR(100) NULL ,

  `employer` VARCHAR(100) NULL ,

  `description` TEXT NOT NULL ,

  `source` VARCHAR(45) NULL ,

  `timestamp` TIMESTAMP NOT NULL ,

  `url` VARCHAR(1000) NULL ,

  `latitude` DOUBLE NULL ,

  `longitude` DOUBLE NULL ,

  `closing_date` DATE NULL ,

  `hours` VARCHAR(45) NULL ,

  `industry` VARCHAR(45) NULL ,

  `type` VARCHAR(45) NULL ,

  `city` VARCHAR(45) NULL ,

  PRIMARY KEY (`id`) ,

  INDEX `idx_timestamp` (`timestamp` ASC) ) 

ENGINE = InnoDB;

ALTER TABLE jobseekerdb.socialjobs ADD UNIQUE `url_UNIQUE` (url(100));
ALTER TABLE jobseekerdb.jobs ADD UNIQUE `url_UNIQUE` (url(100));

USE `JobSeekerDB` ;



SET SQL_MODE=@OLD_SQL_MODE;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;

SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

