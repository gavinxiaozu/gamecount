/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package personal.gavin.gamecount.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import personal.gavin.gamecount.R;
import personal.gavin.gamecount.widget.ColorPicker.OnColorChangedListener;

public class ColorPickerDialog extends DialogFragment {

    private OnColorChangedListener mListener;
    private int mInitialColor;
    private ColorPicker mColorPicker;
    
    public ColorPickerDialog(int color){
    	mInitialColor = color;
    }

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// Get the layout inflater   
		LayoutInflater inflater = getActivity().getLayoutInflater();    
		
		// Inflate and set the layout for the dialog    
		// Pass null as the parent view because its going in the dialog layout 
		
		View dialogView = inflater.inflate(R.layout.dialog_color_picker, null);
		mColorPicker = (ColorPicker) dialogView.findViewById(R.id.color_picker);
		mColorPicker.setColor(mInitialColor);
		mColorPicker.setOnColorChangedListener(new OnColorChangedListener(){

			@Override
			public void colorChanged(int color) {
				// TODO Auto-generated method stub
				if(mListener != null) {
					mListener.colorChanged(color);
					dismiss();
				}
			}
			
		});
		
		return new AlertDialog.Builder(this.getActivity())
		.setView(dialogView)
		.setTitle(R.string.color_pick)
		.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
		
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				if(mColorPicker != null && mListener != null) {
					mListener.colorChanged(mColorPicker.getColor());
				}
			}
		})
		.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		})
		.create();
	}
	
	public void setOnColorChangedListener(OnColorChangedListener l) {
    	mListener = l;
    }
	
	public void setColor(int color) {
		if(mColorPicker != null)
			mColorPicker.setColor(color);
	}
}
