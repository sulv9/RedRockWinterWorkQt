

# 待优化的地方：

1.  添加全局的加载Dialog动画（现在v0.1.2.3版本首页加载数据的时候一片空白，无加载动画）
2. 将下拉刷新和上滑加载的RecyclerView封装为SwipeRecycler类，方便后续开发（v0.1.2.3版本的首页Recycler是暴力向Adapter中添加头部和底部View，然后在Fragment中编写对应监听事件的，耦合度较高，不便于二次利用）--- 暂时无法实现😭
3. 添加TabLayout指示器动画，Indicator随着手指的滑动有一个变长再变短的效果（可能需要通过反射重写Indicator效果，有些麻烦）
4. 实现缓存预加载内容，每次网络请求完成后通过Sp保存数据，下次进入app时如果当前没有最新数据，则加载上一次保存过的数据，提高用户体验感
5. 在首页文章的Item中为其添加自定义的分类标签TextView（如“最新”， “置顶”， “项目”）

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

    <img src="screenshot\v0.1.2.2.gif" alt="v0.1.2.2" width="200" height="440" />

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
    - 还有一个要注意的点，网络请求完数据在View中更新UI时需要开启新的线程，这里需要把得到Bean数据作为`message.setData()`传输出去，所以Bean需要使用Parcelable，然后让`bundle.putParcelableArrayList()`，再在线程中`message.getData()`得到Bean数组。
  
- v0.1.2.3: 

  - **2020.1.30** 更新首页文章列表，为RecyclerView的Adapter添加了四个部分的View，分别是REFRESH_VIEW(下拉刷新View)，BANNER_VIEW（轮播图View），ARTICLE_VIEW（文章列表View），FOOTER_VIEW（底部加载View）。效果图：

    <img src="screenshot\v0.1.2.3.gif" width="200" height="440" />

  - 首先要将BannerRecycler添加到文章Recycler的Adapter中，也即IndexFragment中只有一个RecyclerView。需要先在onCreateViewHolder()和getItemType()两个方法中加载BannerView及初始化指示器，然后onBindViewHolder()中给rvBanner初始化一些滑动监听事件，并在Adapter里创建一个Banner接口，在onBindView中回调接口使Banner自动滚动，在IndexFragment中添加接口监听事件。

  - 然后在头部添加刷新View，在底部添加加载View。刷新View需要为rvArticle设置触摸事件(setOnTouchListener())，在DOWN中记录下手指坐标，在MOVE中判断滑动距离，**核心思想是通过改变refreshView的topMargin来实现下拉显示刷新View的**。底部加载View是通过recycler的滑动监听事件(addOnScrollListener())实现，如果recycler处于滑动或者闲置状态时，通过其layoutManager判断是否为最后一条Item，如果是则显示FooterView，并开启线程加载下一页的数据，在加载完成后在让其消失。

  - 其他的没啥好说的了，因为要在adapter里面额外添加三个不同的view，还要设置各自不同的监听事件，中间遇到的Bug数都数不清了，这里说两个我记得比较清楚的，**第一个是设置recycler的TouchListener来获取Y轴坐标的**，这个要注意一开始getRawY()放在哪里，这里我参考网上放在了最外面，但如果放到DOWN里面获取的话会出一些问题，需要提前return true，这是个说不清的坑，具体的原因我也没搞太懂。第二个是在切换界面的时候adapter会被绑定到两个recyclerview上导致`FATAL EXCEPTION: divide by zero`，也就是每次在获取Banner的线程中都要对adapter进行初始化才行，不能添加if(adapter == null)，这个具体解决方案参考 第 #2796 issue，https://github.com/CymChad/BaseRecyclerViewAdapterHelper/issues/2796.
  
- v0.1.2.4更新：

  - 此次更新主要涉及到两层TabLayout监听事件的设置，以及解析相应的网络请求数据。（这点用MVP架构是真的香啊，处理数据的请求直接由Presenter提交了，只需要把回调事件写好就可以了。）效果图：

    <img src="screenshot\v0.1.2.4.gif" width="200" height="440" />

  - 这里基本没啥技术难度，就只需要为TabLayout`addOnTabSelectedListener`就可以了，其中加载数据的时候可以通过`tab.setTag()`这个方法储存每个Tab对应的数据(Bean)，然后在监听事件中通过Tag获取每页文章对应的Id，拼接在URL后面再发送更新Recycler列表的请求就OK了。

  - 比较坑的是，我花了一个下午想来封装一个可以上滑加载下拉刷新(下拉刷新要自定义，不用Google提供的SwipeLayout，那个刷新感觉太丑了)的RecyclerView，结果呢，我大意了呀，没有料到这么复杂，结果写了一个下午白写了。理论上来讲上滑加载和下拉刷新的代码基本上都是一样的操作，完全可以剖离出来，可是由于RecyclerView这个控件的特殊之处，我们不能直接写一个View继承自RecyclerView，我看网上好多大佬都是自定义一个继承自LinearLayout或者FrameLayout的View，然后内部新建一个RecyclerView变量，同时还要提供一些RecyclerView常用到的方法的接口，然后还要写一个**配套的Adapter**，因为顶部刷新和底部加载的两个布局都属于Recycler的两个特殊Item，需要LayoutManager，RecyclerView.Adapter以及RecyclerView三者的配合才能写出来，我感觉如果要达到我想要的这个效果的话，工程量会非常大😂。所以就暂时避开了这个坑，直接再次对体系界面里面的RecyclerView重写`onTouchListener()`事件实现下拉刷新功能。

  - 不过这次重写下拉刷新功能的时候感觉自己的代码变得精简了一些，相比与`IndexFragment.java`中的冗杂一堆让人看着就头疼的代码，这次`TreeFragment.java`中的代码写的更熟练，思路更清晰了一些，通过将多行代码分离成函数使得代码整体更加美观了些，也更加明确了从网络请求到加载数据这个过程中各个部分的分工。
  
