package com.rumahfit.liquidglass

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// ==========================================
// 1. TEMA & WARNA (Liquid Glass Theme)
// ==========================================
object LiquidTheme {
    val BgLight1 = Color(0xFFF0F9FF)
    val BgLight2 = Color(0xFFE0F2FE)
    val BgLight3 = Color(0xFFFFFFFF)

    val CyanPrimary = Color(0xFF0284C7)
    val CyanVibrant = Color(0xFF0EA5E9)
    val CyanSoft = Color(0xFF38BDF8)
    val MintAccent = Color(0xFF10B981)
    val OrangeAccent = Color(0xFFFF5722)
    val TextDark = Color(0xFF0F172A)
    val TextMuted = Color(0xFF64748B)

    val GlassCardBg = Color(0xC2FFFFFF)
    val GlassBorder = Color(0xDDFFFFFF)
}

// ==========================================
// 2. MODEL DATA GERAKAN & ATLET
// ==========================================
data class AndroidExercise(
    val id: String,
    val name: String,
    val category: String,
    val isGym: Boolean,
    val sets: Int,
    val reps: Int,
    val restSec: Int,
    val isTimeBased: Boolean,
    val instruction: String,
    val target: String
)

val FULL_EXERCISE_LIST = listOf(
    // Dada
    AndroidExercise("pushup", "Push-Up Standar", "Dada", false, 4, 12, 60, false, "Badan lurus, turunkan dada siku 45°, dorong kembali.", "Pectoralis Major, Triceps"),
    AndroidExercise("widepushup", "Wide Push-Up", "Dada", false, 3, 12, 60, false, "Posisi tangan lebih lebar dari bahu.", "Dada Luar & Bahu"),
    AndroidExercise("diamond", "Diamond Push-Up", "Dada", false, 3, 10, 60, false, "Ibu jari & telunjuk bertemu membentuk berlian.", "Triceps & Dada Tengah"),
    AndroidExercise("decline", "Decline Push-Up", "Dada", false, 3, 10, 60, false, "Kaki di atas kursi/elevasi, tangan di lantai.", "Dada Bagian Atas"),
    AndroidExercise("dbbench", "DB Bench Press", "Dada", true, 4, 10, 75, false, "Dorong dumbbell ke atas di atas dada.", "Pectoralis Major"),

    // Punggung
    AndroidExercise("superman", "Superman Hold", "Punggung", false, 3, 30, 45, true, "Tengkurap, angkat dada & kaki bersamaan.", "Erector Spinae"),
    AndroidExercise("towelrow", "Towel Doorframe Row", "Punggung", false, 4, 12, 60, false, "Tarik tubuh menggunakan handuk kuat.", "Latissimus Dorsi"),
    AndroidExercise("dbrow", "DB Bent-Over Row", "Punggung", true, 4, 12, 60, false, "Bungkuk 45°, tarik dumbbell ke pinggul.", "Lats & Belikat"),

    // Kaki
    AndroidExercise("squat", "Bodyweight Squat", "Kaki", false, 4, 15, 60, false, "Turunkan pinggul seperti duduk, paha sejajar lantai.", "Quadriceps & Glutes"),
    AndroidExercise("lunges", "Walking Lunges", "Kaki", false, 3, 12, 60, false, "Langkahkan kaki ke depan tekuk 90 derajat.", "Quads & Hamstring"),
    AndroidExercise("glutebridge", "Glute Bridge", "Kaki", false, 3, 15, 45, false, "Dorong pinggul ke atas kencangkan pantat.", "Gluteus Maximus"),
    AndroidExercise("wallsit", "Wall Sit Isometric", "Kaki", false, 3, 45, 60, true, "Punggung nempel dinding paha sejajar.", "Ketahanan Quads"),
    AndroidExercise("dbsquat", "DB Goblet Squat", "Kaki", true, 4, 12, 60, false, "Pegang dumbbell di dada, lakukan deep squat.", "Quads & Glutes"),

    // Bahu & Lengan
    AndroidExercise("pikepush", "Pike Push-Up", "Bahu", false, 3, 10, 60, false, "Pinggul tinggi bentuk V terbalik, turunkan kepala.", "Deltoid Anterior"),
    AndroidExercise("chairdips", "Chair Dips", "Lengan", false, 3, 12, 60, false, "Tangan di kursi, tekuk siku 90 derajat.", "Triceps Brachii"),
    AndroidExercise("dbohp", "DB Overhead Press", "Bahu", true, 4, 10, 60, false, "Dorong dumbbell tegak lurus ke atas kepala.", "Bahu & Traps"),
    AndroidExercise("dbcurl", "DB Bicep Curl", "Lengan", true, 3, 12, 45, false, "Tekuk siku mengunci lengan atas.", "Biceps Brachii"),

    // Core & Kardio
    AndroidExercise("plank", "Forearm Plank", "Core", false, 3, 45, 45, true, "Tumpu pada siku dan jari kaki, perut kencang.", "Transverse Abdominis"),
    AndroidExercise("mtnclimber", "Mountain Climber", "Core", false, 3, 30, 45, true, "Posisi pushup, tarik lutut ke dada bergantian.", "Abs & Cardio"),
    AndroidExercise("burpee", "Full Burpees", "Kardio", false, 4, 10, 60, false, "Jongkok, tendang ke pushup, lompat tegak.", "Full Body Explosive"),
    AndroidExercise("jumpjack", "Jumping Jacks", "Kardio", false, 3, 30, 30, true, "Lompat membuka kaki dan tepuk tangan.", "Cardiovascular")
)

