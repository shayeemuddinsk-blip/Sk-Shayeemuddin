package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.TuitionDao
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        User::class,
        Course::class,
        Attendance::class,
        FeePayment::class,
        Notice::class,
        StudyMaterial::class,
        Homework::class
    ],
    version = 1,
    exportSchema = false
)
abstract class TuitionDatabase : RoomDatabase() {

    abstract fun tuitionDao(): TuitionDao

    companion object {
        @Volatile
        private var INSTANCE: TuitionDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): TuitionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TuitionDatabase::class.java,
                    "tuition_manager_database"
                )
                .addCallback(TuitionDatabaseCallback(scope))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class TuitionDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.tuitionDao())
                }
            }
        }

        suspend fun populateDatabase(dao: TuitionDao) {
            // 1. Insert Core Users
            val adminUser = User(
                id = "admin@smart.com",
                name = "Admin Principal",
                email = "admin@smart.com",
                role = "ADMIN",
                profileImage = "0",
                phone = "+1 (555) 019-2831",
                joinDate = "2026-01-10"
            )
            val teacher1 = User(
                id = "teacher1@smart.com",
                name = "Mr. Kevin Smith",
                email = "teacher1@smart.com",
                role = "TEACHER",
                profileImage = "1",
                phone = "+1 (555) 012-3214",
                subjects = "Mathematics, Physics",
                joinDate = "2026-02-15"
            )
            val teacher2 = User(
                id = "teacher2@smart.com",
                name = "Miss Sarah Adams",
                email = "teacher2@smart.com",
                role = "TEACHER",
                profileImage = "2",
                phone = "+1 (555) 013-8877",
                subjects = "Chemistry, Biology",
                joinDate = "2026-03-01"
            )
            val student1 = User(
                id = "student1@smart.com",
                name = "Alex Johnson",
                email = "student1@smart.com",
                role = "STUDENT",
                profileImage = "3",
                phone = "+1 (555) 014-9988",
                grade = "Grade 10",
                joinDate = "2026-04-01"
            )
            val student2 = User(
                id = "student2@smart.com",
                name = "Emma Watson",
                email = "student2@smart.com",
                role = "STUDENT",
                profileImage = "4",
                phone = "+1 (555) 015-7766",
                grade = "Grade 11",
                joinDate = "2026-04-10"
            )
            val student3 = User(
                id = "student3@smart.com",
                name = "Justin Bieber",
                email = "student3@smart.com",
                role = "STUDENT",
                profileImage = "5",
                phone = "+1 (555) 016-5544",
                grade = "Grade 12",
                joinDate = "2026-05-01"
            )

            dao.insertUser(adminUser)
            dao.insertUser(teacher1)
            dao.insertUser(teacher2)
            dao.insertUser(student1)
            dao.insertUser(student2)
            dao.insertUser(student3)

            // 2. Insert Courses
            val course1 = Course(
                id = 1,
                name = "Calculus Masterclass",
                subject = "Mathematics",
                teacherId = "teacher1@smart.com",
                schedule = "Mon, Wed 4:00 PM - 5:30 PM",
                monthlyFee = 150.0,
                roomNo = "Lab 1"
            )
            val course2 = Course(
                id = 2,
                name = "Intro to Mechanics",
                subject = "Physics",
                teacherId = "teacher1@smart.com",
                schedule = "Tue, Thu 3:00 PM - 4:30 PM",
                monthlyFee = 120.0,
                roomNo = "Room 302"
            )
            val course3 = Course(
                id = 3,
                name = "Organic Chemistry",
                subject = "Chemistry",
                teacherId = "teacher2@smart.com",
                schedule = "Fri 2:00 PM - 5:00 PM",
                monthlyFee = 180.0,
                roomNo = "Chemistry Lab"
            )

            dao.insertCourse(course1)
            dao.insertCourse(course2)
            dao.insertCourse(course3)

            // 3. Insert Attendance
            dao.insertAttendance(Attendance(studentId = "student1@smart.com", courseId = 1, date = "2026-05-25", status = "Present", studentName = "Alex Johnson"))
            dao.insertAttendance(Attendance(studentId = "student1@smart.com", courseId = 2, date = "2026-05-26", status = "Present", studentName = "Alex Johnson"))
            dao.insertAttendance(Attendance(studentId = "student2@smart.com", courseId = 1, date = "2026-05-25", status = "Late", studentName = "Emma Watson"))
            dao.insertAttendance(Attendance(studentId = "student2@smart.com", courseId = 3, date = "2026-05-28", status = "Absent", studentName = "Emma Watson"))

            // 4. Insert Fee Payments
            dao.insertFeePayment(FeePayment(studentId = "student1@smart.com", studentName = "Alex Johnson", courseName = "Calculus Masterclass", amountPaid = 150.0, amountDue = 0.0, monthName = "May 2026", status = "PAID", paymentDate = "2026-05-02"))
            dao.insertFeePayment(FeePayment(studentId = "student1@smart.com", studentName = "Alex Johnson", courseName = "Intro to Mechanics", amountPaid = 120.0, amountDue = 0.0, monthName = "May 2026", status = "PAID", paymentDate = "2026-05-03"))
            dao.insertFeePayment(FeePayment(studentId = "student2@smart.com", studentName = "Emma Watson", courseName = "Calculus Masterclass", amountPaid = 150.0, amountDue = 0.0, monthName = "May 2026", status = "PAID", paymentDate = "2026-05-05"))
            dao.insertFeePayment(FeePayment(studentId = "student2@smart.com", studentName = "Emma Watson", courseName = "Organic Chemistry", amountPaid = 0.0, amountDue = 180.0, monthName = "May 2026", status = "DUE"))
            dao.insertFeePayment(FeePayment(studentId = "student3@smart.com", studentName = "Justin Bieber", courseName = "Calculus Masterclass", amountPaid = 0.0, amountDue = 150.0, monthName = "May 2026", status = "OVERDUE"))

            // 5. Insert Notices
            dao.insertNotice(Notice(title = "Monthly Mock Exam Scheduling", content = "The mathematics mock examination for calculus is scheduled for June 5th, 2026. All calculus stream students must report at 9:00 AM sharp with active graphing tools.", date = "2026-05-28", targetRole = "STUDENT", senderName = "Admin Principal"))
            dao.insertNotice(Notice(title = "Syllabus Progress Report Call", content = "All stream teachers are requested to submit their lesson plans and current progress reports for the month of May by Friday afternoon.", date = "2026-05-27", targetRole = "TEACHER", senderName = "Admin Principal"))
            dao.insertNotice(Notice(title = "Summer Tuition Holiday Notice", content = "Tuition classes will remain suspended on June 15th-18th due to national summer break. Normal routines resume June 19th.", date = "2026-05-25", targetRole = "ALL", senderName = "Admin Principal"))

            // 6. Insert Study Materials
            dao.insertStudyMaterial(StudyMaterial(title = "Calculus Derivatives & Integrals Sheet", description = "Contains complete formulas and quick reference steps for complex trigonometry differentials.", courseName = "Calculus Masterclass", fileType = "PDF", uploadDate = "2026-05-20", teacherName = "Mr. Kevin Smith"))
            dao.insertStudyMaterial(StudyMaterial(title = "Kinematics Vector Handout", description = "Overview of 2D and 3D projectile calculations for upcoming exam.", courseName = "Intro to Mechanics", fileType = "PDF", uploadDate = "2026-05-22", teacherName = "Mr. Kevin Smith"))

            // 7. Insert Homework
            dao.insertHomework(Homework(title = "Assignment on Organic Compounds", description = "Prepare structure drawings of alkanes, alkenes and study reaction tables on page 98.", courseName = "Organic Chemistry", dueDate = "2026-06-02", teacherName = "Miss Sarah Adams", feedback = "To be submitted by email or physical paper."))
        }
    }
}
