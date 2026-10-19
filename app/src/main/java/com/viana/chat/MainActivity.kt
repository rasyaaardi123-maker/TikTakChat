package com.viana.chat

import android.content.Intent
import android.graphics.Color as AndroidColor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.ui.res.painterResource
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.drawPlainBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.runtimeShaderEffect
import com.kyant.backdrop.effects.vibrancy
import com.kyant.backdrop.highlight.Highlight
import com.kyant.backdrop.shadow.InnerShadow
import com.kyant.backdrop.shadow.Shadow
import com.kyant.backdrop.catalog.components.LiquidBottomTab
import com.kyant.backdrop.catalog.components.LiquidBottomTabs
import com.kyant.backdrop.catalog.components.LiquidToggle
import com.kyant.shapes.Capsule
import com.kyant.shapes.RoundedRectangle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.LaunchedEffect

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = AndroidColor.TRANSPARENT
        window.navigationBarColor = AndroidColor.TRANSPARENT
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
        setContent {
            MaterialTheme {
                TikTakChatApp()
            }
        }
    }
}

private enum class ChatPage(val title: String, val icon: ImageVector) {
    STATUS("Status", AppIcons.Status),
    CALLS("Calls", AppIcons.Call),
    CHATS("Chats", AppIcons.Chat),
    PROFILE("Settings", AppIcons.Profile)
}

private data class ChatPreview(
    val name: String,
    val message: String,
    val time: String,
    val unread: Int = 0,
    val initials: String
)

private data class StatusPreview(
    val name: String,
    val time: String,
    val initials: String,
    val viewed: Boolean = false
)

private data class CallPreview(
    val name: String,
    val detail: String,
    val time: String,
    val missed: Boolean = false,
    val video: Boolean = false,
    val initials: String
)

private data class SettingItem(
    val title: String,
    val detail: String = "",
    val isToggle: Boolean = false
)

private data class SearchResult(
    val title: String,
    val detail: String,
    val page: ChatPage
)

private data class CallShortcut(val name: String, val number: String)

private val initialChats = listOf(
    ChatPreview("Naya", "Nanti jadi ketemu?", "10:42", 2, "NA"),
    ChatPreview("Dimas", "Oke, makasih!", "10:18", initials = "DI"),
    ChatPreview("Raka", "Kirim fotonya ya", "Yesterday", initials = "RA"),
    ChatPreview("Mila", "Voice message", "Yesterday", initials = "MI"),
    ChatPreview("Keluarga", "Ibu: Jangan lupa makan", "Monday", 4, "KE"),
    ChatPreview("Project Team", "Meeting dimulai jam 2", "Monday", initials = "PT")
)

private val statusPreviews = listOf(
    StatusPreview("Naya", "10:30", "NA"),
    StatusPreview("Dimas", "09:15", "DI"),
    StatusPreview("Mila", "Yesterday", "MI", viewed = true),
    StatusPreview("Raka", "Yesterday", "RA", viewed = true)
)

private val callPreviews = listOf(
    CallPreview("Naya", "Incoming voice call", "10:42", initials = "NA"),
    CallPreview("Dimas", "Outgoing video call", "Yesterday", video = true, initials = "DI"),
    CallPreview("Raka", "Missed voice call", "Yesterday", missed = true, initials = "RA"),
    CallPreview("Mila", "Incoming video call", "Monday", video = true, initials = "MI")
)

private val callShortcuts = listOf(
    CallShortcut("Naya", "+62 812 3456 7001"),
    CallShortcut("Dimas", "+62 812 3456 7002"),
    CallShortcut("Raka", "+62 812 3456 7003"),
    CallShortcut("Mila", "+62 812 3456 7004"),
    CallShortcut("Keluarga", "+62 812 3456 7005"),
    CallShortcut("Alya", "+62 812 3456 7006"),
    CallShortcut("Bima", "+62 812 3456 7007"),
    CallShortcut("Tara", "+62 812 3456 7008")
)

private val chatActions = listOf(
    "New Chat",
    "Contacts",
    "New Group",
    "Broadcast Lists",
    "Linked Devices",
    "Mark All as Read",
    "Starred Messages"
)

private val statusActions = listOf("Upload Status", "Camera", "Gallery", "Text", "Poll")

private val settingGroups = listOf(
    listOf(SettingItem("Profile", "Name, photo, about")),
    listOf(SettingItem("Starred Messages"), SettingItem("Lovers")),
    listOf(
        SettingItem("Account"),
        SettingItem("Privacy"),
        SettingItem("Chats"),
        SettingItem("Notifications"),
        SettingItem("Storage and Data")
    ),
    listOf(
        SettingItem("Theme"),
        SettingItem("Liquid Glass", isToggle = true),
        SettingItem("Wallpaper"),
        SettingItem("Bubble Chat"),
        SettingItem("Text")
    ),
    listOf(
        SettingItem("Camera"),
        SettingItem("Audio / VN"),
        SettingItem("Video Chat"),
        SettingItem("Messenger Rich")
    ),
    listOf(SettingItem("Sticker"), SettingItem("Payment")),
    listOf(
        SettingItem("Language"),
        SettingItem("Accessibility"),
        SettingItem("Security"),
        SettingItem("About")
    )
)

