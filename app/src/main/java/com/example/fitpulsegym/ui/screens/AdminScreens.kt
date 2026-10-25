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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContactMail
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.fitpulsegym.data.local.BookingEntity
import com.example.fitpulsegym.data.local.EquipmentEntity
import com.example.fitpulsegym.data.local.GymExpenseEntity
import com.example.fitpulsegym.data.local.GymLockerEntity
import com.example.fitpulsegym.data.local.LeadInquiryEntity
import com.example.fitpulsegym.data.local.MembershipPlanEntity
import com.example.fitpulsegym.data.model.ClassCategory
import com.example.fitpulsegym.data.model.GymClass
import com.example.fitpulsegym.data.model.dayName
import com.example.fitpulsegym.theme.FitDark
import com.example.fitpulsegym.theme.FitLime
import com.example.fitpulsegym.ui.components.CapacityBar
import com.example.fitpulsegym.ui.components.CategoryPill
import com.example.fitpulsegym.ui.components.FitPrimaryButton
import com.example.fitpulsegym.ui.components.HeroCard
import com.example.fitpulsegym.ui.components.SectionHeader
import com.example.fitpulsegym.ui.components.StatusPill
import com.example.fitpulsegym.ui.components.StatusTone
import com.example.fitpulsegym.ui.components.categoryIcon
import com.example.fitpulsegym.ui.viewmodel.AdminViewModel
import com.example.fitpulsegym.util.daysUntil
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ==========================================
// 1. OWNER COMMAND DASHBOARD
// ==========================================
@Composable
fun AdminDashboardScreen(
  vm: AdminViewModel,
  onNavigateToSlots: () -> Unit = {},
  onNavigateToMembers: () -> Unit = {},
  onNavigateToFacility: () -> Unit = {},
  onNavigateToBusiness: () -> Unit = {}
) {
  val classes by vm.classes.collectAsState()
  val bookings by vm.bookings.collectAsState()
  val members by vm.members.collectAsState()
  val gymConfig by vm.gymConfig.collectAsState()
  val equipment by vm.equipment.collectAsState()
  val expenses by vm.expenses.collectAsState()
  val lockers by vm.lockers.collectAsState()
  val leads by vm.leads.collectAsState()

  val confirmed = bookings.count { it.status == "CONFIRMED" }
  val wait = bookings.count { it.status == "WAITLIST" }
  val fill = if (classes.isEmpty()) 0f else classes.map { if (it.capacity == 0) 0f else it.bookedCount.toFloat() / it.capacity }.average().toFloat()

  val totalExpenses = expenses.sumOf { it.amountInr }
  val totalRevenue = 184500
  val netProfit = totalRevenue - totalExpenses

  val occupiedLockers = lockers.count { it.status == "OCCUPIED" }
  val equipmentIssues = equipment.count { it.status == "UNDER_REPAIR" || it.status == "SERVICE_DUE" }

  var showBroadcastDialog by remember { mutableStateOf(false) }

  Column(
    Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    SectionHeader("Gym Owner HQ", "Command & operations center for your whole gym")

    // Studio Announcement Banner
    Card(
      Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
      Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(color = MaterialTheme.colorScheme.primary, shape = CircleShape) {
          Icon(Icons.Filled.Campaign, null, modifier = Modifier.padding(8.dp).size(20.dp), tint = MaterialTheme.colorScheme.onPrimary)
        }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
          Text("Active Broadcast Notice", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
          Text(
            gymConfig?.announcement ?: "All classes running on normal schedule today.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
          )
        }
        IconButton(onClick = { showBroadcastDialog = true }) {
          Icon(Icons.Filled.Edit, "Edit Announcement", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
        }
      }
    }

    // Equipment Alert Banner if any issue
    if (equipmentIssues > 0) {
      Card(
        Modifier
          .fillMaxWidth()
          .clickable { onNavigateToFacility() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
      ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Filled.Warning, null, tint = MaterialTheme.colorScheme.error)
          Spacer(Modifier.width(10.dp))
          Column(Modifier.weight(1f)) {
            Text("Facility Attention Required", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onErrorContainer)
            Text("$equipmentIssues machine(s) need maintenance or repair. Tap to manage.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
          }
        }
      }
    }

    // Key Stats Metrics Grid
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
      StatCard("Floor Headcount", "$occupiedLockers / 60", Icons.Filled.FitnessCenter, Modifier.weight(1f))
      StatCard("Active Athletes", "${members.size}", Icons.Filled.People, Modifier.weight(1f))
    }
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
      StatCard("Avg Occupancy", "${(fill * 100).toInt()}%", Icons.Filled.Groups, Modifier.weight(1f))
      StatCard("Trial Leads", "${leads.count { it.status != "CONVERTED" }}", Icons.Filled.ContactMail, Modifier.weight(1f))
    }

    // Financial Health Hero Card
    HeroCard {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
          Text("Monthly Financial Ledger", style = MaterialTheme.typography.labelLarge, color = Color.White.copy(alpha = 0.75f))
          Text("₹${"%,d".format(netProfit)}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = FitLime)
          Spacer(Modifier.height(4.dp))
          Text(
            "Revenue ₹${"%,d".format(totalRevenue)} • Expenses ₹${"%,d".format(totalExpenses)}",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.85f),
            fontWeight = FontWeight.SemiBold,
          )
        }
        Surface(color = FitLime, contentColor = FitDark, shape = RoundedCornerShape(16.dp)) {
          Icon(Icons.Filled.TrendingUp, null, modifier = Modifier.padding(12.dp).size(24.dp))
        }
      }
    }

    // Quick Command Shortcuts (4 Gym Pillars)
    Text("Manage Gym Pillars", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
      OwnerQuickAction("Slots & Sched", Icons.Filled.CalendarMonth, onNavigateToSlots, Modifier.weight(1f))
      OwnerQuickAction("Athletes & CRM", Icons.Filled.People, onNavigateToMembers, Modifier.weight(1f))
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
      OwnerQuickAction("Facility & Gear", Icons.Filled.FitnessCenter, onNavigateToFacility, Modifier.weight(1f))
      OwnerQuickAction("Finances & Plans", Icons.Filled.Receipt, onNavigateToBusiness, Modifier.weight(1f))
    }

    // Studio details footer card
    Card(
      Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
      Column(Modifier.padding(14.dp)) {
        Text(gymConfig?.gymName ?: "FitPulse Gym", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
        Text(gymConfig?.address ?: "Bangalore, India", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(6.dp))
        Text("Facility Hours: ${gymConfig?.openHours ?: "05:30 AM - 10:30 PM"} • Wi-Fi: ${gymConfig?.wifiSsid ?: "FitPulse"}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
      }
    }
  }

  if (showBroadcastDialog) {
    BroadcastDialog(
      current = gymConfig?.announcement ?: "",
      onDismiss = { showBroadcastDialog = false },
      onConfirm = {
        vm.broadcastAnnouncement(it)
        showBroadcastDialog = false
      }
    )
  }
}

