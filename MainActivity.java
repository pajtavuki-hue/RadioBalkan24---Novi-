package com.balkanradio.app;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.view.KeyEvent;
import android.content.Intent;
import android.net.Uri;

import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;
import androidx.core.content.ContextCompat;
import com.google.common.util.concurrent.ListenableFuture;

public class MainActivity extends Activity {
    private WebView webView;
    private ListenableFuture<MediaController> controllerFuture;
    private MediaController controller;
    private static final String SITE = "https://lighthearted-stardust-37d35c.netlify.app/";

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webView = new WebView(this);
        webView.setBackgroundColor(0xFF10131B);
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false); s.setAllowContentAccess(true);
        s.setDatabaseEnabled(true); s.setSupportZoom(false);
        webView.addJavascriptInterface(new RadioBridge(), "RadioBalkanNative");
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Patch only this WebView runtime; the published website files are not changed.
                String js = "(function(){if(window.__rbNativePatched)return;window.__rbNativePatched=true;"+
                    "function patch(){if(typeof window.playStation==='function'&&!window.__rbPlayWrapped){"+
                    "var oldPlay=window.playStation;window.playStation=async function(id){var result=await oldPlay.apply(this,arguments);"+
                    "setTimeout(function(){try{var a=document.querySelector('audio');var n=document.getElementById('nowname');"+
                    "if(a&&a.currentSrc){RadioBalkanNative.play(a.currentSrc,(n&&n.textContent)||'RadioBalkan24');a.pause();}"+
                    "}catch(e){}},350);return result;};window.__rbPlayWrapped=true;}"+
                    "if(typeof window.stopPlayback==='function'&&!window.__rbStopWrapped){var oldStop=window.stopPlayback;window.stopPlayback=function(){"+
                    "try{RadioBalkanNative.stop();}catch(e){}return oldStop.apply(this,arguments);};window.__rbStopWrapped=true;}}"+
                    "patch();setInterval(patch,500);})();";
                view.evaluateJavascript(js, null);
            }
            @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url != null && (url.startsWith("https://") || url.startsWith("http://"))) {
                    Uri u = Uri.parse(url);
                    if (SITE.startsWith(u.getScheme()+"://"+u.getHost())) return false;
                    // Keep ordinary navigation in the app; external intent links are left to WebView.
                }
                return false;
            }
        });
        setContentView(webView);
        SessionToken token = new SessionToken(this, new android.content.ComponentName(this, RadioPlaybackService.class));
        controllerFuture = new MediaController.Builder(this, token).buildAsync();
        controllerFuture.addListener(() -> { try { controller = controllerFuture.get(); } catch (Exception ignored) {} }, ContextCompat.getMainExecutor(this));
        webView.loadUrl(SITE);
    }

    private class RadioBridge {
        @JavascriptInterface public void play(String url, String title) {
            if (url == null || !(url.startsWith("http://") || url.startsWith("https://"))) return;
            runOnUiThread(() -> {
                Intent i = new Intent(MainActivity.this, RadioPlaybackService.class);
                i.setAction(RadioPlaybackService.ACTION_PLAY); i.putExtra(RadioPlaybackService.EXTRA_URL, url);
                i.putExtra(RadioPlaybackService.EXTRA_TITLE, title == null || title.trim().isEmpty() ? "Radio uživo" : title);
                ContextCompat.startForegroundService(MainActivity.this, i);
            });
        }
        @JavascriptInterface public void stop() {
            runOnUiThread(() -> { Intent i = new Intent(MainActivity.this, RadioPlaybackService.class); i.setAction(RadioPlaybackService.ACTION_STOP); startService(i); });
        }
    }

    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView != null && webView.canGoBack()) { webView.goBack(); return true; }
        return super.onKeyDown(keyCode, event);
    }
    @Override protected void onDestroy() {
        if (controllerFuture != null) MediaController.releaseFuture(controllerFuture);
        if (webView != null) webView.destroy(); super.onDestroy();
    }
}
