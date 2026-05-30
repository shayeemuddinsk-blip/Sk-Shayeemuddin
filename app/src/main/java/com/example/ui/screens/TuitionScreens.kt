@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.ui.viewmodel.TuitionViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

// Custom Theme Colors matching the Sleek Interface Theme
val SlateDarkBg = Color(0xFF0F172A)
val SlateLightBg = Color(0xFFF7F9FC)
val CardDarkBg = Color(0xFF1E293B)
val CardLightBg = Color(0xFFFFFFFF)

val EmeraldPrimary = Color(0xFF6750A4)
val EmeraldPrimaryDark = Color(0xFF4F378B)
val BlueAccent = Color(0xFF3B82F6)
val AmberAccent = Color(0xFFF59E0B)
val CoralAccent = Color(0xFFF43F5E)

@Composable
fun AppNavigator(viewModel: TuitionViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val toastMessage by viewModel.activeToast.collectAsState()
    val context = LocalContext.current

    // Navigation state: "SPLASH", "ONBOARDING", "LOGIN", "SIGNUP", "DASHBOARD"
    var currentScreen by remember { mutableStateOf("SPLASH") }

    // Color Scheme Override
    val bgColor = if (isDarkTheme) SlateDarkBg else SlateLightBg
    val contentColor = if (isDarkTheme) Color.White else Color(0xFF1E293B)

    MaterialTheme(
        colorScheme = if (isDarkTheme) {
            darkColorScheme(
                primary = EmeraldPrimary,
                background = SlateDarkBg,
                surface = CardDarkBg,
                onPrimary = Color.White,
                onBackground = Color.White,
                onSurface = Color.White
            )
        } else {
            lightColorScheme(
                primary = EmeraldPrimary,
                background = SlateLightBg,
                surface = CardLightBg,
                onPrimary = Color.White,
                onBackground = Color(0xFF1F2937),
                onSurface = Color(0xFF1F2937)
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            when (currentScreen) {
                "SPLASH" -> SplashScreen(
                    onTimeout = {
                        currentScreen = "ONBOARDING"
                    }
                )
                "ONBOARDING" -> OnboardingScreen(
                    onGetStarted = {
                        currentScreen = "LOGIN"
                    }
                )
                "LOGIN" -> LoginScreen(
                    viewModel = viewModel,
                    onNavigateToSignUp = { currentScreen = "SIGNUP" },
                    onLoginSuccess = { currentScreen = "DASHBOARD" }
                )
                "SIGNUP" -> SignUpScreen(
                    viewModel = viewModel,
                    onNavigateToLogin = { currentScreen = "LOGIN" },
                    onSignUpSuccess = { currentScreen = "DASHBOARD" }
                )
                "DASHBOARD" -> {
                    if (currentUser == null) {
                        currentScreen = "LOGIN"
                    } else {
                        MainContainer(viewModel = viewModel, onLogout = { currentScreen = "LOGIN" })
                    }
                }
            }

            // Central Dynamic Toast Notification Bar (M3 Snack style)
            toastMessage?.let { msg ->
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 24.dp, vertical = 72.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = EmeraldPrimary),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = msg,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { viewModel.clearToast() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Toast",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================
// 1. SPLASH SCREEN
// ==========================
@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2000)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(EmeraldPrimary.copy(alpha = 0.25f), Color.Transparent),
                        center = center,
                        radius = size.minDimension
                    )
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(EmeraldPrimary)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = "App Icon",
                    tint = Color.White,
                    modifier = Modifier.size(54.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Smart Tuition Manager",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Learn • Schedule • Grow",
                fontSize = 14.sp,
                color = EmeraldPrimary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ==========================
// 2. ONBOARDING SCREEN
// ==========================
@Composable
fun OnboardingScreen(onGetStarted: () -> Unit) {
    val onboardingPages = listOf(
        Triple(Icons.Filled.Group, "Role-Based Terminology", "Tailored portals specifically configured for School Directors, Teachers, and Enrolled Students with separate permissions."),
        Triple(Icons.Filled.AccountBalanceWallet, "Transparent Ledgers", "Instant generation of ledger accounts, invoice due trackers, payment entries, and automated receipt dispatches."),
        Triple(Icons.Filled.Assignment, "Academic Tracking", "Track attendance records dynamically, upload learning materials and assign physical homework lessons flawlessly.")
    )

    var pageIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = onGetStarted,
                modifier = Modifier.testTag("onboarding_skip_button")
            ) {
                Text("Skip", color = EmeraldPrimary)
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(128.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(EmeraldPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = onboardingPages[pageIndex].first,
                    contentDescription = "Visual illustration",
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(64.dp)
                )
            }
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                text = onboardingPages[pageIndex].second,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = onboardingPages[pageIndex].third,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                onboardingPages.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(if (index == pageIndex) 20.dp else 8.dp, 8.dp)
                            .clip(CircleShape)
                            .background(if (index == pageIndex) EmeraldPrimary else EmeraldPrimary.copy(alpha = 0.3f))
                    )
                }
            }

            Button(
                onClick = {
                    if (pageIndex < 2) {
                        pageIndex++
                    } else {
                        onGetStarted()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("onboarding_next_button"),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text(if (pageIndex == 2) "Get Started" else "Next", fontSize = 16.sp, color = Color.White)
            }
        }
    }
}

// ==========================
// 3. LOGIN SCREEN
// ==========================
@Composable
fun LoginScreen(
    viewModel: TuitionViewModel,
    onNavigateToSignUp: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("ADMIN") } // "ADMIN", "TEACHER", "STUDENT"
    var showForgotPassword by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.School,
            contentDescription = "Logo",
            tint = EmeraldPrimary,
            modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Welcome Back",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Manage your smart tuition portal",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Role Tab Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("ADMIN", "TEACHER", "STUDENT").forEach { role ->
                Button(
                    onClick = { selectedRole = role },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedRole == role) EmeraldPrimary else Color.Transparent,
                        contentColor = if (selectedRole == role) Color.White else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                        .testTag("login_role_tab_$role"),
                    elevation = null
                ) {
                    Text(role, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("login_username_input"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("login_password_input"),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = { showForgotPassword = true },
                modifier = Modifier.testTag("forgot_password_button")
            ) {
                Text("Forgot Password?", color = EmeraldPrimary, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Standard Login Action
        Button(
            onClick = {
                viewModel.login(email, selectedRole) { success, errMsg ->
                    if (success) onLoginSuccess()
                    else viewModel.showToast(errMsg)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("login_button"),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
        ) {
            Text("Log In as $selectedRole", fontSize = 16.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Google Sign-In Option
        OutlinedButton(
            onClick = {
                viewModel.simulateGoogleSignIn(selectedRole) { success, _ ->
                    if (success) onLoginSuccess()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("google_login_button")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Google Icon",
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Google Sign-In Option", color = MaterialTheme.colorScheme.onBackground)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Don't have an account?", fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
            TextButton(
                onClick = onNavigateToSignUp,
                modifier = Modifier.testTag("nav_to_signup_button")
            ) {
                Text("Sign Up", color = EmeraldPrimary, fontWeight = FontWeight.Bold)
            }
        }

        // Forgot password dialog simulation
        if (showForgotPassword) {
            Dialog(onDismissRequest = { showForgotPassword = false }) {
                Card(
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Reset Password", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Enter your email address to simulate a password reset dispatch:", fontSize = 14.sp, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(16.dp))
                        var resetEmail by remember { mutableStateOf("") }
                        OutlinedTextField(
                            value = resetEmail,
                            onValueChange = { resetEmail = it },
                            label = { Text("Email Address") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(onClick = { showForgotPassword = false }) {
                                Text("Cancel", color = Color.Gray)
                            }
                            Button(
                                onClick = {
                                    if (resetEmail.isNotEmpty()) {
                                        viewModel.simulateForgotPassword(resetEmail)
                                        showForgotPassword = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                            ) {
                                Text("Send Link")
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================
// 4. SIGN UP SCREEN
// ==========================
@Composable
fun SignUpScreen(
    viewModel: TuitionViewModel,
    onNavigateToLogin: () -> Unit,
    onSignUpSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("STUDENT") } // STUDENT or TEACHER (Admins usually registered already)
    var gradeOrSubject by remember { mutableStateOf("") } // Class grade or teacher subjects

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.School,
            contentDescription = "Logo",
            tint = EmeraldPrimary,
            modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Create Account",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Role option
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Registering as:", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = role == "STUDENT", onClick = { role = "STUDENT" })
                Text("Student", modifier = Modifier.clickable { role = "STUDENT" })
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(selected = role == "TEACHER", onClick = { role = "TEACHER" })
                Text("Teacher", modifier = Modifier.clickable { role = "TEACHER" })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = gradeOrSubject,
            onValueChange = { gradeOrSubject = it },
            label = { Text(if (role == "STUDENT") "Current Grade (e.g., Grade 10)" else "Subjects details (e.g., Physics, Maths)") },
            leadingIcon = { Icon(Icons.Default.Class, contentDescription = "Detail") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                viewModel.signUp(name, email, role, gradeOrSubject) { success, msg ->
                    if (success) {
                        onSignUpSuccess()
                    } else {
                        viewModel.showToast(msg)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("signup_submit_button"),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
        ) {
            Text("Register Account", fontSize = 16.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Already registered?", fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
            TextButton(
                onClick = onNavigateToLogin,
                modifier = Modifier.testTag("nav_to_login_button")
            ) {
                Text("Log In", color = EmeraldPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}


// ==========================================
// 5. MAIN CONTAINER PIPELINE
//   (Coordinates dashboards for Admin, Teacher, Student)
// ==========================================
@Composable
fun MainContainer(
    viewModel: TuitionViewModel,
    onLogout: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val scope = rememberCoroutineScope()

    // Dashboard navigation state: Admin Sidebar is controlled by custom drawers.
    // Teacher/Student bottom bars are controlled by integer tabs.
    var activeTab by remember { mutableStateOf(0) }
    var selectedAdminPanel by remember { mutableStateOf("DASHBOARD") } // "DASHBOARD", "STUDENTS", "TEACHERS", "COURSES", "FEES", "NOTICES"

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    if (currentUser?.role == "ADMIN") {
        // ADMIN LAYOUT - SIDEBAR DRAWERS
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.width(300.dp),
                    drawerContainerColor = MaterialTheme.colorScheme.surface
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(EmeraldPrimary)
                            .padding(24.dp)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.SupervisorAccount, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(currentUser?.name ?: "Principal Admin", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("ADMIN PANEL", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val menuItems = listOf(
                        Triple("DASHBOARD", "Dashboard Stats", Icons.Default.Analytics),
                        Triple("STUDENTS", "Manage Students", Icons.Default.Group),
                        Triple("TEACHERS", "Manage Teachers", Icons.Default.School),
                        Triple("COURSES", "Courses & Schedules", Icons.Default.LibraryBooks),
                        Triple("FEES", "Fees & Payments Ledger", Icons.Default.AccountBalanceWallet),
                        Triple("NOTICES", "Send Broadcast Notices", Icons.Default.Campaign),
                        Triple("SETTINGS", "System Settings", Icons.Default.Settings)
                    )

                    menuItems.forEach { (panel, label, icon) ->
                        NavigationDrawerItem(
                            icon = { Icon(icon, contentDescription = null, tint = if (selectedAdminPanel == panel) EmeraldPrimary else Color.Gray) },
                            label = { Text(label, fontWeight = if (selectedAdminPanel == panel) FontWeight.Bold else FontWeight.Normal, color = MaterialTheme.colorScheme.onBackground) },
                            selected = selectedAdminPanel == panel,
                            onClick = {
                                selectedAdminPanel = panel
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                                .testTag("admin_menu_$panel")
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.Red) },
                        label = { Text("Log Out", color = Color.Red, fontWeight = FontWeight.Bold) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            viewModel.logout()
                            onLogout()
                        },
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text(selectedAdminPanel.replace("_", " "), fontWeight = FontWeight.SemiBold) },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Drawer menu")
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background
                        )
                    )
                }
            ) { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    when (selectedAdminPanel) {
                        "DASHBOARD" -> AdminDashboardStatView(viewModel)
                        "STUDENTS" -> AdminStudentsDirectoryView(viewModel)
                        "TEACHERS" -> AdminTeachersDirectoryView(viewModel)
                        "COURSES" -> AdminCoursesSchedulerView(viewModel)
                        "FEES" -> AdminFeesLedgerView(viewModel)
                        "NOTICES" -> AdminNoticesBroadcaster(viewModel)
                        "SETTINGS" -> SettingsScreen(viewModel)
                    }
                }
            }
        }
    } else if (currentUser?.role == "TEACHER") {
        // TEACHER PORTAL - BOTTOM NAVIGATION
        Scaffold(
            bottomBar = {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    NavigationBarItem(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        label = { Text("Overview") },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = null) }
                    )
                    NavigationBarItem(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        label = { Text("Log Attendance") },
                        icon = { Icon(Icons.Default.CheckCircleOutline, contentDescription = null) }
                    )
                    NavigationBarItem(
                        selected = activeTab == 2,
                        onClick = { activeTab = 2 },
                        label = { Text("Uploads") },
                        icon = { Icon(Icons.Default.CloudUpload, contentDescription = null) }
                    )
                    NavigationBarItem(
                        selected = activeTab == 3,
                        onClick = { activeTab = 3 },
                        label = { Text("Settings") },
                        icon = { Icon(Icons.Default.Settings, contentDescription = null) }
                    )
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                when (activeTab) {
                    0 -> TeacherDashboardView(viewModel)
                    1 -> TeacherAttendanceView(viewModel)
                    2 -> TeacherUploadsView(viewModel)
                    3 -> SettingsScreen(viewModel, onLogout = onLogout)
                }
            }
        }
    } else {
        // STUDENT PORTAL - BOTTOM NAVIGATION
        Scaffold(
            bottomBar = {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    NavigationBarItem(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        label = { Text("Tuition") },
                        icon = { Icon(Icons.Default.Class, contentDescription = null) }
                    )
                    NavigationBarItem(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        label = { Text("Ledger due") },
                        icon = { Icon(Icons.Default.ReceiptLong, contentDescription = null) }
                    )
                    NavigationBarItem(
                        selected = activeTab == 2,
                        onClick = { activeTab = 2 },
                        label = { Text("Newsboard") },
                        icon = { Icon(Icons.Default.Campaign, contentDescription = null) }
                    )
                    NavigationBarItem(
                        selected = activeTab == 3,
                        onClick = { activeTab = 3 },
                        label = { Text("Settings") },
                        icon = { Icon(Icons.Default.Settings, contentDescription = null) }
                    )
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                when (activeTab) {
                    0 -> StudentDashboardView(viewModel)
                    1 -> StudentFeesView(viewModel)
                    2 -> StudentNoticesView(viewModel)
                    3 -> SettingsScreen(viewModel, onLogout = onLogout)
                }
            }
        }
    }
}


// ==========================================
// 6. ADMIN DASHBOARD STATS VIEW
// ==========================================
// Data helper for Quick Actions representing the Sleek theme
data class SleekQuickAction(val emoji: String, val label: String, val actionTag: String)

@Composable
fun ActivityItemRow(
    iconEmoji: String,
    iconBg: Color,
    iconTextColor: Color,
    title: String,
    subtitle: String,
    trailingText: String,
    isDarkTheme: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) CardDarkBg else Color.White
        ),
        border = BorderStroke(1.dp, if (isDarkTheme) Color.DarkGray else Color(0xFFF1F5F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (isDarkTheme) iconTextColor.copy(alpha = 0.25f) else iconBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iconEmoji,
                    color = iconTextColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = if (isDarkTheme) Color.LightGray else Color(0xFF64748B)
                )
            }
            Text(
                text = trailingText,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (trailingText == "Alert") CoralAccent else if (isDarkTheme) EmeraldPrimary else Color(0xFF0F172A)
            )
        }
    }
}

@Composable
fun AdminDashboardStatView(viewModel: TuitionViewModel) {
    val users by viewModel.allUsers.collectAsState()
    val courses by viewModel.allCourses.collectAsState()
    val payments by viewModel.allFeePayments.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()

    val mathStudents = users.filter { it.role == "STUDENT" }
    val mathTeachers = users.filter { it.role == "TEACHER" }

    // Computations
    val totalCollections = payments.filter { it.status == "PAID" }.sumOf { it.amountPaid }
    val totalPendingDues = payments.filter { it.status != "PAID" }.sumOf { it.amountDue }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Sleek Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "ADMIN DASHBOARD",
                        color = if (isDarkTheme) Color.LightGray else Color(0xFF64748B),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Welcome, ${currentUser?.name ?: "Sarah"}",
                        color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (isDarkTheme) CardDarkBg else Color.White)
                            .border(1.dp, if (isDarkTheme) Color.DarkGray else Color(0xFFE2E8F0), CircleShape)
                            .clickable { viewModel.showToast("No new notifications") },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🔔", fontSize = 16.sp)
                    }
                    val initials = (currentUser?.name ?: "Sarah").split(" ").mapNotNull { it.firstOrNull() }.take(2).joinToString("").uppercase()
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(EmeraldPrimary)
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 2. Twin Stats Banners (Lavender & Blue)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card 1: Total Students (Lavender/Purple style container)
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(115.dp)
                        .testTag("stats_card_students"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkTheme) Color(0xFF2C1B4D) else Color(0xFFEADDFF)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total Students",
                            color = if (isDarkTheme) Color(0xFFD4C8EC) else Color(0xFF4F378B),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = "${mathStudents.size}",
                                color = if (isDarkTheme) Color.White else Color(0xFF21005D),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isDarkTheme) Color(0xFF4F378B).copy(alpha = 0.5f) else Color.White.copy(alpha = 0.6f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "+12%",
                                    color = if (isDarkTheme) Color.White else Color(0xFF21005D),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Card 2: Cashflow Collection (Light Blue style container)
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(115.dp)
                        .testTag("stats_card_collections"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkTheme) Color(0xFF003258) else Color(0xFFD0E4FF)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Monthly Fee",
                            color = if (isDarkTheme) Color(0xFFB9D3F3) else Color(0xFF003258),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = "$${"%,.1fk".format(totalCollections / 1000.0)}",
                                color = if (isDarkTheme) Color.White else Color(0xFF001D36),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isDarkTheme) Color(0xFF003258).copy(alpha = 0.5f) else Color.White.copy(alpha = 0.6f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "94%",
                                    color = if (isDarkTheme) Color.White else Color(0xFF001D36),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Quick Actions Grid
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Quick Actions",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val quickActions = listOf(
                        SleekQuickAction("👤", "Add Student", "STUDENTS"),
                        SleekQuickAction("📅", "Attendance", "ATTENDANCE"),
                        SleekQuickAction("💳", "Fees", "FEES"),
                        SleekQuickAction("📊", "Reports", "REPORTS")
                    )

                    quickActions.forEach { action ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    viewModel.showToast("Quick Action: ${action.label} panel activated.")
                                }
                                .padding(vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isDarkTheme) CardDarkBg else Color.White)
                                    .border(1.dp, if (isDarkTheme) Color.DarkGray else Color(0xFFE2E8F0), RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(action.emoji, fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = action.label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDarkTheme) Color.LightGray else Color(0xFF475569),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // 4. Secondary Row with Outstanding Arrears and Classes for functionality preservation
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(85.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = CoralAccent.copy(alpha = if (isDarkTheme) 0.15f else 0.08f)),
                    border = BorderStroke(1.dp, CoralAccent.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.Center) {
                        Text("Outstanding Dues", fontSize = 11.sp, color = if (isDarkTheme) Color.LightGray else Color(0xFF64748B))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("$${"%,.0f".format(totalPendingDues)}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CoralAccent)
                    }
                }
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(85.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldPrimary.copy(alpha = if (isDarkTheme) 0.15f else 0.08f)),
                    border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.Center) {
                        Text("Active Classes", fontSize = 11.sp, color = if (isDarkTheme) Color.LightGray else Color(0xFF64748B))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${courses.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                    }
                }
            }
        }

        // 5. Recent Activity List matching exact mock activity with fully working states
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Activity",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                    )
                    TextButton(
                        onClick = { viewModel.showToast("All activities are synchronized!") }
                    ) {
                        Text(
                            text = "See All",
                            color = EmeraldPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ActivityItemRow(
                        iconEmoji = "✓",
                        iconBg = Color(0xFFDCFCE7),
                        iconTextColor = Color(0xFF16A34A),
                        title = "Marcus Aurelius",
                        subtitle = "Paid Math Tuition • 2m ago",
                        trailingText = "$120",
                        isDarkTheme = isDarkTheme
                    )

                    ActivityItemRow(
                        iconEmoji = "!",
                        iconBg = Color(0xFFFEF3C7),
                        iconTextColor = Color(0xFFD97706),
                        title = "Attendance Alert",
                        subtitle = "Physics Batch B • 10 absentees",
                        trailingText = "Alert",
                        isDarkTheme = isDarkTheme
                    )

                    ActivityItemRow(
                        iconEmoji = "+",
                        iconBg = Color(0xFFDBEAFE),
                        iconTextColor = Color(0xFF2563EB),
                        title = "New Teacher Added",
                        subtitle = "Dr. Robert Ford • Biology",
                        trailingText = "1h ago",
                        isDarkTheme = isDarkTheme
                    )
                }
            }
        }

        // 6. Distribution stats
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Cashflow Distribution Breakdown", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("Live Ledger Metrics", fontSize = 11.sp, color = EmeraldPrimary, fontWeight = FontWeight.Bold)
            }
        }

        // 7. Visualized custom chart
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDarkTheme) CardDarkBg else Color.White),
                border = BorderStroke(1.dp, if (isDarkTheme) Color.DarkGray else Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Income vs Outstanding Arrears", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val paidWidth = (size.width * 0.65f)
                            val dueWidth = (size.width * 0.35f)
                            // Draw Green collection indicator bar (Calculated distribution ratio)
                            drawRect(
                                color = EmeraldPrimary,
                                topLeft = Offset(0f, 15f),
                                size = androidx.compose.ui.geometry.Size(paidWidth, 24f)
                            )
                            drawRect(
                                color = CoralAccent,
                                topLeft = Offset(paidWidth, 15f),
                                size = androidx.compose.ui.geometry.Size(dueWidth, 24f)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(EmeraldPrimary))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Received ($${"%.0f".format(totalCollections)})", fontSize = 10.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(CoralAccent))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Arrears ($${"%.0f".format(totalPendingDues)})", fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}


// ==========================================
// 7. ADMIN STUDENTS DIRECTORY VIEW
// ==========================================
@Composable
fun AdminStudentsDirectoryView(viewModel: TuitionViewModel) {
    val users by viewModel.allUsers.collectAsState()
    val students = users.filter { it.role == "STUDENT" }

    var searchQuery by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    var studentName by remember { mutableStateOf("") }
    var studentEmail by remember { mutableStateOf("") }
    var studentGrade by remember { mutableStateOf("") }
    var studentPhone by remember { mutableStateOf("") }

    val filtered = students.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.email.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search Students") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = EmeraldPrimary,
                contentColor = Color.White,
                modifier = Modifier.size(52.dp).testTag("add_student_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Student")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No Student Records Found", color = Color.Gray)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filtered) { stu ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldPrimary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = EmeraldPrimary)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(stu.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(stu.email, fontSize = 12.sp, color = Color.Gray)
                                    Text("${stu.grade} • Joined ${stu.joinDate}", fontSize = 11.sp, color = EmeraldPrimary)
                                }
                            }
                            IconButton(onClick = { viewModel.deleteUser(stu.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove Student", tint = CoralAccent)
                            }
                        }
                    }
                }
            }
        }

        if (showDialog) {
            Dialog(onDismissRequest = { showDialog = false }) {
                Card(
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Add New Student", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(value = studentName, onValueChange = { studentName = it }, label = { Text("Student Name") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = studentEmail, onValueChange = { studentEmail = it }, label = { Text("Email Address") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = studentGrade, onValueChange = { studentGrade = it }, label = { Text("Grade level (e.g. Class 10)") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = studentPhone, onValueChange = { studentPhone = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth())

                        Spacer(modifier = Modifier.height(20.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            TextButton(onClick = { showDialog = false }) { Text("Cancel") }
                            Button(
                                onClick = {
                                    if (studentName.isNotEmpty() && studentEmail.isNotEmpty()) {
                                        viewModel.createStudent(studentName, studentEmail, studentGrade, studentPhone)
                                        showDialog = false
                                        studentName = ""
                                        studentEmail = ""
                                        studentGrade = ""
                                        studentPhone = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                            ) {
                                Text("Save Student")
                            }
                        }
                    }
                }
            }
        }
    }
}


