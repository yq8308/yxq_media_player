package com.test.yxq.yxq_media_player.utils;

import android.view.SurfaceHolder;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by Administrator on 2016/5/1.
 */
public class IjkMediaPlayerDelegate {

    public static IjkMediaPlayer mMediaPlayer;

    public static void setMediaPlayer(IjkMediaPlayer mp) {
        if (mMediaPlayer != null && mMediaPlayer != mp) {
            if (mMediaPlayer.isPlaying())
                mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mMediaPlayer = mp;
    }

    public static IjkMediaPlayer getMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = createMediaPlayer();
        }
        return mMediaPlayer;
    }

    public static IjkMediaPlayer createMediaPlayer() {
        IjkMediaPlayer mediaPlayer = new IjkMediaPlayer();
        //set default options
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-buffer-size", 50000);   //设置最大缓存大小byte
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "min-frames", 25);         //设置最少保存的帧数
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);  //硬解
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0);
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_YV12);
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);   //是否允许丢帧，当cpu效率低下的时候
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0); //设置是否自动播放，当底层prepare完成后，这里设置为0非自动播放，由自己start启动
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
        return mediaPlayer;
    }

    public static void openVideo(IjkMediaPlayer mediaPlayer, SurfaceHolder holder, String dataSource) {
        try {
            mediaPlayer.setDataSource(dataSource);
            mediaPlayer.setDisplay(holder);
            /**
             * prepareAsync() 流程说明：
             * _prepareAsync(native方法，后面进入c中的函数调用)->IjkMediaPlayer_prepareAsync->ijkmp_prepare_async->ijkmp_prepare_async_l->ffp_prepare_async_l(这个里面会打印一些log信息
             *   av_log(NULL, AV_LOG_INFO, "===== versions =====\n");
             *   ffp_show_version_str(ffp, "FFmpeg",         av_version_info());
             *   ffp_show_version_int(ffp, "libavutil",      avutil_version());
             *   ffp_show_version_int(ffp, "libavcodec",     avcodec_version());
             *   ffp_show_version_int(ffp, "libavformat",    avformat_version());
             *   ffp_show_version_int(ffp, "libswscale",     swscale_version());
             *   ffp_show_version_int(ffp, "libswresample",  swresample_version());
             *   av_log(NULL, AV_LOG_INFO, "===== options =====\n");
             *   ffp_show_dict(ffp, "player-opts", ffp->player_opts);
             *   ffp_show_dict(ffp, "format-opts", ffp->format_opts);
             *   ffp_show_dict(ffp, "codec-opts ", ffp->codec_opts);
             *   ffp_show_dict(ffp, "sws-opts   ", ffp->sws_dict);
             *   ffp_show_dict(ffp, "swr-opts   ", ffp->swr_opts);
             *   av_log(NULL, AV_LOG_INFO, "===================\n");  )
             *   接着，stream_open,
             *   创建一个显示线程SDL_CreateThreadEx(&is->_video_refresh_tid, video_refresh_thread, ffp, "ff_vout"); 会调用video_refresh方法：called to display each frame
             *   创建一个解码线程SDL_CreateThreadEx(&is->_read_tid, read_thread, ffp, "ff_read"); read_thread ：this thread gets the stream from the disk or the network
             */
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void destroy() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying())
                mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            IjkMediaPlayer.native_profileEnd();
        }
    }

    public static void play() {
        if (mMediaPlayer != null) {
            if (!mMediaPlayer.isPlaying())
                mMediaPlayer.start();
        }
    }

    public static void pause() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying())
                mMediaPlayer.pause();
        }
    }

    public static boolean isPlaying() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying())
                return true;
        }
        return false;
    }
}
