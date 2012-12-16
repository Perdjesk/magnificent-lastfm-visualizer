package de.umass.lastfm.cache;

import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import ch.gapa.master.mlv.data.Edge;
import de.umass.lastfm.Artist;

public final class SQLiteCache extends Cache {

	public static final String TABLE_CACHE = "LASTFM_CACHE";

	public static final String COLUMN_ID = "QUERY";

	public static final String COLUMN_RESPONSE = "RESPONSE";

	public static final String COLUMN_EXPIRATION = "EXPIRATION_DATE";

	private static final String DATABASE_CREATE = "create table " + TABLE_CACHE + "(" + COLUMN_ID
			+ " text primary key, " + COLUMN_RESPONSE + " text not null + " + COLUMN_EXPIRATION + " integer not null);";

	private static final String DATABASE_NAME = "mlvisu.db";

	private static final int DATABASE_VERSION = 1;

	private static final class SQLiteHandler extends SQLiteOpenHelper implements Externalizable {

		public SQLiteHandler ( Context context, String name, CursorFactory factory, int version ) {
			super( context, name, factory, version );
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

		double _stiffness;
		int _length;
		Collection<Edge<Artist>> _edges;
		
		@SuppressWarnings ( "unchecked" )
		public void readExternal ( ObjectInput in ) throws IOException, ClassNotFoundException {
			_stiffness = in.readDouble();
			_length = in.readInt();
			_edges = (Collection<Edge<Artist>>) in.readObject();
		}

		public void writeExternal ( ObjectOutput out ) throws IOException {
			out.writeDouble( _stiffness );
			out.writeInt( _length );
			out.writeObject( _edges );
		}

	}

	@Override
	public boolean contains ( String cacheEntryName ) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public InputStream load ( String cacheEntryName ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove ( String cacheEntryName ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void store ( String cacheEntryName, InputStream inputStream, long expirationDate ) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isExpired ( String cacheEntryName ) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear () {
		// TODO Auto-generated method stub

	}

}
