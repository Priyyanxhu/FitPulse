package com.example.fitpulsegym.data.seed

import com.example.fitpulsegym.data.local.GymClassEntity
import com.example.fitpulsegym.data.local.MemberProfileEntity
import com.example.fitpulsegym.data.local.WorkoutLogEntity
import com.example.fitpulsegym.data.model.MembershipPlan

object SeedData {
  fun classes(): List<GymClassEntity> = listOf(
    GymClassEntity("c-hiit-mon", "Inferno HIIT 45", "HIIT", "Rohan Mehta", "CrossFit L2 • 6 yrs • fat-loss specialist", 0, 6, 30, 45, 20, 14, "Turf A", false),
    GymClassEntity("c-yoga-mon", "Sunrise Vinyasa", "YOGA", "Ananya Iyer", "RYT-500 • mobility & breathwork", 0, 7, 30, 60, 25, 18, "Studio 1", false),
    GymClassEntity("c-spin-mon", "Rhythm Ride", "SPIN", "Kabir Singh", "Spin cert • 4 yrs • endurance", 0, 18, 0, 45, 18, 18, "Spin Room", false),
    GymClassEntity("c-str-tue", "Iron Foundations", "STRENGTH", "Vikram Rao", "Powerlifting coach • 8 yrs", 1, 7, 0, 60, 16, 9, "Rack 1-4", false),
    GymClassEntity("c-cross-tue", "WOD: Engine Room", "CROSSFIT", "Rohan Mehta", "CrossFit L2 • 6 yrs", 1, 18, 30, 60, 20, 20, "Box Floor", false),
    GymClassEntity("c-yoga-wed", "Core & Calm Pilates", "PILATES", "Ananya Iyer", "RYT-500 • mobility", 2, 8, 0, 50, 22, 11, "Studio 1", false),
    GymClassEntity("c-box-wed", "Boxing Fundamentals", "BOXING", "Arjun Nair", "State champ • footwork & pads", 2, 19, 0, 60, 14, 6, "Ring Side", false),
    GymClassEntity("c-hiit-thu", "Metcon Madness", "HIIT", "Sara Khan", "HIIT • 5 yrs • conditioning", 3, 6, 30, 45, 20, 17, "Turf A", false),
    GymClassEntity("c-spin-thu", "Climb & Sprint", "SPIN", "Kabir Singh", "Spin cert • endurance", 3, 18, 0, 45, 18, 12, "Spin Room", false),
    GymClassEntity("c-yoga-fri", "Power Yoga Flow", "YOGA", "Ananya Iyer", "RYT-500", 4, 7, 0, 60, 25, 22, "Studio 1", false),
    GymClassEntity("c-cross-fri", "Friday Throwdown", "CROSSFIT", "Rohan Mehta", "CrossFit L2", 4, 18, 30, 60, 24, 24, "Box Floor", false),
    GymClassEntity("c-dance-sat", "Zumba Party", "DANCE", "Sara Khan", "Zumba licensed • high energy", 5, 9, 0, 50, 30, 21, "Studio 2", false),
    GymClassEntity("c-str-sat", "Squat Clinic", "STRENGTH", "Vikram Rao", "Powerlifting coach", 5, 10, 30, 75, 12, 5, "Rack 1-4", false),
    GymClassEntity("c-yoga-sun", "Slow Sunday Reset", "YOGA", "Ananya Iyer", "Restorative • all levels", 6, 9, 30, 60, 25, 10, "Studio 1", false),
    GymClassEntity("c-spin-sun", "Endurance Ride 60", "SPIN", "Kabir Singh", "Aerobic base builder", 6, 11, 0, 60, 18, 7, "Spin Room", false)
  )

  fun member(): MemberProfileEntity = MemberProfileEntity(
    memberId = "member-001",
    name = "Aarav Sharma",
    email = "aarav@example.com",
    phone = "+91 98765 43210",
    planName = "Pro Monthly",
    expiryEpochMillis = System.currentTimeMillis() + 18L * 24 * 60 * 60 * 1000,
    qrToken = "FITPULSE-member-001-PRO",
    weightKg = 74.0f,
    heightCm = 178.0f,
    fitnessGoal = "Strength & Lean Muscle",
    waterIntakeMl = 1750,
    waterGoalMl = 3000,
    lockerNumber = "L-42"
  )

  fun transactions(): List<com.example.fitpulsegym.data.local.PaymentTransactionEntity> = listOf(
    com.example.fitpulsegym.data.local.PaymentTransactionEntity("tx-01", "Pro Monthly", 2499, "UPI (Google Pay)", System.currentTimeMillis() - 12L * 86400000, "INV-2026-9021"),
    com.example.fitpulsegym.data.local.PaymentTransactionEntity("tx-02", "Pro Monthly", 2499, "Visa Debit Card", System.currentTimeMillis() - 42L * 86400000, "INV-2026-7844")
  )