@Composable
private fun TikTakChatApp() {
    val backdrop = rememberLayerBackdrop()
    val contentBackdrop = rememberLayerBackdrop()
    val pagerState = rememberPagerState(initialPage = 2, pageCount = { ChatPage.entries.size })
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val searchFocusRequester = remember { FocusRequester() }
    var searchOpen by rememberSaveable { mutableStateOf(false) }
    var searchClosing by remember { mutableStateOf(false) }
    var searchText by rememberSaveable { mutableStateOf("") }
    var actionMenuOpen by rememberSaveable { mutableStateOf(false) }
    var liquidGlassEnabled by rememberSaveable { mutableStateOf(true) }
    var selectedSetting by remember { mutableStateOf<SettingItem?>(null) }
    var selectedChat by remember { mutableStateOf<ChatPreview?>(null) }
    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedSetting = SettingItem("Status photo selected", uri.lastPathSegment.orEmpty())
        }
    }
    val currentPage = ChatPage.entries[pagerState.currentPage]
    val searchResults = remember(searchText) { searchAllPages(searchText) }

    BackHandler(searchOpen || actionMenuOpen) {
        if (searchOpen) {
            searchClosing = true
            keyboardController?.hide()
        } else {
            actionMenuOpen = false
        }
    }

    LaunchedEffect(searchOpen, searchClosing) {
        if (searchOpen && !searchClosing) {
            delay(360)
            searchFocusRequester.requestFocus()
            keyboardController?.show()
        } else {
            keyboardController?.hide()
        }
    }
    LaunchedEffect(pagerState.currentPage) {
        actionMenuOpen = false
    }

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        Box(Modifier.fillMaxSize().layerBackdrop(contentBackdrop)) {
            Box(Modifier.fillMaxSize().layerBackdrop(backdrop)) {
                HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                beyondViewportPageCount = 1
            ) { page ->
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AnimatedContent(
                        targetState = ChatPage.entries[page],
                        modifier = Modifier.fillMaxSize(),
                        transitionSpec = {
                            fadeIn(animationSpec = tween(160)) togetherWith fadeOut(animationSpec = tween(110))
                        },
                        label = "chat-page-content"
                    ) { selectedPage ->
                        when (selectedPage) {
                            ChatPage.STATUS -> StatusScreen(
                                query = "",
                                onStatusClick = { actionMenuOpen = true }
                            )
                            ChatPage.CALLS -> CallsScreen(
                                query = "",
                                onCallClick = { call ->
                                    selectedSetting = SettingItem(call.name, call.detail)
                                }
                            )
                            ChatPage.CHATS -> ChatsScreen(
                                query = "",
                                onChatClick = { selectedChat = it }
                            )
                            ChatPage.PROFILE -> ProfileScreen(
                                query = "",
                                liquidGlassEnabled = liquidGlassEnabled,
                                backdrop = backdrop,
                                onLiquidGlassChange = { liquidGlassEnabled = it },
                                onSettingClick = { selectedSetting = it }
                            )
                        }
                    }
                }
            }
            }
            ProgressiveBlurEdge(backdrop, top = true, glassEnabled = liquidGlassEnabled, modifier = Modifier.align(Alignment.TopCenter))
            ProgressiveBlurEdge(backdrop, top = false, glassEnabled = liquidGlassEnabled, modifier = Modifier.align(Alignment.BottomCenter))

            BottomNavigation(
                backdrop = backdrop,
                selectedIndex = pagerState.currentPage,
                onTabSelected = { index ->
                    actionMenuOpen = false
                    scope.launch { pagerState.animateScrollToPage(index) }
                },
                searchOpen = searchOpen,
                glassEnabled = liquidGlassEnabled,
                onSearchClick = {
                    actionMenuOpen = false
                    searchClosing = false
                    searchOpen = true
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
            Box(
                Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 12.dp)
            ) {
                PageTitle(
                    page = currentPage,
                    menuOpen = actionMenuOpen && !searchOpen,
                    glassEnabled = liquidGlassEnabled,
                    backdrop = backdrop,
                    onDismiss = { actionMenuOpen = false },
                    onClick = { actionMenuOpen = !actionMenuOpen }
                )
            }
        }

        AnimatedVisibility(
            visible = actionMenuOpen && !searchOpen,
            modifier = Modifier.fillMaxSize(),
            enter = fadeIn(tween(110)) + expandVertically(
                expandFrom = Alignment.Top,
                animationSpec = spring(dampingRatio = 0.72f, stiffness = 420f)
            ),
            exit = fadeOut(tween(90)) + shrinkVertically(
                shrinkTowards = Alignment.Top,
                animationSpec = spring(dampingRatio = 0.72f, stiffness = 520f)
            ),
            label = "page-action-popup"
        ) {
            PageActionOverlay(
                page = currentPage,
                backdrop = contentBackdrop,
                glassEnabled = liquidGlassEnabled,
                onDismiss = { actionMenuOpen = false },
                onAction = { action ->
                    actionMenuOpen = false
                    when (currentPage) {
                        ChatPage.STATUS -> when (action) {
                            "Upload Status", "Gallery" -> photoPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                            "Camera" -> runCatching {
                                context.startActivity(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
                            }
                            else -> selectedSetting = SettingItem(action, "Create a $action status")
                        }
                        ChatPage.CALLS -> {
                            val number = callShortcuts.firstOrNull { it.name == action }?.number.orEmpty()
                            if (number.isNotEmpty()) {
                                context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number")))
                            }
                        }
                        else -> selectedSetting = SettingItem(action, "")
                    }
                }
            )
        }

        AnimatedVisibility(
            visible = searchOpen,
            modifier = Modifier.fillMaxSize(),
            enter = fadeIn(tween(80)),
            exit = fadeOut(tween(90)),
            label = "global-search-scrim"
        ) {
            GlobalSearchOverlay(
                query = searchText,
                results = searchResults,
                backdrop = contentBackdrop,
                glassEnabled = liquidGlassEnabled,
                focusRequester = searchFocusRequester,
                closing = searchClosing,
                onQueryChange = { searchText = it },
                onClose = { searchClosing = true },
                onCloseComplete = {
                    searchOpen = false
                    searchClosing = false
                    searchText = ""
                },
                onResult = { result ->
                    scope.launch { pagerState.animateScrollToPage(result.page.ordinal) }
                    when (result.page) {
                        ChatPage.CHATS -> initialChats.firstOrNull { it.name == result.title }?.let { selectedChat = it }
                        else -> selectedSetting = SettingItem(result.title, result.detail)
                    }
                    searchClosing = true
                }
            )
        }
    }

    selectedChat?.let { chat ->
        ChatConversationDialog(
            chat = chat,
            onDismiss = { selectedChat = null }
        )
    }

    selectedSetting?.let { setting ->
        AlertDialog(
            onDismissRequest = { selectedSetting = null },
            title = { Text(setting.title, color = Color(0xFFF1F5F6)) },
            text = { Text(setting.detail.ifBlank { "TikTakChat setting" }, color = Color(0xFFB8C5CA)) },
            confirmButton = {
                TextButton(onClick = { selectedSetting = null }) { Text("OK", color = Color(0xFF9DDDD5)) }
            },
            backgroundColor = Color(0xFF192226),
            shape = RoundedCornerShape(24.dp)
        )
    }
}

