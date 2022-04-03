package data.generation

import STUDENT_COUNT
import data.model.Student

object GenerateStudents {

    var emptyCount = 0

    fun generateStudents(): List<Student> {
        val students = mutableListOf<Student>()

        for (i in 0 until STUDENT_COUNT) {
            val student = Student(
                id = i,
                fio = GenerateNames.generateName(),
                skills = GenerateSkills.generateSkills(),
                training_group = GenerateSkills.generateGroup()
            )
            if (student.skills.isEmpty()) emptyCount++
            students.add(student)
        }

        return students
    }
}