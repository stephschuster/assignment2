package bgu.spl.a2;

/**
 * Describes a monitor that supports the concept of versioning - its idea is
 * simple, the monitor has a version number which you can receive via the method
 * {@link #getVersion()} once you have a version number, you can call
 * {@link #await(int)} with this version number in order to wait until this
 * version number changes.
 *
 * you can also increment the version number by one using the {@link #inc()}
 * method.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class VersionMonitor {
    private int versionNum = 0;
    private boolean isWaiting;
    private int goalVersion = 0; // will change in the future. first time there is no one waiting so doesnt matter

    //in the forum they said we can add package protected constructor here
    /*package*/ VersionMonitor() {
                isWaiting = false; //first time no one is waiting
    }


    public int getVersion() {
        return versionNum;
    }

    public synchronized void inc() {
            versionNum++;
            if(isWaiting && getVersion() == goalVersion) {
                isWaiting = false;
                this.notifyAll();
            }
    }

    public synchronized void await(int version) throws InterruptedException {
        while(version == versionNum) {
            try {
                goalVersion = getVersion() + 1;
                isWaiting = true;
                this.wait();
            }
            catch (InterruptedException e) {}
        }
    }
}