private fun searchAllPages(query: String): List<SearchResult> {
    if (query.isBlank()) return emptyList()
    val matches = mutableListOf<SearchResult>()
    initialChats.filter {
        it.name.contains(query, true) || it.message.contains(query, true)
    }.forEach { matches += SearchResult(it.name, it.message, ChatPage.CHATS) }
    statusPreviews.filter { it.name.contains(query, true) }.forEach {
        matches += SearchResult(it.name, "Status update", ChatPage.STATUS)
    }
    statusActions.filter { it.contains(query, true) }.forEach {
        matches += SearchResult(it, "Status action", ChatPage.STATUS)
    }
    callPreviews.filter { it.name.contains(query, true) || it.detail.contains(query, true) }.forEach {
        matches += SearchResult(it.name, it.detail, ChatPage.CALLS)
    }
    callShortcuts.filter { it.name.contains(query, true) || it.number.contains(query, true) }.forEach {
        matches += SearchResult(it.name, it.number, ChatPage.CALLS)
    }
    chatActions.filter { it.contains(query, true) }.forEach {
        matches += SearchResult(it, "Chat action", ChatPage.CHATS)
    }
    settingGroups.flatten().filter {
        it.title.contains(query, true) || it.detail.contains(query, true)
    }.forEach {
        matches += SearchResult(it.title, it.detail.ifBlank { "Settings" }, ChatPage.PROFILE)
    }
    return matches
}

