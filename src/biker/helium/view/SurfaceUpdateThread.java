package biker.helium.view;

import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
/**
 *	Call the onDraw of the DrawableView that contains
 */
public class SurfaceUpdateThread extends Thread {
	private SurfaceHolder _surfaceHolder;
	private GameView _drawableView;
    private boolean _run = false;
 
    public SurfaceUpdateThread( GameView drawableView) {
        _surfaceHolder = ((SurfaceView)drawableView).getHolder();
        _drawableView = drawableView;
    }
 
    public void setRunning(boolean run) {
        _run = run;
    }
    
    @Override
    public void run() {
        Canvas c;
        while (_run) {
            c = null;
            try {
                c = _surfaceHolder.lockCanvas(null);
                synchronized (_surfaceHolder) {
                    _drawableView.onDraw(c);
                }
            } finally {
                // do this in a finally so that if an exception is thrown
                // during the above, we don't leave the Surface in an
                // inconsistent state
                if (c != null) {
                    _surfaceHolder.unlockCanvasAndPost(c);
                }
            }
        }
    }
}
