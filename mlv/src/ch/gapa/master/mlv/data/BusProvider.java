package ch.gapa.master.mlv.data;

import com.squareup.otto.Bus;

public enum BusProvider {
  INSTANCE;

  private final Bus _bus;

  private BusProvider () {
    _bus = new Bus( "MLV-BUS" );
  }

  public Bus getBus () {
    return _bus;
  }
}
