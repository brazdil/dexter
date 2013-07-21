package uk.ac.cam.db538.dexter;

public interface ProgressCallback {

    public void update(int finished, int outOf);

}
