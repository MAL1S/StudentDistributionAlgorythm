package domain.data

import com.grapecity.documents.excel.Workbook
import data.model.Participation
import data.model.Project
import data.model.Student
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook as ApacheWorkbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object ExportDataToExcel {

    fun writeProjectsWithStudents(
        students: List<Student>,
        notApplied: List<Student>,
        projects: List<Project>,
        participations: List<Participation>,
        institute: String,
        isUniformly: Boolean = false,
        filePath: String = if (isUniformly) "F:/yarmarka_data/output/равномерно_$institute.xlsx"
        else "F:/yarmarka_data/output/$institute.xlsx"
    ) {
        val workBook = Workbook()
        //workBook.worksheets.add()
        var statIndex = 1
        val workSheetStats = workBook.worksheets.get(0)
        workSheetStats.name = "Статистика по проектам"
        workSheetStats.getRange("A$statIndex:F$statIndex").value =
            arrayOf("Название проекта", "ID проекта", "Укпомплектованность", "Процент молчунов")

        statIndex++

        for (project in projects) {
            val partsCount = participations.count { it.projectId == project.id && it.stateId == 1 }
            //println(project)
            //println("$partsCount ${participations.count { it.projectId == project.id && it.priority == 5 }}")
            workSheetStats.getRange("A$statIndex:F$statIndex").value =
                arrayOf(project.title,
                    project.id,
                    partsCount,
                    if (partsCount == 0) "0%" else "${(participations.count { it.projectId == project.id && it.priority == 5 } / partsCount.toDouble() * 100).toInt()}%")
            statIndex++
        }

        workBook.worksheets.add()
        var studIndex = 1
        val workSheetStud = workBook.worksheets.get(1)
        workSheetStud.name = "Не зачисленные студенты"
        workSheetStud.getRange("A$studIndex:C$studIndex").value =
            arrayOf("ФИО", "Группа", "Номер зачетной книжки")
        studIndex++
        for (student in notApplied) {
            workSheetStud.getRange("A$studIndex:C$studIndex").value = arrayOf(
                student.fio,
                student.realGroup,
                student.id
            )
            studIndex++
        }

        var index = 2
        for (project in projects) {
            workBook.worksheets.add()
            val workSheet = workBook.worksheets.get(index)
            //println(project.title)
            val tempName = project.title
                .replace("?", "")
                .replace(":", "")
                .replace("/", " ")
            workSheet.name = "\"" + tempName
                .substring(0, if (tempName.length < 27) tempName.length else 27) + "\"${index - 1}"
//            try {
//                workSheet.name = project.title
//                    .replace("?", "")
//                    .replace(":", "")
//                    .replace("/", " ")
//            } catch (e: Exception) {
//                workSheet.name = (project.title + "1")
//                    .replace("?", "")
//                    .replace(":", "")
//                    .replace("/", " ")
//            }

            val projectParticipations = participations.filter { it.projectId == project.id && it.stateId == 1 }
            var participationIndexExcel = 1
            workSheet.getRange("A$participationIndexExcel:D$participationIndexExcel").value =
                arrayOf("Название", "Заказчик", "Руководители", "Группы")
            participationIndexExcel++

            workSheet.getRange("A$participationIndexExcel:D$participationIndexExcel").value =
                arrayOf(
                    project.title,
                    project.customer,
                    project.supervisors.toString().replace("[", "").replace("]", ""),
                    project.groups.toString().replace("[", "").replace("]", "")
                )
            participationIndexExcel++

            workSheet.getRange("A$participationIndexExcel:E$participationIndexExcel").value =
                arrayOf("ФИО", "Группа", "Номер зачетной книжки", "Номер приоритета", "Активность")
            participationIndexExcel++

            //println(projectParticipations)
            for (p in projectParticipations.sortedBy { it.priority }) {
                val student = students.find { it.id == p.studentId }

                //println("${p.studentId} $student")
                workSheet.getRange("A$participationIndexExcel:F$participationIndexExcel").value = arrayOf(
                    student?.fio,
                    student?.realGroup,
                    student?.id,
                    p.priority,
                    if (p.priority == 5) "Молчун" else if (p.priority == 4) "Не попал на свои проекты по заявкам" else "Активный"
                )
                participationIndexExcel++
            }
            index++
        }
        workBook.save(filePath)
        deleteShit(filePath, index, institute, isUniformly)
    }

    private fun deleteShit(filePath: String, lastSheetNumber: Int, institute: String, isUniformly: Boolean) {
        //println(filePath)
        //println(lastSheetNumber)
        val inputStream = FileInputStream(filePath)
        val book = WorkbookFactory.create(inputStream)
        //book.removeSheetAt(lastSheetNumber)
        book.removeSheetAt(lastSheetNumber)
        val f = if (isUniformly) File("F:/yarmarka_data/output/output/равномерно_$institute.xlsx")
        else File("F:/yarmarka_data/output/output/$institute.xlsx")
        f.createNewFile()
        val outputStream = FileOutputStream(f)
        book.write(outputStream)
        outputStream.close()
    }

    fun writeFreeStudents(
        students: List<Student>,
        institute: String,
        filePath: String = "F:/yarmarka_data/output/лишние_$institute.xlsx"
    ) {
//        val workBook = Workbook()
//        val workSheet = workBook.worksheets.get(0)
//        workSheet.name = "Не зачисленные студенты"
//        workSheet.getRange("A1:E1").value =
//            arrayOf("ФИО", "Группа", "Номер зачетной книжки")
//        var index = 1
//        for (i in students) {
//            workSheet.getRange("A$index:C$index").value = arrayOf(
//                i.fio,
//                i.realGroup,
//                i.id
//            )
//            index++
//        }
//        workBook.save(filePath)
    }
}