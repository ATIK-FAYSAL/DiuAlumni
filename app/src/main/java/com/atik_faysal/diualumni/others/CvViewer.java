package com.atik_faysal.diualumni.others;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;



public class CvViewer extends Activity
{

     @SuppressLint("SetJavaScriptEnabled")
     @Override
     protected void onCreate(@Nullable Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          //setContentView(R.layout.pdf_view);
          String url = getIntent().getExtras().getString("url");
          //String google = "http://docs.google.com/gview?embedded=true&url=";

          WebView webView = new WebView(this);
          webView.getSettings().setJavaScriptEnabled(true);
          final Activity activity = this;
          webView.setWebViewClient(new WebViewClient()
          {
               @Override
               public void onReceivedError(WebView view, int errorCode,String description,String failUrl) {
                    Toast.makeText(activity,description,Toast.LENGTH_LONG).show();
               }

               @SuppressLint("NewApi")
               @Override
               public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    onReceivedError(view,error.getErrorCode(),error.getDescription().toString(),request.getUrl().toString());
               }
          });
          webView.loadUrl(url);
          setContentView(webView);
     }

}
