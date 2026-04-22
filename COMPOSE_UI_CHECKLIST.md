# Compose UI 开发清单

> 本文档从完整迁移方案中提炼，**仅保留 UI 还原所必需的上下文**。
> 假数据来源：`MockData.kt` → `MockDataProvider`

---

## 1. 视觉映射表 — Theme Tokens

### 1.1 颜色系统

#### 核心色板

| Token 名 | Hex | 用途 |
|-----------|-----|------|
| `Primary` | `#0D120E` | 全局深色背景/文字主色（TabBar 填充、标题文字、按钮背景） |
| `PrimaryText` | `#0D120E` | 正文/标题文字色 |
| `SecondaryText` | `#757575` | 副标题/辅助说明文字 |
| `DisabledText` | `#D6D6D6` | 占位符/不可用文字/引导页 skip 文字 |
| `Surface` | `#F8F8F8` | 浅色面板/卡片背景（跟练页休息面板等） |
| `SurfaceAlt` | `#F4F3EE` | 钱包/提现页面背景 |
| `SurfaceWarm` | `#D7D5C7` | 进度条轨道/规则弹窗背景 |
| `Divider` | `#B2B2B2` | 分割线/占位缩略图背景 |

#### 品牌渐变

| Token 名 | 渐变色 | 方向 | 用途 |
|-----------|--------|------|------|
| `GradientBrand` | `#C9FF6B` → `#FFED29` | leading → trailing | TabBar 光晕、启动页进度条、收集页按钮、里程碑 |
| `GradientOrange` | `#FE6544` → `#FBA23C` | leading → trailing | 强调按钮（碎片兑换、跑步详情强度条） |
| `GradientWallet` | `#000000` → `#37511B` → `#669A35` | top → bottom | 钱包余额渐变 |
| `GradientStep` | `#AFFF24` → `#FFEB09` | 环形 | 步数圆环进度条 |
| `GradientWithdraw` | `#C6DB1F@0.2` → `#08C261@0.2` | top → bottom | 提现页进度条背景 |

#### 功能色

| Token 名 | Hex | 用途 |
|-----------|-----|------|
| `Accent` | `#E2FF04` | 高亮标签/跟练进度条/收集卡牌未锁按钮背景 |
| `Success` | `#08C261` | 提现成功/审核状态 |
| `SuccessAlt` | `#54C98C` | 审核中状态 |
| `Danger` | `#C00F0C` | 删除按钮 |
| `Orange` | `#FF8534` | TodayTraining 锁标签 |
| `Blue` | `#2BADF4` | 签到入口高亮 |
| `InnerShadowPurple` | `#839EFF` | AdGamePlay 按钮内阴影 |

#### 收集/兑换专用色

| Token 名 | Hex | 用途 |
|-----------|-----|------|
| `FragmentTextDark` | `#5D5C1B` | 碎片文字/标题 |
| `FragmentShadow` | `#94820B@0.29` | 碎片文字阴影 |
| `FragmentSubtext` | `#9B8C19` | 碎片副文字 |
| `FragmentBg` | `#FFFEE6` | 碎片卡片背景 |

#### 步数里程碑专用色

| Token 名 | Hex/说明 |
|-----------|----------|
| `MilestoneActive` | `#C9FF6B` |
| `MilestoneClaimed` | `#B5B08D` (单色) |
| `MilestoneDefault` | `#E0E0E0` |
| `MilestoneInactive` | `#E8E8E8` |
| `MilestoneGradient` | `#FFDA24` → `#FE9814` → `#FC1702` (可领取) |
| `MilestoneButtonInner1` | `#FF9B20@0.22` (inner shadow) |
| `MilestoneButtonInner2` | `#FFAB2C@0.4` (inner shadow) |

---

### 1.2 字体规范

iOS 默认用 `SF Pro`（`.system`），Android 对应 `sans-serif`（默认）。
自定义字体仅一个：`Inter-BlackItalic.otf` → `res/font/inter_black_italic.otf`

#### 字号映射表（pt → sp）

> iOS `.system(size: X, weight: W)` 中尺寸后缀 `.UI` 表示设计稿尺寸。
> Android 1:1 映射即可，pt ≈ sp。

