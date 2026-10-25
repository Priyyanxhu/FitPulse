package com.example.fitpulsegym.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.SportsMma
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitpulsegym.data.model.GymClass
import com.example.fitpulsegym.theme.FitDark
import com.example.fitpulsegym.theme.FitDeepGreen
import com.example.fitpulsegym.theme.FitEmerald
import com.example.fitpulsegym.theme.FitLime
import com.example.fitpulsegym.theme.FitLimeSoft
import com.example.fitpulsegym.theme.Success
import com.example.fitpulsegym.theme.SuccessContainer
import com.example.fitpulsegym.theme.Warning
import com.example.fitpulsegym.theme.WarningContainer

// ── Category → icon mapping (no emoji icons per UX guidelines) ──
fun categoryIcon(name: String): ImageVector =
  when (name.uppercase()) {
    "HIIT" -> Icons.Filled.Bolt
    "YOGA" -> Icons.Filled.SelfImprovement
    "SPIN" -> Icons.Filled.DirectionsBike
    "CROSSFIT" -> Icons.Filled.FitnessCenter
    "WEIGHTLIFTING", "STRENGTH" -> Icons.Filled.FitnessCenter
    "PILATES" -> Icons.Filled.AccessibilityNew
    "BOXING" -> Icons.Filled.SportsMma
    "ZUMBA", "DANCE" -> Icons.Filled.MusicNote
    else -> Icons.Filled.FitnessCenter
  }

@Composable
fun SectionHeader(
  title: String,
  subtitle: String = "",
  actionLabel: String? = null,
  onAction: (() -> Unit)? = null,
) {
  Row(
    Modifier.fillMaxWidth().padding(vertical = 8.dp),
    verticalAlignment = Alignment.Bottom,
  ) {
    Column(Modifier.weight(1f)) {
      Text(
        title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.2).spFix(),
      )
      if (subtitle.isNotBlank()) {
        Spacer(Modifier.height(2.dp))
        Text(
          subtitle,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
        )
      }
    }
    if (actionLabel != null && onAction != null) {
      Spacer(Modifier.width(12.dp))
      OutlinedButton(onClick = onAction) { Text(actionLabel) }
    }
  }
}

// tiny helper to keep letterSpacing readable
private fun Double.spFix(): androidx.compose.ui.unit.TextUnit =
  androidx.compose.ui.unit.TextUnit(this.toFloat(), androidx.compose.ui.unit.TextUnitType.Sp)

@Composable
fun CategoryPill(label: String, icon: ImageVector? = null) {
  Surface(
    color = MaterialTheme.colorScheme.surfaceContainerHigh,
    contentColor = MaterialTheme.colorScheme.onSurface,
    shape = RoundedCornerShape(10.dp),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
  ) {
    Row(
      Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      Icon(
        imageVector = icon ?: categoryIcon(label),
        contentDescription = null,
        modifier = Modifier.size(14.dp),
        tint = MaterialTheme.colorScheme.primary
      )
      Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold)
    }
  }
}

@Composable
fun StatusPill(text: String, tone: StatusTone) {
  val (bg, fg, border) = when (tone) {
    StatusTone.SUCCESS -> Triple(SuccessContainer, Success, Success.copy(alpha = 0.35f))
    StatusTone.WARNING -> Triple(WarningContainer, Warning, Warning.copy(alpha = 0.35f))
    StatusTone.ERROR -> Triple(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.error.copy(alpha = 0.35f))
    StatusTone.NEUTRAL -> Triple(MaterialTheme.colorScheme.surfaceContainerHigh, MaterialTheme.colorScheme.onSurfaceVariant, MaterialTheme.colorScheme.outlineVariant)
    StatusTone.BRAND -> Triple(FitLimeSoft, FitDark, FitLime)
  }
  Surface(
    color = bg,
    contentColor = fg,
    shape = RoundedCornerShape(999.dp),
    border = BorderStroke(1.dp, border)
  ) {
    Text(
      text,
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
      style = MaterialTheme.typography.labelSmall,
      fontWeight = FontWeight.Black,
      letterSpacing = 0.4.sp
    )
  }
}

enum class StatusTone { SUCCESS, WARNING, ERROR, NEUTRAL, BRAND }

@Composable
fun AvailabilityPill(isCancelled: Boolean, isFull: Boolean, spotsLeft: Int) {
  when {
    isCancelled -> StatusPill("Cancelled", StatusTone.ERROR)
    isFull -> StatusPill("FULL", StatusTone.ERROR)
    spotsLeft <= 3 -> StatusPill("$spotsLeft left", StatusTone.WARNING)
    else -> StatusPill("$spotsLeft open", StatusTone.SUCCESS)
  }
}

@Composable
fun CapacityBar(
  booked: Int,
  capacity: Int,
  modifier: Modifier = Modifier,
) {
  val frac = if (capacity == 0) 0f else (booked.toFloat() / capacity.toFloat()).coerceIn(0f, 1f)
  val animated by animateFloatAsState(frac, label = "capacity")
  val isFull = booked >= capacity
  val almostFull = !isFull && (capacity - booked) <= 3
  val barColor = when {
    isFull -> MaterialTheme.colorScheme.error
    almostFull -> Warning
    else -> MaterialTheme.colorScheme.primary
  }
  Column(modifier) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
      Text(
        "$booked/$capacity booked",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Text(
        if (isFull) "FULL" else "${capacity - booked} spots left",
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = barColor,
      )
    }
    Spacer(Modifier.height(6.dp))
    LinearProgressIndicator(
      progress = { animated },
      modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(99.dp)),
      color = barColor,
      trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
    )
  }
}

