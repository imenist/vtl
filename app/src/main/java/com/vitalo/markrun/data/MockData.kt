/**
 * MockData.kt — Vitalo (MarkRun) Android 迁移假数据
 *
 * 基于 iOS Xcode 项目中的 Model 层和 ViewModel 属性生成。
 * 覆盖范围：首页列表、课程详情、跑步模块、任务页、签到、兑换/钱包、
 *           收集/图鉴、个人中心（侧边栏）、步数、提现记录等全部 UI 元素。
 *
 * 使用方式：
 *   val subjects = MockDataProvider.subjects
 *   val cards    = MockDataProvider.cards
 */

package com.vitalo.markrun.data.mock

import com.google.gson.annotations.SerializedName
import java.util.UUID

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── 1. 通用包装 & 枚举 ────
// ═══════════════════════════════════════════════════════════════════════════════

/** 通用 API 响应包装（对应 iOS CommonResponse<T>） */
data class CommonResponse<T>(
    @SerializedName("error_code") val errorCode: Int,
    @SerializedName("error_message") val errorMessage: String?,
    val data: T?
)

/** 性别（对应 iOS Gender 枚举） */
enum class Gender(val value: String) {
    FEMALE("female"),
    MALE("male")
}

/** H5 小游戏类型（对应 iOS WebGameKind） */
enum class WebGameKind {
    FLIP_CARD,
    SLOT_MACHINE,
    WHEEL,
    SMASH_EGG,
    SIGN_IN,
    DEBUG
}

/** 签到奖励类型（对应 iOS SignInRewardType） */
enum class SignInRewardType {
    COIN,   // 金币奖励
    CASH    // 提现码奖励
}

// endregion

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── 2. API 数据模型（网络 DTO） ────
// ═══════════════════════════════════════════════════════════════════════════════

// ──────────────── 2.1 训练 / 课程模块 ────────────────

/** 动作视频（对应 iOS Video） */
data class Video(
    @SerializedName("camera_angle") val cameraAngle: Int,
    @SerializedName("first_frame") val firstFrame: String,  // 视频首帧 URL
    @SerializedName("video_url") val videoUrl: String        // 视频 URL
)

/** 语音覆盖（对应 iOS VoiceCoverage） */
data class VoiceCoverage(
    @SerializedName("customize_prompt") val customizePrompt: String,
    @SerializedName("prompt_time_point") val promptTimePoint: Int, // 秒
    val speed: Double? = null
)

/**
 * 训练动作（对应 iOS Action）
 *
 * ⚠️ iOS 注释说 Action 自带的 duration 人工填写可能不对，应以 MountAction.duration 为准。
 */
data class Action(
    val code: String,
    val name: String,
    val thumbnail: String,                                   // 缩略图 URL
    @SerializedName("thumbnail_size")
    val thumbnailSize: String? = null,
    val videos: List<Video>,
    @SerializedName("countdown_time_point")
    val countdownTimePoint: Int,
    @SerializedName("countdown_prompt")
    val countdownPrompt: String,
    @SerializedName("preview_prompt")
    val previewPrompt: String,
    @SerializedName("preview_prompt_mp3")
    val previewPromptMp3: String? = null,
    val calorie: Double,
    val steps: String,                                       // 行动步骤（富文本）
    val label: List<Int>,
    val level: Int,
    @SerializedName("training_area")
    val trainingArea: List<Int>,
    @SerializedName("common_mistake")
    val commonMistake: String,
    val feel: String? = null,
    val breathing: String? = null,
    val good: String? = null,
    @SerializedName("voice_coach_txt")
    val voiceCoachTxt: String,
    @SerializedName("voice_coach_mp3")
    val voiceCoachMp3: String? = null
)

/** 挂载动作（对应 iOS MountAction） */
data class MountAction(
    val part: Int,                                           // 所属部分
    @SerializedName("preview_duration") val previewDuration: Int, // 秒
    val duration: Int,                                       // 秒（以此为准）
    @SerializedName("voice_coverage") val voiceCoverage: List<VoiceCoverage>? = null,
    val action: Action? = null
)

/** 课程（对应 iOS Lesson） */
data class Lesson(
    val code: String,
    val name: String,
    val cover: String,                                       // 封面 URL
    @SerializedName("cover_size")
    val coverSize: String? = null,
    @SerializedName("overview_cover")
    val overviewCover: String? = null,
    @SerializedName("overview_video")
    val overviewVideo: String? = null,
    @SerializedName("cover_tablet")
    val coverTablet: String = "",
    @SerializedName("cover_size_tablet")
    val coverSizeTablet: String? = null,
    @SerializedName("overview_cover_tablet")
    val overviewCoverTablet: String? = null,
    @SerializedName("overview_video_tablet")
    val overviewVideoTablet: String? = null,
    val duration: Int,                                       // 秒
    val calorie: Double,                                     // kcal
    val introduction: String? = null,
    val label: List<Int>? = null,
    val level: Int = 1,
    val target: List<Int>? = null,
    @SerializedName("training_area")
    val trainingArea: List<Int>? = null,
    val pay: Int = 0,                                        // 1=付费 0=免费
    @SerializedName("mount_actions")
    val mountActions: List<MountAction>? = null
)

/** 训练/专题训练（对应 iOS Training） */
data class Training(
    val code: String,
    val name: String,
    val type: Int,                                           // TrainingType
    val cover: String? = null,                               // 封面 URL
    @SerializedName("cover_size")
    val coverSize: String? = null,
    val video: String? = null,
    @SerializedName("duration_type")
    val durationType: Int = 0,
    val duration: Int = 0,                                   // 秒
    val calorie: Double = 0.0
)

/** 专题/分类（对应 iOS Subject） */
data class Subject(
    @SerializedName("subject_id") val subjectId: Int,
    val name: String,
    val label: List<Int>? = null,
    val training: List<Training>? = null,
    val style: String = "default"
)

/** 专题分页响应（对应 iOS SubjectListPage） */
data class SubjectListPage(
    val subject: List<Subject>,
    val cursor: Int
)

/** 专辑（对应 iOS Album） */
data class Album(
    val code: String,
    val name: String,
    val icon: String,                                        // 首页图标 URL
    @SerializedName("icon_size") val iconSize: String? = null,
    val cover: String? = null,
    @SerializedName("cover_size") val coverSize: String? = null,
    @SerializedName("lesson_num") val lessonNum: Int,
    val lessons: List<Lesson>? = null
)

/** 挂载课程（对应 iOS MountLesson） */
data class MountLesson(
    val day: Int,
    val stage: Int,
    val copywriting: String,
    val lesson: Lesson? = null
)

/** 训练计划（对应 iOS Plan） */
data class Plan(
    val code: String,
    val name: String,
    val type: Int,
    val cover: String,
    @SerializedName("cover_size") val coverSize: String? = null,
    @SerializedName("overview_cover") val overviewCover: String? = null,
    @SerializedName("overview_video") val overviewVideo: String? = null,
    @SerializedName("cover_tablet") val coverTablet: String = "",
    @SerializedName("cover_size_tablet") val coverSizeTablet: String? = null,
    @SerializedName("overview_cover_tablet") val overviewCoverTablet: String? = null,
    @SerializedName("overview_video_tablet") val overviewVideoTablet: String? = null,
    val days: Int,
    val calorie: Double,
    val introduction: String,
    val label: List<Int>,
    val level: Int,
    val target: List<Int>,
    @SerializedName("training_area") val trainingArea: List<Int>,
    @SerializedName("mount_lessons") val mountLessons: List<MountLesson>
)

// ──────────────── 2.2 账号 / 登录模块 ────────────────

/** Token（对应 iOS Token） */
data class Token(
    @SerializedName("access_token") val accessToken: String? = null,
    @SerializedName("expired_in") val expiredIn: Int = 7200,
    @SerializedName("refresh_token") val refreshToken: String? = null
)

/** 自动登录结果（对应 iOS AutoLoginResult） */
data class AutoLoginResult(
    @SerializedName("has_registered") val hasRegistered: Boolean,
    @SerializedName("user_id") val userId: Int? = null,
    val token: Token,
    @SerializedName("binding_accounts") val bindingAccounts: Int = 0,
    @SerializedName("register_time") val registerTime: Int = 0
)

// ──────────────── 2.3 金币 / 提现模块 ────────────────

