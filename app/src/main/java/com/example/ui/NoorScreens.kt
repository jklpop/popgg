@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.ui.theme.MyApplicationTheme
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.CardOverlayDark
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.SoftGold
import com.example.ui.theme.TextGray
import kotlinx.coroutines.launch

@Composable
fun NoorAppContent(viewModel: NoorViewModel) {
    val systemDark = isSystemInDarkTheme()
    val accent = viewModel.accentAccentColor

    MyApplicationTheme(
        darkTheme = viewModel.darkThemeEnabled,
        primaryAccent = accent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (viewModel.currentScreen) {
                "splash" -> SplashScreen(viewModel)
                "onboarding" -> OnboardingScreen(viewModel)
                "main" -> MainScaffold(viewModel)
            }
        }
    }
}

// ==========================================
// 1. SPLASH SCREEN (Cinematic & Spiritual)
// ==========================================
@Composable
fun SplashScreen(viewModel: NoorViewModel) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "pulse"
    )

    var startAnimation by remember { mutableStateOf(false) }
    val fadeAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(1500), label = "fade"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        kotlinx.coroutines.delay(3500) // 3.5s cinematic welcome
        viewModel.currentScreen = "onboarding"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                // Royal Islamic dark radial gradient background
                val colors = listOf(Color(0xFF0C1F35), Color(0xFF060F1A))
                drawRect(
                    brush = Brush.radialGradient(
                        colors = colors,
                        center = center,
                        radius = size.maxDimension / 2f
                    )
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Starry Particles Ambient Simulation
        Canvas(modifier = Modifier.fillMaxSize()) {
            for (i in 0..25) {
                val x = (i * 12345 % 100) / 100f * size.width
                val y = (i * 98765 % 100) / 100f * size.height
                val radius = (i % 3 + 1) * 1.5f
                drawCircle(
                    color = SoftGold.copy(alpha = 0.35f),
                    radius = radius,
                    center = Offset(x, y)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(24.dp)
                .scale(fadeAnim)
        ) {
            // Elegant glowing calligraphic crescent moon & star container
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(170.dp)
                    .scale(pulseScale)
            ) {
                // outer glow ring
                Spacer(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawBehind {
                            drawCircle(
                                color = EmeraldGreen.copy(alpha = 0.28f),
                                radius = size.minDimension / 2f,
                                style = Stroke(width = 8f)
                            )
                        }
                )

                // The Golden Emblem representing faith and guidance
                Icon(
                    imageVector = Icons.Default.Explore,
                    contentDescription = "Quran Light Logo",
                    tint = SoftGold,
                    modifier = Modifier.size(90.dp)
                )
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Faith Star",
                    tint = EmeraldGreen,
                    modifier = Modifier
                        .size(30.dp)
                        .offset(x = 24.dp, y = (-24).dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "نُـــور",
                style = MaterialTheme.typography.displayLarge.copy(
                    color = SoftGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 46.sp,
                    fontFamily = FontFamily.Serif
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = "NOOR ISLAMIC",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color.White.copy(alpha = 0.85f),
                    letterSpacing = 6.sp,
                    fontWeight = FontWeight.Light,
                    fontSize = 15.sp
                ),
                modifier = Modifier.padding(top = 8.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "بوابة الهدى والطمأنينة\nYour Oasis of Serbian Serenity",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextGray,
                    lineHeight = 22.sp,
                    fontStyle = FontStyle.Italic
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

// ==========================================
// 2. ONBOARDING (Modern Premium Setup)
// ==========================================
@Composable
fun OnboardingScreen(viewModel: NoorViewModel) {
    val languages = listOf(
        "ar" to "العربية",
        "en" to "English",
        "fr" to "Français",
        "tr" to "Türkçe",
        "ur" to "اردو"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                val darkGrad = Brush.verticalGradient(
                    colors = listOf(Color(0xFF091E30), Color(0xFF060F17))
                )
                drawRect(brush = darkGrad)
            }
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            // Header Info
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(EmeraldGreen.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (viewModel.onboardingStep) {
                        1 -> Icons.Default.Translate
                        2 -> Icons.Default.WbSunny
                        else -> Icons.Default.CheckCircle
                    },
                    contentDescription = "Onboarding Info",
                    tint = SoftGold,
                    modifier = Modifier.size(45.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (viewModel.onboardingStep) {
                1 -> {
                    Text(
                        text = "اختر لغة التطبيق Preferred Language",
                        style = MaterialTheme.typography.headlineMedium.copy(color = Color.White, fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "اختر اللغة لمواءمة تلاوات القرآن والأذكار.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextGray),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(languages) { langItem ->
                            val isSelected = viewModel.selectedLanguage == langItem.first
                            Card(
                                onClick = { viewModel.selectedLanguage = langItem.first },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) EmeraldGreen else CardOverlayDark
                                ),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = if (isSelected) SoftGold else Color.White.copy(alpha = 0.1f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(18.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = langItem.second,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "selected",
                                            tint = SoftGold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    Text(
                        text = "حدد هدفك اليومي Daily Goal",
                        style = MaterialTheme.typography.headlineMedium.copy(color = Color.White, fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "كم عدد المرات التي تفضل قراءة القرآن أو الذكر فيها؟",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextGray),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    val goals = listOf("5 آيات يومياً (5 Verses)", "10 آيات يومياً (10 Verses)", "ورد كامل (1 Hizb)", "حافظ الأذكار فقط (Azkar Only)")
                    var selectedGoalIndex by remember { mutableStateOf(0) }

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        itemsIndexed(goals) { index, goal ->
                            val isSelected = selectedGoalIndex == index
                            Card(
                                onClick = { selectedGoalIndex = index },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) EmeraldGreen else CardOverlayDark
                                ),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = if (isSelected) SoftGold else Color.White.copy(alpha = 0.1f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(18.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = goal,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "selected",
                                            tint = SoftGold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                3 -> {
                    Text(
                        text = "طابع الهوية البصرية Accent Palette",
                        style = MaterialTheme.typography.headlineMedium.copy(color = Color.White, fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "اختر اللون الأساسي لواجهة استخدامك الفاخرة.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextGray),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(28.dp))

                    val palettes = listOf(
                        Triple(EmeraldGreen, "الزمردي الفاخر", "Emerald Green"),
                        Triple(Color(0xFF0B1F33), "أزرق الأثير", "Midnight Blue"),
                        Triple(SoftGold, "الذهبي الروحي", "Soft Gold")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        palettes.forEach { item ->
                            val isSelected = viewModel.accentAccentColor == item.first
                            Card(
                                onClick = { viewModel.accentAccentColor = item.first },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(130.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = CardOverlayDark),
                                border = BorderStroke(
                                    width = 2.dp,
                                    color = if (isSelected) SoftGold else Color.Transparent
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(item.first, CircleShape)
                                            .border(1.dp, Color.White, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = item.second,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        ),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (viewModel.onboardingStep > 1) {
                    TextButton(onClick = { viewModel.onboardingStep-- }) {
                        Text(
                            text = "السابق Back",
                            style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(48.dp))
                }

                Button(
                    onClick = {
                        if (viewModel.onboardingStep < 3) {
                            viewModel.onboardingStep++
                        } else {
                            viewModel.currentScreen = "main"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SoftGold),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("onboarding_continue_btn")
                ) {
                    Text(
                        text = if (viewModel.onboardingStep < 3) "التالي Continue" else "ابدأ الآن Begin",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color(0xFF040C12),
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}

// ==========================================
// 3. MAIN SCAFFOLD & NAV BAR
// ==========================================
@Composable
fun MainScaffold(viewModel: NoorViewModel) {
    Scaffold(
        bottomBar = {
            ModernBottomNavBar(viewModel)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (viewModel.activeTab) {
                "home" -> HomeScreen(viewModel)
                "quran" -> QuranScreen(viewModel)
                "azkar" -> AzkarScreen(viewModel)
                "qibla" -> QiblaScreen(viewModel)
                "progress" -> ProgressScreen(viewModel)
                "settings" -> SettingsScreen(viewModel)
            }
        }
    }
}

@Composable
fun ModernBottomNavBar(viewModel: NoorViewModel) {
    val items = listOf(
        Triple("home", Icons.Default.Home, "الرئيسية"),
        Triple("quran", Icons.Default.Book, "القرآن"),
        Triple("azkar", Icons.Default.Favorite, "الأذكار"),
        Triple("qibla", Icons.Default.Explore, "القبلة"),
        Triple("progress", Icons.Default.Star, "المتابعة"),
        Triple("settings", Icons.Default.Settings, "الإعدادات")
    )

    Surface(
        tonalElevation = 10.dp,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { barItem ->
                val isSelected = viewModel.activeTab == barItem.first
                val activeBg = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(activeBg)
                        .clickable(onClick = { viewModel.activeTab = barItem.first })
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("nav_tab_${barItem.first}")
                ) {
                    Icon(
                        imageVector = barItem.second,
                        contentDescription = barItem.third,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else TextGray,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = barItem.third,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else TextGray
                        )
                    )
                }
            }
        }
    }
}

// ==========================================
// 4. HOME SCREEN (Islamic Dashboard)
// ==========================================
@Composable
fun HomeScreen(viewModel: NoorViewModel) {
    var aiChatModalVisible by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Global Level Header Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        val city = viewModel.cities[viewModel.selectedCityIndex]
                        Text(
                            text = "السلام عليكم ورحمة الله",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = EmeraldGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${city.nameArabic} (${city.nameEnglish})",
                                style = MaterialTheme.typography.bodyMedium.copy(color = TextGray)
                            )
                        }
                    }

                    // Level/Points Badge
                    val currentProgress = viewModel.userProgress.collectAsState().value ?: UserProgress()
                    Row(
                        modifier = Modifier
                            .background(EmeraldGreen.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .border(1.dp, SoftGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Points",
                            tint = SoftGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${currentProgress.points} Pts",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = SoftGold,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            // Countdown Hero Panel (Glassmorphism Styled card)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (viewModel.darkThemeEnabled) CardOverlayDark else MaterialTheme.colorScheme.primaryContainer
                    ),
                    border = BorderStroke(1.dp, SoftGold.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val hijriStr = "١٢ ذو الحجة ١٤٤٧ هـ"
                        Text(
                            text = hijriStr,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = SoftGold,
                                fontWeight = FontWeight.SemiBold
                            ),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Translating Next Prayer correctly based on state
                        val prayerArabic = when (viewModel.nextPrayerName.lowercase()) {
                            "fajr" -> "الفجر"
                            "sunrise" -> "الشروق"
                            "dhuhr" -> "الظهر"
                            "asr" -> "العصر"
                            "maghrib" -> "المغرب"
                            "isha" -> "العشاء"
                            else -> "الصلاة"
                        }

                        Text(
                            text = "الصلاة القادمة: $prayerArabic",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold
                            ),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = viewModel.nextPrayerCountdown,
                            style = MaterialTheme.typography.displayLarge.copy(
                                color = EmeraldGreen,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 42.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.testTag("prayer_timer_countdown")
                        )

                        Text(
                            text = "الوقت المتبقي للأذان",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextGray),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Prayer Timing Checklist Cards
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "مواقيت صلاة اليوم وتتبع الالتزام",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    viewModel.prayerTimingsList.forEach { timing ->
                        val lower = timing.nameEnglish.lowercase()
                        val isChecked = when (lower) {
                            "fajr" -> viewModel.todayPrayerTrack.fajr
                            "dhuhr" -> viewModel.todayPrayerTrack.dhuhr
                            "asr" -> viewModel.todayPrayerTrack.asr
                            "maghrib" -> viewModel.todayPrayerTrack.maghrib
                            "isha" -> viewModel.todayPrayerTrack.isha
                            else -> false
                        }

                        // Sunrise isn't a mandatory prayer to check, but can show
                        val isSunrise = lower == "sunrise"

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag(timing.testTag),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isChecked) EmeraldGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (isChecked) EmeraldGreen else Color.Transparent
                             )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = when (lower) {
                                            "fajr", "sunrise" -> Icons.Default.WbSunny
                                            "dhuhr" -> Icons.Default.WbCloudy
                                            "asr" -> Icons.Default.WbSunny
                                            "maghrib" -> Icons.Default.NightsStay
                                            else -> Icons.Default.Bedtime
                                        },
                                        contentDescription = "Prayer Icon",
                                        tint = if (isChecked) EmeraldGreen else SoftGold,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = timing.nameArabic,
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 17.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                        Text(
                                            text = timing.nameEnglish,
                                            style = MaterialTheme.typography.bodyMedium.copy(color = TextGray)
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = timing.timeString,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 18.sp
                                        ),
                                        modifier = Modifier.padding(end = 12.dp)
                                    )

                                    if (!isSunrise) {
                                        IconButton(
                                            onClick = { viewModel.togglePrayerTracked(lower) },
                                            modifier = Modifier.testTag("check_prayer_${lower}")
                                        ) {
                                            Icon(
                                                imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Default.Circle,
                                                contentDescription = "Track Prayer",
                                                tint = if (isChecked) EmeraldGreen else TextGray,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Daily Content (Ayah, Hadith, Dua cards)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "الغذاء الروحي لليوم",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    // Ayah Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Badge(containerColor = SoftGold) {
                                    Text(
                                        "آية اليوم Ayah",
                                        modifier = Modifier.padding(4.dp),
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    tint = TextGray,
                                    modifier = Modifier.clickable { /* Share visual card */ }
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = viewModel.ayahOfTheDayArabic,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    color = EmeraldGreen,
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily.Serif
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = viewModel.ayahOfTheDayTranslation,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = viewModel.ayahOfTheDayRef,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextGray,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    // Hadith Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "حديث اليوم Hadith",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = SoftGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = viewModel.hadithOfTheDayArabic,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = viewModel.hadithOfTheDayTranslation,
                                style = MaterialTheme.typography.bodyMedium.copy(color = TextGray)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "— ${viewModel.hadithOfTheDayRef}",
                                style = MaterialTheme.typography.labelLarge.copy(color = EmeraldGreen)
                            )
                        }
                    }

                    // Dua Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "دعاء اليوم Dua",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = EmeraldGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = viewModel.duaOfTheDayArabic,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = viewModel.duaOfTheDayTranslation,
                                style = MaterialTheme.typography.bodyMedium.copy(color = TextGray)
                            )
                        }
                    }
                }
            }

            // Quick Actions List
            item {
                Column {
                    Text(
                        text = "روابط سريعة",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val items = listOf(
                            Triple("quran", Icons.Default.Book, "القرآن"),
                            Triple("azkar", Icons.Default.Favorite, "الأذكار"),
                            Triple("qibla", Icons.Default.Explore, "القبلة")
                        )
                        items.forEach { action ->
                            Card(
                                onClick = { viewModel.activeTab = action.first },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = action.second,
                                        contentDescription = action.third,
                                        tint = EmeraldGreen,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = action.third,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }
        }

        // FLOATING CHAT BUTTON FOR THE AI ASSISTANT
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp, end = 16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick = { aiChatModalVisible = true },
                containerColor = SoftGold,
                contentColor = Color.Black,
                shape = CircleShape,
                modifier = Modifier
                    .size(60.dp)
                    .testTag("ai_chat_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = "AI Assistant",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }

    if (aiChatModalVisible) {
        AiChatModal(viewModel, onDismiss = { aiChatModalVisible = false })
    }
}

// ==========================================
// 5. QURAN SCREEN (Complete Standalone Quran)
// ==========================================
@Composable
fun QuranScreen(viewModel: NoorViewModel) {
    val activeSurah = viewModel.selectedSurah

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (activeSurah == null) {
            // INDEX LIST VIEW
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "القرآن الكريم",
                    style = MaterialTheme.typography.displayLarge.copy(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    )
                )

                // Showing quick Bookmarks status
                val bookmarks = viewModel.allBookmarks.collectAsState(emptyList()).value
                Text(
                    text = "${bookmarks.size} إشارة مرجعية",
                    style = MaterialTheme.typography.bodyMedium.copy(color = SoftGold, fontWeight = FontWeight.Bold)
                )
            }

            // Tab Selector: All Surahs vs Offline Downloaded
            var selectedTab by remember { mutableStateOf(0) } // 0: All, 1: Downloaded
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val tabs = listOf("جميع السور (114)", "المحملة أوفلاين")
                tabs.forEachIndexed { index, text ->
                    val isSelected = selectedTab == index
                    val bg = if (isSelected) EmeraldGreen else Color.Transparent
                    val tc = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(bg)
                            .clickable { selectedTab = index }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = tc
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // "Continue Reading" Premium Card
            if (viewModel.lastReadSurahId > 0) {
                val lastSurah = QuranData.surahsList.find { it.id == viewModel.lastReadSurahId }
                if (lastSurah != null) {
                    Card(
                        onClick = { viewModel.selectActiveSurah(lastSurah) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .testTag("continue_reading_card"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = EmeraldGreen.copy(alpha = 0.12f)),
                        border = BorderStroke(1.dp, SoftGold.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(SoftGold.copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bookmark,
                                        contentDescription = "Last Read",
                                        tint = SoftGold,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "متابعة القراءة • Continue Reading",
                                        style = MaterialTheme.typography.labelMedium.copy(color = SoftGold, fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "سورة ${lastSurah.nameArabic} (${lastSurah.nameEnglish}) • آية ${viewModel.lastReadAyahNumber}",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Open",
                                tint = EmeraldGreen
                            )
                        }
                    }
                }
            }

            // Search Bar
            OutlinedTextField(
                value = viewModel.quranSearchQuery,
                onValueChange = { viewModel.quranSearchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("quran_search_field"),
                placeholder = { Text("البحث في السور... Search Surahs...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (viewModel.quranSearchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.quranSearchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Surahs index list filter by download tab + search queries
            val downloadedList = viewModel.allDownloadedSurahs.collectAsState(emptyList()).value
            val filteredSurahs = QuranData.surahsList.filter { surah ->
                val matchesSearch = surah.nameEnglish.lowercase().contains(viewModel.quranSearchQuery.lowercase()) ||
                        surah.nameArabic.contains(viewModel.quranSearchQuery)
                val matchesTab = if (selectedTab == 1) {
                    downloadedList.any { it.surahId == surah.id }
                } else true
                matchesSearch && matchesTab
            }

            if (filteredSurahs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = "No Surahs Found",
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (selectedTab == 1) "لم تقم بتحميل أي سُورة بعد للعمل أوفلاين." else "لم نعثر على نتائج مطابقة للبحث",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredSurahs) { surah ->
                        val isDownloaded = downloadedList.any { it.surahId == surah.id }
                        val isDownloading = viewModel.downloadingSurahIds.contains(surah.id)

                        Card(
                            onClick = { viewModel.selectActiveSurah(surah) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("surah_card_${surah.id}"),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Surah Number Circular Box
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(EmeraldGreen.copy(alpha = 0.15f), CircleShape)
                                            .border(1.5.dp, SoftGold, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${surah.id}",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = EmeraldGreen,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = surah.nameEnglish,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            )
                                            if (isDownloaded) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .background(SoftGold.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = "OFFLINE",
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = SoftGold
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = "${surah.versesCount} آية • ${if (surah.type == "Meccan") "مكية" else "مدنية"}",
                                            style = MaterialTheme.typography.bodyMedium.copy(color = TextGray)
                                        )
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Custom Download trigger icon
                                    if (isDownloading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = EmeraldGreen,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        if (isDownloaded) {
                                            IconButton(onClick = { viewModel.deleteDownloadedSurah(surah.id) }) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = "Completed Offline",
                                                    tint = EmeraldGreen,
                                                    modifier = Modifier.size(22.dp)
                                                )
                                            }
                                        } else {
                                            IconButton(onClick = { viewModel.downloadSurah(surah) }) {
                                                Icon(
                                                    imageVector = Icons.Default.ArrowDownward,
                                                    contentDescription = "Download Surah Offline",
                                                    tint = TextGray,
                                                    modifier = Modifier.size(22.dp)
                                                )
                                            }
                                        }
                                    }

                                    Text(
                                        text = surah.nameArabic,
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            color = EmeraldGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(30.dp)) }
                }
            }
        } else {
            // DETAILED SURAH READER VIEW
            // Top action bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .statusBarsPadding()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { 
                    viewModel.stopQuranAudioPlayer()
                    viewModel.selectedSurah = null 
                }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Index", tint = EmeraldGreen)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = activeSurah.nameArabic,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${activeSurah.nameEnglish} • ${activeSurah.versesCount} آية",
                        style = MaterialTheme.typography.labelLarge.copy(color = TextGray)
                    )
                }

                // Interactive header controls: Switch translation, Switch Tafsir
                Row {
                    IconButton(onClick = { viewModel.quranTranslationVisible = !viewModel.quranTranslationVisible }) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = "Toggle Translation",
                            tint = if (viewModel.quranTranslationVisible) EmeraldGreen else TextGray
                        )
                    }
                    IconButton(onClick = { viewModel.quranTafsirVisible = !viewModel.quranTafsirVisible }) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "Toggle Tafsir",
                            tint = if (viewModel.quranTafsirVisible) SoftGold else TextGray
                        )
                    }
                }
            }

            // Warnings, fallback errors
            if (viewModel.quranErrorMsg != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SoftGold.copy(alpha = 0.15f))
                ) {
                    Text(
                        text = viewModel.quranErrorMsg!!,
                        style = MaterialTheme.typography.bodyMedium.copy(color = SoftGold, fontWeight = FontWeight.Bold),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Audio Player Control panel
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldGreen.copy(alpha = 0.1f)),
                border = BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.toggleQuranAudio() },
                            modifier = Modifier.testTag("play_audio_btn")
                        ) {
                            Icon(
                                imageVector = if (viewModel.isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play Audio",
                                tint = EmeraldGreen,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = if (viewModel.isAudioPlaying) "التلاوة مجودة تلقائياً..." else "استمع لتلاوة السورة المباركة",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = EmeraldGreen)
                            )
                            Text(
                                text = "القارئ: ${viewModel.selectedReciter}",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextGray)
                            )
                        }
                    }

                    // Reciter Choice Dropdown Picker
                    var reciterMenuExpanded by remember { mutableStateOf(false) }
                    Box {
                        Button(
                            onClick = { reciterMenuExpanded = true },
                            colors = ButtonDefaults.buttonColors(containerColor = SoftGold),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(text = "تغيير القارئ", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        DropdownMenu(
                            expanded = reciterMenuExpanded,
                            onDismissRequest = { reciterMenuExpanded = false }
                        ) {
                            val reciters = listOf("Mishary Al-Afasy", "Abdul Basit", "Al-Ghamdi")
                            reciters.forEach { name ->
                                DropdownMenuItem(
                                    text = { Text(name) },
                                    onClick = {
                                        viewModel.selectedReciter = name
                                        reciterMenuExpanded = false
                                        // Restart playback if was playing
                                        if (viewModel.isAudioPlaying) {
                                            viewModel.stopQuranAudioPlayer()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Loading screen or verses list
            if (viewModel.isQuranLoading && viewModel.currentSurahVerses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = EmeraldGreen)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "جاري تحميل الآيات العطرة من الخادم...",
                            style = MaterialTheme.typography.bodyMedium.copy(color = EmeraldGreen, fontWeight = FontWeight.Bold)
                        )
                    }
                }
            } else {
                // SMART PAGINATION: If Surah is long (total ayahs > 30), render in pages to prevent Compose Lagrange freezes
                val pageSize = 30
                val versesList = viewModel.currentSurahVerses
                val totalVerses = versesList.size
                val pageCount = if (totalVerses > 0) (totalVerses + pageSize - 1) / pageSize else 1
                var currentPageIndex by remember(activeSurah.id) { mutableStateOf(0) }

                if (pageCount > 1) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(end = 12.dp)
                    ) {
                        items(count = pageCount) { p ->
                            val from = p * pageSize + 1
                            val to = kotlin.math.min((p + 1) * pageSize, totalVerses)
                            val isSelected = p == currentPageIndex

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(30.dp))
                                    .background(if (isSelected) EmeraldGreen else MaterialTheme.colorScheme.surface)
                                    .border(1.dp, if (isSelected) SoftGold else Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(30.dp))
                                    .clickable { currentPageIndex = p }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "الآيات $from - $to",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                val startIndex = currentPageIndex * pageSize
                val endIndex = kotlin.math.min(startIndex + pageSize, totalVerses)
                val pagedVerses = if (versesList.isNotEmpty() && startIndex < totalVerses) {
                    versesList.subList(startIndex, endIndex)
                } else {
                    versesList
                }

                // Verses list display
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Prepend Bismillah except for At-Tawbah (9)
                    if (activeSurah.id != 1 && activeSurah.id != 9 && currentPageIndex == 0) {
                        item {
                            Text(
                                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    color = EmeraldGreen,
                                    fontSize = 22.sp,
                                    fontFamily = FontFamily.Serif
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    itemsIndexed(pagedVerses) { index, verse ->
                        val isCurrentlyPlaying = viewModel.isAudioPlaying && (viewModel.activePlayingAyahNumber == verse.number)
                        val isHighlighted = viewModel.isAyahHighlighted(activeSurah.id, verse.number)
                        val bookmarked = viewModel.isBookmarkedLocally(activeSurah.id, verse.number)

                        // Compose background card based on playing and custom highlighted states
                        val cardBg = if (isCurrentlyPlaying) {
                            EmeraldGreen.copy(alpha = 0.15f)
                        } else if (isHighlighted) {
                            SoftGold.copy(alpha = 0.12f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        }

                        val strokeBorder = if (isCurrentlyPlaying) {
                            BorderStroke(1.dp, SoftGold)
                        } else if (isHighlighted) {
                            BorderStroke(1.dp, SoftGold.copy(alpha = 0.4f))
                        } else {
                            BorderStroke(0.dp, Color.Transparent)
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ayah_card_${verse.number}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = cardBg),
                            border = strokeBorder
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                // Top status bar of the Verse Card: Number badge and actions
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Circular Verse number badge
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(EmeraldGreen.copy(alpha = 0.15f), CircleShape)
                                            .border(1.dp, SoftGold, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${verse.number}",
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                color = EmeraldGreen,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        )
                                    }

                                    // Action icons (Play single, Highlight brush, Bookmark, edit notes)
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        // Play single audio
                                        IconButton(onClick = { 
                                            if (isCurrentlyPlaying) {
                                                viewModel.stopQuranAudioPlayer()
                                            } else {
                                                viewModel.playAyahAudio(verse)
                                            }
                                        }) {
                                            Icon(
                                                imageVector = if (isCurrentlyPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                                                contentDescription = "Read Verse Audio",
                                                tint = if (isCurrentlyPlaying) SoftGold else EmeraldGreen,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        // Toggle Highlight brush
                                        IconButton(onClick = { viewModel.toggleAyahHighlight(activeSurah.id, verse.number) }) {
                                            Icon(
                                                imageVector = Icons.Default.Brush,
                                                contentDescription = "Highlight Verse",
                                                tint = if (isHighlighted) SoftGold else TextGray,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        // Bookmark
                                        IconButton(onClick = { viewModel.bookmarkAyah(verse.number) }) {
                                            Icon(
                                                imageVector = if (bookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                                contentDescription = "Bookmark",
                                                tint = if (bookmarked) SoftGold else TextGray,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        // Show note editor field helper
                                        var localShowNoteEditor by remember { mutableStateOf(false) }
                                        IconButton(onClick = { localShowNoteEditor = !localShowNoteEditor }) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Edit Notes",
                                                tint = if (localShowNoteEditor) EmeraldGreen else TextGray,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }

                                // Arabic text (Premium readable size)
                                Text(
                                    text = verse.textArabic,
                                    style = MaterialTheme.typography.displayLarge.copy(
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 23.sp,
                                        lineHeight = 42.sp,
                                        fontFamily = FontFamily.Serif
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp)
                                        .clickable {
                                            // Clicking the Arabic text saves it as last read position!
                                            viewModel.saveLastReadPosition(activeSurah.id, activeSurah.nameEnglish, verse.number)
                                        },
                                    textAlign = TextAlign.Right
                                )

                                // Custom Tafsir Frame
                                if (viewModel.quranTafsirVisible && verse.tafsir.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(EmeraldGreen.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                                            .border(1.dp, EmeraldGreen.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                                            .padding(10.dp)
                                    ) {
                                        Text(
                                            text = "التفسير المبسط (الجلالين):",
                                            style = MaterialTheme.typography.labelMedium.copy(color = SoftGold, fontWeight = FontWeight.Bold)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = verse.tafsir,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                                                lineHeight = 22.sp
                                            ),
                                            textAlign = TextAlign.Right,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }

                                // Interactive Translation text Display
                                if (viewModel.quranTranslationVisible) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    val transText = when (viewModel.selectedLanguage) {
                                        "en" -> verse.textEnglish
                                        "fr" -> verse.textFrench
                                        "tr" -> verse.textTurkish
                                        "ur" -> verse.textUrdu
                                        else -> verse.textEnglish
                                    }
                                    Text(
                                        text = if (transText.isNotBlank()) transText else verse.textEnglish,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.82f),
                                            lineHeight = 22.sp
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                // Persisted Written Notes segment
                                val existingNotes = viewModel.allNotes.collectAsState(emptyList()).value.filter {
                                    it.surahNumber == activeSurah.id && it.ayahNumber == verse.number
                                }

                                if (existingNotes.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(SoftGold.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                                            .padding(8.dp)
                                    ) {
                                        Text(
                                            text = "ملاحظاتي الشخصية My Notes:",
                                            style = MaterialTheme.typography.labelSmall.copy(color = SoftGold, fontWeight = FontWeight.Bold)
                                        )
                                        existingNotes.forEach { note ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = note.noteText,
                                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface),
                                                    modifier = Modifier.weight(1f)
                                                )
                                                IconButton(
                                                    onClick = { viewModel.deleteNote(note.id) },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = "Delete Note",
                                                        tint = Color.Red.copy(alpha = 0.7f),
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // Interactive inline notes writing text field
                                var localNoteText by remember { mutableStateOf("") }
                                var localShowNoteEditor by remember { mutableStateOf(false) }
                                
                                if (localShowNoteEditor) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = localNoteText,
                                        onValueChange = { localNoteText = it },
                                        modifier = Modifier.fillMaxWidth(),
                                        placeholder = { Text("اكتب تدبرًا أو خاطرة للآية الكريمة...") },
                                        trailingIcon = {
                                            IconButton(onClick = {
                                                if (localNoteText.isNotBlank()) {
                                                    viewModel.addNoteToAyah(verse.number, localNoteText)
                                                    localNoteText = ""
                                                    localShowNoteEditor = false
                                                }
                                            }) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                                    contentDescription = "Save Notes",
                                                    tint = EmeraldGreen,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        },
                                        singleLine = true,
                                        textStyle = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(30.dp)) }
                }
            }
        }
    }
}

// ==========================================
// 6. AZKAR SCREEN (With Electronic Sibha)
// ==========================================
@Composable
fun AzkarScreen(viewModel: NoorViewModel) {
    var isSibhaTabSelected by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isSibhaTabSelected) "السبحة الإلكترونية" else "الأذكار اليومية",
                style = MaterialTheme.typography.displayLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
            )

            // Dynamic Accent Swapper Tab
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                TabButton(
                    text = "الأذكار",
                    isSelected = !isSibhaTabSelected,
                    onClick = { isSibhaTabSelected = false }
                )
                TabButton(
                    text = "السبحة",
                    isSelected = isSibhaTabSelected,
                    onClick = { isSibhaTabSelected = true }
                )
            }
        }

        if (!isSibhaTabSelected) {
            // AZKAR MULTI CATEGORIES LIST
            val categories = AzkarData.categories

            ScrollableTabRow(
                selectedTabIndex = viewModel.activeCategoryIndex,
                edgePadding = 16.dp,
                containerColor = Color.Transparent,
                contentColor = EmeraldGreen
            ) {
                categories.forEachIndexed { idx, cat ->
                    Tab(
                        selected = viewModel.activeCategoryIndex == idx,
                        onClick = { viewModel.activeCategoryIndex = idx },
                        text = {
                            Text(
                                text = cat.nameArabic,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (viewModel.activeCategoryIndex == idx) EmeraldGreen else TextGray
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action: Reset counts of state
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = { viewModel.resetZikrProgress() },
                    colors = ButtonDefaults.textButtonColors(contentColor = SoftGold)
                ) {
                    Text(
                        "إعادة تهيئة العدادات Reset Counts",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            val activeCat = categories[viewModel.activeCategoryIndex]

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(activeCat.items) { zikr ->
                    val remaining = viewModel.getRemainingAzkarCount(zikr)
                    val isDone = remaining == 0

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.decrementZikrCount(zikr) },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDone) EmeraldGreen.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isDone) EmeraldGreen else Color.Transparent
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            Text(
                                text = zikr.arabicText,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 19.sp,
                                    lineHeight = 32.sp,
                                    textAlign = TextAlign.Right,
                                    fontFamily = FontFamily.Serif
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = zikr.translation,
                                style = MaterialTheme.typography.bodyMedium.copy(color = TextGray)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = zikr.description,
                                    style = MaterialTheme.typography.labelLarge.copy(color = SoftGold, fontSize = 11.sp),
                                    modifier = Modifier.weight(1.5f)
                                )

                                // Count Box
                                Box(
                                    modifier = Modifier
                                        .background(if (isDone) EmeraldGreen else SoftGold, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = if (isDone) "تم التكرار" else "المتبقي: $remaining",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color.Black,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(30.dp)) }
            }
        } else {
            // SIBHA ELECTRONIC WIDGET - DE LUXE MODERN SMART TASBIH
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // 1. HORIZONTAL SELECTED DHIKR CHIPS
                Text(
                    text = "اختر الذكر المفضل للورد",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextGray),
                    modifier = Modifier.align(Alignment.End)
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    viewModel.tasbihDhikrs.forEachIndexed { index, dhikr ->
                        val isSelected = viewModel.selectedDhikrIndex == index
                        val borderCol = if (isSelected) EmeraldGreen else Color.Transparent
                        val bgCol = if (isSelected) EmeraldGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                        val textCol = if (isSelected) SoftGold else TextGray
                        
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(bgCol)
                                .border(1.5.dp, borderCol, RoundedCornerShape(12.dp))
                                .clickable { viewModel.selectDhikr(index) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .testTag("tasbih_dhikr_chip_$index")
                        ) {
                            Text(
                                text = dhikr,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = textCol
                                )
                            )
                        }
                    }
                }

                // 2. THE GRAND GLOWING DIAL & CIRCULAR PROGRESS RING
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(250.dp)
                ) {
                    // Touch feedback scale state
                    var activeScaleState by remember { mutableStateOf(1.0f) }
                    val scale by animateFloatAsState(targetValue = activeScaleState, animationSpec = spring(stiffness = Spring.StiffnessLow))
                    
                    // Golden and Emerald soft radial glow backgrounds
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(scale)
                            .drawBehind {
                                val brush = Brush.radialGradient(
                                    colors = listOf(
                                        EmeraldGreen.copy(alpha = 0.18f),
                                        Color.Transparent
                                    ),
                                    center = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f),
                                    radius = size.minDimension / 1.5f
                                )
                                drawCircle(brush = brush, radius = size.minDimension / 2f)
                            }
                    )

                    // Deluxe Glowing Circular Progress Ring
                    val progressFraction = if (viewModel.tasbihDailyGoal > 0) {
                        (viewModel.tasbihDailyCount.toFloat() / viewModel.tasbihDailyGoal.toFloat()).coerceIn(0.0f, 1.0f)
                    } else 0.0f
                    
                    val ringTrackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.15f)
                    val ringAccentGlowColor = SoftGold.copy(alpha = 0.25f)
                    val ringProgressColor = EmeraldGreen

                    Canvas(modifier = Modifier.size(220.dp)) {
                        val strokeWidth = 12.dp.toPx()
                        
                        // Background full track ring
                        drawCircle(
                            color = ringTrackColor,
                            style = Stroke(width = strokeWidth)
                        )
                        
                        // Glowing accent rings
                        drawArc(
                            color = ringAccentGlowColor,
                            startAngle = -90f,
                            sweepAngle = 360f * progressFraction,
                            useCenter = false,
                            style = Stroke(width = strokeWidth + 4.dp.toPx(), cap = StrokeCap.Round)
                        )
                        
                        drawArc(
                            color = ringProgressColor,
                            startAngle = -90f,
                            sweepAngle = 360f * progressFraction,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    // Interactive dial center
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(175.dp)
                            .clip(CircleShape)
                            .scale(scale)
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                                        MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
                                    )
                                )
                            )
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        activeScaleState = 0.94f
                                        viewModel.incrementTasbih()
                                        try {
                                            tryAwaitRelease()
                                        } catch (_: Exception) {}
                                        activeScaleState = 1.0f
                                    }
                                )
                            }
                            .testTag("tasbih_great_dial")
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "${viewModel.tasbihSessionCount}",
                                style = MaterialTheme.typography.displayLarge.copy(
                                    color = SoftGold,
                                    fontSize = 58.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.Serif
                                )
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text(
                                text = viewModel.tasbihDhikrs.getOrNull(viewModel.selectedDhikrIndex) ?: "سبحان الله",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = EmeraldGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            )
                            
                            Spacer(modifier = Modifier.height(6.dp))
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(12.dp), tint = SoftGold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "مجموعة: ${viewModel.tasbihRoundTarget}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = TextGray)
                                )
                            }
                        }
                    }
                }

                // 3. CORE CONTROL ACTION ROW (Undo, Reset, Targets)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Undo action (تراجع)
                    IconButton(
                        onClick = { viewModel.undoLastTasbih() },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface, CircleShape)
                            .size(46.dp)
                            .testTag("tasbih_undo_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Undo",
                            tint = SoftGold
                        )
                    }

                    // Session group target selector
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(4.dp)
                    ) {
                        listOf(33, 99, 100, 0).forEach { target ->
                            val isSelected = viewModel.tasbihRoundTarget == target
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) EmeraldGreen else Color.Transparent)
                                    .clickable { 
                                        viewModel.tasbihRoundTarget = target
                                        viewModel.saveTasbihStateToPrefs()
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (target == 0) "∞" else "$target",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else TextGray
                                )
                            }
                        }
                    }

                    // Reset action (تصفير)
                    IconButton(
                        onClick = { viewModel.resetTasbihCount() },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface, CircleShape)
                            .size(46.dp)
                            .testTag("tasbih_reset_only")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset",
                            tint = Color.Red.copy(alpha = 0.8f)
                        )
                    }
                }

                // 4. THE GLASSMORPHIC DAILY TARGET CARD
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.TrendingUp, contentDescription = null, tint = EmeraldGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "الهدف اليومي للتسبيحات",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            
                            // Adjust goal button
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                TextButton(
                                    onClick = { viewModel.updateDailyGoal((viewModel.tasbihDailyGoal - 50).coerceAtLeast(50)) },
                                    colors = ButtonDefaults.textButtonColors(contentColor = SoftGold)
                                ) {
                                    Text("-٥٠", fontWeight = FontWeight.Bold)
                                }
                                Text(
                                    text = "${viewModel.tasbihDailyGoal}",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SoftGold
                                )
                                TextButton(
                                    onClick = { viewModel.updateDailyGoal(viewModel.tasbihDailyGoal + 50) },
                                    colors = ButtonDefaults.textButtonColors(contentColor = SoftGold)
                                ) {
                                    Text("+٥٠", fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Linear Progress
                        val ratio = if (viewModel.tasbihDailyGoal > 0) {
                            (viewModel.tasbihDailyCount.toFloat() / viewModel.tasbihDailyGoal.toFloat()).coerceIn(0.0f, 1.0f)
                        } else 0.0f
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(ratio)
                                    .background(
                                        Brush.horizontalGradient(listOf(EmeraldGreen, SoftGold))
                                    )
                            )
                        }

                        Text(
                            text = "أنجزت اليوم ${viewModel.tasbihDailyCount} تسبيحة من هدفك البالغ ${viewModel.tasbihDailyGoal}.",
                            fontSize = 12.sp,
                            color = TextGray,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // 5. ACHIEVEMENTS & BADGES SYSTEM
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "الأوسمة والإنجازات المستحقة",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.align(Alignment.End)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val achievementsDef = listOf(
                            Triple("starter", "مسبّح مبتدئ", "أتمم أول ١٠٠ تسبيحة"),
                            Triple("constant", "حارس الأذكار", "حقق هدفك اليومي"),
                            Triple("golden", "التاج الذهبي", "أنجز ١٠٠٠ تسبيحة كبرى")
                        )

                        achievementsDef.forEach { item ->
                            val isUnlocked = viewModel.unlockedAchievements.contains(item.first)
                            val tintColor = if (isUnlocked) SoftGold else Color.Gray.copy(alpha = 0.4f)
                            val bgCol = if (isUnlocked) EmeraldGreen.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
                            
                            Card(
                                modifier = Modifier.weight(1.0f),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = bgCol)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = if (item.first == "golden") Icons.Default.Star else Icons.Default.Favorite,
                                        contentDescription = null,
                                        tint = tintColor,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    
                                    Spacer(modifier = Modifier.height(6.dp))
                                    
                                    Text(
                                        text = item.second,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isUnlocked) MaterialTheme.colorScheme.onSurface else TextGray,
                                        textAlign = TextAlign.Center
                                    )
                                    
                                    Spacer(modifier = Modifier.height(4.dp))
                                    
                                    Text(
                                        text = item.third,
                                        fontSize = 9.sp,
                                        color = TextGray,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // 6. WEEKLY HISTORY CHARTS
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "إحصائيات التسبيح الأسبوعية (History)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.align(Alignment.End)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            val daysOfWeek = listOf("الأحد", "الإثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة", "السبت")
                            val dataHist = viewModel.tasbihWeeklyHistory
                            
                            daysOfWeek.forEachIndexed { idx, day ->
                                val dailyVal = dataHist.getOrNull(idx) ?: 0
                                val barHeightFraction = (dailyVal.toFloat() / 300f).coerceIn(0.05f, 1.0f)
                                
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Bottom,
                                    modifier = Modifier.fillMaxHeight()
                                ) {
                                    Text(
                                        text = "$dailyVal",
                                        fontSize = 8.sp,
                                        color = SoftGold,
                                        fontWeight = FontWeight.Bold
                                    )
                                    
                                    Spacer(modifier = Modifier.height(4.dp))
                                    
                                    Box(
                                        modifier = Modifier
                                            .width(16.dp)
                                            .fillMaxHeight(barHeightFraction)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                Brush.verticalGradient(
                                                    listOf(SoftGold, EmeraldGreen)
                                                )
                                            )
                                    )
                                    
                                    Spacer(modifier = Modifier.height(6.dp))
                                    
                                    Text(
                                        text = day.substring(0, day.length.coerceAtMost(3)),
                                        fontSize = 10.sp,
                                        color = TextGray
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

// Helpers
@Composable
fun TabButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) EmeraldGreen else Color.Transparent,
            contentColor = if (isSelected) Color.White else TextGray
        ),
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
        modifier = Modifier.height(34.dp)
    ) {
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

// ==========================================
// 7. QIBLA SCREEN (Interactive Compass)
// ==========================================
@Composable
fun QiblaScreen(viewModel: NoorViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "اتجاه القبلة Qibla",
                style = MaterialTheme.typography.displayLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
            )

            // Switch layout
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                TabButton(
                    text = "البوصلة Compass",
                    isSelected = !viewModel.arModeActive,
                    onClick = { viewModel.arModeActive = false }
                )
                TabButton(
                    text = "كاميرا AR AR Mode",
                    isSelected = viewModel.arModeActive,
                    onClick = { viewModel.arModeActive = true }
                )
            }
        }

        if (!viewModel.arModeActive) {
            // HIGH FIDELITY REAL-TIME ROTATING COMPASS
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "قم بتدوير الهاتف لمطابقة المؤشرات الذهبية",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextGray),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // The Compass Dial Frame
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(280.dp)
                ) {
                    // Outer decorative circle
                    Spacer(
                        modifier = Modifier
                            .fillMaxSize()
                            .drawBehind {
                                drawCircle(
                                    color = if (viewModel.isMeccaAligned) EmeraldGreen else Color.White.copy(alpha = 0.1f),
                                    radius = size.minDimension / 2f,
                                    style = Stroke(width = 4f)
                                )
                            }
                    )

                    // Compass plate rotated based on live degree state
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(-viewModel.compassDegree),
                        contentAlignment = Alignment.Center
                    ) {
                        // Drawing directions markers
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(color = Color.White.copy(alpha = 0.03f), radius = size.minDimension / 2.3f)
                            // Draw compass hashes
                            val hashes = listOf("N", "E", "S", "W")
                            val offset = size.minDimension / 2.5f
                        }

                        // Compass markers text labels
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text("N", color = Color.Red, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.TopCenter))
                            Text("S", color = TextGray, modifier = Modifier.align(Alignment.BottomCenter))
                            Text("E", color = TextGray, modifier = Modifier.align(Alignment.CenterEnd).padding(end = 6.dp))
                            Text("W", color = TextGray, modifier = Modifier.align(Alignment.CenterStart).padding(start = 6.dp))
                        }

                        // Qibla direction Arrow inside plate points to 252 degrees
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = "Mecca pointer",
                            tint = if (viewModel.isMeccaAligned) EmeraldGreen else SoftGold,
                            modifier = Modifier
                                .size(85.dp)
                                .rotate(viewModel.computedQiblaAngle)
                        )
                    }

                    // Green Lock indicator to guide user when aligned
                    if (viewModel.isMeccaAligned) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = EmeraldGreen),
                            shape = CircleShape,
                            modifier = Modifier.align(Alignment.TopCenter).offset(y = (-15).dp)
                        ) {
                            Text(
                                "الكعبة • Makkah Aligns!",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("الزاوية القبلية", color = TextGray, fontSize = 12.sp)
                        Text("${viewModel.computedQiblaAngle}° N", fontWeight = FontWeight.Bold, color = EmeraldGreen, fontSize = 18.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("اتجاهك الحالي", color = TextGray, fontSize = 12.sp)
                        Text("${viewModel.compassDegree.toInt()}°", fontWeight = FontWeight.Bold, color = SoftGold, fontSize = 18.sp)
                    }
                }
            }
        } else {
            // AR MODE CAMERA SIMULATION (Saves on hardware bugs!)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color.Black)
            ) {
                // Layer 1: Elegant digital simulated Camera grid lines and noise overlay
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(color = Color(0xFF0F1A24).copy(alpha = 0.85f))
                    // Grid lines
                    drawLine(color = Color.White.copy(alpha = 0.08f), start = Offset(size.width / 3f, 0f), end = Offset(size.width / 3f, size.height))
                    drawLine(color = Color.White.copy(alpha = 0.08f), start = Offset(size.width * 2 / 3f, 0f), end = Offset(size.width * 2 / 3f, size.height))
                    drawLine(color = Color.White.copy(alpha = 0.08f), start = Offset(0f, size.height / 3f), end = Offset(size.width, size.height / 3f))
                    drawLine(color = Color.White.copy(alpha = 0.08f), start = Offset(0f, size.height * 2 / 3f), end = Offset(size.width, size.height * 2 / 3f))
                }

                // Layer 2: Glowing virtual overlay displaying Kaaba icon relative to alignments
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (viewModel.isMeccaAligned) {
                        // Kaaba is centered and locked!
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Holo Kaaba Golden",
                            tint = SoftGold,
                            modifier = Modifier
                                .size(120.dp)
                                .scale(1.15f)
                        )
                        Text(
                            text = "بيت الله الحرام - مكة المكرمة\nHoly Kaaba Aligned!",
                            style = MaterialTheme.typography.titleLarge.copy(color = SoftGold, fontWeight = FontWeight.Bold),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        // Guide the user with arrows depending on direction
                        val turnArabic = if (viewModel.compassDegree < viewModel.computedQiblaAngle) "أدر للـيمين Turn Right" else "أدر للـيسار Turn Left"
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Guide Arrow",
                            tint = EmeraldGreen,
                            modifier = Modifier
                                .size(80.dp)
                                .rotate(if (viewModel.compassDegree < viewModel.computedQiblaAngle) 0f else 180f)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = turnArabic,
                            style = MaterialTheme.typography.titleLarge.copy(color = Color.White, fontWeight = FontWeight.Bold),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Layer 3: Tech hud markers
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(
                        "AR QIBLA VIEW (SIMULATED HUD CAM)",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        color = TextGray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ==========================================
// 8. PROGRESS SCREEN (Gamified Tracker)
// ==========================================
@Composable
fun ProgressScreen(viewModel: NoorViewModel) {
    val progress = viewModel.userProgress.collectAsState().value ?: UserProgress()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "متابعة الإنجازات Progress",
                style = MaterialTheme.typography.displayLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
            )
        }

        // Gamification Header panel: Active Stats
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("مستواك الحالي", color = TextGray, fontSize = 12.sp)
                        Text("المرتبة ${progress.level}", style = MaterialTheme.typography.headlineMedium.copy(color = SoftGold, fontWeight = FontWeight.Bold))
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("إجمالي النقاط", color = TextGray, fontSize = 12.sp)
                        Text("${progress.points}", style = MaterialTheme.typography.headlineMedium.copy(color = EmeraldGreen, fontWeight = FontWeight.Bold))
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("المواظبة القصوى", color = TextGray, fontSize = 12.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = "Active", tint = SoftGold, modifier = Modifier.size(20.dp))
                            Text("${progress.streakDays} أيام", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
        }

        // LEVEL MILESTONE PROGRESS BAR
        item {
            val pointsToNextLevel = 100 - (progress.points % 100)
            val currentLevelPoints = progress.points % 100
            val fraction = currentLevelPoints / 100f

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("الدرجة القادمة (مستوى ${progress.level + 1})", style = MaterialTheme.typography.bodyMedium.copy(color = TextGray))
                    Text("متبقي $pointsToNextLevel نقطة", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = EmeraldGreen))
                }
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = fraction,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(CircleShape),
                    color = EmeraldGreen,
                    trackColor = CardOverlayDark
                )
            }
        }

        // BADGES GRID LIST
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "شارتي الأكاديمية (Achievements)",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                val badges = listOf(
                    Triple("Dawn Prayer Knight", "مصلّ فضيل الفجر", progress.points >= 15),
                    Triple("Vance Sibha Master", "مسبّح الأوراد", progress.points >= 25),
                    Triple("Quran Scholar Tracker", "مدوّن آي التنزيل", progress.points >= 50),
                    Triple("Faith Pilgrim Elite", "مقيم المكتوبات", progress.points >= 80),
                    Triple("Daily Wisdom Seeker", "الحكيم الذاكر", progress.points >= 100),
                    Triple("Al-Mu'min Al-Noor", "شامة الرحمة نور", progress.points >= 150)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(badges) { badge ->
                        val isLocked = !badge.third
                        val containerBg = if (isLocked) CardOverlayDark.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                        val labelColor = if (isLocked) TextGray else MaterialTheme.colorScheme.onSurface

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            border = if (!isLocked) BorderStroke(1.dp, SoftGold) else null,
                            colors = CardDefaults.cardColors(containerColor = containerBg)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.EmojiEvents,
                                    contentDescription = badge.first,
                                    tint = if (isLocked) TextGray else SoftGold,
                                    modifier = Modifier.size(34.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = badge.second,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = labelColor),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = badge.first,
                                    style = MaterialTheme.typography.labelLarge.copy(color = TextGray, fontSize = 10.sp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}

// ==========================================
// 9. SETTINGS SCREEN (Preferences Controller)
// ==========================================
@Composable
fun SettingsScreen(viewModel: NoorViewModel) {
    var showResetDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "الإعدادات Settings",
                style = MaterialTheme.typography.displayLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
            )
        }

        // Lang preferences
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "اللغة والترجمة (Lang Translation)", style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(10.dp))

                    val options = listOf(
                        "ar" to "العربية",
                        "en" to "English",
                        "fr" to "Français",
                        "tr" to "Türkçe",
                        "ur" to "اردو"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        options.forEach { item ->
                            val isSelected = viewModel.selectedLanguage == item.first
                            Button(
                                onClick = { viewModel.selectedLanguage = item.first },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) EmeraldGreen else CardOverlayDark,
                                    contentColor = if (isSelected) Color.White else TextGray
                                ),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                modifier = Modifier.weight(1.0f).height(32.dp)
                            ) {
                                Text(item.second, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Theme and Dark mode
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "الوضع الليلي (Dark Theme)", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                        Switch(
                            checked = viewModel.darkThemeEnabled,
                            onCheckedChange = { viewModel.darkThemeEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = EmeraldGreen)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(text = "ألوان واجهة المستخدم (Interface Accent)", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val accents = listOf(EmeraldGreen, Color(0xFF0B1F33), SoftGold)
                        accents.forEach { color ->
                            val isSelected = viewModel.accentAccentColor == color
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(color, CircleShape)
                                    .border(if (isSelected) 3.dp else 0.dp, SoftGold, CircleShape)
                                    .clickable { viewModel.accentAccentColor = color }
                            )
                        }
                    }
                }
            }
        }

        // Azan sounds customization - DE LUXE SMART ATHAN SYSTEM CONFIG
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "المؤذن الذكي ونظام الأذان Smart Athan",
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen),
                        modifier = Modifier.align(Alignment.End)
                    )

                    // 1. Silent Mode Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("الوضع الصامت للأذان", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                            Text("كتم الأذان تلقائيًا أثناء الاجتماعات أو النوم", fontSize = 11.sp, color = TextGray)
                        }
                        Switch(
                            checked = viewModel.azanSettingsSilentMode,
                            onCheckedChange = { viewModel.toggleSilentMode(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = EmeraldGreen)
                        )
                    }

                    // 2. Pre-prayer alert setup
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("التنبيهات المسبقة للأذان", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                Text("تنبيه لطيف قبل الأذان بدقائق للاستعداد", fontSize = 11.sp, color = TextGray)
                            }
                            Switch(
                                checked = viewModel.azanPreNotificationEnabled,
                                onCheckedChange = { viewModel.togglePrePrayerStatus(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = EmeraldGreen)
                            )
                        }

                        if (viewModel.azanPreNotificationEnabled) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("زمن التنبيه المبكر", fontSize = 12.sp, color = TextGray)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    TextButton(
                                        onClick = { viewModel.updatePrePrayerMinutes((viewModel.azanPreNotificationMinutes - 5).coerceAtLeast(5)) },
                                        colors = ButtonDefaults.textButtonColors(contentColor = SoftGold)
                                    ) {
                                        Text("-٥ دقائق", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Text(
                                        "${viewModel.azanPreNotificationMinutes} دقيقة",
                                        fontWeight = FontWeight.Bold,
                                        color = SoftGold,
                                        fontSize = 13.sp
                                    )
                                    TextButton(
                                        onClick = { viewModel.updatePrePrayerMinutes(viewModel.azanPreNotificationMinutes + 5) },
                                        colors = ButtonDefaults.textButtonColors(contentColor = SoftGold)
                                    ) {
                                        Text("+٥ دقائق", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // 3. Per Prayer Customization Deck
                    Text(
                        text = "تخصيص أصوات المؤذن لكل صلاة",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextGray),
                        modifier = Modifier.align(Alignment.End)
                    )

                    val prayers = listOf(
                        Triple("Fajr", "صلاة الفجر", viewModel.azanEnabledForFajr),
                        Triple("Dhuhr", "صلاة الظهر", viewModel.azanEnabledForDhuhr),
                        Triple("Asr", "صلاة العصر", viewModel.azanEnabledForAsr),
                        Triple("Maghrib", "صلاة المغرب", viewModel.azanEnabledForMaghrib),
                        Triple("Isha", "صلاة العشاء", viewModel.azanEnabledForIsha)
                    )

                    prayers.forEach { item ->
                        val key = item.first
                        val arabicName = item.second
                        val isEnabled = item.third
                        val currentMuezzin = when (key) {
                            "Fajr" -> viewModel.azanSoundForFajr
                            "Dhuhr" -> viewModel.azanSoundForDhuhr
                            "Asr" -> viewModel.azanSoundForAsr
                            "Maghrib" -> viewModel.azanSoundForMaghrib
                            else -> viewModel.azanSoundForIsha
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                                .border(1.dp, MaterialTheme.colorScheme.surface, RoundedCornerShape(14.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Row with Title & Enable Switch
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp), tint = if (isEnabled) EmeraldGreen else Color.Gray)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(arabicName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                }
                                Switch(
                                    checked = isEnabled,
                                    onCheckedChange = { viewModel.togglePrayerAthanStatus(key, it) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = EmeraldGreen),
                                    modifier = Modifier.scale(0.85f)
                                )
                            }

                            if (isEnabled) {
                                Spacer(modifier = Modifier.height(2.dp))
                                // Horizontal Reciter Chips
                                Text("اختر صوت المؤذن للصلاة:", fontSize = 11.sp, color = TextGray, modifier = Modifier.align(Alignment.End))
                                
                                val voices = listOf("أذان مكة المكرّمة (الحرم)", "أذان المدينة المنوّرة (النبوي)", "أذان المسجد الأقصى المبارك", "أذان القاهرة الهادئ")
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    voices.forEach { voice ->
                                        val isSelected = currentMuezzin == voice
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSelected) EmeraldGreen else MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
                                                .border(1.dp, if (isSelected) SoftGold else Color.Transparent, RoundedCornerShape(8.dp))
                                                .clickable { viewModel.saveMuezzinForPrayer(key, voice) }
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = voice.substringBefore(" (").take(15) + if (voice.contains("(")) "..." else "",
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) Color.White else TextGray
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                // Mini Audio player row with Wave Animation
                                val isPlayingThis = viewModel.isTestAthanPlaying && viewModel.testAthanPrayerSelected == key
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Live Sound Wave visualizer
                                    Row(
                                        modifier = Modifier
                                            .width(130.dp)
                                            .fillMaxHeight(),
                                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (isPlayingThis) {
                                            viewModel.audioWaveAnimationProgress.forEach { heightMultiplier ->
                                                Box(
                                                    modifier = Modifier
                                                        .width(3.dp)
                                                        .fillMaxHeight(heightMultiplier)
                                                        .clip(CircleShape)
                                                        .background(
                                                            Brush.verticalGradient(listOf(EmeraldGreen, SoftGold))
                                                        )
                                                )
                                            }
                                        } else {
                                            // Static small dots
                                            repeat(16) { i ->
                                                Box(
                                                    modifier = Modifier
                                                        .width(3.dp)
                                                        .height(2.dp)
                                                        .clip(CircleShape)
                                                        .background(Color.Gray.copy(alpha = 0.4f))
                                                )
                                            }
                                        }
                                    }

                                    // Preview play/stop button
                                    Button(
                                        onClick = { viewModel.togglePreviewAthan(key) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isPlayingThis) Color.Red.copy(alpha = 0.15f) else EmeraldGreen.copy(alpha = 0.15f),
                                            contentColor = if (isPlayingThis) Color.Red else EmeraldGreen
                                        ),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isPlayingThis) Icons.Default.Stop else Icons.Default.PlayArrow,
                                            contentDescription = "Preview",
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (isPlayingThis) "إيقاف المعاينة" else "استماع للمؤذن", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Reset Settings Option
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showResetDialog = true }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("تهيئة إعدادات التطبيق Reset System Settings", color = Color.Red, fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = Color.Red)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("تأكيد تهيئة الإعدادات؟ Confirm System Reset") },
            text = { Text("سيؤدي ذلك إلى إعادة لغة التطبيق، الألوان، وأصوات الأذان للوضع الافتراضي.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.selectedLanguage = "ar"
                    viewModel.darkThemeEnabled = true
                    viewModel.accentAccentColor = EmeraldGreen
                    viewModel.selectedAzanSound = "Makkah Adhan"
                    showResetDialog = false
                }) {
                    Text("إعادة تهيئة Confirm", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("إلغاء Cancel")
                }
            }
        )
    }
}

