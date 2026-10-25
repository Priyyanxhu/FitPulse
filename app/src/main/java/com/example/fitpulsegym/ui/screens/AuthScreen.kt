package com.example.fitpulsegym.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitpulsegym.theme.FitDark
import com.example.fitpulsegym.theme.FitDeepGreen
import com.example.fitpulsegym.theme.FitEmerald
import androidx.compose.ui.res.painterResource
import com.example.fitpulsegym.R
import com.example.fitpulsegym.theme.FitLime
import com.example.fitpulsegym.ui.components.FitPrimaryButton

@Composable
fun AuthScreen(onLogin: (String) -> Unit) {
  var selectedRoleTab by remember { mutableIntStateOf(0) } // 0 = Athlete Portal, 1 = Gym Owner HQ
  var phone by remember { mutableStateOf("+91 98765 43210") }
  var name by remember { mutableStateOf("Aarav Sharma") }
  val isDark = isSystemInDarkTheme()

  Box(
    Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            Color(0xFF07140D),
            Color(0xFF0E281A),
            Color(0xFF133624),
            if (isDark) Color(0xFF0C1110) else Color(0xFFF4F6EF)
          )
        )
      )
  ) {
    // Ambient Lime Glow
    Box(
      Modifier
        .align(Alignment.TopCenter)
        .padding(top = 30.dp)
        .size(320.dp)
        .background(
          Brush.radialGradient(
            listOf(FitLime.copy(alpha = 0.28f), Color.Transparent)
          ),
          CircleShape
        )
    )

    Column(
      Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp, vertical = 24.dp),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      // Brand Logo
      Surface(
        color = FitDark,
        contentColor = Color.White,
        shape = CircleShape,
        shadowElevation = 12.dp,
        border = BorderStroke(2.dp, FitLime)
      ) {
        Image(
          painter = painterResource(R.drawable.ic_launcher_foreground),
          contentDescription = "FitPulse logo",
          modifier = Modifier.padding(10.dp).size(44.dp)
        )
      }
      Spacer(Modifier.height(14.dp))
      Text(
        "FitPulse",
        style = MaterialTheme.typography.displaySmall,
        fontWeight = FontWeight.Black,
        color = Color.White,
        letterSpacing = (-0.5).sp
      )
      Text(
        if (selectedRoleTab == 0) "Build strength • Fuel energy • Stay motivated"
        else "Command & operations ERP for your whole gym business",
        style = MaterialTheme.typography.bodyMedium,
        color = Color.White.copy(alpha = 0.85f),
      )
      Spacer(Modifier.height(20.dp))

      // Main Glassmorphic Card
      Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(10.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(Modifier.padding(20.dp)) {
          // Role Segmented Tabs
          TabRow(
            selectedTabIndex = selectedRoleTab,
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            modifier = Modifier.clip(RoundedCornerShape(14.dp))
          ) {
            Tab(
              selected = selectedRoleTab == 0,
              onClick = {
                selectedRoleTab = 0
                name = "Aarav Sharma"
                phone = "+91 98765 43210"
              },
              text = { Text("Athlete Portal", fontWeight = FontWeight.Bold) },
              icon = { Icon(Icons.Filled.Person, null, modifier = Modifier.size(16.dp)) }
            )
            Tab(
              selected = selectedRoleTab == 1,
              onClick = {
                selectedRoleTab = 1
                name = "Studio Director (Coach)"
                phone = "owner@fitpulse.in"
              },
              text = { Text("Gym Owner HQ", fontWeight = FontWeight.Bold) },
              icon = { Icon(Icons.Filled.AdminPanelSettings, null, modifier = Modifier.size(16.dp)) }
            )
          }

          Spacer(Modifier.height(16.dp))

          Text(
            if (selectedRoleTab == 0) "Athlete Account" else "Gym Management Portal",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold
          )
          Text(
            if (selectedRoleTab == 0) "Sign in to book slots, log workouts and access your turnstile pass"
            else "Access occupancy, equipment servicing, locker bays, schedules & P&L financials",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          Spacer(Modifier.height(14.dp))

          OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(if (selectedRoleTab == 0) "Member Name" else "Owner / Coach Name") },
            leadingIcon = { Icon(Icons.Filled.Person, null) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
          )
          Spacer(Modifier.height(10.dp))

          OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text(if (selectedRoleTab == 0) "Mobile Number" else "Studio Admin Email / Phone") },
            leadingIcon = { Icon(Icons.Filled.Email, null) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
          )
          Spacer(Modifier.height(18.dp))

          if (selectedRoleTab == 0) {
            FitPrimaryButton(
              onClick = { onLogin("member") },
              modifier = Modifier.fillMaxWidth()
            ) {
              Icon(Icons.Filled.Bolt, null, modifier = Modifier.size(18.dp))
              Spacer(Modifier.width(8.dp))
              Text("Enter Athlete Portal", fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.height(10.dp))
            OutlinedButton(
              onClick = { onLogin("member") },
              modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
              shape = RoundedCornerShape(16.dp),
            ) {
              Icon(Icons.Filled.Fingerprint, null, modifier = Modifier.size(18.dp))
              Spacer(Modifier.width(8.dp))
              Text("Biometric Instant Sign In", fontWeight = FontWeight.SemiBold)
            }
          } else {
            FitPrimaryButton(
              onClick = { onLogin("admin") },
              modifier = Modifier.fillMaxWidth()
            ) {
              Icon(Icons.Filled.FitnessCenter, null, modifier = Modifier.size(18.dp))
              Spacer(Modifier.width(8.dp))
              Text("Launch Gym Owner HQ", fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.height(10.dp))
            OutlinedButton(
              onClick = { onLogin("admin") },
              modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
              shape = RoundedCornerShape(16.dp),
            ) {
              Icon(Icons.Filled.Shield, null, modifier = Modifier.size(18.dp))
              Spacer(Modifier.width(8.dp))
              Text("Quick Coach / Front-Desk Access", fontWeight = FontWeight.SemiBold)
            }
          }
        }
      }

      Spacer(Modifier.height(20.dp))

      // Bottom Feature Badges
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
      ) {
        MiniPerk(Icons.Filled.Bolt, "15 Weekly Classes", Modifier.weight(1f))
        MiniPerk(Icons.Filled.QrCode2, "Turnstile Pass", Modifier.weight(1f))
        MiniPerk(Icons.Filled.FitnessCenter, "Facility ERP", Modifier.weight(1f))
      }
    }
  }
}

@Composable
private fun MiniPerk(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, modifier: Modifier = Modifier) {
  Surface(
    color = Color.White.copy(alpha = 0.12f),
    contentColor = Color.White,
    shape = RoundedCornerShape(999.dp),
    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.18f)),
    modifier = modifier
  ) {
    Row(
      Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      Icon(icon, null, modifier = Modifier.size(13.dp), tint = FitLime)
      Spacer(Modifier.width(4.dp))
      Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
    }
  }
}
