package com.example.fitpulsegym.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitpulsegym.data.model.ClassCategory
import com.example.fitpulsegym.data.model.dayFullName
import com.example.fitpulsegym.data.model.dayName
import com.example.fitpulsegym.ui.components.ClassCard
import com.example.fitpulsegym.ui.components.EmptyStateCard
import com.example.fitpulsegym.ui.components.FitPrimaryButton
import com.example.fitpulsegym.ui.components.SectionHeader
import com.example.fitpulsegym.ui.components.categoryIcon
import com.example.fitpulsegym.ui.viewmodel.BookingViewModel
import com.example.fitpulsegym.ui.viewmodel.ScheduleViewModel
import com.example.fitpulsegym.util.pushLocalNotification

@Composable
fun ScheduleScreen(scheduleVm: ScheduleViewModel, bookingVm: BookingViewModel, snacks: SnackbarHostState) {
  val day by scheduleVm.day.collectAsState()
  val query by scheduleVm.query.collectAsState()
  val cat by scheduleVm.category.collectAsState()
  val items by scheduleVm.filtered.collectAsState()
  val msg by bookingVm.message.collectAsState()
  val ctx = LocalContext.current

  LaunchedEffect(msg) {
    msg?.let { snacks.showSnackbar(it); bookingVm.clearMsg() }
  }

  Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
    SectionHeader("Class Schedule", dayFullName(day) + " • tap to book instantly")
    OutlinedTextField(
      value = query,
      onValueChange = scheduleVm::setQuery,
      label = { Text("Search class or trainer") },
      leadingIcon = { Icon(Icons.Filled.Search, null) },
      modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
      singleLine = true,
      shape = RoundedCornerShape(16.dp),
    )
    Spacer(Modifier.height(10.dp))
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      items((0..6).toList()) { d ->
        FilterChip(
          selected = d == day,
          onClick = { scheduleVm.setDay(d) },
          label = { Text(dayName(d), fontWeight = if (d == day) FontWeight.Bold else FontWeight.Medium) },
          shape = RoundedCornerShape(12.dp),
          colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer),
          modifier = Modifier.heightIn(min = 40.dp),
        )
      }
    }
    Spacer(Modifier.height(8.dp))
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      item {
        FilterChip(
          selected = cat == null,
          onClick = { scheduleVm.setCategory(null) },
          label = { Text("All") },
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.heightIn(min = 40.dp),
        )
      }
      items(ClassCategory.entries) { c ->
        FilterChip(
          selected = cat == c,
          onClick = { scheduleVm.setCategory(if (cat == c) null else c) },
          label = { Text(c.label) },
          leadingIcon = { Icon(categoryIcon(c.label), null, modifier = Modifier.size(16.dp)) },
          shape = RoundedCornerShape(12.dp),
          colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer),
          modifier = Modifier.heightIn(min = 40.dp),
        )
      }
    }
    Spacer(Modifier.height(4.dp))
    if (items.isEmpty()) {
      EmptyStateCard(
        title = "No classes match",
        subtitle = "Try another day, search term or category.",
        icon = Icons.Filled.CalendarMonth,
      )
    }
    LazyColumn(Modifier.weight(1f)) {
      items(items, key = { it.id }) { c ->
        ClassCard(c) {
          Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
              if (c.isCancelled) "Unavailable"
              else if (c.bookedCount >= c.capacity) "Only waitlist left"
              else "1-tap instant book",
              fontWeight = FontWeight.SemiBold,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.weight(1f),
            )
            FitPrimaryButton(
              onClick = { bookingVm.book(c.id) { t, b -> pushLocalNotification(ctx, t, b) } },
              enabled = !c.isCancelled,
            ) {
              Text(if (c.bookedCount >= c.capacity) "Waitlist" else "Book", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}
