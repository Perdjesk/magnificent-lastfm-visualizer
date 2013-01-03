package de.umass.lastfm.cache;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public final class SQLiteCache extends Cache {

  public static final String TABLE_CACHE = "LASTFM_CACHE";
  public static final String COLUMN_ID = "QUERY";
  public static final String COLUMN_RESPONSE = "RESPONSE";
  public static final String COLUMN_EXPIRATION = "EXPIRATION_DATE";
  private static final String DATABASE_CREATE = "create table " + TABLE_CACHE + "(" + COLUMN_ID + " text primary key, "
      + COLUMN_RESPONSE + " text not null + " + COLUMN_EXPIRATION + " integer not null);";
  private static final String DATABASE_NAME = "mlvisu.db";
  private static final int DATABASE_VERSION = 1;
  private final SQLiteHelper _helper;
  private final String[] allColumns = { COLUMN_ID, COLUMN_RESPONSE, COLUMN_EXPIRATION };
  private SQLiteDatabase _database;

  public SQLiteCache ( final Context context ) {
    _helper = new SQLiteHelper( context );
    open();
  }

  private void open () throws SQLException {
    _database = _helper.getWritableDatabase();
  }

  @Override
  public boolean contains ( String cacheEntryName ) {
    String SELECT = COLUMN_ID + " = ?";
    Cursor cursor = _database.query( DATABASE_NAME, allColumns, SELECT, new String [] { cacheEntryName }, null, null,
        null );
    boolean contained = cursor.moveToFirst();
    return contained;
  }

  @Override
  public InputStream load ( String cacheEntryName ) {
    String SELECT = COLUMN_ID + " = ?";
    Cursor cursor = _database.query( DATABASE_NAME, allColumns, SELECT, new String [] { cacheEntryName }, null, null,
        null );
    try {
      if ( cursor.moveToFirst() ) {
        String response = cursor.getString( 1 );
        return new ByteArrayInputStream( response.getBytes( "UTF-8" ) );
      }
    } catch ( UnsupportedEncodingException e ) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public void remove ( String cacheEntryName ) {
    _database.delete( DATABASE_NAME, COLUMN_ID + " = ?", new String [] { cacheEntryName } );
  }

  @Override
  public void store ( String cacheEntryName, InputStream inputStream, long expirationDate ) {
    StringWriter writer = new StringWriter();
    try {
      copy( inputStream, writer, "UTF-8" );
    } catch ( IOException e ) {
      e.printStackTrace();
    }
    String response = writer.toString();
    ContentValues values = new ContentValues();
    values.put( COLUMN_ID, cacheEntryName );
    values.put( COLUMN_RESPONSE, response );
    values.put( COLUMN_EXPIRATION, expirationDate );
    _database.insert( DATABASE_NAME, null, values );
  }

  @Override
  public boolean isExpired ( String cacheEntryName ) {
    String SELECT = COLUMN_ID + " = ?";
    Cursor cursor = _database.query( DATABASE_NAME, new String [] { COLUMN_EXPIRATION }, SELECT,
        new String [] { cacheEntryName }, null, null, null );
    if ( cursor.moveToFirst() ) {
      long expiration = cursor.getLong( 0 );
      return expiration < System.currentTimeMillis();
    }
    return false;
  }

  @Override
  public void clear () {
    _database.delete( DATABASE_NAME, null, null );
  }

  private static final class SQLiteHelper extends SQLiteOpenHelper {

    public SQLiteHelper ( Context context ) {
      super( context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    @Override
    public void onCreate ( SQLiteDatabase db ) {
      db.execSQL( DATABASE_CREATE );
    }

    @Override
    public void onUpgrade ( SQLiteDatabase db, int oldVersion, int newVersion ) {
      db.execSQL( "DROP TABLE IF EXISTS " + TABLE_CACHE );
      onCreate( db );
    }
  }

  // Copy and stuff from Apache Common
  private static final int DEFAULT_BUFFER_SIZE = 8096 * 4;

  public static int copy ( Reader input, Writer output ) throws IOException {
    long count = copyLarge( input, output );
    if ( count > Integer.MAX_VALUE ) {
      return -1;
    }
    return (int) count;
  }

  public static long copyLarge ( Reader input, Writer output ) throws IOException {
    char[] buffer = new char [ DEFAULT_BUFFER_SIZE ];
    long count = 0;
    int n = 0;
    while ( -1 != ( n = input.read( buffer ) ) ) {
      output.write( buffer, 0, n );
      count += n;
    }
    return count;
  }

  public static void copy ( InputStream input, Writer output ) throws IOException {
    InputStreamReader in = new InputStreamReader( input );
    copy( in, output );
  }

  public static void copy ( InputStream input, Writer output, String encoding ) throws IOException {
    if ( encoding == null ) {
      copy( input, output );
    } else {
      InputStreamReader in = new InputStreamReader( input, encoding );
      copy( in, output );
    }
  }
}
