package com.example.fitpulsegym.di

import android.content.Context
import androidx.room.Room
import com.example.fitpulsegym.data.local.FitPulseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
  @Provides
  @Singleton
  fun provideDatabase(@ApplicationContext ctx: Context): FitPulseDatabase =
    Room.databaseBuilder(ctx, FitPulseDatabase::class.java, "fitpulse.db")
      .fallbackToDestructiveMigration(true)
      .build()

  @Provides fun provideClassDao(db: FitPulseDatabase) = db.gymClassDao()
  @Provides fun provideBookingDao(db: FitPulseDatabase) = db.bookingDao()
  @Provides fun provideLogDao(db: FitPulseDatabase) = db.workoutLogDao()
  @Provides fun provideAlertDao(db: FitPulseDatabase) = db.alertDao()
  @Provides fun provideMemberDao(db: FitPulseDatabase) = db.memberDao()
  @Provides fun provideTransactionDao(db: FitPulseDatabase) = db.transactionDao()
  @Provides fun providePlanDao(db: FitPulseDatabase) = db.planDao()
  @Provides fun provideGymConfigDao(db: FitPulseDatabase) = db.gymConfigDao()
  @Provides fun provideTrainerDao(db: FitPulseDatabase) = db.trainerDao()
  @Provides fun provideEquipmentDao(db: FitPulseDatabase) = db.equipmentDao()
  @Provides fun provideExpenseDao(db: FitPulseDatabase) = db.expenseDao()
  @Provides fun provideLockerDao(db: FitPulseDatabase) = db.lockerDao()
  @Provides fun provideLeadDao(db: FitPulseDatabase) = db.leadDao()

  // Clean Architecture bindings: domain interfaces → Room/Retrofit impls
  @Provides @Singleton
  fun bindSchedule(repo: com.example.fitpulsegym.data.repository.RoomScheduleRepository): com.example.fitpulsegym.domain.repository.ScheduleRepository = repo
  @Provides @Singleton
  fun bindBooking(repo: com.example.fitpulsegym.data.repository.RoomBookingRepository): com.example.fitpulsegym.domain.repository.BookingRepository = repo
  @Provides @Singleton
  fun bindMembership(repo: com.example.fitpulsegym.data.repository.RoomMembershipRepository): com.example.fitpulsegym.domain.repository.MembershipRepository = repo
  @Provides @Singleton
  fun bindAuth(repo: com.example.fitpulsegym.data.repository.FakeAuthRepository): com.example.fitpulsegym.domain.repository.AuthRepository = repo
}
