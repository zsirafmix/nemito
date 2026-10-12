package hu.oli.nemito;
import android.service.quicksettings.*;
public class MuteTile extends TileService {
  @Override public void onStartListening(){upd();}
  @Override public void onClick(){Store.setOn(this,!Store.on(this)); upd();}
  void upd(){Tile t=getQsTile(); if(t==null)return; boolean on=Store.on(this); t.setState(on?Tile.STATE_ACTIVE:Tile.STATE_INACTIVE); t.setLabel(on?"Némítás BE":"Némítás KI"); t.updateTile();}
}
