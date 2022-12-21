SELECT name.new_student, age.new_student, name.faculty
FROM (SELECT name.student, age.student, faculty_id.student student_id.avatar
      FROM student
      JOIN avatar ON student_id.avatar = id.student
      WHERE student_id IS NOT NULL) AS new_student
JOIN faculty ON faculty_id.new_student = id.faculty