| 场景 | iOS Size | Weight | Compose 建议 Token |
|------|----------|--------|-------------------|
| 大标题 (引导页) | 40 | `.heavy` | `HeadlineLarge` |
| 页面主标题 | 28 | `.heavy` | `HeadlineMedium` |
| 签到标题 | 26 | `.bold` | `HeadlineSmall` |
| 副标题 | 24 | `.heavy` | `TitleLarge` |
| 数字大字 | 42 | `.bold` | 自定义 `DisplayNumber` |
| 模块标题 | 20 | `.heavy` | `TitleMedium` (Inter-BlackItalic) |
| 按钮文字 | 18 | `.black` / `.bold` | `TitleSmall` |
| 正文标题 | 16 | `.semibold` | `BodyLarge` |
| 正文 | 15~16 | `.light` / `.regular` | `BodyMedium` |
| 辅助说明 | 14 | `.regular` | `BodySmall` |
| 标签/徽章 | 13 | `.medium` | `LabelLarge` |
| 小标签 | 12 | `.semibold` / `.medium` | `LabelMedium` |
| 微标签 | 10~11 | `.medium` | `LabelSmall` |

---

### 1.3 间距 & 圆角

| Token | 值 | 用途 |
|-------|-----|------|
| `SpacingXS` | 4dp | 图标与文字内间距 |
| `SpacingSM` | 8dp | 列表项内部 |
| `SpacingMD` | 12dp | 卡片内边距 |
| `SpacingLG` | 16dp | 区域外边距 |
| `SpacingXL` | 20dp | 屏幕水平边距 |
| `SpacingXXL` | 24~32dp | 大块间距 |
| `RadiusSM` | 8dp | 小按钮/标签 |
| `RadiusMD` | 12dp | 卡片/对话框 |
| `RadiusLG` | 16dp | 大容器 |
| `RadiusXL` | 20dp | TabBar 顶部圆角 |
| `RadiusFull` | 90dp | 跑步按钮（圆形） |

---

### 1.4 TabBar 关键尺寸

| 属性 | 值 |
|------|-----|
| 高度 | 78dp (含背景) + safeAreaBottom |
| 可用高度 | 43dp + safeAreaBottom |
| 背景色 | `#0D120E` |
| 顶部圆角 | 20dp (仅 topLeft + topRight) |
| 光晕 | `GradientBrand` 渐变矩形，blur=10，opacity=0.4 |
| 跑步按钮 | 60×60dp 圆形，`GradientBrand` 背景，offset Y: -30dp |
| 跑步按钮 shadow | `rgba(0.93, 0.95, 0.24, 0.5)` ≈ `#EDF23D@50%`，radius=4.3，y=-4 |
| 跑步图标 | 36×36dp (居中于 60dp 按钮) |
| Tab 间距 | 等分，左右边距 5.25 × (screenWidth / 375) |
| 跑步两侧留白 | 18.75 × (screenWidth / 375) |

---

## 2. 屏幕清单

### Tab 0：LessonScreen（课程列表）

| 组件 | iOS 源文件 | MockData 字段 |
|------|-----------|--------------|
| Subject 分区列表 (LazyColumn) | `LessonView.swift` | `subjects`, `subjectListPage` |
| TodayTrainingCardView | `TodayTrainingCardView.swift` | `sampleTrainings` |
| LessonCardView | `LessonCardView.swift` | `sampleLessons` |
| TopBar 左侧: CoinBalanceView (showExchange=true, 含 Redeem 按钮) | `CoinBalanceView.swift` | `coinInfoList` |
| TopBar 右上: AdGamePlayButton + RulesButton + MineButton | `AdGamePlayButton.swift`, `EarnRulesDialog.swift` | — |
| 下拉刷新 | — | — |
| DailySignEntranceView (签到入口浮层) | `DailySignEntranceView.swift` | — |

**交互**：点击 Training 卡片 → LessonDetailScreen；点击 Redeem → 切换到 ExchangeScreen Tab

---

### Tab 1：TaskScreen（每日任务）