@Composable
private fun PageTitle(
    page: ChatPage,
    menuOpen: Boolean,
    glassEnabled: Boolean,
    backdrop: LayerBackdrop,
    onDismiss: () -> Unit,
    onClick: () -> Unit
) {
    BoxWithConstraints(Modifier.fillMaxWidth().height(56.dp), contentAlignment = Alignment.Center) {
        if (page == ChatPage.PROFILE) {
            Text(
                page.title,
                color = Color(0xFFF4F7F8),
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            val targetWidth = if (menuOpen) maxWidth - 36.dp else 118.dp
            val width by animateDpAsState(
                targetValue = targetWidth,
                animationSpec = spring(dampingRatio = 0.62f, stiffness = 420f),
                label = "page-title-morph-width"
            )
            val height by animateDpAsState(
                targetValue = if (menuOpen) 52.dp else 44.dp,
                animationSpec = spring(dampingRatio = 0.62f, stiffness = 460f),
                label = "page-title-morph-height"
            )
            Row(
            modifier = Modifier
                .width(width)
                .height(height)
                .clip(Capsule())
                .then(if (glassEnabled) Modifier.drawBackdrop(
                    backdrop = backdrop,
                    shape = { Capsule() },
                    effects = {
                        vibrancy()
                        blur(8.dp.toPx())
                        lens(16.dp.toPx(), 24.dp.toPx())
                    },
                    highlight = { Highlight.Default.copy(alpha = if (menuOpen) 0.38f else 0.26f) },
                    shadow = { Shadow(alpha = 0.1f) },
                    innerShadow = { InnerShadow(radius = 5.dp, alpha = 0.14f) },
                    onDrawSurface = { drawRect(Color.White.copy(alpha = 0.19f)) }
                ) else Modifier.background(Color(0xFF263136), Capsule()))
                .clickable(enabled = page != ChatPage.PROFILE && !menuOpen, onClick = onClick)
                .padding(horizontal = if (menuOpen) 12.dp else 22.dp),
            verticalAlignment = Alignment.CenterVertically
            ) {
                if (menuOpen) {
                    Text(
                        page.title,
                        color = Color(0xFFF4F7F8),
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.subtitle1,
                        modifier = Modifier.weight(1f).padding(start = 4.dp)
                    )
                    IconButtonGlass(
                        imageVector = AppIcons.Close,
                        description = "Close ${page.title} menu",
                        onClick = onDismiss,
                        backdrop = backdrop,
                        glassEnabled = glassEnabled,
                        size = 38.dp
                    )
                } else {
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text(
                            page.title,
                            color = Color(0xFFF4F7F8),
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.subtitle1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PageActionOverlay(
    page: ChatPage,
    backdrop: LayerBackdrop,
    glassEnabled: Boolean,
    onDismiss: () -> Unit,
    onAction: (String) -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(top = 94.dp)
                .drawPlainBackdrop(
                    backdrop = backdrop,
                    shape = { RectangleShape },
                    effects = { if (glassEnabled) blur(36.dp.toPx()) }
                )
                .clickable(onClick = onDismiss)
        )
        Column(
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 18.dp, end = 18.dp, top = 76.dp, bottom = 14.dp)
        ) {
            Spacer(Modifier.height(4.dp))
            if (glassEnabled) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .drawBackdrop(
                            backdrop = backdrop,
                            shape = { RoundedRectangle(24.dp) },
                            effects = {
                                vibrancy()
                                blur(14.dp.toPx())
                                lens(14.dp.toPx(), 22.dp.toPx())
                            },
                            highlight = { Highlight.Default.copy(alpha = 0.3f) },
                            shadow = { Shadow(alpha = 0.14f) },
                            innerShadow = { InnerShadow(radius = 8.dp, alpha = 0.12f) },
                            onDrawSurface = { drawRect(Color.White.copy(alpha = 0.16f)) }
                        )
                        .padding(horizontal = 16.dp, vertical = 5.dp)
                ) {
                    ActionRows(page, onAction)
                }
            } else {
                Column(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF20292D)).padding(horizontal = 16.dp, vertical = 5.dp)
                ) { ActionRows(page, onAction) }
            }
        }
    }
}

@Composable
private fun ActionRows(page: ChatPage, onAction: (String) -> Unit) {
    when (page) {
        ChatPage.STATUS -> statusActions.forEachIndexed { index, action ->
            PopupActionRow(action, isLast = index == statusActions.lastIndex) { onAction(action) }
        }
        ChatPage.CALLS -> LazyColumn(modifier = Modifier.heightIn(max = 62.dp * 5)) {
            items(callShortcuts, key = { it.number }) { shortcut ->
                PopupActionRow("${shortcut.name}  ${shortcut.number}") { onAction(shortcut.name) }
            }
        }
        ChatPage.CHATS -> chatActions.forEachIndexed { index, action ->
            PopupActionRow(action, isLast = index == chatActions.lastIndex) { onAction(action) }
        }
        ChatPage.PROFILE -> {
            val settings = settingGroups.flatten()
            settings.forEachIndexed { index, setting ->
                PopupActionRow(setting.title, isLast = index == settings.lastIndex) {
                    onAction(setting.title)
                }
            }
        }
    }
}

