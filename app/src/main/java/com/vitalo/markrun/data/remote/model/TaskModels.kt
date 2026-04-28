package com.vitalo.markrun.data.remote.model

enum class DailyTaskKind {
    CHEST,
    CHEST_ALL,
    RUNNING,
    TRAINING,
    SIGN_IN,
    NEW_USER_SPIN,
    CRACK_EGG,
    LUCKY_SLOT,
    NOTIFICATION,
    MOTION_USAGE,
    DAILY_RELAXATION,
    MULTI_DAILY_RELAXATION,
    UPPER_STEP_CONVERSION
}

data class DailyTaskInfo(
    val kind: DailyTaskKind,
    val canClaim: Boolean,
    val claimed: Boolean,
    val reward: Int
)

data class DailyTaskStatus(
    var chestClaimed: MutableList<Boolean> = mutableListOf(false, false, false, false),
    var chestAllClaimed: Boolean = false,
    var runningKcal: Int = 0,
    var runningRewardClaimed: Boolean = false,
    var trainingMinutes: Int = 0,
    var trainingRewardClaimed: Boolean = false,
    var signInToday: Boolean = false,
    var signInClaimed: Boolean = false,
    var newUserSpinCount: Int = 0,
    var newUserSpinClaimed: Boolean = false,
    var crackEggCount: Int = 0,
    var crackEggClaimed: Boolean = false,
    var luckySlotCount: Int = 0,
    var luckySlotClaimed: Boolean = false,
    var upRunKcalLimitToday: Boolean = false,
    var upRunKcalLimitClaimed: Boolean = false,
    var dailyRelaxationCompleted: Boolean = false,
    var dailyRelaxationRewardClaimed: Boolean = false,
    var multiDailyRelaxationCompletedLinkIndices: MutableList<Boolean> = mutableListOf(false, false, false),
    var multiDailyRelaxationRewardClaimed: Boolean = false,
    var upperStepConversionClaimed: Boolean = false,
    var notificationClaimed: Boolean = false,
    var motionUsageClaimed: Boolean = false
) {
    val multiDailyRelaxationCompletedCount: Int
        get() = multiDailyRelaxationCompletedLinkIndices.count { it }

    companion object {
        fun empty() = DailyTaskStatus()
    }
}

enum class ChestState {
    NOT_READY, WAITING, CLAIMABLE, CLAIMED, EXPIRED
}

enum class SignInRewardType {
    COIN, CASH
}

data class SignInModel(
    val day: Int,
    var isSignedIn: Boolean = false,
    var isToday: Boolean = false,
    var isExpired: Boolean = false,
    var rewardAmount: Int = 100,
    var rewardType: SignInRewardType = SignInRewardType.COIN,
    var cashAmount: Double? = null,
    var cashCode: String? = null,
    var signInDate: Long? = null
)

data class Card(
    val id: Int,
    val name: String,
    val description: String,
    val fragmentId: Int,
    val fragmentName: String,
    val fragmentRequiredNum: Int,
    val rewardWeight: Int
) {
    companion object {
        val presets: List<Card> = listOf(
            Card(10000001, "Fruit", "You picked up peel and it turned into fresh fruit.", 1000100001, "Peel Piece", 9, 9),
            Card(10000002, "BuBu", "BuBu is a beloved plushie made from scrap cloth.", 1000200002, "Rags", 9, 9),
            Card(10000003, "Bottle", "You picked up an empty bottle. A passerby smiled.", 1000300003, "Plastic Bit", 9, 9),
            Card(10000004, "Drone", "The drone built from batteries flies free.", 1000400004, "Battery Chip", 9, 9),
            Card(10000005, "Stone House", "This stone house stands strong and unshakeable.", 1000500005, "Rock Shard", 9, 9),
            Card(10000006, "Flower", "A little flower saying, Great run today!", 1000600006, "Petal Piece", 9, 9),
            Card(10000007, "Forest", "Every leaf became a forest, adding green.", 1000700007, "Leaf Drop", 9, 9),
            Card(10000008, "Bathtub", "Each drop of sweat became happiness.", 1000800008, "Sweat Bead", 9, 6),
            Card(10000009, "Sunny", "Light filters through leaves, giving well done!", 1000900009, "Sunbeam Bit", 9, 6),
            Card(10000010, "Windmill", "The windmill blows worries away.", 1001000010, "Breeze Chip", 9, 6),
            Card(10000011, "Umbrella", "An umbrella shelters you in rain.", 1001100011, "Raindrop Bit", 9, 6),
            Card(10000012, "Black Cat", "A black cat crosses your route, you feel lucky.", 1001200012, "Fish Chip", 9, 3),
            Card(10000013, "Drunk Cat", "A drunk cat rubs you and brings joy.", 1001300013, "Wine Bottle Chip", 9, 3),
            Card(10000014, "Spotted Dog", "A pup chases you, feels like childhood.", 1001400014, "Fur Bit", 9, 3),
            Card(10000015, "Poodle", "A poodle wags at you, You're so fast!", 1001500015, "Bow Bit", 9, 3),
            Card(10000016, "Warmth", "A stranger nods, the world feels lighter.", 1001600016, "Hello Piece", 9, 1)
        )

        fun cardImageName(fragmentId: Int): String = when (fragmentId) {
            1000100001 -> "img_card_fruit"
            1000200002 -> "img_card_doll"
            1000300003 -> "img_card_bottle"
            1000400004 -> "img_card_battery"
            1000500005 -> "img_card_rock"
            1000600006 -> "img_card_flower"
            1000700007 -> "img_card_forest"
            1000800008 -> "img_card_sweat"
            1000900009 -> "img_card_sun"
            1001000010 -> "img_card_breeze"
            1001100011 -> "img_card_rain"
            1001200012 -> "img_card_black_cat"
            1001300013 -> "img_card_drunk_cat"
            1001400014 -> "img_card_spotted_dog"
            1001500015 -> "img_card_poodle"
            1001600016 -> "img_card_warmth"
            else -> ""
        }
    }
}

enum class StepMilestoneStatus {
    LOCKED, CLAIMABLE, CLAIMED
}

data class StepMilestone(
    val requiredSteps: Int,
    val rewardCoins: Int,
    val isFree: Boolean,
    var status: StepMilestoneStatus = StepMilestoneStatus.LOCKED
) {
    companion object {
        fun allMilestones(): List<StepMilestone> = listOf(
            StepMilestone(0, 10, true),
            StepMilestone(10, 30, true),
            StepMilestone(100, 50, true),
            StepMilestone(500, 100, false),
            StepMilestone(1000, 150, false),
            StepMilestone(2000, 200, false),
            StepMilestone(3000, 250, false),
            StepMilestone(4000, 300, false)
        )
    }
}
