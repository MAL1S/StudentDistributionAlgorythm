package domain.data

import com.grapecity.documents.excel.Workbook
import data.model.Participation
import data.model.Project
import data.model.Student

object ExportDataToExcel {

    fun writeProjectsWithStudents(
        students: List<Student>,
        projects: List<Project>,
        participations: List<Participation>,
        filePath: String
    ) {
        val workBook = Workbook()
        for ((index, project) in projects.withIndex()) {
            val workSheet = workBook.worksheets.get(index)
            workSheet.name = project.title

            val projectParticipations = participations.filter { it.projectId == project.id && it.stateId == 1 }
            var participationIndexExcel = 1
            workSheet.getRange("A$participationIndexExcel:F$participationIndexExcel").value =
                arrayOf("Название", "Заказчик", "Руководители", "Сложность", "Группы")
            participationIndexExcel++

            workSheet.getRange("A$participationIndexExcel:F$participationIndexExcel").value =
                arrayOf(
                    project.title,
                    project.customer,
                    project.supervisors.toString().replace("[", "").replace("]", ""),
                    project.difficulty,
                    project.groups.toString().replace("[", "").replace("]", "")
                )
            participationIndexExcel++

            workSheet.getRange("A$participationIndexExcel:E$participationIndexExcel").value =
                arrayOf("ФИО", "Группа", "Номер зачетной книжки", "Номер приоритета", "Активность")
            participationIndexExcel++

            for (p in projectParticipations) {
                val student = students.find { it.id == p.studentId }

                workSheet.getRange("A$participationIndexExcel:F$participationIndexExcel").value = arrayOf(
                    student?.fio,
                    student?.realGroup,
                    student?.id,
                    p.priority,
                    if (p.stateId == 0) "Молчун" else if (p.stateId == 1) "Активный" else "Активный, но на его проекте было мало участников"
                )
                participationIndexExcel++
            }
        }
        workBook.save(filePath)
    }

    fun writeFreeStudents(students: List<Student>, filePath: String) {
        val workBook = Workbook()
        val workSheet = workBook.worksheets.get(0)
        workSheet.name = "Не зачисленные студенты"
        workSheet.getRange("A1:E1").value =
            arrayOf("ФИО", "Группа", "Номер зачетной книжки")
        for ((index, i) in students.withIndex()) {
            workSheet.getRange("A$index:C$index").value = arrayOf(
                i.fio,
                i.realGroup,
                i.id
            )
        }
        workBook.save(filePath)
    }
}