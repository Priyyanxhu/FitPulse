package com.example.fitpulsegym.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitpulsegym.ui.components.EmptyStateCard
import com.example.fitpulsegym.ui.components.SectionHeader
import com.example.fitpulsegym.ui.components.StatusPill
import com.example.fitpulsegym.ui.components.StatusTone
import com.example.fitpulsegym.ui.viewmodel.ProgressViewModel
import com.example.fitpulsegym.util.daysUntil
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProgressScreen(vm: ProgressViewModel) {
  val logs by vm.logs.collectAsState()
  val bookings by vm.bookings.collectAsState()
  val member by vm.member.collectAsState()
  val attended = bookings.count { it.status == "ATTENDED" }
  val pbCount = logs.count { it.personalBest }
  val goalFrac = (attended.toFloat() / 12f).coerceIn(0f, 1f)

  Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
    SectionHeader("Workout Log & Milestones", "Attendance, PBs and membership countdown")
    Card(
      Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(22.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
      Column(Modifier.padding(18.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(12.dp)) {
            Icon(Icons.Filled.FitnessCenter, null, modifier = Modifier.padding(8.dp).size(20.dp))
          }
          Spacer(Modifier.width(10.dp))
          Column(Modifier.weight(1f)) {
            Text("${member?.planName ?: "Pro"}", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
            Text(
              "${daysUntil(member?.expiryEpochMillis ?: 0)} days left • ${(goalFrac * 100).toInt()}% of monthly goal",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }
        Spacer(Modifier.height(12.dp))
        LinearProgressIndicator(
          progress = { goalFrac },
          modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(99.dp)),
          trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        )
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          MilestoneChip(Icons.Filled.FitnessCenter, "$attended attended", Modifier.weight(1f))
          MilestoneChip(Icons.Filled.EmojiEvents, "$pbCount PBs", Modifier.weight(1f))
          MilestoneChip(Icons.Filled.LocalFireDepartment, "${(goalFrac * 100).toInt()}% goal", Modifier.weight(1f))
        }
      }
    }
    Spacer(Modifier.height(10.dp))
    LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
      items(logs, key = { it.id }) { l ->
        Card(
          Modifier.fillMaxWidth().padding(vertical = 4.dp),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        ) {
          Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(l.classTitle, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
                if (l.personalBest) StatusPill("PB", StatusTone.BRAND)
              }
              Spacer(Modifier.height(4.dp))
              val fmt = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
              Text(
                "${fmt.format(Date(l.dateMillis))} • ${l.calories} kcal",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
              )
              Text(l.notes, style = MaterialTheme.typography.bodySmall)
            }
          }
        }
      }
      if (logs.isEmpty()) {
        item {
          EmptyStateCard(
            title = "No workouts logged yet",
            subtitle = "Attend a class to start your streak — PBs show up here.",
            icon = Icons.Filled.FitnessCenter,
          )
        }
      }
    }
  }
}

@Composable
private fun MilestoneChip(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, mod: Modifier = Modifier) {
  Surface(color = MaterialTheme.colorScheme.surfaceContainerHigh, shape = RoundedCornerShape(12.dp), modifier = mod) {
    Row(Modifier.padding(horizontal = 10.dp, vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) {
      Icon(icon, null, modifier = Modifier.size(16.dp))
      Spacer(Modifier.width(6.dp))
      Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
    }
  }
}