/** 金币信息（对应 iOS CoinInfo） */
data class CoinInfo(
    @SerializedName("coin_code") val coinCode: String,
    @SerializedName("total_coin") val totalCoin: Double,
    @SerializedName("used_coin") val usedCoin: Double,
    @SerializedName("existing_coin") val existingCoin: Double
)

/** 金币类型（对应 iOS CoinType） */
data class CoinType(
    @SerializedName("coin_code") val coinCode: String,
    @SerializedName("coin_name") val coinName: String,
    val description: String
)

/** 提现档位（对应 iOS WithdrawalAmount） */
data class WithdrawalAmount(
    @SerializedName("cash_out_id") val cashOutId: Int,
    @SerializedName("prod_name") val prodName: String? = null,
    @SerializedName("filter_id") val filterId: Int? = null,
    @SerializedName("coin_code") val coinCode: String,
    @SerializedName("real_currency") val realCurrency: Double,
    @SerializedName("withdraw_code") val withdrawCode: String,
    @SerializedName("withdraw_type") val withdrawType: Int,   // 0=快速 1=新人专享 2=普通
    val state: Int = 1,
    @SerializedName("limit_per_user") val limitPerUser: Int = 1,
    @SerializedName("limit_user_per_day") val limitUserPerDay: Int = 1,
    @SerializedName("coin_amount_v2") var coinAmountV2: Double = 0.0,
    @SerializedName("common_state") val commonState: Int = 1,
    @SerializedName("limit_per_day") val limitPerDay: Int = 1,
    @SerializedName("coin_amount") val coinAmount: Int = 0,
    @SerializedName("limit_break_times") val limitBreakTimes: Int = 0,
    @SerializedName("limit_sign_days") var limitSignDays: Int = 0,
    @SerializedName("limit_clock_days") val limitClockDays: Int = 0,
    // 本地字段
    @SerializedName("watch_ad_times") var watchAdTimes: Int? = null,
    @SerializedName("crack_egg_fragments") var crackEggFragments: Int? = null,
    @Transient var isExchanged: Boolean = false
)

/** 提现配置（对应 iOS WithdrawalConfig） */
data class WithdrawalConfig(
    @SerializedName("is_today_withdraw") val isTodayWithdraw: Int,
    @SerializedName("withdraw_amounts") val withdrawAmounts: List<WithdrawalAmount>,
    @SerializedName("apply_withdraw_status") val applyWithdrawStatus: Int,
    @SerializedName("quick_withdraw") val quickWithdraw: Int,
    @SerializedName("is_first_time_withdraw") val isFirstTimeWithdraw: Int,
    @SerializedName("new_user_exclusive") val newUserExclusive: Int
)

/** 提现信息（对应 iOS WithdrawalInfo） */
data class WithdrawalInfo(
    val amount: Double,
    @SerializedName("apply_time") val applyTime: Long,
    @SerializedName("transfer_time") val transferTime: Long? = null,
    @SerializedName("withdraw_code") val withdrawCode: String,
    @SerializedName("response_code") val responseCode: String? = null,
    @SerializedName("withdraw_id") val withdrawId: String,
    @SerializedName("coin_code") val coinCode: String,
    @SerializedName("gc_claim_code") val gcClaimCode: String? = null,
    val queue: Int,
    @SerializedName("withdraw_method") val withdrawMethod: Int,
    val email: String? = null,
    val status: Int   // 0=待审核 1=已到账 2=失败
)

/** 提现记录（对应 iOS WithdrawalRecords） */
data class WithdrawalRecords(
    @SerializedName("withdraw_infos") val withdrawInfos: List<WithdrawalInfo>?,
    @SerializedName("next_cursor") val nextCursor: Long? = null
)

/** 提现结果（对应 iOS WithdrawalResult） */
data class WithdrawalResult(
    @SerializedName("gc_claim_code") val gcClaimCode: String? = null,
    val status: Int,
    @SerializedName("withdraw_id") val withdrawId: String,
    val queue: Int? = null
)

/** 商户信息（对应 iOS MerchantInfo） */
data class MerchantInfo(
    val id: Int,
    val merchant: String,
    val partner: Int,
    @SerializedName("account_status") val accountStatus: Int
)

/** 提现填写信息（对应 iOS WithdrawFillInfo） */
data class WithdrawFillInfo(
    val withdrawMethod: String,
    val accountName: String,
    val accountId: String,
    val email: String
)

// endregion

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── 3. 本地数据模型 ────
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * 跑步记录（对应 iOS RunningRecord — Room Entity）
 *
 * @property pace 配速 (分钟/公里)，计算属性
 */
data class RunningRecord(
    val id: Long = 0,
    val date: String,
    val startTime: Long,                                     // timestamp ms
    val endTime: Long,
    val duration: Int,                                       // 秒
    val distance: Double,                                    // 米
    val speed: Double,                                       // 米/秒
    val calories: Double,
    val startLatitude: Double,
    val startLongitude: Double,
    val endLatitude: Double,
    val endLongitude: Double,
    val lowIntensityTime: Int = 0,
    val moderateIntensityTime: Int = 0,
    val highIntensityTime: Int = 0,
    val imagePath: String? = null,
    val fragmentsCount: Int = 0,
    val coins: Int = 0,
    val adCoins: Int? = null
) {
    /** 配速 (min/km) */
    val pace: Double
        get() = if (distance > 0) (duration / 60.0) / (distance / 1000.0) else 0.0

    /** 格式化配速 e.g. 6'30" */
    val paceString: String
        get() {
            val minutes = pace.toInt()
            val seconds = ((pace - minutes) * 60).toInt()
            return "${minutes}'${String.format("%02d", seconds)}\""
        }

    /** 格式化时长 e.g. 32:15 或 01:02:03 */
    val durationString: String
        get() {
            val h = duration / 3600
            val m = (duration % 3600) / 60
            val s = duration % 60
            return if (h > 0) String.format("%02d:%02d:%02d", h, m, s)
            else String.format("%02d:%02d", m, s)
        }
}

/** 跑步轨迹点（对应 iOS RunningPoint） */
data class RunningPoint(
    val id: Long = 0,
    val recordId: Long,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val speed: Double
)

/** 体重记录（对应 iOS WeightRecord） */
data class WeightRecord(
    val id: String = UUID.randomUUID().toString(),
    val date: Long,       // timestamp ms
    val weight: Int        // kg
)

/** 步数历史（对应 iOS StepHistoryItem） */
data class StepHistoryItem(
    val id: String = UUID.randomUUID().toString(),
    val date: Long,        // timestamp ms
    val stepCount: Int
)

/** 用户信息（对应 iOS User） */
data class User(
    val gender: Gender? = null,
    val birthday: Long = 0L,     // timestamp ms
    val height: Int = 170,       // cm
    val weight: Int = 65         // kg
)

// ──────────────── 签到模型 ────────────────

/** 签到模型（对应 iOS SignInModel） */
data class SignInModel(
    val day: Int,                                            // 1-28
    var isSignedIn: Boolean = false,
    var isToday: Boolean = false,
    var isExpired: Boolean = false,
    var rewardType: SignInRewardType = SignInRewardType.COIN,
    var rewardImage: Int? = null,                         // 奖励图片资源名
    var rewardAmount: Int? = null,                           // 金币数量
    var cashAmount: Double? = null,                          // 提现金额
    var cashCode: String? = null,
    var signInDate: Long? = null                             // timestamp
)

// ──────────────── 任务模型 ────────────────