// ==========================================
// 10. AI ISLAMIC DIALOG MODAL (Gemini Powered)
// ==========================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiChatModal(viewModel: NoorViewModel, onDismiss: () -> Unit) {
    var typedQuery by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(EmeraldGreen, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "مساعدك الإسلامي الذكي Noor AI",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    )
                }

                TextButton(onClick = { viewModel.clearChat() }) {
                    Text("مسح السجل Clear Chat", color = Color.Red, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Text(
                "استفتِ أو اطرح سؤالاً إسلامياً في الفقه، الأذكار، أو تاريخ سور القرآن الكريم.",
                color = TextGray,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            // Chat Threads
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(viewModel.aiChatHistory) { message ->
                    val isUser = message.isUser
                    val align = if (isUser) Alignment.End else Alignment.Start
                    val bubbleColor = if (isUser) EmeraldGreen else CardOverlayDark
                    val tColor = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface

                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = align) {
                        Card(
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (isUser) 16.dp else 0.dp,
                                bottomEnd = if (isUser) 0.dp else 16.dp
                            ),
                            colors = CardDefaults.cardColors(containerColor = bubbleColor),
                            border = if (!isUser) BorderStroke(1.dp, SoftGold.copy(alpha = 0.3f)) else null,
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = message.text,
                                    style = MaterialTheme.typography.bodyLarge.copy(color = tColor, fontSize = 15.sp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = message.timestamp,
                                    style = MaterialTheme.typography.labelLarge.copy(color = TextGray.copy(alpha = 0.7f), fontSize = 10.sp),
                                    modifier = Modifier.align(Alignment.End)
                                )
                            }
                        }
                    }
                }

                if (viewModel.aiApiResponseLoading) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = SoftGold)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("يتأمل المساعد في سؤاله الكريم...", color = TextGray, fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Query Input Box
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = typedQuery,
                    onValueChange = { typedQuery = it },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_chat_prompt_input_field"),
                    placeholder = { Text("اسأل نور AI هنا...") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        if (typedQuery.isNotBlank()) {
                            viewModel.sendChatMessage(typedQuery)
                            typedQuery = ""
                        }
                    })
                )

                Spacer(modifier = Modifier.width(10.dp))

                IconButton(
                    onClick = {
                        if (typedQuery.isNotBlank()) {
                            viewModel.sendChatMessage(typedQuery)
                            typedQuery = ""
                        }
                    },
                    modifier = Modifier
                        .background(SoftGold, CircleShape)
                        .size(48.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.Black)
                }
            }
        }
    }
}
