package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.database.PromotionCampaign
import com.example.data.database.SavedSeoItem
import com.example.ui.theme.ProgressGold
import com.example.ui.theme.ProgressGreen
import com.example.ui.theme.YoutubeCoral
import com.example.ui.theme.YoutubeRed
import com.example.viewmodel.YoutubeServicesViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MainDashboard(viewModel: YoutubeServicesViewModel) {
    val context = LocalContext.current
    val toastMsg by viewModel.toastMessage.collectAsState()

    // Handle ViewModel notifications
    LaunchedEffect(toastMsg) {
        toastMsg?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearToastMessage()
        }
    }

    var currentTab by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        bottomBar = {
            DashboardBottomNavigation(
                selectedTab = currentTab,
                onTabSelected = { currentTab = it }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Branded Header Row with Creator Points Indicator
            AppHeader(viewModel = viewModel)

            // Dynamic panel content transition
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when (currentTab) {
                    0 -> CampaignsPanel(viewModel = viewModel)
                    1 -> TagsExtractorPanel(viewModel = viewModel)
                    2 -> ContentWizardPanel(viewModel = viewModel)
                    3 -> ToolKitPanel(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun AppHeader(viewModel: YoutubeServicesViewModel) {
    val stats by viewModel.userStats.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .border(width = 1.dp, color = Color(0xFFE2E8F0))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = "App Logo",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "FREE YOUTUBE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Growth & SEO Suite",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp
                )
            }
        }

        // Creator Points Chip
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = "Creator Energy Points",
                tint = ProgressGold,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${stats?.points ?: 1200} PTS",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.testTag("creator_points_display")
            )
        }
    }
}

@Composable
fun DashboardBottomNavigation(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val items = listOf(
        NavigationItem("Campaigns", Icons.Default.Campaign, Icons.Outlined.Campaign),
        NavigationItem("SEO Tags", Icons.Default.Label, Icons.Outlined.Label),
        NavigationItem("AI Writer", Icons.Default.Assignment, Icons.Outlined.Assignment),
        NavigationItem("Tool Kit", Icons.Default.QueryStats, Icons.Outlined.QueryStats)
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier.height(72.dp)
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                icon = {
                    Icon(
                        imageVector = if (selectedTab == index) item.filledIcon else item.outlinedIcon,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = Color(0xFF64748B),
                    unselectedTextColor = Color(0xFF64748B)
                ),
                modifier = Modifier.testTag("nav_tab_$index")
            )
        }
    }
}

data class NavigationItem(
    val label: String,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector
)

// --- SUB-SCREEN 1: CAMPAIGNS PANEL ---

