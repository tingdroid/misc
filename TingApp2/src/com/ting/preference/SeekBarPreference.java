package com.ting.preference;

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
import android.content.SharedPreferences;
import android.content.res.TypedArray;
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
	private TextView mValueText, mMessageText, mMinText, mMaxText;
	private Context mContext;

	private String mText;
	private DecimalFormat mFormat;

	private String mOriginalSummary;
	private int mMax;
	private float mValue = 0;
	private float mDefault, mvMin, mvMax;
	private boolean mIsFloat;

	public SeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;

		// override summary if it contains value parameter (%1)
		mOriginalSummary = getSummary().toString();
		if (mOriginalSummary != null) {
			if (mOriginalSummary.contains("%1")) {
				setSummary(null);
			} else {
				mOriginalSummary = null;
			} 
		}
		
		int attrsWanted[] = new int[] { 
				android.R.attr.format, 
				android.R.attr.text
		};
		TypedArray a = context.obtainStyledAttributes(attrs, attrsWanted);

        for (int i = a.getIndexCount() ; 0 < i--; ) {
            int attr = a.getIndex(i);
            switch (attrsWanted[i]) {
                case android.R.attr.format:
            		String fmt = a.getString(attr);
            		mFormat = new DecimalFormat(fmt != null ? fmt : DEFAULT_FORMAT);
                    break;
                case android.R.attr.text:
                	mText = a.getString(attr);
                    break;
            }
        }
		a.recycle();
		
		String type = attrs.getAttributeValue(ANDROID_NS, "valueType");
		mIsFloat = FLOAT_TYPE.equals(type);

		mMax = attrs.getAttributeIntValue(ANDROID_NS, "max", 100);

		if (mIsFloat) {
			mvMin = attrs.getAttributeFloatValue(ANDROID_NS, "valueFrom", 0);
			mvMax = attrs.getAttributeFloatValue(ANDROID_NS, "valueTo", mMax);
			mDefault = attrs.getAttributeFloatValue(ANDROID_NS, "defaultValue",
					mvMin);
		} else {
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

		if (getDialogMessage() != null) {
			mMessageText = new TextView(mContext);
			mMessageText.setPadding(0, 0, 0, 16);
			layout.addView(mMessageText, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));
		}

		mValueText = new TextView(mContext);
		mValueText.setGravity(Gravity.CENTER_HORIZONTAL);
		mValueText.setTextSize(32);
		layout.addView(mValueText, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		RelativeLayout rangeLayout = new RelativeLayout(mContext);
		{
			mMinText = new TextView(mContext);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			rangeLayout.addView(mMinText, params);

			mMaxText = new TextView(mContext);
			params = new RelativeLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			rangeLayout.addView(mMaxText, params);

			layout.addView(rangeLayout);
		}
		
		mSeekBar = new SeekBar(mContext);
		layout.addView(mSeekBar, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		return layout;
	}

	@Override
	protected void onBindDialogView(View v) {
		super.onBindDialogView(v);
		mValue = getPersisted();

		if (getDialogMessage() != null) {
			mMessageText.setText(getDialogMessage());
		}
		mMinText.setText(format(mvMin));
		mMaxText.setText(format(mvMax));
		mValueText.setText(getProgressString());
		mSeekBar.setMax(mMax);
		mSeekBar.setProgress(toProgress(mValue));
		mSeekBar.setOnSeekBarChangeListener(this);
	}

	@Override
	protected void onSetInitialValue(boolean restore, Object defaultValue) {
		super.onSetInitialValue(restore, defaultValue);
		if (restore) {
			mValue = getPersisted();
		} else {
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
		} else {
			persistInt(Math.round(fVal));
		}
	}

	private void updateSummary() {
		if (mOriginalSummary == null)
			return;
		setSummary(String.format(mOriginalSummary, format(mValue)));
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
		return mText == null ? text : mText.contains("%1") ? String.format(
				mText, text) : text + mText;
	}

	public String format(float value) {
		return mIsFloat ? mFormat.format(value) : mFormat.format(Math
				.round(value));
	}
}
