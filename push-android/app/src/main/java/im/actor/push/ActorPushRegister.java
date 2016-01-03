package im.actor.push;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.UUID;

/**
 * Registration for Actor Push
 */
public final class ActorPushRegister {

    private String topic;
    private Context context;
    private SharedPreferences sharedPreferences;

    public ActorPushRegister(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences("actor_push_register", Context.MODE_PRIVATE);
        this.topic = sharedPreferences.getString("topic", null);
    }

    public String registerForPush() {
        if (this.topic != null) {
            return this.topic;
        }
        topic = UUID.randomUUID().toString();
        sharedPreferences.edit().putString("topic", topic).commit();

        context.startService(new Intent(context, ActorPushService.class)
                .putExtra("mqtt_url", "tcp://lab2.81port.com:1883")
                .putExtra("mqtt_topic", topic));

        return topic;
    }
}