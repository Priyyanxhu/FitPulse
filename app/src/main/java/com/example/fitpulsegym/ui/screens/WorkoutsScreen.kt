package com.example.fitpulsegym.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.Brush
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.fitpulsegym.data.model.Exercise
import com.example.fitpulsegym.data.model.ExerciseLibrary
import com.example.fitpulsegym.data.model.MuscleGroup
import com.example.fitpulsegym.theme.FitDark
import com.example.fitpulsegym.theme.FitLime
import com.example.fitpulsegym.ui.components.EmptyStateCard
import com.example.fitpulsegym.ui.components.FitPrimaryButton
import com.example.fitpulsegym.ui.components.SectionHeader
import com.example.fitpulsegym.ui.components.StatusPill
import com.example.fitpulsegym.ui.components.StatusTone
import com.example.fitpulsegym.ui.viewmodel.ProgressViewModel
import com.example.fitpulsegym.util.daysUntil
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WorkoutsScreen(vm: ProgressViewModel) {
  val logs by vm.logs.collectAsState()
  val bookings by vm.bookings.collectAsState()
  val member by vm.member.collectAsState()

  var selectedTab by remember { mutableIntStateOf(0) } // 0 = Logs, 1 = Exercise Guide
  var showLogDialog by remember { mutableStateOf(false) }
  var prefilledExercise by remember { mutableStateOf<String?>(null) }
  var selectedMuscleGroup by remember { mutableStateOf<MuscleGroup?>(null) }

  val attended = bookings.count { it.status == "ATTENDED" }
  val pbCount = logs.count { it.personalBest }
  val goalFrac = (attended.toFloat() / 12f).coerceIn(0f, 1f)
  val water = member?.waterIntakeMl ?: 1750
  val waterGoal = member?.waterGoalMl ?: 3000
  val waterFrac = (water.toFloat() / waterGoal.toFloat()).coerceIn(0f, 1f)

  Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
    Row(
      Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      SectionHeader("Workout Hub", "Train hard • Stay strong • Level up")
      FitPrimaryButton(onClick = { prefilledExercise = null; showLogDialog = true }) {
        Icon(Icons.Filled.Add, null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(4.dp))
        Text("Log", fontWeight = FontWeight.Bold)
      }
    }

    // Daily Habit & Water Card
    Card(
      Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Column(
        Modifier
          .fillMaxWidth()
          .background(
            Brush.verticalGradient(
              listOf(
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.surfaceContainerLow
              )
            )
          )
          .padding(18.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(14.dp)
          ) {
            Icon(Icons.Filled.WaterDrop, null, modifier = Modifier.padding(10.dp).size(22.dp), tint = MaterialTheme.colorScheme.primary)
          }
          Spacer(Modifier.width(12.dp))
          Column(Modifier.weight(1f)) {
            Text("Daily Hydration Target", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium)
            Text("$water / $waterGoal ml • ${(waterFrac * 100).toInt()}% completed", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
          }
          IconButton(onClick = { vm.resetWater() }, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Filled.Replay, "Reset water", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }
        Spacer(Modifier.height(12.dp))
        LinearProgressIndicator(
          progress = { waterFrac },
          modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(99.dp)),
          color = FitLime,
          trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedButton(
            onClick = { vm.addWater(250) },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.weight(1f)
          ) {
            Icon(Icons.Filled.WaterDrop, null, modifier = Modifier.size(15.dp))
            Spacer(Modifier.width(4.dp))
            Text("+250 ml", fontWeight = FontWeight.Bold)
          }
          OutlinedButton(
            onClick = { vm.addWater(500) },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.weight(1f)
          ) {
            Icon(Icons.Filled.WaterDrop, null, modifier = Modifier.size(15.dp))
            Spacer(Modifier.width(4.dp))
            Text("+500 ml", fontWeight = FontWeight.Bold)
          }
        }

        Spacer(Modifier.height(14.dp))
        // Milestones Row
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          MilestonePill(Icons.Filled.FitnessCenter, "$attended attended", Modifier.weight(1f), iconTint = FitLime)
          MilestonePill(Icons.Filled.EmojiEvents, "$pbCount PBs", Modifier.weight(1f), iconTint = Color(0xFFFFB800))
          MilestonePill(Icons.Filled.LocalFireDepartment, "${(goalFrac * 100).toInt()}% streak", Modifier.weight(1f), iconTint = Color(0xFFFF7A59))
        }
      }
    }

    Spacer(Modifier.height(10.dp))

    // Sub-Tabs: Workout Log vs Exercise Guide
    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
      modifier = Modifier.clip(RoundedCornerShape(14.dp))
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = { Text("Activity Log (${logs.size})", fontWeight = FontWeight.Bold) },
        icon = { Icon(Icons.Filled.FitnessCenter, null, modifier = Modifier.size(18.dp)) }
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = { Text("Exercise Guide", fontWeight = FontWeight.Bold) },
        icon = { Icon(Icons.Filled.MenuBook, null, modifier = Modifier.size(18.dp)) }
      )
    }

    Spacer(Modifier.height(8.dp))

    if (selectedTab == 0) {
      // Tab 0: Activity Logs
      LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        items(logs, key = { it.id }) { l ->
          Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
          ) {
            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
              Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(l.classTitle, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                  if (l.personalBest) {
                    StatusPill("PERSONAL BEST", StatusTone.BRAND)
                  }
                }
                Spacer(Modifier.height(3.dp))
                val fmt = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
                Text(
                  "${fmt.format(Date(l.dateMillis))} • ${l.calories} kcal",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.primary,
                  fontWeight = FontWeight.SemiBold
                )
                if (l.notes.isNotBlank()) {
                  Spacer(Modifier.height(2.dp))
                  Text(l.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
              }
              IconButton(onClick = { vm.deleteWorkout(l.id) }) {
                Icon(Icons.Filled.Delete, "Delete", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error)
              }
            }
          }
        }
        if (logs.isEmpty()) {
          item {
            EmptyStateCard(
              title = "No workouts logged yet",
              subtitle = "Tap 'Log' above or attend a scheduled class to start your streak!",
              icon = Icons.Filled.FitnessCenter
            )
          }
        }
      }
    } else {
      // Tab 1: Exercise Guide
      Column(Modifier.weight(1f)) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(bottom = 8.dp)) {
          item {
            FilterChip(
              selected = selectedMuscleGroup == null,
              onClick = { selectedMuscleGroup = null },
              label = { Text("All Muscles") },
              shape = RoundedCornerShape(12.dp)
            )
          }
          items(MuscleGroup.entries) { m ->
            FilterChip(
              selected = selectedMuscleGroup == m,
              onClick = { selectedMuscleGroup = if (selectedMuscleGroup == m) null else m },
              label = { Text("${m.iconEmoji} ${m.label}") },
              shape = RoundedCornerShape(12.dp),
              colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer)
            )
          }
        }

        val filteredExercises = remember(selectedMuscleGroup) {
          if (selectedMuscleGroup == null) ExerciseLibrary.exercises
          else ExerciseLibrary.exercises.filter { it.muscleGroup == selectedMuscleGroup }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          items(filteredExercises, key = { it.id }) { ex ->
            ExerciseCard(
              exercise = ex,
              onLog = {
                prefilledExercise = ex.name
                showLogDialog = true
              }
            )
          }
        }
      }
    }
  }

  if (showLogDialog) {
    LogWorkoutDialog(
      initialTitle = prefilledExercise ?: "Gym Session",
      onDismiss = { showLogDialog = false },
      onConfirm = { title, cal, notes, isPb ->
        vm.logWorkout(title, cal, notes, isPb)
        showLogDialog = false
      }
    )
  }
}

