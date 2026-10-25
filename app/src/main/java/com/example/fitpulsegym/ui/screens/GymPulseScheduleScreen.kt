package com.example.fitpulsegym.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitpulsegym.data.model.ClassCategory
import com.example.fitpulsegym.data.model.dayFullName
import com.example.fitpulsegym.data.model.dayName
import com.example.fitpulsegym.domain.model.GymClass
import com.example.fitpulsegym.theme.FitDark
import com.example.fitpulsegym.theme.FitDeepGreen
import com.example.fitpulsegym.theme.FitLime
import com.example.fitpulsegym.ui.components.AvailabilityPill
import com.example.fitpulsegym.ui.components.CapacityBar
import com.example.fitpulsegym.ui.components.CategoryPill
import com.example.fitpulsegym.ui.components.EmptyStateCard
import com.example.fitpulsegym.ui.components.ErrorStateCard
import com.example.fitpulsegym.ui.components.FitPrimaryButton
import com.example.fitpulsegym.ui.components.LoadingRow
import com.example.fitpulsegym.ui.components.SectionHeader
import com.example.fitpulsegym.ui.components.categoryIcon
import com.example.fitpulsegym.ui.state.GymPulseUiState
import com.example.fitpulsegym.ui.viewmodel.GymPulseScheduleViewModel

@Composable
fun GymPulseScheduleScreen(vm: GymPulseScheduleViewModel, snacks: SnackbarHostState) {
  val day by vm.day.collectAsState()
  val msg by vm.bookingMsg.collectAsState()
  val state by remember(day) { vm.scheduleForDay(day) }.collectAsState()

  var query by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf<String?>(null) }
  var detailedClass by remember { mutableStateOf<GymClass?>(null) }

  LaunchedEffect(day) { vm.refresh() }
  LaunchedEffect(msg) { msg?.let { snacks.showSnackbar(it); vm.clearMsg() } }

  Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
    SectionHeader(
      title = "Class Schedule",
      subtitle = "${dayFullName(day)} • Crossfit, Yoga, Strength, Boxing & Zumba",
    )

    // Search bar
    OutlinedTextField(
      value = query,
      onValueChange = { query = it },
      label = { Text("Search class, trainer or station") },
      leadingIcon = { Icon(Icons.Filled.Search, null) },
      singleLine = true,
      shape = RoundedCornerShape(16.dp),
      modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp)
    )

    Spacer(Modifier.height(8.dp))

    // Days selector
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      items((0..6).toList()) { d ->
        FilterChip(
          selected = d == day,
          onClick = { vm.setDay(d) },
          label = { Text(dayName(d), fontWeight = if (d == day) FontWeight.Bold else FontWeight.Medium) },
          shape = RoundedCornerShape(12.dp),
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
          ),
          modifier = Modifier.heightIn(min = 40.dp),
        )
      }
    }

    Spacer(Modifier.height(6.dp))

    // Category chips
    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
      item {
        FilterChip(
          selected = selectedCategory == null,
          onClick = { selectedCategory = null },
          label = { Text("All") },
          shape = RoundedCornerShape(12.dp)
        )
      }
      items(ClassCategory.entries) { cat ->
        FilterChip(
          selected = selectedCategory == cat.name,
          onClick = { selectedCategory = if (selectedCategory == cat.name) null else cat.name },
          label = { Text(cat.label) },
          leadingIcon = { Icon(categoryIcon(cat.label), null, modifier = Modifier.size(15.dp)) },
          shape = RoundedCornerShape(12.dp),
          colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer)
        )
      }
    }

    Spacer(Modifier.height(8.dp))

    when (val s = state) {
      is GymPulseUiState.Loading -> LoadingRow("Loading slots…")
      is GymPulseUiState.Error -> ErrorStateCard("Schedule unavailable", s.message, onRetry = { vm.refresh() })
      is GymPulseUiState.Empty -> EmptyStateCard(
        title = "No slots this day",
        subtitle = "Try another day — coaches add slots weekly.",
        icon = Icons.Filled.CalendarMonth,
      )
      is GymPulseUiState.Success -> {
        val filtered = s.data.filter {
          (selectedCategory == null || it.category.name == selectedCategory) &&
            (query.isBlank() || it.title.contains(query, ignoreCase = true) || it.trainerName.contains(query, ignoreCase = true) || it.station.contains(query, ignoreCase = true))
        }

        if (filtered.isEmpty()) {
          EmptyStateCard(
            title = "No matching classes",
            subtitle = "Try adjusting your search or category filter.",
            icon = Icons.Filled.Search
          )
        } else {
          LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(filtered, key = { it.id }) { c ->
              SlotCard(
                c = c,
                onCardClick = { detailedClass = c },
                onBook = { vm.book(c.id) }
              )
            }
          }
        }
      }
    }
  }

  // Class Details Dialog
  detailedClass?.let { c ->
    ClassDetailsDialog(
      slot = c,
      onDismiss = { detailedClass = null },
      onBook = {
        vm.book(c.id)
        detailedClass = null
      }
    )
  }
}