/** 每日任务类型（对应 iOS DailyTaskKind） */
enum class DailyTaskKind {
    CHEST,                  // 宝箱（附带 index 0-3）
    CHEST_ALL,              // 全部宝箱
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

/** 每日任务条目（对应 iOS DailyTaskInfo） */
data class DailyTaskInfo(
    val kind: DailyTaskKind,
    val chestIndex: Int = 0, // 仅 CHEST 类型使用
    val canClaim: Boolean,
    val claimed: Boolean,
    val reward: Int            // 金币数
)

// ──────────────── 收集 / 图鉴模型 ────────────────

/**
 * 收集卡片（对应 iOS Card）
 *
 * 图片资源映射表（iOS imageset → Android R.drawable）：
 *  - img_card_fruit        → R.drawable.img_card_fruit
 *  - img_card_doll         → R.drawable.img_card_doll
 *  - img_card_bottle       → R.drawable.img_card_bottle
 *  - img_card_battery      → R.drawable.img_card_battery
 *  - img_card_rock         → R.drawable.img_card_rock
 *  - img_card_flower       → R.drawable.img_card_flower
 *  - img_card_forest       → R.drawable.img_card_forest
 *  - img_card_sweat        → R.drawable.img_card_sweat
 *  - img_card_sun          → R.drawable.img_card_sun
 *  - img_card_breeze       → R.drawable.img_card_breeze
 *  - img_card_rain         → R.drawable.img_card_rain
 *  - img_card_black_cat    → R.drawable.img_card_black_cat
 *  - img_card_drunk_cat    → R.drawable.img_card_drunk_cat
 *  - img_card_spotted_dog  → R.drawable.img_card_spotted_dog
 *  - img_card_poodle       → R.drawable.img_card_poodle
 *  - img_card_warmth       → R.drawable.img_card_warmth
 *
 * 碎片资源映射表：
 *  - img_fragment_fruit        → R.drawable.img_fragment_fruit
 *  - img_fragment_doll         → R.drawable.img_fragment_doll
 *  - img_fragment_bottle       → R.drawable.img_fragment_bottle
 *  - img_fragment_battery      → R.drawable.img_fragment_battery
 *  - img_fragment_rock         → R.drawable.img_fragment_rock
 *  - img_fragment_flower       → R.drawable.img_fragment_flower
 *  - img_fragment_forest       → R.drawable.img_fragment_forest
 *  - img_fragment_sweat        → R.drawable.img_fragment_sweat
 *  - img_fragment_sun          → R.drawable.img_fragment_sun
 *  - img_fragment_breeze       → R.drawable.img_fragment_breeze
 *  - img_fragment_rain         → R.drawable.img_fragment_rain
 *  - img_fragment_black_cat    → R.drawable.img_fragment_black_cat
 *  - img_fragment_drunk_cat    → R.drawable.img_fragment_drunk_cat
 *  - img_fragment_spotted_dog  → R.drawable.img_fragment_spotted_dog
 *  - img_fragment_poodle       → R.drawable.img_fragment_poodle
 *  - img_fragment_warmth       → R.drawable.img_fragment_warmth
 */
data class Card(
    val id: Int,
    val name: String,
    val description: String,
    val fragmentId: Int,
    val fragmentName: String,
    val fragmentRequiredNum: Int,
    val eventId: Int,
    val rewardWeight: Int,
    // ── Android 资源占位 ──
    /** 完整卡片图片资源名, 映射到 R.drawable.img_card_xxx */
    val cardImageRes: Int,
    /** 碎片图片资源名, 映射到 R.drawable.img_fragment_xxx */
    val fragmentImageRes: Int
)

/**
 * 卡片事件（对应 iOS CardEvent）
 *
 * 事件图片资源映射表（iOS imageset → Android R.drawable）：
 *  - img_event_pick_rubbish → R.drawable.img_event_pick_rubbish
 *  - img_event_wipe_sweat   → R.drawable.img_event_wipe_sweat
 *  - img_event_look_sky     → R.drawable.img_event_look_sky
 *  - img_event_pet_cat      → R.drawable.img_event_pet_cat
 *  - img_event_pet_dog      → R.drawable.img_event_pet_dog
 *  - img_event_say_hello    → R.drawable.img_event_say_hello
 *
 * Lottie 动画映射表（assets/lottie/xxx/data.json）：
 *  - RunningEventPickRubbish
 *  - RunningEventWipeSweat
 *  - RunningEventLookSky
 *  - RunningEventPetCat
 *  - RunningEventPetDog
 *  - RunningEventSayHello
 *  - RunningEventPickCoin
 */
data class CardEvent(
    val id: Int,
    val name: String,
    val description: String,
    /** 事件静态图片资源名, 映射到 R.drawable.img_event_xxx */
    val eventImageRes: Int,
    /** Lottie 动画名, 用于 assets/lottie/{lottieName}/data.json */
    val lottieName: String
)

// endregion

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── 4. MockDataProvider ────
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * 假数据提供者。
 *
 * ⚠️ 所有图片 URL 使用 `https://placehold.co` 占位，实际 Android 项目请替换为
 *    R.drawable 或 Coil 加载真实远程 URL。
 *
 * 图标/图片资源名通过注释标注 iOS 原始名称，便于映射 Android R.drawable。
 */
object MockDataProvider {

    // ════════════════════════════ 占位图 URL ════════════════════════════

    private const val PLACEHOLDER_COVER_WIDE =
        "https://placehold.co/800x450/1a1a2e/C9FF6B?text=Cover"
    private const val PLACEHOLDER_COVER_TALL =
        "https://placehold.co/400x600/1a1a2e/C9FF6B?text=Cover"
    private const val PLACEHOLDER_THUMB = "https://placehold.co/200x200/222222/FFFFFF?text=Action"
    private const val PLACEHOLDER_ICON_SM = "https://placehold.co/100x100/0D120E/FFED29?text=Icon"
    private const val PLACEHOLDER_VIDEO =
        "https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/360/Big_Buck_Bunny_360_10s_1MB.mp4"
    private const val PLACEHOLDER_FIRST_FRAME =
        "https://placehold.co/800x450/333333/FFFFFF?text=Frame"
    private const val PLACEHOLDER_AVATAR = "https://placehold.co/120x120/444444/C9FF6B?text=Me"

    // ════════════════════════════ 首页列表（Home / LessonView） ════════════════════════════

    // region ── 2.1 Videos ──

    val sampleVideos = listOf(
        Video(cameraAngle = 1, firstFrame = PLACEHOLDER_FIRST_FRAME, videoUrl = PLACEHOLDER_VIDEO),
        Video(cameraAngle = 2, firstFrame = PLACEHOLDER_FIRST_FRAME, videoUrl = PLACEHOLDER_VIDEO)
    )

    // endregion

    // region ── 2.2 Actions ──

    val sampleActions = listOf(
        Action(
            code = "ACT001",
            name = "Jumping Jacks",
            thumbnail = PLACEHOLDER_THUMB,
            thumbnailSize = "200x200",
            videos = sampleVideos,
            countdownTimePoint = 25,
            countdownPrompt = "3 seconds left!",
            previewPrompt = "Stand straight, arms by your side",
            previewPromptMp3 = null,
            calorie = 5.2,
            steps = "1. Stand with feet together, arms at sides.\n2. Jump feet apart while raising arms overhead.\n3. Jump back to starting position.\n4. Repeat.",
            label = listOf(1, 2),
            level = 1,
            trainingArea = listOf(1, 2, 3),
            commonMistake = "Don't lock your knees when landing. Keep a slight bend to absorb impact and protect your joints during high-rep sets.",
            feel = "Full body warm-up with increased heart rate",
            breathing = "Inhale when arms go up, exhale when arms go down",
            good = "Great for cardiovascular health and full-body warm-up",
            voiceCoachTxt = "Let's start with jumping jacks! Keep your core tight and move those arms!"
        ),
        Action(
            code = "ACT002",
            name = "High Knees",
            thumbnail = PLACEHOLDER_THUMB,
            videos = sampleVideos,
            countdownTimePoint = 20,
            countdownPrompt = "Almost done!",
            previewPrompt = "Run in place, lifting knees high",
            calorie = 8.5,
            steps = "1. Stand tall.\n2. Lift right knee to hip height.\n3. Switch to left knee.\n4. Continue alternating rapidly.",
            label = listOf(1),
            level = 2,
            trainingArea = listOf(4, 5),
            commonMistake = "Avoid leaning backward; stay upright",
            good = "Boosts heart rate quickly, engages core and hip flexors",
            voiceCoachTxt = "Knees up! Keep that pace strong!"
        ),
        Action(
            code = "ACT003",
            name = "Plank Hold",
            thumbnail = PLACEHOLDER_THUMB,
            videos = sampleVideos,
            countdownTimePoint = 50,
            countdownPrompt = "10 more seconds!",
            previewPrompt = "Hold plank position",
            calorie = 3.8,
            steps = "1. Get into push-up position.\n2. Lower onto forearms.\n3. Keep body in a straight line from head to heels.\n4. Hold position.",
            label = listOf(3),
            level = 1,
            trainingArea = listOf(2),
            commonMistake = "Don't let your hips sag or pike up",
            breathing = "Breathe steadily throughout the hold",
            good = "Core strength, stability, and endurance",
            voiceCoachTxt = "Hold it steady! Don't drop those hips!"
        ),
        // 空描述测试
        Action(
            code = "ACT004",
            name = "Mountain Climbers",
            thumbnail = PLACEHOLDER_THUMB,
            videos = emptyList(), // ← 空视频列表测试
            countdownTimePoint = 25,
            countdownPrompt = "",
            previewPrompt = "",
            calorie = 7.0,
            steps = "",           // ← 空步骤测试
            label = emptyList(),
            level = 3,
            trainingArea = listOf(1, 2, 4),
            commonMistake = "",
            voiceCoachTxt = ""
        )
    )

