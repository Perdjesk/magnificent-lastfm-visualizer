package ch.gapa.master.mlv.view;

import android.graphics.Point;

public final class TapEvent {

	private final Point _location;

	public TapEvent ( final Point tapLocation ) {
		this._location = tapLocation;
	}

	public Point getLocation () {
		return _location;
	}

}
