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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitpulsegym.data.model.MembershipPlan
import com.example.fitpulsegym.data.seed.SeedData
import com.example.fitpulsegym.theme.FitDark
import com.example.fitpulsegym.theme.FitLime
import com.example.fitpulsegym.ui.components.EmptyStateCard
import com.example.fitpulsegym.ui.components.FitPrimaryButton
import com.example.fitpulsegym.ui.components.SectionHeader
import com.example.fitpulsegym.ui.components.StatusPill
import com.example.fitpulsegym.ui.components.StatusTone
import com.example.fitpulsegym.ui.viewmodel.MembershipViewModel
import com.example.fitpulsegym.util.daysUntil
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MembershipScreen(vm: MembershipViewModel) {
  val member by vm.member.collectAsState()
  val transactions by vm.transactions.collectAsState()
  val livePlans by vm.livePlans.collectAsState()

  val displayPlans = remember(livePlans) {
    if (livePlans.isNotEmpty()) {
      livePlans.map {
        MembershipPlan(
          id = it.id,
          name = it.name,
          priceInr = it.priceInr,
          durationDays = it.durationDays,
          perks = it.perks.split(",").map { s -> s.trim() }.filter { s -> s.isNotBlank() }
        )
      }
    } else {
      SeedData.plans()
    }
  }

  var selectedTab by remember { mutableIntStateOf(0) } // 0 = Plans, 1 = Invoices
  var pendingPlan: MembershipPlan? by remember { mutableStateOf(null) }

  Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
    SectionHeader("Membership & Billing", "Flexible plans, instant passes & billing invoices")

    // Active Membership Card
    Card(
      Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(22.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
      Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(color = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary, shape = RoundedCornerShape(16.dp)) {
          Icon(Icons.Filled.CardMembership, null, modifier = Modifier.padding(12.dp).size(24.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
          Text("Current active plan", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
          Text(
            member?.planName ?: "No plan",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
          )
          val exp = member?.expiryEpochMillis ?: 0L
          Text(
            "Valid until ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(exp))} • ${daysUntil(exp)} days remaining",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.SemiBold,
          )
        }
      }
    }

    Spacer(Modifier.height(10.dp))

    // Sub-Tabs
    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
      modifier = Modifier.clip(RoundedCornerShape(14.dp))
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = { Text("Available Plans (${displayPlans.size})", fontWeight = FontWeight.Bold) },
        icon = { Icon(Icons.Filled.CardMembership, null, modifier = Modifier.size(18.dp)) }
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = { Text("Invoices (${transactions.size})", fontWeight = FontWeight.Bold) },
        icon = { Icon(Icons.Filled.Receipt, null, modifier = Modifier.size(18.dp)) }
      )
    }

    Spacer(Modifier.height(10.dp))

    if (selectedTab == 0) {
      // Tab 0: Plans List
      LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        items(displayPlans, key = { it.id }) { p ->
          val isPopular = p.name.contains("Quarterly", ignoreCase = true) || p.name.contains("Pro", ignoreCase = true)
          Card(
            Modifier.fillMaxWidth().padding(vertical = 4.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(
              if (isPopular) 1.5.dp else 1.dp,
              if (isPopular) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
            ),
            elevation = CardDefaults.cardElevation(if (isPopular) 3.dp else 1.dp),
          ) {
            Column(Modifier.padding(18.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(p.name, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                if (isPopular) {
                  Surface(color = FitLime, contentColor = FitDark, shape = RoundedCornerShape(999.dp)) {
                    Row(Modifier.padding(horizontal = 10.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Filled.Star, null, modifier = Modifier.size(13.dp))
                      Spacer(Modifier.width(4.dp))
                      Text("POPULAR", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold)
                    }
                  }
                }
              }
              Spacer(Modifier.height(4.dp))
              Text(
                "₹${p.priceInr}  •  ${p.durationDays} days  •  ₹${p.priceInr / p.durationDays.coerceAtLeast(1)}/day",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall,
              )
              Spacer(Modifier.height(8.dp))
              p.perks.forEach {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                  Icon(Icons.Filled.Check, null, modifier = Modifier.size(15.dp), tint = MaterialTheme.colorScheme.primary)
                  Spacer(Modifier.width(8.dp))
                  Text(it, style = MaterialTheme.typography.bodySmall)
                }
              }
              Spacer(Modifier.height(12.dp))
              FitPrimaryButton(onClick = { pendingPlan = p }, modifier = Modifier.fillMaxWidth()) {
                Text("Upgrade / Renew ${p.name} — ₹${p.priceInr}", fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    } else {
      // Tab 1: Invoices & Receipts
      LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        items(transactions, key = { it.id }) { tx ->
          Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
          ) {
            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
              Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(12.dp)) {
                Icon(Icons.Filled.Receipt, null, modifier = Modifier.padding(8.dp).size(20.dp), tint = MaterialTheme.colorScheme.primary)
              }
              Spacer(Modifier.width(12.dp))
              Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(tx.planName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                  StatusPill("PAID", StatusTone.SUCCESS)
                }
                Spacer(Modifier.height(2.dp))
                val fmt = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                Text(
                  "${tx.invoiceNumber} • ${fmt.format(Date(tx.timestamp))}",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  "₹${tx.amountInr} via ${tx.paymentMethod}",
                  style = MaterialTheme.typography.bodySmall,
                  fontWeight = FontWeight.SemiBold,
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }
          }
        }
        if (transactions.isEmpty()) {
          item {
            EmptyStateCard(
              title = "No invoices yet",
              subtitle = "When you purchase or renew a membership, receipt invoices appear here.",
              icon = Icons.Filled.Receipt
            )
          }
        }
      }
    }
  }

  // Interactive Checkout Dialog
  pendingPlan?.let { p ->
    CheckoutDialog(
      plan = p,
      onDismiss = { pendingPlan = null },
      onConfirm = { method, finalPrice ->
        vm.buyWithPayment(p.name, p.durationDays, finalPrice, method)
        pendingPlan = null
      }
    )
  }
}

@Composable
private fun CheckoutDialog(
  plan: MembershipPlan,
  onDismiss: () -> Unit,
  onConfirm: (method: String, finalPrice: Int) -> Unit
) {
  var promoCode by remember { mutableStateOf("") }
  var appliedPromo by remember { mutableStateOf<String?>(null) }
  var discountPercent by remember { mutableIntStateOf(0) }
  var selectedMethod by remember { mutableStateOf("UPI (Google Pay / PhonePe)") }

  val basePrice = plan.priceInr
  val finalPrice = if (discountPercent > 0) basePrice * (100 - discountPercent) / 100 else basePrice

  val paymentMethods = listOf(
    "UPI (Google Pay / PhonePe)" to Icons.Filled.Payment,
    "Credit / Debit Card" to Icons.Filled.CreditCard,
    "Net Banking" to Icons.Filled.AccountBalance,
    "Pay at Front Desk" to Icons.Filled.QrCode2
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Filled.CardMembership, null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(8.dp))
        Text("Checkout: ${plan.name}", fontWeight = FontWeight.Bold)
      }
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Price Summary Box
        Surface(
          color = MaterialTheme.colorScheme.surfaceContainerHigh,
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(Modifier.padding(14.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Plan Duration", style = MaterialTheme.typography.bodySmall)
              Text("${plan.durationDays} Days", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(4.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Base Price", style = MaterialTheme.typography.bodySmall)
              Text("₹$basePrice", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
            }
            if (discountPercent > 0) {
              Spacer(Modifier.height(4.dp))
              Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Promo '$appliedPromo' ($discountPercent% OFF)", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                Text("-₹${basePrice - finalPrice}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
              }
            }
            Spacer(Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
              Text("Total Payable", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
              Text("₹$finalPrice", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            }
          }
        }

        // Promo Code
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          OutlinedTextField(
            value = promoCode,
            onValueChange = { promoCode = it.uppercase() },
            label = { Text("Promo (Try 'PULSE20')") },
            singleLine = true,
            leadingIcon = { Icon(Icons.Filled.Discount, null) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f)
          )
          OutlinedButton(
            onClick = {
              if (promoCode == "PULSE20") {
                appliedPromo = "PULSE20"
                discountPercent = 20
              } else if (promoCode == "FIT10") {
                appliedPromo = "FIT10"
                discountPercent = 10
              }
            },
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("Apply")
          }
        }

        Text("Select Payment Method", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
          paymentMethods.forEach { (m, icon) ->
            Row(
              Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .padding(vertical = 2.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              RadioButton(
                selected = selectedMethod == m,
                onClick = { selectedMethod = m }
              )
              Icon(icon, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
              Spacer(Modifier.width(8.dp))
              Text(m, style = MaterialTheme.typography.bodyMedium, fontWeight = if (selectedMethod == m) FontWeight.Bold else FontWeight.Normal)
            }
          }
        }
      }
    },
    confirmButton = {
      FitPrimaryButton(
        onClick = { onConfirm(selectedMethod, finalPrice) },
        modifier = Modifier.fillMaxWidth()
      ) {
        Text("Pay ₹$finalPrice & Activate", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel") }
    },
    shape = RoundedCornerShape(24.dp)
  )
}
