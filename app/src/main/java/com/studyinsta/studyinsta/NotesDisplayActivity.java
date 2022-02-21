package com.studyinsta.studyinsta;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NotesDisplayActivity extends AppCompatActivity {

//    private WebView webView;
    private PDFView pdfView;
    private String notesUrl;
    public static Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_notes_display);

        ///loading Dialog
        loadingDialog = new Dialog(NotesDisplayActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        /////end loading dialog
        //URL intent se pass karna
        Intent urlIntent = getIntent();
        notesUrl = urlIntent.getStringExtra("course_notes_url");

        pdfView = findViewById(R.id.pdfViewN);

        new PdfDownloader().execute(notesUrl);

//        webView = findViewById(R.id.notes_webview);
//
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setBuiltInZoomControls(true);
//
//        //Below two lines are to load the webpage in FIT TO SCREEN size bye default
//        webView.getSettings().setLoadWithOverviewMode(true);
//        webView.getSettings().setUseWideViewPort(true);
//
//        webView.setWebViewClient(new WebViewClient(){
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                //do something when the page loading has started
//                super.onPageStarted(view, url, favicon);
//            }
//
//            @Override
//            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//                //do something when the page could not load and encountered some error
//                loadingDialog.dismiss();
//                super.onReceivedError(view, request, error);
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//
//                webView.loadUrl("javascript:(function() { " +
//                        "document.querySelector('[role=\"toolbar\"]').remove();}) ()");
//                loadingDialog.dismiss();
//                super.onPageFinished(view, url);
//            }
//        });
//
//        webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + notesUrl);

    }

    private class PdfDownloader extends AsyncTask<String, Void, InputStream>{

        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;

            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                if (urlConnection.getResponseCode() == 200){
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            } catch (MalformedURLException e) {
                loadingDialog.dismiss();
                Toast.makeText(NotesDisplayActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (IOException e) {
                loadingDialog.dismiss();
                Toast.makeText(NotesDisplayActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            pdfView.fromStream(inputStream)
                    .defaultPage(0)
                    .enableAnnotationRendering(true)
                    .onLoad(new OnLoadCompleteListener() {
                        @Override
                        public void loadComplete(int nbPages) {
                            loadingDialog.dismiss();
                        }
                    })
                    .scrollHandle(new DefaultScrollHandle(NotesDisplayActivity.this))
                    .spacing(2)
                    .load();

        }
    }

}