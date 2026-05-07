package com.vitalo.markrun.ui.signin

import androidx.lifecycle.ViewModel
import com.vitalo.markrun.data.local.prefs.AppPreferences
import com.vitalo.markrun.data.remote.model.SignInModel
import com.vitalo.markrun.data.remote.model.SignInRewardType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*
import android.app.Activity
import com.vitalo.markrun.ad.AdManager
import com.vitalo.markrun.ad.Ads
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val coinManager: com.vitalo.markrun.service.CoinManager
) : ViewModel() {

    private val cycleDays = 28
    private val daysPerWeek = 7
    private val totalWeeks = 4

    private val _signInData = MutableStateFlow<List<SignInModel>>(emptyList())
    val signInData: StateFlow<List<SignInModel>> = _signInData

    private val _currentDay = MutableStateFlow(1)
    val currentDay: StateFlow<Int> = _currentDay

    private val _remainingDays = MutableStateFlow(28)
    val remainingDays: StateFlow<Int> = _remainingDays

    private val _currentWeekIndex = MutableStateFlow(0)
    val currentWeekIndex: StateFlow<Int> = _currentWeekIndex

    private val _claimedChests = MutableStateFlow<Set<Int>>(emptySet())
    val claimedChests: StateFlow<Set<Int>> = _claimedChests

    private var cycleStartDate: Long

    init {
        cycleStartDate = appPreferences.getLong("SignInCycleStartDate")
        if (cycleStartDate == 0L) {
            cycleStartDate = System.currentTimeMillis()
            appPreferences.setLong("SignInCycleStartDate", cycleStartDate)
        }
        initializeSignInData()
        updateCurrentStatus()
        updateClaimedChests()
    }

    private fun updateClaimedChests() {
        val claimed = mutableSetOf<Int>()
        if (appPreferences.getBoolean("SignInChestClaimed_2")) claimed.add(2)
        if (appPreferences.getBoolean("SignInChestClaimed_7")) claimed.add(7)
        if (appPreferences.getBoolean("SignInChestClaimed_15")) claimed.add(15)
        _claimedChests.value = claimed
    }

    private fun getDailySignCfg(): com.vitalo.markrun.common.ab.impl.SignRewardConfig? {
        if (appPreferences.getBoolean("SignInFirstRoundConfigSaved")) {
            val json = appPreferences.getString("SignInFirstRoundDailySignCfg")
            if (!json.isNullOrEmpty()) {
                return com.vitalo.markrun.util.MmkvUtils.gson.fromJson(json, com.vitalo.markrun.common.ab.impl.SignRewardConfig::class.java)
            }
        }
        return com.vitalo.markrun.common.ab.AbConfigDataRepo.getCurrentConfig(com.vitalo.markrun.common.ab.AbSidTable.SIGN_REWARD) as? com.vitalo.markrun.common.ab.impl.SignRewardConfig
    }
    
    private fun saveFirstRoundConfig() {
        val cfg = com.vitalo.markrun.common.ab.AbConfigDataRepo.getCurrentConfig(com.vitalo.markrun.common.ab.AbSidTable.SIGN_REWARD) as? com.vitalo.markrun.common.ab.impl.SignRewardConfig
        if (cfg != null) {
            appPreferences.setString("SignInFirstRoundDailySignCfg", com.vitalo.markrun.util.MmkvUtils.gson.toJson(cfg))
        }
        val withdrawCfg = com.vitalo.markrun.common.ab.AbConfigDataRepo.getCurrentConfig(com.vitalo.markrun.common.ab.AbSidTable.WITHDRAW_GRADE) as? com.vitalo.markrun.common.ab.impl.WithdrawGradeConfig
        if (withdrawCfg != null) {
            appPreferences.setString("FrozenWithdrawGradeConfig", com.vitalo.markrun.util.MmkvUtils.gson.toJson(withdrawCfg))
        }
        appPreferences.setBoolean("SignInFirstRoundConfigSaved", true)
    }

    private fun isFirstRoundSignIn(): Boolean {
        val cycleStart = appPreferences.getLong("SignInCycleStartDate")
        if (cycleStart == 0L) return true
        val today = Calendar.getInstance()
        val startCal = Calendar.getInstance().apply { timeInMillis = cycleStart }
        return daysBetween(startCal, today) < cycleDays
    }

    private fun initializeSignInData() {
        val isFirstRound = isFirstRoundSignIn()
        
        if (isFirstRound && !appPreferences.getBoolean("SignInFirstRoundConfigSaved")) {
            saveFirstRoundConfig()
        }
        
        val cfg = getDailySignCfg()
        val signCashDayStr = cfg?.signCashDay ?: ""
        val cashDays = signCashDayStr.split("[,，]".toRegex())
            .mapNotNull { it.trim().toIntOrNull() }
            .filter { it in 1..cycleDays }
            .take(3)
            
        val abConfig = com.vitalo.markrun.common.ab.AbConfigDataRepo.getCurrentConfig(com.vitalo.markrun.common.ab.AbSidTable.WITHDRAW_ENABLE) as? com.vitalo.markrun.common.ab.impl.WithdrawEnableConfig
        val isWithdrawEnabled = abConfig?.smallWithdrawEnable == "1" || abConfig?.largeWithdrawEnable == "1"

        val shouldProvideCashReward = isFirstRound && isWithdrawEnabled

        val data = (1..cycleDays).map { day ->
            val isCash = shouldProvideCashReward && cashDays.contains(day)
            SignInModel(
                day = day,
                rewardAmount = if (day == 7 || day == 14 || day == 21 || day == 28) {
                    cfg?.sign7Coin ?: 200
                } else {
                    cfg?.sign1Coin ?: 100
                },
                rewardType = if (isCash) SignInRewardType.CASH else SignInRewardType.COIN,
                cashAmount = if (isCash) 1.0 else null // Dummy cash amount
            )
        }.toMutableList()

        val signedInDays = loadSignedInDays()
        for (day in signedInDays) {
            val index = data.indexOfFirst { it.day == day }
            if (index >= 0) {
                data[index] = data[index].copy(isSignedIn = true)
            }
        }
        _signInData.value = data
    }

    fun updateCurrentStatus() {
        val today = Calendar.getInstance()
        val cycleStart = Calendar.getInstance().apply { timeInMillis = cycleStartDate }

        val daysSinceStart = daysBetween(cycleStart, today)

        if (daysSinceStart >= cycleDays) {
            val currentTotalDays = getTotalSignDays()
            appPreferences.setInt("TotalSignDays", appPreferences.getInt("TotalSignDays") + currentTotalDays)
            
            val cyclesPassed = daysSinceStart / cycleDays
            cycleStart.add(Calendar.DAY_OF_YEAR, cyclesPassed * cycleDays)
            cycleStartDate = cycleStart.timeInMillis
            appPreferences.setLong("SignInCycleStartDate", cycleStartDate)
            appPreferences.remove("SignedInDays")
            
            // Reset chest claims
            appPreferences.remove("SignInChestClaimed_2")
            appPreferences.remove("SignInChestClaimed_7")
            appPreferences.remove("SignInChestClaimed_15")
            updateClaimedChests()
            
            initializeSignInData()
            val newDays = daysBetween(cycleStart, today)
            _currentDay.value = minOf(newDays + 1, cycleDays)
        } else {
            _currentDay.value = minOf(daysSinceStart + 1, cycleDays)
        }

        _remainingDays.value = maxOf(0, cycleDays - _currentDay.value + 1)
        _currentWeekIndex.value = minOf((_currentDay.value - 1) / daysPerWeek, totalWeeks - 1)

        val updatedData = _signInData.value.map { model ->
            model.copy(
                isToday = model.day == _currentDay.value,
                isExpired = model.day < _currentDay.value && !model.isSignedIn
            )
        }
        _signInData.value = updatedData
    }

    fun getWeekData(weekIndex: Int): List<SignInModel> {
        if (weekIndex < 0 || weekIndex >= totalWeeks) return emptyList()
        val start = weekIndex * daysPerWeek
        val end = minOf(start + daysPerWeek, _signInData.value.size)
        return _signInData.value.subList(start, end)
    }

    fun signIn(activity: Activity, day: Int? = null) {
        val targetDay = day ?: _currentDay.value
        if (targetDay < 1 || targetDay > cycleDays) return

        val isToday = targetDay == _currentDay.value
        val isExpired = targetDay < _currentDay.value
        if (!isToday && !isExpired) return

        // Verify the day is not already signed in before launching ad
        val currentData = _signInData.value
        val index = currentData.indexOfFirst { it.day == targetDay }
        if (index < 0 || currentData[index].isSignedIn) return

        android.util.Log.d("SignInViewModel", "signIn: calling AdManager.showAd for day=$targetDay")

        AdManager.showAd(
            activity = activity,
            virtualId = Ads.REWARD_ACTIVITY_SIGN_IN,
            onComplete = {
                android.util.Log.d("SignInViewModel", "onComplete triggered for day=$targetDay")
                performSignIn(targetDay)
            }
        )
    }

    private fun performSignIn(targetDay: Int) {
        android.util.Log.d("SignInViewModel", "performSignIn: day=$targetDay")
        val data = _signInData.value.toMutableList()
        val index = data.indexOfFirst { it.day == targetDay }
        if (index < 0 || data[index].isSignedIn) {
            android.util.Log.w("SignInViewModel", "performSignIn: day already signed or not found")
            return
        }
        data[index] = data[index].copy(
            isSignedIn = true,
            signInDate = System.currentTimeMillis()
        )
        _signInData.value = data
        saveSignedInData()

        android.util.Log.d("SignInViewModel", "performSignIn: rewardType=${data[index].rewardType}, amount=${data[index].rewardAmount}")
        if (data[index].rewardType == SignInRewardType.COIN) {
            coinManager.addCoin(data[index].rewardAmount)
            com.vitalo.markrun.ui.common.GlobalOverlayManager.showCoinArrivedOverlay(data[index].rewardAmount)
        }

        updateCurrentStatus()
    }

    fun getTotalSignDays(): Int = _signInData.value.count { it.isSignedIn }

    fun isChestClaimed(requiredDays: Int): Boolean {
        return _claimedChests.value.contains(requiredDays)
    }

    fun claimChest(activity: Activity, requiredDays: Int) {
        val accumulateDays = getTotalSignDays()
        if (!isChestClaimed(requiredDays) && accumulateDays >= requiredDays) {
            AdManager.showAd(
                activity = activity,
                virtualId = Ads.REWARD_ACTIVITY_SIGN_IN,
                onComplete = {
                    performClaimChest(requiredDays)
                }
            )
        }
    }
    
    private fun performClaimChest(requiredDays: Int) {
        appPreferences.setBoolean("SignInChestClaimed_$requiredDays", true)
        updateClaimedChests()
        val cfg = getDailySignCfg()
        val streakCoin = cfg?.signStreakCoin ?: 100
        coinManager.addCoin(streakCoin)
        com.vitalo.markrun.ui.common.GlobalOverlayManager.showCoinArrivedOverlay(streakCoin)
        updateCurrentStatus()
    }

    private fun loadSignedInDays(): List<Int> {
        val json = appPreferences.getString("SignedInDays") ?: return emptyList()
        return try {
            json.removeSurrounding("[", "]").split(",")
                .mapNotNull { it.trim().toIntOrNull() }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun saveSignedInData() {
        val days = _signInData.value.filter { it.isSignedIn }.map { it.day }
        appPreferences.setString("SignedInDays", days.toString())
        appPreferences.setLong("SignInCycleStartDate", cycleStartDate)
    }

    private fun daysBetween(start: Calendar, end: Calendar): Int {
        val startDay = Calendar.getInstance().apply {
            set(start.get(Calendar.YEAR), start.get(Calendar.MONTH), start.get(Calendar.DAY_OF_MONTH), 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val endDay = Calendar.getInstance().apply {
            set(end.get(Calendar.YEAR), end.get(Calendar.MONTH), end.get(Calendar.DAY_OF_MONTH), 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val diffMs = endDay.timeInMillis - startDay.timeInMillis
        return (diffMs / (24 * 60 * 60 * 1000)).toInt()
    }
}