@Composable
private fun PopupActionRow(action: String, isLast: Boolean = false, onClick: () -> Unit) {
    Column {
        Row(
            Modifier.fillMaxWidth().height(54.dp).clickable(onClick = onClick).padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(action, color = Color(0xFFF1F5F6), fontSize = 15.sp, modifier = Modifier.weight(1f))
            Text(">", color = Color(0xFFB6C4C9), fontSize = 20.sp)
        }
        if (!isLast) Divider(color = Color.White.copy(alpha = 0.18f), thickness = 0.7.dp)
    }
}

@Composable
private fun GlobalSearchOverlay(
    query: String,
    results: List<SearchResult>,
    backdrop: LayerBackdrop,
    glassEnabled: Boolean,
    focusRequester: FocusRequester,
    closing: Boolean,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit,
    onCloseComplete: () -> Unit,
    onResult: (SearchResult) -> Unit
) {
    val morphProgress = remember { Animatable(0f) }
    LaunchedEffect(closing) {
        if (closing) {
            morphProgress.animateTo(0f, spring(dampingRatio = 0.58f, stiffness = 420f))
            onCloseComplete()
        } else {
            morphProgress.animateTo(1f, spring(dampingRatio = 0.58f, stiffness = 360f))
        }
    }
    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleY = morphProgress.value
                    transformOrigin = TransformOrigin(0.5f, 0f)
                }
                .drawPlainBackdrop(
                    backdrop = backdrop,
                    shape = { RectangleShape },
                    effects = { if (glassEnabled) blur(34.dp.toPx()) }
                )
                .clickable(onClick = {})
        )
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val progress = morphProgress.value.coerceIn(0f, 1f)
            val collapsedWidth = 56.dp
            val expandedWidth = maxWidth - 32.dp
            val composerWidth = collapsedWidth + (expandedWidth - collapsedWidth) * progress
            val startX = maxWidth - collapsedWidth - 14.dp
            val endX = 16.dp
            val composerX = startX + (endX - startX) * progress
            val startY = maxHeight - 112.dp
            val endY = 34.dp
            val composerY = startY + (endY - startY) * progress

            Column(
                Modifier
                    .fillMaxSize()
                    .imePadding()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp)
                    .padding(top = 104.dp)
            ) {
                if (query.isNotBlank()) {
                    if (results.isEmpty()) {
                        Text("No results", color = Color(0xFFB8C5CA), modifier = Modifier.padding(12.dp))
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 10.dp)
                        ) {
                            items(results, key = { "${it.page.name}-${it.title}-${it.detail}" }) { result ->
                                Row(
                                    Modifier.fillMaxWidth().height(64.dp).clickable { onResult(result) }.padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(Modifier.weight(1f)) {
                                        Text(result.title, color = Color.White, fontWeight = FontWeight.SemiBold)
                                        Text(result.detail, color = Color(0xFFB8C5CA), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                    Text(result.page.title, color = Color(0xFFB8C5CA), fontSize = 12.sp)
                                }
                                Divider(color = Color.White.copy(alpha = 0.14f), thickness = 0.7.dp)
                            }
                        }
                    }
                }
            }

            Row(
                Modifier
                    .offset(x = composerX, y = composerY)
                    .width(composerWidth)
                    .height(56.dp)
                    .clip(Capsule())
                    .then(if (glassEnabled) Modifier.drawBackdrop(
                        backdrop = backdrop,
                        shape = { Capsule() },
                        effects = {
                            vibrancy()
                            blur(12.dp.toPx())
                            lens(18.dp.toPx(), 26.dp.toPx())
                        },
                        highlight = { Highlight.Default.copy(alpha = 0.4f) },
                        shadow = { Shadow(alpha = 0.16f) },
                        innerShadow = { InnerShadow(radius = 7.dp, alpha = 0.16f) },
                        onDrawSurface = { drawRect(Color.White.copy(alpha = 0.2f)) }
                    ) else Modifier.background(Color(0xFF263136), Capsule()))
                    .padding(start = 18.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(AppIcons.Search, contentDescription = null, tint = Color(0xFFEAF1F3), modifier = Modifier.size(21.dp))
                Spacer(Modifier.width(12.dp))
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    textStyle = MaterialTheme.typography.subtitle1.copy(color = Color.White),
                    decorationBox = { innerTextField ->
                        Box(Modifier.weight(1f)) {
                            if (query.isEmpty()) Text("Search everything", color = Color(0xFFB8C5CA))
                            innerTextField()
                        }
                    },
                    modifier = Modifier.weight(1f).alpha(progress).focusRequester(focusRequester)
                )
                if (progress >= 0.9f) {
                    IconButtonGlass(
                        imageVector = AppIcons.Close,
                        description = "Close search",
                        onClick = onClose,
                        backdrop = backdrop,
                        glassEnabled = glassEnabled,
                        size = 40.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgressiveBlurEdge(
    backdrop: LayerBackdrop,
    top: Boolean,
    glassEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .fillMaxWidth()
            .height(220.dp)
            .drawPlainBackdrop(
                backdrop = backdrop,
                shape = { RectangleShape },
                effects = {
                    if (glassEnabled) {
                        blur(32.dp.toPx())
                        runtimeShaderEffect(
                        "AlphaMask",
                        """
uniform shader content;
uniform float2 size;
uniform float fadeFromTop;
layout(color) uniform half4 tint;
half4 main(float2 coord) {
    float progress = coord.y / size.y;
    float fade = fadeFromTop > 0.5
        ? 1.0 - smoothstep(0.0, 1.0, progress)
        : smoothstep(0.0, 1.0, progress);
    half4 blurred = content.eval(coord);
    half4 tinted = mix(blurred, tint, 0.25);
    return tinted * fade;
}
""",
                        "content"
                    ) {
                        setFloatUniform("size", size.width, size.height)
                        setFloatUniform("fadeFromTop", if (top) 1f else 0f)
                        setColorUniform("tint", Color(0xFF101619))
                    }
                    }
                }
            )
    )
}

@Composable
private fun FloatingTitle(
    title: String,
    query: String,
    searching: Boolean,
    onQueryChange: (String) -> Unit,
    onCloseSearch: () -> Unit,
    backdrop: LayerBackdrop
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(52.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .then(if (searching) Modifier.width(280.dp) else Modifier)
                .clip(Capsule())
                .drawBackdrop(
                    backdrop = backdrop,
                    shape = { Capsule() },
                    effects = {
                        vibrancy()
                        blur(8.dp.toPx())
                        lens(12.dp.toPx(), 16.dp.toPx())
                    },
                    highlight = { Highlight.Default.copy(alpha = 0.28f) },
                    shadow = { Shadow(alpha = 0.08f) },
                    innerShadow = { InnerShadow(radius = 4.dp, alpha = 0.12f) },
                    onDrawSurface = { drawRect(Color.White.copy(alpha = 0.24f)) }
                )
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (searching) {
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    textStyle = MaterialTheme.typography.subtitle1.copy(color = Color(0xFFF1F5F6)),
                    decorationBox = { innerTextField ->
                        Box {
                            if (query.isEmpty()) Text("Search chats and settings", color = Color(0xFFB8C5CA))
                            innerTextField()
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
                IconButtonGlass(
                    imageVector = AppIcons.Close,
                    description = "Close search",
                    onClick = onCloseSearch,
                    backdrop = backdrop,
                    glassEnabled = true,
                    size = 28.dp
                )
            } else {
                Text(
                    text = title,
                    style = MaterialTheme.typography.subtitle1.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFF1F5F6)
                    )
                )
            }
        }
    }
}

@Composable
private fun ChatsScreen(query: String, onChatClick: (ChatPreview) -> Unit) {
    val chats = initialChats.filter {
        it.name.contains(query, ignoreCase = true) || it.message.contains(query, ignoreCase = true)
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 74.dp, bottom = 120.dp)
    ) {
            items(chats, key = { it.name }) { chat -> 
            ChatRow(chat = chat, onClick = { onChatClick(chat) })
        }
    }
}