data class AthleteProtocol(
    val id: String,
    val name: String,
    val badge: String,
    val desc: String,
    val exerciseIds: List<String>
)

val PRO_ATHLETES_LIST = listOf(
    AthleteProtocol("nasional", "Atlet Nasional RI", "Kemenpora", "Kurikulum WAH Kemenpora RI untuk daya tahan atletik.", listOf("pushup", "squat", "plank", "lunges", "jumpjack")),
    AthleteProtocol("basket", "Basket NBA Agility", "NBA Pro", "Plyometrics, kelincahan akselerasi, dan kekuatan lompatan.", listOf("burpee", "squat", "wallsit", "mtnclimber")),
    AthleteProtocol("silat", "Pencak Silat Tabata", "Bela Diri", "Interval ledakan tenaga 20s kerja / 10s istirahat.", listOf("pushup", "squat", "mtnclimber", "plank")),
    AthleteProtocol("tinju", "Tinju Juara Dunia", "Boxing", "Kardio bahu konstan, skipping, dan ketahanan 12 ronde.", listOf("pikepush", "burpee", "jumpjack", "plank")),
    AthleteProtocol("sepakbola", "Sepak Bola Timnas", "Timnas", "Stamina paha bawah, sprint interval, dan keseimbangan lari.", listOf("lunges", "squat", "mtnclimber", "wallsit"))
)

// ==========================================
// 3. KOMPONEN REUSABLE (Liquid Glass)
// ==========================================
@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(28.dp),
    backgroundColor: Color = LiquidTheme.GlassCardBg,
    borderColor: Color = LiquidTheme.GlassBorder,
    elevation: Dp = 10.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = LiquidTheme.CyanVibrant.copy(alpha = 0.15f),
                spotColor = LiquidTheme.CyanPrimary.copy(alpha = 0.20f)
            )
            .clip(shape)
            .background(
                brush = Brush.linearGradient(
                    listOf(
                        backgroundColor,
                        backgroundColor.copy(alpha = 0.60f)
                    )
                )
            )
            .border(
                width = 1.2.dp,
                brush = Brush.linearGradient(
                    listOf(
                        borderColor,
                        borderColor.copy(alpha = 0.40f)
                    )
                ),
                shape = shape
            )
            .padding(18.dp)
    ) {
        Column(content = content)
    }
}

