package com.zhangyt.module.mine.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.SystemUpdate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zhangyt.common.language.Language
import com.zhangyt.common.theme.ThemeStyle
import com.zhangyt.core.designsystem.compose.ToolKitSpacing
import com.zhangyt.core.designsystem.compose.ToolKitTheme
import com.zhangyt.core.designsystem.compose.toolKitPrimaryColor
import com.zhangyt.module.mine.R
import com.zhangyt.module.mine.viewmodel.MineUiState
import com.zhangyt.module.mine.viewmodel.MineViewModel

@Composable
fun MineScreen(
    viewModel: MineViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ToolKitTheme(themeStyle = state.theme) {
        MineContent(
            state = state,
            onThemeSelected = viewModel::selectTheme,
            onLanguageSelected = viewModel::selectLanguage,
            onCheckUpdate = viewModel::openOta,
            onLogout = viewModel::logout,
            modifier = modifier,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MineContent(
    state: MineUiState,
    onThemeSelected: (ThemeStyle) -> Unit,
    onLanguageSelected: (Language) -> Unit,
    onCheckUpdate: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.mine_title),
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { contentPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentAlignment = Alignment.TopCenter,
        ) {
            val horizontalPadding = if (maxWidth >= 600.dp) {
                ToolKitSpacing.section
            } else {
                ToolKitSpacing.large
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = ToolKitSpacing.maxContentWidth),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = horizontalPadding,
                    vertical = ToolKitSpacing.large,
                ),
                verticalArrangement = Arrangement.spacedBy(ToolKitSpacing.medium),
            ) {
                item(key = "profile") {
                    ProfileCard(state)
                }
                item(key = "personalization_title") {
                    SectionTitle(stringResource(R.string.mine_personalization))
                }
                item(key = "theme") {
                    ThemeSelector(
                        selectedTheme = state.theme,
                        onThemeSelected = onThemeSelected,
                    )
                }
                item(key = "language") {
                    LanguageSelector(
                        selectedLanguage = state.language,
                        onLanguageSelected = onLanguageSelected,
                    )
                }
                item(key = "system_title") {
                    SectionTitle(stringResource(R.string.mine_system_services))
                }
                item(key = "update") {
                    SettingsRow(
                        title = stringResource(R.string.mine_check_update),
                        description = stringResource(R.string.mine_check_update_description),
                        icon = { Icon(Icons.Outlined.SystemUpdate, contentDescription = null) },
                        onClick = onCheckUpdate,
                    )
                }
                if (state.isLoggedIn) {
                    item(key = "logout") {
                        OutlinedButton(
                            onClick = onLogout,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("mine_logout"),
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Logout,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                            )
                            Spacer(Modifier.width(ToolKitSpacing.small))
                            Text(
                                text = stringResource(R.string.mine_logout),
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }
                item(key = "bottom_space") {
                    Spacer(Modifier.height(ToolKitSpacing.large))
                }
            }
        }
    }
}

@Composable
private fun ProfileCard(state: MineUiState) {
    val profileDescription = stringResource(R.string.mine_profile_description)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = profileDescription },
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ToolKitSpacing.extraLarge),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp),
                )
            }
            Spacer(Modifier.width(ToolKitSpacing.large))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = state.nickname.ifBlank { stringResource(R.string.mine_guest) },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(ToolKitSpacing.extraSmall))
                Text(
                    text = state.phone.ifBlank { stringResource(R.string.mine_not_logged_in) },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ThemeSelector(
    selectedTheme: ThemeStyle,
    onThemeSelected: (ThemeStyle) -> Unit,
) {
    PreferenceCard(
        title = stringResource(R.string.mine_theme_switch),
        icon = { Icon(Icons.Outlined.Palette, contentDescription = null) },
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(ToolKitSpacing.small),
        ) {
            items(
                items = ThemeStyle.entries,
                key = ThemeStyle::name,
            ) { theme ->
                val selected = theme == selectedTheme
                FilterChip(
                    selected = selected,
                    onClick = { onThemeSelected(theme) },
                    label = { Text(themeLabel(theme)) },
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(toolKitPrimaryColor(theme)),
                        )
                    },
                    modifier = Modifier.testTag("mine_theme_${theme.name}"),
                )
            }
        }
    }
}

@Composable
private fun LanguageSelector(
    selectedLanguage: Language,
    onLanguageSelected: (Language) -> Unit,
) {
    PreferenceCard(
        title = stringResource(R.string.mine_language_switch),
        icon = { Icon(Icons.Outlined.Language, contentDescription = null) },
    ) {
        Language.entries.forEachIndexed { index, language ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = language == selectedLanguage,
                        role = Role.RadioButton,
                        onClick = { onLanguageSelected(language) },
                    )
                    .testTag("mine_language_${language.name}")
                    .padding(vertical = ToolKitSpacing.small),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = languageLabel(language),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge,
                )
                RadioButton(
                    selected = language == selectedLanguage,
                    onClick = null,
                )
            }
            if (index != Language.entries.lastIndex) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }
        }
    }
}

@Composable
private fun PreferenceCard(
    title: String,
    icon: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(ToolKitSpacing.large)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center,
                ) {
                    icon()
                }
                Spacer(Modifier.width(ToolKitSpacing.medium))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(Modifier.height(ToolKitSpacing.medium))
            content()
        }
    }
}

@Composable
private fun SettingsRow(
    title: String,
    description: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("mine_check_update"),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ToolKitSpacing.large),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center,
            ) {
                icon()
            }
            Spacer(Modifier.width(ToolKitSpacing.medium))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(ToolKitSpacing.extraSmall))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(
            start = ToolKitSpacing.extraSmall,
            top = ToolKitSpacing.small,
        ),
    )
}

@Composable
private fun themeLabel(theme: ThemeStyle): String = stringResource(
    when (theme) {
        ThemeStyle.BLUE -> R.string.mine_theme_blue
        ThemeStyle.RED -> R.string.mine_theme_red
        ThemeStyle.GREEN -> R.string.mine_theme_green
        ThemeStyle.PURPLE -> R.string.mine_theme_purple
        ThemeStyle.DARK -> R.string.mine_theme_dark
    }
)

@Composable
private fun languageLabel(language: Language): String = stringResource(
    when (language) {
        Language.SYSTEM -> R.string.mine_language_system
        Language.ZH_CN -> R.string.mine_language_chinese
        Language.EN -> R.string.mine_language_english
        Language.JA -> R.string.mine_language_japanese
    }
)

@Preview(name = "Mine Light", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun MineContentLightPreview() {
    ToolKitTheme {
        MineContent(
            state = MineUiState(
                nickname = "张三",
                phone = "138 0000 0000",
                isLoggedIn = true,
                theme = ThemeStyle.BLUE,
                language = Language.ZH_CN,
            ),
            onThemeSelected = {},
            onLanguageSelected = {},
            onCheckUpdate = {},
            onLogout = {},
        )
    }
}

@Preview(
    name = "Mine Dark",
    showBackground = true,
    widthDp = 390,
    heightDp = 844,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun MineContentDarkPreview() {
    ToolKitTheme(themeStyle = ThemeStyle.DARK) {
        MineContent(
            state = MineUiState(
                nickname = "Alex",
                phone = "Not signed in",
                isLoggedIn = false,
                theme = ThemeStyle.DARK,
                language = Language.EN,
            ),
            onThemeSelected = {},
            onLanguageSelected = {},
            onCheckUpdate = {},
            onLogout = {},
        )
    }
}