@Composable
private fun ChatRow(chat: ChatPreview, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(76.dp).clickable(onClick = onClick).padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(initials = chat.initials, size = 52.dp)
        Spacer(Modifier.width(13.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(chat.name, color = Color(0xFFF1F5F6), fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                Text(chat.time, color = Color(0xFFB8C5CA), fontSize = 12.sp)
            }
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(chat.message, color = Color(0xFFD9E2E5), maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                if (chat.unread > 0) {
                    Spacer(Modifier.width(8.dp))
                    Box(Modifier.size(20.dp).background(Color(0xFF148B86), CircleShape), contentAlignment = Alignment.Center) {
                        Text(chat.unread.toString(), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Divider(color = Color.White.copy(alpha = 0.42f), thickness = 0.7.dp)
        }
    }
}

@Composable
private fun StatusScreen(query: String, onStatusClick: () -> Unit) {
    val statuses = statusPreviews.filter { it.name.contains(query, ignoreCase = true) }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 74.dp, bottom = 120.dp)
    ) {
            item { 
            Row(
                Modifier.fillMaxWidth().height(78.dp).clickable(onClick = onStatusClick),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    Avatar(initials = "ME", size = 54.dp)
                    Box(Modifier.align(Alignment.BottomEnd).size(21.dp).background(Color(0xFF138B86), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(AppIcons.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text("My status", color = Color(0xFFF1F5F6), fontWeight = FontWeight.SemiBold)
                    Text("Tap to add a status update", color = Color(0xFFB8C5CA), fontSize = 13.sp)
                }
            }
            SectionLabel("RECENT UPDATES")
        }
        items(statuses, key = { it.name }) { status ->
            Row(
                Modifier.fillMaxWidth().height(74.dp).clickable(onClick = onStatusClick),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Avatar(
                    initials = status.initials,
                    size = 54.dp,
                    ring = if (status.viewed) Color(0xFF95A6B2) else Color(0xFF138B86)
                )
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(status.name, color = Color(0xFFF1F5F6), fontWeight = FontWeight.SemiBold)
                    Text(status.time, color = Color(0xFFB8C5CA), fontSize = 13.sp)
                }
            }
            Divider(color = Color.White.copy(alpha = 0.42f), thickness = 0.7.dp)
        }
    }
}

@Composable
private fun CallsScreen(query: String, onCallClick: (CallPreview) -> Unit) {
    val calls = callPreviews.filter { it.name.contains(query, ignoreCase = true) || it.detail.contains(query, ignoreCase = true) }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 74.dp, bottom = 120.dp)
    ) {
            item { SectionLabel("RECENT") } 
        items(calls, key = { it.name }) { call ->
            Row(
                Modifier.fillMaxWidth().height(76.dp).clickable { onCallClick(call) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Avatar(initials = call.initials, size = 52.dp)
                Spacer(Modifier.width(13.dp))
                Column(Modifier.weight(1f)) {
                    Text(call.name, color = if (call.missed) Color(0xFFFF8B80) else Color(0xFFF1F5F6), fontWeight = FontWeight.SemiBold)
                    Text(call.detail, color = Color(0xFFB8C5CA), fontSize = 13.sp)
                }
                Text(call.time, color = Color(0xFFB8C5CA), fontSize = 12.sp)
                Spacer(Modifier.width(12.dp))
                Icon(
                    imageVector = if (call.video) AppIcons.Video else AppIcons.Call,
                    contentDescription = call.detail,
                    tint = Color(0xFF138B86),
                    modifier = Modifier.size(22.dp)
                )
            }
            Divider(color = Color.White.copy(alpha = 0.42f), thickness = 0.7.dp)
        }
    }
}

@Composable
private fun ProfileScreen(
    query: String,
    liquidGlassEnabled: Boolean,
    backdrop: LayerBackdrop,
    onLiquidGlassChange: (Boolean) -> Unit,
    onSettingClick: (SettingItem) -> Unit
) {
    val groups = settingGroups
        .map { group -> group.filter { it.title.contains(query, ignoreCase = true) } }
        .filter { it.isNotEmpty() }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 74.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
            itemsIndexed(groups, key = { index, group -> "settings-$index-${group.first().title}" }) { _, group -> 
            SettingGroup(
                settings = group,
                backdrop = backdrop,
                glassEnabled = liquidGlassEnabled,
                liquidGlassEnabled = liquidGlassEnabled,
                onLiquidGlassChange = onLiquidGlassChange,
                onSettingClick = onSettingClick
            )
        }
    }
}

@Composable
private fun SettingGroup(
    settings: List<SettingItem>,
    backdrop: LayerBackdrop,
    glassEnabled: Boolean,
    liquidGlassEnabled: Boolean,
    onLiquidGlassChange: (Boolean) -> Unit,
    onSettingClick: (SettingItem) -> Unit
) {
    val glassModifier = if (glassEnabled) {
        Modifier.drawBackdrop(
            backdrop = backdrop,
            shape = { RoundedRectangle(20.dp) },
            effects = {
                vibrancy()
                blur(8.dp.toPx())
                lens(12.dp.toPx(), 18.dp.toPx())
            },
            highlight = { Highlight.Default.copy(alpha = 0.16f) },
            shadow = { Shadow(alpha = 0.08f) },
            innerShadow = { InnerShadow(radius = 5.dp, alpha = 0.1f) },
            onDrawSurface = { drawRect(Color.White.copy(alpha = 0.13f)) }
        )
    } else {
        Modifier.background(Color(0xFF20292D), RoundedCornerShape(20.dp))
    }
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).then(glassModifier)
    ) {
        settings.forEachIndexed { index, setting ->
            SettingRow(
                setting = setting,
                backdrop = backdrop,
                glassEnabled = glassEnabled,
                liquidGlassEnabled = liquidGlassEnabled,
                onLiquidGlassChange = onLiquidGlassChange,
                onClick = { onSettingClick(setting) }
            )
            if (index != settings.lastIndex) {
                Divider(
                    color = Color.White.copy(alpha = 0.16f),
                    thickness = 0.7.dp,
                    modifier = Modifier.padding(start = 18.dp)
                )
            }
        }
    }
}

