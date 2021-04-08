package gb.multi;

public class Multithreading {
    private final Object mon = new Object();
    private char currentLetter = 'A';

    public static void main(String[] args) {
        Multithreading waitNotifyApp = new Multithreading();
        new Thread(() -> {
            waitNotifyApp.printA();
        }).start();
        new Thread(() -> {
            waitNotifyApp.printB();
        }).start();
        new Thread(() -> {
            waitNotifyApp.printC();
        }).start();
    }

    public void printA() {
        synchronized (mon) {
            try {
                for (int i = 0; i < 5; i++) {
                    while (currentLetter != 'A') {
                        mon.wait();
                    }
                    System.out.print("A");
                    currentLetter = 'B';
                    mon.notifyAll();
                    //Thread.sleep(2500);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void printB() {
        synchronized (mon) {
            try {
                for (int i = 0; i < 5; i++) {
                    while (currentLetter != 'B') {
                        mon.wait();
                    }
                    System.out.print("B");
                    currentLetter = 'C';
                    mon.notifyAll();
                  //  Thread.sleep(1);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void printC() {
        synchronized (mon) {
            try {
                for (int i = 0; i < 5; i++) {
                    while (currentLetter != 'C') {
                        mon.wait();
                    }
                    System.out.print("C");
                    currentLetter = 'A';
                    mon.notifyAll();
                  //  Thread.sleep(10);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

