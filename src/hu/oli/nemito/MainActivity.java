package hu.oli.nemito;
import android.app.*;import android.content.*;import android.graphics.*;import android.os.*;import android.provider.Settings;import android.view.*;import android.widget.*;
public class MainActivity extends Activity {
  Handler h=new Handler(); Runnable fin=new Runnable(){public void run(){finishAndRemoveTask();}}; boolean shownOnboard; static long last;
  static int dp(Context c,float v){return (int)(v*c.getResources().getDisplayMetrics().density+.5f);}
  @Override protected void onCreate(Bundle b){super.onCreate(b); if(Build.VERSION.SDK_INT>=33&&checkSelfPermission("android.permission.POST_NOTIFICATIONS")!=0) requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"},1);
    if(b==null) decide();}
  @Override protected void onNewIntent(Intent i){super.onNewIntent(i); setIntent(i); decide();}
  @Override protected void onResume(){super.onResume(); if(shownOnboard&&Store.granted(this)){shownOnboard=false; decide();}}
  void decide(){h.removeCallbacks(fin); if(!Store.granted(this)){onboard();return;} if(getIntent().hasExtra("show")){String sh=getIntent().getStringExtra("show"); getIntent().removeExtra("show"); state(sh.equals("on"));return;} long now=SystemClock.elapsedRealtime(); if(now-last<1500)return; last=now; if(Pin.needed(this)){startActivity(new Intent(this,PinActivity.class).putExtra("mode","off").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)); finishAndRemoveTask(); return;} boolean v=!Store.on(this); Store.setOn(this,v); state(v);}
  TextView tv(String s,float sz,int col,boolean bold){TextView t=new TextView(this); t.setText(s); t.setTextSize(sz); t.setTextColor(col); if(bold)t.setTypeface(Typeface.create("sans-serif-black",Typeface.NORMAL)); t.setGravity(Gravity.CENTER); return t;}
  void onboard(){shownOnboard=true; LinearLayout l=new LinearLayout(this); l.setOrientation(1); l.setGravity(Gravity.CENTER); int p=dp(this,28); l.setPadding(p,p,p,p); l.setBackgroundColor(Color.BLACK);
    ImageView ic=new ImageView(this); ic.setImageResource(R.drawable.ic_fg); l.addView(ic,new LinearLayout.LayoutParams(dp(this,120),dp(this,120)));
    l.addView(tv("NÉMÍTÓ",34,Store.Y,true));
    TextView d=tv("Ahhoz, hogy a kiválasztott appok értesítéseit el tudjam tüntetni, engedélyezned kell az „Értesítési hozzáférést” a Némító számára.\n\nA következő képernyőn kapcsold be a Némítót, majd lépj vissza.",16,0xFFDDDDDD,false); d.setPadding(0,dp(this,20),0,dp(this,28)); l.addView(d);
    Button bt=btn("ENGEDÉLY MEGADÁSA",true); bt.setOnClickListener(new View.OnClickListener(){public void onClick(View v){try{startActivity(new Intent(Build.VERSION.SDK_INT>=22?Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS:"android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));}catch(Exception e){startActivity(new Intent(Settings.ACTION_SETTINGS));}}}); l.addView(bt);
    setContentView(l);}
  Button btn(String s,boolean filled){Button b=new Button(this); b.setText(s); b.setAllCaps(false); b.setTextSize(16); b.setTypeface(Typeface.DEFAULT_BOLD); b.setTextColor(filled?Color.BLACK:Store.Y);
    android.graphics.drawable.GradientDrawable g=new android.graphics.drawable.GradientDrawable(); g.setCornerRadius(dp(this,14)); if(filled)g.setColor(Store.Y); else {g.setColor(0); g.setStroke(dp(this,2),Store.Y);} b.setBackground(g); b.setPadding(dp(this,24),dp(this,14),dp(this,24),dp(this,14)); b.setStateListAnimator(null); return b;}
  void state(boolean on){FrameLayout f=new FrameLayout(this); f.setBackgroundColor(on?Store.Y:Color.BLACK);
    LinearLayout l=new LinearLayout(this); l.setOrientation(1); l.setGravity(Gravity.CENTER);
    ImageView ic=new ImageView(this); ic.setImageResource(R.drawable.ic_fg); if(on)ic.setColorFilter(Color.BLACK); else ic.setAlpha(.5f); l.addView(ic,new LinearLayout.LayoutParams(dp(this,140),dp(this,140)));
    l.addView(tv(on?"NÉMÍTÁS BE":"NÉMÍTÁS KI",40,on?Color.BLACK:Color.WHITE,true));
    int n=Store.sel(this).size(); l.addView(tv(on?n+" app értesítései némítva":"Minden értesítés újra megjelenik",16,on?0xFF333333:Store.GREY,false));
    f.addView(l,new FrameLayout.LayoutParams(-1,-1));
    ImageButton g=new ImageButton(this); g.setImageResource(android.R.drawable.ic_menu_preferences); g.setColorFilter(on?Color.BLACK:Store.Y); g.setBackgroundColor(0); g.setContentDescription("Beállítások");
    FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(dp(this,64),dp(this,64),Gravity.TOP|Gravity.END); lp.setMargins(0,dp(this,24),dp(this,12),0); f.addView(g,lp);
    g.setOnClickListener(new View.OnClickListener(){public void onClick(View v){h.removeCallbacks(fin); startActivity(new Intent(MainActivity.this,Pin.needed(MainActivity.this)?PinActivity.class:SettingsActivity.class).putExtra("mode","settings").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)); finishAndRemoveTask();}});
    f.setOnClickListener(new View.OnClickListener(){public void onClick(View v){h.removeCallbacks(fin);}});
    setContentView(f); if(Build.VERSION.SDK_INT>=21)getWindow().setStatusBarColor(on?Store.Y:Color.BLACK); h.postDelayed(fin,getIntent().getBooleanExtra("hold",false)?600000:1200);}
}