- v0.1.2.5更新

  - 主要涉及到的技术有流式布局FlowLayout，左右RecyclerView双列表联动。效果图：

    <img src="screenshot\v0.1.2.5.gif" width="200" height="440" />

  - FlowLayout。这个是参照鸿洋大神14年的博客来写的，总体不算太难，但是需要考虑一些细节，例如换行时第一个childView也需要被测量，捕捉。还有在onLayout中每获取完一行lineViews后，需要将其重置，`lineViews = new ArrayList<>()`，注意不能使用`lineViews.clear()`这样上一行保存的数据也会被清理掉。

  - 然后是把FlowLayout添加到右侧Recycler中去，需要在Adapter里对FlowLayout添加子View，这里需要注意一点就是在onBindViewHolder中每次需要先**removeAllViews()**。

  - 第三点，左侧Recycler联动右侧Recycler。实现点击左侧Item右侧Item滑动到相应位置，这里直接写个点击接口然后smoothScrollToPosition就可以了，但是如果值属这样写的话**到后面第四点的时候会出现很多很多莫名其妙的Bug**。

  - 第四点，右侧Recycler联动左侧。但右侧Recycler滑动的特定位置的时候左侧Recycler跟着相应滑动。

    - 重写右侧Recycler的滑动监听事件，通过LayoutManager获取第一个可见的Item位置，然后让左侧Recycler滑到相应的位置（这里左侧Recycler的滑动单独写了一个方法，使得右侧每次滑动时左侧Recycler被选中的Item都能处于中间位置，借助`recyclerView.scrollBy()`函数计算出每次position到中间Item的距离，需要用到getTop()）。
    - 然后要在Bean中添加一个isSelected属性，在左边的Adapter的onBindViewHolder里面判断，如果isSelected为true则设置红色，否则设置为灰色。
    - 然后回到右边的滑动监听事件，在左边滑动到相应位置之后，设置选中Item的isSelected为true，未选中的为false，然后通知adapter更新数据。
    - 然后就是第三点提到的Bug了，有很多很多，我就只说最后比较关键的吧，这个是因为我滑动监听事件是在onScroll()里面写的，所以每次点击滑倒对应位置时会出现其他Item也被选中的情况，所以我们需要两个全局变量监听点击结果，一个为isClickMoving记录现在右侧Recycler是否为点击的滑倒，如果为true，onScroll()里面的方法就不执行。另一个变量为clickPosition记录点击Item的位置，然后我们还需要重写右侧滑倒监听事件的`onScrollStateChanged()`方法，如果isMoving为true并且此时处于闲置状态，设置isMoving为false并且设置上一个点击的Item为未选中状态，然后更新`lastItemPosition = clickPosition`。到此左右联动的Recycler差不多就结束了，不过其实中间真的会出现很多很多奇怪的Bug。

  - 感想：写代码能力加强了，相比于写首页Banner，并且把Banner添加到RecyclerView的适配器中去，最后还要在里面加个下滑刷新上拉加载功能，这些我一开始写了一个星期，但是这次的双列表联动Recycler其实网上的资料并不多，而且还不是我想要的那种效果，虽然花了两天时间才写出个勉勉强强可以看的成果，但还是有些菜了，不过比一开始进步了不少。还要我这三个界面一直都在围绕着RecyclerView来写，所以对Recycler的缓存理解也加深了不少。
  
- v0.1.2.6更新问答界面，很简单没啥好说的，由于写首页的基础，这页很快就写完了，只有一个Recycler+刷新和加载Item。

  效果图：

  <img src="screenshot\v0.1.2.6.gif" width="200" height="440" />
  
- v0.1.2.7更新项目和公众号界面，和前面的体系界面差不多，套模板cv很快，只需要改个网址就可以了。

  效果图：

  <img src="screenshot\v0.1.2.7.gif" width="200" height="440" />

  