@Composable
private fun OwnerQuickAction(label: String, icon: ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
  Card(
    modifier = modifier.clickable { onClick() },
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
      Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(12.dp)
      ) {
        Icon(icon, null, modifier = Modifier.padding(8.dp).size(20.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
      }
      Spacer(Modifier.width(10.dp))
      Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
    }
  }
}

@Composable
private fun BroadcastDialog(current: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
  var msg by remember { mutableStateOf(current) }
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Broadcast Studio Notice", fontWeight = FontWeight.Bold) },
    text = {
      Column {
        Text("Sends an instant alert to all member notifications and updates the home announcement banner.", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
          value = msg,
          onValueChange = { msg = it },
          label = { Text("Announcement text") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp)
        )
      }
    },
    confirmButton = {
      FitPrimaryButton(onClick = { onConfirm(msg.ifBlank { "Welcome to FitPulse!" }) }) {
        Text("Broadcast Notice", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel") }
    },
    shape = RoundedCornerShape(24.dp)
  )
}

// ==========================================
// 2. SLOT & SCHEDULE MANAGER (WITH ROSTER)
// ==========================================
@Composable
fun SlotManagerScreen(vm: AdminViewModel) {
  val classes by vm.classes.collectAsState()
  var showAdd by remember { mutableStateOf(false) }
  var selectedDay by remember { mutableStateOf<Int?>(null) }
  var inspectingSlot by remember { mutableStateOf<GymClass?>(null) }

  val filtered = remember(classes, selectedDay) {
    if (selectedDay == null) classes
    else classes.filter { it.dayIndex == selectedDay }
  }

  Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
      SectionHeader("Schedule & Slots", "${classes.size} classes scheduled this week")
      FitPrimaryButton(onClick = { showAdd = true }) {
        Icon(Icons.Filled.Add, null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text("New Slot", fontWeight = FontWeight.Bold)
      }
    }

    // Day filter
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      modifier = Modifier.padding(bottom = 8.dp)
    ) {
      item {
        FilterChip(
          selected = selectedDay == null,
          onClick = { selectedDay = null },
          label = { Text("All Days") },
          shape = RoundedCornerShape(12.dp)
        )
      }
      items((0..6).toList()) { d ->
        FilterChip(
          selected = selectedDay == d,
          onClick = { selectedDay = if (selectedDay == d) null else d },
          label = { Text(dayName(d)) },
          shape = RoundedCornerShape(12.dp),
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
          )
        )
      }
    }

    LazyColumn(Modifier.weight(1f)) {
      items(filtered, key = { it.id }) { c ->
        var cap by remember(c.id, c.capacity) { mutableFloatStateOf(c.capacity.toFloat()) }
        Card(
          Modifier.fillMaxWidth().padding(vertical = 5.dp),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        ) {
          Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              CategoryPill(c.category.label, categoryIcon(c.category.label))
              Spacer(Modifier.weight(1f))
              Text("${dayName(c.dayIndex)} • ${c.timeLabel}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
              Spacer(Modifier.width(8.dp))
              IconButton(onClick = { vm.deleteClass(c.id) }) {
                Icon(Icons.Filled.Delete, "Delete slot", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error)
              }
            }
            Spacer(Modifier.height(8.dp))
            Text(c.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text("Coach ${c.trainerName} • ${c.station}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(10.dp))
            CapacityBar(c.bookedCount, c.capacity)
            Spacer(Modifier.height(6.dp))
            Text("Capacity limit: ${cap.toInt()} spots", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
            Slider(cap, onValueChange = { cap = it }, valueRange = 1f..40f, steps = 38, onValueChangeFinished = { vm.setCapacity(c.id, cap.toInt()) })
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
              Switch(checked = !c.isCancelled, onCheckedChange = { vm.setCancelled(c.id, !it) })
              Text(
                if (c.isCancelled) "Cancelled" else "Open for booking",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
              )
              Spacer(Modifier.weight(1f))
              OutlinedButton(
                onClick = { inspectingSlot = c },
                shape = RoundedCornerShape(12.dp),
              ) {
                Icon(Icons.Filled.Groups, null, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("Roster (${c.bookedCount})")
              }
              OutlinedButton(
                onClick = { vm.setCancelled(c.id, !c.isCancelled) },
                shape = RoundedCornerShape(12.dp),
              ) {
                Text(if (c.isCancelled) "Re-open" else "Cancel")
              }
            }
          }
        }
      }
    }
  }

  if (showAdd) AddSlotDialog(vm) { showAdd = false }

  if (inspectingSlot != null) {
    AttendeeRosterDialog(vm = vm, slot = inspectingSlot!!) { inspectingSlot = null }
  }
}

