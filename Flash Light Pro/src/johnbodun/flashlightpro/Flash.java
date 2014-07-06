package johnbodun.flashlightpro;

import jianhuang.flashlightpro.navdrawer.DrawerActivity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class Flash extends DrawerActivity implements Callback {

	private static Camera camera;
	private ToggleButton flash_button;
	private TextView percent;
	float progress = 0.5f;
	SeekBar brightness_seekBar;
	CheckBox checkBox;
	Spinner spinner_color;
	View layout;
	PowerManager.WakeLock wakeLock;
	TableLayout tableLayout;
	private SurfaceHolder mHolder;

	int orangeColor;

	// CountDownTimer countdown;
	// RadioButton RadioButton1, RadioButton2, RadioButton3;
	// Button start, stop;

	@Override
	protected void onStart() {
		super.onStart();

		SurfaceView preview = (SurfaceView) findViewById(R.id.preview);
		SurfaceHolder mHolder = preview.getHolder();
		mHolder.addCallback(this);
	}

	@Override
	public void onResume() {
		super.onResume(); // Always call the superclass method first

		// Get the Camera instance as the activity achieves full user focus
		if (camera == null) {
			getCameraInstance(); // Local method to handle camera init
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (camera != null) {
			camera.release();
			camera = null;
		}
	}

	/*
	 * @Override protected void onRestart() { super.onRestart(); // Always call
	 * the superclass method first // Activity being restarted from stopped
	 * state
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.flash);
		super.setInflaterOnView("home");

		flash_button = (ToggleButton) findViewById(R.id.flash_button);
		percent = (TextView) findViewById(R.id.percent);
		spinner_color = (Spinner) findViewById(R.id.spinner);
		layout = (View) findViewById(R.id.layout);
		tableLayout = (TableLayout) findViewById(R.id.tableLayout);
		tableLayout.setVisibility(View.INVISIBLE);

		final SeekBar brightness_seekBar = (SeekBar) findViewById(R.id.brightness_seekBar);
		final CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);

		orangeColor = getResources().getColor(R.color.Orange);

		// start.setVisibility(View.INVISIBLE);
		// stop.setVisibility(View.INVISIBLE);
		// keep screen on

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		spinner_color.setVisibility(View.INVISIBLE);

		Context context = this;

		PackageManager pm = context.getPackageManager();
		PowerManager pom = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pom.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "tag");
		wakeLock.acquire();

		// code for brightness
		brightness_seekBar
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					public void onProgressChanged(SeekBar arg0, int arg1,
							boolean arg2) {

						progress = (float) arg1 / 100;
						percent.setText(String
								.valueOf((int) ((progress + 0.01) * 100)) + "%");
						WindowManager.LayoutParams layoutParams = getWindow()
								.getAttributes();
						layoutParams.screenBrightness = (float) (progress + 0.01);
						getWindow().setAttributes(layoutParams);
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
					}
				});

		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (checkBox.isChecked()) {

					// flash_button.setVisibility(View.INVISIBLE);
					// text.setVisibility(View.INVISIBLE);
					spinner_color.setVisibility(View.VISIBLE);
					tableLayout.setVisibility(View.VISIBLE);

					spinner_color
							.setOnItemSelectedListener(new OnItemSelectedListener() {
								public void onItemSelected(
										AdapterView<?> parentView,
										View selecedItemView, int position,
										long id) {
									setBackgroundColor(position);
								}

								@Override
								public void onNothingSelected(
										AdapterView<?> arg0) {
									// TODO Auto-generated method stub
								}
							});
				}

				else {
					// flash_button.setVisibility(View.VISIBLE);
					// text.setVisibility(View.VISIBLE);
					tableLayout.setVisibility(View.INVISIBLE);
					spinner_color.setVisibility(View.INVISIBLE);
					// layout.setBackgroundColor(Color.parseColor("#58F01D"));
				}
			}
		});

		// code for camera
		// if device support camera?
		if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			Log.e("ERROR", "Device has no camera!");
			return;
		}

		getCameraInstance();
		final Parameters p = camera.getParameters();

		flash_button.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				p.setFlashMode(Parameters.FLASH_MODE_OFF);
				camera.setParameters(p);
				camera.stopPreview();
				if (!flash_button.isChecked()) {
					Log.i("info", "torch is turn off!");
					p.setFlashMode(Parameters.FLASH_MODE_OFF);
					camera.setParameters(p);
					camera.stopPreview();
				} else {
					Log.i("info", "torch is turn on!");
					p.setFlashMode(Parameters.FLASH_MODE_TORCH);
					camera.setParameters(p);
					camera.startPreview();
				}
			}
		});
	}

	protected void setBackgroundColor(int position) {
		switch (position) {
		case 0:
			layout.setBackgroundColor(Color.YELLOW);
			break;
		case 1:
			layout.setBackgroundColor(Color.BLUE);
			break;
		case 2:
			layout.setBackgroundColor(Color.RED);
			break;
		case 3:
			layout.setBackgroundColor(Color.parseColor("#800080"));
			break;
		case 4:
			layout.setBackgroundColor(Color.GREEN);
			break;
		case 5:
			layout.setBackgroundColor(Color.GRAY);
			break;
		case 6:
			layout.setBackgroundColor(Color.BLACK);
			checkBox.setTextColor(orangeColor);
			break;
		case 7:
			layout.setBackgroundColor(Color.WHITE);
			break;
		case 8:
			layout.setBackgroundColor(orangeColor);
			break;
		}
	}

	public static Camera getCameraInstance() {
		camera = null;
		try {
			camera = Camera.open();// attempt to get a Camera instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
		}
		return camera; // returns null if camera is unavailable
	}

	public void onDestroy() {
		this.wakeLock.release();
		super.onDestroy();
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		// mHolder = holder;
		// try {
		// Log.i("ERROR", "setting preview");
		// camera.setPreviewDisplay(mHolder);
		// } catch (IOException e) {
		// e.printStackTrace();
		// Log.e("ERROR", "Can no hold surfaceView");
		// }
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		Log.i("SurfaceHolder", "stopping preview");
		camera.stopPreview();
		mHolder = null;
	}

	public void clickButtonYellow(View v) {
		layout.setBackgroundColor(Color.YELLOW);
	}

	public void clickButtonBlue(View v) {
		layout.setBackgroundColor(Color.BLUE);
	}

	public void clickButtonRed(View v) {
		layout.setBackgroundColor(Color.RED);
	}

	public void clickButtonPurple(View v) {
		// purple
		layout.setBackgroundColor(Color.parseColor("#800080"));
	}

	public void clickButtonGreen(View v) {
		layout.setBackgroundColor(Color.GREEN);
	}

	public void clickButtonGray(View v) {
		layout.setBackgroundColor(Color.GRAY);
	}

	public void clickButtonBlack(View v) {
		layout.setBackgroundColor(Color.BLACK);
	}

	public void clickButtonWhite(View v) {
		layout.setBackgroundColor(Color.WHITE);
	}

	public void clickButtonOrange(View v) {
		layout.setBackgroundColor(orangeColor);
	}
}