@Composable
fun CampaignsPanel(viewModel: YoutubeServicesViewModel) {
    var subTab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = subTab,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[subTab]),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            Tab(
                selected = subTab == 0,
                onClick = { subTab = 0 },
                text = { Text("Active Boosts", fontWeight = FontWeight.Bold) },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = Color(0xFF64748B)
            )
            Tab(
                selected = subTab == 1,
                onClick = { subTab = 1 },
                text = { Text("Earn Points", fontWeight = FontWeight.Bold) },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = Color(0xFF64748B)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            if (subTab == 0) {
                ActiveBoostsTab(viewModel = viewModel)
            } else {
                EarnPointsTab(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ActiveBoostsTab(viewModel: YoutubeServicesViewModel) {
    val campaigns by viewModel.campaigns.collectAsState()

    var videoUrl by remember { mutableStateOf("") }
    var videoTitle by remember { mutableStateOf("") }
    var campaignType by remember { mutableStateOf("Views") } // "Views", "Subscribers", "Likes", "Watch Time"
    var targetCount by remember { mutableIntStateOf(100) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Quick Campaign Creation Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🚀 Run Free Promotion Campaign",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Boost views, subscribers, likes, and watch time organically using Creator Points. 100% working and secure.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = videoUrl,
                        onValueChange = { videoUrl = it },
                        label = { Text("YouTube Video URL") },
                        placeholder = { Text("https://www.youtube.com/watch?v=...") },
                        leadingIcon = { Icon(Icons.Default.SmartDisplay, "URL icon", tint = MaterialTheme.colorScheme.primary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("campaign_url_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color(0xFFCBD5E1),
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = videoTitle,
                        onValueChange = { videoTitle = it },
                        label = { Text("Video Title / Campaign Name") },
                        placeholder = { Text("My Amazing Video Topic") },
                        leadingIcon = { Icon(Icons.Default.Campaign, "Title icon", tint = MaterialTheme.colorScheme.primary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("campaign_title_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color(0xFFCBD5E1),
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Campaign Type Selection Chips
                    Text(
                        text = "Campaign Boost Category:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val types = listOf("Views", "Subscribers", "Likes", "Watch Time")
                        types.forEach { type ->
                            val isSelected = campaignType == type
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFF1F3F9))
                                    .clickable { campaignType = type }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = type.split(" ")[0], // short label
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Target Count Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Target Boost Amount:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val counts = listOf(50, 100, 250, 500)
                            counts.forEach { count ->
                                val isSel = targetCount == count
                                Box(
                                    modifier = Modifier
                                        .padding(start = 4.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)
                                        .border(1.dp, if (isSel) MaterialTheme.colorScheme.primary else Color(0xFFCBD5E1), RoundedCornerShape(4.dp))
                                        .clickable { targetCount = count }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "$count",
                                        color = if (isSel) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val pointsCost = when (campaignType) {
                        "Views" -> targetCount * 2
                        "Subscribers" -> targetCount * 10
                        "Likes" -> targetCount * 4
                        "Watch Time" -> targetCount * 15
                        else -> targetCount * 3
                    }

                    Button(
                        onClick = {
                            val success = viewModel.createCampaign(
                                videoUrl = videoUrl,
                                title = videoTitle,
                                type = campaignType,
                                targetCount = targetCount
                            )
                            if (success) {
                                videoUrl = ""
                                videoTitle = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("start_campaign_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Bolt, "Bolt icon")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Start Boost Campaign (Costs $pointsCost PTS)",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // 100,000+ Booster Channel Pool Card
        item {
            val totalChannels by viewModel.totalBoosterChannels.collectAsState()
            val logs by viewModel.boosterLogs.collectAsState()

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Dns,
                                contentDescription = "Network Pool",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "🌐 100,000+ Booster Channel Pool",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface,
                                letterSpacing = (-0.5).sp
                            )
                        }

                        // Live Pulse Dot
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(ProgressGreen.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(ProgressGreen)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ACTIVE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = ProgressGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "A massive global network of registered YouTube channels ready to boost your campaign naturally.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Stats Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Stat 1: Channels
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color(0xFFF1F3F9), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "BOOSTER POOL",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = String.format("%,d", totalChannels),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Stat 2: Speed
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color(0xFFF1F3F9), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "NETWORK IP",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Rotated Tunnels",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Stat 3: Capacity
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color(0xFFF1F3F9), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "BOOSTING ENGINE",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "100% Working",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = ProgressGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Real-Time Console Logs Ticker
                    Text(
                        text = "Live Tunnels Activity Logs:",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E2026))
                            .padding(8.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(logs) { log ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "> ",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = log,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = Color(0xFFA7F3D0) // clean green terminal text
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Active Campaigns List
        item {
            Text(
                text = "📊 Live Campaign Board",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp),
                letterSpacing = (-0.5).sp
            )
        }

        if (campaigns.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = "No campaigns",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Active Campaigns Yet",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Submit a video link above and spend Creator Points to run a free organic views/subscribers boost campaign!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(campaigns, key = { it.id }) { campaign ->
                CampaignItemRow(campaign = campaign, onDelete = { viewModel.deleteCampaign(campaign.id) })
            }
        }
    }
}

@Composable
fun CampaignItemRow(campaign: PromotionCampaign, onDelete: () -> Unit) {
    val progressPercent = (campaign.currentCount.toFloat() / campaign.targetCount.toFloat()).coerceIn(0f, 1f)

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            1.dp,
            if (campaign.status == "Active") MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else Color(0xFFE2E8F0)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    val icon = when (campaign.campaignType) {
                        "Views" -> Icons.Default.Visibility
                        "Subscribers" -> Icons.Default.CheckCircle
                        "Likes" -> Icons.Default.ThumbUp
                        else -> Icons.Default.SmartDisplay
                    }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (campaign.status == "Active") MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color(0xFFF1F3F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = campaign.campaignType,
                            tint = if (campaign.status == "Active") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = campaign.videoTitle,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "${campaign.campaignType} Campaign • URL ID: ...${campaign.videoUrl.takeLast(10)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Status Chip or Delete Button
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (campaign.status == "Active") ProgressGold.copy(alpha = 0.15f) else ProgressGreen.copy(alpha = 0.15f)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = campaign.status.uppercase(),
                            color = if (campaign.status == "Active") ProgressGold else ProgressGreen,
                            fontWeight = FontWeight.Black,
                            fontSize = 9.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete campaign",
                            tint = Color(0xFFEF5350),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Campaign progress slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Delivering Boost Progress:",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${campaign.currentCount} / ${campaign.targetCount} (${(progressPercent * 100).toInt()}%)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (campaign.status == "Active") MaterialTheme.colorScheme.primary else ProgressGreen
                )
            }
            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { progressPercent },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = if (campaign.status == "Active") MaterialTheme.colorScheme.primary else ProgressGreen,
                trackColor = Color(0xFFE2E8F0)
            )
        }
    }
}

// --- Earn Points Tab (Quests) ---

@Composable
fun EarnPointsTab(viewModel: YoutubeServicesViewModel) {
    var quizStep by remember { mutableIntStateOf(0) } // 0: start, 1: correct, 2: wrong
    var activeQuizIndex by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableIntStateOf(-1) }

    val coroutineScope = rememberCoroutineScope()

    // Trivia Questions Pool
    val quizzes = listOf(
        CreatorQuizData(
            question = "Which is the most critical factor for a high YouTube Click-Through-Rate (CTR)?",
            options = listOf(
                "Video tag counts in metadata",
                "Hook and professional custom Thumbnail + Title",
                "High resolution camera gear",
                "Video description word count"
            ),
            correctIndex = 1,
            reward = 150,
            hint = "CTR depends directly on what a user sees before clicking on search results: Thumbnail and Title!"
        ),
        CreatorQuizData(
            question = "How long must the initial 'Hook' be in a video to effectively retain viewers?",
            options = listOf(
                "First 2 to 3 minutes",
                "First 5 to 15 seconds",
                "At least 45 seconds",
                "Doesn't matter as long as video is long"
            ),
            correctIndex = 1,
            reward = 150,
            hint = "YouTube analytics prove that the first 5 to 15 seconds decide if a viewer stays or clicks away."
        ),
        CreatorQuizData(
            question = "Which keyword placement is best for indexing a video on search?",
            options = listOf(
                "First 2 sentences of the description and Title",
                "Only in the tags list",
                "At the very end of the video file name",
                "In comments section pinned"
            ),
            correctIndex = 0,
            reward = 150,
            hint = "YouTube crawler indexes the Title and the top 2 sentences of the Description first."
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Daily Creator Quiz Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "Quiz icon",
                            tint = ProgressGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Daily Creator Knowledge Quest",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface,
                            letterSpacing = (-0.5).sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Answer creator trivia correctly to earn immediate +150 Creator Points!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    val activeQuiz = quizzes[activeQuizIndex]

                    if (quizStep == 0) {
                        Text(
                            text = activeQuiz.question,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        activeQuiz.options.forEachIndexed { idx, option ->
                            val isSel = selectedOptionIndex == idx
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) MaterialTheme.colorScheme.primaryContainer else Color(0xFFF1F3F9))
                                    .border(
                                        width = 1.dp,
                                        color = if (isSel) MaterialTheme.colorScheme.primary else Color(0xFFE2E8F0),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedOptionIndex = idx }
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "${'A' + idx}. $option",
                                    color = if (isSel) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (selectedOptionIndex == -1) return@Button
                                if (selectedOptionIndex == activeQuiz.correctIndex) {
                                    quizStep = 1
                                    viewModel.earnPoints(activeQuiz.reward, "Knowledge Quest")
                                } else {
                                    quizStep = 2
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Submit Answer", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    } else if (quizStep == 1) {
                        // Correct Screen
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Success",
                                tint = ProgressGreen,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "100% CORRECT!",
                                fontWeight = FontWeight.Black,
                                color = ProgressGreen,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Amazing! You have earned +${activeQuiz.reward} Creator Points to boost your campaign.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = {
                                    quizStep = 0
                                    selectedOptionIndex = -1
                                    activeQuizIndex = (activeQuizIndex + 1) % quizzes.size
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Next Trivia Quiz", color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        // Wrong Screen
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.HelpOutline,
                                contentDescription = "Incorrect",
                                tint = ProgressGold,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "INCORRECT ANSWER",
                                fontWeight = FontWeight.Black,
                                color = ProgressGold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "💡 Tip: ${activeQuiz.hint}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .background(Color(0xFFF1F3F9), RoundedCornerShape(6.dp))
                                    .padding(10.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Row {
                                Button(
                                    onClick = {
                                        quizStep = 0
                                        selectedOptionIndex = -1
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Try Again", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        quizStep = 0
                                        selectedOptionIndex = -1
                                        activeQuizIndex = (activeQuizIndex + 1) % quizzes.size
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Skip Quiz", color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Promo Engagement Room
        item {
            Text(
                text = "💬 Creator Collaboration Feed",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp),
                letterSpacing = (-0.5).sp
            )
        }

        item {
            Text(
                text = "Help other creators by reviewing their promotional content. Engage for 5 seconds to earn +50 Creator Points!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Engagement feeds list
        val engagementFeeds = listOf(
            FeedVideoData("Unboxing the Newest Flagship Smartphone", "MTech Reviews", "Tech", "+50 PTS"),
            FeedVideoData("My Solo Backpacking Trip through Japan", "Wanderlust Chronicles", "Travel", "+50 PTS"),
            FeedVideoData("How to Build a Custom Mechanical Keyboard", "Crafty Keys", "DIY", "+50 PTS")
        )

        items(engagementFeeds) { feed ->
            var engagingStatus by remember { mutableStateOf("Engage") } // "Engage", "Engaging...", "Rewarded"
            var timerCount by remember { mutableIntStateOf(5) }

            LaunchedEffect(engagingStatus) {
                if (engagingStatus == "Engaging...") {
                    while (timerCount > 0) {
                        delay(1000)
                        timerCount--
                    }
                    engagingStatus = "Rewarded"
                    viewModel.earnPoints(50, "Reviewing ${feed.channelName}")
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.OndemandVideo,
                                contentDescription = "Video Icon",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = feed.title,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "${feed.channelName} • ${feed.category}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (engagingStatus == "Engage") {
                                engagingStatus = "Engaging..."
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when (engagingStatus) {
                                "Engaging..." -> Color(0xFFE2E8F0)
                                "Rewarded" -> ProgressGreen
                                else -> MaterialTheme.colorScheme.primary
                            }
                        ),
                        shape = RoundedCornerShape(8.dp),
                        enabled = engagingStatus == "Engage"
                    ) {
                        Text(
                            text = when (engagingStatus) {
                                "Engaging..." -> "Watch ($timerCount s)"
                                "Rewarded" -> "Done +50"
                                else -> "Review Feed"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (engagingStatus == "Engaging...") Color(0xFF475569) else Color.White
                        )
                    }
                }
            }
        }
    }
}

data class CreatorQuizData(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val reward: Int,
    val hint: String
)

data class FeedVideoData(
    val title: String,
    val channelName: String,
    val category: String,
    val rewardText: String
)

// --- SUB-SCREEN 2: SEO TAGS EXTRACTOR & GENERATOR PANEL ---

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagsExtractorPanel(viewModel: YoutubeServicesViewModel) {
    var searchInput by remember { mutableStateOf("") }
    val isGenerating by viewModel.isGenerating.collectAsState()
    val extractedTags by viewModel.extractedTags.collectAsState()
    val rawText by viewModel.seoResultText.collectAsState()

    val clipboardManager = LocalClipboardManager.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🏷️ Video Tag Extractor & Keywords Generator",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Extract highest ranking tags from any competitor's YouTube video URL, or input search keywords to generate a perfect SEO-optimized list.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = searchInput,
                        onValueChange = { searchInput = it },
                        label = { Text("Video link OR Focus Keywords") },
                        placeholder = { Text("e.g. Android development / https://...") },
                        leadingIcon = { Icon(Icons.Default.Search, "Search icon", tint = MaterialTheme.colorScheme.primary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tag_search_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color(0xFFCBD5E1),
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { viewModel.extractOrGenerateTags(searchInput) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("extract_tags_button"),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isGenerating
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Bolt, "Bolt icon")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Extract & Generate 100% Working Tags", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        if (extractedTags.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SEO Tag Results (${extractedTags.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground,
                        letterSpacing = (-0.5).sp
                    )

                    Row {
                        Button(
                            onClick = {
                                val allTags = extractedTags.joinToString(", ")
                                clipboardManager.setText(AnnotatedString(allTags))
                                viewModel.showToast("All tags copied to clipboard!")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.ContentPaste, "Copy", modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy All", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Button(
                            onClick = {
                                viewModel.saveSeoToLibrary("Tags", searchInput, rawText)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Bookmark, "Save", modifier = Modifier.size(14.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Save Set", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FlowRow(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        extractedTags.forEach { tag ->
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                    .clickable {
                                        clipboardManager.setText(AnnotatedString(tag))
                                        viewModel.showToast("Copied: '$tag'")
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = tag,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.ContentPaste,
                                    contentDescription = "Copy tag",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- SUB-SCREEN 3: AI CREATOR WIZARD (GEMINI) PANEL ---

@Composable
fun ContentWizardPanel(viewModel: YoutubeServicesViewModel) {
    var subPanelTab by remember { mutableIntStateOf(0) } // 0: AI Generator, 1: Saved Library

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = subPanelTab,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[subPanelTab]),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            Tab(
                selected = subPanelTab == 0,
                onClick = { subPanelTab = 0 },
                text = { Text("Generate AI Content", fontWeight = FontWeight.Bold) },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = Color(0xFF64748B)
            )
            Tab(
                selected = subPanelTab == 1,
                onClick = { subPanelTab = 1 },
                text = { Text("Saved Library", fontWeight = FontWeight.Bold) },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = Color(0xFF64748B)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            if (subPanelTab == 0) {
                AiGeneratorTab(viewModel = viewModel)
            } else {
                SavedLibraryTab(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun AiGeneratorTab(viewModel: YoutubeServicesViewModel) {
    var focusTopic by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Titles") } // "Titles", "Description", "Script"

    val isGenerating by viewModel.isGenerating.collectAsState()
    val generatedText by viewModel.seoResultText.collectAsState()

    val clipboardManager = LocalClipboardManager.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "✨ AI YouTube Writer Service",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Use Gemini AI to instantly write high-retention scripts, keyword-dense descriptions, and viral clickable titles for 100% free.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = focusTopic,
                        onValueChange = { focusTopic = it },
                        label = { Text("Video Topic / Focus Idea") },
                        placeholder = { Text("e.g. 10 Secret Hacks for Video Editing") },
                        leadingIcon = { Icon(Icons.Default.Bolt, "Topic icon", tint = MaterialTheme.colorScheme.primary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("wizard_topic_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color(0xFFCBD5E1),
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Mode Selection Buttons
                    Text(
                        text = "What would you like to generate?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val categories = listOf("Titles", "Description", "Script")
                        categories.forEach { cat ->
                            val isSel = selectedCategory == cat
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) MaterialTheme.colorScheme.primary else Color(0xFFF1F3F9))
                                    .clickable { selectedCategory = cat }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cat,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.generateAiContent(focusTopic, selectedCategory) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("wizard_generate_button"),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isGenerating
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Bolt, "Generate Icon")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Generate with Gemini AI", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        if (generatedText.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Generated Suggestions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground,
                        letterSpacing = (-0.5).sp
                    )

                    Row {
                        Button(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(generatedText))
                                viewModel.showToast("Copied content to clipboard!")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.ContentPaste, "Copy", modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy text", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Button(
                            onClick = {
                                viewModel.saveSeoToLibrary(selectedCategory, focusTopic, generatedText)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Bookmark, "Save", modifier = Modifier.size(14.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Save Library", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = generatedText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SavedLibraryTab(viewModel: YoutubeServicesViewModel) {
    val savedItems by viewModel.savedSeoItems.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    if (savedItems.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Bookmark,
                contentDescription = "Empty library",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(54.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Library is Empty",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = (-0.5).sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Your generated Tags, Titles, Descriptions and Scripts saved locally will appear here for easy copy-pasting.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(savedItems, key = { it.id }) { item ->
                var isExpanded by remember { mutableStateOf(false) }

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = item.category.uppercase(),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 9.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = item.inputTopic,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = 13.sp
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(item.resultText))
                                        viewModel.showToast("Copied: '${item.inputTopic}' content")
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentPaste,
                                        contentDescription = "Copy from Library",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(
                                    onClick = { viewModel.deleteSavedSeo(item.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete item",
                                        tint = Color(0xFFEF5350),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = item.resultText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (isExpanded) "Show Less" else "Expand Output",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { isExpanded = !isExpanded }
                                .padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

// --- SUB-SCREEN 4: TOOL KIT (THUMBNAIL DOWNLOADER & VIDEO AUDIT) PANEL ---

@Composable
fun ToolKitPanel(viewModel: YoutubeServicesViewModel) {
    var toolSubTab by remember { mutableIntStateOf(0) } // 0: Thumbnail, 1: SEO Audit

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = toolSubTab,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[toolSubTab]),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            Tab(
                selected = toolSubTab == 0,
                onClick = { toolSubTab = 0 },
                text = { Text("Thumbnail HD", fontWeight = FontWeight.Bold) },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = Color(0xFF64748B)
            )
            Tab(
                selected = toolSubTab == 1,
                onClick = { toolSubTab = 1 },
                text = { Text("SEO Audit Pro", fontWeight = FontWeight.Bold) },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = Color(0xFF64748B)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            if (toolSubTab == 0) {
                ThumbnailDownloaderTab(viewModel = viewModel)
            } else {
                VideoAuditTab(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ThumbnailDownloaderTab(viewModel: YoutubeServicesViewModel) {
    var videoUrlInput by remember { mutableStateOf("") }
    var extractedId by remember { mutableStateOf("") }

    val clipboardManager = LocalClipboardManager.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🖼️ HD Thumbnail Extractor & Checker",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Download original high-definition MaxRes (1080p/720p) custom cover thumbnail images from any live YouTube video.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = videoUrlInput,
                        onValueChange = {
                            videoUrlInput = it
                            extractedId = viewModel.extractVideoId(it)
                        },
                        label = { Text("YouTube Video URL") },
                        placeholder = { Text("https://www.youtube.com/watch?v=...") },
                        leadingIcon = { Icon(Icons.Default.Image, "Image Icon", tint = MaterialTheme.colorScheme.primary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("thumbnail_url_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color(0xFFCBD5E1),
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (videoUrlInput.isBlank()) {
                                viewModel.showToast("Please enter a valid video link!")
                                return@Button
                            }
                            extractedId = viewModel.extractVideoId(videoUrlInput)
                            if (extractedId.isEmpty()) {
                                viewModel.showToast("Could not extract Video ID. Please check URL!")
                            } else {
                                viewModel.showToast("Thumbnail extracted!")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("extract_thumbnail_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Download, "Download Icon")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Extract Thumbnail Cover", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        if (extractedId.isNotEmpty()) {
            val hqThumbnailUrl = "https://img.youtube.com/vi/$extractedId/maxresdefault.jpg"

            item {
                Text(
                    text = "HD Video Thumbnail Preview",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 8.dp),
                    letterSpacing = (-0.5).sp
                )
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Display Thumbnail image using Coil
                        AsyncImage(
                            model = hqThumbnailUrl,
                            contentDescription = "YouTube Thumbnail Preview",
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 180.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black),
                            alignment = Alignment.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Download URL Address:",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF1F3F9), RoundedCornerShape(6.dp))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = hqThumbnailUrl,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.ContentPaste,
                                contentDescription = "Copy thumbnail URL",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable {
                                        clipboardManager.setText(AnnotatedString(hqThumbnailUrl))
                                        viewModel.showToast("HD Link copied to clipboard!")
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VideoAuditTab(viewModel: YoutubeServicesViewModel) {
    var auditUrl by remember { mutableStateOf("") }

    val isGenerating by viewModel.isGenerating.collectAsState()
    val auditResult by viewModel.auditResult.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🔍 AI Video Optimization Audit",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Submit any competitor video URL. Our AI audit calculates SEO performance score and gives exact advice to maximize CTR, views, and indexation.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = auditUrl,
                        onValueChange = { auditUrl = it },
                        label = { Text("Video URL to Audit") },
                        placeholder = { Text("https://www.youtube.com/watch?v=...") },
                        leadingIcon = { Icon(Icons.Default.OndemandVideo, "Ondemand Video Icon", tint = MaterialTheme.colorScheme.primary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("audit_url_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color(0xFFCBD5E1),
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { viewModel.runVideoAudit(auditUrl) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("run_audit_button"),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isGenerating
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Bolt, "Bolt Icon")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Analyze & Generate Optimization Plan", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        auditResult?.let { result ->
            item {
                Text(
                    text = "📊 Audit Results Breakdown",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 8.dp),
                    letterSpacing = (-0.5).sp
                )
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Circular animated Score Gauge
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(80.dp)
                            ) {
                                val scoreProgressState = animateFloatAsState(
                                    targetValue = result.score.toFloat() / 100f,
                                    animationSpec = tween(1200), label = "Score gauge"
                                )
                                Canvas(modifier = Modifier.size(70.dp)) {
                                    drawArc(
                                        color = Color(0xFFE2E8F0),
                                        startAngle = -90f,
                                        sweepAngle = 360f,
                                        useCenter = false,
                                        style = Stroke(width = 6.dp.toPx())
                                    )
                                    drawArc(
                                        color = if (result.score >= 85) ProgressGreen else ProgressGold,
                                        startAngle = -90f,
                                        sweepAngle = 360f * scoreProgressState.value,
                                        useCenter = false,
                                        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${result.score}",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "SCORE",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Video Searchability Index",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = when {
                                        result.score >= 85 -> "Excellent search optimization! Ready to rank on top pages."
                                        result.score >= 70 -> "Moderate optimization. Apply key changes to index properly."
                                        else -> "Critical errors found. Complete changes to start getting organic views."
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Score metrics progress bars
                        ScoreMetricBar("Video Title CTR Rank", result.titleScore)
                        ScoreMetricBar("Description Keyword Index", result.descriptionScore)
                        ScoreMetricBar("Tags Relevancy Density", result.tagsScore)
                        ScoreMetricBar("Thumbnail Click Appeal", result.thumbnailScore)
                    }
                }
            }

            item {
                Text(
                    text = "💡 4 Instantly Working Recommendations",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 8.dp),
                    letterSpacing = (-0.5).sp
                )
            }

            // Recommendations List
            items(result.recommendations) { rec ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "Check list",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = rec,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreMetricBar(label: String, score: Int) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = "$score/100", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { score.toFloat() / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(CircleShape),
            color = if (score >= 80) ProgressGreen else ProgressGold,
            trackColor = Color(0xFFE2E8F0)
        )
    }
}
