-- liquibase formatted sql

-- changeSet igatin:1
CREATE INDEX student_name_index ON student (name);

-- changeSet igatin:2
CREATE INDEX faculty_cn_index ON faculty (color, name);