package ch.gapa.master.mlv.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import ch.gapa.master.mlv.data.Graph;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import de.umass.lastfm.Artist;
import de.umass.lastfm.Tag;

public class ArtistWrapper {

  public static final int RADIUS = 20;
  public static final int SELECTED_COLOR = Color.GREEN;
  public static final int DEFAULT_COLOR = Color.rgb( 204, 204, 204 );
  public static final String DEFAULT_TEXT = "Artist Wrapper";
  private final Artist _artist;
  private final Point _position = new Point();
  private final Point _temporaryPosition = new Point();
  private final Paint _paint = new Paint();
  private Status _status;
  private boolean _placement = true;
  private Paint _paintText = new Paint();
  Random rand = new Random();

  public ArtistWrapper ( final Artist artist ) {
    _artist = artist;
    _paint.setAntiAlias( true );
    _paint.setColor( DEFAULT_COLOR );
    _paintText.setAntiAlias(true);
    _paintText.setHinting(Paint.HINTING_ON);
    _paintText.setColor( Color.WHITE );
    _paintText.setTextAlign( Paint.Align.CENTER );
    defineColor();
    _position.x = rand.nextInt( 1000 );
    _position.y = rand.nextInt( 1000 );
  }

  private void defineColor () {
    // TODO: lancer dans un thread
    Collection<Tag> tags = Artist.getTopTags( _artist.getName(), Constants.API_KEY );
    Iterator<Tag> it = tags.iterator();
    Integer color = null;
    while ( it.hasNext() ) {
      color = Constants.tagColor.get( it.next().getName() );
      if ( color != null ) {
        _paint.setColor( color );
        break;
      }
    }
  }

  public boolean isFaded () {
    return _paint.getAlpha() < 255;
  }

  public void setAlpha ( int x ) {
    _paint.setAlpha( x );
    _paintText.setAlpha(x);
  }

  public void setPosition ( final Point position ) {
    _position.set( position.x, position.y );
    _temporaryPosition.set( position.x, position.y );
  }

  public Point getPosition () {
    return _position;
  }

  public void setPlacement ( boolean placement ) {
    _placement = placement;
  }

  public boolean hasPlacement () {
    return _placement;
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
    if ( _placement ) {
      canvas.drawCircle( _position.x, _position.y, RADIUS, _paint );
      canvas.drawText( _artist.getName(), _position.x, _position.y + RADIUS + _paint.getTextSize(), _paintText );
    }
  }

  public Status getStatus () {
    return _status;
  }

  public void setStatus ( final Status status ) {
      this._status = status;
    switch ( _status ) {
    case NOT_SELECTED:
      break;
    case SELECTED:
      break;
    case EXPAND:
      break;
    case EXPANDED:
      break;
    }
  }

  public boolean contains ( final Point location ) {
    return ( location.x > ( _position.x - RADIUS ) && location.x < ( _position.x + RADIUS ) && location.y > ( _position.y - RADIUS ) && location.y < ( _position.y + RADIUS ) );
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