    // endregion

    // region ── 2.3 MountActions ──

    val sampleMountActions = sampleActions.mapIndexed { index, action ->
        MountAction(
            part = index + 1,
            previewDuration = 5,
            duration = listOf(30, 25, 60, 30)[index],
            voiceCoverage = listOf(
                VoiceCoverage(customizePrompt = "Keep going!", promptTimePoint = 10),
                VoiceCoverage(customizePrompt = "Halfway there!", promptTimePoint = 15, speed = 1.2)
            ),
            action = action
        )
    }

    // endregion

    // region ── 2.4 Lessons ──

    val sampleLessons = listOf(
        Lesson(
            code = "LESSON001",
            name = "Full Body Burn",
            cover = PLACEHOLDER_COVER_WIDE,
            coverSize = "800x450",
            duration = 900,  // 15 min
            calorie = 120.0,
            introduction = "A high-intensity full body workout designed to burn calories and build lean muscle. Perfect for intermediate fitness enthusiasts looking to challenge themselves with a mix of cardio and strength exercises.",
            label = listOf(1, 2),
            level = 2,
            target = listOf(1),
            trainingArea = listOf(1, 2, 3, 4),
            pay = 0,
            mountActions = sampleMountActions
        ),
        Lesson(
            code = "LESSON002",
            name = "Core Crusher",
            cover = PLACEHOLDER_COVER_WIDE,
            duration = 600,  // 10 min
            calorie = 85.0,
            introduction = "Focus on your core with this targeted ab workout",
            level = 1,
            target = listOf(2),
            trainingArea = listOf(2),
            pay = 0,
            mountActions = sampleMountActions.take(2)
        ),
        Lesson(
            code = "LESSON003",
            name = "Leg Day Challenge — Advanced High Intensity Lower Body Training For Maximum Results",
            cover = PLACEHOLDER_COVER_WIDE,
            duration = 1800,  // 30 min — 测试长时长显示
            calorie = 250.0,
            // 测试长文本介绍（换行测试）
            introduction = "Push your lower body to the limit with this advanced leg workout.\n\n" +
                    "This program targets your quadriceps, hamstrings, glutes, and calves " +
                    "through a carefully designed sequence of exercises.\n\n" +
                    "• Warm-up: 5 minutes dynamic stretching\n" +
                    "• Main set: 20 minutes high-intensity\n" +
                    "• Cool-down: 5 minutes static stretching\n\n" +
                    "Recommended for experienced athletes who have been training consistently for at least 3 months.",
            label = listOf(3),
            level = 3,
            target = listOf(3),
            trainingArea = listOf(4, 5),
            pay = 1, // 付费课程
            mountActions = sampleMountActions
        ),
        // 空数据场景
        Lesson(
            code = "LESSON_EMPTY",
            name = "Coming Soon",
            cover = "",                // ← 空封面 URL
            duration = 0,
            calorie = 0.0,
            introduction = null,       // ← 空简介
            label = null,
            level = 0,
            target = null,
            trainingArea = null,
            pay = 0,
            mountActions = null        // ← 无动作
        )
    )

    // endregion

    // region ── 2.5 Trainings ──

    val sampleTrainings = listOf(
        Training(
            code = "TR001",
            name = "Morning Boost",
            type = 1,
            cover = PLACEHOLDER_COVER_WIDE,
            coverSize = "800x450",
            duration = 900,
            calorie = 120.0
        ),
        Training(
            code = "TR002",
            name = "Quick HIIT",
            type = 1,
            cover = PLACEHOLDER_COVER_WIDE,
            duration = 600,
            calorie = 95.0
        ),
        Training(
            code = "TR003",
            name = "Evening Stretch",
            type = 2,
            cover = PLACEHOLDER_COVER_WIDE,
            duration = 1200,
            calorie = 60.0
        ),
        Training(
            code = "TR004",
            name = "Cardio Thunder — High Intensity Interval Training For Advanced Athletes With Extended Cool-Down",
            type = 1,
            cover = PLACEHOLDER_COVER_WIDE,
            duration = 1800,
            calorie = 300.0
        ), // 长标题测试
        Training(
            code = "TR005",
            name = "Yoga Flow",
            type = 3,
            cover = null,
            duration = 2400,
            calorie = 80.0
        ),   // 无封面测试
        Training(
            code = "TR_EMPTY",
            name = "",
            type = 0,
            cover = null,
            duration = 0,
            calorie = 0.0
        )        // 空数据测试
    )

    // endregion

    // region ── 2.6 Subjects (首页列表核心数据) ──

    /**
     * 首页课程列表 — Subject 列表
     *
     * iOS `LessonView` 通过 SubjectListPage API 获取此数据，
     * 渲染为多个横向滚动区域，每个 Subject 下有若干 Training 卡片。
     *
     * 图标资源映射：
     *  - ic_home_tab_training  → R.drawable.ic_home_tab_training
     *  - ic_home_tab_task      → R.drawable.ic_home_tab_task
     *  - ic_home_tab_running   → R.drawable.ic_home_tab_running
     *  - ic_home_tab_exchange  → R.drawable.ic_home_tab_exchange
     *  - ic_home_tab_collection→ R.drawable.ic_home_tab_collection
     */
    val subjects = listOf(
        Subject(
            subjectId = 1,
            name = "Today's Plan",
            label = listOf(1),
            training = sampleTrainings.take(2),
            style = "highlight"
        ),
        Subject(
            subjectId = 2,
            name = "Full Body Workouts",
            training = sampleTrainings.take(4),
            style = "default"
        ),
        Subject(
            subjectId = 3,
            name = "Stretch & Recovery",
            training = listOf(sampleTrainings[2], sampleTrainings[4]),
            style = "default"
        ),
        // 空训练列表 — 测试空态
        Subject(
            subjectId = 4,
            name = "Premium Collection",
            training = emptyList(),
            style = "default"
        )
    )

    val subjectListPage = SubjectListPage(subject = subjects, cursor = 0)

    // endregion

    // region ── 2.7 Albums ──

    val albums = listOf(
        Album(
            code = "ALBUM001",
            name = "7-Day Beginner Challenge",
            icon = PLACEHOLDER_ICON_SM,   // iOS: ic_album_beginner
            iconSize = "100x100",
            cover = PLACEHOLDER_COVER_WIDE,
            lessonNum = 7,
            lessons = sampleLessons.take(3)
        ),
        Album(
            code = "ALBUM002",
            name = "Fat Burn Series",
            icon = PLACEHOLDER_ICON_SM,   // iOS: ic_album_fat_burn
            cover = PLACEHOLDER_COVER_WIDE,
            lessonNum = 12,
            lessons = null  // 测试尚未加载的课程列表
        )
    )

    // endregion

    // region ── 2.8 Plans ──

    val plans = listOf(
        Plan(
            code = "PLAN001",
            name = "4-Week Total Body Transformation — Beginner to Intermediate Progressive Overload Program",
            type = 1,
            cover = PLACEHOLDER_COVER_WIDE,
            days = 28,
            calorie = 3500.0,
            introduction = "Transform your body in just 4 weeks with our progressive workout plan. Each week increases in intensity to help you build strength, burn fat, and improve overall fitness.\n\nWeek 1-2: Foundation building\nWeek 3-4: Intensity ramping",
            label = listOf(1, 2),
            level = 1,
            target = listOf(1, 2),
            trainingArea = listOf(1, 2, 3, 4, 5),
            mountLessons = listOf(
                MountLesson(
                    day = 1,
                    stage = 1,
                    copywriting = "Let's start easy",
                    lesson = sampleLessons[0]
                ),
                MountLesson(
                    day = 2,
                    stage = 1,
                    copywriting = "Core day",
                    lesson = sampleLessons[1]
                ),
                MountLesson(day = 3, stage = 1, copywriting = "Rest day", lesson = null) // 休息日无课程
            )
        )
    )

