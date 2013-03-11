package com.ting.preference;

/* The original code was written by Matthew Wiggins 
 * and is released under the APACHE 2.0 license 
 * 
 * Enhanced to support Min, Max and Float type
 * by Oleg Kobchenko, 2013-06  
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import java.text.DecimalFormat;

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
 * <dd>Shown above the SeekBar as a splash message in the dialog
 * <dt>android:text
 * <dd>({@link String#format} string) Format pattern for the current value of
 * the slider in the dialog; can be used to indicate the units
 * <dt>android:max
 * <dd>(int &gt; 0) Number of steps between valueFrom and valueTo. Behaves the
 * same as SeekBar's max attribute. E.g. with max: 2, valueFrom: 1, valueTo: 5,
 * the possible values are 1, 3, 5.
 * <dt>android:valueType
 * <dd>('intType' or 'floatType') Data type of domain units. 'intType' is
 * Default.
 * <dt>android:defaultValue
 * <dd>(int or float) Default value in domain units.
 * <dt>android:valueFrom
 * <dd>(int or float) Minimum value in domain units
 * <dt>android:valueTo
 * <dd>(int or float) Maximum value in domain units
 * <dt>android:format
 * <dd>({@link DecimalFormat} string) Format string for the value, before it is
 * processed by "text" format pattern; can used to control decimal digits
 * </dl>
 * <p>
 * Note: Parameter values and defaults for 'floatType' should contain decimal
 * point and digits, whereas for 'intType' should not.
 */
public class SeekBarPreference extends DialogPreference implements
SeekBar.OnSeekBarChangeListener {
	private static final String ANDROID_NS = "http://schemas.android.com/apk/res/android";
	private static final String FLOAT_TYPE = "0";
	private static final String DEFAULT_FORMAT = "#.####";

	private SeekBar mSeekBar;
	private TextView mValueText;
	private Context mContext;

	private String mDialogMessage, mSuffix, mSummary;
	private int mMax;
	private float mValue = 0;
	private float mDefault, mvMin, mvMax;
	private boolean mIsFloat;

	private DecimalFormat mFormat;

	public SeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;

		mDialogMessage = attrs.getAttributeValue(ANDROID_NS, "dialogMessage");
		mSuffix = attrs.getAttributeValue(ANDROID_NS, "text");
		String format = attrs.getAttributeValue(ANDROID_NS, "format");
		mFormat = new DecimalFormat(format != null ? format : DEFAULT_FORMAT);

/*		int attrsWanted[] = new int[] {
			android.R.attr.dialogMessage,
			android.R.attr.summary
		};
		TypedArray attrsResolved = context.obtainStyledAttributes(attrs,attrsWanted);
		mDialogMessage = attrsResolved.getString(0);
		mSummary = attrsResolved.getString(1);
		attrsResolved.recycle();
		mDialogTitleResId = theAttrs.getResourceId(0,myDefaultResId);
*/
		mSummary = attrs.getAttributeValue(ANDROID_NS, "summary");		
        if (false && mSummary.contains("@")) {
			int summId = attrs.getAttributeResourceValue(ANDROID_NS, "summary", 0);
			if (summId != 0) {
				mSummary = getContext().getResources().getString(summId);
			}
		}
		
		String type = attrs.getAttributeValue(ANDROID_NS, "valueType");
		mIsFloat = FLOAT_TYPE.equals(type);

		mMax = attrs.getAttributeIntValue(ANDROID_NS, "max", 100);

		if (mIsFloat) {
			mvMin = attrs.getAttributeFloatValue(ANDROID_NS, "valueFrom", 0);
			mvMax = attrs.getAttributeFloatValue(ANDROID_NS, "valueTo", mMax);
			mDefault = attrs.getAttributeFloatValue(ANDROID_NS, "defaultValue",
													mvMin);
		}
		else {
			mvMin = attrs.getAttributeIntValue(ANDROID_NS, "valueFrom", 0);
			mvMax = attrs.getAttributeIntValue(ANDROID_NS, "valueTo", mMax);
			mDefault = attrs.getAttributeIntValue(ANDROID_NS, "defaultValue",
												  (int) mvMin);
		}
	}

	// DialogPreference overrides

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
			minText.setText(format(mvMin));
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			rangeLayout.addView(minText, params);

			TextView maxText = new TextView(mContext);
			maxText.setText(format(mvMax));
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

		mValueText.setText(getProgressString());
		return layout;
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
		if (restore) {
			mValue = getPersisted();
		}
		else {
			mValue = Float.parseFloat(String.valueOf(defaultValue));
		}
		updateSummary();		
	}

	/**
	 * Called when the dialog is dismissed and should be used to save data to
	 * the {@link SharedPreferences}.
	 * 
	 * @param positiveResult
	 *            Whether the positive button was clicked (true), or the
	 *            negative button was clicked or the dialog was canceled
	 *            (false).
	 */
	@Override
	protected void onDialogClosed(boolean positiveResult) {
		if (!positiveResult)
			return;
		if (callChangeListener(getProgressNumber())) {
			setPersisted(mValue);
			updateSummary();
		}
	}

	// OnSeekBarChangeListener overrides

	public void onProgressChanged(SeekBar seek, int value, boolean fromTouch) {
		mValue = fromProgress(value);
		mValueText.setText(getProgressString());
	}

	public void onStartTrackingTouch(SeekBar seek) {
	}

	public void onStopTrackingTouch(SeekBar seek) {
	}

	// helpers

	int toProgress(float fVal) {
		float mvStep = (mvMax - mvMin) / mMax;
		return Math.round((fVal - mvMin) / mvStep);
	}

	float fromProgress(int iVal) {
		float mvStep = (mvMax - mvMin) / mMax;
		return mvMin + (float) iVal * mvStep;
	}

	float getPersisted() {
		float fVal = mDefault;
		if (shouldPersist()) {
			fVal = mIsFloat ? getPersistedFloat(mDefault)
				: getPersistedInt((int) mDefault);
		}
		return fVal;
	}

	void setPersisted(float fVal) {
		if (!shouldPersist())
			return;
		if (mIsFloat) {
			persistFloat(fVal);
		}
		else {
			persistInt(Math.round(fVal));
		}
	}

	private void updateSummary() {
		if (mSummary != null) return;

		if (mSummary.contains("%1")) {
			setSummary(String.format(mSummary, format(mValue)));
		}
	}

	// public interface

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
		return Math.round(mValue);
	}

	public float getProgressFloat() {
		return mValue;
	}

	public Number getProgressNumber() {
		return mIsFloat ? Float.valueOf(mValue) : Integer.valueOf(Math
																  .round(mValue));
	}

	public String getProgressString() {
		String text = format(mValue);
		return mSuffix == null ? text : mSuffix.contains("%1") ? String.format(
			mSuffix, text) : text + mSuffix;
	}

	public String format(float value) {
		return mIsFloat ? mFormat.format(value) : mFormat.format(Math
																 .round(value));
	}
}