**组件与源码映射：**
| Components | iOS 源文件 | MockData 字段 |
|------------|-------------|---------------|
| TaskView (主入口) | `TaskView.swift` | — |
| TaskContentView (主内容区) | `TaskContentView.swift` | `dailyTasks` |
| TaskChestListView (限时宝箱行) | `TaskChestListView.swift` / `TaskChestItemView.swift` | `dailyTasks` (CHEST 开头) |
| SettingSectionView (模块标题) | `SettingSectionView.swift` | — |
| DailyTaskView (通用单任务项) | `DailyTaskView.swift` | `dailyTasks` |
| TaskDailyRelaxationItemView (多入口休闲任务) | `TaskDailyRelaxationItemView.swift` | `dailyTasks` (multiDailyRelaxation) |
| TopBar 左侧: CoinBalanceView | `CoinBalanceView.swift` | `coinInfoList` |

**UI 骨架复刻指南 (Compose)：**
> 还原核心点：Task 页面的根部是一个**带滚动能力的淡黄色到浅黄色渐变背景** (`LinearGradient`)，TopBar 与内容均直接放在统一的 `ScrollView`（对应 Compose 的 `Column + verticalScroll`）中。
```kotlin
@Composable
fun TaskScreen(
    isLoading: Boolean,
    isNetworkError: Boolean,
    onRetry: () -> Unit
) {
    val scrollState = rememberScrollState()
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    // 1. 全局带滚动和渐变背景的主体
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFDFFFE), // 对应 iOS red: 0.99, green: 1.0, blue: 0.97
                        Color(0xFFFDF28C)  // 对应 iOS red: 0.99, green: 0.95, blue: 0.55
                    ),
                    start = Offset(x = Float.POSITIVE_INFINITY, y = Float.POSITIVE_INFINITY), // 对应特定角度
                    end = Offset(0f, 0f)
                )
            )
            .verticalScroll(scrollState)
    ) {
        // 2. 随页面滚动的 TopBar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .statusBarsPadding()
                .padding(top = 8.dp)
        ) {
            Text(
                text = "Task Center",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0D120E),
                modifier = Modifier.align(Alignment.Center)
            )
            CoinBalanceView(modifier = Modifier.align(Alignment.CenterStart))
        }

        Spacer(modifier = Modifier.height(22.dp))

        // 3. 页面状态与内容切换
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight - 146.dp), // 近似预留顶部高度
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (isNetworkError) {
            CommonNetworkErrorView(
                onRetry = onRetry,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight - 146.dp)
            )
        } else {
            TaskContentView()
        }
    }
}

@Composable
fun TaskContentView() {
    Column(modifier = Modifier.fillMaxWidth()) {
        // 4. 限时宝箱区域
        SettingSectionView(
            sectionName = "Limited-Time Chest",
            modifier = Modifier.padding(start = 21.dp)
        )
        TaskChestListView(
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 5. 每日任务区域
        SettingSectionView(
            sectionName = "Daily Tasks",
            modifier = Modifier.padding(start = 21.dp)
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 43.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 列表生成:
            // - multiDailyRelaxation 类型 -> TaskDailyRelaxationItemView
            // - 其他常规类型 -> DailyTaskView
        }
    }
}
```

---

### Tab 2：跑步入口（不切 Tab，直接 navigate）

点击 → LocationPermissionScreen 或 RunTrackerScreen

---

### Tab 3/4：ExchangeScreen（兑换/钱包） & CollectionScreen（收集/图鉴）

> 顺序由 AB 测试决定，默认 Tab3=Exchange, Tab4=Collection

#### ExchangeScreen

**组件与源码映射：**
| Components | iOS 源文件 | MockData 字段 |
|------------|-------------|---------------|
| ExchangeView (主页) | `ExchangeView.swift` | — |
| MyWalletView (余额卡) | `MyWalletView.swift` / `MyWalletWithAdView.swift` | `coinInfoList`, `coinTypes` |
| WithdrawScreen (提现) | `WithdrawlView.swift` | `withdrawalConfig`, `withdrawalAmounts` |
| WithdrawMethodsView | `WithdrawMethodsView.swift` | — |
| WithdrawFillInfoDialog | `WithdrawFillInfoDialog.swift` | `merchants` |
| WithdrawCodeDialog (小额兑换码) | `WithdrawCodeDialog.swift` | — (Lottie: `WithdrawCodeDialog`) |
| WithdrawQueuingDialog (大额排队) | `WithdrawQueuingDialog.swift` | — |
| WithdrawRecordScreen | `WithdrawRecordView.swift` | `withdrawalRecords` |
| WithdrawReviewDialog | `WithdrawReviewDialog.swift` | — |
| RedeemFragmentView (碎片兑换入口) | `RedeemFragmentView.swift` | `cards`, `fragmentProgress` |
| ExchangeFragmentDialog | `ExchangeFragmentDialog.swift` | — |
| ADProgressDialog | `ADProgressDialog.swift` | — |
| ConversionRulesDialog | `ConversionRulesDialog.swift` | — |