    // endregion

    // ════════════════════════════ 账号 / 登录 ════════════════════════════

    val mockToken = Token(
        accessToken = "mock_access_token_abc123xyz",
        expiredIn = 7200,
        refreshToken = "mock_refresh_token_def456uvw"
    )

    val mockAutoLoginResult = AutoLoginResult(
        hasRegistered = true,
        userId = 100001,
        token = mockToken,
        bindingAccounts = 0,
        registerTime = 1712000000  // 约 2024-04-02
    )

    // ════════════════════════════ 用户信息 ════════════════════════════

    val mockUser = User(
        gender = Gender.MALE,
        birthday = 946684800000L,  // 2000-01-01
        height = 175,
        weight = 70
    )

    /** 空用户 — 测试新手引导未完成场景 */
    val mockUserEmpty = User()

    // ════════════════════════════ 金币 / 提现 (钱包页 ExchangeView) ════════════════════════════

    // region ── 金币信息 ──

    /**
     * 金币信息
     * 首页顶部 CoinBalanceView 展示 existingCoin
     *
     * 图标资源：ic_coin → R.drawable.ic_coin
     */
    val coinInfoList = listOf(
        CoinInfo(coinCode = "CALO", totalCoin = 12580.0, usedCoin = 3200.0, existingCoin = 9380.0),
        CoinInfo(coinCode = "BONUS", totalCoin = 500.0, usedCoin = 0.0, existingCoin = 500.0)
    )

    /** 零余额 — 测试空钱包 */
    val coinInfoEmpty =
        CoinInfo(coinCode = "CALO", totalCoin = 0.0, usedCoin = 0.0, existingCoin = 0.0)

    val coinTypes = listOf(
        CoinType(
            coinCode = "CALO",
            coinName = "CaloCoin",
            description = "Main currency earned through workouts, running, and daily tasks"
        ),
        CoinType(
            coinCode = "BONUS",
            coinName = "BonusCoin",
            description = "Bonus currency from special events and referrals"
        )
    )

    // endregion

    // region ── 提现配置 & 记录 ──

    val withdrawalAmounts = listOf(
        WithdrawalAmount(
            cashOutId = 1,
            coinCode = "CALO",
            realCurrency = 0.3,
            withdrawCode = "WC001",
            withdrawType = 0,
            coinAmount = 1000,
            coinAmountV2 = 1000.0,
            limitSignDays = 0
        ),
        WithdrawalAmount(
            cashOutId = 2,
            coinCode = "CALO",
            realCurrency = 1.0,
            withdrawCode = "WC002",
            withdrawType = 1,
            coinAmount = 3000,
            coinAmountV2 = 3000.0,
            limitSignDays = 3,
            watchAdTimes = 5
        ),
        WithdrawalAmount(
            cashOutId = 3,
            coinCode = "CALO",
            realCurrency = 5.0,
            withdrawCode = "WC003",
            withdrawType = 2,
            coinAmount = 15000,
            coinAmountV2 = 15000.0,
            limitSignDays = 7,
            watchAdTimes = 20
        ),
        WithdrawalAmount(
            cashOutId = 4,
            coinCode = "CALO",
            realCurrency = 10.0,
            withdrawCode = "WC004",
            withdrawType = 2,
            coinAmount = 30000,
            coinAmountV2 = 30000.0,
            limitSignDays = 14,
            watchAdTimes = 50
        ),
        WithdrawalAmount(
            cashOutId = 5,
            coinCode = "CALO",
            realCurrency = 50.0,
            withdrawCode = "WC005",
            withdrawType = 2,
            coinAmount = 100000,
            coinAmountV2 = 100000.0,
            limitSignDays = 28,
            watchAdTimes = 100
        ),
        // 已兑换项
        WithdrawalAmount(
            cashOutId = 6,
            coinCode = "CALO",
            realCurrency = 0.1,
            withdrawCode = "WC006",
            withdrawType = 0,
            coinAmount = 500,
            coinAmountV2 = 500.0,
            isExchanged = true
        )
    )

    val withdrawalConfig = WithdrawalConfig(
        isTodayWithdraw = 0,
        withdrawAmounts = withdrawalAmounts,
        applyWithdrawStatus = 1,
        quickWithdraw = 1,
        isFirstTimeWithdraw = 1,
        newUserExclusive = 1
    )

    /** 提现记录 — 展示多种状态 */
    val withdrawalRecords = WithdrawalRecords(
        withdrawInfos = listOf(
            WithdrawalInfo(
                amount = 0.3,
                applyTime = 1712200000000,
                transferTime = 1712200300000,
                withdrawCode = "WC001",
                withdrawId = "WD-001",
                coinCode = "CALO",
                gcClaimCode = "GIFT-ABC123",
                queue = 0,
                withdrawMethod = 1,
                email = "user@example.com",
                status = 1
            ),
            WithdrawalInfo(
                amount = 1.0,
                applyTime = 1712100000000,
                withdrawCode = "WC002",
                withdrawId = "WD-002",
                coinCode = "CALO",
                queue = 15,
                withdrawMethod = 1,
                email = "user@example.com",
                status = 0
            ),  // 排队中
            WithdrawalInfo(
                amount = 5.0,
                applyTime = 1711900000000,
                withdrawCode = "WC003",
                withdrawId = "WD-003",
                coinCode = "CALO",
                queue = 0,
                withdrawMethod = 1,
                status = 2
            ),  // 失败, 无邮箱
            // 空记录场景在 withdrawalRecordsEmpty 中
        ),
        nextCursor = null
    )

    /** 空提现记录 */
    val withdrawalRecordsEmpty = WithdrawalRecords(withdrawInfos = emptyList(), nextCursor = null)

    val merchants = listOf(
        MerchantInfo(id = 1, merchant = "PayPal", partner = 1, accountStatus = 1),
        MerchantInfo(id = 2, merchant = "GiftCard", partner = 2, accountStatus = 1)
    )

    // endregion

    // ════════════════════════════ 收集 / 图鉴 (CollectionView) ════════════════════════════

