package com.example.fitpulsegym.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitpulsegym.data.model.dayName
import com.example.fitpulsegym.ui.components.EmptyStateCard
import com.example.fitpulsegym.ui.components.FitPrimaryButton
import com.example.fitpulsegym.ui.components.SectionHeader
import com.example.fitpulsegym.ui.components.StatusPill
import com.example.fitpulsegym.ui.components.StatusTone
import com.example.fitpulsegym.ui.viewmodel.BookingViewModel

@Composable
fun BookingsScreen(vm: BookingViewModel, snacks: SnackbarHostState, onShowQr: (String) -> Unit) {
  val list by vm.bookings.collectAsState()
  val msg by vm.message.collectAsState()
  LaunchedEffect(msg) { msg?.let { snacks.showSnackbar(it); vm.clearMsg() } }
  val active = list.filter { it.status == "CONFIRMED" || it.status == "WAITLIST" }
  val past = list.filter { it.status != "CONFIRMED" && it.status != "WAITLIST" }

  Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
    SectionHeader("My Bookings", "${active.size} upcoming • QR ready for check-in")
    LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
      items(active, key = { it.id }) { b ->
        Card(
          Modifier.fillMaxWidth().padding(vertical = 5.dp),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        ) {
          Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Surface(color = MaterialTheme.colorScheme.surfaceContainerHigh, shape = RoundedCornerShape(12.dp)) {
                Icon(Icons.Filled.EventAvailable, null, modifier = Modifier.padding(8.dp).size(20.dp))
              }
              Spacer(Modifier.width(10.dp))
              Text(b.classTitle, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
              StatusPill(
                if (b.status == "CONFIRMED") "Confirmed" else "Waitlist",
                if (b.status == "CONFIRMED") StatusTone.SUCCESS else StatusTone.WARNING,
              )
            }
            Spacer(Modifier.height(8.dp))
            Text(
              "${dayName(b.dayIndex)} • ${b.timeLabel} • Coach ${b.trainerName}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
              "QR  ${b.qrToken}",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              FitPrimaryButton(onClick = { onShowQr(b.qrToken) }, modifier = Modifier.weight(1f)) {
                Icon(Icons.Filled.QrCode2, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Show QR", fontWeight = FontWeight.Bold)
              }
              OutlinedButton(
                onClick = { vm.cancel(b.id, b.classId) },
                modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                shape = RoundedCornerShape(16.dp),
              ) { Text("Cancel") }
            }
          }
        }
      }
      if (past.isNotEmpty()) {
        item {
          Text(
            "History",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 10.dp),
          )
        }
        items(past, key = { it.id }) { b ->
          Card(
            Modifier.fillMaxWidth().padding(vertical = 4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
          ) {
            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
              Column(Modifier.weight(1f)) {
                Text(b.classTitle, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleSmall)
                Text("${b.status} • ${b.timeLabel}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
              StatusPill(b.status, StatusTone.NEUTRAL)
            }
          }
        }
      }
      if (active.isEmpty() && past.isEmpty()) {
        item {
          EmptyStateCard(
            title = "No bookings yet",
            subtitle = "Grab a spot from Schedule — your QR pass appears here instantly.",
            icon = Icons.Filled.CalendarMonth,
          )
        }
      }
    }
  }
}
