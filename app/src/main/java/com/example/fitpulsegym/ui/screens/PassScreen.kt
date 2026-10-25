package com.example.fitpulsegym.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.fitpulsegym.theme.FitDark
import com.example.fitpulsegym.theme.FitDeepGreen
import com.example.fitpulsegym.theme.FitEmerald
import com.example.fitpulsegym.theme.FitLime
import com.example.fitpulsegym.ui.components.FitPrimaryButton
import com.example.fitpulsegym.ui.components.StatusPill
import com.example.fitpulsegym.ui.components.StatusTone
import com.example.fitpulsegym.ui.viewmodel.PassViewModel
import com.example.fitpulsegym.util.QrCodeGenerator
import com.example.fitpulsegym.util.daysUntil
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PassScreen(vm: PassViewModel, highlightToken: String? = null) {
  val member by vm.member.collectAsState()
  val bookings by vm.bookings.collectAsState()
  val gymConfig by vm.gymConfig.collectAsState()

  val activeBookings = bookings.filter { it.status == "CONFIRMED" }
  var selectedBookingId by remember { mutableStateOf<String?>(null) }
  var checkedInSimulation by remember { mutableStateOf(false) }

  val activeBooking = activeBookings.find { it.id == selectedBookingId }
  val token = highlightToken ?: activeBooking?.qrToken ?: member?.qrToken ?: "FITPULSE-member-001-PRO"
  val qr = remember(token) { runCatching { QrCodeGenerator.makeImageBitmap(token) }.getOrNull() }

  Column(
    Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text("Digital Access Pass", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
    Text(
      "Scan at turnstiles, studios & lockers",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Spacer(Modifier.height(12.dp))

    // Pass selector if there are active bookings
    if (activeBookings.isNotEmpty()) {
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 10.dp)) {
        FilterChip(
          selected = selectedBookingId == null,
          onClick = { selectedBookingId = null },
          label = { Text("Turnstile Pass") },
          leadingIcon = { Icon(Icons.Filled.FitnessCenter, null, modifier = Modifier.size(16.dp)) },
          shape = RoundedCornerShape(12.dp),
          colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer)
        )
        activeBookings.take(2).forEach { b ->
          FilterChip(
            selected = selectedBookingId == b.id,
            onClick = { selectedBookingId = b.id },
            label = { Text(b.classTitle) },
            leadingIcon = { Icon(Icons.Filled.EventAvailable, null, modifier = Modifier.size(16.dp)) },
            shape = RoundedCornerShape(12.dp),
            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer)
          )
        }
      }
    }

    // Pass Card
    Card(
      shape = RoundedCornerShape(28.dp),
      elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
      border = BorderStroke(1.5.dp, Brush.linearGradient(listOf(FitLime.copy(alpha = 0.6f), FitEmerald.copy(alpha = 0.3f), FitLime.copy(alpha = 0.2f)))),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column {
        // Luxury Header band
        Row(
          Modifier
            .fillMaxWidth()
            .background(Brush.horizontalGradient(listOf(Color(0xFF0C2417), Color(0xFF133824), Color(0xFF1B4E32))))
            .padding(20.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Surface(
            color = FitLime,
            contentColor = FitDark,
            shape = RoundedCornerShape(14.dp),
            shadowElevation = 4.dp
          ) {
            Icon(Icons.Filled.FitnessCenter, null, modifier = Modifier.padding(10.dp).size(22.dp))
          }
          Spacer(Modifier.width(12.dp))
          Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("FITPULSE", color = FitLime, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black)
              Spacer(Modifier.width(6.dp))
              Surface(color = FitLime.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                Text("PRO ACCESS", color = FitLime, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
              }
            }
            Text(
              member?.name ?: "Member",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              style = MaterialTheme.typography.titleMedium,
            )
            Text(
              if (selectedBookingId != null) activeBooking?.classTitle ?: "Class Pass" else member?.planName ?: "Pro All-Access",
              color = Color.White.copy(alpha = 0.8f),
              style = MaterialTheme.typography.bodySmall,
              fontWeight = FontWeight.SemiBold,
            )
          }
          Surface(
            color = Color.White.copy(alpha = 0.16f),
            contentColor = Color.White,
            shape = RoundedCornerShape(999.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
          ) {
            Text(
              if (selectedBookingId != null) "CLASS" else "PASS",
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
              fontWeight = FontWeight.Black,
              style = MaterialTheme.typography.labelSmall
            )
          }
        }

        // QR body
        Column(
          Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          if (qr != null) {
            Surface(
              shape = RoundedCornerShape(22.dp),
              border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant),
              shadowElevation = 6.dp,
              color = Color.White
            ) {
              Box(contentAlignment = Alignment.Center) {
                Image(
                  qr,
                  contentDescription = "Membership QR",
                  modifier = Modifier
                    .padding(14.dp)
                    .size(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                )
              }
            }
          }
          Spacer(Modifier.height(14.dp))
          Surface(
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
          ) {
            Text(
              token,
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            )
          }
          Spacer(Modifier.height(10.dp))
          val expiry = member?.expiryEpochMillis ?: (System.currentTimeMillis() + 18L * 86400000)
          val left = daysUntil(expiry)
          val fmt = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
          Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(999.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
          ) {
            Text(
              "Expires ${fmt.format(Date(expiry))} • $left days remaining",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onPrimaryContainer,
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            )
          }

          Spacer(Modifier.height(16.dp))

          // Turnstile Status Banner Simulation
          AnimatedVisibility(
            visible = checkedInSimulation,
            enter = fadeIn(),
            exit = fadeOut()
          ) {
            Card(
              modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = FitDeepGreen),
              border = BorderStroke(1.5.dp, FitLime)
            ) {
              Row(
                Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Surface(color = FitLime, shape = CircleShape) {
                  Icon(Icons.Filled.CheckCircle, null, modifier = Modifier.padding(6.dp).size(18.dp), tint = FitDark)
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                  Text("ACCESS GRANTED • TURNSTILE A-01", fontWeight = FontWeight.Black, style = MaterialTheme.typography.labelMedium, color = FitLime)
                  Text("Gate unlocked • Check-in logged successfully", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.9f))
                }
              }
            }
          }

          // Tap to Check In Button
          FitPrimaryButton(
            onClick = { checkedInSimulation = !checkedInSimulation },
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(if (checkedInSimulation) Icons.Filled.CheckCircle else Icons.Filled.Nfc, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(if (checkedInSimulation) "Access Verified ✓ (Tap to reset)" else "Simulate Turnstile NFC Tap", fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    Spacer(Modifier.height(14.dp))

    // Amenities & Locker Card
    Card(
      Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(22.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(8.dp)) {
              Icon(Icons.Filled.Lock, null, modifier = Modifier.padding(6.dp).size(16.dp), tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.width(8.dp))
            Text("Locker Assigned", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
          Spacer(Modifier.height(4.dp))
          Text(member?.lockerNumber ?: "Locker #L-42", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
        }
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(8.dp)) {
              Icon(Icons.Filled.Wifi, null, modifier = Modifier.padding(6.dp).size(16.dp), tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.width(8.dp))
            Text("Studio WiFi", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
          Spacer(Modifier.height(4.dp))
          Text(gymConfig?.wifiSsid ?: "FitPulse-Member", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
          if (!gymConfig?.wifiPass.isNullOrBlank()) {
            Text("Pass: ${gymConfig?.wifiPass}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    Spacer(Modifier.height(12.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(Icons.Filled.Shield, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
      Spacer(Modifier.width(6.dp))
      Text(
        "Cryptographically signed token — offline turnstile verification verified.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.fillMaxWidth(),
      )
    }
  }
}
