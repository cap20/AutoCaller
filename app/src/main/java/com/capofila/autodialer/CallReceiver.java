package com.capofila.autodialer;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

public class CallReceiver extends PhonecallReceiver {



    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        Log.d("Call rec", "onOutgoingCallStarted: " + start + number);
        Toast.makeText(ctx,"Call Started",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"Call Received",Toast.LENGTH_LONG).show();
        Log.d("asd", "onReceive: call received ");
        super.onReceive(context, intent);
    }

    @Override
    public void onCallStateChanged(Context context, int state, String number) {
        Log.d("Sadsa", " : ");
        super.onCallStateChanged(context, state, number);
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.d("call rec", "onOutgoingCallEnded: " + start + end);
        Toast.makeText(ctx,"Call ended " + start + end,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
    }



}