  fun logs(): List<WorkoutLogEntity> {
    val now = System.currentTimeMillis()
    val d = 24L * 60 * 60 * 1000
    return listOf(
      WorkoutLogEntity("l1", "Inferno HIIT 45", now - 1 * d, 520, "PB on burpee ladder!", true),
      WorkoutLogEntity("l2", "Sunrise Vinyasa", now - 3 * d, 220, "Held crow pose 20s", false),
      WorkoutLogEntity("l3", "Rhythm Ride", now - 5 * d, 480, "Avg 210W, felt strong", false),
      WorkoutLogEntity("l4", "Iron Foundations", now - 8 * d, 350, "Squat 100kg x 5", true)
    )
  }

  fun plans(): List<MembershipPlan> = listOf(
    MembershipPlan("p1", "Day Pass", 299, 1, listOf("1 class credit", "Locker access", "No commitment")),
    MembershipPlan("p2", "Pro Monthly", 2499, 30, listOf("Unlimited classes", "1 PT session", "Sauna access")),
    MembershipPlan("p3", "Pro Quarterly", 6499, 90, listOf("Unlimited classes", "3 PT sessions", "Nutrition plan", "Guest passes x2")),
    MembershipPlan("p4", "Elite Annual", 21999, 365, listOf("Everything in Quarterly", "Monthly body scan", "Merch kit", "Priority waitlist"))
  )

  fun planEntities(): List<com.example.fitpulsegym.data.local.MembershipPlanEntity> = listOf(
    com.example.fitpulsegym.data.local.MembershipPlanEntity("p1", "Day Pass", 299, 1, "1 class credit,Locker access,No commitment", false),
    com.example.fitpulsegym.data.local.MembershipPlanEntity("p2", "Pro Monthly", 2499, 30, "Unlimited classes,1 PT session,Sauna access", true),
    com.example.fitpulsegym.data.local.MembershipPlanEntity("p3", "Pro Quarterly", 6499, 90, "Unlimited classes,3 PT sessions,Nutrition plan,Guest passes x2", true),
    com.example.fitpulsegym.data.local.MembershipPlanEntity("p4", "Elite Annual", 21999, 365, "Everything in Quarterly,Monthly body scan,Merch kit,Priority waitlist", false)
  )

  fun gymConfig(): com.example.fitpulsegym.data.local.GymConfigEntity =
    com.example.fitpulsegym.data.local.GymConfigEntity(
      id = "default_gym",
      gymName = "FitPulse Gym & Boutique Studio",
      address = "42 Silicon Avenue, 2nd Floor, Indiranagar, Bangalore",
      phone = "+91 80 4123 4567",
      announcement = "⚡ Studio Notice: Hydration bar restocked & new CrossFit rogue rigs installed on Turf A!",
      wifiSsid = "FitPulse-HighSpeed",
      wifiPass = "PulseFit2026",
      openHours = "05:30 AM - 10:30 PM"
    )

  fun trainers(): List<com.example.fitpulsegym.data.local.TrainerEntity> = listOf(
    com.example.fitpulsegym.data.local.TrainerEntity("t1", "Rohan Mehta", "CrossFit & Conditioning", "CrossFit L2 • 6 yrs exp • fat-loss specialist", "+91 98765 11001"),
    com.example.fitpulsegym.data.local.TrainerEntity("t2", "Ananya Iyer", "Vinyasa & Mobility", "RYT-500 • mobility, core & breathwork", "+91 98765 11002"),
    com.example.fitpulsegym.data.local.TrainerEntity("t3", "Vikram Rao", "Powerlifting & Strength", "National powerlifter • 8 yrs coaching", "+91 98765 11003"),
    com.example.fitpulsegym.data.local.TrainerEntity("t4", "Kabir Singh", "Indoor Spin & Endurance", "Certified Spin instructor • 5 yrs exp", "+91 98765 11004"),
    com.example.fitpulsegym.data.local.TrainerEntity("t5", "Sara Khan", "Zumba & HIIT", "Licensed Zumba & HIIT specialist", "+91 98765 11005"),
    com.example.fitpulsegym.data.local.TrainerEntity("t6", "Arjun Nair", "Boxing & Combat", "State Boxing Champion • footwork & mitts", "+91 98765 11006")
  )

  fun equipments(): List<com.example.fitpulsegym.data.local.EquipmentEntity> = listOf(
    com.example.fitpulsegym.data.local.EquipmentEntity("eq-01", "Matrix T70 Commercial Treadmill #1", "Cardio", "OPERATIONAL", "Cardio Deck", "14 Oct 2026", "Running belt tension & deck lubricated"),
    com.example.fitpulsegym.data.local.EquipmentEntity("eq-02", "Rogue Monster Lite Squat Rack #1", "Strength", "OPERATIONAL", "Free Weights Area", "01 Oct 2026", "J-cups, safety pins & upright bolts inspected"),
    com.example.fitpulsegym.data.local.EquipmentEntity("eq-03", "LifeFitness Dual Adjustable Pulley", "Machines", "SERVICE_DUE", "Main Floor", "10 Aug 2026", "Left pulley cable shows slight wear; service scheduled"),
    com.example.fitpulsegym.data.local.EquipmentEntity("eq-04", "Concept2 RowErg #1", "Cardio", "OPERATIONAL", "Studio A", "20 Sep 2026", "PM5 monitor updated; chain cleaned"),
    com.example.fitpulsegym.data.local.EquipmentEntity("eq-05", "Keiser M3i Indoor Spin Bike #08", "Cardio", "UNDER_REPAIR", "Spin Room", "18 Oct 2026", "Magnetic shifter lever loose; technician visiting Thursday"),
    com.example.fitpulsegym.data.local.EquipmentEntity("eq-06", "Eleiko Olympic Bumper Plate Set", "Free Weights", "OPERATIONAL", "Platform 1", "05 Oct 2026", "Standard barbell spin smooth; collars intact")
  )

