package com.example.android.sltmachin;

public class Wheel extends Thread {

        interface WheelListener{
            void newImage(int img);
        }

        private int[] imgs = {R.drawable.cherry, R.drawable.grape, R.drawable.strawberry};
        public int currentIndex;
        private WheelListener wheelListener;
        private long frameDuration;
        private long startIn;
        private boolean isStarted;

        public Wheel(WheelListener wheelListener, long frameDuration, long startIn) {
            this.wheelListener = wheelListener;
            this.frameDuration = frameDuration;
            this.startIn = startIn;
            currentIndex = 0;
            isStarted = true;
        }

        public void nextImg() {
            currentIndex++;

            if (currentIndex == imgs.length) {
                currentIndex = 0;
            }
        }

        @Override
        public void run() {
            try {
                Thread.sleep(startIn);
            } catch (InterruptedException e) {
            }

            while(isStarted) {
                try {
                    Thread.sleep(frameDuration);
                } catch (InterruptedException e) {
                }

                nextImg();

                if (wheelListener != null) {
                    wheelListener.newImage(imgs[currentIndex]);
                }
            }
        }

        public void stopWheel() {
            isStarted = false;
        }



}
