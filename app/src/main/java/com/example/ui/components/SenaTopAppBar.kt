package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SenaBackground
import com.example.ui.theme.SenaElectricCyan
import com.example.ui.theme.SenaOrangeCTA
import com.example.ui.theme.SenaPeach
import com.example.ui.theme.SenaTextPrimary

@Composable
fun SenaTopAppBar(
    modifier: Modifier = Modifier,
    onMenuClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(SenaBackground.copy(alpha = 0.95f))
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Menu Drawer Toggle
        IconButton(
            onClick = onMenuClick,
            modifier = Modifier.testTag("top_app_bar_menu_button")
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Open Drawer Menu",
                tint = SenaTextPrimary,
                modifier = Modifier.size(26.dp)
            )
        }

        // SENA Glowing Header Branding
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.clickable { onMenuClick() }
        ) {
            Text(
                text = "SENA",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = SenaPeach
            )
            Box(
                modifier = Modifier
                    .offset(x = 4.dp, y = (-8).dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E2638))
                    .padding(3.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "Sena Electric Badge",
                    tint = SenaElectricCyan,
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        // Profile Avatar Icon with Glowing Ring
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(SenaPeach, SenaOrangeCTA, SenaBackground)
                    )
                )
                .border(1.5.dp, SenaPeach, CircleShape)
                .clickable { onProfileClick() }
                .testTag("top_app_bar_profile_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "User Profile",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
