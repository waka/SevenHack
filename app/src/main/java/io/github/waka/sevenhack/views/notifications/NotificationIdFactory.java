package io.github.waka.sevenhack.views.notifications;

import io.github.waka.sevenhack.data.entities.Episode;

class NotificationIdFactory {

    enum Type {
        DOWNLOADING(1000),
        DOWNLOADED(2000),
        DOWNLOAD_FAILED(3000),
        PLAYER(4000);

        private final int code;

        Type(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    public static int get(Episode episode, Type type) {
        return episode.id + type.getCode();
    }

    public static int get(Type type) {
        return type.getCode();
    }
}