**UI 骨架复刻指南 (Compose)：**
> 还原核心点：`TopBar` (My Wallet 及状态图标) 随页面主内容**一起滚动**，而绿色渐变背景`img_exchange_bg`是底层固定的。
```kotlin
@Composable
fun ExchangeScreen(adEnable: Boolean, showWithdrawl: Boolean) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    
    Box(modifier = Modifier.fillMaxSize()) {
        // 1. 全局背景层
        Image(
            painter = painterResource(id = R.drawable.img_exchange_bg),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)
        )
        Box(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.33f).background(Color.White).align(Alignment.BottomCenter)
        )

        // 2. 滚动内容层
        val scrollState = rememberScrollState()
        Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
            
            // 2.1 随页滚动的 TopBar
            Box(modifier = Modifier.fillMaxWidth().height(48.dp).padding(top = 8.dp)) {
                Text(
                    text = "My Wallet", 
                    fontSize = 20.sp, 
                    fontWeight = FontWeight.SemiBold, 
                    color = Color(0xFF0D120E),
                    modifier = Modifier.align(Alignment.Center)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CoinBalanceView()
                    if (showWithdrawl) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_exchange_faq),
                                contentDescription = "FAQ",
                                modifier = Modifier.padding(end = 10.dp).size(32.dp)
                            )
                            Image(
                                painter = painterResource(id = R.drawable.ic_exchange_record),
                                contentDescription = "Record",
                                modifier = Modifier.padding(end = 18.dp).size(32.dp)
                            )
                        }
                    } else Spacer(modifier = Modifier.weight(1f))
                }
            }
            
            // 2.2 余额卡片
            if (adEnable) {
                ExchangeAdView(modifier = Modifier.padding(top = 22.dp))
            } else {
                MyWalletView(modifier = Modifier.padding(top = 38.75.dp))
            }

            // 2.3 提现表单 或 兑换界面
            if (showWithdrawl) {
                Column(
                    modifier = Modifier
                        .padding(top = 27.dp)
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                ) {
                    WithdrawlView()
                    
                    Row(
                        modifier = Modifier.padding(top = 16.dp).align(Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.width(50.dp).height(1.dp).background(Color(0xFFEAEAEA)))
                        Text("Withdrawal Method", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFD6D6C7), modifier = Modifier.padding(horizontal = 10.dp))
                        Box(modifier = Modifier.width(50.dp).height(1.dp).background(Color(0xFFEAEAEA)))
                    }
                    
                    WithdrawMethodsView(modifier = Modifier.padding(top = 16.dp))
                    Spacer(modifier = Modifier.height(124.dp))
                }
            } else {
                RedeemFragmentView()
            }
        }
    }
}
```

#### CollectionScreen

**组件与源码映射：**
| Components | iOS 源文件 | MockData 字段 |
|------------|-------------|---------------|
| CollectionView (主页) | `CollectionView.swift` | — |
| 卡片网格 (LazyVerticalGrid) | `CardGridItem.swift` | `cards`, `fragmentProgress` |
| CardDetailOverlay | `CardDetailOverlay.swift` | `cards`, `cardEvents` |
| CollectionIntroOverlay | `CollectionIntroOverlay.swift` | — |