    /**
     * 16 张收集卡片 — 1:1 复刻 iOS Card.presets
     *
     * fragmentProgress 模拟：部分卡片已解锁，部分收集中，部分未开始。
     */
    val cards = listOf(
        Card(
            id = 10000001,
            name = "Fruit",
            description = "You picked up peel and it turned into fresh fruit—making the world cleaner.",
            fragmentId = 1000100001,
            fragmentName = "Peel Piece",
            fragmentRequiredNum = 9,
            eventId = 1,
            rewardWeight = 9,
            cardImageRes = com.vitalo.markrun.R.drawable.img_card_fruit,
            fragmentImageRes = com.vitalo.markrun.R.drawable.img_fragment_fruit
        ),
        Card(
            id = 10000002,
            name = "BuBu",
            description = "BuBu is a beloved plushie made from every scrap cloth you gathered.",
            fragmentId = 1000200002,
            fragmentName = "Rags",
            fragmentRequiredNum = 9,
            eventId = 1,
            rewardWeight = 9,
            cardImageRes = com.vitalo.markrun.R.drawable.img_card_doll,
            fragmentImageRes = com.vitalo.markrun.R.drawable.img_fragment_doll
        ),
        Card(
            id = 10000003,
            name = "Bottle",
            description = "You picked up an empty bottle. A passerby smiled, and so did you.",
            fragmentId = 1000300003,
            fragmentName = "Plastic Bit",
            fragmentRequiredNum = 9,
            eventId = 1,
            rewardWeight = 9,
            cardImageRes = com.vitalo.markrun.R.drawable.img_card_bottle,
            fragmentImageRes = com.vitalo.markrun.R.drawable.img_fragment_bottle
        ),
        Card(
            id = 10000004,
            name = "Drone",
            description = "The drone built from batteries you found flies free in the sky.",
            fragmentId = 1000400004,
            fragmentName = "Battery Chip",
            fragmentRequiredNum = 9,
            eventId = 1,
            rewardWeight = 9,
            cardImageRes = com.vitalo.markrun.R.drawable.img_card_battery,
            fragmentImageRes = com.vitalo.markrun.R.drawable.img_fragment_battery
        ),
        Card(
            id = 10000005,
            name = "Stone House",
            description = "This stone house made of every rock you found stands strong and unshakeable.",
            fragmentId = 1000500005,
            fragmentName = "Rock Shard",
            fragmentRequiredNum = 9,
            eventId = 1,
            rewardWeight = 9,
            cardImageRes = com.vitalo.markrun.R.drawable.img_card_rock,
            fragmentImageRes = com.vitalo.markrun.R.drawable.img_fragment_rock
        ),
        Card(
            id = 10000006,
            name = "Flower",
            description = "A little flower stuck to your shoe, saying, \"Great run today!\"",
            fragmentId = 1000600006,
            fragmentName = "Petal Piece",
            fragmentRequiredNum = 9,
            eventId = 1,
            rewardWeight = 9,
            cardImageRes = com.vitalo.markrun.R.drawable.img_card_flower,
            fragmentImageRes = com.vitalo.markrun.R.drawable.img_fragment_flower
        ),
        Card(
            id = 10000007,
            name = "Forest",
            description = "Every leaf you held became a forest—adding green to the world.",
            fragmentId = 1000700007,
            fragmentName = "Leaf Drop",
            fragmentRequiredNum = 9,
            eventId = 1,
            rewardWeight = 9,
            cardImageRes = com.vitalo.markrun.R.drawable.img_card_forest,
            fragmentImageRes = com.vitalo.markrun.R.drawable.img_fragment_forest
        ),
        Card(
            id = 10000008,
            name = "Bathtub",
            description = "Each drop of sweat became a badge of happiness after the shower.",
            fragmentId = 1000800008,
            fragmentName = "Sweat Bead",
            fragmentRequiredNum = 9,
            eventId = 2,
            rewardWeight = 6,
            cardImageRes = com.vitalo.markrun.R.drawable.img_card_sweat,
            fragmentImageRes = com.vitalo.markrun.R.drawable.img_fragment_sweat
        ),
        Card(
            id = 10000009,
            name = "Sunny",
            description = "It's sunny—light filters through leaves, giving you a \"well done!\"",
            fragmentId = 1000900009,
            fragmentName = "Sunbeam Bit",
            fragmentRequiredNum = 9,
            eventId = 3,
            rewardWeight = 6,
            cardImageRes = com.vitalo.markrun.R.drawable.img_card_sun,
            fragmentImageRes = com.vitalo.markrun.R.drawable.img_fragment_sun
        ),
        Card(
            id = 10000010,
            name = "Windmill",
            description = "The windmill rustles your hair and blows worries away as you run on.",
            fragmentId = 1001000010,
            fragmentName = "Breeze Chip",
            fragmentRequiredNum = 9,
            eventId = 3,
            rewardWeight = 6,
            cardImageRes = com.vitalo.markrun.R.drawable.img_card_breeze,
            fragmentImageRes = com.vitalo.markrun.R.drawable.img_fragment_breeze
        ),
        Card(
            id = 10000011,
            name = "Umbrella",
            description = "An umbrella shelters you in rain—wishing you the very best!",
            fragmentId = 1001100011,
            fragmentName = "Raindrop Bit",
            fragmentRequiredNum = 9,
            eventId = 3,
            rewardWeight = 6,
            cardImageRes = com.vitalo.markrun.R.drawable.img_card_rain,
            fragmentImageRes = com.vitalo.markrun.R.drawable.img_fragment_rain
        ),
        Card(
            id = 10000012,
            name = "Black Cat",
            description = "A black cat crosses your morning route, looks back—you feel lucky.",
            fragmentId = 1001200012,
            fragmentName = "Fish Chip",
            fragmentRequiredNum = 9,
            eventId = 4,
            rewardWeight = 3,
            cardImageRes = com.vitalo.markrun.R.drawable.img_card_black_cat,
            fragmentImageRes = com.vitalo.markrun.R.drawable.img_fragment_black_cat
        ),
        Card(
            id = 10000013,
            name = "Drunk Cat",
            description = "In the evening, a drunk cat rubs you and brings a moment of joy.",
            fragmentId = 1001300013,
            fragmentName = "Wine Bottle Chip",
            fragmentRequiredNum = 9,
            eventId = 4,
            rewardWeight = 3,
            cardImageRes = com.vitalo.markrun.R.drawable.img_card_drunk_cat,
            fragmentImageRes = com.vitalo.markrun.R.drawable.img_fragment_drunk_cat
        ),
        Card(
            id = 10000014,
            name = "Spotted Dog",
            description = "A pup chases you suddenly—running together feels just like childhood.",
            fragmentId = 1001400014,
            fragmentName = "Fur Bit",
            fragmentRequiredNum = 9,
            eventId = 5,
            rewardWeight = 3,
            cardImageRes = com.vitalo.markrun.R.drawable.img_card_spotted_dog,
            fragmentImageRes = com.vitalo.markrun.R.drawable.img_fragment_spotted_dog
        ),
        Card(
            id = 10000015,
            name = "Poodle",
            description = "A poodle in the park wags at you, as if saying, \"You're so fast!\"",
            fragmentId = 1001500015,
            fragmentName = "Bow Bit",
            fragmentRequiredNum = 9,
            eventId = 5,
            rewardWeight = 3,
            cardImageRes = com.vitalo.markrun.R.drawable.img_card_poodle,
            fragmentImageRes = com.vitalo.markrun.R.drawable.img_fragment_poodle
        ),
        Card(
            id = 10000016,
            name = "Warmth",
            description = "A stranger nods at you—suddenly the world feels a bit lighter.",
            fragmentId = 1001600016,
            fragmentName = "Hello Piece",
            fragmentRequiredNum = 9,
            eventId = 6,
            rewardWeight = 1,
            cardImageRes = com.vitalo.markrun.R.drawable.img_card_warmth,
            fragmentImageRes = com.vitalo.markrun.R.drawable.img_fragment_warmth
        )
    )

    /** 碎片收集进度模拟: fragmentId → collectedCount */
    val fragmentProgress: Map<Int, Int> = mapOf(
        1000100001 to 9,  // Fruit — 已解锁
        1000200002 to 5,  // BuBu — 收集中
        1000300003 to 9,  // Bottle — 已解锁
        1000400004 to 0,  // Drone — 未开始
        1000500005 to 2,  // Stone House — 刚起步
        1000600006 to 9,  // Flower — 已解锁
        1000700007 to 7,  // Forest — 即将解锁
        1000800008 to 0,
        1000900009 to 3,
        1001000010 to 0,
        1001100011 to 0,
        1001200012 to 1,
        1001300013 to 0,
        1001400014 to 0,
        1001500015 to 0,
        1001600016 to 0
    )

    /** 卡片事件 — 1:1 复刻 iOS CardEvent.presets */
    val cardEvents = listOf(
        CardEvent(
            id = 1,
            name = "Pick Rubbish",
            description = "You picked up some trash and made the world cleaner. You found",
            eventImageRes = com.vitalo.markrun.R.drawable.img_event_pick_rubbish,
            lottieName = "RunningEventPickRubbish"
        ),
        CardEvent(
            id = 2,
            name = "Wipe Sweat",
            description = "You paused to wipe your sweat and discovered",
            eventImageRes = com.vitalo.markrun.R.drawable.img_event_wipe_sweat,
            lottieName = "RunningEventWipeSweat"
        ),
        CardEvent(
            id = 3,
            name = "Look Sky",
            description = "You slowed down, looked up at the sky, and noticed",
            eventImageRes = com.vitalo.markrun.R.drawable.img_event_look_sky,
            lottieName = "RunningEventLookSky"
        ),
        CardEvent(
            id = 4,
            name = "Pet Cat",
            description = "You knelt to pet a street cat and picked up",
            eventImageRes = com.vitalo.markrun.R.drawable.img_event_pet_cat,
            lottieName = "RunningEventPetCat"
        ),
        CardEvent(
            id = 5,
            name = "Pet Dog",
            description = "A puppy wagged its tail at you. You patted its head and found",
            eventImageRes = com.vitalo.markrun.R.drawable.img_event_pet_dog,
            lottieName = "RunningEventPetDog"
        ),
        CardEvent(
            id = 6,
            name = "Say Hello",
            description = "You smiled and greeted a stranger. They gifted you",
            eventImageRes = com.vitalo.markrun.R.drawable.img_event_say_hello,
            lottieName = "RunningEventSayHello"
        ),
        CardEvent(
            id = 7,
            name = "Pick Coin",
            description = "When you bend down, you find a pile of CaloCoins on the ground",
            eventImageRes = 0,
            lottieName = "RunningEventPickCoin"
        ) // 无静态图
    )

