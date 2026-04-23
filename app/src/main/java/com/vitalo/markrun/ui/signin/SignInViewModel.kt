package com.vitalo.markrun.ui.signin

import androidx.lifecycle.ViewModel
import com.vitalo.markrun.data.local.prefs.AppPreferences
import com.vitalo.markrun.data.remote.model.SignInModel
import com.vitalo.markrun.data.remote.model.SignInRewardType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val appPreferences: AppPreferences
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

    private var cycleStartDate: Long

    init {
        cycleStartDate = appPreferences.getLong("SignInCycleStartDate")
        if (cycleStartDate == 0L) {
            cycleStartDate = System.currentTimeMillis()
            appPreferences.setLong("SignInCycleStartDate", cycleStartDate)
        }
        initializeSignInData()
        updateCurrentStatus()
    }

    private fun initializeSignInData() {
        val cashDays = listOf(7, 14, 21, 28) // Example cash reward days
        val data = (1..cycleDays).map { day ->
            val isCash = cashDays.contains(day)
            SignInModel(
                day = day,
                rewardAmount = if (day % 7 == 0) 300 else 100,
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

    fun signIn(day: Int? = null) {
        val targetDay = day ?: _currentDay.value
        if (targetDay < 1 || targetDay > cycleDays) return

        val isToday = targetDay == _currentDay.value
        val isExpired = targetDay < _currentDay.value
        if (!isToday && !isExpired) return

        val data = _signInData.value.toMutableList()
        val index = data.indexOfFirst { it.day == targetDay }
        if (index < 0 || data[index].isSignedIn) return

        // TODO: 接入广告 SDK 后在此处展示广告
        // AdManager.showAd(onReward = { success ->
        //     if (success) {
        //         performSignIn(targetDay, index, data)
        //     }
        // })
        
        // 目前暂时代替为直接签到成功
        performSignIn(index, data)
    }

    private fun performSignIn(index: Int, data: MutableList<SignInModel>) {
        data[index] = data[index].copy(
            isSignedIn = true,
            signInDate = System.currentTimeMillis()
        )
        _signInData.value = data
        saveSignedInData()

        if (data[index].rewardType == SignInRewardType.COIN) {
            addLocalCoin(data[index].rewardAmount)
        }
        
        updateCurrentStatus()
    }

    fun getTotalSignDays(): Int = _signInData.value.count { it.isSignedIn }

    fun isChestClaimed(requiredDays: Int): Boolean {
        return appPreferences.getBoolean("SignInChestClaimed_$requiredDays")
    }

    fun claimChest(requiredDays: Int) {
        val accumulateDays = getTotalSignDays()
        if (!isChestClaimed(requiredDays) && accumulateDays >= requiredDays) {
            // TODO: 接入广告 SDK 后在此处展示广告
            // AdManager.showAd(onReward = { success ->
            //     if (success) {
            //         performClaimChest(requiredDays)
            //     }
            // })
            
            // 目前暂时代替为直接领取成功
            performClaimChest(requiredDays)
        }
    }
    
    private fun performClaimChest(requiredDays: Int) {
        appPreferences.setBoolean("SignInChestClaimed_$requiredDays", true)
        addLocalCoin(100) // Default chest reward
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

    private fun addLocalCoin(amount: Int) {
        val current = appPreferences.getInt("local_coin_balance")
        appPreferences.setInt("local_coin_balance", current + amount)
    }
}
