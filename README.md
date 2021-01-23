# WanAndroid-RedRockWinterWorkQt

# 更新日志：
v0.1.1: 更新启动页，加了点动画特效

v0.1.2: 更新主界面内容，界面设计借鉴掘金app

- 主界面组成：Toolbar+TabLayout+ViewPager

  - v0.1.2.1 效果图：

    <img src="screenshot\v0.1.2.1.jpg" alt="size" style="zoom:25%;" />

- Toolbar：

  - **上下滑动时对应搜索框的显示与隐藏**：在Toolbar外面套层`AppBarLayout`，并将Toolbar的`app:layout_scrollFlags`属性设置为scroll|enterAlways|snap。
  - **自定义Toolbar内容**：Toolbar继承自ViewGroup，所以可以在其内部添加子布局。如图显示，左边为EditText，右边为一个自定义的圆形ImageView。
    - EditText需要设置为不可编辑`android:focusable="false"`,但这样也会出现一个**Bug**：长按EditText时可以选择粘贴内容，所以还需要设置一个属性`android:enabled="false"`。
    - CircleImageView是用BitmapShader进行裁剪的，文件里有各个步骤的详细注释。
  - **Toolbar内容要与TabLayout完美融合**：取消Toolbar的阴影，在AppBarLayout布局中加入`app:elevation="0dp"`属性，并且将Toolbar与TabLayout的背景颜色都设置为白色。
  - **设置状态栏颜色**：由于Toolbar背景为白色，状态栏背景也为白色，会导致图标看不清，所以通过设置`View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR`属性来设置状态栏图标为黑色，*不过这个方法只适用于Android5.0以上的手机*。
  - **坑-Toolbar左边会空出来一部分区域**：百度之后发现是由于`contentInsetStart`属性引起的ActionBar不能完全填充的原因。于是在Toolbar中加入属性`app:contentInsetStart="0dp"`即可去掉空白。

- NestedScrollView：

  - `activity_main.xml`最外层的布局为`CoordinatorLayout`，要在NestedScrollView中添加行为属性与Toolbar的行为对应：`app:layout_behavior="@string/appbar_scrolling_view_behavior"`。
  - 设置`android:fillViewport="true"`属性以保证ScrollView可以全屏显示。
