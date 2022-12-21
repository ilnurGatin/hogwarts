SELECT name.student, age.student, name.faculty
FROM (SELECT name.student, age.student, student_id.avatar
      FROM student
      JOIN avatar ON student_id.avatar = id.student
      WHERE student_id IS NOT NULL)
JOIN faculty ON faculty_id.student = id.faculty

