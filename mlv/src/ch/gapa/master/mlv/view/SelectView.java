package ch.gapa.master.mlv.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import ch.gapa.master.mlv.MainActivity;
import ch.gapa.master.mlv.R;
import ch.gapa.master.mlv.model.Constants;
import de.umass.lastfm.Period;

public class SelectView extends RelativeLayout implements
		OnItemSelectedListener, View.OnClickListener {

	Button submit;
	EditText userText;
	MainActivity activity;
	String user = Constants.DEFAULT_USER;
	Period period = Constants.DEFAULT_PERIOD;

	public SelectView(Context context) {
		super(context);
		activity = (MainActivity) context;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.addView(inflater.inflate(R.layout.entryapp, null));
		Spinner spinner = (Spinner) findViewById(R.id.period_spinner);
		spinner.setAdapter(new ArrayAdapter<Period>(context,
				android.R.layout.simple_spinner_item, Period.values()));
		spinner.setOnItemSelectedListener(this);
		submit = (Button) findViewById(R.id.submit);
		submit.setOnClickListener(this);
		userText = (EditText) findViewById(R.id.editText1);
		userText.setText(user);
	}

	//@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		period = (Period) parent.getItemAtPosition(pos);
	}

	//@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	//@Override
	public void onClick(View v) {
		if (!userText.getText().toString().equals(""))
			user = userText.getText().toString();
		activity.setContentGraphView(user, period);
	}
}