    // ════════════════════════════ 跑步模块 (RunningView) ════════════════════════════

    /**
     * 跑步记录样本
     *
     * 坐标点使用新加坡区域 (1.29°N, 103.85°E) 模拟。
     */
    val runningRecords = listOf(
        RunningRecord(
            id = 1,
            date = "Apr 10, 2026",
            startTime = 1712710800000,
            endTime = 1712712600000,
            duration = 1800,     // 30 min
            distance = 5230.0,   // 5.23 km
            speed = 2.91,
            calories = 320.0,
            startLatitude = 1.2903,
            startLongitude = 103.8519,
            endLatitude = 1.2965,
            endLongitude = 103.8563,
            lowIntensityTime = 600,
            moderateIntensityTime = 900,
            highIntensityTime = 300,
            fragmentsCount = 3,
            coins = 450,
            adCoins = 100
        ),
        RunningRecord(
            id = 2,
            date = "Apr 9, 2026",
            startTime = 1712624400000,
            endTime = 1712625300000,
            duration = 900,
            distance = 1520.0,
            speed = 1.69,
            calories = 95.0,
            startLatitude = 1.2852,
            startLongitude = 103.8440,
            endLatitude = 1.2880,
            endLongitude = 103.8475,
            lowIntensityTime = 600,
            moderateIntensityTime = 300,
            highIntensityTime = 0,
            fragmentsCount = 1,
            coins = 150
        ),
        // 长距离跑 — 测试大数值展示
        RunningRecord(
            id = 3,
            date = "Apr 7, 2026",
            startTime = 1712451600000,
            endTime = 1712458800000,
            duration = 7200,     // 2 hours
            distance = 21100.0,  // 半马
            speed = 2.93,
            calories = 1350.0,
            startLatitude = 1.2800,
            startLongitude = 103.8300,
            endLatitude = 1.3100,
            endLongitude = 103.8700,
            lowIntensityTime = 1200,
            moderateIntensityTime = 3600,
            highIntensityTime = 2400,
            fragmentsCount = 8,
            coins = 1200,
            adCoins = 300
        ),
        // 最短跑 — 边界测试
        RunningRecord(
            id = 4,
            date = "Apr 6, 2026",
            startTime = 1712365200000,
            endTime = 1712365260000,
            duration = 60,
            distance = 120.0,
            speed = 2.0,
            calories = 8.0,
            startLatitude = 1.2903,
            startLongitude = 103.8519,
            endLatitude = 1.2905,
            endLongitude = 103.8520,
            fragmentsCount = 0,
            coins = 10
        )
    )

    /** 空跑步记录 — 新用户 / 最近活动为空 */
    val runningRecordsEmpty = emptyList<RunningRecord>()

    /** 跑步轨迹点（Record #1 的轨迹） */
    val runningPoints = listOf(
        RunningPoint(
            id = 1,
            recordId = 1,
            latitude = 1.2903,
            longitude = 103.8519,
            timestamp = 1712710800000,
            speed = 2.5
        ),
        RunningPoint(
            id = 2,
            recordId = 1,
            latitude = 1.2910,
            longitude = 103.8525,
            timestamp = 1712710860000,
            speed = 2.8
        ),
        RunningPoint(
            id = 3,
            recordId = 1,
            latitude = 1.2920,
            longitude = 103.8535,
            timestamp = 1712710920000,
            speed = 3.0
        ),
        RunningPoint(
            id = 4,
            recordId = 1,
            latitude = 1.2935,
            longitude = 103.8540,
            timestamp = 1712710980000,
            speed = 3.2
        ),
        RunningPoint(
            id = 5,
            recordId = 1,
            latitude = 1.2945,
            longitude = 103.8550,
            timestamp = 1712711040000,
            speed = 2.9
        ),
        RunningPoint(
            id = 6,
            recordId = 1,
            latitude = 1.2955,
            longitude = 103.8555,
            timestamp = 1712711100000,
            speed = 2.7
        ),
        RunningPoint(
            id = 7,
            recordId = 1,
            latitude = 1.2965,
            longitude = 103.8563,
            timestamp = 1712711160000,
            speed = 2.5
        )
    )

    // ════════════════════════════ 签到 (SignInView) ════════════════════════════

    /**
     * 28 天签到数据
     *
     * 模拟用户第 10 天的场景：Day 1-7 已签到，Day 8-9 过期未签，Day 10 是今天。
     * Day 7/14/21/28 为大额金币日（500 金币），其余为 100 金币。
     * Day 3 和 Day 7 包含提现码奖励。
     */
    val signInData: List<SignInModel> = (1..28).map { day ->
        SignInModel(
            day = day,
            isSignedIn = day <= 7,
            isToday = day == 10,
            isExpired = day in 8..9,
            rewardType = when (day) {
                3 -> SignInRewardType.CASH
                7 -> SignInRewardType.CASH
                else -> SignInRewardType.COIN
            },
            rewardImage = when (day) {
                // iOS: ic_sign_in_chest_7 / ic_sign_in_chest_14 等
                7, 14, 21, 28 -> com.vitalo.markrun.R.drawable.ic_sign_in_chest
                else -> null
            },
            rewardAmount = when {
                day == 3 || day == 7 -> null // 提现码奖励无金币数
                day % 7 == 0 -> 500
                else -> 100
            },
            cashAmount = when (day) {
                3 -> 0.10
                7 -> 0.30
                else -> null
            },
            cashCode = when (day) {
                3 -> "SIGN3-ABCDEF"
                7 -> "SIGN7-GHIJKL"
                else -> null
            }
        )
    }

    /** 全新用户签到 — 全部未签 */
    val signInDataNewUser: List<SignInModel> = (1..28).map { day ->
        SignInModel(
            day = day,
            isToday = day == 1,
            rewardType = SignInRewardType.COIN,
            rewardAmount = if (day % 7 == 0) 500 else 100
        )
    }

    // ════════════════════════════ 任务 (TaskView) ════════════════════════════

    /**
     * 每日任务列表
     *
     * 图标资源映射（iOS → Android）：
     *  - ic_task_chest_0 ~ ic_task_chest_3 → R.drawable.ic_task_chest_*
     *  - ic_task_running                   → R.drawable.ic_task_running
     *  - ic_task_training                  → R.drawable.ic_task_training
     *  - ic_task_sign_in                   → R.drawable.ic_task_sign_in
     *  - ic_task_spin                      → R.drawable.ic_task_spin
     *  - ic_task_crack_egg                 → R.drawable.ic_task_crack_egg
     *  - ic_task_slot                      → R.drawable.ic_task_slot
     *  - ic_task_notification              → R.drawable.ic_task_notification
     *  - ic_task_step                      → R.drawable.ic_task_step
     *  - ic_task_relax                     → R.drawable.ic_task_relax
     */
    val dailyTasks = listOf(
        // 宝箱任务 (0-3)
        DailyTaskInfo(
            kind = DailyTaskKind.CHEST,
            chestIndex = 0,
            canClaim = true,
            claimed = false,
            reward = 50
        ),
        DailyTaskInfo(
            kind = DailyTaskKind.CHEST,
            chestIndex = 1,
            canClaim = false,
            claimed = false,
            reward = 100
        ),
        DailyTaskInfo(
            kind = DailyTaskKind.CHEST,
            chestIndex = 2,
            canClaim = false,
            claimed = false,
            reward = 200
        ),
        DailyTaskInfo(
            kind = DailyTaskKind.CHEST,
            chestIndex = 3,
            canClaim = false,
            claimed = false,
            reward = 500
        ),
        // 综合宝箱
        DailyTaskInfo(
            kind = DailyTaskKind.CHEST_ALL,
            canClaim = false,
            claimed = false,
            reward = 1000
        ),
        // 跑步任务
        DailyTaskInfo(
            kind = DailyTaskKind.RUNNING,
            canClaim = false,
            claimed = false,
            reward = 200
        ),
        // 训练任务
        DailyTaskInfo(
            kind = DailyTaskKind.TRAINING,
            canClaim = true,
            claimed = false,
            reward = 150
        ),
        // 签到
        DailyTaskInfo(
            kind = DailyTaskKind.SIGN_IN,
            canClaim = false,
            claimed = true,
            reward = 100
        ),  // 已领取
        // 新人转盘
        DailyTaskInfo(
            kind = DailyTaskKind.NEW_USER_SPIN,
            canClaim = false,
            claimed = false,
            reward = 50
        ),
        // 砸蛋
        DailyTaskInfo(
            kind = DailyTaskKind.CRACK_EGG,
            canClaim = false,
            claimed = false,
            reward = 80
        ),
        // 老虎机
        DailyTaskInfo(
            kind = DailyTaskKind.LUCKY_SLOT,
            canClaim = false,
            claimed = false,
            reward = 80
        ),
        // 通知权限
        DailyTaskInfo(
            kind = DailyTaskKind.NOTIFICATION,
            canClaim = true,
            claimed = false,
            reward = 50
        ),
        // 运动权限
        DailyTaskInfo(
            kind = DailyTaskKind.MOTION_USAGE,
            canClaim = false,
            claimed = true,
            reward = 50
        ),
        // 每日放松
        DailyTaskInfo(
            kind = DailyTaskKind.DAILY_RELAXATION,
            canClaim = false,
            claimed = false,
            reward = 100
        ),
        // 多日放松
        DailyTaskInfo(
            kind = DailyTaskKind.MULTI_DAILY_RELAXATION,
            canClaim = false,
            claimed = false,
            reward = 120
        ),
        // 步数上限提升
        DailyTaskInfo(
            kind = DailyTaskKind.UPPER_STEP_CONVERSION,
            canClaim = false,
            claimed = false,
            reward = 200
        )
    )

