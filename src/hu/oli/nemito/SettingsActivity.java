package hu.oli.nemito;
import android.app.*;import android.content.*;import android.content.pm.*;import android.graphics.*;import android.graphics.drawable.*;import android.os.*;import android.text.*;import android.view.*;import android.widget.*;
import org.json.*;import java.text.SimpleDateFormat;import java.util.*;
public class SettingsActivity extends Activity {
  static boolean unlocked;
  boolean guard(){if(Pin.needed(this)&&!unlocked){startActivity(new Intent(this,PinActivity.class).putExtra("mode","settings")); finish(); return true;} return false;}
  static class App{String pkg,name;Drawable icon;}
  List<App> all=new ArrayList<>(); List<Object> rows=new ArrayList<>(); int tab=0; String q=""; Map<String,Drawable> icons=new HashMap<>();
  ListView lv; Ad ad; EditText search; TextView[] tabs=new TextView[4]; TextView stateBtn,hint;
  int dp(float v){return MainActivity.dp(this,v);}
  GradientDrawable rr(int col,int r,int stroke){GradientDrawable g=new GradientDrawable(); g.setColor(col); g.setCornerRadius(dp(r)); if(stroke!=0)g.setStroke(dp(1),stroke); return g;}
  TextView t(String s,float sz,int c){TextView v=new TextView(this); v.setText(s); v.setTextSize(sz); v.setTextColor(c); return v;}
  @Override protected void onCreate(Bundle b){super.onCreate(b); if(guard())return;
    LinearLayout root=new LinearLayout(this); root.setOrientation(1); root.setBackgroundColor(Store.BG); int p=dp(16); root.setPadding(p,dp(20),p,0);
    LinearLayout top=new LinearLayout(this); top.setGravity(Gravity.CENTER_VERTICAL);
    TextView ti=t("Némító",28,Color.WHITE); ti.setTypeface(Typeface.create("sans-serif-black",0)); top.addView(ti,new LinearLayout.LayoutParams(0,-2,1));
    stateBtn=t("",14,Color.BLACK); stateBtn.setTypeface(Typeface.DEFAULT_BOLD); stateBtn.setPadding(dp(16),dp(8),dp(16),dp(8)); top.addView(stateBtn);
    stateBtn.setOnClickListener(new View.OnClickListener(){public void onClick(View v){if(!Store.granted(SettingsActivity.this)){startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));return;} Store.setOn(SettingsActivity.this,!Store.on(SettingsActivity.this)); refreshState();}});
    root.addView(top);
    hint=t("",13,Store.GREY); hint.setPadding(0,dp(4),0,dp(12)); root.addView(hint);
    LinearLayout tb=new LinearLayout(this); tb.setBackground(rr(Store.CARD,14,0)); tb.setPadding(dp(4),dp(4),dp(4),dp(4));
    String[] names={"Appok","Lemaradt","Küldők","Kód"};
    for(int i=0;i<4;i++){final int k=i; TextView x=t(names[i],14,Color.WHITE); x.setGravity(Gravity.CENTER); x.setTypeface(Typeface.DEFAULT_BOLD); x.setPadding(0,dp(10),0,dp(10)); tabs[i]=x; tb.addView(x,new LinearLayout.LayoutParams(0,-2,1));
      x.setOnClickListener(new View.OnClickListener(){public void onClick(View v){tab=k; build();}});}
    root.addView(tb);
    search=new EditText(this); search.setHint("Keresés…"); search.setHintTextColor(Store.GREY); search.setTextColor(Color.WHITE); search.setSingleLine(); search.setBackground(rr(Store.CARD,14,0x33FFD400)); search.setPadding(dp(16),dp(12),dp(16),dp(12));
    LinearLayout.LayoutParams sl=new LinearLayout.LayoutParams(-1,-2); sl.topMargin=dp(12); root.addView(search,sl);
    search.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int a,int b,int c){} public void onTextChanged(CharSequence s,int a,int b,int c){} public void afterTextChanged(Editable e){q=e.toString().toLowerCase(); build();}});
    lv=new ListView(this); lv.setDivider(null); lv.setSelector(new ColorDrawable(0)); lv.setPadding(0,dp(8),0,dp(16)); lv.setClipToPadding(false); ad=new Ad(); lv.setAdapter(ad);
    root.addView(lv,new LinearLayout.LayoutParams(-1,0,1)); setContentView(root);
    new Thread(new Runnable(){public void run(){ PackageManager pm=getPackageManager(); Intent i=new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER); final List<App> l=new ArrayList<>(); Set<String> seen=new HashSet<>();
      for(ResolveInfo r:pm.queryIntentActivities(i,0)){String pk=r.activityInfo.packageName; if(pk.equals(getPackageName())||!seen.add(pk))continue; App a=new App(); a.pkg=pk; a.name=r.loadLabel(pm).toString(); a.icon=r.loadIcon(pm); l.add(a);}
      final Set<String> s=Store.sel(SettingsActivity.this); Collections.sort(l,new Comparator<App>(){public int compare(App x,App y){boolean a=s.contains(x.pkg),b=s.contains(y.pkg); if(a!=b)return a?-1:1; return x.name.compareToIgnoreCase(y.name);}});
      runOnUiThread(new Runnable(){public void run(){all=l; for(App a:l)icons.put(a.pkg,a.icon); build();}});}}).start();
  }
  @Override protected void onResume(){super.onResume(); if(guard())return; refreshState(); build();}
  void refreshState(){boolean g=Store.granted(this),on=Store.on(this);
    if(!g){stateBtn.setText("ENGEDÉLY KELL"); stateBtn.setBackground(rr(Store.Y,20,0)); stateBtn.setTextColor(Color.BLACK); hint.setText("Nincs értesítési hozzáférés – koppints a gombra.");}
    else {stateBtn.setText(on?"NÉMÍTÁS BE":"NÉMÍTÁS KI"); stateBtn.setBackground(on?rr(Store.Y,20,0):rr(0,20,Store.Y)); stateBtn.setTextColor(on?Color.BLACK:Store.Y);
      hint.setText(Store.sel(this).size()+" app kiválasztva · az ikon minden indításkor vált");}}
  String fmt(long t){return new SimpleDateFormat("MM.dd. HH:mm",new Locale("hu")).format(new Date(t));}
  void build(){if(tabs[0]==null)return; for(int i=0;i<4;i++){tabs[i].setBackground(i==tab?rr(Store.Y,10,0):null); tabs[i].setTextColor(i==tab?Color.BLACK:0xFFBBBBBB);}
    search.setVisibility(tab==0?View.VISIBLE:View.GONE); rows.clear();
    if(tab==0){ if(all.isEmpty())rows.add("Appok betöltése…"); for(App a:all) if(q.isEmpty()||a.name.toLowerCase().contains(q)||a.pkg.contains(q)) rows.add(a);}
    else if(tab==1){JSONArray m=Store.arr(this,"missed"); if(m.length()>0)rows.add("#clear"); else rows.add("Nincs lemaradt értesítés."); for(int i=0;i<m.length();i++)rows.add(m.optJSONObject(i));}
    else if(tab==3){boolean ps=Pin.set(this); rows.add(ps?"Kód beállítva. Bekapcsolt némításnál a kikapcsoláshoz és a beállításokhoz is kell.":"Nincs kód. Beállíthatsz egy 4 jegyű kódot, ami bekapcsolt némításnál a kikapcsoláshoz és a beállításokhoz kell. A bekapcsoláshoz soha nem kell kód.");
      if(ps){rows.add("@change|Kód módosítása"); rows.add("@remove|Kód törlése");} else rows.add("@set|Kód beállítása");}
    else {JSONArray m=Store.arr(this,"recent"); rows.add("Az utóbbi 50 értesítést küldő app – egy koppintással hozzáadhatod a némítottakhoz."); for(int i=0;i<m.length();i++){JSONObject o=m.optJSONObject(i); try{o.put("r",1);}catch(Exception e){} rows.add(o);}}
    ad.notifyDataSetChanged(); refreshState();}
  Drawable icon(String pkg){Drawable d=icons.get(pkg); if(d==null){try{d=getPackageManager().getApplicationIcon(pkg);}catch(Exception e){d=new ColorDrawable(Store.CARD);} icons.put(pkg,d);} return d;}
  class Ad extends BaseAdapter{
    public int getCount(){return rows.size();} public Object getItem(int i){return rows.get(i);} public long getItemId(int i){return i;}
    public int getViewTypeCount(){return 1;} 
    public View getView(int i,View cv,ViewGroup par){Object o=rows.get(i); LinearLayout row=new LinearLayout(SettingsActivity.this); row.setGravity(Gravity.CENTER_VERTICAL); row.setPadding(dp(14),dp(10),dp(10),dp(10));
      AbsListView.LayoutParams lp=new AbsListView.LayoutParams(-1,-2); row.setLayoutParams(lp);
      if(o instanceof String){String s=(String)o; if(s.equals("#clear")){TextView c=t("Lista törlése",14,Store.Y); c.setTypeface(Typeface.DEFAULT_BOLD); c.setGravity(Gravity.END); row.addView(c,new LinearLayout.LayoutParams(-1,-2));
          row.setOnClickListener(new View.OnClickListener(){public void onClick(View v){Store.clear(SettingsActivity.this,"missed"); build();}});}
        else if(s.startsWith("@")){final String md=s.substring(1,s.indexOf('|')); TextView c=t(s.substring(s.indexOf('|')+1),16,md.equals("remove")?Store.Y:Color.BLACK); c.setTypeface(Typeface.DEFAULT_BOLD); c.setGravity(Gravity.CENTER); c.setPadding(0,dp(14),0,dp(14)); c.setBackground(md.equals("remove")?rr(0,14,Store.Y):rr(Store.Y,14,0)); row.addView(c,new LinearLayout.LayoutParams(-1,-2));
          c.setOnClickListener(new View.OnClickListener(){public void onClick(View v){startActivity(new Intent(SettingsActivity.this,PinActivity.class).putExtra("mode",md));}});}
        else {TextView c=t(s,13,Store.GREY); row.addView(c);} return row;}
      LinearLayout card=new LinearLayout(SettingsActivity.this); card.setGravity(Gravity.CENTER_VERTICAL); card.setBackground(rr(Store.CARD,14,0)); card.setPadding(dp(12),dp(10),dp(8),dp(10));
      row.setPadding(0,dp(4),0,dp(4)); row.addView(card,new LinearLayout.LayoutParams(-1,-2));
      final String pkg; String name,sub=null; if(o instanceof App){pkg=((App)o).pkg;name=((App)o).name;} else {JSONObject j=(JSONObject)o; pkg=j.optString("p"); name=Store.label(SettingsActivity.this,pkg);
        if(j.has("r")) sub=fmt(j.optLong("t")); else {String ti=j.optString("ti"),x=j.optString("x"); sub=fmt(j.optLong("t"))+(ti.isEmpty()?"":" · "+ti)+(x.isEmpty()?"":"\n"+x);}}
      ImageView ic=new ImageView(SettingsActivity.this); ic.setImageDrawable(icon(pkg)); LinearLayout.LayoutParams il=new LinearLayout.LayoutParams(dp(40),dp(40)); il.rightMargin=dp(12); card.addView(ic,il);
      LinearLayout tx=new LinearLayout(SettingsActivity.this); tx.setOrientation(1); TextView n=t(name,16,Color.WHITE); n.setSingleLine(); n.setEllipsize(TextUtils.TruncateAt.END); tx.addView(n);
      if(sub!=null){TextView s2=t(sub,12,Store.GREY); s2.setMaxLines(3); s2.setEllipsize(TextUtils.TruncateAt.END); tx.addView(s2);} card.addView(tx,new LinearLayout.LayoutParams(0,-2,1));
      final boolean selected=Store.sel(SettingsActivity.this).contains(pkg);
      if(o instanceof App){Switch sw=new Switch(SettingsActivity.this); sw.setChecked(selected); tint(sw); card.addView(sw);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){public void onCheckedChanged(CompoundButton b,boolean v){Store.setSel(SettingsActivity.this,pkg,v); refreshState();}});}
      else if(((JSONObject)o).has("r")){final TextView a=t(selected?"némítva":"hozzáad",13,selected?Store.GREY:Color.BLACK); a.setTypeface(Typeface.DEFAULT_BOLD); a.setPadding(dp(14),dp(8),dp(14),dp(8)); if(!selected)a.setBackground(rr(Store.Y,16,0)); card.addView(a);
        if(!selected)a.setOnClickListener(new View.OnClickListener(){public void onClick(View v){Store.setSel(SettingsActivity.this,pkg,true); build();}});}
      return row;}
  }
  void tint(Switch s){int[][] st={{android.R.attr.state_checked},{}}; s.setThumbTintList(new android.content.res.ColorStateList(st,new int[]{Store.Y,0xFFBBBBBB})); s.setTrackTintList(new android.content.res.ColorStateList(st,new int[]{0x88FFD400,0xFF444444}));}
}
