package com.heavybox.jtix.ecs;

public interface ComponentLogics extends Component {

    @Override
    default int getBitmask() {
        return Type.AUDIO.bitmask;
    }

    void start();
    void frameUpdate(float delta);
    void fixedUpdate(float delta);
    void onDestroy();

    void handleSignal(final Object signal);

}