@Composable
fun ClassCard(
  gymClass: GymClass,
  trailing: @Composable () -> Unit = {},
) {
  Card(
    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp, pressedElevation = 3.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
  ) {
    Column(Modifier.padding(16.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        CategoryPill(gymClass.category.label)
        Spacer(Modifier.width(8.dp))
        Icon(
          Icons.Filled.Schedule, null,
          modifier = Modifier.size(14.dp),
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.width(4.dp))
        Text(
          "${gymClass.timeLabel}–${gymClass.endLabel}",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.weight(1f))
        AvailabilityPill(gymClass.isCancelled, gymClass.bookedCount >= gymClass.capacity, gymClass.spotsLeft)
      }
      Spacer(Modifier.height(10.dp))
      Text(
        gymClass.title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
      )
      Spacer(Modifier.height(4.dp))
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Filled.Person, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(4.dp))
        Text(
          "Coach ${gymClass.trainerName}",
          style = MaterialTheme.typography.bodySmall,
          fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.width(6.dp))
        Icon(Icons.Filled.LocationOn, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(2.dp))
        Text(gymClass.station, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
      if (gymClass.trainerBio.isNotBlank()) {
        Spacer(Modifier.height(2.dp))
        Text(
          gymClass.trainerBio,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
        )
      }
      Spacer(Modifier.height(12.dp))
      CapacityBar(gymClass.bookedCount, gymClass.capacity)
      Spacer(Modifier.height(6.dp))
      trailing()
    }
  }
}

// ── Gradient hero card (membership / highlights) ──
@Composable
fun HeroCard(
  modifier: Modifier = Modifier,
  content: @Composable ColumnScope.() -> Unit,
) {
  val brush = Brush.linearGradient(
    colors = listOf(Color(0xFF0C2417), Color(0xFF133824), Color(0xFF1B4E32)),
  )
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(26.dp),
    colors = CardDefaults.cardColors(containerColor = FitDeepGreen),
    border = BorderStroke(1.dp, FitLime.copy(alpha = 0.25f)),
    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
  ) {
    Box(
      Modifier.background(brush).padding(20.dp),
    ) {
      // Lime glow dot
      Box(
        Modifier.align(Alignment.TopEnd)
          .size(130.dp)
          .background(Brush.radialGradient(listOf(FitLime.copy(alpha = 0.28f), Color.Transparent)), CircleShape),
      )
      Column(content = content)
    }
  }
}

@Composable
fun HeroLabel(text: String) {
  Surface(color = FitLime, contentColor = FitDark, shape = RoundedCornerShape(999.dp)) {
    Text(
      text.uppercase(),
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
      style = MaterialTheme.typography.labelSmall,
      fontWeight = FontWeight.ExtraBold,
    )
  }
}

@Composable
fun FitPrimaryButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  content: @Composable RowScope.() -> Unit,
) {
  Button(
    onClick = onClick,
    modifier = modifier.heightIn(min = 48.dp),
    enabled = enabled,
    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 6.dp),
    shape = RoundedCornerShape(16.dp),
    colors = ButtonDefaults.buttonColors(
      containerColor = FitLime,
      contentColor = FitDark,
      disabledContainerColor = FitLime.copy(alpha = 0.35f),
      disabledContentColor = FitDark.copy(alpha = 0.5f),
    ),
    content = content,
  )
}

// ── State helpers ──
@Composable
fun LoadingRow(label: String = "Loading…") {
  Row(
    Modifier.fillMaxWidth().padding(20.dp),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
    Spacer(Modifier.width(10.dp))
    Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
  }
}

@Composable
fun EmptyStateCard(
  title: String,
  subtitle: String,
  icon: ImageVector = Icons.Filled.Info,
  actionLabel: String? = null,
  onAction: (() -> Unit)? = null,
) {
  Card(
    Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
  ) {
    Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
      Surface(color = MaterialTheme.colorScheme.surfaceContainerHighest, shape = CircleShape) {
        Icon(icon, null, modifier = Modifier.padding(12.dp).size(24.dp))
      }
      Spacer(Modifier.height(10.dp))
      Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
      Spacer(Modifier.height(4.dp))
      Text(
        subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      if (actionLabel != null && onAction != null) {
        Spacer(Modifier.height(12.dp))
        FitPrimaryButton(onClick = onAction, modifier = Modifier.fillMaxWidth()) { Text(actionLabel) }
      }
    }
  }
}

@Composable
fun ErrorStateCard(
  title: String,
  message: String,
  onRetry: () -> Unit,
) {
  Card(
    Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
  ) {
    Column(Modifier.padding(18.dp)) {
      Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer)
      Spacer(Modifier.height(4.dp))
      Text(message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
      Spacer(Modifier.height(12.dp))
      Button(onClick = onRetry) { Text("Retry") }
    }
  }
}
