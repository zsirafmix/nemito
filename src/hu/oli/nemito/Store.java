package hu.oli.nemito;
import android.app.*;import android.content.*;import android.os.Build;import android.provider.Settings;
import org.json.*;import java.util.*;
public class Store {
  static final int Y=0xFFFFD400, BG=0xFF0B0B0B, CARD=0xFF1A1A1A, GREY=0xFF8A8A8A;
  static SharedPreferences p(Context c){return c.getSharedPreferences("n",0);}
  static boolean on(Context c){return p(c).getBoolean("on",false);}
  static Set<String> sel(Context c){return new HashSet<>(p(c).getStringSet("sel",new HashSet<String>()));}
  static void setSel(Context c,String pkg,boolean v){Set<String> s=sel(c); if(v)s.add(pkg); else s.remove(pkg); p(c).edit().putStringSet("sel",s).apply(); if(on(c))ongoing(c);}
  static boolean granted(Context c){String f=Settings.Secure.getString(c.getContentResolver(),"enabled_notification_listeners"); return f!=null&&f.contains(c.getPackageName());}
  static void setOn(Context c,boolean v){p(c).edit().putBoolean("on",v).commit(); if(v)SettingsActivity.unlocked=false; if(v){ongoing(c); MuteService.cancelActiveNow();} else ((NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(1);
    if(Build.VERSION.SDK_INT>=24) try{android.service.quicksettings.TileService.requestListeningState(c,new ComponentName(c,MuteTile.class));}catch(Exception e){} }
  static void ongoing(Context c){NotificationManager nm=(NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
    Notification.Builder b; if(Build.VERSION.SDK_INT>=26){try{nm.deleteNotificationChannel("st");}catch(Exception e){} NotificationChannel ch=new NotificationChannel("st2","Némítás állapota",NotificationManager.IMPORTANCE_LOW); ch.setSound(null,null); ch.enableVibration(false); ch.setShowBadge(false); nm.createNotificationChannel(ch); b=new Notification.Builder(c,"st2");} else {b=new Notification.Builder(c); b.setPriority(Notification.PRIORITY_LOW);}
    Intent i=new Intent(c,SettingsActivity.class); PendingIntent pi=PendingIntent.getActivity(c,0,i,PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);
    b.setSmallIcon(R.drawable.ic_star).setContentTitle("Némítás aktív – "+sel(c).size()+" app").setContentText("Koppints a beállításokhoz").setOngoing(true).setColor(Y).setContentIntent(pi).setShowWhen(false);
    try{nm.notify(1,b.build());}catch(Exception e){}}
  static JSONArray arr(Context c,String k){try{return new JSONArray(p(c).getString(k,"[]"));}catch(Exception e){return new JSONArray();}}
  static synchronized void push(Context c,String k,JSONObject o,int max,String dedupPkg){JSONArray a=arr(c,k),n=new JSONArray(); n.put(o);
    for(int i=0;i<a.length()&&n.length()<max;i++){JSONObject x=a.optJSONObject(i); if(x==null)continue; if(dedupPkg!=null&&dedupPkg.equals(x.optString("p")))continue; n.put(x);} p(c).edit().putString(k,n.toString()).apply();}
  static void clear(Context c,String k){p(c).edit().remove(k).apply();}
  static String label(Context c,String pkg){try{return c.getPackageManager().getApplicationLabel(c.getPackageManager().getApplicationInfo(pkg,0)).toString();}catch(Exception e){return pkg;}}

}