@Composable
private fun SettingRow(
    setting: SettingItem,
    backdrop: LayerBackdrop,
    glassEnabled: Boolean,
    liquidGlassEnabled: Boolean,
    onLiquidGlassChange: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (setting.detail.isBlank()) 52.dp else 70.dp)
            .then(if (setting.isToggle) Modifier else Modifier.clickable(onClick = onClick))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(setting.title, color = Color(0xFFF1F5F6), fontSize = 15.sp)
            if (setting.detail.isNotBlank()) Text(setting.detail, color = Color(0xFFB8C5CA), fontSize = 12.sp)
        }
        if (setting.isToggle) {
            LiquidToggle(
                selected = { liquidGlassEnabled },
                onSelect = onLiquidGlassChange,
                backdrop = backdrop,
                modifier = Modifier.size(width = 64.dp, height = 30.dp)
            )
        } else {
            Text(">", color = Color(0xFFB8C5CA), fontSize = 20.sp)
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = Color(0xFFB8C5CA),
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
    )
}

@Composable
private fun Avatar(initials: String, size: Dp, ring: Color? = null) {
    Box(
        modifier = Modifier
            .size(size)
            .then(if (ring != null) Modifier.border(2.dp, ring, CircleShape).padding(3.dp) else Modifier)
            .background(Color(0xFFB9D8DE), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(initials, color = Color(0xFF164E63), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}

@Composable
private fun BottomNavigation(
    backdrop: LayerBackdrop,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    searchOpen: Boolean,
    glassEnabled: Boolean,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedTabIndex: () -> Int = remember(selectedIndex) { { selectedIndex } }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 14.dp, end = 14.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LiquidBottomTabs(
            selectedTabIndex = selectedTabIndex,
            onTabSelected = onTabSelected,
            backdrop = backdrop,
            tabsCount = ChatPage.entries.size,
            modifier = Modifier.weight(1f).height(64.dp)
        ) {
            ChatPage.entries.forEachIndexed { index, page ->
                LiquidBottomTab(
                    onClick = { onTabSelected(index) },
                    modifier = Modifier.semantics { contentDescription = page.title }
                ) {
                    Icon(
                        imageVector = page.icon,
                        contentDescription = null,
                        tint = Color(0xFFEAF1F3),
                        modifier = Modifier.size(25.dp)
                    )
                }
            }
        }
        IconButtonGlass(
            imageVector = if (searchOpen) AppIcons.Close else AppIcons.Search,
            description = "Search",
            onClick = onSearchClick,
            backdrop = backdrop,
            glassEnabled = glassEnabled,
            size = 64.dp
        )
    }
}

@Composable
private fun IconButtonGlass(
    imageVector: ImageVector,
    description: String,
    onClick: () -> Unit,
    backdrop: LayerBackdrop,
    glassEnabled: Boolean,
    size: Dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .then(if (glassEnabled) Modifier.drawBackdrop(
                backdrop = backdrop,
                shape = { CircleShape },
                effects = {
                    vibrancy()
                    blur(8.dp.toPx())
                    lens(12.dp.toPx(), 18.dp.toPx())
                },
                highlight = { Highlight.Default.copy(alpha = 0.35f) },
                shadow = { Shadow(alpha = 0.1f) },
                innerShadow = { InnerShadow(radius = 5.dp, alpha = 0.12f) },
                onDrawSurface = { drawRect(Color.White.copy(alpha = 0.26f)) }
            ) else Modifier.background(Color(0xFF293438), CircleShape))
            .clickable(onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector, contentDescription = null, tint = Color(0xFFEAF1F3), modifier = Modifier.size(24.dp))
    }
}

@Composable
private fun ChatConversationDialog(chat: ChatPreview, onDismiss: () -> Unit) {
    var messageText by rememberSaveable(chat.name) { mutableStateOf("") }
    val messages = remember(chat.name) {
        mutableStateListOf("Hey, how are you?", chat.message)
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(chat.name) },
        text = {
            Column {
                messages.forEachIndexed { index, message ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (index % 2 == 0) Arrangement.Start else Arrangement.End) {
                        Text(
                            message,
                            color = Color.White,
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (index % 2 == 0) Color(0xFF2C383D) else Color(0xFF295F57))
                                .padding(horizontal = 12.dp, vertical = 9.dp)
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                BasicTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.body1.copy(color = Color.White),
                    decorationBox = { inner ->
                        Box(Modifier.fillMaxWidth().background(Color(0xFF202B30), RoundedCornerShape(18.dp)).padding(12.dp)) {
                            if (messageText.isEmpty()) Text("Message", color = Color(0xFFB8C5CA))
                            inner()
                        }
                    }
                )
                TextButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            messages += messageText.trim()
                            messageText = ""
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) { Text("Send") }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close", color = Color(0xFF9DDDD5)) } },
        backgroundColor = Color(0xFF192226),
        shape = RoundedCornerShape(24.dp)
    )
}