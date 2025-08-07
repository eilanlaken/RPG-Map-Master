package com.heavybox.jtix.ecs;

public interface ComponentAudio extends Component {

    @Override
    default int getBitmask() {
        return Type.AUDIO.bitmask;
    }

}
