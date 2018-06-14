package io.github.githubjakob.convolutionalSat;

/**
 * Created by jakob on 14.06.18.
 */
public class Enums {

    public enum Group {
        ENCODER("encoder"),
        DECODER("decoder"),
        CHANNEL("channel");

        private String name;

        Group(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    };
}
