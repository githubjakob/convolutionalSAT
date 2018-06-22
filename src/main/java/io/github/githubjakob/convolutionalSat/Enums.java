package io.github.githubjakob.convolutionalSat;

/**
 * Created by jakob on 14.06.18.
 */
public class Enums {

    public enum Module {
        ENCODER("encoder"),
        DECODER("decoder"),
        CHANNEL("channel");

        private String name;

        Module(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    };
}
