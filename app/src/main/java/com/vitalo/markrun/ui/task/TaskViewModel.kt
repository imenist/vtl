package com.vitalo.markrun.ui.task

import androidx.lifecycle.ViewModel
import com.vitalo.markrun.data.local.prefs.AppPreferences
import com.vitalo.markrun.data.remote.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.*
import com.vitalo.markrun.service.CoinManager
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val coinManager: CoinManager
) : ViewModel() {

    private val _dailyTaskStatus = MutableStateFlow(DailyTaskStatus.empty())
    val dailyTaskStatus: StateFlow<DailyTaskStatus> = _dailyTaskStatus

    val coinBalance: StateFlow<Double> = coinManager.coinBalance

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val chestHours = listOf(8, 12, 16, 20)

    val chestReward = 50
    private val chestAllReward = 100
    private val runningReward = 200
    private val trainingReward = 150
    private val signInReward = 50
    private val spinReward = 100
    private val crackEggReward = 100
    private val luckySlotReward = 100

    init {
        loadAndPublishDailyTaskStatus()
    }

    fun loadAndPublishDailyTaskStatus() {
        _dailyTaskStatus.value = loadDailyTaskStatus()
    }

    fun allDailyTasks(): List<DailyTaskInfo> {
        val status = _dailyTaskStatus.value
        val tasks = mutableListOf<DailyTaskInfo>()

        tasks.add(DailyTaskInfo(DailyTaskKind.NOTIFICATION, canClaim = false, claimed = status.notificationClaimed, reward = 50))
        tasks.add(DailyTaskInfo(DailyTaskKind.MOTION_USAGE, canClaim = false, claimed = status.motionUsageClaimed, reward = 50))
        tasks.add(DailyTaskInfo(DailyTaskKind.SIGN_IN, canClaim = status.signInToday && !status.signInClaimed, claimed = status.signInClaimed, reward = signInReward))
        tasks.add(DailyTaskInfo(DailyTaskKind.CHEST_ALL, canClaim = status.chestClaimed.all { it } && !status.chestAllClaimed, claimed = status.chestAllClaimed, reward = chestAllReward))
        tasks.add(DailyTaskInfo(DailyTaskKind.NEW_USER_SPIN, canClaim = status.newUserSpinCount >= 10 && !status.newUserSpinClaimed, claimed = status.newUserSpinClaimed, reward = spinReward))
        tasks.add(DailyTaskInfo(DailyTaskKind.CRACK_EGG, canClaim = status.crackEggCount >= 10 && !status.crackEggClaimed, claimed = status.crackEggClaimed, reward = crackEggReward))
        tasks.add(DailyTaskInfo(DailyTaskKind.LUCKY_SLOT, canClaim = status.luckySlotCount >= 10 && !status.luckySlotClaimed, claimed = status.luckySlotClaimed, reward = luckySlotReward))
        tasks.add(DailyTaskInfo(DailyTaskKind.UPPER_STEP_CONVERSION, canClaim = false, claimed = status.upperStepConversionClaimed, reward = 200))
        tasks.add(DailyTaskInfo(DailyTaskKind.MULTI_DAILY_RELAXATION, canClaim = status.multiDailyRelaxationCompletedCount >= 3 && !status.multiDailyRelaxationRewardClaimed, claimed = status.multiDailyRelaxationRewardClaimed, reward = 120))
        tasks.add(DailyTaskInfo(DailyTaskKind.DAILY_RELAXATION, canClaim = status.dailyRelaxationCompleted && !status.dailyRelaxationRewardClaimed, claimed = status.dailyRelaxationRewardClaimed, reward = 100))
        tasks.add(DailyTaskInfo(DailyTaskKind.RUNNING, canClaim = status.runningKcal >= 200 && !status.runningRewardClaimed, claimed = status.runningRewardClaimed, reward = runningReward))
        tasks.add(DailyTaskInfo(DailyTaskKind.TRAINING, canClaim = status.trainingMinutes >= 30 && !status.trainingRewardClaimed, claimed = status.trainingRewardClaimed, reward = trainingReward))

        val targetKinds = listOf(
            DailyTaskKind.NOTIFICATION,
            DailyTaskKind.MOTION_USAGE,
            DailyTaskKind.SIGN_IN,
            DailyTaskKind.CHEST_ALL,
            DailyTaskKind.NEW_USER_SPIN,
            DailyTaskKind.CRACK_EGG,
            DailyTaskKind.LUCKY_SLOT,
            DailyTaskKind.UPPER_STEP_CONVERSION,
            DailyTaskKind.MULTI_DAILY_RELAXATION,
            DailyTaskKind.DAILY_RELAXATION,
            DailyTaskKind.RUNNING,
            DailyTaskKind.TRAINING
        )

        return tasks.sortedWith { lhs, rhs ->
            if (lhs.claimed == rhs.claimed) {
                targetKinds.indexOf(lhs.kind).compareTo(targetKinds.indexOf(rhs.kind))
            } else {
                if (!lhs.claimed && rhs.claimed) -1 else 1
            }
        }
    }

    fun claimReward(kind: DailyTaskKind): Int {
        val status = loadDailyTaskStatus()
        var coin = 0
        var changed = false

        when (kind) {
            DailyTaskKind.CHEST_ALL -> {
                if (status.chestClaimed.all { it } && !status.chestAllClaimed) {
                    status.chestAllClaimed = true
                    coin = chestAllReward
                    changed = true
                }
            }
            DailyTaskKind.RUNNING -> {
                if (status.runningKcal >= 200 && !status.runningRewardClaimed) {
                    status.runningRewardClaimed = true
                    coin = runningReward
                    changed = true
                }
            }
            DailyTaskKind.TRAINING -> {
                if (status.trainingMinutes >= 30 && !status.trainingRewardClaimed) {
                    status.trainingRewardClaimed = true
                    coin = trainingReward
                    changed = true
                }
            }
            DailyTaskKind.SIGN_IN -> {
                if (status.signInToday && !status.signInClaimed) {
                    status.signInClaimed = true
                    coin = signInReward
                    changed = true
                }
            }
            DailyTaskKind.NEW_USER_SPIN -> {
                if (status.newUserSpinCount >= 10 && !status.newUserSpinClaimed) {
                    status.newUserSpinClaimed = true
                    coin = spinReward
                    changed = true
                }
            }
            DailyTaskKind.CRACK_EGG -> {
                if (status.crackEggCount >= 10 && !status.crackEggClaimed) {
                    status.crackEggClaimed = true
                    coin = crackEggReward
                    changed = true
                }
            }
            DailyTaskKind.LUCKY_SLOT -> {
                if (status.luckySlotCount >= 10 && !status.luckySlotClaimed) {
                    status.luckySlotClaimed = true
                    coin = luckySlotReward
                    changed = true
                }
            }
            else -> {}
        }

        if (changed) {
            saveDailyTaskStatus(status)
            if (coin > 0) {
                addLocalCoin(coin)
            }
        }
        return coin
    }

    fun chestState(index: Int): ChestState {
        val status = _dailyTaskStatus.value
        if (index < 0 || index > 3) return ChestState.NOT_READY
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if (status.chestClaimed[index]) return ChestState.CLAIMED
        if (hour < chestHours[index]) return ChestState.WAITING
        val nextIdx = index + 1
        if (nextIdx < chestHours.size && hour >= chestHours[nextIdx]) return ChestState.EXPIRED
        return ChestState.CLAIMABLE
    }

    fun markUpperStepConversionClaimed() {
        val status = loadDailyTaskStatus()
        status.upperStepConversionClaimed = true
        saveDailyTaskStatus(status)
    }

    fun markNotificationClaimed() {
        val status = loadDailyTaskStatus()
        if (!status.notificationClaimed) {
            status.notificationClaimed = true
            saveDailyTaskStatus(status)
            addLocalCoin(50)
        }
    }

    fun markMotionUsageClaimed() {
        val status = loadDailyTaskStatus()
        if (!status.motionUsageClaimed) {
            status.motionUsageClaimed = true
            saveDailyTaskStatus(status)
            addLocalCoin(50)
        }
    }

    fun claimChest(index: Int): Int {
        val status = loadDailyTaskStatus()
        if (index < 0 || index > 3) return 0
        if (!status.chestClaimed[index] && chestState(index) == ChestState.CLAIMABLE) {
            status.chestClaimed[index] = true
            saveDailyTaskStatus(status)
            addLocalCoin(chestReward)
            return chestReward
        }
        return 0
    }

    fun chestTimeLabel(index: Int): String {
        if (index < 0 || index > 3) return "--:--"
        return String.format("%02d:00", chestHours[index])
    }

    fun getTaskName(kind: DailyTaskKind): String {
        val status = _dailyTaskStatus.value
        return when (kind) {
            DailyTaskKind.SIGN_IN -> "Daily Sign-in"
            DailyTaskKind.CHEST_ALL -> "Claim Treasure chests(${status.chestClaimed.count { it }}/4)"
            DailyTaskKind.NEW_USER_SPIN -> "Lucky Spin (${minOf(status.newUserSpinCount, 10)}/10)"
            DailyTaskKind.CRACK_EGG -> "Crack Golden Egg (${minOf(status.crackEggCount, 10)}/10)"
            DailyTaskKind.LUCKY_SLOT -> "Play Lucky Slot (${minOf(status.luckySlotCount, 10)}/10)"
            DailyTaskKind.RUNNING -> "Running Goal(${minOf(status.runningKcal, 200)}/200)"
            DailyTaskKind.TRAINING -> "Training Goal(${minOf(status.trainingMinutes, 30)}/30)"
            DailyTaskKind.NOTIFICATION -> "Daily Notification Permission"
            DailyTaskKind.MOTION_USAGE -> "Enable Sports and Health"
            else -> ""
        }
    }

    fun getTaskDesc(kind: DailyTaskKind): String = when (kind) {
        DailyTaskKind.SIGN_IN -> "Sign in for 28 days and get rewards"
        DailyTaskKind.CHEST_ALL -> "Claim at 8/12/16/20 Daily"
        DailyTaskKind.NEW_USER_SPIN, DailyTaskKind.CRACK_EGG, DailyTaskKind.LUCKY_SLOT -> "Have fun and earn"
        DailyTaskKind.RUNNING -> "Burn 200 kcal by Running"
        DailyTaskKind.TRAINING -> "Finish 30 min Course Training"
        else -> ""
    }

    fun getTaskIcon(kind: DailyTaskKind): String = when (kind) {
        DailyTaskKind.SIGN_IN -> "img_daily_task_sign"
        DailyTaskKind.CHEST_ALL -> "img_daily_task_chest"
        DailyTaskKind.NEW_USER_SPIN -> "img_daily_task_spin"
        DailyTaskKind.CRACK_EGG -> "img_daily_task_egg"
        DailyTaskKind.LUCKY_SLOT -> "img_daily_task_slot"
        DailyTaskKind.RUNNING -> "img_daily_task_running"
        DailyTaskKind.TRAINING -> "img_daily_task_workout"
        else -> ""
    }

    private fun todayKey(): String {
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.US)
        return sdf.format(Date())
    }

    private fun loadDailyTaskStatus(): DailyTaskStatus {
        val key = todayKey()
        return appPreferences.getCodable("DailyTaskStatus_$key", DailyTaskStatus::class.java)
            ?: DailyTaskStatus.empty()
    }

    private fun saveDailyTaskStatus(status: DailyTaskStatus) {
        val key = todayKey()
        appPreferences.setCodable("DailyTaskStatus_$key", status)
        loadAndPublishDailyTaskStatus()
    }

    private fun addLocalCoin(amount: Int) {
        val current = appPreferences.getInt("local_coin_balance")
        appPreferences.setInt("local_coin_balance", current + amount)
        coinManager.refreshCoins() // optionally trigger a refresh or sync
    }
}
