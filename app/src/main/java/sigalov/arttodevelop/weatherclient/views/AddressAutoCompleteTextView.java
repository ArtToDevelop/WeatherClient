package sigalov.arttodevelop.weatherclient.views;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.ProgressBar;

import sigalov.arttodevelop.weatherclient.adapters.AutoCompleteBaseAdapter;

public class AddressAutoCompleteTextView extends AppCompatAutoCompleteTextView {
    // MESSAGE_TEXT_CHANGED попадает в handleMessage. Здесь не используется.
    // требуется только для вызова sendMessageDelayed
    private static final int MESSAGE_TEXT_CHANGED = 100;

    // задержка в миллисекундах
    private int autocompleteDelay = 0;
    private ProgressBar mLoadingIndicator;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            AddressAutoCompleteTextView.super.performFiltering((CharSequence) msg.obj, msg.arg1);
        }
    };

    public AddressAutoCompleteTextView(Context context) {
        super(context);
    }

    public AddressAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AddressAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        InputConnection conn = super.onCreateInputConnection(outAttrs);
        outAttrs.imeOptions &= ~EditorInfo.IME_FLAG_NO_ENTER_ACTION;
        return conn;
    }

    @Override
    protected void performFiltering(CharSequence text, int keyCode) {
        if(autocompleteDelay > 0) {
            if (mLoadingIndicator != null) {
                mLoadingIndicator.setVisibility(View.VISIBLE);
            }

            mHandler.removeMessages(MESSAGE_TEXT_CHANGED);
            mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_TEXT_CHANGED, text), autocompleteDelay);
        }
        else {
            super.performFiltering(text, keyCode);
        }
    }

    @Override
    public void onFilterComplete(int count) {
        if (mLoadingIndicator != null) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
        }

        super.onFilterComplete(count);
    }

    @Override
    public <T extends ListAdapter & Filterable> void setAdapter(T adapter) {
        if(adapter instanceof AutoCompleteBaseAdapter) {
            ((AutoCompleteBaseAdapter) adapter).setOnNewFilterItemsListener(new AutoCompleteBaseAdapter.OnNewFilterItems() {
                @Override
                public void onNewFilterItems(AutoCompleteBaseAdapter adapter) {
                    performFiltering(getText(), 0);
                }
            });
        }

        super.setAdapter(adapter);
    }

    public void setLoadingIndicator(ProgressBar progressBar) {
        mLoadingIndicator = progressBar;
    }

    public void setAutocompleteDelay(int autocompleteDelay) {
        this.autocompleteDelay = autocompleteDelay;
    }


}
