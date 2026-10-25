package com.example.fitpulsegym.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitpulsegym.domain.model.MembershipStatus
import com.example.fitpulsegym.theme.FitDark
import com.example.fitpulsegym.theme.FitLime
import com.example.fitpulsegym.ui.components.CategoryPill
import com.example.fitpulsegym.ui.components.EmptyStateCard
import com.example.fitpulsegym.ui.components.ErrorStateCard
import com.example.fitpulsegym.ui.components.HeroCard
import com.example.fitpulsegym.ui.components.HeroLabel
import com.example.fitpulsegym.ui.components.LoadingRow
import com.example.fitpulsegym.ui.components.SectionHeader
import com.example.fitpulsegym.ui.components.StatusPill
import com.example.fitpulsegym.ui.components.StatusTone
import com.example.fitpulsegym.ui.components.categoryIcon
import com.example.fitpulsegym.ui.state.GymPulseUiState
import com.example.fitpulsegym.ui.viewmodel.HomeDashboardViewModel

/**
 * Home / Dashboard — Loading / Success / Error / Empty with hero + quick actions + habit widget + upcoming list.
 */
@Composable
fun HomeDashboardScreen(
  vm: HomeDashboardViewModel,
  onBrowseSchedule: () -> Unit,
  onOpenPass: () -> Unit,
  onOpenWorkouts: () -> Unit,
  onOpenProfile: () -> Unit,
) {
  val state by vm.state.collectAsState()
  val gymConfig by vm.gymConfig.collectAsState()

  Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
    GreetingHeader(onOpenProfile = onOpenProfile)
    Spacer(Modifier.height(8.dp))

    // Active Studio Announcement from Gym Owner
    if (!gymConfig?.announcement.isNullOrBlank()) {
      Card(
        Modifier.fillMaxWidth().padding(bottom = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
      ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
          Surface(color = MaterialTheme.colorScheme.primary, shape = CircleShape) {
            Icon(Icons.Filled.Campaign, null, modifier = Modifier.padding(6.dp).size(16.dp), tint = MaterialTheme.colorScheme.onPrimary)
          }
          Spacer(Modifier.width(10.dp))
          Column(Modifier.weight(1f)) {
            Text("Studio Notice", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Text(gymConfig!!.announcement, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
          }
        }
      }
    }
    when (val s = state) {
      is GymPulseUiState.Loading -> {
        LoadingRow("Loading your membership…")
      }
      is GymPulseUiState.Error -> {
        ErrorStateCard("Couldn't load dashboard", s.message, onRetry = onBrowseSchedule)
      }
      is GymPulseUiState.Empty -> {
        EmptyStateCard(
          title = "No membership found",
          subtitle = "Pick a plan to unlock bookings, QR check-in and streaks.",
          icon = Icons.Filled.FitnessCenter,
          actionLabel = "Browse classes",
          onAction = onBrowseSchedule,
        )
      }
      is GymPulseUiState.Success -> {
        val data = s.data
        val m = data.membership
        val status = m?.status() ?: MembershipStatus.EXPIRED
        val daysLeft = m?.daysLeft() ?: 0L

        HeroCard {
          Row(verticalAlignment = Alignment.CenterVertically) {
            HeroLabel(
              when (status) {
                MembershipStatus.ACTIVE -> "Active"
                MembershipStatus.EXPIRING_SOON -> "Expiring soon"
                MembershipStatus.EXPIRED -> "Expired"
              }
            )
            Spacer(Modifier.weight(1f))
            Surface(color = Color.White.copy(alpha = 0.16f), contentColor = Color.White, shape = CircleShape) {
              Icon(Icons.Filled.FitnessCenter, null, modifier = Modifier.padding(8.dp).size(18.dp))
            }
          }
          Spacer(Modifier.height(10.dp))
          Text("Active membership", style = MaterialTheme.typography.labelLarge, color = Color.White.copy(alpha = 0.75f))
          Text(
            m?.planName ?: "No plan",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = Color.White,
          )
          Spacer(Modifier.height(6.dp))
          Text(
            when (status) {
              MembershipStatus.ACTIVE -> "$daysLeft days left • you're all set"
              MembershipStatus.EXPIRING_SOON -> "$daysLeft days left — renew to keep your streak"
              MembershipStatus.EXPIRED -> "Expired — renew to keep booking"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = FitLime,
            fontWeight = FontWeight.SemiBold,
          )
          Spacer(Modifier.height(14.dp))
          LinearProgressIndicator(
            progress = { ((30 - daysLeft.coerceIn(0, 30)) / 30f).coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(99.dp)),
            color = FitLime,
            trackColor = Color.White.copy(alpha = 0.22f),
          )
          Spacer(Modifier.height(16.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            androidx.compose.material3.Button(
              onClick = onBrowseSchedule,
              modifier = Modifier.weight(1f).heightIn(min = 48.dp),
              shape = RoundedCornerShape(16.dp),
              colors = ButtonDefaults.buttonColors(containerColor = FitLime, contentColor = FitDark),
            ) {
              Icon(Icons.Filled.CalendarMonth, null, modifier = Modifier.size(18.dp))
              Spacer(Modifier.width(8.dp))
              Text("Book a slot", fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
              onClick = onOpenPass,
              modifier = Modifier.weight(1f).heightIn(min = 48.dp),
              shape = RoundedCornerShape(16.dp),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
              border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.5f)),
            ) {
              Icon(Icons.Filled.QrCode2, null, modifier = Modifier.size(18.dp))
              Spacer(Modifier.width(8.dp))
              Text("QR pass", fontWeight = FontWeight.Bold)
            }
          }
        }

        Spacer(Modifier.height(10.dp))

        // Quick Shortcut Grid (Workouts, Guide, Hydrate)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
          HomeShortcutCard("Workouts", Icons.Filled.FitnessCenter, onOpenWorkouts, Modifier.weight(1f))
          HomeShortcutCard("Exercise Guide", Icons.Filled.MenuBook, onOpenWorkouts, Modifier.weight(1f))
          HomeShortcutCard("Digital Pass", Icons.Filled.QrCode2, onOpenPass, Modifier.weight(1f))
        }

        Spacer(Modifier.height(6.dp))
        LazyColumn(Modifier.weight(1f)) {
          // Daily fitness tip card
          item {
            Card(
              Modifier.fillMaxWidth().padding(vertical = 4.dp),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
              border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
              Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = CircleShape) {
                  Icon(Icons.Filled.Lightbulb, null, modifier = Modifier.padding(6.dp).size(16.dp), tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                  Text("Coach Tip of the Day", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                  Text("Hit 30g protein within 45 mins of training to boost muscle protein synthesis.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
              }
            }
          }

          item {
            Card(
              Modifier.fillMaxWidth().padding(vertical = 4.dp),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
              border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
              Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(color = MaterialTheme.colorScheme.tertiary, shape = CircleShape) {
                  Icon(Icons.Filled.Bolt, null, modifier = Modifier.padding(6.dp).size(16.dp), tint = Color.White)
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                  Text("Strength & Energy Boost", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onTertiaryContainer)
                  Text("Every rep builds power. Stay consistent and chase your personal best today.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onTertiaryContainer)
                }
              }
            }
          }

          item {
            SectionHeader(
              title = "Upcoming (${data.upcoming.size})",
              subtitle = "QR-ready check-ins",
              actionLabel = if (data.upcoming.isNotEmpty()) "View all" else null,
              onAction = null,
            )
          }
          if (data.upcoming.isEmpty()) {
            item {
              Text(
                "Nothing booked — tap a suggestion below.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp),
              )
            }
          }
          items(data.upcoming, key = { it.id }) { b ->
            Card(
              Modifier.fillMaxWidth().padding(vertical = 5.dp),
              shape = RoundedCornerShape(18.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            ) {
              Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  color = MaterialTheme.colorScheme.surfaceContainerHigh,
                  shape = RoundedCornerShape(14.dp),
                ) {
                  Icon(Icons.Filled.CalendarMonth, null, modifier = Modifier.padding(10.dp).size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                  Text(b.classId, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                  Spacer(Modifier.height(2.dp))
                  Text(
                    "${b.status} • ${b.qrToken.take(22)}…",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                  )
                }
                StatusPill(
                  if (b.status.name == "CONFIRMED") "Confirmed" else "Waitlist",
                  if (b.status.name == "CONFIRMED") StatusTone.SUCCESS else StatusTone.WARNING,
                )
              }
            }
          }
          item {
            SectionHeader(title = "Suggested for you", subtitle = "Based on your week")
          }
          items(data.suggested, key = { it.id }) { c ->
            Card(
              Modifier.fillMaxWidth().padding(vertical = 5.dp),
              shape = RoundedCornerShape(18.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            ) {
              Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                  CategoryPill(c.category.name, categoryIcon(c.category.name))
                  Spacer(Modifier.height(8.dp))
                  Text(c.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                  Spacer(Modifier.height(2.dp))
                  Text(
                    "${c.timeLabel} • ${c.durationMin} min • ${c.spotsLeft}/${c.capacity} left",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                  )
                }
                Spacer(Modifier.width(10.dp))
                OutlinedButton(onClick = onBrowseSchedule) {
                  Text("Book")
                  Spacer(Modifier.width(4.dp))
                  Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(16.dp))
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun HomeShortcutCard(label: String, icon: ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
  Card(
    modifier = modifier.clickable { onClick() },
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Surface(
        color = FitLime.copy(alpha = 0.2f),
        contentColor = MaterialTheme.colorScheme.primary,
        shape = CircleShape
      ) {
        Icon(icon, null, modifier = Modifier.padding(8.dp).size(20.dp), tint = MaterialTheme.colorScheme.primary)
      }
      Spacer(Modifier.height(8.dp))
      Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold)
    }
  }
}

@Composable
private fun GreetingHeader(onOpenProfile: () -> Unit) {
  val cal = java.util.Calendar.getInstance()
  val hour = cal.get(java.util.Calendar.HOUR_OF_DAY)
  val greeting = when {
    hour < 12 -> "Good morning"
    hour < 17 -> "Good afternoon"
    else -> "Good evening"
  }
  val dateStr = java.text.SimpleDateFormat("EEEE, dd MMM", java.util.Locale.getDefault()).format(cal.time)

  Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 6.dp)) {
    Column(Modifier.weight(1f)) {
      Text(
        "$greeting, Champion",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Black,
      )
      Text(
        "$dateStr • Push your limits today",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold
      )
    }
    Surface(
      color = FitLime,
      contentColor = FitDark,
      shape = CircleShape,
      shadowElevation = 4.dp,
      modifier = Modifier.clickable { onOpenProfile() }
    ) {
      Text(
        "AS",
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
        fontWeight = FontWeight.Black,
        style = MaterialTheme.typography.titleSmall,
      )
    }
  }
}
