package com.example.fitpulsegym.data.model

enum class MuscleGroup(val label: String, val iconEmoji: String) {
  CHEST("Chest", "🏋️"),
  BACK("Back", "🚣"),
  LEGS("Legs", "🦵"),
  SHOULDERS("Shoulders", "💪"),
  ARMS("Arms", "🦾"),
  CORE("Core & Abs", "🧘"),
  CARDIO("Cardio", "⚡")
}

data class Exercise(
  val id: String,
  val name: String,
  val muscleGroup: MuscleGroup,
  val equipment: String,
  val difficulty: String,
  val targetMuscles: String,
  val instructions: String,
  val proTip: String,
  val burnKcalPer10Min: Int
)

object ExerciseLibrary {
  val exercises: List<Exercise> = listOf(
    // Chest
    Exercise(
      id = "ex-bench",
      name = "Barbell Flat Bench Press",
      muscleGroup = MuscleGroup.CHEST,
      equipment = "Barbell & Bench",
      difficulty = "Intermediate",
      targetMuscles = "Pectoralis Major, Anterior Deltoids, Triceps",
      instructions = "Lie flat with eyes under the bar. Retract scapulae, grip slightly wider than shoulder width. Lower bar smoothly to mid-chest, pause briefly, then press explosively upward.",
      proTip = "Keep feet firmly planted on the floor and maintain a natural arch in your lower back.",
      burnKcalPer10Min = 85
    ),
    Exercise(
      id = "ex-incline-db",
      name = "Incline Dumbbell Press",
      muscleGroup = MuscleGroup.CHEST,
      equipment = "Dumbbells & 30° Bench",
      difficulty = "Beginner",
      targetMuscles = "Clavicular (Upper) Pectorals, Triceps",
      instructions = "Set bench to 30 degrees. Press dumbbells up without clanging at the top. Lower until elbows reach roughly 90 degrees with a deep chest stretch.",
      proTip = "Keep wrists stacked directly over your elbows throughout the entire movement.",
      burnKcalPer10Min = 75
    ),
    Exercise(
      id = "ex-cable-fly",
      name = "Standing Cable Chest Fly",
      muscleGroup = MuscleGroup.CHEST,
      equipment = "Cable Crossover",
      difficulty = "Intermediate",
      targetMuscles = "Inner Chest, Pectoral Stretch",
      instructions = "Stand centered with a slight forward split stance. Bring hands together in a wide hugging motion, squeezing the chest hard for 1 second at the peak.",
      proTip = "Maintain a slight bend in your elbows; avoid turning the motion into a press.",
      burnKcalPer10Min = 65
    ),

    // Back
    Exercise(
      id = "ex-deadlift",
      name = "Conventional Barbell Deadlift",
      muscleGroup = MuscleGroup.BACK,
      equipment = "Olympic Barbell & Bumper Plates",
      difficulty = "Advanced",
      targetMuscles = "Erector Spinae, Lats, Glutes, Hamstrings",
      instructions = "Stand with feet hip-width, bar over mid-foot. Hinge at hips to grip bar outside knees. Pull slack, brace core, and drive the floor away to stand tall.",
      proTip = "Think about pushing the ground down rather than pulling the bar up with your arms.",
      burnKcalPer10Min = 110
    ),
    Exercise(
      id = "ex-lat-pulldown",
      name = "Wide-Grip Lat Pulldown",
      muscleGroup = MuscleGroup.BACK,
      equipment = "Lat Pulldown Station",
      difficulty = "Beginner",
      targetMuscles = "Latissimus Dorsi, Teres Major, Biceps",
      instructions = "Secure thighs under pads. Take a comfortable wide overhand grip. Lean back roughly 10 degrees and pull the bar smoothly toward your upper collarbone.",
      proTip = "Lead with your elbows and squeeze your shoulder blades down and back.",
      burnKcalPer10Min = 70
    ),
    Exercise(
      id = "ex-cable-row",
      name = "Seated Cable Row",
      muscleGroup = MuscleGroup.BACK,
      equipment = "Low Cable Pulley with V-Bar",
      difficulty = "Beginner",
      targetMuscles = "Rhomboids, Middle Traps, Lats",
      instructions = "Sit upright with knees slightly bent. Pull the handle toward your lower abdomen while keeping your chest proud and spine neutral.",
      proTip = "Do not round your lower back on the eccentric return; control the stretch.",
      burnKcalPer10Min = 75
    ),

    // Legs
    Exercise(
      id = "ex-squat",
      name = "Barbell Back Squat",
      muscleGroup = MuscleGroup.LEGS,
      equipment = "Squat Rack & Barbell",
      difficulty = "Advanced",
      targetMuscles = "Quadriceps, Gluteus Maximus, Adductors, Core",
      instructions = "Rest bar on upper traps. Unrack, step back, and set feet shoulder-width. Inhale, brace, and descend until hips are parallel or below knee crease.",
      proTip = "Drive knees out in the direction of your toes throughout the descent and ascent.",
      burnKcalPer10Min = 100
    ),
    Exercise(
      id = "ex-rdl",
      name = "Romanian Deadlift (RDL)",
      muscleGroup = MuscleGroup.LEGS,
      equipment = "Barbell or Heavy Dumbbells",
      difficulty = "Intermediate",
      targetMuscles = "Hamstrings, Glute-Ham Tie-in, Lower Back",
      instructions = "Hold bar at hip level. Push your hips straight back with a soft knee bend while keeping the bar in contact with your shins until you feel a deep hamstring stretch.",
      proTip = "Stop the descent as soon as your hips stop moving backward to prevent lower back rounding.",
      burnKcalPer10Min = 85
    ),
    Exercise(
      id = "ex-bulgarian",
      name = "Bulgarian Split Squat",
      muscleGroup = MuscleGroup.LEGS,
      equipment = "Flat Bench & Dumbbells",
      difficulty = "Intermediate",
      targetMuscles = "Quads, Glutes, Hip Stabilizers",
      instructions = "Place top of back foot on bench behind you. Lower rear knee toward the floor until front thigh is parallel. Drive through the front heel to rise.",
      proTip = "Lean torso slightly forward at 15 degrees to shift maximum tension onto the working glute.",
      burnKcalPer10Min = 90
    ),

    // Shoulders
    Exercise(
      id = "ex-ohp",
      name = "Standing Overhead Barbell Press",
      muscleGroup = MuscleGroup.SHOULDERS,
      equipment = "Barbell",
      difficulty = "Intermediate",
      targetMuscles = "Anterior Deltoids, Lateral Delts, Triceps, Core",
      instructions = "Hold bar at shoulder collarbone height. Squeeze glutes and abs. Press bar vertically in a straight path, moving head back slightly, then through at lockout.",
      proTip = "Never arch backward excessively; lock your ribcage down to protect the lower spine.",
      burnKcalPer10Min = 80
    ),
    Exercise(
      id = "ex-lat-raise",
      name = "Dumbbell Lateral Raise",
      muscleGroup = MuscleGroup.SHOULDERS,
      equipment = "Light Dumbbells",
      difficulty = "Beginner",
      targetMuscles = "Lateral Deltoid (Shoulder Cap Width)",
      instructions = "Hold dumbbells at sides with slight forward torso tilt. Raise arms laterally in the scapular plane (30° forward) until parallel to the floor.",
      proTip = "Think about pushing the weights out to the walls rather than pulling them up.",
      burnKcalPer10Min = 60
    ),
    Exercise(
      id = "ex-face-pull",
      name = "Rope Face Pulls",
      muscleGroup = MuscleGroup.SHOULDERS,
      equipment = "Cable Machine with Rope",
      difficulty = "Beginner",
      targetMuscles = "Rear Deltoids, Infraspinatus, Rotator Cuff",
      instructions = "Set cable to face height. Grip rope ends with thumbs pointing back. Pull rope toward your nose while externally rotating hands backward.",
      proTip = "Crucial posture exercise. Pause 2 seconds at full contraction on every single rep.",
      burnKcalPer10Min = 55
    ),

    // Arms
    Exercise(
      id = "ex-bicep-curl",
      name = "EZ-Bar Bicep Curl",
      muscleGroup = MuscleGroup.ARMS,
      equipment = "EZ-Curl Bar",
      difficulty = "Beginner",
      targetMuscles = "Biceps Brachii, Brachialis",
      instructions = "Grip inner angled serrations. Keep elbows pinned to sides. Curl upward until peak squeeze, then lower under full control for 3 seconds.",
      proTip = "Do not swing your hips or use momentum; strict form stimulates 2x more muscle fibers.",
      burnKcalPer10Min = 65
    ),
    Exercise(
      id = "ex-rope-pushdown",
      name = "Cable Tricep Rope Pushdown",
      muscleGroup = MuscleGroup.ARMS,
      equipment = "High Cable Pulley with Rope",
      difficulty = "Beginner",
      targetMuscles = "Triceps Lateral & Medial Heads",
      instructions = "Pin elbows beside ribs. Extend arms downward, flaring the rope ends apart at the bottom for an intense tricep contraction.",
      proTip = "Keep your shoulders retracted and still; only your forearms should move.",
      burnKcalPer10Min = 60
    ),

    // Core
    Exercise(
      id = "ex-hanging-raise",
      name = "Hanging Knee to Chest Raise",
      muscleGroup = MuscleGroup.CORE,
      equipment = "Pull-Up Bar or Captain's Chair",
      difficulty = "Intermediate",
      targetMuscles = "Rectus Abdominis, Hip Flexors, Deep Core",
      instructions = "Hang with active shoulders. Exhale and curl knees toward your chest, tilting pelvis backward to fully engage the lower abdominal wall.",
      proTip = "Don't just swing legs; round the lower spine slightly at top to engage abs rather than just hip flexors.",
      burnKcalPer10Min = 70
    ),
    Exercise(
      id = "ex-plank",
      name = "Forearm Active Plank",
      muscleGroup = MuscleGroup.CORE,
      equipment = "Yoga Mat",
      difficulty = "Beginner",
      targetMuscles = "Transverse Abdominis, Glutes, Scapular Stabilizers",
      instructions = "Rest on forearms and toes. Form a straight line from heels to ears. Pull belly button inward and squeeze glutes hard.",
      proTip = "Actively pull your elbows toward your toes to generate full-body isometric tension.",
      burnKcalPer10Min = 50
    ),

    // Cardio
    Exercise(
      id = "ex-hiit-burpee",
      name = "Plyometric Chest-to-Floor Burpee",
      muscleGroup = MuscleGroup.CARDIO,
      equipment = "Bodyweight",
      difficulty = "Intermediate",
      targetMuscles = "Full Body Aerobic & Anaerobic Power",
      instructions = "Drop hands to floor, kick feet back, lower chest to ground. Push up explosively, snap feet back beneath hips, and jump upward with hands overhead.",
      proTip = "Pace your breathing: exhale as you jump, inhale on the drop.",
      burnKcalPer10Min = 130
    ),
    Exercise(
      id = "ex-kettlebell-swing",
      name = "Russian Kettlebell Swing",
      muscleGroup = MuscleGroup.CARDIO,
      equipment = "Kettlebell (16kg - 24kg)",
      difficulty = "Intermediate",
      targetMuscles = "Posterior Chain, Glute Power, Cardiovascular Base",
      instructions = "Hinge back with bell between knees. Snap hips forward aggressively to float bell to chest height. Guide bell back into the hinge.",
      proTip = "The power comes 100% from your glute snap; your arms merely act as ropes holding the weight.",
      burnKcalPer10Min = 120
    )
  )
}
