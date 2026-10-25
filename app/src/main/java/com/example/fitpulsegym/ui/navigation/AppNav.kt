package com.example.fitpulsegym.ui.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fitpulsegym.theme.FitDark
import com.example.fitpulsegym.theme.FitLime
import com.example.fitpulsegym.ui.screens.AdminDashboardScreen
import com.example.fitpulsegym.ui.screens.AttendanceScreen
import com.example.fitpulsegym.ui.screens.AuthScreen
import com.example.fitpulsegym.ui.screens.BookingsScreen
import com.example.fitpulsegym.ui.screens.BusinessSettingsScreen
import com.example.fitpulsegym.ui.screens.FacilityManagerScreen
import com.example.fitpulsegym.ui.screens.GymPulseScheduleScreen
import com.example.fitpulsegym.ui.screens.HomeDashboardScreen
import com.example.fitpulsegym.ui.screens.MemberDirectoryScreen
import com.example.fitpulsegym.ui.screens.MembershipScreen
import com.example.fitpulsegym.ui.screens.PassScreen
import com.example.fitpulsegym.ui.screens.PlanManagerScreen
import com.example.fitpulsegym.ui.screens.ProfileDialog
import com.example.fitpulsegym.ui.screens.SlotManagerScreen
import com.example.fitpulsegym.ui.screens.StudioSettingsScreen
import com.example.fitpulsegym.ui.screens.WorkoutsScreen
import com.example.fitpulsegym.ui.viewmodel.AdminViewModel
import com.example.fitpulsegym.ui.viewmodel.AuthViewModel
import com.example.fitpulsegym.ui.viewmodel.BookingViewModel
import com.example.fitpulsegym.ui.viewmodel.GymPulseScheduleViewModel
import com.example.fitpulsegym.ui.viewmodel.HomeDashboardViewModel
import com.example.fitpulsegym.ui.viewmodel.MembershipViewModel
import com.example.fitpulsegym.ui.viewmodel.PassViewModel
import com.example.fitpulsegym.ui.viewmodel.ProgressViewModel
import com.example.fitpulsegym.ui.viewmodel.ScheduleViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Tab(val label: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitPulseNav(authVm: AuthViewModel = hiltViewModel()) {
  val nav = rememberNavController()
  val loggedIn by authVm.loggedIn.collectAsState()
  val role by authVm.role.collectAsState()
  NavHost(nav, startDestination = "auth") {
    composable("auth") {
      if (loggedIn) {
        if (role == "admin") AdminHome(onLogout = { authVm.logout(); nav.navigate("auth") { popUpTo(0) } })
        else MemberHome(onLogout = { authVm.logout(); nav.navigate("auth") { popUpTo(0) } })
      } else {
        AuthScreen(onLogin = {
          authVm.login(it)
        })
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberHome(onLogout: () -> Unit) {
  val tabs = listOf(
    Tab("Home", Icons.Filled.Dashboard),
    Tab("Schedule", Icons.Filled.CalendarMonth),
    Tab("Workouts", Icons.Filled.FitnessCenter),
    Tab("Bookings", Icons.Filled.EventAvailable),
    Tab("Pass & Plan", Icons.Filled.QrCode2),
  )
  var idx by remember { mutableIntStateOf(0) }
  var passSubTab by remember { mutableIntStateOf(0) } // 0 = Pass, 1 = Plan
  var qrToken by remember { mutableStateOf<String?>(null) }
  var showAlerts by remember { mutableStateOf(false) }
  var showProfile by remember { mutableStateOf(false) }
  val snacks = remember { SnackbarHostState() }

  val scheduleVm: ScheduleViewModel = hiltViewModel()
  val bookingVm: BookingViewModel = hiltViewModel()
  val passVm: PassViewModel = hiltViewModel()
  val progressVm: ProgressViewModel = hiltViewModel()
  val memberVm: MembershipViewModel = hiltViewModel()
  val homeVm: HomeDashboardViewModel = hiltViewModel()
  val pulseScheduleVm: GymPulseScheduleViewModel = hiltViewModel()

  val member by progressVm.member.collectAsState()
  val alerts by scheduleVm.alerts.collectAsState(initial = emptyList())
  val unreadAlerts = alerts.count { !it.read }

  Scaffold(
    snackbarHost = { SnackbarHost(snacks) },
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(color = FitLime, contentColor = FitDark, shape = CircleShape) {
              Icon(Icons.Filled.FitnessCenter, null, modifier = Modifier.padding(7.dp).size(18.dp))
            }
            Spacer(Modifier.width(10.dp))
            Column {
              Text("FitPulse", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleLarge)
              Text(
                when (idx) {
                  0 -> "Today at a glance"
                  1 -> "Live class availability"
                  2 -> "Lifts, streaks & guides"
                  3 -> "Your upcoming sessions"
                  else -> if (passSubTab == 0) "Turnstile QR pass" else "Plans & billing invoices"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
          }
        },
        actions = {
          IconButton(onClick = { showAlerts = true }) {
            BadgedBox(
              badge = {
                if (unreadAlerts > 0) {
                  Badge { Text("$unreadAlerts") }
                }
              }
            ) {
              Icon(Icons.Filled.Notifications, "alerts")
            }
          }
          // Profile avatar chip
          Surface(
            color = FitLime,
            contentColor = FitDark,
            shape = CircleShape,
            modifier = Modifier
              .padding(end = 12.dp)
              .clickable { showProfile = true }
          ) {
            Text(
              member?.name?.split(" ")?.mapNotNull { it.firstOrNull()?.toString() }?.take(2)?.joinToString("")?.ifEmpty { "AS" } ?: "AS",
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              fontWeight = FontWeight.Black,
              style = MaterialTheme.typography.labelMedium,
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
      )
    },
    bottomBar = {
      NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainerLow) {
        tabs.forEachIndexed { i, t ->
          NavigationBarItem(
            selected = idx == i,
            onClick = { idx = i },
            icon = { Icon(t.icon, t.label) },
            label = { Text(t.label, fontWeight = if (idx == i) FontWeight.Bold else FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
              indicatorColor = MaterialTheme.colorScheme.primaryContainer,
            ),
          )
        }
      }
    },
  ) { pad ->
    Box(Modifier.padding(pad).fillMaxSize()) {
      when (idx) {
        0 -> HomeDashboardScreen(
          vm = homeVm,
          onBrowseSchedule = { idx = 1 },
          onOpenPass = { idx = 4; passSubTab = 0 },
          onOpenWorkouts = { idx = 2 },
          onOpenProfile = { showProfile = true }
        )
        1 -> GymPulseScheduleScreen(pulseScheduleVm, snacks)
        2 -> WorkoutsScreen(progressVm)
        3 -> BookingsScreen(bookingVm, snacks) {
          qrToken = it
          idx = 4
          passSubTab = 0
        }
        4 -> {
          // Pass & Plan combined with segmented control
          Column(Modifier.fillMaxSize()) {
            TabRow(
              selectedTabIndex = passSubTab,
              containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp).clip(RoundedCornerShape(14.dp))
            ) {
              Tab(
                selected = passSubTab == 0,
                onClick = { passSubTab = 0 },
                text = { Text("Turnstile Pass", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Filled.QrCode2, null, modifier = Modifier.size(18.dp)) }
              )
              Tab(
                selected = passSubTab == 1,
                onClick = { passSubTab = 1 },
                text = { Text("Membership Plans", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Filled.CardMembership, null, modifier = Modifier.size(18.dp)) }
              )
            }
            if (passSubTab == 0) {
              PassScreen(passVm, qrToken)
            } else {
              MembershipScreen(memberVm)
            }
          }
        }
      }
    }
  }

  // Profile Dialog
  if (showProfile) {
    ProfileDialog(
      member = member,
      onSave = { name, email, phone, weight, height, goal, locker ->
        progressVm.updateProfile(name, email, phone, weight, height, goal, locker)
      },
      onLogout = onLogout,
      onDismiss = { showProfile = false }
    )
  }

  // Alerts Dialog
  if (showAlerts) {
    AlertsDialog(
      vm = scheduleVm,
      bookingVm = bookingVm,
      onClose = { showAlerts = false }
    )
  }
}

@Composable
private fun AlertsDialog(
  vm: ScheduleViewModel,
  bookingVm: BookingViewModel,
  onClose: () -> Unit
) {
  val alerts by vm.alerts.collectAsState(initial = emptyList())
  var filter by remember { mutableStateOf("All") }

  val filteredAlerts = remember(alerts, filter) {
    when (filter) {
      "Bookings" -> alerts.filter { it.message.contains("Booked", ignoreCase = true) || it.message.contains("attended", ignoreCase = true) }
      "Waitlist" -> alerts.filter { it.message.contains("waitlist", ignoreCase = true) || it.message.contains("opened", ignoreCase = true) }
      "Billing" -> alerts.filter { it.message.contains("activated", ignoreCase = true) || it.message.contains("Invoice", ignoreCase = true) }
      else -> alerts
    }
  }

  AlertDialog(
    onDismissRequest = onClose,
    title = {
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text("Notifications", fontWeight = FontWeight.Bold)
        if (alerts.any { !it.read }) {
          TextButton(onClick = { bookingVm.markRead() }) {
            Text("Mark read", style = MaterialTheme.typography.labelMedium)
          }
        }
      }
    },
    text = {
      Column {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(bottom = 8.dp)) {
          listOf("All", "Bookings", "Waitlist", "Billing").forEach { f ->
            FilterChip(
              selected = filter == f,
              onClick = { filter = f },
              label = { Text(f, style = MaterialTheme.typography.labelSmall) },
              shape = RoundedCornerShape(10.dp)
            )
          }
        }
        LazyColumn(Modifier.height(300.dp)) {
          items(filteredAlerts.take(25)) { a ->
            Card(
              Modifier.fillMaxWidth().padding(vertical = 4.dp),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            ) {
              Column(Modifier.padding(12.dp)) {
                Text(a.message, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(4.dp))
                Text(
                  SimpleDateFormat("dd MMM hh:mm a", Locale.getDefault()).format(Date(a.timestamp)),
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }
            }
          }
          if (filteredAlerts.isEmpty()) {
            item {
              Text("No notifications in this filter.", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(16.dp))
            }
          }
        }
      }
    },
    confirmButton = { Button(onClick = onClose) { Text("Done") } },
    shape = RoundedCornerShape(24.dp),
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHome(onLogout: () -> Unit) {
  val tabs = listOf(
    Tab("Dashboard", Icons.Filled.Dashboard),
    Tab("Schedule", Icons.Filled.CalendarMonth),
    Tab("Members & Leads", Icons.Filled.People),
    Tab("Facility & Gear", Icons.Filled.FitnessCenter),
    Tab("Business & Studio", Icons.Filled.TrendingUp),
  )
  var idx by remember { mutableIntStateOf(0) }
  val vm: AdminViewModel = hiltViewModel()
  var showLogout by remember { mutableStateOf(false) }
  var showScanner by remember { mutableStateOf(false) }
  var isMemberPreview by remember { mutableStateOf(false) }

  if (isMemberPreview) {
    Column(Modifier.fillMaxSize()) {
      Surface(
        color = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Person, null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text("Previewing FitPulse as Athlete", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
          }
          OutlinedButton(
            onClick = { isMemberPreview = false },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.height(32.dp)
          ) {
            Text("Back to Owner HQ", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold)
          }
        }
      }
      Box(Modifier.weight(1f)) {
        MemberHome(onLogout = { isMemberPreview = false })
      }
    }
    return
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(color = FitLime, contentColor = FitDark, shape = CircleShape) {
              Icon(Icons.Filled.FitnessCenter, null, modifier = Modifier.padding(7.dp).size(18.dp))
            }
            Spacer(Modifier.width(10.dp))
            Column {
              Text("FitPulse • Gym Owner HQ", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleLarge)
              Text(
                when (idx) {
                  0 -> "Live occupancy & financials"
                  1 -> "Schedule & capacity limits"
                  2 -> "Member CRM & sales pipeline"
                  3 -> "Machines, servicing & lockers"
                  else -> "P&L financials, plans & studio"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        },
        actions = {
          IconButton(onClick = { showScanner = true }) {
            Icon(Icons.Filled.QrCodeScanner, "Turnstile Scanner", tint = MaterialTheme.colorScheme.primary)
          }
          OutlinedButton(
            onClick = { isMemberPreview = true },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(end = 4.dp).height(36.dp)
          ) {
            Icon(Icons.Filled.Person, null, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text("Member View", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
          }
          TextButton(onClick = { showLogout = true }) { Text("Logout", fontWeight = FontWeight.Bold) }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
      )
    },
    bottomBar = {
      NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainerLow) {
        tabs.forEachIndexed { i, t ->
          NavigationBarItem(
            selected = idx == i,
            onClick = { idx = i },
            icon = { Icon(t.icon, t.label) },
            label = { Text(t.label, fontWeight = if (idx == i) FontWeight.Bold else FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
              indicatorColor = MaterialTheme.colorScheme.primaryContainer,
            ),
          )
        }
      }
    },
  ) { pad ->
    Box(Modifier.padding(pad).fillMaxSize()) {
      when (idx) {
        0 -> AdminDashboardScreen(
          vm = vm,
          onNavigateToSlots = { idx = 1 },
          onNavigateToMembers = { idx = 2 },
          onNavigateToFacility = { idx = 3 },
          onNavigateToBusiness = { idx = 4 }
        )
        1 -> SlotManagerScreen(vm)
        2 -> MemberDirectoryScreen(vm)
        3 -> FacilityManagerScreen(vm)
        4 -> BusinessSettingsScreen(vm)
      }
    }
  }

  // Scanner Dialog
  if (showScanner) {
    AlertDialog(
      onDismissRequest = { showScanner = false },
      title = {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
          Text("Turnstile & Pass Scanner", fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Box(Modifier.height(480.dp).fillMaxWidth()) {
          AttendanceScreen(vm)
        }
      },
      confirmButton = {
        Button(onClick = { showScanner = false }) { Text("Close Scanner") }
      },
      shape = RoundedCornerShape(24.dp),
    )
  }

  if (showLogout) {
    AlertDialog(
      onDismissRequest = { showLogout = false },
      title = { Text("Logout from Owner Panel?", fontWeight = FontWeight.Bold) },
      text = { Text("Return to the role login picker.") },
      confirmButton = { Button(onClick = { showLogout = false; onLogout() }) { Text("Logout") } },
      dismissButton = { TextButton(onClick = { showLogout = false }) { Text("Stay") } },
      shape = RoundedCornerShape(24.dp),
    )
  }
}