@Composable
fun GlassSpringButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "BtnSpring"
    )

    val shape = RoundedCornerShape(22.dp)
    val bgBrush = if (isPrimary) {
        Brush.horizontalGradient(listOf(LiquidTheme.CyanVibrant, LiquidTheme.CyanPrimary))
    } else {
        Brush.linearGradient(listOf(Color.White.copy(alpha = 0.85f), Color.White.copy(alpha = 0.45f)))
    }

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = if (isPrimary) 8.dp else 4.dp,
                shape = shape,
                spotColor = if (isPrimary) LiquidTheme.CyanPrimary.copy(alpha = 0.35f) else Color.Transparent
            )
            .clip(shape)
            .background(bgBrush)
            .border(
                width = 1.dp,
                color = if (isPrimary) Color.White.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.8f),
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 20.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            content = content
        )
    }
}

@Composable
fun AnimatedLiquidBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "LiquidBlobsTransition")

    val offset1 by infiniteTransition.animateFloat(
        initialValue = -30f,
        targetValue = 35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Blob1Offset"
    )

    val offset2 by infiniteTransition.animateFloat(
        initialValue = 40f,
        targetValue = -30f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Blob2Offset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(LiquidTheme.BgLight1, LiquidTheme.BgLight2, LiquidTheme.BgLight3)))
    ) {
        Box(
            modifier = Modifier
                .size(340.dp)
                .offset(x = (-60).dp, y = (30 + offset1).dp)
                .blur(70.dp)
                .background(LiquidTheme.CyanSoft.copy(alpha = 0.42f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(380.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 80.dp, y = (60 + offset2).dp)
                .blur(80.dp)
                .background(LiquidTheme.CyanVibrant.copy(alpha = 0.32f), CircleShape)
        )
    }
}

// ==========================================
// 4. LAYAR UTAMA (MainScreen & Tabs)
// ==========================================
enum class AppNav(val title: String, val icon: ImageVector) {
    HOME("Home", Icons.Rounded.Home),
    KATALOG("Gerakan", Icons.Rounded.FitnessCenter),
    GPS("GPS", Icons.Rounded.Navigation),
    ATLET("Atlet", Icons.Rounded.EmojiEvents),
    WORKOUT("Player", Icons.Rounded.PlayArrow)
}

@Composable
fun MainScreen() {
    var selectedTab by remember { mutableStateOf(AppNav.HOME) }
    var isGymMode by remember { mutableStateOf(false) }
    var streakCount by remember { mutableIntStateOf(8) }
    var waterGlasses by remember { mutableIntStateOf(6) }

    // State Latihan Berjalan
    var activeExercises by remember { mutableStateOf<List<AndroidExercise>>(emptyList()) }
    var currentExIndex by remember { mutableIntStateOf(0) }
    var currentSet by remember { mutableIntStateOf(1) }
    var isResting by remember { mutableStateOf(false) }
    var restSeconds by remember { mutableIntStateOf(60) }

    // Helper Sound Generator
    val toneGen = remember {
        try { ToneGenerator(AudioManager.STREAM_MUSIC, 70) } catch (e: Exception) { null }
    }

    // Rest Countdown
    LaunchedEffect(isResting, restSeconds) {
        if (isResting && restSeconds > 0) {
            delay(1000L)
            restSeconds -= 1
            if (restSeconds in 1..3) {
                toneGen?.startTone(ToneGenerator.TONE_PROP_BEEP, 100)
            } else if (restSeconds == 0) {
                toneGen?.startTone(ToneGenerator.TONE_PROP_ACK, 250)
                isResting = false
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedLiquidBackground()

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                LiquidBottomNav(selectedTab = selectedTab, onSelect = { selectedTab = it })
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedTab) {
                    AppNav.HOME -> {
                        HomeScreenContent(
                            streak = streakCount,
                            water = waterGlasses,
                            isGym = isGymMode,
                            onToggleGym = { isGymMode = !isGymMode },
                            onAddWater = { waterGlasses++ },
                            onStartDaily = {
                                activeExercises = FULL_EXERCISE_LIST.filter { it.isGym == isGymMode }.take(5)
                                currentExIndex = 0
                                currentSet = 1
                                selectedTab = AppNav.WORKOUT
                            }
                        )
                    }
                    AppNav.KATALOG -> {
                        CatalogScreenContent(
                            isGym = isGymMode,
                            onPlayExercise = { ex ->
                                activeExercises = listOf(ex)
                                currentExIndex = 0
                                currentSet = 1
                                selectedTab = AppNav.WORKOUT
                            }
                        )
                    }
                    AppNav.GPS -> {
                        GPSScreenContent()
                    }
                    AppNav.ATLET -> {
                        AthleteScreenContent(
                            onStartAthlete = { ath ->
                                activeExercises = FULL_EXERCISE_LIST.filter { ath.exerciseIds.contains(it.id) }
                                currentExIndex = 0
                                currentSet = 1
                                selectedTab = AppNav.WORKOUT
                            }
                        )
                    }
                    AppNav.WORKOUT -> {
                        WorkoutPlayerContent(
                            exercises = activeExercises,
                            currentIndex = currentExIndex,
                            currentSet = currentSet,
                            isResting = isResting,
                            restSeconds = restSeconds,
                            onCompleteSet = {
                                toneGen?.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
                                val ex = activeExercises.getOrNull(currentExIndex) ?: return@WorkoutPlayerContent
                                if (currentSet < ex.sets) {
                                    currentSet += 1
                                    restSeconds = ex.restSec
                                    isResting = true
                                } else {
                                    if (currentExIndex < activeExercises.size - 1) {
                                        currentExIndex += 1
                                        currentSet = 1
                                        restSeconds = ex.restSec
                                        isResting = true
                                    } else {
                                        streakCount += 1
                                        toneGen?.startTone(ToneGenerator.TONE_PROP_ACK, 400)
                                        selectedTab = AppNav.HOME
                                    }
                                }
                            },
                            onSkipRest = { isResting = false },
                            onCancel = { selectedTab = AppNav.HOME }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreenContent(
    streak: Int,
    water: Int,
    isGym: Boolean,
    onToggleGym: () -> Unit,
    onAddWater: () -> Unit,
    onStartDaily: () -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("RumahFit Liquid", fontSize = 24.sp, fontWeight = FontWeight.Black, color = LiquidTheme.TextDark)
                Text(if (isGym) "Mode: Gym Dumbbell" else "Mode: Calisthenics Bodyweight", fontSize = 12.sp, color = LiquidTheme.TextMuted)
            }
            GlassSpringButton(onClick = onToggleGym, isPrimary = false) {
                Text(if (isGym) "Ke Bodyweight" else "Ke Gym", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LiquidTheme.CyanPrimary)
            }
        }

        // Program Hari Ini Glass Card
        LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("PROGRAM UTAMA", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LiquidTheme.CyanPrimary)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.LocalFireDepartment, null, tint = LiquidTheme.OrangeAccent, modifier = Modifier.size(16.dp))
                    Text("$streak Hari", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LiquidTheme.TextDark)
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(if (isGym) "Upper Body Dumbbell Flow" else "Chest & Core Calisthenics", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = LiquidTheme.TextDark)
            Text("5 Gerakan Pilihan • 25 Menit • 280 kcal", fontSize = 12.sp, color = LiquidTheme.TextMuted)
            Spacer(Modifier.height(16.dp))
            GlassSpringButton(onClick = onStartDaily, isPrimary = true, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Rounded.PlayArrow, null, tint = Color.White)
         