@Composable
private fun SlotCard(
  c: GymClass,
  onCardClick: () -> Unit,
  onBook: () -> Unit
) {
  Card(
    Modifier
      .fillMaxWidth()
      .clickable { onCardClick() },
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
  ) {
    Column(Modifier.padding(18.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        CategoryPill(c.category.name, categoryIcon(c.category.name))
        Spacer(Modifier.width(8.dp))
        Surface(
          color = MaterialTheme.colorScheme.surfaceContainerHigh,
          shape = RoundedCornerShape(8.dp)
        ) {
          Row(
            Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Filled.Schedule, null, modifier = Modifier.size(13.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(4.dp))
            Text(c.timeLabel, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
          }
        }
        Spacer(Modifier.weight(1f))
        AvailabilityPill(c.isCancelled, c.isFull, c.spotsLeft)
      }

      Spacer(Modifier.height(12.dp))
      Text(c.title, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)

      Spacer(Modifier.height(8.dp))
      Row(verticalAlignment = Alignment.CenterVertically) {
        // Coach avatar circle
        Surface(
          color = MaterialTheme.colorScheme.primaryContainer,
          shape = CircleShape,
          modifier = Modifier.size(28.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(
              c.trainerName.take(1).uppercase(),
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Black,
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )
          }
        }
        Spacer(Modifier.width(8.dp))
        Column {
          Text("Coach ${c.trainerName}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.LocationOn, null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(2.dp))
            Text("${c.station} • ${c.durationMin} min", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }
      }

      Spacer(Modifier.height(12.dp))
      CapacityBar(c.bookedCount, c.capacity)
      Spacer(Modifier.height(4.dp))
      Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          "${c.bookedCount} / ${c.capacity} booked",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
          if (c.spotsLeft > 0) "${c.spotsLeft} spots available" else "Full class",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
          color = if (c.spotsLeft <= 3) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        )
      }

      Spacer(Modifier.height(14.dp))
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FitPrimaryButton(
          onClick = onBook,
          enabled = !c.isCancelled && !c.isFull,
          modifier = Modifier.weight(1f),
        ) {
          Text(if (c.isFull) "Join Waitlist" else "Book Slot", fontWeight = FontWeight.Bold)
        }
        if (c.isFull && !c.isCancelled) {
          OutlinedButton(
            onClick = onBook,
            modifier = Modifier.heightIn(min = 48.dp),
            shape = RoundedCornerShape(16.dp),
          ) { Text("Waitlist", fontWeight = FontWeight.Bold) }
        }
      }
    }
  }
}

@Composable
private fun ClassDetailsDialog(
  slot: GymClass,
  onDismiss: () -> Unit,
  onBook: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        CategoryPill(slot.category.name, categoryIcon(slot.category.name))
        Spacer(Modifier.weight(1f))
        AvailabilityPill(slot.isCancelled, slot.isFull, slot.spotsLeft)
      }
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(slot.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)

        // Coach Bio
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
          Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = MaterialTheme.colorScheme.primary, shape = CircleShape) {
              Icon(Icons.Filled.Person, null, modifier = Modifier.padding(8.dp).size(20.dp), tint = MaterialTheme.colorScheme.onPrimary)
            }
            Spacer(Modifier.width(10.dp))
            Column {
              Text("Coach ${slot.trainerName}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
              Text("Certified Studio Trainer • High-Intensity Coach", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
          }
        }

        // Details grid
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Column {
            Text("Station / Studio", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(slot.station, fontWeight = FontWeight.Bold)
          }
          Column {
            Text("Duration", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("${slot.durationMin} Minutes", fontWeight = FontWeight.Bold)
          }
          Column {
            Text("Time", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(slot.timeLabel, fontWeight = FontWeight.Bold)
          }
        }

        CapacityBar(slot.bookedCount, slot.capacity)
        Text(
          "${slot.bookedCount} spots booked out of ${slot.capacity} capacity (${slot.spotsLeft} remaining)",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Surface(color = MaterialTheme.colorScheme.surfaceContainerLow, shape = RoundedCornerShape(10.dp)) {
          Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Info, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(8.dp))
            Text("Bring a water bottle and sweat towel. Arrive 5 minutes before class start.", style = MaterialTheme.typography.labelSmall)
          }
        }
      }
    },
    confirmButton = {
      FitPrimaryButton(
        onClick = onBook,
        enabled = !slot.isCancelled,
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(if (slot.isFull) "Join Waitlist" else "Confirm Booking", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Close") }
    },
    shape = RoundedCornerShape(24.dp)
  )
}
