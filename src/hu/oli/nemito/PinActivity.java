package hu.oli.nemito;
import android.app.*;import android.content.*;import android.graphics.*;import android.graphics.drawable.*;import android.os.*;import android.view.*;import android.view.animation.*;import android.widget.*;
public class PinActivity extends Activity {
  // mode: off, settings, set, change, remove
  String mode,first; int step; StringBuilder cur=new StringBuilder(); TextView title,msg; TextView[] dots=new TextView[4]; Handler h=new Handler(); LinearLayout dotRow;
  int dp(float v){return MainActivity.dp(this,v);}
  @Override protected void onCreate(Bundle b){super.onCreate(b); mode=getIntent().getStringExtra("mode"); if(mode==null)mode="off";
    if(mode.equals("set"))step=1; else step=0;
    LinearLayout l=new LinearLayout(this); l.setOrientation(1); l.setGravity(Gravity.CENTER_HORIZONTAL); l.setBackgroundColor(Color.BLACK); l.setPadding(dp(24),dp(56),dp(24),dp(24));
    title=tv("",24,Color.WHITE); title.setTypeface(Typeface.create("sans-serif-black",0)); l.addView(title);
    msg=tv("",15,Store.GREY); msg.setPadding(0,dp(8),0,dp(28)); l.addView(msg);
    dotRow=new LinearLayout(this); dotRow.setGravity(Gravity.CENTER); for(int i=0;i<4;i++){TextView d=new TextView(this); dots[i]=d; LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(dp(18),dp(18)); lp.setMargins(dp(12),0,dp(12),0); dotRow.addView(d,lp);} l.addView(dotRow);
    GridLayout g=new GridLayout(this); g.setColumnCount(3); String[] k={"1","2","3","4","5","6","7","8","9","✕","0","⌫"};
    for(final String s:k){TextView t=tv(s,28,s.equals("✕")||s.equals("⌫")?Store.Y:Color.WHITE); t.setGravity(Gravity.CENTER); GradientDrawable bg=new GradientDrawable(); bg.setShape(GradientDrawable.OVAL); bg.setColor(s.matches("\\d")?0xFF1A1A1A:0); t.setBackground(bg);
      GridLayout.LayoutParams lp=new GridLayout.LayoutParams(); int ks=Math.min(dp(76),(getResources().getDisplayMetrics().widthPixels-dp(48))/3-dp(16)); lp.width=ks; lp.height=ks; lp.setMargins(dp(8),dp(6),dp(8),dp(6)); g.addView(t,lp);
      t.setOnClickListener(new View.OnClickListener(){public void onClick(View v){key(s);}});}
    LinearLayout.LayoutParams gl=new LinearLayout.LayoutParams(-2,-2); gl.topMargin=dp(36); l.addView(g,gl);
    setContentView(l); prompt(null); }
  TextView tv(String s,float sz,int c){TextView t=new TextView(this); t.setText(s); t.setTextSize(sz); t.setTextColor(c); t.setGravity(Gravity.CENTER); return t;}
  void prompt(String err){String t; if(mode.equals("off"))t="Kód a kikapcsoláshoz"; else if(mode.equals("settings"))t="Kód a beállításokhoz"; else if(step==0)t="Jelenlegi kód"; else if(step==1)t="Új 4 jegyű kód"; else t="Új kód még egyszer";
    title.setText(t); long left=Pin.lockLeft(this);
    if(left>0){msg.setText("Túl sok hibás próbálkozás – várj "+((left+999)/1000)+" mp"); msg.setTextColor(Store.Y); h.postDelayed(new Runnable(){public void run(){prompt(null);}},1000);}
    else if(err!=null){msg.setText(err); msg.setTextColor(Store.Y);} else {msg.setText(mode.equals("off")?"A némítás be van kapcsolva":" "); msg.setTextColor(Store.GREY);}
    dotsUpd();}
  void dotsUpd(){for(int i=0;i<4;i++){GradientDrawable d=new GradientDrawable(); d.setShape(GradientDrawable.OVAL); if(i<cur.length())d.setColor(Store.Y); else d.setStroke(dp(2),0xFF666666); dots[i].setBackground(d);}}
  void key(String s){if(s.equals("✕")){finishAndRemoveTask();return;} if(s.equals("⌫")){if(cur.length()>0)cur.setLength(cur.length()-1); dotsUpd();return;}
    if(cur.length()>=4)return; if(step==0&&Pin.lockLeft(this)>0)return; cur.append(s); dotsUpd(); if(cur.length()==4) h.postDelayed(new Runnable(){public void run(){done(cur.toString()); cur.setLength(0); dotsUpd();}},120);}
  void bad(String m){TranslateAnimation a=new TranslateAnimation(0,dp(14),0,0); a.setDuration(60); a.setRepeatMode(Animation.REVERSE); a.setRepeatCount(5); dotRow.startAnimation(a); try{((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(200);}catch(Exception e){} prompt(m);}
  void done(String p){
    if(step==0){ if(Pin.lockLeft(this)>0){prompt(null);return;}
      if(!Pin.check(this,p)){bad("Hibás kód"); return;}
      if(mode.equals("off")){Store.setOn(this,false); Intent i=new Intent(this,MainActivity.class).putExtra("show","off").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); startActivity(i); finish(); return;}
      if(mode.equals("settings")){SettingsActivity.unlocked=true; startActivity(new Intent(this,SettingsActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)); finish(); return;}
      if(mode.equals("remove")){Pin.remove(this); toast("Kód törölve"); finish(); return;}
      step=1; prompt(null); return;}
    if(step==1){first=p; step=2; prompt(null); return;}
    if(!p.equals(first)){step=1; bad("A két kód nem egyezik – kezdd újra"); return;}
    Pin.save(this,p); toast("Kód elmentve"); finish();}
  void toast(String s){Toast.makeText(this,s,Toast.LENGTH_SHORT).show();}
}
