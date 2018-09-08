/**
 * 
 */
package personal.gavin.gamecount.widget;

import personal.gavin.gamecount.R;
import personal.gavin.gamecount.util.Utils;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author Gavin
 * @version  2013-4-16 下午9:29:36
 */
public class DigitInputPanel extends RelativeLayout implements View.OnClickListener{

	private LayoutInflater mLI;
	private View mKeyPad;
	private TextView mDisplay;
	private OnOkListener mOnOkListener;
	
	private double mValue;
	
	private boolean mCalulate;
	private boolean mModified;
	private Operation mOperation;
	private enum Operation {
		ADD, SUBTRACT
	};
	/**
	 * @param context
	 * @param attrs
	 */
	public DigitInputPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mLI = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mKeyPad = mLI.inflate(R.layout.input_keypad_layout, this, true);
		mDisplay = (TextView) mKeyPad.findViewById(R.id.keypad_number);
		
		((Button)mKeyPad.findViewById(R.id.one)).setOnClickListener(this);
	    ((Button)mKeyPad.findViewById(R.id.two)).setOnClickListener(this);
	    ((Button)mKeyPad.findViewById(R.id.three)).setOnClickListener(this);
	    ((Button)mKeyPad.findViewById(R.id.four)).setOnClickListener(this);
	    ((Button)mKeyPad.findViewById(R.id.five)).setOnClickListener(this);
	    ((Button)mKeyPad.findViewById(R.id.six)).setOnClickListener(this);
	    ((Button)mKeyPad.findViewById(R.id.seven)).setOnClickListener(this);
	    ((Button)mKeyPad.findViewById(R.id.eight)).setOnClickListener(this);
	    ((Button)mKeyPad.findViewById(R.id.nine)).setOnClickListener(this);
	    ((Button)mKeyPad.findViewById(R.id.dot)).setOnClickListener(this);
	    ((Button)mKeyPad.findViewById(R.id.zero)).setOnClickListener(this);
	    
	    ((Button)mKeyPad.findViewById(R.id.clear)).setOnClickListener(this);
	    ((Button)mKeyPad.findViewById(R.id.subtract)).setOnClickListener(this);
	    ((Button)mKeyPad.findViewById(R.id.add)).setOnClickListener(this);
	    ((Button)mKeyPad.findViewById(R.id.equal)).setOnClickListener(this);
	    ((Button)mKeyPad.findViewById(R.id.ok)).setOnClickListener(this);
	    
	    clearState();
	}
	
	public void setOnOkListener(OnOkListener listener) {
		mOnOkListener = listener;
	}

	public interface OnOkListener {
		public void onOk(double value);
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int input = Utils.keyToNumber(v.getId());
		if (input == Utils.NOT_NUMBER_KEY) {
			switch(v.getId()) {
				case R.id.ok:
					if(mOnOkListener != null) mOnOkListener.onOk(getValue());
					this.setVisibility(View.GONE);
					clearState();
					break;
					
				case R.id.dot:
					String display= (String) mDisplay.getText();
					mDisplay.setText(Utils.inputNumberString(display, "."));
					mModified = true;
					break;
					
				case R.id.clear:
					clearState();
					break;
				
				case R.id.subtract:
					mValue = getCalulateResult();
					mOperation = Operation.SUBTRACT;
					mCalulate = true;
					mDisplay.setText("0");
					((Button)mKeyPad.findViewById(R.id.ok)).setVisibility(View.GONE);
					((Button)mKeyPad.findViewById(R.id.equal)).setVisibility(View.VISIBLE);
					break;
				
				case R.id.add:
					mValue = getCalulateResult();
					mOperation = Operation.ADD;
					mCalulate = true;
					mDisplay.setText("0");
					((Button)mKeyPad.findViewById(R.id.ok)).setVisibility(View.GONE);
					((Button)mKeyPad.findViewById(R.id.equal)).setVisibility(View.VISIBLE);
					break;
					
				case R.id.equal:
					mValue = getCalulateResult();
					mCalulate = false;
					mDisplay.setText(Utils.doubleToString(mValue));
					((Button)mKeyPad.findViewById(R.id.equal)).setVisibility(View.GONE);
					((Button)mKeyPad.findViewById(R.id.ok)).setVisibility(View.VISIBLE);
					break;
			}
		} else {
			String display= (String) mDisplay.getText();
			mDisplay.setText(Utils.inputNumberString(display, String.valueOf(input)));
			if(!mCalulate) {
				mValue = Double.valueOf((String) mDisplay.getText());
				mModified = true;
			}
		}
	}
	
	private double getCalulateResult() {
		if (mCalulate) {
			double display = Double.valueOf((String) mDisplay.getText());
			mModified = true;
			switch(mOperation){
			case ADD:
				return mValue + display;
				
			case SUBTRACT:
				return mValue - display;
			}
		}
		return mValue;
	}
	
	private void clearState(){
		mValue = 0;
		mDisplay.setText("0");
		mModified = false;
		mCalulate = false;
		((Button)mKeyPad.findViewById(R.id.equal)).setVisibility(View.GONE);
		((Button)mKeyPad.findViewById(R.id.ok)).setVisibility(View.VISIBLE);
	}

	public double getValue() {
		return mValue;
	}
	
	public boolean getModified() {
		return mModified && !mCalulate;
	}

	public void setValue(double value) {
		clearState();
		mValue = value;
		mDisplay.setText(Utils.doubleToString(value));
	}
	
	public void show() {
		this.setVisibility(View.VISIBLE);
	}
	
	public void dismiss(){
		this.setVisibility(View.GONE);
		clearState();
	}
}