**UI 骨架复刻指南 (Compose)：**
> 还原核心点：`My Collection` 的 Header 是**悬浮置顶(ZStack top)**的，不随内容滚动！因此 `LazyVerticalGrid` 必须增加顶部内边距避让 Header。
```kotlin
@Composable
fun CollectionScreen() {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    // iOS 按 375pt 基准计算高度 106pt
    val headerHeight = 106.dp * (screenWidth.value / 375f)

    Box(modifier = Modifier.fillMaxSize()) {
        
        // 1. 底层独立滚动的卡片网格
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            contentPadding = PaddingValues(
                start = 20.dp, 
                end = 20.dp,
                top = headerHeight + 22.dp, // 核心预留: 避让吸顶 Header
                bottom = 63.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            ),
            modifier = Modifier.fillMaxSize().background(Color.White)
        ) {
            items(16) { index ->
                CardGridItem()
            }
        }

        // 2. 顶层绝对定位的吸顶 Header
        Box(
            modifier = Modifier.fillMaxWidth().height(headerHeight).align(Alignment.TopCenter)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_collection_top_bg),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )
            
            Row(
                modifier = Modifier.fillMaxWidth().align(Alignment.Center).statusBarsPadding(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Collection", 
                    fontSize = 20.sp, 
                    fontWeight = FontWeight.SemiBold, 
                    color = Color.White
                )
                // iOS frame(width: 42, height: 20) with image 20x20
                IconButton(
                    onClick = { /* onShowCollectIntro() */ }, 
                    modifier = Modifier.size(width = 42.dp, height = 20.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_collection_info), 
                        contentDescription = "Info", 
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
```

---

### 独立页面

| 屏幕 | iOS 源文件 | MockData 字段 |
|------|-----------|--------------|
| SplashScreen | `SplashView.swift` | — (Lottie: `SplashBackground`, `SplashForeground`) |
| BeginnerGuideScreen | `BeginnerGuideView.swift` | — |
| GenderSelectionScreen | `GenderSelectionView.swift` | `mockUser` |
| BirthdaySelectionScreen | `BirthdaySelectionView.swift` | `mockUser` |
| HeightSelectionScreen | `HeightSelectionView.swift` | `mockUser` |
| WeightSelectionScreen | `WeightSelectionView.swift` | `mockUser` |
| LessonDetailScreen | `LessonDetailView.swift` | `sampleLessons`, `sampleMountActions` |
| ActionInstructionScreen | `ActionInstructionView.swift` | `sampleActions` |
| FollowAlongScreen | `FollowAlongView.swift` | `sampleMountActions` (ExoPlayer 视频) |
| RestScreen | `RestPageView.swift` | — |
| WorkoutResultScreen | `WorkoutResultView.swift` | — |
| RunningScreen | `RunningView.swift` | `runningRecords` (Google Maps) |
| RunningResultScreen | `RunningResultView.swift` | `runningRecords` |
| RunningDetailScreen | `RunningDetailView.swift` | `runningRecords`, `runningPoints` |
| LocationPermissionScreen | `LocationPermissionView.swift` | — |
| StepCounterScreen | `StepCounterView.swift` | `stepHistory` |
| MineScreen（个人中心） | `MineView.swift` | `bestRecord`, `runningRecords` |
| SettingsScreen | `SettingsView.swift` | — |
| RecentActivitiesScreen | `RecentActivitiesView.swift` | `runningRecords` |
| WeightChartScreen | `WeightChartView.swift` (Vico) | `weightRecords` |
| WebGameScreen | `WebView.swift` | — |

---

### 全局 Overlay / Dialog

| 组件 | Lottie 动画 | 触发场景 |
|------|------------|---------|
| SignInView (签到日历弹窗) | — | Tab0/1 触发 |
| UnifiedCoinArrivedDialog | `CoinArrivedDialog` | 任务完成/签到/广告奖励 |
| RewardPopupOverlay (碎片到账) | — | 碎片兑换 |
| EarnRulesDialog | — | 首页右上角规则按钮 |
| DailyUnlockPackDialog | — | 课程详情页 |
| SpinWheelView (转盘) | `SpinBorder`, `SpinLightAward`, `SpinNowButton`, `SpinWheelResult` | 任务/签到触发 |
| FlipCardOverlayView (翻牌) | — | 任务/兑换触发 |
| DeleteRecordDialog | — | 跑步详情长按 |
| AddWeightDialog | — | 体重图表页 |

