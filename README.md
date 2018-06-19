# YY-VideoPlayer

#### 项目介绍
基于IJKPlayer 的视频播放器 UI引用开源方案，主要对播放器底层源码进行开发。提供相应的使用接口

#### 软件架构
* **Player  播放内核层**：IjkMediaPlayer、ExoPlayr2、MediaPlayer（IPlayerManager）。
* **Manager 内核管理层**：VideoBaseManager（VideoBaseManager <- VideoViewBridge）。
* **GL      渲染控件控件层**：TextureView、SurfaceView、GLSurfaceView（BaseTextureRenderView <-VideoGLViewBaseRender）。
* **Render  渲染控制层**：BaseTextureRenderView、BaseVideoView、BaseVideoPlayer。
* **UI      UI控件层**：VideoControlView、VideoPlayer、StandardVideoPlayer。
#### 结构如下图：

![框架图](https://github.com/ybj366533/YY-VideoPlayer/blob/master/VideoPalyer_Structure.jpg)


#### 应用接口文档

* #### [1、 基础播放器应用     VideoPlayer]
* #### [2、 播放器接口        VideoPlayer-API]
* #### [3、 播放器全局设置    VideoType-API]
* #### [4、 播放器管理器      VideoPlayer-API]
* #### [5、 播放器应用回调     VideoAllCallBack]
* #### [6、 重力感应工具       OrientationUtils]


#### 应用混淆

```
-keep class tv.danmaku.ijk.** { *; }
-dontwarn tv.danmaku.ijk.**

-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
```

