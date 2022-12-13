SELECT *
FROM student;

SELECT *
FROM student
WHERE age BETWEEN 10 AND 20;

SELECT name
FROM student;

SELECT *
FROM student
WHERE lower(name) LIKE '%s%';

SELECT *
FROM student
WHERE age < 20;

SELECT *
FROM student
ORDER BY age