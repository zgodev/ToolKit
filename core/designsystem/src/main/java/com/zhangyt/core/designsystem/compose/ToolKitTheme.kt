package com.zhangyt.core.designsystem.compose

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.zhangyt.common.theme.ThemeStyle
import com.zhangyt.core.designsystem.R

/**
 * @description: 将现有应用主题语义映射为 Compose Material 3 颜色系统
 * @author: zhangyt
 * @Date: 2026-08-04
 */
@Composable
fun ToolKitTheme(
    themeStyle: ThemeStyle = ThemeStyle.BLUE,
    darkTheme: Boolean = themeStyle == ThemeStyle.DARK,
    content: @Composable () -> Unit,
) {
    val primary = toolKitPrimaryColor(themeStyle)
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = if (themeStyle == ThemeStyle.DARK) {
                colorResource(R.color.common_dark_accent)
            } else {
                primary
            },
            background = colorResource(R.color.common_dark_bg),
            surface = Color(0xFF1C1C1E),
            onBackground = colorResource(R.color.common_dark_text_main),
            onSurface = colorResource(R.color.common_dark_text_main),
        )
    } else {
        lightColorScheme(
            primary = primary,
            background = colorResource(R.color.common_bg_page),
            surface = colorResource(R.color.common_white),
            onBackground = colorResource(R.color.common_text_main),
            onSurface = colorResource(R.color.common_text_main),
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}

@Composable
fun toolKitPrimaryColor(themeStyle: ThemeStyle): Color = colorResource(
    when (themeStyle) {
        ThemeStyle.BLUE -> R.color.common_blue_primary
        ThemeStyle.RED -> R.color.common_red_primary
        ThemeStyle.GREEN -> R.color.common_green_primary
        ThemeStyle.PURPLE -> R.color.common_purple_primary
        ThemeStyle.DARK -> R.color.common_dark_accent
    }
)
