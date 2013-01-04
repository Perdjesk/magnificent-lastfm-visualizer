package ch.gapa.master.mlv.model;

import ch.gapa.master.mlv.data.Graph;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import de.umass.lastfm.Artist;

public class ArtistWrapper {

  public static final int RADIUS = 20;
  public static final int SELECTED_COLOR = Color.GREEN;
  public static final int DEFAULT_COLOR = Color.BLUE;
  public static final String DEFAULT_TEXT = "Artist Wrapper";
  private final Artist _artist;
  private final Point _position = new Point();
  private final Point _temporaryPosition = new Point();
  private final Paint _paint = new Paint();
  private Status _status;

  public ArtistWrapper ( final Artist artist ) {
    _artist = artist;
    _paint.setAntiAlias( false );
    _paint.setColor( DEFAULT_COLOR );
    _paint.setTextAlign( Paint.Align.CENTER );
  }

  public void setPosition ( final Point position ) {
    _position.set( position.x, position.y );
    _temporaryPosition.set( position.x, position.y );
  }

  public Point getPosition () {
    return _position;
  }

  public void updateTemporaryPosition ( final int dx, final int dy ) {
    _temporaryPosition.offset( dx, dy );
  }

  public void commitPosition () {
    _position.set( _temporaryPosition.x, _temporaryPosition.y );
  }

  protected Point getTemporaryPosition () {
    return _temporaryPosition;
  }

  public void draw ( final Canvas canvas ) {
    canvas.drawCircle( _position.x, _position.y, RADIUS, _paint );
    canvas.drawText( _artist.getName(), _position.x, _position.y + RADIUS + _paint.getTextSize(), _paint );
  }

  public Status getStatus () {
    return _status;
  }

  public void setStatus ( final Status status ) {
    if ( _status != Status.EXPANDED ) {
      this._status = status;
    }
    switch ( _status ) {
    case NOT_SELECTED:
      _paint.setColor( DEFAULT_COLOR );
      break;
    case SELECTED:
      _paint.setColor( SELECTED_COLOR );
      break;
    case EXPAND:
      break;
    case EXPANDED:
      break;
    }
  }

  public boolean contains ( final Point location ) {
    return ( location.x > ( _position.x - RADIUS ) && location.x < ( _position.x + RADIUS )
        && location.y > ( _position.y - RADIUS ) && location.y < ( _position.y + RADIUS ) );
  }

  public Artist getArtist () {
    return _artist;
  }

  /**
   * Returns whether given objects equals to self ArtistWrapper object. Override for {@link Graph} purposes.
   * 
   * @param obj
   *          the object that may equal itself
   * @return whether the object equals itself
   * @see Object#equals(Object)
   */
  @Override
  public boolean equals ( final Object obj ) {
    if ( this == obj ) {
      return true;
    }
    if ( !( obj instanceof ArtistWrapper ) ) {
      return false;
    }
    ArtistWrapper wrapper = (ArtistWrapper) obj;
    boolean equal = this._artist.equals( wrapper._artist );
    return equal;
  }

  @Override
  public int hashCode () {
    return _artist.hashCode();
  }

  @Override
  public String toString () {
    return _artist.toString();
  }

  public enum Status {
    NOT_SELECTED, SELECTED, EXPAND, EXPANDED;
  }
}
