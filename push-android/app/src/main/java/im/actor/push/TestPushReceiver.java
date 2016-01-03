package im.actor.push;

import android.util.Log;

public class TestPushReceiver extends ActorPushReceiver {
    @Override
    public void onPushReceived(String payload) {
        Log.d("TestPushReceiver", "On push received: " + payload);
    }
}
