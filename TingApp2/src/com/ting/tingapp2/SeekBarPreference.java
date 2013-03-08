package com.ting.tingapp2;

/* The original code was written by Matthew Wiggins 
 * and is released under the APACHE 2.0 license 
 * 
 * Enhanced to support Min, Max and Float type
 * by Oleg Kobchenko, 2013-06  
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 */

import java.text.DecimalFormat;

import android.content.Context;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * <p>
 * A {@link Preference} that displays a SeekBar and a message, and persists the
 * value as an integer. It can be used in a preferences.xml with no other
 * required code.
 * 
 * <p>
 * It uses some standard android attributes:
 * <dl>
 * <dt>android:dialogMessage
 * 		<dd>Shown above the SeekBar as a splash message in the dialog
 * <dt>android:text
 * 		<dd>Format string for the current value of the slider in the dialog; this can be
 *          used to indicate the units
 * <dt>android:max
 * 		<dd>Number of steps between valueFrom and valueTo. 
 *          Behaves the same as SeekBar's max attribute. 
 *          E.g. with max: 2, valueFrom: 1, valueTo: 5, the possible values are 1, 3, 5. 
 * <dt>android:valueType
 * 		<dd>('intType' or 'floatType') Data type of domain units. 'floatType' is Default.
 * <dt>android:defaultValue
 * 		<dd>(int or float) Default value in domain units
 * <dt>android:valueFrom
 * 		<dd>(int or float) Minimum value in domain units
 * <dt>android:valueTo
 * 		<dd>(int or float) Maximum value in domain units
 */
public class SeekBarPreference extends DialogPreference implements
		SeekBar.OnSeekBarChangeListener {
	private static final String ANDROID_NS = "http://schemas.android.com/apk/res/android";
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.####");

	private SeekBar mSeekBar;
	private TextView mValueText;
	private Context mContext;

	private String mDialogMessage, mSuffix;
	private int mMax;
	private float mValue = 0;
	private float mDefault, mvMin, mvMax, mvStep;
	private boolean mIsFloat;

	public SeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;

		mDialogMessage = attrs.getAttributeValue(ANDROID_NS, "dialogMessage");
		mSuffix = attrs.getAttributeValue(ANDROID_NS, "text");
		mIsFloat = !"intType".equals(attrs.getAttributeValue(ANDROID_NS,
				"valueType"));
		mMax = attrs.getAttributeIntValue(ANDROID_NS, "max", 100);

		if (mIsFloat) {
			mvMin = attrs.getAttributeFloatValue(ANDROID_NS, "valueFrom", 0);
			mvMax = attrs.getAttributeFloatValue(ANDROID_NS, "valueTo", mMax);
			mDefault = attrs.getAttributeFloatValue(ANDROID_NS, "defaultValue", 0);
		} else {
			mvMin = attrs.getAttributeIntValue(ANDROID_NS, "valueFrom", 0);
			mvMax = attrs.getAttributeIntValue(ANDROID_NS, "valueTo", mMax);
			mDefault = attrs.getAttributeIntValue(ANDROID_NS, "defaultValue", 0);
		}
		mvStep = (mvMax - mvMin) / mMax;
	}

	@Override
	protected View onCreateDialogView() {
		LinearLayout layout = new LinearLayout(mContext);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setPadding(16, 16, 16, 16);

		if (mDialogMessage != null) {
			TextView messageText = new TextView(mContext);
			messageText.setText(mDialogMessage);
			messageText.setPadding(0, 0, 0, 16);
			layout.addView(messageText, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));
		}

		mValueText = new TextView(mContext);
		mValueText.setGravity(Gravity.CENTER_HORIZONTAL);
		mValueText.setTextSize(32);
		layout.addView(mValueText, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		if (mDialogMessage != null) {
			RelativeLayout rangeLayout = new RelativeLayout(mContext);

			TextView minText = new TextView(mContext);
			minText.setText(DECIMAL_FORMAT.format(mvMin));
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			rangeLayout.addView(minText, params);

			TextView maxText = new TextView(mContext);
			maxText.setText(DECIMAL_FORMAT.format(mvMax));
			params = new RelativeLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			rangeLayout.addView(maxText, params);

			layout.addView(rangeLayout);
		}

		mSeekBar = new SeekBar(mContext);
		layout.addView(mSeekBar, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		mSeekBar.setMax(mMax);
		mValue = getPersisted();
		mSeekBar.setProgress(toProgress(mValue));
		mSeekBar.setOnSeekBarChangeListener(this);

		mValueText.setText(toString());
		return layout;
	}

	int toProgress(float fVal) {
		return (int) ((fVal - mvMin) / mvStep);
	}

	float getPersisted() {
		float fVal = mDefault;
		if (shouldPersist()) {
			fVal = mIsFloat ? getPersistedFloat(mDefault)
					: getPersistedInt((int) mDefault);
		}
		return fVal;
	}

	float fromProgress(int iVal) {
		return mvMin + (float) iVal * mvStep;
	}

	void setPersisted(float fVal) {
		if (!shouldPersist()) return;
		if (mIsFloat) {
			persistFloat(fVal);
		} else {
			persistInt((int)fVal);
		}
	}

	@Override
	protected void onBindDialogView(View v) {
		super.onBindDialogView(v);
		mSeekBar.setMax(mMax);
		mSeekBar.setProgress(toProgress(mValue));
	}

	@Override
	protected void onSetInitialValue(boolean restore, Object defaultValue) {
		super.onSetInitialValue(restore, defaultValue);
		if (restore)
			mValue = getPersisted();
		else
			mValue = Float.parseFloat(String.valueOf(defaultValue));
	}

	public void onProgressChanged(SeekBar seek, int value, boolean fromTouch) {
		mValue = fromProgress(value);
		mValueText.setText(toString());
		setPersisted(mValue);
		callChangeListener(mIsFloat ? Float.valueOf(mValue) : Integer.valueOf((int)mValue));
	}

	public void onStartTrackingTouch(SeekBar seek) {
	}

	public void onStopTrackingTouch(SeekBar seek) {
	}

	public void setMax(int max) {
		mMax = max;
	}

	public int getMax() {
		return mMax;
	}

	public void setProgress(int progress) {
		setProgress((float) progress);
	}
	public void setProgress(float progress) {
		mValue = progress;
		if (mSeekBar != null)
			mSeekBar.setProgress(toProgress(mValue));
	}

	public int getProgressInt() {
		return (int)mValue;
	}
	public float getProgressFloat() {
		return mValue;
	}

	public String toString() {
		String text = DECIMAL_FORMAT.format(mValue);
		return mSuffix != null ? String.format(mSuffix, text) : text;
	}
}