@Composable
private fun AttendeeRosterDialog(vm: AdminViewModel, slot: GymClass, onDismiss: () -> Unit) {
  val attendees by produceState<List<BookingEntity>>(initialValue = emptyList(), slot.id) {
    value = vm.getAttendeesForClass(slot.id)
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Column {
        Text("Class Attendance Roster", fontWeight = FontWeight.Bold)
        Text(slot.title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
      }
    },
    text = {
      Column(Modifier.heightIn(max = 350.dp).fillMaxWidth()) {
        Text("Total Registered: ${attendees.size} athletes", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        if (attendees.isEmpty()) {
          Text("No athletes registered for this slot yet.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
          LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(attendees) { b ->
              Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
              ) {
                Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                  Surface(color = MaterialTheme.colorScheme.primary, shape = CircleShape) {
                    Icon(Icons.Filled.Person, null, modifier = Modifier.padding(6.dp).size(14.dp), tint = MaterialTheme.colorScheme.onPrimary)
                  }
                  Spacer(Modifier.width(8.dp))
                  Column(Modifier.weight(1f)) {
                    Text(b.qrToken, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    Text("Status: ${b.status} • Booked at ${b.timeLabel}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                  }
                  if (b.status != "ATTENDED") {
                    OutlinedButton(
                      onClick = { vm.scan(b.qrToken) },
                      shape = RoundedCornerShape(8.dp),
                      modifier = Modifier.height(32.dp)
                    ) {
                      Text("Check In", style = MaterialTheme.typography.labelSmall)
                    }
                  } else {
                    StatusPill("Attended", StatusTone.SUCCESS)
                  }
                }
              }
            }
          }
        }
      }
    },
    confirmButton = { Button(onClick = onDismiss) { Text("Done") } },
    shape = RoundedCornerShape(24.dp)
  )
}

@Composable
private fun AddSlotDialog(vm: AdminViewModel, onClose: () -> Unit) {
  var title by remember { mutableStateOf("Sunset Strength") }
  var trainer by remember { mutableStateOf("Vikram Rao") }
  var cat by remember { mutableStateOf(ClassCategory.STRENGTH) }
  var day by remember { mutableStateOf("1") }
  var hour by remember { mutableStateOf("18") }
  var min by remember { mutableStateOf("0") }
  var dur by remember { mutableStateOf("60") }
  var cap by remember { mutableStateOf("16") }
  var station by remember { mutableStateOf("Rack 5-8") }

  AlertDialog(
    onDismissRequest = onClose,
    title = { Text("Create Class Slot", fontWeight = FontWeight.Bold) },
    text = {
      Column(Modifier.verticalScroll(rememberScrollState())) {
        OutlinedTextField(title, { title = it }, label = { Text("Title") }, shape = RoundedCornerShape(14.dp))
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(trainer, { trainer = it }, label = { Text("Trainer Name") }, shape = RoundedCornerShape(14.dp))
        Spacer(Modifier.height(8.dp))
        Text("Category", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          items(ClassCategory.entries) { c ->
            FilterChip(
              selected = cat == c,
              onClick = { cat = c },
              label = { Text(c.label) },
              shape = RoundedCornerShape(12.dp),
            )
          }
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(day, { day = it }, label = { Text("Day 0-6") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp))
          OutlinedTextField(hour, { hour = it }, label = { Text("HH") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp))
          OutlinedTextField(min, { min = it }, label = { Text("MM") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp))
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(dur, { dur = it }, label = { Text("Mins") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp))
          OutlinedTextField(cap, { cap = it }, label = { Text("Capacity") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp))
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(station, { station = it }, label = { Text("Station / Room") }, shape = RoundedCornerShape(14.dp))
      }
    },
    confirmButton = {
      FitPrimaryButton(onClick = {
        val d = day.toIntOrNull()?.coerceIn(0, 6) ?: 0
        val h = hour.toIntOrNull()?.coerceIn(0, 23) ?: 18
        val m = min.toIntOrNull()?.coerceIn(0, 59) ?: 0
        val du = dur.toIntOrNull() ?: 60
        val cp = cap.toIntOrNull() ?: 16
        vm.addClass(title, cat, trainer, d, h, m, du, cp, station)
        onClose()
      }) {
        Text("Save Slot", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = { TextButton(onClick = onClose) { Text("Cancel") } },
    shape = RoundedCornerShape(24.dp)
  )
}

// ==========================================
// 3. MEMBER CRM & SALES LEADS PIPELINE
// ==========================================
@Composable
fun MemberDirectoryScreen(vm: AdminViewModel) {
  val members by vm.members.collectAsState()
  val leads by vm.leads.collectAsState()

  var tabIndex by remember { mutableIntStateOf(0) } // 0 = Athletes CRM, 1 = Sales Leads
  var search by remember { mutableStateOf("") }
  var showAddMemberDialog by remember { mutableStateOf(false) }
  var showAddLeadDialog by remember { mutableStateOf(false) }

  val filteredMembers = remember(members, search) {
    if (search.isBlank()) members
    else members.filter {
      it.name.contains(search, ignoreCase = true) ||
        it.phone.contains(search, ignoreCase = true) ||
        it.planName.contains(search, ignoreCase = true)
    }
  }

  val filteredLeads = remember(leads, search) {
    if (search.isBlank()) leads
    else leads.filter {
      it.name.contains(search, ignoreCase = true) ||
        it.phone.contains(search, ignoreCase = true) ||
        it.goal.contains(search, ignoreCase = true)
    }
  }

  Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
      SectionHeader("Members & Pipeline", "${members.size} athletes • ${leads.size} trial leads")
      FitPrimaryButton(onClick = {
        if (tabIndex == 0) showAddMemberDialog = true else showAddLeadDialog = true
      }) {
        Icon(if (tabIndex == 0) Icons.Filled.PersonAdd else Icons.Filled.ContactMail, null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(if (tabIndex == 0) "Add Athlete" else "Add Lead", fontWeight = FontWeight.Bold)
      }
    }

    TabRow(
      selectedTabIndex = tabIndex,
      containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
      modifier = Modifier.padding(bottom = 8.dp).clip(RoundedCornerShape(12.dp))
    ) {
      Tab(
        selected = tabIndex == 0,
        onClick = { tabIndex = 0 },
        text = { Text("Athletes CRM (${members.size})", fontWeight = FontWeight.Bold) },
        icon = { Icon(Icons.Filled.People, null, modifier = Modifier.size(16.dp)) }
      )
      Tab(
        selected = tabIndex == 1,
        onClick = { tabIndex = 1 },
        text = { Text("Trial Leads (${leads.size})", fontWeight = FontWeight.Bold) },
        icon = { Icon(Icons.Filled.ContactMail, null, modifier = Modifier.size(16.dp)) }
      )
    }

    OutlinedTextField(
      value = search,
      onValueChange = { search = it },
      label = { Text(if (tabIndex == 0) "Search athlete by name, phone or plan" else "Search lead by name, phone or goal") },
      leadingIcon = { Icon(Icons.Filled.Search, null) },
      singleLine = true,
      shape = RoundedCornerShape(16.dp),
      modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(10.dp))

    if (tabIndex == 0) {
      // ATHLETES CRM LIST
      LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        items(filteredMembers, key = { it.memberId }) { m ->
          val days = daysUntil(m.expiryEpochMillis)
          val isExpired = days <= 0
          val isExpiringSoon = days in 1..7

          Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
          ) {
            Column(Modifier.padding(16.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  color = MaterialTheme.colorScheme.primaryContainer,
                  contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                  shape = CircleShape
                ) {
                  Text(
                    m.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").ifEmpty { "M" },
                    modifier = Modifier.padding(10.dp),
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.titleSmall
                  )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                  Text(m.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                  Text(m.planName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                }
                StatusPill(
                  when {
                    isExpired -> "EXPIRED"
                    isExpiringSoon -> "$days DAYS LEFT"
                    else -> "ACTIVE"
                  },
                  when {
                    isExpired -> StatusTone.ERROR
                    isExpiringSoon -> StatusTone.WARNING
                    else -> StatusTone.SUCCESS
                  }
                )
                IconButton(onClick = { vm.deleteMember(m.memberId) }) {
                  Icon(Icons.Filled.Delete, "Remove member", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error)
                }
              }
              Spacer(Modifier.height(10.dp))
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Phone, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(4.dp))
                Text(m.phone, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(14.dp))
                Icon(Icons.Filled.Email, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(4.dp))
                Text(m.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
              Spacer(Modifier.height(8.dp))
              Text(
                "Locker: ${m.lockerNumber} • Goal: ${m.fitnessGoal} • Weight: ${m.weightKg}kg",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(Modifier.height(12.dp))
              Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                  onClick = { vm.renewMember(m.memberId, 30) },
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier.weight(1f)
                ) {
                  Text("Extend +30 Days", fontWeight = FontWeight.Bold)
                }
                FitPrimaryButton(
                  onClick = { vm.scan(m.qrToken) },
                  modifier = Modifier.weight(1f)
                ) {
                  Text("Check In", fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }
      }
    } else {
      // SALES LEADS PIPELINE
      LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        items(filteredLeads, key = { it.id }) { l ->
          Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
          ) {
            Column(Modifier.padding(16.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = CircleShape) {
                  Icon(Icons.Filled.ContactMail, null, modifier = Modifier.padding(10.dp).size(18.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                  Text(l.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                  Text("Goal: ${l.goal}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                }
                StatusPill(
                  l.status.replace("_", " "),
                  when (l.status) {
                    "CONVERTED" -> StatusTone.SUCCESS
                    "TRIAL_SCHEDULED" -> StatusTone.WARNING
                    "LOST" -> StatusTone.ERROR
                    else -> StatusTone.NEUTRAL
                  }
                )
                IconButton(onClick = { vm.deleteLead(l.id) }) {
                  Icon(Icons.Filled.Delete, "Remove lead", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error)
                }
              }
              Spacer(Modifier.height(8.dp))
              Text("Phone: ${l.phone} • Trial: ${l.trialDate}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              if (l.notes.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text("Notes: ${l.notes}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
              Spacer(Modifier.height(10.dp))
              Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                  onClick = {
                    val nextStatus = when (l.status) {
                      "NEW_INQUIRY" -> "TRIAL_SCHEDULED"
                      "TRIAL_SCHEDULED" -> "TRIAL_COMPLETED"
                      "TRIAL_COMPLETED" -> "CONVERTED"
                      else -> "CONVERTED"
                    }
                    vm.updateLeadStatus(l.id, nextStatus)
                  },
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier.weight(1f)
                ) {
                  Text("Advance Status", fontWeight = FontWeight.Bold)
                }
                FitPrimaryButton(
                  onClick = {
                    vm.addWalkInMember(l.name, l.phone, "member@fitpulse.in", "Pro Monthly", 30, "L-50")
                    vm.updateLeadStatus(l.id, "CONVERTED")
                  },
                  modifier = Modifier.weight(1f)
                ) {
                  Text("Convert to Member", fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }
      }
    }
  }

  if (showAddMemberDialog) {
    AddMemberDialog(
      onDismiss = { showAddMemberDialog = false },
      onConfirm = { name, phone, email, plan, days, locker ->
        vm.addWalkInMember(name, phone, email, plan, days, locker)
        showAddMemberDialog = false
      }
    )
  }

  if (showAddLeadDialog) {
    AddLeadDialog(
      onDismiss = { showAddLeadDialog = false },
      onConfirm = { name, phone, goal, trial, notes ->
        vm.addLead(name, phone, goal, trial, notes)
        showAddLeadDialog = false
      }
    )
  }
}

@Composable
private fun AddMemberDialog(
  onDismiss: () -> Unit,
  onConfirm: (name: String, phone: String, email: String, plan: String, days: Int, locker: String) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("+91 ") }
  var email by remember { mutableStateOf("") }
  var plan by remember { mutableStateOf("Pro Monthly") }
  var days by remember { mutableStateOf("30") }
  var locker by remember { mutableStateOf("L-50") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Register New Walk-In Athlete", fontWeight = FontWeight.Bold) },
    text = {
      Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(name, { name = it }, label = { Text("Full Name") }, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(phone, { phone = it }, label = { Text("Phone Number") }, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(email, { email = it }, label = { Text("Email Address") }, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(plan, { plan = it }, label = { Text("Plan") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
          OutlinedTextField(days, { days = it }, label = { Text("Days") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
        }
        OutlinedTextField(locker, { locker = it }, label = { Text("Assign Locker #") }, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
      }
    },
    confirmButton = {
      FitPrimaryButton(
        onClick = {
          if (name.isNotBlank()) {
            onConfirm(name, phone, email.ifBlank { "athlete@fitpulse.in" }, plan, days.toIntOrNull() ?: 30, locker)
          }
        }
      ) {
        Text("Register Member", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel") }
    },
    shape = RoundedCornerShape(24.dp)
  )
}

@Composable
private fun AddLeadDialog(
  onDismiss: () -> Unit,
  onConfirm: (name: String, phone: String, goal: String, trial: String, notes: String) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("+91 ") }
  var goal by remember { mutableStateOf("Weight Loss & Tone") }
  var trial by remember { mutableStateOf("Tomorrow 7:00 AM") }
  var notes by remember { mutableStateOf("Inquired at front desk") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Capture Prospective Lead", fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(name, { name = it }, label = { Text("Lead Full Name") }, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(phone, { phone = it }, label = { Text("Contact Phone") }, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(goal, { goal = it }, label = { Text("Primary Fitness Goal") }, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(trial, { trial = it }, label = { Text("Trial Date & Time") }, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(notes, { notes = it }, label = { Text("Notes / Trainer Preference") }, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
      }
    },
    confirmButton = {
      FitPrimaryButton(
        onClick = {
          if (name.isNotBlank()) onConfirm(name, phone, goal, trial, notes)
        }
      ) { Text("Save Lead", fontWeight = FontWeight.Bold) }
    },
    dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    shape = RoundedCornerShape(24.dp)
  )
}

// ==========================================
// 4. FACILITY, EQUIPMENT & LOCKER BAY
// ==========================================
@Composable
fun FacilityManagerScreen(vm: AdminViewModel) {
  val equipment by vm.equipment.collectAsState()
  val lockers by vm.lockers.collectAsState()

  var facilityTab by remember { mutableIntStateOf(0) } // 0 = Equipment, 1 = Lockers
  var showAddEquipment by remember { mutableStateOf(false) }
  var editingEquipment by remember { mutableStateOf<EquipmentEntity?>(null) }
  var selectedLockerToAssign by remember { mutableStateOf<GymLockerEntity?>(null) }
  var categoryFilter by remember { mutableStateOf("All") }

  val filteredEquipment = remember(equipment, categoryFilter) {
    if (categoryFilter == "All") equipment
    else equipment.filter { it.category.equals(categoryFilter, ignoreCase = true) }
  }

  Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
      SectionHeader("Facility & Assets", "${equipment.size} machines • ${lockers.size} lockers")
      if (facilityTab == 0) {
        FitPrimaryButton(onClick = { showAddEquipment = true }) {
          Icon(Icons.Filled.Add, null, modifier = Modifier.size(18.dp))
          Spacer(Modifier.width(6.dp))
          Text("Add Gear", fontWeight = FontWeight.Bold)
        }
      }
    }

    TabRow(
      selectedTabIndex = facilityTab,
      containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
      modifier = Modifier.padding(bottom = 8.dp).clip(RoundedCornerShape(12.dp))
    ) {
      Tab(
        selected = facilityTab == 0,
        onClick = { facilityTab = 0 },
        text = { Text("Equipment & Maintenance", fontWeight = FontWeight.Bold) },
        icon = { Icon(Icons.Filled.Build, null, modifier = Modifier.size(16.dp)) }
      )
      Tab(
        selected = facilityTab == 1,
        onClick = { facilityTab = 1 },
        text = { Text("Locker Bay", fontWeight = FontWeight.Bold) },
        icon = { Icon(Icons.Filled.Lock, null, modifier = Modifier.size(16.dp)) }
      )
    }

    if (facilityTab == 0) {
      // EQUIPMENT TAB
      LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(bottom = 8.dp)) {
        listOf("All", "Cardio", "Strength", "Free Weights", "Machines").forEach { cat ->
          item {
            FilterChip(
              selected = categoryFilter == cat,
              onClick = { categoryFilter = cat },
              label = { Text(cat) },
              shape = RoundedCornerShape(12.dp)
            )
          }
        }
      }

      LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        items(filteredEquipment, key = { it.id }) { eq ->
          val isOperational = eq.status == "OPERATIONAL"
          val isDue = eq.status == "SERVICE_DUE"

          Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
          ) {
            Column(Modifier.padding(16.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  color = if (isOperational) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                  shape = RoundedCornerShape(12.dp)
                ) {
                  Icon(Icons.Filled.FitnessCenter, null, modifier = Modifier.padding(8.dp).size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                  Text(eq.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                  Text("${eq.category} • Zone: ${eq.locationZone}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
                StatusPill(
                  eq.status.replace("_", " "),
                  when {
                    isOperational -> StatusTone.SUCCESS
                    isDue -> StatusTone.WARNING
                    else -> StatusTone.ERROR
                  }
                )
                IconButton(onClick = { vm.deleteEquipment(eq.id) }) {
                  Icon(Icons.Filled.Delete, "Remove gear", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error)
                }
              }
              Spacer(Modifier.height(8.dp))
              Text("Last Service: ${eq.lastServiceDate}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              if (eq.notes.isNotBlank()) {
                Text("Notes: ${eq.notes}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
              Spacer(Modifier.height(10.dp))
              Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                  onClick = { editingEquipment = eq },
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier.weight(1f)
                ) {
                  Icon(Icons.Filled.Edit, null, modifier = Modifier.size(14.dp))
                  Spacer(Modifier.width(4.dp))
                  Text("Update Status")
                }
                if (!isOperational) {
                  FitPrimaryButton(
                    onClick = { vm.updateEquipmentStatus(eq.id, "OPERATIONAL", "Serviced & verified operational") },
                    modifier = Modifier.weight(1f)
                  ) {
                    Text("Mark Repaired ✓", fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }
        }
      }
    } else {
      // LOCKER BAY TAB
      val occupied = lockers.count { it.status == "OCCUPIED" }
      val available = lockers.count { it.status == "AVAILABLE" }
      val maint = lockers.count { it.status == "MAINTENANCE" }

      Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 8.dp)) {
        StatCard("Occupied", "$occupied", Icons.Filled.Lock, Modifier.weight(1f))
        StatCard("Available", "$available", Icons.Filled.LockOpen, Modifier.weight(1f))
        StatCard("Maintenance", "$maint", Icons.Filled.Build, Modifier.weight(1f))
      }

      LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        items(lockers, key = { it.lockerNumber }) { l ->
          Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
          ) {
            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
              Surface(
                color = when (l.status) {
                  "AVAILABLE" -> MaterialTheme.colorScheme.primaryContainer
                  "OCCUPIED" -> MaterialTheme.colorScheme.tertiaryContainer
                  else -> MaterialTheme.colorScheme.errorContainer
                },
                shape = RoundedCornerShape(10.dp)
              ) {
                Text(l.lockerNumber, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
              }
              Spacer(Modifier.width(12.dp))
              Column(Modifier.weight(1f)) {
                Text(
                  if (l.assignedMemberName != null) "Assigned: ${l.assignedMemberName}" else "Locker ${l.lockerNumber}",
                  fontWeight = FontWeight.Bold,
                  style = MaterialTheme.typography.bodyMedium
                )
                Text(
                  if (l.assignedMemberPhone != null) "Phone: ${l.assignedMemberPhone} • ${l.zone}" else l.zone,
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
              StatusPill(
                l.status,
                when (l.status) {
                  "AVAILABLE" -> StatusTone.SUCCESS
                  "OCCUPIED" -> StatusTone.NEUTRAL
                  else -> StatusTone.ERROR
                }
              )
              Spacer(Modifier.width(8.dp))
              if (l.status == "OCCUPIED") {
                OutlinedButton(onClick = { vm.releaseLocker(l.lockerNumber) }, shape = RoundedCornerShape(10.dp)) {
                  Text("Release")
                }
              } else if (l.status == "AVAILABLE") {
                OutlinedButton(onClick = { selectedLockerToAssign = l }, shape = RoundedCornerShape(10.dp)) {
                  Text("Assign")
                }
              } else {
                OutlinedButton(onClick = { vm.setLockerStatus(l.lockerNumber, "AVAILABLE") }, shape = RoundedCornerShape(10.dp)) {
                  Text("Reopen")
                }
              }
            }
          }
        }
      }
    }
  }

  if (showAddEquipment) {
    AddEquipmentDialog(
      onDismiss = { showAddEquipment = false },
      onConfirm = { name, cat, status, zone, lastService, notes ->
        vm.addEquipment(name, cat, status, zone, lastService, notes)
        showAddEquipment = false
      }
    )
  }

  if (editingEquipment != null) {
    EditEquipmentDialog(
      eq = editingEquipment!!,
      onDismiss = { editingEquipment = null },
      onConfirm = { id, status, notes ->
        vm.updateEquipmentStatus(id, status, notes)
        editingEquipment = null
      }
    )
  }

  if (selectedLockerToAssign != null) {
    AssignLockerDialog(
      lockerNumber = selectedLockerToAssign!!.lockerNumber,
      onDismiss = { selectedLockerToAssign = null },
      onConfirm = { locker, name, phone ->
        vm.assignLocker(locker, name, phone)
        selectedLockerToAssign = null
      }
    )
  }
}

@Composable
private fun AddEquipmentDialog(
  onDismiss: () -> Unit,
  onConfirm: (name: String, category: String, status: String, zone: String, lastService: String, notes: String) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("Cardio") }
  var status by remember { mutableStateOf("OPERATIONAL") }
  var zone by remember { mutableStateOf("Main Floor") }
  var lastService by remember { mutableStateOf("23 Oct 2026") }
  var notes by remember { mutableStateOf("New equipment asset onboarded") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Add Facility Equipment", fontWeight = FontWeight.Bold) },
    text = {
      Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(name, { name = it }, label = { Text("Equipment Name (e.g. Treadmill #4)") }, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(category, { category = it }, label = { Text("Category") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
          OutlinedTextField(zone, { zone = it }, label = { Text("Zone") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
        }
        OutlinedTextField(lastService, { lastService = it }, label = { Text("Last Service Date") }, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(notes, { notes = it }, label = { Text("Notes / Calibration") }, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
      }
    },
    confirmButton = {
      FitPrimaryButton(onClick = { if (name.isNotBlank()) onConfirm(name, category, status, zone, lastService, notes) }) {
        Text("Save Equipment")
      }
    },
    dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    shape = RoundedCornerShape(24.dp)
  )
}

@Composable
private fun EditEquipmentDialog(
  eq: EquipmentEntity,
  onDismiss: () -> Unit,
  onConfirm: (id: String, status: String, notes: String) -> Unit
) {
  var status by remember { mutableStateOf(eq.status) }
  var notes by remember { mutableStateOf(eq.notes) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Update Machine Status", fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(eq.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        Text("Current Status", style = MaterialTheme.typography.labelSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          listOf("OPERATIONAL", "SERVICE_DUE", "UNDER_REPAIR").forEach { s ->
            FilterChip(
              selected = status == s,
              onClick = { status = s },
              label = { Text(s.replace("_", " ")) },
              shape = RoundedCornerShape(10.dp)
            )
          }
        }
        OutlinedTextField(notes, { notes = it }, label = { Text("Technician / Service Notes") }, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
      }
    },
    confirmButton = {
      FitPrimaryButton(onClick = { onConfirm(eq.id, status, notes) }) { Text("Update") }
    },
    dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    shape = RoundedCornerShape(24.dp)
  )
}

@Composable
private fun AssignLockerDialog(
  lockerNumber: String,
  onDismiss: () -> Unit,
  onConfirm: (locker: String, name: String, phone: String) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("+91 ") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Assign Locker $lockerNumber", fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(name, { name = it }, label = { Text("Athlete Name") }, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(phone, { phone = it }, label = { Text("Phone Number") }, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
      }
    },
    confirmButton = {
      FitPrimaryButton(onClick = { if (name.isNotBlank()) onConfirm(lockerNumber, name, phone) }) {
        Text("Assign Locker")
      }
    },
    dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    shape = RoundedCornerShape(24.dp)
  )
}

// ==========================================
// 5. BUSINESS, FINANCES, PLANS & STUDIO
// ==========================================
@Composable
fun BusinessSettingsScreen(vm: AdminViewModel) {
  val expenses by vm.expenses.collectAsState()
  val plans by vm.plans.collectAsState()
  val gymConfig by vm.gymConfig.collectAsState()
  val trainers by vm.trainers.collectAsState()

  var businessTab by remember { mutableIntStateOf(0) } // 0 = Financials, 1 = Plans, 2 = Studio & Staff
  var showAddExpense by remember { mutableStateOf(false) }

  Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
      SectionHeader("Business & Studio", "P&L financials, plans & studio configuration")
      if (businessTab == 0) {
        FitPrimaryButton(onClick = { showAddExpense = true }) {
          Icon(Icons.Filled.Add, null, modifier = Modifier.size(18.dp))
          Spacer(Modifier.width(6.dp))
          Text("Log Expense", fontWeight = FontWeight.Bold)
        }
      }
    }

    TabRow(
      selectedTabIndex = businessTab,
      containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
      modifier = Modifier.padding(bottom = 8.dp).clip(RoundedCornerShape(12.dp))
    ) {
      Tab(
        selected = businessTab == 0,
        onClick = { businessTab = 0 },
        text = { Text("Financials & P&L", fontWeight = FontWeight.Bold) },
        icon = { Icon(Icons.Filled.TrendingUp, null, modifier = Modifier.size(16.dp)) }
      )
      Tab(
        selected = businessTab == 1,
        onClick = { businessTab = 1 },
        text = { Text("Plans & Rates", fontWeight = FontWeight.Bold) },
        icon = { Icon(Icons.Filled.CardMembership, null, modifier = Modifier.size(16.dp)) }
      )
      Tab(
        selected = businessTab == 2,
        onClick = { businessTab = 2 },
        text = { Text("Studio & Staff", fontWeight = FontWeight.Bold) },
        icon = { Icon(Icons.Filled.Settings, null, modifier = Modifier.size(16.dp)) }
      )
    }

    when (businessTab) {
      0 -> {
        // FINANCIALS & EXPENSES TAB
        val totalRevenue = 184500
        val totalExpense = expenses.sumOf { it.amountInr }
        val netMargin = totalRevenue - totalExpense

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 8.dp)) {
          StatCard("Revenue", "₹${"%,d".format(totalRevenue)}", Icons.Filled.CardMembership, Modifier.weight(1f))
          StatCard("Expenses", "₹${"%,d".format(totalExpense)}", Icons.Filled.Receipt, Modifier.weight(1f))
          StatCard("Net Profit", "₹${"%,d".format(netMargin)}", Icons.Filled.TrendingUp, Modifier.weight(1f))
        }

        Text("Expense Ledger (${expenses.size} entries)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(vertical = 4.dp))

        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
          items(expenses, key = { it.id }) { ex ->
            val dateFmt = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(ex.timestamp))
            Card(
              Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
              Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(color = MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(10.dp)) {
                  Icon(Icons.Filled.Receipt, null, modifier = Modifier.padding(8.dp).size(20.dp), tint = MaterialTheme.colorScheme.error)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                  Text(ex.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                  Text("${ex.category} • Paid to: ${ex.paidTo}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                  Text(dateFmt, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text("₹${"%,d".format(ex.amountInr)}", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
                IconButton(onClick = { vm.deleteExpense(ex.id) }) {
                  Icon(Icons.Filled.Delete, "Remove expense", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error)
                }
              }
            }
          }
        }
      }
      1 -> {
        // PLANS & RATES TAB
        PlanManagerScreen(vm)
      }
      2 -> {
        // STUDIO & STAFF TAB
        StudioSettingsScreen(vm)
      }
    }
  }

  if (showAddExpense) {
    AddExpenseDialog(
      onDismiss = { showAddExpense = false },
      onConfirm = { title, category, amount, paidTo ->
        vm.addExpense(title, category, amount, paidTo)
        showAddExpense = false
      }
    )
  }
}

@Composable
private fun AddExpenseDialog(
  onDismiss: () -> Unit,
  onConfirm: (title: String, category: String, amount: Int, paidTo: String) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("Utilities") }
  var amount by remember { mutableStateOf("") }
  var paidTo by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Log Gym Expense", fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(title, { title = it }, label = { Text("Expense Title (e.g. AC Repair)") }, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(category, { category = it }, label = { Text("Category") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
          OutlinedTextField(amount, { amount = it }, label = { Text("Amount (₹)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
        }
        OutlinedTextField(paidTo, { paidTo = it }, label = { Text("Paid To / Vendor") }, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
      }
    },
    confirmButton = {
      FitPrimaryButton(onClick = {
        if (title.isNotBlank()) onConfirm(title, category, amount.toIntOrNull() ?: 1000, paidTo.ifBlank { "Vendor" })
      }) {
        Text("Record Expense")
      }
    },
    dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    shape = RoundedCornerShape(24.dp)
  )
}

// ==========================================
// STANDALONE PLAN MANAGER (REUSABLE)
// ==========================================
@Composable
fun PlanManagerScreen(vm: AdminViewModel) {
  val plans by vm.plans.collectAsState()
  var editingPlan by remember { mutableStateOf<MembershipPlanEntity?>(null) }
  var showAddPlan by remember { mutableStateOf(false) }

  Column(Modifier.fillMaxSize()) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
      Text("Plans & Packages (${plans.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
      FitPrimaryButton(onClick = { showAddPlan = true }) {
        Icon(Icons.Filled.Add, null, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(4.dp))
        Text("New Plan")
      }
    }
    Spacer(Modifier.height(8.dp))

    LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
      items(plans, key = { it.id }) { p ->
        Card(
          Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
          Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(p.name, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
              if (p.isPopular) {
                Surface(color = FitLime, contentColor = FitDark, shape = RoundedCornerShape(999.dp)) {
                  Row(Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, null, modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("POPULAR", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold)
                  }
                }
                Spacer(Modifier.width(8.dp))
              }
              IconButton(onClick = { editingPlan = p }) {
                Icon(Icons.Filled.Edit, "Edit plan", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
              }
              IconButton(onClick = { vm.deletePlan(p.id) }) {
                Icon(Icons.Filled.Delete, "Delete plan", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error)
              }
            }
            Spacer(Modifier.height(4.dp))
            Text("₹${p.priceInr} • ${p.durationDays} Days validity", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))
            p.perks.split(",").map { it.trim() }.filter { it.isNotBlank() }.forEach { perk ->
              Text("• $perk", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
          }
        }
      }
    }
  }

  if (showAddPlan || editingPlan != null) {
    PlanEditDialog(
      initial = editingPlan,
      onDismiss = { showAddPlan = false; editingPlan = null },
      onConfirm = { id, name, price, days, perks, isPop ->
        vm.addOrUpdatePlan(id, name, price, days, perks, isPop)
        showAddPlan = false
        editingPlan = null
      }
    )
  }
}

@Composable
private fun PlanEditDialog(
  initial: MembershipPlanEntity?,
  onDismiss: () -> Unit,
  onConfirm: (id: String?, name: String, price: Int, days: Int, perks: String, isPop: Boolean) -> Unit
) {
  var name by remember { mutableStateOf(initial?.name ?: "Elite Semi-Annual") }
  var price by remember { mutableStateOf(initial?.priceInr?.toString() ?: "11999") }
  var days by remember { mutableStateOf(initial?.durationDays?.toString() ?: "180") }
  var perks by remember { mutableStateOf(initial?.perks ?: "Unlimited access,Sauna pass,Nutrition consult") }
  var isPop by remember { mutableStateOf(initial?.isPopular ?: false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(if (initial == null) "Create Plan" else "Edit ${initial.name}", fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(name, { name = it }, label = { Text("Plan Name") }, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(price, { price = it }, label = { Text("Price (₹)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
          OutlinedTextField(days, { days = it }, label = { Text("Days") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
        }
        OutlinedTextField(perks, { perks = it }, label = { Text("Perks (comma-separated)") }, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
        Row(verticalAlignment = Alignment.CenterVertically) {
          Switch(checked = isPop, onCheckedChange = { isPop = it })
          Spacer(Modifier.width(8.dp))
          Text("Feature with 'POPULAR' badge", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
        }
      }
    },
    confirmButton = {
      FitPrimaryButton(
        onClick = {
          onConfirm(initial?.id, name, price.toIntOrNull() ?: 2499, days.toIntOrNull() ?: 30, perks, isPop)
        }
      ) {
        Text("Save Plan", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    shape = RoundedCornerShape(24.dp)
  )
}

// ==========================================
// STANDALONE STUDIO & STAFF SETTINGS
// ==========================================
@Composable
fun StudioSettingsScreen(vm: AdminViewModel) {
  val gymConfig by vm.gymConfig.collectAsState()
  val trainers by vm.trainers.collectAsState()

  var name by remember(gymConfig) { mutableStateOf(gymConfig?.gymName ?: "FitPulse Gym") }
  var address by remember(gymConfig) { mutableStateOf(gymConfig?.address ?: "Indiranagar, Bangalore") }
  var phone by remember(gymConfig) { mutableStateOf(gymConfig?.phone ?: "+91 80 4123 4567") }
  var announcement by remember(gymConfig) { mutableStateOf(gymConfig?.announcement ?: "") }
  var wifiSsid by remember(gymConfig) { mutableStateOf(gymConfig?.wifiSsid ?: "FitPulse-HighSpeed") }
  var wifiPass by remember(gymConfig) { mutableStateOf(gymConfig?.wifiPass ?: "PulseFit2026") }
  var hours by remember(gymConfig) { mutableStateOf(gymConfig?.openHours ?: "05:30 AM - 10:30 PM") }

  var showTrainerModal by remember { mutableStateOf(false) }

  Column(
    Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // Studio Info Card
    Card(
      Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
      Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Studio Identity & Timings", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
        OutlinedTextField(name, { name = it }, label = { Text("Gym / Studio Name") }, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(address, { address = it }, label = { Text("Studio Address") }, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(phone, { phone = it }, label = { Text("Contact Phone") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
          OutlinedTextField(hours, { hours = it }, label = { Text("Operating Hours") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
        }
      }
    }

    // Facility Wi-Fi
    Card(
      Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
      Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Filled.Wifi, null, tint = MaterialTheme.colorScheme.primary)
          Spacer(Modifier.width(8.dp))
          Text("Member Wi-Fi Access", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(wifiSsid, { wifiSsid = it }, label = { Text("Wi-Fi SSID") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
          OutlinedTextField(wifiPass, { wifiPass = it }, label = { Text("Password") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
        }
      }
    }

    FitPrimaryButton(
      onClick = {
        vm.updateGymConfig(name, address, phone, announcement, wifiSsid, wifiPass, hours)
      },
      modifier = Modifier.fillMaxWidth()
    ) {
      Text("Save Studio Settings", fontWeight = FontWeight.Bold)
    }

    Spacer(Modifier.height(6.dp))

    // Trainer Staff Roster
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
      Text("Coach & Trainer Roster (${trainers.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
      OutlinedButton(onClick = { showTrainerModal = true }, shape = RoundedCornerShape(12.dp)) {
        Icon(Icons.Filled.Add, null, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(4.dp))
        Text("Add Coach")
      }
    }

    trainers.forEach { t ->
      Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
      ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
          Surface(color = MaterialTheme.colorScheme.primary, shape = CircleShape) {
            Icon(Icons.Filled.Person, null, modifier = Modifier.padding(8.dp).size(18.dp), tint = MaterialTheme.colorScheme.onPrimary)
          }
          Spacer(Modifier.width(10.dp))
          Column(Modifier.weight(1f)) {
            Text(t.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
            Text(t.specialty, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            Text(t.bio, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
          IconButton(onClick = { vm.deleteTrainer(t.id) }) {
            Icon(Icons.Filled.Delete, "Remove coach", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error)
          }
        }
      }
    }
  }

  if (showTrainerModal) {
    AddTrainerDialog(
      onDismiss = { showTrainerModal = false },
      onConfirm = { coachName, specialty, bio, tPhone ->
        vm.addTrainer(coachName, specialty, bio, tPhone)
        showTrainerModal = false
      }
    )
  }
}

@Composable
private fun AddTrainerDialog(
  onDismiss: () -> Unit,
  onConfirm: (name: String, specialty: String, bio: String, phone: String) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var specialty by remember { mutableStateOf("Strength & Functional") }
  var bio by remember { mutableStateOf("5 yrs exp • Cert Trainer") }
  var phone by remember { mutableStateOf("+91 ") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Onboard New Studio Coach", fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(name, { name = it }, label = { Text("Coach Name") }, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(specialty, { specialty = it }, label = { Text("Specialty") }, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(bio, { bio = it }, label = { Text("Bio / Certifications") }, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(phone, { phone = it }, label = { Text("Phone") }, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
      }
    },
    confirmButton = {
      FitPrimaryButton(
        onClick = {
          if (name.isNotBlank()) onConfirm(name, specialty, bio, phone)
        }
      ) { Text("Add Coach") }
    },
    dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    shape = RoundedCornerShape(24.dp)
  )
}

// ==========================================
// 6. SCANNER & TURNSTILE
// ==========================================
@Composable
fun AttendanceScreen(vm: AdminViewModel) {
  val bookings by vm.bookings.collectAsState()
  val result by vm.scanResult.collectAsState()
  var token by remember { mutableStateOf("") }
  Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
    SectionHeader("Attendance Tracker", "Scan member / booking QR with camera")
    Card(
      Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(22.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
      Column(Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(12.dp)) {
            Icon(Icons.Filled.QrCode2, null, modifier = Modifier.padding(8.dp).size(20.dp))
          }
          Spacer(Modifier.width(10.dp))
          Column {
            Text("Camera scanner", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
            Text("Point at QR — live CameraX + ML Kit feed below.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }
        Spacer(Modifier.height(12.dp))
        CameraQrScanner(onScanned = { vm.scan(it) }, modifier = Modifier.clip(RoundedCornerShape(16.dp)))
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
          value = token,
          onValueChange = { token = it },
          label = { Text("QR token e.g. FITPULSE-...") },
          leadingIcon = { Icon(Icons.Filled.QrCode2, null) },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true,
          shape = RoundedCornerShape(16.dp),
        )
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          FitPrimaryButton(onClick = { vm.scan(token) }, modifier = Modifier.weight(1f)) { Text("Mark present", fontWeight = FontWeight.Bold) }
          OutlinedButton(
            onClick = { token = "FITPULSE-member-001-PRO"; vm.scan("FITPULSE-member-001-PRO") },
            modifier = Modifier.heightIn(min = 48.dp),
            shape = RoundedCornerShape(16.dp),
          ) { Text("Demo pass") }
        }
        result?.let {
          val ok = it.startsWith("✅") || it.startsWith("✓") || it.contains("marked", ignoreCase = true) || it.contains("success", ignoreCase = true)
          val clean = it.removePrefix("✅").removePrefix("❌").trim()
          Spacer(Modifier.height(10.dp))
          Surface(
            color = if (ok) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
            shape = RoundedCornerShape(14.dp),
          ) {
            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
              Icon(
                if (ok) Icons.Filled.CheckCircle else Icons.Filled.Error,
                null,
                modifier = Modifier.size(18.dp),
              )
              Spacer(Modifier.width(8.dp))
              Text(clean, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
            }
          }
        }
      }
    }
    Spacer(Modifier.height(10.dp))
    Text(
      "Today's check-ins (${bookings.count { it.status == "ATTENDED" }})",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold,
    )
    Spacer(Modifier.height(6.dp))
    LazyColumn(Modifier.weight(1f)) {
      items(bookings.filter { it.status == "ATTENDED" }, key = { it.id }) { b ->
        Card(
          Modifier.fillMaxWidth().padding(vertical = 4.dp),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        ) {
          Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(10.dp)) {
              Icon(Icons.Filled.Verified, null, modifier = Modifier.padding(8.dp).size(18.dp))
            }
            Spacer(Modifier.width(10.dp))
            Column {
              Text(b.classTitle, fontWeight = FontWeight.SemiBold)
              Text("${b.trainerName} • ${b.timeLabel}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun StatCard(label: String, value: String, icon: ImageVector, mod: Modifier = Modifier) {
  Card(
    mod,
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(Modifier.padding(16.dp)) {
      Surface(
        color = FitLime.copy(alpha = 0.15f),
        shape = RoundedCornerShape(12.dp)
      ) {
        Icon(icon, null, modifier = Modifier.padding(8.dp).size(20.dp), tint = FitLime)
      }
      Spacer(Modifier.height(12.dp))
      Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
      Spacer(Modifier.height(2.dp))
      Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
    }
  }
}