---

## 3. 公共组件

| 组件 | 说明 |
|------|------|
| `CommonLottieView(animationName, loop)` | Lottie 播放器，路径: `assets/lottie/{name}/data.json` |
| `CommonLoadingView` | 全局 loading 圈 |
| `CommonErrorView` | 网络错误空态 |
| `CommonEmptyView` | 数据为空态 |
| `CoinBalanceView(showExchange)` | 金币余额 + 可选 Redeem 按钮（TopBar 组件） |
| `AdGamePlayButton` | 右上角广告游戏入口 |
| `RulesButton` | 右上角赚币规则按钮 (`ic_earn_rules`) |
| `MineButton` | 右上角个人中心入口 (`ic_personal`) |

---

## 4. 图标资源映射

### 4.1 TabBar 图标

| iOS imageset | Android drawable |
|-------------|-----------------|
| `ic_home_tab_training` | `R.drawable.ic_home_tab_training` |
| `ic_home_tab_task` | `R.drawable.ic_home_tab_task` |
| `ic_home_tab_running` | `R.drawable.ic_home_tab_running` |
| `ic_home_tab_exchange` | `R.drawable.ic_home_tab_exchange` |
| `ic_home_tab_collection` | `R.drawable.ic_home_tab_collection` |

### 4.2 TopBar & 通用图标

| iOS imageset | Android drawable |
|-------------|-----------------|
| `ic_coin` | `R.drawable.ic_coin` |
| `ic_earn_rules` | `R.drawable.ic_earn_rules` |
| `ic_personal` | `R.drawable.ic_personal` |

### 4.3 任务图标

| iOS imageset | Android drawable |
|-------------|-----------------|
| `ic_task_chest_0` ~ `ic_task_chest_3` | `R.drawable.ic_task_chest_*` |
| `ic_task_running` | `R.drawable.ic_task_running` |
| `ic_task_training` | `R.drawable.ic_task_training` |
| `ic_task_sign_in` | `R.drawable.ic_task_sign_in` |
| `ic_task_spin` | `R.drawable.ic_task_spin` |
| `ic_task_crack_egg` | `R.drawable.ic_task_crack_egg` |
| `ic_task_slot` | `R.drawable.ic_task_slot` |
| `ic_task_notification` | `R.drawable.ic_task_notification` |
| `ic_task_step` | `R.drawable.ic_task_step` |
| `ic_task_relax` | `R.drawable.ic_task_relax` |

### 4.4 个人中心图标

| iOS imageset | Android drawable |
|-------------|-----------------|
| `ic_best_distance` | `R.drawable.ic_best_distance` |
| `ic_best_duration` | `R.drawable.ic_best_duration` |
| `ic_best_speed` | `R.drawable.ic_best_speed` |
| `ic_best_calories` | `R.drawable.ic_best_calories` |

### 4.5 收集卡片 & 碎片（16 张）

| 卡片图 | 碎片图 |
|--------|--------|
| `R.drawable.img_card_fruit` | `R.drawable.img_fragment_fruit` |
| `R.drawable.img_card_doll` | `R.drawable.img_fragment_doll` |
| `R.drawable.img_card_bottle` | `R.drawable.img_fragment_bottle` |
| `R.drawable.img_card_battery` | `R.drawable.img_fragment_battery` |
| `R.drawable.img_card_rock` | `R.drawable.img_fragment_rock` |
| `R.drawable.img_card_flower` | `R.drawable.img_fragment_flower` |
| `R.drawable.img_card_forest` | `R.drawable.img_fragment_forest` |
| `R.drawable.img_card_sweat` | `R.drawable.img_fragment_sweat` |
| `R.drawable.img_card_sun` | `R.drawable.img_fragment_sun` |
| `R.drawable.img_card_breeze` | `R.drawable.img_fragment_breeze` |
| `R.drawable.img_card_rain` | `R.drawable.img_fragment_rain` |
| `R.drawable.img_card_black_cat` | `R.drawable.img_fragment_black_cat` |
| `R.drawable.img_card_drunk_cat` | `R.drawable.img_fragment_drunk_cat` |
| `R.drawable.img_card_spotted_dog` | `R.drawable.img_fragment_spotted_dog` |
| `R.drawable.img_card_poodle` | `R.drawable.img_fragment_poodle` |
| `R.drawable.img_card_warmth` | `R.drawable.img_fragment_warmth` |

