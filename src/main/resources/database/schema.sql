CREATE DATABASE IF NOT EXISTS enrollment_system;

USE enrollment_system;


-- =========================================================
-- PROGRAMS
-- =========================================================

CREATE TABLE programs (
                          id VARCHAR(36) PRIMARY KEY,

                          code VARCHAR(20) NOT NULL UNIQUE,
                          name VARCHAR(150) NOT NULL,

                          description TEXT,

                          duration_years INT NOT NULL,

                          department VARCHAR(150),
                          college VARCHAR(150),

                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                              ON UPDATE CURRENT_TIMESTAMP
);





-- =========================================================
-- ROLES
-- =========================================================

CREATE TABLE roles (
                       id VARCHAR(36) PRIMARY KEY,
                       name VARCHAR(50) NOT NULL UNIQUE,
                       description VARCHAR(255)
);


-- =========================================================
-- STUDENTS
-- =========================================================

CREATE TABLE students (
                          id VARCHAR(20) PRIMARY KEY,

                          first_name VARCHAR(50) NOT NULL,
                          middle_name VARCHAR(50),
                          last_name VARCHAR(50) NOT NULL,
                          suffix VARCHAR(10),

                          birth_date DATE NOT NULL,

                          sex VARCHAR(20) NOT NULL,

                          email VARCHAR(150) UNIQUE,
                          phone VARCHAR(30),
                          address VARCHAR(255),

                          program_id VARCHAR(36) NOT NULL,

                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                              ON UPDATE CURRENT_TIMESTAMP,

                          CONSTRAINT fk_students_program
                              FOREIGN KEY (program_id)
                                  REFERENCES programs(id)
);


-- =========================================================
-- USERS
-- =========================================================

CREATE TABLE users (
                       id VARCHAR(36) PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,

                       student_id VARCHAR(20),

                       active BOOLEAN NOT NULL DEFAULT TRUE,

                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                           ON UPDATE CURRENT_TIMESTAMP,

                       CONSTRAINT fk_users_student
                           FOREIGN KEY (student_id)
                               REFERENCES students(id)
);


-- =========================================================
-- USER ROLES
-- =========================================================

CREATE TABLE user_roles (
                            user_id VARCHAR(36) NOT NULL,
                            role_id VARCHAR(36) NOT NULL,

                            PRIMARY KEY (user_id, role_id),

                            CONSTRAINT fk_user_roles_user
                                FOREIGN KEY (user_id)
                                    REFERENCES users(id)
                                    ON DELETE CASCADE,

                            CONSTRAINT fk_user_roles_role
                                FOREIGN KEY (role_id)
                                    REFERENCES roles(id)
                                    ON DELETE CASCADE
);


-- =========================================================
-- ACADEMIC TERMS
-- =========================================================

CREATE TABLE academic_terms (
                                id VARCHAR(36) PRIMARY KEY,

                                school_year VARCHAR(9) NOT NULL,
                                semester VARCHAR(20) NOT NULL,

                                start_date DATE,
                                end_date DATE,

                                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                                    ON UPDATE CURRENT_TIMESTAMP,

                                CONSTRAINT uq_academic_term
                                    UNIQUE (school_year, semester)
);


-- =========================================================
-- COURSES
-- =========================================================

CREATE TABLE courses (
                         id VARCHAR(36) PRIMARY KEY,

                         code VARCHAR(30) NOT NULL UNIQUE,
                         name VARCHAR(150) NOT NULL,

                         description TEXT,

                         units DECIMAL(3,1) NOT NULL,

                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                             ON UPDATE CURRENT_TIMESTAMP
);


-- =========================================================
-- COURSE SCHEDULES
-- =========================================================

