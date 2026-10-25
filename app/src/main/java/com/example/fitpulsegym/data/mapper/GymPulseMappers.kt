package com.example.fitpulsegym.data.mapper

import com.example.fitpulsegym.data.local.BookingEntity
import com.example.fitpulsegym.data.local.GymClassEntity
import com.example.fitpulsegym.data.local.MemberProfileEntity
import com.example.fitpulsegym.data.remote.BookingDto
import com.example.fitpulsegym.data.remote.GymClassDto
import com.example.fitpulsegym.data.remote.MembershipDto
import com.example.fitpulsegym.domain.model.Booking
import com.example.fitpulsegym.domain.model.BookingStatus
import com.example.fitpulsegym.domain.model.GymClass
import com.example.fitpulsegym.domain.model.Membership
import com.example.fitpulsegym.domain.model.SlotCategory
import com.example.fitpulsegym.domain.model.User

fun GymClassDto.toDomain() = GymClass(
  id = id, title = title,
  category = runCatching { SlotCategory.valueOf(category.uppercase()) }.getOrDefault(SlotCategory.CROSSFIT),
  trainerId = trainerId, trainerName = trainerName, dayIndex = dayIndex,
  startHour = startHour, startMinute = startMinute, durationMin = durationMin,
  capacity = capacity, bookedCount = bookedCount, station = station, isCancelled = isCancelled
)

fun GymClass.toDto() = GymClassDto(id, title, category.name, trainerId, trainerName, dayIndex, startHour, startMinute, durationMin, capacity, bookedCount, station, isCancelled)

fun GymClass.toEntity() = GymClassEntity(id, title, category.name, trainerName, "", dayIndex, startHour, startMinute, durationMin, capacity, bookedCount, station, isCancelled)

fun GymClassEntity.toDomain(trainerId: String = "") = GymClass(
  id, title,
  runCatching { SlotCategory.valueOf(category.uppercase()) }.getOrDefault(SlotCategory.CROSSFIT),
  trainerId, trainerName, dayIndex, startHour, startMinute, durationMin, capacity, bookedCount, station, isCancelled
)

fun BookingDto.toDomain() = Booking(id, classId, userId, runCatching { BookingStatus.valueOf(status) }.getOrDefault(BookingStatus.CONFIRMED), qrToken, createdAt)

fun BookingEntity.toDomain(userId: String = "member-001") = Booking(id, classId, userId, runCatching { BookingStatus.valueOf(status) }.getOrDefault(BookingStatus.CONFIRMED), qrToken, createdAt)

fun MembershipDto.toDomain() = Membership(id, userId, planName, startMillis, expiryMillis, isActive)

fun MemberProfileEntity.toDomainUser() = User(memberId, name, email, phone, qrToken)

fun MemberProfileEntity.toDomainMembership() = Membership("m-$memberId", memberId, planName, System.currentTimeMillis(), expiryEpochMillis, true)