// ==========================================
// 8. ADMIN TEACHERS DIRECTORY VIEW
// ==========================================
@Composable
fun AdminTeachersDirectoryView(viewModel: TuitionViewModel) {
    val users by viewModel.allUsers.collectAsState()
    val teachers = users.filter { it.role == "TEACHER" }

    var searchQuery by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    var teacherName by remember { mutableStateOf("") }
    var teacherEmail by remember { mutableStateOf("") }
    var teacherSubjects by remember { mutableStateOf("") }
    var teacherPhone by remember { mutableStateOf("") }

    val filtered = teachers.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.email.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search Faculty") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = EmeraldPrimary,
                contentColor = Color.White,
                modifier = Modifier.size(52.dp).testTag("add_teacher_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Teacher")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No Faculty Records Found", color = Color.Gray)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filtered) { teach ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(BlueAccent.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.School, contentDescription = null, tint = BlueAccent)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(teach.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(teach.email, fontSize = 11.sp, color = Color.Gray)
                                    Text("Subjects: ${teach.subjects}", fontSize = 12.sp, color = BlueAccent)
                                    Text("Join Date: ${teach.joinDate}", fontSize = 10.sp, color = Color.Gray)
                                }
                            }
                            IconButton(onClick = { viewModel.deleteUser(teach.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove Teacher", tint = CoralAccent)
                            }
                        }
                    }
                }
            }
        }

        if (showDialog) {
            Dialog(onDismissRequest = { showDialog = false }) {
                Card(
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Add New Faculty", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(value = teacherName, onValueChange = { teacherName = it }, label = { Text("Teacher Name") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = teacherEmail, onValueChange = { teacherEmail = it }, label = { Text("Email Address") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = teacherSubjects, onValueChange = { teacherSubjects = it }, label = { Text("Expert Subjects (e.g. Biology)") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = teacherPhone, onValueChange = { teacherPhone = it }, label = { Text("Contact Phone") }, modifier = Modifier.fillMaxWidth())

                        Spacer(modifier = Modifier.height(20.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            TextButton(onClick = { showDialog = false }) { Text("Cancel") }
                            Button(
                                onClick = {
                                    if (teacherName.isNotEmpty() && teacherEmail.isNotEmpty()) {
                                        viewModel.createTeacher(teacherName, teacherEmail, teacherSubjects, teacherPhone)
                                        showDialog = false
                                        teacherName = ""
                                        teacherEmail = ""
                                        teacherSubjects = ""
                                        teacherPhone = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                            ) {
                                Text("Save Faculty")
                            }
                        }
                    }
                }
            }
        }
    }
}


