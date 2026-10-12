package hu.oli.nemito;
import android.content.*;import android.util.Base64;import java.security.*;
public class Pin {
  static boolean set(Context c){return Store.p(c).contains("ph");}
  static String h(String salt,String pin){try{MessageDigest d=MessageDigest.getInstance("SHA-256"); byte[] b=(salt+":"+pin).getBytes("UTF-8"); for(int i=0;i<20000;i++){d.reset(); d.update(salt.getBytes("UTF-8")); b=d.digest(b);} return Base64.encodeToString(b,Base64.NO_WRAP);}catch(Exception e){throw new RuntimeException(e);}}
  static void save(Context c,String pin){byte[] s=new byte[16]; new SecureRandom().nextBytes(s); String salt=Base64.encodeToString(s,Base64.NO_WRAP); Store.p(c).edit().putString("ps",salt).putString("ph",h(salt,pin)).apply();}
  static void remove(Context c){Store.p(c).edit().remove("ps").remove("ph").apply();}
  static long lockLeft(Context c){return Math.max(0,Store.p(c).getLong("lock",0)-System.currentTimeMillis());}
  /** returns true if ok */
  static boolean check(Context c,String pin){SharedPreferences p=Store.p(c); boolean ok=h(p.getString("ps",""),pin).equals(p.getString("ph",""));
    if(ok)p.edit().putInt("fails",0).apply(); else {int f=p.getInt("fails",0)+1; if(f>=5){p.edit().putInt("fails",0).putLong("lock",System.currentTimeMillis()+30000).apply();} else p.edit().putInt("fails",f).apply();} return ok;}
  static boolean needed(Context c){return Store.on(c)&&set(c);}
}
