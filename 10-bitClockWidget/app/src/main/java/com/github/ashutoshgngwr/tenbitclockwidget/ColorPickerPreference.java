/*
 *     Copyright (C) 2017  Ashutosh Gangwar
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.ashutoshgngwr.tenbitclockwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.ColorInt;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

import es.dmoral.coloromatic.IndicatorMode;
import es.dmoral.coloromatic.colormode.ColorMode;
import es.dmoral.coloromatic.view.ColorOMaticView;

public class ColorPickerPreference extends Preference implements ColorOMaticView.ButtonBarListener {

  private ColorMode colorMode = ColorMode.RGB;
  private AlertDialog colorPickerDialog;
  private int color;
  private View colorView;

  public ColorPickerPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init(attrs);
  }

  public ColorPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(attrs);
  }

  public ColorPickerPreference(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(attrs);
  }

  private void init(AttributeSet attributeSet) {
    setLayoutResource(R.layout.preference_widget_color_picker);
    if (attributeSet.getAttributeBooleanValue(null, "alphaSlider", false))
      colorMode = ColorMode.ARGB;
  }

  private void showColorPickerDialog(int initColor) {
    ColorOMaticView colorPickerView =
        new ColorOMaticView(initColor, true, colorMode, IndicatorMode.HEX, getContext());
    colorPickerView.enableButtonBar(ColorPickerPreference.this);

    colorPickerDialog = new AlertDialog.Builder(getContext())
        .setView(colorPickerView)
        .show();

    WindowManager.LayoutParams params = new WindowManager.LayoutParams();
    params.copyFrom(colorPickerDialog.getWindow().getAttributes());
    params.width = WindowManager.LayoutParams.WRAP_CONTENT;
    params.height = WindowManager.LayoutParams.WRAP_CONTENT;
    colorPickerDialog.getWindow().setAttributes(params);
  }

  @Override
  public void onBindViewHolder(PreferenceViewHolder viewHolder) {
    super.onBindViewHolder(viewHolder);
    colorView = viewHolder.findViewById(R.id.color_preview);

    ((GradientDrawable)
        ((LayerDrawable) colorView.getBackground())
            .findDrawableByLayerId(R.id.color))
        .setColor(color);
  }

  @Override
  protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
    if (restoreValue)
      color = getPersistedInt(0);
    else
      persistInt(color = (Integer) defaultValue);
  }

  @Override
  protected Object onGetDefaultValue(TypedArray typedArray, int index) {
    return color = Color.parseColor(typedArray.getString(index));
  }

  @Override
  protected void onClick() {
    showColorPickerDialog(color);
  }

  @Override
  public void onPositiveButtonClick(@ColorInt int color) {
    ((GradientDrawable)
        ((LayerDrawable) colorView.getBackground())
            .findDrawableByLayerId(R.id.color))
        .setColor(this.color = color);
    colorPickerDialog.dismiss();
    persistInt(color);
    notifyChanged();
  }

  @Override
  public void onNegativeButtonClick() {
    colorPickerDialog.dismiss();
  }
}
