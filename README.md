# WanAndroid-RedRockWinterWorkQt

# 更新日志：
v0.1.1: 更新启动页，加了点动画特效

v0.1.2: 更新主界面内容，界面设计借鉴掘金app

- v0.1.2.1：
  - 主界面组成：Toolbar+TabLayout+ViewPager（MaterialDesign）

    - v0.1.2.1 效果图：

    <img src="screenshot\v0.1.2.1.jpg" alt="v0.1.2.1" width="200" height="440" />

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

- v0.1.2.2：更新首页的轮播图

  - 涉及到的技术：HttpUrlConnection方法封装，RecyclerView+PagerSnapHelper实现轮播图，Handler的使用，Fragment生命周期的理解，动态添加View。效果图：

    <img src="screenshot\v0.1.2.2.gif" alt="v0.1.2.1" width="200" height="440" />

  - HttpUrlConnection方法封装：

    - 由于之前作业里写过一次，这里对GET请求方法的封装基本没有问题。最大也是最致命的一个问题就是对于Thread和Handler的理解，之前我一直把线程和主函数方法的执行顺序理解为的先后顺序，后来经理了无数个的令人crazy的bug后才恍然大悟，Http请求是在子线程中进行，它不会影响RecyclerView初始化的过程，这里就会产生一个<font color=red>`FATAL EXCEPTION: divide by zero`</font>。错误的发生位置是我在加载图片Position时的代码，因为轮播图是无限多的，所以在adapter里`getItemCount()`方法返回的是`Integer.MAX_VALUE`，所以在获取bannerList中的位置用的是`bannerList.get(position % bannerList.size())`，**然而此时线程中的网络请求还未执行完，所以bannerList.size()为0**。因此需要在`getItemCount()`方法中添加判断`if(bannerList.size == 1) return 0; else if(bannerList.size() == 1) return 1;else return Integer.MAX_VALUE`。
    
  - Banner轮播图的实现：(Ps:这个好坑，写个简单的轮播图我被坑吐了🤮，**一堆坑**)

    - **首先说一下谷歌提供的SnapHelper**，其子类包括LinearSnapHelper和PagerSnapHelper，它可以让我们通过`snapHelper.attachToRecyclerView()`方法**使得RecyclerView变的像ViewPager一样**一次只能滑动一个或多个Item。这个没啥好说的，需要注意的是要在重写的`findTargetSnapPosition()`方法中更新mCurrentIndicatorPosition的数值和指示器的位置。
    - **第二个说下RecyclerView自动播放问题**。我们需要新开一个线程，通过`handler.sendEmptyMessageDelayed()`调用`rv.smoothScrollToPosition()`方法来实现rv的移动，再Handler的最后再此发送个同样的线程以实现无限轮播的效果。**在这里会有几个Bug：**
      1. 像上图中，如果我点向“项目”界面的话，再返回后会发现rv切换item的速度时快时慢，这是因为Fragment在被切换时会自动销毁(**ViewPager的缓存机制**)，但是Handler中的Message仍然存在，当我们再次返回“首页”时，Fragment会被再次创建，也即会新增一个线程，**导致两个线程同时滑动RecyclerView**。**所以我们要重写onPause()方法**，再其中加上`handler.removeMessage()`方法，使得Fragment在被销毁时将更新RecyclerView的线程也被移除。
      2. 上面那个Bug虽被解决了，但是新的Bug又出现了。但我把手机锁屏后再打开时，RecyclerView就停止更新了。这是因为在锁屏后会调用`onPause()`方法，但同时在解锁后会调用Fragment的`onResume()`方法重新绘制界面，所以我们需要在onResume()中添加判断，`if(!handler.hasMessage()) handler.sendEmptyMessageDelay()`。
      3. 参考了一些前辈们的写法，我新增了一个**isAutoPlay的全局布尔变量**，在onResume中设置其为true，在onPause中设置其为false，在Handler中加一个判断，如果isAutoPlay为true则滑动界面（但是不管isAutoPlay是否为true，只要Handler存在它都要重复执行），然后为RecyclerView添加滑动监听事件`addOnScrollListener()`，在其滑动的时候设置isAutoPlay为false，滑动结束后设置isAutoPlay为true。
      4. 最后也是最让我头疼的一个Bug，但没想到它可以很轻松地解决。就是我们在一开始加载Banner的时候得让它`scrollToPosition(1000*bannerList.size())`，避免用户刚进入app时向左滑动Banner为空的情况。可是这个方法不能直接放在Fragment的`onCreateView()`方法中，因为只有当BannerList加载完成后我们才能顺利移动Position，因此要把初始化并移动位置的代码**放到加载Banner网络图片的Handler中**去，加载完成后再移动。
    - **最后就是指示器的问题了**。要在fragment中新加一个LinearLayout布局为指示器的容器（我一开始把它加到了RecyclerView的Item布局中去了，最后代码中find不了它的id）。初始化Indicator：通过for循环新增View`indicatorContainer.addView(view)`。改变Indicator状态：先循环将所有childView设置为花白色，然后再将`mCurrentBannerPosition % bannerList.size()`位置的View设置为桃红色。除了这些还需要注意的是addIndicator()和setIndicator()都要和上面自动播放的第四个bug一样放到加载Banner图片的Handler中去才能生效，否则会报错提示找不到childView。

  - 感想：其实写完后发现轮播图实现起来其实不难，只要思路清晰的话，主要比较麻烦的是一些细节以及处理细节需要掌握的知识，例如对Handler和Fragment生命周期的理解，由于对Handler机制理解的模糊导致自己走了很多看起来sb而且不必要的坑，**基础果然很重要鸭😭**。
  
  - **再次更新**：一觉醒来突然想起了mvp架构或许能够优化代码。于时用了一个上午去学mvp，最后写出了一个简陋版的mvp架构来网络请求Banner数据。
  
    - M：Model层，即业务层，用于发送网络请求并处理请求后得到的JSON数据，发送给Presenter层。
    - P：Presenter层，即传递层（中间层），接受Model层传递过来的数据，并通知View层更新。
    - V：View层，即视图层，主要负责更新UI，对于要处理的数据都交给Presenter来传送给Model处理，最终得到处理后的结果并更新界面。
    - **MVP的核心是**：View层不持有Model层对象的引用，只持有Presenter层对象的引用，任何需要操作数据的行为都要委托给Presenter层，而Model层也是无法直接操作View层的，也只能委托Presenter层。Presenter层持有View对象的引用，除此之外不持有任何其他UI控件的引用。Model会把更新View的操作委托给Presenter层，而Presenter层会把更新View的操作交给View层对象去操作。

