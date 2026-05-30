package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.database.TuitionDatabase
import com.example.data.model.*
import com.example.data.repository.TuitionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TuitionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TuitionRepository

    init {
        val database = TuitionDatabase.getDatabase(application, viewModelScope)
        repository = TuitionRepository(database.tuitionDao())
    }

    // --- Core Database Flows ---
    val allUsers: StateFlow<List<User>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCourses: StateFlow<List<Course>> = repository.allCourses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAttendance: StateFlow<List<Attendance>> = repository.allAttendance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFeePayments: StateFlow<List<FeePayment>> = repository.allFeePayments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotices: StateFlow<List<Notice>> = repository.allNotices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allStudyMaterials: StateFlow<List<StudyMaterial>> = repository.allStudyMaterials
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allHomeworks: StateFlow<List<Homework>> = repository.allHomeworks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    // --- UI/Session Application State ---
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Preferences Settings
    private val _isDarkTheme = MutableStateFlow(true) // Dynamic dark-mode state default
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private val _appLanguage = MutableStateFlow("English")
    val appLanguage: StateFlow<String> = _appLanguage.asStateFlow()

    // Temporary Notifications Banner List
    private val _activeToast = MutableStateFlow<String?>(null)
    val activeToast: StateFlow<String?> = _activeToast.asStateFlow()


    // --- Actions & Methods ---

    fun showToast(msg: String) {
        _activeToast.value = msg
    }

    fun clearToast() {
        _activeToast.value = null
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
        showToast("Theme updated to ${if (_isDarkTheme.value) "Dark" else "Light"} Mode")
    }

    fun toggleNotifications() {
        _notificationsEnabled.value = !_notificationsEnabled.value
        showToast("Notifications ${if (_notificationsEnabled.value) "Enabled" else "Disabled"}")
    }

    fun setLanguage(lang: String) {
        _appLanguage.value = lang
        showToast("Language changed to $lang")
    }

    // Authentication Simulations
    fun login(email: String, role: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val formattedEmail = email.trim().lowercase()
            if (formattedEmail.isEmpty()) {
                onResult(false, "Please enter a valid email address.")
                return@launch
            }

            // Attempt to query from local db
            val existingUser = repository.getUserById(formattedEmail)
            if (existingUser != null) {
                if (existingUser.role == role) {
                    _currentUser.value = existingUser
                    showToast("Welcome back, ${existingUser.name}!")
                    onResult(true, "Success")
                } else {
                    onResult(false, "Roles mismatched. User is registered as ${existingUser.role}.")
                }
            } else {
                // Pre-auth registration for simulator convenience
                val fallbackName = when (role) {
                    "ADMIN" -> "Admin Supervisor"
                    "TEACHER" -> "Faculty Member"
                    else -> "Student Enrolled"
                }
                val newUser = User(
                    id = formattedEmail,
                    name = fallbackName,
                    email = formattedEmail,
                    role = role,
                    profileImage = (0..5).random().toString()
                )
                repository.insertUser(newUser)
                _currentUser.value = newUser
                showToast("New $role account created!")
                onResult(true, "Created account")
            }
        }
    }

    fun signUp(name: String, email: String, role: String, gradeOrSubject: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val formattedEmail = email.trim().lowercase()
            if (name.trim().isEmpty() || formattedEmail.isEmpty()) {
                onResult(false, "Name and Email cannot be empty.")
                return@launch
            }

            val existing = repository.getUserById(formattedEmail)
            if (existing != null) {
                onResult(false, "User already exists with this email address.")
                return@launch
            }

            val newUser = User(
                id = formattedEmail,
                name = name.trim(),
                email = formattedEmail,
                role = role,
                grade = if (role == "STUDENT") gradeOrSubject else "",
                subjects = if (role == "TEACHER") gradeOrSubject else "",
                profileImage = (3..5).random().toString()
            )

            repository.insertUser(newUser)
            _currentUser.value = newUser
            showToast("Successfully Registered as $role!")
            onResult(true, "Registration Completed")
        }
    }

    fun logout() {
        val name = _currentUser.value?.name ?: "User"
        _currentUser.value = null
        showToast("Goodbye, $name!")
    }

    fun updateProfilePicture(imageIndex: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val updated = user.copy(profileImage = imageIndex)
            repository.insertUser(updated)
            _currentUser.value = updated
            showToast("Profile avatar updated successfully!")
        }
    }

    fun simulateForgotPassword(email: String) {
        showToast("Password reset parameters dispatched to $email!")
    }

    fun simulateGoogleSignIn(role: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val randomNum = (1000..9999).random()
            val email = "g.user$randomNum@smart.com"
            val name = "Google User $randomNum"
            val newUser = User(
                id = email,
                name = name,
                email = email,
                role = role,
                profileImage = "0"
            )
            repository.insertUser(newUser)
            _currentUser.value = newUser
            showToast("Authenticated safely via Google Account!")
            onResult(true, "Success")
        }
    }


    // --- Admin Commands ---

    fun createStudent(name: String, email: String, grade: String, phone: String) {
        viewModelScope.launch {
            val student = User(
                id = email.trim().lowercase(),
                name = name.trim(),
                email = email.trim().lowercase(),
                role = "STUDENT",
                grade = grade,
                phone = phone,
                profileImage = (3..5).random().toString()
            )
            repository.insertUser(student)
            showToast("Student '$name' added successfully.")
        }
    }

    fun createTeacher(name: String, email: String, subjects: String, phone: String) {
        viewModelScope.launch {
            val teacher = User(
                id = email.trim().lowercase(),
                name = name.trim(),
                email = email.trim().lowercase(),
                role = "TEACHER",
                subjects = subjects,
                phone = phone,
                profileImage = (1..2).random().toString()
            )
            repository.insertUser(teacher)
            showToast("Teacher '$name' added successfully.")
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            repository.deleteUserById(userId)
            showToast("User record removed successfully.")
        }
    }

    fun createCourse(name: String, subject: String, teacherId: String, schedule: String, fee: Double, room: String) {
        viewModelScope.launch {
            val course = Course(
                name = name,
                subject = subject,
                teacherId = teacherId,
                schedule = schedule,
                monthlyFee = fee,
                roomNo = room
            )
            repository.insertCourse(course)
            showToast("Course '$name' opened successfully.")
        }
    }

    fun deleteCourse(course: Course) {
        viewModelScope.launch {
            repository.deleteCourse(course)
            showToast("Course deleted.")
        }
    }

    fun dispatchNotice(title: String, content: String, target: String) {
        viewModelScope.launch {
            val sender = _currentUser.value?.name ?: "Admin Principal"
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val notice = Notice(
                title = title,
                content = content,
                date = today,
                targetRole = target,
                senderName = sender
            )
            repository.insertNotice(notice)
            showToast("Notice dispatched targetting $target.")
        }
    }

    fun deleteNotice(notice: Notice) {
        viewModelScope.launch {
            repository.deleteNotice(notice)
            showToast("Notice removed.")
        }
    }


    // --- Teacher Commands ---

    fun recordAttendance(studentId: String, studentName: String, courseId: Long, status: String, date: String) {
        viewModelScope.launch {
            val logs = Attendance(
                studentId = studentId,
                courseId = courseId,
                date = date,
                status = status,
                studentName = studentName
            )
            repository.insertAttendance(logs)
            showToast("Attendance logged for $studentName: $status")
        }
    }

    fun uploadStudyMaterial(title: String, desc: String, courseName: String, type: String) {
        viewModelScope.launch {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val teacherName = _currentUser.value?.name ?: "Faculty"
            val mat = StudyMaterial(
                title = title,
                description = desc,
                courseName = courseName,
                fileType = type,
                uploadDate = today,
                teacherName = teacherName
            )
            repository.insertStudyMaterial(mat)
            showToast("Material '$title' published to $courseName.")
        }
    }

    fun publishHomework(title: String, desc: String, courseName: String, dueDate: String) {
        viewModelScope.launch {
            val teacherName = _currentUser.value?.name ?: "Faculty"
            val hw = Homework(
                title = title,
                description = desc,
                courseName = courseName,
                dueDate = dueDate,
                teacherName = teacherName
            )
            repository.insertHomework(hw)
            showToast("Homework assigned for $courseName: Due $dueDate.")
        }
    }

    fun deleteHomework(homework: Homework) {
        viewModelScope.launch {
            repository.deleteHomework(homework)
            showToast("Homework card deleted.")
        }
    }

    fun deleteStudyMaterial(material: StudyMaterial) {
        viewModelScope.launch {
            repository.deleteStudyMaterial(material)
            showToast("Study material visual removed.")
        }
    }


    // --- Fee Management Commands ---

    fun appendFeeRecord(studentId: String, studentName: String, courseName: String, amountDue: Double, monthName: String, status: String) {
        viewModelScope.launch {
            val pay = FeePayment(
                studentId = studentId,
                studentName = studentName,
                courseName = courseName,
                amountDue = amountDue,
                amountPaid = if (status == "PAID") amountDue else 0.0,
                monthName = monthName,
                status = status,
                paymentDate = if (status == "PAID") SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) else ""
            )
            repository.insertFeePayment(pay)
            showToast("Fee record generated for $studentName ($monthName).")
        }
    }

    fun recordPayment(paymentId: Long, amountPaid: Double) {
        viewModelScope.launch {
            // Find current payment details from standard list flow first
            val targetPayment = allFeePayments.value.find { it.id == paymentId } ?: return@launch
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val updated = targetPayment.copy(
                status = "PAID",
                amountPaid = amountPaid,
                amountDue = 0.0,
                paymentDate = today
            )
            repository.insertFeePayment(updated)
            showToast("Payment recorded successfully! Fee invoice updated.")
        }
    }

    fun dispatchFeeReminder(studentName: String) {
        // Simulates push notification reminder dispatch
        showToast("Mobile reminder sent to $studentName: 'Pending tuition fees due call'.")
    }

    fun deleteFeePayment(feePayment: FeePayment) {
        viewModelScope.launch {
            repository.deleteFeePayment(feePayment)
            showToast("Fee payment invoice deleted.")
        }
    }


    // --- Translation Helper (Professional localization support) ---
    fun getTranslation(key: String): String {
        val lang = _appLanguage.value
        val stringsMap = mapOf(
            "English" to mapOf(
                "title" to "Smart Tuition Manager",
                "login" to "Log In",
                "signup" to "Sign Up",
                "logout" to "Log Out",
                "admin" to "Admin",
                "teacher" to "Teacher",
                "student" to "Student",
                "attendance" to "Attendance",
                "fees" to "Fees & Payments",
                "schedule" to "Class Schedule",
                "notices" to "Notices & News",
                "settings" to "Settings",
                "add_new" to "Add New",
                "email" to "Email Address",
                "password" to "Password",
                "forgot" to "Forgot Password?",
                "role" to "Role Types",
                "dashboard" to "Dashboard"
            ),
            "Español" to mapOf(
                "title" to "Administrador de Matrícula",
                "login" to "Iniciar Sesión",
                "signup" to "Registrarse",
                "logout" to "Cerrar Sesión",
                "admin" to "Administrador",
                "teacher" to "Profesor",
                "student" to "Estudiante",
                "attendance" to "Asistencia",
                "fees" to "Tarifas y Pagos",
                "schedule" to "Horario de Clases",
                "notices" to "Avisos y Noticias",
                "settings" to "Ajustes",
                "add_new" to "Agregar Nuevo",
                "email" to "Correo Electrónico",
                "password" to "Contraseña",
                "forgot" to "¿Olvidaste la contraseña?",
                "role" to "Tipos de Rol",
                "dashboard" to "Panel de Control"
            ),
            "Français" to mapOf(
                "title" to "Gestion de Scolarité",
                "login" to "Connexion",
                "signup" to "S'enregistrer",
                "logout" to "Déconnexion",
                "admin" to "Admin",
                "teacher" to "Enseignant",
                "student" to "Étudiant",
                "attendance" to "Présence",
                "fees" to "Frais & Règlements",
                "schedule" to "Emploi du Temps",
                "notices" to "Annonces & Avis",
                "settings" to "Paramètres",
                "add_new" to "Ajouter Nouveau",
                "email" to "Adresse Mail",
                "password" to "Mot de passe",
                "forgot" to "Mot de passe oublié?",
                "role" to "Sélection des rôles",
                "dashboard" to "Tableau de Bord"
            )
        )
        return stringsMap[lang]?.get(key) ?: stringsMap["English"]?.get(key) ?: key
    }
}

class TuitionViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TuitionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TuitionViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
