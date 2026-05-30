package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String, // email or generated uuid
    val name: String,
    val email: String,
    val role: String, // "ADMIN", "TEACHER", "STUDENT"
    val profileImage: String = "", // base64 or placeholder index
    val phone: String = "",
    val grade: String = "", // for student: "Grade 10", "Grade 12", etc.
    val subjects: String = "", // for teacher: comma-separated list of subjects
    val joinDate: String = "2026-05-01"
) : Serializable

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val subject: String,
    val teacherId: String, // User id
    val schedule: String, // "Mon 4-6 PM", etc.
    val monthlyFee: Double,
    val roomNo: String = "Room A"
) : Serializable

@Entity(tableName = "attendance")
data class Attendance(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: String,
    val courseId: Long,
    val date: String, // "YYYY-MM-DD"
    val status: String, // "Present", "Absent", "Late"
    val studentName: String = ""
) : Serializable

@Entity(tableName = "fee_payments")
data class FeePayment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: String,
    val studentName: String,
    val courseName: String,
    val amountPaid: Double,
    val amountDue: Double,
    val monthName: String, // "May 2026", "June 2026"
    val status: String, // "PAID", "DUE", "OVERDUE"
    val paymentDate: String = "", // "YYYY-MM-DD" if paid
    val receiptPdfPath: String = ""
) : Serializable

@Entity(tableName = "notices")
data class Notice(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val date: String, // "YYYY-MM-DD"
    val targetRole: String, // "ALL", "TEACHER", "STUDENT"
    val senderName: String
) : Serializable

@Entity(tableName = "study_materials")
data class StudyMaterial(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val courseName: String,
    val fileType: String, // "PDF", "DOC", "IMAGE"
    val uploadDate: String, // "YYYY-MM-DD"
    val teacherName: String
) : Serializable

@Entity(tableName = "homework")
data class Homework(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val courseName: String,
    val dueDate: String, // "YYYY-MM-DD"
    val teacherName: String,
    val feedback: String = "" // Progress feedback for student
) : Serializable