### 4.6 跑步事件图片

| iOS imageset | Android drawable |
|-------------|-----------------|
| `img_event_pick_rubbish` | `R.drawable.img_event_pick_rubbish` |
| `img_event_wipe_sweat` | `R.drawable.img_event_wipe_sweat` |
| `img_event_look_sky` | `R.drawable.img_event_look_sky` |
| `img_event_pet_cat` | `R.drawable.img_event_pet_cat` |
| `img_event_pet_dog` | `R.drawable.img_event_pet_dog` |
| `img_event_say_hello` | `R.drawable.img_event_say_hello` |

---

## 5. Lottie 动画清单

> 路径: `assets/lottie/{Name}/data.json`

### P0（首版必须）

| 名称 | 用途 |
|------|------|
| `SplashBackground` | 启动页背景 |
| `SplashForeground` | 启动页前景 |
| `CoinArrivedDialog` | 金币到账弹窗 |
| `AddCoinEffect` | 金币增加特效 |
| `WithdrawCodeDialog` | 提现兑换码弹窗 |

### P1（后续添加）

| 名称 | 用途 |
|------|------|
| `CoinArrivedWithProgressDialog` | 带进度的金币到账 |
| `RunningEventPickRubbish` | 跑步事件: 捡垃圾 |
| `RunningEventWipeSweat` | 跑步事件: 擦汗 |
| `RunningEventLookSky` | 跑步事件: 看天 |
| `RunningEventPetCat` | 跑步事件: 撸猫 |
| `RunningEventPetDog` | 跑步事件: 遛狗 |
| `RunningEventSayHello` | 跑步事件: 打招呼 |
| `RunningEventPickCoin` | 跑步事件: 捡金币 |
| `RunningEventPickCoinPrelude` | 捡金币前奏 |
| `SpinBorder` / `SpinLightAward` / `SpinNowButton` / `SpinWheelResult` / `SpinWheelResultBtn` | 转盘系列 |
| `SmashEggEntrance` / `SlotEntrance` | 砸蛋/老虎机入口 |
| `TrainingPackage` | 训练礼包 |
| `WebViewLine` / `WebViewRunning` | WebView 系列 |
| `DailyCourseGift` | 每日课程礼包 |

---

## 6. SwiftUI → Compose 速查

| SwiftUI | Compose |
|---------|---------|
| `@State` | `remember { mutableStateOf() }` |
| `@Binding` | `(T) -> Unit` lambda |
| `NavigationProxy.path.append(route)` | `navController.navigate(route)` |
| `@Environment(\.dismiss)` | `navController.popBackStack()` |
| `.onAppear` | `LaunchedEffect(Unit) {}` |
| `.onChange(of:)` | `LaunchedEffect(value) {}` |
| `fullScreenCover` | `Dialog` / 全屏 Composable |
| `.overlay` / `ZStack` | `Box` 叠层 |
| `.sheet` | `ModalBottomSheet` |
| `ScrollView + LazyVStack` | `LazyColumn` |
| `GeometryReader` | `BoxWithConstraints` / `Modifier.onSizeChanged` |
| `AsyncImage (Kingfisher)` | `AsyncImage (Coil)` |
| `Timer.publish` | `delay()` in coroutine |
| `TabView(selection:)` | `HorizontalPager` 或 直接切换 Composable |

---

## 7. 资源迁移快速参考

| 资源类型 | 来源 | 目标 |
|---------|------|------|
| 图片 (321 张) | `Assets.xcassets/` → `@2x`/`@3x` | `drawable-xhdpi/` / `drawable-xxhdpi/`，snake_case 命名 |
| Lottie (27 个) | `Resources/Lottie/` | `assets/lottie/{Name}/data.json` |
| 字体 | `Resources/Fonts/Inter-BlackItalic.otf` | `res/font/inter_black_italic.otf` |
| 本地化 | `Resources/Localizable.xcstrings` (JSON) | `res/values/strings.xml` + `res/values-{locale}/strings.xml` |
