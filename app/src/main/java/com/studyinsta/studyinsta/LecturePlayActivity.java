package com.studyinsta.studyinsta;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.studyinsta.studyinsta.classes.YoutubePlayerConfig;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LecturePlayActivity extends YouTubeBaseActivity {

    private WebView webView;
    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer.OnInitializedListener onInitializedListener;
    private String lectureUrl;
    public static Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_lecture_play);

        ///loading Dialog
        loadingDialog = new Dialog(LecturePlayActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        /////end loading dialog
        //URL intent se pass karna
        Intent urlIntent = getIntent();
        lectureUrl = urlIntent.getStringExtra("course_lecture_url");

        youTubePlayerView = findViewById(R.id.lecture_video_player);
        webView = findViewById(R.id.vimeoWV);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setWebChromeClient(new ChromeClient());

        if (lectureUrl.length() == 9) {

            //vimeo webview
            youTubePlayerView.setVisibility(View.INVISIBLE);
            webView.setVisibility(View.VISIBLE);

            String webContent = "<iframe src=\"https://player.vimeo.com/video/" + lectureUrl + "\" width=\"100%\" height=\"100%\" frameborder=\"0\" allow=\"autoplay; fullscreen; picture-in-picture\" allowfullscreen></iframe>\n";
            if (webContent.contains("iframe")) {

                Matcher matcher = Pattern.compile("src=\"([^\"]+)\"").matcher(webContent);
                matcher.find();
                String src = matcher.group(1);
                webContent = src;

                try {
                    URL myURL = new URL(src);
                    webView.loadUrl(src);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }


        } else {
            youTubePlayerView.setVisibility(View.VISIBLE);
            webView.setVisibility(View.INVISIBLE);


            //Youtube Player API Code
            onInitializedListener = new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                    youTubePlayer.loadVideo(lectureUrl);
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                }
            };

            youTubePlayerView.initialize(YoutubePlayerConfig.API_KEY, onInitializedListener);


        }


    }


//        webView.setWebViewClient(new WebViewClient(){
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                loadingDialog.show();
//                super.onPageStarted(view, url, favicon);
//            }
//
//            @Override
//            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//                loadingDialog.dismiss();
//                super.onReceivedError(view, request, error);
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                loadingDialog.dismiss();
//                super.onPageFinished(view, url);
//            }
//        });
//        webView.loadData(finalHtmlUrl, "text/html", "utf-8");
//


//Paste below code outside the onCreate Method


    //    }
//    private int setDp(int dp, Context context) {
//        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
//    }
//
    private class ChromeClient extends WebChromeClient {
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        ChromeClient() {
        }

        public Bitmap getDefaultVideoPoster() {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getResources(), 2130837573);
        }

        public void onHideCustomView() {
            ((FrameLayout) getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback) {
            if (this.mCustomView != null) {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout) getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }
}

