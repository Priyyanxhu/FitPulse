package com.example.fitpulsegym.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.fitpulsegym.data.local.MemberProfileEntity
import com.example.fitpulsegym.theme.FitDark
import com.example.fitpulsegym.theme.FitLime
import com.example.fitpulsegym.ui.components.FitPrimaryButton
import com.example.fitpulsegym.ui.components.StatusPill
import com.example.fitpulsegym.ui.components.StatusTone
import kotlin.math.roundToInt

@Composable
fun ProfileDialog(
  member: MemberProfileEntity?,
  onSave: (name: String, email: String, phone: String, weight: Float, height: Float, goal: String, locker: String) -> Unit,
  onLogout: () -> Unit,
  onDismiss: () -> Unit
) {
  var name by remember(member) { mutableStateOf(member?.name ?: "Aarav Sharma") }
  var email by remember(member) { mutableStateOf(member?.email ?: "aarav@example.com") }
  var phone by remember(member) { mutableStateOf(member?.phone ?: "+91 98765 43210") }
  var weightText by remember(member) { mutableStateOf(member?.weightKg?.toString() ?: "74.0") }
  var heightText by remember(member) { mutableStateOf(member?.heightCm?.toString() ?: "178.0") }
  var goal by remember(member) { mutableStateOf(member?.fitnessGoal ?: "Strength & Lean Muscle") }
  var locker by remember(member) { mutableStateOf(member?.lockerNumber ?: "L-42") }

  val weightVal = weightText.toFloatOrNull() ?: 70f
  val heightVal = heightText.toFloatOrNull() ?: 175f
  val heightM = (heightVal / 100f).coerceAtLeast(0.5f)
  val bmi = (weightVal / (heightM * heightM))
  val bmiRounded = (bmi * 10).roundToInt() / 10f

  val bmiCategory = when {
    bmi < 18.5f -> "Underweight" to StatusTone.WARNING
    bmi < 24.9f -> "Healthy / Normal" to StatusTone.SUCCESS
    bmi < 29.9f -> "Overweight" to StatusTone.WARNING
    else -> "High BMI" to StatusTone.ERROR
  }

  val goalsList = listOf("Strength & Lean Muscle", "Fat Loss & Shred", "Athletic Conditioning", "Yoga & Flexibility")

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(color = FitLime, contentColor = FitDark, shape = CircleShape) {
          Text(
            name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").ifEmpty { "ME" },
            modifier = Modifier.padding(10.dp),
            fontWeight = FontWeight.Black,
            style = MaterialTheme.typography.titleMedium
          )
        }
        Spacer(Modifier.width(12.dp))
        Column {
          Text("Member Profile", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleLarge)
          Text(member?.planName ?: "Pro Member", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }
    },
    text = {
      Column(
        Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Contact details
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Full Name") },
          leadingIcon = { Icon(Icons.Filled.Person, null) },
          singleLine = true,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = email,
          onValueChange = { email = it },
          label = { Text("Email Address") },
          leadingIcon = { Icon(Icons.Filled.Email, null) },
          singleLine = true,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text("Phone Number") },
          leadingIcon = { Icon(Icons.Filled.Phone, null) },
          singleLine = true,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.fillMaxWidth()
        )

        // Locker
        OutlinedTextField(
          value = locker,
          onValueChange = { locker = it },
          label = { Text("Assigned Locker #") },
          leadingIcon = { Icon(Icons.Filled.Lock, null) },
          singleLine = true,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.fillMaxWidth()
        )

        // Body Metrics & Live BMI Card
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
          Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Filled.Speed, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
              Spacer(Modifier.width(8.dp))
              Text("Body Metrics & BMI Calculator", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              OutlinedTextField(
                value = weightText,
                onValueChange = { weightText = it },
                label = { Text("Weight (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
              )
              OutlinedTextField(
                value = heightText,
                onValueChange = { heightText = it },
                label = { Text("Height (cm)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
              )
            }
            Spacer(Modifier.height(10.dp))
            Row(
              Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text("BMI: $bmiRounded", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium)
                Text("Calorie Target: ~${if (goal.contains("Fat")) "1,950" else "2,550"} kcal/day", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
              StatusPill(bmiCategory.first, bmiCategory.second)
            }
          }
        }

        // Fitness Goal
        Text("Primary Fitness Goal", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          goalsList.forEach { g ->
            FilterChip(
              selected = goal == g,
              onClick = { goal = g },
              label = { Text(g, fontWeight = if (goal == g) FontWeight.Bold else FontWeight.Normal) },
              shape = RoundedCornerShape(12.dp),
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
              ),
              modifier = Modifier.fillMaxWidth()
            )
          }
        }

        Spacer(Modifier.height(6.dp))
        OutlinedButton(
          onClick = { onDismiss(); onLogout() },
          shape = RoundedCornerShape(14.dp),
          colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("Logout / Switch Role", fontWeight = FontWeight.SemiBold)
        }
      }
    },
    confirmButton = {
      FitPrimaryButton(
        onClick = {
          onSave(
            name,
            email,
            phone,
            weightText.toFloatOrNull() ?: 74f,
            heightText.toFloatOrNull() ?: 178f,
            goal,
            locker
          )
          onDismiss()
        }
      ) {
        Text("Save Profile", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel") }
    },
    shape = RoundedCornerShape(24.dp)
  )
}
