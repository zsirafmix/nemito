package hu.oli.nemito;
import android.service.quicksettings.*;
public class MuteTile extends TileService {
  @Override public void onStartListening(){upd();}
  @Override public void onClick(){ if(Pin.needed(this)){android.content.Intent i=new android.content.Intent(this,PinActivity.class).putExtra("mode","off").addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK); if(android.os.Build.VERSION.SDK_INT>=34) startActivityAndCollapse(android.app.PendingIntent.getActivity(this,5,i,android.app.PendingIntent.FLAG_IMMUTABLE|android.app.PendingIntent.FLAG_UPDATE_CURRENT)); else startActivityAndCollapse(i); return;}Store.setOn(this,!Store.on(this)); upd();}
  void upd(){Tile t=getQsTile(); if(t==null)return; boolean on=Store.on(this); t.setState(on?Tile.STATE_ACTIVE:Tile.STATE_INACTIVE); t.setLabel(on?"Némítás BE":"Némítás KI"); t.updateTile();}
}
