package com.example.data.repository

import com.example.data.dao.TuitionDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class TuitionRepository(private val tuitionDao: TuitionDao) {

    // Users
    val allUsers: Flow<List<User>> = tuitionDao.getAllUsers()
    fun getUsersByRole(role: String): Flow<List<User>> = tuitionDao.getUsersByRole(role)
    suspend fun getUserById(id: String): User? = tuitionDao.getUserById(id)
    suspend fun insertUser(user: User) = tuitionDao.insertUser(user)
    suspend fun deleteUser(user: User) = tuitionDao.deleteUser(user)
    suspend fun deleteUserById(userId: String) = tuitionDao.deleteUserById(userId)

    // Courses
    val allCourses: Flow<List<Course>> = tuitionDao.getAllCourses()
    suspend fun insertCourse(course: Course) = tuitionDao.insertCourse(course)
    suspend fun deleteCourse(course: Course) = tuitionDao.deleteCourse(course)
    suspend fun deleteCourseById(courseId: Long) = tuitionDao.deleteCourseById(courseId)

    // Attendance
    val allAttendance: Flow<List<Attendance>> = tuitionDao.getAllAttendance()
    fun getAttendanceForStudent(studentId: String): Flow<List<Attendance>> = tuitionDao.getAttendanceForStudent(studentId)
    suspend fun insertAttendance(attendance: Attendance) = tuitionDao.insertAttendance(attendance)

    // Payments
    val allFeePayments: Flow<List<FeePayment>> = tuitionDao.getAllFeePayments()
    fun getFeePaymentsForStudent(studentId: String): Flow<List<FeePayment>> = tuitionDao.getFeePaymentsForStudent(studentId)
    suspend fun insertFeePayment(feePayment: FeePayment) = tuitionDao.insertFeePayment(feePayment)
    suspend fun deleteFeePayment(feePayment: FeePayment) = tuitionDao.deleteFeePayment(feePayment)

    // Notices
    val allNotices: Flow<List<Notice>> = tuitionDao.getAllNotices()
    suspend fun insertNotice(notice: Notice) = tuitionDao.insertNotice(notice)
    suspend fun deleteNotice(notice: Notice) = tuitionDao.deleteNotice(notice)

    // Materials
    val allStudyMaterials: Flow<List<StudyMaterial>> = tuitionDao.getAllStudyMaterials()
    suspend fun insertStudyMaterial(material: StudyMaterial) = tuitionDao.insertStudyMaterial(material)
    suspend fun deleteStudyMaterial(material: StudyMaterial) = tuitionDao.deleteStudyMaterial(material)

    // Homework
    val allHomeworks: Flow<List<Homework>> = tuitionDao.getAllHomeworks()
    suspend fun insertHomework(homework: Homework) = tuitionDao.insertHomework(homework)
    suspend fun deleteHomework(homework: Homework) = tuitionDao.deleteHomework(homework)
}
