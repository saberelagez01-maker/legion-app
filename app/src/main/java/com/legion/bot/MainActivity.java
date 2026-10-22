package com.legion.bot;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/** واجهة LEGION PRO BOT: تفتح لوحة التحكم (Flask) المشغّلة على السيرفر داخل WebView. */
public class MainActivity extends Activity {
    private static final String PREFS = "legion";
    private static final String KEY_URL = "server_url";
    private WebView web;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        String url = getSharedPreferences(PREFS, MODE_PRIVATE).getString(KEY_URL, "");
        if (url.isEmpty()) showUrlScreen(); else showWeb(url);
    }

    private void showUrlScreen() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(48, 96, 48, 48);
        box.setGravity(Gravity.CENTER_HORIZONTAL);
        TextView t = new TextView(this);
        t.setText("أدخل رابط السيرفر (مثال: http://123.45.67.89:5000)");
        t.setTextSize(16);
        final EditText in = new EditText(this);
        in.setHint("http://...");
        in.setSingleLine(true);
        Button go = new Button(this);
        go.setText("فتح");
        go.setOnClickListener(v -> {
            String u = in.getText().toString().trim();
            if (u.isEmpty()) return;
            if (!u.startsWith("http://") && !u.startsWith("https://")) u = "http://" + u;
            getSharedPreferences(PREFS, MODE_PRIVATE).edit().putString(KEY_URL, u).apply();
            showWeb(u);
        });
        box.addView(t); box.addView(in); box.addView(go);
        setContentView(box);
    }

    private void showWeb(String url) {
        web = new WebView(this);
        web.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        web.setBackgroundColor(Color.BLACK);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        web.setWebViewClient(new WebViewClient());
        web.setWebChromeClient(new WebChromeClient());
        setContentView(web);
        web.loadUrl(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        m.add(0, 1, 0, "تحديث");
        m.add(0, 2, 1, "تغيير رابط السيرفر");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem i) {
        if (i.getItemId() == 1 && web != null) { web.reload(); return true; }
        if (i.getItemId() == 2) {
            getSharedPreferences(PREFS, MODE_PRIVATE).edit().remove(KEY_URL).apply();
            web = null;
            showUrlScreen();
            return true;
        }
        return super.onOptionsItemSelected(i);
    }

    @Override
    public void onBackPressed() {
        if (web != null && web.canGoBack()) web.goBack(); else super.onBackPressed();
    }
}