@Composable
private fun MilestonePill(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  label: String,
  mod: Modifier = Modifier,
  iconTint: Color = MaterialTheme.colorScheme.primary
) {
  Surface(
    color = MaterialTheme.colorScheme.surfaceContainerHigh,
    shape = RoundedCornerShape(14.dp),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    modifier = mod
  ) {
    Row(
      Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      Icon(icon, null, modifier = Modifier.size(16.dp), tint = iconTint)
      Spacer(Modifier.width(5.dp))
      Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold)
    }
  }
}

@Composable
private fun ExerciseCard(exercise: Exercise, onLog: () -> Unit) {
  Card(
    Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(Modifier.padding(16.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
          Text(exercise.name, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
          Spacer(Modifier.height(2.dp))
          Text(
            "${exercise.muscleGroup.iconEmoji} ${exercise.muscleGroup.label} • ${exercise.equipment}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
          )
        }
        StatusPill(
          exercise.difficulty,
          when (exercise.difficulty) {
            "Beginner" -> StatusTone.SUCCESS
            "Intermediate" -> StatusTone.WARNING
            else -> StatusTone.ERROR
          }
        )
      }
      Spacer(Modifier.height(10.dp))
      Text(exercise.instructions, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
      Spacer(Modifier.height(8.dp))
      Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
      ) {
        Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
          Text("💡 Pro Tip: ", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, color = FitLime)
          Text(exercise.proTip, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }
      Spacer(Modifier.height(12.dp))
      Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(color = MaterialTheme.colorScheme.surfaceContainerLow, shape = RoundedCornerShape(8.dp)) {
          Text(
            "🔥 ~${exercise.burnKcalPer10Min} kcal / 10m",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
        Spacer(Modifier.weight(1f))
        FitPrimaryButton(
          onClick = onLog,
        ) {
          Text("Log this lift", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
private fun LogWorkoutDialog(
  initialTitle: String,
  onDismiss: () -> Unit,
  onConfirm: (title: String, cal: Int, notes: String, isPb: Boolean) -> Unit
) {
  var title by remember { mutableStateOf(initialTitle) }
  var calories by remember { mutableStateOf("450") }
  var duration by remember { mutableStateOf("45") }
  var notes by remember { mutableStateOf("Felt strong! 4 sets of 8 reps.") }
  var isPb by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Log Workout Session", fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Workout / Class Name") },
          singleLine = true,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = calories,
            onValueChange = { calories = it },
            label = { Text("Calories (kcal)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = duration,
            onValueChange = { duration = it },
            label = { Text("Minutes") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.weight(1f)
          )
        }
        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Notes / Weights Lifted") },
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.fillMaxWidth()
        )
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
          Switch(checked = isPb, onCheckedChange = { isPb = it })
          Spacer(Modifier.width(8.dp))
          Text(if (isPb) "🏆 Personal Best (PB)" else "Mark as Personal Best", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
        }
      }
    },
    confirmButton = {
      FitPrimaryButton(
        onClick = {
          onConfirm(title.ifBlank { "Workout" }, calories.toIntOrNull() ?: 350, notes, isPb)
        }
      ) {
        Text("Save Log", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel") }
    },
    shape = RoundedCornerShape(24.dp)
  )
}