    // ════════════════════════════ 步数 (StepCounterView) ════════════════════════════

    /**
     * 步数历史
     *
     * 图标资源：ic_step_shoe → R.drawable.ic_step_shoe
     */
    val stepHistory = listOf(
        StepHistoryItem(date = 1712710800000, stepCount = 8532),
        StepHistoryItem(date = 1712624400000, stepCount = 12045),
        StepHistoryItem(date = 1712538000000, stepCount = 6210),
        StepHistoryItem(date = 1712451600000, stepCount = 15780),  // 高步数日
        StepHistoryItem(date = 1712365200000, stepCount = 3105),
        StepHistoryItem(date = 1712278800000, stepCount = 9920),
        StepHistoryItem(date = 1712192400000, stepCount = 0)       // 零步数日
    )

    // ════════════════════════════ 个人中心 / 侧边栏 (MineView) ════════════════════════════

    /**
     * 体重记录
     *
     * 模拟过去 14 天的数据，部分天无记录（null gap）。
     */
    val weightRecords = listOf(
        WeightRecord(date = 1712710800000, weight = 70),
        WeightRecord(date = 1712624400000, weight = 70),
        WeightRecord(date = 1712538000000, weight = 71),
        // Gap — 模拟未记录
        WeightRecord(date = 1712365200000, weight = 69),
        WeightRecord(date = 1712278800000, weight = 69),
        WeightRecord(date = 1712192400000, weight = 70),
        WeightRecord(date = 1712106000000, weight = 71),
        // Gap
        WeightRecord(date = 1711933200000, weight = 72),
        WeightRecord(date = 1711846800000, weight = 72),
        WeightRecord(date = 1711760400000, weight = 73)
    )

    /** 空体重记录 — 新用户 */
    val weightRecordsEmpty = emptyList<WeightRecord>()

    /**
     * 个人中心最佳记录 — 用于 BestRecordView
     *
     * 图标资源映射：
     *  - ic_best_distance   → R.drawable.ic_best_distance
     *  - ic_best_duration   → R.drawable.ic_best_duration
     *  - ic_best_speed      → R.drawable.ic_best_speed
     *  - ic_best_calories   → R.drawable.ic_best_calories
     *  - ic_personal        → R.drawable.ic_personal
     */
    data class BestRecord(
        val longestDistance: Double,       // km
        val longestDuration: Int,          // 秒
        val fastestPace: String,           // e.g. "5'30\""
        val highestCalories: Double        // kcal
    )

    val bestRecord = BestRecord(
        longestDistance = 21.1,
        longestDuration = 7200,
        fastestPace = "5'15\"",
        highestCalories = 1350.0
    )

    // ════════════════════════════ 侧边栏 / TabBar 图标资源映射 ════════════════════════════

    /**
     * 底部 TabBar 图标资源映射（自定义 TabBar，5 个 Tab）
     *
     * iOS imageset → Android R.drawable：
     *  - ic_home_tab_training    → R.drawable.ic_home_tab_training     (Tab 0: 课程，默认选中)
     *  - ic_home_tab_task        → R.drawable.ic_home_tab_task         (Tab 1: 任务)
     *  - ic_home_tab_running     → R.drawable.ic_home_tab_running      (Tab 2: 跑步，中间凸起按钮)
     *  - ic_home_tab_exchange    → R.drawable.ic_home_tab_exchange     (Tab 3: 兑换)
     *  - ic_home_tab_collection  → R.drawable.ic_home_tab_collection   (Tab 4: 收集)
     *
     * 顶部 TopBar 控件图标：
     *  - ic_coin                 → R.drawable.ic_coin                  (金币余额图标)
     *  - ic_earn_rules           → R.drawable.ic_earn_rules            (赚币规则按钮)
     *  - ic_personal             → R.drawable.ic_personal              (个人中心入口)
     *
     * TabBar 样式参考：
     *  - 背景色: #0D120E
     *  - 顶部圆角: 20dp
     *  - 渐变色: #C9FF6B → #FFED29
     */

    // ════════════════════════════ Lottie 动画清单 (直接复用) ════════════════════════════

    /**
     * Lottie 动画名称 → 路径映射
     *
     * Android 用法: LottieCompositionSpec.Asset("lottie/$name/data.json")
     *
     * 名称列表：
     *  SplashBackground, SplashForeground,
     *  CoinArrivedDialog, CoinArrivedWithProgressDialog, AddCoinEffect,
     *  RunningEventPetCat, RunningEventPetDog, RunningEventPickCoin,
     *  RunningEventPickCoinPrelude, RunningEventPickRubbish,
     *  RunningEventLookSky, RunningEventSayHello, RunningEventWipeSweat,
     *  SmashEggEntrance, SlotEntrance,
     *  SpinBorder, SpinLightAward, SpinNowButton, SpinWheelResult, SpinWheelResultBtn,
     *  TrainingPackage, WithdrawCodeDialog,
     *  WebViewLine, WebViewRunning, DailyCourseGift
     */
    val lottieAnimations = listOf(
        "SplashBackground", "SplashForeground",
        "CoinArrivedDialog", "CoinArrivedWithProgressDialog", "AddCoinEffect",
        "RunningEventPetCat", "RunningEventPetDog", "RunningEventPickCoin",
        "RunningEventPickCoinPrelude", "RunningEventPickRubbish",
        "RunningEventLookSky", "RunningEventSayHello", "RunningEventWipeSweat",
        "SmashEggEntrance", "SlotEntrance",
        "SpinBorder", "SpinLightAward", "SpinNowButton", "SpinWheelResult", "SpinWheelResultBtn",
        "TrainingPackage", "WithdrawCodeDialog",
        "WebViewLine", "WebViewRunning", "DailyCourseGift"
    )

    // ════════════════════════════ 辅助工具方法 ════════════════════════════

    /** 按 rewardWeight 概率随机返回一张卡片（复刻 iOS Card.randomByWeight） */
    fun randomCardByWeight(): Card? {
        val totalWeight = cards.sumOf { it.rewardWeight }
        if (totalWeight <= 0) return null
        val rand = (0 until totalWeight).random()
        var acc = 0
        for (card in cards) {
            acc += card.rewardWeight
            if (rand < acc) return card
        }
        return null
    }

    /** 判断卡片是否已解锁 */
    fun isCardUnlocked(card: Card): Boolean {
        return (fragmentProgress[card.fragmentId] ?: 0) >= card.fragmentRequiredNum
    }

    /** 获取卡片事件的图片资源名 */
    fun getEventImageRes(eventId: Int): Int {
        return cardEvents.find { it.id == eventId }?.eventImageRes ?: 0
    }

    /** 获取卡片事件的 Lottie 动画名 */
    fun getEventLottieName(eventId: Int): String {
        return cardEvents.find { it.id == eventId }?.lottieName ?: ""
    }
}