  fun expenses(): List<com.example.fitpulsegym.data.local.GymExpenseEntity> {
    val now = System.currentTimeMillis()
    val d = 86400000L
    return listOf(
      com.example.fitpulsegym.data.local.GymExpenseEntity("exp-01", "Studio Floor Rent (Commercial)", "Rent", 65000, now - 8 * d, "Commercial Realty Partners"),
      com.example.fitpulsegym.data.local.GymExpenseEntity("exp-02", "BESCOM Power & High-Capacity AC", "Utilities", 18400, now - 12 * d, "State Electricity Board"),
      com.example.fitpulsegym.data.local.GymExpenseEntity("exp-03", "Coach & Staff Payroll (6 Trainers)", "Salaries", 42000, now - 22 * d, "FitPulse Trainer Staff"),
      com.example.fitpulsegym.data.local.GymExpenseEntity("exp-04", "Rogue & LifeFitness Routine Servicing", "Maintenance", 6500, now - 11 * d, "ProFit Technical Services"),
      com.example.fitpulsegym.data.local.GymExpenseEntity("exp-05", "Hygiene Supplies, Towels & Hydration", "Supplies", 4200, now - 5 * d, "CleanPure Hygiene Supplies")
    )
  }

  fun lockers(): List<com.example.fitpulsegym.data.local.GymLockerEntity> = listOf(
    com.example.fitpulsegym.data.local.GymLockerEntity("L-01", "OCCUPIED", "Aarav Sharma", "+91 98765 43210", "Men's Locker Bay"),
    com.example.fitpulsegym.data.local.GymLockerEntity("L-02", "AVAILABLE", null, null, "Men's Locker Bay"),
    com.example.fitpulsegym.data.local.GymLockerEntity("L-03", "OCCUPIED", "Priya Patel", "+91 98765 22334", "Women's Locker Bay"),
    com.example.fitpulsegym.data.local.GymLockerEntity("L-04", "MAINTENANCE", null, null, "Men's Locker Bay"),
    com.example.fitpulsegym.data.local.GymLockerEntity("L-05", "AVAILABLE", null, null, "VIP Suite Bay"),
    com.example.fitpulsegym.data.local.GymLockerEntity("L-06", "OCCUPIED", "Rohan Mehta", "+91 98765 11001", "VIP Suite Bay"),
    com.example.fitpulsegym.data.local.GymLockerEntity("L-07", "AVAILABLE", null, null, "Women's Locker Bay"),
    com.example.fitpulsegym.data.local.GymLockerEntity("L-08", "AVAILABLE", null, null, "Men's Locker Bay"),
    com.example.fitpulsegym.data.local.GymLockerEntity("L-09", "AVAILABLE", null, null, "Men's Locker Bay"),
    com.example.fitpulsegym.data.local.GymLockerEntity("L-10", "OCCUPIED", "Vikram Rao", "+91 98765 11003", "Men's Locker Bay"),
    com.example.fitpulsegym.data.local.GymLockerEntity("L-11", "AVAILABLE", null, null, "Women's Locker Bay"),
    com.example.fitpulsegym.data.local.GymLockerEntity("L-12", "AVAILABLE", null, null, "VIP Suite Bay")
  )

  fun leads(): List<com.example.fitpulsegym.data.local.LeadInquiryEntity> {
    val now = System.currentTimeMillis()
    val d = 86400000L
    return listOf(
      com.example.fitpulsegym.data.local.LeadInquiryEntity("lead-01", "Rajesh Khanna", "+91 98450 12345", "Muscle Building", "TRIAL_SCHEDULED", "Tomorrow 7:00 AM", "Interested in 1-on-1 powerlifting with Vikram", now - 1 * d),
      com.example.fitpulsegym.data.local.LeadInquiryEntity("lead-02", "Sneha Reddy", "+91 99800 67890", "Weight Loss", "NEW_INQUIRY", "Saturday 6:00 PM", "Walked in asking about evening Spin & HIIT slots", now - 2 * d),
      com.example.fitpulsegym.data.local.LeadInquiryEntity("lead-03", "Vikramaditya Bose", "+91 98111 23456", "General Fitness", "CONVERTED", "Completed yesterday", "Joined Pro Monthly via UPI QR. Onboarded!", now - 4 * d),
      com.example.fitpulsegym.data.local.LeadInquiryEntity("lead-04", "Divya Sundaram", "+91 97400 55443", "Mobility & Yoga", "TRIAL_COMPLETED", "Sunday 9:30 AM", "Attended Sunrise Vinyasa; deciding between quarterly vs monthly", now - 3 * d)
    )
  }
}

