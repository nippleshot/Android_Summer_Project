# 《Android应用开发》 大作业说明文档

## * 项目的特点，创新点：

1. 通过使用Exoplayer来实现了视频播放

2. 本APP看视频或目录都支持screen orientation

3. 在‘最热视频’目录Fragment(FragmentAll.java)上显示RecycleView的时候，添加了视频的信息

4. 用户在‘最热视频’目录Fragment上滑动(Scroll Down或者Up)之后找到l了有兴趣的视频，暂时不滑动的话，那么用户可以看到正在屏幕上显示出的视频自动播放的的预览（没有声音）

   

5. 用户在‘最热视频’目录Fragment上（向右➡️）滑可以收藏自己喜欢的视频，（向左⬅️）滑可以删掉不喜欢的视频（也可以取消删除）

   <img width="234" alt="one" src="https://user-images.githubusercontent.com/44460142/91115259-cae1de00-e6c4-11ea-8123-da5c647f8db0.png" style="zoom: 33%;">
     <img width="236" alt="two" src="https://user-images.githubusercontent.com/44460142/91115264-cf0dfb80-e6c4-11ea-901a-28e7c0f45a4a.png" style="zoom: 33%;">

6. 在‘我的视频’目录Fragment(FragmentArchived.java)上可以看到用户在最热视频目录Fragment上（向右➡️）滑过的视频

7. ‘我的视频’目录的内容是从自建的数据库(SQLiteDatabase)获得视频信息，通过（向左⬅️）滑也可以删除收藏的视频

8. 在‘我的视频’目录上的视频，用户还没看完视频就退出视频播放Activity(ExoPlayerActivity.java)的时候，会自动保存用户看到的时间。用户下次再看的话，可以从之前看到的时间继续播放视频

   <img width="235" alt="three" src="https://user-images.githubusercontent.com/44460142/91115267-cfa69200-e6c4-11ea-819d-a265157416b1.png" style="zoom: 33%;">	

   
## * 项目过程中遇到的难点以及解决方案：

- 实现自动播放预览部分中遇到了多种问题。为了解决多种问题，我实现的代码是如下：

  #### playVideo( ) : 确定在最热视频目录上需要播放哪个视频的预览的函数

   《变量说明》

  | 类型    | 变量名                   | 内容                                                         |
  | ------- | ------------------------ | ------------------------------------------------------------ |
  | boolean | isEndOfList              | 用户正在屏幕上看到的目录item是不是最热视频目录的最后的item   |
  | int     | targetPosition           | 要播放的item是ArrayList<MediaObject> mediaObjects中第几号(index) |
  | int     | startPosition            | 用户正在屏幕上看到的目录item中最上面的item号(index)          |
  | int     | endPosition              | 用户正在屏幕上看到的目录item中最下面的item号(index)          |
  | int     | startPositionVideoHeight | 第startPosition号item正在屏幕上可以看到的高度                |
  | int     | endPositionVideoHeight   | 第endPosition号item正在屏幕上可以看到的高度                  |
  | int     | playPosition             | 正在播放中的item的item号(index)                              |

  《代码逻辑》

  A. 决定最终需要播放的item(targetPosition)的部分

  	   a 判断用户正在屏幕上看的目录item是最后的item

  		a.1 是最后的item的话，最终需要播放的item是 ArrayList<MediaObject> mediaObjects的最后一个item

  		a.2 不是最后的item

  			a.2.1 设定两个候选item(startPosition, endPosition)的item号

  				a.2.1.1 如果屏幕上看到的目录item个数是两个以上的话，设定endPosition为startPosition+1 (只需要考虑的候选item就是正在屏幕上可以看到的目录item中最上面的item和接下来把下面的item)

  			a.2.2 判断设定了两个候选item是不是一样 

  				a.2.2.1 一样，那么最终需要播放的item是第startPosition号item

  				a.2.2.2 不一样, 那么设定两个item的高度(startPositionVideoHeight, endPositionVideoHeight)。然后比较两个高度。高度更高的item就是最终需要播放的item

  B. 准备播放targetPosition号的item之前需要考虑的部分

  	   b 判断决定的targetPosition跟正在播放中的item号(playPosition)是不是一样

  		b.1 一样，那么不需要准备播放targetPosition号item, 退出这函数

  		b.2 不一样，那么设定playPosition为targetPosition

  C.  准备然后播放targetPosition号的部分

  	   c 删掉之前的播放的videoSurfaceView, 把videoSurfaceView添加到新决定的位置上。

  		c.1 然后拿到对应的viewholder信息之后播放。

  		

  《实现的代码（VideoPlayerRecyclerView.java）》

  ```java
  public void playVideo(boolean isEndOfList) {
      int targetPosition;
    // A. 决定最终需要播放的item(targetPosition)的部分
      if(!isEndOfList){
          int startPosition = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
          int endPosition = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
  
          if (endPosition - startPosition > 1) {
              endPosition = startPosition + 1;
          }
          if (startPosition < 0 || endPosition < 0) {
              return;
          }
  
          if (startPosition != endPosition) {
              int startPositionVideoHeight = getVisibleVideoSurfaceHeight(startPosition);
              int endPositionVideoHeight = getVisibleVideoSurfaceHeight(endPosition);
  
              targetPosition = startPositionVideoHeight > endPositionVideoHeight ? startPosition : endPosition;
          }
          else {
              targetPosition = startPosition;
          }
      }
      else{
          targetPosition = mediaObjects.size() - 1;
      }
    
    // B. 准备播放targetPosition号的item之前需要考虑的部分
      if (targetPosition == playPosition) {
          return;
      }
      playPosition = targetPosition;
      if (videoSurfaceView == null) {
          return;
      }
    
    // C.  准备然后播放targetPosition号的部分
      videoSurfaceView.setVisibility(INVISIBLE);
      removeVideoView(videoSurfaceView);
  
      int currentPosition = targetPosition 
        - ((LinearLayoutManager)getLayoutManager()).findFirstVisibleItemPosition();
  
      View child = getChildAt(currentPosition);
      if (child == null) {
          return;
      }
  
      VideoPlayerViewHolder holder = (VideoPlayerViewHolder) child.getTag();
      if (holder == null) {
          playPosition = -1;
          return;
      }
      thumbnail = holder.thumbnail;
      progressBar = holder.progressBar;
      volumeControl = holder.volumeControl;
      viewHolderParent = holder.itemView;
      requestManager = holder.requestManager;
      frameLayout = holder.itemView.findViewById(R.id.media_container);
  
      videoSurfaceView.setPlayer(videoPlayer);
  
      DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
              context, Util.getUserAgent(context, "RecyclerView VideoPlayer"));
      String mediaUrl = mediaObjects.get(targetPosition).getMedia_url();
      if (mediaUrl != null) {
          MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                  .createMediaSource(Uri.parse(mediaUrl));
          videoPlayer.prepare(videoSource);
          videoPlayer.setPlayWhenReady(true);
      }
  }
  ```

