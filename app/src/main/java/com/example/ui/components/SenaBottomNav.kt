package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SenaBackground
import com.example.ui.theme.SenaBorder
import com.example.ui.theme.SenaOrangeCTA
import com.example.ui.theme.SenaPeach
import com.example.ui.theme.SenaSurface
import com.example.ui.theme.SenaTextMuted

enum class SenaNavTab {
    HOME, HISTORY, BOOK, WALLET, PROFILE
}

@Composable
fun SenaBottomNav(
    selectedTab: SenaNavTab,
    onTabSelected: (SenaNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(SenaBackground)
            .navigationBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = SenaBorder,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(SenaSurface.copy(alpha = 0.95f))
                .padding(vertical = 8.dp, horizontal = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavItem(
                    label = "Home",
                    filledIcon = Icons.Filled.Explore,
                    outlinedIcon = Icons.Outlined.Explore,
                    isSelected = selectedTab == SenaNavTab.HOME,
                    onClick = { onTabSelected(SenaNavTab.HOME) },
                    testTag = "nav_tab_home"
                )

                NavItem(
                    label = "History",
                    filledIcon = Icons.Filled.History,
                    outlinedIcon = Icons.Outlined.History,
                    isSelected = selectedTab == SenaNavTab.HISTORY,
                    onClick = { onTabSelected(SenaNavTab.HISTORY) },
                    testTag = "nav_tab_history"
                )

                Box(modifier = Modifier.size(52.dp))

                NavItem(
                    label = "Wallet",
                    filledIcon = Icons.Filled.AccountBalanceWallet,
                    outlinedIcon = Icons.Outlined.AccountBalanceWallet,
                    isSelected = selectedTab == SenaNavTab.WALLET,
                    onClick = { onTabSelected(SenaNavTab.WALLET) },
                    hasBadge = true,
                    testTag = "nav_tab_wallet"
                )

                NavItem(
                    label = "Profile",
                    filledIcon = Icons.Filled.Person,
                    outlinedIcon = Icons.Outlined.Person,
                    isSelected = selectedTab == SenaNavTab.PROFILE,
                    onClick = { onTabSelected(SenaNavTab.PROFILE) },
                    testTag = "nav_tab_profile"
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-18).dp)
                .size(56.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(SenaPeach, SenaOrangeCTA)
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(SenaPeach, SenaBackground)
                    ),
                    shape = CircleShape
                )
                .clickable { onTabSelected(SenaNavTab.BOOK) }
                .testTag("nav_tab_book_fab"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Book Ride Action",
                tint = SenaBackground,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    filledIcon: ImageVector,
    outlinedIcon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    hasBadge: Boolean = false,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag(testTag)
    ) {
        Box {
            Icon(
                imageVector = if (isSelected) filledIcon else outlinedIcon,
                contentDescription = label,
                tint = if (isSelected) SenaPeach else SenaTextMuted,
                modifier = Modifier.size(24.dp)
            )

            if (hasBadge) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(SenaPeach)
                        .align(Alignment.TopEnd)
                        .offset(x = 2.dp, y = (-2).dp)
                )
            }
        }

        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) SenaPeach else SenaTextMuted,
            modifier = Modifier.padding(top = 2.dp)
        )

        if (isSelected) {
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(width = 12.dp, height = 2.dp)
                    .clip(CircleShape)
                    .background(SenaPeach)
            )
        }
    }
}