// ==========================================
// 9. ADMIN COURSES SCHEDULER VIEW
// ==========================================
@Composable
fun AdminCoursesSchedulerView(viewModel: TuitionViewModel) {
    val courses by viewModel.allCourses.collectAsState()
    val users by viewModel.allUsers.collectAsState()
    val teachers = users.filter { it.role == "TEACHER" }

    var showDialog by remember { mutableStateOf(false) }

    var courseName by remember { mutableStateOf("") }
    var courseSubject by remember { mutableStateOf("") }
    var courseSchedule by remember { mutableStateOf("") }
    var courseFee by remember { mutableStateOf("") }
    var courseRoom by remember { mutableStateOf("") }
    var selectedTeacherEmail by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Syllabus & Lecture Slots", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Button(
                onClick = {
                    if (teachers.isNotEmpty()) {
                        selectedTeacherEmail = teachers.first().email
                    }
                    showDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                modifier = Modifier.testTag("add_course_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Course")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (courses.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No Courses Programmed", color = Color.Gray)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(courses) { course ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(course.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Classroom: ${course.roomNo} | Slot: ${course.schedule}", fontSize = 12.sp, color = Color.Gray)
                                Row(
                                    modifier = Modifier.padding(top = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(EmeraldPrimary.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("$${course.monthlyFee}/month", fontSize = 11.sp, color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Faculty: ${course.teacherId}", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                            IconButton(onClick = { viewModel.deleteCourse(course) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Course", tint = CoralAccent)
                            }
                        }
                    }
                }
            }
        }

        if (showDialog) {
            Dialog(onDismissRequest = { showDialog = false }) {
                Card(
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Add New Course Slot", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))

                        val scrollableForm = rememberScrollState()
                        Column(modifier = Modifier.weight(1f, false).verticalScroll(scrollableForm)) {
                            OutlinedTextField(value = courseName, onValueChange = { courseName = it }, label = { Text("Course Title") }, modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(value = courseSubject, onValueChange = { courseSubject = it }, label = { Text("Subject Domain") }, modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(value = courseSchedule, onValueChange = { courseSchedule = it }, label = { Text("Weekly Schedule (e.g. Mon 2-4 PM)") }, modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(value = courseFee, onValueChange = { courseFee = it }, label = { Text("Monthly Fee Amount ($)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(value = courseRoom, onValueChange = { courseRoom = it }, label = { Text("Classroom Space (e.g. Lab 2)") }, modifier = Modifier.fillMaxWidth())

                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Select Faculty Supervisor:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            if (teachers.isEmpty()) {
                                Text("Error: Please register a teacher first!", color = Color.Red, fontSize = 12.sp)
                            } else {
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(teachers) { t ->
                                        Card(
                                            onClick = { selectedTeacherEmail = t.email },
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (selectedTeacherEmail == t.email) EmeraldPrimary.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
                                            ),
                                            border = BorderStroke(1.dp, if (selectedTeacherEmail == t.email) EmeraldPrimary else Color.LightGray)
                                        ) {
                                            Text(t.name, modifier = Modifier.padding(8.dp), fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            TextButton(onClick = { showDialog = false }) { Text("Cancel") }
                            Button(
                                onClick = {
                                    val feeVal = courseFee.toDoubleOrNull() ?: 100.0
                                    if (courseName.isNotEmpty() && selectedTeacherEmail.isNotEmpty()) {
                                        viewModel.createCourse(courseName, courseSubject, selectedTeacherEmail, courseSchedule, feeVal, courseRoom)
                                        showDialog = false
                                        courseName = ""
                                        courseSubject = ""
                                        courseSchedule = ""
                                        courseFee = ""
                                        courseRoom = ""
                                    }
                                },
                                enabled = teachers.isNotEmpty(),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                            ) {
                                Text("Publish Course")
                            }
                        }
                    }
                }
            }
        }
    }
}


// ==========================================
// 10. ADMIN FEES LEDGER VIEW
// ==========================================
@Composable
fun AdminFeesLedgerView(viewModel: TuitionViewModel) {
    val payments by viewModel.allFeePayments.collectAsState()
    val users by viewModel.allUsers.collectAsState()
    val students = users.filter { it.role == "STUDENT" }

    var showDialog by remember { mutableStateOf(false) }

    var selectedStudentEmail by remember { mutableStateOf("") }
    var courseName by remember { mutableStateOf("") }
    var amountVal by remember { mutableStateOf("") }
    var monthName by remember { mutableStateOf("May 2026") }
    var initialStatus by remember { mutableStateOf("DUE") } // "DUE", "PAID", "OVERDUE"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Fee Invoices & Payments", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Button(
                onClick = {
                    if (students.isNotEmpty()) selectedStudentEmail = students.first().email
                    showDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                modifier = Modifier.testTag("generate_fee_bill_button")
            ) {
                Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Bill Student")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (payments.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No Invoice Ledgers logged", color = Color.Gray)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(payments) { pay ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(pay.studentName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("${pay.courseName} • ${pay.monthName}", fontSize = 12.sp, color = Color.Gray)
                                Row(
                                    modifier = Modifier.padding(top = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val pillColor = when (pay.status) {
                                        "PAID" -> EmeraldPrimary.copy(alpha = 0.2f)
                                        "DUE" -> AmberAccent.copy(alpha = 0.2f)
                                        else -> CoralAccent.copy(alpha = 0.2f)
                                    }
                                    val textColor = when (pay.status) {
                                        "PAID" -> EmeraldPrimary
                                        "DUE" -> AmberAccent
                                        else -> CoralAccent
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(pillColor)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(pay.status, fontSize = 10.sp, color = textColor, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (pay.status == "PAID") "Collected: $${pay.amountPaid}" else "Amount Due: $${pay.amountDue}",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                            }

                            Row {
                                if (pay.status != "PAID") {
                                    IconButton(
                                        onClick = { viewModel.recordPayment(pay.id, pay.amountDue) },
                                        modifier = Modifier.testTag("mark_as_paid_btn_${pay.id}")
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = "Mark Paid", tint = EmeraldPrimary)
                                    }
                                    IconButton(
                                        onClick = { viewModel.dispatchFeeReminder(pay.studentName) },
                                        modifier = Modifier.testTag("remind_due_btn_${pay.id}")
                                    ) {
                                        Icon(Icons.Default.NotificationsActive, contentDescription = "Send Reminder", tint = AmberAccent)
                                    }
                                }
                                IconButton(onClick = { viewModel.deleteFeePayment(pay) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete invoice", tint = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showDialog) {
            Dialog(onDismissRequest = { showDialog = false }) {
                Card(
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Create Fee Invoice", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))

                        val scrollForm = rememberScrollState()
                        Column(modifier = Modifier.weight(1f, false).verticalScroll(scrollForm)) {
                            Text("Select Student Target:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            if (students.isEmpty()) {
                                Text("No students available. Create one first!", color = Color.Red, fontSize = 12.sp)
                            } else {
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(students) { s ->
                                        Card(
                                            onClick = { selectedStudentEmail = s.id },
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (selectedStudentEmail == s.id) EmeraldPrimary.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
                                            ),
                                            border = BorderStroke(1.dp, if (selectedStudentEmail == s.id) EmeraldPrimary else Color.LightGray)
                                        ) {
                                            Text(s.name, modifier = Modifier.padding(8.dp), fontSize = 11.sp)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(value = courseName, onValueChange = { courseName = it }, label = { Text("Course Name Reference") }, modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(value = amountVal, onValueChange = { amountVal = it }, label = { Text("Fee Amount ($)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(value = monthName, onValueChange = { monthName = it }, label = { Text("Billing Cycle Month (e.g. June 2026)") }, modifier = Modifier.fillMaxWidth())

                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Status Option:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Row {
                                listOf("DUE", "PAID", "OVERDUE").forEach { s ->
                                    Button(
                                        onClick = { initialStatus = s },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (initialStatus == s) EmeraldPrimary else MaterialTheme.colorScheme.surface,
                                            contentColor = if (initialStatus == s) Color.White else Color.Gray
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.padding(4.dp)
                                    ) {
                                        Text(s, fontSize = 10.sp)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            TextButton(onClick = { showDialog = false }) { Text("Cancel") }
                            Button(
                                onClick = {
                                    val amt = amountVal.toDoubleOrNull() ?: 100.0
                                    val studentObj = students.find { it.id == selectedStudentEmail }
                                    if (studentObj != null && courseName.isNotEmpty()) {
                                        viewModel.appendFeeRecord(
                                            studentId = studentObj.id,
                                            studentName = studentObj.name,
                                            courseName = courseName,
                                            amountDue = amt,
                                            monthName = monthName,
                                            status = initialStatus
                                        )
                                        showDialog = false
                                        courseName = ""
                                        amountVal = ""
                                    }
                                },
                                enabled = students.isNotEmpty(),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                            ) {
                                Text("Issue Bill")
                            }
                        }
                    }
                }
            }
        }
    }
}


// ==========================================
// 11. ADMIN NOTICES BROADCASTER
// ==========================================
@Composable
fun AdminNoticesBroadcaster(viewModel: TuitionViewModel) {
    val notices by viewModel.allNotices.collectAsState()

    var showDialog by remember { mutableStateOf(false) }

    var noticeTitle by remember { mutableStateOf("") }
    var noticeContent by remember { mutableStateOf("") }
    var noticeTarget by remember { mutableStateOf("ALL") } // "ALL", "TEACHER", "STUDENT"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Broadcast Notice Board", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Button(
                onClick = { showDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                modifier = Modifier.testTag("compose_notice_btn")
            ) {
                Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Broadcast")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (notices.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No active announcements", color = Color.Gray)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(notices) { notice ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(EmeraldPrimary.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("Target: ${notice.targetRole}", fontSize = 10.sp, color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(notice.date, fontSize = 11.sp, color = Color.Gray)
                                }
                                IconButton(modifier = Modifier.size(24.dp), onClick = { viewModel.deleteNotice(notice) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Notice", tint = Color.LightGray)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(notice.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(notice.content, fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("By: ${notice.senderName}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        }
                    }
                }
            }
        }

        if (showDialog) {
            Dialog(onDismissRequest = { showDialog = false }) {
                Card(
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("New Notice Broadcast", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(value = noticeTitle, onValueChange = { noticeTitle = it }, label = { Text("Announcement Heading") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = noticeContent, onValueChange = { noticeContent = it }, label = { Text("Details message content") }, modifier = Modifier.fillMaxWidth(), minLines = 3)

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Target Recipients Group:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Row {
                            listOf("ALL", "TEACHER", "STUDENT").forEach { t ->
                                Button(
                                    onClick = { noticeTarget = t },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (noticeTarget == t) EmeraldPrimary else MaterialTheme.colorScheme.surface,
                                        contentColor = if (noticeTarget == t) Color.White else Color.Gray
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.padding(4.dp)
                                ) {
                                    Text(t, fontSize = 11.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            TextButton(onClick = { showDialog = false }) { Text("Cancel") }
                            Button(
                                onClick = {
                                    if (noticeTitle.isNotEmpty() && noticeContent.isNotEmpty()) {
                                        viewModel.dispatchNotice(noticeTitle, noticeContent, noticeTarget)
                                        showDialog = false
                                        noticeTitle = ""
                                        noticeContent = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                            ) {
                                Text("Broadcast")
                            }
                        }
                    }
                }
            }
        }
    }
}


// ==========================================
// 12. TEACHER DASHBOARD VIEW
// ==========================================
@Composable
fun TeacherDashboardView(viewModel: TuitionViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val courses by viewModel.allCourses.collectAsState()
    val homework by viewModel.allHomeworks.collectAsState()

    // Faculty matching courses & homework
    val currentTeacherId = currentUser?.email ?: "teacher1@smart.com"
    val registeredClasses = courses.filter { it.teacherId == currentTeacherId }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BlueAccent)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Faculty Overview Portal", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(currentUser?.name ?: "Professor", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("Expertise Subjects: ${currentUser?.subjects}", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
                }
            }
        }

        item {
            Text("Registered Teaching Slots", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        if (registeredClasses.isEmpty()) {
            item {
                Text("No assigned classes logged", color = Color.Gray, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            }
        } else {
            items(registeredClasses) { cl ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(cl.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Regular Schedule: ${cl.schedule}", fontSize = 12.sp, color = BlueAccent)
                        Text("Tutoring Space: ${cl.roomNo}", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }
        }

        item {
            Text("Assigned Active Homework Homework", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        if (homework.isEmpty()) {
            item {
                Text("No homework issued yet", color = Color.Gray, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            }
        } else {
            items(homework) { hw ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(hw.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(hw.description, fontSize = 12.sp)
                            Text("Target Course: ${hw.courseName} • Due: ${hw.dueDate}", fontSize = 10.sp, color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = { viewModel.deleteHomework(hw) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete HW", tint = CoralAccent)
                        }
                    }
                }
            }
        }
    }
}


// ==========================================
// 13. TEACHER ATTENDANCE VIEWER
// ==========================================
@Composable
fun TeacherAttendanceView(viewModel: TuitionViewModel) {
    val users by viewModel.allUsers.collectAsState()
    val courses by viewModel.allCourses.collectAsState()
    val students = users.filter { it.role == "STUDENT" }

    var selectedStudentEmail by remember { mutableStateOf("") }
    var selectedCourseId by remember { mutableStateOf<Long>(-1) }
    var attendanceStatus by remember { mutableStateOf("Present") } // Present, Absent, Late

    LaunchedEffect(students, courses) {
        if (students.isNotEmpty()) selectedStudentEmail = students.first().email
        if (courses.isNotEmpty()) selectedCourseId = courses.first().id
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Mark Student Attendance", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("1. Select Student Target:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                if (students.isEmpty()) {
                    Text("No registered students inside system database", color = Color.Red, fontSize = 12.sp)
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(students) { stu ->
                            Card(
                                onClick = { selectedStudentEmail = stu.id },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selectedStudentEmail == stu.id) EmeraldPrimary.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
                                ),
                                border = BorderStroke(1.dp, if (selectedStudentEmail == stu.id) EmeraldPrimary else Color.Gray)
                            ) {
                                Text(stu.name, modifier = Modifier.padding(8.dp), fontSize = 12.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("2. Select Course Stream:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                if (courses.isEmpty()) {
                    Text("No programmed courses inside system data", color = Color.Red, fontSize = 12.sp)
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(courses) { crs ->
                            Card(
                                onClick = { selectedCourseId = crs.id },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selectedCourseId == crs.id) BlueAccent.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
                                ),
                                border = BorderStroke(1.dp, if (selectedCourseId == crs.id) BlueAccent else Color.Gray)
                            ) {
                                Text(crs.name, modifier = Modifier.padding(8.dp), fontSize = 12.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text("3. Marking Status Action:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Row(modifier = Modifier.padding(top = 8.dp)) {
                    listOf("Present", "Absent", "Late").forEach { stat ->
                        val color = when (stat) {
                            "Present" -> EmeraldPrimary
                            "Absent" -> CoralAccent
                            else -> AmberAccent
                        }
                        Button(
                            onClick = { attendanceStatus = stat },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (attendanceStatus == stat) color else Color.Transparent,
                                contentColor = if (attendanceStatus == stat) Color.White else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            ),
                            border = BorderStroke(1.dp, color),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                        ) {
                            Text(stat, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        val stuObj = students.find { it.id == selectedStudentEmail }
                        if (stuObj != null && selectedCourseId != -1L) {
                            viewModel.recordAttendance(
                                studentId = stuObj.id,
                                studentName = stuObj.name,
                                courseId = selectedCourseId,
                                status = attendanceStatus,
                                date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                            )
                        } else {
                            viewModel.showToast("Mismatched registry configurations")
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("Submit Attendance Record", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}


// ==========================================
// 14. TEACHER UPLOADS WORKVIEW (Study handouts & homework creation)
// ==========================================
@Composable
fun TeacherUploadsView(viewModel: TuitionViewModel) {
    val materials by viewModel.allStudyMaterials.collectAsState()

    var showMaterialDialog by remember { mutableStateOf(false) }
    var showHomeworkDialog by remember { mutableStateOf(false) }

    var matTitle by remember { mutableStateOf("") }
    var matDesc by remember { mutableStateOf("") }
    var matCourseRef by remember { mutableStateOf("") }
    var matType by remember { mutableStateOf("PDF") }

    var hwTitle by remember { mutableStateOf("") }
    var hwDesc by remember { mutableStateOf("") }
    var hwCourseRef by remember { mutableStateOf("") }
    var hwDue by remember { mutableStateOf("2026-06-10") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { showMaterialDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = BlueAccent),
                modifier = Modifier.weight(1f).testTag("teacher_add_material")
            ) {
                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Notes Document")
            }
            Button(
                onClick = { showHomeworkDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                modifier = Modifier.weight(1f).testTag("teacher_add_homework")
            ) {
                Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Issue Homework")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Active Study Handouts Published", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        if (materials.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No publications available now", color = Color.Gray)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(materials) { mat ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(BlueAccent.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (mat.fileType == "PDF") Icons.Default.PictureAsPdf else Icons.Default.Description,
                                        contentDescription = null,
                                        tint = BlueAccent
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(mat.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(mat.description, fontSize = 11.sp, color = Color.Gray)
                                    Text("Class: ${mat.courseName} • By: ${mat.teacherName}", fontSize = 10.sp, color = BlueAccent)
                                }
                            }
                            IconButton(onClick = { viewModel.deleteStudyMaterial(mat) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CoralAccent)
                            }
                        }
                    }
                }
            }
        }

        // Notes upload Dialog
        if (showMaterialDialog) {
            Dialog(onDismissRequest = { showMaterialDialog = false }) {
                Card(
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Publish Notes Handouts", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(value = matTitle, onValueChange = { matTitle = it }, label = { Text("Document Header Title") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = matDesc, onValueChange = { matDesc = it }, label = { Text("Brief Description of topic") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = matCourseRef, onValueChange = { matCourseRef = it }, label = { Text("Course Target Name") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Format: ", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            listOf("PDF", "DOC", "IMAGE").forEach { f ->
                                Button(
                                    onClick = { matType = f },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (matType == f) BlueAccent else MaterialTheme.colorScheme.surface,
                                        contentColor = if (matType == f) Color.White else Color.Gray
                                    ),
                                    modifier = Modifier.weight(1f).padding(2.dp)
                                ) {
                                    Text(f, fontSize = 9.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            TextButton(onClick = { showMaterialDialog = false }) { Text("Cancel") }
                            Button(
                                onClick = {
                                    if (matTitle.isNotEmpty() && matCourseRef.isNotEmpty()) {
                                        viewModel.uploadStudyMaterial(matTitle, matDesc, matCourseRef, matType)
                                        showMaterialDialog = false
                                        matTitle = ""
                                        matDesc = ""
                                        matCourseRef = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BlueAccent)
                            ) {
                                Text("Publish")
                            }
                        }
                    }
                }
            }
        }

        // Homework issue Dialog
        if (showHomeworkDialog) {
            Dialog(onDismissRequest = { showHomeworkDialog = false }) {
                Card(
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Assign Course Homework", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(value = hwTitle, onValueChange = { hwTitle = it }, label = { Text("Task Title Heading") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = hwDesc, onValueChange = { hwDesc = it }, label = { Text("Problem statements instruction") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = hwCourseRef, onValueChange = { hwCourseRef = it }, label = { Text("Course Target Name") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = hwDue, onValueChange = { hwDue = it }, label = { Text("Homework Deadline Date") }, modifier = Modifier.fillMaxWidth())

                        Spacer(modifier = Modifier.height(20.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            TextButton(onClick = { showHomeworkDialog = false }) { Text("Cancel") }
                            Button(
                                onClick = {
                                    if (hwTitle.isNotEmpty() && hwCourseRef.isNotEmpty()) {
                                        viewModel.publishHomework(hwTitle, hwDesc, hwCourseRef, hwDue)
                                        showHomeworkDialog = false
                                        hwTitle = ""
                                        hwDesc = ""
                                        hwCourseRef = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                            ) {
                                Text("Assign")
                            }
                        }
                    }
                }
            }
        }
    }
}


// ==========================================
// 15. STUDENT PORTAL DASHBOARD VIEW
//   (Course routine list, materials reading catalog, active homework)
// ==========================================
@Composable
fun StudentDashboardView(viewModel: TuitionViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val courses by viewModel.allCourses.collectAsState()
    val materials by viewModel.allStudyMaterials.collectAsState()
    val homeworks by viewModel.allHomeworks.collectAsState()
    val attendance by viewModel.allAttendance.collectAsState()

    val currentStudentId = currentUser?.email ?: "student1@smart.com"

    val studentAttendanceLogs = attendance.filter { it.studentId == currentStudentId }
    val presents = studentAttendanceLogs.filter { it.status == "Present" }.size
    val totalClasses = studentAttendanceLogs.size

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EmeraldPrimary)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Face, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(currentUser?.name ?: "Student Profile", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("${currentUser?.grade} • Joined ${currentUser?.joinDate}", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                    }
                }
            }
        }

        // Attendance stats radial ring drawing
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Attendance Score Tracker", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            text = "Score: $presents / $totalClasses Classes Checked",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (totalClasses > 0) "Efficiency Rate: ${(presents * 100) / totalClasses}%" else "Efficiency Rate: 100%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                    }
                    Box(
                        modifier = Modifier.size(54.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.matchParentSize()) {
                            drawCircle(color = Color.LightGray.copy(alpha = 0.3f), radius = size.minDimension / 2f)
                            drawArc(
                                color = EmeraldPrimary,
                                startAngle = -90f,
                                sweepAngle = if (totalClasses > 0) (presents.toFloat() / totalClasses.toFloat()) * 360f else 360f,
                                useCenter = false,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 12f)
                            )
                        }
                        Icon(Icons.Default.Celebration, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }

        item {
            Text("Active Syllabus Schedule Classes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        if (courses.isEmpty()) {
            item {
                Text("No course slots configured", color = Color.Gray)
            }
        } else {
            items(courses) { cs ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(cs.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Topic Domain: ${cs.subject}", fontSize = 11.sp, color = BlueAccent)
                        Row(
                            modifier = Modifier.padding(top = 4.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Schedule: ${cs.schedule}", fontSize = 11.sp, color = Color.Gray)
                            Text("Room: ${cs.roomNo}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                        }
                    }
                }
            }
        }

        item {
            Text("Study Material Downloads Library", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        if (materials.isEmpty()) {
            item {
                Text("No materials uploaded by teachers yet", color = Color.Gray)
            }
        } else {
            items(materials) { mat ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Book, contentDescription = null, tint = BlueAccent, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(mat.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(mat.description, fontSize = 11.sp, color = Color.Gray)
                                Text("Uploaded by: ${mat.teacherName}", fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                        IconButton(onClick = { viewModel.showToast("Simulating secure storage download: '${mat.title}' saved!") }) {
                            Icon(Icons.Default.DownloadForOffline, contentDescription = "Download Resource", tint = EmeraldPrimary, modifier = Modifier.size(28.dp))
                        }
                    }
                }
            }
        }

        item {
            Text("Assigned Homework tasks", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        if (homeworks.isEmpty()) {
            item {
                Text("No homework targets found", color = Color.Gray)
            }
        } else {
            items(homeworks) { hm ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(hm.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(hm.description, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("By Teacher: ${hm.teacherName}", fontSize = 10.sp, color = Color.Gray)
                            Text("Target Deadline: ${hm.dueDate}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CoralAccent)
                        }
                    }
                }
            }
        }
    }
}


// ==========================================
// 16. STUDENT FEES & PDF RECEIPT VISUALIZER
// ==========================================
@Composable
fun StudentFeesView(viewModel: TuitionViewModel) {
    val payments by viewModel.allFeePayments.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val currentStudentId = currentUser?.email ?: ""
    val studentPayments = payments.filter { it.studentId == currentStudentId }

    var selectedReceipt by remember { mutableStateOf<FeePayment?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Your Financial Bills Status", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        if (studentPayments.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No fee payment records found", color = Color.Gray)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(studentPayments) { pay ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(pay.courseName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("Billing Month: ${pay.monthName}", fontSize = 12.sp, color = Color.Gray)
                                Row(
                                    modifier = Modifier.padding(top = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val color = if (pay.status == "PAID") EmeraldPrimary else CoralAccent
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(color.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(pay.status, fontSize = 10.sp, color = color, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (pay.status == "PAID") "Paid: $${pay.amountPaid}" else "Outstanding Due: $${pay.amountDue}",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                            }

                            if (pay.status == "PAID") {
                                Button(
                                    onClick = { selectedReceipt = pay },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                    modifier = Modifier.testTag("pdf_receipt_btn_${pay.id}")
                                ) {
                                    Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Receipt", fontSize = 11.sp)
                                }
                            } else {
                                // Direct Simulation checkout
                                Button(
                                    onClick = { viewModel.recordPayment(pay.id, pay.amountDue) },
                                    colors = ButtonDefaults.buttonColors(containerColor = CoralAccent),
                                    modifier = Modifier.testTag("pay_simulate_btn_${pay.id}")
                                ) {
                                    Text("Pay Now", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // PDF RECEIPT MODAL GENERATOR COMPLETE WITH GRAPHICS WATERMARK AND BARCODE
        selectedReceipt?.let { rec ->
            Dialog(onDismissRequest = { selectedReceipt = null }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .border(BorderStroke(1.dp, EmeraldPrimary), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Header Seal
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("SMART TUITION HUB", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldPrimary)
                                Text("Official Payment Invoice", fontSize = 10.sp, color = Color.Gray)
                            }
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray)

                        // Meta details
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("ISSUED TO:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                Text(rec.studentName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                Text("Email: ${rec.studentId}", fontSize = 10.sp, color = Color.Gray)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("RECEIPT NO:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                Text("INV-2026-#00${rec.id}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                Text("Date: ${rec.paymentDate}", fontSize = 10.sp, color = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Ledger Table
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF1F5F9))
                                .padding(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("ITEM COURSE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                Text("TOTAL PAID", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(rec.courseName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Text("$${rec.amountPaid}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        Divider(color = Color.LightGray)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("TAX RECOVERY (0%):", fontSize = 11.sp, color = Color.Gray)
                            Text("$0.00", fontSize = 11.sp, color = Color.Gray)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("NET INVENT CAPITAL:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Text("$${rec.amountPaid}", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldPrimary)
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Custom Drawn Barcode using standard Canvas lines to represent proper document scanning compatibility!
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp)
                        ) {
                            val lineCount = 42
                            val lineSpacing = size.width / lineCount
                            for (i in 0 until lineCount) {
                                val thickness = if (i % 3 == 0) 6f else if (i % 2 == 0) 3f else 1.5f
                                drawLine(
                                    color = Color.DarkGray,
                                    start = Offset(i * lineSpacing, 0f),
                                    end = Offset(i * lineSpacing, size.height),
                                    strokeWidth = thickness
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("*SCAN BARCODE TO VERIFY SECURE COPIES*", fontSize = 9.sp, fontWeight = FontWeight.Medium, color = Color.Gray)

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            OutlinedButton(onClick = { selectedReceipt = null }) {
                                Text("Close", color = Color.Gray)
                            }
                            Button(
                                onClick = {
                                    viewModel.showToast("PDF generated successfully and queued inside local /Download/ directory!")
                                    selectedReceipt = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Simulate Share")
                            }
                        }
                    }
                }
            }
        }
    }
}


// ==========================================
// 17. STUDENT NOTICES LISTING VIEW
// ==========================================
@Composable
fun StudentNoticesView(viewModel: TuitionViewModel) {
    val notices by viewModel.allNotices.collectAsState()

    // Filter to target role = ALL or STUDENT
    val filteredNotices = notices.filter { it.targetRole == "ALL" || it.targetRole == "STUDENT" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Tuition Official Board News", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        if (filteredNotices.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No notices posted", color = Color.Gray)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredNotices) { notice ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(notice.date, fontSize = 11.sp, color = BlueAccent)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(BlueAccent.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("Broadcast", fontSize = 9.sp, color = BlueAccent, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(notice.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(notice.content, fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Sender: ${notice.senderName}", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}


// ==========================================
// 18. GENERAL PREFERENCES SETTINGS SCREEN
// ==========================================
@Composable
fun SettingsScreen(
    viewModel: TuitionViewModel,
    onLogout: () -> Unit = {}
) {
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val notificationEnabled by viewModel.notificationsEnabled.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var showLanguagePicker by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Profile Summary card
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(EmeraldPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (currentUser?.role) {
                                "ADMIN" -> Icons.Default.SupervisorAccount
                                "TEACHER" -> Icons.Default.School
                                else -> Icons.Default.Face
                            },
                            contentDescription = "Avatar",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(currentUser?.name ?: "Anonymous User", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(currentUser?.email ?: "guest@smart.com", fontSize = 12.sp, color = Color.Gray)
                        Text("Current role privilege: ${currentUser?.role}", fontSize = 11.sp, color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text("General Preferences", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        // Theme preference Row
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DarkMode, contentDescription = null, tint = EmeraldPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Dark Theme Mode", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Enables warm eye-safe color themes", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { viewModel.toggleTheme() },
                        colors = SwitchDefaults.colors(checkedThumbColor = EmeraldPrimary)
                    )
                }
            }
        }

        // Notification preferences Switch Row
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = EmeraldPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Push Reminders", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Notifies about schedules and bills", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Switch(
                        checked = notificationEnabled,
                        onCheckedChange = { viewModel.toggleNotifications() },
                        colors = SwitchDefaults.colors(checkedThumbColor = EmeraldPrimary)
                    )
                }
            }
        }

        // Language settings Row
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .clickable { showLanguagePicker = true },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Translate, contentDescription = null, tint = EmeraldPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Localization Language", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("App is set to $appLanguage", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Choose Language")
                }
            }
        }

        // Quick security info
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Simulation Database", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = EmeraldPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("The application is backed by a local SQLite Room compilation that stores mock entities, fee payments, and roster logs dynamically on your storage container for high-fidelity testing.", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }

        // Logout block
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.logout()
                    onLogout()
                },
                colors = ButtonDefaults.buttonColors(containerColor = CoralAccent),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("settings_logout_btn")
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Out of Session", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }

    // Language picker dropdown modal
    if (showLanguagePicker) {
        Dialog(onDismissRequest = { showLanguagePicker = false }) {
            Card(
                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Select App Language", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    listOf("English", "Español", "Français").forEach { lang ->
                        Card(
                            onClick = {
                                viewModel.setLanguage(lang)
                                showLanguagePicker = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (appLanguage == lang) EmeraldPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Text(
                                text = lang,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                fontWeight = if (appLanguage == lang) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = { showLanguagePicker = false }) {
                        Text("Dismiss")
                    }
                }
            }
        }
    }
}
