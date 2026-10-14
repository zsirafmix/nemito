package hu.oli.nemito;
import android.service.notification.*;import org.json.*;
public class MuteService extends NotificationListenerService {
  static MuteService inst;
  @Override public void onListenerConnected(){inst=this; if(Store.on(this))Store.ongoing(this); if(Store.on(this))cancelActive();}
  @Override public void onListenerDisconnected(){inst=null;}
  static void cancelActiveNow(){if(inst!=null)inst.cancelActive();}
  void cancelActive(){try{StatusBarNotification[] a=getActiveNotifications(); if(a==null)return; java.util.Set<String> s=Store.sel(this); for(StatusBarNotification n:a) if(s.contains(n.getPackageName())) block(n);}catch(Exception e){}}
  @Override public void onNotificationPosted(StatusBarNotification n){
    String pkg=n.getPackageName(); if(pkg.equals(getPackageName()))return;
    try{JSONObject o=new JSONObject().put("p",pkg).put("t",System.currentTimeMillis()); Store.push(this,"recent",o,50,pkg);}catch(Exception e){}
    if(Store.on(this)&&Store.sel(this).contains(pkg)) block(n);
  }
  void block(StatusBarNotification n){
    if(n.isOngoing()&&(n.getNotification().flags&android.app.Notification.FLAG_FOREGROUND_SERVICE)!=0)return;
    cancelNotification(n.getKey());
    try{android.os.Bundle e=n.getNotification().extras; CharSequence t=e.getCharSequence("android.title"),x=e.getCharSequence("android.text");
      if((n.getNotification().flags&android.app.Notification.FLAG_GROUP_SUMMARY)!=0&&t==null)return;
      Store.push(this,"missed",new JSONObject().put("p",n.getPackageName()).put("ti",t==null?"":t.toString()).put("x",x==null?"":x.toString()).put("t",System.currentTimeMillis()),300,null);}catch(Exception e){}
  }
}