CREATE TABLE course_schedules (
                                  id VARCHAR(36) PRIMARY KEY,

                                  course_id VARCHAR(36) NOT NULL,
                                  term_id VARCHAR(36) NOT NULL,

                                  section VARCHAR(30),

                                  day VARCHAR(20) NOT NULL,

                                  start_time TIME NOT NULL,
                                  end_time TIME NOT NULL,

                                  room VARCHAR(50),

                                  capacity INT NOT NULL DEFAULT 30,

                                  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                                      ON UPDATE CURRENT_TIMESTAMP,

                                  CONSTRAINT fk_course_schedule_course
                                      FOREIGN KEY (course_id)
                                          REFERENCES courses(id),

                                  CONSTRAINT fk_course_schedule_term
                                      FOREIGN KEY (term_id)
                                          REFERENCES academic_terms(id)
);


-- =========================================================
-- ENROLLMENTS
-- =========================================================

CREATE TABLE enrollments (
                             id VARCHAR(36) PRIMARY KEY,

                             student_id VARCHAR(20) NOT NULL,
                             term_id VARCHAR(36) NOT NULL,

                             enrollment_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                             status VARCHAR(30) NOT NULL DEFAULT 'PENDING',

                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                                 ON UPDATE CURRENT_TIMESTAMP,

                             CONSTRAINT uq_student_term
                                 UNIQUE (student_id, term_id),

                             CONSTRAINT fk_enrollments_student
                                 FOREIGN KEY (student_id)
                                     REFERENCES students(id),

                             CONSTRAINT fk_enrollments_term
                                 FOREIGN KEY (term_id)
                                     REFERENCES academic_terms(id)
);


-- =========================================================
-- ENROLLMENT COURSES
-- =========================================================

CREATE TABLE enrollment_courses (
                                    enrollment_id VARCHAR(36) NOT NULL,
                                    course_schedule_id VARCHAR(36) NOT NULL,

                                    status VARCHAR(30) NOT NULL DEFAULT 'ENROLLED',

                                    final_grade DECIMAL(5,2),

                                    PRIMARY KEY (enrollment_id, course_schedule_id),

                                    CONSTRAINT fk_enrollment_courses_enrollment
                                        FOREIGN KEY (enrollment_id)
                                            REFERENCES enrollments(id)
                                            ON DELETE CASCADE,

                                    CONSTRAINT fk_enrollment_courses_schedule
                                        FOREIGN KEY (course_schedule_id)
                                            REFERENCES course_schedules(id)
);


-- =========================================================
-- ASSESSMENTS
-- =========================================================

CREATE TABLE assessments (
                             id VARCHAR(36) PRIMARY KEY,

                             enrollment_id VARCHAR(36) NOT NULL UNIQUE,

                             tuition_fee DECIMAL(12,2) NOT NULL DEFAULT 0,
                             miscellaneous_fee DECIMAL(12,2) NOT NULL DEFAULT 0,
                             laboratory_fee DECIMAL(12,2) NOT NULL DEFAULT 0,
                             other_fee DECIMAL(12,2) NOT NULL DEFAULT 0,

                             total_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
                             amount_paid DECIMAL(12,2) NOT NULL DEFAULT 0,
                             balance DECIMAL(12,2) NOT NULL DEFAULT 0,

                             status VARCHAR(30) NOT NULL DEFAULT 'UNPAID',

                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                                 ON UPDATE CURRENT_TIMESTAMP,

                             CONSTRAINT fk_assessments_enrollment
                                 FOREIGN KEY (enrollment_id)
                                     REFERENCES enrollments(id)
);


-- =========================================================
-- PAYMENTS
-- =========================================================

CREATE TABLE payments (
                          id VARCHAR(36) PRIMARY KEY,

                          assessment_id VARCHAR(36) NOT NULL,

                          cashier_id VARCHAR(36) NOT NULL,

                          amount DECIMAL(12,2) NOT NULL,

                          payment_method VARCHAR(30) NOT NULL,

                          reference_number VARCHAR(100),

                          paid_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                          status VARCHAR(30) NOT NULL DEFAULT 'COMPLETED',

                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                          CONSTRAINT fk_payments_assessment
                              FOREIGN KEY (assessment_id)
                                  REFERENCES assessments(id),

                          CONSTRAINT fk_payments_cashier
                              FOREIGN KEY (cashier_id)
                                  REFERENCES users(id)
);